package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.world.entity.Mob;

public class ShakeActorCommand implements EventCommand {
    private final String actorTag;
    private final int totalTicks;
    private final double amplitude;

    private Mob actor;
    private double baseX;
    private double baseZ;
    private int ticksElapsed;
    private boolean done;

    public ShakeActorCommand(String actorTag, int ticks, double amplitude) {
        this.actorTag = actorTag;
        this.totalTicks = Math.max(1, ticks);
        this.amplitude = amplitude;
    }

    @Override
    public void start(EventPlayer player) {
        actor = player.getActor(actorTag);
        if (actor == null) {
            done = true;
            return;
        }
        baseX = actor.getX();
        baseZ = actor.getZ();
        ticksElapsed = 0;
        done = false;
    }

    @Override
    public void tick(EventPlayer player) {
        if (done || actor == null) return;
        ticksElapsed++;
        double offset = (ticksElapsed % 2 == 0 ? amplitude : -amplitude);
        actor.setPos(baseX + offset, actor.getY(), baseZ);
        if (ticksElapsed >= totalTicks) {
            actor.setPos(baseX, actor.getY(), baseZ);
            done = true;
        }
    }

    @Override
    public boolean isComplete() {
        return done;
    }
}