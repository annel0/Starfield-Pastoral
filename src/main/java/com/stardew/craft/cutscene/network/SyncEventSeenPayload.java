package com.stardew.craft.cutscene.network;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.List;

/**
 * Server → Client: full sync of which events the player has seen.
 * Sent on login and after each MarkEventSeen confirmation.
 */
public record SyncEventSeenPayload(List<String> eventIds) implements CustomPacketPayload {

    public static final Type<SyncEventSeenPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "sync_event_seen"));

    public static final StreamCodec<ByteBuf, SyncEventSeenPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), SyncEventSeenPayload::eventIds,
            SyncEventSeenPayload::new);

    @SuppressWarnings("null")
    public static void handle(SyncEventSeenPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientEventSeenCache.replace(new HashSet<>(payload.eventIds));
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
