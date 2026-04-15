package com.stardew.craft.network;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server → Client: tells the client to show an item-pickup HUD message (SDV parity).
 */
public record ItemPickupHudPacket(String itemId, int count, boolean expensive) implements CustomPacketPayload {
    @SuppressWarnings("null")
    public static final Type<ItemPickupHudPacket> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "item_pickup_hud")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, ItemPickupHudPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8, ItemPickupHudPacket::itemId,
        ByteBufCodecs.INT,         ItemPickupHudPacket::count,
        ByteBufCodecs.BOOL,        ItemPickupHudPacket::expensive,
        ItemPickupHudPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("null")
    public static void handle(ItemPickupHudPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                ResourceLocation rl = ResourceLocation.parse(packet.itemId());
                Item item = BuiltInRegistries.ITEM.get(rl);
                if (item != null && item != Items.AIR) {
                    ItemStack stack = new ItemStack(item, packet.count());
                    com.stardew.craft.client.hud.StardewHudMessageManager.showItemPickup(stack, packet.count(), packet.expensive());
                }
            } catch (Exception ignored) {}
        });
    }

    @SuppressWarnings("null")
    public static void sendTo(ServerPlayer player, ItemStack stack, int count, boolean expensive) {
        ResourceLocation rl = BuiltInRegistries.ITEM.getKey(stack.getItem());
        PacketDistributor.sendToPlayer(player, new ItemPickupHudPacket(rl.toString(), count, expensive));
    }
}
