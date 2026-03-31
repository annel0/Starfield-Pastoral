package com.stardew.craft.block.utility;

import com.stardew.craft.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * 垃圾桶战利品表 — 对齐原版 Data/GarbageCans.json + TryGetGarbageItem() 逻辑。
 * <p>
 * 掉落优先级：BeforeAll → 位置特定 → AfterAll。
 * 每个阶段内命中第一个符合条件的条目后即停止。
 */
@SuppressWarnings("null")
public final class GarbageCanLootTable {

    private static final float DEFAULT_BASE_CHANCE = 0.2f;

    private GarbageCanLootTable() {}

    /**
     * 结果记录：包含物品和是否 Mega/DoubleMega 标识。
     */
    public record Result(ItemStack item, boolean isMegaSuccess, boolean isDoubleMegaSuccess) {}

    /**
     * 尝试为给定 canId 计算今日掉落。
     *
     * @param canId            垃圾桶标识（位置 hash 或命名 ID）
     * @param dailyLuck        玩家日运
     * @param trashCansChecked 玩家累计翻垃圾桶次数
     * @param daySeed          当日确定性种子
     * @return 掉落结果，若无掉落则返回 null
     */
    @Nullable
    public static Result tryGetItem(String canId, double dailyLuck, int trashCansChecked, long daySeed) {
        float baseChance = DEFAULT_BASE_CHANCE;
        baseChance += (float) dailyLuck;
        // 原版 Book_Trash 加成 — 暂无此物品，预留兼容
        // if (hasBookTrash) baseChance += 0.2f;

        Random rng = createGarbageRandom(canId, daySeed);

        boolean baseChancePassed = rng.nextDouble() < baseChance;

        // ---- BeforeAll ----
        Result beforeAll = evaluateBeforeAll(rng, baseChancePassed, trashCansChecked);
        if (beforeAll != null) return beforeAll;

        // ---- 位置特定物品 ----
        Result locationResult = evaluateLocation(canId, rng, baseChancePassed, dailyLuck);
        if (locationResult != null) return locationResult;

        // ---- AfterAll ----
        return evaluateAfterAll(rng, baseChancePassed, trashCansChecked);
    }

    // ==================== BeforeAll ====================

    @Nullable
    private static Result evaluateBeforeAll(Random rng, boolean baseChancePassed, int trashCansChecked) {
        // Garbage Hat: 20+ 次, 0.2% 概率, DoubleMega — 我们没有此物品，跳过
        // Trash Catalogue: 50+ 次, 0.2% 概率, DoubleMega — 我们没有此物品，跳过
        // Qi Bean: 需要特殊订单规则 DROP_QI_BEANS, 25% 概率 — 我们没有此物品，跳过
        return null;
    }

    // ==================== 位置特定 ====================

    @Nullable
    private static Result evaluateLocation(String canId, Random rng, boolean baseChancePassed, double dailyLuck) {
        if (!baseChancePassed) return null;

        return switch (canId) {
            case "Blacksmith" -> evaluateBlacksmith(rng, dailyLuck);
            case "Evelyn" -> evaluateEvelyn(rng, dailyLuck);
            case "JojaMart" -> evaluateJojaMart(rng);
            case "Museum" -> evaluateMuseum(rng, dailyLuck);
            // Saloon：DISH_OF_THE_DAY — 暂无此系统，跳过
            // EmilyAndHaley, JodiAndKent, Mayor：无特定物品
            default -> null;
        };
    }

    @Nullable
    private static Result evaluateBlacksmith(Random rng, double dailyLuck) {
        // RANDOM 0.2 @addDailyLuck
        if (rng.nextDouble() >= 0.2 + dailyLuck) return null;

        List<Item> ores = List.of(
                ModItems.EARTH_COPPER_ORE.get(),
                ModItems.EARTH_IRON_ORE.get(),
                ModItems.EARTH_GOLD_ORE.get()
        );
        Item ore = ores.get(rng.nextInt(ores.size()));
        // 数量乘数 1-4
        int count = 1 + rng.nextInt(4);
        return new Result(new ItemStack(ore, count), false, false);
    }

    @Nullable
    private static Result evaluateEvelyn(Random rng, double dailyLuck) {
        // RANDOM 0.2 @addDailyLuck
        if (rng.nextDouble() >= 0.2 + dailyLuck) return null;

        Item cookie = ModItems.COOKING_DISHES.get("cookie").get();
        return new Result(new ItemStack(cookie), false, false);
    }

