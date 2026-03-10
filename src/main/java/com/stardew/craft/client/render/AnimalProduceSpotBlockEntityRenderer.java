package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stardew.craft.blockentity.AnimalProduceSpotBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class AnimalProduceSpotBlockEntityRenderer implements BlockEntityRenderer<AnimalProduceSpotBlockEntity> {
    public AnimalProduceSpotBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@Nonnull AnimalProduceSpotBlockEntity be,
                       float partialTick,
                       @Nonnull PoseStack poseStack,
                       @Nonnull MultiBufferSource buffer,
                       int packedLight,
                       int packedOverlay) {
        ItemStack stack = be.getProduceStack();
        if (stack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        long seed = be.getBlockPos().asLong();
        float yaw = randomRange(seed ^ 0x72B4C91FL, -25.0f, 25.0f);
        float offsetX = randomRange(seed ^ 0x4CC99A11L, -0.06f, 0.06f);
        float offsetZ = randomRange(seed ^ 0x11AA66E3L, -0.08f, 0.08f);

        poseStack.translate(0.5f + offsetX, 0.035f, 0.5f + offsetZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.scale(0.68f, 0.68f, 0.68f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
            stack,
            ItemDisplayContext.GROUND,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            poseStack,
            buffer,
            be.getLevel(),
            0
        );
        poseStack.popPose();
    }

    private static float randomRange(long seed, float min, float max) {
        float t = Mth.frac((float) (Math.sin(seed * 12.9898) * 43758.5453));
        return min + (max - min) * t;
    }
}
