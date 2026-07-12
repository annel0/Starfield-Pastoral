package com.stardew.craft.combat.debuff;

/**
 * Debuff类型枚举
 * 参考星露谷物语中怪物可以施加的负面效果
 */
public enum DebuffType {
    // Negative effects
    SLIMED("Слизь", 4000, true),              // slime residue - slows movement
    JINXED("Проклятие", 8000, true),              // curse - lowers defense
    WEAKNESS("Слабость", 8000, true),            // weakness - lowers attack
    DARKNESS("Тьма", 6000, true),            // darkness - lowers accuracy
    FROZEN("Заморозка", 2000, false),             // frozen - unable to move
    BURNING("Горение", 6000, true),             // burning - damage over time
    NAUSEATED("Тошнота", 5000, true),           // nausea - unable to restore stamina
    STUNNED("Оглушение", 1500, false);            // stunned - unable to act
    
    private final String displayName;
    private final int defaultDuration;  // 默认持续时间（毫秒）
    private final boolean canReduce;    // 是否可以通过免疫降低持续时间
    
    DebuffType(String displayName, int defaultDuration, boolean canReduce) {
        this.displayName = displayName;
        this.defaultDuration = defaultDuration;
        this.canReduce = canReduce;
    }
    
    public String getDisplayName() { return displayName; }
    public int getDefaultDuration() { return defaultDuration; }
    public boolean canReduce() { return canReduce; }
}
