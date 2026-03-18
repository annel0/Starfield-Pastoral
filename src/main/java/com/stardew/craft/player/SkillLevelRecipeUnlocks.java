package com.stardew.craft.player;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardew.craft.StardewCraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SkillLevelRecipeUnlocks {
    private static final Gson GSON = new Gson();
    private static final String DATA_PATH = "/data/stardewcraft/player/skill_level_recipe_unlocks.json";

    private static final Map<String, Map<Integer, List<String>>> UNLOCKS = loadUnlocks();

    private SkillLevelRecipeUnlocks() {
    }

    public static List<String> getUnlocks(SkillType skill, int level) {
        if (skill == null || level <= 0) {
            return List.of();
        }

        Map<Integer, List<String>> byLevel = UNLOCKS.get(skill.getName());
        if (byLevel == null) {
            return List.of();
        }

        return byLevel.getOrDefault(level, List.of());
    }

    private static Map<String, Map<Integer, List<String>>> loadUnlocks() {
        try (InputStream in = SkillLevelRecipeUnlocks.class.getResourceAsStream(DATA_PATH)) {
            if (in == null) {
                StardewCraft.LOGGER.warn("Missing skill recipe unlock data: {}", DATA_PATH);
                return Map.of();
            }

            Type type = new TypeToken<LinkedHashMap<String, LinkedHashMap<String, List<String>>>>() {
            }.getType();
            Map<String, Map<String, List<String>>> raw = GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), type);
            if (raw == null) {
                return Map.of();
            }

            Map<String, Map<Integer, List<String>>> normalized = new LinkedHashMap<>();
            for (Map.Entry<String, Map<String, List<String>>> skillEntry : raw.entrySet()) {
                String skillName = skillEntry.getKey();
                Map<String, List<String>> levelMap = skillEntry.getValue();
                if (skillName == null || skillName.isBlank() || levelMap == null) {
                    continue;
                }

                Map<Integer, List<String>> byLevel = new LinkedHashMap<>();
                for (Map.Entry<String, List<String>> levelEntry : levelMap.entrySet()) {
                    String levelStr = levelEntry.getKey();
                    if (levelStr == null || levelStr.isBlank()) {
                        continue;
                    }
                    try {
                        int level = Integer.parseInt(levelStr);
                        byLevel.put(level, List.copyOf(levelEntry.getValue() == null ? List.of() : levelEntry.getValue()));
                    } catch (NumberFormatException ignored) {
                    }
                }

                normalized.put(skillName, Collections.unmodifiableMap(byLevel));
            }
            return Collections.unmodifiableMap(normalized);
        } catch (Exception ex) {
            StardewCraft.LOGGER.warn("Failed to load skill recipe unlock data: {}", ex.getMessage());
            return Map.of();
        }
    }
}