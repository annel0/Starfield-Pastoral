package com.stardew.craft.manager;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.decor.ResourceClumpBlock;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;

public final class CoalForestClumpSpawnService {
    private static final String INIT_DATA_ID = "stardewcraft_coal_forest_clumps_init";
    private static final int MIN_LARGE_STUMP = 9;
    private static final int MAX_LARGE_STUMP = 11;
    private static final int MIN_HOLLOW_LOG = 3;
    private static final int MAX_HOLLOW_LOG = 4;
    private static final int MAX_ATTEMPTS_PER_SPAWN = 80;
    private static final int CLEAR_MAX_Y = CoalForestArea.MAX_Y + 8;

    private CoalForestClumpSpawnService() {
    }

    public static void onNewDay(ServerLevel level) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }

        ensureRegionChunksLoaded(level);
        clearExisting(level);

        RandomSource random = level.getRandom();
        int largeStumps = randomBetween(random, MIN_LARGE_STUMP, MAX_LARGE_STUMP);
        int hollowLogs = randomBetween(random, MIN_HOLLOW_LOG, MAX_HOLLOW_LOG);

        int spawnedLargeStumps = spawnBatch(level, random, ModBlocks.LARGE_STUMP.get(), largeStumps);
        int spawnedHollowLogs = spawnBatch(level, random, ModBlocks.HOLLOW_LOG.get(), hollowLogs);

        StardewCraft.LOGGER.info("[COAL_FOREST] Daily clump respawn: largeStump={}/{}, hollowLog={}/{}",
                spawnedLargeStumps, largeStumps, spawnedHollowLogs, hollowLogs);
    }

    public static void ensureInitialSpawn(ServerLevel level) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }

        CoalForestClumpInitData data = level.getDataStorage().computeIfAbsent(
                CoalForestClumpInitData.factory(), INIT_DATA_ID);
        if (data.initialized()) {
            return;
        }

        onNewDay(level);
        data.setInitialized(true);
    }

    private static int spawnBatch(ServerLevel level, RandomSource random, Block block, int targetCount) {
        int spawned = 0;
        for (int index = 0; index < targetCount; index++) {
            boolean placed = false;
            for (int attempt = 0; attempt < MAX_ATTEMPTS_PER_SPAWN; attempt++) {
                int x = randomBetween(random, CoalForestArea.MIN_X, CoalForestArea.MAX_X);
                int z = randomBetween(random, CoalForestArea.MIN_Z, CoalForestArea.MAX_Z);
                if (tryPlace(level, random, block, x, z)) {
                    spawned++;
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                StardewCraft.LOGGER.warn("[COAL_FOREST] Failed to place {} after {} attempts",
                        block.getDescriptionId(), MAX_ATTEMPTS_PER_SPAWN);
            }
        }
        return spawned;
    }

    private static boolean tryPlace(ServerLevel level, RandomSource random, Block block, int x, int z) {
        if (!(block instanceof ResourceClumpBlock clump)) {
            return false;
        }

        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
        BlockPos surfacePos = new BlockPos(x, surfaceY, z);
        if (!CoalForestArea.containsGround(surfacePos)) {
            return false;
        }

        if (!level.getBlockState(surfacePos).is(ModBlocks.YELLOW_DIRT.get())) {
            return false;
        }

        BlockPos mainPos = surfacePos.above();
        if (!level.getBlockState(mainPos).canBeReplaced() || !level.canSeeSky(mainPos)) {
            return false;
        }

        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos supportPos = mainPos.offset(dx, -1, dz);
                BlockPos lowerPos = mainPos.offset(dx, 0, dz);
                BlockPos upperPos = lowerPos.above();

                if (!CoalForestArea.containsColumn(lowerPos)) {
                    return false;
                }
                if (!level.getBlockState(supportPos).isFaceSturdy(level, supportPos, Direction.UP)) {
                    return false;
                }
                if (!level.getBlockState(lowerPos).canBeReplaced()) {
                    return false;
                }
                if (!level.getBlockState(upperPos).canBeReplaced()) {
                    return false;
                }
            }
        }

        BlockState state = clump.defaultBlockState()
                .setValue(com.stardew.craft.block.decor.MapDecorStaticBlock.PART,
                        com.stardew.craft.block.decor.MapDecorStaticBlock.Part.MAIN)
                .setValue(com.stardew.craft.block.decor.MapDecorStaticBlock.FACING, facing);
        level.setBlock(mainPos, state, Block.UPDATE_ALL);
        clump.setPlacedBy(level, mainPos, state, null, ItemStack.EMPTY);
        return true;
    }

    private static void clearExisting(ServerLevel level) {
        for (int x = CoalForestArea.MIN_X; x <= CoalForestArea.MAX_X; x++) {
            for (int z = CoalForestArea.MIN_Z; z <= CoalForestArea.MAX_Z; z++) {
                for (int y = CoalForestArea.MIN_Y; y <= CLEAR_MAX_Y; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!(state.getBlock() instanceof ResourceClumpBlock)) {
                        continue;
                    }
                    if (!state.hasProperty(com.stardew.craft.block.decor.MapDecorStaticBlock.PART)
                            || state.getValue(com.stardew.craft.block.decor.MapDecorStaticBlock.PART)
                            != com.stardew.craft.block.decor.MapDecorStaticBlock.Part.MAIN) {
                        continue;
                    }
                    level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                }
            }
        }
    }

    private static void ensureRegionChunksLoaded(ServerLevel level) {
        int minChunkX = CoalForestArea.MIN_X >> 4;
        int maxChunkX = CoalForestArea.MAX_X >> 4;
        int minChunkZ = CoalForestArea.MIN_Z >> 4;
        int maxChunkZ = CoalForestArea.MAX_Z >> 4;
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                level.getChunk(chunkX, chunkZ);
            }
        }
    }

    private static int randomBetween(RandomSource random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private static final class CoalForestClumpInitData extends SavedData {
        private boolean initialized;

        private CoalForestClumpInitData(boolean initialized) {
            this.initialized = initialized;
        }

        static SavedData.Factory<CoalForestClumpInitData> factory() {
            return new SavedData.Factory<>(
                    () -> new CoalForestClumpInitData(false),
                    CoalForestClumpInitData::load);
        }

        static CoalForestClumpInitData load(CompoundTag tag, HolderLookup.Provider registries) {
            return new CoalForestClumpInitData(tag.getBoolean("Initialized"));
        }

        boolean initialized() {
            return initialized;
        }

        void setInitialized(boolean value) {
            if (initialized != value) {
                initialized = value;
                setDirty();
            }
        }

        @Override
        public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
            tag.putBoolean("Initialized", initialized);
            return tag;
        }
    }

}