package com.stardew.craft.hotspring;

import com.stardew.craft.core.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

/**
 * 温泉区域注册表。
 *
 * Phase 1 仅维护 waterBounds：玩家位于其中任一矩形 AABB 内、且脚下方块是水时，视为泡在温泉里。
 * mistBounds / musicBounds 留待 Phase 3/4 引入。
 */
public final class HotSpringAreaRegistry {

    public record Area(ResourceKey<Level> dimension, AABB bounds) {}

    private static final List<Area> WATER_BOUNDS = new ArrayList<>();
    private static final List<Area> MIST_BOUNDS = new ArrayList<>();

    static {
        // 星露谷露天温泉水域：(-31, 79, -197) ↔ (-14, 79, -187)
        // 用户口径：此范围内的水方块视为温泉水。
        registerWater(ModDimensions.STARDEW_VALLEY, -31, 79, -197, -14, 79, -187);
        // mist / 音乐覆盖区域：略大于水域，玩家靠近就有氛围。
        // 用户给定 (-32, 83, -185) ↔ (-12, 79, -198)
        registerMist(ModDimensions.STARDEW_VALLEY, -32, 79, -198, -12, 83, -185);
    }

    private HotSpringAreaRegistry() {}

    public static void registerWater(ResourceKey<Level> dim,
                                     int x1, int y1, int z1,
                                     int x2, int y2, int z2) {
        WATER_BOUNDS.add(new Area(dim, makeAabb(x1, y1, z1, x2, y2, z2)));
    }

    public static void registerMist(ResourceKey<Level> dim,
                                    int x1, int y1, int z1,
                                    int x2, int y2, int z2) {
        MIST_BOUNDS.add(new Area(dim, makeAabb(x1, y1, z1, x2, y2, z2)));
    }

    private static AABB makeAabb(int x1, int y1, int z1, int x2, int y2, int z2) {
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxX = Math.max(x1, x2) + 1.0;
        double maxY = Math.max(y1, y2) + 1.0;
        double maxZ = Math.max(z1, z2) + 1.0;
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * 玩家是否位于温泉水中：
     * 1. 维度匹配
     * 2. 玩家位置在某个 waterBounds AABB 内
     * 3. 玩家脚下方块的流体为水
     */
    public static boolean isInHotSpringWater(ServerPlayer player) {
        ResourceKey<Level> dim = player.level().dimension();
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        for (Area area : WATER_BOUNDS) {
            if (!area.dimension.equals(dim)) continue;
            if (!area.bounds.contains(px, py, pz)) continue;
            BlockPos foot = BlockPos.containing(px, py, pz);
            BlockState state = player.level().getBlockState(foot);
            if (state.getFluidState().is(Fluids.WATER)
                || state.getFluidState().is(Fluids.FLOWING_WATER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 客户端友好版本：仅检查坐标是否落在 mist 范围内。
     * 用于音乐 / overlay 触发判定（不要求方块校验）。
     */
    public static boolean isInMistArea(ResourceKey<Level> dim, double x, double y, double z) {
        for (Area area : MIST_BOUNDS) {
            if (!area.dimension.equals(dim)) continue;
            if (area.bounds.contains(x, y, z)) return true;
        }
        return false;
    }

    /** 客户端用于在水面采样生成 3D 蒸汽粒子。 */
    public static List<Area> getWaterAreas(ResourceKey<Level> dim) {
        List<Area> out = new ArrayList<>();
        for (Area area : WATER_BOUNDS) {
            if (area.dimension.equals(dim)) out.add(area);
        }
        return out;
    }
}
