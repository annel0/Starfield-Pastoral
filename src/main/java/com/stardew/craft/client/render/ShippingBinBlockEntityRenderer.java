package com.stardew.craft.client.render;

import com.stardew.craft.blockentity.ShippingBinBlockEntity;
import com.stardew.craft.client.model.block.ShippingBinGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
public class ShippingBinBlockEntityRenderer extends StardewGeoBlockRenderer<ShippingBinBlockEntity> {
    public ShippingBinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new ShippingBinGeoModel());
    }
}
