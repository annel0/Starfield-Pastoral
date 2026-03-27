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
    public static final int REGION_MAX_X = 14096;
    public static final int REGION_MAX_Z = 14096;

    // 结构布局版本：当结构清单或坐标大改时 +1，可触发重新装载。
    private static final int LAYOUT_VERSION = 6;

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

    private static final String DATA_NAME = "stardew_interior_subspace_layout";

    private static final List<FixedStructure> FIXED_STRUCTURES = new ArrayList<>();
    private static final int STRUCTURE_FORCE_RADIUS_CHUNKS = 3;
    private static boolean interiorChunksForced;

    static {
        // 用户指定：室内坐标都放在 X>10000, Z>10000，且只需保证互不重叠。
        register("pierre_house", PIERRE_HOUSE_STRUCTURE_PATH, PIERRE_HOUSE_ORIGIN.getX(), PIERRE_HOUSE_ORIGIN.getY(), PIERRE_HOUSE_ORIGIN.getZ());
        register("museum", MUSEUM_STRUCTURE_PATH, MUSEUM_ORIGIN.getX(), MUSEUM_ORIGIN.getY(), MUSEUM_ORIGIN.getZ());
        register("blacksmith", BLACKSMITH_STRUCTURE_PATH, BLACKSMITH_ORIGIN.getX(), BLACKSMITH_ORIGIN.getY(), BLACKSMITH_ORIGIN.getZ());

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

        // 室内出口交互体固定在结构相对坐标 X+5,Y+1,Z+6。
        StardewCraft.LOGGER.info("[INTERIOR] Pierre indoor exit interaction anchor = {}", indoorExitPortal);
        StardewCraft.LOGGER.info("[INTERIOR] Museum indoor exit interaction anchor = {}", museumIndoorExitPortal);
        StardewCraft.LOGGER.info("[INTERIOR] Blacksmith indoor exit interaction anchor = {}", blacksmithIndoorExitPortal);
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
    }

    private static void spawnOrReplaceInteractionArea(ServerLevel level,
                                                      BlockPos basePos,
                                                      int widthBlocks,
                                                      int heightBlocks,
                                                      String markerTag,
                                                      String targetTag) {
        AABB searchBox = new AABB(basePos).inflate(6.0D);
        for (Interaction interaction : level.getEntitiesOfClass(Interaction.class, searchBox, e -> e.getTags().contains(markerTag))) {
            interaction.discard();
        }

        for (int dx = 0; dx < widthBlocks; dx++) {
            for (int dy = 0; dy < heightBlocks; dy++) {
                BlockPos pos = basePos.offset(dx, dy, 0);
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
