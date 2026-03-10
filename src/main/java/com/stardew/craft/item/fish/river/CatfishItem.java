package com.stardew.craft.item.fish.river;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鲶鱼 (Catfish)
 * 只在雨天出没的鱼
 * 位置：河流、下水道
 * 季节：春季、秋季（雨天）
 */
public class CatfishItem extends FishItem {
    public CatfishItem(Item.Properties properties) {
        super(
            new int[]{200, 250, 300, 400},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{50, 70, 90, 130},     // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{22, 30, 39, 57},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            75, "mixed",
            properties
        );
    }
}
