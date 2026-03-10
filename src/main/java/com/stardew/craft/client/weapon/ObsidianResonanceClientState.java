package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class ObsidianResonanceClientState {

    private static boolean active = false;
    private static long endTick = 0L;
    private static int totalTicks = 0;

    private ObsidianResonanceClientState() {}

    public static void sync(long nowTick, int remainingTicks, int totalTicks) {
        ObsidianResonanceClientState.active = true;
        ObsidianResonanceClientState.totalTicks = Math.max(1, totalTicks);
        int clampedRemaining = Math.max(0, remainingTicks);
        ObsidianResonanceClientState.endTick = nowTick + clampedRemaining;
    }

    public static void clear() {
        active = false;
        endTick = 0L;
        totalTicks = 0;
    }

    public static float getChargeRatio(Player player) {
        if (!active || player == null || player.level() == null) {
            return -1.0f;
        }
        long nowTick = player.level().getGameTime();
        float remaining = Math.max(0L, endTick - nowTick);
        float ratio = 1.0f - (remaining / Math.max(1.0f, (float) totalTicks));
        return Math.min(1.0f, Math.max(0.0f, ratio));
    }

    public static void clearIfNoPlayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            clear();
        }
    }
}
