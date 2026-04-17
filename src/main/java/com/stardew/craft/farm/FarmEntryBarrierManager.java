package com.stardew.craft.farm;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.interior.InteriorSubspaceManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

/**
 * 在公共区域的三个农场入口处放置屏障墙和传送交互实体。
 * 屏障墙使用 barrier 方块（不可见），传送触发方块触发传送到玩家个人农场。
 *
 * 三个入口坐标（旧农场边界的公共区域一侧）：
 * 1. 南入口（镇子方向）: 屏障 198,-14,160 到 210,8,160; 交互 210,-14,161 到 199,-12,161
 * 2. 东入口（背包方向）: 屏障 216,-14,27 到 209,18,27; 交互 209,-15,26 到 216,-13,26
 * 3. 西入口（森林方向）: 屏障 71,-12,123 到 71,8,116; 交互 70,-12,116 到 70,-10,123
 */
@SuppressWarnings("null")
public class FarmEntryBarrierManager extends SavedData {

    private static final String DATA_NAME = "stardew_farm_entry_barriers";
    private static final String TAG_FARM_ENTRY = "sdv_portal_marker:farm_entry";

    private boolean barriersPlaced = false;

    public FarmEntryBarrierManager() {}

    public static FarmEntryBarrierManager get(ServerLevel level) {
        ServerLevel overworld = level.getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD);
        if (overworld == null) return new FarmEntryBarrierManager();
        return overworld.getDataStorage().computeIfAbsent(factory(), DATA_NAME);
    }

    /**
     * 重置屏障放置状态，用于迁移时强制重新放置。
     */
    public void resetForMigration() {
        barriersPlaced = false;
        setDirty();
    }

    /**
     * 确保屏障墙和交互实体已放置。只执行一次。
     */
    public void ensureBarriersPlaced(ServerLevel stardewLevel) {
        if (barriersPlaced) return;
        if (!ModDimensions.STARDEW_VALLEY.equals(stardewLevel.dimension())) return;

        StardewCraft.LOGGER.info("[FARM_BARRIER] Placing farm entry barriers and interaction entities...");

        // ── 入口 1: 南入口（镇子方向） ──
        // 屏障墙: X 198-210, Y -14 到 8, Z=160
        placeBarrierWall(stardewLevel, 198, 210, -14, 8, 160, 160);
        // 交互实体: X 199-210, Y -14 到 -12, Z=161
        spawnFarmEntryInteractions(stardewLevel,
                new BlockPos(199, -14, 161), new BlockPos(210, -12, 161),
                "sdv_portal_target:farm_entry_south");

        // ── 入口 2: 东入口 ──
        // 屏障墙: X 209-216, Y -14 到 18, Z=27
        placeBarrierWall(stardewLevel, 209, 216, -14, 18, 27, 27);
        // 交互实体: X 209-216, Y -15 到 -13, Z=26
        spawnFarmEntryInteractions(stardewLevel,
                new BlockPos(209, -15, 26), new BlockPos(216, -13, 26),
                "sdv_portal_target:farm_entry_east");

        // ── 入口 3: 西入口（森林方向） ──
        // 屏障墙: X=71, Y -12 到 8, Z 116-123
        placeBarrierWall(stardewLevel, 71, 71, -12, 8, 116, 123);
        // 交互实体: X=70, Y -12 到 -10, Z 116-123
        spawnFarmEntryInteractions(stardewLevel,
                new BlockPos(70, -12, 116), new BlockPos(70, -10, 123),
                "sdv_portal_target:farm_entry_west");

        barriersPlaced = true;
        setDirty();
        StardewCraft.LOGGER.info("[FARM_BARRIER] Farm entry barriers placed successfully.");
    }

    /**
     * 放置 barrier 方块墙。
     */
    private void placeBarrierWall(ServerLevel level, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        int count = 0;
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
                    if (level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, Blocks.BARRIER.defaultBlockState(), 2);
                        count++;
                    }
                }
            }
        }
        StardewCraft.LOGGER.debug("[FARM_BARRIER] Placed {} barrier blocks ({},{},{}) to ({},{},{})",
                count, minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * 在指定范围内放置农场入口传送触发方块。
     */
    private void spawnFarmEntryInteractions(ServerLevel level, BlockPos min, BlockPos max, String targetTag) {
        int xBlocks = max.getX() - min.getX() + 1;
        int yBlocks = max.getY() - min.getY() + 1;
        int zBlocks = max.getZ() - min.getZ() + 1;
        InteriorSubspaceManager.placePortalTriggerArea(
            level, min, yBlocks, xBlocks, zBlocks,
            TAG_FARM_ENTRY, targetTag
        );
    }

    // ── NBT ──

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        tag.putBoolean("BarriersPlaced", barriersPlaced);
        return tag;
    }

    private static FarmEntryBarrierManager load(CompoundTag tag, HolderLookup.Provider provider) {
        FarmEntryBarrierManager manager = new FarmEntryBarrierManager();
        manager.barriersPlaced = tag.getBoolean("BarriersPlaced");
        return manager;
    }

    public static SavedData.Factory<FarmEntryBarrierManager> factory() {
        return new SavedData.Factory<>(FarmEntryBarrierManager::new, FarmEntryBarrierManager::load);
    }
}
