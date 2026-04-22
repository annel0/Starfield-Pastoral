package com.stardew.craft.mixin;

import com.stardew.craft.cutscene.runtime.EventCameraController;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to override camera position/rotation during cutscene events.
 */
@Mixin(Camera.class)
public abstract class EventCameraMixin {

    @Shadow protected abstract void setPosition(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yRot, float xRot);

    @Inject(method = "setup", at = @At("TAIL"))
    private void stardewcraft$overrideCameraForEvent(BlockGetter level, Entity entity, boolean detached,
                                                      boolean thirdPersonReverse, float partialTick,
                                                      CallbackInfo ci) {
        if (EventCameraController.isActive()) {
            setPosition(
                    EventCameraController.getInterpolatedX(partialTick),
                    EventCameraController.getInterpolatedY(partialTick),
                    EventCameraController.getInterpolatedZ(partialTick)
            );
            setRotation(
                    EventCameraController.getInterpolatedYaw(partialTick),
                    EventCameraController.getInterpolatedPitch(partialTick)
            );
        }
    }
}
