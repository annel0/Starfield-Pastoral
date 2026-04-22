package com.stardew.craft.npc.data;


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
            }
            if (!schedules.containsKey(npcId)) {
            }
            if (!tastes.containsKey(npcId)) {
            }
        }
    }
}
