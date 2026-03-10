package com.stardew.craft.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Allows ItemStack counts > 99 to be encoded/decoded over the network.
 *
 * Creative mode uses the serverbound set_creative_mode_slot packet which relies on
 * ItemStack.validatedStreamCodec(...). Vanilla hard-codes the max count as 99 and
 * disconnects with: "Value must be within range [1;99]: 999".
 *
 * Patching validatedStreamCodec is more stable than targeting anonymous inner classes
 * (e.g. ItemStack$3) whose method descriptors/bridge methods can vary.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackValidatedStreamCodecMixin {

    @ModifyConstant(
            method = "validatedStreamCodec",
            constant = @Constant(intValue = 99),
            require = 1
    )
    private static int stardewcraft$raiseValidatedMaxCount(int original) {
        return 999;
    }
}
