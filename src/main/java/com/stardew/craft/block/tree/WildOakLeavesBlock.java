package com.stardew.craft.block.tree;

import com.stardew.craft.item.ModItems;
import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

public class WildOakLeavesBlock extends Block {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private static final float SEED_DROP_CHANCE = 0.20f;

	@SuppressWarnings("null")
	public WildOakLeavesBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@SuppressWarnings("null")
	@Nullable
	@Override
	public BlockState getStateForPlacement(@SuppressWarnings("null") BlockPlaceContext context) {
		// 六向：朝向玩家视线的反方向（与许多 vanilla 方向方块一致）
		return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public boolean isRandomlyTicking(@SuppressWarnings("null") BlockState state) {
		return false;          // leaves never decay naturally
	}

	/**
	 * Only called via scheduled tick from {@code WildTreeChopEvents.scheduleNearbyLeafDecay()}.
	 * Unconditionally removes the leaf and may drop seeds.
	 */
	@Override
	protected void tick(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") ServerLevel level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") net.minecraft.util.RandomSource random) {
		forceDecay(state, level, pos, random);
	}



	/**
	 * Unconditionally destroys this leaf block (called only from tree-chop scheduled tick).
	 * May drop seeds.
	 */
	@SuppressWarnings("null")
	private static void forceDecay(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
		WildTrees.Def def = WildTrees.findByAnyPart(state);
		if (def == null) {
			return;
		}

		if (random.nextFloat() < SEED_DROP_CHANCE) {
			@SuppressWarnings("null")
			ItemStack seed = switch (def.id()) {
				case "oak" -> new ItemStack(ModItems.ACORN.get());
				case "maple" -> new ItemStack(ModItems.MAPLE_SEED.get());
				case "pine" -> new ItemStack(ModItems.PINE_CONE.get());
				case "mahogany" -> new ItemStack(ModItems.MAHOGANY_SEED.get());
				case "mystic_tree" -> new ItemStack(ModItems.MYSTIC_TREE_SEED.get());
				default -> ItemStack.EMPTY;
			};
			if (!seed.isEmpty()) {
				seed.setCount(net.minecraft.util.Mth.nextInt(random, 1, 2));
				Block.popResource(level, pos, seed);
			}
		}
		level.removeBlock(pos, false);
	}

	@SuppressWarnings("null")
	@Override
	protected BlockState rotate(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("null")
	@Override
	protected BlockState mirror(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Mirror mirror) {
		return rotate(state, mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(@SuppressWarnings("null") StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
