package com.stardew.craft.leaderboard;

import java.util.List;

public record LeaderboardSnapshot(
    LeaderboardMetric metric,
    List<LeaderboardEntry> rows,
    LeaderboardEntry selfEntry,
    int totalPlayers,
    long generatedAtMillis
) {
}
