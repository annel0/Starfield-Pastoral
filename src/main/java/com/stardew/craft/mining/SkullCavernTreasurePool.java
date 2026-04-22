package com.stardew.craft.mining;

import com.stardew.craft.item.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

/**
 * 骷髅矿井宝箱奖励池 (SDV MineShaft.getTreasureRoomItem())
 * <p>
 * 26 选 1 均匀分布，与原版 1:1 对齐。
 * 原版中暂未移植到本模组的物品（家具、帽子、饰品盒等）回滚到 {@code Omni Geode ×5}
 * 以保留权重结构——这是 SDV default 分支本身的掉落。
 * <p>
 * 前置 0.02 金色动物饼干 / 0.045 随机饰品分支本模组不存在 Mastery/Trinket 系统，已省略。
 */
public final class SkullCavernTreasurePool {

    private SkullCavernTreasurePool() {}

    /**
     * 从 26 选 1 池中摇出一份奖励。
     * @return 非空 ItemStack；若某 case 对应的物品未注册，回滚到 Omni Geode ×5。
     */
    public static ItemStack roll(RandomSource random) {
        return switch (random.nextInt(26)) {
            // case 0: (O)288 Pale Ale ×5
            case 0 -> stack(ModItems.PALE_ALE, 5);
            // case 1: (O)287 Beer ×10
            case 1 -> stack(ModItems.BEER, 10);
            // case 2: (O)275 Artifact Trove ×5 / 0.66 → (O)848 Cinder Shard ×5-20 (两者均未移植)
            case 2 -> omniGeodeFallback();
            // case 3: (O)773 Life Elixir ×2-4 (未移植)
            case 3 -> omniGeodeFallback();
            // case 4: (O)749 Omni Geode ×5-10
            case 4 -> stack(ModItems.OMNI_GEODE, 5 + (random.nextDouble() < 0.25 ? 5 : 0));
            // case 5: (O)688 Warp Totem: Desert ×5
            case 5 -> stack(ModItems.WARP_TOTEM_DESERT, 5);
            // case 6: (O)681 Rain Totem ×1-3
            case 6 -> stack(ModItems.RAIN_TOTEM, 1 + random.nextInt(3));
            // case 7: (O)628-633 fruit saplings (全部未移植)
            case 7 -> omniGeodeFallback();
            // case 8: (O)645 Iridium Sprinkler ×1-2
            case 8 -> stack(ModItems.IRIDIUM_SPRINKLER, 1 + random.nextInt(2));
            // case 9: (O)621 Quality Sprinkler ×4
            case 9 -> stack(ModItems.QUALITY_SPRINKLER, 4);
            // case 10: 67% random crop seed ×5-20 / 33% (O)802 Cactus Seeds ×15
            case 10 -> rollCase10(random);
            // case 11: (O)286 Cherry Bomb ×15
            case 11 -> stack(ModItems.CHERRY_BOMB, 15);
            // case 12: (O)437 Red Slime Egg / (O)265 Seafoam Pudding (均未移植)
            case 12 -> omniGeodeFallback();
            // case 13: (O)439 Purple Slime Egg (未移植)
            case 13 -> omniGeodeFallback();
            // case 14: 67% (O)349 Strange Bun ×2-4 (未移植) / 33% Spicy Eel or Crab Cakes ×5
            case 14 -> rollCase14(random);            // case 15: (O)337 Iridium Bar ×2-3
            case 15 -> stack(ModItems.IRIDIUM_BAR, 2 + random.nextInt(2));
            // case 16: 67% (O)235-244 dishes/foraging (大多数未移植) / 33% Spicy Eel or Crab Cakes ×5
            case 16 -> rollCase16(random);
            // case 17: (O)74 Prismatic Shard ×1
            case 17 -> stack(ModItems.PRISMATIC_SHARD, 1);
            // case 18: (BC)21 Crystalarium ×1
            case 18 -> stack(ModItems.CRYSTALARIUM, 1);
            // case 19: (BC)25 Mayonnaise Machine ×1
            case 19 -> stack(ModItems.MAYONNAISE_MACHINE, 1);
            // case 20: (BC)165 Auto-Grabber (未移植)
            case 20 -> omniGeodeFallback();
            // case 21: (H)37/(H)38 hats (用户规则：不做帽子)
            case 21 -> omniGeodeFallback();
            // case 22: 需 sawQiPlane 邮件 → MysteryBox×5；本模组无该邮件，走 else 分支 (O)749 ×5-10
            case 22 -> stack(ModItems.OMNI_GEODE, 5 + (random.nextDouble() < 0.25 ? 5 : 0));
            // case 23: (H)65 hat
            case 23 -> omniGeodeFallback();
            // case 24: (BC)272 Stone Owl (未移植)
            case 24 -> omniGeodeFallback();
            // case 25: (H)83 hat
            case 25 -> omniGeodeFallback();
            default -> omniGeodeFallback();
        };
    }

    /** case 10: 67% random vegetable seed ×5-20 / 33% Cactus Seeds (未移植，退回常规种子). */
    private static ItemStack rollCase10(RandomSource random) {
        int mult = (1 + random.nextInt(4)) * 5; // 5/10/15/20
        // SDV 随机 (O)472-498 区段。本模组已有的种子里按相近分布抽一个。
        Supplier<ItemLike>[] seeds = pickSeedsPool();
        ItemLike pick = seeds[random.nextInt(seeds.length)].get();
        return new ItemStack(pick, mult);
    }

    /** case 14: 67% Strange Bun (未移植) / 33% Spicy Eel or Crab Cakes ×5. */
    private static ItemStack rollCase14(RandomSource random) {
        if (random.nextDouble() < 0.33) {
            return random.nextBoolean() ? spicyEel(5) : crabCakes(5);
        }
        return omniGeodeFallback(); // Strange Bun 未移植
    }

    /** case 16: 67% (O)235-244 dishes/foraging (未移植) / 33% Spicy Eel or Crab Cakes ×5. */
    private static ItemStack rollCase16(RandomSource random) {
        if (random.nextDouble() < 0.33) {
            return random.nextBoolean() ? spicyEel(5) : crabCakes(5);
        }
        return omniGeodeFallback(); // 235-244 多为熟食，未移植
    }

    @SuppressWarnings("unchecked")
    private static Supplier<ItemLike>[] pickSeedsPool() {
        return new Supplier[] {
                ModItems.PARSNIP_SEEDS, ModItems.CAULIFLOWER_SEEDS,
                ModItems.POTATO_SEEDS, ModItems.KALE_SEEDS,
                ModItems.GARLIC_SEEDS, ModItems.BLUE_JAZZ_SEEDS
        };
    }

    private static ItemStack stack(Supplier<? extends ItemLike> supplier, int count) {
        return new ItemStack(supplier.get(), count);
    }

    private static ItemStack omniGeodeFallback() {
        return new ItemStack(ModItems.OMNI_GEODE.get(), 5);
    }

    /** Cooking dishes are registered dynamically and don't have static fields. */
    private static ItemStack spicyEel(int count) {
        var holder = ModItems.COOKING_DISHES.get("spicy_eel");
        if (holder == null) return omniGeodeFallback();
        return new ItemStack(holder.get(), count);
    }

    private static ItemStack crabCakes(int count) {
        var holder = ModItems.COOKING_DISHES.get("crab_cakes");
        if (holder == null) return omniGeodeFallback();
        return new ItemStack(holder.get(), count);
    }
}
