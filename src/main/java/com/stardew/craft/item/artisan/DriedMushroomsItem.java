package com.stardew.craft.item.artisan;

public class DriedMushroomsItem extends PreservesItem {
	public DriedMushroomsItem(Properties properties) {
		super(PreserveType.DRIED_MUSHROOMS, properties);
	}

	@Override
	public String getItemTypeKey() {
		return "stardewcraft.type.misc";
	}
}
