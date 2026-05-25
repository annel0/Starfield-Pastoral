package com.stardew.craft.network.payload;

import net.minecraft.network.FriendlyByteBuf;

public record PrizeTicketRewardPreview(String itemId, int count, int prizeLevel) {
    public static void write(FriendlyByteBuf buf, PrizeTicketRewardPreview preview) {
        buf.writeUtf(preview.itemId(), 128);
        buf.writeVarInt(preview.count());
        buf.writeVarInt(preview.prizeLevel());
    }

    public static PrizeTicketRewardPreview read(FriendlyByteBuf buf) {
        return new PrizeTicketRewardPreview(buf.readUtf(128), buf.readVarInt(), buf.readVarInt());
    }
}