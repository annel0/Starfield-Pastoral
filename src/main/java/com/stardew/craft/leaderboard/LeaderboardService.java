package com.stardew.craft.leaderboard;

import com.mojang.authlib.GameProfile;
import com.stardew.craft.mining.MiningDataManager;
import com.stardew.craft.mining.MiningPlayerData;
import com.stardew.craft.player.PlayerDataManager;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class LeaderboardService {
    private static final int DEFAULT_LIMIT = 10;
    private static final long CACHE_TTL_MILLIS = 5000L;
    private static final Map<CacheKey, CachedSnapshot> CACHE = new HashMap<>();

    private LeaderboardService() {
    }

    public static void invalidateCache() {
        CACHE.clear();
    }

    public static LeaderboardSnapshot buildSnapshot(ServerPlayer requester, LeaderboardMetric metric, int page) {
        int safePage = Math.max(0, page);
        CacheKey key = new CacheKey(requester.getUUID(), metric, safePage);
        CachedSnapshot cached = CACHE.get(key);
        long now = System.currentTimeMillis();
        if (cached != null && now - cached.createdAtMillis() <= CACHE_TTL_MILLIS) {
            return cached.snapshot();
        }

        PlayerDataManager playerDataManager = PlayerDataManager.get();
        Map<UUID, PlayerStardewData> playerData = playerDataManager.getAllPlayerData();
        Map<UUID, MiningPlayerData> miningData = MiningDataManager.getAllPlayerData(requester);

        Set<UUID> playerIds = new HashSet<>();
        playerIds.addAll(playerData.keySet());
        playerIds.addAll(miningData.keySet());
        requester.server.getPlayerList().getPlayers().forEach(player -> playerIds.add(player.getUUID()));

        ArrayList<UnrankedEntry> unranked = new ArrayList<>();
        for (UUID playerId : playerIds) {
            PlayerStardewData data = playerData.get(playerId);
            long value = valueFor(metric, playerData.get(playerId), miningData.get(playerId));
            unranked.add(new UnrankedEntry(playerId, resolvePlayerName(requester.server, playerId, data), value,
                    requester.server.getPlayerList().getPlayer(playerId) != null,
                    requester.getUUID().equals(playerId)));
        }

        Comparator<UnrankedEntry> valueComparator = Comparator.comparingLong(UnrankedEntry::value);
        if (metric.descending()) {
            valueComparator = valueComparator.reversed();
        }
        unranked.sort(valueComparator
            .thenComparing(UnrankedEntry::playerName, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(entry -> entry.playerId().toString()));

        ArrayList<LeaderboardEntry> ranked = new ArrayList<>(unranked.size());
        LeaderboardEntry selfEntry = null;
        for (int i = 0; i < unranked.size(); i++) {
            UnrankedEntry entry = unranked.get(i);
            LeaderboardEntry rankedEntry = new LeaderboardEntry(
                    entry.playerId(), entry.playerName(), entry.value(), i + 1, entry.online(), entry.self());
            ranked.add(rankedEntry);
            if (entry.self()) {
                selfEntry = rankedEntry;
            }
        }

        int from = safePage * DEFAULT_LIMIT;
        int to = Math.min(ranked.size(), from + DEFAULT_LIMIT);
        var rows = from >= ranked.size() ? java.util.List.<LeaderboardEntry>of() : java.util.List.copyOf(ranked.subList(from, to));
        LeaderboardSnapshot snapshot = new LeaderboardSnapshot(metric, rows, selfEntry, ranked.size(), now);
        CACHE.put(key, new CachedSnapshot(snapshot, now));
        return snapshot;
    }

    private static long valueFor(LeaderboardMetric metric, PlayerStardewData playerData, MiningPlayerData miningData) {
        return switch (metric) {
            case MONEY -> playerData == null ? 0L : playerData.getMoney();
            case MINE_DEPTH -> miningData == null ? 0L : miningData.getMaxFloorReached();
            case MINE_BLOCKS_BROKEN -> playerData == null ? 0L : playerData.getMineBlocksBrokenTotal();
            case MINE_BLOCKS_BOMBED -> playerData == null ? 0L : playerData.getMineBlocksBombed();
            case FISH_CAUGHT -> playerData == null ? 0L : playerData.getPreciseFishCaught();
            case ITEMS_SHIPPED -> playerData == null ? 0L : playerData.getAllItemsShipped().values().stream().mapToLong(Integer::longValue).sum();
            case SHIPPING_VALUE -> playerData == null ? 0L : playerData.getTotalShippingGold();
            case MONSTERS_SLAIN -> playerData == null ? 0L : playerData.getAllMonsterKills().values().stream().mapToLong(Integer::longValue).sum();
            case TRASH_CANS_CHECKED -> playerData == null ? 0L : playerData.getTrashCansChecked();
        };
    }

    private static String resolvePlayerName(MinecraftServer server, UUID playerId, PlayerStardewData playerData) {
        ServerPlayer online = server.getPlayerList().getPlayer(playerId);
        if (online != null) {
            return online.getName().getString();
        }
        if (playerData != null && !playerData.getLastKnownName().isBlank()) {
            return playerData.getLastKnownName();
        }
        var profileCache = server.getProfileCache();
        if (profileCache != null) {
            return profileCache.get(playerId)
                    .map(GameProfile::getName)
                    .filter(name -> !name.isBlank())
                    .orElse("Player-" + playerId.toString().substring(0, 8));
        }
        return "Player-" + playerId.toString().substring(0, 8);
    }

    private record UnrankedEntry(UUID playerId, String playerName, long value, boolean online, boolean self) {
    }

    private record CacheKey(UUID requesterId, LeaderboardMetric metric, int page) {
    }

    private record CachedSnapshot(LeaderboardSnapshot snapshot, long createdAtMillis) {
    }
}
