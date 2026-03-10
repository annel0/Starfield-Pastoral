package com.stardew.craft.combat.skill;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import com.stardew.craft.combat.VfxColors;
import com.stardew.craft.combat.network.AccretionDiskPayload;
import com.stardew.craft.combat.network.SingularityCorePayload;
import com.stardew.craft.combat.network.BlackHolePostPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 无限之刃 - 永恒坍缩：黑洞牵引 + 多段斩击 + 终击
 */
public final class EternalCollapseTracker {

    private static final class State {
        private final Vec3 center;
        private final long endTick;
        private long nextStrikeTick;
        private int remainingStrikes;
        private final double radius;
        private final float strikeMultiplier;
        private final float critBonus;
        private final boolean finalStrike;
        private final float finalMultiplier;
        private final String skillId;

        private State(Vec3 center, long endTick, long nextStrikeTick, int remainingStrikes, double radius,
                      float strikeMultiplier, float critBonus, boolean finalStrike, float finalMultiplier, String skillId) {
            this.center = center;
            this.endTick = endTick;
            this.nextStrikeTick = nextStrikeTick;
            this.remainingStrikes = remainingStrikes;
            this.radius = radius;
            this.strikeMultiplier = strikeMultiplier;
            this.critBonus = critBonus;
            this.finalStrike = finalStrike;
            this.finalMultiplier = finalMultiplier;
            this.skillId = skillId;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private EternalCollapseTracker() {}

    @SuppressWarnings("null")
    public static void start(ServerPlayer player, Vec3 center, long nowTick, int durationTicks, int strikes, double radius,
                             float strikeMultiplier, float critBonus, boolean finalStrike, float finalMultiplier, String skillId) {
        if (player == null || strikes <= 0 || durationTicks <= 0) {
            return;
        }
        Vec3 useCenter = center == null ? player.position() : center;
        long interval = Math.max(4, durationTicks / Math.max(1, strikes));
        ACTIVE.put(player.getUUID(), new State(
            useCenter,
            nowTick + durationTicks,
            nowTick + interval,
            strikes,
            radius,
            strikeMultiplier,
            critBonus,
            finalStrike,
            finalMultiplier,
            skillId
        ));

        Vec3 pos = useCenter;
        ServerLevel level = player.serverLevel();
        PacketDistributor.sendToPlayersInDimension(level,
            new AccretionDiskPayload((float) pos.x, (float) pos.y, (float) pos.z, (float) radius, durationTicks,
                VfxColors.INFINITY_GOLD));
        PacketDistributor.sendToPlayersInDimension(level,
            new SingularityCorePayload((float) pos.x, (float) pos.y + 0.05f, (float) pos.z, 1.4f, durationTicks,
                VfxColors.INFINITY_GOLD));
        PacketDistributor.sendToPlayersInDimension(level,
            new BlackHolePostPayload((float) pos.x, (float) pos.y + 0.5f, (float) pos.z, 0.35f, 0.9f, durationTicks));
    }

    public static void tick(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }

        pullTargets(player, state);

        if (nowTick >= state.nextStrikeTick && state.remainingStrikes > 0) {
            strike(player, nowTick, state, state.strikeMultiplier, state.critBonus);
            state.remainingStrikes -= 1;
            if (state.remainingStrikes > 0) {
                long interval = Math.max(4, (state.endTick - nowTick) / Math.max(1, state.remainingStrikes));
                state.nextStrikeTick = nowTick + interval;
            }
        }

        if (nowTick >= state.endTick) {
            if (state.finalStrike) {
                strike(player, nowTick, state, state.finalMultiplier, state.critBonus);
            }
            ACTIVE.remove(player.getUUID());
        }
    }

    @SuppressWarnings("null")
    private static void pullTargets(ServerPlayer player, State state) {
        ServerLevel level = player.serverLevel();
        Vec3 center = state.center;
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
            Vec3 pull = dir.normalize().scale(0.10);
            target.setDeltaMovement(target.getDeltaMovement().add(pull));
            target.hurtMarked = true;
            if ((level.getGameTime() & 1L) == 0L) {
                double px = target.getX();
                double py = target.getY() + target.getBbHeight() * 0.5;
                double pz = target.getZ();
                level.sendParticles(ParticleTypes.PORTAL,
                    px, py, pz,
                    1, 0.18, 0.22, 0.18, 0.01);
                if (level.random.nextFloat() < 0.5f) {
                    level.sendParticles(ParticleTypes.END_ROD,
                        px, py, pz,
                        1, 0.12, 0.18, 0.12, 0.01);
                }
            }
        }
    }

    @SuppressWarnings("null")
    private static void strike(ServerPlayer player, long nowTick, State state, float damageMultiplier, float critBonus) {
        ServerLevel level = player.serverLevel();
        Vec3 center = state.center;
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
                .tier(SkillContext.SkillTier.MAJOR)
                .damageMultiplier(damageMultiplier)
                .critChanceBonus(critBonus)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);
            target.invulnerableTime = 0;
            target.hurtTime = 0;
            player.attack(target);
        }

        level.playSound(null, new net.minecraft.core.BlockPos((int) center.x, (int) center.y, (int) center.z), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.5f, 1.3f);
        level.sendParticles(ParticleTypes.PORTAL,
            center.x, center.y + 0.8, center.z,
            16, state.radius * 0.35, 0.5, state.radius * 0.35, 0.02);
        level.sendParticles(ParticleTypes.SMOKE,
            center.x, center.y + 0.5, center.z,
            10, state.radius * 0.35, 0.3, state.radius * 0.35, 0.02);
    }
}
