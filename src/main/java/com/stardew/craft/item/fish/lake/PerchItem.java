package com.stardew.craft.item.fish.lake;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鲈鱼 (Perch)
 * 小型湖鱼
 * 位置：河流、山区湖泊、森林池塘
 * 季节：冬季
 */
public class PerchItem extends FishItem {
    public PerchItem(Item.Properties properties) {
        super(
            new int[]{55, 68, 82, 110},     // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{22, 30, 39, 57},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{9, 12, 16, 23},       // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            35, "dart",
            properties
        );
    }
}
