package com.stardew.craft.combat.debuff;

import java.util.Random;

/**
 * 免疫系统
 * 
 * 星露谷物语免疫机制：
 * - 基础免疫值来自装备（主要是戒指）
 * - 免疫值决定抵抗debuff的概率
 * - 公式: 免疫概率 = 免疫值 × 10%，上限100%
 * - 例如: 免疫值5 = 50%概率抵抗debuff
 * - 免疫值10 = 100%完全免疫
 * 
 * 免疫还可以减少已有debuff的持续时间：
 * - 持续时间减少 = 免疫值 × 10%
 */
public class ImmunitySystem {
    
    private static final Random RANDOM = new Random();
    
    // 每点免疫提供的抵抗概率
    private static final float IMMUNITY_RESIST_PER_POINT = 0.10f;  // 10%
    
    // 最大免疫值
    private static final int MAX_IMMUNITY = 10;
    
    /**
     * 计算是否抵抗debuff
     * 
     * @param immunity 玩家的免疫值
     * @param debuffType debuff类型
     * @return true如果成功抵抗
     */
    public static boolean tryResistDebuff(int immunity, DebuffType debuffType) {
        // 某些debuff无法通过免疫减少（如冻结、眩晕）
        if (!debuffType.canReduce()) {
            return false;
        }
        
        float resistChance = calculateResistChance(immunity);
        return RANDOM.nextFloat() < resistChance;
    }
    
    /**
     * 计算抵抗概率
     * 
     * @param immunity 免疫值
     * @return 抵抗概率 (0.0 - 1.0)
     */
    public static float calculateResistChance(int immunity) {
        int clampedImmunity = Math.min(Math.max(immunity, 0), MAX_IMMUNITY);
        return clampedImmunity * IMMUNITY_RESIST_PER_POINT;
    }
    
    /**
     * 计算debuff持续时间（考虑免疫减少）
     * 
     * @param baseDuration 基础持续时间（毫秒）
     * @param immunity 免疫值
     * @param debuffType debuff类型
     * @return 实际持续时间
     */
    public static int calculateDuration(int baseDuration, int immunity, DebuffType debuffType) {
        // 某些debuff持续时间不受免疫影响
        if (!debuffType.canReduce()) {
            return baseDuration;
        }
        
        float reduction = calculateDurationReduction(immunity);
        int reducedDuration = (int)(baseDuration * (1.0f - reduction));
        
        // 最低持续时间为0.5秒
        return Math.max(reducedDuration, 500);
    }
    
    /**
     * 计算持续时间减少比例
     * 
     * @param immunity 免疫值
     * @return 减少比例 (0.0 - 1.0)
     */
    public static float calculateDurationReduction(int immunity) {
        int clampedImmunity = Math.min(Math.max(immunity, 0), MAX_IMMUNITY);
        return clampedImmunity * IMMUNITY_RESIST_PER_POINT;
    }
    
    /**
     * 获取最大免疫值
     */
    public static int getMaxImmunity() {
        return MAX_IMMUNITY;
    }
}
