package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stardew.craft.blockentity.MuseumExhibitStandBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class MuseumExhibitStandBlockEntityRenderer implements BlockEntityRenderer<MuseumExhibitStandBlockEntity> {
    public MuseumExhibitStandBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @SuppressWarnings("null")
    @Override
    public void render(@Nonnull MuseumExhibitStandBlockEntity be, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ItemStack display = be.getDisplayItem();
        if (display.isEmpty()) {
            return;
        }

        float yaw = ((be.getLevel() != null ? be.getLevel().getGameTime() : 0L) + partialTick) * 2.0f;

        poseStack.pushPose();
        // The display glass top is at Y=21, the bottom plate is at Y=14. Space available: 7 pixels.
        // Scale to 7/16 = 0.4375f. Item center Y = 14/16 + (0.4375 / 2) = 1.09375f
        poseStack.translate(0.5f, 1.09375f, 0.5f);
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.scale(0.4375f, 0.4375f, 0.4375f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                display,
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
}
