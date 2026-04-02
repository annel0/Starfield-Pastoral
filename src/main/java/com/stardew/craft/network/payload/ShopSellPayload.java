package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: player wants to sell the item at inventorySlot.
 *
 * The server validates the item, calculates sell price, removes it from
 * the inventory, and sends back a ShopSellResultPayload.
 */
@SuppressWarnings("null")
public record ShopSellPayload(
    String shopId,
    int    inventorySlot,  // 0-35 in MC (0=hotbar[0] … 8=hotbar[8], 9-35=main)
    int    quantity        // -1 = sell whole stack; otherwise partial
) implements CustomPacketPayload {

    public static final Type<ShopSellPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "shop_sell"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopSellPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ShopSellPayload::shopId,
            ByteBufCodecs.INT,         ShopSellPayload::inventorySlot,
            ByteBufCodecs.INT,         ShopSellPayload::quantity,
            ShopSellPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShopSellPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof net.minecraft.server.level.ServerPlayer player)) return;

            com.stardew.craft.shop.ShopRegistry.ShopDefinition shop =
                com.stardew.craft.shop.ShopRegistry.get(payload.shopId());
            if (shop == null) return;

            // Resolve slot
            int slot = payload.inventorySlot();
            if (slot < 0 || slot >= player.getInventory().getContainerSize()) return;
            net.minecraft.world.item.ItemStack stack = player.getInventory().getItem(slot);
            if (stack.isEmpty()) return;

            // Check if this shop accepts this item and compute sell price
            // (mirrors SDV: highlightItemToSell → canSell, sellToStorePrice × sellPercentage)
            int sellUnit = com.stardew.craft.shop.ShopRegistry.getSellPrice(stack, shop);
            if (sellUnit <= 0) return; // shop does not buy this item

            int qty = (payload.quantity() < 0) ? stack.getCount() : Math.min(payload.quantity(), stack.getCount());
            int earned = sellUnit * qty;

            // Remove items from inventory
            stack.shrink(qty);
            if (stack.isEmpty()) player.getInventory().setItem(slot, net.minecraft.world.item.ItemStack.EMPTY);

            // Credit money (SDV: chargePlayer(currency, -price) → negative = add money)
            com.stardew.craft.player.PlayerStardewDataAPI.addMoney(player, earned);
            int newMoney = com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player);

            // Notify client
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
                new ShopSellResultPayload(true, newMoney, slot, qty, earned));
        });
    }
}
