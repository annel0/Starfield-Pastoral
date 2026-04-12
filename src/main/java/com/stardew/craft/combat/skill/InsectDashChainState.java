package com.stardew.craft.combat.skill;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 昆虫头部 - 甲翼疾掠：连续突进的链式状态
 */
public final class InsectDashChainState {

    private static final int CHAIN_WINDOW_TICKS = 40; // 2s 内可继续连段

    private static final class State {
        private int stage;
        private long expireTick;

        private State(int stage, long expireTick) {
            this.stage = stage;
            this.expireTick = expireTick;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private InsectDashChainState() {}

    public static int getCurrentStage(ServerPlayer player, long nowTick) {
        if (player == null) return 0;
        State state = ACTIVE.get(player.getUUID());
        if (state == null || nowTick > state.expireTick) {
            ACTIVE.remove(player.getUUID());
            return 0;
        }
        return state.stage;
    }

    public static int getNextStage(ServerPlayer player, long nowTick) {
        int current = getCurrentStage(player, nowTick);
        return Math.min(3, current + 1);
    }

    public static void setStage(ServerPlayer player, long nowTick, int stage) {
        if (player == null) return;
        if (stage <= 0) {
            ACTIVE.remove(player.getUUID());
            return;
        }
        ACTIVE.put(player.getUUID(), new State(stage, nowTick + CHAIN_WINDOW_TICKS));
    }

    public static void clear(ServerPlayer player) {
        if (player == null) return;
        ACTIVE.remove(player.getUUID());
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}