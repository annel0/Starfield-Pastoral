package com.stardew.craft.mastery;

import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.player.SkillType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * 精通山洞的固定坐标与映射 — 全部在 ModDimensions.STARDEW_VALLEY。
 *
 * 五个 item_display 实体由 /summon 命令放置在地图上，本类按近似坐标识别归属技能。
 * 蜡烛是原版 candle 方块，每位置 2 根，服务端永远 lit=false；客户端按玩家自身的 mastery
 * 状态渲染火焰（详见 MasteryCandleFlameRenderer）。
 */
public final class MasterySite {
    private MasterySite() {}

    /** 山洞门下半块。门的上半块在此 .above()。 */
    public static final BlockPos DOOR_POS = new BlockPos(-90, 64, 109);

    /** 中央 pedestal — 一格原版讲台，朝南。右键打开 Mastery 总览 UI。 */
    public static final BlockPos CENTRAL_PEDESTAL_POS = new BlockPos(-90, 64, 104);

    /** 单技能 pedestal 信息：item_display 实体大概位置 + 蜡烛 anchor。 */
    public record SkillStation(SkillType skill, Vec3 displayPos, BlockPos candlePos) {}

    private static final Map<SkillType, SkillStation> STATIONS;

    /** 反向：candlePos → SkillType，供 client 渲染快速查询。 */
    private static final Map<BlockPos, SkillType> CANDLE_LOOKUP;

    /** item_display 识别容差（曼哈顿球半径，方块单位）。 */
    private static final double DISPLAY_MATCH_RADIUS = 0.6;

    static {
        STATIONS = new EnumMap<>(SkillType.class);
        STATIONS.put(SkillType.COMBAT,   new SkillStation(SkillType.COMBAT,
            new Vec3(-93.5625, 65.4375, 103.0), new BlockPos(-94, 67, 102)));
        STATIONS.put(SkillType.FORAGING, new SkillStation(SkillType.FORAGING,
            new Vec3(-91.5625, 65.625, 101.0),  new BlockPos(-92, 67, 100)));
        STATIONS.put(SkillType.FARMING,  new SkillStation(SkillType.FARMING,
            new Vec3(-89.5,    65.5,    101.0), new BlockPos(-90, 67, 100)));
        STATIONS.put(SkillType.FISHING,  new SkillStation(SkillType.FISHING,
            new Vec3(-87.5,    65.625,  101.0), new BlockPos(-88, 67, 100)));
        STATIONS.put(SkillType.MINING,   new SkillStation(SkillType.MINING,
            new Vec3(-85.5,    65.625,  103.0), new BlockPos(-86, 67, 102)));

        CANDLE_LOOKUP = new java.util.HashMap<>();
        for (SkillStation s : STATIONS.values()) CANDLE_LOOKUP.put(s.candlePos(), s.skill());
    }

    public static boolean isMasteryDimension(Level level) {
        return level != null && level.dimension().equals(ModDimensions.STARDEW_VALLEY);
    }

    public static boolean isDoorPos(BlockPos pos) {
        return pos.getX() == DOOR_POS.getX()
            && pos.getZ() == DOOR_POS.getZ()
            && (pos.getY() == DOOR_POS.getY() || pos.getY() == DOOR_POS.getY() + 1);
    }

    public static boolean isCentralPedestal(BlockPos pos) {
        return pos.equals(CENTRAL_PEDESTAL_POS);
    }

    public static Optional<SkillType> skillForCandle(BlockPos pos) {
        return Optional.ofNullable(CANDLE_LOOKUP.get(pos));
    }

    /** 通过 item_display 实体的位置反查归属技能。 */
    public static Optional<SkillType> skillForDisplay(Entity entity) {
        if (entity == null) return Optional.empty();
        Vec3 ep = entity.position();
        SkillType best = null;
        double bestSq = DISPLAY_MATCH_RADIUS * DISPLAY_MATCH_RADIUS;
        for (SkillStation s : STATIONS.values()) {
            double sq = s.displayPos().distanceToSqr(ep);
            if (sq <= bestSq) {
                bestSq = sq;
                best = s.skill();
            }
        }
        return Optional.ofNullable(best);
    }

    public static SkillStation station(SkillType skill) {
        return STATIONS.get(skill);
    }
}
