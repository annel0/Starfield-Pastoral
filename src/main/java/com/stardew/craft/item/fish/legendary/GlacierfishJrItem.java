package com.stardew.craft.item.fish.legendary;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 冰川鱼Jr. (Glacierfish Jr.)
 * 冰川鱼的幼体
 * 位置：箭头岛南端
 * 季节：冬季
 * 要求：已捕获冰川鱼、使用传说鱼饵
 */
public class GlacierfishJrItem extends FishItem {
    public GlacierfishJrItem(Item.Properties properties) {
        super(
            new int[]{500, 625, 750, 1000},     // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{125, 175, 225, 325},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{56, 78, 100, 145},        // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            95, "mixed",
            properties
        );
    }
    
    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.legendary_fish";
    }
}
