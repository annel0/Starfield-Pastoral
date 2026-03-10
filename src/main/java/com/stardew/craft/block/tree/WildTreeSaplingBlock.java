package com.stardew.craft.block.tree;

import com.stardew.craft.block.shape.ModelVoxelShapeCache;
import com.stardew.craft.manager.TreeGrowthManager;
import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WildTreeSaplingBlock extends Block {
	private final WildTrees.Def def;
	private final int stage;
	private volatile VoxelShape modelShape;
	private volatile boolean modelShapeResolved;

	@SuppressWarnings("null")
	public WildTreeSaplingBlock(WildTrees.Def def, int stage, Properties properties) {
		super(properties);
		this.def = def;
		this.stage = stage;
	}

	public WildTrees.Def getDef() {
		return def;
	}

	public int getStage() {
		return stage;
	}

	@SuppressWarnings("null")
	@Override
	public VoxelShape getShape(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockGetter level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") CollisionContext context) {
		if (!modelShapeResolved) {
			synchronized (this) {
				if (!modelShapeResolved) {
					String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
					String modelId = ModelVoxelShapeCache.variantModel(blockId, "");
					if (modelId != null && !modelId.isBlank()) {
						modelShape = ModelVoxelShapeCache.shape(modelId);
					}
					modelShapeResolved = true;
				}
			}
		}
		return modelShape != null ? modelShape : super.getShape(state, level, pos, context);
	}

	@SuppressWarnings("null")
	@Override
	public void onPlace(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (level.isClientSide || oldState.getBlock() == state.getBlock()) {
			return;
		}
		// Stage swap (sapling0 <-> sapling1) should not reset tracked growth days.
		if (isSameTreeSapling(oldState.getBlock())) {
			return;
		}
		TreeGrowthManager.get((net.minecraft.server.level.ServerLevel) level).addSapling(level, pos);
	}

	@SuppressWarnings("null")
	@Override
	public void onRemove(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState newState, boolean movedByPiston) {
		super.onRemove(state, level, pos, newState, movedByPiston);
		if (level.isClientSide || state.getBlock() == newState.getBlock()) {
			return;
		}
		// Stage swap (sapling0 <-> sapling1) should not reset tracked growth days.
		if (isSameTreeSapling(newState.getBlock())) {
			return;
		}
		TreeGrowthManager.get((net.minecraft.server.level.ServerLevel) level).removeSapling(level, pos);
	}

	private boolean isSameTreeSapling(Block maybeSapling) {
		return maybeSapling instanceof WildTreeSaplingBlock other && other.getDef() == this.def;
	}
}
