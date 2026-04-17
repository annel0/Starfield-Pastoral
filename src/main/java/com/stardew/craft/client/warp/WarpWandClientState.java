package com.stardew.craft.client.warp;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 客户端缓存：当前玩家已解锁的传送目的地。
 * <p>
 * 由服务端通过 {@code WarpWandSyncPayload} 同步，供 {@code WarpWheelScreen} 读取。
 */
public final class WarpWandClientState {

    private static final Set<String> unlockedDestinations = new HashSet<>();

    private WarpWandClientState() {}

    /** 服务端同步时调用 */
    public static void setUnlocked(Set<String> destinations) {
        unlockedDestinations.clear();
        unlockedDestinations.addAll(destinations);
    }

    /** 添加单个解锁（解锁成功时即时更新，无需等待全量同步） */
    public static void addUnlocked(String destinationId) {
        unlockedDestinations.add(destinationId);
    }

    /** 检查是否已解锁 */
    public static boolean isUnlocked(String destinationId) {
        return unlockedDestinations.contains(destinationId);
    }

    /** 获取所有已解锁的目的地 ID */
    public static Set<String> getUnlocked() {
        return Collections.unmodifiableSet(unlockedDestinations);
    }

    /** 清除缓存（退出服务器时） */
    public static void clear() {
        unlockedDestinations.clear();
    }
}
