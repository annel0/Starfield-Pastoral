package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record CraftingMenuInventoryActionPayload(int action, int slotIndex, boolean rightClick) implements CustomPacketPayload {

    public static final int ACTION_CLICK_SLOT = 0;
    public static final int ACTION_TRASH_CARRIED = 1;
    public static final int ACTION_DROP_CARRIED = 2;

    @SuppressWarnings("null")
    public static final Type<CraftingMenuInventoryActionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "crafting_menu_inventory_action"));

    @SuppressWarnings("null")
    public static final StreamCodec<FriendlyByteBuf, CraftingMenuInventoryActionPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeVarInt(payload.action);
                buf.writeVarInt(payload.slotIndex);
                buf.writeBoolean(payload.rightClick);
            },
            buf -> new CraftingMenuInventoryActionPayload(buf.readVarInt(), buf.readVarInt(), buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("null")
    public static void handle(CraftingMenuInventoryActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            switch (payload.action) {
                case ACTION_CLICK_SLOT -> handleClickSlot(player, payload.slotIndex, payload.rightClick);
                case ACTION_TRASH_CARRIED -> trashCarried(player);
                case ACTION_DROP_CARRIED -> dropCarried(player);
                default -> {
                }
            }
        });
    }

    private static void handleClickSlot(ServerPlayer player, int slotIndex, boolean rightClick) {
        Inventory inv = player.getInventory();
        if (slotIndex < 0 || slotIndex >= inv.items.size()) {
            return;
        }

        ItemStack slotStack = inv.items.get(slotIndex);
        ItemStack carried = player.containerMenu.getCarried();

        if (!rightClick) {
            if (carried.isEmpty()) {
                if (slotStack.isEmpty()) {
                    return;
                }
                player.containerMenu.setCarried(slotStack.copy());
                inv.setItem(slotIndex, ItemStack.EMPTY);
            } else {
                if (slotStack.isEmpty()) {
                    inv.setItem(slotIndex, carried.copy());
                    player.containerMenu.setCarried(ItemStack.EMPTY);
                } else if (ItemStack.isSameItemSameComponents(slotStack, carried)) {
                    int max = Math.min(slotStack.getMaxStackSize(), inv.getMaxStackSize());
                    int space = Math.max(0, max - slotStack.getCount());
                    if (space > 0) {
                        int move = Math.min(space, carried.getCount());
                        slotStack.grow(move);
                        carried.shrink(move);
                        if (carried.isEmpty()) {
                            player.containerMenu.setCarried(ItemStack.EMPTY);
                        } else {
                            player.containerMenu.setCarried(carried);
                        }
                    } else {
                        inv.setItem(slotIndex, carried.copy());
                        player.containerMenu.setCarried(slotStack.copy());
                    }
                } else {
                    inv.setItem(slotIndex, carried.copy());
                    player.containerMenu.setCarried(slotStack.copy());
                }
            }
        } else {
            if (carried.isEmpty()) {
                if (slotStack.isEmpty()) {
                    return;
                }
                int take = (slotStack.getCount() + 1) / 2;
                ItemStack picked = slotStack.copyWithCount(take);
                slotStack.shrink(take);
                inv.setItem(slotIndex, slotStack.isEmpty() ? ItemStack.EMPTY : slotStack);
                player.containerMenu.setCarried(picked);
            } else {
                if (slotStack.isEmpty()) {
                    inv.setItem(slotIndex, carried.copyWithCount(1));
                    carried.shrink(1);
                    player.containerMenu.setCarried(carried.isEmpty() ? ItemStack.EMPTY : carried);
                } else if (ItemStack.isSameItemSameComponents(slotStack, carried) && slotStack.getCount() < slotStack.getMaxStackSize()) {
                    slotStack.grow(1);
                    carried.shrink(1);
                    player.containerMenu.setCarried(carried.isEmpty() ? ItemStack.EMPTY : carried);
                } else {
                    inv.setItem(slotIndex, carried.copy());
                    player.containerMenu.setCarried(slotStack.copy());
                }
            }
        }

        inv.setChanged();
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
    }

    private static void trashCarried(ServerPlayer player) {
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.isEmpty()) {
            return;
        }
        player.containerMenu.setCarried(ItemStack.EMPTY);
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
    }

    private static void dropCarried(ServerPlayer player) {
        ItemStack carried = player.containerMenu.getCarried();
        if (carried.isEmpty()) {
            return;
        }
        player.drop(carried.copy(), false);
        player.containerMenu.setCarried(ItemStack.EMPTY);
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
    }
}
