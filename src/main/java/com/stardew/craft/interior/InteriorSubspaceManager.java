package com.stardew.craft.interior;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.mining.StructureLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 室内亚空间管理器：
 * - 在星露谷维度内划定固定远坐标区域
 * - 维护固定结构清单（路径 + 固定坐标）
 * - 按版本执行一次性装载（可升级）
 */
@SuppressWarnings("null")
public final class InteriorSubspaceManager {

    private InteriorSubspaceManager() {
    }

    // 用户要求：室内亚空间区域必须在第一象限远坐标，且 X>10000、Z>10000。
    public static final int REGION_MIN_X = 10001;
    public static final int REGION_MIN_Z = 10001;
    public static final int REGION_MAX_X = 19000;
    public static final int REGION_MAX_Z = 19000;

    // 结构布局版本：当结构清单或坐标大改时 +1，可触发重新装载。
    private static final int LAYOUT_VERSION = 29;

    private static final String PIERRE_HOUSE_STRUCTURE_PATH = "data/stardewcraft/structures/interior/pierre_house.schem";
    private static final BlockPos PIERRE_HOUSE_ORIGIN = new BlockPos(12032, 70, 12032);
    private static final BlockPos PIERRE_INDOOR_SPAWN_OFFSET = new BlockPos(6, 1, 6);
    private static final BlockPos PIERRE_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(5, 1, 6);
    private static final BlockPos PIERRE_OUTDOOR_ENTRY_POS = new BlockPos(-159, -18, 54);
    private static final BlockPos PIERRE_OUTDOOR_INTERACTION_BASE = new BlockPos(-160, -18, 54);

    private static final String MUSEUM_STRUCTURE_PATH = "data/stardewcraft/structures/interior/museum.schem";
    private static final BlockPos MUSEUM_ORIGIN = new BlockPos(13056, 70, 13056);
    private static final BlockPos MUSEUM_INDOOR_SPAWN_OFFSET = new BlockPos(10, 1, 5);
    private static final BlockPos MUSEUM_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(9, 1, 5);
    private static final BlockPos MUSEUM_OUTDOOR_ENTRY_POS = new BlockPos(-309, -17, -36);
    private static final BlockPos MUSEUM_OUTDOOR_EXIT_POS = new BlockPos(-308, -17, -37);

