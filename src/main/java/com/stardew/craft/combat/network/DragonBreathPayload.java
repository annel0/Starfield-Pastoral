package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.DragonBreathClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record DragonBreathPayload(int stacks) implements CustomPacketPayload {
    @SuppressWarnings("null")
    public static final Type<DragonBreathPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "dragon_breath")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, DragonBreathPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        DragonBreathPayload::stacks,
        DragonBreathPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DragonBreathPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> DragonBreathClientState.setStacks(payload.stacks()));
    }
}
