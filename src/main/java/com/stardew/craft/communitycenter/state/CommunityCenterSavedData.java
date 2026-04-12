package com.stardew.craft.communitycenter.state;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.communitycenter.data.BundleDataManager;
import com.stardew.craft.communitycenter.data.BundleDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Global persistence for Community Center progress.
 * Mirrors SDV's CommunityCenter saved state:
 * - Per-bundle slot completion (boolean[] per bundleId)
 * - Per-bundle reward availability
 * - Per-area completion flag
 */
public class CommunityCenterSavedData extends SavedData {

    private static final String DATA_NAME = "stardew_community_center";

    // NBT tag names
    private static final String TAG_BUNDLES = "Bundles";
    private static final String TAG_BUNDLE_ID = "Id";
    private static final String TAG_SLOTS = "Slots";
    private static final String TAG_REWARD_AVAILABLE = "RewardAvailable";
    private static final String TAG_AREAS_COMPLETE = "AreasComplete";

    /** bundleId → boolean[] (one per ingredient slot, true = donated). */
    private final Map<Integer, boolean[]> bundleSlots = new HashMap<>();

    /** bundleId → whether the reward is available for pickup. */
    private final Map<Integer, Boolean> bundleRewards = new HashMap<>();

    /** areaId → whether fully complete. */
    private final boolean[] areasComplete = new boolean[7];

    // ── Static accessor ──

