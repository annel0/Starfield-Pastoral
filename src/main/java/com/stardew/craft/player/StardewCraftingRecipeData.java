package com.stardew.craft.player;

import com.google.gson.Gson;
import com.stardew.craft.StardewCraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class StardewCraftingRecipeData {
    private static final Gson GSON = new Gson();
    private static final String DATA_PATH = "/data/stardewcraft/player/vanilla_crafting_recipes.json";

    public record IngredientEntry(String item, int count) {
    }

    public record OutputEntry(String item, int count) {
    }

    public record RecipeEntry(String id, OutputEntry output, List<IngredientEntry> ingredients, String unlockCondition) {
    }

    private record RawData(List<RecipeEntry> recipes) {
    }

    private static final Map<String, RecipeEntry> RECIPES = loadRecipes();

    private StardewCraftingRecipeData() {
    }

    public static List<String> getRecipeIds() {
        return List.copyOf(RECIPES.keySet());
    }

    public static Optional<RecipeEntry> getRecipe(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(RECIPES.get(id));
    }

    public static List<RecipeEntry> getRecipes() {
        return List.copyOf(RECIPES.values());
    }

    public static String getUnlockCondition(String id) {
        Optional<RecipeEntry> recipe = getRecipe(id);
        if (recipe.isEmpty()) {
            return "";
        }
        String value = recipe.get().unlockCondition();
        return value == null ? "" : value.trim();
    }

    @SuppressWarnings("null")
    public static ItemStack getOutputStack(String id) {
        Optional<RecipeEntry> recipe = getRecipe(id);
        if (recipe.isEmpty() || recipe.get().output == null) {
            return ItemStack.EMPTY;
        }
        ResourceLocation itemId = ResourceLocation.tryParse(recipe.get().output.item());
        if (itemId == null) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        int count = Math.max(1, recipe.get().output.count());
        return new ItemStack(item, count);
    }

    public static List<IngredientEntry> getIngredientEntries(String id) {
        Optional<RecipeEntry> recipe = getRecipe(id);
        if (recipe.isEmpty() || recipe.get().ingredients == null) {
            return List.of();
        }
        List<IngredientEntry> list = new ArrayList<>();
        for (IngredientEntry entry : recipe.get().ingredients) {
            if (entry == null || entry.item() == null || entry.item().isBlank() || entry.count() <= 0) {
                continue;
            }
            list.add(entry);
        }
        return list;
    }

    @SuppressWarnings("null")
    public static List<Ingredient> toExpandedIngredients(String id) {
        List<IngredientEntry> entries = getIngredientEntries(id);
        if (entries.isEmpty()) {
            return List.of();
        }

        List<Ingredient> expanded = new ArrayList<>();
        for (IngredientEntry entry : entries) {
            ResourceLocation itemId = ResourceLocation.tryParse(entry.item());
            if (itemId == null) {
                continue;
            }
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item == null || item == Items.AIR) {
                continue;
            }
            Ingredient ingredient = Ingredient.of(new ItemStack(item));
            int count = Math.max(1, entry.count());
            for (int i = 0; i < count; i++) {
                expanded.add(ingredient);
            }
        }
        return expanded;
    }

    private static Map<String, RecipeEntry> loadRecipes() {
        try (InputStream in = StardewCraftingRecipeData.class.getResourceAsStream(DATA_PATH)) {
            if (in == null) {
                StardewCraft.LOGGER.warn("Missing vanilla crafting recipe data: {}", DATA_PATH);
                return Map.of();
            }

            RawData raw = GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), RawData.class);
            if (raw == null || raw.recipes == null) {
                return Map.of();
            }

            Map<String, RecipeEntry> map = new LinkedHashMap<>();
            Set<String> seen = new LinkedHashSet<>();
            for (RecipeEntry recipe : raw.recipes) {
                if (recipe == null || recipe.id == null || recipe.id.isBlank()) {
                    continue;
                }
                String key = recipe.id.trim().toLowerCase(Locale.ROOT);
                if (seen.add(key)) {
                    map.put(key, recipe);
                }
            }

            StardewCraft.LOGGER.info("Loaded Stardew crafting recipes: {} entries", map.size());
            return Collections.unmodifiableMap(map);
        } catch (Exception ex) {
            StardewCraft.LOGGER.warn("Failed to load Stardew crafting recipes: {}", ex.getMessage());
            return Map.of();
        }
    }
}
