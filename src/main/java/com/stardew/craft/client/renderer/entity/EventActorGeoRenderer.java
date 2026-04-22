package com.stardew.craft.client.renderer.entity;

import com.stardew.craft.client.model.entity.EventActorGeoModel;
import com.stardew.craft.cutscene.runtime.EventActorEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderer for {@link EventActorEntity}.
 * Uses the same NPC models via {@link EventActorGeoModel}.
 */
public class EventActorGeoRenderer extends GeoEntityRenderer<EventActorEntity> {

    public EventActorGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new EventActorGeoModel());
        this.shadowRadius = 0.35F;
    }
}
