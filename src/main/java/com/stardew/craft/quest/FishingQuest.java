package com.stardew.craft.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.List;

/**
 * SDV FishingQuest — 钓鱼任务
 */
@SuppressWarnings("null")
public class FishingQuest extends StardewQuest {

    private String targetNpc = "";
    private String itemId = "";
    private int numberToFish;
    private int numberFished;
    private int reward;

    public FishingQuest() {
        this.questType = TYPE_FISHING;
    }

    @Override
    public void onFishCaught(ServerPlayer player, String caughtItemId, int count) {
        if (completed || !accepted) return;
        if (itemId.equals(caughtItemId) && numberFished < numberToFish) {
            numberFished = Math.min(numberToFish, numberFished + count);
            if (numberFished >= numberToFish) {
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
     * SDV two-phase completion: after catching enough fish, talk to target NPC to finish.
     */
    @Override
    public void onNpcSocialized(ServerPlayer player, String npcId) {
        if (completed || !accepted) return;
        if (numberFished >= numberToFish && targetNpc != null && targetNpc.equals(npcId)) {
            moneyReward = reward;
            questComplete(player);
        }
    }

    @Override
    public List<String> getObjectiveDescriptions() {
        if (numberFished >= numberToFish && targetNpc != null && !targetNpc.isEmpty()) {
            return Collections.singletonList("回去找 " + targetNpc + " 复命");
        }
        return Collections.singletonList(
            objectiveText.isEmpty()
                ? (itemId + " " + numberFished + "/" + numberToFish)
                : objectiveText
        );
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putString("TargetNpc", targetNpc);
        tag.putString("ItemId", itemId);
        tag.putInt("NumberToFish", numberToFish);
        tag.putInt("NumberFished", numberFished);
        tag.putInt("Reward", reward);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        targetNpc = tag.getString("TargetNpc");
        itemId = tag.getString("ItemId");
        numberToFish = tag.getInt("NumberToFish");
        numberFished = tag.getInt("NumberFished");
        reward = tag.getInt("Reward");
    }

    public String getTargetNpc() { return targetNpc; }
    public void setTargetNpc(String targetNpc) { this.targetNpc = targetNpc; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public int getNumberToFish() { return numberToFish; }
    public void setNumberToFish(int numberToFish) { this.numberToFish = numberToFish; }
    public int getNumberFished() { return numberFished; }
    @Override public int getCurrentObjectiveCount() { return numberFished; }
    @Override public int getTotalObjectiveCount() { return numberToFish; }
    public void setNumberFished(int numberFished) { this.numberFished = numberFished; }
    public int getReward() { return reward; }
    public void setReward(int reward) { this.reward = reward; }
}
