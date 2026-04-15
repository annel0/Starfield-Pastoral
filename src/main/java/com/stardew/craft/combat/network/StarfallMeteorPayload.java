package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record StarfallMeteorPayload(float x, float y, float z, float height, int durationTicks, int color)
        implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<StarfallMeteorPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "starfall_meteor")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, StarfallMeteorPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        StarfallMeteorPayload::x,
        ByteBufCodecs.FLOAT,
        StarfallMeteorPayload::y,
        ByteBufCodecs.FLOAT,
        StarfallMeteorPayload::z,
        ByteBufCodecs.FLOAT,
        StarfallMeteorPayload::height,
        ByteBufCodecs.VAR_INT,
        StarfallMeteorPayload::durationTicks,
        ByteBufCodecs.INT,
        StarfallMeteorPayload::color,
        StarfallMeteorPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StarfallMeteorPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> com.stardew.craft.client.weapon.StarfallMeteorEffectClient.add(
            payload.x(), payload.y(), payload.z(), payload.height(), payload.durationTicks(), payload.color()
        ));
    }
}
