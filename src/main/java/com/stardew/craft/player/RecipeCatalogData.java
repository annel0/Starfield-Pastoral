package com.stardew.craft.player;

import com.google.gson.Gson;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.ModItems;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
    private record RawCatalog(List<String> crafting) {
    }

    private static final Gson GSON = new Gson();
    private static final String DATA_PATH = "/data/stardewcraft/player/known_recipe_catalog.json";

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
        try (InputStream in = RecipeCatalogData.class.getResourceAsStream(DATA_PATH)) {
            if (in == null) {
                StardewCraft.LOGGER.warn("Missing known recipe catalog: {}", DATA_PATH);
                return Set.of();
            }

            RawCatalog parsed = GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), RawCatalog.class);
            if (parsed == null || parsed.crafting == null || parsed.crafting.isEmpty()) {
                return Set.of();
            }

            List<String> normalized = new ArrayList<>();
            for (String id : parsed.crafting) {
                if (id == null || id.isBlank()) {
                    continue;
                }
                if (!normalized.contains(id)) {
                    normalized.add(id);
                }
            }
            StardewCraft.LOGGER.info("Loaded crafting recipe catalog: {} entries", normalized.size());
            return Collections.unmodifiableSet(new LinkedHashSet<>(normalized));
        } catch (Exception ex) {
            StardewCraft.LOGGER.warn("Failed to load known recipe catalog: {}", ex.getMessage());
            return Set.of();
        }
    }
}
