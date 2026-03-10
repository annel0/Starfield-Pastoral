package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 小龙虾 (Crayfish)
 * 淡水甲壳类
 * 位置：淡水蟹笼
 * 
 * 价格: 普通75g, 银星93g, 金星112g, 铱星150g
 */
public class CrayfishItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {75, 93, 112, 150};
    
    public CrayfishItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
