package com.stardew.craft.farm;

import com.stardew.craft.core.FarmAreaResolver;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 农场相关 SavedData 日处理工具。
 * 核心原则：「只处理在线玩家的农场 + 公共区域」。
 *
 * 位置数据天然按坐标区分（因为每个农场在不同的网格槽位），
 * 不需要将 Map 改为 per-UUID 嵌套结构。
 * 只需要在每日处理时过滤掉离线玩家的位置。
 */
public final class FarmDailyProcessHelper {

    private FarmDailyProcessHelper() {}

    /** 日结算期间缓存的在线玩家 UUID 集合，避免对每个位置线性搜索玩家列表 */
    private static Set<UUID> cachedOnlinePlayers;

    /**
     * 日结算开始前调用，预计算在线玩家集合。
     */
    public static void beginDailyProcess(ServerLevel level) {
        cachedOnlinePlayers = new HashSet<>();
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            cachedOnlinePlayers.add(player.getUUID());
        }

        // 递减在线玩家农场的跨季宽限倒计时
        tickGracePeriods(level);
    }

    /**
     * 日结算结束后调用，释放缓存。
     */
    public static void endDailyProcess() {
        cachedOnlinePlayers = null;
    }

    /**
     * 判断某个位置是否应该在本次日处理中被处理。
     * - 公共区域位置 → 始终处理
     * - 玩家农场位置 → 仅当该玩家在线时处理
     *
     * @return true 表示应该处理
     */
    public static boolean shouldProcessPosition(ServerLevel level, BlockPos pos) {
        // 不在农场实例区域 → 公共区域，始终处理
        if (!FarmInstanceAllocator.isInFarmInstanceRegion(pos)) return true;

        UUID owner = FarmAreaResolver.getOwnerAt(pos);
        if (owner == null) return false; // 在实例区域但无人拥有

        // 检查 owner 或任一成员是否在线
        FarmInstance farm = FarmInstanceRegistry.get().getFarm(owner);
        if (farm == null) return false;
        if (cachedOnlinePlayers != null) {
            for (UUID farmer : farm.getAllFarmers()) {
                if (cachedOnlinePlayers.contains(farmer)) return true;
            }
            return false;
        }
        for (UUID farmer : farm.getAllFarmers()) {
            if (level.getServer().getPlayerList().getPlayer(farmer) != null) return true;
        }
        return false;
    }

    /**
     * 获取当前所有在线且拥有农场的玩家 UUID 集合。
     */
    public static Set<UUID> getOnlineFarmOwners(ServerLevel level) {
        Set<UUID> owners = new HashSet<>();
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            if (FarmInstanceRegistry.get().hasFarm(player.getUUID())) {
                owners.add(player.getUUID());
            }
        }
        return owners;
    }

    /**
     * 获取某个玩家的农场实例（owner 或 member 均可）。
     */
    @Nullable
    public static FarmInstance getPlayerFarm(UUID playerUUID) {
        return FarmInstanceRegistry.get().getFarmForPlayer(playerUUID);
    }

    // ── 跨季宽限期倒计时 ──

    /**
     * 每日结算开始时调用。对所有有在线成员的农场递减宽限天数，
     * 并向在线成员发送提示消息。
     */
    private static void tickGracePeriods(ServerLevel level) {
        FarmInstanceRegistry registry = FarmInstanceRegistry.get();
        boolean dirty = false;

        for (FarmInstance farm : registry.getAllFarms()) {
            int remaining = farm.getGraceDaysLeft();
            if (remaining <= 0) continue;

            // 检查是否有任一成员在线
            boolean anyOnline = false;
            for (UUID farmer : farm.getAllFarmers()) {
                if (cachedOnlinePlayers != null ? cachedOnlinePlayers.contains(farmer)
                        : level.getServer().getPlayerList().getPlayer(farmer) != null) {
                    anyOnline = true;
                    break;
                }
            }
            if (!anyOnline) continue;

            remaining--;
            farm.setGraceDaysLeft(remaining);
            dirty = true;

            // 向所有在线成员发送提示
            for (UUID farmer : farm.getAllFarmers()) {
                ServerPlayer p = level.getServer().getPlayerList().getPlayer(farmer);
                if (p == null) continue;
                if (remaining > 0) {
                    p.sendSystemMessage(
                            net.minecraft.network.chat.Component.translatable(
                                    "stardewcraft.farm.grace_period.remaining", remaining));
                } else {
                    p.sendSystemMessage(
                            net.minecraft.network.chat.Component.translatable(
                                    "stardewcraft.farm.grace_period.expired"));
                }
            }
        }

        if (dirty) registry.setDirty();
    }
}
