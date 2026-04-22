package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 蓝铁饼鱼 (Blue Discus) - SDV (O)838
 * Fish.json: Blue Discus/60/dart/2/9/600 2600/spring summer fall winter/both/685 .35/1/.25/.1/0/false
 * 售价 120, 食用度 15
 */
public class BlueDiscusItem extends FishItem {
    private static final int[] PRICE_BY_QUALITY = {120, 150, 180, 240};
    private static final int[] ENERGY_BY_QUALITY = {38, 53, 68, 99};
    private static final int[] HEALTH_BY_QUALITY = {17, 23, 30, 44};

    public BlueDiscusItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 60, "dart", properties);
    }
}