    public static CommunityCenterSavedData get() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) throw new IllegalStateException("No server available");

        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CommunityCenterSavedData::new, CommunityCenterSavedData::load),
                DATA_NAME
        );
    }

    public static CommunityCenterSavedData get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CommunityCenterSavedData::new, CommunityCenterSavedData::load),
                DATA_NAME
        );
    }

    // ── Load / Save ──

    public static CommunityCenterSavedData load(CompoundTag tag, HolderLookup.Provider provider) {
        CommunityCenterSavedData data = new CommunityCenterSavedData();

        // Load bundle slots
        ListTag bundlesList = tag.getList(TAG_BUNDLES, Tag.TAG_COMPOUND);
        for (int i = 0; i < bundlesList.size(); i++) {
            CompoundTag bundleTag = bundlesList.getCompound(i);
            int bundleId = bundleTag.getInt(TAG_BUNDLE_ID);
            byte[] slotBytes = bundleTag.getByteArray(TAG_SLOTS);
            boolean[] slots = new boolean[slotBytes.length];
            for (int j = 0; j < slotBytes.length; j++) {
                slots[j] = slotBytes[j] != 0;
            }
            data.bundleSlots.put(bundleId, slots);
            data.bundleRewards.put(bundleId, bundleTag.getBoolean(TAG_REWARD_AVAILABLE));
        }

        // Load area completion
        if (tag.contains(TAG_AREAS_COMPLETE)) {
            byte[] areaBytes = tag.getByteArray(TAG_AREAS_COMPLETE);
            for (int i = 0; i < Math.min(areaBytes.length, data.areasComplete.length); i++) {
                data.areasComplete[i] = areaBytes[i] != 0;
            }
        }

        StardewCraft.LOGGER.info("[COMMUNITY CENTER] Loaded state: {} bundles tracked, {} areas complete",
                data.bundleSlots.size(), data.countCompletedAreas());
        return data;
    }

    @Override
    public @Nonnull CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        // Save bundle slots
        ListTag bundlesList = new ListTag();
        for (Map.Entry<Integer, boolean[]> entry : bundleSlots.entrySet()) {
            CompoundTag bundleTag = new CompoundTag();
            bundleTag.putInt(TAG_BUNDLE_ID, entry.getKey());

            boolean[] slots = entry.getValue();
            byte[] slotBytes = new byte[slots.length];
            for (int i = 0; i < slots.length; i++) {
                slotBytes[i] = (byte) (slots[i] ? 1 : 0);
            }
            bundleTag.putByteArray(TAG_SLOTS, slotBytes);
            bundleTag.putBoolean(TAG_REWARD_AVAILABLE,
                    bundleRewards.getOrDefault(entry.getKey(), false));

            bundlesList.add(bundleTag);
        }
        tag.put(TAG_BUNDLES, bundlesList);

        // Save area completion
        byte[] areaBytes = new byte[areasComplete.length];
        for (int i = 0; i < areasComplete.length; i++) {
            areaBytes[i] = (byte) (areasComplete[i] ? 1 : 0);
        }
        tag.putByteArray(TAG_AREAS_COMPLETE, areaBytes);

        return tag;
    }

    // ── Bundle Slot Operations ──

    /** Get or initialize the slot array for a bundle. */
    public boolean[] getSlots(int bundleId) {
        return bundleSlots.computeIfAbsent(bundleId, id -> {
            BundleDefinition def = BundleDataManager.getBundle(id);
            int size = (def != null) ? def.totalSlots() : 0;
            return new boolean[size];
        });
    }

    /** Mark a specific ingredient slot as completed. Returns true if state changed. */
    public boolean markSlotComplete(int bundleId, int slotIndex) {
        boolean[] slots = getSlots(bundleId);
        if (slotIndex < 0 || slotIndex >= slots.length) return false;
        if (slots[slotIndex]) return false;

        slots[slotIndex] = true;
        setDirty();
        return true;
    }

    /** Check if a specific slot is complete. */
    public boolean isSlotComplete(int bundleId, int slotIndex) {
        boolean[] slots = bundleSlots.get(bundleId);
        if (slots == null || slotIndex < 0 || slotIndex >= slots.length) return false;
        return slots[slotIndex];
    }

    /** Count how many slots are filled for a bundle. */
    public int countFilledSlots(int bundleId) {
        boolean[] slots = bundleSlots.get(bundleId);
        if (slots == null) return 0;
        int count = 0;
        for (boolean s : slots) {
            if (s) count++;
        }
        return count;
    }

    /**
     * Check if a bundle is complete (filled slots >= requiredCount).
     * Mirrors SDV's numberOfCompleteBundles logic.
     */
    public boolean isBundleComplete(int bundleId) {
        BundleDefinition def = BundleDataManager.getBundle(bundleId);
        if (def == null) return false;
        return countFilledSlots(bundleId) >= def.requiredCount();
    }

    /** Mark all slots of a bundle as complete. */
    public void markBundleAllSlotsComplete(int bundleId) {
        boolean[] slots = getSlots(bundleId);
        boolean changed = false;
        for (int i = 0; i < slots.length; i++) {
            if (!slots[i]) {
                slots[i] = true;
                changed = true;
            }
        }
        if (changed) setDirty();
    }

    // ── Bundle Reward Operations ──

    public boolean isRewardAvailable(int bundleId) {
        return bundleRewards.getOrDefault(bundleId, false);
    }

    public void setRewardAvailable(int bundleId, boolean available) {
        bundleRewards.put(bundleId, available);
        setDirty();
    }

    // ── Area Operations ──

    public boolean isAreaComplete(int areaId) {
        if (areaId < 0 || areaId >= areasComplete.length) return false;
        return areasComplete[areaId];
    }

    /** Mark an area as complete. Returns true if state changed. */
    public boolean markAreaComplete(int areaId) {
        if (areaId < 0 || areaId >= areasComplete.length) return false;
        if (areasComplete[areaId]) return false;
        areasComplete[areaId] = true;
        setDirty();
        return true;
    }

    /**
     * Check if all 6 main areas (0-5) are complete.
     * Mirrors SDV areAllAreasComplete().
     */
    public boolean areAllAreasComplete() {
        for (int i = 0; i <= 5; i++) {
            if (!areasComplete[i]) return false;
        }
        return true;
    }

    /** Count total completed bundles across all areas. */
    public int numberOfCompleteBundles() {
        int count = 0;
        for (int bundleId : bundleSlots.keySet()) {
            if (isBundleComplete(bundleId)) count++;
        }
        return count;
    }

    private int countCompletedAreas() {
        int count = 0;
        for (boolean b : areasComplete) {
            if (b) count++;
        }
        return count;
    }

    // ── Bulk Operations (for debug commands) ──

    /** Reset all progress. */
    public void resetAll() {
        bundleSlots.clear();
        bundleRewards.clear();
        Arrays.fill(areasComplete, false);
        setDirty();
    }

    /** Complete everything (debug). */
    public void completeAll() {
        for (BundleDefinition def : BundleDataManager.getAllBundles()) {
            boolean[] slots = getSlots(def.bundleId());
            Arrays.fill(slots, true);
            bundleRewards.put(def.bundleId(), false); // reward already claimed
        }
        for (int i = 0; i <= 5; i++) {
            areasComplete[i] = true;
        }
        setDirty();
    }

    /** Get an unmodifiable view of all bundle slot data (for syncing). */
    public Map<Integer, boolean[]> getBundleSlotsView() {
        return Collections.unmodifiableMap(bundleSlots);
    }
}
