package com.stardew.craft.item.fish.lake;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 虹鳟鱼 (Rainbow Trout)
 * 彩虹色的美丽鳟鱼
 * 位置：河流、山区湖泊
 * 季节：夏季
 */
public class RainbowTroutItem extends FishItem {
    public RainbowTroutItem(Item.Properties properties) {
        super(
            new int[]{65, 81, 97, 130},     // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{25, 35, 45, 65},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{11, 15, 19, 28},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            45, "mixed",
            properties
        );
    }
}
