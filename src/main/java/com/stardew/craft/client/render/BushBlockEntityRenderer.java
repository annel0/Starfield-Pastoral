package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stardew.craft.block.nature.BerryBushBlock;
import com.stardew.craft.blockentity.BushBlockEntity;
import com.stardew.craft.client.model.block.BushGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
@SuppressWarnings("null")
public class BushBlockEntityRenderer extends StardewGeoBlockRenderer<BushBlockEntity> {
    public BushBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new BushGeoModel());
    }

    @Override
    public void render(BushBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = be.getBlockState();
        if (state.hasProperty(BerryBushBlock.PART) && state.getValue(BerryBushBlock.PART) != BerryBushBlock.Part.MAIN) {
            return;
        }
        super.render(be, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
    }
}