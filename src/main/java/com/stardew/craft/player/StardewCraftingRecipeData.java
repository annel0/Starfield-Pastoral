package com.stardew.craft.player;

import com.google.gson.Gson;
import com.stardew.craft.StardewCraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
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

    public record IngredientEntry(String item, String tag, String displayItem, String displayName, int count) {
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
        return getIngredientEntries(id, false);
    }

    /**
     * Get ingredient entries, with optional Trapper profession modifier.
     * SDV: CraftingRecipe constructor checks profession 7 (Trapper) and reduces
     * Crab Pot recipe from (40 wood + 3 iron bar) to (25 wood + 2 iron bar).
     */
    public static List<IngredientEntry> getIngredientEntries(String id, boolean hasTrapper) {
        Optional<RecipeEntry> recipe = getRecipe(id);
        if (recipe.isEmpty() || recipe.get().ingredients() == null) {
            return List.of();
        }

        // SDV parity: Trapper profession reduces crab pot crafting cost
        if (hasTrapper && "crab_pot".equals(id)) {
            return List.of(
                new IngredientEntry("stardewcraft:wood_normal", null, null, null, 25),
                new IngredientEntry("stardewcraft:iron_bar", null, null, null, 2)
            );
        }

        List<IngredientEntry> list = new ArrayList<>();
        for (IngredientEntry entry : recipe.get().ingredients) {
            if (entry == null || !hasIngredientTarget(entry) || entry.count() <= 0) {
                continue;
            }
            list.add(entry);
        }
        return list;
    }

    @SuppressWarnings("null")
    public static List<Ingredient> toExpandedIngredients(String id) {
        return toExpandedIngredients(id, false);
    }

    @SuppressWarnings("null")
    public static List<Ingredient> toExpandedIngredients(String id, boolean hasTrapper) {
        List<IngredientEntry> entries = getIngredientEntries(id, hasTrapper);
        if (entries.isEmpty()) {
            return List.of();
        }

        List<Ingredient> expanded = new ArrayList<>();
        for (IngredientEntry entry : entries) {
            Ingredient ingredient = toIngredient(entry);
            if (ingredient.isEmpty()) {
                continue;
            }
            int count = Math.max(1, entry.count());
            for (int i = 0; i < count; i++) {
                expanded.add(ingredient);
            }
        }
        return expanded;
    }

    @SuppressWarnings("null")
    public static Ingredient toIngredient(IngredientEntry entry) {
        if (entry == null) {
            return Ingredient.EMPTY;
        }

        String tagValue = entry.tag() == null ? "" : entry.tag().trim();
        if (!tagValue.isEmpty()) {
            ResourceLocation tagId = ResourceLocation.tryParse(tagValue);
            if (tagId == null) {
                return Ingredient.EMPTY;
            }
            return Ingredient.of(TagKey.create(Registries.ITEM, tagId));
        }

        String itemValue = entry.item() == null ? "" : entry.item().trim();
        ResourceLocation itemId = ResourceLocation.tryParse(itemValue);
        if (itemId == null) {
            return Ingredient.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null || item == Items.AIR) {
            return Ingredient.EMPTY;
        }
        return Ingredient.of(new ItemStack(item));
    }

    @SuppressWarnings("null")
    public static ItemStack getDisplayStack(IngredientEntry entry) {
        if (entry == null) {
            return ItemStack.EMPTY;
        }
        String displayItem = entry.displayItem() == null || entry.displayItem().isBlank()
                ? entry.item()
                : entry.displayItem();
        if (displayItem == null || displayItem.isBlank()) {
            return ItemStack.EMPTY;
        }
        ResourceLocation itemId = ResourceLocation.tryParse(displayItem);
        if (itemId == null) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item == null || item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        int count = Math.max(1, entry.count());
        return new ItemStack(item, count);
    }

    public static Component getDisplayName(IngredientEntry entry) {
        if (entry == null) {
            return Component.empty();
        }
        if (entry.displayName() != null && !entry.displayName().isBlank()) {
            return Component.translatable(entry.displayName().trim());
        }
        ItemStack displayStack = getDisplayStack(entry);
        if (!displayStack.isEmpty()) {
            return displayStack.getHoverName().copy();
        }
        return Component.empty();
    }

    private static boolean hasIngredientTarget(IngredientEntry entry) {
        return (entry.item() != null && !entry.item().isBlank())
                || (entry.tag() != null && !entry.tag().isBlank());
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
