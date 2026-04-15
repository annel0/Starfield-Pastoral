package com.stardew.craft.manager;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.nature.ForageBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.registries.DeferredBlock;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * SDV-parity forage spawning service.
 * Called once per day from StardewTimeManager.advanceDayWithSleepTime().
 *
 * <p>Replicates GameLocation.spawnObjects() logic:
 * <ul>
 *   <li>Each zone has MinDailyForageSpawn, MaxDailyForageSpawn, MaxSpawnedForageAtOnce</li>
 *   <li>Each forage entry has a season filter and a chance</li>
 *   <li>Random position within zone bounds, up to 11 attempts per spawn</li>
 *   <li>Must be on top of grass block (Town/Forest/Mountain) or any solid block (Beach)</li>
 *   <li>Must be outdoors (sky visible) for non-beach zones</li>
 * </ul>
 */
@SuppressWarnings("null")
public final class ForageSpawnService {

    private static final String INIT_DATA_ID = "stardewcraft_forage_init";

    private ForageSpawnService() {}

    // ======================== Forage Entry ========================

    private record ForageEntry(DeferredBlock<Block> block, int season, double chance) {
        /** season = -1 means all seasons */
        boolean matchesSeason(int currentSeason) {
            return season == -1 || season == currentSeason;
        }
    }

    // ======================== Zone Definition ========================

    /**
     * A rectangular region in the Stardew dimension where forage can spawn.
     */
    private record ZoneRect(int minX, int minZ, int maxX, int maxZ) {
    }

    private static ZoneRect rect(int x1, int z1, int x2, int z2) {
        return new ZoneRect(Math.min(x1, x2), Math.min(z1, z2), Math.max(x1, x2), Math.max(z1, z2));
    }

    private record ForageZone(
            String name,
            List<ZoneRect> rects,
            List<ForageEntry> entries,
            int minDailySpawn,
            int maxDailySpawn,
            int maxSpawnedAtOnce,
            boolean requireGrass // true = must be on grass_block; false = any solid block
    ) {}

    // ======================== Zone Definitions (SDV parity) ========================

    // Season constants
    private static final int SPRING = 0, SUMMER = 1, FALL = 2, WINTER = 3, ANY = -1;

