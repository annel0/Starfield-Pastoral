package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 红鲷鱼 (Red Snapper)
 * 雨天出没的海鱼
 * 位置：海洋
 * 季节：夏季、秋季（雨天）
 */
public class RedSnapperItem extends FishItem {
    public RedSnapperItem(Item.Properties properties) {
        super(
            new int[]{50, 62, 75, 100},     // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{22, 30, 39, 57},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{9, 12, 16, 23},       // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            40, "mixed",
            properties
        );
    }
}
