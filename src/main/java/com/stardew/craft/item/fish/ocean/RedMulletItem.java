package com.stardew.craft.item.fish.ocean;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 红鲻鱼 (Red Mullet)
 * 夏季和冬季出现的海鱼
 * 位置：海洋
 * 季节：夏季、冬季
 */
public class RedMulletItem extends FishItem {
    public RedMulletItem(Item.Properties properties) {
        super(
            new int[]{75, 93, 112, 150},    // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{25, 35, 45, 65},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{11, 15, 19, 28},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            55, "smooth",
            properties
        );
    }
}
