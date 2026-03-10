package com.stardew.craft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.entity.projectile.TideAnchorProjectileEntity;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class TideAnchorProjectileRenderer extends EntityRenderer<TideAnchorProjectileEntity> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID,
        "textures/gui/weapon_skill/tide_anchor.png"
    );

    public TideAnchorProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(@SuppressWarnings("null") TideAnchorProjectileEntity entity) {
        return TEXTURE;
    }

    @SuppressWarnings("null")
    @Override
    public void render(@SuppressWarnings("null") TideAnchorProjectileEntity entity, float entityYaw, float partialTicks,
                       @SuppressWarnings("null")    PoseStack poseStack, @SuppressWarnings("null")    MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(0.9f, 0.9f, 0.9f);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        PoseStack.Pose last = poseStack.last();
        Matrix4f pose = last.pose();
        @SuppressWarnings("null")
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        float size = 0.5f;
        vertex(consumer, pose, packedLight, -size, -size, 0, 1);
        vertex(consumer, pose, packedLight, size, -size, 1, 1);
        vertex(consumer, pose, packedLight, size, size, 1, 0);
        vertex(consumer, pose, packedLight, -size, size, 0, 0);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @SuppressWarnings("null")
    private static void vertex(VertexConsumer consumer, Matrix4f pose, int light,
                               float x, float y, float u, float v) {
        consumer.addVertex(pose, x, y, 0.0f)
            .setColor(255, 255, 255, 255)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(0.0f, 1.0f, 0.0f);
    }
}
