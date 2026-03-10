package com.stardew.craft.item.fish.lake;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 大头鱼 (Bullhead)
 * 底栖鱼类
 * 位置：山区湖泊
 * 季节：全年
 */
public class BullheadItem extends FishItem {
    public BullheadItem(Item.Properties properties) {
        super(
            new int[]{75, 93, 112, 150},    // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{22, 30, 39, 57},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{9, 12, 16, 23},       // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            46, "smooth",
            properties
        );
    }
}
