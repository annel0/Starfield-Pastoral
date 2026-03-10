package com.stardew.craft.client.weapon;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public final class WeaponEffectRenderTypes {
    private static final RenderStateShard.ShaderStateShard WEAPON_EFFECT_SHADER =
        new RenderStateShard.ShaderStateShard(WeaponShaderRegistry::getWeaponEffect);

    private static final Map<ResourceLocation, RenderType> CACHE = Maps.newHashMap();

    private WeaponEffectRenderTypes() {}

    @SuppressWarnings("null")
    public static RenderType weaponEffect(ResourceLocation texture) {
        if (WeaponShaderRegistry.getWeaponEffect() == null) {
            return RenderType.entityTranslucent(texture);
        }
        return CACHE.computeIfAbsent(texture, tex -> RenderType.create(
            "stardewcraft_weapon_effect_" + tex.getPath().replace('/', '_'),
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                .setShaderState(WEAPON_EFFECT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(tex, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                .setLightmapState(RenderStateShard.LIGHTMAP)
                .setOverlayState(RenderStateShard.OVERLAY)
                .createCompositeState(false)
        ));
    }
}
