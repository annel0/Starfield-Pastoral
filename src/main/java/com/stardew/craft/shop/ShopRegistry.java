package com.stardew.craft.shop;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Server-side static registry of shop definitions.
 *
 * SalableItemTags mapping (SDV Data/Shops.json → MC IStardewItem.getItemTypeKey()):
 *
 *   SeedShop  : crop,fruit,seed,fertilizer,cooking,cooking_ingredient,
 *               animal_product,artisan_goods,artisan_animal_quality,forage
 *   FishShop  : fish,legendary_fish,crabpot (fishing tackle/bait NOT sellable back)
 *   AnimalShop: animal_product,artisan_animal_quality
 *
 * Season constants: 0=spring, 1=summer, 2=fall, 3=winter  (matches StardewTimeManager)
 */
public final class ShopRegistry {

    // Season indices – mirrors StardewTimeManager.currentSeason values
    private static final int SPRING = 0;
    private static final int SUMMER = 1;
    private static final int FALL   = 2;

    public record ShopDefinition(
        String              shopId,
        String              ownerNpcId,
        String              ownerDialogue,
        List<ShopItemEntry> items,
        /** itemTypeKeys whose items this shop will buy from the player. */
        Set<String>         acceptedSellTypes
    ) {
        /**
         * Returns items that should currently be visible in the shop given
         * the current season and year.  Mirrors SDV ShopBuilder.CheckItemCondition.
         */
        public List<ShopItemEntry> getAvailableItems(int season, int year) {
            return items.stream()
                    .filter(e -> e.isAvailableIn(season, year))
                    .collect(Collectors.toList());
        }
    }

    private static final Map<String, ShopDefinition> REGISTRY = new LinkedHashMap<>();

