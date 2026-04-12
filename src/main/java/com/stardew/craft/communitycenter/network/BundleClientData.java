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
    private int version = 0;

    private BundleClientData() {}

    public void update(Map<Integer, boolean[]> newSlots, boolean[] newAreas, Map<Integer, Boolean> newRewards) {
        bundleSlots.clear();
        bundleSlots.putAll(newSlots);

        System.arraycopy(newAreas, 0, areasComplete, 0, Math.min(newAreas.length, 7));

        bundleRewards.clear();
        bundleRewards.putAll(newRewards);

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

    public int getVersion() {
        return version;
    }

    public void clear() {
        bundleSlots.clear();
        bundleRewards.clear();
        for (int i = 0; i < 7; i++) areasComplete[i] = false;
        version++;
    }
}
