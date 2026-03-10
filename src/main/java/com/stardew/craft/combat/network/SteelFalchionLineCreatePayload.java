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

public record SteelFalchionLineCreatePayload(int lineId, float x, float y, float z, int durationTicks, float width)
        implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<SteelFalchionLineCreatePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "steel_falchion_line_create")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, SteelFalchionLineCreatePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        SteelFalchionLineCreatePayload::lineId,
        ByteBufCodecs.FLOAT,
        SteelFalchionLineCreatePayload::x,
        ByteBufCodecs.FLOAT,
        SteelFalchionLineCreatePayload::y,
        ByteBufCodecs.FLOAT,
        SteelFalchionLineCreatePayload::z,
        ByteBufCodecs.VAR_INT,
        SteelFalchionLineCreatePayload::durationTicks,
        ByteBufCodecs.FLOAT,
        SteelFalchionLineCreatePayload::width,
        SteelFalchionLineCreatePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SteelFalchionLineCreatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> SteelFalchionLineEffectClient.create(
            payload.lineId(), payload.x(), payload.y(), payload.z(), payload.durationTicks(), payload.width()
        ));
    }
}
