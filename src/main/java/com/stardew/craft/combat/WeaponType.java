package com.stardew.craft.combat;

/**
 * 武器类型枚举
 */
public enum WeaponType {
    SWORD(0, "sword", "stardewcraft.type.weapon.sword", 1.6f, 3.0f),           // 剑 - 均衡型，右键格挡
    DAGGER(1, "dagger", "stardewcraft.type.weapon.dagger", 2.5f, 2.0f),       // 匕首 - 高暴击，右键连刺
    CLUB(2, "club", "stardewcraft.type.weapon.club", 0.75f, 4.0f),            // 锤子 - 高伤害，右键砸地AOE
    SLINGSHOT(3, "slingshot", "stardewcraft.type.weapon.slingshot", 1.0f, 3.0f); // 弹弓 - 远程
    
    private final int id;
    private final String name;
    private final String translationKey;
    private final float attackSpeed;
    private final float attackRange;
    
    WeaponType(int id, String name, String translationKey, float attackSpeed, float attackRange) {
        this.id = id;
        this.name = name;
        this.translationKey = translationKey;
        this.attackSpeed = attackSpeed;
        this.attackRange = attackRange;
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDisplayName() { return translationKey; }
    public String getTranslationKey() { return translationKey; }
    public float getAttackSpeed() { return attackSpeed; }
    public float getAttackRange() { return attackRange; }
    
    public static WeaponType fromId(int id) {
        for (WeaponType type : values()) {
            if (type.id == id) return type;
        }
        return SWORD;
    }
    
    public static WeaponType fromName(String name) {
        for (WeaponType type : values()) {
            if (type.name.equals(name)) return type;
        }
        return SWORD;
    }
}
