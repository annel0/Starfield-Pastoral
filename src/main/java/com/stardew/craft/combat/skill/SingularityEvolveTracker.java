package com.stardew.craft.combat.skill;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.neoforged.neoforge.network.PacketDistributor;

import com.stardew.craft.combat.VfxColors;
import com.stardew.craft.combat.network.SingularityRunePayload;
import com.stardew.craft.combat.network.SingularityCorePayload;
import com.stardew.craft.combat.network.RiftPathPayload;
import com.stardew.craft.combat.network.ShockwaveRingPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 无限之刃 - 奇点进化：短暂聚拢 -> 爆心 -> 突进斩
 */
public final class SingularityEvolveTracker {

    private static final class State {
        private final long endTick;
        private final double radius;
        private final float explodeMultiplier;
        private final float slashMultiplier;
        private final String skillId;
        private final boolean evolved;

        private State(long endTick, double radius, float explodeMultiplier, float slashMultiplier, String skillId, boolean evolved) {
            this.endTick = endTick;
            this.radius = radius;
            this.explodeMultiplier = explodeMultiplier;
            this.slashMultiplier = slashMultiplier;
            this.skillId = skillId;
            this.evolved = evolved;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private SingularityEvolveTracker() {}

    @SuppressWarnings("null")
    public static void start(ServerPlayer player, long nowTick, int durationTicks, double radius,
                             float explodeMultiplier, float slashMultiplier, String skillId, boolean evolved) {
        if (player == null) {
            return;
        }
        ACTIVE.put(player.getUUID(), new State(nowTick + Math.max(1, durationTicks), radius, explodeMultiplier, slashMultiplier, skillId, evolved));

        Vec3 pos = player.position();
        ServerLevel level = player.serverLevel();
        int color = evolved ? VfxColors.INFINITY_GOLD : VfxColors.GALAXY_PURPLE;
        PacketDistributor.sendToPlayersInDimension(level,
            new ShockwaveRingPayload((float) pos.x, (float) pos.y, (float) pos.z, 3.6f, 12, color));
        PacketDistributor.sendToPlayersInDimension(level,
            new SingularityRunePayload((float) pos.x, (float) pos.y, (float) pos.z, (float) radius, durationTicks, color));
        PacketDistributor.sendToPlayersInDimension(level,
            new SingularityCorePayload((float) pos.x, (float) pos.y + 0.05f, (float) pos.z, 1.15f, durationTicks, color));
    }

    public static void tick(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick < state.endTick) {
            pullTargets(player, state);
            return;
        }

        explodeAndDash(player, nowTick, state);
        ACTIVE.remove(player.getUUID());
    }

