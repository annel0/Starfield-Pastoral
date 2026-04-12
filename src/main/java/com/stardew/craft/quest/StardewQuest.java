package com.stardew.craft.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SDV Quest 基类 — 1:1 复刻 StardewValley.Quests.Quest
 */
@SuppressWarnings("null")
public class StardewQuest {

    // ─── 类型常量（SDV Quest.type_xxx） ───
    public static final int TYPE_BASIC     = 1;
    public static final int TYPE_CRAFTING  = 2;
    public static final int TYPE_DELIVERY  = 3;
    public static final int TYPE_MONSTER   = 4;
    public static final int TYPE_SOCIALIZE = 5;
    public static final int TYPE_LOCATION  = 6;
    public static final int TYPE_FISHING   = 7;
    public static final int TYPE_BUILDING  = 8;
    public static final int TYPE_HARVEST   = 9;
    public static final int TYPE_RESOURCE  = 10;
    public static final int TYPE_WEEDING   = 11;

    // ─── 核心字段（SDV NetFields） ───
    protected String id = "";
    protected int questType = TYPE_BASIC;
    protected String title = "";
    protected String description = "";
    protected String objectiveText = "";
    @Nullable
    protected String rewardDescription;
    protected int moneyReward;
    protected boolean accepted;
    protected boolean completed;
    protected boolean dailyQuest;
    protected boolean showNew;
    protected boolean canBeCancelled;
    protected boolean destroy;
    protected boolean notifiedComplete;
    protected int daysLeft = -1;
    protected int dayQuestAccepted = -1;
    protected List<String> nextQuests = new ArrayList<>();

    // ─── 构造 ───

    public StardewQuest() {}

    public StardewQuest(String id) {
        this.id = id;
    }

    // ─── 事件回调（子类重写） ───

    public void onAccept(ServerPlayer player) {}

    public void onMonsterSlain(ServerPlayer player, String monsterType) {}

    public void onFishCaught(ServerPlayer player, String itemId, int count) {}

    public void onItemReceived(ServerPlayer player, String itemId, int count) {}

    /** @return true if this quest consumed the offered item (SDV: intercepts before gift processing) */
    public boolean onItemOfferedToNpc(ServerPlayer player, String npcId, String itemId) { return false; }

    public void onRecipeCrafted(ServerPlayer player, String recipeId) {}

    public void onNpcSocialized(ServerPlayer player, String npcId) {}

    public void onWarped(ServerPlayer player, String location) {}

    public void onBuildingExists(ServerPlayer player, String buildingType) {}

    public void onMineFloorReached(ServerPlayer player, int floor) {}

    // ─── 显示接口 ───

    public List<String> getObjectiveDescriptions() {
        if (objectiveText != null && !objectiveText.isEmpty()) {
            return Collections.singletonList(objectiveText);
        }
        return Collections.emptyList();
    }

    /** Current progress count for objectives with numeric progress, or -1 if not applicable. */
    public int getCurrentObjectiveCount() { return -1; }
    /** Total target count for objectives with numeric progress, or -1 if not applicable. */
    public int getTotalObjectiveCount() { return -1; }

    public boolean isTimedQuest() {
        return daysLeft > 0;
    }

    public boolean shouldDisplayAsNew() {
        return showNew;
    }

    public boolean shouldDisplayAsComplete() {
        return completed;
    }

    public boolean hasReward() {
        return moneyReward > 0 || (rewardDescription != null && !rewardDescription.isEmpty());
    }

    public boolean hasMoneyReward() {
        return moneyReward > 0;
    }

    // ─── 完成逻辑（SDV Quest.questComplete） ───

    public void questComplete(ServerPlayer player) {
        if (completed) return;
        completed = true;

        // dailyQuest 统计
        if (dailyQuest) {
            QuestManager mgr = QuestManager.of(player);
            if (mgr != null) {
                mgr.incrementBillboardQuestsDone();
                // 记录当季第几天完成了每日任务（日历星标用）
                try {
                    com.stardew.craft.time.StardewTimeManager tm = com.stardew.craft.time.StardewTimeManager.get();
                    if (tm != null) {
                        mgr.markDailyQuestCompletedDay(tm.getCurrentDay());
                    }
                } catch (Exception ignored) {}
            }
        }

        // 播放完成音效（由 QuestManager 发送网络包触发客户端播放）

        // 如果没有可领取的奖励，立即标记销毁
        if (moneyReward <= 0 && (rewardDescription == null || rewardDescription.isEmpty())) {
            destroy = true;
        }
    }

    // ─── 天数推进 ───

    /**
     * SDV 在天结束时递减 daysLeft。我们在天开始时调用，
     * 所以跳过接受任务当天的 tick，确保 daysLeft=2 给玩家 2 个完整天。
     */
    public void tickDay(int currentGameDay) {
        if (daysLeft > 0) {
            // 接受当天不 tick（SDV 是天结束时递减）
            if (dayQuestAccepted >= 0 && dayQuestAccepted >= currentGameDay) return;
            daysLeft--;
            if (daysLeft <= 0 && !completed) {
                destroy = true;
            }
        }
    }

