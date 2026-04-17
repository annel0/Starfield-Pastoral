package com.stardew.craft.network.payload;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client-side state tracking which NPC IDs are currently hidden for this player.
 * Used during per-player cutscenes (e.g., E112 wizard event) to hide NPCs
 * without modifying the shared server entity state.
 */
public final class ClientNpcVisibilityState {

    private static final Set<String> HIDDEN_NPC_IDS = ConcurrentHashMap.newKeySet();

    private ClientNpcVisibilityState() {}

    public static void hide(String npcId) {
        HIDDEN_NPC_IDS.add(npcId);
    }

    public static void show(String npcId) {
        HIDDEN_NPC_IDS.remove(npcId);
    }

    public static boolean isHidden(String npcId) {
        return HIDDEN_NPC_IDS.contains(npcId);
    }

    /**
     * Called on disconnect/dimension change to reset state.
     */
    public static void clear() {
        HIDDEN_NPC_IDS.clear();
    }
}
