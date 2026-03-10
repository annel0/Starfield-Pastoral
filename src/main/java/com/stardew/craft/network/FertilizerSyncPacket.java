package com.stardew.craft.network;

import com.stardew.craft.block.FertilizerType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

/**
 * 肥料数据同步包 - 服务端发送给客户端
 */
public record FertilizerSyncPacket(
        BlockPos pos,
        @Nullable String fertilizerType // null表示移除肥料
) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final CustomPacketPayload.Type<FertilizerSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("stardewcraft", "fertilizer_sync"));

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, FertilizerSyncPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            FertilizerSyncPacket::pos,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs::optional),
            packet -> java.util.Optional.ofNullable(packet.fertilizerType()),
            (pos, typeOpt) -> new FertilizerSyncPacket(pos, typeOpt.orElse(null))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Nullable
    public FertilizerType getFertilizerType() {
        if (fertilizerType == null) {
            return null;
        }
        try {
            return FertilizerType.valueOf(fertilizerType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