    private static final String BLACKSMITH_STRUCTURE_PATH = "data/stardewcraft/structures/interior/blacksmith.schem";
    private static final BlockPos BLACKSMITH_ORIGIN = new BlockPos(13632, 70, 13632);
    private static final BlockPos BLACKSMITH_INDOOR_SPAWN_OFFSET = new BlockPos(3, 1, 8);
    private static final BlockPos BLACKSMITH_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 8);
    private static final BlockPos BLACKSMITH_OUTDOOR_ENTRY_POS = new BlockPos(-288, -18, -17);
    private static final BlockPos BLACKSMITH_OUTDOOR_EXIT_POS = new BlockPos(-288, -18, -19);

    private static final String TAG_PORTAL_MARKER_OUTSIDE = "sdv_portal_marker:pierre_house_outside";
    private static final String TAG_PORTAL_MARKER_INSIDE = "sdv_portal_marker:pierre_house_inside";
    private static final String TAG_PORTAL_MARKER_MUSEUM_OUTSIDE = "sdv_portal_marker:museum_outside";
    private static final String TAG_PORTAL_MARKER_MUSEUM_INSIDE = "sdv_portal_marker:museum_inside";
    private static final String TAG_PORTAL_MARKER_BLACKSMITH_OUTSIDE = "sdv_portal_marker:blacksmith_outside";
    private static final String TAG_PORTAL_MARKER_BLACKSMITH_INSIDE = "sdv_portal_marker:blacksmith_inside";

    private static final String SALOON_STRUCTURE_PATH = "data/stardewcraft/structures/interior/saloon.schem";
    private static final BlockPos SALOON_ORIGIN = new BlockPos(14208, 70, 14208);
    private static final BlockPos SALOON_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 17);
    private static final BlockPos SALOON_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 17);
    private static final BlockPos SALOON_OUTDOOR_ENTRY_POS = new BlockPos(-164, -17, 15);
    private static final BlockPos SALOON_OUTDOOR_EXIT_POS = new BlockPos(-164, -17, 14);

    private static final String TAG_PORTAL_MARKER_SALOON_OUTSIDE = "sdv_portal_marker:saloon_outside";
    private static final String TAG_PORTAL_MARKER_SALOON_INSIDE = "sdv_portal_marker:saloon_inside";

    private static final String MAYOR_HOUSE_STRUCTURE_PATH = "data/stardewcraft/structures/interior/mayor_house.schem";
    private static final BlockPos MAYOR_HOUSE_ORIGIN = new BlockPos(14784, 70, 14784);
    private static final BlockPos MAYOR_HOUSE_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 5);
    private static final BlockPos MAYOR_HOUSE_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 5);
    private static final BlockPos MAYOR_HOUSE_OUTDOOR_ENTRY_POS = new BlockPos(-197, -17, -22);
    private static final BlockPos MAYOR_HOUSE_OUTDOOR_EXIT_POS = new BlockPos(-197, -17, -23);

    private static final String TAG_PORTAL_MARKER_MAYOR_HOUSE_OUTSIDE = "sdv_portal_marker:mayor_house_outside";
    private static final String TAG_PORTAL_MARKER_MAYOR_HOUSE_INSIDE = "sdv_portal_marker:mayor_house_inside";

    private static final String CLINIC_STRUCTURE_PATH = "data/stardewcraft/structures/interior/clinic.schem";
    private static final BlockPos CLINIC_ORIGIN = new BlockPos(15360, 70, 15360);
    private static final BlockPos CLINIC_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 11);
    private static final BlockPos CLINIC_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 11);
    private static final BlockPos CLINIC_OUTDOOR_ENTRY_POS = new BlockPos(-146, -18, 60);
    private static final BlockPos CLINIC_OUTDOOR_EXIT_POS = new BlockPos(-145, -18, 59);

    private static final String TAG_PORTAL_MARKER_CLINIC_OUTSIDE = "sdv_portal_marker:clinic_outside";
    private static final String TAG_PORTAL_MARKER_CLINIC_INSIDE = "sdv_portal_marker:clinic_inside";

    private static final String RIVER_ROAD_1_STRUCTURE_PATH = "data/stardewcraft/structures/interior/1_river_road.schem";
    private static final BlockPos RIVER_ROAD_1_ORIGIN = new BlockPos(15936, 70, 15936);
    private static final BlockPos RIVER_ROAD_1_INDOOR_SPAWN_OFFSET = new BlockPos(3, 1, 12);
    private static final BlockPos RIVER_ROAD_1_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 12);
    private static final BlockPos RIVER_ROAD_1_OUTDOOR_ENTRY_POS = new BlockPos(-195, -18, 32);
    private static final BlockPos RIVER_ROAD_1_OUTDOOR_EXIT_POS = new BlockPos(-195, -18, 33);

    private static final String TAG_PORTAL_MARKER_RIVER_ROAD_1_OUTSIDE = "sdv_portal_marker:1_river_road_outside";
    private static final String TAG_PORTAL_MARKER_RIVER_ROAD_1_INSIDE = "sdv_portal_marker:1_river_road_inside";

    private static final String CARPENTER_SHOP_STRUCTURE_PATH = "data/stardewcraft/structures/interior/carpenter_shop.schem";
    private static final BlockPos CARPENTER_SHOP_ORIGIN = new BlockPos(16512, 70, 16512);
    private static final BlockPos CARPENTER_SHOP_INDOOR_SPAWN_OFFSET = new BlockPos(13, 5, 7);
    private static final BlockPos CARPENTER_SHOP_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(12, 5, 7);
    private static final BlockPos CARPENTER_SHOP_OUTDOOR_ENTRY_POS = new BlockPos(-213, -12, 219);
    private static final BlockPos CARPENTER_SHOP_OUTDOOR_EXIT_POS = new BlockPos(-213, -12, 218);

    private static final String TAG_PORTAL_MARKER_CARPENTER_SHOP_OUTSIDE = "sdv_portal_marker:carpenter_shop_outside";
    private static final String TAG_PORTAL_MARKER_CARPENTER_SHOP_INSIDE = "sdv_portal_marker:carpenter_shop_inside";

    private static final String WILLOW_LANE_1_STRUCTURE_PATH = "data/stardewcraft/structures/interior/1_willow_lane.schem";
    private static final BlockPos WILLOW_LANE_1_ORIGIN = new BlockPos(17088, 70, 17088);
    private static final BlockPos WILLOW_LANE_1_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 5);
    private static final BlockPos WILLOW_LANE_1_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 5);
    private static final BlockPos WILLOW_LANE_1_OUTDOOR_ENTRY_POS = new BlockPos(-85, -16, -25);
    private static final BlockPos WILLOW_LANE_1_OUTDOOR_EXIT_POS = new BlockPos(-85, -16, -26);

    private static final String TAG_PORTAL_MARKER_WILLOW_LANE_1_OUTSIDE = "sdv_portal_marker:1_willow_lane_outside";
    private static final String TAG_PORTAL_MARKER_WILLOW_LANE_1_INSIDE = "sdv_portal_marker:1_willow_lane_inside";

    private static final String WILLOW_LANE_2_STRUCTURE_PATH = "data/stardewcraft/structures/interior/2_willow_lane.schem";
    private static final BlockPos WILLOW_LANE_2_ORIGIN = new BlockPos(17088, 70, 17664);
    private static final BlockPos WILLOW_LANE_2_INDOOR_SPAWN_OFFSET = new BlockPos(4, 1, 2);
    private static final BlockPos WILLOW_LANE_2_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(3, 1, 2);
    private static final BlockPos WILLOW_LANE_2_OUTDOOR_ENTRY_POS = new BlockPos(-115, -17, -27);
    private static final BlockPos WILLOW_LANE_2_OUTDOOR_EXIT_POS = new BlockPos(-115, -17, -28);

    private static final String TAG_PORTAL_MARKER_WILLOW_LANE_2_OUTSIDE = "sdv_portal_marker:2_willow_lane_outside";
    private static final String TAG_PORTAL_MARKER_WILLOW_LANE_2_INSIDE = "sdv_portal_marker:2_willow_lane_inside";

    private static final String MARNIE_RANCH_STRUCTURE_PATH = "data/stardewcraft/structures/interior/marnie_ranch.schem";
    private static final BlockPos MARNIE_RANCH_ORIGIN = new BlockPos(17088, 70, 18240);
    private static final BlockPos MARNIE_RANCH_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 15);
    private static final BlockPos MARNIE_RANCH_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 15);
    private static final BlockPos MARNIE_RANCH_OUTDOOR_ENTRY_POS = new BlockPos(178, -14, -4);
    private static final BlockPos MARNIE_RANCH_OUTDOOR_EXIT_POS = new BlockPos(178, -14, -5);

    private static final String TAG_PORTAL_MARKER_MARNIE_RANCH_OUTSIDE = "sdv_portal_marker:marnie_ranch_outside";
    private static final String TAG_PORTAL_MARKER_MARNIE_RANCH_INSIDE = "sdv_portal_marker:marnie_ranch_inside";

    private static final String LEAH_COTTAGE_STRUCTURE_PATH = "data/stardewcraft/structures/interior/leah_cottage.schem";
    private static final BlockPos LEAH_COTTAGE_ORIGIN = new BlockPos(17088, 70, 18816);
    private static final BlockPos LEAH_COTTAGE_INDOOR_SPAWN_OFFSET = new BlockPos(6, 1, 7);
    private static final BlockPos LEAH_COTTAGE_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(4, 1, 7);
    private static final BlockPos LEAH_COTTAGE_OUTDOOR_ENTRY_POS = new BlockPos(155, -13, -58);
    private static final BlockPos LEAH_COTTAGE_OUTDOOR_EXIT_POS = new BlockPos(155, -13, -59);

    private static final String TAG_PORTAL_MARKER_LEAH_COTTAGE_OUTSIDE = "sdv_portal_marker:leah_cottage_outside";
    private static final String TAG_PORTAL_MARKER_LEAH_COTTAGE_INSIDE = "sdv_portal_marker:leah_cottage_inside";

    private static final String ADVENTURER_GUILD_STRUCTURE_PATH = "data/stardewcraft/structures/interior/adventurer_guild.schem";
    private static final BlockPos ADVENTURER_GUILD_ORIGIN = new BlockPos(17664, 70, 17088);
    private static final BlockPos ADVENTURER_GUILD_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 6);
    private static final BlockPos ADVENTURER_GUILD_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 6);
    private static final BlockPos ADVENTURER_GUILD_OUTDOOR_ENTRY_POS = new BlockPos(-335, -13, 312);
    private static final BlockPos ADVENTURER_GUILD_OUTDOOR_EXIT_POS = new BlockPos(-334, -13, 311);

    private static final String TAG_PORTAL_MARKER_ADVENTURER_GUILD_OUTSIDE = "sdv_portal_marker:adventurer_guild_outside";
    private static final String TAG_PORTAL_MARKER_ADVENTURER_GUILD_INSIDE = "sdv_portal_marker:adventurer_guild_inside";

    private static final String FISH_SHOP_STRUCTURE_PATH = "data/stardewcraft/structures/interior/fish_shop.schem";
    private static final BlockPos FISH_SHOP_ORIGIN = new BlockPos(17664, 70, 17664);
    private static final BlockPos FISH_SHOP_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 6);
    private static final BlockPos FISH_SHOP_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 6);
    private static final BlockPos FISH_SHOP_OUTDOOR_ENTRY_POS = new BlockPos(-238, -15, -211);
    private static final BlockPos FISH_SHOP_OUTDOOR_EXIT_POS = new BlockPos(-238, -15, -212);

    private static final String TAG_PORTAL_MARKER_FISH_SHOP_OUTSIDE = "sdv_portal_marker:fish_shop_outside";
    private static final String TAG_PORTAL_MARKER_FISH_SHOP_INSIDE = "sdv_portal_marker:fish_shop_inside";

    private static final String ELLIOTT_CABIN_STRUCTURE_PATH = "data/stardewcraft/structures/interior/elliott_cabin.schem";
    private static final BlockPos ELLIOTT_CABIN_ORIGIN = new BlockPos(17664, 70, 18240);
    private static final BlockPos ELLIOTT_CABIN_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 3);
    private static final BlockPos ELLIOTT_CABIN_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 3);
    private static final BlockPos ELLIOTT_CABIN_OUTDOOR_ENTRY_POS = new BlockPos(-267, -13, -152);
    private static final BlockPos ELLIOTT_CABIN_OUTDOOR_EXIT_POS = new BlockPos(-267, -13, -153);

    private static final String TAG_PORTAL_MARKER_ELLIOTT_CABIN_OUTSIDE = "sdv_portal_marker:elliott_cabin_outside";
    private static final String TAG_PORTAL_MARKER_ELLIOTT_CABIN_INSIDE = "sdv_portal_marker:elliott_cabin_inside";

    private static final String WIZARD_TOWER_STRUCTURE_PATH = "data/stardewcraft/structures/interior/wizard_tower.schem";
    private static final BlockPos WIZARD_TOWER_ORIGIN = new BlockPos(18240, 70, 17088);
    private static final BlockPos WIZARD_TOWER_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 9);
    private static final BlockPos WIZARD_TOWER_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 9);
    private static final BlockPos WIZARD_TOWER_OUTDOOR_ENTRY_POS = new BlockPos(340, -1, -42);
    private static final BlockPos WIZARD_TOWER_OUTDOOR_EXIT_POS = new BlockPos(340, -1, -43);

    private static final String TAG_PORTAL_MARKER_WIZARD_TOWER_OUTSIDE = "sdv_portal_marker:wizard_tower_outside";
    private static final String TAG_PORTAL_MARKER_WIZARD_TOWER_INSIDE = "sdv_portal_marker:wizard_tower_inside";
    private static final String TAG_PORTAL_MARKER_WIZARD_TOWER_RETURN_OVERWORLD = "sdv_portal_marker:wizard_tower_return_overworld";
    private static final BlockPos WIZARD_TOWER_RETURN_OVERWORLD_BASE = new BlockPos(18249, 71, 17100);

    // ---- 社区中心 ----
    static final String CC_RUINS_PATH = "data/stardewcraft/structures/interior/community_center_ruins.schem";
    @SuppressWarnings("unused")
    static final String CC_REFURBISHED_PATH = "data/stardewcraft/structures/interior/community_center_refurbished.schem";
    public static final BlockPos CC_ORIGIN = new BlockPos(18816, 69, 18816);
    /** schem 的 pos1（建造世界绝对坐标），用于偏移换算：relative = absolute - SCHEM_POS1 */
    public static final BlockPos CC_SCHEM_POS1 = new BlockPos(-146, 101, -1333);
    public static final BlockPos CC_INDOOR_SPAWN_OFFSET = new BlockPos(16, 1, 37);   // 玩家室内出生点 → 18832,70,18853
    public static final BlockPos CC_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(17, 1, 37); // 室内出口交互实体基点 → (18833,70,18853)~(18833,71,18854)
    /** CC 出门后传送目标（全体共享） */
    public static final BlockPos CC_OUTDOOR_EXIT_POS = new BlockPos(-190, -10, 138);
    private static final BlockPos CC_OUTDOOR_INTERACTION_BASE = new BlockPos(-191, -9, 141); // 室外入口交互实体基点

    private static final String TAG_PORTAL_MARKER_CC_OUTSIDE = "sdv_portal_marker:cc_outside";

    // ---- 温室 ----
    static final String GREENHOUSE_INTERIOR_PATH = "data/stardewcraft/structures/greenhouse/green_house_interior.schem";
    public static final BlockPos GREENHOUSE_INTERIOR_ORIGIN = new BlockPos(18816, 70, 19392);
    public static final BlockPos GREENHOUSE_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 10);
    static final BlockPos GREENHOUSE_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 10);

    private static final String TAG_PORTAL_MARKER_GREENHOUSE_OUTSIDE = "sdv_portal_marker:greenhouse_outside";

    // ---- 矿井入口（室外） ----
    private static final String TAG_PORTAL_MARKER_MINE_OUTSIDE = "sdv_portal_marker:mine_entrance";
    private static final BlockPos MINE_OUTDOOR_ENTRY_POS = new BlockPos(-287, -13, 314);

    private static final String DATA_NAME = "stardew_interior_subspace_layout";

    private static final List<FixedStructure> FIXED_STRUCTURES = new ArrayList<>();
    private static final int STRUCTURE_FORCE_RADIUS_CHUNKS = 3;
    private static boolean interiorChunksForced;

    static {
        // 用户指定：室内坐标都放在 X>10000, Z>10000，且只需保证互不重叠。
        register("pierre_house", PIERRE_HOUSE_STRUCTURE_PATH, PIERRE_HOUSE_ORIGIN.getX(), PIERRE_HOUSE_ORIGIN.getY(), PIERRE_HOUSE_ORIGIN.getZ());
        register("museum", MUSEUM_STRUCTURE_PATH, MUSEUM_ORIGIN.getX(), MUSEUM_ORIGIN.getY(), MUSEUM_ORIGIN.getZ());
        register("blacksmith", BLACKSMITH_STRUCTURE_PATH, BLACKSMITH_ORIGIN.getX(), BLACKSMITH_ORIGIN.getY(), BLACKSMITH_ORIGIN.getZ());
        register("saloon", SALOON_STRUCTURE_PATH, SALOON_ORIGIN.getX(), SALOON_ORIGIN.getY(), SALOON_ORIGIN.getZ());
        register("mayor_house", MAYOR_HOUSE_STRUCTURE_PATH, MAYOR_HOUSE_ORIGIN.getX(), MAYOR_HOUSE_ORIGIN.getY(), MAYOR_HOUSE_ORIGIN.getZ());
        register("clinic", CLINIC_STRUCTURE_PATH, CLINIC_ORIGIN.getX(), CLINIC_ORIGIN.getY(), CLINIC_ORIGIN.getZ());
        register("1_river_road", RIVER_ROAD_1_STRUCTURE_PATH, RIVER_ROAD_1_ORIGIN.getX(), RIVER_ROAD_1_ORIGIN.getY(), RIVER_ROAD_1_ORIGIN.getZ());
        register("carpenter_shop", CARPENTER_SHOP_STRUCTURE_PATH, CARPENTER_SHOP_ORIGIN.getX(), CARPENTER_SHOP_ORIGIN.getY(), CARPENTER_SHOP_ORIGIN.getZ());
        register("1_willow_lane", WILLOW_LANE_1_STRUCTURE_PATH, WILLOW_LANE_1_ORIGIN.getX(), WILLOW_LANE_1_ORIGIN.getY(), WILLOW_LANE_1_ORIGIN.getZ());
        register("2_willow_lane", WILLOW_LANE_2_STRUCTURE_PATH, WILLOW_LANE_2_ORIGIN.getX(), WILLOW_LANE_2_ORIGIN.getY(), WILLOW_LANE_2_ORIGIN.getZ());
        register("marnie_ranch", MARNIE_RANCH_STRUCTURE_PATH, MARNIE_RANCH_ORIGIN.getX(), MARNIE_RANCH_ORIGIN.getY(), MARNIE_RANCH_ORIGIN.getZ());
        register("leah_cottage", LEAH_COTTAGE_STRUCTURE_PATH, LEAH_COTTAGE_ORIGIN.getX(), LEAH_COTTAGE_ORIGIN.getY(), LEAH_COTTAGE_ORIGIN.getZ());
        register("adventurer_guild", ADVENTURER_GUILD_STRUCTURE_PATH, ADVENTURER_GUILD_ORIGIN.getX(), ADVENTURER_GUILD_ORIGIN.getY(), ADVENTURER_GUILD_ORIGIN.getZ());
        register("fish_shop", FISH_SHOP_STRUCTURE_PATH, FISH_SHOP_ORIGIN.getX(), FISH_SHOP_ORIGIN.getY(), FISH_SHOP_ORIGIN.getZ());
        register("elliott_cabin", ELLIOTT_CABIN_STRUCTURE_PATH, ELLIOTT_CABIN_ORIGIN.getX(), ELLIOTT_CABIN_ORIGIN.getY(), ELLIOTT_CABIN_ORIGIN.getZ());
        register("wizard_tower", WIZARD_TOWER_STRUCTURE_PATH, WIZARD_TOWER_ORIGIN.getX(), WIZARD_TOWER_ORIGIN.getY(), WIZARD_TOWER_ORIGIN.getZ());
        // CC 和温室不再注册为 FIXED_STRUCTURES — 由 PlayerInteriorAllocator 按玩家独立加载

        BlockPos indoorSpawn = PIERRE_HOUSE_ORIGIN.offset(PIERRE_INDOOR_SPAWN_OFFSET);
        BlockPos indoorExitPortal = PIERRE_HOUSE_ORIGIN.offset(PIERRE_INDOOR_EXIT_PORTAL_OFFSET);
        BlockPos museumIndoorSpawn = MUSEUM_ORIGIN.offset(MUSEUM_INDOOR_SPAWN_OFFSET);
        BlockPos museumIndoorExitPortal = MUSEUM_ORIGIN.offset(MUSEUM_INDOOR_EXIT_PORTAL_OFFSET);
        BlockPos blacksmithIndoorSpawn = BLACKSMITH_ORIGIN.offset(BLACKSMITH_INDOOR_SPAWN_OFFSET);
        BlockPos blacksmithIndoorExitPortal = BLACKSMITH_ORIGIN.offset(BLACKSMITH_INDOOR_EXIT_PORTAL_OFFSET);

        // 进室内：传送到 X+6,Y+1,Z+6，朝向正东。
        InteriorPortalRegistry.register(
            "pierre_house_enter",
            new InteriorPortalRegistry.PortalTarget(
                indoorSpawn.getX() + 0.5D,
                indoorSpawn.getY(),
                indoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出室内：回到固定室外点 -159 -18 54，朝向正北。
        InteriorPortalRegistry.register(
            "pierre_house_exit",
            new InteriorPortalRegistry.PortalTarget(
                PIERRE_OUTDOOR_ENTRY_POS.getX(),
                PIERRE_OUTDOOR_ENTRY_POS.getY(),
                PIERRE_OUTDOOR_ENTRY_POS.getZ(),
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        // 进博物馆：传送到 X+10,Y+1,Z+5，朝向正东。
        InteriorPortalRegistry.register(
            "museum_enter",
            new InteriorPortalRegistry.PortalTarget(
                museumIndoorSpawn.getX() + 0.5D,
                museumIndoorSpawn.getY(),
                museumIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出博物馆：回到室外 -308 -17 -37，朝向正北。
        InteriorPortalRegistry.register(
            "museum_exit",
            new InteriorPortalRegistry.PortalTarget(
                MUSEUM_OUTDOOR_EXIT_POS.getX(),
                MUSEUM_OUTDOOR_EXIT_POS.getY(),
                MUSEUM_OUTDOOR_EXIT_POS.getZ(),
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        // 进铁匠铺：传送到 X+3,Y+1,Z+8，朝向正东。
        InteriorPortalRegistry.register(
            "blacksmith_enter",
            new InteriorPortalRegistry.PortalTarget(
                blacksmithIndoorSpawn.getX() + 0.5D,
                blacksmithIndoorSpawn.getY(),
                blacksmithIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出铁匠铺：回到室外 -288 -18 -17，朝向正北。
        InteriorPortalRegistry.register(
            "blacksmith_exit",
            new InteriorPortalRegistry.PortalTarget(
                BLACKSMITH_OUTDOOR_EXIT_POS.getX(),
                BLACKSMITH_OUTDOOR_EXIT_POS.getY(),
                BLACKSMITH_OUTDOOR_EXIT_POS.getZ(),
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        BlockPos saloonIndoorSpawn = SALOON_ORIGIN.offset(SALOON_INDOOR_SPAWN_OFFSET);
        BlockPos saloonIndoorExitPortal = SALOON_ORIGIN.offset(SALOON_INDOOR_EXIT_PORTAL_OFFSET);

        // 进酒馆：传送到 X+2,Y+1,Z+17，朝向正东。
        InteriorPortalRegistry.register(
            "saloon_enter",
            new InteriorPortalRegistry.PortalTarget(
                saloonIndoorSpawn.getX() + 0.5D,
                saloonIndoorSpawn.getY(),
                saloonIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出酒馆：回到室外 -164 -17 14，朝向正北。
        InteriorPortalRegistry.register(
            "saloon_exit",
            new InteriorPortalRegistry.PortalTarget(
                SALOON_OUTDOOR_EXIT_POS.getX() + 0.5D,
                SALOON_OUTDOOR_EXIT_POS.getY(),
                SALOON_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        // 室内出口交互体固定在结构相对坐标。
        StardewCraft.LOGGER.info("[INTERIOR] Pierre indoor exit interaction anchor = {}", indoorExitPortal);
        StardewCraft.LOGGER.info("[INTERIOR] Museum indoor exit interaction anchor = {}", museumIndoorExitPortal);
        StardewCraft.LOGGER.info("[INTERIOR] Blacksmith indoor exit interaction anchor = {}", blacksmithIndoorExitPortal);
        StardewCraft.LOGGER.info("[INTERIOR] Saloon indoor exit interaction anchor = {}", saloonIndoorExitPortal);

        BlockPos mayorHouseIndoorSpawn = MAYOR_HOUSE_ORIGIN.offset(MAYOR_HOUSE_INDOOR_SPAWN_OFFSET);
        BlockPos mayorHouseIndoorExitPortal = MAYOR_HOUSE_ORIGIN.offset(MAYOR_HOUSE_INDOOR_EXIT_PORTAL_OFFSET);

        // 进市长家：传送到 X+2,Y+1,Z+5，朝向正东。
        InteriorPortalRegistry.register(
            "mayor_house_enter",
            new InteriorPortalRegistry.PortalTarget(
                mayorHouseIndoorSpawn.getX() + 0.5D,
                mayorHouseIndoorSpawn.getY(),
                mayorHouseIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出市长家：回到室外 -197 -17 -23，朝向正北。
        InteriorPortalRegistry.register(
            "mayor_house_exit",
            new InteriorPortalRegistry.PortalTarget(
                MAYOR_HOUSE_OUTDOOR_EXIT_POS.getX() + 0.5D,
                MAYOR_HOUSE_OUTDOOR_EXIT_POS.getY(),
                MAYOR_HOUSE_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] Mayor house indoor exit interaction anchor = {}", mayorHouseIndoorExitPortal);

        BlockPos clinicIndoorSpawn = CLINIC_ORIGIN.offset(CLINIC_INDOOR_SPAWN_OFFSET);
        BlockPos clinicIndoorExitPortal = CLINIC_ORIGIN.offset(CLINIC_INDOOR_EXIT_PORTAL_OFFSET);

        // 进诊所：传送到 X+2,Y+1,Z+11，朝向正东。
        InteriorPortalRegistry.register(
            "clinic_enter",
            new InteriorPortalRegistry.PortalTarget(
                clinicIndoorSpawn.getX() + 0.5D,
                clinicIndoorSpawn.getY(),
                clinicIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出诊所：回到室外 -145 -18 59，朝向正北。
        InteriorPortalRegistry.register(
            "clinic_exit",
            new InteriorPortalRegistry.PortalTarget(
                CLINIC_OUTDOOR_EXIT_POS.getX() + 0.5D,
                CLINIC_OUTDOOR_EXIT_POS.getY(),
                CLINIC_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] Clinic indoor exit interaction anchor = {}", clinicIndoorExitPortal);

        BlockPos riverRoad1IndoorSpawn = RIVER_ROAD_1_ORIGIN.offset(RIVER_ROAD_1_INDOOR_SPAWN_OFFSET);
        BlockPos riverRoad1IndoorExitPortal = RIVER_ROAD_1_ORIGIN.offset(RIVER_ROAD_1_INDOOR_EXIT_PORTAL_OFFSET);

        // 进1号河路：传送到 X+3,Y+1,Z+12，朝向正东。
        InteriorPortalRegistry.register(
            "1_river_road_enter",
            new InteriorPortalRegistry.PortalTarget(
                riverRoad1IndoorSpawn.getX() + 0.5D,
                riverRoad1IndoorSpawn.getY(),
                riverRoad1IndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出1号河路：回到室外 -195 -18 33，朝向正北。
        InteriorPortalRegistry.register(
            "1_river_road_exit",
            new InteriorPortalRegistry.PortalTarget(
                RIVER_ROAD_1_OUTDOOR_EXIT_POS.getX() + 0.5D,
                RIVER_ROAD_1_OUTDOOR_EXIT_POS.getY(),
                RIVER_ROAD_1_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] 1_river_road indoor exit interaction anchor = {}", riverRoad1IndoorExitPortal);

        BlockPos carpenterShopIndoorSpawn = CARPENTER_SHOP_ORIGIN.offset(CARPENTER_SHOP_INDOOR_SPAWN_OFFSET);
        BlockPos carpenterShopIndoorExitPortal = CARPENTER_SHOP_ORIGIN.offset(CARPENTER_SHOP_INDOOR_EXIT_PORTAL_OFFSET);

        // 进木匠铺：传送到 X+13,Y+5,Z+7，朝向正东。
        InteriorPortalRegistry.register(
            "carpenter_shop_enter",
            new InteriorPortalRegistry.PortalTarget(
                carpenterShopIndoorSpawn.getX() + 0.5D,
                carpenterShopIndoorSpawn.getY(),
                carpenterShopIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出木匠铺：回到室外 -213 -12 218，朝向正北。
        InteriorPortalRegistry.register(
            "carpenter_shop_exit",
            new InteriorPortalRegistry.PortalTarget(
                CARPENTER_SHOP_OUTDOOR_EXIT_POS.getX() + 0.5D,
                CARPENTER_SHOP_OUTDOOR_EXIT_POS.getY(),
                CARPENTER_SHOP_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] Carpenter shop indoor exit interaction anchor = {}", carpenterShopIndoorExitPortal);

        BlockPos willowLane1IndoorSpawn = WILLOW_LANE_1_ORIGIN.offset(WILLOW_LANE_1_INDOOR_SPAWN_OFFSET);
        BlockPos willowLane1IndoorExitPortal = WILLOW_LANE_1_ORIGIN.offset(WILLOW_LANE_1_INDOOR_EXIT_PORTAL_OFFSET);

        // 进1号柳巷：传送到 X+2,Y+1,Z+5，朝向正东。
        InteriorPortalRegistry.register(
            "1_willow_lane_enter",
            new InteriorPortalRegistry.PortalTarget(
                willowLane1IndoorSpawn.getX() + 0.5D,
                willowLane1IndoorSpawn.getY(),
                willowLane1IndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出1号柳巷：回到室外 -85 -16 -26，朝向正北。
        InteriorPortalRegistry.register(
            "1_willow_lane_exit",
            new InteriorPortalRegistry.PortalTarget(
                WILLOW_LANE_1_OUTDOOR_EXIT_POS.getX() + 0.5D,
                WILLOW_LANE_1_OUTDOOR_EXIT_POS.getY(),
                WILLOW_LANE_1_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] 1_willow_lane indoor exit interaction anchor = {}", willowLane1IndoorExitPortal);

        BlockPos willowLane2IndoorSpawn = WILLOW_LANE_2_ORIGIN.offset(WILLOW_LANE_2_INDOOR_SPAWN_OFFSET);
        BlockPos willowLane2IndoorExitPortal = WILLOW_LANE_2_ORIGIN.offset(WILLOW_LANE_2_INDOOR_EXIT_PORTAL_OFFSET);

        // 进2号柳巷：传送到 X+4,Y+1,Z+2，朝向正东。
        InteriorPortalRegistry.register(
            "2_willow_lane_enter",
            new InteriorPortalRegistry.PortalTarget(
                willowLane2IndoorSpawn.getX() + 0.5D,
                willowLane2IndoorSpawn.getY(),
                willowLane2IndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出2号柳巷：回到室外 -115 -17 -28，朝向正北。
        InteriorPortalRegistry.register(
            "2_willow_lane_exit",
            new InteriorPortalRegistry.PortalTarget(
                WILLOW_LANE_2_OUTDOOR_EXIT_POS.getX() + 0.5D,
                WILLOW_LANE_2_OUTDOOR_EXIT_POS.getY(),
                WILLOW_LANE_2_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] 2_willow_lane indoor exit interaction anchor = {}", willowLane2IndoorExitPortal);

        BlockPos marnieRanchIndoorSpawn = MARNIE_RANCH_ORIGIN.offset(MARNIE_RANCH_INDOOR_SPAWN_OFFSET);
        BlockPos marnieRanchIndoorExitPortal = MARNIE_RANCH_ORIGIN.offset(MARNIE_RANCH_INDOOR_EXIT_PORTAL_OFFSET);

        // 进马尼牧场：传送到 Z+15,X+2,Y+1，面朝正东。
        InteriorPortalRegistry.register(
            "marnie_ranch_enter",
            new InteriorPortalRegistry.PortalTarget(
                marnieRanchIndoorSpawn.getX() + 0.5D,
                marnieRanchIndoorSpawn.getY(),
                marnieRanchIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出马尼牧场：回到室外 178 -14 -5，面朝正北。
        InteriorPortalRegistry.register(
            "marnie_ranch_exit",
            new InteriorPortalRegistry.PortalTarget(
                MARNIE_RANCH_OUTDOOR_EXIT_POS.getX() + 0.5D,
                MARNIE_RANCH_OUTDOOR_EXIT_POS.getY(),
                MARNIE_RANCH_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] marnie_ranch indoor exit interaction anchor = {}", marnieRanchIndoorExitPortal);

        BlockPos leahCottageIndoorSpawn = LEAH_COTTAGE_ORIGIN.offset(LEAH_COTTAGE_INDOOR_SPAWN_OFFSET);
        BlockPos leahCottageIndoorExitPortal = LEAH_COTTAGE_ORIGIN.offset(LEAH_COTTAGE_INDOOR_EXIT_PORTAL_OFFSET);

        // 进莉亚小屋：传送到 X+6,Y+1,Z+7，面朝正东。
        InteriorPortalRegistry.register(
            "leah_cottage_enter",
            new InteriorPortalRegistry.PortalTarget(
                leahCottageIndoorSpawn.getX() + 0.5D,
                leahCottageIndoorSpawn.getY(),
                leahCottageIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出莉亚小屋：回到室外 155 -13 -59，面朝正北。
        InteriorPortalRegistry.register(
            "leah_cottage_exit",
            new InteriorPortalRegistry.PortalTarget(
                LEAH_COTTAGE_OUTDOOR_EXIT_POS.getX() + 0.5D,
                LEAH_COTTAGE_OUTDOOR_EXIT_POS.getY(),
                LEAH_COTTAGE_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] leah_cottage indoor exit interaction anchor = {}", leahCottageIndoorExitPortal);

        BlockPos adventurerGuildIndoorSpawn = ADVENTURER_GUILD_ORIGIN.offset(ADVENTURER_GUILD_INDOOR_SPAWN_OFFSET);
        BlockPos adventurerGuildIndoorExitPortal = ADVENTURER_GUILD_ORIGIN.offset(ADVENTURER_GUILD_INDOOR_EXIT_PORTAL_OFFSET);

        // 进冒险家公会：传送到 X+2,Y+1,Z+6，面朝正东。
        InteriorPortalRegistry.register(
            "adventurer_guild_enter",
            new InteriorPortalRegistry.PortalTarget(
                adventurerGuildIndoorSpawn.getX() + 0.5D,
                adventurerGuildIndoorSpawn.getY(),
                adventurerGuildIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出冒险家公会：回到室外 -334 -13 311，面朝正北。
        InteriorPortalRegistry.register(
            "adventurer_guild_exit",
            new InteriorPortalRegistry.PortalTarget(
                ADVENTURER_GUILD_OUTDOOR_EXIT_POS.getX() + 0.5D,
                ADVENTURER_GUILD_OUTDOOR_EXIT_POS.getY(),
                ADVENTURER_GUILD_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] adventurer_guild indoor exit interaction anchor = {}", adventurerGuildIndoorExitPortal);

        BlockPos fishShopIndoorSpawn = FISH_SHOP_ORIGIN.offset(FISH_SHOP_INDOOR_SPAWN_OFFSET);
        BlockPos fishShopIndoorExitPortal = FISH_SHOP_ORIGIN.offset(FISH_SHOP_INDOOR_EXIT_PORTAL_OFFSET);

        // 进鱼店：传送到 X+2,Y+1,Z+6，面朝正东。
        InteriorPortalRegistry.register(
            "fish_shop_enter",
            new InteriorPortalRegistry.PortalTarget(
                fishShopIndoorSpawn.getX() + 0.5D,
                fishShopIndoorSpawn.getY(),
                fishShopIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出鱼店：回到室外 -238 -15 -212，面朝正北。
        InteriorPortalRegistry.register(
            "fish_shop_exit",
            new InteriorPortalRegistry.PortalTarget(
                FISH_SHOP_OUTDOOR_EXIT_POS.getX() + 0.5D,
                FISH_SHOP_OUTDOOR_EXIT_POS.getY(),
                FISH_SHOP_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] fish_shop indoor exit interaction anchor = {}", fishShopIndoorExitPortal);

        BlockPos elliottCabinIndoorSpawn = ELLIOTT_CABIN_ORIGIN.offset(ELLIOTT_CABIN_INDOOR_SPAWN_OFFSET);
        BlockPos elliottCabinIndoorExitPortal = ELLIOTT_CABIN_ORIGIN.offset(ELLIOTT_CABIN_INDOOR_EXIT_PORTAL_OFFSET);

        // 进艾利欧特小屋：传送到 X+2,Y+1,Z+3，面朝正东。
        InteriorPortalRegistry.register(
            "elliott_cabin_enter",
            new InteriorPortalRegistry.PortalTarget(
                elliottCabinIndoorSpawn.getX() + 0.5D,
                elliottCabinIndoorSpawn.getY(),
                elliottCabinIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出艾利欧特小屋：回到室外 -267 -13 -153，面朝正北。
        InteriorPortalRegistry.register(
            "elliott_cabin_exit",
            new InteriorPortalRegistry.PortalTarget(
                ELLIOTT_CABIN_OUTDOOR_EXIT_POS.getX() + 0.5D,
                ELLIOTT_CABIN_OUTDOOR_EXIT_POS.getY(),
                ELLIOTT_CABIN_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] elliott_cabin indoor exit interaction anchor = {}", elliottCabinIndoorExitPortal);

        BlockPos wizardTowerIndoorSpawn = WIZARD_TOWER_ORIGIN.offset(WIZARD_TOWER_INDOOR_SPAWN_OFFSET);
        BlockPos wizardTowerIndoorExitPortal = WIZARD_TOWER_ORIGIN.offset(WIZARD_TOWER_INDOOR_EXIT_PORTAL_OFFSET);

        // 进巫师塔：传送到 X+2,Y+1,Z+9，面朝正东。
        InteriorPortalRegistry.register(
            "wizard_tower_enter",
            new InteriorPortalRegistry.PortalTarget(
                wizardTowerIndoorSpawn.getX() + 0.5D,
                wizardTowerIndoorSpawn.getY(),
                wizardTowerIndoorSpawn.getZ() + 0.5D,
                -90.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.ENTRANCE
            )
        );

        // 出巫师塔：回到室外 340 -1 -43，面朝正北。
        InteriorPortalRegistry.register(
            "wizard_tower_exit",
            new InteriorPortalRegistry.PortalTarget(
                WIZARD_TOWER_OUTDOOR_EXIT_POS.getX() + 0.5D,
                WIZARD_TOWER_OUTDOOR_EXIT_POS.getY(),
                WIZARD_TOWER_OUTDOOR_EXIT_POS.getZ() + 0.5D,
                180.0F,
                0.0F,
                InteriorPortalRegistry.PortalMode.EXIT
            )
        );

        StardewCraft.LOGGER.info("[INTERIOR] wizard_tower indoor exit interaction anchor = {}", wizardTowerIndoorExitPortal);

        // CC 和温室门户不再静态注册 — 由 InteriorPortalInteractionEvents 动态解析
    }

    public static void register(String id, String structurePath, int x, int y, int z) {
        FIXED_STRUCTURES.add(new FixedStructure(id, structurePath, new BlockPos(x, y, z)));
    }

    public static List<FixedStructure> allStructures() {
        return Collections.unmodifiableList(FIXED_STRUCTURES);
    }

    public static boolean isInteriorRegion(ServerLevel level, BlockPos pos) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return false;
        }

        return pos.getX() >= REGION_MIN_X
            && pos.getX() <= REGION_MAX_X
            && pos.getZ() >= REGION_MIN_Z
            && pos.getZ() <= REGION_MAX_Z;
    }

    public static boolean isLayoutInitialized(ServerLevel level) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return false;
        }
        InteriorSubspaceSavedData data = InteriorSubspaceSavedData.get(level);
        return data.layoutVersion == LAYOUT_VERSION && data.initialized;
    }

    // ──────────────── 分批放置状态 ────────────────
    /** 是否正在分批放置中 */
    private static boolean batchPlacementInProgress = false;
    /** 下一个要放置的结构索引 */
    private static int batchPlacementIndex = 0;
    /** 当前结构的 chunk 是否已全部加载 */
    private static boolean currentChunksReady = false;
    /** 当前结构已强制加载的 chunk 集合（仅用于 Phase 2 检查） */
    private static final Set<ChunkPos> currentForcedChunks = new HashSet<>();
    /** 整个批量过程中所有强制加载过的 chunk（放置结束后逐步释放） */
    private static final Set<ChunkPos> allBatchForcedChunks = new HashSet<>();
    /** 等待 chunk 加载的 tick 计数 */
    private static int chunkWaitTicks = 0;
    /** chunk 加载超时上限（200 tick = 10 秒） */
    private static final int MAX_CHUNK_WAIT_TICKS = 200;
    /** 批量完成后是否正在逐步释放 chunk */
    private static boolean gradualReleaseInProgress = false;
    /** 逐步释放用的列表 */
    private static final List<ChunkPos> chunksToRelease = new ArrayList<>();
    /** 每批释放的 chunk 数量 */
    private static final int CHUNKS_RELEASE_PER_BATCH = 2;
    /** 每隔多少 tick 释放一批（给 IO 线程喘息时间） */
    private static final int RELEASE_TICK_INTERVAL = 5;
    /** 结构放置完成后延迟多少 tick 再开始释放（等 IO 线程把结构数据写完） */
    private static final int RELEASE_DELAY_TICKS = 100;
    /** 释放 tick 计数器 */
    private static int releaseTickCounter = 0;

    /**
     * 每 tick 调用一次（由 InteriorSubspaceLifecycleEvents.onLevelTick 调用）。
     * 分阶段工作：
     * Phase 1: 非阻塞 force-load 当前结构所需的 chunk
     * Phase 2: 等待 chunk 全部就绪
     * Phase 3: 放置结构（不释放 chunk，避免 unload 队列压力）
     * Phase 4: 所有结构完成后，等待 IO 冷却，再逐步释放 chunk
     */
    public static void tickBatchPlacement(ServerLevel level) {
        // ── Phase 4: 逐步释放 chunk ──
        if (gradualReleaseInProgress) {
            releaseTickCounter++;
            // 延迟阶段：等 IO 线程把结构数据写完再开始释放
            if (releaseTickCounter <= RELEASE_DELAY_TICKS) {
                return;
            }
            // 每隔 RELEASE_TICK_INTERVAL tick 才释放一批
            if ((releaseTickCounter - RELEASE_DELAY_TICKS) % RELEASE_TICK_INTERVAL != 0) {
                return;
            }
            int toRelease = Math.min(CHUNKS_RELEASE_PER_BATCH, chunksToRelease.size());
            for (int i = 0; i < toRelease; i++) {
                ChunkPos cp = chunksToRelease.remove(chunksToRelease.size() - 1);
                level.setChunkForced(cp.x, cp.z, false);
            }
            if (chunksToRelease.isEmpty()) {
                gradualReleaseInProgress = false;
                releaseTickCounter = 0;
                StardewCraft.LOGGER.info("[INTERIOR] Finished gradual chunk release");
            }
            return;
        }

        if (!batchPlacementInProgress) {
            return;
        }
        if (batchPlacementIndex >= FIXED_STRUCTURES.size()) {
            // 所有建筑放置完成，执行后续初始化
            batchPlacementInProgress = false;
            batchPlacementIndex = 0;
            resetChunkPreloadState();

            ensurePortalInteractions(level);
            migrateFarmAndGreenhousePortals(level);
            PlayerInteriorAllocator.get(level).reloadAllPlaced(level);
            restoreMuseumExhibitStands(level);

            InteriorSubspaceSavedData data = InteriorSubspaceSavedData.get(level);
            data.layoutVersion = LAYOUT_VERSION;
            data.initialized = true;
            data.setDirty();

            StardewCraft.LOGGER.info("[INTERIOR] Interior subspace load complete (batched). version={}", LAYOUT_VERSION);

            // 启动逐步释放
            if (!allBatchForcedChunks.isEmpty()) {
                chunksToRelease.addAll(allBatchForcedChunks);
                allBatchForcedChunks.clear();
                gradualReleaseInProgress = true;
                StardewCraft.LOGGER.info("[INTERIOR] Starting gradual chunk release: {} chunks", chunksToRelease.size());
            }
            return;
        }

        FixedStructure structure = FIXED_STRUCTURES.get(batchPlacementIndex);

        // ── Phase 1: 非阻塞地 force-load 当前结构所需的 chunk ──
        if (currentForcedChunks.isEmpty()) {
            BlockPos origin = structure.origin();
            int cxCenter = origin.getX() >> 4;
            int czCenter = origin.getZ() >> 4;
            // 结构最大 ~73 格 ≈ 5 chunk，向正方向多加载几个 chunk
            for (int dx = -1; dx <= 5; dx++) {
                for (int dz = -1; dz <= 5; dz++) {
                    ChunkPos cp = new ChunkPos(cxCenter + dx, czCenter + dz);
                    currentForcedChunks.add(cp);
                    if (allBatchForcedChunks.add(cp)) {
                        // 只对新 chunk 设置 force（避免重复）
                        level.setChunkForced(cp.x, cp.z, true);
                    }
                }
            }
            chunkWaitTicks = 0;
            currentChunksReady = false;
            return; // 等下个 tick 检查
        }

        // ── Phase 2: 等待所有 chunk 加载完成（非阻塞检查） ──
        if (!currentChunksReady) {
            chunkWaitTicks++;
            boolean allLoaded = true;
            for (ChunkPos cp : currentForcedChunks) {
                if (level.getChunkSource().getChunkNow(cp.x, cp.z) == null) {
                    allLoaded = false;
                    break;
                }
            }
            if (!allLoaded) {
                if (chunkWaitTicks < MAX_CHUNK_WAIT_TICKS) {
                    return; // 继续等待
                }
                // 超时仍未加载 → 中止批量放置，启动逐步释放
                StardewCraft.LOGGER.error("[INTERIOR] Chunk preload timeout for {} after {} ticks, aborting batch",
                        structure.id(), chunkWaitTicks);
                batchPlacementInProgress = false;
                batchPlacementIndex = 0;
                resetChunkPreloadState();
                if (!allBatchForcedChunks.isEmpty()) {
                    chunksToRelease.addAll(allBatchForcedChunks);
                    allBatchForcedChunks.clear();
                    gradualReleaseInProgress = true;
                }
                return;
            }
            currentChunksReady = true;
            // 不 return，本 tick 直接放置
        }

        // ── Phase 3: chunk 已就绪，放置结构（不释放 chunk） ──
        boolean placed = StructureLoader.loadAndPlaceWithResult(level, structure.structurePath(), structure.origin());

        // 清理当前结构状态，但不释放 chunk（留到最后逐步释放）
        currentForcedChunks.clear();
        currentChunksReady = false;

        if (!placed) {
            StardewCraft.LOGGER.error("[INTERIOR] Failed to place structure {} at {}, will retry next load",
                    structure.id(), structure.origin());
            batchPlacementInProgress = false;
            batchPlacementIndex = 0;
            resetChunkPreloadState();
            if (!allBatchForcedChunks.isEmpty()) {
                chunksToRelease.addAll(allBatchForcedChunks);
                allBatchForcedChunks.clear();
                gradualReleaseInProgress = true;
            }
            return;
        }
        batchPlacementIndex++;
    }

    private static void resetChunkPreloadState() {
        currentForcedChunks.clear();
        currentChunksReady = false;
        chunkWaitTicks = 0;
    }

    /** 分批放置（包括逐步释放 chunk）是否正在进行中 */
    public static boolean isBatchPlacementInProgress() {
        return batchPlacementInProgress || gradualReleaseInProgress;
    }

    public static void ensureLoaded(ServerLevel level, String reason) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }

        InteriorSubspaceSavedData data = InteriorSubspaceSavedData.get(level);
        if (data.layoutVersion == LAYOUT_VERSION && data.initialized) {
            return;
        }

        // 如果已经在分批放置中，不要重复启动
        if (batchPlacementInProgress) {
            return;
        }

        StardewCraft.LOGGER.info("[INTERIOR] Starting batched interior structure placement. reason={}, version={}, count={}",
            reason, LAYOUT_VERSION, FIXED_STRUCTURES.size());

        batchPlacementInProgress = true;
        batchPlacementIndex = 0;
    }

    public static void forceReload(ServerLevel level, String reason) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }

        InteriorSubspaceSavedData data = InteriorSubspaceSavedData.get(level);
        data.initialized = false;
        data.layoutVersion = 0;
        data.setDirty();
        ensureLoaded(level, reason);
    }

    public static void setInteriorChunksForced(ServerLevel level, boolean force, String reason) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }
        if (interiorChunksForced == force) {
            return;
        }

        for (FixedStructure structure : FIXED_STRUCTURES) {
            int centerChunkX = structure.origin().getX() >> 4;
            int centerChunkZ = structure.origin().getZ() >> 4;
            for (int dz = -STRUCTURE_FORCE_RADIUS_CHUNKS; dz <= STRUCTURE_FORCE_RADIUS_CHUNKS; dz++) {
                for (int dx = -STRUCTURE_FORCE_RADIUS_CHUNKS; dx <= STRUCTURE_FORCE_RADIUS_CHUNKS; dx++) {
                    int chunkX = centerChunkX + dx;
                    int chunkZ = centerChunkZ + dz;
                    level.setChunkForced(chunkX, chunkZ, force);
                }
            }
        }

        interiorChunksForced = force;
        StardewCraft.LOGGER.info("[INTERIOR] Interior structure chunk forcing toggled: {} (reason={})", force, reason);
    }

    /**
     * 布局重建后，从 MuseumDonationData 中恢复展示柜的陈列物品。
     * standKey 格式: "dimension|x,y,z"，解析出 BlockPos 后对展示柜方块实体还原 displayItem。
     */
    private static void restoreMuseumExhibitStands(ServerLevel level) {
        com.stardew.craft.museum.MuseumDonationData data = com.stardew.craft.museum.MuseumDonationData.get(level);
        java.util.Map<String, String> stands = data.getStandDisplayItems();
        if (stands.isEmpty()) return;

        String dimPrefix = level.dimension().location().toString() + "|";
        int restored = 0;
        for (java.util.Map.Entry<String, String> entry : stands.entrySet()) {
            String key = entry.getKey();
            String itemId = entry.getValue();
            if (key == null || itemId == null || itemId.isBlank()) continue;
            if (!key.startsWith(dimPrefix)) continue;

            // 解析 "x,y,z" 部分
            String coordPart = key.substring(dimPrefix.length());
            String[] parts = coordPart.split(",");
            if (parts.length != 3) continue;

            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int z = Integer.parseInt(parts[2]);
                BlockPos pos = new BlockPos(x, y, z);

                net.minecraft.world.level.block.entity.BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof com.stardew.craft.blockentity.MuseumExhibitStandBlockEntity stand) {
                    net.minecraft.resources.ResourceLocation rl = net.minecraft.resources.ResourceLocation.tryParse(itemId);
                    if (rl != null) {
                        net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(rl);
                        if (item != null && item != net.minecraft.world.item.Items.AIR) {
                            stand.setDisplayItem(new net.minecraft.world.item.ItemStack(item));
                            restored++;
                        }
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        if (restored > 0) {
            StardewCraft.LOGGER.info("[INTERIOR] Restored {} museum exhibit stand(s) after layout rebuild", restored);
        }
    }

    public record FixedStructure(
        String id,
        String structurePath,
        BlockPos origin
    ) {}

    private static void ensurePortalInteractions(ServerLevel level) {
        BlockPos indoorExitPortal = PIERRE_HOUSE_ORIGIN.offset(PIERRE_INDOOR_EXIT_PORTAL_OFFSET);
        BlockPos museumIndoorExitPortal = MUSEUM_ORIGIN.offset(MUSEUM_INDOOR_EXIT_PORTAL_OFFSET);
        BlockPos blacksmithIndoorExitPortal = BLACKSMITH_ORIGIN.offset(BLACKSMITH_INDOOR_EXIT_PORTAL_OFFSET);
        BlockPos saloonIndoorExitPortal = SALOON_ORIGIN.offset(SALOON_INDOOR_EXIT_PORTAL_OFFSET);
        BlockPos mayorHouseIndoorExitPortal = MAYOR_HOUSE_ORIGIN.offset(MAYOR_HOUSE_INDOOR_EXIT_PORTAL_OFFSET);
        BlockPos clinicIndoorExitPortal = CLINIC_ORIGIN.offset(CLINIC_INDOOR_EXIT_PORTAL_OFFSET);

        // 室外入口：固定点，3 宽 x 3 高。(-160,-18,54) → (-158,-16,54)
        placePortalTriggerArea(level, PIERRE_OUTDOOR_INTERACTION_BASE, 3, 3, 1,
            TAG_PORTAL_MARKER_OUTSIDE, "sdv_portal_target:pierre_house_enter");

        // 室内出口：结构相对点，2 高 x 1 宽。
        placePortalTriggerArea(level, indoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_INSIDE, "sdv_portal_target:pierre_house_exit");

        // 博物馆室外入口：2 高 x 2 宽。
        placePortalTriggerArea(level, MUSEUM_OUTDOOR_ENTRY_POS, 2, 2, 1,
            TAG_PORTAL_MARKER_MUSEUM_OUTSIDE, "sdv_portal_target:museum_enter");

        // 博物馆室内出口：2 高 x 1 宽。
        placePortalTriggerArea(level, museumIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_MUSEUM_INSIDE, "sdv_portal_target:museum_exit");

        // 铁匠铺室外入口：2 高 x 1 宽。
        placePortalTriggerArea(level, BLACKSMITH_OUTDOOR_ENTRY_POS, 2, 1, 1,
            TAG_PORTAL_MARKER_BLACKSMITH_OUTSIDE, "sdv_portal_target:blacksmith_enter");

        // 铁匠铺室内出口：2 高 x 1 宽。
        placePortalTriggerArea(level, blacksmithIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_BLACKSMITH_INSIDE, "sdv_portal_target:blacksmith_exit");

        // 酒馆室外入口：2 高 x 2 宽。
        placePortalTriggerArea(level, SALOON_OUTDOOR_ENTRY_POS, 2, 2, 1,
            TAG_PORTAL_MARKER_SALOON_OUTSIDE, "sdv_portal_target:saloon_enter");

        // 酒馆室内出口：2 高 x 1 宽。
        placePortalTriggerArea(level, saloonIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_SALOON_INSIDE, "sdv_portal_target:saloon_exit");

        // 市长家室外入口：2 高 x 2 宽。
        placePortalTriggerArea(level, MAYOR_HOUSE_OUTDOOR_ENTRY_POS, 2, 2, 1,
            TAG_PORTAL_MARKER_MAYOR_HOUSE_OUTSIDE, "sdv_portal_target:mayor_house_enter");

        // 市长家室内出口：2 高 x 1 宽 x 2 深。
        placePortalTriggerArea(level, mayorHouseIndoorExitPortal, 2, 1, 2,
            TAG_PORTAL_MARKER_MAYOR_HOUSE_INSIDE, "sdv_portal_target:mayor_house_exit");

        // 诊所室外入口：2 高 x 3 宽。
        placePortalTriggerArea(level, CLINIC_OUTDOOR_ENTRY_POS, 2, 3, 1,
            TAG_PORTAL_MARKER_CLINIC_OUTSIDE, "sdv_portal_target:clinic_enter");

        // 诊所室内出口：2 高 x 1 宽。
        placePortalTriggerArea(level, clinicIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_CLINIC_INSIDE, "sdv_portal_target:clinic_exit");

        BlockPos riverRoad1IndoorExitPortal = RIVER_ROAD_1_ORIGIN.offset(RIVER_ROAD_1_INDOOR_EXIT_PORTAL_OFFSET);

        // 1号河路室外入口：2 高 x 1 宽。
        placePortalTriggerArea(level, RIVER_ROAD_1_OUTDOOR_ENTRY_POS, 2, 1, 1,
            TAG_PORTAL_MARKER_RIVER_ROAD_1_OUTSIDE, "sdv_portal_target:1_river_road_enter");

        // 1号河路室内出口：2 高 x 1 宽。
        placePortalTriggerArea(level, riverRoad1IndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_RIVER_ROAD_1_INSIDE, "sdv_portal_target:1_river_road_exit");

        BlockPos carpenterShopIndoorExitPortal = CARPENTER_SHOP_ORIGIN.offset(CARPENTER_SHOP_INDOOR_EXIT_PORTAL_OFFSET);

        // 木匠铺室外入口：2 高 x 2 宽。
        placePortalTriggerArea(level, CARPENTER_SHOP_OUTDOOR_ENTRY_POS, 2, 2, 1,
            TAG_PORTAL_MARKER_CARPENTER_SHOP_OUTSIDE, "sdv_portal_target:carpenter_shop_enter");

        // 木匠铺室内出口：2 高 x 1 宽。
        placePortalTriggerArea(level, carpenterShopIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_CARPENTER_SHOP_INSIDE, "sdv_portal_target:carpenter_shop_exit");

        BlockPos willowLane1IndoorExitPortal = WILLOW_LANE_1_ORIGIN.offset(WILLOW_LANE_1_INDOOR_EXIT_PORTAL_OFFSET);

        // 1号柳巷室外入口：2 高 x 2 宽。
        placePortalTriggerArea(level, WILLOW_LANE_1_OUTDOOR_ENTRY_POS, 2, 2, 1,
            TAG_PORTAL_MARKER_WILLOW_LANE_1_OUTSIDE, "sdv_portal_target:1_willow_lane_enter");

        // 1号柳巷室内出口：2 高 x 1 宽。
        placePortalTriggerArea(level, willowLane1IndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_WILLOW_LANE_1_INSIDE, "sdv_portal_target:1_willow_lane_exit");

        BlockPos willowLane2IndoorExitPortal = WILLOW_LANE_2_ORIGIN.offset(WILLOW_LANE_2_INDOOR_EXIT_PORTAL_OFFSET);

        // 2号柳巷室外入口：2 高 x 2 宽。
        placePortalTriggerArea(level, WILLOW_LANE_2_OUTDOOR_ENTRY_POS, 2, 2, 1,
            TAG_PORTAL_MARKER_WILLOW_LANE_2_OUTSIDE, "sdv_portal_target:2_willow_lane_enter");

        // 2号柳巷室内出口：2 高 x 1 宽。
        placePortalTriggerArea(level, willowLane2IndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_WILLOW_LANE_2_INSIDE, "sdv_portal_target:2_willow_lane_exit");

        BlockPos marnieRanchIndoorExitPortal = MARNIE_RANCH_ORIGIN.offset(MARNIE_RANCH_INDOOR_EXIT_PORTAL_OFFSET);

        // 马尼牧场室外入口：2高 x 2宽 x 1深。
        placePortalTriggerArea(level, MARNIE_RANCH_OUTDOOR_ENTRY_POS, 2, 2, 1,
            TAG_PORTAL_MARKER_MARNIE_RANCH_OUTSIDE, "sdv_portal_target:marnie_ranch_enter");

        // 马尼牧场室内出口：2高 x 1宽。
        placePortalTriggerArea(level, marnieRanchIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_MARNIE_RANCH_INSIDE, "sdv_portal_target:marnie_ranch_exit");

        BlockPos leahCottageIndoorExitPortal = LEAH_COTTAGE_ORIGIN.offset(LEAH_COTTAGE_INDOOR_EXIT_PORTAL_OFFSET);

        // 莉亚小屋室外入口：2高 x 2宽 x 1深。
        placePortalTriggerArea(level, LEAH_COTTAGE_OUTDOOR_ENTRY_POS, 2, 2, 1,
            TAG_PORTAL_MARKER_LEAH_COTTAGE_OUTSIDE, "sdv_portal_target:leah_cottage_enter");

        // 莉亚小屋室内出口：2高 x 1宽。
        placePortalTriggerArea(level, leahCottageIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_LEAH_COTTAGE_INSIDE, "sdv_portal_target:leah_cottage_exit");

        BlockPos adventurerGuildIndoorExitPortal = ADVENTURER_GUILD_ORIGIN.offset(ADVENTURER_GUILD_INDOOR_EXIT_PORTAL_OFFSET);

        // 冒险家公会室外入口：2高 x 3宽 x 1深。
        placePortalTriggerArea(level, ADVENTURER_GUILD_OUTDOOR_ENTRY_POS, 2, 3, 1,
            TAG_PORTAL_MARKER_ADVENTURER_GUILD_OUTSIDE, "sdv_portal_target:adventurer_guild_enter");

        // 冒险家公会室内出口：2高 x 1宽。
        placePortalTriggerArea(level, adventurerGuildIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_ADVENTURER_GUILD_INSIDE, "sdv_portal_target:adventurer_guild_exit");

        BlockPos fishShopIndoorExitPortal = FISH_SHOP_ORIGIN.offset(FISH_SHOP_INDOOR_EXIT_PORTAL_OFFSET);

        // 鱼店室外入口：2高 x 2宽 x 1深。
        placePortalTriggerArea(level, FISH_SHOP_OUTDOOR_ENTRY_POS, 2, 2, 1,
            TAG_PORTAL_MARKER_FISH_SHOP_OUTSIDE, "sdv_portal_target:fish_shop_enter");

        // 鱼店室内出口：2高 x 1宽。
        placePortalTriggerArea(level, fishShopIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_FISH_SHOP_INSIDE, "sdv_portal_target:fish_shop_exit");

        BlockPos elliottCabinIndoorExitPortal = ELLIOTT_CABIN_ORIGIN.offset(ELLIOTT_CABIN_INDOOR_EXIT_PORTAL_OFFSET);

        // 艾利欧特小屋室外入口：2高 x 3宽 x 1深。
        placePortalTriggerArea(level, ELLIOTT_CABIN_OUTDOOR_ENTRY_POS, 2, 3, 1,
            TAG_PORTAL_MARKER_ELLIOTT_CABIN_OUTSIDE, "sdv_portal_target:elliott_cabin_enter");

        // 艾利欧特小屋室内出口：2高 x 1宽。
        placePortalTriggerArea(level, elliottCabinIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_ELLIOTT_CABIN_INSIDE, "sdv_portal_target:elliott_cabin_exit");

        BlockPos wizardTowerIndoorExitPortal = WIZARD_TOWER_ORIGIN.offset(WIZARD_TOWER_INDOOR_EXIT_PORTAL_OFFSET);

        // 巫师塔室外入口：2高 x 1宽。
        placePortalTriggerArea(level, WIZARD_TOWER_OUTDOOR_ENTRY_POS, 2, 1, 1,
            TAG_PORTAL_MARKER_WIZARD_TOWER_OUTSIDE, "sdv_portal_target:wizard_tower_enter");

        // 巫师塔室内出口：2高 x 1宽。
        placePortalTriggerArea(level, wizardTowerIndoorExitPortal, 2, 1, 1,
            TAG_PORTAL_MARKER_WIZARD_TOWER_INSIDE, "sdv_portal_target:wizard_tower_exit");

        // 巫师塔内部"回到主世界"：2高 x 3宽 x 3深。
        placePortalTriggerArea(level, WIZARD_TOWER_RETURN_OVERWORLD_BASE, 2, 3, 3,
            TAG_PORTAL_MARKER_WIZARD_TOWER_RETURN_OVERWORLD, "sdv_portal_target:wizard_tower_return_overworld");

        // 矿井室外入口：3高 x 4宽 x 1深。
        placePortalTriggerArea(level, MINE_OUTDOOR_ENTRY_POS, 3, 4, 1,
            TAG_PORTAL_MARKER_MINE_OUTSIDE, "sdv_portal_target:mine_entrance");

        // 社区中心室外入口：2高 x 3宽。
        placePortalTriggerArea(level, CC_OUTDOOR_INTERACTION_BASE, 2, 3, 1,
            TAG_PORTAL_MARKER_CC_OUTSIDE, "sdv_portal_target:community_center_enter");

        // CC 室内出口由 PlayerInteriorAllocator 在每位玩家的 CC 实例中动态放置

        // 温室室内出口由 PlayerInteriorAllocator 在每位玩家的温室实例中动态放置

        // 温室室外入口：仅在温室修复后才放置（由 GreenhouseManager.repair() 调用 spawnGreenhouseOutdoorPortal）
    }

    /**
     * 版本升级时为老存档的农场、温室、农场入口屏障补放 PortalTriggerBlock。
     * <p>
     * 老存档中这些区域使用 Interaction 实体，升级后实体被
     * {@link com.stardew.craft.event.InteriorSubspaceLifecycleEvents#onEntityJoinLevel} 拦截取消加载，
     * 此方法负责在对应位置补放方块，确保传送门可用。
     */
    private static void migrateFarmAndGreenhousePortals(ServerLevel level) {
        // ── 1. 农场入口屏障：重置 barriersPlaced 让其重新放置 ──
        com.stardew.craft.farm.FarmEntryBarrierManager barrierMgr =
                com.stardew.craft.farm.FarmEntryBarrierManager.get(level);
        barrierMgr.resetForMigration();
        barrierMgr.ensureBarriersPlaced(level);

        // ── 2. 所有已初始化农场的出口传送门 ──
        com.stardew.craft.farm.FarmInstanceRegistry registry =
                com.stardew.craft.farm.FarmInstanceRegistry.get();
        int farmPortals = 0;
        for (com.stardew.craft.farm.FarmInstance farm : registry.getAllFarms()) {
            if (!farm.isInitialized()) continue;
            com.stardew.craft.farm.FarmType.FarmLayout layout = farm.getFarmType().getLayout();
            if (layout == null) continue;

            BlockPos origin = farm.getOrigin();

            // 南出口
            placeExitRegion(level, origin, layout.entrySouth(),
                    "sdv_portal_target:farm_exit_south", "sdv_portal_marker:farm_exit");
            // 东出口
            placeExitRegion(level, origin, layout.entryEast(),
                    "sdv_portal_target:farm_exit_east", "sdv_portal_marker:farm_exit");
            // 西出口
            placeExitRegion(level, origin, layout.entryWest(),
                    "sdv_portal_target:farm_exit_west", "sdv_portal_marker:farm_exit");
            farmPortals++;
        }

        // ── 3. 已修复温室的室外入口传送门 ──
        com.stardew.craft.greenhouse.GreenhouseManager ghMgr =
                com.stardew.craft.greenhouse.GreenhouseManager.get(level);
        int ghPortals = 0;
        for (com.stardew.craft.farm.FarmInstance farm : registry.getAllFarms()) {
            if (!farm.isInitialized()) continue;
            if (!ghMgr.isRepairedForPlayer(farm.getOwnerUUID())) continue;

            // 温室入口位置 = 农场温室位置 + (8,0,0)（CW90 旋转后的门口偏移）
            BlockPos ghPos = farm.getGreenhousePos();
            BlockPos portalPos = ghPos.offset(8, 0, 0);
            spawnGreenhouseOutdoorPortalAt(level, portalPos);
            ghPortals++;
        }

        StardewCraft.LOGGER.info("[PORTAL_MIGRATION] Migrated portals: {} farm exit sets, {} greenhouse outdoor portals",
                farmPortals, ghPortals);
    }

    /**
     * 为单个农场出口方向放置 PortalTriggerBlock 区域。
     * 逻辑与 FarmInstanceInitializer.spawnExitEntityRegion 一致。
     */
    private static void placeExitRegion(ServerLevel level, BlockPos origin,
                                         com.stardew.craft.farm.FarmType.EntryData entry,
                                         String targetTag, String markerTag) {
        BlockPos min = origin.offset(entry.exitMin());
        BlockPos max = origin.offset(entry.exitMax());

        int minX = Math.min(min.getX(), max.getX());
        int maxX = Math.max(min.getX(), max.getX());
        int minY = Math.min(min.getY(), max.getY());
        int maxY = Math.max(min.getY(), max.getY());
        int minZ = Math.min(min.getZ(), max.getZ());
        int maxZ = Math.max(min.getZ(), max.getZ());

        int xBlocks = maxX - minX + 1;
        int yBlocks = maxY - minY + 1;
        int zBlocks = maxZ - minZ + 1;

        placePortalTriggerArea(level, new BlockPos(minX, minY, minZ),
                yBlocks, xBlocks, zBlocks, markerTag, targetTag);
    }

    /**
     * 由 PlayerInteriorAllocator 等调用：在指定位置放置 PortalTriggerBlock 区域。
     */
    static void spawnInteractionArea(ServerLevel level, BlockPos basePos,
                                     int heightBlocks, int xBlocks, int zBlocks,
                                     String markerTag, String targetTag) {
        placePortalTriggerArea(level, basePos, heightBlocks, xBlocks, zBlocks, markerTag, targetTag);
    }

    /**
     * 在指定区域放置 PortalTriggerBlock（替代旧版 Interaction 实体）。
     * <p>
     * 如果目标区块尚未加载，会先 force-load 并延迟到下一 tick 重试，
     * 放置完成后自动释放 force-load，确保不阻塞主线程。
     * <p>
     * targetTag 格式："sdv_portal_target:xxx" — 自动提取 "xxx" 作为 BlockEntity 的 targetId。
     */
    public static void placePortalTriggerArea(ServerLevel level,
                                               BlockPos basePos,
                                               int heightBlocks,
                                               int xBlocks,
                                               int zBlocks,
                                               String markerTag,
                                               String targetTag) {
        // ── 区块预加载检查 ──
        java.util.Set<Long> neededChunks = new java.util.HashSet<>();
        for (int dx = 0; dx < xBlocks; dx++) {
            for (int dz = 0; dz < zBlocks; dz++) {
                BlockPos pos = basePos.offset(dx, 0, dz);
                neededChunks.add(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
            }
        }

        boolean allLoaded = true;
        for (long chunkKey : neededChunks) {
            int cx = ChunkPos.getX(chunkKey);
            int cz = ChunkPos.getZ(chunkKey);
            if (level.getChunkSource().getChunkNow(cx, cz) == null) {
                allLoaded = false;
                level.setChunkForced(cx, cz, true);
            }
        }
        if (!allLoaded) {
            // 区块尚未加载，延迟到下一 tick 重试
            level.getServer().tell(new net.minecraft.server.TickTask(
                level.getServer().getTickCount() + 1,
                () -> placePortalTriggerArea(level, basePos, heightBlocks, xBlocks, zBlocks, markerTag, targetTag)
            ));
            StardewCraft.LOGGER.info("[INTERIOR] Deferred portal trigger '{}' at {} — waiting for chunks",
                    markerTag, basePos);
            return;
        }

        // 释放 force-load（方块是持久化数据，放完即可释放）
        for (long chunkKey : neededChunks) {
            int cx = ChunkPos.getX(chunkKey);
            int cz = ChunkPos.getZ(chunkKey);
            level.setChunkForced(cx, cz, false);
        }

        // ── 放置方块 ──
        String targetId = targetTag;
        if (targetTag.startsWith("sdv_portal_target:")) {
            targetId = targetTag.substring("sdv_portal_target:".length());
        }

        int placed = 0;
        for (int dx = 0; dx < xBlocks; dx++) {
            for (int dz = 0; dz < zBlocks; dz++) {
                for (int dy = 0; dy < heightBlocks; dy++) {
                    BlockPos pos = basePos.offset(dx, dy, dz);
                    level.setBlock(pos,
                            com.stardew.craft.block.ModBlocks.PORTAL_TRIGGER.get().defaultBlockState(),
                            net.minecraft.world.level.block.Block.UPDATE_ALL);
                    if (level.getBlockEntity(pos) instanceof
                            com.stardew.craft.blockentity.PortalTriggerBlockEntity be) {
                        be.configure(targetId, markerTag);
                    }
                    placed++;
                }
            }
        }
        StardewCraft.LOGGER.info("[INTERIOR] Portal trigger area '{}': base={}, x={} z={} h={}, placed={}",
                markerTag, basePos, xBlocks, zBlocks, heightBlocks, placed);
    }

    /**
     * 在温室修复后，在农场维度放置室外入口交互实体。
     * 由 GreenhouseManager.repair() 调用。
     */
    public static void spawnGreenhouseOutdoorPortal(ServerLevel level) {
        spawnGreenhouseOutdoorPortalAt(level, com.stardew.craft.greenhouse.GreenhouseManager.OUTDOOR_INTERACTION_BASE);
    }

    /**
     * 在指定位置放置温室室外入口传送触发方块（用于多人农场每玩家温室）。
     * @param pos 温室外观的入口传送触发方块位置
     */
    public static void spawnGreenhouseOutdoorPortalAt(ServerLevel level, BlockPos pos) {
        placePortalTriggerArea(
            level,
            pos,
            2,       // heightBlocks
            1,       // xBlocks
            1,       // zBlocks
            TAG_PORTAL_MARKER_GREENHOUSE_OUTSIDE,
            "sdv_portal_target:greenhouse_enter"
        );
        StardewCraft.LOGGER.info("[INTERIOR] Greenhouse outdoor portal placed at {}", pos);
    }

    private static final class InteriorSubspaceSavedData extends SavedData {
        private int layoutVersion = 0;
        private boolean initialized = false;

        static InteriorSubspaceSavedData get(ServerLevel level) {
            return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(
                    InteriorSubspaceSavedData::new,
                    InteriorSubspaceSavedData::load
                ),
                DATA_NAME
            );
        }

        static InteriorSubspaceSavedData load(net.minecraft.nbt.CompoundTag tag, HolderLookup.Provider provider) {
            InteriorSubspaceSavedData data = new InteriorSubspaceSavedData();
            data.layoutVersion = tag.getInt("layoutVersion");
            data.initialized = tag.getBoolean("initialized");
            return data;
        }

        @Override
        public @Nonnull net.minecraft.nbt.CompoundTag save(@Nonnull net.minecraft.nbt.CompoundTag tag,
                                                           @Nonnull HolderLookup.Provider provider) {
            tag.putInt("layoutVersion", layoutVersion);
            tag.putBoolean("initialized", initialized);
            return tag;
        }
    }
}
