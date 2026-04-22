package com.stardew.craft.mixin;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截原版 ClientboundSetTimePacket 的处理，
 * 阻止它覆盖星露谷维度的 dayTime。
 *
 * 原版 sendLevelInfo / synchronizeTime 发送的 dayTime 来自 DerivedLevelData，
 * 实际返回的是主世界的时间，与我们的虚拟时间不同。
 * 在星露谷维度内直接丢弃这些包，时间由
 * {@link com.stardew.craft.client.StardewClientTimeState} 控制。
 */
@Mixin(ClientPacketListener.class)
public class ClientSetTimeMixin {

    @SuppressWarnings("resource")
    @Inject(method = "handleSetTime", at = @At("HEAD"), cancellable = true)
    private void stardewcraft$blockVanillaTimeInStardew(ClientboundSetTimePacket packet, CallbackInfo ci) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null
                && (level.dimension() == ModDimensions.STARDEW_VALLEY
                    || level.dimension() == ModMiningDimensions.STARDEW_MINING)) {
            // 只更新 gameTime（用于调度），丢弃 dayTime（天空渲染由 mod 控制）
            level.setGameTime(packet.getGameTime());
            ci.cancel();
        }
    }
}
