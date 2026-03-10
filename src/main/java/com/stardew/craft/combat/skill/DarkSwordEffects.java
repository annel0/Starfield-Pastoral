package com.stardew.craft.combat.skill;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("null")
public final class DarkSwordEffects {

    private DarkSwordEffects() {}

    public static void playBloodDebtCast(ServerPlayer player) {
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 pos = player.position();
        double y = pos.y + player.getBbHeight() * 0.6;

        level.sendParticles(ParticleTypes.SMOKE,
            pos.x, y, pos.z,
            10, 0.5, 0.3, 0.5, 0.02);
        level.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
            pos.x, y, pos.z,
            6, 0.4, 0.2, 0.4, 0.01);

        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.8f, 0.9f);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 0.6f, 1.1f);
    }

    public static void playBloodMoonStart(ServerPlayer player) {
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 pos = player.position();
        double y = pos.y + player.getBbHeight() * 0.5;

        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
            pos.x, y, pos.z,
            18, 0.9, 0.4, 0.9, 0.02);
        level.sendParticles(ParticleTypes.SMOKE,
            pos.x, y, pos.z,
            14, 0.9, 0.4, 0.9, 0.02);

        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 0.9f, 0.8f);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.PLAYERS, 0.5f, 1.4f);
    }

    public static void playBloodMoonBurn(ServerPlayer player) {
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 pos = player.position();
        double y = pos.y + player.getBbHeight() * 0.4;

        level.sendParticles(ParticleTypes.SMOKE,
            pos.x, y, pos.z,
            6, 0.35, 0.2, 0.35, 0.01);
        level.sendParticles(ParticleTypes.SOUL,
            pos.x, y, pos.z,
            4, 0.25, 0.2, 0.25, 0.01);
    }

    public static void playLifeSteal(ServerPlayer player) {
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 pos = player.position();
        double y = pos.y + player.getBbHeight() * 0.55;

        level.sendParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR,
            pos.x, y, pos.z,
            8, 0.4, 0.3, 0.4, 0.01);
        level.sendParticles(ParticleTypes.CRIT,
            pos.x, y, pos.z,
            6, 0.35, 0.25, 0.35, 0.02);

        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 0.6f, 1.2f);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 0.4f, 1.8f);
    }

    public static void playBloodMoonBurst(ServerPlayer player) {
        if (player == null || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        Vec3 pos = player.position();
        double y = pos.y + player.getBbHeight() * 0.5;

        level.sendParticles(ParticleTypes.EXPLOSION,
            pos.x, y, pos.z,
            1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
            pos.x, y, pos.z,
            16, 0.9, 0.4, 0.9, 0.03);
        level.sendParticles(ParticleTypes.SMOKE,
            pos.x, y, pos.z,
            18, 0.9, 0.4, 0.9, 0.03);

        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.7f, 0.8f);
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.6f, 1.1f);
    }
}
