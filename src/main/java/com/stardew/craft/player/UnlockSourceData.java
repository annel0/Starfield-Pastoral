package com.stardew.craft.player;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardew.craft.StardewCraft;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data-driven unlock table keyed by source id.
 *
 * Source id examples:
 * - skill:farming:3
 * - shop:wizard_catalogue
 * - mail:queen_of_sauce_2_21
 */
public final class UnlockSourceData {
    public record UnlockBundle(List<String> recipes, List<String> wallpapers, List<String> floorings) {
        public static final UnlockBundle EMPTY = new UnlockBundle(List.of(), List.of(), List.of());
    }

    private record RawUnlockBundle(List<String> recipes, List<String> wallpapers, List<String> floorings) {
    }

    private static final Gson GSON = new Gson();
    private static final String DATA_PATH = "/data/stardewcraft/player/unlock_sources.json";
    private static final Map<String, UnlockBundle> SOURCES = loadSources();

    private UnlockSourceData() {
    }

    public static UnlockBundle getUnlocks(String sourceId) {
        if (sourceId == null || sourceId.isBlank()) {
            return UnlockBundle.EMPTY;
        }
        return SOURCES.getOrDefault(sourceId, UnlockBundle.EMPTY);
    }

    public static boolean hasSource(String sourceId) {
        if (sourceId == null || sourceId.isBlank()) {
            return false;
        }
        return SOURCES.containsKey(sourceId);
    }

    public static List<String> getSourceIds() {
        return List.copyOf(SOURCES.keySet());
    }

    public static String skillLevelSourceId(SkillType skill, int level) {
        if (skill == null || level <= 0) {
            return "";
        }
        return "skill:" + skill.getName() + ":" + level;
    }

    public static UnlockBundle getSkillLevelUnlocks(SkillType skill, int level) {
        String sourceId = skillLevelSourceId(skill, level);
        if (sourceId.isEmpty()) {
            return UnlockBundle.EMPTY;
        }
        return getUnlocks(sourceId);
    }

    private static Map<String, UnlockBundle> loadSources() {
        try (InputStream in = UnlockSourceData.class.getResourceAsStream(DATA_PATH)) {
            if (in == null) {
                StardewCraft.LOGGER.warn("Missing unlock source data: {}", DATA_PATH);
                return Map.of();
            }

            Type type = new TypeToken<LinkedHashMap<String, RawUnlockBundle>>() {
            }.getType();
            Map<String, RawUnlockBundle> parsed = GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), type);
            if (parsed == null) {
                return Map.of();
            }

            Map<String, UnlockBundle> normalized = new LinkedHashMap<>();
            for (Map.Entry<String, RawUnlockBundle> entry : parsed.entrySet()) {
                String key = entry.getKey();
                RawUnlockBundle value = entry.getValue();
                if (key == null || key.isBlank() || value == null) {
                    continue;
                }
                normalized.put(
                        key,
                        new UnlockBundle(
                                safeCopy(value.recipes),
                                safeCopy(value.wallpapers),
                                safeCopy(value.floorings)
                        )
                );
            }

            StardewCraft.LOGGER.info("Loaded unlock sources: {} entries", normalized.size());
            return Collections.unmodifiableMap(normalized);
        } catch (Exception ex) {
            StardewCraft.LOGGER.warn("Failed to load unlock sources: {}", ex.getMessage());
            return Map.of();
        }
    }

    private static List<String> safeCopy(List<String> source) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        List<String> normalized = new ArrayList<>(source.size());
        for (String id : source) {
            if (id == null || id.isBlank()) {
                continue;
            }
            if (!normalized.contains(id)) {
                normalized.add(id);
            }
        }
        return List.copyOf(normalized);
    }
}
