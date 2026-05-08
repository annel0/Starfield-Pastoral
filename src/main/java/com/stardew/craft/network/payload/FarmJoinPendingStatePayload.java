package com.stardew.craft.network.payload;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.farm.FarmJoinClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * S→C: 同步当前玩家是否有待处理的加入农场申请。
 */
@SuppressWarnings("null")
public record FarmJoinPendingStatePayload(boolean pending) implements CustomPacketPayload {

    public static final Type<FarmJoinPendingStatePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "farm_join_pending_state"));

    public static final StreamCodec<FriendlyByteBuf, FarmJoinPendingStatePayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeBoolean(payload.pending),
            buf -> new FarmJoinPendingStatePayload(buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(FarmJoinPendingStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> handleClient(payload));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(FarmJoinPendingStatePayload payload) {
        FarmJoinClientState.setPendingJoinRequest(payload.pending());
    }
}