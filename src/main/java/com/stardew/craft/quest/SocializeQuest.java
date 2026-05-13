package com.stardew.craft.quest;

import com.stardew.craft.npc.data.NpcCapabilityProfile;
import com.stardew.craft.npc.data.NpcDataRegistry;
import com.stardew.craft.npc.runtime.NpcFriendshipDataManager;
import com.stardew.craft.npc.runtime.NpcInteractionService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SDV SocializeQuest — 社交任务
 * 与列表中所有NPC打招呼
 */
@SuppressWarnings("null")
public class SocializeQuest extends StardewQuest {

    private final List<String> whoToGreet = new ArrayList<>();
    private int total;

    public SocializeQuest() {
        this.questType = TYPE_SOCIALIZE;
    }

    /**
     * SDV SocializeQuest.loadQuestInfo() — 首次接受时用注册表中的 NPC 填充 whoToGreet。
     * SDV 筛选逻辑: IntroductionsQuest flag 或 HomeRegion=="Town"，排除 CanSocialize=false。
     * 当前实现用“已实装且参与日常移动/社交”的 NPC 作为 Introductions 名单，
     * 排除 Gunther/Marlon/Wizard/Dwarf/Sandy/Morris 这类静态或未进入日常社交循环的人物。
     */
    @Override
    public void onAccept(ServerPlayer player) {
        if (whoToGreet.isEmpty()) {
            loadQuestInfo();
        } else {
            sanitizeWhoToGreet();
        }
    }

    private void loadQuestInfo() {
        whoToGreet.clear();
        for (var entry : NpcDataRegistry.capabilities().entrySet()) {
            NpcCapabilityProfile profile = entry.getValue();
            if (!isIntroductionsNpc(profile)) continue;
            whoToGreet.add(entry.getKey());
        }
        total = whoToGreet.size();
    }

    private static boolean isIntroductionsNpc(NpcCapabilityProfile profile) {
        return profile != null && profile.canRunPathing();
    }

    private void sanitizeWhoToGreet() {
        int before = whoToGreet.size();
        whoToGreet.removeIf(npcId -> !isIntroductionsNpc(NpcDataRegistry.capabilities().get(npcId)));
        int removed = before - whoToGreet.size();
        if (removed > 0) {
            total = Math.max(whoToGreet.size(), total - removed);
        }
    }

    @Override
    public void onNpcSocialized(ServerPlayer player, String npcId) {
        if (completed || !accepted) return;
        if (whoToGreet.remove(npcId)) {
            if (whoToGreet.isEmpty()) {
                // SDV: give +100 friendship to all known NPCs on quest complete
                if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    NpcFriendshipDataManager friendshipMgr = NpcFriendshipDataManager.get(serverLevel);
                    for (var entry : NpcDataRegistry.capabilities().entrySet()) {
                        if (!isIntroductionsNpc(entry.getValue())) continue;
                        String knownNpc = entry.getKey();
                        NpcFriendshipDataManager.FriendshipState fs = friendshipMgr.getOrCreate(player.getUUID(), knownNpc);
                        int max = NpcInteractionService.getMaxFriendshipPointsFor(knownNpc);
                        if (fs.points() < max) {
                            fs.addPoints(100, max);
                        }
                    }
                    friendshipMgr.setDirty();
                }
                questComplete(player);
            }
        }
    }

    @Override
    public List<String> getObjectiveDescriptions() {
        int greeted = total - whoToGreet.size();
        return Collections.singletonList(
            objectiveText.isEmpty()
                ? ("打招呼 " + greeted + "/" + total)
                : objectiveText
        );
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putInt("Total", total);
        ListTag list = new ListTag();
        for (String npc : whoToGreet) list.add(StringTag.valueOf(npc));
        tag.put("WhoToGreet", list);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        total = tag.getInt("Total");
        whoToGreet.clear();
        if (tag.contains("WhoToGreet", 9)) {
            ListTag list = tag.getList("WhoToGreet", 8);
            for (int i = 0; i < list.size(); i++) {
                whoToGreet.add(list.getString(i));
            }
        }
        sanitizeWhoToGreet();
    }

    public List<String> getWhoToGreet() { return whoToGreet; }
    public int getTotal() { return total; }
    @Override public int getCurrentObjectiveCount() { return total - whoToGreet.size(); }
    @Override public int getTotalObjectiveCount() { return total; }
    public void setTotal(int total) { this.total = total; }
}
