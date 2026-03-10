package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鱿鱼 (Squid)
 * 冬季夜间出没
 * 位置：海洋
 * 季节：冬季（夜间）
 */
public class SquidItem extends FishItem {
    public SquidItem(Item.Properties properties) {
        super(
            new int[]{80, 100, 120, 160},   // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{25, 35, 45, 65},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{11, 15, 19, 28},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            75, "sinker",
            properties
        );
    }
}
