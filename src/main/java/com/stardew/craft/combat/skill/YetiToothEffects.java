package com.stardew.craft.combat.skill;

import com.stardew.craft.combat.network.YetiFreezePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;

public final class YetiToothEffects {

    private YetiToothEffects() {}

    @SuppressWarnings("null")
    public static void applyFreeze(ServerLevel level, LivingEntity target, int durationTicks) {
        if (level == null || target == null) {
            return;
        }

        target.setDeltaMovement(0, 0, 0);
        YetiFreezeTracker.apply(target, level.getGameTime(), durationTicks);
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationTicks, 255, false, true, true));
        target.addEffect(new MobEffectInstance(MobEffects.JUMP, durationTicks, 255, false, false, false));

        PacketDistributor.sendToPlayersTrackingEntityAndSelf(
            target,
            new YetiFreezePayload(target.getId(), durationTicks)
        );

        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.6;
        double z = target.getZ();
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
            x, y, z,
            12, 0.35, 0.25, 0.35, 0.02);
        level.playSound(null, target.blockPosition(),
            net.minecraft.sounds.SoundEvents.GLASS_BREAK,
            net.minecraft.sounds.SoundSource.PLAYERS, 0.5f, 1.5f);
    }

    @SuppressWarnings("null")
    public static void applySlow(LivingEntity target, int durationTicks, int amplifier) {
        if (target == null) {
            return;
        }
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationTicks, amplifier, false, true, true));
    }
}
