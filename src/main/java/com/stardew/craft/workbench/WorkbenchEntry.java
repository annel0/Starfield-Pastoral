package com.stardew.craft.workbench;

import net.minecraft.resources.ResourceLocation;

/**
 * A single workbench recipe entry: what you get, at what cost.
 */
public record WorkbenchEntry(
    ResourceLocation itemId,
    String category,
    int cost,
    int outputCount,
    String namespace
) {
    public String displayCategory() {
        return "stardewcraft.workbench.cat." + category;
    }
}
