package com.stardew.craft.shop;

import java.util.Set;

/**
 * Mirrors SDV ItemStockInformation per-item entry in a Shop.
 * price = 0 means trade-only, tradeItemId = null means gold-only.
 *
 * seasons: set of season ints where 0=spring,1=summer,2=fall,3=winter.
 *   An empty set means the item is available year-round.
 * minYear: the earliest year the item is sold (default 1 = always).
 *   These two fields are server-side only (not sent over the network).
 */
public record ShopItemEntry(
    String itemId,        // "minecraft:golden_sword" / "stardewcraft:parsnip_seeds"
    String displayName,   // localised display name
    String description,   // tooltip description
    int price,            // gold cost; Integer.MAX_VALUE = not available
    int stock,            // Integer.MAX_VALUE = infinite
    String tradeItemId,   // null = no trade requirement
    int tradeItemCount,   // trade item quantity (irrelevant when tradeItemId=null)
    Set<Integer> seasons, // empty = all seasons; 0=spring,1=summer,2=fall,3=winter
    int minYear,          // minimum year to appear in shop (1 = year 1+)
    int minMineLevel,     // minimum mine floor reached to unlock (0 = no requirement)
    String mailFlag       // required player mail flag (null = no requirement)
) {
    public boolean isFree() {
        return price <= 0 && (tradeItemId == null || tradeItemId.isEmpty());
    }

    public boolean requiresTrade() {
        return tradeItemId != null && !tradeItemId.isEmpty();
    }

    /**
     * Returns true when this item should appear in the shop given the current
     * season (0-3) and year (1+).
     */
    public boolean isAvailableIn(int season, int year) {
        if (year < minYear) return false;
        if (seasons.isEmpty()) return true; // available all seasons
        return seasons.contains(season);
    }

    /**
     * Returns true when this item should appear given the player's mine progress
     * and mail flags. Call after isAvailableIn().
     */
    public boolean meetsPlayerConditions(int playerMineLevel, java.util.Set<String> playerMailFlags) {
        if (minMineLevel > 0 && playerMineLevel < minMineLevel) return false;
        if (mailFlag != null && !mailFlag.isEmpty() && !playerMailFlags.contains(mailFlag)) return false;
        return true;
    }

    /**
     * Factory used by the network codec — no seasons/minYear needed on the client
     * because the server already filtered the list before sending.
     */
    public static ShopItemEntry fromNetwork(
            String itemId, String displayName, String description,
            int price, int stock, String tradeItemId, int tradeItemCount) {
        return new ShopItemEntry(itemId, displayName, description,
                price, stock, tradeItemId, tradeItemCount, Set.of(), 1, 0, null);
    }
}
