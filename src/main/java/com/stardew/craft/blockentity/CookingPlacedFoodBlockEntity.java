package com.stardew.craft.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CookingPlacedFoodBlockEntity extends BlockEntity {
    public CookingPlacedFoodBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLACED_COOKING_FOOD.get(), pos, state);
    }
}
