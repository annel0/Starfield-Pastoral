package com.stardew.craft.client.farm;

/**
 * 客户端缓存：当前玩家是否有待处理的加入农场申请。
 */
public final class FarmJoinClientState {

    private static boolean pendingJoinRequest;

    private FarmJoinClientState() {}

    public static boolean hasPendingJoinRequest() {
        return pendingJoinRequest;
    }

    public static void setPendingJoinRequest(boolean pending) {
        pendingJoinRequest = pending;
    }

    public static void clear() {
        pendingJoinRequest = false;
    }
}