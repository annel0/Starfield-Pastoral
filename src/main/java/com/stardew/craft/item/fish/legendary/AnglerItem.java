package com.stardew.craft.item.fish.legendary;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 𩽾𩾌鱼 (Angler)
 * 传说级的秋季鱼
 * 位置：鹈鹕镇北侧木桥
 * 季节：秋季
 * 要求：钓鱼等级3
 */
public class AnglerItem extends FishItem {
    public AnglerItem(Item.Properties properties) {
        super(
            new int[]{900, 1125, 1350, 1800},   // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{225, 315, 405, 585},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{101, 141, 181, 262},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            85, "smooth",
            properties
        );
    }
    
    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.legendary_fish";
    }
}
