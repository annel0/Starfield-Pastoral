package com.stardew.craft.cutscene.command;

import com.stardew.craft.network.payload.ClientNpcVisibilityState;
import com.stardew.craft.cutscene.runtime.EventPlayer;

/**
 * show_npc: restore a previously hidden NPC's visibility during the event.
 * JSON: { "cmd": "show_npc", "npc_id": "wizard" }
 */
public class ShowNpcCommand implements EventCommand {
    private final String npcId;

    public ShowNpcCommand(String npcId) {
        this.npcId = npcId;
    }

    @Override
    public void start(EventPlayer player) {
        ClientNpcVisibilityState.show(npcId);
    }

    @Override public void tick(EventPlayer player) {}
    @Override public boolean isComplete() { return true; }
}
