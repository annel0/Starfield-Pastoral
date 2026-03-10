package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 玉黍螺 (Periwinkle)
 * 小型淡水螺
 * 位置：淡水蟹笼
 * 
 * 价格: 普通20g, 银星25g, 金星30g, 铱星40g
 */
public class PeriwinkleItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {20, 25, 30, 40};
    
    public PeriwinkleItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
