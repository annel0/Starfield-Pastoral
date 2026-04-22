package com.stardew.craft.client.renderer.entity;

import com.stardew.craft.client.model.entity.CamelMerchantGeoModel;
import com.stardew.craft.entity.npc.CamelMerchantEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@SuppressWarnings("null")
public class CamelMerchantGeoRenderer extends GeoEntityRenderer<CamelMerchantEntity> {

    public CamelMerchantGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new CamelMerchantGeoModel());
        this.shadowRadius = 0.6F;
    }
}
