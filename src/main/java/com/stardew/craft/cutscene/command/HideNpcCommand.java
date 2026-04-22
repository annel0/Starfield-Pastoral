package com.stardew.craft.cutscene.command;

import com.stardew.craft.network.payload.ClientNpcVisibilityState;
import com.stardew.craft.cutscene.runtime.EventPlayer;

/**
 * hide_npc: hides a real NPC from rendering during the event.
 * JSON: { "cmd": "hide_npc", "npc_id": "abigail" }
 */
public class HideNpcCommand implements EventCommand {

    private final String npcId;

    public HideNpcCommand(String npcId) {
        this.npcId = npcId;
    }

    @Override
    public void start(EventPlayer player) {
        ClientNpcVisibilityState.hide(npcId);
        player.trackHiddenNpc(npcId);
    }

    @Override
    public void tick(EventPlayer player) {}

    @Override
    public boolean isComplete() { return true; }

    @Override
    public boolean isStateCommand() { return true; }

    @Override
    public void onSkip(EventPlayer player) {
        // Visibility will be restored in EventPlayer.endEvent()
    }
}
