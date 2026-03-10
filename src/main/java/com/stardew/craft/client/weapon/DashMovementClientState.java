package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class DashMovementClientState {

    private static boolean active = false;
    private static long endTick = 0L;
    private static int totalTicks = 0;
    private static Vec3 startPos = null;
    private static Vec3 endPos = null;
    private static boolean startFxPlayed = false;

    private DashMovementClientState() {}

    public static void start(long nowTick, int durationTicks, Vec3 end) {
        if (durationTicks <= 0 || end == null) {
            clear();
            return;
        }
        active = true;
        totalTicks = Math.max(1, durationTicks);
        endTick = nowTick + totalTicks;
        startPos = null;
        endPos = end;
        startFxPlayed = false;
    }

    public static void clear() {
        active = false;
        endTick = 0L;
        totalTicks = 0;
        startPos = null;
        endPos = null;
        startFxPlayed = false;
    }

    @SuppressWarnings("null")
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

    @SuppressWarnings("null")
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            clear();
            return;
        }
        Player player = mc.player;
        if (!isActive(player)) {
            return;
        }
        if (player.isPassenger()) {
            return;
        }
        if (startPos == null) {
            startPos = player.position();
        }
        if (endPos == null) {
            clear();
            return;
        }

        int remaining = (int) Math.max(0L, endTick - player.level().getGameTime());
        if (remaining <= 0) {
            Vec3 vel = player.getDeltaMovement();
            player.setDeltaMovement(0.0, vel.y, 0.0);
            player.hasImpulse = true;
            clear();
            return;
        }

        int elapsed = Math.max(0, totalTicks - remaining);
        int windupTicks = Math.max(1, Math.min(2, totalTicks - 1));
        if (!startFxPlayed) {
            startFxPlayed = true;
            player.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 0.35f, 1.25f);
        }
        if (elapsed == windupTicks) {
            player.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.7f, 1.35f);
            player.playSound(SoundEvents.TRIDENT_THROW.value(), 0.5f, 1.2f);
        }

        Vec3 current = player.position();
        double progress = Math.min(1.0, elapsed / (double) totalTicks);
        double accelFactor = elapsed < windupTicks ? 0.25 : Math.min(1.6, 0.8 + progress * 1.2);
        Vec3 toEnd = endPos.subtract(current);
        Vec3 step = toEnd.scale(1.0 / Math.max(1, remaining));
        Vec3 scaledStep = step.scale(accelFactor);
        Vec3 desiredPos = current.add(scaledStep);
        if (remaining <= 1 || scaledStep.lengthSqr() > toEnd.lengthSqr()) {
            desiredPos = endPos;
        }
        desiredPos = new Vec3(desiredPos.x, player.getY(), desiredPos.z);
        Vec3 safe = findSafePosition(player, adjustForCollision(player, desiredPos));
        if (safe == null) {
            clear();
            return;
        }
        Vec3 desiredVel = safe.subtract(current);
        Vec3 currentVel = player.getDeltaMovement();
        Vec3 nextVel = currentVel.add(desiredVel.subtract(currentVel).scale(0.7));
        player.setDeltaMovement(nextVel.x, currentVel.y, nextVel.z);
        player.hasImpulse = true;
        player.move(MoverType.SELF, player.getDeltaMovement());
        player.fallDistance = 0.0F;

        if (elapsed >= windupTicks) {
            spawnDashTrail(player, current, endPos);
        }
    }

    @SuppressWarnings("null")
    private static void spawnDashTrail(Player player, Vec3 current, Vec3 end) {
        if (player.level() == null || end == null) return;
        Vec3 dir = end.subtract(current);
        if (dir.lengthSqr() < 1.0e-4) return;
        Vec3 back = dir.normalize().scale(-0.4);
        double x = current.x + back.x;
        double y = current.y + 0.6;
        double z = current.z + back.z;
        player.level().addParticle(ParticleTypes.CLOUD, x, y, z, 0.0, 0.0, 0.0);
        if (player.level().getRandom().nextFloat() < 0.35f) {
            player.level().addParticle(ParticleTypes.CRIT, x, y + 0.1, z, 0.0, 0.0, 0.0);
        }
    }

    @SuppressWarnings("null")
    private static Vec3 adjustForCollision(Player player, Vec3 desired) {
        Vec3 start = player.position();
        Vec3 look = desired.subtract(start);
        if (look.lengthSqr() < 1.0E-6) {
            return desired;
        }
        Vec3 dir = new Vec3(look.x, 0.0, look.z).normalize();
        HitResult hit = player.level().clip(new ClipContext(
            start.add(0, player.getBbHeight() * 0.5, 0),
            desired.add(0, player.getBbHeight() * 0.5, 0),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));

        if (hit.getType() != HitResult.Type.MISS) {
            Vec3 hitPos = hit.getLocation();
            return hitPos.subtract(dir.scale(0.4));
        }
        return desired;
    }

    @SuppressWarnings("null")
    private static Vec3 findSafePosition(Player player, Vec3 desired) {
        if (desired == null) return null;
        AABB box = player.getBoundingBox().move(desired.x - player.getX(), desired.y - player.getY(), desired.z - player.getZ());
        if (player.level().noCollision(player, box)) {
            return desired;
        }
        Vec3 raised = desired.add(0, 0.25, 0);
        AABB boxUp = player.getBoundingBox().move(raised.x - player.getX(), raised.y - player.getY(), raised.z - player.getZ());
        if (player.level().noCollision(player, boxUp)) {
            return raised;
        }
        return null;
    }

    public static void clearIfNoPlayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            clear();
        }
    }
}
