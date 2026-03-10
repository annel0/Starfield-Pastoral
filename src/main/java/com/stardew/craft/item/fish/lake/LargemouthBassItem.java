package com.stardew.craft.item.fish.lake;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 大嘴鲈鱼 (Largemouth Bass)
 * 常见的湖鱼
 * 位置：山区湖泊
 * 季节：全年
 */
public class LargemouthBassItem extends FishItem {
    public LargemouthBassItem(Item.Properties properties) {
        super(
            new int[]{100, 125, 150, 200},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{38, 53, 68, 98},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{16, 22, 28, 41},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            50, "mixed",
            properties
        );
    }
}
