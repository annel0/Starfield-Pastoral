package com.stardew.craft.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Client-side cache for NPC friendship overview shown in the V menu social tab.
 */
public final class NpcFriendshipClientCache {
    public record Entry(String npcId, boolean met, boolean canReceiveGifts, int points, int hearts, int giftsThisWeek, boolean giftedToday, boolean talkedToday, int metOrder) {
    }

    private static List<Entry> entries = List.of();
    private static long lastSyncMs;
    private static volatile boolean syncedFromServer = false;

    private NpcFriendshipClientCache() {
    }

    public static boolean isSynced() { return syncedFromServer; }

    public static void update(List<Entry> incoming) {
        syncedFromServer = true;
        if (incoming == null || incoming.isEmpty()) {
            entries = List.of();
            lastSyncMs = System.currentTimeMillis();
            return;
        }

        List<Entry> copy = new ArrayList<>(incoming.size());
        for (Entry entry : incoming) {
            if (entry == null || entry.npcId() == null || entry.npcId().isBlank()) {
                continue;
            }
            int clampedPoints = Math.max(0, entry.points());
            int clampedHearts = Math.max(0, Math.min(14, entry.hearts()));
            int clampedGifts = Math.max(0, Math.min(2, entry.giftsThisWeek()));
            int clampedMetOrder = Math.max(0, entry.metOrder());
            copy.add(new Entry(entry.npcId().trim().toLowerCase(), entry.met(), entry.canReceiveGifts(), clampedPoints, clampedHearts, clampedGifts, entry.giftedToday(), entry.talkedToday(), clampedMetOrder));
        }

        copy.sort(Comparator
            .comparingInt(Entry::points).reversed()
            .thenComparingInt(Entry::metOrder)
            .thenComparing(entry -> NpcDisplayNames.sortKey(entry.npcId()))
            .thenComparing(Entry::npcId));
        entries = List.copyOf(copy);
        lastSyncMs = System.currentTimeMillis();
    }

    public static List<Entry> entries() {
        return entries;
    }

    public static long lastSyncMs() {
        return lastSyncMs;
    }

    public static Entry findByNpcId(String npcId) {
        if (npcId == null || npcId.isBlank()) {
            return null;
        }
        String key = npcId.trim().toLowerCase(Locale.ROOT);
        for (Entry entry : entries) {
            if (key.equals(entry.npcId())) {
                return entry;
            }
        }
        return null;
    }

    public static void updateNpcState(String npcId, int points, int hearts, int giftsThisWeek, boolean giftedToday, boolean talkedToday) {
        if (npcId == null || npcId.isBlank()) {
            return;
        }
        String key = npcId.trim().toLowerCase(Locale.ROOT);
        List<Entry> mutable = new ArrayList<>(entries);
        for (int i = 0; i < mutable.size(); i++) {
            Entry entry = mutable.get(i);
            if (key.equals(entry.npcId())) {
                mutable.set(i, new Entry(
                    key,
                    true,
                    entry.canReceiveGifts(),
                    Math.max(0, points),
                    Math.max(0, Math.min(14, hearts)),
                    Math.max(0, Math.min(2, giftsThisWeek)),
                    giftedToday,
                    talkedToday,
                    entry.metOrder()
                ));
                mutable.sort(Comparator
                    .comparingInt(Entry::points).reversed()
                    .thenComparingInt(Entry::metOrder)
                    .thenComparing(e -> NpcDisplayNames.sortKey(e.npcId()))
                    .thenComparing(Entry::npcId));
                entries = List.copyOf(mutable);
                lastSyncMs = System.currentTimeMillis();
                return;
            }
        }
        mutable.add(new Entry(
            key,
            true,
            true,
            Math.max(0, points),
            Math.max(0, Math.min(14, hearts)),
            Math.max(0, Math.min(2, giftsThisWeek)),
            giftedToday,
            talkedToday,
            Integer.MAX_VALUE
        ));
        mutable.sort(Comparator
            .comparingInt(Entry::points).reversed()
            .thenComparingInt(Entry::metOrder)
            .thenComparing(e -> NpcDisplayNames.sortKey(e.npcId()))
            .thenComparing(Entry::npcId));
        entries = List.copyOf(mutable);
        lastSyncMs = System.currentTimeMillis();
    }

    public static void reset() {
        entries = List.of();
        lastSyncMs = 0L;
        syncedFromServer = false;
    }
}
