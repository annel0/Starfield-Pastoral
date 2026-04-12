package com.stardew.craft.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/**
 * SDV HaveBuildingQuest — 建造建筑任务
 */
@SuppressWarnings("null")
public class HaveBuildingQuest extends StardewQuest {

    private String buildingType = "";

    public HaveBuildingQuest() {
        this.questType = TYPE_BUILDING;
    }

    @Override
    public void onBuildingExists(ServerPlayer player, String existingBuildingType) {
        if (completed || !accepted) return;
        if (buildingType.equals(existingBuildingType)) {
            questComplete(player);
        }
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putString("BuildingType", buildingType);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        buildingType = tag.getString("BuildingType");
    }

    public String getBuildingType() { return buildingType; }
    public void setBuildingType(String buildingType) { this.buildingType = buildingType; }
}
