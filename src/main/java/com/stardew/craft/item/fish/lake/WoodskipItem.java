package com.stardew.craft.item.fish.lake;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 森林鱼 (Woodskip)
 * 只在秘密森林池塘出没的神秘鱼
 * 位置：秘密森林池塘
 * 季节：全年
 */
public class WoodskipItem extends FishItem {
    public WoodskipItem(Item.Properties properties) {
        super(
            new int[]{75, 93, 112, 150},    // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{25, 35, 45, 65},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{11, 15, 19, 28},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            50, "mixed",
            properties
        );
    }
}
