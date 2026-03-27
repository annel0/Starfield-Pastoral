package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stardew.craft.block.decor.MapDecorStaticBlock;
import com.stardew.craft.blockentity.BookshelfGeoBlockEntity;
import com.stardew.craft.client.model.block.BookshelfGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import javax.annotation.Nonnull;

public class BookshelfGeoBlockEntityRenderer extends GeoBlockRenderer<BookshelfGeoBlockEntity> {
    public BookshelfGeoBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new BookshelfGeoModel());
    }

    @Override
    public void render(@Nonnull BookshelfGeoBlockEntity animatable, float partialTick, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
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
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
