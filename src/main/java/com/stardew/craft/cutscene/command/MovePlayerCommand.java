package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public class MovePlayerCommand implements EventCommand {

    private boolean done;

    public MovePlayerCommand(double x, double y, double z, int ticks, boolean relative) {
        this(x, y, z, ticks, relative, null);
    }

    public MovePlayerCommand(double x, double y, double z, int ticks, boolean relative, String anchor) {
    }

    @Override
    public void start(EventPlayer player) {
        var playerEntity = Minecraft.getInstance().player;
        done = true;
        if (playerEntity != null) {
            playerEntity.setDeltaMovement(Vec3.ZERO);
            playerEntity.fallDistance = 0.0F;
        }
    }

    @Override
    public void tick(EventPlayer player) {
    }

    @Override
    public boolean isComplete() { return done; }
}
