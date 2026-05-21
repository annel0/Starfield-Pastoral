package com.stardew.craft.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EggFestivalEggBlockEntity extends BlockEntity {
    public EggFestivalEggBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EGG_FESTIVAL_EGG.get(), pos, state);
    }
}
