package com.stardew.craft.npc.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.Set;

/**
 * Applies project-compatibility filtering to imported vanilla-like NPC data.
 */
@SuppressWarnings("null")
public final class NpcContentFilter {
    private NpcContentFilter() {
    }

    public static JsonObject filterTastes(JsonObject input) {
        JsonObject out = input.deepCopy();
        filterTasteArray(out, "loved");
        filterTasteArray(out, "liked");
        filterTasteArray(out, "neutral");
        filterTasteArray(out, "disliked");
        filterTasteArray(out, "hated");
        return out;
    }

    public static JsonObject filterSchedules(JsonObject input, Set<String> knownLocations) {
        JsonObject out = input.deepCopy();
        for (String key : out.keySet()) {
            JsonElement element = out.get(key);
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject daySchedule = element.getAsJsonObject();
            JsonObject filtered = new JsonObject();
            for (String timeKey : daySchedule.keySet()) {
                JsonElement routeEl = daySchedule.get(timeKey);
                if (!routeEl.isJsonPrimitive()) {
                    continue;
                }

                if (timeKey.startsWith("_")) {
                    filtered.add(timeKey, routeEl.deepCopy());
                    continue;
                }

                String route = routeEl.getAsString();
                String location = firstToken(route);
                if (location == null
                    || isIntegerToken(location)
                    || knownLocations.contains(location.toLowerCase(Locale.ROOT))) {
                    filtered.addProperty(timeKey, route);
                }
            }
            out.add(key, filtered);
        }
        return out;
    }

    private static boolean isIntegerToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            Integer.parseInt(token);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static void filterTasteArray(JsonObject obj, String key) {
        if (!obj.has(key) || !obj.get(key).isJsonArray()) {
            return;
        }
        JsonArray source = obj.getAsJsonArray(key);
        JsonArray kept = new JsonArray();
        for (JsonElement el : source) {
            if (!el.isJsonPrimitive()) {
                continue;
            }
            String token = el.getAsString();
            ResourceLocation resolved = resolveExistingItem(token);
            if (resolved != null) {
                kept.add(resolved.toString());
            }
        }
        obj.add(key, kept);
    }

    public static String resolveExistingItemId(String raw) {
        ResourceLocation resolved = resolveExistingItem(raw);
        return resolved == null ? null : resolved.toString();
    }

    private static ResourceLocation resolveExistingItem(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        String normalized = raw.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
        ResourceLocation direct = ResourceLocation.tryParse(normalized);
        if (direct != null && BuiltInRegistries.ITEM.containsKey(direct)) {
            return direct;
        }

        ResourceLocation stardewId = ResourceLocation.tryParse("stardewcraft:" + normalized);
        if (stardewId != null && BuiltInRegistries.ITEM.containsKey(stardewId)) {
            return stardewId;
        }

        ResourceLocation minecraftId = ResourceLocation.tryParse("minecraft:" + normalized);
        if (minecraftId != null && BuiltInRegistries.ITEM.containsKey(minecraftId)) {
            return minecraftId;
        }

        return null;
    }

    private static String firstToken(String route) {
        if (route == null || route.isBlank()) {
            return null;
        }
        String[] parts = route.trim().split("\\s+");
        return parts.length == 0 ? null : parts[0];
    }
}
