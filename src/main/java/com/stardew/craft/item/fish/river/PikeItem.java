package com.stardew.craft.item.fish.river;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 狗鱼 (Pike)
 * 冬季河流捕食者
 * 位置：河流、森林池塘
 * 季节：夏季、冬季
 */
public class PikeItem extends FishItem {
    public PikeItem(Item.Properties properties) {
        super(
            new int[]{100, 125, 150, 200},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{38, 53, 68, 98},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{16, 22, 28, 41},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            60, "dart",
            properties
        );
    }
}
