package com.stardew.craft.client.render;

import com.stardew.craft.client.model.block.StoneChestGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class StoneChestBlockEntityRenderer extends GeoBlockRenderer<com.stardew.craft.blockentity.StoneChestBlockEntity> {
    public StoneChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new StoneChestGeoModel());
    }
}
