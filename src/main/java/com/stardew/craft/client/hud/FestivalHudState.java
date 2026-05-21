package com.stardew.craft.client.hud;

public final class FestivalHudState {
    private static boolean hidden;

    private FestivalHudState() {
    }

    public static boolean hidden() {
        return hidden;
    }

    public static void setHidden(boolean nextHidden) {
        hidden = nextHidden;
    }
}