package com.stardew.craft.communitycenter.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.stardew.craft.StardewCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Loads bundle definitions from data/stardewcraft/communitycenter/bundles.json.
 * Singleton access via {@link #get()}.
 */
public final class BundleDataManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // ── Singleton state ──
    private static Map<Integer, BundleDefinition> bundlesById = Collections.emptyMap();
    private static Map<Integer, List<BundleDefinition>> bundlesByArea = Collections.emptyMap();
    private static Map<Integer, String> areaNames = Collections.emptyMap();
    private static Map<Integer, String> areaDisplayNameKeys = Collections.emptyMap();

    private BundleDataManager() {}

    // ── Public API ──

    /** Get a bundle definition by its ID. */
    @Nullable
    public static BundleDefinition getBundle(int bundleId) {
        return bundlesById.get(bundleId);
    }

    /** Get all bundles for a given area. */
    public static List<BundleDefinition> getBundlesForArea(int areaId) {
        return bundlesByArea.getOrDefault(areaId, Collections.emptyList());
    }

    /** Get all loaded bundle definitions. */
    public static Collection<BundleDefinition> getAllBundles() {
        return bundlesById.values();
    }

    /** Get the internal name for an area (e.g. "Pantry"). */
    @Nullable
    public static String getAreaName(int areaId) {
        return areaNames.get(areaId);
    }

    /** Get the i18n key for an area's display name. */
    @Nullable
    public static String getAreaDisplayNameKey(int areaId) {
        return areaDisplayNameKeys.get(areaId);
    }

    /** Total number of bundles loaded. */
    public static int bundleCount() {
        return bundlesById.size();
    }

    /**
     * 从网络同步的数据填充客户端 BundleDataManager。
     * 专用服务器场景下，客户端无法通过 datapack ReloadListener 加载 bundle 定义，
     * 需要服务端通过 BundleSyncPayload 将定义同步给客户端。
     */
    public static void applyFromNetwork(Collection<BundleDefinition> defs,
                                         Map<Integer, String> names,
                                         Map<Integer, String> displayKeys) {
        Map<Integer, BundleDefinition> newById = new LinkedHashMap<>();
        Map<Integer, List<BundleDefinition>> newByArea = new HashMap<>();
        for (BundleDefinition def : defs) {
            newById.put(def.bundleId(), def);
            newByArea.computeIfAbsent(def.areaId(), k -> new ArrayList<>()).add(def);
        }
        for (Map.Entry<Integer, List<BundleDefinition>> e : newByArea.entrySet()) {
            e.setValue(Collections.unmodifiableList(e.getValue()));
        }
        bundlesById = Collections.unmodifiableMap(newById);
        bundlesByArea = Collections.unmodifiableMap(newByArea);
        areaNames = Collections.unmodifiableMap(new HashMap<>(names));
        areaDisplayNameKeys = Collections.unmodifiableMap(new HashMap<>(displayKeys));
    }

    // ── Reload Listener ──

    @SuppressWarnings("null")
    public static final class ReloadListener extends SimpleJsonResourceReloadListener {
        public ReloadListener() {
            super(GSON, "communitycenter");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> objects,
                             ResourceManager resourceManager,
                             ProfilerFiller profiler) {

            Map<Integer, BundleDefinition> newBundlesById = new LinkedHashMap<>();
            Map<Integer, List<BundleDefinition>> newBundlesByArea = new HashMap<>();
            Map<Integer, String> newAreaNames = new HashMap<>();
            Map<Integer, String> newAreaDisplayKeys = new HashMap<>();

            for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
                ResourceLocation id = entry.getKey();
                if (!"bundles".equals(id.getPath())) continue;

                JsonElement element = entry.getValue();
                if (element == null || !element.isJsonObject()) continue;

                JsonObject root = element.getAsJsonObject();

                // Parse areas
                if (root.has("areas")) {
                    JsonObject areasObj = root.getAsJsonObject("areas");
                    for (String key : areasObj.keySet()) {
                        int areaId = Integer.parseInt(key);
                        JsonObject areaObj = areasObj.getAsJsonObject(key);
                        newAreaNames.put(areaId, areaObj.get("name").getAsString());
                        newAreaDisplayKeys.put(areaId, areaObj.get("displayNameKey").getAsString());
                    }
                }

                // Parse bundles
                if (root.has("bundles")) {
                    JsonObject bundlesObj = root.getAsJsonObject("bundles");
                    for (String key : bundlesObj.keySet()) {
                        int bundleId = Integer.parseInt(key);
                        JsonObject bObj = bundlesObj.getAsJsonObject(key);

                        int areaId = bObj.get("areaId").getAsInt();
                        String internalName = bObj.get("internalName").getAsString();
                        String displayNameKey = bObj.get("displayNameKey").getAsString();
                        String rewardString = bObj.has("reward") ? bObj.get("reward").getAsString() : "";
                        int color = bObj.get("color").getAsInt();
                        int requiredCount = bObj.get("requiredCount").getAsInt();

                        String ingredientsRaw = bObj.get("ingredients").getAsString();
                        List<BundleIngredient> ingredients = parseIngredients(ingredientsRaw);

                        BundleDefinition def = new BundleDefinition(
                                bundleId, areaId, internalName, displayNameKey,
                                rewardString, Collections.unmodifiableList(ingredients),
                                color, requiredCount
                        );

                        newBundlesById.put(bundleId, def);
                        newBundlesByArea.computeIfAbsent(areaId, k -> new ArrayList<>()).add(def);
                    }
                }
            }

            // Freeze all collections
            for (Map.Entry<Integer, List<BundleDefinition>> e : newBundlesByArea.entrySet()) {
                e.setValue(Collections.unmodifiableList(e.getValue()));
            }

            bundlesById = Collections.unmodifiableMap(newBundlesById);
            bundlesByArea = Collections.unmodifiableMap(newBundlesByArea);
            areaNames = Collections.unmodifiableMap(newAreaNames);
            areaDisplayNameKeys = Collections.unmodifiableMap(newAreaDisplayKeys);

            StardewCraft.LOGGER.info("[COMMUNITY CENTER] Loaded {} bundles across {} areas",
                    newBundlesById.size(), newBundlesByArea.size());
        }

        private static List<BundleIngredient> parseIngredients(String raw) {
            if (raw == null || raw.isBlank()) return Collections.emptyList();

            String[] tokens = raw.trim().split("\\s+");
            List<BundleIngredient> list = new ArrayList<>();

            for (int i = 0; i + 2 < tokens.length; i += 3) {
                String idToken = tokens[i];
                int stack = Integer.parseInt(tokens[i + 1]);
                int quality = Integer.parseInt(tokens[i + 2]);

                int numericId;
                try {
                    numericId = Integer.parseInt(idToken);
                } catch (NumberFormatException e) {
                    // Non-numeric ID (e.g. "DeluxeBait") — treat as named item
                    String resolved = BundleItemResolver.resolve(idToken);
                    list.add(new BundleIngredient(resolved, idToken, 0, stack, quality));
                    continue;
                }

                if (numericId < 0) {
                    // Negative = category (-1 = gold, -4 = fish, etc.)
                    list.add(new BundleIngredient(null, idToken, numericId, stack, quality));
                } else {
                    // Positive = specific item by SDV numeric ID
                    String resolved = BundleItemResolver.resolve(idToken);
                    list.add(new BundleIngredient(resolved, idToken, 0, stack, quality));
                }
            }
            return list;
        }
    }
}
