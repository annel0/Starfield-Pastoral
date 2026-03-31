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
    boolean useScheduleTileOffset,
    String outdoorDoorPoint,
    String indoorEntryPoint
) {
    public NpcLocationAnchor {
        portalTarget = portalTarget == null ? "" : portalTarget.trim().toLowerCase();
        outdoorDoorPoint = outdoorDoorPoint == null ? "" : outdoorDoorPoint.trim();
        indoorEntryPoint = indoorEntryPoint == null ? "" : indoorEntryPoint.trim();
    }
}
