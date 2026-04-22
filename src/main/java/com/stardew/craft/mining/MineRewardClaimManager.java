package com.stardew.craft.mining;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 矿井宝箱奖励领取记录（存档级）。
 *
 * 每个玩家每个存档每层只能领一次 slot 13 的奖励物品。
 * 多玩家之间独立（Lootr 风格）：A 领过不影响 B 能领。
 * 楼层隔天刷新重置宝箱 BlockEntity 时，这里的记录保留，所以第二天同一玩家不会再得奖励。
 */
public class MineRewardClaimManager extends SavedData {

    private static final String DATA_NAME = "stardew_mine_reward_claims";

    private final Map<UUID, Set<Integer>> claimedFloorsByPlayer = new HashMap<>();

    public MineRewardClaimManager() {
        super();
    }

    public static MineRewardClaimManager get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(
                MineRewardClaimManager::new,
                MineRewardClaimManager::load
            ),
            DATA_NAME
        );
    }

    public void clearPlayer(UUID playerId) {
        claimedFloorsByPlayer.remove(playerId);
        setDirty();
    }

    public boolean hasClaimed(UUID playerId, int floor) {
        Set<Integer> claimed = claimedFloorsByPlayer.get(playerId);
        return claimed != null && claimed.contains(floor);
    }

    public void markClaimed(UUID playerId, int floor) {
        claimedFloorsByPlayer.computeIfAbsent(playerId, k -> new HashSet<>()).add(floor);
        setDirty();
    }

    public static MineRewardClaimManager load(CompoundTag tag, HolderLookup.Provider provider) {
        MineRewardClaimManager manager = new MineRewardClaimManager();
        if (tag.contains("claims", Tag.TAG_LIST)) {
            ListTag playersList = tag.getList("claims", Tag.TAG_COMPOUND);
            for (int i = 0; i < playersList.size(); i++) {
                CompoundTag playerTag = playersList.getCompound(i);
                UUID uuid = playerTag.getUUID("UUID");
                Set<Integer> floors = new HashSet<>();
                ListTag floorList = playerTag.getList("Floors", Tag.TAG_INT);
                for (int j = 0; j < floorList.size(); j++) {
                    floors.add(floorList.getInt(j));
                }
                manager.claimedFloorsByPlayer.put(uuid, floors);
            }
        }
        return manager;
    }

    @Override
    public @NotNull CompoundTag save(@SuppressWarnings("null") @NotNull CompoundTag tag, @SuppressWarnings("null") @NotNull HolderLookup.Provider provider) {
        ListTag playersList = new ListTag();
        for (Map.Entry<UUID, Set<Integer>> entry : claimedFloorsByPlayer.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("UUID", entry.getKey());
            ListTag floorList = new ListTag();
            for (Integer floor : entry.getValue()) {
                floorList.add(net.minecraft.nbt.IntTag.valueOf(floor));
            }
            playerTag.put("Floors", floorList);
            playersList.add(playerTag);
        }
        tag.put("claims", playersList);
        return tag;
    }
}
