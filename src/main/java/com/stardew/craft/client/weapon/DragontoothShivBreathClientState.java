package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class DragontoothShivBreathClientState {

    private static boolean active = false;
    private static long endTick = 0L;
    private static int totalTicks = 0;

    private DragontoothShivBreathClientState() {}

    public static void start(long nowTick, int durationTicks) {
        if (durationTicks <= 0) {
            clear();
            return;
        }
        active = true;
        totalTicks = Math.max(1, durationTicks);
        endTick = nowTick + totalTicks;
    }

    public static void clear() {
        active = false;
        endTick = 0L;
        totalTicks = 0;
    }

    public static boolean isActive(Player player) {
        if (!active || player == null || player.level() == null) {
            return false;
        }
        long nowTick = player.level().getGameTime();
        if (nowTick > endTick) {
            clear();
            return false;
        }
        return true;
    }

    public static int getRemainingTicks(Player player) {
        if (!isActive(player)) {
            return 0;
        }
        long nowTick = player.level().getGameTime();
        return (int) Math.max(0, endTick - nowTick);
    }

    public static int getTotalTicks() {
        return Math.max(1, totalTicks);
    }

    @SuppressWarnings("null")
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            clear();
            return;
        }

        Player player = mc.player;
        var level = mc.level;
        if (!isActive(player)) {
            return;
        }

        long nowTick = player.level().getGameTime();
        Vec3 pos = player.position();
        double baseY = pos.y + player.getBbHeight() * 0.6;

        for (int i = 0; i < 3; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2.0;
            double radius = 0.4 + level.random.nextDouble() * 0.7;
            double px = pos.x + Math.cos(angle) * radius;
            double pz = pos.z + Math.sin(angle) * radius;
            level.addParticle(ParticleTypes.DRAGON_BREATH,
                px, baseY + level.random.nextDouble() * 0.3, pz,
                0.0, 0.02, 0.0);
            if (level.random.nextFloat() < 0.5f) {
                level.addParticle(ParticleTypes.FLAME,
                    px, baseY + 0.05, pz,
                    0.0, 0.04, 0.0);
            }
        }

        if (nowTick % 10 == 0) {
            player.playSound(SoundEvents.BLAZE_SHOOT, 0.25f, 1.6f);
            player.playSound(SoundEvents.FIRE_AMBIENT, 0.18f, 1.2f);
        }
    }

    public static void clearIfNoPlayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            clear();
        }
    }
}
