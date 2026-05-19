package com.stardew.craft.client.mastery;

import com.stardew.craft.client.ClientPlayerDataCache;
import com.stardew.craft.mastery.MasterySite;
import com.stardew.craft.player.SkillType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;

/**
 * 客户端每 tick 在已领取技能的蜡烛位置喷火焰粒子，纯本地玩家视角。
 * 不改服务端蜡烛 BlockState，所以其他玩家看到的是各自的状态。
 */
public final class MasteryCandleFlameClient {
    private MasteryCandleFlameClient() {}

    /** 每隔几 tick 喷一次粒子，节流。 */
    private static final int TICK_INTERVAL = 4;
    private static int tickCounter = 0;

    public static void onClientTick() {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null || mc.player == null) return;
        if (mc.isPaused()) return;
        if (!MasterySite.isMasteryDimension(level)) return;
        if (!ClientPlayerDataCache.isSynced()) return;

        tickCounter++;
        if (tickCounter < TICK_INTERVAL) return;
        tickCounter = 0;

        for (SkillType skill : SkillType.values()) {
            if (!ClientPlayerDataCache.hasClaimedMasteryReward(skill)) continue;
            MasterySite.SkillStation st = MasterySite.station(skill);
            if (st == null) continue;
            BlockPos cp = st.candlePos();
            // 蜡烛顶端两个火苗位置：原版 candle 渲染时 1-2 根的 candle 一般在 (0.4-0.6, 0.5+, 0.4-0.6)。
            // 直接在格子中心上方一点点喷小焰粒子。
            double bx = cp.getX() + 0.5;
            double by = cp.getY() + 0.55;
            double bz = cp.getZ() + 0.5;
            level.addParticle(ParticleTypes.SMALL_FLAME, bx - 0.12, by, bz - 0.05, 0, 0, 0);
            level.addParticle(ParticleTypes.SMALL_FLAME, bx + 0.12, by, bz + 0.05, 0, 0, 0);
        }
    }
}
