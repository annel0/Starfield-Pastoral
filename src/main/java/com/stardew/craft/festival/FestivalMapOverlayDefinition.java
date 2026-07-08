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
    boolean cleanupTaggedEntities,
    TreeClearance treeClearance
) {
    public FestivalMapOverlayDefinition(
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
        this(overlayId, regionKey, origin, baseSchematicPath, festivalSchematicPath, boundsMin, boundsMax, safePositions,
            requiresBlackFade, cleanupDroppedItems, cleanupTaggedEntities, TreeClearance.NONE);
    }

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
        treeClearance = treeClearance == null ? TreeClearance.NONE : treeClearance;
    }

    public boolean usesRuntimeBase() {
        return baseSchematicPath.isBlank();
    }

    public record TreeClearance(int horizontalRadius, int up, int down) {
        public static final TreeClearance NONE = new TreeClearance(0, 0, 0);

        public TreeClearance {
            horizontalRadius = Math.max(0, horizontalRadius);
            up = Math.max(0, up);
            down = Math.max(0, down);
        }

        public boolean enabled() {
            return horizontalRadius > 0 || up > 0 || down > 0;
        }
    }
}
