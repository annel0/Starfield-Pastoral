package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.OssifiedMarkClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OssifiedMarkPayload(int entityId, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<OssifiedMarkPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "ossified_mark")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, OssifiedMarkPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        OssifiedMarkPayload::entityId,
        ByteBufCodecs.VAR_INT,
        OssifiedMarkPayload::durationTicks,
        OssifiedMarkPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OssifiedMarkPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> OssifiedMarkClientState.apply(payload.entityId(), payload.durationTicks()));
    }
}
