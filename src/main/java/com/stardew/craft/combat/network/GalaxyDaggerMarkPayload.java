package com.stardew.craft.combat.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.weapon.GalaxyDaggerMarkClientState;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record GalaxyDaggerMarkPayload(int entityId, int durationTicks) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final Type<GalaxyDaggerMarkPayload> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "galaxy_dagger_mark")
    );

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, GalaxyDaggerMarkPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        GalaxyDaggerMarkPayload::entityId,
        ByteBufCodecs.VAR_INT,
        GalaxyDaggerMarkPayload::durationTicks,
        GalaxyDaggerMarkPayload::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GalaxyDaggerMarkPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> GalaxyDaggerMarkClientState.apply(payload.entityId(), payload.durationTicks()));
    }
}
