package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.IridiumNeedleCritClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record IridiumNeedleCritPayload(int stacks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<IridiumNeedleCritPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "iridium_needle_crit")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, IridiumNeedleCritPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        IridiumNeedleCritPayload::stacks,
        IridiumNeedleCritPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(IridiumNeedleCritPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return;
            }
            if (payload.stacks() > 0) {
                IridiumNeedleCritClientState.setStacks(payload.stacks());
            } else {
                IridiumNeedleCritClientState.clear();
            }
        });
    }
}
