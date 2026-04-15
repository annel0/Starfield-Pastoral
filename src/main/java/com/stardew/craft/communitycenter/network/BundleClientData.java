package com.stardew.craft.communitycenter.network;

import java.util.HashMap;
import java.util.Map;

/**
 * Client-side cache for Community Center progress data.
 * Updated by BundleSyncPayload from the server.
 * Read by BundleScreen for rendering.
 */
public final class BundleClientData {

    public static final BundleClientData INSTANCE = new BundleClientData();

    private final Map<Integer, boolean[]> bundleSlots = new HashMap<>();
    private final boolean[] areasComplete = new boolean[7];
    private final Map<Integer, Boolean> bundleRewards = new HashMap<>();
    private boolean canReadJunimoText = false;
    private int version = 0;

    /**
     * 星盘显示用的星星数，由 StarPlacedPayload 驱动。
     * 和 areasComplete 分离，这样区域完成时不会立刻改变星盘纹理，
     * 要等 Junimo 走到星盘放完星之后才递增。
     */
    private int displayStarCount = 0;
    private boolean displayStarsInitialized = false;

    private BundleClientData() {}

    public void update(Map<Integer, boolean[]> newSlots, boolean[] newAreas, Map<Integer, Boolean> newRewards, boolean canRead) {
        bundleSlots.clear();
        bundleSlots.putAll(newSlots);

        System.arraycopy(newAreas, 0, areasComplete, 0, Math.min(newAreas.length, 7));

        // 首次同步（登录）：displayStarCount 追平到实际完成数
        // 后续同步（存入物品）：不动 displayStarCount，等 Junimo 放星的包来递增
        if (!displayStarsInitialized) {
            displayStarsInitialized = true;
            int count = 0;
            for (int i = 0; i <= 5; i++) {
                if (areasComplete[i]) count++;
            }
            displayStarCount = count;
        }

        bundleRewards.clear();
        bundleRewards.putAll(newRewards);

        this.canReadJunimoText = canRead;
        version++;
    }

    public boolean isSlotComplete(int bundleId, int slotIndex) {
        boolean[] slots = bundleSlots.get(bundleId);
        if (slots == null || slotIndex < 0 || slotIndex >= slots.length) return false;
        return slots[slotIndex];
    }

    public boolean isBundleComplete(int bundleId) {
        boolean[] slots = bundleSlots.get(bundleId);
        if (slots == null) return false;
        for (boolean s : slots) {
            if (!s) return false;
        }
        return true;
    }

    public boolean isAreaComplete(int areaId) {
        if (areaId < 0 || areaId >= 7) return false;
        return areasComplete[areaId];
    }

    public boolean isRewardAvailable(int bundleId) {
        return bundleRewards.getOrDefault(bundleId, false);
    }

    public boolean hasAnyRewardForArea(int areaId) {
        // Check if any bundle in this area has a claimable reward
        for (var entry : bundleRewards.entrySet()) {
            if (entry.getValue()) {
                // We'd need area info here; BundleDataManager provides it
                var def = com.stardew.craft.communitycenter.data.BundleDataManager.getBundle(entry.getKey());
                if (def != null && def.areaId() == areaId) {
                    return true;
                }
            }
        }
        return false;
    }

    public int countFilledSlots(int bundleId) {
        boolean[] slots = bundleSlots.get(bundleId);
        if (slots == null) return 0;
        int count = 0;
        for (boolean s : slots) {
            if (s) count++;
        }
        return count;
    }

    public boolean canReadJunimoText() {
        return canReadJunimoText;
    }

    public int getVersion() {
        return version;
    }

    /** 星盘渲染器使用: 获取当前应显示的星星数 */
    public int getDisplayStarCount() {
        return displayStarCount;
    }

    /** Junimo 放完一颗星后调用 (由 StarPlacedPayload 触发) */
    public void incrementDisplayStars() {
        displayStarCount = Math.min(6, displayStarCount + 1);
    }

    /** 登录时从服务端同步当前已完成区域数作为初始值 */
    public void setDisplayStarCount(int count) {
        displayStarCount = Math.max(0, Math.min(6, count));
    }

    public void clear() {
        bundleSlots.clear();
        bundleRewards.clear();
        for (int i = 0; i < 7; i++) areasComplete[i] = false;
        displayStarCount = 0;
        displayStarsInitialized = false;
        version++;
    }
}
