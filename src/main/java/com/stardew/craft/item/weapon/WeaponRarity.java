package com.stardew.craft.item.weapon;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * 武器稀有度等级
 * 决定武器名称颜色和tooltip边框
 */
public enum WeaponRarity {
    COMMON(1, 4, ChatFormatting.WHITE, "普通"),
    UNCOMMON(5, 8, ChatFormatting.GREEN, "精良"),
    RARE(9, 12, ChatFormatting.BLUE, "稀有"),
    EPIC(13, 16, ChatFormatting.DARK_PURPLE, "史诗"),
    LEGENDARY(17, Integer.MAX_VALUE, ChatFormatting.GOLD, "传说");
    
    private final int minLevel;
    private final int maxLevel;
    private final ChatFormatting color;
    private final String displayName;
    
    WeaponRarity(int minLevel, int maxLevel, ChatFormatting color, String displayName) {
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.color = color;
        this.displayName = displayName;
    }
    
    /**
     * 根据武器等级获取稀有度
     */
    public static WeaponRarity fromLevel(int level) {
        for (WeaponRarity rarity : values()) {
            if (level >= rarity.minLevel && level <= rarity.maxLevel) {
                return rarity;
            }
        }
        return COMMON;
    }
    
    public ChatFormatting getColor() {
        return color;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @SuppressWarnings("null")
    public MutableComponent getDisplayComponent() {
        return Component.literal(displayName).withStyle(color);
    }
}
