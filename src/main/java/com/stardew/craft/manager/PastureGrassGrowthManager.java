package com.stardew.craft.manager;

import com.stardew.craft.block.nature.PastureGrassBlock;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PastureGrassGrowthManager extends SavedData {
    private static final String DATA_NAME = "stardew_pasture_grass_growth";

    public static PastureGrassGrowthManager get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(PastureGrassGrowthManager::new, (tag, provider) -> new PastureGrassGrowthManager()),
            DATA_NAME
        );
    }

    @SuppressWarnings("null")
    public void growDaily(ServerLevel level) {
        if (StardewTimeManager.get().getCurrentSeason() == 3) {
            cleanupWinterGrass(level);
            return;
        }

        RandomSource random = level.getRandom();
        List<BlockPos> grasses = collectNearbyPastureGrass(level);
        for (BlockPos pos : grasses) {
            if (!level.isLoaded(pos)) {
                continue;
            }
            if (!(level.getBlockState(pos).getBlock() instanceof PastureGrassBlock)) {
                continue;
            }
            if (random.nextDouble() >= 0.65) {
                continue;
            }

            for (BlockPos neighbor : List.of(pos.north(), pos.south(), pos.east(), pos.west())) {
                if (!level.isLoaded(neighbor) || random.nextDouble() >= 0.25) {
                    continue;
                }
                if (!level.getBlockState(neighbor).isAir()) {
                    continue;
                }

                BlockState sourceState = level.getBlockState(pos);
                BlockState grow = sourceState.getBlock().defaultBlockState().setValue(PastureGrassBlock.VARIANT, random.nextInt(3));
                if (grow.canSurvive(level, neighbor)) {
                    level.setBlock(neighbor, grow, 3);
                }
            }
        }
    }

    @SuppressWarnings("null")
    private void cleanupWinterGrass(ServerLevel level) {
        for (BlockPos pos : collectNearbyPastureGrass(level)) {
            if (level.isLoaded(pos) && level.getBlockState(pos).getBlock() instanceof PastureGrassBlock) {
                level.removeBlock(pos, false);
            }
        }
    }

    @SuppressWarnings("null")
    private List<BlockPos> collectNearbyPastureGrass(ServerLevel level) {
        Set<Long> scannedChunks = new HashSet<>();
        List<BlockPos> results = new ArrayList<>();

        level.players().forEach(player -> {
            int centerChunkX = player.blockPosition().getX() >> 4;
            int centerChunkZ = player.blockPosition().getZ() >> 4;
            int radius = 6;
            for (int cx = centerChunkX - radius; cx <= centerChunkX + radius; cx++) {
                for (int cz = centerChunkZ - radius; cz <= centerChunkZ + radius; cz++) {
                    long key = (((long) cx) << 32) ^ (cz & 0xFFFFFFFFL);
                    if (!scannedChunks.add(key) || !level.hasChunk(cx, cz)) {
                        continue;
                    }

                    int minX = cx << 4;
                    int minZ = cz << 4;
                    for (int x = minX; x < minX + 16; x++) {
                        for (int z = minZ; z < minZ + 16; z++) {
                            int top = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
                            int minY = Math.max(level.getMinBuildHeight(), top - 3);
                            int maxY = Math.min(level.getMaxBuildHeight() - 1, top + 1);
                            for (int y = minY; y <= maxY; y++) {
                                BlockPos pos = new BlockPos(x, y, z);
                                if (level.getBlockState(pos).getBlock() instanceof PastureGrassBlock) {
                                    results.add(pos.immutable());
                                }
                            }
                        }
                    }
                }
            }
        });

        return results;
    }

    @Override
    public net.minecraft.nbt.CompoundTag save(@Nonnull net.minecraft.nbt.CompoundTag tag, @Nonnull net.minecraft.core.HolderLookup.Provider provider) {
        return tag;
    }
}
