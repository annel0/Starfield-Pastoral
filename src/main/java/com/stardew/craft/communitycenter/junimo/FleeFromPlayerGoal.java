package com.stardew.craft.communitycenter.junimo;

import com.stardew.craft.entity.junimo.JunimoEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * SDV parity: Junimo.cs — Before canReadJunimoText, Junimos flee from the player
 * and eventually fade out when they get close.
 * <p>
 * SDV behavior: if (!Game1.player.mailReceived.Contains("canReadJunimoText"))
 *   → Junimo runs away in opposite direction, then fades out.
 */
@SuppressWarnings("null")
public class FleeFromPlayerGoal extends Goal {

    private final JunimoEntity junimo;
    private final double fleeDistance;
    private final double fleeSpeed;

    public FleeFromPlayerGoal(JunimoEntity junimo, double fleeDistance, double fleeSpeed) {
        this.junimo = junimo;
        this.fleeDistance = fleeDistance;
        this.fleeSpeed = fleeSpeed;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Player nearest = junimo.level().getNearestPlayer(junimo, fleeDistance);
        return nearest != null;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse() && !junimo.getNavigation().isDone();
    }

    @Override
    public void start() {
        Player nearest = junimo.level().getNearestPlayer(junimo, fleeDistance);
        if (nearest == null) return;

        // Move in opposite direction from player
        Vec3 junimoPos = junimo.position();
        Vec3 playerPos = nearest.position();
        Vec3 awayDir = junimoPos.subtract(playerPos).normalize();
        Vec3 fleeTarget = junimoPos.add(awayDir.scale(8.0));

        junimo.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, fleeSpeed);
    }

    @Override
    public void stop() {
        // SDV parity: Junimo stops fleeing but stays alive.
        // Idle Junimos (cc_idle_junimo) are permanent — they will flee again
        // when the player approaches next time.
        junimo.getNavigation().stop();
    }
}
