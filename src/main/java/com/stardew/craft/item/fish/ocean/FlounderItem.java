package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 比目鱼 (Flounder)
 * 扁平的底栖鱼
 * 位置：海洋
 * 季节：春季、夏季
 */
public class FlounderItem extends FishItem {
    public FlounderItem(Item.Properties properties) {
        super(
            new int[]{100, 125, 150, 200},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{25, 35, 45, 65},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{11, 15, 19, 28},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            50, "sinker",
            properties
        );
    }
}
