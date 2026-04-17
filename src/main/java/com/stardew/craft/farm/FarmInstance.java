package com.stardew.craft.farm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

/**
 * 单个玩家农场实例的数据记录。
 * 不可变（创建后字段不会改变，除 initialized 和 farmName）。
 */
public class FarmInstance {

    private final UUID ownerUUID;
    private String ownerName;
    private String farmName;
    private final int slotIndex;
    private final BlockPos origin;
    private final FarmType farmType;
    private boolean initialized;
    private long createdTimestamp;
    private int lastOnlineDay;
    private int lastOnlineSeason;

    public FarmInstance(UUID ownerUUID, String ownerName, String farmName,
                        int slotIndex, BlockPos origin, FarmType farmType) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.farmName = farmName;
        this.slotIndex = slotIndex;
        this.origin = origin;
        this.farmType = farmType;
        this.initialized = false;
        this.createdTimestamp = System.currentTimeMillis();
        this.lastOnlineDay = 1;
        this.lastOnlineSeason = 0;
    }

    // ── Getters ──

    public UUID getOwnerUUID() { return ownerUUID; }
    public String getOwnerName() { return ownerName; }
    public String getFarmName() { return farmName; }
    public int getSlotIndex() { return slotIndex; }
    public BlockPos getOrigin() { return origin; }
    public FarmType getFarmType() { return farmType; }
    public boolean isInitialized() { return initialized; }
    public long getCreatedTimestamp() { return createdTimestamp; }
    public int getLastOnlineDay() { return lastOnlineDay; }
    public int getLastOnlineSeason() { return lastOnlineSeason; }

    // ── Setters (mutable fields) ──

    public void setFarmName(String farmName) { this.farmName = farmName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public void markInitialized() { this.initialized = true; }
    public void setLastOnlineDay(int day) { this.lastOnlineDay = day; }
    public void setLastOnlineSeason(int season) { this.lastOnlineSeason = season; }
    public void setCreatedTimestamp(long ts) { this.createdTimestamp = ts; }

    // ── 坐标计算（从 FarmType 布局读取偏移） ──

    /** 农场出生点 */
    public BlockPos getSpawnPoint() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? origin.offset(layout.spawnOffset()) : origin;
    }

    /** 出生点朝向 */
    public float getSpawnYaw() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? layout.spawnYaw() : 90.0f;
    }

    /** 农场图腾柱位置 */
    public BlockPos getFarmTotemPos() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? origin.offset(layout.totemOffset()) : origin;
    }

    /** 温室位置 */
    public BlockPos getGreenhousePos() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? origin.offset(layout.greenhouseOffset()) : origin;
    }

    /** 从公共区域南入口进入时的传送目标 */
    public BlockPos getSouthEntryPos() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? origin.offset(layout.entrySouth().teleportOffset()) : getSpawnPoint();
    }

    public float getSouthEntryYaw() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? layout.entrySouth().yaw() : 90.0f;
    }

    /** 从公共区域东入口进入时的传送目标 */
    public BlockPos getEastEntryPos() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? origin.offset(layout.entryEast().teleportOffset()) : getSpawnPoint();
    }

    public float getEastEntryYaw() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? layout.entryEast().yaw() : -90.0f;
    }

    /** 从公共区域西入口进入时的传送目标 */
    public BlockPos getWestEntryPos() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? origin.offset(layout.entryWest().teleportOffset()) : getSpawnPoint();
    }

    public float getWestEntryYaw() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? layout.entryWest().yaw() : 180.0f;
    }

    /** 农场区域边界最小坐标 */
    public BlockPos getFarmBoundsMin() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? origin.offset(layout.boundsMin()) : origin;
    }

    /** 农场区域边界最大坐标 */
    public BlockPos getFarmBoundsMax() {
        FarmType.FarmLayout layout = farmType.getLayout();
        return layout != null ? origin.offset(layout.boundsMax()) : origin.offset(336, 22, 381);
    }

    /** 判断某位置是否在此农场边界内 */
    public boolean contains(BlockPos pos) {
        BlockPos min = getFarmBoundsMin();
        BlockPos max = getFarmBoundsMax();
        return pos.getX() >= min.getX() && pos.getX() <= max.getX()
            && pos.getY() >= min.getY() && pos.getY() <= max.getY()
            && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
    }

    // ── NBT 序列化 ──

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("OwnerUUID", ownerUUID);
        tag.putString("OwnerName", ownerName);
        tag.putString("FarmName", farmName);
        tag.putInt("SlotIndex", slotIndex);
        tag.putInt("OriginX", origin.getX());
        tag.putInt("OriginY", origin.getY());
        tag.putInt("OriginZ", origin.getZ());
        tag.putString("FarmType", farmType.getId());
        tag.putBoolean("Initialized", initialized);
        tag.putLong("CreatedTimestamp", createdTimestamp);
        tag.putInt("LastOnlineDay", lastOnlineDay);
        tag.putInt("LastOnlineSeason", lastOnlineSeason);
        return tag;
    }

    public static FarmInstance load(CompoundTag tag) {
        UUID uuid = tag.getUUID("OwnerUUID");
        String ownerName = tag.getString("OwnerName");
        String farmName = tag.getString("FarmName");
        int slotIndex = tag.getInt("SlotIndex");
        BlockPos origin = new BlockPos(tag.getInt("OriginX"), tag.getInt("OriginY"), tag.getInt("OriginZ"));
        FarmType farmType = FarmType.fromId(tag.getString("FarmType"));

        FarmInstance instance = new FarmInstance(uuid, ownerName, farmName, slotIndex, origin, farmType);
        instance.initialized = tag.getBoolean("Initialized");
        instance.createdTimestamp = tag.getLong("CreatedTimestamp");
        instance.lastOnlineDay = tag.getInt("LastOnlineDay");
        instance.lastOnlineSeason = tag.getInt("LastOnlineSeason");
        return instance;
    }
}
