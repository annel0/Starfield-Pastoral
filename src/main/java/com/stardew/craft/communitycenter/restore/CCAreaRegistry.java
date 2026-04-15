package com.stardew.craft.communitycenter.restore;

import com.stardew.craft.interior.InteriorSubspaceManager;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 社区中心各区域的坐标注册表。
 * <p>
 * 用户提供的绝对坐标（建造世界 F3 坐标）已换算为 schem 相对坐标，
 * 在运行时加上 {@link InteriorSubspaceManager#CC_ORIGIN} 即可得到服务端远坐标。
 * <p>
 * 换算公式: relative = absolute - CC_SCHEM_POS1
 *   其中 CC_SCHEM_POS1 = (-146, 101, -1333)
 */
@SuppressWarnings("null")
public final class CCAreaRegistry {

    private CCAreaRegistry() {}

    // ── schem 整体 ──
    // pos1: (-146, 101, -1333) → relative (0, 0, 0)
    // pos2: (-124, 108, -1265) → relative (22, 7, 68)

    /** Junimo 小屋入口位置（相对 schem 原点） — 用于 Junimo 搬运动画目的地 */
    public static final BlockPos JUNIMO_HUT_ENTRANCE_OFFSET = new BlockPos(8, 3, 45);

    // ── 区域定义 ──

    public record AreaBounds(
        /** 区域ID (0=Pantry, 1=CraftsRoom, 2=FishTank, 3=BoilerRoom, 4=Vault, 5=Bulletin) */
        int areaId,
        /** 区域最小角（相对 schem 原点），null 表示无独立区域 */
        @Nullable BlockPos boundsMin,
        /** 区域最大角（相对 schem 原点），null 表示无独立区域 */
        @Nullable BlockPos boundsMax,
        /** JunimoNote 方块放置位置（相对 schem 原点） */
        BlockPos noteOffset
    ) {
        /** 此区域是否有独立的房间边界（用于分步修复） */
        public boolean hasRoomBounds() {
            return boundsMin != null && boundsMax != null;
        }

        /** 获取 JunimoNote 在服务端远坐标系的绝对位置 */
        public BlockPos noteWorldPos() {
            return InteriorSubspaceManager.CC_ORIGIN.offset(noteOffset);
        }

        /** 获取区域最小角在服务端远坐标系的绝对位置 */
        @Nullable
        public BlockPos boundsMinWorld() {
            return boundsMin == null ? null : InteriorSubspaceManager.CC_ORIGIN.offset(boundsMin);
        }

        /** 获取区域最大角在服务端远坐标系的绝对位置 */
        @Nullable
        public BlockPos boundsMaxWorld() {
            return boundsMax == null ? null : InteriorSubspaceManager.CC_ORIGIN.offset(boundsMax);
        }
    }

    private static final Map<Integer, AreaBounds> AREAS = new HashMap<>();

    static {
        // 0 = 茶水间 (Pantry)
        // 绝对: (-146,108,-1265) to (-139,103,-1284), note at (-142,104,-1279)
        // 相对: (0,7,68) to (7,2,49) → min(0,2,49), max(7,7,68)
        AREAS.put(0, new AreaBounds(0,
            new BlockPos(0, 2, 49), new BlockPos(7, 7, 68),
            new BlockPos(4, 3, 54)));

        // 1 = 工艺室 (Crafts Room)
        // 绝对: (-124,101,-1283) to (-134,106,-1270), note at (-131,102,-1275)
        // 相对: (22,0,50) to (12,5,63) → min(12,0,50), max(22,5,63)
        AREAS.put(1, new AreaBounds(1,
            new BlockPos(12, 0, 50), new BlockPos(22, 5, 63),
            new BlockPos(15, 1, 58)));

        // 2 = 鱼缸 (Fish Tank) — 无独立房间边界
        // note at (-139,104,-1303) → relative (7,3,30)
        AREAS.put(2, new AreaBounds(2,
            null, null,
            new BlockPos(7, 3, 30)));

        // 3 = 锅炉房 (Boiler Room)
        // 绝对: (-129,101,-1324) to (-140,106,-1333), note at (-133,102,-1328)
        // 相对: (17,0,9) to (6,5,0) → min(6,0,0), max(17,5,9)
        AREAS.put(3, new AreaBounds(3,
            new BlockPos(6, 0, 0), new BlockPos(17, 5, 9),
            new BlockPos(13, 1, 5)));

        // 4 = 金库 (Vault)
        // 绝对: (-137,103,-1309) to (-146,108,-1323), note at (-141,104,-1318)
        // 相对: (9,2,24) to (0,7,10) → min(0,2,10), max(9,7,24)
        AREAS.put(4, new AreaBounds(4,
            new BlockPos(0, 2, 10), new BlockPos(9, 7, 24),
            new BlockPos(5, 3, 15)));

        // 5 = 布告栏 (Bulletin Board) — 无独立房间边界
        // note at (-135,104,-1308) → relative (11,3,25)
        AREAS.put(5, new AreaBounds(5,
            null, null,
            new BlockPos(11, 3, 25)));
    }

    public static final Map<Integer, AreaBounds> ALL_AREAS = Collections.unmodifiableMap(AREAS);

    @Nullable
    public static AreaBounds getArea(int areaId) {
        return AREAS.get(areaId);
    }

    /** 判断一个服务端远坐标是否在 CC 结构范围内 */
    public static boolean isInsideCC(BlockPos worldPos) {
        BlockPos origin = InteriorSubspaceManager.CC_ORIGIN;
        int rx = worldPos.getX() - origin.getX();
        int ry = worldPos.getY() - origin.getY();
        int rz = worldPos.getZ() - origin.getZ();
        return rx >= 0 && rx <= 22
            && ry >= 0 && ry <= 7
            && rz >= 0 && rz <= 68;
    }
}
