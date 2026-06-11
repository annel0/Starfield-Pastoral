package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.npc.runtime.LewisCivicService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record LewisCivicActionPayload(int choice) implements CustomPacketPayload {
    public static final Type<LewisCivicActionPayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "lewis_civic_action"));

    public static final StreamCodec<FriendlyByteBuf, LewisCivicActionPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> buf.writeVarInt(payload.choice()),
        buf -> new LewisCivicActionPayload(buf.readVarInt()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LewisCivicActionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                LewisCivicService.handleMenuChoice(player, payload.choice());
            }
        });
    }
}
