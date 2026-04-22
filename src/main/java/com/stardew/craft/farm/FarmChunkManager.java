package com.stardew.craft.farm;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

/**
 * 农场区块生命周期管理器（轻量版）。
 *
 * 设计原则：**不再永久 forceLoad 农场区块**。
 * - 玩家在场 → MC 原生视距机制自然加载周围区块
 * - 玩家离开 → MC 原生机制自然卸载
 * - 作物系统使用每日结算（CropGrowthManager.growDaily），不依赖 random tick
 * - Utility 机器使用绝对时间戳比较，区块重新加载后自动补偿
 * - 离线追赶（OfflineFarmCatchUp）临时 forceLoad，完成后立即释放
 *
 * 仅保留玩家计数追踪（供其他系统查询）和临时 forceLoad 能力。
 */
public class FarmChunkManager {

    private static final FarmChunkManager INSTANCE = new FarmChunkManager();

    /** 每个农场当前在场玩家数（供其他系统查询） */
    private final Map<Integer, Integer> playerCounts = new HashMap<>();

    /** 临时 forceLoad 的区块（仅用于 catch-up，完成后应调用 releaseTempChunks 释放） */
    private final Map<Integer, Set<ChunkPos>> tempLoadedChunks = new HashMap<>();

    private FarmChunkManager() {}

    public static FarmChunkManager get() {
        return INSTANCE;
    }

    /**
     * 玩家进入农场时调用。
     * 仅追踪玩家计数，不 forceLoad（由 MC 原生视距加载）。
     */
    public void onPlayerEnterFarm(ServerLevel level, ServerPlayer player, FarmInstance farm) {
        int slot = farm.getSlotIndex();
        playerCounts.merge(slot, 1, Integer::sum);

        StardewCraft.LOGGER.debug("[FARM_CHUNK] Player {} entered farm slot {}, players={}",
                player.getName().getString(), slot, playerCounts.getOrDefault(slot, 0));
    }

    /**
     * 玩家离开农场时调用。
     * 仅减少玩家计数。
     */
    public void onPlayerLeaveFarm(ServerLevel level, ServerPlayer player, FarmInstance farm) {
        int slot = farm.getSlotIndex();
        int count = playerCounts.getOrDefault(slot, 1) - 1;
        if (count <= 0) {
            playerCounts.remove(slot);
            StardewCraft.LOGGER.debug("[FARM_CHUNK] No players in farm slot {}", slot);
        } else {
            playerCounts.put(slot, count);
        }
    }

    /**
     * 每 tick 调用（保留接口兼容，当前为空操作）。
     */
    public void tick(ServerLevel level) {
        // 不再需要处理延迟卸载——区块由 MC 原生视距管理
    }

    // ══════════════════════════════════════════
    //  临时 forceLoad（仅用于离线追赶等一次性操作）
    // ══════════════════════════════════════════

    /**
     * 离线追赶用：临时强制加载农场区块。
     * 调用方完成追赶后应调用 {@link #releaseTempChunks(ServerLevel, int)} 释放。
     */
    public void forceLoadFarmChunksForCatchUp(ServerLevel level, int slotIndex) {
        UUID owner = FarmInstanceRegistry.get().getOwnerBySlot(slotIndex);
        if (owner == null) return;
        FarmInstance farm = FarmInstanceRegistry.get().getFarm(owner);
        if (farm == null) return;

        BlockPos min = farm.getFarmBoundsMin();
        BlockPos max = farm.getFarmBoundsMax();
        int minCX = min.getX() >> 4;
        int maxCX = max.getX() >> 4;
        int minCZ = min.getZ() >> 4;
        int maxCZ = max.getZ() >> 4;

        Set<ChunkPos> chunks = new HashSet<>();
        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                level.setChunkForced(cx, cz, true);
                chunks.add(new ChunkPos(cx, cz));
            }
        }
        tempLoadedChunks.put(slotIndex, chunks);

        StardewCraft.LOGGER.info("[FARM_CHUNK] Temp force-loaded {} chunks for catch-up (slot {})",
                chunks.size(), slotIndex);
    }

    /**
     * 释放临时 forceLoad 的区块。
     */
    public void releaseTempChunks(ServerLevel level, int slotIndex) {
        Set<ChunkPos> chunks = tempLoadedChunks.remove(slotIndex);
        if (chunks == null) return;

        for (ChunkPos cp : chunks) {
            level.setChunkForced(cp.x, cp.z, false);
        }
        StardewCraft.LOGGER.info("[FARM_CHUNK] Released {} temp chunks for slot {}", chunks.size(), slotIndex);
    }

    // ══════════════════════════════════════════
    //  查询 & 清理
    // ══════════════════════════════════════════

    /**
     * 玩家下线时清理计数。
     */
    public void onPlayerLogout(ServerLevel level, ServerPlayer player) {
        FarmInstance farm = FarmInstanceRegistry.get().getFarmForPlayer(player.getUUID());
        if (farm == null) return;
        int slot = farm.getSlotIndex();
        if (playerCounts.containsKey(slot)) {
            onPlayerLeaveFarm(level, player, farm);
        }
    }

    /**
     * 判断某个农场当前是否有玩家在场。
     */
    public boolean isFarmLoaded(int slotIndex) {
        return playerCounts.containsKey(slotIndex);
    }

    /**
     * 获取农场在场玩家数。
     */
    public int getPlayerCount(int slotIndex) {
        return playerCounts.getOrDefault(slotIndex, 0);
    }

    /**
     * 服务器关闭时释放所有临时 forceLoad。
     */
    public void onServerStopping(ServerLevel level) {
        for (Map.Entry<Integer, Set<ChunkPos>> entry : tempLoadedChunks.entrySet()) {
            for (ChunkPos cp : entry.getValue()) {
                level.setChunkForced(cp.x, cp.z, false);
            }
        }
        tempLoadedChunks.clear();
        playerCounts.clear();
        StardewCraft.LOGGER.info("[FARM_CHUNK] Cleanup on server stop");
    }
}
