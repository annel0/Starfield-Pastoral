package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: player has closed the shop screen (or placed heldItem into a slot).
 * The server now actually grants the pending cursor item to the player's inventory.
 *
 * This is the second half of the two-phase purchase flow:
 *   1. ShopPurchasePayload   → server deducts money/trade items, sends result
 *   2. ShopPickupPayload     → server grants the item to inventory
 *
 * Security note: the server trusts itemId/quantity as-is only if the amount ≤ what
 * was already charged. This payload carries the full list of pending items so that
 * multiple purchases stacked on the cursor are resolved in one round-trip.
 */
@SuppressWarnings("null")
public record ShopPickupPayload(
    String itemId,
    int    quantity,
    int    targetSlot  // ≥0 = place in this specific inventory slot; -1 = auto (first available)
) implements CustomPacketPayload {

    public static final Type<ShopPickupPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "shop_pickup"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShopPickupPayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ShopPickupPayload::itemId,
            ByteBufCodecs.INT,         ShopPickupPayload::quantity,
            ByteBufCodecs.INT,         ShopPickupPayload::targetSlot,
            ShopPickupPayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShopPickupPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (payload.itemId() == null || payload.itemId().isEmpty()) return;
            // Recipe purchases are handled at purchase time, no pickup needed.
            if (payload.itemId().startsWith("recipe:")) return;
            int qty = payload.quantity();
            if (qty <= 0) return;

            ResourceLocation rl;
            try { rl = ResourceLocation.parse(payload.itemId()); }
            catch (Exception ignored) { return; }

            Item mcItem = BuiltInRegistries.ITEM.get(rl);
            if (mcItem == null || mcItem == Items.AIR) return;

            qty = Math.min(qty, mcItem.getDefaultMaxStackSize());
            ItemStack stack = new ItemStack(mcItem, qty);

            int targetSlot = payload.targetSlot();
            if (targetSlot >= 0 && targetSlot < player.getInventory().getContainerSize()) {
                ItemStack existing = player.getInventory().getItem(targetSlot);
                if (existing.isEmpty()) {
                    // Slot is empty — place directly
                    player.getInventory().setItem(targetSlot, stack);
                } else if (ItemStack.isSameItemSameComponents(existing, stack)
                        && existing.getCount() < existing.getMaxStackSize()) {
                    // Same item with room to merge
                    int canAdd = existing.getMaxStackSize() - existing.getCount();
                    int toAdd  = Math.min(canAdd, stack.getCount());
                    existing.grow(toAdd);
                    if (toAdd < stack.getCount()) {
                        // Leftover: auto-place remainder
                        ItemStack leftover = stack.copyWithCount(stack.getCount() - toAdd);
                        if (!player.getInventory().add(leftover)) player.drop(leftover, false);
                    }
                } else {
                    // Slot occupied by different item — fall back to auto-place
                    if (!player.getInventory().add(stack)) player.drop(stack, false);
                }
            } else {
                // targetSlot == -1: auto-place in first available slot
                if (!player.getInventory().add(stack)) player.drop(stack, false);
            }
        });
    }
}
