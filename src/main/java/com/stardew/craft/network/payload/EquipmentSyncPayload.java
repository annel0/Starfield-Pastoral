package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client: sync equipped items so client can render them in UI.
 */
@SuppressWarnings("null")
public record EquipmentSyncPayload(String leftRing, String rightRing, String boots, ItemStack trinket,
                                   String hat, String shirt, String pants) implements CustomPacketPayload {

    public static final Type<EquipmentSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "equipment_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeUtf(payload.leftRing);
                buf.writeUtf(payload.rightRing);
                buf.writeUtf(payload.boots);
                ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, payload.trinket);
                buf.writeUtf(payload.hat);
                buf.writeUtf(payload.shirt);
                buf.writeUtf(payload.pants);
            },
            buf -> new EquipmentSyncPayload(buf.readUtf(), buf.readUtf(), buf.readUtf(),
                    ItemStack.OPTIONAL_STREAM_CODEC.decode(buf), buf.readUtf(), buf.readUtf(), buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EquipmentSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            com.stardew.craft.client.ClientPlayerDataCache.setEquippedLeftRing(payload.leftRing);
            com.stardew.craft.client.ClientPlayerDataCache.setEquippedRightRing(payload.rightRing);
            com.stardew.craft.client.ClientPlayerDataCache.setEquippedBoots(payload.boots);
            com.stardew.craft.client.ClientPlayerDataCache.setEquippedTrinket(payload.trinket);
            com.stardew.craft.client.ClientPlayerDataCache.setEquippedHat(payload.hat);
            com.stardew.craft.client.ClientPlayerDataCache.setEquippedShirt(payload.shirt);
            com.stardew.craft.client.ClientPlayerDataCache.setEquippedPants(payload.pants);
            if (context.player() != null) {
                com.stardew.craft.client.ClientPlayerDataCache.setCosmeticAppearance(
                        context.player().getUUID(), payload.hat, payload.shirt, payload.pants);
            }
        });
    }
}
