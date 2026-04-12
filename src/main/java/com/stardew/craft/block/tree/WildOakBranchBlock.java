package com.stardew.craft.block.tree;

import com.stardew.craft.block.shape.ModelVoxelShapeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WildOakBranchBlock extends Block {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	private static final Map<String, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<>();

	@SuppressWarnings("null")
	public WildOakBranchBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@SuppressWarnings("null")
	@Nullable
	@Override
	public BlockState getStateForPlacement(@SuppressWarnings("null") BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(@SuppressWarnings("null") StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@SuppressWarnings("null")
	@Override
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return resolveShape(state);
	}

	@SuppressWarnings("null")
	@Override
	public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
		return resolveShape(state);
	}

	@SuppressWarnings("null")
	private VoxelShape resolveShape(BlockState state) {
		String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
		Direction facing = state.getValue(FACING);
		String key = blockId + "#" + facing.getSerializedName();
		return SHAPE_CACHE.computeIfAbsent(key, k -> {
			String variantKey = "facing=" + facing.getSerializedName();
			VoxelShape raw = ModelVoxelShapeCache.variantShape(blockId, variantKey);
			if (raw == null || raw.isEmpty()) {
				String modelId = ModelVoxelShapeCache.variantModel(blockId, variantKey);
				if (modelId == null) modelId = ModelVoxelShapeCache.variantModel(blockId, "");
				if (modelId != null && !modelId.isBlank()) {
					raw = ModelVoxelShapeCache.shapeFromModelId(modelId);
					raw = ModelVoxelShapeCache.rotateY(raw, ModelVoxelShapeCache.horizontalIndex(facing));
				}
			}
			if (raw != null && !raw.isEmpty()) {
				// Clamp to block cell and return AABB
				var bounds = raw.bounds();
				double minX = Math.max(0, bounds.minX);
				double minY = Math.max(0, bounds.minY);
				double minZ = Math.max(0, bounds.minZ);
				double maxX = Math.min(1, bounds.maxX);
				double maxY = Math.min(1, bounds.maxY);
				double maxZ = Math.min(1, bounds.maxZ);
				if (maxX > minX && maxY > minY && maxZ > minZ) {
					return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
				}
			}
			return Shapes.block();
		});
	}
}
