package com.stardew.craft.item.fish.river;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 虎纹鳟鱼 (Tiger Trout)
 * 稀有的杂交鱼
 * 位置：河流
 * 季节：秋季、冬季
 */
public class TigerTroutItem extends FishItem {
    public TigerTroutItem(Item.Properties properties) {
        super(
            new int[]{150, 187, 225, 300},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{38, 53, 68, 98},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{16, 22, 28, 41},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            60, "dart",
            properties
        );
    }
}