    private static final List<ForageZone> ZONES = List.of(
            // ---- Town ----
            // SDV: Daffodil(Spring 0.9), Sweet Pea(Summer 0.9), Blackberry(Fall 0.6),
            //      Crocus(Winter 0.7), Crystal Fruit(Winter 0.1), Holly(Winter 0.5)
            new ForageZone("Town",
                    List.of(
                            rect(159, 221, 19, 193),
                            rect(51, 112, 21, 96),
                            rect(-1, 80, -18, 69)
                    ),
                    List.of(
                            new ForageEntry(ModBlocks.FORAGE_DAFFODIL, SPRING, 0.9),
                            new ForageEntry(ModBlocks.FORAGE_SWEET_PEA, SUMMER, 0.9),
                            new ForageEntry(ModBlocks.FORAGE_BLACKBERRY, FALL, 0.6),
                            new ForageEntry(ModBlocks.FORAGE_CROCUS, WINTER, 0.7),
                            new ForageEntry(ModBlocks.FORAGE_CRYSTAL_FRUIT, WINTER, 0.1),
                            new ForageEntry(ModBlocks.FORAGE_HOLLY, WINTER, 0.5)
                    ),
                    1, 4, 6, true),

            // ---- Forest (Cindersap) ----
            // SDV: Wild Horseradish(Spring 0.9), Dandelion(Spring 0.9),
            //      Spice Berry(Summer 0.6), Sweet Pea(Summer 0.9),
            //      Common Mushroom(Fall 0.9) → skipped (mushroom),
            //      Blackberry(Fall 0.9),
            //      Crocus(Winter 0.9), Crystal Fruit(Winter 0.9), Holly(Winter 0.5)
            new ForageZone("Forest",
                    List.of(
                            rect(197, -110, 134, -194),
                            rect(252, -114, 231, -138),
                            rect(302, -11, 221, 35)
                    ),
                    List.of(
                            new ForageEntry(ModBlocks.FORAGE_WILD_HORSERADISH, SPRING, 0.9),
                            new ForageEntry(ModBlocks.FORAGE_DANDELION, SPRING, 0.9),
                            new ForageEntry(ModBlocks.FORAGE_SPICE_BERRY, SUMMER, 0.6),
                            new ForageEntry(ModBlocks.FORAGE_SWEET_PEA, SUMMER, 0.9),
                            // Common mushroom skipped per user decision
                            new ForageEntry(ModBlocks.FORAGE_BLACKBERRY, FALL, 0.9),
                            new ForageEntry(ModBlocks.FORAGE_CROCUS, WINTER, 0.9),
                            new ForageEntry(ModBlocks.FORAGE_CRYSTAL_FRUIT, WINTER, 0.9),
                            new ForageEntry(ModBlocks.FORAGE_HOLLY, WINTER, 0.5)
                    ),
                    1, 4, 6, true),

            // ---- Mountain ----
            // SDV: Leek(Spring 0.7), Wild Horseradish(Spring 0.5),
            //      Spice Berry(Summer 0.5), Grape(Summer 0.8) → use sweet_pea as substitute,
            //      Common Mushroom(Fall 0.4) → skipped,
            //      Wild Plum(Fall 0.4), Hazelnut(Fall 0.9),
            //      Crystal Fruit(Winter 0.85), Crocus(Winter 0.9), Holly(Winter 0.5)
            new ForageZone("Mountain",
                    List.of(
                            rect(-239, 161, -196, 188),
                            rect(-207, 263, -245, 233),
                            rect(-292, 289, -324, 309),
                            rect(-72, 294, -105, 312)
                    ),
                    List.of(
                            new ForageEntry(ModBlocks.FORAGE_LEEK, SPRING, 0.7),
                            new ForageEntry(ModBlocks.FORAGE_WILD_HORSERADISH, SPRING, 0.5),
                            new ForageEntry(ModBlocks.FORAGE_SPICE_BERRY, SUMMER, 0.5),
                            new ForageEntry(ModBlocks.FORAGE_SWEET_PEA, SUMMER, 0.8),
                            // Common mushroom skipped
                            new ForageEntry(ModBlocks.FORAGE_WILD_PLUM, FALL, 0.4),
                            new ForageEntry(ModBlocks.FORAGE_HAZELNUT, FALL, 0.9),
                            new ForageEntry(ModBlocks.FORAGE_CRYSTAL_FRUIT, WINTER, 0.85),
                            new ForageEntry(ModBlocks.FORAGE_CROCUS, WINTER, 0.9),
                            new ForageEntry(ModBlocks.FORAGE_HOLLY, WINTER, 0.5)
                    ),
                    1, 4, 6, true),

            // ---- Beach ----
            // SDV Beach: Nautilus Shell(Winter 0.8), Rainbow Shell(Summer 0.5),
            //            Coral + Sea Urchin via tidepool logic (simplified: all-season)
            // requireGrass = false: any solid block
            new ForageZone("Beach",
                    List.of(
                            rect(-192, -139, -293, -182),
                            rect(-326, -173, -376, -148)
                    ),
                    List.of(
                            new ForageEntry(ModBlocks.FORAGE_CORAL, ANY, 0.8),
                            new ForageEntry(ModBlocks.FORAGE_SEA_URCHIN, ANY, 0.5),
                            new ForageEntry(ModBlocks.FORAGE_NAUTILUS_SHELL, WINTER, 0.8),
                            new ForageEntry(ModBlocks.FORAGE_RAINBOW_SHELL, SUMMER, 0.5)
                    ),
                    1, 4, 6, false)
    );

    // ======================== Spawn Weight for Beach Rects ========================
    // The second beach rect should spawn more items
    private static final double BEACH_SECOND_RECT_WEIGHT = 1.5;

    // ======================== Main Entry Point ========================

