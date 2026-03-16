package com.stardew.craft.client.render;

import com.stardew.craft.blockentity.WoodenChestBlockEntity;
import com.stardew.craft.client.model.block.WoodenChestGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class WoodenChestBlockEntityRenderer extends GeoBlockRenderer<WoodenChestBlockEntity> {
    public WoodenChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new WoodenChestGeoModel());
    }
}
