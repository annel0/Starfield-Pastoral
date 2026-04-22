package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 狮子鱼 (Lionfish) - SDV (O)837
 * Fish.json: Lionfish/50/smooth/3/12/600 2600/spring summer fall winter/both/685 .35/3/.4/.1/0/false
 * 售价 100, 食用度 15
 */
public class LionfishItem extends FishItem {
    private static final int[] PRICE_BY_QUALITY = {100, 125, 150, 200};
    private static final int[] ENERGY_BY_QUALITY = {38, 53, 68, 99};
    private static final int[] HEALTH_BY_QUALITY = {17, 23, 30, 44};

    public LionfishItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 50, "smooth", properties);
    }
}
