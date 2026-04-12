package com.stardew.craft.item.tool;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

/**
 * 包装一个原版 Tier，将 getUses() 强制返回 0。
 * 这样 TieredItem 构造器调用 properties.durability(tier.getUses())
 * 时会设置 MAX_DAMAGE=0，从根源阻止工具拥有耐久度。
 */
public record IndestructibleTier(Tier wrapped) implements Tier {
    @Override public int getUses() { return 0; }
    @Override public float getSpeed() { return wrapped.getSpeed(); }
    @Override public float getAttackDamageBonus() { return wrapped.getAttackDamageBonus(); }
    @Override public int getEnchantmentValue() { return wrapped.getEnchantmentValue(); }
    @Override public @NotNull TagKey<Block> getIncorrectBlocksForDrops() { return wrapped.getIncorrectBlocksForDrops(); }
    @Override public @NotNull Ingredient getRepairIngredient() { return wrapped.getRepairIngredient(); }
}
