package com.stardew.craft.combat.equipment;

/**
 * 装备属性计算器
 * 
 * 星露谷物语装备系统：
 * - 戒指：提供各种属性加成（防御、免疫、攻击等）
 * - 靴子：提供防御和免疫
 * - 玩家可以同时装备2个戒指和1双靴子
 * 
 * 主要防御来源：
 * - 坚韧戒指 (Sturdy Ring): +1防御
 * - 保护戒指 (Ring of Yoba): 随机抵消伤害
 * - 各种靴子: +1~+4防御
 * 
 * 主要免疫来源：
 * - 抗性戒指 (Immunity Band): +1~+4免疫
 * - 各种靴子: +1~+3免疫
 */
public class EquipmentStats {
    
    private int defense = 0;          // 防御值
    private int immunity = 0;         // 免疫值
    private int attack = 0;           // 攻击加成
    private float critChance = 0;     // 暴击率加成
    private float critPower = 0;      // 暴击伤害加成
    private int magneticRadius = 0;   // 磁力半径
    private float knockbackBonus = 0; // 击退加成
    private int lightRadius = 0;      // 光照半径
    private float luck = 0;           // 幸运加成
    
    // 特殊效果标记
    private boolean hasYobaProtection = false;  // 约巴之戒保护
    private boolean hasThorns = false;          // 荆棘戒指反伤
    private boolean hasVampiric = false;        // 吸血效果
    private boolean hasGlowRing = false;        // 发光戒指
    private boolean hasMagnetRing = false;      // 磁力戒指
    
    private EquipmentStats() {}
    
    // Getters
    public int getDefense() { return defense; }
    public int getImmunity() { return immunity; }
    public int getAttack() { return attack; }
    public float getCritChance() { return critChance; }
    public float getCritPower() { return critPower; }
    public int getMagneticRadius() { return magneticRadius; }
    public float getKnockbackBonus() { return knockbackBonus; }
    public int getLightRadius() { return lightRadius; }
    public float getLuck() { return luck; }
    
    public boolean hasYobaProtection() { return hasYobaProtection; }
    public boolean hasThorns() { return hasThorns; }
    public boolean hasVampiric() { return hasVampiric; }
    public boolean hasGlowRing() { return hasGlowRing; }
    public boolean hasMagnetRing() { return hasMagnetRing; }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final EquipmentStats stats = new EquipmentStats();
        
        public Builder defense(int val) { stats.defense = val; return this; }
        public Builder immunity(int val) { stats.immunity = val; return this; }
        public Builder attack(int val) { stats.attack = val; return this; }
        public Builder critChance(float val) { stats.critChance = val; return this; }
        public Builder critPower(float val) { stats.critPower = val; return this; }
        public Builder magneticRadius(int val) { stats.magneticRadius = val; return this; }
        public Builder knockbackBonus(float val) { stats.knockbackBonus = val; return this; }
        public Builder lightRadius(int val) { stats.lightRadius = val; return this; }
        public Builder luck(float val) { stats.luck = val; return this; }
        
        public Builder yobaProtection(boolean val) { stats.hasYobaProtection = val; return this; }
        public Builder thorns(boolean val) { stats.hasThorns = val; return this; }
        public Builder vampiric(boolean val) { stats.hasVampiric = val; return this; }
        public Builder glowRing(boolean val) { stats.hasGlowRing = val; return this; }
        public Builder magnetRing(boolean val) { stats.hasMagnetRing = val; return this; }
        
        /**
         * 合并另一个装备的属性
         */
        public Builder merge(EquipmentStats other) {
            stats.defense += other.defense;
            stats.immunity += other.immunity;
            stats.attack += other.attack;
            stats.critChance += other.critChance;
            stats.critPower += other.critPower;
            stats.magneticRadius += other.magneticRadius;
            stats.knockbackBonus += other.knockbackBonus;
            stats.lightRadius = Math.max(stats.lightRadius, other.lightRadius);
            stats.luck += other.luck;
            
            stats.hasYobaProtection |= other.hasYobaProtection;
            stats.hasThorns |= other.hasThorns;
            stats.hasVampiric |= other.hasVampiric;
            stats.hasGlowRing |= other.hasGlowRing;
            stats.hasMagnetRing |= other.hasMagnetRing;
            
            return this;
        }
        
        public EquipmentStats build() {
            return stats;
        }
    }
    
    /**
     * 合并多个装备属性
     */
    public static EquipmentStats merge(EquipmentStats... equipments) {
        Builder builder = builder();
        for (EquipmentStats equipment : equipments) {
            if (equipment != null) {
                builder.merge(equipment);
            }
        }
        return builder.build();
    }
}
