package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stardew.craft.block.decor.FlowerDanceDecorBlock;
import com.stardew.craft.blockentity.FlowerDanceDecorBlockEntity;
import com.stardew.craft.client.model.block.FlowerDanceDecorGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class FlowerDanceDecorBlockEntityRenderer extends StardewGeoBlockRenderer<FlowerDanceDecorBlockEntity> {
    public FlowerDanceDecorBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new FlowerDanceDecorGeoModel());
    }

    @Override
    public void render(FlowerDanceDecorBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = animatable.getBlockState();
        Direction facing = state.hasProperty(FlowerDanceDecorBlock.FACING)
            ? state.getValue(FlowerDanceDecorBlock.FACING)
            : Direction.NORTH;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    protected void rotateBlock(@Nonnull Direction facing, @Nonnull PoseStack poseStack) {
    }
}