package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.leaderboard.LeaderboardMetric;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("null")
public record RequestLeaderboardPayload(String metricId, int page) implements CustomPacketPayload {
    public static final Type<RequestLeaderboardPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "request_leaderboard"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestLeaderboardPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public RequestLeaderboardPayload decode(RegistryFriendlyByteBuf buf) {
                    return new RequestLeaderboardPayload(buf.readUtf(), buf.readVarInt());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, RequestLeaderboardPayload payload) {
                    buf.writeUtf(payload.metricId == null ? LeaderboardMetric.MONEY.id() : payload.metricId);
                    buf.writeVarInt(Math.max(0, payload.page));
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestLeaderboardPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            var requestedMetric = LeaderboardMetric.fromId(payload.metricId());
            if (requestedMetric.isEmpty()) {
                LeaderboardSyncPayload.sendErrorToPlayer(player, LeaderboardMetric.MONEY, payload.page(), "stardewcraft.leaderboard.error.invalid_metric");
                return;
            }
            LeaderboardMetric metric = requestedMetric.get();
            try {
                LeaderboardSyncPayload.sendToPlayer(player, metric, payload.page());
            } catch (Exception ex) {
                StardewCraft.LOGGER.warn("Failed to build leaderboard {} for {}: {}", metric.id(), player.getGameProfile().getName(), ex.getMessage());
                LeaderboardSyncPayload.sendErrorToPlayer(player, metric, payload.page(), "stardewcraft.leaderboard.error.server");
            }
        });
    }
}
