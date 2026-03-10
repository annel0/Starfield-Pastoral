package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 龙虾 (Lobster)
 * 蟹笼捕获的大型甲壳类
 * 位置：海洋蟹笼
 * 
 * 价格: 普通120g, 银星150g, 金星180g, 铱星240g
 */
public class LobsterItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {120, 150, 180, 240};
    
    public LobsterItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
