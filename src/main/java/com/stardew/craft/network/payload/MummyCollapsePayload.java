package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.MummyCollapseClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record MummyCollapsePayload(int entityId, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<MummyCollapsePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "mummy_collapse")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, MummyCollapsePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            MummyCollapsePayload::entityId,
            ByteBufCodecs.VAR_INT,
            MummyCollapsePayload::durationTicks,
            MummyCollapsePayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MummyCollapsePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> MummyCollapseClientState.apply(payload.entityId(), payload.durationTicks()));
    }
}