package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 石鱼 (Stonefish)
 * 矿井深处的鱼
 * 位置：矿井（20层）
 * 季节：全年
 * 难度：65，行为：sinker
 */
public class StonefishItem extends FishItem {
    // 售价: 300, 375, 450, 600
    private static final int[] PRICE_BY_QUALITY = {300, 375, 450, 600};
    // 能量: -300 (剧毒不可食)
    private static final int[] ENERGY_BY_QUALITY = {-300, -300, -300, -300};
    // 生命: -135 (剧毒不可食)
    private static final int[] HEALTH_BY_QUALITY = {-135, -135, -135, -135};
    
    public StonefishItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 65, "sinker", properties);
    }
}
