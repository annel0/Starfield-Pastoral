package com.stardew.craft.mixin;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 阻止客户端 ClientLevel 在星露谷维度自行推进 dayTime。
 *
 * 原版 ClientLevel.tickTime() 每 tick 会递增 gameTime 和 dayTime，
 * 其中 dayTime 递增与 mod 的虚拟时间系统冲突。星露谷维度的 dayTime 由
 * {@link com.stardew.craft.client.StardewClientTimeState} 独占控制。
 *
 * 修复：必须保留 gameTime 递增（武器冷却、技能动画等依赖它），
 * 只跳过 dayTime 递增。
 */
@Mixin(ClientLevel.class)
public class ClientLevelTickTimeMixin {

    @SuppressWarnings("resource")
    @Inject(method = "tickTime", at = @At("HEAD"), cancellable = true)
    private void stardewcraft$skipTickTimeInStardew(CallbackInfo ci) {
        ClientLevel self = (ClientLevel) (Object) this;
        if (self.dimension() == ModDimensions.STARDEW_VALLEY
                || self.dimension() == ModMiningDimensions.STARDEW_MINING) {
            // 保留 gameTime 递增（冷却计时、动画进度等都依赖它）
            self.setGameTime(self.getLevelData().getGameTime() + 1L);
            // 跳过 dayTime 递增（由 StardewClientTimeState 独占控制）
            ci.cancel();
        }
    }
}
