package com.stardew.craft.cutscene.command;

import com.stardew.craft.cutscene.runtime.EventCameraController;
import com.stardew.craft.cutscene.runtime.EventPlayer;
import net.minecraft.client.Minecraft;

/**
 * camera: sets or moves the cutscene camera.
 * JSON: { "cmd": "camera", "x": 0, "y": 3, "z": -5, "yaw": 0, "pitch": 30, "relative": true }
 * Optional "ticks" for smooth lerp: { "cmd": "camera", "x": 10, "y": 3, "z": 0, "ticks": 40 }
 */
public class CameraCommand implements EventCommand {

    private final double x, y, z;
    private final float yaw, pitch;
    private final boolean relative;
    private final int ticks;
    private final String anchor;

    private int ticksElapsed;
    private boolean done;

    public CameraCommand(double x, double y, double z, float yaw, float pitch, boolean relative, int ticks) {
        this(x, y, z, yaw, pitch, relative, ticks, null);
    }

    public CameraCommand(double x, double y, double z, float yaw, float pitch,
                          boolean relative, int ticks, String anchor) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.relative = relative;
        this.ticks = ticks;
        this.anchor = anchor;
    }

    @SuppressWarnings("null")
    @Override
    public void start(EventPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        double px, py, pz;
        float eyeHeight = mc.player != null ? mc.player.getEyeHeight() : 1.62f;

        if (relative && mc.player != null) {
            px = mc.player.getX() + x;
            py = mc.player.getY() + eyeHeight + y;
            pz = mc.player.getZ() + z;
        } else {
            px = x + com.stardew.craft.cutscene.runtime.CutsceneAnchorRegistry.offsetX(anchor);
            // Add eye height to absolute Y coordinate so that using F3 coordinates
            // perfectly matches the player's perspective at that location.
            py = y + eyeHeight + com.stardew.craft.cutscene.runtime.CutsceneAnchorRegistry.offsetY(anchor);
            pz = z + com.stardew.craft.cutscene.runtime.CutsceneAnchorRegistry.offsetZ(anchor);
        }

        if (ticks <= 0) {
            // Instant set
            EventCameraController.setPosition(px, py, pz, yaw, pitch);
            done = true;
        } else {
            // If camera not yet active, set starting position first
            if (!EventCameraController.isActive() && mc.player != null) {
                EventCameraController.setPosition(
                        mc.player.getX(),
                        mc.player.getY() + eyeHeight,
                        mc.player.getZ(),
                        mc.player.getYRot(),
                        mc.player.getXRot()
                );
            }
            EventCameraController.moveTo(px, py, pz, yaw, pitch, ticks);
            ticksElapsed = 0;
            done = false;
        }
    }

    @Override
    public void tick(EventPlayer player) {
        if (done) return;
        ticksElapsed++;
        if (ticksElapsed >= ticks) {
            done = true;
        }
    }

    @Override
    public boolean isComplete() { return done; }
}
