package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.AccretionDiskEffectClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record AccretionDiskPayload(float x, float y, float z, float radius, int durationTicks, int color)
        implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<AccretionDiskPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "accretion_disk")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, AccretionDiskPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        AccretionDiskPayload::x,
        ByteBufCodecs.FLOAT,
        AccretionDiskPayload::y,
        ByteBufCodecs.FLOAT,
        AccretionDiskPayload::z,
        ByteBufCodecs.FLOAT,
        AccretionDiskPayload::radius,
        ByteBufCodecs.VAR_INT,
        AccretionDiskPayload::durationTicks,
        ByteBufCodecs.INT,
        AccretionDiskPayload::color,
        AccretionDiskPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(AccretionDiskPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> AccretionDiskEffectClient.add(
            payload.x(), payload.y(), payload.z(), payload.radius(), payload.durationTicks(), payload.color()
        ));
    }
}
