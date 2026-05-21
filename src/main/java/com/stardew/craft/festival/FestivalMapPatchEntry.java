package com.stardew.craft.festival;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;

public record FestivalMapPatchEntry(
    BlockPos relativePos,
    BlockState baseState,
    BlockState festivalState,
    CompoundTag baseBlockEntityTag,
    CompoundTag festivalBlockEntityTag
) {
    CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putIntArray("Pos", new int[] { relativePos.getX(), relativePos.getY(), relativePos.getZ() });
        tag.putString("BaseState", SchematicSnapshot.formatBlockState(baseState));
        tag.putString("FestivalState", SchematicSnapshot.formatBlockState(festivalState));
        if (baseBlockEntityTag != null && !baseBlockEntityTag.isEmpty()) {
            tag.put("BaseBlockEntity", baseBlockEntityTag.copy());
        }
        if (festivalBlockEntityTag != null && !festivalBlockEntityTag.isEmpty()) {
            tag.put("FestivalBlockEntity", festivalBlockEntityTag.copy());
        }
        return tag;
    }

    static FestivalMapPatchEntry load(CompoundTag tag) {
        int[] pos = tag.getIntArray("Pos");
        BlockPos relativePos = pos.length >= 3 ? new BlockPos(pos[0], pos[1], pos[2]) : BlockPos.ZERO;
        CompoundTag baseBlockEntity = tag.contains("BaseBlockEntity", Tag.TAG_COMPOUND) ? tag.getCompound("BaseBlockEntity") : null;
        CompoundTag festivalBlockEntity = tag.contains("FestivalBlockEntity", Tag.TAG_COMPOUND) ? tag.getCompound("FestivalBlockEntity") : null;
        return new FestivalMapPatchEntry(
            relativePos,
            SchematicSnapshot.parseBlockState(tag.getString("BaseState")),
            SchematicSnapshot.parseBlockState(tag.getString("FestivalState")),
            baseBlockEntity,
            festivalBlockEntity
        );
    }
}