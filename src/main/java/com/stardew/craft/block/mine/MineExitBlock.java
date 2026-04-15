package com.stardew.craft.block.mine;

import com.stardew.craft.block.shape.ModelVoxelShapeCache;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 矿井出口方块 - 金色梯子
 * 
 * 特性：
 * - 必须依附在其他方块上（像原版梯子）
 * - 有方向性（FACING属性）
 * - 右键打开GUI选择传送目标
 * - 碰撞箱与原版梯子相同
 */
public class MineExitBlock extends Block {
    
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape[] SHAPES = ModelVoxelShapeCache.horizontalShapes("stardewcraft:block/mine_exit", Direction.NORTH);

    @SuppressWarnings("null")
    public MineExitBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(@SuppressWarnings("null") StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @SuppressWarnings("null")
    @Override
    public BlockState getStateForPlacement(@SuppressWarnings("null") BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        for (Direction dir : context.getNearestLookingDirections()) {
            if (!dir.getAxis().isHorizontal()) {
                continue;
            }
            Direction facing = dir.getOpposite(); // 朝向玩家
            @SuppressWarnings("null")
            BlockState state = this.defaultBlockState().setValue(FACING, facing);
            if (state.canSurvive(context.getLevel(), pos)) {
                return state;
            }
        }
        return null;
    }
    
    /**
     * 检查方块是否可以存在（必须依附在实体方块上）
     */
    @SuppressWarnings("null")
    @Override
    public boolean canSurvive(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") net.minecraft.world.level.LevelReader level, @SuppressWarnings("null") BlockPos pos) {
        @SuppressWarnings("null")
        Direction facing = state.getValue(FACING);
        @SuppressWarnings("null")
        BlockPos attachPos = pos.relative(facing.getOpposite());
        @SuppressWarnings("null")
        BlockState attachState = level.getBlockState(attachPos);
        // 必须依附在完整实体方块上（像梯子一样）
        return attachState.isFaceSturdy(level, attachPos, facing.getOpposite());
    }

    @SuppressWarnings("null")
    @Override
    public VoxelShape getShape(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") BlockGetter level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") CollisionContext context) {
        return SHAPES[ModelVoxelShapeCache.horizontalIndex(state.getValue(FACING))];
    }

    @SuppressWarnings("null")
    @Override
    public BlockState rotate(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("null")
    @Override
    public BlockState mirror(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    /**
     * 检查方块是否可以存在（需要依附在其他方块上）
     */
    @Override
    public BlockState updateShape(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Direction facing, @SuppressWarnings("null") BlockState facingState, 
                                   @SuppressWarnings("null")    LevelAccessor level, @SuppressWarnings("null")    BlockPos currentPos, @SuppressWarnings("null")    BlockPos facingPos) {
        // 依附方块被破坏时掉落
        return this.canSurvive(state, level, currentPos)
            ? state
            : net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
    }

    /**
     * 右键交互 — 映射自 SDV MineShaft.checkAction case 115 (梯子)：
     * createQuestionDialogue(" ", { "Leave", "Do nothing" }, "ExitMine");
     */
    @SuppressWarnings("null")
    @Override
    protected InteractionResult useWithoutItem(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, 
                                               @SuppressWarnings("null")    Player player, @SuppressWarnings("null")    BlockHitResult hitResult) {
        if (level.dimension() != com.stardew.craft.core.ModMiningDimensions.STARDEW_MINING) {
            return InteractionResult.FAIL;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            com.stardew.craft.mining.MiningPlayerData playerData = 
                com.stardew.craft.mining.MiningDataManager.getPlayerData(serverPlayer);
            int currentFloor = playerData != null ? playerData.getCurrentFloor() : 0;
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                serverPlayer,
                new com.stardew.craft.network.payload.OpenMineExitDialogPayload(currentFloor)
            );
        }

        return InteractionResult.CONSUME;
    }
}
