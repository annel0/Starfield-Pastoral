package com.stardew.craft.combat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

/**
 * 维度伤害映射系统
 * 
 * 核心设计：
 * - 在星露谷维度内，使用原版星露谷数值（100HP、武器伤害10-100+等）
 * - 在星露谷维度外（主世界等），伤害按5:1比例映射到MC系统
 * 
 * 映射比例（星露谷 → Minecraft）：
 * - 伤害: 5:1 （星露谷25伤害 = MC 5伤害 = 2.5颗心）
 * - 生命: 5:1 （星露谷100HP = MC 20HP = 10颗心）
 * - 防御: 直接映射 （星露谷防御值 ≈ MC护甲点）
 */
public class DimensionDamageMapper {
    
    // 星露谷维度的资源路径
    private static final ResourceLocation STARDEW_VALLEY_DIMENSION =
        ResourceLocation.fromNamespaceAndPath("stardewcraft", "stardew_valley");
    private static final ResourceLocation STARDEW_MINING_DIMENSION =
        ResourceLocation.fromNamespaceAndPath("stardewcraft", "stardew_mining");
    
    // 伤害映射比例
    private static final float DAMAGE_RATIO = 5.0f;
    
    // 生命映射比例
    private static final float HEALTH_RATIO = 5.0f;
    
    /**
     * 检查玩家是否在星露谷维度
     */
    public static boolean isInStardewDimension(ServerPlayer player) {
        ResourceLocation id = player.level().dimension().location();
        return STARDEW_VALLEY_DIMENSION.equals(id) || STARDEW_MINING_DIMENSION.equals(id);
    }
    
    /**
     * 检查实体是否在星露谷维度
     */
    public static boolean isInStardewDimension(LivingEntity entity) {
        ResourceLocation id = entity.level().dimension().location();
        return STARDEW_VALLEY_DIMENSION.equals(id) || STARDEW_MINING_DIMENSION.equals(id);
    }
    
    /**
     * 映射伤害值
     * 
     * @param stardewDamage 星露谷原版伤害值
     * @param isInStardewDimension 是否在星露谷维度
     * @return 实际应用的伤害值
     */
    public static float mapDamage(float stardewDamage, boolean isInStardewDimension) {
        if (isInStardewDimension) {
            // 在星露谷维度，使用原版数值
            return stardewDamage;
        } else {
            // 在其他维度，按比例缩小
            return stardewDamage / DAMAGE_RATIO;
        }
    }
    
    /**
     * 映射生命值
     * 
     * @param stardewHealth 星露谷原版生命值
     * @param isInStardewDimension 是否在星露谷维度
     * @return 实际应用的生命值
     */
    public static float mapHealth(float stardewHealth, boolean isInStardewDimension) {
        if (isInStardewDimension) {
            return stardewHealth;
        } else {
            return stardewHealth / HEALTH_RATIO;
        }
    }
    
    /**
     * 反向映射伤害（从MC伤害转换为星露谷伤害）
     * 用于计算原版MC伤害对星露谷玩家的影响
     * 
     * @param mcDamage Minecraft伤害值
     * @param isInStardewDimension 是否在星露谷维度
     * @return 转换后的星露谷伤害值
     */
    public static float reverseMapDamage(float mcDamage, boolean isInStardewDimension) {
        if (isInStardewDimension) {
            // 在星露谷维度，MC伤害转换为星露谷数值
            return mcDamage * DAMAGE_RATIO;
        } else {
            // 在其他维度，直接使用MC伤害
            return mcDamage;
        }
    }
    
    /**
     * 获取伤害映射比例
     */
    public static float getDamageRatio() {
        return DAMAGE_RATIO;
    }
    
    /**
     * 获取生命映射比例
     */
    public static float getHealthRatio() {
        return HEALTH_RATIO;
    }
    
    /**
     * 获取星露谷维度ID
     */
    public static ResourceLocation getStardewDimensionId() {
        return STARDEW_VALLEY_DIMENSION;
    }

    /**
     * 获取所有星露谷维度ID
     */
    public static ResourceLocation[] getStardewDimensionIds() {
        return new ResourceLocation[] { STARDEW_VALLEY_DIMENSION, STARDEW_MINING_DIMENSION };
    }
    
    // ==================== 伤害类型处理 ====================
    
    /**
     * 伤害模式
     */
    public enum DamageMode {
        /** 星露谷模式：使用100HP系统，高数值伤害 */
        STARDEW,
        /** Minecraft模式：使用20HP系统，低数值伤害 */
        MINECRAFT
    }
    
    /**
     * 获取当前伤害模式
     */
    public static DamageMode getDamageMode(LivingEntity entity) {
        if (isInStardewDimension(entity)) {
            return DamageMode.STARDEW;
        } else {
            return DamageMode.MINECRAFT;
        }
    }
}
