package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stardew.craft.block.decor.MapDecorStaticBlock;
import com.stardew.craft.blockentity.PillarGeoBlockEntity;
import com.stardew.craft.client.model.block.PillarGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;

@SuppressWarnings("null")
public class PillarGeoBlockEntityRenderer extends StardewGeoBlockRenderer<PillarGeoBlockEntity> {
    public PillarGeoBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new PillarGeoModel());
    }

    @Override
    public void render(PillarGeoBlockEntity animatable, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = animatable.getBlockState();
        if (state.hasProperty(MapDecorStaticBlock.PART)
            && state.getValue(MapDecorStaticBlock.PART) != MapDecorStaticBlock.Part.MAIN) {
            return;
        }

        Direction facing = state.hasProperty(MapDecorStaticBlock.FACING)
            ? state.getValue(MapDecorStaticBlock.FACING)
            : Direction.NORTH;

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot() + 180f));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    protected void rotateBlock(@Nonnull Direction facing, @Nonnull PoseStack poseStack) {
        // Rotation is already handled in render(); prevent GeoBlockRenderer from applying it again.
    }
}
