package com.stardew.craft.player;

import com.stardew.craft.combat.equipment.EquipmentResolver;
import com.stardew.craft.combat.equipment.EquipmentStats;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

/**
 * 星露谷发光戒指实现：在玩家周围放置/清理隐形光源方块。
 *
 * SDV 原版行为：
 * - Small Glow Ring (516): lightLevel 5, 半径约 5 格
 * - Glow Ring (517): lightLevel 10, 半径约 10 格
 * - Iridium Band (527): lightLevel 10 + 磁力 + 攻击
 * - Glowstone Ring (888): lightLevel 10 + 磁力
 *
 * MC 实现：使用 light 方块（waterlogged=false, level=N）投放到玩家附近的空气格。
 * 每 4 tick 更新一次，移动或卸下戒指时清理旧光源。
 */
@SuppressWarnings("null")
public final class PlayerGlowHandler {

    private PlayerGlowHandler() {}

    private static final int UPDATE_INTERVAL = 4;
    /** 每个玩家上一次放置的光源位置 */
    private static final Map<UUID, Set<BlockPos>> ACTIVE_LIGHTS = new WeakHashMap<>();

    /**
     * 服务端 PlayerTick 中调用。
     */
    public static void tick(ServerPlayer player) {
        if (player.isSpectator() || player.isRemoved()) {
            cleanup(player);
            return;
        }

        if (player.tickCount % UPDATE_INTERVAL != 0) return;

        EquipmentStats stats = EquipmentResolver.getMergedStats(player);
        int lightLevel = stats.getLightLevel();

        if (lightLevel <= 0) {
            cleanup(player);
            return;
        }

        // 限制光源等级 1-15
        lightLevel = Math.min(15, Math.max(1, lightLevel));

        ServerLevel level = player.serverLevel();
        BlockPos center = player.blockPosition();

        // SDV: Small Glow 5 格, Glow/Iridium/Glowstone 10 格
        // MC 中光源衰减 1/格，所以扩散是自然的 — 只需放中心 + 少量扩展点
        int radius = lightLevel >= 10 ? 4 : 2;

        Set<BlockPos> newPositions = new HashSet<>();
        BlockState lightState = Blocks.LIGHT.defaultBlockState()
                .setValue(LightBlock.LEVEL, lightLevel);

        // 在玩家周围十字 + 角落放光源（不密铺整个区域避免性能问题）
        for (int dx = -radius; dx <= radius; dx += radius > 0 ? 2 : 1) {
            for (int dz = -radius; dz <= radius; dz += radius > 0 ? 2 : 1) {
                for (int dy = -1; dy <= 1; dy += 2) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    if (!level.isLoaded(pos)) continue;
                    BlockState existing = level.getBlockState(pos);
                    if (existing.isAir()) {
                        level.setBlock(pos, lightState, 3); // UPDATE_NEIGHBORS + UPDATE_CLIENTS → 触发光照引擎
                        newPositions.add(pos);
                    } else if (existing.is(Blocks.LIGHT)) {
                        // 更新光源等级（可能换了戒指）
                        if (existing.getValue(LightBlock.LEVEL) != lightLevel) {
                            level.setBlock(pos, lightState, 3);
                        }
                        newPositions.add(pos);
                    }
                }
            }
        }
        // 中心点也放
        if (level.isLoaded(center)) {
            BlockState existing = level.getBlockState(center);
            if (existing.isAir()) {
                level.setBlock(center, lightState, 3);
                newPositions.add(center);
            } else if (existing.is(Blocks.LIGHT)) {
                if (existing.getValue(LightBlock.LEVEL) != lightLevel) {
                    level.setBlock(center, lightState, 3);
                }
                newPositions.add(center);
            }
        }

        // 清理上一帧放过但现在不需要的光源
        Set<BlockPos> oldPositions = ACTIVE_LIGHTS.get(player.getUUID());
        if (oldPositions != null) {
            for (BlockPos old : oldPositions) {
                if (!newPositions.contains(old) && level.isLoaded(old)) {
                    BlockState bs = level.getBlockState(old);
                    if (bs.is(Blocks.LIGHT)) {
                        level.setBlock(old, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }

        ACTIVE_LIGHTS.put(player.getUUID(), newPositions);
    }

    /**
     * 清理所有该玩家的光源方块。
     */
    public static void cleanup(ServerPlayer player) {
        Set<BlockPos> positions = ACTIVE_LIGHTS.remove(player.getUUID());
        if (positions == null || positions.isEmpty()) return;

        ServerLevel level = player.serverLevel();
        for (BlockPos pos : positions) {
            if (level.isLoaded(pos)) {
                BlockState bs = level.getBlockState(pos);
                if (bs.is(Blocks.LIGHT)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    /**
     * 玩家登出/切维度时调用。
     */
    public static void onPlayerLeave(ServerPlayer player) {
        cleanup(player);
    }
}
