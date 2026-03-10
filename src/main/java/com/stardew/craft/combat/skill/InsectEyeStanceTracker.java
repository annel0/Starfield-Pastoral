package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.InsectEyeStancePayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 昆虫头部 - 复眼架势：1.5秒内首击必暴
 */
public final class InsectEyeStanceTracker {

    private static final class State {
        private long endTick;
        private final String weaponId;
        private final String skillId;
        private final int cooldownTicks;
        private boolean cooldownApplied;
        private boolean firstHitPending;

        private State(long endTick, String weaponId, String skillId, int cooldownTicks) {
            this.endTick = endTick;
            this.weaponId = weaponId;
            this.skillId = skillId;
            this.cooldownTicks = cooldownTicks;
            this.cooldownApplied = false;
            this.firstHitPending = true;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private InsectEyeStanceTracker() {}

    public static void start(ServerPlayer player, long nowTick, int durationTicks, String weaponId, String skillId, int cooldownTicks) {
        if (player == null || durationTicks <= 0 || weaponId == null || skillId == null) {
            return;
        }
        ACTIVE.put(player.getUUID(), new State(nowTick + durationTicks, weaponId, skillId, cooldownTicks));
        PacketDistributor.sendToPlayer(player, new InsectEyeStancePayload(true, durationTicks));
    }

    public static boolean isActive(ServerPlayer player, long nowTick) {
        if (player == null) return false;
        State state = ACTIVE.get(player.getUUID());
        return state != null && nowTick <= state.endTick;
    }

    public static SkillContext getSkillContext(ServerPlayer player, long nowTick) {
        if (player == null) return null;
        State state = ACTIVE.get(player.getUUID());
        if (state == null || nowTick > state.endTick) {
            return null;
        }

        boolean guaranteedCrit = state.firstHitPending;
        if (state.firstHitPending) {
            state.firstHitPending = false;
        }

        return SkillContext.builder()
            .skillId(state.skillId)
            .tier(SkillContext.SkillTier.MINOR)
            .damageMultiplier(1.05f)
            .guaranteedCrit(guaranteedCrit)
            .build();
    }

    public static void tick(ServerPlayer player, long nowTick) {
        if (player == null) return;
        State state = ACTIVE.get(player.getUUID());
        if (state == null) return;

        if (nowTick > state.endTick) {
            applyCooldown(player, state, nowTick);
            PacketDistributor.sendToPlayer(player, new InsectEyeStancePayload(false, 0));
            ACTIVE.remove(player.getUUID());
        }
    }

    private static void applyCooldown(ServerPlayer player, State state, long nowTick) {
        if (!state.cooldownApplied && state.cooldownTicks > 0) {
            WeaponSkillCooldowns.setCooldown(player, state.weaponId, state.skillId, nowTick, state.cooldownTicks);
            state.cooldownApplied = true;
        }
    }
}