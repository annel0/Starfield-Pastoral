package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.OssifiedExecutionCircleEffectClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OssifiedExecutionCirclePayload(float x, float y, float z, float radius, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<OssifiedExecutionCirclePayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "ossified_execution_circle")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, OssifiedExecutionCirclePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.FLOAT,
        OssifiedExecutionCirclePayload::x,
        ByteBufCodecs.FLOAT,
        OssifiedExecutionCirclePayload::y,
        ByteBufCodecs.FLOAT,
        OssifiedExecutionCirclePayload::z,
        ByteBufCodecs.FLOAT,
        OssifiedExecutionCirclePayload::radius,
        ByteBufCodecs.VAR_INT,
        OssifiedExecutionCirclePayload::durationTicks,
        OssifiedExecutionCirclePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OssifiedExecutionCirclePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> OssifiedExecutionCircleEffectClient.add(
            payload.x(), payload.y(), payload.z(), payload.radius(), payload.durationTicks()));
    }
}