    /**
     * Called once per day from StardewTimeManager. Replicates SDV GameLocation.spawnObjects().
     */
    public static void onNewDay(ServerLevel level, int season) {
        RandomSource random = level.getRandom();
        StardewCraft.LOGGER.info("[ForageSpawn] onNewDay called, season={}", season);

        int totalSpawned = 0;
        for (ForageZone zone : ZONES) {
            // Filter entries for current season
            List<ForageEntry> possibleForage = new ArrayList<>();
            for (ForageEntry entry : zone.entries) {
                if (entry.matchesSeason(season)) {
                    possibleForage.add(entry);
                }
            }
            if (possibleForage.isEmpty()) {
                StardewCraft.LOGGER.info("[ForageSpawn] {} zone: no forage entries for season {}", zone.name, season);
                continue;
            }

            // Count existing forage blocks in zone (lightweight heightmap-based scan)
            int existingCount = countForageInZone(level, zone);
            if (existingCount >= zone.maxSpawnedAtOnce) {
                StardewCraft.LOGGER.info("[ForageSpawn] {} zone: already at max ({}/{})",
                        zone.name, existingCount, zone.maxSpawnedAtOnce);
                continue;
            }

            // Determine number to spawn (SDV: random between min and max inclusive)
            int numberToSpawn = zone.minDailySpawn + random.nextInt(
                    zone.maxDailySpawn - zone.minDailySpawn + 1);
            numberToSpawn = Math.min(numberToSpawn, zone.maxSpawnedAtOnce - existingCount);

            StardewCraft.LOGGER.info("[ForageSpawn] {} zone: existing={}, toSpawn={}, possibleEntries={}",
                    zone.name, existingCount, numberToSpawn, possibleForage.size());

            int spawned = 0;
            for (int i = 0; i < numberToSpawn; i++) {
                // SDV: up to 30 attempts per spawn (raised from 11 to compensate for
                // densely decorated terrain with grass/flowers occupying positions)
                for (int attempt = 0; attempt < 30; attempt++) {
                    // Pick random rect (with weight for beach second rect)
                    ZoneRect rect = pickRandomRect(zone, random);

                    // Random position within rect
                    int x = rect.minX + random.nextInt(rect.maxX - rect.minX + 1);
                    int z = rect.minZ + random.nextInt(rect.maxZ - rect.minZ + 1);

                    // Skip if chunk not loaded
                    if (!level.hasChunk(x >> 4, z >> 4)) continue;

                    // Use heightmap that ignores leaves to find surface quickly
                    int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
                    BlockPos surfacePos = new BlockPos(x, surfaceY, z);

                    // If the heightmap surface is a replaceable plant (weeds, flowers, etc.),
                    // look below it for the real solid ground (e.g. grass_block).
                    BlockState surfaceState = level.getBlockState(surfacePos);
                    if (isReplaceablePlant(surfaceState)) {
                        surfacePos = surfacePos.below();
                        surfaceState = level.getBlockState(surfacePos);
                    }
                    BlockPos placePos = surfacePos.above();

                    // Validate surface block
                    if (surfaceState.isAir() || surfaceState.getFluidState().isSource()) continue;

                    // Check placement conditions
                    if (!canPlaceForage(level, surfacePos, placePos, zone.requireGrass)) continue;

                    // Pick a random forage entry and apply chance
                    ForageEntry chosen = possibleForage.get(random.nextInt(possibleForage.size()));
                    if (random.nextDouble() > chosen.chance) continue;

                    // Remove any replaceable plant at the placement position before placing forage
                    BlockState existing = level.getBlockState(placePos);
                    if (!existing.isAir() && isReplaceablePlant(existing)) {
                        level.destroyBlock(placePos, false);
                    }

                    // Place the block
                    level.setBlock(placePos, chosen.block.get().defaultBlockState(), Block.UPDATE_ALL);
                    spawned++;
                    break; // success, move to next spawn slot
                }
            }

            totalSpawned += spawned;
            StardewCraft.LOGGER.info("[ForageSpawn] {} zone: spawned {} forage blocks", zone.name, spawned);
        }
        StardewCraft.LOGGER.info("[ForageSpawn] Day complete: total spawned = {}", totalSpawned);
    }

    // ======================== Helpers ========================

    private static ZoneRect pickRandomRect(ForageZone zone, RandomSource random) {
        List<ZoneRect> rects = zone.rects;
        if (rects.size() == 1) return rects.get(0);

        // For Beach: second rect has higher weight
        if (zone.name.equals("Beach") && rects.size() == 2) {
            double total = 1.0 + BEACH_SECOND_RECT_WEIGHT;
            if (random.nextDouble() * total < 1.0) {
                return rects.get(0);
            } else {
                return rects.get(1);
            }
        }

        // Default: uniform random
        return rects.get(random.nextInt(rects.size()));
    }

