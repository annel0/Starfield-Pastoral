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
 * 包含 UI 时间数据和原始 virtualDayTime 用于客户端天空渲染。
 */
public record TimeSyncPacket(
    int currentTime,
    int currentDay,
    int currentSeason,
    int currentYear,
    long virtualDayTime,
    boolean timeFrozen
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
        ByteBufCodecs.VAR_LONG, TimeSyncPacket::virtualDayTime,
        ByteBufCodecs.BOOL, TimeSyncPacket::timeFrozen,
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
        long vdt = 0;
        var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            vdt = timeManager.getVirtualDayTime(server.overworld());
        }
        return new TimeSyncPacket(
            timeManager.getCurrentTime(),
            timeManager.getCurrentDay(),
            timeManager.getCurrentSeason(),
            timeManager.getCurrentYear(),
            vdt,
            com.stardew.craft.festival.ActiveFestivalHandlers.isAnyTimeFreezeActive()
        );
    }
    
    /**
     * 客户端接收处理
     */
    public static void handle(TimeSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // 更新客户端时间缓存（UI 用）
            StardewTimeManager clientTime = new StardewTimeManager();
            clientTime.setCurrentTime(packet.currentTime());
            clientTime.setCurrentDay(packet.currentDay());
            clientTime.setCurrentSeason(packet.currentSeason());
            clientTime.setCurrentYear(packet.currentYear());
            
            com.stardew.craft.client.hud.StardewTimeHud.updateClientTime(clientTime);
            com.stardew.craft.client.specialorder.ClientSpecialOrderUnlockState.refreshBoardRenderIfChanged();
            
            // 更新客户端天空时间（每 tick 会强制覆盖 ClientLevel.dayTime）
            com.stardew.craft.client.StardewClientTimeState.onServerTimeSync(packet.virtualDayTime(), packet.timeFrozen());
        });
    }
}