    static {
        // -------------------------------------------------------------------
        // Pierre – General Store
        // Source of truth: 数据包/StardewCore/data/stardew/function/shop/pierre_inventory.json
        //   and SDV 1.6 源文件/Content/Data/Shops.json (SeedShop section).
        //
        // Season mapping (SDV SEASON condition → our season int):
        //   spring→0, summer→1, fall→2, winter→3
        // Year-2+ items: garlic_seeds, red_cabbage_seeds, artichoke_seeds
        // After day-15: basic/quality fertilizer, speed_gro
        // Year-2+ fertilizers: quality_retaining_soil, deluxe_speed_gro
        // -------------------------------------------------------------------
        REGISTRY.put("SeedShop", new ShopDefinition(
            "SeedShop",
            "Pierre",
            "Welcome to my shop!",
            List.of(
                // ---- Spring ----
                entry("stardewcraft:parsnip_seeds",     20,  SPRING),
                entry("stardewcraft:green_bean_seeds",  60,  SPRING),
                entry("stardewcraft:cauliflower_seeds", 80,  SPRING),
                entry("stardewcraft:potato_seeds",      50,  SPRING),
                entry("stardewcraft:tulip_seeds",       20,  SPRING),
                entry("stardewcraft:kale_seeds",        70,  SPRING),
                entry("stardewcraft:blue_jazz_seeds",   30,  SPRING),
                entry("stardewcraft:strawberry_seeds", 100,  SPRING), // Egg-Festival item in SDV; included per data pack
                entryYear("stardewcraft:garlic_seeds",  40,  SPRING,  2),

                // ---- Summer ----
                entry("stardewcraft:melon_seeds",       80,  SUMMER),
                entry("stardewcraft:tomato_seeds",      50,  SUMMER),
                entry("stardewcraft:blueberry_seeds",   80,  SUMMER),
                entry("stardewcraft:hot_pepper_seeds",  40,  SUMMER),
                entry("stardewcraft:wheat_seeds",       10,  SUMMER),
                entry("stardewcraft:radish_seeds",      40,  SUMMER),
                entry("stardewcraft:poppy_seeds",      100,  SUMMER),
                entry("stardewcraft:summer_spangle_seeds", 50, SUMMER),
                entry("stardewcraft:hops_seeds",        60,  SUMMER),
                entry("stardewcraft:corn_seeds",       150,  SUMMER),
                entry("stardewcraft:sunflower_seeds",  200,  SUMMER),
                entryYear("stardewcraft:red_cabbage_seeds", 100, SUMMER, 2),

                // ---- Fall ----
                entry("stardewcraft:eggplant_seeds",    20,  FALL),
                entry("stardewcraft:corn_seeds",       150,  FALL),
                entry("stardewcraft:pumpkin_seeds",    100,  FALL),
                entry("stardewcraft:bok_choy_seeds",    50,  FALL),
                entry("stardewcraft:yam_seeds",         60,  FALL),
                entry("stardewcraft:cranberry_seeds",  240,  FALL),
                entry("stardewcraft:wheat_seeds",       10,  FALL),
                entry("stardewcraft:sunflower_seeds",  200,  FALL),
                entry("stardewcraft:fairy_rose_seeds", 200,  FALL),
                entry("stardewcraft:amaranth_seeds",    70,  FALL),
                entry("stardewcraft:grape_seeds",       60,  FALL),
                entryYear("stardewcraft:artichoke_seeds", 30, FALL,  2),

                // ---- Year-round ----
                entryAllSeasons("stardewcraft:grass_starter",         100),
                entryAllSeasons("stardewcraft:blue_grass_starter",    80), // SDV: only from Marnie/Mastery

                // ---- Fertilizers: unlock after day 15 (modelled as minYear=1, all seasons) ----
                entryAllSeasons("stardewcraft:basic_fertilizer",      100),
                entryAllSeasons("stardewcraft:quality_fertilizer",    150),
                entryAllSeasons("stardewcraft:basic_retaining_soil",  100),
                entryAllSeasons("stardewcraft:speed_gro",             100),
                // ---- Fertilizers: Year 2+ (SDV YEAR 2 condition) ----
                entryAllSeasonsYear("stardewcraft:deluxe_fertilizer",        300, 2),
                entryAllSeasonsYear("stardewcraft:quality_retaining_soil",   150, 2),
                entryAllSeasonsYear("stardewcraft:deluxe_retaining_soil",    300, 2),
                entryAllSeasonsYear("stardewcraft:deluxe_speed_gro",          80, 2),
                entryAllSeasonsYear("stardewcraft:hyper_speed_gro",          150, 2),

                // ---- Machinery (SDV: Dehydrator sold at Pierre's, 5000g once) ----
                entryStock("stardewcraft:dehydrator",                       5000, 1),

                // ---- Cooking ingredients (SDV: year-round, no condition) ----
                entryAllSeasons("stardewcraft:sugar",          50),
                entryAllSeasons("stardewcraft:wheat_flour",    50),
                entryAllSeasons("stardewcraft:oil",           100),
                entryAllSeasons("stardewcraft:rice",          100),
                entryAllSeasons("stardewcraft:vinegar",       100)
                // NOTE: SDV also sells rice_shoots (O)273 spring/year2, but we don't
                //   have rice_shoots in the game yet → flagged for user
            ),
            Set.of(
                "stardewcraft.type.crop",
                "stardewcraft.type.fruit",
                "stardewcraft.type.forage",
                "stardewcraft.type.seed",
                "stardewcraft.type.fertilizer",
                "stardewcraft.type.cooking",
                "stardewcraft.type.cooking_ingredient",
                "stardewcraft.type.animal_product",
                "stardewcraft.type.artisan_goods",
                "stardewcraft.type.artisan_animal_quality"
            )
        ));

        // -------------------------------------------------------------------
        // Willy – Fish Shop  (SDV SalableItemTags = bait/fish/sell_at_fish_shop/tackle)
        // NOTE: SDV also sells recycling_machine and warp_totem_beach, but
        //   neither is registered in our game yet → flagged for user.
        // -------------------------------------------------------------------
        REGISTRY.put("FishShop", new ShopDefinition(
            "FishShop",
            "Willy",
            "Howdy! Finest fishing gear around.",
            List.of(
                // Available from the start
                entryAllSeasons("stardewcraft:bait",              5),
                entryAllSeasons("stardewcraft:lead_bobber",     200),
                entryAllSeasons("stardewcraft:spinner",         500),
                entryAllSeasons("stardewcraft:trap_bobber",     500),
                entryAllSeasons("stardewcraft:cork_bobber",     750),
                entryAllSeasons("stardewcraft:treasure_hunter", 750),
                entryAllSeasons("stardewcraft:dressed_spinner",1000),
                entryAllSeasons("stardewcraft:barbed_hook",    1000),
                entryAllSeasons("stardewcraft:quality_bobber", 2000),
                // Rods – limited stock 1 (mirrors SDV AvailableStockLimit = Player)
                entryStock("stardewcraft:fiberglass_rod", 1800, 1),
                entryStock("stardewcraft:iridium_rod",    7500, 1)
                // SDV also: recycling_machine (free, 1 stock), warp_totem_beach (500g)
                // → NOT in our game yet
            ),
            Set.of(
                "stardewcraft.type.fish",
                "stardewcraft.type.legendary_fish",
                "stardewcraft.type.crabpot"
            )
        ));

        // -------------------------------------------------------------------
        // Marnie – Ranch Shop
        // SDV also sells blue_grass_starter (Mastery-unlocked), but we add it to
        // Pierre's year-round section above as a simpler approximation.
        // -------------------------------------------------------------------
        REGISTRY.put("AnimalShop", new ShopDefinition(
            "AnimalShop",
            "Marnie",
            "Looking for animal supplies?",
            List.of(
                entryAllSeasons("stardewcraft:hay",           50),
                entryStock("stardewcraft:milk_pail",        2000, 1),
                entryStock("stardewcraft:shears",           3000, 1),
                entryAllSeasons("stardewcraft:heater",      2000),
                entryStock("stardewcraft:auto_grabber",    25000, 1)
            ),
            Set.of(
                "stardewcraft.type.animal_product",
                "stardewcraft.type.artisan_animal_quality"
            )
        ));

        // -------------------------------------------------------------------
        // Sandy – Oasis Shop  (SDV SalableItemTags same as SeedShop)
        // Sells desert-exclusive seeds year-round, regardless of season.
        // -------------------------------------------------------------------
        REGISTRY.put("OasisShop", new ShopDefinition(
            "OasisShop",
            "Sandy",
            "Welcome to the Oasis!",
            List.of(
                entryAllSeasons("stardewcraft:rhubarb_seeds",    100),
                entryAllSeasons("stardewcraft:starfruit_seeds",  400),
                entryAllSeasons("stardewcraft:beet_seeds",        20),
                entryAllSeasons("stardewcraft:coconut",          400),
                entryAllSeasons("stardewcraft:cactus_fruit",     150)
            ),
            Set.of(
                "stardewcraft.type.crop",
                "stardewcraft.type.fruit",
                "stardewcraft.type.forage",
                "stardewcraft.type.seed"
            )
        ));
    }

