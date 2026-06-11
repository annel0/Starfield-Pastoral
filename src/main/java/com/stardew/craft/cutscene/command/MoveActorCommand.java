package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventActorEntity;
import com.stardew.craft.cutscene.runtime.EventPlayerActorEntity;
import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

/**
 * move_actor: smoothly moves an actor to a target position over a number of ticks.
 * JSON: { "cmd": "move_actor", "actor": "alice", "x": 3, "y": 0, "z": 0, "ticks": 40, "relative": true }
 * If relative=true, x/y/z are offsets from the actor's current position.
 */
public class MoveActorCommand implements EventCommand {

    private final String actorTag;
    private final double dx, dz;
    private final int totalTicks;
    private final boolean relative;
    private final String anchor;

    private Mob actor;
    private double startX, startY, startZ;
    private double endX, endY, endZ;
    private int ticksElapsed;
    private boolean done;

    public MoveActorCommand(String actorTag, double x, double y, double z, int ticks, boolean relative) {
        this(actorTag, x, y, z, ticks, relative, null);
    }

    public MoveActorCommand(String actorTag, double x, double y, double z, int ticks,
                             boolean relative, String anchor) {
        this.actorTag = actorTag;
        this.dx = x;
        this.dz = z;
        this.totalTicks = Math.max(1, ticks);
        this.relative = relative;
        this.anchor = anchor;
    }

    @Override
    public void start(EventPlayer player) {
        actor = player.getActor(actorTag);
        if (actor == null) {
            done = true;
            return;
        }

        startX = actor.getX();
        startY = actor.getY();
        startZ = actor.getZ();

        if (relative) {
            endX = startX + dx;
            endY = startY;
            endZ = startZ + dz;
        } else {
            endX = dx + com.stardew.craft.cutscene.runtime.CutsceneAnchorRegistry.offsetX(anchor);
            endY = startY;
            endZ = dz + com.stardew.craft.cutscene.runtime.CutsceneAnchorRegistry.offsetZ(anchor);
        }

        // Face towards movement direction
        double dirX = endX - startX;
        double dirZ = endZ - startZ;
        if (dirX != 0 || dirZ != 0) {
            float yaw = (float) (Mth.atan2(dirZ, dirX) * Mth.RAD_TO_DEG) - 90.0f;
            actor.setYRot(yaw);
            actor.setYHeadRot(yaw);
            actor.setYBodyRot(yaw);
        }

        // Set walking flag on supported actor types
        if (actor instanceof EventActorEntity npcActor) {
            npcActor.setWalking(true);
        } else if (actor instanceof EventPlayerActorEntity playerActor) {
            playerActor.setWalking(true);
        }
        ticksElapsed = 0;
        done = false;
    }

    @Override
    public void tick(EventPlayer player) {
        if (done) return;

        if (actor == null) {
            done = true;
            return;
        }

        ticksElapsed++;
        float t = (float) ticksElapsed / (float) totalTicks;
        t = Math.min(t, 1.0f);

        // Compute desired XZ position via lerp
        double desiredX = Mth.lerp(t, startX, endX);
        double desiredY = Mth.lerp(t, startY, endY);
        double desiredZ = Mth.lerp(t, startZ, endZ);

        // Cutscene actors use authored coordinates. Do not route through entity collision
        // movement here, or interiors can snap actors into adjacent tiles.
        actor.setPos(desiredX, desiredY, desiredZ);

        if (ticksElapsed >= totalTicks) {
            actor.setPos(endX, endY, endZ);
            if (actor instanceof EventActorEntity npcActor) {
                npcActor.setWalking(false);
            } else if (actor instanceof EventPlayerActorEntity playerActor) {
                playerActor.setWalking(false);
            }
            done = true;
        }
    }

    @Override
    public boolean isComplete() { return done; }
}
