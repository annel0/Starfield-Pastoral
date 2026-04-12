package com.stardew.craft.dimension;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.decor.MapDecorStaticBlock;
import com.stardew.craft.block.nature.PastureGrassBlock;
import com.stardew.craft.block.nature.WildWeedsBlock;
import com.stardew.craft.blockentity.MailboxBlockEntity;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.tree.WildTrees;
import com.stardew.craft.tree.preset.TreePresetPlacer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 农场初始化器 — 首次进入星露谷维度时放置默认方块（信箱、出货箱等）
 * 以及自然生成物（树木、杂草、石头、牧草、树苗）。
 * 使用 SavedData 保证仅执行一次。
 *
 * SDV 标准农场 Paths 层分布（80×65 = 5200 格）：
 *   树木 98（橡28/枫29/松41）, 杂草 492, 石头 449,
 *   牧草 390, 树苗 21, 大型资源 39
 *
 * 我们的区域：X 127→185, Z 36→145, Y -12→-2（59×110 ≈ 6490 格）
 * 按比例放大，放置在黄土 (YELLOW_DIRT) 上方。
 */
@SuppressWarnings("null")
public class FarmInitializer {

    private static final String DATA_ID = "stardewcraft_farm_init";

    // Default mailbox at (141, -12, 129)
    private static final BlockPos MAILBOX_POS = new BlockPos(141, -12, 129);
    // Default shipping bin at (139, -12, 135)
    private static final BlockPos SHIPPING_BIN_POS = new BlockPos(139, -12, 135);

    // ── Spawn region (inclusive, matches FARM_BOUNDS from TotemPoleBlock) ──
    private static final int SPAWN_MIN_X = 103;
    private static final int SPAWN_MAX_X = 311;
    private static final int SPAWN_MIN_Z = 37;
    private static final int SPAWN_MAX_Z = 154;

    // ── Spawn counts (SDV parity, scaled for ~24600-tile area, ×1.7 density) ──
    private static final int TREE_COUNT = 170;
    private static final int WEED_COUNT = 850;
    private static final int STONE_COUNT = 765;
    private static final int GRASS_COUNT = 680;
    private static final int SAPLING_COUNT = 34;

    // ── Exclusion: keep area around spawn/buildings clear ──
    private static final int CLEAR_RADIUS = 5;

    /**
     * 首次进入星露谷维度时调用。确保信箱/出货箱 + 自然碎片已放置。
     * 会主动预加载农场区域的区块，保证 heightmap 可用。
     */
    public static void ensureInitialized(ServerLevel level) {
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) return;

        FarmInitData data = level.getDataStorage().computeIfAbsent(
                FarmInitData.factory(), DATA_ID);

        if (data.isInitialized()) return;

        // 预加载农场区域的所有区块，确保 heightmap / getBlockState 能正确工作
        preloadFarmChunks(level);

        placeDefaultBlocks(level);
        boolean debrisPlaced = spawnNaturalDebris(level);

