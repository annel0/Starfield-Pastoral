package com.stardew.craft.cutscene.network;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Client-side cache of events the local player has already seen.
 * Populated by {@link SyncEventSeenPayload} on login.
 */
public final class ClientEventSeenCache {

    private static volatile Set<String> seenEvents = Set.of();
    private static volatile boolean syncedFromServer = false;

    private ClientEventSeenCache() {}

    public static boolean hasSeen(String eventId) {
        return seenEvents.contains(eventId);
    }

    public static Set<String> all() {
        return seenEvents;
    }

    public static void replace(Set<String> events) {
        seenEvents = Collections.unmodifiableSet(new HashSet<>(events));
        syncedFromServer = true;
    }

    /**
     * Returns true once the server has sent at least one SyncEventSeenPayload.
     * Used by EventTriggerChecker to avoid auto-firing events during the
     * brief login window when the seen cache is still empty.
     */
    public static boolean isSynced() {
        return syncedFromServer;
    }

    public static void reset() {
        seenEvents = Set.of();
        syncedFromServer = false;
    }
}
