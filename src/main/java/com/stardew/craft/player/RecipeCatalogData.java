package com.stardew.craft.player;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.ModItems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Known recipe catalog used for player-owned recipe systems.
 *
 * Uses data file for crafting recipes and keeps cooking list extensible from the ModItems registry map.
 */
public final class RecipeCatalogData {
    private static final Set<String> CRAFTING_RECIPES = loadCraftingRecipes();

    private RecipeCatalogData() {
    }

    public static List<String> getCraftingRecipeIds() {
        return List.copyOf(CRAFTING_RECIPES);
    }

    public static List<String> getCookingRecipeIds() {
        return List.copyOf(ModItems.COOKING_DISHES.keySet());
    }

    public static List<String> getRecipeIds(String category) {
        if (category == null || category.isBlank()) {
            return List.of();
        }

        String normalized = category.toLowerCase(Locale.ROOT);
        if ("cooking".equals(normalized)) {
            return getCookingRecipeIds();
        }
        if ("crafting".equals(normalized)) {
            return getCraftingRecipeIds();
        }
        return List.of();
    }

    public static List<String> getAllKnownRecipeIds() {
        Set<String> all = new LinkedHashSet<>();
        all.addAll(CRAFTING_RECIPES);
        all.addAll(ModItems.COOKING_DISHES.keySet());
        return List.copyOf(all);
    }

    private static Set<String> loadCraftingRecipes() {
        List<String> ids = StardewCraftingRecipeData.getRecipeIds();
        if (ids.isEmpty()) {
            StardewCraft.LOGGER.warn("Stardew crafting recipe data is empty");
            return Set.of();
        }

        List<String> normalized = new ArrayList<>();
        for (String id : ids) {
            if (id == null || id.isBlank()) {
                continue;
            }
            if (!normalized.contains(id)) {
                normalized.add(id);
            }
        }

        StardewCraft.LOGGER.info("Loaded crafting recipe catalog from Stardew data: {} entries", normalized.size());
        return Collections.unmodifiableSet(new LinkedHashSet<>(normalized));
    }
}
