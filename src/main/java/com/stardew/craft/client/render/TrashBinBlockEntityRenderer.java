package com.stardew.craft.client.render;

import com.stardew.craft.blockentity.TrashBinBlockEntity;
import com.stardew.craft.client.model.block.TrashBinGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
public class TrashBinBlockEntityRenderer extends StardewGeoBlockRenderer<TrashBinBlockEntity> {
    public TrashBinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new TrashBinGeoModel());
    }
}
