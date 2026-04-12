package com.stardew.craft.manager;

import com.stardew.craft.item.ModItems;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.*;

/**
 * SDV-parity artifact drop service for hoe digging (artifact spots).
 * Replicates GameLocation.digUpArtifactSpot() + ItemQueryResolver RANDOM_ARTIFACT_FOR_DIG_SPOT.
 *
 * <p>SDV evaluation order:
 * <ol>
 *   <li>Merge Default.ArtifactSpots + Location.ArtifactSpots</li>
 *   <li>Sort by Precedence (Default -100 first, Location 0 middle, Default 100 last)</li>
 *   <li>Evaluate in order; stop on first hit unless ContinueOnDrop=true</li>
 * </ol>
 *
 * <p>Precedence -100 (Default, evaluated first):
 *   Mixed Seeds 20% (not Farm) → RANDOM_ARTIFACT 100% → Bone Fragment 20% (ContinueOnDrop)
 * <p>Precedence 0 (Location-specific):
 *   copper ore, fossils, etc. per location
 * <p>Precedence 100 (Default, evaluated last):
 *   Stone 11% → Warp Totems 10% (after day 28) → Clay 1-3 100% (guaranteed)
 */
@SuppressWarnings("null")
public final class ArtifactDropService {

    private ArtifactDropService() {}

    // ======================== Zone Definition ========================

    private record ZoneRect(int minX, int minZ, int maxX, int maxZ) {
        boolean contains(int x, int z) {
            return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
        }
    }

    private static ZoneRect rect(int x1, int z1, int x2, int z2) {
        return new ZoneRect(Math.min(x1, x2), Math.min(z1, z2), Math.max(x1, x2), Math.max(z1, z2));
    }

    private static final Map<String, List<ZoneRect>> ZONE_RECTS = new LinkedHashMap<>();

    static {
        ZONE_RECTS.put("Town", List.of(
                rect(159, 193, 19, 221),
                rect(51, 96, 21, 112),
                rect(-1, 69, -18, 80)
        ));
        ZONE_RECTS.put("Forest", List.of(
                rect(134, -194, 197, -110),
                rect(231, -138, 252, -114),
                rect(221, -11, 302, 35)
        ));
        ZONE_RECTS.put("Mountain", List.of(
                rect(-239, 161, -196, 188),
                rect(-245, 233, -207, 263),
                rect(-324, 289, -292, 309),
                rect(-105, 294, -72, 312)
        ));
        ZONE_RECTS.put("Beach", List.of(
                rect(-293, -182, -192, -139),
                rect(-376, -173, -326, -148)
        ));
        ZONE_RECTS.put("Farm", List.of(
                rect(-80, -100, 80, 80)
        ));
        ZONE_RECTS.put("BusStop", List.of(
                rect(-20, 80, 20, 130)
        ));
        ZONE_RECTS.put("Backwoods", List.of(
                rect(-100, 130, 20, 190)
        ));
        ZONE_RECTS.put("Railroad", List.of(
                rect(-300, 300, -250, 350)
        ));
        ZONE_RECTS.put("Desert", List.of(
                rect(-500, -400, -350, -250)
        ));
    }

    // ======================== Unified Drop Entry ========================

    private record DropEntry(
            String id,
            double chance,
            int precedence,
            boolean continueOnDrop,
            DeferredItem<? extends Item> item,
            List<DeferredItem<? extends Item>> randomItems,
            int minStack,
            int maxStack,
            DropCondition condition
    ) {}

    @FunctionalInterface
    private interface DropCondition {
        boolean test(String location, int season, int totalDaysPlayed, RandomSource random);
    }

    private static final DropCondition ALWAYS = (loc, season, days, rng) -> true;

    // ======================== Default ArtifactSpots (Precedence -100 and 100) ========================

    private static final List<DropEntry> DEFAULT_DROPS = new ArrayList<>();

