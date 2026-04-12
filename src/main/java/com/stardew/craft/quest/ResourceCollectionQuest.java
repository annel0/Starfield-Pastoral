package com.stardew.craft.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.List;

/**
 * SDV ResourceCollectionQuest — 资源收集任务
 * 收集指定数量的资源（铜/铁/金/铱/木/石）
 */
@SuppressWarnings("null")
public class ResourceCollectionQuest extends StardewQuest {

    private String targetNpc = "";
    private String itemId = "";
    private int number;
    private int numberCollected;
    private int reward;

    public ResourceCollectionQuest() {
        this.questType = TYPE_RESOURCE;
    }

    @Override
    public void onItemReceived(ServerPlayer player, String receivedItemId, int count) {
        if (completed || !accepted) return;
        if (itemId.equals(receivedItemId) && numberCollected < number) {
            numberCollected = Math.min(number, numberCollected + count);
            if (numberCollected >= number) {
                // SDV: don't questComplete yet if there's a target NPC — enter report phase
                if (targetNpc == null || targetNpc.isEmpty()) {
                    moneyReward = reward;
                    questComplete(player);
                }
                // else: wait for onNpcSocialized (report phase)
            }
        }
    }

    /**
     * SDV two-phase completion: after collecting enough resources, talk to target NPC to finish.
     */
    @Override
    public void onNpcSocialized(ServerPlayer player, String npcId) {
        if (completed || !accepted) return;
        if (numberCollected >= number && targetNpc != null && targetNpc.equals(npcId)) {
            moneyReward = reward;
            questComplete(player);
        }
    }

    @Override
    public List<String> getObjectiveDescriptions() {
        if (numberCollected >= number && targetNpc != null && !targetNpc.isEmpty()) {
            return Collections.singletonList("回去找 " + targetNpc + " 复命");
        }
        return Collections.singletonList(
            objectiveText.isEmpty()
                ? (itemId + " " + numberCollected + "/" + number)
                : objectiveText
        );
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putString("TargetNpc", targetNpc);
        tag.putString("ItemId", itemId);
        tag.putInt("Number", number);
        tag.putInt("NumberCollected", numberCollected);
        tag.putInt("Reward", reward);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        targetNpc = tag.getString("TargetNpc");
        itemId = tag.getString("ItemId");
        number = tag.getInt("Number");
        numberCollected = tag.getInt("NumberCollected");
        reward = tag.getInt("Reward");
    }

    public String getTargetNpc() { return targetNpc; }
    public void setTargetNpc(String targetNpc) { this.targetNpc = targetNpc; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public int getNumberCollected() { return numberCollected; }
    @Override public int getCurrentObjectiveCount() { return numberCollected; }
    @Override public int getTotalObjectiveCount() { return number; }
    public void setNumberCollected(int numberCollected) { this.numberCollected = numberCollected; }
    public int getReward() { return reward; }
    public void setReward(int reward) { this.reward = reward; }
}
