package com.stardew.craft.client.fishpond;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;

public final class ClientFishPondFishRenderer {
    private static final float PX = 1.0F / 16.0F;

    private ClientFishPondFishRenderer() {
    }

    public static void renderFish(ItemStack fishStack,
                                  PoseStack poseStack,
                                  MultiBufferSource buffer,
                                  ClientLevel level,
                                  int packedLight,
                                  float yawDegrees,
                                  float pitchDegrees,
                                  float rollDegrees,
                                  float renderScale,
                                  boolean flipped) {
        if (fishStack.isEmpty()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(rollDegrees));
        if (flipped) {
            poseStack.scale(-1.0F, 1.0F, 1.0F);
        }
        poseStack.scale(renderScale, renderScale, renderScale);

        BakedModel model = minecraft.getItemRenderer().getModel(fishStack, level, null, 0);
        @SuppressWarnings("deprecation")
        RenderType renderType = ItemBlockRenderTypes.getRenderType(fishStack, true);

        renderPass(minecraft, model, fishStack, poseStack, buffer.getBuffer(renderType), packedLight, 1.0F, 1.0F, 1.0F, 0.030F * PX);
        renderPass(minecraft, model, fishStack, poseStack, buffer.getBuffer(renderType), packedLight, 1.0F, 1.0F, 1.0F, 0.000F);
        renderPass(minecraft, model, fishStack, poseStack, buffer.getBuffer(renderType), packedLight, 0.72F, 0.72F, 0.72F, -0.030F * PX);
        renderPass(minecraft, model, fishStack, poseStack, buffer.getBuffer(renderType), packedLight, 0.52F, 0.52F, 0.52F, -0.060F * PX);
    }

    private static void renderPass(Minecraft minecraft,
                                   BakedModel model,
                                   ItemStack fishStack,
                                   PoseStack poseStack,
                                   VertexConsumer consumer,
                                   int packedLight,
                                   float red,
                                   float green,
                                   float blue,
                                   float depthOffset) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.0F, depthOffset);
        minecraft.getItemRenderer().renderModelLists(
            model,
            fishStack,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            new TintingVertexConsumer(consumer, red, green, blue)
        );
        poseStack.popPose();
    }

    private static final class TintingVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;
        private final float red;
        private final float green;
        private final float blue;

        private TintingVertexConsumer(VertexConsumer delegate, float red, float green, float blue) {
            this.delegate = delegate;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        @Override
        public VertexConsumer addVertex(float x, float y, float z) {
            return delegate.addVertex(x, y, z);
        }

        @Override
        public VertexConsumer setColor(int red, int green, int blue, int alpha) {
            return delegate.setColor(
                Math.min(255, Math.max(0, Math.round(red * this.red))),
                Math.min(255, Math.max(0, Math.round(green * this.green))),
                Math.min(255, Math.max(0, Math.round(blue * this.blue))),
                alpha
            );
        }

        @Override
        public VertexConsumer setUv(float u, float v) {
            return delegate.setUv(u, v);
        }

        @Override
        public VertexConsumer setUv1(int u, int v) {
            return delegate.setUv1(u, v);
        }

        @Override
        public VertexConsumer setUv2(int u, int v) {
            return delegate.setUv2(u, v);
        }

        @Override
        public VertexConsumer setNormal(float x, float y, float z) {
            return delegate.setNormal(x, y, z);
        }
    }
}