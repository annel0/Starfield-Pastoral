package com.stardew.craft.client.renderer.entity.indicator;

import net.minecraft.resources.ResourceLocation;

public record NpcOverheadIndicator(
    ResourceLocation texture,
    int u,
    int v,
    int width,
    int height,
    int textureWidth,
    int textureHeight,
    float scale,
    float yOffset
) {
}
