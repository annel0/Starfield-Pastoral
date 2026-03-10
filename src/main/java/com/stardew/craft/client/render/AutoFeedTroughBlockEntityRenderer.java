package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stardew.craft.blockentity.AutoFeedTroughBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("null")
public class AutoFeedTroughBlockEntityRenderer implements BlockEntityRenderer<AutoFeedTroughBlockEntity> {
    public AutoFeedTroughBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @SuppressWarnings("null")
    @Override
    public void render(@SuppressWarnings("null") AutoFeedTroughBlockEntity be, float partialTick, @SuppressWarnings("null") PoseStack poseStack, @SuppressWarnings("null") MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (be == null) {
            return;
        }

        ItemStack hay = be.getHayStack();
        if (hay.isEmpty()) {
            return;
        }

        long seed = be.getBlockPos().asLong();
        float yaw = randomRange(seed ^ 0x72B4C91FL, -25.0f, 25.0f);
        float offsetX = randomRange(seed ^ 0x4CC99A11L, -0.06f, 0.06f);
        float offsetZ = randomRange(seed ^ 0x11AA66E3L, -0.08f, 0.08f);

        poseStack.pushPose();
        poseStack.translate(0.5f + offsetX, 0.145f, 0.5f + offsetZ);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0f));
        poseStack.scale(0.72f, 0.72f, 0.72f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
            hay,
            ItemDisplayContext.FIXED,
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
