package com.stardew.craft.mining;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

/**
 * 梯子生成概率计算器
 * 
 * 按照星露谷原版公式计算梯子出现概率：
 * p = 0.02 + 1/max(1, stonesLeft) + luckLevel/100 + dailyLuck/5
 * 
 * 额外加成：
 * - 如果本层敌人数为0：+0.04
 * - 特殊buff：×1.25
 * - 如果stonesLeft==0：必出
 */
public class LadderProbabilityCalculator {
    
    /**
     * 计算梯子生成概率
     * 
     * @param stonesLeft 本层剩余石头数
     * @param luckLevel 玩家幸运等级（暂未实现，默认0）
     * @param dailyLuck 每日幸运值（-0.1 到 0.1）
     * @param enemyCount 本层敌人数量
     * @param hasDwarfBuff 是否有矮人雕像buff
     * @return 生成概率（0.0-1.0）
     */
    public static double calculateProbability(
        int stonesLeft,
        int luckLevel,
        double dailyLuck,
        int enemyCount,
        boolean hasDwarfBuff
    ) {
        // 如果没有石头了，必出
        if (stonesLeft <= 0) {
            return 1.0;
        }
        
    // 基础概率：0.012 + 0.6/max(1, stonesLeft) + luckLevel/100 + dailyLuck/5
    double baseProbability = 0.012;
    baseProbability += 0.6 / Math.max(1, stonesLeft);
        baseProbability += luckLevel / 100.0;
        baseProbability += dailyLuck / 5.0;
        
        // 如果本层没有敌人了，额外+0.02
        if (enemyCount == 0) {
            baseProbability += 0.02;
        }
        
        // 如果有矮人雕像buff，概率×1.15
        if (hasDwarfBuff) {
            baseProbability *= 1.15;
        }
        
        // 确保概率在0-1之间
        return Math.max(0.0, Math.min(1.0, baseProbability));
    }
    
    /**
     * 判断是否生成梯子
     * 
     * @param stonesLeft 本层剩余石头数
     * @param player 玩家（用于获取幸运值）
     * @param floorData 楼层数据
     * @param random 随机源
     * @return 是否应该生成梯子
     */
    public static boolean shouldGenerateLadder(
        int stonesLeft,
        ServerPlayer player,
        MineFloorData floorData,
        RandomSource random
    ) {
        // 如果本层已经有梯子了，不再生成
        if (floorData.hasLadderFound()) {
            return false;
        }
        
        // 获取玩家数据
        int luckLevel = 0; // TODO: 从玩家数据获取幸运等级
        double dailyLuck = getDailyLuck(player); // TODO: 从玩家数据获取每日幸运
        boolean hasDwarfBuff = false; // TODO: 检查玩家是否有矮人雕像buff
        
        // 计算概率
        double probability = calculateProbability(
            stonesLeft,
            luckLevel,
            dailyLuck,
            floorData.getEnemyCount(),
            hasDwarfBuff
        );
        
        // 随机判断
        return random.nextDouble() < probability;
    }
    
    /**
     * 获取玩家的每日幸运值
     * TODO: 从PlayerStardewDataAPI获取
     */
    private static double getDailyLuck(ServerPlayer player) {
        // 暂时返回0，后续对接PlayerStardewDataAPI
        return 0.0;
    }
}
