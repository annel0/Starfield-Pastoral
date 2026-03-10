package com.stardew.craft.item.fish.legendary;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 绯红鱼 (Crimsonfish)
 * 传说级的红色鱼
 * 位置：海滩东侧
 * 季节：夏季
 * 要求：钓鱼等级5
 */
public class CrimsonfishItem extends FishItem {
    public CrimsonfishItem(Item.Properties properties) {
        super(
            new int[]{1500, 1875, 2250, 3000},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{375, 525, 675, 975},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{168, 235, 302, 436},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            95, "mixed",
            properties
        );
    }
    
    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.legendary_fish";
    }
}