    /**
     * Check if forage can be placed at placePos on top of surfacePos.
     */
    private static boolean canPlaceForage(ServerLevel level, BlockPos surfacePos, BlockPos placePos,
                                          boolean requireGrass) {
        BlockState surfaceState = level.getBlockState(surfacePos);
        BlockState placeState = level.getBlockState(placePos);

        // Must be air or a replaceable plant (grass, flowers, ferns) at placement position
        if (!placeState.isAir() && !isReplaceablePlant(placeState)) return false;

        // Must see sky (outdoors check)
        if (!level.canSeeSky(placePos)) return false;

        if (requireGrass) {
            // Town/Forest/Mountain: must be on grass_block (SDV: "Spawnable" tile property on Back layer)
            return surfaceState.is(Blocks.GRASS_BLOCK);
        } else {
            // Beach: any solid block
            return surfaceState.isFaceSturdy(level, surfacePos, net.minecraft.core.Direction.UP);
        }
    }

    /**
     * Returns true if the block state is a weak decorative plant that forage can replace.
     * Includes short grass, tall grass, flowers, ferns, and double-tall plants.
     */
    private static boolean isReplaceablePlant(BlockState state) {
        Block block = state.getBlock();
        // Our mod's wild weeds (杂草)
        if (block instanceof com.stardew.craft.block.nature.WildWeedsBlock) return true;
        // Short grass and fern
        if (block == Blocks.SHORT_GRASS || block == Blocks.FERN) return true;
        // Tall grass and large fern
        if (block == Blocks.TALL_GRASS || block == Blocks.LARGE_FERN) return true;
        // All vanilla small flowers (poppy, dandelion, cornflower, etc.)
        if (block instanceof FlowerBlock) return true;
        // Double-tall flowers (sunflower, lilac, rose bush, peony)
        if (block instanceof DoublePlantBlock) return true;
        // Generic bush check for any modded short plants
        if (block instanceof TallGrassBlock) return true;
        // Check if the block is replaceable by world generation (covers most decorative plants)
        return state.canBeReplaced();
    }

    /**
     * Count existing ForageBlock instances in a zone using heightmap for fast surface lookup.
     * Only scans loaded chunks. Samples every other block to reduce cost.
     */
    private static int countForageInZone(ServerLevel level, ForageZone zone) {
        int count = 0;
        for (ZoneRect rect : zone.rects) {
            for (int x = rect.minX; x <= rect.maxX; x += 2) {
                for (int z = rect.minZ; z <= rect.maxZ; z += 2) {
                    // Skip unloaded chunks
                    if (!level.hasChunk(x >> 4, z >> 4)) continue;

                    // Heightmap gives the Y of the first motion-blocking block from the top + 1
                    int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                    // Check at surfaceY - 1 (topmost non-air) and surfaceY (in case forage is above a decoration)
                    BlockPos topPos = new BlockPos(x, surfaceY - 1, z);
                    BlockState state = level.getBlockState(topPos);
                    if (state.getBlock() instanceof ForageBlock) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    // ======================== First-Day Initial Spawn ========================

    /**
     * Called on first entry into the Stardew dimension. Ensures forage exists on Day 1.
     * Uses SavedData to guarantee it only runs once per world.
     */
    public static void ensureInitialSpawn(ServerLevel level, int season) {
        if (!level.dimension().equals(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY)) return;

        ForageInitData data = level.getDataStorage().computeIfAbsent(
                ForageInitData.factory(), INIT_DATA_ID);
        if (data.isInitialized()) return;

        StardewCraft.LOGGER.info("[ForageSpawn] Running first-day initial forage spawn (season={})", season);
        onNewDay(level, season);
        data.markInitialized();
    }

    public static class ForageInitData extends SavedData {
        private boolean initialized;

        public ForageInitData() {}

        private ForageInitData(CompoundTag tag) {
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

        public static SavedData.Factory<ForageInitData> factory() {
            return new SavedData.Factory<>(ForageInitData::new, (tag, provider) -> new ForageInitData(tag));
        }
    }
}
