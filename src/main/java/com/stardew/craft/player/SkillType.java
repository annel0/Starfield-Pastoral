package com.stardew.craft.player;

/**
 * 技能类型枚举
 * 对应星露谷物语的5种技能
 */
public enum SkillType {
    FARMING(0, "farming", "农业"),
    FISHING(1, "fishing", "钓鱼"),
    FORAGING(2, "foraging", "觅食"),
    MINING(3, "mining", "采矿"),
    COMBAT(4, "combat", "战斗");
    
    private final int id;
    private final String name;
    private final String displayName;
    
    SkillType(int id, String name, String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根据ID获取技能类型
     */
    public static SkillType fromId(int id) {
        for (SkillType skill : values()) {
            if (skill.id == id) {
                return skill;
            }
        }
        return FARMING;
    }
    
    /**
     * 根据名称获取技能类型
     */
    public static SkillType fromName(String name) {
        for (SkillType skill : values()) {
            if (skill.name.equalsIgnoreCase(name)) {
                return skill;
            }
        }
        return null;
    }
}
