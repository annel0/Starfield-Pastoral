package com.stardew.craft.npc.data;

import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Central in-memory registry for NPC data-driven resources.
 */
public final class NpcDataRegistry {
    private static volatile Map<String, NpcCapabilityProfile> CAPABILITIES = Collections.emptyMap();
    private static volatile Map<String, JsonObject> DIALOGUES = Collections.emptyMap();
    private static volatile Map<String, JsonObject> SCHEDULES = Collections.emptyMap();
    private static volatile Map<String, JsonObject> TASTES = Collections.emptyMap();
    private static volatile Map<String, JsonObject> EVENTS = Collections.emptyMap();
    private static volatile Set<String> LOCATION_MAPPINGS = Collections.emptySet();
    private static volatile Map<String, String> LOCATION_ALIASES = Collections.emptyMap();
    private static volatile Map<String, NpcLocationAnchor> LOCATION_ANCHORS = Collections.emptyMap();

    private NpcDataRegistry() {
    }

    public static void replaceCapabilities(Map<String, NpcCapabilityProfile> capabilities) {
        CAPABILITIES = Collections.unmodifiableMap(new LinkedHashMap<>(capabilities));
    }

    public static void replaceDialogues(Map<String, JsonObject> dialogues) {
        DIALOGUES = Collections.unmodifiableMap(new LinkedHashMap<>(dialogues));
    }

    public static void replaceSchedules(Map<String, JsonObject> schedules) {
        SCHEDULES = Collections.unmodifiableMap(new LinkedHashMap<>(schedules));
    }

    public static void replaceTastes(Map<String, JsonObject> tastes) {
        TASTES = Collections.unmodifiableMap(new LinkedHashMap<>(tastes));
    }

    public static void replaceEvents(Map<String, JsonObject> events) {
        EVENTS = Collections.unmodifiableMap(new LinkedHashMap<>(events));
    }

    public static void replaceLocationMappings(Set<String> locations) {
        LOCATION_MAPPINGS = Collections.unmodifiableSet(new LinkedHashSet<>(locations));
    }

    public static void replaceLocationAliases(Map<String, String> aliases) {
        LOCATION_ALIASES = Collections.unmodifiableMap(new LinkedHashMap<>(aliases));
    }

    public static void replaceLocationAnchors(Map<String, NpcLocationAnchor> anchors) {
        LOCATION_ANCHORS = Collections.unmodifiableMap(new LinkedHashMap<>(anchors));
    }

    public static Map<String, NpcCapabilityProfile> capabilities() {
        return CAPABILITIES;
    }

    public static Map<String, JsonObject> dialogues() {
        return DIALOGUES;
    }

    public static Map<String, JsonObject> schedules() {
        return SCHEDULES;
    }

    public static Map<String, JsonObject> tastes() {
        return TASTES;
    }

    public static Map<String, JsonObject> events() {
        return EVENTS;
    }

    public static Set<String> locationMappings() {
        return LOCATION_MAPPINGS;
    }

    public static Map<String, String> locationAliases() {
        return LOCATION_ALIASES;
    }

    public static Map<String, NpcLocationAnchor> locationAnchors() {
        return LOCATION_ANCHORS;
    }
}
