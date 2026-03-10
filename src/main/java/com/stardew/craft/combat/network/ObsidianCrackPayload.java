package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.ObsidianCrackEffectClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record ObsidianCrackPayload(float x, float y, float z, float yaw, float length, int durationTicks)
        implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<ObsidianCrackPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "obsidian_crack")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, ObsidianCrackPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        ObsidianCrackPayload::x,
        ByteBufCodecs.FLOAT,
        ObsidianCrackPayload::y,
        ByteBufCodecs.FLOAT,
        ObsidianCrackPayload::z,
        ByteBufCodecs.FLOAT,
        ObsidianCrackPayload::yaw,
        ByteBufCodecs.FLOAT,
        ObsidianCrackPayload::length,
        ByteBufCodecs.VAR_INT,
        ObsidianCrackPayload::durationTicks,
        ObsidianCrackPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ObsidianCrackPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> ObsidianCrackEffectClient.add(
            payload.x(), payload.y(), payload.z(), payload.yaw(), payload.length(), payload.durationTicks()
        ));
    }
}