package com.stardew.craft.client.render;

import net.minecraft.world.level.block.entity.BlockEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import javax.annotation.Nonnull;

public class StardewGeoBlockRenderer<T extends BlockEntity & GeoBlockEntity> extends GeoBlockRenderer<T> {
    public StardewGeoBlockRenderer(GeoModel<T> model) {
        super(model);
    }

    @Override
    public boolean shouldRenderOffScreen(@Nonnull T blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}