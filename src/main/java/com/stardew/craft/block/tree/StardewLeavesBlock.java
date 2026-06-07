package com.stardew.craft.block.tree;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public class StardewLeavesBlock extends LeavesBlock {
	private static final int FAST_DECAY_DELAY = 3;

	public StardewLeavesBlock(Properties properties) {
		super(properties);
	}

	@SuppressWarnings("null")
	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
		if (shouldFastDecay(updated)) {
			level.scheduleTick(pos, this, FAST_DECAY_DELAY);
		}
		return updated;
	}

	@SuppressWarnings("null")
	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);
		BlockState current = level.getBlockState(pos);
		if (current.is(this) && shouldFastDecay(current)) {
			level.destroyBlock(pos, true);
		}
	}

	@SuppressWarnings("null")
	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);
		BlockState current = level.getBlockState(pos);
		if (current.is(this) && shouldFastDecay(current)) {
			level.scheduleTick(pos, this, FAST_DECAY_DELAY + random.nextInt(4));
		}
	}

	private static boolean shouldFastDecay(BlockState state) {
		return state.hasProperty(PERSISTENT)
				&& state.hasProperty(DISTANCE)
				&& !state.getValue(PERSISTENT)
				&& state.getValue(DISTANCE) >= DECAY_DISTANCE;
	}
}
