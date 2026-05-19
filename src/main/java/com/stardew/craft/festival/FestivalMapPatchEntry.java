package com.stardew.craft.festival;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public record FestivalMapPatchEntry(
    BlockPos relativePos,
    BlockState baseState,
    BlockState festivalState,
    CompoundTag baseBlockEntityTag,
    CompoundTag festivalBlockEntityTag
) {
}