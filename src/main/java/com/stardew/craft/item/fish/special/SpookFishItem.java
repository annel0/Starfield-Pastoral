package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 斑点鱼 (Spook Fish)
 * 夜市特有的鱼
 * 位置：夜市潜艇
 * 季节：冬季（夜市）
 * 难度：60，行为：dart
 */
public class SpookFishItem extends FishItem {
    // 售价: 220, 275, 330, 440
    private static final int[] PRICE_BY_QUALITY = {220, 275, 330, 440};
    // 能量: 25, 35, 45, 65
    private static final int[] ENERGY_BY_QUALITY = {25, 35, 45, 65};
    // 生命: 11, 15, 20, 29
    private static final int[] HEALTH_BY_QUALITY = {11, 15, 20, 29};
    
    public SpookFishItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 60, "dart", properties);
    }
}
