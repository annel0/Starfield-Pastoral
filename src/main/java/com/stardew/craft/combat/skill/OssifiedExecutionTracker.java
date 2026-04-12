package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.OssifiedExecutionCirclePayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class OssifiedExecutionTracker {

    private static final class CircleState {
        private final Vec3 center;
        private final float radius;
        private long endTick;
        private long nextDamageTick;

        private CircleState(Vec3 center, float radius, long endTick, long nextDamageTick) {
            this.center = center;
            this.radius = radius;
            this.endTick = endTick;
            this.nextDamageTick = nextDamageTick;
        }
    }

    private static final Map<UUID, CircleState> ACTIVE = new HashMap<>();

    private OssifiedExecutionTracker() {}

    @SuppressWarnings("null")
    public static void start(ServerPlayer player, LivingEntity anchor, long nowTick, float radius, int durationTicks) {
        if (player == null || anchor == null || durationTicks <= 0) {
            return;
        }
        Vec3 center = anchor.position();
        CircleState state = new CircleState(center, radius, nowTick + durationTicks, nowTick);
        ACTIVE.put(player.getUUID(), state);

        if (player.level() instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(anchor,
                new OssifiedExecutionCirclePayload(
                    (float) center.x, (float) center.y, (float) center.z, radius, durationTicks));

            serverLevel.sendParticles(ParticleTypes.SOUL,
                center.x, center.y + 0.05, center.z,
                18, radius * 0.4, 0.1, radius * 0.4, 0.02);
            serverLevel.sendParticles(ParticleTypes.ASH,
                center.x, center.y + 0.05, center.z,
                10, radius * 0.35, 0.1, radius * 0.35, 0.02);
            serverLevel.playSound(null, anchor.blockPosition(),
                SoundEvents.BONE_BLOCK_BREAK, SoundSource.PLAYERS, 1.1f, 0.9f);
            serverLevel.playSound(null, anchor.blockPosition(),
                SoundEvents.SOUL_ESCAPE.value(), SoundSource.PLAYERS, 1.1f, 0.8f);
            serverLevel.playSound(null, anchor.blockPosition(),
                SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.8f, 0.7f);
        }
    }

    @SuppressWarnings("null")
    public static void tick(ServerPlayer player, long nowTick) {
        CircleState state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick >= state.endTick) {
            ACTIVE.remove(player.getUUID());
            return;
        }

        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        List<LivingEntity> targets = getTargetsInRadius(serverLevel, state.center, state.radius, player);
        pullTargets(targets, state.center, state.radius);

        if (nowTick % 5L == 0L) {
            for (int i = 0; i < 16; i++) {
                double ang = (Math.PI * 2.0) * (i / 16.0);
                double px = state.center.x + Math.cos(ang) * state.radius;
                double pz = state.center.z + Math.sin(ang) * state.radius;
                double py = state.center.y + 0.05;
                serverLevel.sendParticles(ParticleTypes.SOUL, px, py, pz, 1, 0.02, 0.02, 0.02, 0.0);
                serverLevel.sendParticles(ParticleTypes.ASH, px, py, pz, 1, 0.02, 0.02, 0.02, 0.0);
            }
        }

        if (nowTick >= state.nextDamageTick) {
            state.nextDamageTick += 20;
            for (LivingEntity target : targets) {
                SkillContext context = SkillContext.builder()
                    .skillId("ossified_execution_dot")
                    .tier(SkillContext.SkillTier.MAJOR)
                    .damageMultiplier(1.0f)
                    .build();
                WeaponSkillContextStore.setPending(player, context, nowTick + 5);

                target.invulnerableTime = 0;
                target.hurtTime = 0;
                target.hurt(player.damageSources().playerAttack(player), 1.0F);

                serverLevel.sendParticles(ParticleTypes.ASH,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                    6, 0.25, 0.2, 0.25, 0.01);
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                    4, 0.2, 0.15, 0.2, 0.01);
                serverLevel.playSound(null, target.blockPosition(),
                    SoundEvents.BONE_BLOCK_HIT, SoundSource.PLAYERS, 0.75f, 1.2f);
            }
        }
    }

    public static float getCritDamageBonus(Player attacker, LivingEntity target, long nowTick) {
        CircleState state = ACTIVE.get(attacker.getUUID());
        if (state == null || nowTick >= state.endTick) {
            return 0.0f;
        }
        if (target.distanceToSqr(state.center.x, state.center.y, state.center.z) > state.radius * state.radius) {
            return 0.0f;
        }
        return 0.20f;
    }

    private static List<LivingEntity> getTargetsInRadius(ServerLevel level, Vec3 center, float radius, Player owner) {
        AABB box = new AABB(
            center.x - radius, center.y - radius * 0.6, center.z - radius,
            center.x + radius, center.y + radius * 0.6, center.z + radius
        );
        return level.getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isPickable() && entity.isAlive() && entity != owner);
    }

    @SuppressWarnings("null")
    private static void pullTargets(List<LivingEntity> targets, Vec3 center, float radius) {
        for (LivingEntity target : targets) {
            Vec3 toCenter = new Vec3(center.x - target.getX(), 0.0, center.z - target.getZ());
            double dist = toCenter.length();
            if (dist < 0.01 || dist > radius) {
                continue;
            }
            Vec3 dir = toCenter.normalize();
            double strength = 0.02 + (1.0 - (dist / radius)) * 0.03;
            Vec3 pull = new Vec3(dir.x * strength, 0.0, dir.z * strength);
            target.setDeltaMovement(target.getDeltaMovement().add(pull));
        }
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}
