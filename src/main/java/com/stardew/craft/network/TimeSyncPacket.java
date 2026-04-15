package com.stardew.craft.network;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.time.StardewTimeManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

/**
 * 时间同步数据包（服务端 -> 客户端）
 */
public record TimeSyncPacket(
    int currentTime,
    int currentDay,
    int currentSeason,
    int currentYear
) implements CustomPacketPayload {
    
    @SuppressWarnings("null")
    public static final Type<TimeSyncPacket> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "time_sync")
    );
    
    @SuppressWarnings("null")
    public static final StreamCodec<ByteBuf, TimeSyncPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, TimeSyncPacket::currentTime,
        ByteBufCodecs.INT, TimeSyncPacket::currentDay,
        ByteBufCodecs.INT, TimeSyncPacket::currentSeason,
        ByteBufCodecs.INT, TimeSyncPacket::currentYear,
        TimeSyncPacket::new
    );
    
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    /**
     * 从时间管理器创建数据包
     */
    public static TimeSyncPacket fromTimeManager(StardewTimeManager timeManager) {
        return new TimeSyncPacket(
            timeManager.getCurrentTime(),
            timeManager.getCurrentDay(),
            timeManager.getCurrentSeason(),
            timeManager.getCurrentYear()
        );
    }
    
    /**
     * 客户端接收处理
     */
    public static void handle(TimeSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // 更新客户端时间缓存
            StardewTimeManager clientTime = new StardewTimeManager();
            clientTime.setCurrentTime(packet.currentTime());
            clientTime.setCurrentDay(packet.currentDay());
            clientTime.setCurrentSeason(packet.currentSeason());
            
            com.stardew.craft.client.hud.StardewTimeHud.updateClientTime(clientTime);
        });
    }
}
