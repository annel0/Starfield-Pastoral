package com.stardew.craft.item.fish.river;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 小嘴鲈鱼 (Smallmouth Bass)
 * 活跃的战斗鱼
 * 位置：河流、森林河流
 * 季节：春季、秋季
 */
public class SmallmouthBassItem extends FishItem {
    public SmallmouthBassItem(Item.Properties properties) {
        super(
            new int[]{50, 62, 75, 100},     // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{25, 35, 45, 65},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{11, 15, 19, 28},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            28, "mixed",
            properties
        );
    }
}
