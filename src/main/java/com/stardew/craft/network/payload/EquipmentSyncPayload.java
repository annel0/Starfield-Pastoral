package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.ClientPlayerDataCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> Client: sync equipped items so client can render them in UI.
 */
@SuppressWarnings("null")
public record EquipmentSyncPayload(String leftRing, String rightRing, String boots) implements CustomPacketPayload {

    public static final Type<EquipmentSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "equipment_sync"));

    public static final StreamCodec<FriendlyByteBuf, EquipmentSyncPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeUtf(payload.leftRing);
                buf.writeUtf(payload.rightRing);
                buf.writeUtf(payload.boots);
            },
            buf -> new EquipmentSyncPayload(buf.readUtf(), buf.readUtf(), buf.readUtf())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(EquipmentSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientPlayerDataCache.setEquippedLeftRing(payload.leftRing);
            ClientPlayerDataCache.setEquippedRightRing(payload.rightRing);
            ClientPlayerDataCache.setEquippedBoots(payload.boots);
        });
    }
}
