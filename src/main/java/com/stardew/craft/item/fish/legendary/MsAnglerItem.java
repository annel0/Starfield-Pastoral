package com.stardew.craft.item.fish.legendary;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 安静女士 (Ms. Angler)
 * 𩽾𩾌鱼的伴侣
 * 位置：鹈鹕镇北侧木桥
 * 季节：秋季
 * 要求：已捕获𩽾𩾌鱼、使用传说鱼饵
 */
public class MsAnglerItem extends FishItem {
    public MsAnglerItem(Item.Properties properties) {
        super(
            new int[]{450, 562, 675, 900},      // 价格：普通, 银星×1.25, 金星×1.5, 铱星×2.0
            new int[]{112, 156, 201, 291},      // 能量：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            new int[]{50, 70, 90, 130},         // 生命：普通, 银星×1.4, 金星×1.8, 铱星×2.6
            80, "smooth",
            properties
        );
    }
    
    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.legendary_fish";
    }
}
