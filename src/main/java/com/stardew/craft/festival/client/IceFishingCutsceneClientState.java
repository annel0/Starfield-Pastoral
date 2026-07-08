package com.stardew.craft.festival.client;

public final class IceFishingCutsceneClientState {
    private static boolean playerWon;
    private static String winnerText = "";

    private IceFishingCutsceneClientState() {
    }

    public static void set(boolean won, String text) {
        playerWon = won;
        winnerText = text == null ? "" : text;
    }

    public static boolean playerWon() {
        return playerWon;
    }

    public static String winnerText() {
        return winnerText;
    }
}
