package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 金枪鱼 (Tuna)
 * 大型远洋鱼类
 * 位置：海洋
 * 季节：夏季、冬季
 */
public class TunaItem extends FishItem {
    public TunaItem(Item.Properties properties) {
        super(
            new int[]{100, 125, 150, 200},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{38, 53, 68, 98},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{16, 22, 28, 41},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            70, "mixed",
            properties
        );
    }
}
