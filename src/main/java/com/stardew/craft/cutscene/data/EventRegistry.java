package com.stardew.craft.cutscene.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

import java.util.*;

/**
 * Loads and indexes all event JSON files from {@code data/stardewcraft/cutscene_events/}.
 *
 * Events are indexed by:
 * - id (unique lookup)
 * - trigger location (for enter_area checks)
 * - trigger npc (for interact_npc checks)
 */
public final class EventRegistry {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** All events by id. */
    private static volatile Map<String, EventData> byId = Map.of();

    /** Events indexed by trigger location name. */
    private static volatile Map<String, List<EventData>> byLocation = Map.of();

    /** Events indexed by trigger NPC id. */
    private static volatile Map<String, List<EventData>> byNpc = Map.of();

    /** All time_check events. */
    private static volatile List<EventData> timeCheckEvents = List.of();

    /** Raw JSON strings for server→client sync. Keys = event id, values = JSON string. */
    private static volatile Map<String, String> rawJsonById = Map.of();

    private EventRegistry() {}

    // ─── accessors ───

    public static EventData getById(String id) {
        return byId.get(id);
    }

    public static List<EventData> getByLocation(String location) {
        return byLocation.getOrDefault(location, List.of());
    }

    public static List<EventData> getByNpc(String npcId) {
        return byNpc.getOrDefault(npcId, List.of());
    }

    public static List<EventData> getTimeCheckEvents() {
        return timeCheckEvents;
    }

    public static Collection<EventData> all() {
        return byId.values();
    }

    /** Returns raw JSON map for server→client sync. */
    public static Map<String, String> getRawJsonMap() {
        return rawJsonById;
    }

    /** Client-side: rebuild registry from synced JSON strings. */
    public static void loadFromJsonStrings(Map<String, String> jsonMap) {
        Map<String, EventData> newById = new HashMap<>();
        Map<String, List<EventData>> newByLocation = new HashMap<>();
        Map<String, List<EventData>> newByNpc = new HashMap<>();
        List<EventData> newTimeCheck = new ArrayList<>();

        for (Map.Entry<String, String> entry : jsonMap.entrySet()) {
            try {
                JsonObject obj = JsonParser.parseString(entry.getValue()).getAsJsonObject();
                EventData data = EventData.fromJson(obj);
                newById.put(data.id(), data);

                EventTrigger trigger = data.trigger();
                switch (trigger.type()) {
                    case "enter_area" -> {
                        if (trigger.location() != null) {
                            newByLocation.computeIfAbsent(trigger.location(), k -> new ArrayList<>()).add(data);
                        }
                    }
                    case "interact_npc" -> {
                        if (trigger.npc() != null) {
                            newByNpc.computeIfAbsent(trigger.npc(), k -> new ArrayList<>()).add(data);
                        }
                    }
                    case "time_check" -> newTimeCheck.add(data);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to parse synced cutscene event {}: {}", entry.getKey(), e.getMessage());
            }
        }

        byId = Map.copyOf(newById);
        Map<String, List<EventData>> immutableByLoc = new HashMap<>();
        newByLocation.forEach((k, v) -> immutableByLoc.put(k, List.copyOf(v)));
        byLocation = Map.copyOf(immutableByLoc);
        Map<String, List<EventData>> immutableByNpc = new HashMap<>();
        newByNpc.forEach((k, v) -> immutableByNpc.put(k, List.copyOf(v)));
        byNpc = Map.copyOf(immutableByNpc);
        timeCheckEvents = List.copyOf(newTimeCheck);

        LOGGER.info("Synced {} cutscene events from server", newById.size());
    }

    // ─── reload listener ───

    public static final class ReloadListener extends SimpleJsonResourceReloadListener {
        public ReloadListener() {
            super(GSON, "cutscene_events");
        }

        @Override
        protected void apply(@javax.annotation.Nonnull Map<ResourceLocation, JsonElement> objects,
                             @javax.annotation.Nonnull ResourceManager resourceManager,
                             @javax.annotation.Nonnull ProfilerFiller profiler) {
            Map<String, EventData> newById = new HashMap<>();
            Map<String, List<EventData>> newByLocation = new HashMap<>();
            Map<String, List<EventData>> newByNpc = new HashMap<>();
            List<EventData> newTimeCheck = new ArrayList<>();

            for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
                ResourceLocation rl = entry.getKey();
                JsonElement element = entry.getValue();
                if (element == null || !element.isJsonObject()) continue;

                try {
                    EventData data = EventData.fromJson(element.getAsJsonObject());
                    newById.put(data.id(), data);

                    EventTrigger trigger = data.trigger();
                    switch (trigger.type()) {
                        case "enter_area" -> {
                            if (trigger.location() != null) {
                                newByLocation.computeIfAbsent(trigger.location(), k -> new ArrayList<>()).add(data);
                            }
                        }
                        case "interact_npc" -> {
                            if (trigger.npc() != null) {
                                newByNpc.computeIfAbsent(trigger.npc(), k -> new ArrayList<>()).add(data);
                            }
                        }
                        case "time_check" -> newTimeCheck.add(data);
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to parse cutscene event {}: {}", rl, e.getMessage());
                }
            }

            byId = Map.copyOf(newById);
            // make inner lists immutable
            Map<String, List<EventData>> immutableByLoc = new HashMap<>();
            newByLocation.forEach((k, v) -> immutableByLoc.put(k, List.copyOf(v)));
            byLocation = Map.copyOf(immutableByLoc);

            Map<String, List<EventData>> immutableByNpc = new HashMap<>();
            newByNpc.forEach((k, v) -> immutableByNpc.put(k, List.copyOf(v)));
            byNpc = Map.copyOf(immutableByNpc);

            timeCheckEvents = List.copyOf(newTimeCheck);

            LOGGER.info("Loaded {} cutscene events", newById.size());

            // Store raw JSON for server→client sync
            Map<String, String> newRawJson = new HashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> e : objects.entrySet()) {
                if (e.getValue() != null && e.getValue().isJsonObject()) {
                    JsonObject obj = e.getValue().getAsJsonObject();
                    if (obj.has("id")) {
                        newRawJson.put(obj.get("id").getAsString(), GSON.toJson(obj));
                    }
                }
            }
            rawJsonById = Map.copyOf(newRawJson);
        }
    }
}
