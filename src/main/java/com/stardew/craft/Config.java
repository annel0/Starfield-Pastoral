package com.stardew.craft;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    public static final Client CLIENT = new Client(CLIENT_BUILDER);
    public static final General GENERAL = new General(COMMON_BUILDER);
    public static final TotemPole TOTEM_POLE = new TotemPole(COMMON_BUILDER);
    public static final BuildingManager COOP_MANAGER = new BuildingManager(COMMON_BUILDER, "coopManager", "coop_manager",
            new TierDefaults(4, 0, 1, 0, 216),
            new TierDefaults(8, 0, 1, 1, 288),
            new TierDefaults(0, 12, 1, 1, 360));
    public static final BuildingManager BARN_MANAGER = new BuildingManager(COMMON_BUILDER, "barnManager", "barn_manager",
            new TierDefaults(4, 0, 1, 0, 252),
            new TierDefaults(8, 0, 1, 0, 336),
            new TierDefaults(0, 12, 1, 0, 420));
    public static final Mining MINING = new Mining(COMMON_BUILDER);
    public static final Fishing FISHING = new Fishing(COMMON_BUILDER);

    public static final ModConfigSpec COMMON_SPEC = COMMON_BUILDER.build();
    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();
    static final ModConfigSpec SPEC = COMMON_SPEC;

    public static final ModConfigSpec.BooleanValue ENABLE_WEAPON_SPECIAL_EFFECTS = CLIENT.ENABLE_WEAPON_SPECIAL_EFFECTS;
    public static final ModConfigSpec.BooleanValue ENABLE_WEAPON_POST_EFFECTS = CLIENT.ENABLE_WEAPON_POST_EFFECTS;
    public static final ModConfigSpec.BooleanValue ENABLE_UI_INFO_SUITE = CLIENT.ENABLE_UI_INFO_SUITE;

    public static final ModConfigSpec.IntValue MAX_STACK_SIZE = GENERAL.MAX_STACK_SIZE;

    public static final ModConfigSpec.BooleanValue TOTEM_POLE_ENFORCE_PLACEMENT_RULES = TOTEM_POLE.ENFORCE_PLACEMENT_RULES;

    public static final ModConfigSpec.IntValue COOP_SCAN_RANGE_XZ = COOP_MANAGER.SCAN_RANGE_XZ;
    public static final ModConfigSpec.IntValue COOP_SCAN_RANGE_UP = COOP_MANAGER.SCAN_RANGE_UP;
    public static final ModConfigSpec.IntValue COOP_SCAN_RANGE_DOWN = COOP_MANAGER.SCAN_RANGE_DOWN;
    public static final ModConfigSpec.BooleanValue COOP_REQUIRE_ENCLOSED = COOP_MANAGER.REQUIRE_ENCLOSED;
    public static final ModConfigSpec.BooleanValue COOP_REQUIRE_DOOR = COOP_MANAGER.REQUIRE_DOOR;
    public static final ModConfigSpec.IntValue COOP_MIN_DOOR_COUNT = COOP_MANAGER.MIN_DOOR_COUNT;
    public static final ModConfigSpec.BooleanValue COOP_DEV_HINTS = COOP_MANAGER.DEV_HINTS;
    public static final ModConfigSpec.IntValue COOP_T1_FEED_TROUGH = COOP_MANAGER.TIER1.FEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T1_AUTOFEED_TROUGH = COOP_MANAGER.TIER1.AUTOFEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T1_HAY_HOPPER = COOP_MANAGER.TIER1.HAY_HOPPER;
    public static final ModConfigSpec.IntValue COOP_T1_INCUBATOR = COOP_MANAGER.TIER1.INCUBATOR;
    public static final ModConfigSpec.IntValue COOP_T1_MIN_INTERIOR_BLOCKS = COOP_MANAGER.TIER1.MIN_INTERIOR_BLOCKS;
    public static final ModConfigSpec.IntValue COOP_T2_FEED_TROUGH = COOP_MANAGER.TIER2.FEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T2_AUTOFEED_TROUGH = COOP_MANAGER.TIER2.AUTOFEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T2_HAY_HOPPER = COOP_MANAGER.TIER2.HAY_HOPPER;
    public static final ModConfigSpec.IntValue COOP_T2_INCUBATOR = COOP_MANAGER.TIER2.INCUBATOR;
    public static final ModConfigSpec.IntValue COOP_T2_MIN_INTERIOR_BLOCKS = COOP_MANAGER.TIER2.MIN_INTERIOR_BLOCKS;
    public static final ModConfigSpec.IntValue COOP_T3_FEED_TROUGH = COOP_MANAGER.TIER3.FEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T3_AUTOFEED_TROUGH = COOP_MANAGER.TIER3.AUTOFEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T3_HAY_HOPPER = COOP_MANAGER.TIER3.HAY_HOPPER;
    public static final ModConfigSpec.IntValue COOP_T3_INCUBATOR = COOP_MANAGER.TIER3.INCUBATOR;
    public static final ModConfigSpec.IntValue COOP_T3_MIN_INTERIOR_BLOCKS = COOP_MANAGER.TIER3.MIN_INTERIOR_BLOCKS;

    public static final ModConfigSpec.IntValue BARN_SCAN_RANGE_XZ = BARN_MANAGER.SCAN_RANGE_XZ;
    public static final ModConfigSpec.IntValue BARN_SCAN_RANGE_UP = BARN_MANAGER.SCAN_RANGE_UP;
    public static final ModConfigSpec.IntValue BARN_SCAN_RANGE_DOWN = BARN_MANAGER.SCAN_RANGE_DOWN;
    public static final ModConfigSpec.BooleanValue BARN_REQUIRE_ENCLOSED = BARN_MANAGER.REQUIRE_ENCLOSED;
    public static final ModConfigSpec.BooleanValue BARN_REQUIRE_DOOR = BARN_MANAGER.REQUIRE_DOOR;
    public static final ModConfigSpec.IntValue BARN_MIN_DOOR_COUNT = BARN_MANAGER.MIN_DOOR_COUNT;
    public static final ModConfigSpec.BooleanValue BARN_DEV_HINTS = BARN_MANAGER.DEV_HINTS;
    public static final ModConfigSpec.IntValue BARN_T1_FEED_TROUGH = BARN_MANAGER.TIER1.FEED_TROUGH;
    public static final ModConfigSpec.IntValue BARN_T1_AUTOFEED_TROUGH = BARN_MANAGER.TIER1.AUTOFEED_TROUGH;
    public static final ModConfigSpec.IntValue BARN_T1_HAY_HOPPER = BARN_MANAGER.TIER1.HAY_HOPPER;
    public static final ModConfigSpec.IntValue BARN_T1_INCUBATOR = BARN_MANAGER.TIER1.INCUBATOR;
    public static final ModConfigSpec.IntValue BARN_T1_MIN_INTERIOR_BLOCKS = BARN_MANAGER.TIER1.MIN_INTERIOR_BLOCKS;
    public static final ModConfigSpec.IntValue BARN_T2_FEED_TROUGH = BARN_MANAGER.TIER2.FEED_TROUGH;
    public static final ModConfigSpec.IntValue BARN_T2_AUTOFEED_TROUGH = BARN_MANAGER.TIER2.AUTOFEED_TROUGH;
    public static final ModConfigSpec.IntValue BARN_T2_HAY_HOPPER = BARN_MANAGER.TIER2.HAY_HOPPER;
    public static final ModConfigSpec.IntValue BARN_T2_INCUBATOR = BARN_MANAGER.TIER2.INCUBATOR;
    public static final ModConfigSpec.IntValue BARN_T2_MIN_INTERIOR_BLOCKS = BARN_MANAGER.TIER2.MIN_INTERIOR_BLOCKS;
    public static final ModConfigSpec.IntValue BARN_T3_FEED_TROUGH = BARN_MANAGER.TIER3.FEED_TROUGH;
    public static final ModConfigSpec.IntValue BARN_T3_AUTOFEED_TROUGH = BARN_MANAGER.TIER3.AUTOFEED_TROUGH;
    public static final ModConfigSpec.IntValue BARN_T3_HAY_HOPPER = BARN_MANAGER.TIER3.HAY_HOPPER;
    public static final ModConfigSpec.IntValue BARN_T3_INCUBATOR = BARN_MANAGER.TIER3.INCUBATOR;
    public static final ModConfigSpec.IntValue BARN_T3_MIN_INTERIOR_BLOCKS = BARN_MANAGER.TIER3.MIN_INTERIOR_BLOCKS;

    public static final ModConfigSpec.BooleanValue SHOW_MONSTER_HP_BAR = MINING.SHOW_MONSTER_HP_BAR;
    public static final ModConfigSpec.DoubleValue MINE_LADDER_BASE_CHANCE = MINING.LADDER_BASE_CHANCE;
    public static final ModConfigSpec.BooleanValue ENABLE_FISHING_MINIGAME = FISHING.ENABLE_MINIGAME;

    private Config() {
    }

    public static final class Client {
        public final ModConfigSpec.BooleanValue ENABLE_WEAPON_SPECIAL_EFFECTS;
        public final ModConfigSpec.BooleanValue ENABLE_WEAPON_POST_EFFECTS;
        public final ModConfigSpec.BooleanValue ENABLE_UI_INFO_SUITE;

        private Client(ModConfigSpec.Builder builder) {
            builder.push("client");
            ENABLE_WEAPON_SPECIAL_EFFECTS = builder
                    .comment("Enable weapon special effects (rings, rifts, meteors, cores)")
                    .translation("config.stardewcraft.client.weapon_special_effects")
                    .define("weaponSpecialEffects", true);

            ENABLE_WEAPON_POST_EFFECTS = builder
                    .comment("Enable weapon post-processing effects (reserved for future shaders)")
                    .translation("config.stardewcraft.client.weapon_post_effects")
                    .define("weaponPostEffects", true);

            builder.push("uiInfoSuite");
            ENABLE_UI_INFO_SUITE = builder
                    .comment("Enable UI Info Suite features (Experience bars, tooltips, luck, NPC locations, etc.)")
                    .translation("config.stardewcraft.client.ui_info_suite")
                    .define("enabled", true);
            builder.pop();
            builder.pop();
        }
    }

    public static final class General {
        public final ModConfigSpec.IntValue MAX_STACK_SIZE;

        private General(ModConfigSpec.Builder builder) {
            builder.push("general");
            MAX_STACK_SIZE = builder
                    .comment("Maximum stack size for stackable items.",
                            "Set to 64 to use vanilla behavior, or up to 999 for Stardew Valley parity.",
                            "Requires restart to take full effect.")
                    .translation("config.stardewcraft.general.max_stack_size")
                    .defineInRange("maxStackSize", 999, 64, 999);
            builder.pop();
        }
    }

    public static final class TotemPole {
        public final ModConfigSpec.BooleanValue ENFORCE_PLACEMENT_RULES;

        private TotemPole(ModConfigSpec.Builder builder) {
            builder.push("totemPole");
            ENFORCE_PLACEMENT_RULES = builder
                    .comment("Restrict totem poles to Stardew Valley and their configured placement areas")
                    .translation("config.stardewcraft.totem_pole.enforce_placement_rules")
                    .define("enforcePlacementRules", true);
            builder.pop();
        }
    }

    public static final class BuildingManager {
        public final ModConfigSpec.IntValue SCAN_RANGE_XZ;
        public final ModConfigSpec.IntValue SCAN_RANGE_UP;
        public final ModConfigSpec.IntValue SCAN_RANGE_DOWN;
        public final ModConfigSpec.BooleanValue REQUIRE_ENCLOSED;
        public final ModConfigSpec.BooleanValue REQUIRE_DOOR;
        public final ModConfigSpec.IntValue MIN_DOOR_COUNT;
        public final ModConfigSpec.BooleanValue DEV_HINTS;
        public final Tier TIER1;
        public final Tier TIER2;
        public final Tier TIER3;

        private BuildingManager(ModConfigSpec.Builder builder, String path, String translationSection,
                                TierDefaults tier1, TierDefaults tier2, TierDefaults tier3) {
            builder.push(path);
            String prefix = "config.stardewcraft." + translationSection;

            SCAN_RANGE_XZ = builder
                    .comment("Horizontal scan range for building manager validation")
                    .translation(prefix + ".scan_range_xz")
                    .defineInRange("scanRangeXZ", 12, 4, 64);

            SCAN_RANGE_UP = builder
                    .comment("Vertical scan range above building manager")
                    .translation(prefix + ".scan_range_up")
                    .defineInRange("scanRangeUp", 12, 1, 32);

            SCAN_RANGE_DOWN = builder
                    .comment("Vertical scan range below building manager")
                    .translation(prefix + ".scan_range_down")
                    .defineInRange("scanRangeDown", 12, 0, 32);

            REQUIRE_ENCLOSED = builder
                    .comment("Whether building shell must be enclosed (walls + roof + floor)")
                    .translation(prefix + ".require_enclosed")
                    .define("requireEnclosed", true);

            REQUIRE_DOOR = builder
                    .comment("Whether building shell must contain door/fence gate on boundary")
                    .translation(prefix + ".require_door")
                    .define("requireDoor", true);

            MIN_DOOR_COUNT = builder
                    .comment("Minimum number of doors/fence gates required on building boundary")
                    .translation(prefix + ".min_door_count")
                    .defineInRange("minDoorCount", 1, 0, 8);

            DEV_HINTS = builder
                    .comment("Show detailed validation hints in development environment")
                    .translation(prefix + ".validation_hints")
                    .define("devHints", true);

            TIER1 = new Tier(builder, prefix, "tier1", tier1);
            TIER2 = new Tier(builder, prefix, "tier2", tier2);
            TIER3 = new Tier(builder, prefix, "tier3", tier3);
            builder.pop();
        }
    }

    public static final class Tier {
        public final ModConfigSpec.IntValue FEED_TROUGH;
        public final ModConfigSpec.IntValue AUTOFEED_TROUGH;
        public final ModConfigSpec.IntValue HAY_HOPPER;
        public final ModConfigSpec.IntValue INCUBATOR;
        public final ModConfigSpec.IntValue MIN_INTERIOR_BLOCKS;

        private Tier(ModConfigSpec.Builder builder, String prefix, String path, TierDefaults defaults) {
            builder.push(path);
            FEED_TROUGH = builder
                    .translation(prefix + "." + path + ".feed_trough")
                    .defineInRange("feedTrough", defaults.feedTrough(), 0, 64);
            AUTOFEED_TROUGH = builder
                    .translation(prefix + "." + path + ".autofeed_trough")
                    .defineInRange("autofeedTrough", defaults.autofeedTrough(), 0, 64);
            HAY_HOPPER = builder
                    .translation(prefix + "." + path + ".hay_hopper")
                    .defineInRange("hayHopper", defaults.hayHopper(), 0, 16);
            INCUBATOR = builder
                    .translation(prefix + "." + path + ".incubator")
                    .defineInRange("incubator", defaults.incubator(), 0, 16);
            MIN_INTERIOR_BLOCKS = builder
                    .translation(prefix + "." + path + ".min_interior_blocks")
                    .defineInRange("minInteriorBlocks", defaults.minInteriorBlocks(), 1, 4096);
            builder.pop();
        }
    }

    private record TierDefaults(int feedTrough, int autofeedTrough, int hayHopper, int incubator,
                                int minInteriorBlocks) {
    }

    public static final class Mining {
        public final ModConfigSpec.BooleanValue SHOW_MONSTER_HP_BAR;
        public final ModConfigSpec.DoubleValue LADDER_BASE_CHANCE;

        private Mining(ModConfigSpec.Builder builder) {
            builder.push("mining");
            SHOW_MONSTER_HP_BAR = builder
                    .comment("Show monster name and HP bar above their heads in the mine")
                    .translation("config.stardewcraft.mining.show_monster_hp_bar")
                    .define("showMonsterHpBar", true);

            LADDER_BASE_CHANCE = builder
                    .comment("Base chance for a mine ladder to appear after breaking a countable mine stone.",
                            "The final chance also includes stones-left, luck, enemy-clear, and buff modifiers.",
                            "Value is a decimal chance: 0.012 means 1.2%.")
                    .translation("config.stardewcraft.mining.ladder_base_chance")
                    .defineInRange("ladderBaseChance", 0.012D, 0.0D, 1.0D);
            builder.pop();
        }
    }

    public static final class Fishing {
        public final ModConfigSpec.BooleanValue ENABLE_MINIGAME;

        private Fishing(ModConfigSpec.Builder builder) {
            builder.push("fishing");
            ENABLE_MINIGAME = builder
                    .comment("Enable the fishing minigame for fish catches.",
                            "If disabled, fish are caught immediately after biting; non-fish catchables still behave as instant catches.")
                    .translation("config.stardewcraft.fishing.enable_minigame")
                    .define("enableMinigame", true);
            builder.pop();
        }
    }
}
