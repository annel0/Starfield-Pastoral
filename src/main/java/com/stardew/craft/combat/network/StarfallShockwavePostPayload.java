package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record StarfallShockwavePostPayload(float x, float y, float z, float radiusNorm, float strength, int durationTicks)
        implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<StarfallShockwavePostPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "starfall_shockwave_post")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, StarfallShockwavePostPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        StarfallShockwavePostPayload::x,
        ByteBufCodecs.FLOAT,
        StarfallShockwavePostPayload::y,
        ByteBufCodecs.FLOAT,
        StarfallShockwavePostPayload::z,
        ByteBufCodecs.FLOAT,
        StarfallShockwavePostPayload::radiusNorm,
        ByteBufCodecs.FLOAT,
        StarfallShockwavePostPayload::strength,
        ByteBufCodecs.VAR_INT,
        StarfallShockwavePostPayload::durationTicks,
        StarfallShockwavePostPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(StarfallShockwavePostPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            com.stardew.craft.client.weapon.StarfallShockwavePostEffectClient.add(
                payload.x(), payload.y(), payload.z(), payload.radiusNorm(), payload.strength(), payload.durationTicks()
            );
            com.stardew.craft.client.weapon.CameraShakeState.kick(0.7f, 6, 4.0f);
        });
    }
}
