package com.stardew.craft.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Fixes creative-mode disconnects when sending stacks > 99.
 *
 * The serverbound set_creative_mode_slot packet decodes an ItemStack via an internal StreamCodec
 * implementation which enforces a hard-coded count range of [1;99]. When the client tries to send
 * count=999, decoding fails with:
 * "Value must be within range [1;99]: 999".
 */
@Mixin(targets = "net.minecraft.world.item.ItemStack$3")
public abstract class ItemStackStreamCodecRangeMixin {

    @ModifyConstant(
            method = "decode(Lnet/minecraft/network/RegistryFriendlyByteBuf;)Lnet/minecraft/world/item/ItemStack;",
            constant = @Constant(intValue = 99),
            require = 1
    )
    private int stardewcraft$raiseMaxCountOnDecode(int original) {
        return 999;
    }

    @ModifyConstant(
            method = "encode(Lnet/minecraft/network/RegistryFriendlyByteBuf;Lnet/minecraft/world/item/ItemStack;)V",
            constant = @Constant(intValue = 99),
            require = 0
    )
    private int stardewcraft$raiseMaxCountOnEncode(int original) {
        return 999;
    }
}
