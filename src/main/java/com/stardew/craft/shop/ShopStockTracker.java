package com.stardew.craft.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks per-player limited-stock purchases within a single game day.
 *
 * SDV parity: mirrors SynchronizedShopStock — per-player, resets each new day.
 *
 * Thread safety: called only on the server logical thread (enqueueWork).
 */
@SuppressWarnings("null")
public final class ShopStockTracker {

    /** key = "playerId/shopId/itemId" → total purchased count this day */
    private static final Map<String, Integer> purchased = new HashMap<>();

    private ShopStockTracker() {}

    private static String key(UUID playerId, String shopId, String itemId) {
        return playerId.toString() + "/" + shopId + "/" + itemId;
    }

    /**
     * Returns remaining stock for this player today.
     * Returns {@link Integer#MAX_VALUE} when stock is unlimited.
     */
    public static int getRemaining(UUID playerId, String shopId, String itemId, int originalStock) {
        if (originalStock == Integer.MAX_VALUE) return Integer.MAX_VALUE;
        int bought = purchased.getOrDefault(key(playerId, shopId, itemId), 0);
        return Math.max(0, originalStock - bought);
    }

    /** Record a successful purchase for a player. */
    public static void recordPurchase(UUID playerId, String shopId, String itemId, int amount) {
        String k = key(playerId, shopId, itemId);
        purchased.merge(k, amount, Integer::sum);
    }

    /**
     * Reset all per-player purchase counts.
     * Call at the start of each new game day.
     */
    public static void resetForNewDay() {
        purchased.clear();
    }
}
