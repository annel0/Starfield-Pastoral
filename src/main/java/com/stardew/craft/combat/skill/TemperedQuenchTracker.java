package com.stardew.craft.combat.skill;

import com.stardew.craft.effect.ModMobEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TemperedQuenchTracker {

    private static final class State {
        private final long triggerTick;
        private final UUID targetId;
        private State(long triggerTick, UUID targetId) {
            this.triggerTick = triggerTick;
            this.targetId = targetId;
        }
    }

    private static final Map<UUID, State> PENDING = new HashMap<>();

    private TemperedQuenchTracker() {}

    public static void start(ServerPlayer player, LivingEntity target, long nowTick, int delayTicks) {
        if (player == null || target == null) {
            return;
        }
        long triggerTick = nowTick + Math.max(1, delayTicks);
        PENDING.put(player.getUUID(), new State(triggerTick, target.getUUID()));
    }

    @SuppressWarnings("null")
    public static void tick(ServerPlayer player, long nowTick) {
        State state = PENDING.get(player.getUUID());
        if (state == null) {
            return;
        }

        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        LivingEntity target = null;
        if (state.targetId != null) {
            Entity entity = serverLevel.getEntity(state.targetId);
            if (entity instanceof LivingEntity living && living.isAlive()) {
                target = living;
            }
        }

        if (nowTick < state.triggerTick) {
            return;
        }

        PENDING.remove(player.getUUID());

        if (target == null) {
            return;
        }

        explode(player, serverLevel, nowTick, target);
    }

    @SuppressWarnings("null")
    private static void explode(ServerPlayer player, ServerLevel level, long nowTick, LivingEntity target) {
        Vec3 center = target.position();

        level.playSound(null, center.x, center.y, center.z,
            SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.9f, 0.9f);
        level.playSound(null, center.x, center.y, center.z,
            SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS, 0.8f, 1.1f);
        level.playSound(null, center.x, center.y, center.z,
            SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 0.7f, 0.9f);

        level.sendParticles(ParticleTypes.FLAME,
            center.x, center.y + 0.2, center.z,
            18, 0.8, 0.2, 0.8, 0.02);
        level.sendParticles(ParticleTypes.LAVA,
            center.x, center.y + 0.15, center.z,
            8, 0.5, 0.15, 0.5, 0.01);
        level.sendParticles(ParticleTypes.SMOKE,
            center.x, center.y + 0.1, center.z,
            10, 0.7, 0.1, 0.7, 0.02);

        SkillContext context = SkillContext.builder()
            .skillId("tempered_quench_blast")
            .tier(SkillContext.SkillTier.MINOR)
            .damageMultiplier(0.45f)
            .build();
        WeaponSkillContextStore.setPending(player, context, nowTick + 5);

        target.invulnerableTime = 0;
        target.hurtTime = 0;
        target.hurt(player.damageSources().playerAttack(player), 1.0F);

        target.addEffect(new MobEffectInstance(ModMobEffects.VULNERABLE, 60, 1, false, true, true));
    }
}
