package com.stardew.craft.fishing;

import com.stardew.craft.core.FarmAreaResolver;
import com.stardew.craft.fishing.data.FishingDataManager;
import com.stardew.craft.hotspring.HotSpringAreaRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public final class WaterFeatureSpawnRules {
    private WaterFeatureSpawnRules() {}

    public static boolean canSpawnAt(ServerLevel level, BlockPos pos) {
        return !isBlockedSpawnArea(level, pos);
    }

    public static boolean isBlockedSpawnArea(ServerLevel level, BlockPos pos) {
        if (level == null || pos == null) {
            return true;
        }
        return isInHotSpringArea(level, pos)
            || FarmAreaResolver.isInFarmArea(level, pos)
            || FishingDataManager.hasBiomeTagPublic(level.getBiome(pos), "stardewcraft:is_sewers");
    }

    private static boolean isInHotSpringArea(ServerLevel level, BlockPos pos) {
        Vec3 center = Vec3.atCenterOf(pos);
        for (HotSpringAreaRegistry.Area area : HotSpringAreaRegistry.getWaterAreas(level.dimension())) {
            if (area.bounds().contains(center)) {
                return true;
            }
        }
        return false;
    }
}