    @Nullable
    private static Result evaluateJojaMart(Random rng) {
        // SYNCED_RANDOM day garbage_joja 0.2 — 简化为直接 0.2 判定
        if (rng.nextDouble() >= 0.2) return null;

        // 原版：Movie Ticket 25% / Corn 75% — 我们没有 Movie Ticket，直接给 Corn
        // 原版还有 Joja Cola 条目，但有额外条件，这里简化
        List<Item> items = List.of(
                ModItems.CORN.get(),
                ModItems.CORN.get(),
                ModItems.CORN.get(),
                ModItems.JOJA_COLA.get()
        );
        Item item = items.get(rng.nextInt(items.size()));
        return new Result(new ItemStack(item), false, false);
    }

    @Nullable
    private static Result evaluateMuseum(Random rng, double dailyLuck) {
        // 先尝试 Omni Geode (需两个条件都通过)
        boolean geodeBasePass = rng.nextDouble() < 0.2 + dailyLuck;
        boolean omniPass = rng.nextDouble() < 0.05;
        if (geodeBasePass && omniPass) {
            return new Result(new ItemStack(ModItems.OMNI_GEODE.get()), false, false);
        }
        // 普通 Geode
        if (geodeBasePass) {
            return new Result(new ItemStack(ModItems.GEODE.get()), false, false);
        }
        return null;
    }

    // ==================== AfterAll ====================

    @Nullable
    private static Result evaluateAfterAll(Random rng, boolean baseChancePassed, int trashCansChecked) {
        // MegaSuccess: 20+ 次, 1% 概率
        if (trashCansChecked >= 20 && rng.nextDouble() < 0.01) {
            Item[] megaPool = getMegaSuccessPool();
            if (megaPool.length > 0) {
                Item item = megaPool[rng.nextInt(megaPool.length)];
                return new Result(new ItemStack(item), true, false);
            }
        }

        // Fallback: 需要通过 baseChance
        if (!baseChancePassed) return null;

        Item[] fallbackPool = getFallbackPool();
        if (fallbackPool.length > 0) {
            Item item = fallbackPool[rng.nextInt(fallbackPool.length)];
            return new Result(new ItemStack(item), false, false);
        }

        return null;
    }

    // ==================== 物品池 ====================

    /**
     * AfterAll MegaSuccess 随机池。
     * <p>原版: Green Algae, Bread, Field Snack, Acorn, Maple Seed, Pine Cone, RANDOM_BASE_SEASON_ITEM
     * <p>我们有: Green Algae, Bread, Acorn, Maple Seed, Pine Cone
     * <p>缺失: Field Snack, RANDOM_BASE_SEASON_ITEM
     */
    private static Item[] getMegaSuccessPool() {
        return new Item[]{
                ModItems.GREEN_ALGAE.get(),
                ModItems.COOKING_DISHES.get("bread").get(),
                ModItems.ACORN.get(),
                ModItems.MAPLE_SEED.get(),
                ModItems.PINE_CONE.get()
        };
    }

    /**
     * AfterAll Fallback 随机池。
     * <p>原版: Green Algae, Bread, Field Snack, Acorn, Maple Seed, Pine Cone,
     *          RANDOM_BASE_SEASON_ITEM, Trash, Joja Cola, Broken Glasses, Broken CD, Soggy Newspaper
     * <p>缺失: Field Snack, RANDOM_BASE_SEASON_ITEM
     */
    private static Item[] getFallbackPool() {
        return new Item[]{
                ModItems.GREEN_ALGAE.get(),
                ModItems.COOKING_DISHES.get("bread").get(),
                ModItems.ACORN.get(),
                ModItems.MAPLE_SEED.get(),
                ModItems.PINE_CONE.get(),
                ModItems.TRASH.get(),
                ModItems.JOJA_COLA.get(),
                ModItems.BROKEN_GLASSES.get(),
                ModItems.BROKEN_CD.get(),
                ModItems.SOGGY_NEWSPAPER.get()
        };
    }

    // ==================== RNG ====================

    /**
     * 创建确定性垃圾桶 RNG。对齐原版 CreateDaySaveRandom(777 + hash(id))。
     */
    private static Random createGarbageRandom(String canId, long daySeed) {
        long seed = daySeed ^ (777L + deterministicHash(canId));
        Random rng = new Random(seed);
        // 原版 prewarm: 两轮随机预热
        int prewarm = rng.nextInt(100);
        for (int i = 0; i < prewarm; i++) rng.nextDouble();
        prewarm = rng.nextInt(100);
        for (int i = 0; i < prewarm; i++) rng.nextDouble();
        return rng;
    }

    /**
     * 确定性字符串哈希（对齐 .NET GetDeterministicHashCode）。
     */
    private static int deterministicHash(String s) {
        int hash1 = 5381;
        int hash2 = hash1;
        for (int i = 0; i < s.length(); i += 2) {
            hash1 = ((hash1 << 5) + hash1) ^ s.charAt(i);
            if (i + 1 < s.length()) {
                hash2 = ((hash2 << 5) + hash2) ^ s.charAt(i + 1);
            }
        }
        return hash1 + (hash2 * 1566083941);
    }
}
