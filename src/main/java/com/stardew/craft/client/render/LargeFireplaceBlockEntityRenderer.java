package com.stardew.craft.client.render;

import com.stardew.craft.blockentity.LargeFireplaceBlockEntity;
import com.stardew.craft.client.model.block.LargeFireplaceGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class LargeFireplaceBlockEntityRenderer extends GeoBlockRenderer<LargeFireplaceBlockEntity> {
    public LargeFireplaceBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new LargeFireplaceGeoModel());
    }
}
