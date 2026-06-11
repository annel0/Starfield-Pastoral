package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record OpenAuctionCreatePayload(int currentDay, int currentMinute, int occupiedDayMask) implements CustomPacketPayload {
    public static final Type<OpenAuctionCreatePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_auction_create"));

    public static final StreamCodec<FriendlyByteBuf, OpenAuctionCreatePayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeVarInt(payload.currentDay());
            buf.writeVarInt(payload.currentMinute());
            buf.writeVarInt(payload.occupiedDayMask());
        },
        buf -> new OpenAuctionCreatePayload(buf.readVarInt(), buf.readVarInt(), buf.readVarInt()));

    /** True when {@code dayOffset} (1..14) is already taken by an existing auction. */
    public boolean isDayOccupied(int dayOffset) {
        return dayOffset >= 1 && dayOffset <= 14 && (occupiedDayMask & (1 << (dayOffset - 1))) != 0;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenAuctionCreatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(OpenAuctionCreatePayload payload) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {
            mc.setScreen(new com.stardew.craft.client.gui.auction.AuctionCreateScreen(
                payload.currentDay(), payload.currentMinute(), payload.occupiedDayMask()));
        }
    }
}
