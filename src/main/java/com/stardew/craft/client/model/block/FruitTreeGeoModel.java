package com.stardew.craft.client.model.block;

import com.stardew.craft.blockentity.FruitTreeBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class FruitTreeGeoModel extends GeoModel<FruitTreeBlockEntity> {
    @Override
    public ResourceLocation getModelResource(FruitTreeBlockEntity animatable) {
        return animatable.getFruitTreeType().matureModel();
    }

    @Override
    public ResourceLocation getTextureResource(FruitTreeBlockEntity animatable) {
        return animatable.getFruitTreeType().matureTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(FruitTreeBlockEntity animatable) {
        return null;
    }
}
