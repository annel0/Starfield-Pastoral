package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.animal.service.AnimalShopService;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.Villager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = StardewCraft.MODID)
public class AnimalShopInteractionEvents {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!ModDimensions.STARDEW_VALLEY.equals(player.serverLevel().dimension())) {
            return;
        }
        if (!(event.getTarget() instanceof Villager villager)) {
            return;
        }

        String name = villager.getName().getString();
        if (!"Marnie".equalsIgnoreCase(name)) {
            return;
        }

        AnimalShopService.openForPlayer(player);
        event.setCanceled(true);
    }
}
