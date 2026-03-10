package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 史莱姆鱼 (Slimejack)
 * 史莱姆聚集地的鱼
 * 位置：史莱姆牧场、变种矿井
 * 季节：全年
 * 难度：55，行为：dart
 */
public class SlimejackItem extends FishItem {
    // 售价: 100, 125, 150, 200
    private static final int[] PRICE_BY_QUALITY = {100, 125, 150, 200};
    // 能量: -300 (有毒不可食)
    private static final int[] ENERGY_BY_QUALITY = {-300, -300, -300, -300};
    // 生命: -135 (有毒不可食)
    private static final int[] HEALTH_BY_QUALITY = {-135, -135, -135, -135};
    
    public SlimejackItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 55, "dart", properties);
    }
}