    // ─── NBT 序列化 ───

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Id", id);
        tag.putInt("Type", questType);
        tag.putString("Title", title);
        tag.putString("Description", description);
        tag.putString("ObjectiveText", objectiveText);
        if (rewardDescription != null) tag.putString("RewardDescription", rewardDescription);
        tag.putInt("MoneyReward", moneyReward);
        tag.putBoolean("Accepted", accepted);
        tag.putBoolean("Completed", completed);
        tag.putBoolean("DailyQuest", dailyQuest);
        tag.putBoolean("ShowNew", showNew);
        tag.putBoolean("CanBeCancelled", canBeCancelled);
        tag.putBoolean("Destroy", destroy);
        tag.putBoolean("NotifiedComplete", notifiedComplete);
        tag.putInt("DaysLeft", daysLeft);
        tag.putInt("DayQuestAccepted", dayQuestAccepted);
        if (!nextQuests.isEmpty()) {
            ListTag list = new ListTag();
            for (String nq : nextQuests) list.add(StringTag.valueOf(nq));
            tag.put("NextQuests", list);
        }
        saveExtra(tag);
        return tag;
    }

    /** 子类重写以保存额外字段 */
    protected void saveExtra(CompoundTag tag) {}

    /** 子类重写以加载额外字段 */
    protected void loadExtra(CompoundTag tag) {}

    public static StardewQuest load(CompoundTag tag) {
        int type = tag.getInt("Type");
        StardewQuest quest = createByType(type);
        quest.id = tag.getString("Id");
        quest.questType = type;
        quest.title = tag.getString("Title");
        quest.description = tag.getString("Description");
        quest.objectiveText = tag.getString("ObjectiveText");
        quest.rewardDescription = tag.contains("RewardDescription") ? tag.getString("RewardDescription") : null;
        quest.moneyReward = tag.getInt("MoneyReward");
        quest.accepted = tag.getBoolean("Accepted");
        quest.completed = tag.getBoolean("Completed");
        quest.dailyQuest = tag.getBoolean("DailyQuest");
        quest.showNew = tag.getBoolean("ShowNew");
        quest.canBeCancelled = tag.getBoolean("CanBeCancelled");
        quest.destroy = tag.getBoolean("Destroy");
        quest.notifiedComplete = tag.getBoolean("NotifiedComplete");
        quest.daysLeft = tag.getInt("DaysLeft");
        quest.dayQuestAccepted = tag.getInt("DayQuestAccepted");
        if (tag.contains("NextQuests", 9)) {
            ListTag list = tag.getList("NextQuests", 8);
            for (int i = 0; i < list.size(); i++) {
                quest.nextQuests.add(list.getString(i));
            }
        }
        quest.loadExtra(tag);
        return quest;
    }

    private static StardewQuest createByType(int type) {
        return switch (type) {
            case TYPE_CRAFTING  -> new CraftingQuest();
            case TYPE_DELIVERY  -> new ItemDeliveryQuest();
            case TYPE_MONSTER   -> new SlayMonsterQuest();
            case TYPE_SOCIALIZE -> new SocializeQuest();
            case TYPE_LOCATION  -> new GoSomewhereQuest();
            case TYPE_FISHING   -> new FishingQuest();
            case TYPE_BUILDING  -> new HaveBuildingQuest();
            case TYPE_HARVEST   -> new ItemHarvestQuest();
            case TYPE_RESOURCE  -> new ResourceCollectionQuest();
            default             -> new StardewQuest();
        };
    }

    // ─── Getters / Setters ───

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getQuestType() { return questType; }
    public void setQuestType(int questType) { this.questType = questType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getObjectiveText() { return objectiveText; }
    public void setObjectiveText(String objectiveText) { this.objectiveText = objectiveText; }
    @Nullable public String getRewardDescription() { return rewardDescription; }
    public void setRewardDescription(@Nullable String rewardDescription) { this.rewardDescription = rewardDescription; }
    public int getMoneyReward() { return moneyReward; }
    public void setMoneyReward(int moneyReward) { this.moneyReward = moneyReward; }
    public boolean isAccepted() { return accepted; }
    public void setAccepted(boolean accepted) { this.accepted = accepted; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public boolean isDailyQuest() { return dailyQuest; }
    public void setDailyQuest(boolean dailyQuest) { this.dailyQuest = dailyQuest; }
    public boolean isShowNew() { return showNew; }
    public void setShowNew(boolean showNew) { this.showNew = showNew; }
    public boolean isCanBeCancelled() { return canBeCancelled; }
    public void setCanBeCancelled(boolean canBeCancelled) { this.canBeCancelled = canBeCancelled; }
    public boolean isDestroy() { return destroy; }
    public void setDestroy(boolean destroy) { this.destroy = destroy; }
    public boolean isNotifiedComplete() { return notifiedComplete; }
    public void setNotifiedComplete(boolean notifiedComplete) { this.notifiedComplete = notifiedComplete; }
    public int getDaysLeft() { return daysLeft; }
    public void setDaysLeft(int daysLeft) { this.daysLeft = daysLeft; }
    public int getDayQuestAccepted() { return dayQuestAccepted; }
    public void setDayQuestAccepted(int dayQuestAccepted) { this.dayQuestAccepted = dayQuestAccepted; }
    public List<String> getNextQuests() { return nextQuests; }
    public void setNextQuests(List<String> nextQuests) { this.nextQuests = nextQuests; }
}
