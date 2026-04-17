package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.shop.MarlonService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: player chose an option from Marlon's menu.
 * 0 = Shop, 1 = Gil, 2 = Recovery (item recovery)
 */
@SuppressWarnings("null")
public record MarlonMenuChoicePayload(int choice) implements CustomPacketPayload {

    public static final Type<MarlonMenuChoicePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "marlon_menu_choice"));

    public static final StreamCodec<FriendlyByteBuf, MarlonMenuChoicePayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> buf.writeVarInt(payload.choice()),
        buf -> new MarlonMenuChoicePayload(buf.readVarInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @SuppressWarnings("null")
    public static void handle(MarlonMenuChoicePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            int choice = payload.choice();
            if (choice < 0 || choice > 2) return;
            MarlonService.handleChoice(player, choice);
        });
    }
}
