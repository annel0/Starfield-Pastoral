package com.stardew.craft.item.misc;

import com.stardew.craft.item.StardewQualityItem;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Muscle Remedy — cures Exhaustion debuff in addition to normal edibility effects.
 * Mirrors vanilla Stardew Valley item 351 behavior.
 */
public class MuscleRemedyItem extends StardewQualityItem {

    public MuscleRemedyItem(String typeKey, int basePrice, int edibility, boolean supportsQuality, Properties properties) {
        super(typeKey, basePrice, edibility, supportsQuality, properties, true);
    }

    @Override
    public @Nonnull ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level,
            @Nonnull LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(
                Objects.requireNonNull(stack), Objects.requireNonNull(level), Objects.requireNonNull(livingEntity));

        if (!level.isClientSide && livingEntity instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            PlayerStardewDataAPI.cureExhaustion(serverPlayer);
        }

        return result;
    }
}
