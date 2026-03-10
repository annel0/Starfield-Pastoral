package com.stardew.craft.combat.buff;

/**
 * 战斗相关Buff类型
 * 
 * 这里只定义影响战斗的buff，其他buff（如钓鱼、采集等）
 * 应该在对应的系统中定义
 */
public enum CombatBuffType {
    // 攻击相关
    ATTACK("攻击", true),         // 增加攻击力
    CRITICAL("暴击", true),       // 增加暴击率
    
    // 防御相关
    DEFENSE("防御", true),        // 增加防御力
    IMMUNITY("免疫", true),       // 增加免疫值
    
    // 移动相关
    SPEED("速度", true),          // 增加移动速度
    
    // 战士技能buff
    WARRIOR_ENERGY("战士活力", true),     // 战士技能 - 攻击时恢复体力
    
    // 侦察技能buff
    ACROBAT_COOLDOWN("杂技师", true),     // 特殊移动技能冷却减少
    
    // 特殊食物buff
    ROCK_CANDY("岩石糖果", true),          // +250最大体力
    MONSTER_MUSK("怪物麝香", false);        // 增加怪物遭遇率
    
    private final String displayName;
    private final boolean isBeneficial;  // 是否有益buff
    
    CombatBuffType(String displayName, boolean isBeneficial) {
        this.displayName = displayName;
        this.isBeneficial = isBeneficial;
    }
    
    public String getDisplayName() { return displayName; }
    public boolean isBeneficial() { return isBeneficial; }
}
