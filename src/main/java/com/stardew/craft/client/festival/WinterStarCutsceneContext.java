package com.stardew.craft.client.festival;

/** Client-only variables supplied before the shared Winter Star cutscene starts. */
public final class WinterStarCutsceneContext {
    private static String giverId = "lewis";
    private static String beforeKey = "stardewcraft.festival.winter_star.giver.default_before";
    private static String afterKey = "stardewcraft.festival.winter_star.giver.default_after";
    private static String itemId = "minecraft:air";

    private WinterStarCutsceneContext() {
    }

    public static void configure(String giver, String before, String after, String item) {
        giverId = giver == null || giver.isBlank() ? "lewis" : giver;
        beforeKey = before == null ? "" : before;
        afterKey = after == null ? "" : after;
        itemId = item == null || item.isBlank() ? "minecraft:air" : item;
    }

    public static String giverId() {
        return giverId;
    }

    public static String beforeKey() {
        return beforeKey;
    }

    public static String afterKey() {
        return afterKey;
    }

    public static String itemId() {
        return itemId;
    }
}
