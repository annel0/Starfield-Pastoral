package com.stardew.craft.mixin;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 阻止原版在星露谷维度检测"全员睡眠 → 跳过夜晚"逻辑。
 * <p>
 * 星露谷维度使用自定义的 {@link com.stardew.craft.event.SleepVoteTracker} 投票机制，
 * 不能让原版的 {@code wakeUpAllPlayers()} 提前唤醒正在等待投票的玩家。
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevelSleepMixin {

    @Shadow @Final private SleepStatus sleepStatus;

    /**
     * 对 {@code ServerLevel.tick()} 中的 {@code sleepStatus.areEnoughSleeping(i)} 调用进行重定向。
     * 在星露谷/矿井维度中始终返回 false，阻止原版睡眠推进逻辑。
     */
    @Redirect(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/players/SleepStatus;areEnoughSleeping(I)Z"
        )
    )
    @SuppressWarnings("resource")
    private boolean stardew$blockVanillaSleepFinish(SleepStatus instance, int percentage) {
        ServerLevel self = (ServerLevel) (Object) this;
        if (self.dimension() == ModDimensions.STARDEW_VALLEY
                || self.dimension() == ModMiningDimensions.STARDEW_MINING) {
            return false;
        }
        return instance.areEnoughSleeping(percentage);
    }
}
