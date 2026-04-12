package com.stardew.craft.quest.network;

import com.stardew.craft.quest.StardewQuest;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 客户端任务数据缓存 — 由 S→C 同步包更新，UI 读取此处数据
 */
@OnlyIn(Dist.CLIENT)
public final class ClientQuestData {

    private static final List<StardewQuest> questLog = new ArrayList<>();
    @Nullable
    private static StardewQuest dailyQuest;
    private static int billboardQuestsDone;
    private static final Set<Integer> dailyQuestCompletedDays = new HashSet<>();

    private ClientQuestData() {}

    // ─── 写入（由网络包 handler 调用） ───

    public static void setQuestLog(List<StardewQuest> quests) {
        // 保留客户端已设置的 showNew=false 状态，避免服务端同步覆盖
        java.util.Set<String> viewedIds = new java.util.HashSet<>();
        for (StardewQuest old : questLog) {
            if (!old.isShowNew()) {
                viewedIds.add(old.getId());
            }
        }
        questLog.clear();
        for (StardewQuest q : quests) {
            if (viewedIds.contains(q.getId())) {
                q.setShowNew(false);
            }
            questLog.add(q);
        }
    }

    public static void setDailyQuest(@Nullable StardewQuest quest) {
        dailyQuest = quest;
    }

    public static void setBillboardQuestsDone(int count) {
        billboardQuestsDone = count;
    }

    public static void setDailyQuestCompletedDays(Set<Integer> days) {
        dailyQuestCompletedDays.clear();
        dailyQuestCompletedDays.addAll(days);
    }

    /** 标记某个任务完成（收到 QuestCompletePayload 时调用） */
    public static void markCompleted(String questId) {
        for (StardewQuest q : questLog) {
            if (q.getId().equals(questId)) {
                q.setCompleted(true);
                break;
            }
        }
    }

    /** 清空（断线/登出时） */
    public static void clear() {
        questLog.clear();
        dailyQuest = null;
        billboardQuestsDone = 0;
        dailyQuestCompletedDays.clear();
    }

    // ─── 读取（UI 调用） ───

    public static List<StardewQuest> getQuestLog() {
        return Collections.unmodifiableList(questLog);
    }

    @Nullable
    public static StardewQuest getDailyQuest() {
        return dailyQuest;
    }

    public static int getBillboardQuestsDone() {
        return billboardQuestsDone;
    }

    public static boolean isDailyQuestCompletedOnDay(int day) {
        return dailyQuestCompletedDays.contains(day);
    }

    public static boolean hasQuest(String questId) {
        for (StardewQuest q : questLog) {
            if (q.getId().equals(questId)) return true;
        }
        return false;
    }
}
