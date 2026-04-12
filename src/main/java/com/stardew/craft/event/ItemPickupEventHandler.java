package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.item.IStardewItem;
import com.stardew.craft.network.ItemPickupHudPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

/**
 * Sends an item-pickup HUD notification (SDV parity) when a player picks up
 * an item entity in a stardew dimension. Creative/command pickups are excluded
 * because they bypass the item entity system entirely.
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
@SuppressWarnings("null")
public class ItemPickupEventHandler {

    private static final int EXPENSIVE_THRESHOLD = 500; // SDV: sellToStorePrice > 500

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Post event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        // Only in stardew dimensions
        var dim = player.level().dimension();
        if (!ModDimensions.STARDEW_VALLEY.equals(dim) && !ModMiningDimensions.STARDEW_MINING.equals(dim)) {
            return;
        }

        ItemStack original = event.getOriginalStack();
        if (original.isEmpty()) return;

        int count = original.getCount();
        boolean expensive = false;
        if (original.getItem() instanceof IStardewItem si) {
            int sellPrice = si.getSellPrice(original);
            expensive = sellPrice > EXPENSIVE_THRESHOLD;
        }

        ItemPickupHudPacket.sendTo(player, original, count, expensive);

        // Quest: item received
        String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(original.getItem()).toString();
        com.stardew.craft.quest.StardewQuestEvents.fireItemReceived(player, itemId, count);
    }
}
