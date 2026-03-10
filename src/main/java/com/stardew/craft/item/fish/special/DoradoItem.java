package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 黄金鱼 (Dorado)
 * 夏季的稀有鱼类
 * 位置：森林河流
 * 季节：夏季
 * 难度：78，行为：mixed
 */
public class DoradoItem extends FishItem {
    // 售价: 100, 125, 150, 200
    private static final int[] PRICE_BY_QUALITY = {100, 125, 150, 200};
    // 能量: 38, 53, 68, 98
    private static final int[] ENERGY_BY_QUALITY = {38, 53, 68, 98};
    // 生命: 17, 23, 30, 44
    private static final int[] HEALTH_BY_QUALITY = {17, 23, 30, 44};
    
    public DoradoItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 78, "mixed", properties);
    }
}
