package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 鸟蛤 (Cockle)
 * 心形的贝类
 * 位置：海洋蟹笼、沙滩（采集可获得金星/铱星品质）
 * 
 * 价格: 普通50g, 银星62g, 金星75g, 铱星100g
 */
public class CockleItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {50, 62, 75, 100};
    
    public CockleItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
