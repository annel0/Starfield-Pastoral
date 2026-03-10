package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 午夜鱿鱼 (Midnight Squid)
 * 夜市特有的鱿鱼
 * 位置：夜市潜艇
 * 季节：冬季（夜市）
 * 难度：55，行为：sinker
 */
public class MidnightSquidItem extends FishItem {
    // 售价: 100, 125, 150, 200
    private static final int[] PRICE_BY_QUALITY = {100, 125, 150, 200};
    // 能量: 25, 35, 45, 65
    private static final int[] ENERGY_BY_QUALITY = {25, 35, 45, 65};
    // 生命: 11, 15, 20, 29
    private static final int[] HEALTH_BY_QUALITY = {11, 15, 20, 29};
    
    public MidnightSquidItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 55, "sinker", properties);
    }
}
