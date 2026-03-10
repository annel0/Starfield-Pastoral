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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.List;

@SuppressWarnings("unused")
public final class InfinityDaggerMarkRenderer {

    private static final ResourceLocation MARK_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/water_ring.png"
    );

    @SuppressWarnings("null")
    private static final RenderType MARK_RENDER_TYPE = RenderType.create(
        "stardew_infinity_dagger_mark",
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

    private InfinityDaggerMarkRenderer() {}

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
        long nowTick = mc.level.getGameTime();
        AABB box = new AABB(
            camPos.x - 48, camPos.y - 48, camPos.z - 48,
            camPos.x + 48, camPos.y + 48, camPos.z + 48
        );

        List<LivingEntity> entities = mc.level.getEntitiesOfClass(LivingEntity.class, box);
        if (entities.isEmpty()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = buffer.getBuffer(MARK_RENDER_TYPE);
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);

        for (LivingEntity entity : entities) {
            if (!InfinityDaggerMarkClientState.isMarked(entity.getId(), nowTick)) {
                continue;
            }

            float remainingRatio = InfinityDaggerMarkClientState.getRemainingRatio(entity.getId(), nowTick);
            float fade = remainingRatio < 0.35f ? (remainingRatio / 0.35f) : 1.0f;
            float pulse = 0.8f + 0.2f * Mth.sin((nowTick + partial + entity.getId()) * 0.25f);
            int alpha = Math.min(255, Math.max(20, (int) (200 * fade * pulse)));

            int r = 0x5A;
            int g = 0x2E;
            int b = 0x8A;

            Vec3 pos = entity.position().add(0, entity.getBbHeight() * 0.6, 0);
            double x = pos.x - camPos.x;
            double y = pos.y - camPos.y;
            double z = pos.z - camPos.z;

            float rot = -(nowTick + partial) * 4.5f;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(dispatcher.cameraOrientation());
            poseStack.mulPose(Axis.ZP.rotationDegrees(rot));
            poseStack.scale(0.5f, 0.5f, 0.5f);

            PoseStack.Pose last = poseStack.last();
            Matrix4f pose = last.pose();

            float size = 0.56f;
            vertex(consumer, pose, 0xF000F0, r, g, b, alpha, -size, -size, 0, 1);
            vertex(consumer, pose, 0xF000F0, r, g, b, alpha, size, -size, 1, 1);
            vertex(consumer, pose, 0xF000F0, r, g, b, alpha, size, size, 1, 0);
            vertex(consumer, pose, 0xF000F0, r, g, b, alpha, -size, size, 0, 0);

            int innerAlpha = Math.max(10, (int) (alpha * 0.6f));
            float innerSize = size * 0.72f;
            vertex(consumer, pose, 0xF000F0, r, g, b, innerAlpha, -innerSize, -innerSize, 0, 1);
            vertex(consumer, pose, 0xF000F0, r, g, b, innerAlpha, innerSize, -innerSize, 1, 1);
            vertex(consumer, pose, 0xF000F0, r, g, b, innerAlpha, innerSize, innerSize, 1, 0);
            vertex(consumer, pose, 0xF000F0, r, g, b, innerAlpha, -innerSize, innerSize, 0, 0);

            poseStack.popPose();
        }

        buffer.endBatch(MARK_RENDER_TYPE);
    }

    @SuppressWarnings("null")
    private static void vertex(VertexConsumer consumer, Matrix4f pose, int light, int r, int g, int b, int alpha,
                               float x, float y, float u, float v) {
        consumer.addVertex(pose, x, y, 0.0f)
            .setColor(r, g, b, alpha)
            .setUv(u, v)
            .setOverlay(net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(0.0f, 1.0f, 0.0f);
    }
}