    static {
        // --- Precedence -100 ---
        // LOST_BOOK_OR_ITEM (O)770 — Mixed Seeds 20%, not on Farm
        DEFAULT_DROPS.add(new DropEntry("LOST_BOOK_OR_ITEM (O)770", 0.2, -100, false,
                ModItems.MIXED_SEEDS, null, 1, 1,
                (loc, season, days, rng) -> !"Farm".equals(loc)));

        // RANDOM_ARTIFACT_FOR_DIG_SPOT — 100% chance, actual artifact roll done in code
        DEFAULT_DROPS.add(new DropEntry("RANDOM_ARTIFACT_FOR_DIG_SPOT", 1.0, -100, false,
                null, null, 1, 1, ALWAYS));

        // (O)416 Snow Yam — 50% chance, Winter AND RANDOM 0.4 (effective ~20%)
        DEFAULT_DROPS.add(new DropEntry("(O)416", 0.5, -100, false,
                ModItems.VANILLA_CATEGORY_ITEMS.get("snow_yam"), null, 1, 1,
                (loc, season, days, rng) -> season == 3 && rng.nextDouble() < 0.4));

        // (O)412 Winter Root — 50% chance, Winter only
        DEFAULT_DROPS.add(new DropEntry("(O)412", 0.5, -100, false,
                ModItems.VANILLA_CATEGORY_ITEMS.get("winter_root"), null, 1, 1,
                (loc, season, days, rng) -> season == 3));

        // (O)890 Qi Beans — special order, not applicable → skip

        // (O)273 Rice Shoot — 6.25%, Spring, not Beach, stack 2-5
        DEFAULT_DROPS.add(new DropEntry("(O)273", 0.0625, -100, false,
                ModItems.VANILLA_CATEGORY_ITEMS.get("rice_shoot"), null, 2, 5,
                (loc, season, days, rng) -> season == 0 && !"Beach".equals(loc)));

        // (O)881 Bone Fragment — 20%, ContinueOnDrop=true
        // SDV requires guntherBones mail or active Gunther quest; we simplify to always available
        DEFAULT_DROPS.add(new DropEntry("(O)881", 0.2, -100, true,
                ModItems.BONE_FRAGMENT, null, 2, 5, ALWAYS));

        // --- Precedence 100 ---
        // SECRET_NOTE_OR_ITEM (O)390 — Stone 11%
        DEFAULT_DROPS.add(new DropEntry("SECRET_NOTE_OR_ITEM (O)390", 0.11, 100, false,
                ModItems.STONE, null, 1, 1, ALWAYS));

        // (O)688|(O)689|(O)690 — Warp Totems, 10%, after day 28
        DEFAULT_DROPS.add(new DropEntry("(O)688|(O)689|(O)690", 0.1, 100, false,
                null,
                List.of(ModItems.WARP_TOTEM_FARM, ModItems.WARP_TOTEM_MOUNTAIN, ModItems.WARP_TOTEM_BEACH),
                1, 1,
                (loc, season, days, rng) -> days >= 28));

        // (O)330 Clay — 100% guaranteed fallback, stack 1-3
        DEFAULT_DROPS.add(new DropEntry("(O)330", 1.0, 100, false,
                ModItems.CLAY, null, 1, 3, ALWAYS));
    }

    // ======================== Location-specific ArtifactSpots (Precedence 0) ========================

    private static final Map<String, List<DropEntry>> LOCATION_DROPS = new LinkedHashMap<>();

