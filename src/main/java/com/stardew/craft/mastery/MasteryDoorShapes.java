package com.stardew.craft.mastery;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 复刻 vanilla DoorBlock 闭门 3 像素 slab 形状（按 FACING 切 4 个方向）。
 * 用于：未满足精通条件的玩家看到的门视觉/碰撞形状。
 */
public final class MasteryDoorShapes {
    private MasteryDoorShapes() {}

    private static final VoxelShape CLOSED_NORTH = Block.box(0, 0, 13, 16, 16, 16);
    private static final VoxelShape CLOSED_SOUTH = Block.box(0, 0, 0,  16, 16, 3);
    private static final VoxelShape CLOSED_WEST  = Block.box(13, 0, 0, 16, 16, 16);
    private static final VoxelShape CLOSED_EAST  = Block.box(0, 0, 0,  3,  16, 16);

    public static VoxelShape closedShape(BlockState state) {
        Direction facing = state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
            ? state.getValue(BlockStateProperties.HORIZONTAL_FACING)
            : Direction.NORTH;
        return switch (facing) {
            case SOUTH -> CLOSED_SOUTH;
            case WEST -> CLOSED_WEST;
            case EAST -> CLOSED_EAST;
            default -> CLOSED_NORTH;
        };
    }
}
