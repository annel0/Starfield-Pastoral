package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record OpenWarpWheelPayload() implements CustomPacketPayload {
    public static final Type<OpenWarpWheelPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "open_warp_wheel"));

    public static final StreamCodec<FriendlyByteBuf, OpenWarpWheelPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> {
            },
            buf -> new OpenWarpWheelPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenWarpWheelPayload payload, IPayloadContext context) {
        context.enqueueWork(com.stardew.craft.client.gui.WarpWheelScreen::open);
    }
}