    static {
        // Town: copper_ore 20%, rusty_spoon 20%, prehistoric_rib 10%, mixed_seeds 20%, stone 25%
        LOCATION_DROPS.put("Town", List.of(
                locDrop("(O)378", ModItems.COPPER_ORE, 0.2),
                locDrop("(O)110", ModItems.RUSTY_SPOON, 0.2),
                locDrop("(O)583", ModItems.PREHISTORIC_RIB, 0.1),
                locDrop("LOST_BOOK_OR_ITEM (O)770", ModItems.MIXED_SEEDS, 0.2),
                locDrop("(O)390", ModItems.STONE, 0.25)
        ));
        // Mountain: coal 60%, prehistoric_skull 10%, copper_ore 10%, mixed_seeds 15%, stone 25%
        LOCATION_DROPS.put("Mountain", List.of(
                locDrop("(O)382", ModItems.COAL, 0.6),
                locDrop("(O)581", ModItems.PREHISTORIC_SKULL, 0.1),
                locDrop("(O)378", ModItems.COPPER_ORE, 0.1),
                locDrop("LOST_BOOK_OR_ITEM (O)770", ModItems.MIXED_SEEDS, 0.15),
                locDrop("(O)390", ModItems.STONE, 0.25)
        ));
        // Forest: copper_ore 8%, prehistoric_scapula 10%, palm_fossil 1%, mixed_seeds 15%, stone 25%
        LOCATION_DROPS.put("Forest", List.of(
                locDrop("(O)378", ModItems.COPPER_ORE, 0.08),
                locDrop("(O)579", ModItems.PREHISTORIC_SCAPULA, 0.1),
                locDrop("(O)588", ModItems.PALM_FOSSIL, 0.01),
                locDrop("LOST_BOOK_OR_ITEM (O)770", ModItems.MIXED_SEEDS, 0.15),
                locDrop("(O)390", ModItems.STONE, 0.25)
        ));
        // Beach: gold_ore 8%, trilobite 3%, mixed_seeds 15%, stone 25%
        LOCATION_DROPS.put("Beach", List.of(
                locDrop("(O)384", ModItems.GOLD_ORE, 0.08),
                locDrop("(O)589", ModItems.TRILOBITE, 0.03),
                locDrop("LOST_BOOK_OR_ITEM (O)770", ModItems.MIXED_SEEDS, 0.15),
                locDrop("(O)390", ModItems.STONE, 0.25)
        ));
        // BusStop: prehistoric_vertebra 8%, copper_ore 15%, mixed_seeds 15%, stone 25%
        LOCATION_DROPS.put("BusStop", List.of(
                locDrop("(O)584", ModItems.PREHISTORIC_VERTEBRA, 0.08),
                locDrop("(O)378", ModItems.COPPER_ORE, 0.15),
                locDrop("LOST_BOOK_OR_ITEM (O)770", ModItems.MIXED_SEEDS, 0.15),
                locDrop("(O)390", ModItems.STONE, 0.25)
        ));
        // Backwoods: coal 60%, skeletal_hand 10%, copper_ore 10%, mixed_seeds 15%, stone 25%
        LOCATION_DROPS.put("Backwoods", List.of(
                locDrop("(O)382", ModItems.COAL, 0.6),
                locDrop("(O)582", ModItems.SKELETAL_HAND, 0.1),
                locDrop("(O)378", ModItems.COPPER_ORE, 0.1),
                locDrop("LOST_BOOK_OR_ITEM (O)770", ModItems.MIXED_SEEDS, 0.15),
                locDrop("(O)390", ModItems.STONE, 0.25)
        ));
        // Railroad: prehistoric_tibia 10%, copper_ore 15%, mixed_seeds 19%, stone 25%
        LOCATION_DROPS.put("Railroad", List.of(
                locDrop("(O)580", ModItems.PREHISTORIC_TIBIA, 0.1),
                locDrop("(O)378", ModItems.COPPER_ORE, 0.15),
                locDrop("LOST_BOOK_OR_ITEM (O)770", ModItems.MIXED_SEEDS, 0.19),
                locDrop("(O)390", ModItems.STONE, 0.25)
        ));
        // Farm_Standard: coal 50%, mixed_seeds 10%, stone 25%
        LOCATION_DROPS.put("Farm", List.of(
                locDrop("(O)382", ModItems.COAL, 0.5),
                locDrop("(O)770", ModItems.MIXED_SEEDS, 0.1),
                locDrop("(O)390", ModItems.STONE, 0.25)
        ));
        // Desert has no location-specific ArtifactSpots in SDV data
    }

