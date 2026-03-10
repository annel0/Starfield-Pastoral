package com.stardew.craft.mixin;

import com.stardew.craft.client.weapon.CameraShakeState;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraShakeMixin {
    @Shadow private float yRot;
    @Shadow private float xRot;

    @Shadow protected abstract void setRotation(float yRot, float xRot);

    @Inject(method = "setup", at = @At("TAIL"))
    private void stardewcraft$applyCameraShake(BlockGetter level, Entity entity, boolean detached, boolean thirdPersonReverse,
                                               float partialTick, CallbackInfo ci) {
        float yaw = CameraShakeState.getYawOffset(partialTick);
        float pitch = CameraShakeState.getPitchOffset(partialTick);
        if (yaw != 0.0f || pitch != 0.0f) {
            setRotation(this.yRot + yaw, this.xRot + pitch);
        }
    }
}
