package com.stardew.craft.client.mining;

import net.minecraft.core.BlockPos;

/**
 * 客户端矿井状态缓存
 * 存储当前楼层的楼梯位置，供 LadderHighlightRenderer 使用。
 */
public final class ClientMiningState {

    private static int ladderFloor = -1;
    private static boolean ladderFound = false;
    private static BlockPos ladderPos = null;

    private ClientMiningState() {}

    public static void setLadderState(int floor, boolean found, BlockPos pos) {
        ladderFloor = floor;
        ladderFound = found;
        ladderPos = found ? pos : null;
    }

    public static boolean hasLadder() {
        return ladderFound;
    }

    public static BlockPos getLadderPos() {
        return ladderPos;
    }

    public static int getLadderFloor() {
        return ladderFloor;
    }

    public static void reset() {
        ladderFloor = -1;
        ladderFound = false;
        ladderPos = null;
    }
}
