package com.stardew.craft.animal.service;

import com.stardew.craft.animal.model.AnimalBuildingRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@SuppressWarnings("null")
public final class AnimalDoorStateService {
    private AnimalDoorStateService() {
    }

    public static boolean isAnyBoundaryDoorOpen(ServerLevel level, AnimalBuildingRecord building) {
        if (level == null || building == null) {
            return false;
        }

        if (!building.boundaryDoorCells().isEmpty()) {
            for (Long packed : building.boundaryDoorCells()) {
                BlockPos pos = BlockPos.of(packed);
                BlockState state = level.getBlockState(pos);
                if (isDoorOrFenceGate(state) && isOpen(state)) {
                    return true;
                }
            }
            return false;
        }

        for (int y = building.minY() - 1; y <= building.maxY() + 1; y++) {
            for (int z = building.minZ() - 1; z <= building.maxZ() + 1; z++) {
                for (int x = building.minX() - 1; x <= building.maxX() + 1; x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (isDoorOrFenceGate(state) && isOpen(state)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static int setBoundaryDoorsOpen(ServerLevel level, AnimalBuildingRecord building, boolean open) {
        if (level == null || building == null) {
            return 0;
        }

        int changed = 0;
        if (!building.boundaryDoorCells().isEmpty()) {
            for (Long packed : building.boundaryDoorCells()) {
                BlockPos pos = BlockPos.of(packed);
                BlockState state = level.getBlockState(pos);
                if (!isDoorOrFenceGate(state) || !state.hasProperty(BlockStateProperties.OPEN)) {
                    continue;
                }

                boolean current = Boolean.TRUE.equals(state.getValue(BlockStateProperties.OPEN));
                if (current == open) {
                    continue;
                }

                level.setBlock(pos, state.setValue(BlockStateProperties.OPEN, open), 3);
                changed++;
            }
            return changed;
        }

        for (int y = building.minY() - 1; y <= building.maxY() + 1; y++) {
            for (int z = building.minZ() - 1; z <= building.maxZ() + 1; z++) {
                for (int x = building.minX() - 1; x <= building.maxX() + 1; x++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!isDoorOrFenceGate(state) || !state.hasProperty(BlockStateProperties.OPEN)) {
                        continue;
                    }

                    boolean current = Boolean.TRUE.equals(state.getValue(BlockStateProperties.OPEN));
                    if (current == open) {
                        continue;
                    }

                    level.setBlock(pos, state.setValue(BlockStateProperties.OPEN, open), 3);
                    changed++;
                }
            }
        }

        return changed;
    }

    public static boolean isDoorOrFenceGate(BlockState state) {
        return state.is(BlockTags.DOORS) || state.is(BlockTags.FENCE_GATES);
    }

    public static boolean isOpen(BlockState state) {
        return state.hasProperty(BlockStateProperties.OPEN) && Boolean.TRUE.equals(state.getValue(BlockStateProperties.OPEN));
    }
}
