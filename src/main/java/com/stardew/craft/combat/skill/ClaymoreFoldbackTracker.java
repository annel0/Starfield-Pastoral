package com.stardew.craft.combat.skill;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 双刃大剑“回刃折返”二段斩击追击
 */
public final class ClaymoreFoldbackTracker {

    private static final Map<UUID, State> PENDING = new HashMap<>();

    private record State(long fireTick, UUID targetId, String weaponId, String skillId) {}

    private ClaymoreFoldbackTracker() {}

    public static void start(ServerPlayer player, long nowTick, int delayTicks,
                             LivingEntity target, String weaponId, String skillId) {
        if (player == null || weaponId == null || skillId == null) {
            return;
        }
        long fireTick = nowTick + Math.max(1, delayTicks);
        UUID targetId = target != null ? target.getUUID() : null;
        PENDING.put(player.getUUID(), new State(fireTick, targetId, weaponId, skillId));
    }

    @SuppressWarnings("null")
    public static void tick(ServerPlayer player, long nowTick) {
        State state = PENDING.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick < state.fireTick) {
            return;
        }
        PENDING.remove(player.getUUID());

        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        double range = 4.5;
        LivingEntity target = null;

        if (state.targetId != null) {
            Entity entity = serverLevel.getEntity(state.targetId);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                target = living;
            }
        }

        if (target == null) {
            target = findTargetInFront(player, range);
        }

        if (target == null) {
            return;
        }

        // 允许二段在短时间内命中，清除默认无敌帧
        target.invulnerableTime = 0;
        target.hurtTime = 0;

        SkillContext context = SkillContext.builder()
            .skillId(state.skillId)
            .tier(SkillContext.SkillTier.MINOR)
            .damageMultiplier(1.2f)
            .build();
        WeaponSkillContextStore.setPending(player, context, nowTick + 5);
        target.hurt(player.damageSources().playerAttack(player), 1.0F);

        if (target.hurtTime > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 0, false, true, true));
        }

        WeaponSkillAnimationDispatcher.sendSkillAnim(player, state.weaponId, "claymore_foldback_return", 12);
    }

    public static void clear(ServerPlayer player) {
        if (player != null) {
            PENDING.remove(player.getUUID());
        }
    }

    private static LivingEntity findTargetInFront(ServerPlayer player, double range) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        @SuppressWarnings("null")
        Vec3 end = eyePos.add(lookVec.scale(range));
        @SuppressWarnings("null")
        AABB box = player.getBoundingBox().expandTowards(lookVec.scale(range)).inflate(1.0D);

        @SuppressWarnings("null")
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            player.level(),
            player,
            eyePos,
            end,
            box,
            entity -> entity instanceof LivingEntity && entity.isPickable() && entity != player
        );

        return hit != null ? (LivingEntity) hit.getEntity() : null;
    }
}
