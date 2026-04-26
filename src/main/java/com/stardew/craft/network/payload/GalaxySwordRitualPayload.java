package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GalaxySwordRitualPayload(int durationTicks) implements CustomPacketPayload {

    public static final Type<GalaxySwordRitualPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "galaxy_sword_ritual")
    );

    public static final StreamCodec<ByteBuf, GalaxySwordRitualPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            GalaxySwordRitualPayload::durationTicks,
            GalaxySwordRitualPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GalaxySwordRitualPayload payload, IPayloadContext context) {
        context.enqueueWork(() ->
                com.stardew.craft.client.ritual.GalaxySwordRitualClientState.start(payload.durationTicks())
        );
    }
}