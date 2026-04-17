package com.stardew.craft.animal.model;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class AnimalBuildingRecord {
    private final String buildingId;
    private final String ownerPlayerUuid;
    private final AnimalBuildingType buildingType;
    private String customName;
    private final String dimensionId;
    private final BlockPos managerPos;
    private final int range;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;
    private final int capacity;
    private final int hayCapacity;
    private final boolean active;
    private boolean doorOpen;
    private final Set<Long> interiorAirCells;
    private final Set<Long> boundaryDoorCells;
    private final Set<Long> memberAnimalIds;

    public AnimalBuildingRecord(String buildingId,
                                String ownerPlayerUuid,
                                AnimalBuildingType buildingType,
                                String customName,
                                String dimensionId,
                                BlockPos managerPos,
                                int range,
                                int minX,
                                int minY,
                                int minZ,
                                int maxX,
                                int maxY,
                                int maxZ,
                                int capacity,
                                int hayCapacity,
                                boolean active,
                                boolean doorOpen,
                                Set<Long> interiorAirCells,
                                Set<Long> boundaryDoorCells,
                                Set<Long> memberAnimalIds) {
        this.buildingId = buildingId;
        this.ownerPlayerUuid = ownerPlayerUuid;
        this.buildingType = buildingType;
        this.customName = customName;
        this.dimensionId = dimensionId;
        this.managerPos = managerPos;
        this.range = range;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.capacity = capacity;
        this.hayCapacity = hayCapacity;
        this.active = active;
        this.doorOpen = doorOpen;
        this.interiorAirCells = interiorAirCells == null ? new LinkedHashSet<>() : new LinkedHashSet<>(interiorAirCells);
        this.boundaryDoorCells = boundaryDoorCells == null ? new LinkedHashSet<>() : new LinkedHashSet<>(boundaryDoorCells);
        this.memberAnimalIds = memberAnimalIds == null ? new LinkedHashSet<>() : new LinkedHashSet<>(memberAnimalIds);
    }

    public String buildingId() {
        return buildingId;
    }

    public String ownerPlayerUuid() {
        return ownerPlayerUuid;
    }

    public AnimalBuildingType buildingType() {
        return buildingType;
    }

    public String customName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String dimensionId() {
        return dimensionId;
    }

    public BlockPos managerPos() {
        return managerPos;
    }

    public int range() {
        return range;
    }

    public int minX() {
        return minX;
    }

    public int minY() {
        return minY;
    }

    public int minZ() {
        return minZ;
    }

    public int maxX() {
        return maxX;
    }

    public int maxY() {
        return maxY;
    }

    public int maxZ() {
        return maxZ;
    }

    public int capacity() {
        return capacity;
    }

    public int hayCapacity() {
        return hayCapacity;
    }

    public boolean active() {
        return active;
    }

    public boolean doorOpen() {
        return doorOpen;
    }

    public void setDoorOpen(boolean doorOpen) {
        this.doorOpen = doorOpen;
    }

    public Set<Long> memberAnimalIds() {
        return Collections.unmodifiableSet(memberAnimalIds);
    }

    public Set<Long> interiorAirCells() {
        return Collections.unmodifiableSet(interiorAirCells);
    }

    public Set<Long> boundaryDoorCells() {
        return Collections.unmodifiableSet(boundaryDoorCells);
    }

    public boolean isInBounds(BlockPos pos) {
        if (!interiorAirCells.isEmpty()) {
            return interiorAirCells.contains(pos.asLong());
        }
        return isWithinBoundingBox(pos);
    }

    public boolean isWithinBoundingBox(BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return x >= minX && x <= maxX
            && y >= minY && y <= maxY
            && z >= minZ && z <= maxZ;
    }

    public boolean isBoundaryDoor(BlockPos pos) {
        if (!boundaryDoorCells.isEmpty()) {
            return boundaryDoorCells.contains(pos.asLong());
        }
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        return x >= minX - 1 && x <= maxX + 1
            && y >= minY - 1 && y <= maxY + 1
            && z >= minZ - 1 && z <= maxZ + 1;
    }

    public boolean hasCapacity() {
        return memberAnimalIds.size() < capacity;
    }

    public void addAnimal(long animalId) {
        memberAnimalIds.add(animalId);
    }

    public void removeAnimal(long animalId) {
        memberAnimalIds.remove(animalId);
    }

    @SuppressWarnings("null")
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("buildingId", buildingId);
        tag.putString("ownerPlayerUuid", ownerPlayerUuid);
        tag.putString("buildingType", buildingType.id());
        tag.putString("customName", customName);
        tag.putString("dimensionId", dimensionId);
        tag.put("managerPos", NbtUtils.writeBlockPos(managerPos));
        tag.putInt("range", range);
        tag.putInt("minX", minX);
        tag.putInt("minY", minY);
        tag.putInt("minZ", minZ);
        tag.putInt("maxX", maxX);
        tag.putInt("maxY", maxY);
        tag.putInt("maxZ", maxZ);
        tag.putInt("capacity", capacity);
        tag.putInt("hayCapacity", hayCapacity);
        tag.putBoolean("active", active);
        tag.putBoolean("doorOpen", doorOpen);

        ListTag interiorTag = new ListTag();
        for (Long cell : interiorAirCells) {
            CompoundTag cellTag = new CompoundTag();
            cellTag.putLong("cell", cell);
            interiorTag.add(cellTag);
        }
        tag.put("interiorAirCells", interiorTag);

        ListTag doorTag = new ListTag();
        for (Long door : boundaryDoorCells) {
            CompoundTag doorCellTag = new CompoundTag();
            doorCellTag.putLong("door", door);
            doorTag.add(doorCellTag);
        }
        tag.put("boundaryDoorCells", doorTag);

        ListTag membersTag = new ListTag();
        for (Long memberAnimalId : memberAnimalIds) {
            CompoundTag memberTag = new CompoundTag();
            memberTag.putLong("animalId", memberAnimalId);
            membersTag.add(memberTag);
        }
        tag.put("memberAnimalIds", membersTag);

        return tag;
    }

    public static AnimalBuildingRecord load(CompoundTag tag) {
        Set<Long> interiorAirCells = new LinkedHashSet<>();
        ListTag interiorTag = tag.getList("interiorAirCells", Tag.TAG_COMPOUND);
        for (int i = 0; i < interiorTag.size(); i++) {
            CompoundTag cellTag = interiorTag.getCompound(i);
            interiorAirCells.add(cellTag.getLong("cell"));
        }

        Set<Long> boundaryDoorCells = new LinkedHashSet<>();
        ListTag doorTag = tag.getList("boundaryDoorCells", Tag.TAG_COMPOUND);
        for (int i = 0; i < doorTag.size(); i++) {
            CompoundTag doorCellTag = doorTag.getCompound(i);
            boundaryDoorCells.add(doorCellTag.getLong("door"));
        }

        Set<Long> memberIds = new LinkedHashSet<>();
        ListTag membersTag = tag.getList("memberAnimalIds", Tag.TAG_COMPOUND);
        for (int i = 0; i < membersTag.size(); i++) {
            CompoundTag memberTag = membersTag.getCompound(i);
            memberIds.add(memberTag.getLong("animalId"));
        }

        return new AnimalBuildingRecord(
            tag.getString("buildingId"),
            tag.contains("ownerPlayerUuid") ? tag.getString("ownerPlayerUuid") : "",
            AnimalBuildingType.fromId(tag.getString("buildingType")),
            tag.getString("customName"),
            tag.getString("dimensionId"),
            NbtUtils.readBlockPos(tag, "managerPos").orElse(BlockPos.ZERO),
            tag.getInt("range"),
            tag.getInt("minX"),
            tag.getInt("minY"),
            tag.getInt("minZ"),
            tag.getInt("maxX"),
            tag.getInt("maxY"),
            tag.getInt("maxZ"),
            tag.getInt("capacity"),
            tag.contains("hayCapacity") ? tag.getInt("hayCapacity") : 0,
            !tag.contains("active") || tag.getBoolean("active"),
            tag.getBoolean("doorOpen"),
            interiorAirCells,
            boundaryDoorCells,
            memberIds
        );
    }
}
