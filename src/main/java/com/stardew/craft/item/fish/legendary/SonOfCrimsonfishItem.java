package com.stardew.craft.item.fish.legendary;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 绯红鱼之子 (Son of Crimsonfish)
 * 绯红鱼的后代
 * 位置：海滩东侧
 * 季节：夏季
 * 要求：已捕获绯红鱼、使用传说鱼饵
 */
public class SonOfCrimsonfishItem extends FishItem {
    public SonOfCrimsonfishItem(Item.Properties properties) {
        super(
            new int[]{750, 937, 1125, 1500},    // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{187, 261, 336, 486},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{84, 117, 151, 218},       // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            90, "mixed",
            properties
        );
    }
    
    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.legendary_fish";
    }
}
