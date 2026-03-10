package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.mining.LadderProbabilityCalculator;
import com.stardew.craft.mining.MineFloorData;
import com.stardew.craft.mining.MineFloorDataManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * 矿井方块破坏事件处理器
 * 
 * 实现星露谷式的梯子生成机制：
 * - 玩家挖掉主石头时触发
 * - 按概率判断是否生成梯子
 * - 梯子生成在玩家脚下
 * - 显示提示消息
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public class MiningBlockBreakHandler {
    
    /**
     * 检查方块是否是可计数的主石头
     */
    private static boolean isCountableStone(Block block) {
        // 检查是否是主石头（6种）
        return block == ModBlocks.EARTH_SHALE.get() ||
               block == ModBlocks.FROST_GNEISS.get() ||
               block == ModBlocks.LAVA_BASALT.get() ||
               block == ModBlocks.BANDED_MARBLE.get() ||
               block == ModBlocks.LIMESTONE.get() ||
               block == ModBlocks.MOSSY_SANDSTONE.get() ||
               block == ModBlocks.CRACKED_SLATE.get() ||
               block == ModBlocks.SCORIA.get() ||
               block == ModBlocks.SALT_ROCK.get();
    }
    
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // 检查是否在矿井维度
        if (!isMiningDimension(serverLevel)) {
            return;
        }
        
        // 检查是否是玩家破坏
        if (!(event.getPlayer() instanceof ServerPlayer player)) {
            return;
        }
        
        BlockPos pos = event.getPos();
        BlockState state = event.getState();
        handleStoneBreak(serverLevel, player, pos, state);
    }

    /**
     * 用于可取消破坏的兼容入口（例如自定义挖掘逻辑）。
     */
    @SuppressWarnings("null")
    public static void handleStoneBreak(ServerLevel serverLevel, ServerPlayer player, BlockPos pos, BlockState state) {
        Block block = state.getBlock();

        // 检查是否是可计数的主石头
        if (!isCountableStone(block)) {
            return;
        }

        // 获取楼层数据
        int floorNumber = getFloorNumber(pos);
        MineFloorDataManager manager = MineFloorDataManager.get(serverLevel);
        MineFloorData floorData = manager.getFloorData(floorNumber);

        if (floorData == null) {
            // 楼层数据不存在，不处理（可能是玩家还未正式进入楼层）
            return;
        }

        // 减少stonesLeft计数
        floorData.decrementStone();
        manager.setFloorData(floorNumber, floorData);

        // 判断是否生成梯子
        boolean shouldGenerate = LadderProbabilityCalculator.shouldGenerateLadder(
            floorData.getStonesLeft(),
            player,
            floorData,
            serverLevel.getRandom()
        );

        if (shouldGenerate) {
            // 生成梯子在玩家脚下
            BlockPos playerPos = player.blockPosition();
            BlockPos ladderPos = playerPos.below(); // 玩家脚下一格

            // 检查位置是否可以放置梯子
            @SuppressWarnings("null")
            BlockState belowState = serverLevel.getBlockState(ladderPos);
            if (belowState.isAir() || belowState.canBeReplaced()) {
                // 放置梯子
                serverLevel.setBlock(ladderPos, ModBlocks.MINE_LADDER.get().defaultBlockState(), 3);

                // 标记本层已找到梯子
                floorData.setLadderFound(true);
                floorData.setLadderPos(ladderPos);
                manager.setFloorData(floorNumber, floorData);

                // 发送消息给玩家
                player.sendSystemMessage(Component.translatable("message.stardew_craft.ladder_found"));

                StardewCraft.LOGGER.info("玩家 {} 在第 {} 层发现了梯子！位置: {}",
                    player.getName().getString(), floorNumber, ladderPos);
            }
        }
    }
    
    /**
     * 检查是否在矿井维度
     * TODO: 实现正确的维度检查
     */
    private static boolean isMiningDimension(ServerLevel level) {
        return level.dimension() == ModMiningDimensions.STARDEW_MINING;
    }
    
    /**
     * 从坐标计算楼层编号
     * 每层中心在 (0, 64, floor * 100)
     */
    private static int getFloorNumber(BlockPos pos) {
        return Math.round(pos.getZ() / 100.0f);
    }
}
