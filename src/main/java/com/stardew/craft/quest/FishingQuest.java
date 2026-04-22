package com.stardew.craft.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
    public List<Component> getObjectiveComponents() {
        // 对象完成等待 NPC 复命阶段：单独一条 "回去找 XX 复命"
        if (numberFished >= numberToFish && targetNpc != null && !targetNpc.isEmpty()) {
            return Collections.singletonList(Component.translatable(
                    "stardewcraft.quest.report_to",
                    Component.translatable("entity.stardewcraft.npc." + targetNpc)));
        }
        // 进度阶段：用本地化键 + 当前 N/M 实时构造
        if (objectiveKey != null && !objectiveKey.isEmpty() && objectiveArgs.size() >= 2) {
            // args: [count, itemKey, progress] → 替换 progress 为当前 numberFished
            return Collections.singletonList(Component.translatable(
                    objectiveKey,
                    String.valueOf(numberToFish),
                    Component.translatable(objectiveArgs.get(1)),
                    String.valueOf(numberFished)));
        }
        return super.getObjectiveComponents();
    }

    @Override
    public List<String> getObjectiveDescriptions() {
        List<Component> comps = getObjectiveComponents();
        List<String> out = new java.util.ArrayList<>(comps.size());
        for (Component c : comps) out.add(c.getString());
        return out;
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
