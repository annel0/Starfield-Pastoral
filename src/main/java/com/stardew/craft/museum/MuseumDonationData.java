package com.stardew.craft.museum;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Museum donation persistence (debug scaffolding for future museum logic).
 */
public class MuseumDonationData extends SavedData {
    private static final String DATA_NAME = "stardew_museum_donations";
    private static final String TAG_DONATED = "donated";

    private final Set<String> donatedItems = new HashSet<>();

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
}
