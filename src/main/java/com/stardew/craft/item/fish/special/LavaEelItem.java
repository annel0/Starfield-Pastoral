package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 熔岩鳗鱼 (Lava Eel)
 * 火山矿井的稀有鱼
 * 位置：矿井（100层）
 * 季节：全年
 * 难度：90，行为：mixed
 */
public class LavaEelItem extends FishItem {
    // 售价: 700, 875, 1050, 1400
    private static final int[] PRICE_BY_QUALITY = {700, 875, 1050, 1400};
    // 能量: -300 (剧毒不可食)
    private static final int[] ENERGY_BY_QUALITY = {-300, -300, -300, -300};
    // 生命: -135 (剧毒不可食)
    private static final int[] HEALTH_BY_QUALITY = {-135, -135, -135, -135};
    
    public LavaEelItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 90, "mixed", properties);
    }
}
