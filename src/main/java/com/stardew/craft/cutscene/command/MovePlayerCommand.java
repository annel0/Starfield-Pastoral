package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

public class MovePlayerCommand implements EventCommand {

    private final double dx, dz;
    private final int totalTicks;
    private final boolean relative;
    private final String anchor;

    private LocalPlayer playerEntity;
    private double startX, startZ;
    private double endX, endZ;
    private int ticksElapsed;
    private double verticalVelocity;
    private boolean done;

    public MovePlayerCommand(double x, double y, double z, int ticks, boolean relative) {
        this(x, y, z, ticks, relative, null);
    }

    public MovePlayerCommand(double x, double y, double z, int ticks, boolean relative, String anchor) {
        this.dx = x;
        this.dz = z;
        this.totalTicks = Math.max(1, ticks);
        this.relative = relative;
        this.anchor = anchor;
    }

    @Override
    public void start(EventPlayer player) {
        playerEntity = Minecraft.getInstance().player;
        if (playerEntity == null) {
            done = true;
            return;
        }
        startX = playerEntity.getX();
        startZ = playerEntity.getZ();

        if (relative) {
            endX = startX + dx;
            endZ = startZ + dz;
        } else {
            endX = dx + com.stardew.craft.cutscene.runtime.CutsceneAnchorRegistry.offsetX(anchor);
            endZ = dz + com.stardew.craft.cutscene.runtime.CutsceneAnchorRegistry.offsetZ(anchor);
        }
        
        double dirX = endX - startX;
        double dirZ = endZ - startZ;
        if (dirX != 0 || dirZ != 0) {
            float yaw = (float) (Mth.atan2(dirZ, dirX) * Mth.RAD_TO_DEG) - 90.0f;
            playerEntity.setYRot(yaw);
            playerEntity.setYHeadRot(yaw);
        }
        
        ticksElapsed = 0;
        verticalVelocity = 0;
        done = false;
    }

    @Override
    public void tick(EventPlayer player) {
        if (done || playerEntity == null) return;

        ticksElapsed++;
        float t = (float) ticksElapsed / (float) totalTicks;
        t = Math.min(t, 1.0f);

        double desiredX = Mth.lerp(t, startX, endX);
        double desiredZ = Mth.lerp(t, startZ, endZ);
        double moveX = desiredX - playerEntity.getX();
        double moveZ = desiredZ - playerEntity.getZ();

        // Apply gravity for step-up support
        if (!playerEntity.onGround()) {
            verticalVelocity -= 0.08;
            verticalVelocity *= 0.98;
        } else {
            verticalVelocity = -0.04;
        }

        playerEntity.move(MoverType.SELF, new Vec3(moveX, verticalVelocity, moveZ));

        if (ticksElapsed >= totalTicks) {
            done = true;
        }
    }

    @Override
    public boolean isComplete() { return done; }
}
