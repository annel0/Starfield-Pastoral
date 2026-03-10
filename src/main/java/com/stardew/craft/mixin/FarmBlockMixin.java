package com.stardew.craft.mixin;

import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FarmBlock.class)
public class FarmBlockMixin {

    /**
     * 禁止星露谷维度的耕地自动湿润（通过附近的水）
     * 这样耕地就不会自动变成湿润耕地，哪怕旁边有水
     */
    @Inject(method = "isNearWater", at = @At("HEAD"), cancellable = true)
    private static void isNearWater(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (level instanceof Level l && l.dimension().location().equals(ModDimensions.STARDEW_VALLEY.location())) {
            cir.setReturnValue(false);
        }
    }

    /**
     * 禁止星露谷维度的耕地退化为泥土（因为干涸）
     */
    @Inject(method = "turnToDirt", at = @At("HEAD"), cancellable = true)
    private static void turnToDirt(Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        if (level.dimension().location().equals(ModDimensions.STARDEW_VALLEY.location())) {
            ci.cancel();
        }
    }

    /**
     * 禁止随机刻改变湿度（原版会随着随机刻将湿度降维0）
     * 我们要在过夜时统一变为0（除非有保水土壤）
     * 
     * 在 randomTick 中，原版逻辑是：
     * 1. 并没有 setMoisture 的单独方法，是直接 level.setBlock(..., state.setValue(MOISTURE, newAmount), 2)
     * 2. 我们通过 Inject randomTick 并取消大部分逻辑来实现
     */
    @SuppressWarnings("null")
    @Inject(method = "randomTick", at = @At("HEAD"), cancellable = true)
    public void randomTick(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random, CallbackInfo ci) {
        if (level.dimension().location().equals(ModDimensions.STARDEW_VALLEY.location())) {
            // 星露谷维度中：
            // 1. 下雨时会自动湿润 (原版逻辑) -> 我们保留这个逻辑？
            //    原版Stardew下雨天不需要浇水。Minecraft randomTick里会检查 isRainingAt(pos.above())
            
            if (!level.isRainingAt(pos.above())) {
                // 如果不下雨，禁止任何随机刻导致的湿度变化 (保持当前湿度)
                // 原版逻辑是如果不下雨且不是isNearWater，moisture会慢慢减
                ci.cancel(); 
            } else {
                // 如果下雨，允许原版逻辑执行（原版逻辑会将湿度设为7）
                // FarmBlock.java: 
                // if (isRainingAt) { level.setBlock(pos, state.setValue(MOISTURE, 7), 2); }
                // 所以不cancel
            }
        }
    }
}
