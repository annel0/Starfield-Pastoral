package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 河豚 (Pufferfish)
 * 危险的有毒鱼
 * 位置：海洋
 * 季节：夏季（中午高温）
 */
public class PufferfishItem extends FishItem {
    public PufferfishItem(Item.Properties properties) {
        super(
            new int[]{200, 250, 300, 400},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{50, 70, 90, 130},     // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{22, 30, 39, 57},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            80, "floater",
            properties
        );
    }
}
