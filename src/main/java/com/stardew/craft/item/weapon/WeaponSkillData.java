package com.stardew.craft.item.weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * 武器技能数据
 */
public class WeaponSkillData {
    
    private final String id;
    private final String nameKey;
    private final List<String> descriptionKeys;
    private final int damagePercent;
    private final List<String> effectKeys;
    private final int cooldown;
    private final String iconChar; // 自定义字体中的技能图标字符
    
    private WeaponSkillData(Builder builder) {
        this.id = builder.id;
        this.nameKey = builder.nameKey;
        this.descriptionKeys = builder.descriptionKeys;
        this.damagePercent = builder.damagePercent;
        this.effectKeys = builder.effectKeys;
        this.cooldown = builder.cooldown;
        this.iconChar = builder.iconChar;
    }
    
    // Getters
    public String getId() { return id; }
    public String getNameKey() { return nameKey; }
    public List<String> getDescriptionKeys() { return descriptionKeys; }
    public int getDamagePercent() { return damagePercent; }
    public List<String> getEffectKeys() { return effectKeys; }
    public int getCooldown() { return cooldown; }
    public String getIconChar() { return iconChar; }
    
    public static Builder builder(String id) {
        return new Builder(id);
    }
    
    public static class Builder {
        private final String id;
        private String nameKey = "";
        private List<String> descriptionKeys = new ArrayList<>();
        private int damagePercent = 0;
        private List<String> effectKeys = new ArrayList<>();
        private int cooldown = 0;
        private String iconChar = null;
        
        public Builder(String id) {
            this.id = id;
        }
        
        public Builder nameKey(String nameKey) {
            this.nameKey = nameKey;
            return this;
        }
        
        public Builder descriptionKeys(String... keys) {
            this.descriptionKeys = List.of(keys);
            return this;
        }
        
        public Builder damage(int percent) {
            this.damagePercent = percent;
            return this;
        }
        
        public Builder effectKey(String key) {
            this.effectKeys.add(key);
            return this;
        }
        
        public Builder cooldown(int seconds) {
            this.cooldown = seconds;
            return this;
        }
        
        public Builder icon(String iconChar) {
            this.iconChar = iconChar;
            return this;
        }
        
        public WeaponSkillData build() {
            return new WeaponSkillData(this);
        }
    }
}
