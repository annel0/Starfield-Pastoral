package com.stardew.craft.item.fish.river;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鲷鱼 (Bream)
 * 一种常见的河鱼
 * 位置：河流
 * 季节：全年
 */
public class BreamItem extends FishItem {
    public BreamItem(Item.Properties properties) {
        super(
            new int[]{45, 56, 67, 90},      // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{22, 30, 39, 57},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{9, 12, 16, 23},       // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            35, "smooth",
            properties
        );
    }
}
