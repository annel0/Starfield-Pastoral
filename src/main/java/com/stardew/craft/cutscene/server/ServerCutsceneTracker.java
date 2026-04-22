package com.stardew.craft.cutscene.server;

import com.stardew.craft.cutscene.network.TriggerEventPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端侧记录当前正在观看 cutscene 的玩家。
 * 用于在事件期间禁止方块破坏/放置/实体交互等行为。
 *
 * 注意：客户端完成事件后会通过 {@link com.stardew.craft.cutscene.network.MarkEventSeenPayload}
 * 回调服务端，届时会清除对应玩家的活动状态。
 */
public final class ServerCutsceneTracker {

    private static final Set<UUID> ACTIVE = ConcurrentHashMap.newKeySet();

    private ServerCutsceneTracker() {}

    /** 启动一个事件：记录玩家为活动状态并向其发送触发包。 */
    public static void startEvent(ServerPlayer player, String eventId) {
        ACTIVE.add(player.getUUID());
        PacketDistributor.sendToPlayer(player, new TriggerEventPayload(eventId));
    }

    /** 仅标记玩家为活动状态（不发送网络包）。 */
    public static void markActive(UUID playerId) {
        ACTIVE.add(playerId);
    }

    /** 清除玩家的活动状态。 */
    public static void clear(UUID playerId) {
        ACTIVE.remove(playerId);
    }

    /** 判断玩家当前是否处于 cutscene 中。 */
    public static boolean isActive(UUID playerId) {
        return ACTIVE.contains(playerId);
    }
}
