package com.stardew.craft.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WildWeedsBlockEntity extends BlockEntity {
	public WildWeedsBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.WILD_WEEDS.get(), pos, state);
	}
}
