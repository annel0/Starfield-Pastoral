package com.stardew.craft.client.weapon;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.stardew.craft.StardewCraft;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

@SuppressWarnings("unused")
public final class TideMarkRenderer {

    private static final ResourceLocation MARK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/tide_mark.png"
    );

    @SuppressWarnings("null")
    private static final RenderType MARK_RENDER_TYPE = RenderType.create(
        "stardew_tide_mark",
        DefaultVertexFormat.POSITION_TEX_COLOR,
        VertexFormat.Mode.QUADS,
        256,
        false,
        true,
        RenderType.CompositeState.builder()
            .setShaderState(new RenderType.ShaderStateShard(GameRenderer::getPositionTexColorShader))
            .setTextureState(new RenderType.TextureStateShard(MARK_TEXTURE, false, false))
            .setTransparencyState(new RenderType.TransparencyStateShard("translucent_transparency", () -> {
                com.mojang.blaze3d.systems.RenderSystem.enableBlend();
                com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
            }, () -> {
                com.mojang.blaze3d.systems.RenderSystem.disableBlend();
            }))
            .setWriteMaskState(new RenderType.WriteMaskStateShard(true, false))
            .setCullState(new RenderType.CullStateShard(false))
            .setDepthTestState(new RenderType.DepthTestStateShard("always", 519))
            .createCompositeState(false)
    );

    private TideMarkRenderer() {}

    @SuppressWarnings("null")
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        Vec3 camPos = event.getCamera().getPosition();
        @SuppressWarnings("null")
        long nowTick = mc.level.getGameTime();
        AABB box = new AABB(
            camPos.x - 48, camPos.y - 48, camPos.z - 48,
            camPos.x + 48, camPos.y + 48, camPos.z + 48
        );

        @SuppressWarnings("null")
        List<LivingEntity> entities = mc.level.getEntitiesOfClass(LivingEntity.class, box);
        if (entities.isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        @SuppressWarnings("null")
        VertexConsumer consumer = buffer.getBuffer(MARK_RENDER_TYPE);
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        for (LivingEntity entity : entities) {
            if (!TideMarkClientState.isMarked(entity.getId(), nowTick)) {
                continue;
            }

            Vec3 pos = entity.position().add(0, entity.getBbHeight() * 0.6, 0);
            double x = pos.x - camPos.x;
            double y = pos.y - camPos.y;
            double z = pos.z - camPos.z;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(dispatcher.cameraOrientation());
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            poseStack.scale(0.6f, 0.6f, 0.6f);

            PoseStack.Pose last = poseStack.last();
            Matrix4f pose = last.pose();

            float size = 0.6f;
            vertex(consumer, pose, 0xF000F0, -size, -size, 0, 1);
            vertex(consumer, pose, 0xF000F0, size, -size, 1, 1);
            vertex(consumer, pose, 0xF000F0, size, size, 1, 0);
            vertex(consumer, pose, 0xF000F0, -size, size, 0, 0);

            poseStack.popPose();
        }

        buffer.endBatch(MARK_RENDER_TYPE);
    }

    @SuppressWarnings("null")
    private static void vertex(VertexConsumer consumer, Matrix4f pose, int light,
                               float x, float y, float u, float v) {
        consumer.addVertex(pose, x, y, 0.0f)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(0.0f, 1.0f, 0.0f);
    }
}
