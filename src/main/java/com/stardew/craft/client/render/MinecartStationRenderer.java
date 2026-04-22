package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stardew.craft.entity.minecart.MinecartStationEntity;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.joml.Quaternionf;

/**
 * 矿车站点渲染器 — 复用原版 MinecartModel 的模型+纹理，
 * 站着不动（不像真矿车那样会晃来晃去）。
 */
@SuppressWarnings("null")
public class MinecartStationRenderer extends EntityRenderer<MinecartStationEntity> {

    private static final ResourceLocation MINECART_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/minecart.png");

    private final MinecartModel<AbstractMinecart> model;

    public MinecartStationRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.7F;
        this.model = new MinecartModel<>(ctx.bakeLayer(ModelLayers.MINECART));
    }

    @Override
    public void render(MinecartStationEntity entity, float yaw, float partialTicks,
                       PoseStack pose, MultiBufferSource buffer, int packedLight) {
        super.render(entity, yaw, partialTicks, pose, buffer, packedLight);
        pose.pushPose();
        pose.translate(0.0D, 0.375D, 0.0D);
        // 旋转到玩家设置的 yRot，站立不翻转。
        pose.mulPose(new Quaternionf().rotationY(-yaw * Mth.DEG_TO_RAD));
        // 原版 Minecart 渲染时 model 是倒过来摆的
        pose.scale(-1.0F, -1.0F, 1.0F);
        this.model.setupAnim(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
        var vertex = buffer.getBuffer(this.model.renderType(MINECART_TEXTURE));
        this.model.renderToBuffer(pose, vertex, packedLight, net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY);
        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(MinecartStationEntity entity) {
        return MINECART_TEXTURE;
    }
}
