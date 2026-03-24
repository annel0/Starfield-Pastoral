package com.stardew.craft.museum;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Museum donation persistence (debug scaffolding for future museum logic).
 */
public class MuseumDonationData extends SavedData {
    private static final String DATA_NAME = "stardew_museum_donations";
    private static final String TAG_DONATED = "donated";
    private static final String TAG_DONATION_MODE = "donation_mode";
    private static final String TAG_SESSION_PENDING = "session_pending";
    private static final String TAG_STAND_ITEMS = "stand_items";

    private final Set<String> donatedItems = new HashSet<>();
    private final Set<String> sessionPendingItems = new HashSet<>();
    private final Map<String, String> standDisplayItems = new HashMap<>();
    private boolean donationModeActive;

    public static MuseumDonationData get(ServerLevel level) {
        return level.getServer()
                .overworld()
                .getDataStorage()
                .computeIfAbsent(
                        new SavedData.Factory<>(MuseumDonationData::new, MuseumDonationData::load),
                        DATA_NAME
                );
    }

    public static MuseumDonationData load(CompoundTag tag, HolderLookup.Provider provider) {
        MuseumDonationData data = new MuseumDonationData();
        ListTag list = tag.getList(TAG_DONATED, CompoundTag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            data.donatedItems.add(list.getString(i));
        }

        data.donationModeActive = tag.getBoolean(TAG_DONATION_MODE);

        ListTag pendingList = tag.getList(TAG_SESSION_PENDING, CompoundTag.TAG_STRING);
        for (int i = 0; i < pendingList.size(); i++) {
            data.sessionPendingItems.add(pendingList.getString(i));
        }

        CompoundTag standTag = tag.getCompound(TAG_STAND_ITEMS);
        for (String key : standTag.getAllKeys()) {
            if (key == null) {
                continue;
            }
            String itemId = Objects.requireNonNull(standTag.getString(key));
            if (!itemId.isBlank()) {
                data.standDisplayItems.put(key, itemId);
            }
        }
        return data;
    }

    @Override
    public @Nonnull CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        ListTag list = new ListTag();
        for (String id : donatedItems) {
            if (id != null) {
                list.add(StringTag.valueOf(id));
            }
        }
        tag.put(TAG_DONATED, list);

        tag.putBoolean(TAG_DONATION_MODE, donationModeActive);

        ListTag pendingList = new ListTag();
        for (String id : sessionPendingItems) {
            if (id != null) {
                pendingList.add(StringTag.valueOf(id));
            }
        }
        tag.put(TAG_SESSION_PENDING, pendingList);

        CompoundTag standTag = new CompoundTag();
        for (Map.Entry<String, String> entry : standDisplayItems.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                standTag.putString(Objects.requireNonNull(entry.getKey()), Objects.requireNonNull(entry.getValue()));
            }
        }
        tag.put(TAG_STAND_ITEMS, standTag);
        return tag;
    }

    public boolean donate(String itemId) {
        boolean added = donatedItems.add(itemId);
        if (added) {
            setDirty();
        }
        return added;
    }

    public boolean isDonated(String itemId) {
        return donatedItems.contains(itemId);
    }

    public Set<String> getDonatedItems() {
        return Collections.unmodifiableSet(donatedItems);
    }

    public boolean isDonationModeActive() {
        return donationModeActive;
    }

    public void startDonationMode() {
        if (!donationModeActive) {
            donationModeActive = true;
            setDirty();
        }
    }

    public EndSessionResult endDonationMode() {
        Set<String> missing = getMissingSessionItems();
        if (!missing.isEmpty()) {
            return new EndSessionResult(false, missing);
        }

        for (String itemId : standDisplayItems.values()) {
            donatedItems.add(itemId);
        }
        donationModeActive = false;
        sessionPendingItems.clear();
        setDirty();
        return new EndSessionResult(true, Collections.emptySet());
    }

    public boolean isItemDonated(String itemId) {
        return donatedItems.contains(itemId);
    }

    public boolean canDonateItem(String itemId) {
        if (!donationModeActive) {
            return false;
        }
        // Allow rearranging already-donated items as long as the same item
        // is not currently displayed on another stand.
        return !standDisplayItems.containsValue(itemId);
    }

    public void setStandDisplayedItem(String standKey, String itemId) {
        if (standKey == null || standKey.isBlank()) {
            return;
        }

        if (itemId == null || itemId.isBlank()) {
            if (standDisplayItems.remove(standKey) != null) {
                setDirty();
            }
            return;
        }

        String old = standDisplayItems.put(standKey, itemId);
        if (!itemId.equals(old)) {
            setDirty();
        }
    }

    public void markSessionPendingItem(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return;
        }
        if (sessionPendingItems.add(itemId)) {
            setDirty();
        }
    }

    private Set<String> getMissingSessionItems() {
        Set<String> requiredItems = new HashSet<>(donatedItems);
        requiredItems.addAll(sessionPendingItems);

        Set<String> displayed = new HashSet<>(standDisplayItems.values());
        Set<String> missing = new HashSet<>();
        
        for (String req : requiredItems) {
            if (!displayed.contains(req)) {
                missing.add(req);
            }
        }
        return missing;
    }

    public static String standKey(ServerLevel level, net.minecraft.core.BlockPos pos) {
        ResourceKey<Level> dimension = level.dimension();
        ResourceLocation dimId = dimension.location();
        return dimId + "|" + pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    public record EndSessionResult(boolean success, Set<String> missingItems) {
    }
}
