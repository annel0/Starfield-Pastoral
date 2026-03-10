package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鳀鱼 (Anchovy)
 * 常见的小型海鱼
 * 位置：海洋
 * 季节：春季、秋季
 */
public class AnchovyItem extends FishItem {
    public AnchovyItem(Item.Properties properties) {
        super(
            new int[]{30, 37, 45, 60},      // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{13, 18, 23, 33},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{5, 7, 9, 13},         // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            30, "dart",
            properties
        );
    }
}
