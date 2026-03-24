package com.stardew.craft.npc.data;

import com.stardew.craft.StardewCraft;

import java.util.Map;

/**
 * Validates cross-file data completeness for implemented NPCs.
 */
public final class NpcDataDiagnostics {
    private NpcDataDiagnostics() {
    }

    public static void validateAndLog(Map<String, NpcCapabilityProfile> capabilities,
                                      Map<String, com.google.gson.JsonObject> dialogues,
                                      Map<String, com.google.gson.JsonObject> schedules,
                                      Map<String, com.google.gson.JsonObject> tastes) {
        for (NpcCapabilityProfile capability : capabilities.values()) {
            if (!capability.implemented()) {
                continue;
            }

            String npcId = capability.npcId();
            if (!dialogues.containsKey(npcId)) {
                StardewCraft.LOGGER.warn("NPC data missing dialogue for implemented npc='{}'", npcId);
            }
            if (!schedules.containsKey(npcId)) {
                StardewCraft.LOGGER.warn("NPC data missing schedule for implemented npc='{}'", npcId);
            }
            if (!tastes.containsKey(npcId)) {
                StardewCraft.LOGGER.warn("NPC data missing tastes for implemented npc='{}'", npcId);
            }
        }
    }
}
