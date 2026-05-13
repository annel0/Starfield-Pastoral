package com.stardew.craft.warp;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * 传送魔杖目的地数据。
 *
 * @param id        唯一标识符，如 "farm"
 * @param nameKey   翻译 key，如 "stardewcraft.warp.farm"
 * @param descKey   描述翻译 key，如 "stardewcraft.warp.farm.desc"
 * @param cost      解锁价格（金币），0 = 默认解锁
 * @param x         目标 X 坐标
 * @param y         目标 Y 坐标
 * @param z         目标 Z 坐标
 * @param dimension 目标维度
 * @param requiresPlayerFarm 是否需要动态解析玩家所属农场
 */
public record WarpDestination(
        String id,
        String nameKey,
        String descKey,
        int cost,
        double x, double y, double z,
        ResourceKey<Level> dimension,
        boolean requiresPlayerFarm
) {
    public WarpDestination(String id, String nameKey, String descKey, int cost,
                           double x, double y, double z, ResourceKey<Level> dimension) {
        this(id, nameKey, descKey, cost, x, y, z, dimension, false);
    }

    /** 是否默认解锁（cost == 0） */
    public boolean isFreeByDefault() {
        return cost <= 0;
    }
}
