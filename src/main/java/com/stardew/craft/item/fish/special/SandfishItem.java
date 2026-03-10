package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 沙鱼 (Sandfish)
 * 沙漠绿洲的鱼
 * 位置：沙漠
 * 季节：全年
 * 难度：65，行为：mixed
 */
public class SandfishItem extends FishItem {
    // 售价: 75, 93, 112, 150
    private static final int[] PRICE_BY_QUALITY = {75, 93, 112, 150};
    // 能量: -12 (有毒，不随品质变化)
    private static final int[] ENERGY_BY_QUALITY = {-12, -12, -12, -12};
    // 生命: -5 (有毒，不随品质变化)
    private static final int[] HEALTH_BY_QUALITY = {-5, -5, -5, -5};
    
    public SandfishItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 65, "mixed", properties);
    }
}
