package com.stardew.craft.block.utility;

import com.stardew.craft.entity.seat.SofaSeatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

@SuppressWarnings("null")
public class ChairBlock extends MapUtilityStaticBlock {
    private final double seatYOffset;

    public ChairBlock(Properties properties, String modelId, double seatYOffset) {
        super(properties, modelId);
        this.seatYOffset = seatYOffset;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        SofaSeatEntity seat = SofaSeatEntity.getOrCreate((net.minecraft.server.level.ServerLevel) level, pos, seatYOffset);
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
