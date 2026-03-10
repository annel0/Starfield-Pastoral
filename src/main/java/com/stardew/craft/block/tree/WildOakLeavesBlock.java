package com.stardew.craft.block.tree;

import com.stardew.craft.item.ModItems;
import com.stardew.craft.manager.WildLeavesPlacedManager;
import com.stardew.craft.tree.WildTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class WildOakLeavesBlock extends Block {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private static final int SUPPORT_RADIUS = 6;
	// Stardew feel: leaves decay fast and are a meaningful seed source.
	private static final float SEED_DROP_CHANCE = 0.20f;
	private static final int DECAY_CHECK_DELAY_TICKS = 6;

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
		return true;
	}

	@Override
	public void randomTick(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") ServerLevel level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") net.minecraft.util.RandomSource random) {
		tryDecay(state, level, pos, random);
	}

	@Override
	protected void tick(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") ServerLevel level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") net.minecraft.util.RandomSource random) {
		tryDecay(state, level, pos, random);
	}

	@SuppressWarnings("null")
	@Override
	protected void onPlace(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			serverLevel.scheduleTick(pos, this, DECAY_CHECK_DELAY_TICKS);
		}
	}

	@SuppressWarnings("null")
	@Override
	protected void neighborChanged(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") Block block, @SuppressWarnings("null") BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			serverLevel.scheduleTick(pos, this, DECAY_CHECK_DELAY_TICKS);
		}
	}

	@SuppressWarnings("null")
	@Override
	public void setPlacedBy(@SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState state, @SuppressWarnings("null") @Nullable LivingEntity placer, @SuppressWarnings("null") ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			WildLeavesPlacedManager.get(serverLevel).markPlaced(pos);
		}
	}

	@SuppressWarnings("null")
	@Override
	protected void onRemove(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState newState, boolean movedByPiston) {
		if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
			if (newState.getBlock() != state.getBlock()) {
				WildLeavesPlacedManager.get(serverLevel).unmarkPlaced(pos);
			}
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	private static boolean hasNearbySupport(ServerLevel level, BlockPos pos, WildTrees.Def def) {
		Block trunk0 = def.trunk0().get();
		Block trunk1 = def.trunk1().get();
		Block branch1 = def.branch1().get();
		Block branch2 = def.branch2().get();
		for (int dx = -SUPPORT_RADIUS; dx <= SUPPORT_RADIUS; dx++) {
			for (int dy = -SUPPORT_RADIUS; dy <= SUPPORT_RADIUS; dy++) {
				for (int dz = -SUPPORT_RADIUS; dz <= SUPPORT_RADIUS; dz++) {
					int manhattan = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
					if (manhattan > SUPPORT_RADIUS) {
						continue;
					}
					BlockPos p = pos.offset(dx, dy, dz);
					@SuppressWarnings("null")
					Block b = level.getBlockState(p).getBlock();
					if (b == trunk1 || b == branch1 || b == branch2) {
						return true;
					}
					// Important: do NOT treat a lone stump (trunk0 only) as support,
					// otherwise leaves will never decay after felling (since trunk0 stays).
					if (b == trunk0 && !isLonelyStumpTrunk0(level, p, def)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressWarnings("null")
	private static boolean isLonelyStumpTrunk0(ServerLevel level, BlockPos pos, WildTrees.Def def) {
		if (level.getBlockState(pos).getBlock() != def.trunk0().get()) {
			return false;
		}
		@SuppressWarnings("null")
		Block above = level.getBlockState(pos.above()).getBlock();
		if (above == def.trunk1().get() || above == def.trunk0().get()) {
			return false;
		}
		for (Direction d : Direction.Plane.HORIZONTAL) {
			@SuppressWarnings("null")
			Block b = level.getBlockState(pos.relative(d)).getBlock();
			if (b == def.branch1().get() || b == def.branch2().get()) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("null")
	private static void tryDecay(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
		WildTrees.Def def = WildTrees.findByAnyPart(state);
		if (def == null) {
			return;
		}
		// Player-placed leaves do not decay.
		if (WildLeavesPlacedManager.get(level).isPlaced(pos)) {
			return;
		}
		// If there is no nearby trunk/branch support, decay like vanilla leaves.
		if (hasNearbySupport(level, pos, def)) {
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
