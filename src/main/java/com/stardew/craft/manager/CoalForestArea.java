package com.stardew.craft.manager;

import net.minecraft.core.BlockPos;

public final class CoalForestArea {
    public static final int MIN_X = 129;
    public static final int MAX_X = 207;
    public static final int MIN_Y = -16;
    public static final int MAX_Y = 2;
    public static final int MIN_Z = -246;
    public static final int MAX_Z = -102;

    private CoalForestArea() {
    }

    public static boolean containsColumn(BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        return x >= MIN_X && x <= MAX_X && z >= MIN_Z && z <= MAX_Z;
    }

    public static boolean containsGround(BlockPos pos) {
        return containsColumn(pos) && pos.getY() >= MIN_Y && pos.getY() <= MAX_Y;
    }
}