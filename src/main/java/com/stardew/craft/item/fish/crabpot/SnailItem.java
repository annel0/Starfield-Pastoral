package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 蜗牛 (Snail)
 * 淡水螺类
 * 位置：淡水蟹笼
 * 
 * 价格: 普通65g, 银星81g, 金星97g, 铱星130g
 */
public class SnailItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {65, 81, 97, 130};
    
    public SnailItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
