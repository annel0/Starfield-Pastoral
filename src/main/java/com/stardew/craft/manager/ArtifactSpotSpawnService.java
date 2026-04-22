package com.stardew.craft.manager;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;

/**
 * SDV-parity artifact spot spawning service.
 *
 * <p>Replicates GameLocation.spawnObjects() artifact-spot section:
 * <ul>
 *   <li>Daily: remove existing spots with 15% chance each, then spawn new ones</li>
 *   <li>Spawn loop: chanceForNewArtifactAttempt starts at 1.0, *= 0.75 per iteration;
 *       +0.10 in winter</li>
 *   <li>Cap: stop if &gt;1 spot exists (winter: &gt;4)</li>
 *   <li>Must be on yellow_dirt, exposed to sky, block above is air</li>
 * </ul>
 *
 * <p>Also handles chunk-load spawning: when a chunk loads the first time during a
 * world session, artifact spots are attempted on any eligible yellow_dirt in that chunk.
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public final class ArtifactSpotSpawnService {

    private ArtifactSpotSpawnService() {}

    private static final String INIT_DATA_ID = "stardewcraft_artifact_spot_init";

    // SDV: Farm cap at >0; non-farm cap at >1; winter allows up to 4
    private static final int MAX_SPOTS_FARM = 0;
    private static final int MAX_SPOTS_NON_FARM = 1;
    private static final int MAX_SPOTS_WINTER = 4;

    // ======================== Zone Definition ========================

    private record ZoneRect(int minX, int minZ, int maxX, int maxZ) {}

    private static ZoneRect rect(int x1, int z1, int x2, int z2) {
        return new ZoneRect(Math.min(x1, x2), Math.min(z1, z2), Math.max(x1, x2), Math.max(z1, z2));
    }

    /**
     * All zones where artifact spots can spawn.
     * Uses same coordinate regions as ForageSpawnService + ArtifactDropService.
     */
    private record SpawnZone(String name, ZoneRect[] rects, int tileWidth, int tileHeight, SurfaceKind surface) {
        SpawnZone(String name, ZoneRect[] rects, int tileWidth, int tileHeight) {
            this(name, rects, tileWidth, tileHeight, SurfaceKind.YELLOW_DIRT);
        }
    }

    /** 表面类型：YELLOW_DIRT → 黄土；DESERT_SANDSTONE → 普通砂岩。决定刷生 / 检测 / 还原用哪种方块。 */
    private enum SurfaceKind { YELLOW_DIRT, DESERT_SANDSTONE }

    // SDV locations mapped to MC coordinates
    private static final SpawnZone[] ZONES = {
            new SpawnZone("Town",
                    new ZoneRect[]{ rect(19, 193, 159, 221), rect(21, 96, 51, 112), rect(-18, 69, -1, 80) },
                    140, 30),
            new SpawnZone("Forest",
                    new ZoneRect[]{ rect(134, -194, 197, -110), rect(231, -138, 252, -114), rect(221, -11, 302, 35) },
                    170, 90),
            new SpawnZone("Mountain",
                    new ZoneRect[]{ rect(-239, 161, -196, 188), rect(-245, 233, -207, 263), rect(-324, 289, -292, 309), rect(-105, 294, -72, 312) },
                    250, 150),
            new SpawnZone("Beach",
                    new ZoneRect[]{ rect(-293, -182, -192, -139), rect(-376, -173, -326, -148) },
                    190, 45),
            new SpawnZone("Farm",
                    new ZoneRect[]{ rect(-80, -100, 80, 80) },
                    160, 180),
            new SpawnZone("BusStop",
                    new ZoneRect[]{ rect(-20, 80, 20, 130) },
                    40, 50),
            new SpawnZone("Backwoods",
                    new ZoneRect[]{ rect(-100, 130, 20, 190) },
                    120, 60),
            new SpawnZone("Railroad",
                    new ZoneRect[]{ rect(-300, 300, -250, 350) },
                    50, 50),
            new SpawnZone("Desert",
                    new ZoneRect[]{ rect(-372, 1285, -259, 1423) },
                    113, 138, SurfaceKind.DESERT_SANDSTONE),
    };

    // ======================== Daily Spawn (called from StardewTimeManager) ========================

    /**
     * Called once per day. Replicates SDV GameLocation.spawnObjects() artifact spot logic.
     */
    public static void onNewDay(ServerLevel level, int season) {
        if (!level.dimension().equals(ModDimensions.STARDEW_VALLEY)) return;

        RandomSource random = level.getRandom();

        for (SpawnZone zone : ZONES) {
            // 0. Revert farmland back to yellow_dirt in non-farm zones
            //    (artifact spots dug by players become farmland, should reset daily)
            //    Desert variant tills to vanilla sandstone, no daily revert needed.
            if (!"Farm".equals(zone.name) && zone.surface == SurfaceKind.YELLOW_DIRT) {
                for (ZoneRect rect : zone.rects) {
                    revertFarmlandInRect(level, rect);
                }
            }

            // 1. Count & remove existing spots (SDV: 15% removal per spot per day)
            int existingCount = 0;
            for (ZoneRect rect : zone.rects) {
                removeAndCountSpots(level, rect, random, zone.surface);
                existingCount += countSpotsInRect(level, rect, zone.surface);
            }

            // 2. Check cap: SDV — Farm stops if >0, non-Farm stops if >1,
            //    but in Winter spawning continues as long as count <= 4
            //    沙漠区域：跳过上限检查（用 bbox 全扫描 + 低概率自调节密度）
            if (zone.surface != SurfaceKind.DESERT_SANDSTONE) {
                int threshold = "Farm".equals(zone.name) ? MAX_SPOTS_FARM : MAX_SPOTS_NON_FARM;
                if (existingCount > threshold && (season != 3 || existingCount > MAX_SPOTS_WINTER)) {
                    continue;
                }
            }

            // 3a. 沙漠区：扫描整个包围盒，所有露天砂岩按概率变成沙漠远古斑点
            if (zone.surface == SurfaceKind.DESERT_SANDSTONE) {
                final double perBlockChance = 0.0005; // 略低于岩板区累积概率
                for (ZoneRect rect : zone.rects) {
                    for (int x = rect.minX; x <= rect.maxX; x++) {
                        for (int z = rect.minZ; z <= rect.maxZ; z++) {
                            if (!level.hasChunk(x >> 4, z >> 4)) continue;
                            if (random.nextDouble() >= perBlockChance) continue;
                            if (canSpawnArtifactSpot(level, x, z, zone.surface)) {
                                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                                BlockPos pos = new BlockPos(x, surfaceY, z);
                                level.setBlock(pos, spotBlockFor(zone.surface).defaultBlockState(), Block.UPDATE_ALL);
                            }
                        }
                    }
                }
                continue;
            }

            // 3b. SDV spawn loop: chanceForNewArtifactAttempt（黄土区域）
            double chanceForNewAttempt = 1.0;
            while (random.nextDouble() < chanceForNewAttempt) {
                // SDV: *= 0.75, winter +0.10
                // Decay FIRST so that `continue` (e.g. unloaded chunk) cannot skip it
                chanceForNewAttempt *= 0.75;
                if (season == 3) {
                    chanceForNewAttempt += 0.10;
                }

                // Pick a random rect
                ZoneRect rect = zone.rects[random.nextInt(zone.rects.length)];

                // Random position within rect
                int x = rect.minX + random.nextInt(rect.maxX - rect.minX + 1);
                int z = rect.minZ + random.nextInt(rect.maxZ - rect.minZ + 1);

                // Skip if chunk not loaded (no sync generation)
                if (!level.hasChunk(x >> 4, z >> 4)) continue;

                // Check spawn conditions
                if (canSpawnArtifactSpot(level, x, z, zone.surface)) {
                    int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                    BlockPos pos = new BlockPos(x, surfaceY, z);
                    level.setBlock(pos, spotBlockFor(zone.surface).defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    // ======================== Chunk Load Spawn ========================

    /**
     * On chunk load, attempt to spawn artifact spots on eligible yellow_dirt blocks.
     * Uses a low per-block probability to avoid overloading, simulating the net density
     * SDV would have had across the map.
     */
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!serverLevel.dimension().equals(ModDimensions.STARDEW_VALLEY)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        // Defer artifact-spot spawning to the next server tick to avoid
        // recursive chunk loading (getHeight / setBlock inside a ChunkEvent.Load
        // callback can deadlock the server thread).
        final int savedChunkX = chunk.getPos().getMinBlockX();
        final int savedChunkZ = chunk.getPos().getMinBlockZ();
        serverLevel.getServer().tell(new net.minecraft.server.TickTask(
            serverLevel.getServer().getTickCount() + 1, () -> {
                RandomSource random = serverLevel.getRandom();
                boolean isDesertChunk = savedChunkX >= -372 && savedChunkX <= -259
                        && savedChunkZ >= 1285 && savedChunkZ <= 1423;
                for (int dx = 0; dx < 16; dx++) {
                    for (int dz = 0; dz < 16; dz++) {
                        int x = savedChunkX + dx;
                        int z = savedChunkZ + dz;
                        if (random.nextDouble() >= 0.00067) continue;
                        int surfaceY = serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                        BlockPos pos = new BlockPos(x, surfaceY, z);
                        BlockState state = serverLevel.getBlockState(pos);
                        BlockPos above = pos.above();
                        if (!serverLevel.getBlockState(above).isAir() || !serverLevel.canSeeSky(above)) continue;
                        if (state.is(ModBlocks.YELLOW_DIRT.get())) {
                            serverLevel.setBlock(pos, ModBlocks.ARTIFACT_SPOT_DIRT.get().defaultBlockState(),
                                    Block.UPDATE_ALL);
                        } else if (isDesertChunk && isAnySandstone(state)) {
                            serverLevel.setBlock(pos, ModBlocks.DESERT_ARTIFACT_SPOT.get().defaultBlockState(),
                                    Block.UPDATE_ALL);
                        }
                    }
                }
            }));
    }

    // ======================== Helpers ========================

    /**
     * Check if an artifact spot can spawn at (x, z).
     * Conditions: surface block matches the zone's surface kind, block above is air, can see sky.
     */
    private static boolean canSpawnArtifactSpot(ServerLevel level, int x, int z, SurfaceKind surface) {
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
        BlockPos pos = new BlockPos(x, surfaceY, z);
        BlockState state = level.getBlockState(pos);

        if (!matchesSurface(state, surface)) return false;

        BlockPos above = pos.above();
        return level.getBlockState(above).isAir() && level.canSeeSky(above);
    }

    /** 判断表面是否为该区域可生成远古斑点的原始方块。 */
    private static boolean matchesSurface(BlockState state, SurfaceKind surface) {
        return switch (surface) {
            case YELLOW_DIRT -> state.is(ModBlocks.YELLOW_DIRT.get());
            case DESERT_SANDSTONE -> isAnySandstone(state);
        };
    }

    /** 匹配所有砂岩变种（普通/红色 × 原始/切制/雕纹/平滑）以及沙子。 */
    private static boolean isAnySandstone(BlockState state) {
        return state.is(net.minecraft.world.level.block.Blocks.SANDSTONE)
                || state.is(net.minecraft.world.level.block.Blocks.CUT_SANDSTONE)
                || state.is(net.minecraft.world.level.block.Blocks.CHISELED_SANDSTONE)
                || state.is(net.minecraft.world.level.block.Blocks.SMOOTH_SANDSTONE)
                || state.is(net.minecraft.world.level.block.Blocks.RED_SANDSTONE)
                || state.is(net.minecraft.world.level.block.Blocks.CUT_RED_SANDSTONE)
                || state.is(net.minecraft.world.level.block.Blocks.CHISELED_RED_SANDSTONE)
                || state.is(net.minecraft.world.level.block.Blocks.SMOOTH_RED_SANDSTONE)
                || state.is(net.minecraft.world.level.block.Blocks.SAND);
    }

    /** 返回该表面类型对应的远古斑点方块。 */
    private static Block spotBlockFor(SurfaceKind surface) {
        return surface == SurfaceKind.DESERT_SANDSTONE
                ? ModBlocks.DESERT_ARTIFACT_SPOT.get()
                : ModBlocks.ARTIFACT_SPOT_DIRT.get();
    }

    /** 返回该表面类型被锄头锄后应还原为哪种原始方块。 */
    private static Block underlyingBlockFor(SurfaceKind surface) {
        return surface == SurfaceKind.DESERT_SANDSTONE
                ? net.minecraft.world.level.block.Blocks.SANDSTONE
                : ModBlocks.YELLOW_DIRT.get();
    }

    /**
     * Remove existing artifact spots with 15% chance each (SDV parity).
     * Returns number removed.
     */
    private static int removeAndCountSpots(ServerLevel level, ZoneRect rect, RandomSource random, SurfaceKind surface) {
        int removed = 0;
        Block spotBlock = spotBlockFor(surface);
        Block underlying = underlyingBlockFor(surface);
        for (int x = rect.minX; x <= rect.maxX; x++) {
            for (int z = rect.minZ; z <= rect.maxZ; z++) {
                if (!level.hasChunk(x >> 4, z >> 4)) continue;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                BlockPos pos = new BlockPos(x, surfaceY, z);
                BlockState state = level.getBlockState(pos);
                if (state.is(spotBlock)) {
                    if (random.nextDouble() < 0.15) {
                        level.setBlock(pos, underlying.defaultBlockState(), Block.UPDATE_ALL);
                        removed++;
                    }
                }
            }
        }
        return removed;
    }

    /**
     * Count existing artifact spots in a rect.
     */
    private static int countSpotsInRect(ServerLevel level, ZoneRect rect, SurfaceKind surface) {
        int count = 0;
        Block spotBlock = spotBlockFor(surface);
        for (int x = rect.minX; x <= rect.maxX; x++) {
            for (int z = rect.minZ; z <= rect.maxZ; z++) {
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                BlockPos pos = new BlockPos(x, surfaceY, z);
                if (level.getBlockState(pos).is(spotBlock)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Revert any farmland in a non-farm zone rect back to yellow_dirt.
     * This handles artifact spots that were dug by players — they become farmland,
     * and should reset to yellow_dirt the next day so new artifact spots can spawn.
     *
     * <p>但若耕地上方有作物 / forage / 任何非空气方块，则跳过——保留耕地，
     * 否则 BushBlock 类（forage、作物等）会失去支撑而被 vanilla 自动破坏并丢物品。
     */
    private static void revertFarmlandInRect(ServerLevel level, ZoneRect rect) {
        for (int x = rect.minX; x <= rect.maxX; x++) {
            for (int z = rect.minZ; z <= rect.maxZ; z++) {
                if (!level.hasChunk(x >> 4, z >> 4)) continue;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                BlockPos pos = new BlockPos(x, surfaceY, z);
                if (!level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.FARMLAND)) continue;
                // 上方有方块（作物/forage/装饰等）则保留耕地，避免支撑被毁
                if (!level.getBlockState(pos.above()).isAir()) continue;
                level.setBlock(pos, ModBlocks.YELLOW_DIRT.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }

    // ======================== First-Day Initial Spawn ========================

    /**
     * Called on first entry into the Stardew dimension. Ensures artifact spots exist on Day 1.
     * Uses SavedData to guarantee it only runs once per world.
     */
    public static void ensureInitialSpawn(ServerLevel level, int season) {
        if (!level.dimension().equals(ModDimensions.STARDEW_VALLEY)) return;

        ArtifactInitData data = level.getDataStorage().computeIfAbsent(
                ArtifactInitData.factory(), INIT_DATA_ID);
        if (!data.isInitialized()) {
            onNewDay(level, season);
            data.markInitialized();
            data.markDesertInitialized();
            return;
        }
        // 老存档补扫：沙漠远古斑点是后加的，需要一次性全 bbox 扫描
        if (!data.isDesertInitialized()) {
            RandomSource random = level.getRandom();
            for (SpawnZone zone : ZONES) {
                if (zone.surface != SurfaceKind.DESERT_SANDSTONE) continue;
                final double perBlockChance = 0.0008; // 首次补扫略高于每日量
                for (ZoneRect rect : zone.rects) {
                    for (int x = rect.minX; x <= rect.maxX; x++) {
                        for (int z = rect.minZ; z <= rect.maxZ; z++) {
                            if (!level.hasChunk(x >> 4, z >> 4)) continue;
                            if (random.nextDouble() >= perBlockChance) continue;
                            if (canSpawnArtifactSpot(level, x, z, zone.surface)) {
                                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                                BlockPos pos = new BlockPos(x, surfaceY, z);
                                level.setBlock(pos, spotBlockFor(zone.surface).defaultBlockState(), Block.UPDATE_ALL);
                            }
                        }
                    }
                }
            }
            data.markDesertInitialized();
        }
    }

    public static class ArtifactInitData extends SavedData {
        private boolean initialized;
        private boolean desertInitialized;

        public ArtifactInitData() {}

        private ArtifactInitData(CompoundTag tag) {
            this.initialized = tag.getBoolean("Initialized");
            this.desertInitialized = tag.getBoolean("DesertInitialized");
        }

        public boolean isInitialized() { return initialized; }
        public boolean isDesertInitialized() { return desertInitialized; }

        public void markInitialized() {
            this.initialized = true;
            setDirty();
        }

        public void markDesertInitialized() {
            this.desertInitialized = true;
            setDirty();
        }

        @Override
        @Nonnull
        public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
            tag.putBoolean("Initialized", initialized);
            tag.putBoolean("DesertInitialized", desertInitialized);
            return tag;
        }

        public static SavedData.Factory<ArtifactInitData> factory() {
            return new SavedData.Factory<>(ArtifactInitData::new, (tag, provider) -> new ArtifactInitData(tag));
        }
    }
}
