package com.stardew.craft.item.weapon;

import com.stardew.craft.combat.WeaponType;

/**
 * 武器数据类
 * 包含所有武器属性和技能信息
 */
public class WeaponData {
    
    private final String id;
    private final String name;
    private final WeaponType weaponType;
    private final int level;
    private final int damageMin;
    private final int damageMax;
    private final double critChance;
    private final double critPower;
    private final int speed;
    private final int defense;
    private final double weight;
    private final WeaponSkillData skill1;
    private final WeaponSkillData skill2;
    private final String loreKey;
    
    private WeaponData(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.weaponType = builder.weaponType;
        this.level = builder.level;
        this.damageMin = builder.damageMin;
        this.damageMax = builder.damageMax;
        this.critChance = builder.critChance;
        this.critPower = builder.critPower;
        this.speed = builder.speed;
        this.defense = builder.defense;
        this.weight = builder.weight;
        this.skill1 = builder.skill1;
        this.skill2 = builder.skill2;
        this.loreKey = builder.loreKey;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public WeaponType getWeaponType() { return weaponType; }
    public int getLevel() { return level; }
    public int getDamageMin() { return damageMin; }
    public int getDamageMax() { return damageMax; }
    public double getCritChance() { return critChance; }
    public double getCritPower() { return critPower; }
    public int getSpeed() { return speed; }
    public int getDefense() { return defense; }
    public double getWeight() { return weight; }
    public WeaponSkillData getSkill1() { return skill1; }
    public WeaponSkillData getSkill2() { return skill2; }
    public String getLoreKey() { return loreKey; }
    
    /**
     * 获取武器稀有度
     */
    public WeaponRarity getRarity() {
        return WeaponRarity.fromLevel(level);
    }
    
    /**
     * 获取平均伤害
     */
    public double getAverageDamage() {
        return (damageMin + damageMax) / 2.0;
    }
    
    public static Builder builder(String id) {
        return new Builder(id);
    }
    
    public static class Builder {
        private final String id;
        private String name = "";
        private WeaponType weaponType = WeaponType.SWORD;
        private int level = 1;
        private int damageMin = 1;
        private int damageMax = 1;
        private double critChance = 0.02;
        private double critPower = 1.0;
        private int speed = 0;
        private int defense = 0;
        private double weight = 0;
        private WeaponSkillData skill1 = null;
        private WeaponSkillData skill2 = null;
        private String loreKey = "";
        
        public Builder(String id) {
            this.id = id;
        }
        
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        
        public Builder type(WeaponType type) {
            this.weaponType = type;
            // 匕首默认暴击率+1%
            if (type == WeaponType.DAGGER && this.critChance == 0.02) {
                this.critChance = 0.03;
            }
            return this;
        }
        
        public Builder level(int level) {
            this.level = level;
            return this;
        }
        
        public Builder damage(int min, int max) {
            this.damageMin = min;
            this.damageMax = max;
            return this;
        }
        
        public Builder critChance(double chance) {
            this.critChance = chance;
            return this;
        }
        
        public Builder critPower(double power) {
            this.critPower = power;
            return this;
        }
        
        public Builder speed(int speed) {
            this.speed = speed;
            return this;
        }
        
        public Builder defense(int defense) {
            this.defense = defense;
            return this;
        }
        
        public Builder weight(double weight) {
            this.weight = weight;
            return this;
        }
        
        public Builder skill1(WeaponSkillData skill) {
            this.skill1 = skill;
            return this;
        }
        
        public Builder skill2(WeaponSkillData skill) {
            this.skill2 = skill;
            return this;
        }
        
        public Builder loreKey(String loreKey) {
            this.loreKey = loreKey;
            return this;
        }
        
        public WeaponData build() {
            return new WeaponData(this);
        }
    }
}
