package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.shop.ShopItemEntry;
import com.stardew.craft.shop.ShopRegistry;
import com.stardew.craft.shop.ShopStockTracker;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

/**
 * Client → Server: player wants to buy qty units of item at index itemIndex
 * from shop shopId.
 */
@SuppressWarnings("null")
public record ShopPurchasePayload(
    String shopId,
    int itemIndex,
    int quantity
) implements CustomPacketPayload {

    public static final Type<ShopPurchasePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "shop_purchase"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopPurchasePayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ShopPurchasePayload::shopId,
            ByteBufCodecs.INT,         ShopPurchasePayload::itemIndex,
            ByteBufCodecs.INT,         ShopPurchasePayload::quantity,
            ShopPurchasePayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShopPurchasePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            // Special handling for Clint's tool upgrade shop (dynamic, per-player)
            if (payload.shopId().equals("ClintUpgrade")) {
                com.stardew.craft.shop.BlacksmithService.handleToolUpgradePurchaseFromShop(
                    player, payload.itemIndex(), payload.quantity());
                return;
            }

            // Special handling for Furniture Catalogue (dynamic item list, all free)
            if (payload.shopId().equals(com.stardew.craft.block.decor.FurnitureCatalogueBlock.SHOP_ID)) {
                handleFurnitureCataloguePurchase(player, payload.itemIndex(), payload.quantity());
                return;
            }

            // Special handling for Marlon's item recovery shop (SDV parity)
            // SDV: player buys ONE item → that item is returned → ALL lost items cleared
            if (payload.shopId().equals("MarlonRecovery")) {
                com.stardew.craft.shop.MarlonService.handleRecoveryPurchaseFromShop(
                    player, payload.itemIndex());
                return;
            }

            ShopRegistry.ShopDefinition shop = ShopRegistry.get(payload.shopId());
            if (shop == null) return;

            // Rebuild the EXACT same filtered list that was sent to this client when the shop opened.
            // This ensures itemIndex maps to the correct item (recipes/sold-out items are excluded).
            List<ShopItemEntry> items = ShopRegistry.getFilteredItemsForPlayer(
                payload.shopId(), shop, player);
            if (payload.itemIndex() < 0 || payload.itemIndex() >= items.size()) return;

            ShopItemEntry entry = items.get(payload.itemIndex());
            int qty  = Math.max(1, payload.quantity());

            // Clamp to available stock (also checks per-player daily stock via tracker)
            if (entry.stock() != Integer.MAX_VALUE) {
                int remaining = ShopStockTracker.getRemaining(
                    player.getUUID(), payload.shopId(), entry.itemId(), entry.stock());
                qty = Math.min(qty, remaining);
                if (qty <= 0) {
                    sendResult(player, false, 0, "", 0, payload.itemIndex());
                    return;
                }
            }

            int cost = entry.price() * qty;

            // Recipe purchases: check if already learned BEFORE deducting money
            if (entry.itemId().startsWith("recipe:")) {
                String recipeId = com.stardew.craft.shop.SaloonService.extractRecipeId(entry.itemId());
                com.stardew.craft.player.PlayerStardewData data =
                    com.stardew.craft.player.PlayerDataManager.getPlayerData(player);
                if (data.isRecipeUnlocked(recipeId)) {
                    // Already learned — reject without charging
                    sendResult(player, false, com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player), "", 0, payload.itemIndex());
                    return;
                }
                // Deduct money
                if (cost > 0) {
                    boolean ok = com.stardew.craft.player.PlayerStardewDataAPI.removeMoney(player, cost);
                    if (!ok) {
                        sendResult(player, false, com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player), "", 0, payload.itemIndex());
                        return;
                    }
                }
                // Unlock the recipe
                data.unlockRecipe(recipeId);
                com.stardew.craft.player.PlayerDataEventHandler.syncPlayerData(player, data);
                int newMoneyRecipe = com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player);
                if (entry.stock() != Integer.MAX_VALUE) {
                    ShopStockTracker.recordPurchase(player.getUUID(), payload.shopId(), entry.itemId(), qty);
                }
                sendResult(player, true, newMoneyRecipe, entry.itemId(), qty, payload.itemIndex());
                return;
            }

            // Trade-item requirement (SDV stock.TradeItem)
            if (entry.requiresTrade()) {
                ResourceLocation tradeId;
                try {
                    tradeId = ResourceLocation.parse(entry.tradeItemId());
                } catch (Exception ignored) {
                    sendResult(player, false, com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player), "", 0, payload.itemIndex());
                    return;
                }

                net.minecraft.world.item.Item tradeItem =
                    net.minecraft.core.registries.BuiltInRegistries.ITEM.get(tradeId);
                if (tradeItem == null || tradeItem == net.minecraft.world.item.Items.AIR) {
                    sendResult(player, false, com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player), "", 0, payload.itemIndex());
                    return;
                }

                int tradeNeed = Math.max(1, entry.tradeItemCount()) * qty;
                if (player.getInventory().countItem(tradeItem) < tradeNeed) {
                    sendResult(player, false, com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player), "", 0, payload.itemIndex());
                    return;
                }
            }

            // Server-side money check and deduct
            if (cost > 0) {
                boolean ok = com.stardew.craft.player.PlayerStardewDataAPI.removeMoney(player, cost);
                if (!ok) {
                    int currentMoney = com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player);
                    sendResult(player, false, currentMoney, "", 0, payload.itemIndex());
                    return;
                }
            }

            // Consume trade items after payment, before granting purchase.
            if (entry.requiresTrade()) {
                ResourceLocation tradeId = ResourceLocation.parse(entry.tradeItemId());
                net.minecraft.world.item.Item tradeItem =
                    net.minecraft.core.registries.BuiltInRegistries.ITEM.get(tradeId);
                int tradeNeed = Math.max(1, entry.tradeItemCount()) * qty;
                int remaining = tradeNeed;
                for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++) {
                    ItemStack slot = player.getInventory().getItem(i);
                    if (!slot.isEmpty() && slot.is(tradeItem)) {
                        int take = Math.min(remaining, slot.getCount());
                        slot.shrink(take);
                        remaining -= take;
                    }
                }
                if (remaining > 0) {
                    // Safety rollback: refund money if trade consumption failed unexpectedly.
                    if (cost > 0) com.stardew.craft.player.PlayerStardewDataAPI.addMoney(player, cost);
                    sendResult(player, false, com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player), "", 0, payload.itemIndex());
                    return;
                }
            }

            // Validate item exists before confirming the purchase.
            // The item is NOT added to inventory here; the client will hold it on the
            // cursor (SDV heldItem) and send ShopPickupPayload when the screen closes
            // or the player places it into a slot.
            try {
                ResourceLocation rl = ResourceLocation.parse(entry.itemId());
                net.minecraft.world.item.Item mcItem =
                    net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl);
                if (mcItem == null || mcItem == net.minecraft.world.item.Items.AIR) {
                    // Refund if item doesn't exist
                    if (cost > 0) com.stardew.craft.player.PlayerStardewDataAPI.addMoney(player, cost);
                    sendResult(player, false, com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player), "", 0, payload.itemIndex());
                    return;
                }
                // Item is valid – money/trade already deducted above, send success.
                // Actual item delivery happens when client sends ShopPickupPayload.
            } catch (Exception e) {
                // Refund on error
                if (cost > 0) com.stardew.craft.player.PlayerStardewDataAPI.addMoney(player, cost);
                sendResult(player, false, com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player), "", 0, payload.itemIndex());
                return;
            }

            int newMoney = com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player);

            // Record the purchase so remaining daily stock is tracked (SDV: SynchronizedShopStock parity)
            if (entry.stock() != Integer.MAX_VALUE) {
                ShopStockTracker.recordPurchase(player.getUUID(), payload.shopId(), entry.itemId(), qty);
            }

            sendResult(player, true, newMoney, entry.itemId(), qty, payload.itemIndex());
        });
    }

    private static void sendResult(ServerPlayer player, boolean ok, int money, String itemId, int qty, int idx) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player,
            new com.stardew.craft.network.payload.ShopPurchaseResultPayload(ok, money, itemId, qty, idx));
    }

    /**
     * Handles purchases from the Furniture Catalogue.
     * All items are free and unlimited — just validate the index and send success.
     */
    private static void handleFurnitureCataloguePurchase(ServerPlayer player, int itemIndex, int quantity) {
        List<ShopItemEntry> items = com.stardew.craft.block.decor.FurnitureCatalogueBlock.buildCatalogueItems();
        if (itemIndex < 0 || itemIndex >= items.size()) return;

        ShopItemEntry entry = items.get(itemIndex);
        int qty = Math.max(1, quantity);
        int money = com.stardew.craft.player.PlayerStardewDataAPI.getMoney(player);

        // Validate the item exists in MC registry
        try {
            net.minecraft.resources.ResourceLocation rl = net.minecraft.resources.ResourceLocation.parse(entry.itemId());
            net.minecraft.world.item.Item mcItem =
                    net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl);
            if (mcItem == null || mcItem == net.minecraft.world.item.Items.AIR) {
                sendResult(player, false, money, "", 0, itemIndex);
                return;
            }
        } catch (Exception e) {
            sendResult(player, false, money, "", 0, itemIndex);
            return;
        }

        // Free purchase — no money deduction, no stock tracking
        sendResult(player, true, money, entry.itemId(), qty, itemIndex);
    }
}
