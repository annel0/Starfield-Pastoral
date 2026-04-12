package com.stardew.craft.shop;

import java.util.List;

/**
 * Data class representing a single building blueprint in Robin's carpenter menu.
 * Mirrors SDV CarpenterMenu.BlueprintEntry.
 */
public record CarpenterBlueprint(
    String id,
    String displayName,
    String description,
    int cost,
    List<MaterialEntry> materials,
    String resultItemId,
    boolean isUpgrade
) {
    public record MaterialEntry(
        String itemId,
        int count
    ) {}
}
