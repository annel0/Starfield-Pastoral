package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.SingularityRuneEffectClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SingularityRunePayload(float x, float y, float z, float radius, int durationTicks, int color)
        implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<SingularityRunePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "singularity_rune")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, SingularityRunePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        SingularityRunePayload::x,
        ByteBufCodecs.FLOAT,
        SingularityRunePayload::y,
        ByteBufCodecs.FLOAT,
        SingularityRunePayload::z,
        ByteBufCodecs.FLOAT,
        SingularityRunePayload::radius,
        ByteBufCodecs.VAR_INT,
        SingularityRunePayload::durationTicks,
        ByteBufCodecs.INT,
        SingularityRunePayload::color,
        SingularityRunePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SingularityRunePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> SingularityRuneEffectClient.add(
            payload.x(), payload.y(), payload.z(), payload.radius(), payload.durationTicks(), payload.color()
        ));
    }
}
