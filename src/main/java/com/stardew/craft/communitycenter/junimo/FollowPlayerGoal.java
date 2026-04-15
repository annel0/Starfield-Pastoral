package com.stardew.craft.communitycenter.junimo;

import com.stardew.craft.entity.junimo.JunimoEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

/**
 * SDV parity: Junimo.cs — After canReadJunimoText, Junimos follow the player
 * at a gentle pace within a certain distance range.
 * <p>
 * SDV behavior: friendly Junimos wander near the player within 5 tiles.
 * They don't follow aggressively — just gently gravitate.
 */
@SuppressWarnings("null")
public class FollowPlayerGoal extends Goal {

    private final JunimoEntity junimo;
    private final double followRange;
    private final double followSpeed;
    private Player target;
    private int cooldown;

    public FollowPlayerGoal(JunimoEntity junimo, double followRange, double followSpeed) {
        this.junimo = junimo;
        this.followRange = followRange;
        this.followSpeed = followSpeed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        target = junimo.level().getNearestPlayer(junimo, followRange);
        if (target == null) return false;
        // Only follow if player is within range but not too close
        double dist = junimo.distanceTo(target);
        return dist > 3.0 && dist < followRange;
    }

    @Override
    public boolean canContinueToUse() {
        if (target == null || !target.isAlive()) return false;
        double dist = junimo.distanceTo(target);
        return dist > 2.0 && dist < followRange * 1.5;
    }

    @Override
    public void start() {
        cooldown = 0;
    }

    @Override
    public void tick() {
        if (target == null) return;
        cooldown--;
        if (cooldown <= 0) {
            cooldown = 10; // re-path every 10 ticks
            junimo.getNavigation().moveTo(target, followSpeed);
        }
    }

    @Override
    public void stop() {
        target = null;
        junimo.getNavigation().stop();
    }
}
