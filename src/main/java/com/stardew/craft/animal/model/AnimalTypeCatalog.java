package com.stardew.craft.animal.model;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class AnimalTypeCatalog {
    private static final Map<String, AnimalTypeSpec> KNOWN = Map.ofEntries(
        Map.entry("white_chicken", new AnimalTypeSpec("white_chicken", "coop", 3)),
        Map.entry("brown_chicken", new AnimalTypeSpec("brown_chicken", "coop", 3)),
        Map.entry("blue_chicken", new AnimalTypeSpec("blue_chicken", "coop", 3)),
        Map.entry("golden_chicken", new AnimalTypeSpec("golden_chicken", "coop", 3)),
        Map.entry("duck", new AnimalTypeSpec("duck", "coop", 5)),
        Map.entry("void_chicken", new AnimalTypeSpec("void_chicken", "coop", 3)),
        Map.entry("rabbit", new AnimalTypeSpec("rabbit", "coop", 6)),
        Map.entry("ostrich", new AnimalTypeSpec("ostrich", "coop", 7)),
        Map.entry("dinosaur", new AnimalTypeSpec("dinosaur", "coop", 0)),
        Map.entry("cow", new AnimalTypeSpec("cow", "barn", 5)),
        Map.entry("goat", new AnimalTypeSpec("goat", "barn", 5)),
        Map.entry("sheep", new AnimalTypeSpec("sheep", "barn", 4)),
        Map.entry("pig", new AnimalTypeSpec("pig", "barn", 10))
    );

    private AnimalTypeCatalog() {
    }

    public static AnimalTypeSpec resolve(String animalTypeId) {
        if (animalTypeId == null || animalTypeId.isBlank()) {
            return new AnimalTypeSpec("unknown", "barn", 5);
        }
        String key = animalTypeId.toLowerCase(Locale.ROOT);
        return KNOWN.getOrDefault(key, new AnimalTypeSpec(key, classifyFamilyFallback(key), 5));
    }

    private static String classifyFamilyFallback(String key) {
        if (key.contains("chicken") || key.contains("duck") || key.contains("rabbit") || key.contains("dinosaur") || key.contains("ostrich")) {
            return "coop";
        }
        return "barn";
    }

    public static Set<String> knownTypeIds() {
        return KNOWN.keySet();
    }

    public record AnimalTypeSpec(String id, String family, int daysToMature) {
    }
}
