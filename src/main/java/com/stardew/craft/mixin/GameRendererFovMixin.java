package com.stardew.craft.mixin;

import com.stardew.craft.client.weapon.CameraShakeState;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public abstract class GameRendererFovMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void stardewcraft$applyFovShake(Camera camera, float partialTick, boolean useFovSetting,
                                            CallbackInfoReturnable<Double> cir) {
        float delta = CameraShakeState.getFovDelta(partialTick);
        if (delta != 0.0f) {
            cir.setReturnValue(cir.getReturnValue() + delta);
        }
    }
}
