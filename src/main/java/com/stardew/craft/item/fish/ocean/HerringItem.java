package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鲱鱼 (Herring)
 * 常见的海鱼
 * 位置：海洋
 * 季节：春季、冬季
 */
public class HerringItem extends FishItem {
    public HerringItem(Item.Properties properties) {
        super(
            new int[]{30, 37, 45, 60},      // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{13, 18, 23, 33},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{5, 7, 9, 13},         // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            25, "dart",
            properties
        );
    }
}
