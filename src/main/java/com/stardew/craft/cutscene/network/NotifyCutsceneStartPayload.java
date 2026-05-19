package com.stardew.craft.cutscene.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.cutscene.server.ServerCutsceneTracker;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client → Server: notify the server that the client began playing an event locally
 * (e.g. enter_area triggers fired purely client-side).
 *
 * Server uses this to mark the player as cutscene-active so interaction lock applies.
 */
public record NotifyCutsceneStartPayload(String eventId) implements CustomPacketPayload {

    public static final Type<NotifyCutsceneStartPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "notify_cutscene_start"));

    public static final StreamCodec<ByteBuf, NotifyCutsceneStartPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, NotifyCutsceneStartPayload::eventId,
            NotifyCutsceneStartPayload::new);

    @SuppressWarnings("null")
    public static void handle(NotifyCutsceneStartPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) context.player();
            ServerCutsceneTracker.markActive(player);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
