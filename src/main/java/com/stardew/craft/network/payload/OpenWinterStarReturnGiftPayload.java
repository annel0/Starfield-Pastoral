package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Supplies per-player giver/item variables before the shared return-gift cutscene starts. */
@SuppressWarnings("null")
public record OpenWinterStarReturnGiftPayload(String giverId, String beforeKey, String afterKey,
                                               String itemId, int count) implements CustomPacketPayload {
    public static final Type<OpenWinterStarReturnGiftPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_winter_star_return_gift"));
    public static final StreamCodec<FriendlyByteBuf, OpenWinterStarReturnGiftPayload> STREAM_CODEC = StreamCodec.of(
        (buf, value) -> {
            buf.writeUtf(value.giverId(), 64); buf.writeUtf(value.beforeKey(), 256);
            buf.writeUtf(value.afterKey(), 256); buf.writeUtf(value.itemId(), 256); buf.writeVarInt(value.count());
        },
        buf -> new OpenWinterStarReturnGiftPayload(buf.readUtf(64), buf.readUtf(256), buf.readUtf(256), buf.readUtf(256), buf.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(OpenWinterStarReturnGiftPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            com.stardew.craft.client.festival.WinterStarCutsceneContext.configure(
                payload.giverId(), payload.beforeKey(), payload.afterKey(), payload.itemId());
        });
    }
}
