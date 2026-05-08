package com.stardew.craft.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.stardew.craft.client.MummyCollapseClientState;
import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.world.entity.monster.Drowned;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrownedRenderer.class)
public class DrownedRendererMummyCollapseMixin {

    private static final float COLLAPSE_PIVOT_Y = 0.45F;

    @Inject(method = "setupRotations(Lnet/minecraft/world/entity/monster/Drowned;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V", at = @At("TAIL"))
    private void stardewcraft$applyCollapsedMummyRotation(
            Drowned entity,
            PoseStack poseStack,
            float bob,
            float yBodyRot,
            float partialTick,
            float scale,
            CallbackInfo ci
    ) {
        long nowTick = entity.level().getGameTime();
        if (!MummyCollapseClientState.isCollapsed(entity.getId(), nowTick)) {
            return;
        }

        float pivotY = COLLAPSE_PIVOT_Y / scale;
        poseStack.rotateAround(Axis.XP.rotationDegrees(90.0F), 0.0F, pivotY, 0.0F);
    }
}