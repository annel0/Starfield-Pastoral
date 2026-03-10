package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.SingularityClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SingularityPayload(int stacks) implements CustomPacketPayload {
    @SuppressWarnings("null")
    public static final Type<SingularityPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "singularity")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, SingularityPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        SingularityPayload::stacks,
        SingularityPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SingularityPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> SingularityClientState.setStacks(payload.stacks()));
    }
}
