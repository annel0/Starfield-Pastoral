package com.stardew.craft.client.render;

import com.stardew.craft.blockentity.MineChestBlockEntity;
import com.stardew.craft.client.model.block.MineChestGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MineChestBlockEntityRenderer extends GeoBlockRenderer<MineChestBlockEntity> {
    public MineChestBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new MineChestGeoModel());
    }
}
