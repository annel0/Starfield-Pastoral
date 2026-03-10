package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.WeaponStats;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("null")
public final class DarkSwordBloodMoonTracker {

    private static final class State {
        private final long endTick;
        private long nextBurnTick;
        private final int burnIntervalTicks;
        private float totalBurned;
        private float totalHealed;
        private final String weaponId;
        private final String skillId;
        private final int cooldownTicks;
        private State(long endTick, long nextBurnTick, int burnIntervalTicks, String weaponId, String skillId, int cooldownTicks) {
            this.endTick = endTick;
            this.nextBurnTick = nextBurnTick;
            this.burnIntervalTicks = burnIntervalTicks;
            this.weaponId = weaponId;
            this.skillId = skillId;
            this.cooldownTicks = cooldownTicks;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();
    private static final float LIFESTEAL_RATIO = 0.30f;
    private static final float DAMAGE_BONUS_MULTIPLIER = 1.35f;

    private DarkSwordBloodMoonTracker() {}

    public static void start(ServerPlayer player, long nowTick, int durationTicks, int burnIntervalTicks, String weaponId, String skillId, int cooldownTicks) {
        if (player == null || durationTicks <= 0) {
            return;
        }
        long endTick = nowTick + durationTicks;
        int interval = Math.max(1, burnIntervalTicks);
        ACTIVE.put(player.getUUID(), new State(endTick, nowTick + interval, interval, weaponId, skillId, cooldownTicks));
        DarkSwordEffects.playBloodMoonStart(player);
    }

    public static boolean isActive(ServerPlayer player, long nowTick) {
        if (player == null) return false;
        State state = ACTIVE.get(player.getUUID());
        return state != null && nowTick <= state.endTick;
    }

    public static float getLifestealRatio(ServerPlayer player, long nowTick) {
        return isActive(player, nowTick) ? LIFESTEAL_RATIO : 0.0f;
    }

    public static float getDamageBonusMultiplier(ServerPlayer player, long nowTick) {
        return isActive(player, nowTick) ? DAMAGE_BONUS_MULTIPLIER : 1.0f;
    }

    public static void recordLifeSteal(ServerPlayer player, long nowTick, float healedAmount) {
        if (player == null) return;
        State state = ACTIVE.get(player.getUUID());
        if (state == null || nowTick > state.endTick) {
            return;
        }
        state.totalHealed += Math.max(0.0f, healedAmount);
    }

    public static void tick(ServerPlayer player, long nowTick) {
        if (player == null) return;
        State state = ACTIVE.get(player.getUUID());
        if (state == null) return;

        if (nowTick > state.endTick) {
            finish(player, state, nowTick);
            ACTIVE.remove(player.getUUID());
            return;
        }

        if (nowTick >= state.nextBurnTick) {
            state.nextBurnTick += state.burnIntervalTicks;
            applyBurn(player, state);
        }
    }

    private static void applyBurn(ServerPlayer player, State state) {
        int max = PlayerStardewDataAPI.getMaxHealth(player);
        int current = PlayerStardewDataAPI.getHealth(player);
        int burn = Math.max(1, Math.round(max * 0.01f));
        int next = Math.max(1, current - burn);
        int actualBurn = Math.max(0, current - next);

        if (actualBurn > 0) {
            PlayerStardewDataAPI.setHealth(player, next);
            state.totalBurned += actualBurn;
            DarkSwordEffects.playBloodMoonBurn(player);
        }
    }

    private static void finish(ServerPlayer player, State state, long nowTick) {
        if (state.cooldownTicks > 0 && state.weaponId != null && !state.weaponId.isEmpty()
            && state.skillId != null && !state.skillId.isEmpty()) {
            WeaponSkillCooldowns.setCooldown(player, state.weaponId, state.skillId, nowTick, state.cooldownTicks);
        }
        float netBurn = Math.max(0.0f, state.totalBurned - state.totalHealed);
        if (netBurn <= 0.0f) {
            return;
        }

        float burstDamage = netBurn;
        WeaponStats weaponStats = WeaponStats.fromItemStack(player.getMainHandItem());
        float avgDamage = weaponStats.getAverageDamage();
        if (avgDamage <= 0.0f) {
            return;
        }
        float damageMultiplier = Math.max(0.1f, burstDamage / avgDamage);

        if (!(player.level() instanceof ServerLevel level)) {
            return;
        }

        Vec3 center = player.position();
        List<LivingEntity> targets = getTargetsInRadius(level, center, 3.5f, player);
        if (targets.isEmpty()) {
            return;
        }

        DarkSwordEffects.playBloodMoonBurst(player);

        for (LivingEntity target : targets) {
            SkillContext context = SkillContext.builder()
                .skillId("dark_sword_blood_moon_burst")
                .tier(SkillContext.SkillTier.MAJOR)
                .damageMultiplier(damageMultiplier)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);

            target.invulnerableTime = 0;
            target.hurtTime = 0;
            target.hurt(player.damageSources().playerAttack(player), 1.0F);
        }
    }

    private static List<LivingEntity> getTargetsInRadius(ServerLevel level, Vec3 center, float radius, Player owner) {
        AABB box = new AABB(
            center.x - radius, center.y - radius * 0.6, center.z - radius,
            center.x + radius, center.y + radius * 0.6, center.z + radius
        );
        return level.getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isPickable() && entity.isAlive() && entity != owner);
    }
}
