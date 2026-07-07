package com.stardew.craft.item.cosmetic;

import net.minecraft.resources.ResourceLocation;

public class StardewHatItem extends StardewCosmeticItem {
    private final ResourceLocation modelLocation;

    public StardewHatItem(String vanillaId, int sellPrice, ResourceLocation modelLocation, Properties properties) {
        super(StardewCosmeticSlot.HAT, vanillaId, sellPrice, properties);
        this.modelLocation = modelLocation;
    }

    public ResourceLocation getModelLocation() {
        return modelLocation;
    }
}
