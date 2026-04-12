package com.stardew.craft.quest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

/**
 * SDV GoSomewhereQuest — 前往某地任务
 */
@SuppressWarnings("null")
public class GoSomewhereQuest extends StardewQuest {

    private static final String MINE_FLOOR_PREFIX = "MineFloor:";

    private String whereToGo = "";

    public GoSomewhereQuest() {
        this.questType = TYPE_LOCATION;
    }

    @Override
    public void onWarped(ServerPlayer player, String location) {
        if (completed || !accepted) return;
        if (isMineFloorTarget()) return;
        if (whereToGo.equals(location)) {
            questComplete(player);
        }
    }

    @Override
    public void onMineFloorReached(ServerPlayer player, int floor) {
        if (completed || !accepted || !isMineFloorTarget()) return;
        int targetFloor = getMineFloorTarget();
        if (targetFloor > 0 && floor >= targetFloor) {
            questComplete(player);
        }
    }

    private boolean isMineFloorTarget() {
        return whereToGo != null && whereToGo.startsWith(MINE_FLOOR_PREFIX);
    }

    private int getMineFloorTarget() {
        if (!isMineFloorTarget()) return -1;
        try {
            return Integer.parseInt(whereToGo.substring(MINE_FLOOR_PREFIX.length()));
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    @Override
    protected void saveExtra(CompoundTag tag) {
        tag.putString("WhereToGo", whereToGo);
    }

    @Override
    protected void loadExtra(CompoundTag tag) {
        whereToGo = tag.getString("WhereToGo");
    }

    public String getWhereToGo() { return whereToGo; }
    public void setWhereToGo(String whereToGo) { this.whereToGo = whereToGo; }
}
