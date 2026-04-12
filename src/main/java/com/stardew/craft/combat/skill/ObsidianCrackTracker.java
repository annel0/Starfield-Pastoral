package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.ObsidianCrackPayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 黑曜石之刃 - 裂界一线：地面裂线特效 + 拉拽 + 爆裂伤害。
 */
public final class ObsidianCrackTracker {

    private static final int EFFECT_DURATION_TICKS = 20;
    private static final int EXPLODE_DELAY_TICKS = 8;
    private static final double PULL_RADIUS = 3.0;

    private static final class State {
        private final long explodeTick;
        private final Vec3 start;
        private final Vec3 end;
        private final String skillId;

        private State(long explodeTick, Vec3 start, Vec3 end, float yaw, float length, String weaponId, String skillId) {
            this.explodeTick = explodeTick;
            this.start = start;
            this.end = end;
            this.skillId = skillId;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private ObsidianCrackTracker() {}

    @SuppressWarnings("null")
    public static void start(ServerPlayer player, long nowTick, Vec3 start, Vec3 end, float yaw, float length,
                             String weaponId, String skillId) {
        if (player == null) {
            return;
        }
        ACTIVE.put(player.getUUID(), new State(nowTick + EXPLODE_DELAY_TICKS, start, end, yaw, length, weaponId, skillId));

        @SuppressWarnings("null")
        Vec3 center = start.add(end).scale(0.5);
        PacketDistributor.sendToPlayersInDimension(player.serverLevel(),
            new ObsidianCrackPayload((float) center.x, (float) center.y, (float) center.z, yaw, length, EFFECT_DURATION_TICKS));

        ServerLevel level = player.serverLevel();
        level.playSound(null, center.x, center.y, center.z,
            SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.9f, 0.8f);
    }

    public static void tick(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick < state.explodeTick) {
            return;
        }

        explode(player, nowTick, state);
        ACTIVE.remove(player.getUUID());
    }

    @SuppressWarnings("null")
    private static void explode(ServerPlayer player, long nowTick, State state) {
        ServerLevel level = player.serverLevel();
        Vec3 start = state.start;
        Vec3 end = state.end;

        Vec3 min = new Vec3(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.min(start.z, end.z));
        Vec3 max = new Vec3(Math.max(start.x, end.x), Math.max(start.y, end.y), Math.max(start.z, end.z));
        AABB box = new AABB(min, max).inflate(PULL_RADIUS, 1.5, PULL_RADIUS);
        @SuppressWarnings("null")
        List<LivingEntity> targets = level.getEntitiesOfClass(
            LivingEntity.class,
            box,
            entity -> entity.isPickable() && entity != player
        );

        for (LivingEntity target : targets) {
            if (distanceToSegmentSqr2D(target.position(), start, end) > PULL_RADIUS * PULL_RADIUS) {
                continue;
            }
            Vec3 nearest = nearestPointOnSegment2D(target.position(), start, end);
            target.teleportTo(nearest.x, target.getY(), nearest.z);

            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, true, true));

            SkillContext context = SkillContext.builder()
                .skillId(state.skillId)
                .tier(SkillContext.SkillTier.MAJOR)
                .damageMultiplier(1.6f)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);
            target.invulnerableTime = 0;
            target.hurtTime = 0;
            target.hurt(player.damageSources().playerAttack(player), 1.0F);
        }

        level.playSound(null, player.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.9f, 0.9f);
        level.playSound(null, player.blockPosition(), SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.6f, 0.8f);
        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.9f, 0.9f);

        Vec3 center = start.add(end).scale(0.5);
        level.sendParticles(ParticleTypes.CRIT,
            center.x, center.y + 0.2, center.z,
            24, 0.7, 0.08, 0.7, 0.1);
        level.sendParticles(ParticleTypes.SMOKE,
            center.x, center.y + 0.1, center.z,
            16, 0.7, 0.02, 0.7, 0.03);
        level.sendParticles(ParticleTypes.EXPLOSION,
            center.x, center.y + 0.15, center.z,
            2, 0.2, 0.0, 0.2, 0.01);

        int steps = 12;
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            double px = start.x + (end.x - start.x) * t;
            double pz = start.z + (end.z - start.z) * t;
            level.sendParticles(ParticleTypes.CRIT,
                px, center.y + 0.08, pz,
                2, 0.08, 0.02, 0.08, 0.02);
            level.sendParticles(ParticleTypes.SMOKE,
                px, center.y + 0.04, pz,
                1, 0.06, 0.01, 0.06, 0.01);
        }
    }

    private static double distanceToSegmentSqr2D(Vec3 p, Vec3 a, Vec3 b) {
        Vec3 ap = new Vec3(p.x - a.x, 0.0, p.z - a.z);
        Vec3 ab = new Vec3(b.x - a.x, 0.0, b.z - a.z);
        double abLen2 = ab.lengthSqr();
        if (abLen2 < 1.0E-6) {
            return ap.lengthSqr();
        }
        double t = (ap.x * ab.x + ap.z * ab.z) / abLen2;
        t = Math.max(0.0, Math.min(1.0, t));
        double cx = a.x + ab.x * t;
        double cz = a.z + ab.z * t;
        double dx = p.x - cx;
        double dz = p.z - cz;
        return dx * dx + dz * dz;
    }

    private static Vec3 nearestPointOnSegment2D(Vec3 p, Vec3 a, Vec3 b) {
        Vec3 ap = new Vec3(p.x - a.x, 0.0, p.z - a.z);
        Vec3 ab = new Vec3(b.x - a.x, 0.0, b.z - a.z);
        double abLen2 = ab.lengthSqr();
        if (abLen2 < 1.0E-6) {
            return new Vec3(a.x, a.y, a.z);
        }
        double t = (ap.x * ab.x + ap.z * ab.z) / abLen2;
        t = Math.max(0.0, Math.min(1.0, t));
        return new Vec3(a.x + ab.x * t, a.y, a.z + ab.z * t);
    }

    /** Clean up state when a player logs out to prevent memory leaks. */
    public static void removePlayer(UUID playerId) {
        ACTIVE.remove(playerId);
    }
}