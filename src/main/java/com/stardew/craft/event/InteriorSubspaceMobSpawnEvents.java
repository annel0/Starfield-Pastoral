package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.interior.InteriorSubspaceManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public final class InteriorSubspaceMobSpawnEvents {

    private InteriorSubspaceMobSpawnEvents() {
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!(event.getEntity() instanceof Mob mob)) {
            return;
        }
        if (!InteriorSubspaceManager.isInteriorRegion(level, mob.blockPosition())) {
            return;
        }

        event.setCanceled(true);
        if (mob.isAlive()) {
            mob.discard();
        }
    }
}
