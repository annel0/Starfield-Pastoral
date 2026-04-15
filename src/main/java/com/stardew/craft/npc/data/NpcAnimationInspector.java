package com.stardew.craft.npc.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Checks NPC animation assets to determine whether walk animation exists.
 */
public final class NpcAnimationInspector {
    private static final Map<String, Boolean> WALK_ANIM_CACHE = new ConcurrentHashMap<>();

    private NpcAnimationInspector() {
    }

    public static boolean hasWalkAnimation(String npcId) {
        if (npcId == null || npcId.isBlank()) {
            return false;
        }
        String key = npcId.toLowerCase();
        Boolean cached = WALK_ANIM_CACHE.get(key);
        if (cached != null) return cached;

        String path = "assets/stardewcraft/animations/entity/npc/" + key + ".animation.json";
        try (InputStream stream = NpcAnimationInspector.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) {
                WALK_ANIM_CACHE.put(key, false);
                return false;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            if (!root.has("animations") || !root.get("animations").isJsonObject()) {
                WALK_ANIM_CACHE.put(key, false);
                return false;
            }

            JsonObject animations = root.getAsJsonObject("animations");
            for (String animKey : animations.keySet()) {
                String lower = animKey.toLowerCase();
                if (lower.equals("walk") || lower.contains("walk")) {
                    WALK_ANIM_CACHE.put(key, true);
                    return true;
                }
            }
            WALK_ANIM_CACHE.put(key, false);
            return false;
        } catch (Exception ignored) {
            WALK_ANIM_CACHE.put(key, false);
            return false;
        }
    }
}
