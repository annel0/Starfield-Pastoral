package com.stardew.craft.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.stardew.craft.client.fishing.FishingBiteVisuals;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Apply a short "dip" transform on bite, driven by our client-side visuals timer.
 */
@Mixin(FishingHookRenderer.class)
public abstract class FishingHookRendererBiteDipMixin {
	private static final ThreadLocal<Boolean> STARDEWCRAFT_PUSHED = ThreadLocal.withInitial(() -> false);

	@Inject(
			method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At("HEAD"),
			require = 0
	)
	private void stardewcraft$dipOnBiteHead(FishingHook hook, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
		if (hook == null) {
			return;
		}
		float dy = FishingBiteVisuals.getBobberDipOffsetY(hook.getId());
		if (dy == 0f) {
			return;
		}
		poseStack.pushPose();
		STARDEWCRAFT_PUSHED.set(true);
		poseStack.translate(0.0, dy, 0.0);
	}

	@Inject(
			method = "render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At("RETURN"),
			require = 0
	)
	private void stardewcraft$dipOnBiteReturn(FishingHook hook, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
		if (Boolean.TRUE.equals(STARDEWCRAFT_PUSHED.get())) {
			STARDEWCRAFT_PUSHED.set(false);
			poseStack.popPose();
		}
	}
}
