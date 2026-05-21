package com.stardew.craft.festival;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stardew.craft.time.StardewTimeManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class FestivalDialogueService {
    private static final String KEY_INDEX_RESOURCE = "data/stardewcraft/festival_dialogue_keys.json";
    private static volatile Map<String, Set<String>> dialogueKeysByFestival;

    private FestivalDialogueService() {
    }

    public static String resolveDialogueKey(String festivalId, String npcId) {
        int year = StardewTimeManager.get().getCurrentYear();
        return resolveDialogueKey(festivalId, npcId, year);
    }

    public static String resolveDialogueKey(String festivalId, String npcId, int year) {
        String normalizedFestivalId = normalizeFestivalId(festivalId);
        String normalizedNpcId = normalizeDialogueKey(npcId);
        if (normalizedFestivalId.isBlank() || normalizedNpcId.isBlank()) {
            return null;
        }

        Set<String> availableKeys = dialogueKeysByFestival().get(normalizedFestivalId);
        if (availableKeys == null || availableKeys.isEmpty()) {
            return null;
        }

        if (year > 1 && year % 2 == 0) {
            String yearTwoKey = normalizedNpcId + "_y2";
            if (availableKeys.contains(yearTwoKey)) {
                return translationKey(normalizedFestivalId, yearTwoKey);
            }
        }
        if (availableKeys.contains(normalizedNpcId)) {
            return translationKey(normalizedFestivalId, normalizedNpcId);
        }
        return null;
    }

    private static String translationKey(String festivalId, String dialogueKey) {
        return "stardewcraft.festival." + festivalId + ".dialogue." + dialogueKey;
    }

    private static Map<String, Set<String>> dialogueKeysByFestival() {
        Map<String, Set<String>> cached = dialogueKeysByFestival;
        if (cached != null) {
            return cached;
        }
        Map<String, Set<String>> loaded = loadDialogueKeys();
        dialogueKeysByFestival = loaded;
        return loaded;
    }

    private static Map<String, Set<String>> loadDialogueKeys() {
        try (InputStream stream = FestivalDialogueService.class.getClassLoader().getResourceAsStream(KEY_INDEX_RESOURCE)) {
            if (stream == null) {
                return Map.of();
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
            Map<String, Set<String>> result = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                JsonArray values = entry.getValue().getAsJsonArray();
                Set<String> keys = new LinkedHashSet<>();
                for (JsonElement value : values) {
                    String key = normalizeDialogueKey(value.getAsString());
                    if (!key.isBlank()) {
                        keys.add(key);
                    }
                }
                result.put(normalizeFestivalId(entry.getKey()), Set.copyOf(keys));
            }
            return Map.copyOf(result);
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    private static String normalizeFestivalId(String festivalId) {
        return festivalId == null ? "" : festivalId.trim().toLowerCase(Locale.ROOT);
    }

    private static String normalizeDialogueKey(String dialogueKey) {
        if (dialogueKey == null) {
            return "";
        }
        String trimmed = dialogueKey.trim();
        if (trimmed.equals("???")) {
            return "krobus";
        }
        return trimmed.toLowerCase(Locale.ROOT).replace('?', '_');
    }
}
