package com.stardew.craft.network;

import com.stardew.craft.StardewCraft;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 楼梯状态同步包 - 服务端 → 客户端
 * 通知客户端某层的楼梯已被发现及其位置，以及是否为竖井。
 */
public record LadderSyncPacket(int floorNumber, boolean ladderFound, BlockPos ladderPos, boolean isShaft) implements CustomPacketPayload {

    @SuppressWarnings("null")
    public static final CustomPacketPayload.Type<LadderSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "ladder_sync"));

    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, LadderSyncPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    LadderSyncPacket::floorNumber,
                    ByteBufCodecs.BOOL,
                    LadderSyncPacket::ladderFound,
                    BlockPos.STREAM_CODEC.cast(),
                    LadderSyncPacket::ladderPos,
                    ByteBufCodecs.BOOL,
                    LadderSyncPacket::isShaft,
                    LadderSyncPacket::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /**
     * 客户端处理：缓存楼梯位置供高亮渲染使用
     */
    public static void handle(LadderSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            com.stardew.craft.client.mining.ClientMiningState.setLadderState(
                    packet.floorNumber(), packet.ladderFound(), packet.ladderPos(), packet.isShaft());
        });
    }
}
