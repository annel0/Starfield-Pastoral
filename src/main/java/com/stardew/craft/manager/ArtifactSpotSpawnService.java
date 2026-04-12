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
    private record SpawnZone(String name, ZoneRect[] rects, int tileWidth, int tileHeight) {}

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
    };

    // ======================== Daily Spawn (called from StardewTimeManager) ========================

    /**
     * Called once per day. Replicates SDV GameLocation.spawnObjects() artifact spot logic.
     */
    public static void onNewDay(ServerLevel level, int season) {
        if (!level.dimension().equals(ModDimensions.STARDEW_VALLEY)) return;

        RandomSource random = level.getRandom();
        int totalSpawned = 0;
        int totalRemoved = 0;

        for (SpawnZone zone : ZONES) {
            // 1. Count & remove existing spots (SDV: 15% removal per spot per day)
            int existingCount = 0;
            for (ZoneRect rect : zone.rects) {
                int removed = removeAndCountSpots(level, rect, random);
                totalRemoved += removed;
                existingCount += countSpotsInRect(level, rect);
            }

            // 2. Check cap: SDV — Farm stops if >0, non-Farm stops if >1,
            //    but in Winter spawning continues as long as count <= 4
            int threshold = "Farm".equals(zone.name) ? MAX_SPOTS_FARM : MAX_SPOTS_NON_FARM;
            if (existingCount > threshold && (season != 3 || existingCount > MAX_SPOTS_WINTER)) {
                continue;
            }

            // 3. SDV spawn loop: chanceForNewArtifactAttempt
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
                if (canSpawnArtifactSpot(level, x, z)) {
                    int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                    BlockPos pos = new BlockPos(x, surfaceY, z);
                    level.setBlock(pos, ModBlocks.ARTIFACT_SPOT_DIRT.get().defaultBlockState(), Block.UPDATE_ALL);
                    totalSpawned++;
                }
            }
        }

        if (totalSpawned > 0 || totalRemoved > 0) {
            StardewCraft.LOGGER.info("[ArtifactSpot] Daily: spawned={}, removed={}", totalSpawned, totalRemoved);
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
                int spawned = 0;
                for (int dx = 0; dx < 16; dx++) {
                    for (int dz = 0; dz < 16; dz++) {
                        int x = savedChunkX + dx;
                        int z = savedChunkZ + dz;
                        if (random.nextDouble() >= 0.00067) continue;
                        int surfaceY = serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                        BlockPos pos = new BlockPos(x, surfaceY, z);
                        BlockState state = serverLevel.getBlockState(pos);
                        if (state.is(ModBlocks.YELLOW_DIRT.get())) {
                            BlockPos above = pos.above();
                            if (serverLevel.getBlockState(above).isAir() && serverLevel.canSeeSky(above)) {
                                serverLevel.setBlock(pos, ModBlocks.ARTIFACT_SPOT_DIRT.get().defaultBlockState(),
                                        Block.UPDATE_ALL);
                                spawned++;
                            }
                        }
                    }
                }
                if (spawned > 0) {
                    StardewCraft.LOGGER.debug("[ArtifactSpot] Chunk [{},{}]: spawned {} spots on load",
                            savedChunkX >> 4, savedChunkZ >> 4, spawned);
                }
            }));
    }

    // ======================== Helpers ========================

    /**
     * Check if an artifact spot can spawn at (x, z).
     * Conditions: surface block is yellow_dirt, block above is air, can see sky.
     */
    private static boolean canSpawnArtifactSpot(ServerLevel level, int x, int z) {
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
        BlockPos pos = new BlockPos(x, surfaceY, z);
        BlockState state = level.getBlockState(pos);

        if (!state.is(ModBlocks.YELLOW_DIRT.get())) return false;

        BlockPos above = pos.above();
        return level.getBlockState(above).isAir() && level.canSeeSky(above);
    }

    /**
     * Remove existing artifact spots with 15% chance each (SDV parity).
     * Returns number removed.
     */
    private static int removeAndCountSpots(ServerLevel level, ZoneRect rect, RandomSource random) {
        int removed = 0;
        for (int x = rect.minX; x <= rect.maxX; x++) {
            for (int z = rect.minZ; z <= rect.maxZ; z++) {
                if (!level.hasChunk(x >> 4, z >> 4)) continue;
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                BlockPos pos = new BlockPos(x, surfaceY, z);
                BlockState state = level.getBlockState(pos);
                if (state.is(ModBlocks.ARTIFACT_SPOT_DIRT.get())) {
                    if (random.nextDouble() < 0.15) {
                        level.setBlock(pos, ModBlocks.YELLOW_DIRT.get().defaultBlockState(), Block.UPDATE_ALL);
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
    private static int countSpotsInRect(ServerLevel level, ZoneRect rect) {
        int count = 0;
        for (int x = rect.minX; x <= rect.maxX; x++) {
            for (int z = rect.minZ; z <= rect.maxZ; z++) {
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
                BlockPos pos = new BlockPos(x, surfaceY, z);
                if (level.getBlockState(pos).is(ModBlocks.ARTIFACT_SPOT_DIRT.get())) {
                    count++;
                }
            }
        }
        return count;
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
        if (data.isInitialized()) return;

        StardewCraft.LOGGER.info("[ArtifactSpot] Running first-day initial artifact spot spawn (season={})", season);
        onNewDay(level, season);
        data.markInitialized();
    }

    public static class ArtifactInitData extends SavedData {
        private boolean initialized;

        public ArtifactInitData() {}

        private ArtifactInitData(CompoundTag tag) {
            this.initialized = tag.getBoolean("Initialized");
        }

        public boolean isInitialized() { return initialized; }

        public void markInitialized() {
            this.initialized = true;
            setDirty();
        }

        @Override
        @Nonnull
        public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
            tag.putBoolean("Initialized", initialized);
            return tag;
        }

        public static SavedData.Factory<ArtifactInitData> factory() {
            return new SavedData.Factory<>(ArtifactInitData::new, (tag, provider) -> new ArtifactInitData(tag));
        }
    }
}
