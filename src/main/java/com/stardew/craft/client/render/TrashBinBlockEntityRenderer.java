package com.stardew.craft.client.render;

import com.stardew.craft.blockentity.TrashBinBlockEntity;
import com.stardew.craft.client.model.block.TrashBinGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class TrashBinBlockEntityRenderer extends GeoBlockRenderer<TrashBinBlockEntity> {
    public TrashBinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new TrashBinGeoModel());
    }
}
