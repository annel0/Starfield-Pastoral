package com.stardew.craft.festival;

import net.minecraft.core.BlockPos;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class FestivalMapOverlayRegistry {
    private static final Map<String, FestivalMapOverlayDefinition> DEFINITIONS = new LinkedHashMap<>();

    static {
        register(new FestivalMapOverlayDefinition(
            "Town-EggFestival",
            "Town",
            new BlockPos(-38, 63, -23),
            "",
            "data/stardewcraft/structures/festivals/egg_festival_town.schem",
            new BlockPos(-38, 63, -23),
            new BlockPos(67, 68, 52),
            List.of(),
            true,
            true,
            true
        ));
    }

    private FestivalMapOverlayRegistry() {
    }

    public static void register(FestivalMapOverlayDefinition definition) {
        if (definition == null) {
            return;
        }
        DEFINITIONS.put(normalize(definition.overlayId()), definition);
    }

    public static Optional<FestivalMapOverlayDefinition> get(String overlayId) {
        if (overlayId == null || overlayId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(DEFINITIONS.get(normalize(overlayId)));
    }

    public static boolean isRegistered(String overlayId) {
        return get(overlayId).isPresent();
    }

    public static Collection<FestivalMapOverlayDefinition> all() {
        return java.util.List.copyOf(DEFINITIONS.values());
    }

    private static String normalize(String overlayId) {
        return overlayId.toLowerCase(Locale.ROOT);
    }
}