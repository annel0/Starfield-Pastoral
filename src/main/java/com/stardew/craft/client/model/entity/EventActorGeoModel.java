package com.stardew.craft.client.model.entity;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.cutscene.runtime.EventActorEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeckoLib model for {@link EventActorEntity}.
 * Reuses the same NPC model/texture/animation files based on npcId,
 * identical to {@link NpcGeoModel}.
 */
public class EventActorGeoModel extends GeoModel<EventActorEntity> {

    private static final String FALLBACK_NPC_ID = "lewis";

    @Override
    public ResourceLocation getModelResource(EventActorEntity entity) {
        String id = resolveNpcId(entity);
        return ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID,
                "geo/entity/npc/" + id + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EventActorEntity entity) {
        String id = resolveNpcId(entity);
        return ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID,
                "textures/entity/npc/" + id + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(EventActorEntity entity) {
        String id = resolveNpcId(entity);
        return ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID,
                "animations/entity/npc/" + id + ".animation.json");
    }

    private String resolveNpcId(EventActorEntity entity) {
        String raw = entity.getNpcId();
        if (raw == null || raw.isEmpty()) return FALLBACK_NPC_ID;

        ResourceManager rm = Minecraft.getInstance().getResourceManager();
        // Check that the model file exists
        ResourceLocation modelRL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID,
                "geo/entity/npc/" + raw + ".geo.json");
        if (rm.getResource(modelRL).isPresent()) {
            return raw;
        }
        return FALLBACK_NPC_ID;
    }
}
