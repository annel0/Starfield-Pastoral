package com.stardew.craft.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.List;

/**
 * SDV SlayMonsterQuest — 讨伐怪物任务
 */
@SuppressWarnings("null")
public class SlayMonsterQuest extends StardewQuest {

    private String monsterName = "";
    private String targetNpc = "";
    private int numberToKill;
    private int numberKilled;
    private int reward;

    public SlayMonsterQuest() {
        this.questType = TYPE_MONSTER;
    }

    @Override
    public void onMonsterSlain(ServerPlayer player, String monsterType) {
        if (completed || !accepted) return;
        if (monsterName.equals(monsterType) && numberKilled < numberToKill) {
            numberKilled = Math.min(numberToKill, numberKilled + 1);
            if (numberKilled >= numberToKill) {
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
     * SDV two-phase completion: after killing enough monsters, talk to target NPC to finish.
     */
    @Override
    public void onNpcSocialized(ServerPlayer player, String npcId) {
        if (completed || !accepted) return;
        if (numberKilled >= numberToKill && targetNpc != null && targetNpc.equals(npcId)) {
            moneyReward = reward;
            questComplete(player);
        }
    }

    @Override
    public List<String> getObjectiveDescriptions() {
        if (numberKilled >= numberToKill && targetNpc != null && !targetNpc.isEmpty()) {
            return Collections.singletonList("回去找 " + targetNpc + " 复命");
        }
        return Collections.singletonList(
            monsterName + " " + numberKilled + "/" + numberToKill
        );
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putString("MonsterName", monsterName);
        tag.putString("TargetNpc", targetNpc);
        tag.putInt("NumberToKill", numberToKill);
        tag.putInt("NumberKilled", numberKilled);
        tag.putInt("Reward", reward);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        monsterName = tag.getString("MonsterName");
        targetNpc = tag.getString("TargetNpc");
        numberToKill = tag.getInt("NumberToKill");
        numberKilled = tag.getInt("NumberKilled");
        reward = tag.getInt("Reward");
    }

    public String getMonsterName() { return monsterName; }
    public void setMonsterName(String monsterName) { this.monsterName = monsterName; }
    public String getTargetNpc() { return targetNpc; }
    public void setTargetNpc(String targetNpc) { this.targetNpc = targetNpc; }
    public int getNumberToKill() { return numberToKill; }
    public void setNumberToKill(int numberToKill) { this.numberToKill = numberToKill; }
    public int getNumberKilled() { return numberKilled; }
    @Override public int getCurrentObjectiveCount() { return numberKilled; }
    @Override public int getTotalObjectiveCount() { return numberToKill; }
    public void setNumberKilled(int numberKilled) { this.numberKilled = numberKilled; }
    public int getReward() { return reward; }
    public void setReward(int reward) { this.reward = reward; }
}
