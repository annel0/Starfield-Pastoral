package com.stardew.craft.leaderboard;

import java.util.Arrays;
import java.util.Optional;

public enum LeaderboardMetric {
    MONEY(LeaderboardCategory.WEALTH, "money", "stardewcraft.leaderboard.money", "stardewcraft.leaderboard.short.money", "stardewcraft.leaderboard.desc.money", "stardewcraft.leaderboard.value.money", true),
    MINE_DEPTH(LeaderboardCategory.MINING, "mine_depth", "stardewcraft.leaderboard.mine_depth", "stardewcraft.leaderboard.short.mine_depth", "stardewcraft.leaderboard.desc.mine_depth", "stardewcraft.leaderboard.value.mine_depth", true),
    MINE_BLOCKS_BROKEN(LeaderboardCategory.MINING, "mine_blocks_broken", "stardewcraft.leaderboard.mine_blocks_broken", "stardewcraft.leaderboard.short.mine_blocks_broken", "stardewcraft.leaderboard.desc.mine_blocks_broken", "stardewcraft.leaderboard.value.count", true),
    MINE_BLOCKS_BOMBED(LeaderboardCategory.MINING, "mine_blocks_bombed", "stardewcraft.leaderboard.mine_blocks_bombed", "stardewcraft.leaderboard.short.mine_blocks_bombed", "stardewcraft.leaderboard.desc.mine_blocks_bombed", "stardewcraft.leaderboard.value.count", true),
    FISH_CAUGHT(LeaderboardCategory.FISHING, "fish_caught", "stardewcraft.leaderboard.fish_caught", "stardewcraft.leaderboard.short.fish_caught", "stardewcraft.leaderboard.desc.fish_caught", "stardewcraft.leaderboard.value.count", true),
    ITEMS_SHIPPED(LeaderboardCategory.SHIPPING, "items_shipped", "stardewcraft.leaderboard.items_shipped", "stardewcraft.leaderboard.short.items_shipped", "stardewcraft.leaderboard.desc.items_shipped", "stardewcraft.leaderboard.value.count", true),
    SHIPPING_VALUE(LeaderboardCategory.SHIPPING, "shipping_value", "stardewcraft.leaderboard.shipping_value", "stardewcraft.leaderboard.short.shipping_value", "stardewcraft.leaderboard.desc.shipping_value", "stardewcraft.leaderboard.value.money", true),
    MONSTERS_SLAIN(LeaderboardCategory.COMBAT, "monsters_slain", "stardewcraft.leaderboard.monsters_slain", "stardewcraft.leaderboard.short.monsters_slain", "stardewcraft.leaderboard.desc.monsters_slain", "stardewcraft.leaderboard.value.count", true),
    TRASH_CANS_CHECKED(LeaderboardCategory.LIFE, "trash_cans_checked", "stardewcraft.leaderboard.trash_cans_checked", "stardewcraft.leaderboard.short.trash_cans_checked", "stardewcraft.leaderboard.desc.trash_cans_checked", "stardewcraft.leaderboard.value.times", true);

    private final LeaderboardCategory category;
    private final String id;
    private final String titleKey;
    private final String shortKey;
    private final String descriptionKey;
    private final String valueKey;
    private final boolean descending;

    LeaderboardMetric(LeaderboardCategory category, String id, String titleKey, String shortKey, String descriptionKey, String valueKey, boolean descending) {
        this.category = category;
        this.id = id;
        this.titleKey = titleKey;
        this.shortKey = shortKey;
        this.descriptionKey = descriptionKey;
        this.valueKey = valueKey;
        this.descending = descending;
    }

    public LeaderboardCategory category() {
        return category;
    }

    public String id() {
        return id;
    }

    public String titleKey() {
        return titleKey;
    }

    public String shortKey() {
        return shortKey;
    }

    public String descriptionKey() {
        return descriptionKey;
    }

    public String valueKey() {
        return valueKey;
    }

    public boolean descending() {
        return descending;
    }

    public static Optional<LeaderboardMetric> fromId(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(metric -> metric.id.equals(id)).findFirst();
    }
}
