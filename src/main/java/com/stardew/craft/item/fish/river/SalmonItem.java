package com.stardew.craft.item.fish.river;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鲑鱼 (Salmon)
 * 洄游鱼类
 * 位置：河流
 * 季节：秋季
 */
public class SalmonItem extends FishItem {
    public SalmonItem(Item.Properties properties) {
        super(
            new int[]{75, 93, 112, 150},    // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{38, 53, 68, 98},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{16, 22, 28, 41},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            50, "mixed",
            properties
        );
    }
}
