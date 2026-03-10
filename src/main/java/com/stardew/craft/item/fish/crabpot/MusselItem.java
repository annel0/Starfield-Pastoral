package com.stardew.craft.item.fish.crabpot;

import net.minecraft.world.item.Item;

/**
 * 贻贝 (Mussel)
 * 常见的海洋双壳类
 * 位置：海洋蟹笼、沙滩（采集可获得金星/铱星品质）
 * 
 * 价格: 普通30g, 银星37g, 金星45g, 铱星60g
 */
public class MusselItem extends CrabPotItem {
    // 按品质售价: [普通, 银星, 金星, 铱星]
    private static final int[] PRICE_BY_QUALITY = {30, 37, 45, 60};
    
    public MusselItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, properties);
    }
}
