package com.stardew.craft.greenhouse;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.farming.SeasonLocationRules;
import com.stardew.craft.interior.InteriorSubspaceManager;
import com.stardew.craft.mining.StructureLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 温室系统管理器 — 跟踪温室修复状态，提供坐标常量，处理修复流程。
 * <p>
 * 生命周期：
 * 1. FarmInitializer 放置破损温室 (ruins)
 * 2. CC Pantry (area 0) 完成后 → repair() 覆盖为修复版 + 生成传送实体
 * 3. 温室内部通过 InteriorSubspaceManager 注册，区块常加载
 * 4. SeasonLocationRules 注册季节豁免规则
 */
@SuppressWarnings("null")
public class GreenhouseManager extends SavedData {

    private static final String DATA_ID = "stardewcraft_greenhouse";

    // ═══════════════════════════════════════════════════════════════
    //  坐标常量
    // ═══════════════════════════════════════════════════════════════

    /** 温室外观在农场中的放置原点 */
    public static final BlockPos FARM_ORIGIN = new BlockPos(232, -12, 112);

    /** 温室内部在 interior 子空间中的原点 */
    public static final BlockPos INTERIOR_ORIGIN = new BlockPos(18816, 70, 19392);

    /** 进入温室后玩家出生点偏移 (相对于 INTERIOR_ORIGIN) */
    public static final BlockPos INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 10);

    /** 室内出口交互实体偏移 (相对于 INTERIOR_ORIGIN) */
    public static final BlockPos INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 10);

    /**
     * 室外入口交互实体位置。
     * 原始 schem 中门偏移为 (0,0,8)，经 CW90 旋转后变为 (8,0,0)。
     */
    public static final BlockPos OUTDOOR_INTERACTION_BASE = FARM_ORIGIN.offset(8, 0, 0);

    /** 出门后玩家传送目标 = 室外交互实体同位置，面朝北 */
    public static final BlockPos OUTDOOR_EXIT_POS = OUTDOOR_INTERACTION_BASE;

    // ═══════════════════════════════════════════════════════════════
    //  结构路径
    // ═══════════════════════════════════════════════════════════════

    public static final String RUINS_STRUCTURE_PATH = "data/stardewcraft/structures/greenhouse/green_house_ruins.schem";
    public static final String REPAIRED_STRUCTURE_PATH = "data/stardewcraft/structures/greenhouse/green_house_refurbished.schem";
    public static final String INTERIOR_STRUCTURE_PATH = "data/stardewcraft/structures/greenhouse/green_house_interior.schem";

    // ═══════════════════════════════════════════════════════════════
    //  状态
    // ═══════════════════════════════════════════════════════════════

    private boolean repaired = false;
    private boolean ruinsPlaced = false;

    /** 每玩家温室修复状态（农场实例化后使用） */
    private final Map<UUID, Boolean> repairedByOwner = new HashMap<>();
    /** 每玩家温室放置状态 */
    private final Map<UUID, Boolean> ruinsPlacedByOwner = new HashMap<>();

    public GreenhouseManager() {}

    public boolean isRepaired() {
        return repaired;
    }

    public boolean isRuinsPlaced() {
        return ruinsPlaced;
    }

    public void markRuinsPlaced() {
        this.ruinsPlaced = true;
        setDirty();
    }

    // ═══════════════════════════════════════════════════════════════
    //  多人农场：每玩家温室管理
    // ═══════════════════════════════════════════════════════════════

    /**
     * 确保某个玩家的农场温室已放置。在农场初始化时调用。
     */
    public void ensurePlacedForPlayer(ServerLevel farmLevel, UUID ownerUUID) {
        if (Boolean.TRUE.equals(ruinsPlacedByOwner.get(ownerUUID))) return;

        com.stardew.craft.farm.FarmInstance farm =
                com.stardew.craft.farm.FarmInstanceRegistry.get().getFarm(ownerUUID);
        if (farm == null) return;

        BlockPos farmGreenhouse = farm.getGreenhousePos();

        // 预加载区块
        int minCX = farmGreenhouse.getX() >> 4;
        int maxCX = (farmGreenhouse.getX() + 30) >> 4;
        int minCZ = farmGreenhouse.getZ() >> 4;
        int maxCZ = (farmGreenhouse.getZ() + 30) >> 4;
        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                farmLevel.getChunk(cx, cz);
            }
        }

        // 检查 CC Pantry 是否已完成（对该玩家）
        var ccData = com.stardew.craft.communitycenter.state.CommunityCenterSavedData.get();
        if (ccData.isAreaComplete(ownerUUID, 0)) {
            StardewCraft.LOGGER.info("[GREENHOUSE] Placing repaired greenhouse for {} at {}",
                    ownerUUID, farmGreenhouse);
            com.stardew.craft.mining.StructureLoader.loadAndPlaceCW90(farmLevel,
                    REPAIRED_STRUCTURE_PATH, farmGreenhouse);
            repairedByOwner.put(ownerUUID, true);
            // 生成室外入口交互实体（CW90后门偏移 (8,0,0)）
            BlockPos portalPos = farmGreenhouse.offset(8, 0, 0);
            com.stardew.craft.interior.InteriorSubspaceManager.spawnGreenhouseOutdoorPortalAt(farmLevel, portalPos);
        } else {
            StardewCraft.LOGGER.info("[GREENHOUSE] Placing greenhouse ruins for {} at {}",
                    ownerUUID, farmGreenhouse);
            com.stardew.craft.mining.StructureLoader.loadAndPlaceCW90(farmLevel,
                    RUINS_STRUCTURE_PATH, farmGreenhouse);
        }

        ruinsPlacedByOwner.put(ownerUUID, true);
        setDirty();
    }

    /**
     * CC Pantry 完成后修复某个玩家的温室。
     */
    public void repairForPlayer(ServerLevel farmLevel, UUID ownerUUID) {
        if (Boolean.TRUE.equals(repairedByOwner.get(ownerUUID))) return;

        com.stardew.craft.farm.FarmInstance farm =
                com.stardew.craft.farm.FarmInstanceRegistry.get().getFarm(ownerUUID);
        if (farm == null) return;

        BlockPos farmGreenhouse = farm.getGreenhousePos();
        com.stardew.craft.mining.StructureLoader.loadAndPlaceCW90(farmLevel,
                REPAIRED_STRUCTURE_PATH, farmGreenhouse);

        // 生成室外入口交互实体（CW90后门偏移 (8,0,0)）
        BlockPos portalPos = farmGreenhouse.offset(8, 0, 0);
        com.stardew.craft.interior.InteriorSubspaceManager.spawnGreenhouseOutdoorPortalAt(farmLevel, portalPos);

        repairedByOwner.put(ownerUUID, true);
        setDirty();
        StardewCraft.LOGGER.info("[GREENHOUSE] Greenhouse repaired for player {} at {}",
                ownerUUID, farmGreenhouse);
    }

    public boolean isRepairedForPlayer(UUID ownerUUID) {
        return Boolean.TRUE.equals(repairedByOwner.get(ownerUUID));
    }

    // ═══════════════════════════════════════════════════════════════
    //  修复温室（旧公共区域温室 — 保留向后兼容）
    // ═══════════════════════════════════════════════════════════════

    /**
     * CC Pantry 完成时调用：覆盖放置修复版 schem + 生成传送交互实体。
     */
    public void repair(ServerLevel farmLevel) {
        if (repaired) return;

        StardewCraft.LOGGER.info("[GREENHOUSE] Repairing greenhouse at {}", FARM_ORIGIN);

        // 覆盖放置修复版 schem（顺时针 90° 旋转）
        StructureLoader.loadAndPlaceCW90(farmLevel, REPAIRED_STRUCTURE_PATH, FARM_ORIGIN);

        // 生成室外入口传送交互实体
        InteriorSubspaceManager.spawnGreenhouseOutdoorPortal(farmLevel);

        repaired = true;
        setDirty();

        StardewCraft.LOGGER.info("[GREENHOUSE] Greenhouse repaired successfully");
    }

    // ═══════════════════════════════════════════════════════════════
    //  放置破损温室（用于首次初始化 / 已有存档新加模组）
    // ═══════════════════════════════════════════════════════════════

    /**
     * 确保温室外观已放置。如果是新世界或已有存档首次加载模组，放置 ruins。
     * 如果 Pantry 已经完成，放置 refurbished 并生成传送交互实体。
     */
    public void ensurePlaced(ServerLevel farmLevel) {
        if (ruinsPlaced) return;

        // 预加载温室区域的区块
        int minCX = FARM_ORIGIN.getX() >> 4;
        int maxCX = (FARM_ORIGIN.getX() + 30) >> 4; // 温室不会超过 30 格宽
        int minCZ = FARM_ORIGIN.getZ() >> 4;
        int maxCZ = (FARM_ORIGIN.getZ() + 30) >> 4;
        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                farmLevel.getChunk(cx, cz);
            }
        }

        // 检查 Pantry 是否已完成（旧全局温室使用 legacy UUID）
        var ccData = com.stardew.craft.communitycenter.state.CommunityCenterSavedData.get();
        if (ccData.isAreaComplete(new java.util.UUID(0L, 0L), 0)) {
            // Pantry 已完成 → 直接放置修复版（CW90 旋转）
            StardewCraft.LOGGER.info("[GREENHOUSE] Pantry already complete, placing repaired greenhouse");
            StructureLoader.loadAndPlaceCW90(farmLevel, REPAIRED_STRUCTURE_PATH, FARM_ORIGIN);
            InteriorSubspaceManager.spawnGreenhouseOutdoorPortal(farmLevel);
            repaired = true;
        } else {
            // 放置破损版（CW90 旋转）
            StardewCraft.LOGGER.info("[GREENHOUSE] Placing greenhouse ruins at {}", FARM_ORIGIN);
            StructureLoader.loadAndPlaceCW90(farmLevel, RUINS_STRUCTURE_PATH, FARM_ORIGIN);
        }

        ruinsPlaced = true;
        setDirty();
    }

    // ═══════════════════════════════════════════════════════════════
    //  季节豁免区域判定
    // ═══════════════════════════════════════════════════════════════

    /** 温室内部尺寸上限（宽松设定，确保覆盖 schem 范围） */
    private static final int INTERIOR_SIZE = 64;

    /**
     * 检查给定位置是否在任意玩家的温室内部子空间中。
     * 支持 per-player 温室室内: 遍历所有已分配的温室原点。
     */
    public static boolean isInGreenhouseInterior(Level level, BlockPos pos) {
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) return false;

        // 旧的固定位置范围检查（向后兼容）
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        if (x >= INTERIOR_ORIGIN.getX() && x < INTERIOR_ORIGIN.getX() + INTERIOR_SIZE
            && z >= INTERIOR_ORIGIN.getZ() && z < INTERIOR_ORIGIN.getZ() + INTERIOR_SIZE
            && y >= INTERIOR_ORIGIN.getY() && y < INTERIOR_ORIGIN.getY() + INTERIOR_SIZE) {
            return true;
        }

        // 检查 per-player 温室（通过 PlayerInteriorAllocator）
        if (level instanceof ServerLevel sl) {
            return com.stardew.craft.interior.PlayerInteriorAllocator.get(sl).isInsideAnyGreenhouse(pos);
        }
        return false;
    }

    /**
     * 获取指定玩家从温室出来时应传送到的位置。
     * 查找玩家的农场温室门口位置。如果没有农场则返回默认位置。
     */
    public static BlockPos getExitPosForPlayer(net.minecraft.server.level.ServerPlayer player) {
        com.stardew.craft.farm.FarmInstance farm =
            com.stardew.craft.farm.FarmInstanceRegistry.get().getFarmForPlayer(player.getUUID());
        if (farm != null) {
            // 农场温室门口（CW90后门偏移 (8,0,0)）
            return farm.getGreenhousePos().offset(8, 0, 0);
        }
        // 没有农场 → 默认温室室外位置
        return OUTDOOR_EXIT_POS;
    }

    /**
     * 检查给定位置是否在任意玩家的温室外观区域中（用于方块保护）。
     * 遍历所有已注册的农场，检查每个农场的温室位置。
     */
    public static boolean isInGreenhouseExterior(Level level, BlockPos pos) {
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) return false;

        // 检查旧公共区域温室位置
        if (isInGreenhouseExteriorRange(pos, FARM_ORIGIN)) return true;

        // 检查每个玩家农场的温室位置
        com.stardew.craft.farm.FarmInstanceRegistry registry =
                com.stardew.craft.farm.FarmInstanceRegistry.get();
        for (com.stardew.craft.farm.FarmInstance farm : registry.getAllFarms()) {
            BlockPos ghPos = farm.getGreenhousePos();
            if (isInGreenhouseExteriorRange(pos, ghPos)) return true;
        }
        return false;
    }

    private static boolean isInGreenhouseExteriorRange(BlockPos pos, BlockPos ghOrigin) {
        int x = pos.getX(), y = pos.getY(), z = pos.getZ();
        return x >= ghOrigin.getX() && x < ghOrigin.getX() + 20
            && z >= ghOrigin.getZ() && z < ghOrigin.getZ() + 18
            && y >= ghOrigin.getY() && y < ghOrigin.getY() + 20;
    }

    // ═══════════════════════════════════════════════════════════════
    //  静态初始化：注册季节豁免规则
    // ═══════════════════════════════════════════════════════════════

    private static boolean seasonRuleRegistered = false;

    /**
     * 注册温室季节豁免规则。在模组加载时调用一次。
     */
    public static void registerSeasonRule() {
        if (seasonRuleRegistered) return;
        SeasonLocationRules.registerIgnoreSeasonsRule(GreenhouseManager::isInGreenhouseInterior);
        seasonRuleRegistered = true;
        StardewCraft.LOGGER.info("[GREENHOUSE] Season immunity rule registered");
    }

    // ═══════════════════════════════════════════════════════════════
    //  SavedData 持久化
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Nonnull
    public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider provider) {
        tag.putBoolean("Repaired", repaired);
        tag.putBoolean("RuinsPlaced", ruinsPlaced);

        // Per-player greenhouse state
        CompoundTag repairedTag = new CompoundTag();
        for (Map.Entry<UUID, Boolean> entry : repairedByOwner.entrySet()) {
            repairedTag.putBoolean(entry.getKey().toString(), entry.getValue());
        }
        tag.put("RepairedByOwner", repairedTag);

        CompoundTag ruinsTag = new CompoundTag();
        for (Map.Entry<UUID, Boolean> entry : ruinsPlacedByOwner.entrySet()) {
            ruinsTag.putBoolean(entry.getKey().toString(), entry.getValue());
        }
        tag.put("RuinsPlacedByOwner", ruinsTag);

        return tag;
    }

    private static GreenhouseManager load(CompoundTag tag, HolderLookup.Provider provider) {
        GreenhouseManager mgr = new GreenhouseManager();
        mgr.repaired = tag.getBoolean("Repaired");
        mgr.ruinsPlaced = tag.getBoolean("RuinsPlaced");

        if (tag.contains("RepairedByOwner")) {
            CompoundTag repairedTag = tag.getCompound("RepairedByOwner");
            for (String key : repairedTag.getAllKeys()) {
                try {
                    mgr.repairedByOwner.put(UUID.fromString(key), repairedTag.getBoolean(key));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        if (tag.contains("RuinsPlacedByOwner")) {
            CompoundTag ruinsTag = tag.getCompound("RuinsPlacedByOwner");
            for (String key : ruinsTag.getAllKeys()) {
                try {
                    mgr.ruinsPlacedByOwner.put(UUID.fromString(key), ruinsTag.getBoolean(key));
                } catch (IllegalArgumentException ignored) {}
            }
        }

        return mgr;
    }

    public static GreenhouseManager get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(GreenhouseManager::new, GreenhouseManager::load),
            DATA_ID
        );
    }
}