    private static DropEntry locDrop(String id, DeferredItem<? extends Item> item, double chance) {
        return new DropEntry(id, chance, 0, false, item, null, 1, 1, ALWAYS);
    }

    // ======================== ArtifactSpotChances (from Objects.json) ========================

    private record ArtifactChance(DeferredItem<? extends Item> item, double chance) {}

    private static final Map<String, List<ArtifactChance>> ARTIFACT_SPOT_CHANCES = new LinkedHashMap<>();

    static {
        addAC("Town", ModItems.CHIPPED_AMPHORA, 0.04);
        addAC("Town", ModItems.ANCIENT_DOLL, 0.01);
        addAC("Town", ModItems.CHEWING_STICK, 0.01);
        addAC("Town", ModItems.ORNAMENTAL_FAN, 0.008);
        addAC("Town", ModItems.RUSTY_SPOON, 0.05);
        addAC("Town", ModItems.BONE_FLUTE, 0.005);
        addAC("Town", ModItems.ANCIENT_DRUM, 0.005);
        addAC("Town", ModItems.STRANGE_DOLL_GREEN, 0.001);
        addAC("Town", ModItems.STRANGE_DOLL_YELLOW, 0.001);

        addAC("Mountain", ModItems.ARROWHEAD, 0.02);
        addAC("Mountain", ModItems.ANCIENT_DOLL, 0.04);
        addAC("Mountain", ModItems.CHEWING_STICK, 0.02);
        addAC("Mountain", ModItems.DINOSAUR_EGG, 0.008);
        addAC("Mountain", ModItems.ANCIENT_SWORD, 0.008);
        addAC("Mountain", ModItems.RUSTY_COG, 0.05);
        addAC("Mountain", ModItems.ANCIENT_SEED, 0.01);
        addAC("Mountain", ModItems.PREHISTORIC_TOOL, 0.03);
        addAC("Mountain", ModItems.PREHISTORIC_HANDAXE, 0.05);
        addAC("Mountain", ModItems.BONE_FLUTE, 0.01);
        addAC("Mountain", ModItems.AMPHIBIAN_FOSSIL, 0.01);
        addAC("Mountain", ModItems.TRILOBITE, 0.03);
        addAC("Mountain", ModItems.STRANGE_DOLL_GREEN, 0.001);
        addAC("Mountain", ModItems.STRANGE_DOLL_YELLOW, 0.001);

        addAC("Forest", ModItems.ARROWHEAD, 0.02);
        addAC("Forest", ModItems.ANCIENT_DOLL, 0.03);
        addAC("Forest", ModItems.ELVISH_JEWELRY, 0.01);
        addAC("Forest", ModItems.CHEWING_STICK, 0.02);
        addAC("Forest", ModItems.ORNAMENTAL_FAN, 0.01);
        addAC("Forest", ModItems.ANCIENT_SWORD, 0.01);
        addAC("Forest", ModItems.ANCIENT_SEED, 0.01);
        addAC("Forest", ModItems.PREHISTORIC_TOOL, 0.03);
        addAC("Forest", ModItems.PREHISTORIC_HANDAXE, 0.05);
        addAC("Forest", ModItems.BONE_FLUTE, 0.01);
        addAC("Forest", ModItems.ANCIENT_DRUM, 0.01);
        addAC("Forest", ModItems.AMPHIBIAN_FOSSIL, 0.01);
        addAC("Forest", ModItems.TRILOBITE, 0.03);
        addAC("Forest", ModItems.PALM_FOSSIL, 0.01);
        addAC("Forest", ModItems.STRANGE_DOLL_GREEN, 0.001);
        addAC("Forest", ModItems.STRANGE_DOLL_YELLOW, 0.001);

        addAC("Beach", ModItems.ORNAMENTAL_FAN, 0.02);
        addAC("Beach", ModItems.DRIED_STARFISH, 0.1);
        addAC("Beach", ModItems.ANCHOR, 0.05);
        addAC("Beach", ModItems.GLASS_SHARDS, 0.1);
        addAC("Beach", ModItems.NAUTILUS_FOSSIL, 0.03);
        addAC("Beach", ModItems.PALM_FOSSIL, 0.01);
        addAC("Beach", ModItems.STRANGE_DOLL_GREEN, 0.001);
        addAC("Beach", ModItems.STRANGE_DOLL_YELLOW, 0.001);

        addAC("Farm", ModItems.RUSTY_SPUR, 0.1);
        addAC("Farm", ModItems.CHICKEN_STATUE, 0.1);
        addAC("Farm", ModItems.STRANGE_DOLL_GREEN, 0.001);
        addAC("Farm", ModItems.STRANGE_DOLL_YELLOW, 0.001);

        addAC("BusStop", ModItems.ARROWHEAD, 0.02);
        addAC("BusStop", ModItems.ANCIENT_DOLL, 0.03);
        addAC("BusStop", ModItems.PREHISTORIC_TOOL, 0.04);
        addAC("BusStop", ModItems.PREHISTORIC_HANDAXE, 0.05);
        addAC("BusStop", ModItems.ANCIENT_DRUM, 0.01);
        addAC("BusStop", ModItems.STRANGE_DOLL_GREEN, 0.001);
        addAC("BusStop", ModItems.STRANGE_DOLL_YELLOW, 0.001);

        addAC("Backwoods", ModItems.SKELETAL_HAND, 0.1);
        addAC("Backwoods", ModItems.STRANGE_DOLL_GREEN, 0.001);
        addAC("Backwoods", ModItems.STRANGE_DOLL_YELLOW, 0.001);

        addAC("Railroad", ModItems.STRANGE_DOLL_GREEN, 0.001);
        addAC("Railroad", ModItems.STRANGE_DOLL_YELLOW, 0.001);

        addAC("Desert", ModItems.GOLDEN_MASK, 0.04);
        addAC("Desert", ModItems.GOLDEN_RELIC, 0.06);
        addAC("Desert", ModItems.SKELETAL_TAIL, 0.06);
        addAC("Desert", ModItems.PALM_FOSSIL, 0.1);

        addAC("UndergroundMine", ModItems.DINOSAUR_EGG, 0.01);
        addAC("UndergroundMine", ModItems.RARE_DISC, 0.01);
        addAC("UndergroundMine", ModItems.DWARVISH_HELM, 0.01);
        addAC("UndergroundMine", ModItems.DWARF_GADGET, 0.001);
        addAC("UndergroundMine", ModItems.BONE_FLUTE, 0.02);
        addAC("UndergroundMine", ModItems.ANCIENT_DRUM, 0.02);
        addAC("UndergroundMine", ModItems.STRANGE_DOLL_GREEN, 0.001);
        addAC("UndergroundMine", ModItems.STRANGE_DOLL_YELLOW, 0.001);
    }

