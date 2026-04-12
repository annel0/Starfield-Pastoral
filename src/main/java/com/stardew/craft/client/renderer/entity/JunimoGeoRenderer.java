package com.stardew.craft.client.renderer.entity;

import com.stardew.craft.client.model.entity.JunimoGeoModel;
import com.stardew.craft.entity.junimo.JunimoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class JunimoGeoRenderer extends GeoEntityRenderer<JunimoEntity> {

    public JunimoGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new JunimoGeoModel());
        this.shadowRadius = 0.25F;
        addRenderLayer(new JunimoTintLayer(this));
    }
}
