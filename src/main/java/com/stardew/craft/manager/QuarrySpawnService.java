package com.stardew.craft.manager;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.registries.DeferredBlock;

import javax.annotation.Nonnull;

/**
 * 采石场（Quarry）每日刷新服务 — SDV Mountain.quarryDayUpdate 的 MC 等价实现。
 *
 * <p>规则（完全照抄 SDV Mountain.cs:238-321）：
 * <ul>
 *   <li>每天尝试次数：N = min(16, 5 + year × 2)</li>
 *   <li>每次尝试随机抽一个 tile，必须 Y=-12 为砂土（coarse_dirt）且 Y=-11 为空气</li>
 *   <li>概率级联（按顺序判定，首个命中即放置并跳出）：
 *     <ol>
 *       <li>6% → 树苗（随机 oak / maple 的 SAPLING1）</li>
 *       <li>2% → 矿物节点：内 10% 神秘石替代(FIRE_QUARTZ)，90% 宝石原矿（7 种均分）</li>
 *       <li>4% → 远古斑点（SDV 内有 15% 种子斑点子分支，因我们无对应方块，全部归为远古斑点）</li>
 *       <li>15% → 大矿脉：0.1% 铱 / 10% 金 / 33% 铁 / 其余 铜（用对应 earth_*_ore 替代大节点）</li>
 *       <li>10% → 煤矿（earth_coal_ore 替代 BasicCoalNode）</li>
 *       <li>兜底 → 普通石头（6 种 STARDEW_STONES 等概率）</li>
 *     </ol>
 *   </li>
 * </ul>
 *
 * <p>采石场区域：X ∈ [-493, -416]，Z ∈ [232, 299]，生成平面 Y = -12。
 */
@SuppressWarnings("null")
public final class QuarrySpawnService {

    private static final String INIT_DATA_ID = "stardewcraft_quarry_init";

    // ── 区域 ──
    private static final int AREA_MIN_X = -493;
    private static final int AREA_MAX_X = -416;
    private static final int AREA_MIN_Z = 232;
    private static final int AREA_MAX_Z = 299;
    private static final int FLOOR_Y = -14;

    // ── 普通石头：6 种 STARDEW_STONES（等价 SDV ID 32/38/40/42/668/670） ──
    private static final java.util.List<net.neoforged.neoforge.registries.DeferredBlock<Block>> PLAIN_STONES =
            java.util.List.of(
                    ModBlocks.EARTH_SHALE,
                    ModBlocks.FROST_GNEISS,
                    ModBlocks.LAVA_BASALT,
                    ModBlocks.BANDED_MARBLE,
                    ModBlocks.LIMESTONE,
                    ModBlocks.MOSSY_SANDSTONE
            );

    // ── 宝石原矿（SDV Object ID 2/4/6/8/10/12/14） ──
    private static final java.util.List<net.neoforged.neoforge.registries.DeferredBlock<Block>> GEM_ORES =
            java.util.List.of(
                    ModBlocks.AMETHYST_ORE,
                    ModBlocks.AQUAMARINE_ORE,
                    ModBlocks.DIAMOND_ORE,
                    ModBlocks.EMERALD_ORE,
                    ModBlocks.JADE_ORE,
                    ModBlocks.RUBY_ORE,
                    ModBlocks.TOPAZ_ORE
            );

    // ── 树苗（SDV: random 1/2，growthStage=1） ──
    private static final java.util.List<net.neoforged.neoforge.registries.DeferredBlock<Block>> SAPLINGS =
            java.util.List.of(
                    ModBlocks.WILD_OAK_SAPLING1,
                    ModBlocks.WILD_MAPLE_SAPLING1
            );

    private QuarrySpawnService() {}

    // ======================== 入口 ========================

    /** 每日（过夜结算）调用，从 StardewTimeManager 触发。 */
    public static void onNewDay(ServerLevel level, int year) {
        if (!level.dimension().equals(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY)) return;

        int n = Math.min(16, 5 + year * 2);
        RandomSource random = level.getRandom();
        int placed = 0;
        for (int i = 0; i < n; i++) {
            if (trySpawnOne(level, random)) placed++;
        }
        StardewCraft.LOGGER.info("[QUARRY] onNewDay year={} attempts={} placed={}", year, n, placed);
    }

