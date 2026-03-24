package com.stardew.craft.npc.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Checks NPC animation assets to determine whether walk animation exists.
 */
public final class NpcAnimationInspector {
    private NpcAnimationInspector() {
    }

    public static boolean hasWalkAnimation(String npcId) {
        if (npcId == null || npcId.isBlank()) {
            return false;
        }

        String path = "assets/stardewcraft/animations/entity/npc/" + npcId.toLowerCase() + ".animation.json";
        try (InputStream stream = NpcAnimationInspector.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                return false;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("animations") || !root.get("animations").isJsonObject()) {
                return false;
            }

            JsonObject animations = root.getAsJsonObject("animations");
            for (String key : animations.keySet()) {
                String lower = key.toLowerCase();
                if (lower.equals("walk") || lower.contains("walk")) {
                    return true;
                }
            }
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }
}
