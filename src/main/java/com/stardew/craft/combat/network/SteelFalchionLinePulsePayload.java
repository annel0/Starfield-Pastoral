package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.SteelFalchionLineEffectClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SteelFalchionLinePulsePayload(int lineId, int durationTicks)
        implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<SteelFalchionLinePulsePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "steel_falchion_line_pulse")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, SteelFalchionLinePulsePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        SteelFalchionLinePulsePayload::lineId,
        ByteBufCodecs.VAR_INT,
        SteelFalchionLinePulsePayload::durationTicks,
        SteelFalchionLinePulsePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SteelFalchionLinePulsePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> SteelFalchionLineEffectClient.pulse(payload.lineId(), payload.durationTicks()));
    }
}
