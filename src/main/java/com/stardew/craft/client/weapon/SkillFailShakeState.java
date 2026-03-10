package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

/**
 * 客户端技能失败抖动状态（仿钓鱼蓄力的轻微颤抖）。
 * 纯视觉，不影响服务端逻辑。
 */
public final class SkillFailShakeState {
    public static final int TOTAL_TICKS = 8;

    private static int mainTicks = 0;
    private static int offTicks = 0;

    private SkillFailShakeState() {}

    public static void start(InteractionHand hand) {
        if (hand == InteractionHand.OFF_HAND) {
            offTicks = TOTAL_TICKS;
        } else {
            mainTicks = TOTAL_TICKS;
        }
    }

    public static void reset() {
        mainTicks = 0;
        offTicks = 0;
    }

    public static boolean isActive(InteractionHand hand) {
        return (hand == InteractionHand.OFF_HAND ? offTicks : mainTicks) > 0;
    }

    public static void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            reset();
            return;
        }
        if (mainTicks > 0) {
            mainTicks--;
        }
        if (offTicks > 0) {
            offTicks--;
        }
    }

    public static float getProgress01(InteractionHand hand, float partialTick) {
        int ticks = hand == InteractionHand.OFF_HAND ? offTicks : mainTicks;
        if (ticks <= 0) {
            return 0.0F;
        }
        float elapsed = (TOTAL_TICKS - ticks) + partialTick;
        float t = elapsed / (float) TOTAL_TICKS;
        if (t < 0.0F) return 0.0F;
        if (t > 1.0F) return 1.0F;
        return t;
    }
}
