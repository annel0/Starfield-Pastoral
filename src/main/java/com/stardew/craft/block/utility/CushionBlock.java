package com.stardew.craft.block.utility;

import com.stardew.craft.entity.seat.SofaSeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("null")
public class CushionBlock extends MapUtilityStaticBlock {
    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, WoodenChestColorPalette.size() - 1);
    private static final double SEAT_Y_OFFSET = 3.0D / 16.0D;

    public CushionBlock(Properties properties, String modelId) {
        super(properties, modelId);
        registerDefaultState(defaultBlockState()
            .setValue(PART, Part.MAIN)
            .setValue(FACING, net.minecraft.core.Direction.NORTH)
            .setValue(COLOR, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLOR);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        SofaSeatEntity seat = SofaSeatEntity.getOrCreate((net.minecraft.server.level.ServerLevel) level, pos, SEAT_Y_OFFSET);
        if (seat == null) {
            return InteractionResult.PASS;
        }

        if (seat.isVehicle()) {
            return InteractionResult.CONSUME;
        }

        if (!player.startRiding(seat, false)) {
            return InteractionResult.PASS;
        }

        Vec3 seatPos = seat.position();
        player.teleportTo(seatPos.x, seatPos.y, seatPos.z);
        player.setYBodyRot(state.getValue(FACING).toYRot() + 180.0F);
        player.setYRot(state.getValue(FACING).toYRot() + 180.0F);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            SofaSeatEntity.removeForPos((net.minecraft.server.level.ServerLevel) level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}