    // -------------------------------------------------------------------
    // Entry helpers (server-side only; seasons/minYear not sent over network)
    // -------------------------------------------------------------------

    /** Seasonal item available from year 1+, infinite stock. */
    private static ShopItemEntry entry(String id, int price, int season) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(season), 1);
    }

    /** Seasonal item, custom min-year, infinite stock. */
    private static ShopItemEntry entryYear(String id, int price, int season, int minYear) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(season), minYear);
    }

    /** Year-round item (all seasons) from year 1+, infinite stock. */
    private static ShopItemEntry entryAllSeasons(String id, int price) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(), 1);
    }

    /** Year-round item with custom minYear, infinite stock. */
    private static ShopItemEntry entryAllSeasonsYear(String id, int price, int minYear) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(), minYear);
    }

    /** Year-round item with limited stock, from year 1+. */
    private static ShopItemEntry entryStock(String id, int price, int stock) {
        return new ShopItemEntry(id, "", "", price, stock,
                null, 0, Set.of(), 1);
    }

    // -------------------------------------------------------------------

    public static ShopDefinition get(String shopId) {
        return REGISTRY.get(shopId);
    }

    public static List<String> allShopIds() {
        return new ArrayList<>(REGISTRY.keySet());
    }

    /**
     * Returns the sell price for an item at a given shop, or 0 if this shop
     * won't buy it.
     *
     * Logic (mirrors SDV ShopMenu.highlightItemToSell + sellToStorePrice):
     *   1. The item must implement IStardewItem and getSellPrice() > 0.
     *   2. The item's typeKey must be in shop.acceptedSellTypes.
     *   3. Sell price = IStardewItem.getSellPrice(stack) * sellPercentage (SDV default 1.0).
     *      → we use 1.0, same as SDV default.
     */
    public static int getSellPrice(net.minecraft.world.item.ItemStack stack,
                                   ShopDefinition shop) {
        if (stack.isEmpty()) return 0;
        net.minecraft.world.item.Item item = stack.getItem();
        if (!(item instanceof com.stardew.craft.item.IStardewItem si)) return 0;
        int basePrice = si.getSellPrice(stack);
        if (basePrice <= 0) return 0;
        String typeKey = si.getItemTypeKey();
        if (!shop.acceptedSellTypes().contains(typeKey)) return 0;
        return basePrice; // SDV: sellPercentage default 1.0f
    }
}
