package com.stardew.craft.item.fish.lake;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鲟鱼 (Sturgeon)
 * 古老的大型鱼类，可产鱼子酱
 * 位置：山区湖泊
 * 季节：夏季、冬季
 */
public class SturgeonItem extends FishItem {
    public SturgeonItem(Item.Properties properties) {
        super(
            new int[]{200, 250, 300, 400},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{50, 70, 90, 130},     // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{22, 30, 39, 57},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            78, "mixed",
            properties
        );
    }
}
