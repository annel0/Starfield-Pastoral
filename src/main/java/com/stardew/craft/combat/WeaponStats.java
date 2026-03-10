package com.stardew.craft.combat;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 武器属性
 * 从物品NBT中读取星露谷武器的各项属性
 */
public class WeaponStats {
    
    // NBT标签名
    public static final String TAG_STARDEW_WEAPON = "StardewWeapon";
    public static final String TAG_WEAPON_TYPE = "Type";
    public static final String TAG_MIN_DAMAGE = "MinDamage";
    public static final String TAG_MAX_DAMAGE = "MaxDamage";
    public static final String TAG_CRIT_CHANCE = "CritChance";
    public static final String TAG_CRIT_POWER = "CritPower";
    public static final String TAG_SPEED = "Speed";
    public static final String TAG_DEFENSE = "Defense";
    public static final String TAG_PRECISION = "Precision";
    public static final String TAG_KNOCKBACK = "Knockback";
    
    private final WeaponType weaponType;
    private final float minDamage;
    private final float maxDamage;
    private final float critChance;      // 基础暴击率 (0.02 = 2%)
    private final float bonusCritChance; // 额外暴击率
    private final float bonusCritPower;  // 额外暴击伤害 (以百分比计，10 = +10%)
    private final int speed;             // 速度修正
    private final int defense;           // 防御值
    private final float precision;       // 精确度 (降低敌人闪避)
    private final float knockback;       // 击退力度
    
    private WeaponStats(Builder builder) {
        this.weaponType = builder.weaponType;
        this.minDamage = builder.minDamage;
        this.maxDamage = builder.maxDamage;
        this.critChance = builder.critChance;
        this.bonusCritChance = builder.bonusCritChance;
        this.bonusCritPower = builder.bonusCritPower;
        this.speed = builder.speed;
        this.defense = builder.defense;
        this.precision = builder.precision;
        this.knockback = builder.knockback;
    }
    
    /**
     * 从物品NBT读取武器属性
     */
    public static WeaponStats fromItemStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return empty();
        }
        
        @SuppressWarnings("null")
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) {
            return empty();
        }
        
        CompoundTag tag = data.copyTag();
        if (!tag.contains(TAG_STARDEW_WEAPON)) {
            return empty();
        }
        
        CompoundTag weaponTag = tag.getCompound(TAG_STARDEW_WEAPON);
        
        return builder()
            .weaponType(WeaponType.fromId(weaponTag.getInt(TAG_WEAPON_TYPE)))
            .minDamage(weaponTag.getFloat(TAG_MIN_DAMAGE))
            .maxDamage(weaponTag.getFloat(TAG_MAX_DAMAGE))
            .critChance(weaponTag.contains(TAG_CRIT_CHANCE) ? weaponTag.getFloat(TAG_CRIT_CHANCE) : 0.02f)
            .bonusCritPower(weaponTag.getFloat(TAG_CRIT_POWER))
            .speed(weaponTag.getInt(TAG_SPEED))
            .defense(weaponTag.getInt(TAG_DEFENSE))
            .precision(weaponTag.getFloat(TAG_PRECISION))
            .knockback(weaponTag.getFloat(TAG_KNOCKBACK))
            .build();
    }
    
    /**
     * 将武器属性写入物品NBT
     */
    @SuppressWarnings("null")
    public void writeToItemStack(ItemStack stack) {
        @SuppressWarnings("null")
        CustomData existingData = stack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = existingData != null ? existingData.copyTag() : new CompoundTag();
        
        CompoundTag weaponTag = new CompoundTag();
        weaponTag.putInt(TAG_WEAPON_TYPE, weaponType.getId());
        weaponTag.putFloat(TAG_MIN_DAMAGE, minDamage);
        weaponTag.putFloat(TAG_MAX_DAMAGE, maxDamage);
        weaponTag.putFloat(TAG_CRIT_CHANCE, critChance);
        weaponTag.putFloat(TAG_CRIT_POWER, bonusCritPower);
        weaponTag.putInt(TAG_SPEED, speed);
        weaponTag.putInt(TAG_DEFENSE, defense);
        weaponTag.putFloat(TAG_PRECISION, precision);
        weaponTag.putFloat(TAG_KNOCKBACK, knockback);
        
        tag.put(TAG_STARDEW_WEAPON, weaponTag);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    
    /**
     * 空武器属性（徒手）
     */
    public static WeaponStats empty() {
        return builder()
            .weaponType(WeaponType.SWORD)
            .minDamage(1)
            .maxDamage(1)
            .critChance(0.02f)
            .build();
    }
    
    // Getters
    public WeaponType getWeaponType() { return weaponType; }
    public float getMinDamage() { return minDamage; }
    public float getMaxDamage() { return maxDamage; }
    public float getCritChance() { return critChance; }
    public float getBonusCritChance() { return bonusCritChance; }
    public float getBonusCritPower() { return bonusCritPower; }
    public int getSpeed() { return speed; }
    public int getDefense() { return defense; }
    public float getPrecision() { return precision; }
    public float getKnockback() { return knockback; }
    
    /**
     * 获取平均伤害
     */
    public float getAverageDamage() {
        return (minDamage + maxDamage) / 2f;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private WeaponType weaponType = WeaponType.SWORD;
        private float minDamage = 1;
        private float maxDamage = 1;
        private float critChance = 0.02f;
        private float bonusCritChance = 0;
        private float bonusCritPower = 0;
        private int speed = 0;
        private int defense = 0;
        private float precision = 0;
        private float knockback = 0;
        
        public Builder weaponType(WeaponType val) { this.weaponType = val; return this; }
        public Builder minDamage(float val) { this.minDamage = val; return this; }
        public Builder maxDamage(float val) { this.maxDamage = val; return this; }
        public Builder critChance(float val) { this.critChance = val; return this; }
        public Builder bonusCritChance(float val) { this.bonusCritChance = val; return this; }
        public Builder bonusCritPower(float val) { this.bonusCritPower = val; return this; }
        public Builder speed(int val) { this.speed = val; return this; }
        public Builder defense(int val) { this.defense = val; return this; }
        public Builder precision(float val) { this.precision = val; return this; }
        public Builder knockback(float val) { this.knockback = val; return this; }
        
        public WeaponStats build() {
            return new WeaponStats(this);
        }
    }
}
