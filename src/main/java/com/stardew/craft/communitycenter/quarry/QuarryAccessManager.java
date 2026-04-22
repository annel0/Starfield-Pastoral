package com.stardew.craft.communitycenter.quarry;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.interior.InteriorSubspaceManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;

/**
 * 采石场访问门：工艺室献祭 (ccCraftsRoom) 对应的小镇→采石场通行逻辑。
 *
 * 设计要点：
 * - 小镇这边的桥是永远相连的（视觉上没有断桥），但在 X=-396 这个面上放一堵
 *   barrier 屏障墙，把直接走过去的物理通路封住。
 * - 屏障中间留出的 3×2 方形空洞里放 PortalTriggerBlock（target=quarry_entrance），
 *   玩家右键这里，服务端检查 ccCraftsRoom flag：有 → 传送到桥对面；无 → 提示。
 * - 对面（采石场侧）也放一组 PortalTriggerBlock（target=quarry_exit）作为返程。
 * - 该 manager 用 SavedData 标记 "已放置"，确保老存档升级上来也能补放。
 */
@SuppressWarnings("null")
public class QuarryAccessManager extends SavedData {

    private static final String DATA_NAME = "stardew_quarry_access";
    private static final String TAG_QUARRY = "sdv_portal_marker:quarry_access";

    /**
     * 放置逻辑版本号。坐标或屏障结构有改动时把这个数 +1，老存档下次加载会自动
     * 清除旧放置标记并重新放置（ensurePlaced 会检测 placedVersion < CURRENT_VERSION）。
     */
    private static final int CURRENT_VERSION = 2;

    // ── 采石场区域（与 QuarrySpawnService 保持一致） ──
    public static final int QUARRY_MIN_X = -493;
    public static final int QUARRY_MAX_X = -416;
    public static final int QUARRY_MIN_Z = 232;
    public static final int QUARRY_MAX_Z = 299;

    /** 判断坐标是否在采石场矩形内（忽略 Y，整个立方柱都算采石场，保证挖深也能操作）。 */
    public static boolean isInQuarryArea(BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        return x >= QUARRY_MIN_X && x <= QUARRY_MAX_X
                && z >= QUARRY_MIN_Z && z <= QUARRY_MAX_Z;
    }

    // ── 屏障墙（X=-396 这一整面） ──
    private static final int BARRIER_X = -396;
    private static final int BARRIER_Y_MIN = -20;
    private static final int BARRIER_Y_MAX = 33;
    private static final int BARRIER_Z_MIN = 32;
    private static final int BARRIER_Z_MAX = 412;

    // ── 镇子侧触发方块（X=-395） ──
    private static final int TOWN_TRIGGER_X = -395;
    private static final int TOWN_TRIGGER_Y_MIN = -12;
    private static final int TOWN_TRIGGER_Y_MAX = -11;
    private static final int TOWN_TRIGGER_Z_MIN = 260;
    private static final int TOWN_TRIGGER_Z_MAX = 262;

    // ── 采石场侧返程触发方块（X=-397） ──
    private static final int QUARRY_TRIGGER_X = -397;
    private static final int QUARRY_TRIGGER_Y_MIN = -12;
    private static final int QUARRY_TRIGGER_Y_MAX = -11;
    private static final int QUARRY_TRIGGER_Z_MIN = 260;
    private static final int QUARRY_TRIGGER_Z_MAX = 262;

    // ── 传送目标 ──
    /** 镇子 → 采石场：(-398, -12, 261) 面朝西（yaw=90） */
    public static final double ENTRY_DEST_X = -398 + 0.5;
    public static final double ENTRY_DEST_Y = -12.0;
    public static final double ENTRY_DEST_Z = 261 + 0.5;
    public static final float ENTRY_DEST_YAW = 90.0F;
    public static final float ENTRY_DEST_PITCH = 0.0F;

    /** 采石场 → 镇子：落到镇子侧触发方块东侧一格，面朝东（避免立刻再触发） */
    public static final double EXIT_DEST_X = -394 + 0.5;
    public static final double EXIT_DEST_Y = -12.0;
    public static final double EXIT_DEST_Z = 261 + 0.5;
    public static final float EXIT_DEST_YAW = -90.0F;
    public static final float EXIT_DEST_PITCH = 0.0F;

    /** 上次已应用的放置版本。0 = 从未放置。 */
    private int placedVersion = 0;

    public QuarryAccessManager() {}

