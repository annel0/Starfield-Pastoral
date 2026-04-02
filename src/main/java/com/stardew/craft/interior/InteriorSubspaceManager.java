package com.stardew.craft.interior;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.mining.StructureLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private static final int LAYOUT_VERSION = 18;

    private static final String PIERRE_HOUSE_STRUCTURE_PATH = "data/stardewcraft/structures/interior/pierre_house.schem";
    private static final BlockPos PIERRE_HOUSE_ORIGIN = new BlockPos(12032, 70, 12032);
    private static final BlockPos PIERRE_INDOOR_SPAWN_OFFSET = new BlockPos(6, 1, 6);
    private static final BlockPos PIERRE_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(5, 1, 6);
    private static final BlockPos PIERRE_OUTDOOR_ENTRY_POS = new BlockPos(-159, -18, 54);

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
    private static final BlockPos BLACKSMITH_OUTDOOR_EXIT_POS = new BlockPos(-288, -18, -17);

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

        // 进马尼牧场：传送到 Z+15,X+2,Y+1，面朝正北。
        InteriorPortalRegistry.register(
            "marnie_ranch_enter",
            new InteriorPortalRegistry.PortalTarget(
                marnieRanchIndoorSpawn.getX() + 0.5D,
                marnieRanchIndoorSpawn.getY(),
                marnieRanchIndoorSpawn.getZ() + 0.5D,
                180.0F,
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

        // 进莉亚小屋：传送到 X+6,Y+1,Z+7，面朝正北。
        InteriorPortalRegistry.register(
            "leah_cottage_enter",
            new InteriorPortalRegistry.PortalTarget(
                leahCottageIndoorSpawn.getX() + 0.5D,
                leahCottageIndoorSpawn.getY(),
                leahCottageIndoorSpawn.getZ() + 0.5D,
                180.0F,
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

        // 进冒险家公会：传送到 X+2,Y+1,Z+6，面朝正北。
        InteriorPortalRegistry.register(
            "adventurer_guild_enter",
            new InteriorPortalRegistry.PortalTarget(
                adventurerGuildIndoorSpawn.getX() + 0.5D,
                adventurerGuildIndoorSpawn.getY(),
                adventurerGuildIndoorSpawn.getZ() + 0.5D,
                180.0F,
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

        // 进鱼店：传送到 X+2,Y+1,Z+6，面朝正北。
        InteriorPortalRegistry.register(
            "fish_shop_enter",
            new InteriorPortalRegistry.PortalTarget(
                fishShopIndoorSpawn.getX() + 0.5D,
                fishShopIndoorSpawn.getY(),
                fishShopIndoorSpawn.getZ() + 0.5D,
                180.0F,
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

        // 进艾利欧特小屋：传送到 X+2,Y+1,Z+3，面朝正北。
        InteriorPortalRegistry.register(
            "elliott_cabin_enter",
            new InteriorPortalRegistry.PortalTarget(
                elliottCabinIndoorSpawn.getX() + 0.5D,
                elliottCabinIndoorSpawn.getY(),
                elliottCabinIndoorSpawn.getZ() + 0.5D,
                180.0F,
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

    public static void ensureLoaded(ServerLevel level, String reason) {
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            return;
        }

        InteriorSubspaceSavedData data = InteriorSubspaceSavedData.get(level);
        if (data.layoutVersion == LAYOUT_VERSION && data.initialized) {
            return;
        }

        StardewCraft.LOGGER.info("[INTERIOR] Loading interior subspace structures. reason={}, version={}, count={}",
            reason, LAYOUT_VERSION, FIXED_STRUCTURES.size());

        boolean ok = placeAllStructures(level);
        if (!ok) {
            StardewCraft.LOGGER.error("[INTERIOR] Structure placement failed; keep layout uninitialized for retry. reason={}", reason);
            return;
        }

        ensurePortalInteractions(level);

        data.layoutVersion = LAYOUT_VERSION;
        data.initialized = true;
        data.setDirty();

        StardewCraft.LOGGER.info("[INTERIOR] Interior subspace load complete. version={}", LAYOUT_VERSION);
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
                    if (force) {
                        level.getChunk(chunkX, chunkZ);
                    }
                }
            }
        }

        interiorChunksForced = force;
        StardewCraft.LOGGER.info("[INTERIOR] Interior structure chunk forcing toggled: {} (reason={})", force, reason);
    }

    private static boolean placeAllStructures(ServerLevel level) {
        boolean allSuccess = true;
        for (FixedStructure structure : FIXED_STRUCTURES) {
            boolean placed = StructureLoader.loadAndPlaceWithResult(level, structure.structurePath(), structure.origin());
            if (!placed) {
                allSuccess = false;
            }
        }
        return allSuccess;
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

        // 室外入口：固定点，隐形交互体，约 3 高 x 2 宽。
        spawnOrReplaceInteractionArea(
            level,
            PIERRE_OUTDOOR_ENTRY_POS,
            2,
            3,
            TAG_PORTAL_MARKER_OUTSIDE,
            "sdv_portal_target:pierre_house_enter"
        );

        // 室内出口：结构相对点，2 高 x 1 宽，与结构一并加载。
        spawnOrReplaceInteractionArea(
            level,
            indoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_INSIDE,
            "sdv_portal_target:pierre_house_exit"
        );

        // 博物馆室外入口：(-309,-17,-36) 与 (-308,-17,-36)，2 高 x 2 宽。
        spawnOrReplaceInteractionArea(
            level,
            MUSEUM_OUTDOOR_ENTRY_POS,
            2,
            2,
            TAG_PORTAL_MARKER_MUSEUM_OUTSIDE,
            "sdv_portal_target:museum_enter"
        );

        // 博物馆室内出口：结构相对点 X+9,Y+1,Z+5，2 高 x 1 宽。
        spawnOrReplaceInteractionArea(
            level,
            museumIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_MUSEUM_INSIDE,
            "sdv_portal_target:museum_exit"
        );

        // 铁匠铺室外入口：(-288,-18,-17)，2 高 x 1 宽。
        spawnOrReplaceInteractionArea(
            level,
            BLACKSMITH_OUTDOOR_ENTRY_POS,
            1,
            2,
            TAG_PORTAL_MARKER_BLACKSMITH_OUTSIDE,
            "sdv_portal_target:blacksmith_enter"
        );

        // 铁匠铺室内出口：结构相对点 X+1,Y+1,Z+8，2 高 x 1 宽。
        spawnOrReplaceInteractionArea(
            level,
            blacksmithIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_BLACKSMITH_INSIDE,
            "sdv_portal_target:blacksmith_exit"
        );

        // 酒馆室外入口：(-164,-17,15) 到 (-163,-16,15)，2 长 x 1 宽 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            SALOON_OUTDOOR_ENTRY_POS,
            2,
            2,
            TAG_PORTAL_MARKER_SALOON_OUTSIDE,
            "sdv_portal_target:saloon_enter"
        );

        // 酒馆室内出口：结构相对点 X+1,Y+1,Z+17，1 宽 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            saloonIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_SALOON_INSIDE,
            "sdv_portal_target:saloon_exit"
        );

        // 市长家室外入口：(-197,-17,-22) 到 (-196,-17,-22)，2 长 x 1 宽 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            MAYOR_HOUSE_OUTDOOR_ENTRY_POS,
            2,
            2,
            TAG_PORTAL_MARKER_MAYOR_HOUSE_OUTSIDE,
            "sdv_portal_target:mayor_house_enter"
        );

        // 市长家室内出口：结构相对点 X+1,Y+1,Z+5 到 Z+6，沿 Z 轴延伸 2 格 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            mayorHouseIndoorExitPortal,
            2,
            1,
            2,
            TAG_PORTAL_MARKER_MAYOR_HOUSE_INSIDE,
            "sdv_portal_target:mayor_house_exit"
        );

        // 诊所室外入口：(-146,-18,60) 到 (-144,-17,60)，3 长 x 1 宽 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            CLINIC_OUTDOOR_ENTRY_POS,
            3,
            2,
            TAG_PORTAL_MARKER_CLINIC_OUTSIDE,
            "sdv_portal_target:clinic_enter"
        );

        // 诊所室内出口：结构相对点 X+1,Y+1,Z+11，1 宽 x 1 深 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            clinicIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_CLINIC_INSIDE,
            "sdv_portal_target:clinic_exit"
        );

        BlockPos riverRoad1IndoorExitPortal = RIVER_ROAD_1_ORIGIN.offset(RIVER_ROAD_1_INDOOR_EXIT_PORTAL_OFFSET);

        // 1号河路室外入口：(-195,-18,32)，1 宽 x 1 深 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            RIVER_ROAD_1_OUTDOOR_ENTRY_POS,
            1,
            2,
            TAG_PORTAL_MARKER_RIVER_ROAD_1_OUTSIDE,
            "sdv_portal_target:1_river_road_enter"
        );

        // 1号河路室内出口：结构相对点 X+1,Y+1,Z+12，1 宽 x 1 深 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            riverRoad1IndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_RIVER_ROAD_1_INSIDE,
            "sdv_portal_target:1_river_road_exit"
        );

        BlockPos carpenterShopIndoorExitPortal = CARPENTER_SHOP_ORIGIN.offset(CARPENTER_SHOP_INDOOR_EXIT_PORTAL_OFFSET);

        // 木匠铺室外入口：(-212,-12,219)到(-213,-11,219)，2 长 x 1 宽 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            CARPENTER_SHOP_OUTDOOR_ENTRY_POS,
            2,
            2,
            TAG_PORTAL_MARKER_CARPENTER_SHOP_OUTSIDE,
            "sdv_portal_target:carpenter_shop_enter"
        );

        // 木匠铺室内出口：结构相对点 X+12,Y+5,Z+7，1 宽 x 1 深 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            carpenterShopIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_CARPENTER_SHOP_INSIDE,
            "sdv_portal_target:carpenter_shop_exit"
        );

        BlockPos willowLane1IndoorExitPortal = WILLOW_LANE_1_ORIGIN.offset(WILLOW_LANE_1_INDOOR_EXIT_PORTAL_OFFSET);

        // 1号柳巷室外入口：(-85,-16,-25)到(-84,-15,-25)，2 长 x 1 宽 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            WILLOW_LANE_1_OUTDOOR_ENTRY_POS,
            2,
            2,
            TAG_PORTAL_MARKER_WILLOW_LANE_1_OUTSIDE,
            "sdv_portal_target:1_willow_lane_enter"
        );

        // 1号柳巷室内出口：结构相对点 X+1,Y+1,Z+5，1 宽 x 1 深 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            willowLane1IndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_WILLOW_LANE_1_INSIDE,
            "sdv_portal_target:1_willow_lane_exit"
        );

        BlockPos willowLane2IndoorExitPortal = WILLOW_LANE_2_ORIGIN.offset(WILLOW_LANE_2_INDOOR_EXIT_PORTAL_OFFSET);

        // 2号柳巷室外入口：(-115,-17,-27)到(-114,-16,-27)，2 长 x 1 宽 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            WILLOW_LANE_2_OUTDOOR_ENTRY_POS,
            2,
            2,
            TAG_PORTAL_MARKER_WILLOW_LANE_2_OUTSIDE,
            "sdv_portal_target:2_willow_lane_enter"
        );

        // 2号柳巷室内出口：结构相对点 X+3,Y+1,Z+2，1 宽 x 1 深 x 2 高。
        spawnOrReplaceInteractionArea(
            level,
            willowLane2IndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_WILLOW_LANE_2_INSIDE,
            "sdv_portal_target:2_willow_lane_exit"
        );

        BlockPos marnieRanchIndoorExitPortal = MARNIE_RANCH_ORIGIN.offset(MARNIE_RANCH_INDOOR_EXIT_PORTAL_OFFSET);

        // 马尼牧场室外入口：178,-14,-4 到 179,-14,-4，2长 x 1宽 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            MARNIE_RANCH_OUTDOOR_ENTRY_POS,
            2,
            2,
            1,
            TAG_PORTAL_MARKER_MARNIE_RANCH_OUTSIDE,
            "sdv_portal_target:marnie_ranch_enter"
        );

        // 马尼牧场室内出口：结构相对点 X+1,Y+1,Z+15，1宽 x 1深 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            marnieRanchIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_MARNIE_RANCH_INSIDE,
            "sdv_portal_target:marnie_ranch_exit"
        );

        BlockPos leahCottageIndoorExitPortal = LEAH_COTTAGE_ORIGIN.offset(LEAH_COTTAGE_INDOOR_EXIT_PORTAL_OFFSET);

        // 莉亚小屋室外入口：155,-13,-58 到 156,-13,-58，2长 x 1宽 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            LEAH_COTTAGE_OUTDOOR_ENTRY_POS,
            2,
            2,
            1,
            TAG_PORTAL_MARKER_LEAH_COTTAGE_OUTSIDE,
            "sdv_portal_target:leah_cottage_enter"
        );

        // 莉亚小屋室内出口：结构相对点 X+4,Y+1,Z+7，1宽 x 1深 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            leahCottageIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_LEAH_COTTAGE_INSIDE,
            "sdv_portal_target:leah_cottage_exit"
        );

        BlockPos adventurerGuildIndoorExitPortal = ADVENTURER_GUILD_ORIGIN.offset(ADVENTURER_GUILD_INDOOR_EXIT_PORTAL_OFFSET);

        // 冒险家公会室外入口：-335,-13,312 到 -333,-13,312，3长 x 1宽 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            ADVENTURER_GUILD_OUTDOOR_ENTRY_POS,
            2,
            3,
            1,
            TAG_PORTAL_MARKER_ADVENTURER_GUILD_OUTSIDE,
            "sdv_portal_target:adventurer_guild_enter"
        );

        // 冒险家公会室内出口：结构相对点 X+1,Y+1,Z+6，1宽 x 1深 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            adventurerGuildIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_ADVENTURER_GUILD_INSIDE,
            "sdv_portal_target:adventurer_guild_exit"
        );

        BlockPos fishShopIndoorExitPortal = FISH_SHOP_ORIGIN.offset(FISH_SHOP_INDOOR_EXIT_PORTAL_OFFSET);

        // 鱼店室外入口：-238,-15,-211 到 -237,-14,-211，2长 x 1宽 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            FISH_SHOP_OUTDOOR_ENTRY_POS,
            2,
            2,
            1,
            TAG_PORTAL_MARKER_FISH_SHOP_OUTSIDE,
            "sdv_portal_target:fish_shop_enter"
        );

        // 鱼店室内出口：结构相对点 X+1,Y+1,Z+6，1宽 x 1深 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            fishShopIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_FISH_SHOP_INSIDE,
            "sdv_portal_target:fish_shop_exit"
        );

        BlockPos elliottCabinIndoorExitPortal = ELLIOTT_CABIN_ORIGIN.offset(ELLIOTT_CABIN_INDOOR_EXIT_PORTAL_OFFSET);

        // 艾利欧特小屋室外入口：-267,-13,-152 到 -265,-12,-152，3长 x 1宽 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            ELLIOTT_CABIN_OUTDOOR_ENTRY_POS,
            2,
            3,
            1,
            TAG_PORTAL_MARKER_ELLIOTT_CABIN_OUTSIDE,
            "sdv_portal_target:elliott_cabin_enter"
        );

        // 艾利欧特小屋室内出口：结构相对点 X+1,Y+1,Z+3，1宽 x 1深 x 2高。
        spawnOrReplaceInteractionArea(
            level,
            elliottCabinIndoorExitPortal,
            1,
            2,
            TAG_PORTAL_MARKER_ELLIOTT_CABIN_INSIDE,
            "sdv_portal_target:elliott_cabin_exit"
        );
    }

    private static void spawnOrReplaceInteractionArea(ServerLevel level,
                                                      BlockPos basePos,
                                                      int widthBlocksX,
                                                      int heightBlocks,
                                                      String markerTag,
                                                      String targetTag) {
        spawnOrReplaceInteractionArea(level, basePos, heightBlocks, widthBlocksX, 1, markerTag, targetTag);
    }

    private static void spawnOrReplaceInteractionArea(ServerLevel level,
                                                      BlockPos basePos,
                                                      int heightBlocks,
                                                      int xBlocks,
                                                      int zBlocks,
                                                      String markerTag,
                                                      String targetTag) {
        AABB searchBox = new AABB(basePos).inflate(6.0D);
        for (Interaction interaction : level.getEntitiesOfClass(Interaction.class, searchBox, e -> e.getTags().contains(markerTag))) {
            interaction.discard();
        }

        for (int dx = 0; dx < xBlocks; dx++) {
            for (int dz = 0; dz < zBlocks; dz++) {
                for (int dy = 0; dy < heightBlocks; dy++) {
                    BlockPos pos = basePos.offset(dx, dy, dz);
                    level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
                    Entity entity = EntityType.INTERACTION.create(level);
                    if (!(entity instanceof Interaction interaction)) {
                        StardewCraft.LOGGER.warn("[INTERIOR] Failed to create interaction entity at {}", pos);
                        continue;
                    }

                    interaction.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
                    interaction.addTag(markerTag);
                    interaction.addTag(targetTag);
                    level.addFreshEntity(interaction);
                }
            }
        }
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
