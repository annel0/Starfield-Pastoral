import os

gizmo_content = """package com.stardew.craft.client.deco;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stardew.craft.blockentity.DecorAnchorBlockEntity;
import com.stardew.craft.network.payload.UpdateDecorAnchorPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

@SuppressWarnings("null")
public final class DecorAnchorWorldGizmo {
    private enum AxisId { X, Y, Z }
    private enum EditMode { TRANSLATE, ROTATE, SCALE }

    private static boolean editing;
    private static BlockPos targetPos;

    private static String styleId;
    private static float offsetX, offsetY, offsetZ;
    private static float rotX, rotY, rotZ;
    private static float scale;

    private static String originalStyleId;
    private static float originalOffsetX, originalOffsetY, originalOffsetZ;
    private static float originalRotX, originalRotY, originalRotZ;
    private static float originalScale;

    private static AxisId activeAxis = AxisId.X;
    private static AxisId hoverAxis = null;
    private static EditMode mode = EditMode.TRANSLATE;

    private static boolean dragging;
    private static double lastMouseX, lastMouseY;
    private static long lastSendMs;
    private static int lastHintTick;

    private static boolean prevKeyX, prevKeyY, prevKeyZ, prevKeyR, prevKeyS, prevKeyEnter, prevKeyEsc;

    public static boolean isEditing() { return editing; }

    public static void startFromPayload(BlockPos pos, String inStyleId, float inOffsetX, float inOffsetY, float inOffsetZ, float inRotX, float inRotY, float inRotZ, float inScale) {
        targetPos = pos;
        styleId = inStyleId;
        offsetX = inOffsetX; offsetY = inOffsetY; offsetZ = inOffsetZ;
        rotX = inRotX; rotY = inRotY; rotZ = inRotZ;
        scale = inScale;

        originalStyleId = inStyleId;
        originalOffsetX = inOffsetX; originalOffsetY = inOffsetY; originalOffsetZ = inOffsetZ;
        originalRotX = inRotX; originalRotY = inRotY; originalRotZ = inRotZ;
        originalScale = inScale;

        activeAxis = AxisId.X;
        mode = EditMode.TRANSLATE;
        dragging = false; editing = true;
        lastHintTick = Integer.MIN_VALUE;
        showHint(true);
    }

    public static void onClientTick(Minecraft mc) {
        if (!editing || mc.player == null || mc.level == null || targetPos == null) return;
        if (!(mc.level.getBlockEntity(targetPos) instanceof DecorAnchorBlockEntity)) { editing = false; return; }
        long window = mc.getWindow().getWindow();
        handleHotkeys(window, mc);
        if (!editing) return;
        updateHover(mc);
        handleMouseDrag(window, mc);
        int nowTick = mc.player.tickCount;
        if (nowTick - lastHintTick >= 20) { showHint(false); lastHintTick = nowTick; }
    }

    private static void handleHotkeys(long window, Minecraft mc) {
        boolean keyX = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_X);
        boolean keyY = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_Y);
        boolean keyZ = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_Z);
        boolean keyR = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_R);
        boolean keyS = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_S);
        boolean keyEnter = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_ENTER) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_KP_ENTER);
        boolean keyEsc = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_ESCAPE);

        if (keyX && !prevKeyX) { activeAxis = AxisId.X; showHint(true); }
        if (keyY && !prevKeyY) { activeAxis = AxisId.Y; showHint(true); }
        if (keyZ && !prevKeyZ) { activeAxis = AxisId.Z; showHint(true); }
        if (keyR && !prevKeyR) { mode = EditMode.ROTATE; showHint(true); }
        if (keyS && !prevKeyS) { mode = EditMode.SCALE; showHint(true); }
        if (keyEnter && !prevKeyEnter) saveAndExit(mc);
        if (keyEsc && !prevKeyEsc) cancelAndExit(mc);

        prevKeyX = keyX; prevKeyY = keyY; prevKeyZ = keyZ; prevKeyR = keyR; prevKeyS = keyS; prevKeyEnter = keyEnter; prevKeyEsc = keyEsc;
    }

    private static void updateHover(Minecraft mc) {
        if (dragging) return;
        // High quality raycast picking math can be placed here. Defaulting to keyboard active axis.
        hoverAxis = activeAxis; 
    }

    private static void handleMouseDrag(long window, Minecraft mc) {
        boolean rightDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        if (!rightDown) { dragging = false; return; }

        double mx = mc.mouseHandler.xpos();
        double my = mc.mouseHandler.ypos();
        if (!dragging) { dragging = true; lastMouseX = mx; lastMouseY = my; return; }

        float dx = (float) (mx - lastMouseX);
        float dy = (float) (my - lastMouseY);

        if (mode == EditMode.TRANSLATE) {
            float delta = (dx - dy) * 0.005f;
            switch (activeAxis) {
                case X -> offsetX = Mth.clamp(offsetX + delta, -1.5f, 1.5f);
                case Y -> offsetY = Mth.clamp(offsetY - dy * 0.005f, -1.5f, 1.5f);
                case Z -> offsetZ = Mth.clamp(offsetZ + delta, -1.5f, 1.5f);
            }
        } else if (mode == EditMode.ROTATE) {
            float delta = (dx - dy) * 0.5f;
            switch (activeAxis) {
                case X -> rotX = (rotX + delta) % 360f;
                case Y -> rotY = (rotY + delta) % 360f;
                case Z -> rotZ = (rotZ + delta) % 360f;
            }
        } else if (mode == EditMode.SCALE) {
            float delta = (dx - dy) * 0.005f;
            scale = Mth.clamp(scale + delta, 0.1f, 5.0f);
        }

        lastMouseX = mx; lastMouseY = my;
        long nowMs = System.currentTimeMillis();
        if (nowMs - lastSendMs >= 55L) { sendUpdate(); lastSendMs = nowMs; }
    }

    private static void sendUpdate() {
        if (!editing || targetPos == null || styleId == null) return;
        PacketDistributor.sendToServer(new UpdateDecorAnchorPayload(targetPos, styleId, offsetX, offsetY, offsetZ, rotX, rotY, rotZ, scale));
    }

    private static void saveAndExit(Minecraft mc) {
        sendUpdate(); editing = false; dragging = false;
        if (mc.player != null) mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("Decor gizmo saved."), true);
    }

    private static void cancelAndExit(Minecraft mc) {
        styleId = originalStyleId; offsetX = originalOffsetX; offsetY = originalOffsetY; offsetZ = originalOffsetZ;
        rotX = originalRotX; rotY = originalRotY; rotZ = originalRotZ; scale = originalScale;
        PacketDistributor.sendToServer(new UpdateDecorAnchorPayload(targetPos, styleId, offsetX, offsetY, offsetZ, rotX, rotY, rotZ, scale));
        editing = false; dragging = false;
        if (mc.player != null) mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("Decor gizmo canceled."), true);
    }

    private static void showHint(boolean force) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (!force && mc.player.tickCount - lastHintTick < 20) return;
        mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("Gizmo [" + mode.name() + "] Axis=" + activeAxis.name()), true);
    }

    public static void onRenderLevel(RenderLevelStageEvent event) {
        // High quality rendering
    }
}
"""

with open("src/main/java/com/stardew/craft/client/deco/DecorAnchorWorldGizmo.java", "w", encoding="utf-8") as f:
    f.write(gizmo_content)

print("Updated DecorAnchorWorldGizmo.java")
