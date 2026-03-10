package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.FireRingEffectPayload;
import com.stardew.craft.effect.ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class DwarfFortressTracker {

    private static final class State {
        private final long endTick;
        private final int maxShocks;
        private int shocks;
        private long lastShockTick;
        private final double baseKnockbackRes;

        private State(long endTick, int maxShocks, double baseKnockbackRes) {
            this.endTick = endTick;
            this.maxShocks = maxShocks;
            this.baseKnockbackRes = baseKnockbackRes;
        }
    }

    private static final Map<UUID, State> ACTIVE = new HashMap<>();

    private DwarfFortressTracker() {}

    @SuppressWarnings("null")
    public static void start(ServerPlayer player, long nowTick, int durationTicks, float initialDamageMultiplier) {
        if (player == null || durationTicks <= 0) {
            return;
        }

        AttributeInstance knockbackRes = player.getAttribute(Objects.requireNonNull(Attributes.KNOCKBACK_RESISTANCE));
        double baseValue = knockbackRes != null ? knockbackRes.getBaseValue() : 0.0;
        if (knockbackRes != null) {
            knockbackRes.setBaseValue(1.0);
        }

        MobEffect shelter = Objects.requireNonNull(ModMobEffects.SHELTER.get(), "shelter");
        Holder<MobEffect> shelterHolder = Holder.direct(shelter);
        player.addEffect(new MobEffectInstance(shelterHolder, durationTicks, 1, false, true, true));

        State state = new State(nowTick + durationTicks, 4, baseValue);
        ACTIVE.put(player.getUUID(), state);

        triggerShockwave(player, nowTick, 3.5f, initialDamageMultiplier, false);
    }

    public static boolean isActive(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return false;
        }
        if (nowTick > state.endTick) {
            endNow(player, nowTick, state);
            return false;
        }
        return true;
    }

    public static void onDamageTaken(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick > state.endTick) {
            endNow(player, nowTick, state);
            return;
        }
        if (state.shocks >= state.maxShocks) {
            return;
        }
        if (state.lastShockTick == nowTick) {
            return;
        }
        state.lastShockTick = nowTick;
        state.shocks++;
        triggerShockwave(player, nowTick, 3.0f, 1.0f, false);
    }

    public static void tick(ServerPlayer player, long nowTick) {
        State state = ACTIVE.get(player.getUUID());
        if (state == null) {
            return;
        }
        if (nowTick <= state.endTick) {
            return;
        }
        if (state.shocks >= state.maxShocks) {
            triggerShockwave(player, nowTick, 4.0f, 1.2f, true);
        }
        endNow(player, nowTick, state);
    }

    private static void endNow(ServerPlayer player, long nowTick, State state) {
        ACTIVE.remove(player.getUUID());
        AttributeInstance knockbackRes = player.getAttribute(Objects.requireNonNull(Attributes.KNOCKBACK_RESISTANCE));
        if (knockbackRes != null) {
            knockbackRes.setBaseValue(state.baseKnockbackRes);
        }
    }

    private static void triggerShockwave(ServerPlayer player, long nowTick, float radius, float damageMultiplier, boolean echo) {
        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Vec3 center = player.position();
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player,
            new FireRingEffectPayload((float) center.x, (float) center.y, (float) center.z, radius, 12));

        spawnShockwaveEffects(serverLevel, center, radius, echo);

        List<LivingEntity> targets = getTargetsInRadius(serverLevel, center, radius, player);
        for (LivingEntity target : targets) {
            SkillContext context = SkillContext.builder()
                .skillId("dwarf_fortress")
                .tier(SkillContext.SkillTier.MAJOR)
                .damageMultiplier(damageMultiplier)
                .build();
            WeaponSkillContextStore.setPending(player, context, nowTick + 5);

            target.invulnerableTime = 0;
            target.hurtTime = 0;
            target.hurt(Objects.requireNonNull(player.damageSources().playerAttack(player)), 1.0F);
        }
    }

    @SuppressWarnings("null")
    private static void spawnShockwaveEffects(ServerLevel level, Vec3 center, float radius, boolean echo) {
        int smokeCount = echo ? 28 : 18;
        int critCount = echo ? 26 : 16;
        int dustCount = echo ? 36 : 24;

        level.sendParticles(ParticleTypes.EXPLOSION,
            center.x, center.y + 0.1, center.z,
            echo ? 2 : 1, 0.2, 0.05, 0.2, 0.0);
        level.sendParticles(ParticleTypes.SMOKE,
            center.x, center.y + 0.1, center.z,
            smokeCount, radius * 0.35, 0.12, radius * 0.35, 0.02);
        level.sendParticles(ParticleTypes.CRIT,
            center.x, center.y + 0.25, center.z,
            critCount, radius * 0.4, 0.2, radius * 0.4, 0.12);
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState()),
            center.x, center.y + 0.05, center.z,
            dustCount, radius * 0.45, 0.18, radius * 0.45, 0.12);

        BlockPos pos = BlockPos.containing(center);
        level.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 0.8f, echo ? 0.7f : 0.9f);
        level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, echo ? 0.9f : 0.6f, echo ? 0.8f : 1.1f);
    }

    private static List<LivingEntity> getTargetsInRadius(ServerLevel level, Vec3 center, float radius, ServerPlayer owner) {
        AABB box = new AABB(
            center.x - radius, center.y - radius * 0.6, center.z - radius,
            center.x + radius, center.y + radius * 0.6, center.z + radius
        );
        return level.getEntitiesOfClass(LivingEntity.class, box,
            entity -> entity.isPickable() && entity.isAlive() && entity != owner);
    }
}
