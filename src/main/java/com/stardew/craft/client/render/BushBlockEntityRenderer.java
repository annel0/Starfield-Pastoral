package com.stardew.craft.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stardew.craft.StardewCraftClient;
import com.stardew.craft.block.nature.BerryBushBlock;
import com.stardew.craft.blockentity.BushBlockEntity;
import com.stardew.craft.client.model.block.BushGeoModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.util.Color;
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

    @Override
    public Color getRenderColor(BushBlockEntity animatable, float partialTick, int packedLight) {
        int color = StardewCraftClient.resolveSeasonalLeafColor(
                animatable.getBlockState(),
                animatable.getLevel(),
                animatable.getBlockPos());
        return Color.ofARGB(255, (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
    }

    @Nullable
    @Override
    public RenderType getRenderType(BushBlockEntity animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityCutoutNoCull(texture);
    }
}