    /** 初始全图铺设密度 — 每个砂土格按此概率触发一次放置尝试（与原版 hand-painted 采石场密度近似）。 */
    private static final double INITIAL_FILL_CHANCE = 0.20;

    /** 重置初始化标记，下次进入时会重新铺石头。用于 pregen region 覆盖后。 */
    public static void resetInitialSpawn(ServerLevel level) {
        if (!level.dimension().equals(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY)) return;
        QuarryInitData data = level.getDataStorage().computeIfAbsent(
                QuarryInitData.factory(), INIT_DATA_ID);
        data.resetForMigration();
    }

    /** 首次进入星露谷维度时执行初始化（老存档升级也会自动补）。幂等。 */
    public static void ensureInitialSpawn(ServerLevel level, int year) {
        if (!level.dimension().equals(com.stardew.craft.core.ModDimensions.STARDEW_VALLEY)) return;

        QuarryInitData data = level.getDataStorage().computeIfAbsent(
                QuarryInitData.factory(), INIT_DATA_ID);
        if (data.isInitialized()) return;

        StardewCraft.LOGGER.info("[QUARRY] Running initial dense spawn (year={}, fillChance={})", year, INITIAL_FILL_CHANCE);

        // 强制加载采石场覆盖的区块，保证 setBlock 不被 chunk unloaded 跳过。
        java.util.List<long[]> forced = new java.util.ArrayList<>();
        int cxMin = AREA_MIN_X >> 4, cxMax = AREA_MAX_X >> 4;
        int czMin = AREA_MIN_Z >> 4, czMax = AREA_MAX_Z >> 4;
        for (int cx = cxMin; cx <= cxMax; cx++) {
            for (int cz = czMin; cz <= czMax; cz++) {
                level.setChunkForced(cx, cz, true);
                forced.add(new long[]{cx, cz});
            }
        }

        try {
            RandomSource r = level.getRandom();
            int placed = 0, attempts = 0;
            // 全图逐格扫描：每个砂土格按概率独立滚一次，拿到 SDV 级别的密度
            for (int x = AREA_MIN_X; x <= AREA_MAX_X; x++) {
                for (int z = AREA_MIN_Z; z <= AREA_MAX_Z; z++) {
                    if (r.nextDouble() > INITIAL_FILL_CHANCE) continue;
                    attempts++;
                    if (trySpawnAt(level, r, x, z)) placed++;
                }
            }
            StardewCraft.LOGGER.info("[QUARRY] Initial dense spawn done: attempts={} placed={}", attempts, placed);
        } finally {
            for (long[] c : forced) {
                level.setChunkForced((int) c[0], (int) c[1], false);
            }
        }
        data.markInitialized();
    }

    // ======================== 核心：一次随机放置尝试 ========================

    private static boolean trySpawnOne(ServerLevel level, RandomSource r) {
        int x = AREA_MIN_X + r.nextInt(AREA_MAX_X - AREA_MIN_X + 1);
        int z = AREA_MIN_Z + r.nextInt(AREA_MAX_Z - AREA_MIN_Z + 1);
        return trySpawnAt(level, r, x, z);
    }

    private static boolean trySpawnAt(ServerLevel level, RandomSource r, int x, int z) {
        BlockPos floor = new BlockPos(x, FLOOR_Y, z);
        BlockPos above = floor.above();

        if (!level.hasChunk(x >> 4, z >> 4)) return false;
        if (!isQuarryFloor(level, floor)) return false;

        Block toPlace = pickBlock(r);
        if (toPlace == null) return false;

        // 其他方块（石头/矿石/树苗等）放在 Y=-11 地表上
        if (!isReplaceableAbove(level, above)) return false;
        level.setBlock(above, toPlace.defaultBlockState(), Block.UPDATE_ALL);
        return true;
    }

    /** 严格只在 Y=-12 砂土层生成（按用户指定）。兼容已被替换为黄土的格子，避免重复落点。 */
    private static boolean isQuarryFloor(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        // 砂土 = coarse_dirt。同时兼容普通泥土/rooted_dirt 边界异常。
        return state.is(Blocks.COARSE_DIRT) || state.is(Blocks.DIRT) || state.is(Blocks.ROOTED_DIRT);
    }

    private static boolean isReplaceableAbove(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || state.canBeReplaced();
    }

