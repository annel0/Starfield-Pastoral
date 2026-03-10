package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 冰冻矿工 (Ice Pip)
 * 极寒矿井的鱼
 * 位置：矿井（60层）
 * 季节：全年
 * 难度：85，行为：dart
 */
public class IcePipItem extends FishItem {
    // 售价: 500, 625, 750, 1000
    private static final int[] PRICE_BY_QUALITY = {500, 625, 750, 1000};
    // 能量: -300 (剧毒不可食)
    private static final int[] ENERGY_BY_QUALITY = {-300, -300, -300, -300};
    // 生命: -135 (剧毒不可食)
    private static final int[] HEALTH_BY_QUALITY = {-135, -135, -135, -135};
    
    public IcePipItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 85, "dart", properties);
    }
}
