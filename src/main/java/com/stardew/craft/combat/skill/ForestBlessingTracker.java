package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.ForestBlessingPayload;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 森林赐福：持续治疗追踪
 */
public final class ForestBlessingTracker {

    private static final class BlessingState {
        private long endTick;
        private long nextHealTick;
        private int healPerTick;
        private int intervalTicks;
        private int cooldownTicks;
        private String weaponId;
        private String skillId;
        private boolean cooldownApplied;

        private BlessingState(long endTick, long nextHealTick, int healPerTick, int intervalTicks,
                              int cooldownTicks, String weaponId, String skillId) {
            this.endTick = endTick;
            this.nextHealTick = nextHealTick;
            this.healPerTick = healPerTick;
            this.intervalTicks = intervalTicks;
            this.cooldownTicks = cooldownTicks;
            this.weaponId = weaponId;
            this.skillId = skillId;
            this.cooldownApplied = false;
        }
    }

    private static final Map<UUID, BlessingState> ACTIVE = new HashMap<>();

    private ForestBlessingTracker() {}

    public static void start(ServerPlayer player, long nowTick, int durationTicks, int healPerTick, int intervalTicks,
                             String weaponId, String skillId, int cooldownTicks) {
        if (player == null || durationTicks <= 0 || healPerTick <= 0 || intervalTicks <= 0
            || weaponId == null || skillId == null) {
            return;
        }

        long end = nowTick + durationTicks;
        long next = nowTick + intervalTicks;
        BlessingState state = ACTIVE.get(player.getUUID());
        if (state == null) {
            ACTIVE.put(player.getUUID(), new BlessingState(end, next, healPerTick, intervalTicks,
                cooldownTicks, weaponId, skillId));
        } else {
            state.endTick = Math.max(state.endTick, end);
            state.nextHealTick = next;
            state.healPerTick = healPerTick;
            state.intervalTicks = intervalTicks;
            state.cooldownTicks = cooldownTicks;
            state.weaponId = weaponId;
            state.skillId = skillId;
            state.cooldownApplied = false;
        }

        PacketDistributor.sendToPlayer(player, new ForestBlessingPayload(true, durationTicks));
    }

    public static void tick(ServerPlayer player, long nowTick) {
        if (player == null) return;
        BlessingState state = ACTIVE.get(player.getUUID());
        if (state == null) return;

        if (nowTick >= state.endTick) {
            if (!state.cooldownApplied && state.cooldownTicks > 0) {
                WeaponSkillCooldowns.setCooldown(player, state.weaponId, state.skillId, nowTick, state.cooldownTicks);
                state.cooldownApplied = true;
            }
            PacketDistributor.sendToPlayer(player, new ForestBlessingPayload(false, 0));
            ACTIVE.remove(player.getUUID());
            return;
        }

        while (nowTick >= state.nextHealTick) {
            heal(player, state.healPerTick);
            state.nextHealTick += state.intervalTicks;
        }
    }

    public static void healImmediate(ServerPlayer player, int amount) {
        if (player == null || amount <= 0) return;
        heal(player, amount);
    }

    public static boolean isActive(ServerPlayer player, long nowTick) {
        if (player == null) return false;
        BlessingState state = ACTIVE.get(player.getUUID());
        return state != null && nowTick < state.endTick;
    }

    private static void heal(ServerPlayer player, int amount) {
        int max = PlayerStardewDataAPI.getMaxHealth(player);
        int current = PlayerStardewDataAPI.getHealth(player);
        if (current >= max) return;
        int next = Math.min(max, current + amount);
        PlayerStardewDataAPI.setHealth(player, next);
    }
}
