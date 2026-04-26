package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;

/**
 * face_actor: instantly rotates an actor to face a direction (yaw).
 * JSON: { "cmd": "face_actor", "actor": "alice", "yaw": 180 }
 * Or: { "cmd": "face_actor", "actor": "alice", "face_actor": "bob" } to face another actor.
 * The special tag "player" targets the local player.
 */
public class FaceActorCommand implements EventCommand {

    private final String actorTag;
    private final Float yaw;
    private final String faceTarget;

    public FaceActorCommand(String actorTag, Float yaw, String faceTarget) {
        this.actorTag = actorTag;
        this.yaw = yaw;
        this.faceTarget = faceTarget;
    }

    @Override
    public void start(EventPlayer player) {
        // Resolve source position/rotation target (may be player or actor).
        net.minecraft.world.entity.LivingEntity actor;
        if ("player".equals(actorTag)) {
            actor = Minecraft.getInstance().player;
        } else {
            actor = player.getActor(actorTag);
        }
        if (actor == null) return;

        float finalYaw;
        if (faceTarget != null) {
            net.minecraft.world.entity.LivingEntity target;
            if ("player".equals(faceTarget)) {
                target = Minecraft.getInstance().player;
            } else {
                target = player.getActor(faceTarget);
            }
            if (target == null) return;
            double dx = target.getX() - actor.getX();
            double dz = target.getZ() - actor.getZ();
            finalYaw = (float) (Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        } else if (yaw != null) {
            finalYaw = yaw;
        } else {
            return;
        }

        actor.setYRot(finalYaw);
        actor.setYHeadRot(finalYaw);
        if (actor instanceof Mob m) m.setYBodyRot(finalYaw);
    }

    @Override
    public void tick(EventPlayer player) {}

    @Override
    public boolean isComplete() { return true; }
}
