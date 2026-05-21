package com.stardew.craft.festival;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class FestivalRegistry {
    public static final int SPRING = 0;
    public static final int SUMMER = 1;
    public static final int FALL = 2;
    public static final int WINTER = 3;

    private static final Map<String, FestivalDefinition> DEFINITIONS = new LinkedHashMap<>();

    static {
        register(new FestivalDefinition(
            "spring13", FestivalType.ACTIVE, "Egg Festival", "Egg Festival", "",
            SPRING, 13, 13, 900, 1400, true, false, "",
            "Town", "Town-EggFestival", Map.of(), List.of("Festival_EggFestival_Pierre"), "egg_hunt"
        ));
        register(new FestivalDefinition(
            "spring24", FestivalType.ACTIVE, "Flower Dance", "Flower Dance", "",
            SPRING, 24, 24, 900, 1400, true, false, "",
            "Forest", "Forest-FlowerFestival", Map.of(), List.of(), "flower_dance"
        ));
        register(new FestivalDefinition(
            "summer11", FestivalType.ACTIVE, "Luau", "Luau", "",
            SUMMER, 11, 11, 900, 1400, true, false, "",
            "Beach", "Beach-Luau", Map.of(), List.of(), "luau_soup"
        ));
        register(new FestivalDefinition(
            "summer28", FestivalType.ACTIVE, "Dance of the Moonlight Jellies", "Dance of the Moonlight Jellies", "",
            SUMMER, 28, 28, 2200, 2400, true, false, "",
            "Beach", "Beach-Jellies", Map.of(), List.of(), "moonlight_jellies"
        ));
        register(new FestivalDefinition(
            "fall16", FestivalType.ACTIVE, "Stardew Valley Fair", "Stardew Valley Fair", "",
            FALL, 16, 16, 900, 1500, true, false, "",
            "Town", "Town-Fair", Map.of(), List.of("Festival_Fair_StarTokenShop"), "stardew_valley_fair"
        ));
        register(new FestivalDefinition(
            "fall27", FestivalType.ACTIVE, "Spirit's Eve", "Spirit's Eve", "",
            FALL, 27, 27, 2200, 2350, true, false, "",
            "Town", "Town-Halloween", Map.of(), List.of("Festival_SpiritsEve"), "spirit_eve"
        ));
        register(new FestivalDefinition(
            "winter8", FestivalType.ACTIVE, "Festival of Ice", "Festival of Ice", "",
            WINTER, 8, 8, 900, 1400, true, false, "",
            "Forest", "Forest-IceFestival", Map.of(), List.of(), "ice_fishing_contest"
        ));
        register(new FestivalDefinition(
            "winter25", FestivalType.ACTIVE, "Feast of the Winter Star", "Feast of the Winter Star", "",
            WINTER, 25, 25, 900, 1400, true, false, "",
            "Town", "Town-Christmas", Map.of(), List.of(), "winter_star_gift"
        ));

        register(new FestivalDefinition(
            "DesertFestival", FestivalType.PASSIVE, "Desert Festival", "[LocalizedText Strings\\1_6_Strings:DesertFestival]", "LOCATION_ACCESSIBLE Desert",
            SPRING, 15, 17, 1000, 2600, true, false, "[LocalizedText Strings\\1_6_Strings:DesertFestival_NowOpen]",
            "Desert", "DesertFestival", Map.of("Desert", "DesertFestival"), List.of(), "desert_festival"
        ));
        register(new FestivalDefinition(
            "TroutDerby", FestivalType.PASSIVE, "Trout Derby", "", "",
            SUMMER, 20, 21, 610, 2600, false, true, "[LocalizedText Strings\\1_6_Strings:TroutDerby_NowOpen]",
            "Forest", "", Map.of(), List.of(), "trout_derby"
        ));
        register(new FestivalDefinition(
            "SquidFest", FestivalType.PASSIVE, "SquidFest", "", "",
            WINTER, 12, 13, 610, 2600, false, true, "[LocalizedText Strings\\1_6_Strings:SquidFest_NowOpen]",
            "Beach", "", Map.of(), List.of(), "squid_fest"
        ));
        register(new FestivalDefinition(
            "NightMarket", FestivalType.PASSIVE, "Night Market", "[LocalizedText Strings\\UI:Billboard_NightMarket]", "",
            WINTER, 15, 17, 1700, 2600, true, false, "[LocalizedText Strings\\Events:BeachNightMarket_NowOpen]",
            "Beach", "BeachNightMarket", Map.of("Beach", "BeachNightMarket"), List.of(), "night_market"
        ));
    }

    private FestivalRegistry() {
    }

    private static void register(FestivalDefinition definition) {
        DEFINITIONS.put(normalizeId(definition.id()), definition);
    }

    public static Optional<FestivalDefinition> get(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(DEFINITIONS.get(normalizeId(id)));
    }

    public static Optional<FestivalDefinition> getByOverlayId(String overlayId) {
        if (overlayId == null || overlayId.isBlank()) {
            return Optional.empty();
        }
        String normalized = overlayId.toLowerCase(Locale.ROOT);
        return DEFINITIONS.values().stream()
            .filter(definition -> definition.mapOverlayId().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst();
    }

    public static Collection<FestivalDefinition> all() {
        return List.copyOf(DEFINITIONS.values());
    }

    public static List<FestivalDefinition> activeFestivals() {
        return DEFINITIONS.values().stream()
            .filter(definition -> definition.type() == FestivalType.ACTIVE)
            .toList();
    }

    public static List<FestivalDefinition> passiveFestivals() {
        return DEFINITIONS.values().stream()
            .filter(definition -> definition.type() == FestivalType.PASSIVE)
            .toList();
    }

    private static String normalizeId(String id) {
        return id.toLowerCase(Locale.ROOT);
    }
}