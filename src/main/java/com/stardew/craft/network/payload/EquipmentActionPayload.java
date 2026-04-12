package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.equipment.StardewBootsItem;
import com.stardew.craft.item.equipment.StardewRingItem;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Client -> Server: player clicks an equipment slot in the inventory page.
 * slotType: 0=left ring, 1=right ring, 2=boots
 */
@SuppressWarnings("null")
public record EquipmentActionPayload(int slotType) implements CustomPacketPayload {

    public static final int SLOT_LEFT_RING = 0;
    public static final int SLOT_RIGHT_RING = 1;
    public static final int SLOT_BOOTS = 2;

    public static final Type<EquipmentActionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "equipment_action"));

    public static final StreamCodec<FriendlyByteBuf, EquipmentActionPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeVarInt(payload.slotType),
            buf -> new EquipmentActionPayload(buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EquipmentActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;

            PlayerStardewData data = PlayerDataManager.getPlayerData(player);
            ItemStack carried = player.containerMenu.getCarried();

            switch (payload.slotType) {
                case SLOT_LEFT_RING -> handleRingSlot(player, data, carried, true);
                case SLOT_RIGHT_RING -> handleRingSlot(player, data, carried, false);
                case SLOT_BOOTS -> handleBootsSlot(player, data, carried);
            }

            // sync back to client
            PacketDistributor.sendToPlayer(player, new EquipmentSyncPayload(
                    data.getEquippedLeftRing(),
                    data.getEquippedRightRing(),
                    data.getEquippedBoots()
            ));
        });
    }

    private static void handleRingSlot(ServerPlayer player, PlayerStardewData data, ItemStack carried, boolean left) {
        String currentId = left ? data.getEquippedLeftRing() : data.getEquippedRightRing();

        if (carried.isEmpty()) {
            // unequip
            if (!currentId.isEmpty()) {
                ItemStack unequipped = idToStack(currentId);
                player.containerMenu.setCarried(unequipped);
                if (left) data.setEquippedLeftRing("");
                else data.setEquippedRightRing("");
            }
        } else if (carried.getItem() instanceof StardewRingItem) {
            String newId = BuiltInRegistries.ITEM.getKey(carried.getItem()).toString();
            if (!currentId.isEmpty()) {
                // swap
                ItemStack unequipped = idToStack(currentId);
                player.containerMenu.setCarried(unequipped);
            } else {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
            if (left) data.setEquippedLeftRing(newId);
            else data.setEquippedRightRing(newId);
        }
    }

    private static void handleBootsSlot(ServerPlayer player, PlayerStardewData data, ItemStack carried) {
        String currentId = data.getEquippedBoots();

        if (carried.isEmpty()) {
            if (!currentId.isEmpty()) {
                ItemStack unequipped = idToStack(currentId);
                player.containerMenu.setCarried(unequipped);
                data.setEquippedBoots("");
            }
        } else if (carried.getItem() instanceof StardewBootsItem) {
            String newId = BuiltInRegistries.ITEM.getKey(carried.getItem()).toString();
            if (!currentId.isEmpty()) {
                ItemStack unequipped = idToStack(currentId);
                player.containerMenu.setCarried(unequipped);
            } else {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
            data.setEquippedBoots(newId);
        }
    }

    private static ItemStack idToStack(String id) {
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) return ItemStack.EMPTY;
        Item item = BuiltInRegistries.ITEM.get(rl);
        return new ItemStack(item);
    }
}
