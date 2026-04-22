package com.stardew.craft.mixin;

import com.stardew.craft.core.ModDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在星露谷维度中拦截原版天气 GameEvent 包，
 * 防止主世界下雨时客户端出现"闪雨"现象。
 *
 * <p>星露谷天气完全由 {@link com.stardew.craft.weather.WeatherManager} 管理，
 * 通过自定义 {@link com.stardew.craft.network.WeatherSyncPacket} 同步到客户端。
 * 原版的 {@code START_RAINING / STOP_RAINING / RAIN_LEVEL_CHANGE / THUNDER_LEVEL_CHANGE}
 * 包来自共享的 PrimaryLevelData，会携带主世界的天气状态，必须完全屏蔽。
 */
@Mixin(ClientPacketListener.class)
public class ClientWeatherPacketMixin {

    @SuppressWarnings("null")
    @Inject(method = "handleGameEvent", at = @At("HEAD"), cancellable = true)
    private void onHandleGameEvent(ClientboundGameEventPacket packet, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        // 只在星露谷维度拦截
        if (!mc.level.dimension().location().equals(ModDimensions.STARDEW_VALLEY.location())) {
            return;
        }

        // 屏蔽所有原版天气相关 GameEvent
        ClientboundGameEventPacket.Type eventType = packet.getEvent();
        if (eventType == ClientboundGameEventPacket.START_RAINING
                || eventType == ClientboundGameEventPacket.STOP_RAINING
                || eventType == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE
                || eventType == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE) {
            ci.cancel();
        }
    }
}
