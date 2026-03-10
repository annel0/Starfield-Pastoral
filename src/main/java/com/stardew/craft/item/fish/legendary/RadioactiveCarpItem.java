package com.stardew.craft.item.fish.legendary;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 放射性鲤鱼 (Radioactive Carp)
 * 变种鲤鱼的放射性版本
 * 位置：下水道
 * 季节：全年
 * 要求：已捕获变种鲤鱼、使用传说鱼饵
 */
public class RadioactiveCarpItem extends FishItem {
    public RadioactiveCarpItem(Item.Properties properties) {
        super(
            new int[]{500, 625, 750, 1000},     // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{125, 175, 225, 325},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{56, 78, 100, 145},        // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            80, "dart",
            properties
        );
    }
    
    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.legendary_fish";
    }
}
