package com.stardew.craft.client.model.entity;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.entity.npc.StardewNpcEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.model.GeoModel;

@SuppressWarnings("null")
public class NpcGeoModel extends GeoModel<StardewNpcEntity> {
    private static final String FALLBACK_NPC_ID = "lewis";

    @Override
    public ResourceLocation getModelResource(StardewNpcEntity animatable) {
        String npcId = resolveNpcId(animatable);
        return ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/entity/npc/" + npcId + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(StardewNpcEntity animatable) {
        String npcId = resolveNpcId(animatable);
        return ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/entity/npc/" + npcId + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(StardewNpcEntity animatable) {
        String npcId = resolveNpcId(animatable);
        return ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "animations/entity/npc/" + npcId + ".animation.json");
    }

    private static String resolveNpcId(StardewNpcEntity animatable) {
        String npcId = animatable.getNpcId();
        if (npcId == null || npcId.isBlank()) {
            return FALLBACK_NPC_ID;
        }

        String normalized = npcId.toLowerCase();
        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        if (resourceManager == null) {
            return normalized;
        }

        ResourceLocation model = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "geo/entity/npc/" + normalized + ".geo.json");
        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/entity/npc/" + normalized + ".png");
        ResourceLocation animation = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "animations/entity/npc/" + normalized + ".animation.json");
        if (resourceManager.getResource(model).isPresent()
            && resourceManager.getResource(texture).isPresent()
            && resourceManager.getResource(animation).isPresent()) {
            return normalized;
        }

        return FALLBACK_NPC_ID;
    }
}
