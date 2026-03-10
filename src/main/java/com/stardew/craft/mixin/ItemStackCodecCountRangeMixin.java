package com.stardew.craft.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Raises the ItemStack CODEC "count" range from [1, 99] to [1, 999].
 *
 * Vanilla builds ItemStack.CODEC via ExtraCodecs.intRange(1, 99) for the "count" field.
 * This range is used during validation (e.g. validatedStreamCodec re-encodes with ItemStack.CODEC),
 * and will otherwise disconnect clients when counts exceed 99.
 */
@Mixin(ItemStack.class)
public abstract class ItemStackCodecCountRangeMixin {

    @ModifyConstant(
            method = "lambda$static$3(Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;)Lcom/mojang/datafixers/kinds/App;",
            constant = @Constant(intValue = 99),
            require = 1
    )
    private static int stardewcraft$raiseItemStackCodecMaxCount(int original) {
        return 999;
    }
}
