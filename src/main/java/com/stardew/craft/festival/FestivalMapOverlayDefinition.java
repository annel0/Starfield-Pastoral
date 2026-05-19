package com.stardew.craft.festival;

import net.minecraft.core.BlockPos;

import java.util.List;

public record FestivalMapOverlayDefinition(
    String overlayId,
    String regionKey,
    BlockPos origin,
    String baseSchematicPath,
    String festivalSchematicPath,
    BlockPos boundsMin,
    BlockPos boundsMax,
    List<BlockPos> safePositions,
    boolean requiresBlackFade,
    boolean cleanupDroppedItems,
    boolean cleanupTaggedEntities
) {
    public FestivalMapOverlayDefinition {
        if (overlayId == null || overlayId.isBlank()) {
            throw new IllegalArgumentException("overlay id must not be blank");
        }
        regionKey = regionKey == null ? "" : regionKey;
        origin = origin == null ? BlockPos.ZERO : origin;
        baseSchematicPath = baseSchematicPath == null ? "" : baseSchematicPath;
        festivalSchematicPath = festivalSchematicPath == null ? "" : festivalSchematicPath;
        boundsMin = boundsMin == null ? BlockPos.ZERO : boundsMin;
        boundsMax = boundsMax == null ? BlockPos.ZERO : boundsMax;
        safePositions = safePositions == null ? List.of() : List.copyOf(safePositions);
    }
}