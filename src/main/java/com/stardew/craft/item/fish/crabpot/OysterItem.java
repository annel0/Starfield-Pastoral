package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 牡蛎 (Oyster)
 * 珍贵的贝类
 * 位置：海洋蟹笼、沙滩（采集可获得金星/铱星品质）
 * 
 * 价格: 普通40g, 银星50g, 金星60g, 铱星80g
 */
public class OysterItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {40, 50, 60, 80};
    
    public OysterItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
