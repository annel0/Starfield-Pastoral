package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record IceFishingHudStatePayload(boolean active, int remainingMs, int fishCaught)
    implements CustomPacketPayload {
    public static final Type<IceFishingHudStatePayload> TYPE =
        new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "ice_fishing_hud_state"));

    public static final StreamCodec<ByteBuf, IceFishingHudStatePayload> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.BOOL,
            IceFishingHudStatePayload::active,
            ByteBufCodecs.VAR_INT,
            IceFishingHudStatePayload::remainingMs,
            ByteBufCodecs.VAR_INT,
            IceFishingHudStatePayload::fishCaught,
            IceFishingHudStatePayload::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(IceFishingHudStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @net.neoforged.api.distmarker.OnlyIn(net.neoforged.api.distmarker.Dist.CLIENT)
    private static void handleClient(IceFishingHudStatePayload payload) {
        com.stardew.craft.client.hud.StardewTimeHud.setIceFishingHudState(
            payload.active(),
            payload.remainingMs(),
            payload.fishCaught()
        );
    }
}
