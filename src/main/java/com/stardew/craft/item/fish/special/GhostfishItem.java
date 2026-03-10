package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 幽灵鱼 (Ghostfish)
 * 矿井中的透明鱼
 * 位置：矿井（20-60层）
 * 季节：全年
 * 难度：50，行为：mixed
 */
public class GhostfishItem extends FishItem {
    // 售价: 45, 56, 67, 90
    private static final int[] PRICE_BY_QUALITY = {45, 56, 67, 90};
    // 能量: 13, 18, 23, 33
    private static final int[] ENERGY_BY_QUALITY = {13, 18, 23, 33};
    // 生命: 5, 8, 10, 14
    private static final int[] HEALTH_BY_QUALITY = {5, 8, 10, 14};
    
    public GhostfishItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 50, "mixed", properties);
    }
}
