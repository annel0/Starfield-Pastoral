package com.stardew.craft.item.fish.legendary;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 冰川鱼 (Glacierfish)
 * 传说级的冰冻鱼
 * 位置：箭头岛南端
 * 季节：冬季
 * 要求：钓鱼等级6
 */
public class GlacierfishItem extends FishItem {
    public GlacierfishItem(Item.Properties properties) {
        super(
            new int[]{1000, 1250, 1500, 2000},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{250, 350, 450, 650},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{112, 156, 201, 291},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            100, "mixed",
            properties
        );
    }
    
    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.legendary_fish";
    }
}
