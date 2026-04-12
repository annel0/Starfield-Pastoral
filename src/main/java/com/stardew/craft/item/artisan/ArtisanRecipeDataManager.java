package com.stardew.craft.item.artisan;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.item.IStardewItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ArtisanRecipeDataManager {
    private ArtisanRecipeDataManager() {
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile Map<String, List<Recipe>> RECIPES_BY_MACHINE = Collections.emptyMap();

    public enum InputMode {
        DEFAULT,
        CROP_TYPE,
        MINERAL_TYPE,
        FISH_TYPE
    }

    public enum OutputMode {
        FIXED,
        COPY_INPUT,
        SEEDMAKER,
        SMOKED
    }

    public record SeedMakerRule(double ancientChance,
                                double mixedChance,
                                int mixedMin,
                                int mixedMax,
                                int seedMin,
                                int seedMax) {
    }

    private static final SeedMakerRule DEFAULT_SEEDMAKER_RULE = new SeedMakerRule(0.005, 0.02, 1, 4, 1, 3);

    public record Recipe(@Nullable ResourceLocation inputId,
                         @Nullable TagKey<Item> inputTag,
                         InputMode inputMode,
                         @Nullable ResourceLocation outputId,
                         int outputCount,
                         int minutes,
                         int consumeCount,
                         boolean keepInputQuality,
                         int outputQuality,
                         @Nullable PreserveType preserveType,
                         @Nullable SeedMakerRule seedMakerRule,
                         OutputMode outputMode) {
        @SuppressWarnings("null")
        public boolean matches(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            if (inputMode != null && inputMode != InputMode.DEFAULT) {
                return matchesByMode(stack, inputMode);
            }
            if (inputId != null) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                return inputId.equals(id);
            }
            if (inputTag != null) {
                return stack.is(inputTag);
            }
            return false;
        }
    }

    private static boolean matchesByMode(ItemStack stack, InputMode mode) {
        Item item = stack.getItem();
        if (!(item instanceof IStardewItem stardewItem)) {
            return false;
        }
        String typeKey = stardewItem.getItemTypeKey();
        return switch (mode) {
            case CROP_TYPE -> "stardewcraft.type.crop".equals(typeKey);
            case MINERAL_TYPE -> "stardewcraft.type.mineral".equals(typeKey);
            case FISH_TYPE -> "stardewcraft.type.fish".equals(typeKey)
                    || "stardewcraft.type.crabpot".equals(typeKey)
                    || "stardewcraft.type.legendary_fish".equals(typeKey);
            default -> false;
        };
    }

    @SuppressWarnings("null")
    public static Optional<Recipe> getRecipe(String machineKey, ItemStack stack) {
        if (machineKey == null || machineKey.isBlank() || stack == null || stack.isEmpty()) {
            return Optional.empty();
        }
        List<Recipe> recipes = RECIPES_BY_MACHINE.get(machineKey);
        if (recipes == null || recipes.isEmpty()) {
            return Optional.empty();
        }
        for (Recipe recipe : recipes) {
            if (recipe.matches(stack)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    public static Optional<Recipe> getRecipeByOutput(String machineKey, ResourceLocation outputId) {
        if (machineKey == null || machineKey.isBlank() || outputId == null) {
            return Optional.empty();
        }
        List<Recipe> recipes = RECIPES_BY_MACHINE.get(machineKey);
        if (recipes == null || recipes.isEmpty()) {
            return Optional.empty();
        }
        for (Recipe recipe : recipes) {
            if (outputId.equals(recipe.outputId())) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    public static List<Recipe> getRecipes(String machineKey) {
        if (machineKey == null || machineKey.isBlank()) {
            return List.of();
        }
        List<Recipe> recipes = RECIPES_BY_MACHINE.get(machineKey);
        return recipes == null ? List.of() : recipes;
    }

    public static java.util.Set<String> getAllMachineKeys() {
        return RECIPES_BY_MACHINE.keySet();
    }

    public static final class ReloadListener extends SimpleJsonResourceReloadListener {
        public ReloadListener() {
            super(GSON, "artisan");
        }

        @Override
        protected void apply(@SuppressWarnings("null") Map<ResourceLocation, JsonElement> objects,
                             @SuppressWarnings("null") ResourceManager resourceManager,
                             @SuppressWarnings("null") ProfilerFiller profiler) {
            Map<String, List<Recipe>> loaded = new HashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
                ResourceLocation resourceId = entry.getKey();
                JsonElement element = entry.getValue();
                if (element == null || !element.isJsonObject()) {
                    continue;
                }
                JsonObject root = element.getAsJsonObject();
                String machineKey = readString(root, "machine");
                if (machineKey == null || machineKey.isBlank()) {
                    StardewCraft.LOGGER.warn("Artisan recipe {} missing machine key", resourceId);
                    continue;
                }
                JsonArray recipes = root.has("recipes") && root.get("recipes").isJsonArray()
                        ? root.getAsJsonArray("recipes")
                        : new JsonArray();
                List<Recipe> list = loaded.computeIfAbsent(machineKey, key -> new ArrayList<>());
                for (JsonElement recipeEl : recipes) {
                    if (!recipeEl.isJsonObject()) {
                        continue;
                    }
                    JsonObject recipeObj = recipeEl.getAsJsonObject();
                    if (recipeObj.has("comment")) {
                        continue;
                    }
                    ResourceLocation inputId = readId(recipeObj, "input");
                    TagKey<Item> inputTag = readTag(recipeObj, "tag");
                    InputMode inputMode = readInputMode(recipeObj, "inputMode");
                    if (inputMode == InputMode.DEFAULT && inputId == null && inputTag == null) {
                        continue;
                    }
                    OutputMode outputMode = readOutputMode(recipeObj, "outputMode");
                    ResourceLocation outputId = readId(recipeObj, "output");
                    if (outputMode == OutputMode.FIXED && outputId == null) {
                        StardewCraft.LOGGER.warn("Artisan recipe {} has invalid output", resourceId);
                        continue;
                    }
                    int outputCount = readInt(recipeObj, "outputCount", 1);
                    int minutes = readInt(recipeObj, "minutes", 0);
                    int consumeCount = readInt(recipeObj, "consume", 1);
                    QualityRule qualityRule = readQualityRule(recipeObj);
                    PreserveType preserveType = readPreserveType(recipeObj, "preserveType");
                    SeedMakerRule seedMakerRule = outputMode == OutputMode.SEEDMAKER
                            ? readSeedMakerRule(recipeObj)
                            : null;
                    if (minutes <= 0) {
                        StardewCraft.LOGGER.warn("Artisan recipe {} has invalid minutes", resourceId);
                        continue;
                    }
                    outputCount = Math.max(1, outputCount);
                    consumeCount = Math.max(1, consumeCount);
                    list.add(new Recipe(inputId, inputTag, inputMode, outputId, outputCount, minutes, consumeCount,
                            qualityRule.keepInputQuality(), qualityRule.outputQuality(), preserveType, seedMakerRule, outputMode));
                }
            }

            Map<String, List<Recipe>> frozen = new HashMap<>();
            for (Map.Entry<String, List<Recipe>> entry : loaded.entrySet()) {
                frozen.put(entry.getKey(), Collections.unmodifiableList(entry.getValue()));
            }
            RECIPES_BY_MACHINE = Collections.unmodifiableMap(frozen);
            StardewCraft.LOGGER.info("Loaded artisan recipes: {} machines", RECIPES_BY_MACHINE.size());
        }

        private static int readInt(JsonObject obj, String key, int fallback) {
            if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
                try {
                    return obj.get(key).getAsInt();
                } catch (Exception ignored) {
                    return fallback;
                }
            }
            return fallback;
        }

        private static double readDouble(JsonObject obj, String key, double fallback) {
            if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
                try {
                    return obj.get(key).getAsDouble();
                } catch (Exception ignored) {
                    return fallback;
                }
            }
            return fallback;
        }

        private static String readString(JsonObject obj, String key) {
            if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
                try {
                    return obj.get(key).getAsString();
                } catch (Exception ignored) {
                    return null;
                }
            }
            return null;
        }

        @Nullable
        @SuppressWarnings("null")
        private static ResourceLocation readId(JsonObject obj, String key) {
            String raw = readString(obj, key);
            if (raw == null || raw.isBlank()) {
                return null;
            }
            String trimmed = raw.trim();
            return ResourceLocation.tryParse(trimmed);
        }

        @Nullable
        @SuppressWarnings("null")
        private static TagKey<Item> readTag(JsonObject obj, String key) {
            ResourceLocation id = readId(obj, key);
            if (id == null) {
                return null;
            }
            return TagKey.create(Registries.ITEM, id);
        }

        private static QualityRule readQualityRule(JsonObject obj) {
            if (!obj.has("quality")) {
                return new QualityRule(false, -1);
            }
            JsonElement el = obj.get("quality");
            if (el.isJsonPrimitive()) {
                if (el.getAsJsonPrimitive().isNumber()) {
                    return new QualityRule(false, el.getAsInt());
                }
                if (el.getAsJsonPrimitive().isString()) {
                    String raw = el.getAsString();
                    if (raw != null) {
                        String key = raw.trim().toLowerCase();
                        if ("keep".equals(key)) {
                            return new QualityRule(true, -1);
                        }
                        Integer quality = parseQualityKey(key);
                        if (quality != null) {
                            return new QualityRule(false, quality);
                        }
                    }
                }
            }
            return new QualityRule(false, -1);
        }

        private static Integer parseQualityKey(String key) {
            return switch (key) {
                case "normal" -> 0;
                case "silver" -> 1;
                case "gold" -> 2;
                case "iridium" -> 3;
                default -> null;
            };
        }

        private static InputMode readInputMode(JsonObject obj, String key) {
            String raw = readString(obj, key);
            if (raw == null || raw.isBlank()) {
                return InputMode.DEFAULT;
            }
            return switch (raw.trim().toLowerCase()) {
                case "crop_type" -> InputMode.CROP_TYPE;
                case "mineral_type" -> InputMode.MINERAL_TYPE;
                case "fish_type" -> InputMode.FISH_TYPE;
                default -> InputMode.DEFAULT;
            };
        }

        private static OutputMode readOutputMode(JsonObject obj, String key) {
            String raw = readString(obj, key);
            if (raw == null || raw.isBlank()) {
                return OutputMode.FIXED;
            }
            return switch (raw.trim().toLowerCase()) {
                case "copy_input" -> OutputMode.COPY_INPUT;
                case "seedmaker" -> OutputMode.SEEDMAKER;
                case "smoked" -> OutputMode.SMOKED;
                default -> OutputMode.FIXED;
            };
        }

        private static SeedMakerRule readSeedMakerRule(JsonObject obj) {
            if (!obj.has("seedmaker") || !obj.get("seedmaker").isJsonObject()) {
                return DEFAULT_SEEDMAKER_RULE;
            }
            JsonObject seedObj = obj.getAsJsonObject("seedmaker");
            double ancientChance = readDouble(seedObj, "ancientChance", DEFAULT_SEEDMAKER_RULE.ancientChance());
            double mixedChance = readDouble(seedObj, "mixedChance", DEFAULT_SEEDMAKER_RULE.mixedChance());
            int mixedMin = readInt(seedObj, "mixedMin", DEFAULT_SEEDMAKER_RULE.mixedMin());
            int mixedMax = readInt(seedObj, "mixedMax", DEFAULT_SEEDMAKER_RULE.mixedMax());
            int seedMin = readInt(seedObj, "seedMin", DEFAULT_SEEDMAKER_RULE.seedMin());
            int seedMax = readInt(seedObj, "seedMax", DEFAULT_SEEDMAKER_RULE.seedMax());
            mixedMin = Math.max(0, mixedMin);
            mixedMax = Math.max(mixedMin, mixedMax);
            seedMin = Math.max(0, seedMin);
            seedMax = Math.max(seedMin, seedMax);
            ancientChance = Math.max(0.0, Math.min(1.0, ancientChance));
            mixedChance = Math.max(0.0, Math.min(1.0, mixedChance));
            return new SeedMakerRule(ancientChance, mixedChance, mixedMin, mixedMax, seedMin, seedMax);
        }

        @Nullable
        private static PreserveType readPreserveType(JsonObject obj, String key) {
            String raw = readString(obj, key);
            if (raw == null || raw.isBlank()) {
                return null;
            }
            try {
                return PreserveType.valueOf(raw.trim().toUpperCase());
            } catch (Exception ignored) {
                return null;
            }
        }
    }

    private record QualityRule(boolean keepInputQuality, int outputQuality) {
    }
}
