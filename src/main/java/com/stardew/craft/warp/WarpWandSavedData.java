package com.stardew.craft.warp;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

/**
 * 传送魔杖解锁状态持久化 — per-player。
 * <p>
 * 每位玩家拥有独立的已解锁目的地集合。
 * cost == 0 的目的地默认解锁，不存储。
 */
public class WarpWandSavedData extends SavedData {

    private static final String DATA_NAME = "stardew_warp_wand";
    private static final String TAG_PLAYERS = "Players";
    private static final String TAG_UUID = "UUID";
    private static final String TAG_UNLOCKED = "Unlocked";

    private final Map<UUID, Set<String>> playerUnlocks = new HashMap<>();

    // ── Static accessor ──

    public static WarpWandSavedData get() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) throw new IllegalStateException("No server available");
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new Factory<>(WarpWandSavedData::new, WarpWandSavedData::load),
                DATA_NAME
        );
    }

    // ── API ──

    public void clearPlayer(UUID playerId) {
        playerUnlocks.remove(playerId);
        setDirty();
    }

    /**
     * 检查玩家是否已解锁指定目的地。
     * cost == 0 的目的地默认解锁。
     */
    public boolean isUnlocked(UUID playerId, String destinationId) {
        WarpDestination dest = WarpDestinations.getById(destinationId);
        if (dest != null && dest.isFreeByDefault()) return true;
        Set<String> unlocked = playerUnlocks.get(playerId);
        return unlocked != null && unlocked.contains(destinationId);
    }

    /** 解锁指定目的地 */
    public void unlock(UUID playerId, String destinationId) {
        playerUnlocks.computeIfAbsent(playerId, k -> new HashSet<>()).add(destinationId);
        setDirty();
    }

    /** 获取玩家已解锁的所有目的地 ID（包含默认解锁的） */
    public Set<String> getUnlockedDestinations(UUID playerId) {
        Set<String> result = new HashSet<>();
        for (WarpDestination dest : WarpDestinations.getAll()) {
            if (dest.isFreeByDefault()) {
                result.add(dest.id());
            }
        }
        Set<String> extra = playerUnlocks.get(playerId);
        if (extra != null) {
            result.addAll(extra);
        }
        return result;
    }

    // ── NBT ──

    @Override
    public CompoundTag save(@javax.annotation.Nonnull CompoundTag tag, @javax.annotation.Nonnull HolderLookup.Provider registries) {
        ListTag playersList = new ListTag();
        for (var entry : playerUnlocks.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID(TAG_UUID, entry.getKey());
            ListTag unlockedList = new ListTag();
            for (String id : entry.getValue()) {
                unlockedList.add(StringTag.valueOf(id));
            }
            playerTag.put(TAG_UNLOCKED, unlockedList);
            playersList.add(playerTag);
        }
        tag.put(TAG_PLAYERS, playersList);
        return tag;
    }

    public static WarpWandSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        WarpWandSavedData data = new WarpWandSavedData();
        if (tag.contains(TAG_PLAYERS)) {
            ListTag playersList = tag.getList(TAG_PLAYERS, Tag.TAG_COMPOUND);
            for (int i = 0; i < playersList.size(); i++) {
                CompoundTag playerTag = playersList.getCompound(i);
                UUID uuid = playerTag.getUUID(TAG_UUID);
                Set<String> unlocked = new HashSet<>();
                ListTag unlockedList = playerTag.getList(TAG_UNLOCKED, Tag.TAG_STRING);
                for (int j = 0; j < unlockedList.size(); j++) {
                    unlocked.add(unlockedList.getString(j));
                }
                data.playerUnlocks.put(uuid, unlocked);
            }
        }
        StardewCraft.LOGGER.debug("[WARP] Loaded warp wand data for {} players", data.playerUnlocks.size());
        return data;
    }
}
