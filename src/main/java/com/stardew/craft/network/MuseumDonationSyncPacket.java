package com.stardew.craft.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.ClientMuseumDonationCache;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Museum donation sync - server -> client.
 */
@SuppressWarnings("null")
public record MuseumDonationSyncPacket(List<String> donatedIds) implements CustomPacketPayload {
    public MuseumDonationSyncPacket {
        if (donatedIds == null) {
            donatedIds = List.of();
        }
    }
    public static final CustomPacketPayload.Type<MuseumDonationSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "museum_donations_sync"));

    public static final StreamCodec<ByteBuf, MuseumDonationSyncPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MuseumDonationSyncPacket decode(ByteBuf buf) {
            int size = ByteBufCodecs.VAR_INT.decode(buf);
            List<String> ids = new ArrayList<>(Math.max(0, size));
            for (int i = 0; i < size; i++) {
                ids.add(ByteBufCodecs.STRING_UTF8.decode(buf));
            }
            return new MuseumDonationSyncPacket(ids);
        }

        @Override
        public void encode(ByteBuf buf, MuseumDonationSyncPacket value) {
            List<String> ids = value.donatedIds();
            ByteBufCodecs.VAR_INT.encode(buf, ids.size());
            for (String id : ids) {
                ByteBufCodecs.STRING_UTF8.encode(buf, id);
            }
        }
    };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(MuseumDonationSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientMuseumDonationCache.setDonated(packet.donatedIds()));
    }
}