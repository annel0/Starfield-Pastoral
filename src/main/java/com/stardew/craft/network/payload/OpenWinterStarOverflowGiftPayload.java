package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Vanilla CreateOverflowMenu equivalent for a Winter Star return gift. */
@SuppressWarnings("null")
public record OpenWinterStarOverflowGiftPayload(String itemId, int count) implements CustomPacketPayload {
    public static final Type<OpenWinterStarOverflowGiftPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_winter_star_overflow_gift"));
    public static final StreamCodec<FriendlyByteBuf, OpenWinterStarOverflowGiftPayload> STREAM_CODEC = StreamCodec.of(
        (buf, value) -> { buf.writeUtf(value.itemId(), 256); buf.writeVarInt(value.count()); },
        buf -> new OpenWinterStarOverflowGiftPayload(buf.readUtf(256), buf.readVarInt()));
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    public static void handle(OpenWinterStarOverflowGiftPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(OpenWinterStarOverflowGiftPayload payload) {
        net.minecraft.client.Minecraft.getInstance().setScreen(
            new com.stardew.craft.client.gui.festival.WinterStarOverflowGiftScreen(payload.itemId(), payload.count()));
    }
}
