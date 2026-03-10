package com.stardew.craft.combat.skill;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DarkSwordBloodDebtTracker {

    private static final class State {
        private final long endTick;
        private final String weaponId;
        private final String skillId;
        private final int cooldownTicks;
        private State(long endTick) {
            this(endTick, "", "", 0);
        }

        private State(long endTick, String weaponId, String skillId, int cooldownTicks) {
            this.endTick = endTick;
            this.weaponId = weaponId;
            this.skillId = skillId;
            this.cooldownTicks = cooldownTicks;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();
    private static final float LIFESTEAL_RATIO = 0.20f;

    private DarkSwordBloodDebtTracker() {}

    public static void start(ServerPlayer player, long nowTick, int durationTicks, String weaponId, String skillId, int cooldownTicks) {
        if (player == null || durationTicks <= 0) {
            return;
        }
        ACTIVE.put(player.getUUID(), new State(nowTick + durationTicks, weaponId, skillId, cooldownTicks));
    }

    public static boolean isActive(ServerPlayer player, long nowTick) {
        if (player == null) return false;
        State state = ACTIVE.get(player.getUUID());
        return state != null && nowTick <= state.endTick;
    }

    public static float getLifestealRatio(ServerPlayer player, long nowTick) {
        return isActive(player, nowTick) ? LIFESTEAL_RATIO : 0.0f;
    }

    public static void tick(ServerPlayer player, long nowTick) {
        if (player == null) return;
        State state = ACTIVE.get(player.getUUID());
        if (state == null) return;
        if (nowTick > state.endTick) {
            finish(player, state, nowTick);
            ACTIVE.remove(player.getUUID());
        }
    }

    private static void finish(ServerPlayer player, State state, long nowTick) {
        if (player == null) {
            return;
        }
        if (state.cooldownTicks > 0 && !state.weaponId.isEmpty() && !state.skillId.isEmpty()) {
            WeaponSkillCooldowns.setCooldown(player, state.weaponId, state.skillId, nowTick, state.cooldownTicks);
        }
    }
}
