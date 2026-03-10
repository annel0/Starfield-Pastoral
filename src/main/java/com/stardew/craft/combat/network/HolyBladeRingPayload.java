package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.HolyBladeRingEffectClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HolyBladeRingPayload(float x, float y, float z, float maxRadius, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<HolyBladeRingPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "holy_blade_ring")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, HolyBladeRingPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        HolyBladeRingPayload::x,
        ByteBufCodecs.FLOAT,
        HolyBladeRingPayload::y,
        ByteBufCodecs.FLOAT,
        HolyBladeRingPayload::z,
        ByteBufCodecs.FLOAT,
        HolyBladeRingPayload::maxRadius,
        ByteBufCodecs.VAR_INT,
        HolyBladeRingPayload::durationTicks,
        HolyBladeRingPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(HolyBladeRingPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> HolyBladeRingEffectClient.add(
            payload.x(), payload.y(), payload.z(), payload.maxRadius(), payload.durationTicks()
        ));
    }
}
