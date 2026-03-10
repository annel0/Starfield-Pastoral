package com.stardew.craft.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * 天气同步数据包 - 服务端发送给客户端
 */
public record WeatherSyncPacket(
        String dimension,
        String weatherType,
        String weatherForTomorrow
) implements CustomPacketPayload {

    @SuppressWarnings("null")
public static final CustomPacketPayload.Type<WeatherSyncPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("stardewcraft", "weather_sync"));

    @SuppressWarnings("null")
public static final StreamCodec<ByteBuf, WeatherSyncPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            WeatherSyncPacket::dimension,
            ByteBufCodecs.STRING_UTF8,
            WeatherSyncPacket::weatherType,
            ByteBufCodecs.STRING_UTF8,
            WeatherSyncPacket::weatherForTomorrow,
            WeatherSyncPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
