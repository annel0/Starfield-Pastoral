package com.stardew.craft.combat.skill;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WindSpireTracker {

    private static final float CRIT_BONUS = 0.10f;
    private static final Map<UUID, Long> ACTIVE = new HashMap<>();

    private WindSpireTracker() {}

    public static void start(ServerPlayer player, long nowTick, int durationTicks) {
        if (player == null || durationTicks <= 0) {
            return;
        }
        ACTIVE.put(player.getUUID(), nowTick + durationTicks);
    }

    public static float getCritChanceBonus(ServerPlayer player, long nowTick) {
        if (player == null) {
            return 0.0f;
        }
        Long endTick = ACTIVE.get(player.getUUID());
        if (endTick == null) {
            return 0.0f;
        }
        if (nowTick > endTick) {
            ACTIVE.remove(player.getUUID());
            return 0.0f;
        }
        return CRIT_BONUS;
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}
