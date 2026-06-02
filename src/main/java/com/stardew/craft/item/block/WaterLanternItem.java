package com.stardew.craft.item.block;

import com.stardew.craft.item.IStardewItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlaceOnWaterBlockItem;
import net.minecraft.world.level.block.Block;

public class WaterLanternItem extends PlaceOnWaterBlockItem implements IStardewItem {
    private final String itemTypeKey;
    private final int sellPrice;

    public WaterLanternItem(Block block, String itemTypeKey, int sellPrice, Item.Properties properties) {
        super(block, properties);
        this.itemTypeKey = itemTypeKey;
        this.sellPrice = sellPrice;
    }

    @Override
    public String getItemTypeKey() {
        return itemTypeKey;
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return sellPrice <= 0 ? -1 : sellPrice;
    }
}