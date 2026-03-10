package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 章鱼 (Octopus)
 * 夏季的深海生物
 * 位置：海洋
 * 季节：夏季
 */
public class OctopusItem extends FishItem {
    public OctopusItem(Item.Properties properties) {
        super(
            new int[]{150, 187, 225, 300},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{38, 53, 68, 98},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{16, 22, 28, 41},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            95, "sinker",
            properties
        );
    }
}
