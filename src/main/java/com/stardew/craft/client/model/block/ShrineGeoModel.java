package com.stardew.craft.client.model.block;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.blockentity.ShrineBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ShrineGeoModel extends GeoModel<ShrineBlockEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/decor/shrine_7.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/deco/misc/common/shrine_7.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "animations/block/decor/shrine_7.animation.json");

    @Override
    public ResourceLocation getModelResource(ShrineBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(ShrineBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(ShrineBlockEntity animatable) {
        return ANIMATION;
    }
}
