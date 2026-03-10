package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 虾 (Shrimp)
 * 小型海洋甲壳类
 * 位置：海洋蟹笼
 * 
 * 价格: 普通60g, 银星75g, 金星90g, 铱星120g
 */
public class ShrimpItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {60, 75, 90, 120};
    
    public ShrimpItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