    // ======================== 概率级联（SDV parity） ========================

    private static Block pickBlock(RandomSource r) {
        // 1) 6% 树苗
        if (r.nextDouble() < 0.06) {
            return SAPLINGS.get(r.nextInt(SAPLINGS.size())).get();
        }
        // 2) 2% 矿物/宝石节点：内 10% 神秘石(→FIRE_QUARTZ)，90% 宝石原矿
        if (r.nextDouble() < 0.02) {
            if (r.nextDouble() < 0.1) {
                return ModBlocks.FIRE_QUARTZ.get();
            }
            return GEM_ORES.get(r.nextInt(GEM_ORES.size())).get();
        }
        // 3) 15% 大矿脉：0.1% 铱 / 10% 金 / 33% 铁 / 其余 铜
        if (r.nextDouble() < 0.15) {
            if (r.nextDouble() < 0.001) return ModBlocks.EARTH_IRIDIUM_ORE.get();
            if (r.nextDouble() < 0.1)   return ModBlocks.EARTH_GOLD_ORE.get();
            if (r.nextDouble() < 0.33)  return ModBlocks.EARTH_IRON_ORE.get();
            return ModBlocks.EARTH_COPPER_ORE.get();
        }
        // 4) 10% 煤矿节点
        if (r.nextDouble() < 0.1) {
            return ModBlocks.EARTH_COAL_ORE.get();
        }
        // 5) 兜底：6 种普通石头等概率
        return PLAIN_STONES.get(r.nextInt(PLAIN_STONES.size())).get();
    }

    public static boolean canPlayerBreakInQuarry(BlockState state) {
        return isQuarryResourceBlock(state) || com.stardew.craft.tree.WildTrees.isAnyWildTreePart(state);
    }

    public static boolean canBombDestroyInQuarry(BlockState state) {
        return isQuarryResourceBlock(state);
    }

    private static boolean isQuarryResourceBlock(BlockState state) {
        return state.is(ModBlocks.ARTIFACT_SPOT_DIRT.get())
                || state.is(ModBlocks.EARTH_COPPER_ORE.get())
                || state.is(ModBlocks.EARTH_COAL_ORE.get())
                || state.is(ModBlocks.EARTH_IRON_ORE.get())
                || state.is(ModBlocks.EARTH_GOLD_ORE.get())
                || state.is(ModBlocks.EARTH_IRIDIUM_ORE.get())
                || state.is(ModBlocks.FIRE_QUARTZ.get())
                || isAny(state, PLAIN_STONES)
                || isAny(state, GEM_ORES);
    }

    private static boolean isAny(BlockState state, java.util.List<DeferredBlock<Block>> blocks) {
        for (DeferredBlock<Block> block : blocks) {
            if (state.is(block.get())) {
                return true;
            }
        }
        return false;
    }

    // ======================== 持久化（首次初始化标志） ========================

    /**
     * 初始化版本号。改动采石场区域、放置密度、可生成方块表等任何会影响「初始面貌」的参数时
     * 把这个数 +1，老存档下次进入星露谷会重新铺一遍。
     */
    public static final int CURRENT_VERSION = 2;

    public static class QuarryInitData extends SavedData {
        private int initializedVersion;

        public QuarryInitData() {}

        private QuarryInitData(CompoundTag tag) {
            if (tag.contains("InitializedVersion")) {
                this.initializedVersion = tag.getInt("InitializedVersion");
            } else if (tag.contains("Initialized")) {
                // 旧存档兼容：老 Initialized=true 视为版本 1
                this.initializedVersion = tag.getBoolean("Initialized") ? 1 : 0;
            }
        }

        /** 已初始化到至少 CURRENT_VERSION 则认为无需再跑。 */
        public boolean isInitialized() { return initializedVersion >= CURRENT_VERSION; }

        public void markInitialized() {
            this.initializedVersion = CURRENT_VERSION;
            setDirty();
        }

        public void resetForMigration() {
            this.initializedVersion = 0;
            setDirty();
        }

        @Override
        @Nonnull
        public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
            tag.putInt("InitializedVersion", initializedVersion);
            return tag;
        }

        public static SavedData.Factory<QuarryInitData> factory() {
            return new SavedData.Factory<>(QuarryInitData::new, (tag, provider) -> new QuarryInitData(tag));
        }
    }
}
