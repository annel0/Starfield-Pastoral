package com.stardew.craft.client;

import com.stardew.craft.blockentity.MuseumExhibitStandBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Client-side authoritative state of museum exhibit stand display items for the local player
 * in the current dimension. The server is authoritative; the cache lets newly loaded block
 * entities hydrate themselves even if the sync packet arrived before their chunk was ready,
 * and lets re-syncs clear stale items from stands the player has emptied.
 */
public final class ClientMuseumStandCache {
    private static final Map<BlockPos, String> ITEMS = new HashMap<>();
    private static final Set<MuseumExhibitStandBlockEntity> STANDS = Collections.newSetFromMap(new java.util.WeakHashMap<>());

    private ClientMuseumStandCache() {
    }

    public static void replaceAll(Map<BlockPos, String> posItems) {
        ITEMS.clear();
        if (posItems != null) {
            for (Map.Entry<BlockPos, String> entry : posItems.entrySet()) {
                String itemId = entry.getValue();
                if (itemId == null || itemId.isEmpty()) continue;
                ITEMS.put(entry.getKey().immutable(), itemId);
            }
        }
        applyToLoadedStands();
    }

    public static ItemStack lookupStack(BlockPos pos) {
        String itemId = ITEMS.get(pos);
        return resolveStack(itemId);
    }

    public static void register(MuseumExhibitStandBlockEntity be) {
        STANDS.add(be);
        be.setClientDisplayItem(lookupStack(be.getBlockPos()));
    }

    public static void unregister(MuseumExhibitStandBlockEntity be) {
        STANDS.remove(be);
    }

    public static void clear() {
        ITEMS.clear();
        // Do not iterate STANDS here; client level teardown already discards them.
    }

    private static void applyToLoadedStands() {
        for (MuseumExhibitStandBlockEntity be : new HashSet<>(STANDS)) {
            if (be == null || be.isRemoved()) continue;
            be.setClientDisplayItem(lookupStack(be.getBlockPos()));
        }
    }

    private static ItemStack resolveStack(String itemId) {
        if (itemId == null || itemId.isEmpty()) return ItemStack.EMPTY;
        ResourceLocation rl = ResourceLocation.tryParse(itemId);
        if (rl == null) return ItemStack.EMPTY;
        Item item = BuiltInRegistries.ITEM.get(rl);
        if (item == null || item == Items.AIR) return ItemStack.EMPTY;
        return new ItemStack(item);
    }
}
