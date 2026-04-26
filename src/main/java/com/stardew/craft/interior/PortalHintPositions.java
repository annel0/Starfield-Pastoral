package com.stardew.craft.interior;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Static registry of portal hint positions for client-side rendering.
 * Entity tags are server-only and not synced to clients, so we maintain
 * a compile-time list of known portal locations instead.
 */
public final class PortalHintPositions {

    private PortalHintPositions() {}

    /**
     * @param pos       bottom-center of the first block in the interaction area
     * @param isEnter   true = outdoor→indoor, false = indoor→outdoor
     * @param xBlocks   width along X axis (blocks)
     * @param heightBlocks height (blocks)
     * @param zBlocks   depth along Z axis (blocks)
     * @param hintStyle rendering style: ENTER (amber), EXIT (blue), RETURN_OVERWORLD (green)
     * @param destinationKey translation key suffix for the destination name (e.g. "pierre_shop")
     */
    public record HintInfo(Vec3 pos, boolean isEnter, int xBlocks, int heightBlocks, int zBlocks,
                           HintStyle hintStyle, String destinationKey) {
        public HintInfo(Vec3 pos, boolean isEnter, int xBlocks, int heightBlocks, int zBlocks, String destinationKey) {
            this(pos, isEnter, xBlocks, heightBlocks, zBlocks,
                 isEnter ? HintStyle.ENTER : HintStyle.EXIT, destinationKey);
        }
    }

    public enum HintStyle {
        ENTER,            // warm amber/gold
        EXIT,             // cool blue-white
        RETURN_OVERWORLD, // green
        LOCKED            // gray — 献祭未完成，视觉上告知"进不去"
    }

    private static final List<HintInfo> ALL = new ArrayList<>();

    public static List<HintInfo> all() {
        return Collections.unmodifiableList(ALL);
    }

    @SuppressWarnings("null")
    private static void enter(BlockPos pos, int xBlocks, int heightBlocks, int zBlocks, String destKey) {
        ALL.add(new HintInfo(Vec3.atBottomCenterOf(pos), true, xBlocks, heightBlocks, zBlocks, destKey));
    }

    @SuppressWarnings("null")
    private static void exit(BlockPos origin, BlockPos offset, int xBlocks, int heightBlocks, int zBlocks, String destKey) {
        BlockPos abs = origin.offset(offset);
        ALL.add(new HintInfo(Vec3.atBottomCenterOf(abs), false, xBlocks, heightBlocks, zBlocks, destKey));
    }

