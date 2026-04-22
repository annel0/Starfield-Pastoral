package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 黄貂鱼 (Stingray) - SDV (O)836
 * Fish.json: Stingray/80/sinker/18/60/600 2600/spring summer fall winter/both/685 .35/4/.2/.1/0/false
 * 售价 180, 食用度 15
 */
public class StingrayItem extends FishItem {
    private static final int[] PRICE_BY_QUALITY = {180, 225, 270, 360};
    private static final int[] ENERGY_BY_QUALITY = {38, 53, 68, 99};
    private static final int[] HEALTH_BY_QUALITY = {17, 23, 30, 44};

    public StingrayItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 80, "sinker", properties);
    }
}
