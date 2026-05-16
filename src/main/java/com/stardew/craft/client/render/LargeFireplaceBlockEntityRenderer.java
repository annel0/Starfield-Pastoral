package com.stardew.craft.client.render;

import com.stardew.craft.blockentity.LargeFireplaceBlockEntity;
import com.stardew.craft.client.model.block.LargeFireplaceGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
public class LargeFireplaceBlockEntityRenderer extends StardewGeoBlockRenderer<LargeFireplaceBlockEntity> {
    public LargeFireplaceBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new LargeFireplaceGeoModel());
    }
}
