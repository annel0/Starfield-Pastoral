package com.stardew.craft.item.fish.lake;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 午夜鲤鱼 (Midnight Carp)
 * 神秘的夜间鱼类
 * 位置：山区湖泊、森林池塘
 * 季节：秋季、冬季（夜间）
 */
public class MidnightCarpItem extends FishItem {
    public MidnightCarpItem(Item.Properties properties) {
        super(
            new int[]{150, 187, 225, 300},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{38, 53, 68, 98},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{16, 22, 28, 41},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            55, "mixed",
            properties
        );
    }
}
