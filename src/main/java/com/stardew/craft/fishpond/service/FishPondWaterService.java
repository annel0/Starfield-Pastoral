package com.stardew.craft.fishpond.service;

import com.stardew.craft.fluid.ModFluids;
import com.stardew.craft.fishpond.model.FishPondRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public final class FishPondWaterService {
    private FishPondWaterService() {
    }

    public static void rebindPondWater(ServerLevel level,
                                       FishPondRecord previousRecord,
                                       Set<Long> newWaterCells) {
        if (previousRecord != null) {
            restoreVanillaWater(level, previousRecord.waterCells());
        }
        applyPondWater(level, newWaterCells);
    }

    public static void removePondWater(ServerLevel level, FishPondRecord record) {
        if (record == null) {
            return;
        }
        restoreVanillaWater(level, record.waterCells());
    }

    public static void applyPondWater(ServerLevel level, Set<Long> waterCells) {
        if (waterCells == null || waterCells.isEmpty()) {
            return;
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Long cell : waterCells) {
            cursor.set(BlockPos.of(cell));
            level.setBlock(cursor, ModFluids.FISH_POND_WATER.get().defaultFluidState().createLegacyBlock(), 2);
        }
    }

    public static void restoreVanillaWater(ServerLevel level, Set<Long> waterCells) {
        if (waterCells == null || waterCells.isEmpty()) {
            return;
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Long cell : waterCells) {
            cursor.set(BlockPos.of(cell));
            level.setBlock(cursor, Blocks.WATER.defaultBlockState(), 2);
        }
    }
}