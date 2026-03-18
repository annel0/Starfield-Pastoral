package com.stardew.craft.client.deco;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stardew.craft.client.gui.DecorAnchorGizmoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class DecorAnchorWorldGizmo {
    private DecorAnchorWorldGizmo() {}

    public static void startFromPayload(BlockPos pos, String inStyleId, float ox, float oy, float oz, float rx, float ry, float rz, float sx, float sy, float sz) {
        Minecraft.getInstance().setScreen(new DecorAnchorGizmoScreen(pos, inStyleId, ox, oy, oz, rx, ry, rz, sx, sy, sz));
    }

    public static void saveMatrices(RenderLevelStageEvent event) {
        if (DecorAnchorGizmoScreen.instance != null) {
            DecorAnchorGizmoScreen.viewMatrix.set(event.getPoseStack().last().pose());
            DecorAnchorGizmoScreen.projMatrix.set(event.getProjectionMatrix());
        }
    }

    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        saveMatrices(event);
        PoseStack pose = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 camPos = event.getCamera().getPosition();
        if (DecorAnchorGizmoScreen.instance == null) return;
        
        BlockPos target = DecorAnchorGizmoScreen.instance.getTargetPos();
        float ox = DecorAnchorGizmoScreen.instance.offsetX;
        float oy = DecorAnchorGizmoScreen.instance.offsetY;
        float oz = DecorAnchorGizmoScreen.instance.offsetZ;
        
        pose.pushPose();
        pose.translate(target.getX() + 0.5 - camPos.x + ox, target.getY() - camPos.y + oy, target.getZ() + 0.5 - camPos.z + oz);
        
        VertexConsumer vc = buffers.getBuffer(RenderType.lines());
        Matrix4f m = pose.last().pose();

        DecorAnchorGizmoScreen.Axis hover = DecorAnchorGizmoScreen.instance.hoveredAxis;
        DecorAnchorGizmoScreen.Axis drag = DecorAnchorGizmoScreen.instance.draggingAxis;
        
        boolean redActive = (drag == DecorAnchorGizmoScreen.Axis.X || (drag == DecorAnchorGizmoScreen.Axis.NONE && hover == DecorAnchorGizmoScreen.Axis.X));
        boolean grnActive = (drag == DecorAnchorGizmoScreen.Axis.Y || (drag == DecorAnchorGizmoScreen.Axis.NONE && hover == DecorAnchorGizmoScreen.Axis.Y));
        boolean bluActive = (drag == DecorAnchorGizmoScreen.Axis.Z || (drag == DecorAnchorGizmoScreen.Axis.NONE && hover == DecorAnchorGizmoScreen.Axis.Z));

        // X axis (Red)
        vc.addVertex(m, 0, 0, 0).setColor(redActive ? 0xFFFFDD55 : 0xFFFF0000).setNormal(pose.last(), 1, 0, 0);
        vc.addVertex(m, 1.5f, 0, 0).setColor(redActive ? 0xFFFFDD55 : 0xFFFF0000).setNormal(pose.last(), 1, 0, 0);

        // Y axis (Green)
        vc.addVertex(m, 0, 0, 0).setColor(grnActive ? 0xFFFFDD55 : 0xFF00FF00).setNormal(pose.last(), 0, 1, 0);
        vc.addVertex(m, 0, 1.5f, 0).setColor(grnActive ? 0xFFFFDD55 : 0xFF00FF00).setNormal(pose.last(), 0, 1, 0);

        // Z axis (Blue)
        vc.addVertex(m, 0, 0, 0).setColor(bluActive ? 0xFFFFDD55 : 0xFF0000FF).setNormal(pose.last(), 0, 0, 1);
        vc.addVertex(m, 0, 0, 1.5f).setColor(bluActive ? 0xFFFFDD55 : 0xFF0000FF).setNormal(pose.last(), 0, 0, 1);
        
        pose.popPose();
    }
}
