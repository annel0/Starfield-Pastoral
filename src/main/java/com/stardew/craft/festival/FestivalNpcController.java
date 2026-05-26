package com.stardew.craft.festival;

public final class FestivalNpcController {
    private FestivalNpcController() {
    }

    public static boolean controlsNpc(String npcId) {
        return ActiveFestivalHandlers.controlsNpc(npcId);
    }
}