package com.stardew.craft.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NewTreePartBlockEntity extends BlockEntity {
	public NewTreePartBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.NEW_TREE_PART.get(), pos, state);
	}
}
