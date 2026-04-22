package com.stardew.craft.desert;

import net.minecraft.core.BlockPos;

/**
 * 沙漠区域的所有固定坐标常量。
 * <p>
 * 所有 offset 均相对于 desert.schem 的放置原点 {@link #DESERT_ORIGIN}。
 * 世界绝对坐标 = DESERT_ORIGIN + offset。
 */
public final class DesertConstants {

    private DesertConstants() {}

    // ── schem 放置原点（世界绝对坐标，从 pos1/pos2 推算） ──
    // pos1 = (-169, 26, 1483), pos2 = (-466, -55, 1160) → min = (-466, -55, 1160)
    public static final BlockPos DESERT_ORIGIN = new BlockPos(-466, -55, 1160);

    // ── 玩家到达点（公交站到达）──
    // 世界: (-350, -43, 1322) → offset: (116, 12, 162)
    public static final BlockPos ARRIVAL_OFFSET = new BlockPos(116, 12, 162);

    // ── 骷髅矿出口到达点 ──
    // 世界: (-339, -42, 1268) → offset: (127, 13, 108)
    public static final BlockPos SKULL_CAVERN_EXIT_OFFSET = new BlockPos(127, 13, 108);

    // ── 矿井入口传送方块 ──
    // 世界 from (-340,-42,1266) to (-338,-40,1266)
    // offset from (126,13,106) to (128,15,106) → base=(126,13,106), size x=3, h=3, z=1
    public static final BlockPos MINE_PORTAL_OFFSET = new BlockPos(126, 13, 106);
    public static final int MINE_PORTAL_X = 3;
    public static final int MINE_PORTAL_H = 3;
    public static final int MINE_PORTAL_Z = 1;

    // ── Oasis 入口传送方块 ──
    // 世界 from (-360,-40,1414) to (-359,-39,1414)
    // offset from (106,15,254) to (107,16,254) → base=(106,15,254), size x=2, h=2, z=1
    public static final BlockPos OASIS_PORTAL_OFFSET = new BlockPos(106, 15, 254);
    public static final int OASIS_PORTAL_X = 2;
    public static final int OASIS_PORTAL_H = 2;
    public static final int OASIS_PORTAL_Z = 1;

    // ── 公交站牌传送区域（主世界一侧） ──
    // 区域 (81,-12,202) → (84,-9,205)：4x4x4 空气方块区域，全部替换为 PortalTrigger
    public static final BlockPos BUS_PORTAL_BASE = new BlockPos(81, -12, 202);
    public static final int BUS_PORTAL_X = 4;
    public static final int BUS_PORTAL_H = 4;
    public static final int BUS_PORTAL_Z = 4;
    public static final String TAG_BUS_PORTAL_MARKER = "sdv_portal_marker:desert_bus";
    public static final String TAG_BUS_PORTAL_TARGET = "sdv_portal_target:desert_bus";

    // ── 返程公交站牌传送区域（沙漠一侧） ──
    // 世界绝对坐标 (-351,-42,1316) → (-350,-41,1316)：size x=2, h=2, z=1
    public static final BlockPos BUS_RETURN_PORTAL_BASE = new BlockPos(-351, -42, 1316);
    public static final int BUS_RETURN_PORTAL_X = 2;
    public static final int BUS_RETURN_PORTAL_H = 2;
    public static final int BUS_RETURN_PORTAL_Z = 1;
    public static final String TAG_BUS_RETURN_PORTAL_MARKER = "sdv_portal_marker:desert_bus_return";
    public static final String TAG_BUS_RETURN_PORTAL_TARGET = "sdv_portal_target:desert_bus_return";
    /** 返程到达点——鹈鹕镇公交站（主世界绝对坐标，朝北）。 */
    public static final BlockPos TOWN_RETURN_ARRIVAL = new BlockPos(71, -12, 207);

    // ── 公交票价 ──
    public static final int BUS_TICKET_PRICE = 500;

    // ── Oasis Interior ──
    public static final String OASIS_INTERIOR_PATH = "data/stardewcraft/structures/interior/oasis_interior.schem";
    // 分配在 interior 区域（下一个空闲槽位，接在 wizard_tower 之后）
    public static final BlockPos OASIS_INTERIOR_ORIGIN = new BlockPos(18240, 70, 17664);
    // 玄家进入后传送到 origin + (2, 1, 4)
    public static final BlockPos OASIS_INDOOR_SPAWN_OFFSET = new BlockPos(2, 1, 4);
    // 室内出口传送方块 origin + (1, 1, 4)，size 1×2×1
    public static final BlockPos OASIS_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(1, 1, 4);
    public static final int OASIS_EXIT_PORTAL_X = 1;
    public static final int OASIS_EXIT_PORTAL_H = 2;
    public static final int OASIS_EXIT_PORTAL_Z = 1;

    // ── 门户标识 ──
    public static final String TAG_MINE_PORTAL_MARKER = "sdv_portal_marker:desert_mine_entrance";
    public static final String TAG_MINE_PORTAL_TARGET = "sdv_portal_target:desert_mine_enter";
    public static final String TAG_OASIS_PORTAL_MARKER_OUTSIDE = "sdv_portal_marker:oasis_outside";
    public static final String TAG_OASIS_PORTAL_TARGET_ENTER = "sdv_portal_target:oasis_enter";
    public static final String TAG_OASIS_PORTAL_MARKER_INSIDE = "sdv_portal_marker:oasis_inside";
    public static final String TAG_OASIS_PORTAL_TARGET_EXIT = "sdv_portal_target:oasis_exit";

    // ── 沙漠 schem 资源路径 ──
    public static final String DESERT_SCHEM_PATH = "data/stardewcraft/structures/desert.schem";

    /** 世界绝对坐标 = DESERT_ORIGIN + offset */
    public static BlockPos worldPos(BlockPos offset) {
        return DESERT_ORIGIN.offset(offset);
    }

    // ── 沙漠 schem 包围盒（世界坐标，含两端，供运行时区域判定使用） ──
    public static final int DESERT_BBOX_MIN_X = DESERT_ORIGIN.getX();           // -466
    public static final int DESERT_BBOX_MAX_X = DESERT_ORIGIN.getX() + 297;     // -169
    public static final int DESERT_BBOX_MIN_Z = DESERT_ORIGIN.getZ();           // 1160
    public static final int DESERT_BBOX_MAX_Z = DESERT_ORIGIN.getZ() + 323;     // 1483

    /** 判断给定 XZ 坐标是否在沙漠 schem 包围盒内（不考虑 Y）。 */
    public static boolean isInDesertRegion(int x, int z) {
        return x >= DESERT_BBOX_MIN_X && x <= DESERT_BBOX_MAX_X
                && z >= DESERT_BBOX_MIN_Z && z <= DESERT_BBOX_MAX_Z;
    }

    /** 判断给定 BlockPos 是否在沙漠 schem 包围盒内（不考虑 Y）。 */
    public static boolean isInDesertRegion(BlockPos pos) {
        return isInDesertRegion(pos.getX(), pos.getZ());
    }
}
