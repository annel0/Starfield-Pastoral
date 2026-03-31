package com.stardew.craft.client.model.block;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.blockentity.PillarGeoBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PillarGeoModel extends GeoModel<PillarGeoBlockEntity> {
    private static final ResourceLocation MODEL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/block/decor/pillar_1.geo.json");
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/block/deco/misc/common/pillar_1.png");
    private static final ResourceLocation ANIMATION = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "animations/block/decor/pillar_1.animation.json");

    @Override
    public ResourceLocation getModelResource(PillarGeoBlockEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(PillarGeoBlockEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(PillarGeoBlockEntity animatable) {
        return ANIMATION;
    }
}
