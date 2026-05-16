package com.stardew.craft.mixin;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.weather.WeatherManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelWeatherStateMixin {

    @Inject(method = "isRaining", at = @At("HEAD"), cancellable = true)
    private void stardewcraft$useStardewRainState(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel level && ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            cir.setReturnValue(WeatherManager.isRaining(level));
        }
    }

    @Inject(method = "isThundering", at = @At("HEAD"), cancellable = true)
    private void stardewcraft$useStardewThunderState(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerLevel level && ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            cir.setReturnValue(WeatherManager.isThundering(level));
        }
    }
}