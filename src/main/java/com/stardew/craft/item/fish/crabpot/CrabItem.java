package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 螃蟹 (Crab)
 * 常见的海洋蟹类
 * 位置：海洋蟹笼
 * 
 * 价格: 普通100g, 银星125g, 金星150g, 铱星200g
 */
public class CrabItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {100, 125, 150, 200};
    
    public CrabItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
