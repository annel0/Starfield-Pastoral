package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class TemplarJudgementImpactClient {

    private TemplarJudgementImpactClient() {}

    @SuppressWarnings("null")
    public static void playImpact(int entityId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        Entity entity = mc.level.getEntity(entityId);
        if (!(entity instanceof LivingEntity target)) {
            return;
        }

        double x = target.getX();
        double y = target.getY() + target.getBbHeight() * 0.6;
        double z = target.getZ();

        mc.level.playLocalSound(x, y, z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.7f, 1.6f, false);
        mc.level.playLocalSound(x, y, z, SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.PLAYERS, 0.6f, 1.1f, false);

        for (int i = 0; i < 10; i++) {
            @SuppressWarnings("null")
            double ox = (mc.level.random.nextDouble() - 0.5) * 0.6;
            @SuppressWarnings("null")
            double oy = (mc.level.random.nextDouble() - 0.5) * 0.4;
            @SuppressWarnings("null")
            double oz = (mc.level.random.nextDouble() - 0.5) * 0.6;
            mc.level.addParticle(ParticleTypes.END_ROD,
                x + ox, y + oy, z + oz,
                0, 0.02, 0);
        }
        for (int i = 0; i < 8; i++) {
            @SuppressWarnings("null")
            double ox = (mc.level.random.nextDouble() - 0.5) * 0.5;
            @SuppressWarnings("null")
            double oy = (mc.level.random.nextDouble() - 0.5) * 0.3;
            @SuppressWarnings("null")
            double oz = (mc.level.random.nextDouble() - 0.5) * 0.5;
            mc.level.addParticle(ParticleTypes.SWEEP_ATTACK,
                x + ox, y + oy, z + oz,
                0, 0, 0);
        }
        mc.level.addParticle(ParticleTypes.FLASH, x, y, z, 0, 0, 0);
        mc.level.addParticle(ParticleTypes.EXPLOSION, x, y, z, 0, 0.02, 0);
    }
}
