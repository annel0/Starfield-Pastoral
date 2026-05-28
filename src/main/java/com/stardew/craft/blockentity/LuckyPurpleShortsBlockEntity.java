package com.stardew.craft.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LuckyPurpleShortsBlockEntity extends BlockEntity {
    public LuckyPurpleShortsBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LUCKY_PURPLE_SHORTS.get(), pos, state);
    }
}