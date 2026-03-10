package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 蝎子鲤鱼 (Scorpion Carp)
 * 沙漠的危险鱼类
 * 位置：沙漠
 * 季节：全年
 * 难度：90，行为：dart
 */
public class ScorpionCarpItem extends FishItem {
    // 售价: 150, 187, 225, 300
    private static final int[] PRICE_BY_QUALITY = {150, 187, 225, 300};
    // 能量: -50 (剧毒，不随品质变化)
    private static final int[] ENERGY_BY_QUALITY = {-50, -50, -50, -50};
    // 生命: -22 (剧毒，不随品质变化)
    private static final int[] HEALTH_BY_QUALITY = {-22, -22, -22, -22};
    
    public ScorpionCarpItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 90, "dart", properties);
    }
}
