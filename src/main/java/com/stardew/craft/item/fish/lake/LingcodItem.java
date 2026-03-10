package com.stardew.craft.item.fish.lake;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 林鳕 (Lingcod)
 * 冬季特有的大型捕食性鱼类
 * 位置：河流、山区湖泊
 * 季节：冬季
 */
public class LingcodItem extends FishItem {
    public LingcodItem(Item.Properties properties) {
        super(
            new int[]{120, 150, 180, 240},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{38, 53, 68, 98},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{16, 22, 28, 41},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            85, "mixed",
            properties
        );
    }
}
