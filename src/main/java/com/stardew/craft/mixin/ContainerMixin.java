package com.stardew.craft.mixin;

import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
public interface ContainerMixin {

    @Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
    default void stardewcraft$maxStackSize(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(999);
    }
}
