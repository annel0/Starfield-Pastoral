package com.stardew.craft.mining;

import com.stardew.craft.StardewCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * 矿井坐标管理器 - 按照 Implementation Plan 管理层级坐标
 * 
 * 坐标规则：
 * - 0层：大厅中心 (0, 64, 0)，出生点 (0, 66, -8) 面朝北
 * - 其他层：中心 (0, 66, floor×100+14)
 * 
 * 示例：
 * - 大厅（0层）：中心 (0, 64, 0)
 * - 1层：中心 (0, 64, 100)
 * - 2层：中心 (0, 64, 200)
 * - 40层：中心 (0, 64, 4000)
 */
public class MiningCoordinates {
    
    // 固定坐标
    public static final int FIXED_X = 0;
    public static final int FIXED_Y = 66;
    
    // 层间距
    public static final int FLOOR_SPACING = 500;
    
    /**
     * 获取指定层数的中心坐标
     */
    public static BlockPos getFloorCenter(int floor) {
        if (floor == 0) {
            return new BlockPos(FIXED_X, 64, 0);
        }
        int z = floor * FLOOR_SPACING + 14;
        return new BlockPos(FIXED_X, FIXED_Y, z);
    }
    
    /**
      * 传送玩家到指定层数
      * 传送到安全区中心
     */
    @SuppressWarnings("null")
    public static void teleportPlayerToFloor(ServerPlayer player, ServerLevel level, int floor) {
    double x = 0.5;
    double y = (floor == 0) ? 66.0 : 66.0;
    int z = (floor == 0) ? -8 : floor * FLOOR_SPACING + 14;
          double z_pos = z + 0.5;
        
        player.teleportTo(level, x, y, z_pos, 180.0f, 0.0f);

        StardewCraft.LOGGER.info("[MINE] Teleported player {} to floor {} at ({}, {}, {})", 
            player.getName().getString(), floor, x, y, z);
    }
    
    /**
     * 获取结构生成的起始位置（左下角）
     * 假设结构是 20x20 到 50x50 的正方形，以中心点为基准
     * 这个需要根据你实际的 NBT 结构大小调整
     */
    public static BlockPos getStructureOrigin(int floor, int structureSize) {
        BlockPos center = getFloorCenter(floor);
        int offset = structureSize / 2;
        return center.offset(-offset, 0, -offset);
    }
}
