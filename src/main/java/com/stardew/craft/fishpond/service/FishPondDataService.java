package com.stardew.craft.fishpond.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stardew.craft.StardewCraft;
import com.stardew.craft.fishpond.model.FishPondRecord;
import com.stardew.craft.item.IStardewItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class FishPondDataService {
    public static final int NO_OVERRIDE_WATER_COLOR = -1;
    private static final String RESOURCE_PATH = "/data/stardewcraft/fishpond/fish_pond_data.json";
    private static final int DEFAULT_MAX_POPULATION = 10;
    private static final FishPondDataService INSTANCE = new FishPondDataService(loadEntries());
    private static final Map<String, String> LEGENDARY_ITEM_QIDS = createLegendaryItemQids();

    private final List<PondData> entries;

    private FishPondDataService(List<PondData> entries) {
        this.entries = entries;
    }

    public static FishPondDataService get() {
        return INSTANCE;
    }

    public Optional<PondData> resolve(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        ResourceLocation itemId = ResourceLocation.tryParse(String.valueOf(BuiltInRegistries.ITEM.getKey(stack.getItem())));
        if (itemId == null) {
            return Optional.empty();
        }
        return resolveByItemPath(itemId.getPath());
    }

    public Optional<PondData> resolveFishTypeId(String fishTypeId) {
        if (fishTypeId == null || fishTypeId.isBlank()) {
            return Optional.empty();
        }
        ResourceLocation itemId = ResourceLocation.tryParse(fishTypeId);
        if (itemId == null || !BuiltInRegistries.ITEM.containsKey(itemId)) {
            return Optional.empty();
        }
        return resolveByItemPath(itemId.getPath());
    }

    public int resolveWaterColor(FishPondRecord pond, ItemStack inputStack) {
        Optional<PondData> pondData = resolve(inputStack);
        if (pondData.isEmpty()) {
            return NO_OVERRIDE_WATER_COLOR;
        }

        for (WaterColorRule rule : pondData.get().waterColors()) {
            if (pond.currentPopulation() < rule.minPopulation()) {
                continue;
            }
            if (pond.lastUnlockedPopulationGate() < rule.minUnlockedPopulationGate()) {
                continue;
            }
            if (!matchesCondition(rule.condition(), inputStack)) {
                continue;
            }
            if (rule.copyFromInput()) {
                DyedItemColor dyed = inputStack.get(DataComponents.DYED_COLOR);
                return dyed != null ? (dyed.rgb() & 0xFFFFFF) : NO_OVERRIDE_WATER_COLOR;
            }
            return rule.rgb();
        }

        return NO_OVERRIDE_WATER_COLOR;
    }

    public int resolveWaterColor(FishPondRecord pond) {
        ItemStack fishStack = createFishStack(pond.fishTypeId());
        if (fishStack.isEmpty()) {
            return NO_OVERRIDE_WATER_COLOR;
        }
        return resolveWaterColor(pond, fishStack);
    }

    public int resolveMaxPopulation(ItemStack stack) {
        return resolve(stack)
            .map(data -> resolveCurrentMaxPopulation(data, 0))
            .orElse(DEFAULT_MAX_POPULATION);
    }

    public int resolveCurrentMaxPopulation(FishPondRecord pond) {
        return resolveFishTypeId(pond.fishTypeId())
            .map(data -> resolveCurrentMaxPopulation(data, pond.lastUnlockedPopulationGate()))
            .orElse(pond.maxPopulation() > 0 ? pond.maxPopulation() : DEFAULT_MAX_POPULATION);
    }

    public int resolveSpawnTime(FishPondRecord pond) {
        Optional<PondData> pondData = resolveFishTypeId(pond.fishTypeId());
        if (pondData.isEmpty()) {
            return -1;
        }
        int configured = pondData.get().spawnTime();
        if (configured >= 0) {
            return configured;
        }

        ItemStack fishStack = createFishStack(pond.fishTypeId());
        if (!fishStack.isEmpty() && fishStack.getItem() instanceof IStardewItem stardewItem) {
            int value = stardewItem.getSellPrice(fishStack);
            if (value <= 30) {
                return 1;
            }
            if (value <= 80) {
                return 2;
            }
            if (value <= 120) {
                return 3;
            }
            if (value <= 250) {
                return 4;
            }
            return 5;
        }
        return 3;
    }

    public Optional<ProducedItem> rollProducedItem(FishPondRecord pond, RandomSource random) {
        Optional<PondData> pondData = resolveFishTypeId(pond.fishTypeId());
        if (pondData.isEmpty()) {
            return Optional.empty();
        }

        ItemStack fishStack = createFishStack(pond.fishTypeId());
        ProducedItem selected = null;
        for (ProducedItem producedItem : pondData.get().producedItems()) {
            if (pond.currentPopulation() < producedItem.requiredPopulation()) {
                continue;
            }
            if (selected != null && selected.precedence() <= producedItem.precedence()) {
                continue;
            }
            if (random.nextDouble() >= producedItem.chance()) {
                continue;
            }
            if (!matchesCondition(producedItem.condition(), fishStack)) {
                continue;
            }
            selected = producedItem;
        }
        return Optional.ofNullable(selected);
    }

    public Optional<NeededItemData> resolveNeededItem(FishPondRecord pond) {
        if (pond.currentPopulation() < pond.maxPopulation()) {
            return Optional.empty();
        }

        Optional<PondData> pondData = resolveFishTypeId(pond.fishTypeId());
        if (pondData.isEmpty()) {
            return Optional.empty();
        }
        if (pond.maxPopulation() + 1 <= pond.lastUnlockedPopulationGate()) {
            return Optional.empty();
        }

        List<String> gates = pondData.get().populationGates().get(pond.maxPopulation() + 1);
        if (gates == null || gates.isEmpty()) {
            return Optional.empty();
        }

        RandomSource random = RandomSource.create(mixNeedSeed(pond));
        String[] split = gates.get(random.nextInt(gates.size())).trim().split("\\s+");
        if (split.length == 0 || split[0].isBlank()) {
            return Optional.empty();
        }

        int count = 1;
        if (split.length >= 3) {
            int min = Integer.parseInt(split[1]);
            int max = Integer.parseInt(split[2]);
            count = random.nextInt(min, max + 1);
        } else if (split.length >= 2) {
            count = Integer.parseInt(split[1]);
        }
        return Optional.of(new NeededItemData(split[0], count));
    }

    public boolean hasPopulationGateForNextLevel(FishPondRecord pond) {
        return resolveFishTypeId(pond.fishTypeId())
            .map(data -> data.populationGates().containsKey(pond.maxPopulation() + 1))
            .orElse(false);
    }

    private Optional<PondData> resolveByItemPath(String path) {
        String requiredTag = "item_" + path;
        for (PondData entry : entries) {
            if (entry.requiredTags().contains(requiredTag)) {
                return Optional.of(entry);
            }
        }
        if (LEGENDARY_ITEM_QIDS.containsKey(path)) {
            for (PondData entry : entries) {
                if (entry.requiredTags().contains("fish_legendary")) {
                    return Optional.of(entry);
                }
            }
        }
        return Optional.empty();
    }

    private int resolveCurrentMaxPopulation(PondData data, int lastUnlockedPopulationGate) {
        if (data.maxPopulation() > 0) {
            return data.maxPopulation();
        }
        int resolved = 0;
        for (int i = 1; i <= DEFAULT_MAX_POPULATION; i++) {
            if (i <= lastUnlockedPopulationGate) {
                resolved = i;
                continue;
            }
            if (!data.populationGates().containsKey(i)) {
                resolved = i;
                continue;
            }
            break;
        }
        return resolved > 0 ? resolved : DEFAULT_MAX_POPULATION;
    }

    private ItemStack createFishStack(String fishTypeId) {
        ResourceLocation itemId = ResourceLocation.tryParse(fishTypeId);
        if (itemId == null || !BuiltInRegistries.ITEM.containsKey(itemId)) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.get(itemId);
        return new ItemStack(item);
    }

    private boolean matchesCondition(String condition, ItemStack inputStack) {
        if (condition == null || condition.isBlank()) {
            return true;
        }
        String trimmed = condition.trim();
        if (!trimmed.startsWith("ITEM_ID Input ")) {
            return false;
        }

        ResourceLocation itemId = ResourceLocation.tryParse(String.valueOf(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(inputStack.getItem())));
        if (itemId == null) {
            return false;
        }
        String qualifiedItemId = LEGENDARY_ITEM_QIDS.get(itemId.getPath());
        if (qualifiedItemId == null) {
            return false;
        }

        String[] tokens = trimmed.substring("ITEM_ID Input ".length()).split("\\s+");
        for (String token : tokens) {
            if (qualifiedItemId.equals(token)) {
                return true;
            }
        }
        return false;
    }

    private static List<PondData> loadEntries() {
        List<PondData> loaded = new ArrayList<>();
        try (InputStream stream = FishPondDataService.class.getResourceAsStream(RESOURCE_PATH)) {
            if (stream == null) {
                StardewCraft.LOGGER.warn("Fish pond data resource missing: {}", RESOURCE_PATH);
                return loaded;
            }
            try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                JsonArray root = JsonParser.parseReader(reader).getAsJsonArray();
                for (JsonElement element : root) {
                    JsonObject object = element.getAsJsonObject();
                    Set<String> requiredTags = new LinkedHashSet<>();
                    JsonArray tags = getArray(object, "RequiredTags");
                    if (tags != null) {
                        for (JsonElement tag : tags) {
                            requiredTags.add(tag.getAsString().toLowerCase(Locale.ROOT));
                        }
                    }

                    List<WaterColorRule> waterColors = new ArrayList<>();
                    JsonArray waterColorArray = getArray(object, "WaterColor");
                    if (waterColorArray != null) {
                        for (JsonElement waterColorElement : waterColorArray) {
                            JsonObject waterColorObject = waterColorElement.getAsJsonObject();
                            String colorText = getString(waterColorObject, "Color");
                            waterColors.add(new WaterColorRule(
                                getString(waterColorObject, "Id"),
                                colorText,
                                parseColor(colorText),
                                getInt(waterColorObject, "MinPopulation", 0),
                                getInt(waterColorObject, "MinUnlockedPopulationGate", 0),
                                getString(waterColorObject, "Condition")
                            ));
                        }
                    }

                    List<ProducedItem> producedItems = new ArrayList<>();
                    JsonArray producedItemsArray = getArray(object, "ProducedItems");
                    if (producedItemsArray != null) {
                        for (JsonElement producedItemElement : producedItemsArray) {
                            JsonObject producedItemObject = producedItemElement.getAsJsonObject();
                            producedItems.add(new ProducedItem(
                                getString(producedItemObject, "Id"),
                                getString(producedItemObject, "ItemId"),
                                getInt(producedItemObject, "RequiredPopulation", 0),
                                getDouble(producedItemObject, "Chance", 0.0D),
                                getInt(producedItemObject, "Precedence", 0),
                                getString(producedItemObject, "Condition"),
                                getInt(producedItemObject, "MinStack", -1),
                                getInt(producedItemObject, "MaxStack", -1)
                            ));
                        }
                    }

                    Map<Integer, List<String>> populationGates = new HashMap<>();
                    JsonObject populationGatesObject = getObject(object, "PopulationGates");
                    if (populationGatesObject != null) {
                        for (Map.Entry<String, JsonElement> gateEntry : populationGatesObject.entrySet()) {
                            JsonArray gateChoicesArray = gateEntry.getValue().getAsJsonArray();
                            List<String> gateChoices = new ArrayList<>();
                            for (JsonElement gateChoiceElement : gateChoicesArray) {
                                gateChoices.add(gateChoiceElement.getAsString());
                            }
                            populationGates.put(Integer.parseInt(gateEntry.getKey()), gateChoices);
                        }
                    }

                    loaded.add(new PondData(
                        getString(object, "Id"),
                        requiredTags,
                        getInt(object, "MaxPopulation", -1),
                        getInt(object, "SpawnTime", -1),
                        getDouble(object, "BaseMinProduceChance", 0.0D),
                        getDouble(object, "BaseMaxProduceChance", 0.0D),
                        waterColors,
                        producedItems,
                        populationGates
                    ));
                }
            }
        } catch (Exception ex) {
            StardewCraft.LOGGER.error("Failed to load fish pond data from {}", RESOURCE_PATH, ex);
        }
        return loaded;
    }

    private static String getString(JsonObject object, String member) {
        JsonElement value = object.get(member);
        return value == null || value.isJsonNull() ? "" : value.getAsString();
    }

    private static JsonArray getArray(JsonObject object, String member) {
        JsonElement value = object.get(member);
        return value == null || value.isJsonNull() || !value.isJsonArray() ? null : value.getAsJsonArray();
    }

    private static JsonObject getObject(JsonObject object, String member) {
        JsonElement value = object.get(member);
        return value == null || value.isJsonNull() || !value.isJsonObject() ? null : value.getAsJsonObject();
    }

    private static int getInt(JsonObject object, String member, int defaultValue) {
        JsonElement value = object.get(member);
        return value == null || value.isJsonNull() ? defaultValue : value.getAsInt();
    }

    private static double getDouble(JsonObject object, String member, double defaultValue) {
        JsonElement value = object.get(member);
        return value == null || value.isJsonNull() ? defaultValue : value.getAsDouble();
    }

    private static int parseColor(String colorText) {
        if (colorText == null || colorText.isBlank() || "CopyFromInput".equalsIgnoreCase(colorText)) {
            return NO_OVERRIDE_WATER_COLOR;
        }
        String[] components = colorText.trim().split("\\s+");
        if (components.length != 3) {
            return NO_OVERRIDE_WATER_COLOR;
        }
        int red = Integer.parseInt(components[0]);
        int green = Integer.parseInt(components[1]);
        int blue = Integer.parseInt(components[2]);
        return ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | (blue & 0xFF);
    }

    private static Map<String, String> createLegendaryItemQids() {
        Map<String, String> map = new HashMap<>();
        map.put("angler", "(O)160");
        map.put("crimsonfish", "(O)159");
        map.put("glacierfish", "(O)775");
        map.put("legend", "(O)163");
        map.put("mutant_carp", "(O)682");
        map.put("legend_ii", "(O)900");
        map.put("glacierfish_jr", "(O)902");
        map.put("son_of_crimsonfish", "(O)898");
        map.put("ms_angler", "(O)899");
        map.put("radioactive_carp", "(O)901");
        return map;
    }

    private static long mixNeedSeed(FishPondRecord pond) {
        long seed = 1469598103934665603L;
        seed = (seed ^ (long) pond.managerPos().getX() * 1000L) * 1099511628211L;
        seed = (seed ^ (long) pond.managerPos().getZ() * 2000L) * 1099511628211L;
        return seed;
    }

    public record PondData(String id,
                           Set<String> requiredTags,
                           int maxPopulation,
                           int spawnTime,
                           double baseMinProduceChance,
                           double baseMaxProduceChance,
                           List<WaterColorRule> waterColors,
                           List<ProducedItem> producedItems,
                           Map<Integer, List<String>> populationGates) {
    }

    public record ProducedItem(String id,
                               String itemId,
                               int requiredPopulation,
                               double chance,
                               int precedence,
                               String condition,
                               int minStack,
                               int maxStack) {
        public int rollStackCount(RandomSource random) {
            if (minStack > 0 && maxStack >= minStack) {
                return random.nextInt(minStack, maxStack + 1);
            }
            if (minStack > 0) {
                return minStack;
            }
            return 1;
        }
    }

    public record NeededItemData(String itemId, int count) {
    }

    public record WaterColorRule(String id,
                                 String colorText,
                                 int rgb,
                                 int minPopulation,
                                 int minUnlockedPopulationGate,
                                 String condition) {
        public boolean copyFromInput() {
            return "CopyFromInput".equalsIgnoreCase(colorText);
        }
    }
}