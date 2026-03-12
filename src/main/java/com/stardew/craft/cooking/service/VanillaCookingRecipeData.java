package com.stardew.craft.cooking.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.IStardewItem;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class VanillaCookingRecipeData {
    public record IngredientRequirement(String token, int count) {}

    private static final Gson GSON = new Gson();
    private static final String RECIPES_PATH = "/data/stardewcraft/cooking/vanilla_cooking_recipes.json";
    private static final String TOKEN_MAP_PATH = "/data/stardewcraft/cooking/vanilla_cooking_ingredient_map.json";

    private static final Map<String, List<IngredientRequirement>> RECIPES = loadRecipes();
    private static final Map<String, String> TOKEN_TO_ITEM_PATH = loadTokenToItemPath();
    private static final Map<String, ResourceLocation> FALLBACK_ITEMS = Map.of(
            "sugar", ResourceLocation.fromNamespaceAndPath("minecraft", "sugar"),
            "dandelion", ResourceLocation.fromNamespaceAndPath("minecraft", "dandelion"),
            "moss", ResourceLocation.fromNamespaceAndPath("minecraft", "moss_block")
    );

    private VanillaCookingRecipeData() {
    }

    public static List<IngredientRequirement> getRequirements(String dishItemPath) {
        if (dishItemPath == null || dishItemPath.isBlank()) {
            return List.of();
        }
        return RECIPES.getOrDefault(dishItemPath, List.of());
    }

    public static boolean matchesToken(ItemStack stack, String token) {
        if (stack == null || stack.isEmpty() || token == null || token.isBlank()) {
            return false;
        }

        Integer category = tryParseCategoryToken(token);
        if (category != null) {
            return matchesCategory(stack, category);
        }

        Item item = resolveTokenItem(token);
        return item != null && stack.is(item);
    }

    @SuppressWarnings("null")
    public static Item resolveTokenItem(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String path = TOKEN_TO_ITEM_PATH.get(token);
        if (path == null || path.isBlank()) {
            return null;
        }

        ResourceLocation stardewId = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, path);
        if (BuiltInRegistries.ITEM.containsKey(stardewId)) {
            return BuiltInRegistries.ITEM.get(stardewId);
        }

        ResourceLocation fallback = FALLBACK_ITEMS.get(path);
        if (fallback != null && BuiltInRegistries.ITEM.containsKey(fallback)) {
            return BuiltInRegistries.ITEM.get(fallback);
        }

        return null;
    }

    @SuppressWarnings("null")
    public static Component describeToken(String token) {
        Integer category = tryParseCategoryToken(token);
        if (category != null) {
            return switch (category) {
                case -4 -> Component.translatable("stardewcraft.cooking.ingredient.any_fish");
                case -5 -> Component.translatable("stardewcraft.cooking.ingredient.any_egg");
                case -6 -> Component.translatable("stardewcraft.cooking.ingredient.any_milk");
                default -> Component.literal(token);
            };
        }

        Item item = resolveTokenItem(token);
        if (item != null) {
            return item.getDefaultInstance().getHoverName();
        }

        String raw = TOKEN_TO_ITEM_PATH.get(token);
        return Component.literal(raw == null ? token : raw);
    }

    private static Integer tryParseCategoryToken(String token) {
        try {
            int parsed = Integer.parseInt(token);
            if (parsed < 0) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
        }
        return null;
    }

    @SuppressWarnings("null")
    private static boolean matchesCategory(ItemStack stack, int category) {
        if (!(stack.getItem() instanceof IStardewItem stardewItem)) {
            return false;
        }

        String typeKey = stardewItem.getItemTypeKey();
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();

        return switch (category) {
            case -4 -> "stardewcraft.type.fish".equals(typeKey)
                    || "stardewcraft.type.legendary_fish".equals(typeKey);
            case -5 -> "stardewcraft.type.animal_product".equals(typeKey) && path.contains("egg");
            case -6 -> "stardewcraft.type.animal_product".equals(typeKey) && path.contains("milk");
            default -> false;
        };
    }

    private static Map<String, List<IngredientRequirement>> loadRecipes() {
        try (InputStream in = VanillaCookingRecipeData.class.getResourceAsStream(RECIPES_PATH)) {
            if (in == null) {
                StardewCraft.LOGGER.warn("Missing cooking recipe data resource: {}", RECIPES_PATH);
                return Map.of();
            }

            Type type = new TypeToken<LinkedHashMap<String, List<IngredientRequirement>>>() {
            }.getType();
            Map<String, List<IngredientRequirement>> parsed = GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), type);
            if (parsed == null) {
                return Map.of();
            }
            StardewCraft.LOGGER.info("Loaded vanilla cooking recipes: {} entries", parsed.size());
            return Collections.unmodifiableMap(parsed);
        } catch (Exception ex) {
            StardewCraft.LOGGER.warn("Failed to load cooking recipes: {}", ex.getMessage());
            return Map.of();
        }
    }

    private static Map<String, String> loadTokenToItemPath() {
        try (InputStream in = VanillaCookingRecipeData.class.getResourceAsStream(TOKEN_MAP_PATH)) {
            if (in == null) {
                StardewCraft.LOGGER.warn("Missing ingredient token map resource: {}", TOKEN_MAP_PATH);
                return Map.of();
            }

            Type type = new TypeToken<LinkedHashMap<String, String>>() {
            }.getType();
            Map<String, String> parsed = GSON.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), type);
            if (parsed == null) {
                return Map.of();
            }

            Map<String, String> normalized = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : parsed.entrySet()) {
                normalized.put(entry.getKey(), entry.getValue().toLowerCase(Locale.ROOT));
            }
            return Collections.unmodifiableMap(normalized);
        } catch (Exception ex) {
            StardewCraft.LOGGER.warn("Failed to load ingredient token map: {}", ex.getMessage());
            return Map.of();
        }
    }
}
