package com.stardew.craft.mixin;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public abstract class ItemBlockRenderTypesLeavesMixin {
    @Shadow
    private static boolean renderCutout;

    @Inject(method = "getChunkRenderType", at = @At("HEAD"), cancellable = true)
    private static void stardewcraft$forceMinecraftLeavesCutout(BlockState state, CallbackInfoReturnable<RenderType> cir) {
        if (state.is(BlockTags.LEAVES)) {
            cir.setReturnValue(RenderType.cutoutMipped());
        }
    }

    @Inject(method = "getMovingBlockRenderType", at = @At("HEAD"), cancellable = true)
    private static void stardewcraft$forceMovingLeavesCutout(BlockState state, CallbackInfoReturnable<RenderType> cir) {
        if (state.is(BlockTags.LEAVES)) {
            cir.setReturnValue(RenderType.cutoutMipped());
        }
    }

    @Inject(method = "getRenderLayers", at = @At("HEAD"), cancellable = true)
    private static void stardewcraft$forceLeafRenderLayers(BlockState state, CallbackInfoReturnable<ChunkRenderTypeSet> cir) {
        if (state.is(BlockTags.LEAVES)) {
            cir.setReturnValue(ChunkRenderTypeSet.of(RenderType.cutoutMipped()));
        }
    }

    @Inject(method = "setFancy", at = @At("TAIL"))
    private static void stardewcraft$keepLeavesCutout(boolean fancy, CallbackInfo ci) {
        renderCutout = true;
    }
}
