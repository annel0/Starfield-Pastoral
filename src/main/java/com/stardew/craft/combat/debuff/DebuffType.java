package com.stardew.craft.combat.debuff;

/**
 * Debuff类型枚举
 * 参考星露谷物语中怪物可以施加的负面效果
 */
public enum DebuffType {
    // 减益效果
    SLIMED("粘液", 4000, true),              // 史莱姆粘液 - 减速
    JINXED("诅咒", 8000, true),              // 诅咒 - 降低防御
    WEAKNESS("虚弱", 8000, true),            // 虚弱 - 降低攻击
    DARKNESS("黑暗", 6000, true),            // 黑暗 - 降低命中
    FROZEN("冻结", 2000, false),             // 冻结 - 无法移动
    BURNING("燃烧", 6000, true),             // 燃烧 - 持续伤害
    NAUSEATED("恶心", 5000, true),           // 恶心 - 无法恢复体力
    STUNNED("眩晕", 1500, false);            // 眩晕 - 无法行动
    
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
