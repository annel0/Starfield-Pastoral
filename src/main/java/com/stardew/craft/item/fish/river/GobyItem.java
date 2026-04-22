package com.stardew.craft.item.fish.river;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 鰕虎鱼 (Goby) - SDV (O)Goby
 * Fish.json: Goby/55/dart/3/12/800 1800/spring summer fall/both/684 .35/1/.25/.2/0/false
 * 售价 150, 食用度 -25 (无营养)
 */
public class GobyItem extends FishItem {
    private static final int[] PRICE_BY_QUALITY = {150, 187, 225, 300};
    private static final int[] ENERGY_BY_QUALITY = {-25, -25, -25, -25};
    private static final int[] HEALTH_BY_QUALITY = {-11, -11, -11, -11};

    public GobyItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 55, "dart", properties);
    }
}
