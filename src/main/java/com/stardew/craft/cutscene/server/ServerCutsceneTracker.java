package com.stardew.craft.cutscene.server;

import com.stardew.craft.cutscene.network.TriggerEventPayload;
import com.stardew.craft.warp.ModTeleport;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端侧记录当前正在观看 cutscene 的玩家。
 * 用于在事件期间禁止方块破坏/放置/实体交互等行为。
 * 同时把真实玩家临时切到旁观模式，避免剧情播放时被攻击、淹死、烧死或摔死。
 *
 * 注意：客户端完成事件后会通过 {@link com.stardew.craft.cutscene.network.MarkEventSeenPayload}
 * 回调服务端，届时会清除对应玩家的活动状态。
 */
public final class ServerCutsceneTracker {

    private static final Map<UUID, State> ACTIVE = new ConcurrentHashMap<>();

    private static final class State {
        private final GameType originalGameMode;
        private final ResourceKey<Level> originalDimension;
        private final double x;
        private final double y;
        private final double z;
        private final float yaw;
        private final float pitch;
        private boolean restoreOriginalPosition = true;

        private State(ServerPlayer player, GameType originalGameMode) {
            this.originalGameMode = originalGameMode;
            this.originalDimension = player.level().dimension();
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
            this.yaw = player.getYRot();
            this.pitch = player.getXRot();
        }
    }

    private ServerCutsceneTracker() {}

    /** 启动一个事件：记录玩家为活动状态并向其发送触发包。 */
    public static void startEvent(ServerPlayer player, String eventId) {
        markActive(player);
        PacketDistributor.sendToPlayer(player, new TriggerEventPayload(eventId));
    }

    /** 仅标记玩家为活动状态（不发送网络包）。 */
    public static void markActive(ServerPlayer player) {
        ACTIVE.computeIfAbsent(player.getUUID(), ignored -> {
            GameType original = player.gameMode.getGameModeForPlayer();
            State state = new State(player, original);
            protectPlayer(player);
            if (original != GameType.SPECTATOR) {
                player.setGameMode(GameType.SPECTATOR);
            }
            return state;
        });
    }

    public static void markServerMovedPlayer(ServerPlayer player) {
        State state = ACTIVE.get(player.getUUID());
        if (state != null) {
            state.restoreOriginalPosition = false;
        }
    }

    /** 清除玩家的活动状态。 */
    public static void clear(ServerPlayer player) {
        State state = ACTIVE.remove(player.getUUID());
        if (state == null) {
            return;
        }

        protectPlayer(player);
        if (state.restoreOriginalPosition) {
            net.minecraft.server.level.ServerLevel originalLevel = player.server.getLevel(state.originalDimension);
            if (originalLevel != null) {
                ModTeleport.to(player, originalLevel, state.x, state.y, state.z, state.yaw, state.pitch);
            }
        }
        if (player.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            player.setGameMode(state.originalGameMode != null ? state.originalGameMode : GameType.SURVIVAL);
        }
        player.setDeltaMovement(0.0D, 0.0D, 0.0D);
        player.fallDistance = 0.0f;
        player.invulnerableTime = Math.max(player.invulnerableTime, 40);
    }

    /** 清除玩家的活动状态；用于无法安全访问 ServerPlayer 实例的兜底路径。 */
    public static void clear(UUID playerId) {
        ACTIVE.remove(playerId);
    }

    /** 剧情期间持续清掉环境危险的积累状态。 */
    public static void tickProtection(ServerPlayer player) {
        if (!isActive(player.getUUID())) {
            return;
        }
        protectPlayer(player);
    }

    /** 判断玩家当前是否处于 cutscene 中。 */
    public static boolean isActive(UUID playerId) {
        return ACTIVE.containsKey(playerId);
    }

    private static void protectPlayer(ServerPlayer player) {
        player.setAirSupply(player.getMaxAirSupply());
        player.clearFire();
        player.fallDistance = 0.0f;
    }
}
