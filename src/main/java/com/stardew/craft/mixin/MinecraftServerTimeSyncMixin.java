package com.stardew.craft.mixin;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 拦截原版每秒一次的 synchronizeTime() 调用，
 * 跳过星露谷相关维度，防止原版 dayTime 包覆盖虚拟时间。
 */
@Mixin(MinecraftServer.class)
public class MinecraftServerTimeSyncMixin {

    @Inject(method = "synchronizeTime", at = @At("HEAD"), cancellable = true)
    private void stardewcraft$skipStardewTimeSync(ServerLevel level, CallbackInfo ci) {
        if (level.dimension() == ModDimensions.STARDEW_VALLEY
                || level.dimension() == ModMiningDimensions.STARDEW_MINING) {
            ci.cancel();
        }
    }
}
