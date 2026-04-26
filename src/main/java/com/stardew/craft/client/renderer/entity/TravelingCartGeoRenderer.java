package com.stardew.craft.client.renderer.entity;

import com.stardew.craft.client.model.entity.TravelingCartGeoModel;
import com.stardew.craft.entity.npc.TravelingCartEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@SuppressWarnings("null")
public class TravelingCartGeoRenderer extends GeoEntityRenderer<TravelingCartEntity> {

    public TravelingCartGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new TravelingCartGeoModel());
        this.shadowRadius = 1.2F;
    }
}