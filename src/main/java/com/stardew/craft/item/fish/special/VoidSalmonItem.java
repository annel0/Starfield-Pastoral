package com.stardew.craft.item.fish.special;

import com.stardew.craft.item.fish.FishItem;
import net.minecraft.world.item.Item;

/**
 * 虚空鲑鱼 (Void Salmon)
 * 黑暗的神秘鱼
 * 位置：女巫沼泽
 * 季节：全年
 * 难度：80，行为：mixed
 */
public class VoidSalmonItem extends FishItem {
    // 售价: 150, 187, 225, 300
    private static final int[] PRICE_BY_QUALITY = {150, 187, 225, 300};
    // 能量: 38, 53, 68, 98
    private static final int[] ENERGY_BY_QUALITY = {38, 53, 68, 98};
    // 生命: 17, 23, 30, 44
    private static final int[] HEALTH_BY_QUALITY = {17, 23, 30, 44};
    
    public VoidSalmonItem(Item.Properties properties) {
        super(PRICE_BY_QUALITY, ENERGY_BY_QUALITY, HEALTH_BY_QUALITY, 80, "mixed", properties);
    }
}
