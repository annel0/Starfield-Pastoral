package com.stardew.craft.client.render;

import com.stardew.craft.blockentity.WoodenChestBlockEntity;
import com.stardew.craft.client.model.block.WoodenChestGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
public class WoodenChestBlockEntityRenderer extends StardewGeoBlockRenderer<WoodenChestBlockEntity> {
    public WoodenChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new WoodenChestGeoModel());
    }
}
