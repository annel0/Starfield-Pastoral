package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鳗鱼 (Eel)
 * 雨天出没的滑溜鱼
 * 位置：海洋
 * 季节：春季、秋季（雨天）
 */
public class EelItem extends FishItem {
    public EelItem(Item.Properties properties) {
        super(
            new int[]{85, 106, 127, 170},   // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{25, 35, 45, 65},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{11, 15, 19, 28},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            70, "smooth",
            properties
        );
    }
}