    private static void addAC(String location, DeferredItem<? extends Item> item, double chance) {
        ARTIFACT_SPOT_CHANCES.computeIfAbsent(location, k -> new ArrayList<>())
                .add(new ArtifactChance(item, chance));
    }

    // ======================== Zone Resolution ========================

    public static String resolveLocation(ServerLevel level, BlockPos pos) {
        if (level.dimension().equals(ModMiningDimensions.STARDEW_MINING)) {
            return "UndergroundMine";
        }
        if (!level.dimension().equals(ModDimensions.STARDEW_VALLEY)) {
            return "Default";
        }
        int x = pos.getX();
        int z = pos.getZ();
        for (Map.Entry<String, List<ZoneRect>> entry : ZONE_RECTS.entrySet()) {
            for (ZoneRect r : entry.getValue()) {
                if (r.contains(x, z)) {
                    return entry.getKey();
                }
            }
        }
        return "Default";
    }

    // ======================== Main Drop Logic ========================

    /**
     * Rolls artifact/item drops for a hoe dig at the given position.
     * Mirrors SDV's digUpArtifactSpot(): merges Default + Location drops,
     * sorts by Precedence, evaluates with ContinueOnDrop support.
     *
     * @return list of drops (usually 1, but ContinueOnDrop entries can add more).
     *         Guaranteed non-empty since Clay is the 100% fallback.
     */
    @SuppressWarnings("null")
    public static List<ItemStack> rollDrops(ServerLevel level, BlockPos pos) {
        RandomSource random = level.getRandom();
        String location = resolveLocation(level, pos);
        StardewTimeManager tm = StardewTimeManager.get();
        int season = tm.getCurrentSeason();
        int totalDaysPlayed = (tm.getCurrentYear() - 1) * 112 + season * 28 + tm.getCurrentDay();

        // Build merged drop list: Default + Location, then sort by Precedence (stable)
        List<DropEntry> allDrops = new ArrayList<>(DEFAULT_DROPS);
        List<DropEntry> locDrops = LOCATION_DROPS.get(location);
        if (locDrops != null) {
            allDrops.addAll(locDrops);
        }
        allDrops.sort(Comparator.comparingInt(DropEntry::precedence));

        List<ItemStack> results = new ArrayList<>();

        for (DropEntry drop : allDrops) {
            // Check chance
            if (random.nextDouble() >= drop.chance) {
                continue;
            }
            // Check condition
            if (!drop.condition.test(location, season, totalDaysPlayed, random)) {
                continue;
            }

            // Special handling for RANDOM_ARTIFACT_FOR_DIG_SPOT
            if ("RANDOM_ARTIFACT_FOR_DIG_SPOT".equals(drop.id)) {
                ItemStack artifact = rollRandomArtifact(location, random);
                if (artifact != null) {
                    results.add(artifact);
                    if (!drop.continueOnDrop) break;
                }
                continue;
            }

            // Resolve item
            ItemStack stack = resolveDropItem(drop, random);
            if (!stack.isEmpty()) {
                results.add(stack);
                if (!drop.continueOnDrop) {
                    break;
                }
            }
        }

        // Should never be empty (Clay is guaranteed fallback), but just in case
        if (results.isEmpty()) {
            results.add(new ItemStack(ModItems.CLAY.get()));
        }
        return results;
    }

