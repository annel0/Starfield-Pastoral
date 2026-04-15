package com.stardew.craft.npc.data;

import com.google.gson.JsonObject;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Central in-memory registry for NPC data-driven resources.
 * All data is packed into an immutable snapshot and atomically swapped
 * so readers never see a half-updated state.
 */
public final class NpcDataRegistry {

    /** Immutable snapshot of all NPC data. */
    private record Snapshot(
        Map<String, NpcCapabilityProfile> capabilities,
        Map<String, JsonObject> dialogues,
        Map<String, JsonObject> schedules,
        Map<String, JsonObject> tastes,
        Map<String, JsonObject> events,
        Set<String> locationMappings,
        Map<String, String> locationAliases,
        Map<String, NpcLocationAnchor> locationAnchors
    ) {
        static final Snapshot EMPTY = new Snapshot(
            Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(),
            Collections.emptyMap(), Collections.emptyMap(), Collections.emptySet(),
            Collections.emptyMap(), Collections.emptyMap()
        );
    }

    private static volatile Snapshot CURRENT = Snapshot.EMPTY;

    private NpcDataRegistry() {
    }

    public static void replaceCapabilities(Map<String, NpcCapabilityProfile> capabilities) {
        Snapshot s = CURRENT;
        CURRENT = new Snapshot(Collections.unmodifiableMap(new LinkedHashMap<>(capabilities)),
            s.dialogues, s.schedules, s.tastes, s.events, s.locationMappings, s.locationAliases, s.locationAnchors);
    }

    public static void replaceDialogues(Map<String, JsonObject> dialogues) {
        Snapshot s = CURRENT;
        CURRENT = new Snapshot(s.capabilities, Collections.unmodifiableMap(new LinkedHashMap<>(dialogues)),
            s.schedules, s.tastes, s.events, s.locationMappings, s.locationAliases, s.locationAnchors);
    }

    public static void replaceSchedules(Map<String, JsonObject> schedules) {
        Snapshot s = CURRENT;
        CURRENT = new Snapshot(s.capabilities, s.dialogues, Collections.unmodifiableMap(new LinkedHashMap<>(schedules)),
            s.tastes, s.events, s.locationMappings, s.locationAliases, s.locationAnchors);
    }

    public static void replaceTastes(Map<String, JsonObject> tastes) {
        Snapshot s = CURRENT;
        CURRENT = new Snapshot(s.capabilities, s.dialogues, s.schedules,
            Collections.unmodifiableMap(new LinkedHashMap<>(tastes)), s.events, s.locationMappings, s.locationAliases, s.locationAnchors);
    }

    public static void replaceEvents(Map<String, JsonObject> events) {
        Snapshot s = CURRENT;
        CURRENT = new Snapshot(s.capabilities, s.dialogues, s.schedules, s.tastes,
            Collections.unmodifiableMap(new LinkedHashMap<>(events)), s.locationMappings, s.locationAliases, s.locationAnchors);
    }

    public static void replaceLocationMappings(Set<String> locations) {
        Snapshot s = CURRENT;
        CURRENT = new Snapshot(s.capabilities, s.dialogues, s.schedules, s.tastes, s.events,
            Collections.unmodifiableSet(new LinkedHashSet<>(locations)), s.locationAliases, s.locationAnchors);
    }

    public static void replaceLocationAliases(Map<String, String> aliases) {
        Snapshot s = CURRENT;
        CURRENT = new Snapshot(s.capabilities, s.dialogues, s.schedules, s.tastes, s.events,
            s.locationMappings, Collections.unmodifiableMap(new LinkedHashMap<>(aliases)), s.locationAnchors);
    }

    public static void replaceLocationAnchors(Map<String, NpcLocationAnchor> anchors) {
        Snapshot s = CURRENT;
        CURRENT = new Snapshot(s.capabilities, s.dialogues, s.schedules, s.tastes, s.events,
            s.locationMappings, s.locationAliases, Collections.unmodifiableMap(new LinkedHashMap<>(anchors)));
    }

    public static Map<String, NpcCapabilityProfile> capabilities() {
        return CURRENT.capabilities;
    }

    public static Map<String, JsonObject> dialogues() {
        return CURRENT.dialogues;
    }

    public static Map<String, JsonObject> schedules() {
        return CURRENT.schedules;
    }

    public static Map<String, JsonObject> tastes() {
        return CURRENT.tastes;
    }

    public static Map<String, JsonObject> events() {
        return CURRENT.events;
    }

    public static Set<String> locationMappings() {
        return CURRENT.locationMappings;
    }

    public static Map<String, String> locationAliases() {
        return CURRENT.locationAliases;
    }

    public static Map<String, NpcLocationAnchor> locationAnchors() {
        return CURRENT.locationAnchors;
    }
}
