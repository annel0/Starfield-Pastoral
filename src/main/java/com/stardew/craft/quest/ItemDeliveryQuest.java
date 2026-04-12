package com.stardew.craft.quest;

import com.stardew.craft.npc.runtime.NpcFriendshipDataManager;
import com.stardew.craft.npc.runtime.NpcInteractionService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.List;

/**
 * SDV ItemDeliveryQuest — 物品交付任务
 * 将指定物品交给指定NPC
 */
@SuppressWarnings("null")
public class ItemDeliveryQuest extends StardewQuest {

    private String targetNpc = "";
    private String itemId = "";
    private int number = 1;
    private int numberDelivered;
    private String targetMessage = "";

    public ItemDeliveryQuest() {
        this.questType = TYPE_DELIVERY;
    }

    @Override
    public boolean onItemOfferedToNpc(ServerPlayer player, String npcId, String offeredItemId) {
        if (completed || !accepted) return false;
        if (targetNpc.equals(npcId) && itemId.equals(offeredItemId)) {
            numberDelivered++;
            if (numberDelivered >= number) {
                questComplete(player);
            }
            return true; // SDV: quest intercepts item, skip gift taste processing
        }
        return false;
    }

    @Override
    public void questComplete(ServerPlayer player) {
        if (completed) return;
        super.questComplete(player);
        // SDV: dailyQuest gives +150, story quest gives +255
        if (!targetNpc.isEmpty() && player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            NpcFriendshipDataManager friendshipMgr = NpcFriendshipDataManager.get(serverLevel);
            NpcFriendshipDataManager.FriendshipState fs = friendshipMgr.getOrCreate(player.getUUID(), targetNpc);
            int friendshipBonus = dailyQuest ? 150 : 255;
            fs.addPoints(friendshipBonus, NpcInteractionService.getMaxFriendshipPointsFor(targetNpc));
            friendshipMgr.setDirty();
        }
    }

    @Override
    public int getCurrentObjectiveCount() { return number > 1 ? numberDelivered : -1; }
    @Override
    public int getTotalObjectiveCount() { return number > 1 ? number : -1; }

    @Override
    public List<String> getObjectiveDescriptions() {
        if (!objectiveText.isEmpty()) {
            return Collections.singletonList(objectiveText);
        }
        if (number > 1) {
            return Collections.singletonList("将 " + number + " 个物品交给 " + targetNpc + " (" + numberDelivered + "/" + number + ")");
        }
        return Collections.singletonList("将物品交给 " + targetNpc);
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putString("TargetNpc", targetNpc);
        tag.putString("ItemId", itemId);
        tag.putInt("Number", number);
        tag.putInt("NumberDelivered", numberDelivered);
        tag.putString("TargetMessage", targetMessage);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        targetNpc = tag.getString("TargetNpc");
        itemId = tag.getString("ItemId");
        number = tag.getInt("Number");
        numberDelivered = tag.getInt("NumberDelivered");
        targetMessage = tag.getString("TargetMessage");
    }

    // ─── Getters / Setters ───
    public String getTargetNpc() { return targetNpc; }
    public void setTargetNpc(String targetNpc) { this.targetNpc = targetNpc; }
    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getTargetMessage() { return targetMessage; }
    public void setTargetMessage(String targetMessage) { this.targetMessage = targetMessage; }
}
