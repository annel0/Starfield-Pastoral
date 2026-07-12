package com.stardew.craft.combat.buff;

/**
 * 战斗相关Buff类型
 * 
 * 这里只定义影响战斗的buff，其他buff（如钓鱼、采集等）
 * 应该在对应的系统中定义
 */
public enum CombatBuffType {
    // Attack-related
    ATTACK("Атака", true),         // increases attack power
    CRITICAL("Крит. удар", true),       // increases critical hit chance

    // Defense-related
    DEFENSE("Защита", true),        // increases defense
    IMMUNITY("Иммунитет", true),       // increases immunity value

    // Movement-related
    SPEED("Скорость", true),          // increases movement speed

    // Warrior skill buff
    WARRIOR_ENERGY("Энергия воина", true),     // Warrior skill - restores stamina on attack

    // Scout skill buff
    ACROBAT_COOLDOWN("Акробат", true),     // reduces special movement skill cooldown

    // Special food buffs
    ROCK_CANDY("Леденец", true),          // +250 max stamina
    MONSTER_MUSK("Мускус монстра", false);        // increases monster encounter rate
    
    private final String displayName;
    private final boolean isBeneficial;  // 是否有益buff
    
    CombatBuffType(String displayName, boolean isBeneficial) {
        this.displayName = displayName;
        this.isBeneficial = isBeneficial;
    }
    
    public String getDisplayName() { return displayName; }
    public boolean isBeneficial() { return isBeneficial; }
}
