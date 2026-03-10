package com.stardew.craft.block.utility;

import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.shape.ModelVoxelShapeCache;
import com.stardew.craft.blockentity.UtilityDropHelper;
import com.stardew.craft.blockentity.InsertResult;
import com.stardew.craft.blockentity.KegBlockEntity;
import com.stardew.craft.blockentity.ModBlockEntities;
import com.stardew.craft.blockentity.MissingItemRequirement;
import com.stardew.craft.network.MissingItemHudMessagePacket;
import com.stardew.craft.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 小桶（Keg）- 将作物加工为果酒/果汁/饮品
 */
public class KegBlock extends Block implements EntityBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty WORKING = BooleanProperty.create("working");

	private static final VoxelShape[] SHAPES = ModelVoxelShapeCache.horizontalShapes("stardewcraft:block/utility/keg", Direction.SOUTH);

	@SuppressWarnings("null")
	public KegBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any()
			.setValue(FACING, Direction.NORTH)
			.setValue(WORKING, false));
	}

	@Override
	protected void createBlockStateDefinition(@SuppressWarnings("null") StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WORKING);
	}

	@Override
	public RenderShape getRenderShape(@SuppressWarnings("null") BlockState state) {
		// 用 BE renderer 渲染本体 + 工作态浮动 + 就绪气泡
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@SuppressWarnings("null")
	@Override
	protected List<ItemStack> getDrops(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") LootParams.Builder params) {
		return List.of(new ItemStack(ModBlocks.KEG.get()));
	}

	@SuppressWarnings("null")
	@Override
	public VoxelShape getShape(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockGetter level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") CollisionContext context) {
		return SHAPES[ModelVoxelShapeCache.horizontalIndex(state.getValue(FACING))];
	}

	@SuppressWarnings("null")
	@Override
	public VoxelShape getCollisionShape(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockGetter level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") CollisionContext context) {
		return SHAPES[ModelVoxelShapeCache.horizontalIndex(state.getValue(FACING))];
	}

	@Override
	@Nullable
	public BlockEntity newBlockEntity(@SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState state) {
		return new KegBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockEntityType<T> type) {
		if (type != ModBlockEntities.KEG.get()) {
			return null;
		}
		if (level.isClientSide) {
			return (lvl, pos, st, be) -> KegBlockEntity.clientTick(lvl, pos, st, (KegBlockEntity) be);
		}
		return (lvl, pos, st, be) -> KegBlockEntity.serverTick(lvl, pos, st, (KegBlockEntity) be);
	}

	@SuppressWarnings("null")
	@Override
	public BlockState getStateForPlacement(@SuppressWarnings("null") BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
	}

	@SuppressWarnings("null")
	@Override
	public BlockState rotate(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("null")
	@Override
	public BlockState mirror(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@SuppressWarnings("null")
	@Override
	protected ItemInteractionResult useItemOn(@SuppressWarnings("null") ItemStack stack, @SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") Player player, @SuppressWarnings("null") InteractionHand hand, @SuppressWarnings("null") BlockHitResult hit) {
		if (level.isClientSide) {
			return ItemInteractionResult.sidedSuccess(true);
		}

		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof KegBlockEntity keg)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		// 就绪优先收获（即使手里有物品）
		if (keg.isReady()) {
			ItemStack product = keg.harvestOne();
			if (!product.isEmpty()) {
				if (!player.addItem(product)) {
					player.drop(product, false);
				}
				level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6f, 1.0f);
				return ItemInteractionResult.sidedSuccess(false);
			}
		}

		// 放入原料
		if (!stack.isEmpty()) {
			InsertResult result = keg.tryInsertWithResult(stack, player);
			if (result.inserted()) {
				if (state.hasProperty(WORKING) && !state.getValue(WORKING)) {
					level.setBlock(pos, state.setValue(WORKING, true), 3);
				}
				level.playSound(null, pos, ModSounds.SHIP.get(), SoundSource.BLOCKS, 0.9f, 1.0f);
				level.playSound(null, pos, ModSounds.BUBBLES.get(), SoundSource.BLOCKS, 0.7f, 1.0f);
				return ItemInteractionResult.sidedSuccess(false);
			}
			MissingItemRequirement missing = result.missingRequirement();
			if (missing != null) {
				MissingItemHudMessagePacket.sendTo(player, missing.item(), missing.requiredCount());
				return ItemInteractionResult.sidedSuccess(false);
			}
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@SuppressWarnings("null")
	@Override
	public void onRemove(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && !isMoving) {
			UtilityDropHelper.dropAutomationContents(level, pos);
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}

	@SuppressWarnings("null")
	@Override
	protected InteractionResult useWithoutItem(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") Player player, @SuppressWarnings("null") BlockHitResult hit) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof KegBlockEntity keg)) {
			return InteractionResult.PASS;
		}

		if (!keg.isReady()) {
			return InteractionResult.PASS;
		}

		ItemStack product = keg.harvestOne();
		if (product.isEmpty()) {
			return InteractionResult.PASS;
		}
		if (!player.addItem(product)) {
			player.drop(product, false);
		}
		level.playSound(null, pos, net.minecraft.sounds.SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.6f, 1.0f);
		return InteractionResult.CONSUME;
	}
}
