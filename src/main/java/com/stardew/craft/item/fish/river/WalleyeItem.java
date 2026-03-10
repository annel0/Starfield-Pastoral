package com.stardew.craft.item.fish.river;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 大眼鱼 (Walleye)
 * 只在雨天的秋季出没
 * 位置：河流、湖泊、森林池塘
 * 季节：秋季（雨天）
 */
public class WalleyeItem extends FishItem {
    public WalleyeItem(Item.Properties properties) {
        super(
            new int[]{105, 131, 157, 210},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{38, 53, 68, 98},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{16, 22, 28, 41},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            45, "smooth",
            properties
        );
    }
}
