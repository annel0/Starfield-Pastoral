package com.stardew.craft.client.gui;

import com.stardew.craft.blockentity.DecorAnchorBlockEntity;
import com.stardew.craft.network.payload.UpdateDecorAnchorPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@SuppressWarnings("null")
public class DecorAnchorGizmoScreen extends Screen {

    public static DecorAnchorGizmoScreen instance = null;
    public static Matrix4f viewMatrix = new Matrix4f();
    public static Matrix4f projMatrix = new Matrix4f();

    private final BlockPos targetPos;
    private final String styleId;

    public float offsetX, offsetY, offsetZ;
    public float rotX, rotY, rotZ;
    public float scaleX, scaleY, scaleZ;

    private float originalOffsetX, originalOffsetY, originalOffsetZ;
    private float originalRotX, originalRotY, originalRotZ;
    private float originalScaleX, originalScaleY, originalScaleZ;

    public enum GizmoMode { TRANSLATE, ROTATE, SCALE }
    public enum Axis { NONE, X, Y, Z }

    public GizmoMode currentMode = GizmoMode.TRANSLATE;
    public Axis hoveredAxis = Axis.NONE;
    public Axis draggingAxis = Axis.NONE;

    private double dragStartX, dragStartY;
    private float initialParamValue;

    public DecorAnchorGizmoScreen(BlockPos targetPos, String styleId, float ox, float oy, float oz, float rx, float ry, float rz, float sx, float sy, float sz) {
        super(Component.literal("Gizmo Screen"));
        this.targetPos = targetPos;
        this.styleId = styleId;
        this.offsetX = ox; this.offsetY = oy; this.offsetZ = oz;
        this.rotX = rx; this.rotY = ry; this.rotZ = rz;
        this.scaleX = sx; this.scaleY = sy; this.scaleZ = sz;
        
        this.originalOffsetX = ox; this.originalOffsetY = oy; this.originalOffsetZ = oz;
        this.originalRotX = rx; this.originalRotY = ry; this.originalRotZ = rz;
        this.originalScaleX = sx; this.originalScaleY = sy; this.originalScaleZ = sz;
    }

    @Override
    protected void init() {
        super.init();
        instance = this;
    }

    @Override
    public void removed() {
        super.removed();
        if (instance == this) instance = null;
    }

    @Override
    public void renderBackground(GuiGraphics gg, int mouseX, int mouseY, float pt) {
        // no background
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float pt) {
        super.render(gg, mouseX, mouseY, pt);
        
        gg.drawCenteredString(font, "Axiom Style Gizmo (T: Translate, R: Rotate, S: Scale, ENTER: Save, ESC: Cancel)", width / 2, 10, 0xFFFFFF);
        gg.drawCenteredString(font, "Current Mode: " + currentMode.name(), width / 2, 25, 0xFFFF55);
        gg.drawCenteredString(font, String.format("Offsets: %.2f, %.2f, %.2f", offsetX, offsetY, offsetZ), width / 2, 40, 0xAAAAAA);

        if (draggingAxis == Axis.NONE) {
            hoveredAxis = detectHover(mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredAxis != Axis.NONE) {
            draggingAxis = hoveredAxis;
            dragStartX = mouseX;
            dragStartY = mouseY;
            
            if (currentMode == GizmoMode.TRANSLATE) {
                if (draggingAxis == Axis.X) initialParamValue = offsetX;
                if (draggingAxis == Axis.Y) initialParamValue = offsetY;
                if (draggingAxis == Axis.Z) initialParamValue = offsetZ;
            } else if (currentMode == GizmoMode.ROTATE) {
                if (draggingAxis == Axis.X) initialParamValue = rotX;
                if (draggingAxis == Axis.Y) initialParamValue = rotY;
                if (draggingAxis == Axis.Z) initialParamValue = rotZ;
            } else {
                if (draggingAxis == Axis.X) initialParamValue = scaleX;
                if (draggingAxis == Axis.Y) initialParamValue = scaleY;
                if (draggingAxis == Axis.Z) initialParamValue = scaleZ;
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggingAxis != Axis.NONE) {
            applyDrag(mouseX, mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (draggingAxis != Axis.NONE) {
            draggingAxis = Axis.NONE;
            sendUpdate();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_T) currentMode = GizmoMode.TRANSLATE;
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_R) currentMode = GizmoMode.ROTATE;
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_S) currentMode = GizmoMode.SCALE;
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER) {
            sendUpdate();
            this.onClose();
            return true;
        }
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            revert();
            sendUpdate();
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void revert() {
        offsetX = originalOffsetX; offsetY = originalOffsetY; offsetZ = originalOffsetZ;
        rotX = originalRotX; rotY = originalRotY; rotZ = originalRotZ;
        scaleX = originalScaleX; scaleY = originalScaleY; scaleZ = originalScaleZ;
    }

    private void sendUpdate() {
        PacketDistributor.sendToServer(new UpdateDecorAnchorPayload(
            targetPos, styleId, offsetX, offsetY, offsetZ, rotX, rotY, rotZ, scaleX, scaleY, scaleZ
        ));
    }

    private Axis detectHover(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.cameraEntity == null) return Axis.NONE;
        Vector3f camPos = new Vector3f((float)mc.gameRenderer.getMainCamera().getPosition().x, (float)mc.gameRenderer.getMainCamera().getPosition().y, (float)mc.gameRenderer.getMainCamera().getPosition().z);
        Vector3f origin = new Vector3f(targetPos.getX() + 0.5f + offsetX, targetPos.getY() + offsetY, targetPos.getZ() + 0.5f + offsetZ);
        
        float bestDist = 20.0f; // Screen pixels
        Axis hit = Axis.NONE;
        
        if (currentMode == GizmoMode.SCALE) {
            // Scale and Translate gizmos are both linear axes
        }

        // Check X
        Vector3f pX = new Vector3f(origin).add(2.0f, 0, 0);
        float dX = distToLineScreen(origin, pX, mouseX, mouseY);
        if (dX < bestDist) { bestDist = dX; hit = Axis.X; }

        // Check Y
        Vector3f pY = new Vector3f(origin).add(0, 2.0f, 0);
        float dY = distToLineScreen(origin, pY, mouseX, mouseY);
        if (dY < bestDist) { bestDist = dY; hit = Axis.Y; }

        // Check Z
        Vector3f pZ = new Vector3f(origin).add(0, 0, 2.0f);
        float dZ = distToLineScreen(origin, pZ, mouseX, mouseY);
        if (dZ < bestDist) { bestDist = dZ; hit = Axis.Z; }

        return hit;
    }

    private float distToLineScreen(Vector3f w0, Vector3f w1, double mx, double my) {
        Vector4f s0 = project(w0);
        Vector4f s1 = project(w1);
        if (s0 == null || s1 == null) return 99999f;
        
        float dx = s1.x - s0.x;
        float dy = s1.y - s0.y;
        float lenSq = dx*dx + dy*dy;
        if (lenSq == 0) return (float)Math.hypot(mx - s0.x, my - s0.y);
        
        float t = (float)(((mx - s0.x)*dx + (my - s0.y)*dy) / lenSq);
        t = Math.max(0, Math.min(1, t));
        
        float projX = s0.x + t * dx;
        float projY = s0.y + t * dy;
        return (float)Math.hypot(mx - projX, my - projY);
    }

    private Vector4f project(Vector3f worldPos) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.cameraEntity == null) return null;
        Vector3f camPos = new Vector3f((float)mc.gameRenderer.getMainCamera().getPosition().x, (float)mc.gameRenderer.getMainCamera().getPosition().y, (float)mc.gameRenderer.getMainCamera().getPosition().z);
        
