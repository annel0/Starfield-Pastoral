package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * jump: make an actor jump.
 * JSON: {"cmd":"jump", "actor":"robin"}
 * Optional: "strength": 0.5 (default 0.5)
 */
public class JumpCommand implements EventCommand {
    private final String actorTag;
    private final double strength;

    public JumpCommand(String actorTag, double strength) {
        this.actorTag = actorTag;
        this.strength = strength;
    }

    @Override
    public void start(EventPlayer player) {
        Mob actor = player.getActor(actorTag);
        if (actor == null) return;
        Vec3 motion = actor.getDeltaMovement();
        actor.setDeltaMovement(motion.x, strength, motion.z);
        actor.hasImpulse = true;
    }

    @Override public void tick(EventPlayer player) {}
    @Override public boolean isComplete() { return true; }
}
