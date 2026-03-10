package com.stardew.craft.item.fish.legendary;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 传说之鱼 (Legend)
 * 最稀有的传说级鱼类
 * 位置：山区湖泊
 * 季节：春季（雨天）
 * 要求：钓鱼等级10
 */
public class LegendItem extends FishItem {
    public LegendItem(Item.Properties properties) {
        super(
            new int[]{5000, 6250, 7500, 10000}, // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{1250, 1750, 2250, 3250},  // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{562, 786, 1011, 1461},    // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            110, "mixed",
            properties
        );
    }
    
    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.legendary_fish";
    }
}
