package com.stardew.craft.interior;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class InteriorRegionRegistry {
    private static final String RESOURCE_PATH = "data/stardewcraft/interiors/fixed_interior_regions.json";
    private static final List<InteriorRegion> REGIONS = new ArrayList<>();
    private static final Map<String, String> ALIASES = new HashMap<>();
    private static boolean loaded;

    private InteriorRegionRegistry() {
    }

    public static Optional<InteriorRegion> fixedInteriorAt(BlockPos pos) {
        ensureLoaded();
        if (pos == null) {
            return Optional.empty();
        }
        for (InteriorRegion region : REGIONS) {
            if (region.contains(pos)) {
                return Optional.of(region);
            }
        }
        return Optional.empty();
    }

    public static String fixedInteriorIdAt(BlockPos pos) {
        return fixedInteriorAt(pos).map(InteriorRegion::id).orElse("");
    }

    public static boolean isInFixedInterior(BlockPos pos) {
        return fixedInteriorAt(pos).isPresent();
    }

    public static String canonicalInteriorId(String idOrAlias) {
        ensureLoaded();
        if (idOrAlias == null || idOrAlias.isBlank()) {
            return "";
        }
        String normalized = normalize(idOrAlias);
        return ALIASES.getOrDefault(normalized, normalized);
    }

    public static boolean hasFixedInteriorAlias(String idOrAlias) {
        ensureLoaded();
        if (idOrAlias == null || idOrAlias.isBlank()) {
            return false;
        }
        return ALIASES.containsKey(normalize(idOrAlias));
    }

    public static List<InteriorRegion> regions() {
        ensureLoaded();
        return Collections.unmodifiableList(REGIONS);
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        REGIONS.clear();
        ALIASES.clear();

        try (InputStream input = InteriorRegionRegistry.class.getClassLoader().getResourceAsStream(RESOURCE_PATH)) {
            if (input == null) {
                StardewCraft.LOGGER.error("[INTERIOR_REGION] Missing {}", RESOURCE_PATH);
                return;
            }
            JsonObject root = JsonParser.parseReader(new InputStreamReader(input, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonArray regions = root.getAsJsonArray("regions");
            if (regions == null) {
                StardewCraft.LOGGER.error("[INTERIOR_REGION] {} has no regions array", RESOURCE_PATH);
                return;
            }
            for (JsonElement element : regions) {
                if (element == null || !element.isJsonObject()) {
                    continue;
                }
                InteriorRegion region = readRegion(element.getAsJsonObject());
                if (region == null) {
                    continue;
                }
                REGIONS.add(region);
                ALIASES.put(normalize(region.id()), region.id());
                for (String alias : region.aliases()) {
                    ALIASES.put(normalize(alias), region.id());
                }
            }
            StardewCraft.LOGGER.info("[INTERIOR_REGION] Loaded {} fixed interior regions", REGIONS.size());
        } catch (Exception ex) {
            StardewCraft.LOGGER.error("[INTERIOR_REGION] Failed to load {}", RESOURCE_PATH, ex);
        }
    }

    private static InteriorRegion readRegion(JsonObject obj) {
        String id = readString(obj, "id");
        String ledgerId = readString(obj, "ledger_id");
        if (id == null || id.isBlank()) {
            return null;
        }
        int[] min = readVec3i(obj.getAsJsonArray("min"));
        int[] max = readVec3i(obj.getAsJsonArray("max"));
        if (min == null || max == null) {
            StardewCraft.LOGGER.error("[INTERIOR_REGION] Region {} is missing min/max", id);
            return null;
        }
        List<String> aliases = new ArrayList<>();
        JsonArray aliasArray = obj.getAsJsonArray("aliases");
        if (aliasArray != null) {
            for (JsonElement alias : aliasArray) {
                if (alias != null && alias.isJsonPrimitive()) {
                    aliases.add(alias.getAsString());
                }
            }
        }
        return new InteriorRegion(
            normalize(id),
            ledgerId == null ? "" : ledgerId.trim(),
            Math.min(min[0], max[0]),
            Math.min(min[1], max[1]),
            Math.min(min[2], max[2]),
            Math.max(min[0], max[0]),
            Math.max(min[1], max[1]),
            Math.max(min[2], max[2]),
            List.copyOf(aliases)
        );
    }

    private static String readString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || !obj.get(key).isJsonPrimitive()) {
            return null;
        }
        return obj.get(key).getAsString();
    }

    private static int[] readVec3i(JsonArray array) {
        if (array == null || array.size() != 3) {
            return null;
        }
        return new int[] {
            array.get(0).getAsInt(),
            array.get(1).getAsInt(),
            array.get(2).getAsInt()
        };
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    public record InteriorRegion(
        String id,
        String ledgerId,
        int minX,
        int minY,
        int minZ,
        int maxX,
        int maxY,
        int maxZ,
        List<String> aliases
    ) {
        public boolean contains(BlockPos pos) {
            return pos.getX() >= minX && pos.getX() <= maxX
                && pos.getY() >= minY && pos.getY() <= maxY
                && pos.getZ() >= minZ && pos.getZ() <= maxZ;
        }
    }
}