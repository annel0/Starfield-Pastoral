package com.stardew.craft.client.model.block;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.blockentity.UncertaintyStatueBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class UncertaintyStatueGeoModel extends GeoModel<UncertaintyStatueBlockEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID, "geo/block/decor/uncertainty_statue.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        StardewCraft.MODID, "textures/block/decor/uncertainty_statue.png");

    @Override
    public ResourceLocation getModelResource(UncertaintyStatueBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(UncertaintyStatueBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(UncertaintyStatueBlockEntity animatable) {
        return null;
    }
}
