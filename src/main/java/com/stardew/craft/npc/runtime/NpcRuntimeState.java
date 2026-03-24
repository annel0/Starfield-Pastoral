package com.stardew.craft.npc.runtime;

import net.minecraft.nbt.CompoundTag;

/**
 * Persistent runtime state for one NPC.
 */
@SuppressWarnings("null")
public final class NpcRuntimeState {
    private final String npcId;
    private String locationName;
    private String activeScheduleKey;
    private int scheduleCheckpoint;
    private int tileX;
    private int tileY;
    private int facing;
    private int scheduleNodeIndex;
    private String routeBehaviorToken;
    private boolean pathingSuppressed;

    public NpcRuntimeState(String npcId) {
        this.npcId = npcId;
        this.locationName = "Town";
        this.activeScheduleKey = "default";
        this.scheduleCheckpoint = 600;
        this.tileX = 0;
        this.tileY = 0;
        this.facing = 2;
        this.scheduleNodeIndex = 0;
        this.routeBehaviorToken = "";
        this.pathingSuppressed = false;
    }

    public String npcId() {
        return npcId;
    }

    public String locationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        if (locationName != null && !locationName.isBlank()) {
            this.locationName = locationName;
        }
    }

    public String activeScheduleKey() {
        return activeScheduleKey;
    }

    public void setActiveScheduleKey(String activeScheduleKey) {
        if (activeScheduleKey != null && !activeScheduleKey.isBlank()) {
            this.activeScheduleKey = activeScheduleKey;
        }
    }

    public int scheduleCheckpoint() {
        return scheduleCheckpoint;
    }

    public void setScheduleCheckpoint(int scheduleCheckpoint) {
        this.scheduleCheckpoint = Math.max(0, scheduleCheckpoint);
    }

    public int tileX() {
        return tileX;
    }

    public void setTileX(int tileX) {
        this.tileX = tileX;
    }

    public int tileY() {
        return tileY;
    }

    public void setTileY(int tileY) {
        this.tileY = tileY;
    }

    public int facing() {
        return facing;
    }

    public void setFacing(int facing) {
        this.facing = facing;
    }

    public int scheduleNodeIndex() {
        return scheduleNodeIndex;
    }

    public void setScheduleNodeIndex(int scheduleNodeIndex) {
        this.scheduleNodeIndex = Math.max(0, scheduleNodeIndex);
    }

    public String routeBehaviorToken() {
        return routeBehaviorToken;
    }

    public void setRouteBehaviorToken(String routeBehaviorToken) {
        this.routeBehaviorToken = routeBehaviorToken == null ? "" : routeBehaviorToken;
    }

    public boolean pathingSuppressed() {
        return pathingSuppressed;
    }

    public void setPathingSuppressed(boolean pathingSuppressed) {
        this.pathingSuppressed = pathingSuppressed;
    }

    public CompoundTag toNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putString("NpcId", npcId);
        tag.putString("LocationName", locationName);
        tag.putString("ActiveScheduleKey", activeScheduleKey);
        tag.putInt("ScheduleCheckpoint", scheduleCheckpoint);
        tag.putInt("TileX", tileX);
        tag.putInt("TileY", tileY);
        tag.putInt("Facing", facing);
        tag.putInt("ScheduleNodeIndex", scheduleNodeIndex);
        tag.putString("RouteBehaviorToken", routeBehaviorToken);
        tag.putBoolean("PathingSuppressed", pathingSuppressed);
        return tag;
    }

    public static NpcRuntimeState fromNbt(CompoundTag tag) {
        String npcId = tag.getString("NpcId");
        NpcRuntimeState state = new NpcRuntimeState(npcId);
        state.locationName = tag.contains("LocationName") ? tag.getString("LocationName") : "Town";
        state.activeScheduleKey = tag.contains("ActiveScheduleKey") ? tag.getString("ActiveScheduleKey") : "default";
        state.scheduleCheckpoint = tag.contains("ScheduleCheckpoint") ? Math.max(0, tag.getInt("ScheduleCheckpoint")) : 600;
        state.tileX = tag.getInt("TileX");
        state.tileY = tag.getInt("TileY");
        state.facing = tag.contains("Facing") ? tag.getInt("Facing") : 2;
        state.scheduleNodeIndex = Math.max(0, tag.getInt("ScheduleNodeIndex"));
        state.routeBehaviorToken = tag.contains("RouteBehaviorToken") ? tag.getString("RouteBehaviorToken") : "";
        state.pathingSuppressed = tag.getBoolean("PathingSuppressed");
        return state;
    }
}
