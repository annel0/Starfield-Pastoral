package com.stardew.craft;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_WEAPON_SPECIAL_EFFECTS = BUILDER
            .comment("Enable weapon special effects (rings, rifts, meteors, cores)")
            .define("client.weaponSpecialEffects", true);

    public static final ModConfigSpec.BooleanValue ENABLE_WEAPON_POST_EFFECTS = BUILDER
            .comment("Enable weapon post-processing effects (reserved for future shaders)")
            .define("client.weaponPostEffects", true);

    public static final ModConfigSpec.BooleanValue ENABLE_UI_INFO_SUITE = BUILDER
            .comment("Enable UI Info Suite features (Experience bars, tooltips, luck, NPC locations, etc.)")
            .define("client.uiInfoSuite.enabled", true);

    static {
        BUILDER.push("coopManager");

        COOP_SCAN_RANGE_XZ = BUILDER
                .comment("Horizontal scan range for coop manager validation")
                .translation("config.stardewcraft.coop_manager.scan_range_xz")
                .defineInRange("scanRangeXZ", 12, 4, 64);

        COOP_SCAN_RANGE_UP = BUILDER
                .comment("Vertical scan range above coop manager")
                .translation("config.stardewcraft.coop_manager.scan_range_up")
                .defineInRange("scanRangeUp", 12, 1, 32);

        COOP_SCAN_RANGE_DOWN = BUILDER
                .comment("Vertical scan range below coop manager")
                .translation("config.stardewcraft.coop_manager.scan_range_down")
                .defineInRange("scanRangeDown", 12, 0, 32);

        COOP_REQUIRE_ENCLOSED = BUILDER
                .comment("Whether coop shell must be enclosed (walls + roof + floor)")
                .translation("config.stardewcraft.coop_manager.require_enclosed")
                .define("requireEnclosed", true);

        COOP_REQUIRE_DOOR = BUILDER
                .comment("Whether coop shell must contain door/fence gate on boundary")
                .translation("config.stardewcraft.coop_manager.require_door")
                .define("requireDoor", true);

        COOP_MIN_DOOR_COUNT = BUILDER
                .comment("Minimum number of doors/fence gates required on coop boundary")
                .translation("config.stardewcraft.coop_manager.min_door_count")
                .defineInRange("minDoorCount", 1, 0, 8);

        COOP_DEV_HINTS = BUILDER
                .comment("Show detailed validation hints in development environment")
                .translation("config.stardewcraft.coop_manager.validation_hints")
                .define("devHints", true);

        BUILDER.push("tier1");
        COOP_T1_FEED_TROUGH = BUILDER.translation("config.stardewcraft.coop_manager.tier1.feed_trough").defineInRange("feedTrough", 4, 0, 64);
        COOP_T1_AUTOFEED_TROUGH = BUILDER.translation("config.stardewcraft.coop_manager.tier1.autofeed_trough").defineInRange("autofeedTrough", 0, 0, 64);
        COOP_T1_HAY_HOPPER = BUILDER.translation("config.stardewcraft.coop_manager.tier1.hay_hopper").defineInRange("hayHopper", 1, 0, 16);
        COOP_T1_INCUBATOR = BUILDER.translation("config.stardewcraft.coop_manager.tier1.incubator").defineInRange("incubator", 0, 0, 16);
        COOP_T1_MIN_INTERIOR_BLOCKS = BUILDER.translation("config.stardewcraft.coop_manager.tier1.min_interior_blocks").defineInRange("minInteriorBlocks", 216, 1, 4096);
        BUILDER.pop();

        BUILDER.push("tier2");
        COOP_T2_FEED_TROUGH = BUILDER.translation("config.stardewcraft.coop_manager.tier2.feed_trough").defineInRange("feedTrough", 8, 0, 64);
        COOP_T2_AUTOFEED_TROUGH = BUILDER.translation("config.stardewcraft.coop_manager.tier2.autofeed_trough").defineInRange("autofeedTrough", 0, 0, 64);
        COOP_T2_HAY_HOPPER = BUILDER.translation("config.stardewcraft.coop_manager.tier2.hay_hopper").defineInRange("hayHopper", 1, 0, 16);
        COOP_T2_INCUBATOR = BUILDER.translation("config.stardewcraft.coop_manager.tier2.incubator").defineInRange("incubator", 1, 0, 16);
        COOP_T2_MIN_INTERIOR_BLOCKS = BUILDER.translation("config.stardewcraft.coop_manager.tier2.min_interior_blocks").defineInRange("minInteriorBlocks", 288, 1, 4096);
        BUILDER.pop();

        BUILDER.push("tier3");
        COOP_T3_FEED_TROUGH = BUILDER.translation("config.stardewcraft.coop_manager.tier3.feed_trough").defineInRange("feedTrough", 0, 0, 64);
        COOP_T3_AUTOFEED_TROUGH = BUILDER.translation("config.stardewcraft.coop_manager.tier3.autofeed_trough").defineInRange("autofeedTrough", 12, 0, 64);
        COOP_T3_HAY_HOPPER = BUILDER.translation("config.stardewcraft.coop_manager.tier3.hay_hopper").defineInRange("hayHopper", 1, 0, 16);
        COOP_T3_INCUBATOR = BUILDER.translation("config.stardewcraft.coop_manager.tier3.incubator").defineInRange("incubator", 1, 0, 16);
        COOP_T3_MIN_INTERIOR_BLOCKS = BUILDER.translation("config.stardewcraft.coop_manager.tier3.min_interior_blocks").defineInRange("minInteriorBlocks", 360, 1, 4096);
        BUILDER.pop();

        BUILDER.pop();

        BUILDER.push("barnManager");

        BARN_SCAN_RANGE_XZ = BUILDER
                .comment("Horizontal scan range for barn manager validation")
                .translation("config.stardewcraft.barn_manager.scan_range_xz")
                .defineInRange("scanRangeXZ", 12, 4, 64);

        BARN_SCAN_RANGE_UP = BUILDER
                .comment("Vertical scan range above barn manager")
                .translation("config.stardewcraft.barn_manager.scan_range_up")
                .defineInRange("scanRangeUp", 12, 1, 32);

        BARN_SCAN_RANGE_DOWN = BUILDER
                .comment("Vertical scan range below barn manager")
                .translation("config.stardewcraft.barn_manager.scan_range_down")
                .defineInRange("scanRangeDown", 12, 0, 32);

        BARN_REQUIRE_ENCLOSED = BUILDER
                .comment("Whether barn shell must be enclosed (walls + roof + floor)")
                .translation("config.stardewcraft.barn_manager.require_enclosed")
                .define("requireEnclosed", true);

        BARN_REQUIRE_DOOR = BUILDER
                .comment("Whether barn shell must contain door/fence gate on boundary")
                .translation("config.stardewcraft.barn_manager.require_door")
                .define("requireDoor", true);

        BARN_MIN_DOOR_COUNT = BUILDER
                .comment("Minimum number of doors/fence gates required on barn boundary")
                .translation("config.stardewcraft.barn_manager.min_door_count")
                .defineInRange("minDoorCount", 1, 0, 8);

        BARN_DEV_HINTS = BUILDER
                .comment("Show detailed validation hints in development environment")
                .translation("config.stardewcraft.barn_manager.validation_hints")
                .define("devHints", true);

        BUILDER.push("tier1");
        BARN_T1_FEED_TROUGH = BUILDER.translation("config.stardewcraft.barn_manager.tier1.feed_trough").defineInRange("feedTrough", 4, 0, 64);
        BARN_T1_AUTOFEED_TROUGH = BUILDER.translation("config.stardewcraft.barn_manager.tier1.autofeed_trough").defineInRange("autofeedTrough", 0, 0, 64);
        BARN_T1_HAY_HOPPER = BUILDER.translation("config.stardewcraft.barn_manager.tier1.hay_hopper").defineInRange("hayHopper", 1, 0, 16);
        BARN_T1_INCUBATOR = BUILDER.translation("config.stardewcraft.barn_manager.tier1.incubator").defineInRange("incubator", 0, 0, 16);
        BARN_T1_MIN_INTERIOR_BLOCKS = BUILDER.translation("config.stardewcraft.barn_manager.tier1.min_interior_blocks").defineInRange("minInteriorBlocks", 252, 1, 4096);
        BUILDER.pop();

        BUILDER.push("tier2");
        BARN_T2_FEED_TROUGH = BUILDER.translation("config.stardewcraft.barn_manager.tier2.feed_trough").defineInRange("feedTrough", 8, 0, 64);
        BARN_T2_AUTOFEED_TROUGH = BUILDER.translation("config.stardewcraft.barn_manager.tier2.autofeed_trough").defineInRange("autofeedTrough", 0, 0, 64);
        BARN_T2_HAY_HOPPER = BUILDER.translation("config.stardewcraft.barn_manager.tier2.hay_hopper").defineInRange("hayHopper", 1, 0, 16);
        BARN_T2_INCUBATOR = BUILDER.translation("config.stardewcraft.barn_manager.tier2.incubator").defineInRange("incubator", 0, 0, 16);
        BARN_T2_MIN_INTERIOR_BLOCKS = BUILDER.translation("config.stardewcraft.barn_manager.tier2.min_interior_blocks").defineInRange("minInteriorBlocks", 336, 1, 4096);
        BUILDER.pop();

        BUILDER.push("tier3");
        BARN_T3_FEED_TROUGH = BUILDER.translation("config.stardewcraft.barn_manager.tier3.feed_trough").defineInRange("feedTrough", 0, 0, 64);
        BARN_T3_AUTOFEED_TROUGH = BUILDER.translation("config.stardewcraft.barn_manager.tier3.autofeed_trough").defineInRange("autofeedTrough", 12, 0, 64);
        BARN_T3_HAY_HOPPER = BUILDER.translation("config.stardewcraft.barn_manager.tier3.hay_hopper").defineInRange("hayHopper", 1, 0, 16);
        BARN_T3_INCUBATOR = BUILDER.translation("config.stardewcraft.barn_manager.tier3.incubator").defineInRange("incubator", 0, 0, 16);
        BARN_T3_MIN_INTERIOR_BLOCKS = BUILDER.translation("config.stardewcraft.barn_manager.tier3.min_interior_blocks").defineInRange("minInteriorBlocks", 420, 1, 4096);
        BUILDER.pop();

        BUILDER.pop();
    }

    public static final ModConfigSpec.IntValue COOP_SCAN_RANGE_XZ;
    public static final ModConfigSpec.IntValue COOP_SCAN_RANGE_UP;
    public static final ModConfigSpec.IntValue COOP_SCAN_RANGE_DOWN;
    public static final ModConfigSpec.BooleanValue COOP_REQUIRE_ENCLOSED;
    public static final ModConfigSpec.BooleanValue COOP_REQUIRE_DOOR;
    public static final ModConfigSpec.IntValue COOP_MIN_DOOR_COUNT;
    public static final ModConfigSpec.BooleanValue COOP_DEV_HINTS;

        public static final ModConfigSpec.IntValue BARN_SCAN_RANGE_XZ;
        public static final ModConfigSpec.IntValue BARN_SCAN_RANGE_UP;
        public static final ModConfigSpec.IntValue BARN_SCAN_RANGE_DOWN;
        public static final ModConfigSpec.BooleanValue BARN_REQUIRE_ENCLOSED;
        public static final ModConfigSpec.BooleanValue BARN_REQUIRE_DOOR;
        public static final ModConfigSpec.IntValue BARN_MIN_DOOR_COUNT;
        public static final ModConfigSpec.BooleanValue BARN_DEV_HINTS;

    public static final ModConfigSpec.IntValue COOP_T1_FEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T1_AUTOFEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T1_HAY_HOPPER;
    public static final ModConfigSpec.IntValue COOP_T1_INCUBATOR;
        public static final ModConfigSpec.IntValue COOP_T1_MIN_INTERIOR_BLOCKS;

    public static final ModConfigSpec.IntValue COOP_T2_FEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T2_AUTOFEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T2_HAY_HOPPER;
    public static final ModConfigSpec.IntValue COOP_T2_INCUBATOR;
        public static final ModConfigSpec.IntValue COOP_T2_MIN_INTERIOR_BLOCKS;

    public static final ModConfigSpec.IntValue COOP_T3_FEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T3_AUTOFEED_TROUGH;
    public static final ModConfigSpec.IntValue COOP_T3_HAY_HOPPER;
    public static final ModConfigSpec.IntValue COOP_T3_INCUBATOR;
        public static final ModConfigSpec.IntValue COOP_T3_MIN_INTERIOR_BLOCKS;

        public static final ModConfigSpec.IntValue BARN_T1_FEED_TROUGH;
        public static final ModConfigSpec.IntValue BARN_T1_AUTOFEED_TROUGH;
        public static final ModConfigSpec.IntValue BARN_T1_HAY_HOPPER;
        public static final ModConfigSpec.IntValue BARN_T1_INCUBATOR;
                public static final ModConfigSpec.IntValue BARN_T1_MIN_INTERIOR_BLOCKS;

        public static final ModConfigSpec.IntValue BARN_T2_FEED_TROUGH;
        public static final ModConfigSpec.IntValue BARN_T2_AUTOFEED_TROUGH;
        public static final ModConfigSpec.IntValue BARN_T2_HAY_HOPPER;
        public static final ModConfigSpec.IntValue BARN_T2_INCUBATOR;
                public static final ModConfigSpec.IntValue BARN_T2_MIN_INTERIOR_BLOCKS;

        public static final ModConfigSpec.IntValue BARN_T3_FEED_TROUGH;
        public static final ModConfigSpec.IntValue BARN_T3_AUTOFEED_TROUGH;
        public static final ModConfigSpec.IntValue BARN_T3_HAY_HOPPER;
        public static final ModConfigSpec.IntValue BARN_T3_INCUBATOR;
                public static final ModConfigSpec.IntValue BARN_T3_MIN_INTERIOR_BLOCKS;

    static final ModConfigSpec SPEC = BUILDER.build();
}
