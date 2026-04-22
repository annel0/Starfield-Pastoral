package com.stardew.craft.spawner;

import com.stardew.craft.block.crop.StardewCropBlock;
import com.stardew.craft.block.crop.CauliflowerCropBlock;
import com.stardew.craft.block.crop.MelonCropBlock;
import com.stardew.craft.block.crop.PumpkinCropBlock;
import com.stardew.craft.block.crop.PowderMelonCropBlock;
import com.stardew.craft.block.ModBlocks;
import com.stardew.craft.block.crop.giant.GiantCropBlock;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * 巨型作物生成器（每日生长结算后调用）。
 *
 * SDV 行为对齐：
 * <ul>
 *   <li>1% 概率（per-tile per-day per-cropId 确定性 RNG）</li>
 *   <li>触发 tile 视为 3×3 中心；9 格必须全部为同种成熟作物</li>
 *   <li>满足后清空 9 格 -> 在中心放置 GiantCropBlock 主块（+8 个上方扩展格）</li>
 * </ul>
 */
public final class GiantCropSpawner {

    /** 每日 spawn 概率（SDV 默认 0.01）。 */
    public static final double SPAWN_CHANCE = 0.01;

    private GiantCropSpawner() {}

    /**
     * 在给定的成熟作物 tile 处尝试 roll spawn。tile 视为 3×3 区域的**中心格**。
     * 必须由调用者保证 cropBlock state 此时 AGE == MAX_AGE。
     */
    public static void tryRoll(ServerLevel level, BlockPos centerPos, StardewCropBlock cropBlock) {
        GiantCropBlock giant = resolveGiantBlock(cropBlock);
        if (giant == null) {
            return;
        }

        // 多格作物（甜瓜/南瓜等 2 高 DOUBLE_BLOCK_HALF）：归一化到 LOWER（根部），
        // 否则中心格落到 UPPER（y+1）会让巨型作物悬空生成；同时避免 UPPER/LOWER 双 roll。
        BlockState centerState = level.getBlockState(centerPos);
        if (centerState.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            if (centerState.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF)
                    == net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER) {
                // 仅由 LOWER 触发；防止上下两半同一天 roll 两次。
                return;
            }
        }

        // 确定性 RNG：(year, season, day) ^ pos.asLong ^ giantId.hashCode
        RandomSource rng = createDeterministicRng(centerPos, giant);
        if (rng.nextDouble() >= SPAWN_CHANCE) {
            return;
        }

        // 检查 3×3 范围内 9 格是否全部为同种成熟作物
        if (!isAllMatureCropsAround(level, centerPos, cropBlock.getClass())) {
            return;
        }

        // 检查上方 3×3 (y+1) 是否可被替换（避免压坏建筑/方块）
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos above = centerPos.offset(dx, 1, dz);
                BlockState s = level.getBlockState(above);
                // 同种作物的 UPPER 半也算"自家"，可以一并清理。
                if (cropBlock.getClass().isInstance(s.getBlock())) {
                    continue;
                }
                if (!s.isAir() && !s.canBeReplaced()) {
                    return;
                }
            }
        }

        // 移除 9 株作物 + 上方 3×3 也清空（包含同种作物 UPPER 半 / 可替换的草等）
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos above = centerPos.offset(dx, 1, dz);
                BlockState aboveState = level.getBlockState(above);
                if (cropBlock.getClass().isInstance(aboveState.getBlock())
                        || (!aboveState.isAir() && aboveState.canBeReplaced())) {
                    level.removeBlock(above, false);
                }
                BlockPos crop = centerPos.offset(dx, 0, dz);
                level.removeBlock(crop, false);
            }
        }

        // 放置巨型作物（main + 17 个 extension）
        giant.placeFootprint(level, centerPos);
    }

    private static boolean isAllMatureCropsAround(ServerLevel level, BlockPos center,
                                                   Class<? extends StardewCropBlock> expectedClass) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos p = center.offset(dx, 0, dz);
                BlockState s = level.getBlockState(p);
                if (!expectedClass.isInstance(s.getBlock())) {
                    return false;
                }
                if (!s.hasProperty(StardewCropBlock.AGE)) {
                    return false;
                }
                if (s.getValue(StardewCropBlock.AGE) != StardewCropBlock.MAX_AGE) {
                    return false;
                }
                // 多格作物：要求 9 格全部是 LOWER，确保它们都站在同一 y 平面（不会一半 LOWER 一半 UPPER）。
                if (s.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF)
                        && s.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF)
                        != net.minecraft.world.level.block.state.properties.DoubleBlockHalf.LOWER) {
                    return false;
                }
            }
        }
        return true;
    }

    private static RandomSource createDeterministicRng(BlockPos pos, GiantCropBlock giant) {
        StardewTimeManager tm = StardewTimeManager.get();
        long absDay = tm == null ? 0L
                : ((long) (tm.getCurrentYear() - 1) * 112L
                    + (long) tm.getCurrentSeason() * 28L
                    + (long) tm.getCurrentDay());
        long seed = absDay * 0x9E3779B97F4A7C15L
                ^ pos.asLong()
                ^ ((long) giant.getClass().hashCode() << 16);
        return RandomSource.create(seed);
    }

    @Nullable
    private static GiantCropBlock resolveGiantBlock(StardewCropBlock cropBlock) {
        if (cropBlock instanceof CauliflowerCropBlock) {
            return (GiantCropBlock) ModBlocks.GIANT_CAULIFLOWER.get();
        }
        if (cropBlock instanceof MelonCropBlock) {
            return (GiantCropBlock) ModBlocks.GIANT_MELON.get();
        }
        if (cropBlock instanceof PumpkinCropBlock) {
            return (GiantCropBlock) ModBlocks.GIANT_PUMPKIN.get();
        }
        if (cropBlock instanceof PowderMelonCropBlock) {
            return (GiantCropBlock) ModBlocks.GIANT_POWDERMELON.get();
        }
        return null;
    }
}