        Vector4f p = new Vector4f(worldPos.x - camPos.x, worldPos.y - camPos.y, worldPos.z - camPos.z, 1.0f);
        p.mul(viewMatrix).mul(projMatrix);
        if (p.w <= 0) return null;
        
        p.x /= p.w; p.y /= p.w;
        
        float screenX = (p.x + 1.0f) * 0.5f * mc.getWindow().getGuiScaledWidth();
        float screenY = (1.0f - p.y) * 0.5f * mc.getWindow().getGuiScaledHeight();
        
        return new Vector4f(screenX, screenY, 0, 0);
    }

    private void applyDrag(double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.cameraEntity == null) return;
        Vector3f origin = new Vector3f(targetPos.getX() + 0.5f + (draggingAxis==Axis.X?0:offsetX), targetPos.getY() + (draggingAxis==Axis.Y?0:offsetY), targetPos.getZ() + 0.5f + (draggingAxis==Axis.Z?0:offsetZ));
        
        Vector3f end = new Vector3f(origin);
        if (draggingAxis == Axis.X) end.add(1, 0, 0);
        if (draggingAxis == Axis.Y) end.add(0, 1, 0);
        if (draggingAxis == Axis.Z) end.add(0, 0, 1);
        
        Vector4f s0 = project(origin);
        Vector4f s1 = project(end);
        if (s0 == null || s1 == null) return;
        
        float dx = s1.x - s0.x;
        float dy = s1.y - s0.y;
        float lenSq = dx*dx + dy*dy;
        if (lenSq == 0) return;
        
        float t = (float)(((mouseX - s0.x)*dx + (mouseY - s0.y)*dy) / lenSq);
        float dt = (float)(((mouseX - dragStartX)*dx + (mouseY - dragStartY)*dy) / lenSq);
        
        // Multipliers based on mode
        if (currentMode == GizmoMode.TRANSLATE) {
            float val = initialParamValue + dt * 1.5f;
            if (draggingAxis == Axis.X) offsetX = val;
            if (draggingAxis == Axis.Y) offsetY = val;
            if (draggingAxis == Axis.Z) offsetZ = val;
        } else if (currentMode == GizmoMode.ROTATE) {
            float val = initialParamValue - dt * 180f;
            if (draggingAxis == Axis.X) rotX = val;
            if (draggingAxis == Axis.Y) rotY = val;
            if (draggingAxis == Axis.Z) rotZ = val;
        } else if (currentMode == GizmoMode.SCALE) {
            float val = initialParamValue + dt * 1.0f;
            if (val < 0.1f) val = 0.1f;
            if (draggingAxis == Axis.X) scaleX = val;
            if (draggingAxis == Axis.Y) scaleY = val;
            if (draggingAxis == Axis.Z) scaleZ = val;
        }
    }
    
    public BlockPos getTargetPos() { return targetPos; }
}
