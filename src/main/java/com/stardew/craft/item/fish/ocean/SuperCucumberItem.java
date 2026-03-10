package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 超级海参 (Super Cucumber)
 * 稀有的神秘海洋生物
 * 位置：海洋
 * 季节：夏季、秋季（夜间）
 */
public class SuperCucumberItem extends FishItem {
    public SuperCucumberItem(Item.Properties properties) {
        super(
            new int[]{250, 312, 375, 500},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{63, 88, 113, 163},    // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{28, 39, 50, 72},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            80, "sinker",
            properties
        );
    }
}
