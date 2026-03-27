package com.stardew.craft.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class CookingIngredientAvailabilityCache {
    private static Map<String, Integer> fridgeTokenCounts = Map.of();

    private CookingIngredientAvailabilityCache() {
    }

    public static void setFridgeTokenCounts(Map<String, Integer> counts) {
        if (counts == null || counts.isEmpty()) {
            fridgeTokenCounts = Map.of();
            return;
        }
        fridgeTokenCounts = Collections.unmodifiableMap(new HashMap<>(counts));
    }

    public static int getFridgeTokenCount(String token) {
        if (token == null || token.isBlank()) {
            return 0;
        }
        return fridgeTokenCounts.getOrDefault(token, 0);
    }
}
