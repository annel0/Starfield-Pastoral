package com.stardew.craft.item.fish.lake;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 太阳鱼 (Sunfish)
 * 夏季常见的小型鱼
 * 位置：河流、森林池塘
 * 季节：春季、夏季
 */
public class SunfishItem extends FishItem {
    public SunfishItem(Item.Properties properties) {
        super(
            new int[]{30, 37, 45, 60},      // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{13, 18, 23, 33},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{5, 7, 9, 13},         // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            30, "mixed",
            properties
        );
    }
}
