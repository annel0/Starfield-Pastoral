package com.stardew.craft.item.misc;

import com.stardew.craft.item.StardewQualityItem;
import com.stardew.craft.player.PlayerStardewDataAPI;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LifeElixirItem extends StardewQualityItem {
    public LifeElixirItem(String typeKey, int basePrice, int edibility, boolean supportsQuality, Properties properties) {
        super(typeKey, basePrice, edibility, supportsQuality, properties, true);
    }

    @Override
    public @Nonnull ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level,
            @Nonnull LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide && livingEntity instanceof ServerPlayer serverPlayer) {
            PlayerStardewDataAPI.setHealth(serverPlayer, PlayerStardewDataAPI.getMaxHealth(serverPlayer));
        }
        return result;
    }
}