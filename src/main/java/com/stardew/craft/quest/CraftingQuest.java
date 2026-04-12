package com.stardew.craft.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/**
 * SDV CraftingQuest — 合成任务
 * 合成指定物品即完成
 */
@SuppressWarnings("null")
public class CraftingQuest extends StardewQuest {

    private String itemId = "";

    public CraftingQuest() {
        this.questType = TYPE_CRAFTING;
    }

    @Override
    public void onRecipeCrafted(ServerPlayer player, String recipeId) {
        if (completed || !accepted) return;
        if (itemId.equals(recipeId)) {
            questComplete(player);
        }
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putString("ItemId", itemId);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        itemId = tag.getString("ItemId");
    }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }
}
