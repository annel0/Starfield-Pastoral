package com.stardew.craft.manager;

import net.minecraft.core.BlockPos;

public final class CoalForestArea {
    public static final int MIN_X = -265;
    public static final int MAX_X = -183;
    public static final int MIN_Y = 67;
    public static final int MAX_Y = 86;
    public static final int MIN_Z = -1;
    public static final int MAX_Z = 42;

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