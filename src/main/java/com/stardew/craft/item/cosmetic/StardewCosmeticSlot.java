package com.stardew.craft.item.cosmetic;

public enum StardewCosmeticSlot {
    HAT("stardewcraft.type.hat"),
    SHIRT("stardewcraft.type.shirt"),
    PANTS("stardewcraft.type.pants");

    private final String typeKey;

    StardewCosmeticSlot(String typeKey) {
        this.typeKey = typeKey;
    }

    public String typeKey() {
        return typeKey;
    }
}
