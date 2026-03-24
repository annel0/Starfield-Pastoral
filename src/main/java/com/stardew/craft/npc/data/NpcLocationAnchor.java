package com.stardew.craft.npc.data;

/**
 * Canonical location anchor for NPC schedule resolution.
 */
public record NpcLocationAnchor(
    double x,
    double y,
    double z,
    boolean indoor,
    String portalTarget,
    boolean useGroundHeight,
    boolean useScheduleTileOffset
) {
    public NpcLocationAnchor {
        portalTarget = portalTarget == null ? "" : portalTarget.trim().toLowerCase();
    }
}
