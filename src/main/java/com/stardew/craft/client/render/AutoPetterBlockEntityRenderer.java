package com.stardew.craft.client.render;

import com.stardew.craft.blockentity.AutoPetterBlockEntity;
import com.stardew.craft.client.model.block.AutoPetterGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
public class AutoPetterBlockEntityRenderer extends StardewGeoBlockRenderer<AutoPetterBlockEntity> {
    public AutoPetterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new AutoPetterGeoModel());
    }
}