    public static QuarryAccessManager get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (overworld == null) return new QuarryAccessManager();
        return overworld.getDataStorage().computeIfAbsent(factory(), DATA_NAME);
    }

    public void resetForMigration() {
        placedVersion = 0;
        setDirty();
    }

    /**
     * 确保屏障墙与两侧传送触发方块已放置。基于版本号幂等：
     * 如果 placedVersion < CURRENT_VERSION，会重新运行放置逻辑（覆盖旧坐标），
     * 让老存档在坐标被改动后能自动升级。
     */
    public void ensurePlaced(ServerLevel stardewLevel) {
        if (placedVersion >= CURRENT_VERSION) return;
        if (!ModDimensions.STARDEW_VALLEY.equals(stardewLevel.dimension())) return;

        StardewCraft.LOGGER.info("[QUARRY] Placing quarry access barrier + triggers (oldVersion={}, newVersion={})",
                placedVersion, CURRENT_VERSION);

        placeBarrierWall(stardewLevel);
        placeTrigger(stardewLevel,
                TOWN_TRIGGER_X, TOWN_TRIGGER_Y_MIN, TOWN_TRIGGER_Z_MIN,
                TOWN_TRIGGER_X, TOWN_TRIGGER_Y_MAX, TOWN_TRIGGER_Z_MAX,
                "sdv_portal_target:quarry_entrance");
        placeTrigger(stardewLevel,
                QUARRY_TRIGGER_X, QUARRY_TRIGGER_Y_MIN, QUARRY_TRIGGER_Z_MIN,
                QUARRY_TRIGGER_X, QUARRY_TRIGGER_Y_MAX, QUARRY_TRIGGER_Z_MAX,
                "sdv_portal_target:quarry_exit");

        placedVersion = CURRENT_VERSION;
        setDirty();
        StardewCraft.LOGGER.info("[QUARRY] Quarry access setup complete.");
    }

    private void placeBarrierWall(ServerLevel level) {
        int minX = Math.min(BARRIER_X, BARRIER_X);
        int maxX = minX;
        int minY = Math.min(BARRIER_Y_MIN, BARRIER_Y_MAX);
        int maxY = Math.max(BARRIER_Y_MIN, BARRIER_Y_MAX);
        int minZ = Math.min(BARRIER_Z_MIN, BARRIER_Z_MAX);
        int maxZ = Math.max(BARRIER_Z_MIN, BARRIER_Z_MAX);
        int count = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    // 保证 chunk 加载
                    level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
                    if (level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 2);
                        count++;
                    }
                }
            }
        }
        StardewCraft.LOGGER.info("[QUARRY] Placed {} barrier blocks along X={} (Y {}..{}, Z {}..{})",
                count, BARRIER_X, minY, maxY, minZ, maxZ);
    }

    private void placeTrigger(ServerLevel level,
                              int minX, int minY, int minZ,
                              int maxX, int maxY, int maxZ,
                              String targetTag) {
        int lx = Math.min(minX, maxX);
        int ly = Math.min(minY, maxY);
        int lz = Math.min(minZ, maxZ);
        int hx = Math.max(minX, maxX);
        int hy = Math.max(minY, maxY);
        int hz = Math.max(minZ, maxZ);
        int xBlocks = hx - lx + 1;
        int yBlocks = hy - ly + 1;
        int zBlocks = hz - lz + 1;
        InteriorSubspaceManager.placePortalTriggerArea(
                level, new BlockPos(lx, ly, lz),
                yBlocks, xBlocks, zBlocks,
                TAG_QUARRY, targetTag);
    }

    // ── NBT ──

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        tag.putInt("PlacedVersion", placedVersion);
        return tag;
    }

    private static QuarryAccessManager load(CompoundTag tag, HolderLookup.Provider provider) {
        QuarryAccessManager manager = new QuarryAccessManager();
        if (tag.contains("PlacedVersion")) {
            manager.placedVersion = tag.getInt("PlacedVersion");
        } else if (tag.contains("Placed")) {
            // 旧存档兼容：老 Placed=true 视为版本 1（即当前实现的首个版本）
            manager.placedVersion = tag.getBoolean("Placed") ? 1 : 0;
        }
        return manager;
    }

    public static SavedData.Factory<QuarryAccessManager> factory() {
        return new SavedData.Factory<>(QuarryAccessManager::new, QuarryAccessManager::load);
    }
}
