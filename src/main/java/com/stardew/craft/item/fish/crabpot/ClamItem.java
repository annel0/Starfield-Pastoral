package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 蛤蜊 (Clam)
 * 常见的贝类，也可以在沙滩拾取
 * 位置：海洋蟹笼、沙滩（采集可获得金星/铱星品质）
 * 
 * 价格: 普通50g, 银星62g, 金星75g, 铱星100g
 */
public class ClamItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {50, 62, 75, 100};
    
    public ClamItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
