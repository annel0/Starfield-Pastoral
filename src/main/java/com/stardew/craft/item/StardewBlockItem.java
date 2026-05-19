package com.stardew.craft.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

@SuppressWarnings("null")
public class StardewBlockItem extends BlockItem implements IStardewItem {
	private final String itemTypeKey;
	private final int sellPrice;

	@SuppressWarnings("null")
	public StardewBlockItem(Block block, String itemTypeKey, int sellPrice, Item.Properties properties) {
		this(block, itemTypeKey, sellPrice, null, properties);
	}

	@SuppressWarnings("null")
	public StardewBlockItem(Block block, String itemTypeKey, int sellPrice, String descriptionKey, Item.Properties properties) {
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
