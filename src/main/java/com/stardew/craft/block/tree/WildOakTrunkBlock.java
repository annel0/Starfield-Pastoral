package com.stardew.craft.block.tree;

import com.stardew.craft.block.shape.ModelVoxelShapeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings("null")
public class WildOakTrunkBlock extends Block {
	private volatile VoxelShape cachedShape;
	private volatile boolean shapeResolved;

	public WildOakTrunkBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return resolveShape(state);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return resolveShape(state);
	}

	private VoxelShape resolveShape(BlockState state) {
		if (!shapeResolved) {
			synchronized (this) {
				if (!shapeResolved) {
					String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
					String modelId = ModelVoxelShapeCache.variantModel(blockId, "");
					if (modelId != null && !modelId.isBlank()) {
						VoxelShape raw = ModelVoxelShapeCache.shape(modelId);
						if (!raw.isEmpty()) {
							var bounds = raw.bounds();
							cachedShape = Shapes.box(bounds.minX, bounds.minY, bounds.minZ,
								bounds.maxX, bounds.maxY, bounds.maxZ);
						}
					}
					shapeResolved = true;
				}
			}
		}
		return cachedShape != null ? cachedShape : Shapes.block();
	}
}
