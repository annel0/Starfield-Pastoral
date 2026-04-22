package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stardew.craft.blockentity.GiantCropBlockEntity;
import com.stardew.craft.client.model.block.GiantCropGeoModel;
import com.stardew.craft.block.crop.giant.GiantCropBlock;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

@SuppressWarnings("null")
public class GiantCropBlockEntityRenderer extends GeoBlockRenderer<GiantCropBlockEntity> {

    public GiantCropBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new GiantCropGeoModel());
    }

    @Override
    public void render(GiantCropBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        if (state.hasProperty(GiantCropBlock.PART)
            && state.getValue(GiantCropBlock.PART) != GiantCropBlock.Part.MAIN) {
            return;
        }
        super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
