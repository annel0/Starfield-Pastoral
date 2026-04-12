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
            "stardewcraft.shop.seedshop.dialogue",
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
                entryAllSeasons("stardewcraft:vinegar",       100),

                // ---- Tree fruits (SDV: from fruit trees, sold at Pierre as shortcut) ----
                entry("stardewcraft:cherry",               80,  SPRING),
                entry("stardewcraft:apricot",              50,  SPRING),
                entry("stardewcraft:peach",               140,  SUMMER),
                entry("stardewcraft:orange",              100,  SUMMER),
                entry("stardewcraft:banana",              150,  SUMMER),
                entry("stardewcraft:mango",               130,  SUMMER),
                entry("stardewcraft:apple",               100,  FALL),
                entry("stardewcraft:pomegranate",         140,  FALL),
                entryAllSeasons("stardewcraft:pineapple",        300)
                // TODO: implement proper fruit tree sapling system
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
            "stardewcraft.shop.fishshop.dialogue",
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
                entryStock("stardewcraft:iridium_rod",    7500, 1),
                entryStock("stardewcraft:training_rod",     25, 1),
                // 海滩拾取物（SDV: beach forage, 作为合成材料的获取途径）
                entryAllSeasons("stardewcraft:coral",         80),
                entryAllSeasons("stardewcraft:sea_urchin",   160)
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
            "stardewcraft.shop.animalshop.dialogue",
            List.of(
                entryAllSeasons("stardewcraft:hay",           50),
                entryStock("stardewcraft:milk_pail",        2000, 1),
                entryStock("stardewcraft:shears",           3000, 1),
                entryAllSeasons("stardewcraft:heater",      2000),
                entryStock("stardewcraft:auto_grabber",    25000, 1),
                entryStock("stardewcraft:auto_petter",     50000, 1)
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
            "stardewcraft.shop.oasis.dialogue",
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

        // -------------------------------------------------------------------
        // Clint – Blacksmith Material Shop
        // SDV parity: Data/Shops.json → "Blacksmith" section
        // Copper Ore 75→150g, Iron Ore 150→250g, Coal 150→250g, Gold Ore 400→750g
        // -------------------------------------------------------------------
        REGISTRY.put("Blacksmith", new ShopDefinition(
            "Blacksmith",
            "Clint",
            "stardewcraft.npc.clint.shop_dialogue",
            List.of(
                entryAllSeasons("stardewcraft:copper_ore",     75),
                entryAllSeasons("stardewcraft:iron_ore",      150),
                entryAllSeasons("stardewcraft:coal",          150),
                entryAllSeasons("stardewcraft:gold_ore",      400),
                // 基础工具（补充新手获取途径）
                entryStock("stardewcraft:pickaxe",            100, 1),
                entryStock("stardewcraft:axe",                100, 1),
                entryStock("stardewcraft:hoe",                100, 1),
                entryStock("stardewcraft:watering_can",       100, 1),
                entryStock("stardewcraft:scythe",             100, 1)
            ),
            Set.of() // Blacksmith doesn't buy items from player
        ));

        // -------------------------------------------------------------------
        // Gus – Saloon
        // SDV parity: Data/Shops.json → "Saloon" section
        // 8 consumable items + 9 cooking recipes (recipe: prefix = unlock-only)
        // Recipes: stock 1, once learned they disappear (server checks unlocked)
        // -------------------------------------------------------------------
        REGISTRY.put("Saloon", new ShopDefinition(
            "Saloon",
            "Gus",
            "stardewcraft.shop.saloon.dialogue",
            List.of(
                // ---- Consumable food items ----
                entryAllSeasons("stardewcraft:beer",         400),
                entryAllSeasons("stardewcraft:salad",        220),
                entryAllSeasons("stardewcraft:bread",        120),
                entryAllSeasons("stardewcraft:spaghetti",    240),
                entryAllSeasons("stardewcraft:pizza",        600),
                entryAllSeasons("stardewcraft:coffee",       300),
                // ---- Cooking recipes (SDV: sold as recipe unlocks) ----
                entryRecipe("stardewcraft:hashbrowns",        50),
                entryRecipe("stardewcraft:omelet",           100),
                entryRecipe("stardewcraft:pancakes",         100),
                entryRecipe("stardewcraft:bread",            100),
                entryRecipe("stardewcraft:tortilla",         100),
                entryRecipe("stardewcraft:pizza",            150),
                entryRecipe("stardewcraft:maki_roll",        300),
                entryRecipe("stardewcraft:cookie",           300),
                entryRecipe("stardewcraft:triple_shot_espresso", 5000)
            ),
            Set.of() // Saloon doesn't buy items from player
        ));

        // -------------------------------------------------------------------
        // Harvey – Hospital / Clinic
        // SDV parity: Data/Shops.json → "Hospital" section
        // Only 2 items: Energy Tonic (1000g) and Muscle Remedy (1000g)
        // -------------------------------------------------------------------
        REGISTRY.put("Hospital", new ShopDefinition(
            "Hospital",
            "Harvey",
            "stardewcraft.shop.hospital.dialogue",
            List.of(
                entryAllSeasons("stardewcraft:energy_tonic",   1000),
                entryAllSeasons("stardewcraft:muscle_remedy",  1000)
            ),
            Set.of() // Hospital doesn't buy items from player
        ));

        // -------------------------------------------------------------------
        // Marlon – Adventurer's Guild
        // SDV parity: Data/Shops.json → "AdventureShop" section
        // Items use MINE_LOWEST_LEVEL_REACHED / PLAYER_HAS_MAIL conditions.
        // Items NOT sold by Marlon (obtained elsewhere) are omitted:
        //   - Neptune's Glaive, Broken Trident → fishing treasure
        //   - Insect Head → Bug Killer goal (Gil)
        //   - Dark Sword → Haunted Skull drop
        //   - Meowmere → not in SDV shop
        //   - Dwarf/Dragontooth/Infinity weapons → volcano / forge (deferred)
        //   - Ossified Blade, Holy Blade, Yeti Tooth → mine chest / special
        //   - Burglar's Shank, Wicked Kris → mine drops / chests
        //   - Gil reward rings removed (slime_charmer, savage, vampire, etc.)
        //   - Craftable rings removed (warrior, ring_of_yoba, sturdy, thorns)
        // -------------------------------------------------------------------
        REGISTRY.put("AdventureShop", new ShopDefinition(
            "AdventureShop",
            "Marlon",
            "stardewcraft.shop.adventureshop.dialogue",
            List.of(
                // ---- Swords (SDV prices + mine-level conditions) ----
                entryAllSeasons("stardewcraft:rusty_sword",          250),   // no condition
                entryAllSeasons("stardewcraft:wooden_blade",         250),   // no condition
                entryMine("stardewcraft:steel_smallsword",           750, 20),
                entryMine("stardewcraft:silver_saber",               750, 20),
                entryMine("stardewcraft:pirate_sword",               850, 25),
                entryMine("stardewcraft:cutlass",                   1500, 25),
                entryMine("stardewcraft:iron_edge",                 2000, 40),
                entryMine("stardewcraft:forest_sword",              2000, 40),
                entryMine("stardewcraft:tempered_broadsword",       4000, 55),
                entryMine("stardewcraft:bone_sword",                6000, 75),
                entryMine("stardewcraft:steel_falchion",            9000, 90),
                entryMine("stardewcraft:obsidian_edge",             9000, 90),
                entryMine("stardewcraft:lava_katana",              25000, 120),
                entryMail("stardewcraft:galaxy_sword",             50000, "galaxySword"),

                // ---- Daggers (SDV prices + mine-level conditions) ----
                entryAllSeasons("stardewcraft:carving_knife",        100),   // no condition
                entryMine("stardewcraft:wind_spire",                 500, 15),
                entryMine("stardewcraft:elf_blade",                  750, 20),
                entryMine("stardewcraft:iron_dirk",                  500, 15),
                entryMine("stardewcraft:crystal_dagger",            4500, 60),
                entryMine("stardewcraft:shadow_dagger",             2000, 45),
                entryMail("stardewcraft:galaxy_dagger",            35000, "galaxySword"),

                // ---- Clubs (skip per user request — "棍棒类武器先不做了") ----
                // entryMine("stardewcraft:femur",                   350, 10),

                // ---- Boots (SDV prices + mine-level conditions) ----
                entryAllSeasons("stardewcraft:sneakers",             500),   // no condition
                entryMine("stardewcraft:rubber_boots",               500, 10),
                entryMine("stardewcraft:leather_boots",              500, 10),
                entryMine("stardewcraft:tundra_boots",               750, 50),
                entryMine("stardewcraft:thermal_boots",             1250, 40),
                entryMine("stardewcraft:combat_boots",              2000, 40),
                entryMine("stardewcraft:dark_boots",                2500, 80),
                entryMine("stardewcraft:firewalker_boots",          2000, 80),
                entryMine("stardewcraft:space_boots",               5000, 110),

                // ---- Rings (only items sold in SDV AdventureShop) ----
                entryAllSeasons("stardewcraft:small_glow_ring",     1000),
                entryMine("stardewcraft:glow_ring",                 2000, 40),
                entryAllSeasons("stardewcraft:small_magnet_ring",   1000),
                entryMine("stardewcraft:magnet_ring",               2000, 80),
                entryMine("stardewcraft:immunity_band",             4000, 80)
                // NOTE: slime_charmer, warrior, vampire, savage, ring_of_yoba,
                // sturdy, burglar's, crabshell, napalm, thorns, lucky, hot_java,
                // protection, soul_sapper, phoenix → obtained via Gil rewards
                // or crafting, NOT sold by Marlon.
            ),
            Set.of() // AdventureShop doesn't buy items from player in SDV
        ));

        // -------------------------------------------------------------------
        // Dwarf – Mine Entrance Shop
        // SDV parity: Data/Shops.json → "Dwarf" section
        // Sells bombs, mining consumables, and misc items.
        // Items we don't have yet are skipped.
        // -------------------------------------------------------------------
        REGISTRY.put("DwarfShop", new ShopDefinition(
            "DwarfShop",
            "Dwarf",
            "stardewcraft.shop.dwarf.dialogue",
            List.of(
                // Bombs (SDV exact prices)
                entryAllSeasons("stardewcraft:cherry_bomb",   450),   // SDV: 450g
                entryAllSeasons("stardewcraft:bomb_item",    1000),   // SDV: 1000g
                entryAllSeasons("stardewcraft:mega_bomb",    1600)    // SDV: 1600g
                // Life Elixir, Oil of Garlic, Miner's Treat → not yet implemented
                // Rarecrow #6, Cobblestone Path, Weathered Floor recipe → not yet implemented
            ),
            Set.of() // Dwarf doesn't buy items from player
        ));

        // -------------------------------------------------------------------
        // Robin – Carpenter Shop
        // SDV parity: Data/Shops.json → "Carpenter" section
        // Sells raw materials (Wood, Stone, Hardwood) + recipes/furniture.
        // Items we don't have yet are skipped per user rule.
        // -------------------------------------------------------------------
        REGISTRY.put("CarpenterShop", new ShopDefinition(
            "CarpenterShop",
            "Robin",
            "stardewcraft.shop.carpenter.dialogue",
            List.of(
                entryAllSeasons("stardewcraft:wood_normal",   10),
                entryAllSeasons("stardewcraft:stone",         20),
                entryAllSeasons("stardewcraft:wood_hard",    200),
                entryAllSeasons("stardewcraft:moss",          50),
                // Wallpaper & flooring
                entryAllSeasons("stardewcraft:wallpaper_block", 2),
                entryAllSeasons("stardewcraft:flooring_block",  2),
                // Basic furniture
                entryAllSeasons("stardewcraft:bed_1",         100),
                entryAllSeasons("stardewcraft:chair_1",        50),
                entryAllSeasons("stardewcraft:oak_table",     100),
                entryAllSeasons("stardewcraft:light_1",        75),
                entryAllSeasons("stardewcraft:dresser_1",     200),
                entryAllSeasons("stardewcraft:carpet_1",       25),
                entryAllSeasons("stardewcraft:photo_frame",    50),
                // Utility / farm upgrades
                entryAllSeasons("stardewcraft:cooking_pot",   5000),
                entryAllSeasons("stardewcraft:fridge",        3000),
                entryAllSeasons("stardewcraft:shipping_bin",  2500),
                entryAllSeasons("stardewcraft:trash_bin",      500),
                entryAllSeasons("stardewcraft:incubator",     5000),
                // Animal building utilities
                entryAllSeasons("stardewcraft:hay_hopper",             2000),
                entryAllSeasons("stardewcraft:feed_trough",            1000),
                entryAllSeasons("stardewcraft:autofeed_trough_upgrader", 10000),
                // Catalogue & tools
                entryAllSeasons("stardewcraft:furniture_catalogue", 200000),
                entryAllSeasons("stardewcraft:paintbrush",   0)
            ),
            Set.of("stardewcraft.type.resource")
        ));
    }

    // -------------------------------------------------------------------
    // Entry helpers (server-side only; seasons/minYear not sent over network)
    // -------------------------------------------------------------------

    /** Seasonal item available from year 1+, infinite stock. */
    private static ShopItemEntry entry(String id, int price, int season) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(season), 1, 0, null);
    }

    /** Seasonal item, custom min-year, infinite stock. */
    private static ShopItemEntry entryYear(String id, int price, int season, int minYear) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(season), minYear, 0, null);
    }

    /** Year-round item (all seasons) from year 1+, infinite stock. */
    private static ShopItemEntry entryAllSeasons(String id, int price) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(), 1, 0, null);
    }

    /** Year-round item with custom minYear, infinite stock. */
    private static ShopItemEntry entryAllSeasonsYear(String id, int price, int minYear) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(), minYear, 0, null);
    }

    /** Year-round item with limited stock, from year 1+. */
    private static ShopItemEntry entryStock(String id, int price, int stock) {
        return new ShopItemEntry(id, "", "", price, stock,
                null, 0, Set.of(), 1, 0, null);
    }

    /**
     * Recipe unlock entry: stock 1 (per player, once learned it's gone).
     * Uses "recipe:" prefix so server/client know this is a recipe unlock, not a physical item.
     */
    private static ShopItemEntry entryRecipe(String dishId, int price) {
        return new ShopItemEntry("recipe:" + dishId, "", "", price, 1,
                null, 0, Set.of(), 1, 0, null);
    }

    /** Year-round item with mine-level requirement, infinite stock. */
    private static ShopItemEntry entryMine(String id, int price, int minMineLevel) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(), 1, minMineLevel, null);
    }

    /** Year-round item with mail-flag requirement, infinite stock. */
    private static ShopItemEntry entryMail(String id, int price, String mailFlag) {
        return new ShopItemEntry(id, "", "", price, Integer.MAX_VALUE,
                null, 0, Set.of(), 1, 0, mailFlag);
    }

    // -------------------------------------------------------------------

    public static ShopDefinition get(String shopId) {
        return REGISTRY.get(shopId);
    }

    public static List<String> allShopIds() {
        return new ArrayList<>(REGISTRY.keySet());
    }

    /**
     * Build the filtered item list that should be visible to a specific player.
     * This applies season/year filtering, per-player stock tracking, and recipe-already-known filtering.
     * Must be used both when OPENING the shop and when HANDLING purchases, so that indices match.
     */
    public static List<ShopItemEntry> getFilteredItemsForPlayer(
            String shopId, ShopDefinition shop,
            net.minecraft.server.level.ServerPlayer player) {
        com.stardew.craft.time.StardewTimeManager time = com.stardew.craft.time.StardewTimeManager.get();
        List<ShopItemEntry> rawItems = shop.getAvailableItems(time.getCurrentSeason(), time.getCurrentYear());

        java.util.UUID playerId = player.getUUID();
        com.stardew.craft.player.PlayerStardewData data =
            com.stardew.craft.player.PlayerDataManager.getPlayerData(player);

        // Gather player conditions for mine-level / mail-flag filtering
        int playerMineLevel = com.stardew.craft.mining.MiningDataManager.getPlayerData(player).getMaxFloorReached();
        java.util.Set<String> playerMailFlags = data.getMailFlags();

        List<ShopItemEntry> result = new ArrayList<>();
        for (ShopItemEntry e : rawItems) {
            // SDV parity: never show recipes the player already knows
            if (e.itemId().startsWith("recipe:")) {
                String recipeId = SaloonService.extractRecipeId(e.itemId());
                if (data.isRecipeUnlocked(recipeId)) continue;
            }

            // SDV parity: mine-level and mail-flag conditions
            if (!e.meetsPlayerConditions(playerMineLevel, playerMailFlags)) continue;

            int remaining = ShopStockTracker.getRemaining(playerId, shopId, e.itemId(), e.stock());
            if (remaining == 0) continue;

            result.add(remaining == e.stock() ? e : new ShopItemEntry(
                e.itemId(), e.displayName(), e.description(),
                e.price(), remaining, e.tradeItemId(), e.tradeItemCount(),
                e.seasons(), e.minYear(), e.minMineLevel(), e.mailFlag()
            ));
        }
        return result;
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
