package com.stardew.craft.block.nature;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import javax.annotation.Nullable;

/**
 * 黄土方块 — 行为与原版泥土完全一致，可被锄头翻耕为耕地。
 */
public class YellowDirtBlock extends Block {

    public YellowDirtBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("null")
    @Override
    @Nullable
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.HOE_TILL) {
            return Blocks.FARMLAND.defaultBlockState();
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
