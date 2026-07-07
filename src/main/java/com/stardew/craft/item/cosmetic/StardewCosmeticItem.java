package com.stardew.craft.item.cosmetic;

import com.stardew.craft.item.IStardewItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class StardewCosmeticItem extends Item implements IStardewItem {
    private final StardewCosmeticSlot cosmeticSlot;
    private final String vanillaId;
    private final int sellPrice;

    protected StardewCosmeticItem(StardewCosmeticSlot cosmeticSlot, String vanillaId, int sellPrice, Properties properties) {
        super(properties);
        this.cosmeticSlot = cosmeticSlot;
        this.vanillaId = vanillaId;
        this.sellPrice = sellPrice;
    }

    public StardewCosmeticSlot getCosmeticSlot() {
        return cosmeticSlot;
    }

    public String getVanillaId() {
        return vanillaId;
    }

    @Override
    public String getItemTypeKey() {
        return cosmeticSlot.typeKey();
    }

    @Override
    public int getSellPrice(ItemStack stack) {
        return sellPrice <= 0 ? -1 : sellPrice;
    }
}
