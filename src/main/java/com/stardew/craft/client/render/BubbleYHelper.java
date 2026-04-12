package com.stardew.craft.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 根据方块模型的 VoxelShape 最大 Y，自动计算气泡底部位置。
 * gap 为模型顶部到气泡底部的间距（块坐标单位）。
 */
public final class BubbleYHelper {
	private static final float DEFAULT_GAP = 0.05f;

	private BubbleYHelper() {
	}

	/**
	 * 计算气泡的 Y 坐标（模型最高点 + gap）。
	 */
	@SuppressWarnings("null")
	public static float get(BlockState state, BlockGetter level, BlockPos pos) {
		return get(state, level, pos, DEFAULT_GAP);
	}

	@SuppressWarnings("null")
	public static float get(BlockState state, BlockGetter level, BlockPos pos, float gap) {
		VoxelShape shape = state.getShape(level, pos, CollisionContext.empty());
		double maxY = shape.isEmpty() ? 1.0 : shape.max(Direction.Axis.Y);
		return (float) maxY + gap;
	}
}
