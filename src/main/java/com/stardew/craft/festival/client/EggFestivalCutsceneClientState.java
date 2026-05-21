package com.stardew.craft.festival.client;

import java.util.List;
import java.util.UUID;

public final class EggFestivalCutsceneClientState {
    private static int participantCount;
    private static boolean playerWon;
    private static int winnerMask;
    private static String winnerText = "";
    private static List<UUID> participantIds = List.of();

    private EggFestivalCutsceneClientState() {
    }

    public static void set(int count, boolean won, int mask, String text, List<UUID> ids) {
        participantCount = Math.max(0, count);
        playerWon = won;
        winnerMask = mask;
        winnerText = text == null ? "" : text;
        participantIds = ids == null ? List.of() : List.copyOf(ids);
    }

    public static int participantCount() {
        return participantCount;
    }

    public static boolean playerWon() {
        return playerWon;
    }

    public static boolean isWinnerIndex(int index) {
        return index >= 0 && index < Integer.SIZE - 1 && (winnerMask & (1 << index)) != 0;
    }

    public static String winnerText() {
        return winnerText;
    }

    public static UUID participantId(int index) {
        return index >= 0 && index < participantIds.size() ? participantIds.get(index) : null;
    }
}