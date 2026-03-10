package com.stardew.craft.item.fish.legendary;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 变种鲤鱼 (Mutant Carp)
 * 传说级的变异鱼
 * 位置：下水道
 * 季节：全年
 */
public class MutantCarpItem extends FishItem {
    public MutantCarpItem(Item.Properties properties) {
        super(
            new int[]{1000, 1250, 1500, 2000},  // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{250, 350, 450, 650},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{112, 156, 201, 291},      // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            80, "dart",
            properties
        );
    }
    
    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.legendary_fish";
    }
}
