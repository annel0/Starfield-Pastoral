package com.stardew.craft.mining;

import com.stardew.craft.item.ModItems;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * 矿井宝箱奖励表 — 定义每个特殊层数的一次性奖励物品。
 * 对照 SDV MineShaft.addLevelChests()。
 */
public final class MineChestLootTable {

    private MineChestLootTable() {}

    /** 该层是否应生成宝箱 */
    public static boolean isChestFloor(int floor) {
        return switch (floor) {
            // 普通矿井固定宝箱层
            case 10, 20, 40, 50, 60, 70, 80, 90, 100, 110, 120 -> true;
            // 骷髅矿井宝藏室（SDV MineShaft.isForcedChestLevel）
            case 220, 320, 420 -> true;
            default -> false;
        };
    }

    /** 该层是否为骷髅矿井宝藏室（奖励来自 {@link SkullCavernTreasurePool}，而非静态表）。 */
    public static boolean isSkullCavernTreasureFloor(int floor) {
        return floor == 220 || floor == 320 || floor == 420;
    }

    /** 该层的骷髅矿井宝箱数量（SDV：220→1，320→2，420→3）。 */
    public static int getSkullCavernChestCount(int floor) {
        return switch (floor) {
            case 220 -> 1;
            case 320 -> 2;
            case 420 -> 3;
            default -> 0;
        };
    }

    /**
     * 获取指定层数的奖励物品。
     * @return 奖励 ItemStack，非宝箱层或骷髅矿井宝藏室返回 null
     *         （骷髅矿井宝藏室请调用 {@link SkullCavernTreasurePool#roll}）
     */
    @Nullable
    @SuppressWarnings("null")
    public static ItemStack getRewardForFloor(int floor) {
        return switch (floor) {
            case 10  -> new ItemStack(ModItems.LEATHER_BOOTS.get());          // (B)506
            case 20  -> new ItemStack(ModItems.CARVING_KNIFE.get());          // (W)11
            case 40  -> new ItemStack(ModItems.CUTLASS.get());                // (W)32
            case 50  -> new ItemStack(ModItems.TUNDRA_BOOTS.get());           // (B)509
            case 60  -> new ItemStack(ModItems.CRYSTAL_DAGGER.get());         // (W)21
            case 70  -> new ItemStack(ModItems.TEMPERED_BROADSWORD.get());    // (W)33
            case 80  -> new ItemStack(ModItems.FIREWALKER_BOOTS.get());       // (B)512
            case 90  -> new ItemStack(ModItems.SHADOW_DAGGER.get());          // (W)8
            case 100 -> new ItemStack(ModItems.STARDROP.get());               // Object 434
            case 110 -> new ItemStack(ModItems.SPACE_BOOTS.get());            // (B)514
            case 120 -> new ItemStack(ModItems.SKULL_KEY.get());              // SpecialItem(4) 骷髅钥匙
            default  -> null;
        };
    }

    /** 物品放在箱子第二行正中间 (slot index 13 = row 1, col 4) */
    public static final int REWARD_SLOT = 13;
}
