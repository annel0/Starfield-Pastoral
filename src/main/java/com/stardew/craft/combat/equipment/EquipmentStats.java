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
    private int magneticRadius = 0;   // 磁力半径（格）
    private float knockbackBonus = 0; // 击退加成
    private float luck = 0;           // 幸运加成
    private int lightLevel = 0;       // 光源等级 (SDV Glow Ring)
    
    // 特殊效果标记
    private boolean hasYobaProtection = false;  // 约巴之戒保护
    private boolean hasThorns = false;          // 荆棘戒指反伤
    private boolean hasSlimeCharmer = false;    // 史莱姆免伤
    private boolean hasSturdy = false;          // 减半负面持续时间
    private boolean hasBurglar = false;         // 怪物掉落×2
    private boolean hasProtection = false;      // 减伤保护
    private boolean hasPhoenix = false;         // 死亡复活
    
    private EquipmentStats() {}
    
    // Getters
    public int getDefense() { return defense; }
    public int getImmunity() { return immunity; }
    public int getAttack() { return attack; }
    public float getCritChance() { return critChance; }
    public float getCritPower() { return critPower; }
    public int getMagneticRadius() { return magneticRadius; }
    public float getKnockbackBonus() { return knockbackBonus; }
    public float getLuck() { return luck; }
    public int getLightLevel() { return lightLevel; }
    
    public boolean hasYobaProtection() { return hasYobaProtection; }
    public boolean hasThorns() { return hasThorns; }
    public boolean hasSlimeCharmer() { return hasSlimeCharmer; }
    public boolean hasSturdy() { return hasSturdy; }
    public boolean hasBurglar() { return hasBurglar; }
    public boolean hasProtection() { return hasProtection; }
    public boolean hasPhoenix() { return hasPhoenix; }
    
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
        public Builder luck(float val) { stats.luck = val; return this; }
        public Builder lightLevel(int val) { stats.lightLevel = val; return this; }
        
        public Builder yobaProtection(boolean val) { stats.hasYobaProtection = val; return this; }
        public Builder thorns(boolean val) { stats.hasThorns = val; return this; }
        public Builder slimeCharmer(boolean val) { stats.hasSlimeCharmer = val; return this; }
        public Builder sturdy(boolean val) { stats.hasSturdy = val; return this; }
        public Builder burglar(boolean val) { stats.hasBurglar = val; return this; }
        public Builder protection(boolean val) { stats.hasProtection = val; return this; }
        public Builder phoenix(boolean val) { stats.hasPhoenix = val; return this; }
        
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
            stats.luck += other.luck;
            stats.lightLevel = Math.max(stats.lightLevel, other.lightLevel);
            
            stats.hasYobaProtection |= other.hasYobaProtection;
            stats.hasThorns |= other.hasThorns;
            stats.hasSlimeCharmer |= other.hasSlimeCharmer;
            stats.hasSturdy |= other.hasSturdy;
            stats.hasBurglar |= other.hasBurglar;
            stats.hasProtection |= other.hasProtection;
            stats.hasPhoenix |= other.hasPhoenix;
            
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