        if (debrisPlaced) {
            data.markInitialized();
            StardewCraft.LOGGER.info("[FARM_INIT] Farm initialization complete (blocks + natural debris)");
        } else {
            StardewCraft.LOGGER.warn("[FARM_INIT] No debris placed — will retry on next entry");
        }
    }

    /**
     * 预加载农场生成区域覆盖的所有区块。
     */
    private static void preloadFarmChunks(ServerLevel level) {
        int minCX = SPAWN_MIN_X >> 4;
        int maxCX = SPAWN_MAX_X >> 4;
        int minCZ = SPAWN_MIN_Z >> 4;
        int maxCZ = SPAWN_MAX_Z >> 4;
        int count = 0;
        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                level.getChunk(cx, cz); // force-load
                count++;
            }
        }
        StardewCraft.LOGGER.info("[FARM_INIT] Pre-loaded {} chunks for farm area", count);
    }

    private static void placeDefaultBlocks(ServerLevel level) {
        // Place mailbox (no facing, PART=MAIN + EXTENSION above)
        placeMailbox(level, MAILBOX_POS);

        // Place shipping bin facing north
        placeShippingBin(level, SHIPPING_BIN_POS, Direction.NORTH);
    }

    private static void placeMailbox(ServerLevel level, BlockPos pos) {
        BlockState mainState = ModBlocks.MAILBOX.get().defaultBlockState()
                .setValue(MapDecorStaticBlock.FACING, Direction.WEST)
                .setValue(MapDecorStaticBlock.PART, MapDecorStaticBlock.Part.MAIN);
        BlockState extState = mainState.setValue(MapDecorStaticBlock.PART, MapDecorStaticBlock.Part.EXTENSION);

        level.setBlock(pos, mainState, 3);
        level.setBlock(pos.above(), extState, 3);

        // Mark as system block (indestructible)
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MailboxBlockEntity mailbox) {
            mailbox.setSystemBlock(true);
        }
    }

    private static void placeShippingBin(ServerLevel level, BlockPos pos, Direction facing) {
        BlockState state = ModBlocks.SHIPPING_BIN.get().defaultBlockState();
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
        }
        level.setBlock(pos, state, 3);
    }

    // ── Natural debris spawning ──

    /**
     * 在黄土地面上随机生成树木、杂草、石头、牧草、树苗。
     * 仿照 SDV Farm.tmx Paths 层的分布密度和空间规律。
     */
    /**
     * @return true if debris was placed successfully
     */
    private static boolean spawnNaturalDebris(ServerLevel level) {
        RandomSource random = level.getRandom();

        // 1) 收集所有可放置的黄土表面位置（上方为空气、露天）
        List<BlockPos> candidates = collectOpenYellowDirtPositions(level);
        if (candidates.isEmpty()) {
            StardewCraft.LOGGER.warn("[FARM_INIT] No open yellow_dirt found in spawn region!");
            return false;
        }

        StardewCraft.LOGGER.info("[FARM_INIT] Found {} candidate positions for debris", candidates.size());

        // 一次性 shuffle，所有生成物共用同一个洗牌列表
        Collections.shuffle(candidates, new java.util.Random(random.nextLong()));

        // 用 Set 追踪已用位置（O(1) 查询），避免 ArrayList.remove 的 O(n) 代价
        Set<BlockPos> used = new HashSet<>();

        // 2) 先放树（树最大，需要空间，先占位避免和其它东西重叠）
        int treesPlaced = spawnTrees(level, random, candidates, used);

        // 3) 标记树木周围位置为已用（树放置后其周围位置可能不再是空气）
        //    不需要重新扫描——直接在后续方法中检测 isAir 即可

        // 4) 石头（用大地页岩和苔藓砂岩）
        int stonesPlaced = spawnSimpleBlocks(level, random, candidates, used, STONE_COUNT,
                new Block[]{ ModBlocks.EARTH_SHALE.get(), ModBlocks.MOSSY_SANDSTONE.get() });

        // 5) 杂草
        int weedsPlaced = spawnWeeds(level, random, candidates, used);

        // 6) 牧草
        int grassPlaced = spawnGrass(level, random, candidates, used);

        // 7) 树苗
        int saplingsPlaced = spawnSaplings(level, random, candidates, used);

        StardewCraft.LOGGER.info("[FARM_INIT] Spawned: trees={}, stones={}, weeds={}, grass={}, saplings={}",
                treesPlaced, stonesPlaced, weedsPlaced, grassPlaced, saplingsPlaced);
        return true;
    }

    /**
     * 收集区域内所有"露天黄土"位置：
     * 从 Y=100 向下扫描到 Y=-64，找到第一个 YELLOW_DIRT 即停。
     * 不依赖 heightmap（prebuilt .mca 中模组方块的 heightmap 可能不可靠）。
     * - 方块是 YELLOW_DIRT
     * - 上方是空气
     * - 上方能看到天空（无屋顶遮挡）
     * - 不在出生点/信箱/出货箱附近（CLEAR_RADIUS）
     * 返回的 pos 是黄土方块上方一格（放置物的位置）。
     */
    private static final int SCAN_Y_MAX = 100;
    private static final int SCAN_Y_MIN = -64;

    private static List<BlockPos> collectOpenYellowDirtPositions(ServerLevel level) {
        List<BlockPos> result = new ArrayList<>();
        Block yellowDirt = ModBlocks.YELLOW_DIRT.get();
        BlockPos spawnPos = new BlockPos(150, -12, 119);

        int scannedColumns = 0;
        int yellowDirtFound = 0;

        for (int x = SPAWN_MIN_X; x <= SPAWN_MAX_X; x++) {
            for (int z = SPAWN_MIN_Z; z <= SPAWN_MAX_Z; z++) {
                scannedColumns++;
                // 排除出生点/信箱/出货箱附近
                if (isNearProtected(x, z, spawnPos)) continue;

                // 从上往下扫描，找第一个非空气方块
                for (int y = SCAN_Y_MAX; y >= SCAN_Y_MIN; y--) {
                    BlockPos groundPos = new BlockPos(x, y, z);
                    BlockState groundState = level.getBlockState(groundPos);
                    if (groundState.isAir()) continue;

                    // 找到了第一个非空气方块
                    if (groundState.getBlock() == yellowDirt) {
                        yellowDirtFound++;
                        BlockPos abovePos = groundPos.above();
                        if (level.getBlockState(abovePos).isAir() && level.canSeeSky(abovePos)) {
                            result.add(abovePos);
                        }
                    }
                    break; // 不管是不是黄土，都停止该列的扫描
                }
            }
        }

        StardewCraft.LOGGER.info("[FARM_INIT] Scanned {} columns, found {} yellow_dirt surfaces, {} eligible positions",
                scannedColumns, yellowDirtFound, result.size());
        return result;
    }

    private static boolean isNearProtected(int x, int z, BlockPos spawnPos) {
        // 出生点附近
        if (Math.abs(x - spawnPos.getX()) <= CLEAR_RADIUS && Math.abs(z - spawnPos.getZ()) <= CLEAR_RADIUS) {
            return true;
        }
        // 信箱附近
        if (Math.abs(x - MAILBOX_POS.getX()) <= 2 && Math.abs(z - MAILBOX_POS.getZ()) <= 2) {
            return true;
        }
        // 出货箱附近
        if (Math.abs(x - SHIPPING_BIN_POS.getX()) <= 2 && Math.abs(z - SHIPPING_BIN_POS.getZ()) <= 2) {
            return true;
        }
        return false;
    }

    /**
     * 放置成年树（橡/枫/松混合，比例参照 SDV: 28:29:41 ≈ 3:3:4）。
     * 树之间保持最小距离 4 格以避免重叠。
     */
    private static int spawnTrees(ServerLevel level, RandomSource random,
                                    List<BlockPos> candidates, Set<BlockPos> used) {
        if (candidates.isEmpty()) return 0;

        WildTrees.Def[] treeDefs = { WildTrees.OAK, WildTrees.MAPLE, WildTrees.PINE };
        int[] weights = { 3, 3, 4 }; // 累积: oak 30%, maple 30%, pine 40%
        int totalWeight = 10;

        List<BlockPos> treeTrunks = new ArrayList<>(); // 已放置的树位置
        int placed = 0;

        for (BlockPos pos : candidates) {
            if (placed >= TREE_COUNT) break;
            if (used.contains(pos)) continue;

            // 最小间隔 4 格
            if (tooCloseToAny(pos, treeTrunks, 4)) continue;

            // 选树种
            int roll = random.nextInt(totalWeight);
            int cumulative = 0;
            WildTrees.Def chosen = treeDefs[0];
            for (int i = 0; i < treeDefs.length; i++) {
                cumulative += weights[i];
                if (roll < cumulative) {
                    chosen = treeDefs[i];
                    break;
                }
            }

            // 尝试用 preset 放置完整的成年树
            if (TreePresetPlacer.placeFromConfigOrNull(level, pos, chosen)) {
                treeTrunks.add(pos);
                used.add(pos);
                placed++;
            }
        }
        return placed;
    }

    private static boolean tooCloseToAny(BlockPos pos, List<BlockPos> existing, int minDist) {
        for (BlockPos e : existing) {
            int dx = Math.abs(pos.getX() - e.getX());
            int dz = Math.abs(pos.getZ() - e.getZ());
            if (dx <= minDist && dz <= minDist) return true;
        }
        return false;
    }

    /**
     * 放置简单方块（石头等）在黄土上方。
     * 仅在上方是空气时放置，放完后从候选列表中移除。
     */
    private static int spawnSimpleBlocks(ServerLevel level, RandomSource random,
                                          List<BlockPos> candidates, Set<BlockPos> used,
                                          int count, Block[] blocks) {
        int placed = 0;
        for (BlockPos pos : candidates) {
            if (placed >= count) break;
            if (used.contains(pos)) continue;
            if (!level.getBlockState(pos).isAir()) continue;

            Block block = blocks[random.nextInt(blocks.length)];
            level.setBlock(pos, block.defaultBlockState(), 3);
            used.add(pos);
            placed++;
        }
        return placed;
    }

    /**
     * 放置杂草（WildWeedsBlock），季节和变体根据当前季节设定。
     */
    private static int spawnWeeds(ServerLevel level, RandomSource random,
                                    List<BlockPos> candidates, Set<BlockPos> used) {
        int season = StardewTimeManager.get().getCurrentSeason();
        season = Math.max(0, Math.min(3, season));

        int placed = 0;
        for (BlockPos pos : candidates) {
            if (placed >= WEED_COUNT) break;
            if (used.contains(pos)) continue;
            if (!level.getBlockState(pos).isAir()) continue;

            int variant = (season == 3) ? 0 : random.nextInt(3);
            BlockState state = ModBlocks.WILD_WEEDS.get().defaultBlockState()
                    .setValue(WildWeedsBlock.SEASON, season)
                    .setValue(WildWeedsBlock.VARIANT, variant);
            level.setBlock(pos, state, 3);
            used.add(pos);
            placed++;
        }
        return placed;
    }

    /**
     * 放置牧草丛（PastureGrassBlock），随机变体。
     */
    private static int spawnGrass(ServerLevel level, RandomSource random,
                                    List<BlockPos> candidates, Set<BlockPos> used) {
        int placed = 0;
        for (BlockPos pos : candidates) {
            if (placed >= GRASS_COUNT) break;
            if (used.contains(pos)) continue;
            if (!level.getBlockState(pos).isAir()) continue;

            int variant = random.nextInt(3);
            BlockState state = ModBlocks.PASTURE_GRASS.get().defaultBlockState()
                    .setValue(PastureGrassBlock.VARIANT, variant);
            level.setBlock(pos, state, 3);
            used.add(pos);
            placed++;
        }
        return placed;
    }

    /**
     * 放置树苗（随机橡/枫/松，stage 0 或 1）。
     */
    private static int spawnSaplings(ServerLevel level, RandomSource random,
                                      List<BlockPos> candidates, Set<BlockPos> used) {
        WildTrees.Def[] treeDefs = { WildTrees.OAK, WildTrees.MAPLE, WildTrees.PINE };

        int placed = 0;
        for (BlockPos pos : candidates) {
            if (placed >= SAPLING_COUNT) break;
            if (used.contains(pos)) continue;
            if (!level.getBlockState(pos).isAir()) continue;

            WildTrees.Def def = treeDefs[random.nextInt(treeDefs.length)];
            Block sapling = random.nextBoolean() ? def.sapling0().get() : def.sapling1().get();
            level.setBlock(pos, sapling.defaultBlockState(), 3);
            used.add(pos);
            placed++;
        }
        return placed;
    }

    // ── SavedData ──

    public static class FarmInitData extends SavedData {
        private boolean initialized;

        public FarmInitData() {
        }

        private FarmInitData(CompoundTag tag) {
            this.initialized = tag.getBoolean("Initialized");
        }

        public boolean isInitialized() {
            return initialized;
        }

        public void markInitialized() {
            this.initialized = true;
            setDirty();
        }

        @Override
        @Nonnull
        public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
            tag.putBoolean("Initialized", initialized);
            return tag;
        }

        public static SavedData.Factory<FarmInitData> factory() {
            return new SavedData.Factory<>(FarmInitData::new, (tag, provider) -> new FarmInitData(tag));
        }
    }
}
