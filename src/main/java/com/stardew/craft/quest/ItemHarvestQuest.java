package com.stardew.craft.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.List;

/**
 * SDV ItemHarvestQuest — 物品收获任务
 * 收获指定数量的物品
 */
@SuppressWarnings("null")
public class ItemHarvestQuest extends StardewQuest {

    private String itemId = "";
    private int number;
    private int numberHarvested;

    public ItemHarvestQuest() {
        this.questType = TYPE_HARVEST;
    }

    @Override
    public void onItemReceived(ServerPlayer player, String receivedItemId, int count) {
        if (completed || !accepted) return;
        if (itemId.equals(receivedItemId)) {
            numberHarvested += count;
            if (numberHarvested >= number) {
                questComplete(player);
            }
        }
    }

    @Override
    public List<String> getObjectiveDescriptions() {
        return Collections.singletonList(
            objectiveText.isEmpty()
                ? (itemId + " " + numberHarvested + "/" + number)
                : objectiveText
        );
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putString("ItemId", itemId);
        tag.putInt("Number", number);
        tag.putInt("NumberHarvested", numberHarvested);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        itemId = tag.getString("ItemId");
        number = tag.getInt("Number");
        numberHarvested = tag.getInt("NumberHarvested");
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public int getNumberHarvested() { return numberHarvested; }
    @Override public int getCurrentObjectiveCount() { return numberHarvested; }
    @Override public int getTotalObjectiveCount() { return number; }
    public void setNumberHarvested(int numberHarvested) { this.numberHarvested = numberHarvested; }
}