    @SuppressWarnings("null")
    private static void pullTargets(ServerPlayer player, State state) {
        ServerLevel level = player.serverLevel();
        Vec3 center = player.position();
        AABB box = new AABB(
            center.x - state.radius, center.y - 1.5, center.z - state.radius,
            center.x + state.radius, center.y + 2.0, center.z + state.radius
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(
            LivingEntity.class,
            box,
            entity -> entity.isPickable() && entity != player
        );

        for (LivingEntity target : targets) {
            Vec3 dir = center.subtract(target.position());
            if (dir.lengthSqr() < 1.0E-4) {
                continue;
            }
            Vec3 pull = dir.normalize().scale(0.15);
            target.setDeltaMovement(target.getDeltaMovement().add(pull));
            target.hurtMarked = true;
            if ((level.getGameTime() & 1L) == 0L) {
                double px = target.getX();
                double py = target.getY() + target.getBbHeight() * 0.5;
                double pz = target.getZ();
                level.sendParticles(ParticleTypes.END_ROD,
                    px, py, pz,
                    1, 0.15, 0.2, 0.15, 0.01);
                if (level.random.nextFloat() < 0.6f) {
                    level.sendParticles(ParticleTypes.ENCHANT,
                        px, py, pz,
                        1, 0.12, 0.18, 0.12, 0.01);
                }
            }
        }
    }

    @SuppressWarnings("null")
    private static void explodeAndDash(ServerPlayer player, long nowTick, State state) {
        ServerLevel level = player.serverLevel();
        Vec3 center = player.position();
        AABB box = new AABB(
            center.x - state.radius, center.y - 1.5, center.z - state.radius,
            center.x + state.radius, center.y + 2.0, center.z + state.radius
        );

        List<LivingEntity> targets = level.getEntitiesOfClass(
            LivingEntity.class,
            box,
            entity -> entity.isPickable() && entity != player
        );

        for (LivingEntity target : targets) {
            SkillContext context = SkillContext.builder()
                .skillId(state.skillId)
                .tier(SkillContext.SkillTier.MINOR)
                .damageMultiplier(state.explodeMultiplier)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);
            target.invulnerableTime = 0;
            target.hurtTime = 0;
            player.attack(target);
        }

        level.playSound(null, player.blockPosition(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 0.8f, 1.1f);
        level.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.6f, 1.2f);
        level.sendParticles(ParticleTypes.PORTAL,
            center.x, center.y + 0.8, center.z,
            30, state.radius * 0.4, 0.6, state.radius * 0.4, 0.05);

        dashForward(player, nowTick, 5.0);

        if (state.evolved) {
            Vec3 look = getHorizontalLook(player).normalize();
            Vec3 pos = player.position();
            float yaw = (float) (Math.atan2(-look.x, look.z) * (180.0 / Math.PI));
            PacketDistributor.sendToPlayersInDimension(level,
                new RiftPathPayload((float) pos.x, (float) pos.y, (float) pos.z, yaw, 3.0f, 40, VfxColors.INFINITY_GOLD));
            RiftPathDamageTracker.start(player, pos, yaw, 3.0f, 40, "singularity_rift_path");
        }

        Vec3 start = player.position();
        Vec3 look = getHorizontalLook(player).normalize();
        Vec3 end = start.add(look.scale(5.0));
        List<LivingEntity> slashTargets = findTargetsOnPath(level, player, start, end, 0.9);
        for (LivingEntity target : slashTargets) {
            SkillContext context = SkillContext.builder()
                .skillId(state.skillId)
                .tier(SkillContext.SkillTier.MINOR)
                .damageMultiplier(state.slashMultiplier)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);
            target.invulnerableTime = 0;
            target.hurtTime = 0;
            player.attack(target);
        }
    }

    @SuppressWarnings("null")
    private static Vec3 getHorizontalLook(ServerPlayer player) {
        Vec3 look = player.getLookAngle();
        Vec3 dir = new Vec3(look.x, 0.0, look.z);
        if (dir.lengthSqr() < 1.0E-4) {
            dir = look;
        }
        return dir.normalize();
    }

    @SuppressWarnings("null")
    private static void dashForward(ServerPlayer player, long nowTick, double distance) {
        Vec3 start = player.position();
        Vec3 look = getHorizontalLook(player);
        Vec3 end = start.add(look.scale(distance));

        HitResult hit = player.level().clip(new ClipContext(
            start.add(0, player.getBbHeight() * 0.5, 0),
            end.add(0, player.getBbHeight() * 0.5, 0),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));

        if (hit.getType() != HitResult.Type.MISS) {
            Vec3 hitPos = hit.getLocation();
            end = hitPos.subtract(look.scale(0.4));
        }

        DashMovementTracker.start(player, nowTick, end, 5);
    }

    @SuppressWarnings("null")
    private static List<LivingEntity> findTargetsOnPath(ServerLevel level, ServerPlayer player, Vec3 start, Vec3 end, double halfWidth) {
        Vec3 min = new Vec3(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.min(start.z, end.z));
        Vec3 max = new Vec3(Math.max(start.x, end.x), Math.max(start.y, end.y), Math.max(start.z, end.z));
        AABB box = new AABB(min, max).inflate(halfWidth, 1.2, halfWidth);
        List<LivingEntity> targets = level.getEntitiesOfClass(
            LivingEntity.class,
            box,
            entity -> entity.isPickable() && entity != player
        );
        return targets;
    }
}
