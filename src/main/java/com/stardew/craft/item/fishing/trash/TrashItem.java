package com.stardew.craft.item.fishing.trash;

import com.stardew.craft.item.IStardewItem;
import net.minecraft.world.item.Item;

/**
 * 垃圾类物品基类
 * 价格为基础价格，不受品质影响（垃圾没有品质）
 */
public class TrashItem extends Item implements IStardewItem {
    private final int basePrice;

    public TrashItem(int basePrice, Item.Properties properties) {
        super(properties);
        this.basePrice = basePrice;
    }

    public int getBasePrice() {
        return basePrice;
    }

    @Override
    public String getItemTypeKey() {
        return "stardewcraft.type.trash";
    }
}
