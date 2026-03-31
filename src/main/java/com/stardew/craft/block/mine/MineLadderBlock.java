package com.stardew.craft.block.mine;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.shape.ModelVoxelShapeCache;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.mining.MiningCoordinates;
import com.stardew.craft.mining.MiningDataManager;
import com.stardew.craft.mining.MiningPlayerData;
import com.stardew.craft.network.MiningFloorSyncPacket;
import com.stardew.craft.player.PlayerStardewDataAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
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
 * 矿井梯子方块 - 通往下一层的传送点
 * 
 * 功能：
 * - 右键交互传送到下一层
 * - 只能在矿井维度使用
 * - 不可破坏（硬度很高）
 * - 紫色粒子效果
 */
public class MineLadderBlock extends Block {
    
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape[] SHAPES = ModelVoxelShapeCache.horizontalShapes("stardewcraft:block/ladder", Direction.NORTH);

    @SuppressWarnings("null")
    public MineLadderBlock(Properties properties) {
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
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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

    /**
     * 禁止生存模式破坏矿井梯子
     */
    @SuppressWarnings("null")
    @Override
    public boolean onDestroyedByPlayer(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") Player player, boolean willHarvest, @SuppressWarnings("null") net.minecraft.world.level.material.FluidState fluid) {
        // 只有创造模式才能破坏
        if (!player.isCreative()) {
            player.displayClientMessage(net.minecraft.network.chat.Component.translatable("message.stardewcraft.cannot_break_ladder"), true);
            return false;
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    /**
     * 右键交互 - 传送到下一层
     */
    @SuppressWarnings("null")
    @Override
    protected InteractionResult useWithoutItem(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") Player player, @SuppressWarnings("null") BlockHitResult hitResult) {
        // 只在服务端处理
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        // 只在矿井维度有效
        if (level.dimension() != ModMiningDimensions.STARDEW_MINING) {
            return InteractionResult.PASS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        // 获取玩家矿井数据
        MiningPlayerData playerData = MiningDataManager.getPlayerData(serverPlayer);
        int currentFloor = playerData.getCurrentFloor();
        int nextFloor = currentFloor + 1;

        StardewCraft.LOGGER.info("[MINE] Player {} descending from floor {} to floor {}", 
            player.getName().getString(), currentFloor, nextFloor);

        // 生成下一层（如果还没生成或需要刷新）
        com.stardew.craft.mining.MineFloorGenerator.generateFloor((ServerLevel) level, nextFloor);

        // 更新层数
        playerData.setCurrentFloor(nextFloor);
        MiningDataManager.savePlayerData(serverPlayer, playerData);
        PlayerStardewDataAPI.applyStardewCraftingConditionUnlocks(serverPlayer);

        // 播放传送音效
        level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 1.0F);

        // 传送到下一层
        MiningCoordinates.teleportPlayerToFloor(serverPlayer, (ServerLevel) level, nextFloor);

        // 同步层数到客户端
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
            serverPlayer, 
            new MiningFloorSyncPacket(nextFloor)
        );

        return InteractionResult.SUCCESS;
    }

    /**
     * 粒子效果
     */
    @SuppressWarnings("null")
    @Override
    public void animateTick(@SuppressWarnings("null") BlockState state, @SuppressWarnings("null") Level level, @SuppressWarnings("null") BlockPos pos, @SuppressWarnings("null") RandomSource random) {
        super.animateTick(state, level, pos, random);
        
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 0.5;
        double centerZ = pos.getZ() + 0.5;
        
        // 传送门粒子 - 从边缘向中心
        for (int i = 0; i < 3; i++) {
            double x = centerX + (random.nextDouble() - 0.5) * 0.8;
            double y = centerY + random.nextDouble() * 0.6;
            double z = centerZ + (random.nextDouble() - 0.5) * 0.8;
            
            double vx = (centerX - x) * 0.5;
            double vy = -0.1;
            double vz = (centerZ - z) * 0.5;
            
            level.addParticle(ParticleTypes.PORTAL, x, y, z, vx, vy, vz);
        }
        
        // 附魔符文粒子
        if (random.nextInt(3) == 0) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 1.0 + random.nextDouble() * 1.0;
            double x = centerX + Math.cos(angle) * radius;
            double z = centerZ + Math.sin(angle) * radius;
            double y = centerY + random.nextDouble() * 1.5;
            
            double vx = centerX - x;
            double vy = centerY - y + 0.5;
            double vz = centerZ - z;
            
            level.addParticle(ParticleTypes.ENCHANT, x, y, z, vx, vy, vz);
        }
    }
}
