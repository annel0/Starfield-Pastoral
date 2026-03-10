package com.stardew.craft.item.weapon;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IStardewWeapon {
    String getWeaponId();
    WeaponData getWeaponData();
    InteractionResultHolder<ItemStack> useSkill(Level level, Player player, InteractionHand hand, boolean majorSkill);
}
