package com.stardew.craft.core;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * 农场区域边界判定工具。
 * 坐标与 TotemPoleBlock.FARM_BOUNDS / FarmInitializer 保持一致。
 */
public final class FarmAreaHelper {

    private static final int MIN_X = 36;
    private static final int MAX_X = 311;
    private static final int MIN_Y = -18;
    private static final int MAX_Y = 103;
    private static final int MIN_Z = 37;
    private static final int MAX_Z = 154;

    private FarmAreaHelper() {}

    /**
     * 判断某个方块位置是否在星露谷维度的农场区域内。
     */
    public static boolean isInFarmArea(Level level, BlockPos pos) {
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) {
            return false;
        }
        return pos.getX() >= MIN_X && pos.getX() <= MAX_X
            && pos.getY() >= MIN_Y && pos.getY() <= MAX_Y
            && pos.getZ() >= MIN_Z && pos.getZ() <= MAX_Z;
    }

    /**
     * 判断某个方块位置是否在星露谷维度但不在农场区域内。
     */
    public static boolean isInStardewButNotFarm(Level level, BlockPos pos) {
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) {
            return false;
        }
        return !(pos.getX() >= MIN_X && pos.getX() <= MAX_X
              && pos.getY() >= MIN_Y && pos.getY() <= MAX_Y
              && pos.getZ() >= MIN_Z && pos.getZ() <= MAX_Z);
    }
}
