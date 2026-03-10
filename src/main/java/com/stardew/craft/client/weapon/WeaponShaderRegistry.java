package com.stardew.craft.client.weapon;

import com.stardew.craft.StardewCraft;
import net.minecraft.client.renderer.ShaderInstance;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

public final class WeaponShaderRegistry {
    private static ShaderInstance weaponEffect;

    private WeaponShaderRegistry() {}

    @SubscribeEvent
    @SuppressWarnings("null")
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(
            new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "weapon_effect"),
                DefaultVertexFormat.POSITION_TEX_COLOR
            ),
            shader -> weaponEffect = shader
        );
    }

    public static void onRegisterShadersSafe(RegisterShadersEvent event) {
        try {
            onRegisterShaders(event);
        } catch (IOException e) {
            StardewCraft.LOGGER.error("Failed to register weapon shader", e);
        }
    }

    public static ShaderInstance getWeaponEffect() {
        return weaponEffect;
    }
}
