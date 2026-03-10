package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.InfinityDaggerMarkClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record InfinityDaggerMarkPayload(int entityId, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<InfinityDaggerMarkPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "infinity_dagger_mark")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, InfinityDaggerMarkPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        InfinityDaggerMarkPayload::entityId,
        ByteBufCodecs.VAR_INT,
        InfinityDaggerMarkPayload::durationTicks,
        InfinityDaggerMarkPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(InfinityDaggerMarkPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> InfinityDaggerMarkClientState.apply(payload.entityId(), payload.durationTicks()));
    }
}
