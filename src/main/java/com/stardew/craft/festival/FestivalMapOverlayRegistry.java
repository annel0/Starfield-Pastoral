package com.stardew.craft.festival;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class FestivalMapOverlayRegistry {
    private static final Map<String, FestivalMapOverlayDefinition> DEFINITIONS = new LinkedHashMap<>();

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

    public static Collection<FestivalMapOverlayDefinition> all() {
        return java.util.List.copyOf(DEFINITIONS.values());
    }

    private static String normalize(String overlayId) {
        return overlayId.toLowerCase(Locale.ROOT);
    }
}