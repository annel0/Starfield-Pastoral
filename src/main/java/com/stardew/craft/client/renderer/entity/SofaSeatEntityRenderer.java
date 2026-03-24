package com.stardew.craft.client.renderer.entity;

import com.stardew.craft.entity.seat.SofaSeatEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("null")
public class SofaSeatEntityRenderer extends EntityRenderer<SofaSeatEntity> {
    private static final ResourceLocation EMPTY = TextureAtlas.LOCATION_BLOCKS;

    public SofaSeatEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(SofaSeatEntity entity) {
        return EMPTY;
    }
}