    /**
     * Convenience method that returns only the first drop.
     * Most callers just need a single ItemStack.
     */
    public static ItemStack rollDrop(ServerLevel level, BlockPos pos) {
        List<ItemStack> drops = rollDrops(level, pos);
        return drops.get(0);
    }

    /**
     * Returns all drops (for callers that support ContinueOnDrop multi-drops).
     */
    public static List<ItemStack> rollAllDrops(ServerLevel level, BlockPos pos) {
        return rollDrops(level, pos);
    }

    private static ItemStack resolveDropItem(DropEntry drop, RandomSource random) {
        Item item;
        if (drop.randomItems != null && !drop.randomItems.isEmpty()) {
            // Pick random from list (e.g. warp totems)
            item = drop.randomItems.get(random.nextInt(drop.randomItems.size())).get();
        } else if (drop.item != null) {
            item = drop.item.get();
        } else {
            return ItemStack.EMPTY;
        }
        int count = drop.minStack;
        if (drop.maxStack > drop.minStack) {
            count = drop.minStack + random.nextInt(drop.maxStack - drop.minStack + 1);
        }
        return new ItemStack(item, count);
    }

    /**
     * Rolls RANDOM_ARTIFACT_FOR_DIG_SPOT: iterates all artifacts with ArtifactSpotChances
     * for the current location, returns the first match.
     */
    private static ItemStack rollRandomArtifact(String location, RandomSource random) {
        List<ArtifactChance> chances = ARTIFACT_SPOT_CHANCES.get(location);
        if (chances == null) return null;
        for (ArtifactChance ac : chances) {
            if (random.nextDouble() < ac.chance) {
                return new ItemStack(ac.item.get());
            }
        }
        return null;
    }
}
