package com.stardew.craft.mixin;

import com.stardew.craft.fishing.server.FishingSessionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Disable vanilla FishingHook bite/lure logic when StardewCraft is controlling fishing.
 *
 * We still spawn a vanilla FishingHook for rendering (line + bobber), but we don't want vanilla to
 * randomly trigger its own bite dip / sounds on top of our SV-like timing.
 */
@Mixin(FishingHook.class)
public abstract class FishingHookDisableVanillaBiteMixin {
	@Inject(method = "catchingFish", at = @At("HEAD"), cancellable = true, require = 0)
	private void stardewcraft$disableVanillaCatchingFish(BlockPos pos, CallbackInfo ci) {
		FishingHook self = (FishingHook) (Object) this;
		if (self.level() == null || self.level().isClientSide) {
			return;
		}
		Entity owner = self.getOwner();
		if (!(owner instanceof ServerPlayer player)) {
			return;
		}
		FishingSessionManager mgr = FishingSessionManager.get(player.server);
		if (mgr.getState(player) != null) {
			ci.cancel();
		}
	}
}