    static {
        // ---- Outdoor entry portals (enter) ----
        // Sizes match placePortalTriggerArea calls in InteriorSubspaceManager
        enter(new BlockPos(-160, -18, 54),   3, 3, 1, "pierre_shop");
        enter(new BlockPos(-309, -17, -36),  2, 2, 1, "museum");
        enter(new BlockPos(-288, -18, -17),  1, 2, 1, "blacksmith");
        enter(new BlockPos(-164, -17, 15),   2, 2, 1, "saloon");
        enter(new BlockPos(-197, -17, -22),  2, 2, 1, "mayor_house");
        enter(new BlockPos(-146, -18, 60),   3, 2, 1, "clinic");
        enter(new BlockPos(-195, -18, 32),   1, 2, 1, "1_river_road");
        enter(new BlockPos(-213, -12, 219),  2, 2, 1, "carpenter");
        enter(new BlockPos(-85, -16, -25),   2, 2, 1, "1_willow_lane");
        enter(new BlockPos(-115, -17, -27),  2, 2, 1, "2_willow_lane");
        enter(new BlockPos(178, -14, -4),    2, 2, 1, "marnie_ranch");
        enter(new BlockPos(155, -13, -58),   2, 2, 1, "leah_cottage");
        enter(new BlockPos(-335, -13, 312),  3, 2, 1, "adventurer_guild");
        enter(new BlockPos(-238, -15, -211), 2, 2, 1, "fish_shop");
        enter(new BlockPos(-267, -13, -152), 3, 2, 1, "elliott_cabin");
        enter(new BlockPos(340, -1, -42),    1, 2, 1, "wizard_tower");

        // ---- Indoor exit portals (exit) ----
        BlockPos pierreO  = new BlockPos(12032, 70, 12032);
        exit(pierreO, new BlockPos(5, 1, 6), 1, 2, 1, "pierre_shop");

        BlockPos museumO  = new BlockPos(13056, 70, 13056);
        exit(museumO, new BlockPos(9, 1, 5), 1, 2, 1, "museum");

        BlockPos blacksmithO = new BlockPos(13632, 70, 13632);
        exit(blacksmithO, new BlockPos(1, 1, 8), 1, 2, 1, "blacksmith");

        BlockPos saloonO = new BlockPos(14208, 70, 14208);
        exit(saloonO, new BlockPos(1, 1, 17), 1, 2, 1, "saloon");

        BlockPos mayorO = new BlockPos(14784, 70, 14784);
        exit(mayorO, new BlockPos(1, 1, 5), 1, 2, 2, "mayor_house");

        BlockPos clinicO = new BlockPos(15360, 70, 15360);
        exit(clinicO, new BlockPos(1, 1, 11), 1, 2, 1, "clinic");

        BlockPos riverRoad1O = new BlockPos(15936, 70, 15936);
        exit(riverRoad1O, new BlockPos(1, 1, 12), 1, 2, 1, "1_river_road");

        BlockPos carpenterO = new BlockPos(16512, 70, 16512);
        exit(carpenterO, new BlockPos(12, 5, 7), 1, 2, 1, "carpenter");

        BlockPos willow1O = new BlockPos(17088, 70, 17088);
        exit(willow1O, new BlockPos(1, 1, 5), 1, 2, 1, "1_willow_lane");

        BlockPos willow2O = new BlockPos(17088, 70, 17664);
        exit(willow2O, new BlockPos(3, 1, 2), 1, 2, 1, "2_willow_lane");

        BlockPos marnieO = new BlockPos(17088, 70, 18240);
        exit(marnieO, new BlockPos(1, 1, 15), 1, 2, 1, "marnie_ranch");

        BlockPos leahO = new BlockPos(17088, 70, 18816);
        exit(leahO, new BlockPos(4, 1, 7), 1, 2, 1, "leah_cottage");

        BlockPos adventurerO = new BlockPos(17664, 70, 17088);
        exit(adventurerO, new BlockPos(1, 1, 6), 1, 2, 1, "adventurer_guild");

        BlockPos fishShopO = new BlockPos(17664, 70, 17664);
        exit(fishShopO, new BlockPos(1, 1, 6), 1, 2, 1, "fish_shop");

        BlockPos elliottO = new BlockPos(17664, 70, 18240);
        exit(elliottO, new BlockPos(1, 1, 3), 1, 2, 1, "elliott_cabin");

        BlockPos wizardO = new BlockPos(18240, 70, 17088);
        exit(wizardO, new BlockPos(1, 1, 9), 1, 2, 1, "wizard_tower");

        // ---- Wizard tower "return to overworld" portal (green) ----
        BlockPos wizardReturnBase = new BlockPos(18249, 71, 17100);
        ALL.add(new HintInfo(
            Vec3.atBottomCenterOf(wizardReturnBase),
            false,  // not an "enter" portal
            3, 2, 3,
            HintStyle.RETURN_OVERWORLD,
            "overworld"
        ));

        // ---- Mine entrance (outdoor, Stardew Valley dimension) ----
        enter(new BlockPos(-287, -13, 314), 4, 3, 1, "mine");

        // ---- Mine exit (indoor, Mining dimension) ----
        ALL.add(new HintInfo(
            Vec3.atBottomCenterOf(new BlockPos(0, 66, -7)),
            false,  // exit portal
            1, 2, 1,
            HintStyle.EXIT,
            "mine"
        ));

        // ---- Community Center outdoor entry ----
        enter(new BlockPos(-191, -9, 141), 3, 2, 1, "community_center");

        // ---- Community Center indoor exit ----
        BlockPos ccOrigin = new BlockPos(18816, 69, 18816);
        exit(ccOrigin, new BlockPos(17, 1, 37), 1, 2, 2, "community_center");

        // ---- Farm entry portals (outdoor, fixed coordinates) ----
        // South entry (toward town): interaction area (199,-14,161)→(210,-12,161)
        enter(new BlockPos(199, -14, 161), 12, 3, 1, "farm_south");
        // East entry: interaction area (209,-15,26)→(216,-13,26)
        enter(new BlockPos(209, -15, 26), 8, 3, 1, "farm_east");
        // West entry (toward forest): interaction area (70,-12,116)→(70,-10,123)
        enter(new BlockPos(70, -12, 116), 1, 3, 8, "farm_west");

        // ---- Joja Mart outdoor entry ----
        enter(new BlockPos(-294, -16, 59), 4, 4, 1, "joja_mart");

        // ---- Joja Mart indoor exit ----
        BlockPos jojaMartO = new BlockPos(18240, 70, 18240);
        exit(jojaMartO, new BlockPos(1, 1, 13), 1, 2, 2, "joja_mart");

        // ---- Desert: Oasis outdoor entry (in calico desert) ----
        enter(new BlockPos(-360, -40, 1414), 2, 2, 1, "oasis");

        // ---- Desert: Oasis indoor exit ----
        BlockPos oasisO = new BlockPos(18240, 70, 17664);
        exit(oasisO, new BlockPos(1, 1, 4), 1, 2, 1, "oasis");

        // ---- Desert: mine entrance (placeholder, skull cavern) ----
        enter(new BlockPos(-340, -42, 1266), 3, 3, 1, "desert_mine");

        // ---- Skull Cavern exit (mining dimension, floor 121 lobby) ----
        // Portal: origin(-6,64,24211) + (1, 1..2, 2..3) → basePos (-5, 65, 24213), size 1×2×2
        ALL.add(new HintInfo(
            Vec3.atBottomCenterOf(new BlockPos(-5, 65, 24213)),
            false,  // exit portal
            1, 2, 2,
            HintStyle.RETURN_OVERWORLD,
            "desert_mine"
        ));
    }
}
