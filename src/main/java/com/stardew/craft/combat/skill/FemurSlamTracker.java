package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.TremorBlockPayload;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public final class FemurSlamTracker {

    private static final Map<UUID, State> PENDING = new HashMap<>();
    private static final double RANGE = 5.0;
    private static final double MIN_DOT = 0.5;
    private static final int SLOW_TICKS = 40;
    private static final int STAGGER_TICKS = 4;
    private static final float KNOCKBACK_MULTI = 0.7f;
    private static final float KNOCKBACK_SINGLE = 1.1f;
    private static final int QUAKE_TREMOR_MAX = 28;

    private record State(long fireTick, String weaponId, String skillId, float damageMultiplier, int cooldownTicks) {}

    private FemurSlamTracker() {}

    public static boolean isCharging(Player player) {
        return player != null && PENDING.containsKey(player.getUUID());
    }

    public static void start(ServerPlayer player, long nowTick, int delayTicks,
                             String weaponId, String skillId, float damageMultiplier, int cooldownTicks) {
        if (player == null || weaponId == null || skillId == null) {
            return;
        }
        long fireTick = nowTick + Math.max(1, delayTicks);
        PENDING.put(player.getUUID(), new State(fireTick, weaponId, skillId, damageMultiplier, cooldownTicks));
    }

    @SuppressWarnings("null")
    public static void tick(ServerPlayer player, long nowTick) {
        State state = PENDING.get(player.getUUID());
        if (state != null) {
            if (nowTick < state.fireTick) {
                if (!player.isUsingItem()) {
                    PENDING.remove(player.getUUID());
                }
            } else {
                PENDING.remove(player.getUUID());
                handleSlam(player, nowTick, state);
            }
        }

    }

    @SuppressWarnings("null")
    private static void handleSlam(ServerPlayer player, long nowTick, State state) {
        player.swing(InteractionHand.MAIN_HAND, true);
        player.stopUsingItem();

        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        List<LivingEntity> targets = findTargetsInArc(player, RANGE, MIN_DOT);
        boolean hitAny = !targets.isEmpty();

        if (hitAny) {
            boolean singleTarget = targets.size() == 1;

            for (LivingEntity target : targets) {
                SkillContext context = SkillContext.builder()
                    .skillId(state.skillId)
                    .tier(SkillContext.SkillTier.MINOR)
                    .damageMultiplier(state.damageMultiplier)
                    .build();
                WeaponSkillContextStore.setPending(player, context, nowTick + 5);
                player.attack(target);

                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SLOW_TICKS, 0, false, true, true));

                float knockback = singleTarget ? KNOCKBACK_SINGLE : KNOCKBACK_MULTI;
                applyKnockback(player, target, knockback);

                if (singleTarget) {
                    target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, STAGGER_TICKS, 0, false, true, true));
                    target.setDeltaMovement(0.0, target.getDeltaMovement().y, 0.0);
                    target.hasImpulse = true;
                }

                spawnHitParticles(serverLevel, target);
            }
        }
        spawnQuakeImpact(serverLevel, player);
        spawnTremorBurst(serverLevel, player, RANGE);

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.IRON_GOLEM_ATTACK, SoundSource.PLAYERS, 0.85f, 0.9f);
        serverLevel.playSound(null, player.blockPosition(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.55f, 0.85f);
        WeaponSkillCooldowns.setCooldown(player, state.weaponId, state.skillId, nowTick, state.cooldownTicks);
    }

    private static void applyKnockback(Player player, LivingEntity target, float strength) {
        double dx = player.getX() - target.getX();
        double dz = player.getZ() - target.getZ();
        if (dx * dx + dz * dz > 0.0001) {
            target.knockback(strength, dx, dz);
        }
    }

    @SuppressWarnings("null")
    private static void spawnHitParticles(ServerLevel level, LivingEntity target) {
        ItemParticleOption bone = new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.BONE));
        level.sendParticles(
            bone,
            target.getX(),
            target.getY() + target.getBbHeight() * 0.6,
            target.getZ(),
            6,
            0.2, 0.2, 0.2,
            0.02
        );
    }

    @SuppressWarnings("null")
    private static void spawnQuakeImpact(ServerLevel level, Player player) {
        Vec3 center = player.position();
        double baseY = player.getY() + 0.05;

        level.sendParticles(ParticleTypes.POOF, center.x, baseY + 0.1, center.z, 10, 0.35, 0.05, 0.35, 0.02);
        level.sendParticles(ParticleTypes.CRIT, center.x, baseY + 0.2, center.z, 8, 0.45, 0.1, 0.45, 0.05);
    }

    @SuppressWarnings("null")
    private static void spawnTremorBurst(ServerLevel serverLevel, Player player, double radius) {
        RandomSource random = serverLevel.random;
        Vec3 center = player.position();
        Vec3 look = player.getLookAngle();
        Vec3 flatLook = new Vec3(look.x, 0.0, look.z).normalize();

        int tremorCount = Math.min(QUAKE_TREMOR_MAX, Math.max(18, (int) (radius * radius * 0.6)));
        spawnTremorAt(serverLevel, center, random, 0.32f, 14);
        for (int i = 0; i < tremorCount; i++) {
            Vec3 offset = randomPointInArc(random, radius);
            if (offset.lengthSqr() < 0.0001) {
                continue;
            }
            Vec3 flatOffset = new Vec3(offset.x, 0.0, offset.z).normalize();
            if (flatOffset.dot(flatLook) < MIN_DOT) {
                continue;
            }
            Vec3 pos = center.add(offset);
            spawnTremorAt(serverLevel, pos, random, 0.22f, 2);
        }
    }

    private static void spawnTremorAt(ServerLevel level, Vec3 pos, RandomSource random, float ySpeed, int count) {
        @SuppressWarnings("null")
        BlockState ground = level.getBlockState(BlockPos.containing(pos.x, pos.y, pos.z).below());
        if (ground.isAir()) {
            return;
        }
        BlockParticleOption debris = new BlockParticleOption(java.util.Objects.requireNonNull(ParticleTypes.BLOCK), ground);
        level.sendParticles(debris, pos.x, pos.y + 0.05, pos.z, count, 0.06, 0.02, 0.06, 0.02);
        level.sendParticles(java.util.Objects.requireNonNull(ParticleTypes.CLOUD), pos.x, pos.y + 0.08, pos.z, 1, 0.05, 0.0, 0.05, 0.02);
        PacketDistributor.sendToPlayersInDimension(level,
            new TremorBlockPayload((float) pos.x, (float) pos.y + 0.05f, (float) pos.z,
                Block.getId(ground), ySpeed + random.nextFloat() * 0.12f));
    }

    private static Vec3 randomPointInArc(RandomSource random, double radius) {
        double r = Math.sqrt(random.nextDouble()) * radius;
        double angle = (random.nextDouble() - 0.5) * (Math.PI * 2.0 / 3.0); // 120 degrees
        return new Vec3(r * Math.cos(angle), 0.2, r * Math.sin(angle));
    }

    private static List<LivingEntity> findTargetsInArc(Player player, double range, double minDot) {
        Vec3 origin = player.position();
        Vec3 look = player.getLookAngle();
        Vec3 flatLook = new Vec3(look.x, 0.0, look.z).normalize();
        AABB box = player.getBoundingBox().inflate(range, 1.25, range);
        @SuppressWarnings("null")
        List<LivingEntity> targets = player.level().getEntitiesOfClass(
            LivingEntity.class,
            box,
            entity -> entity.isPickable() && entity != player
        );

        targets.removeIf(entity -> {
            @SuppressWarnings("null")
            Vec3 to = entity.position().subtract(origin);
            double dist2 = to.x * to.x + to.z * to.z;
            if (dist2 > range * range) {
                return true;
            }
            Vec3 flatTo = new Vec3(to.x, 0.0, to.z);
            if (flatTo.lengthSqr() < 0.0001) {
                return true;
            }
            @SuppressWarnings("null")
            double dot = flatTo.normalize().dot(flatLook);
            return dot < minDot;
        });

        targets.sort((a, b) -> Double.compare(a.distanceToSqr(player), b.distanceToSqr(player)));
        return targets;
    }

    @SuppressWarnings("unused")
    private static LivingEntity findTargetEntity(Player player, double range) {
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
