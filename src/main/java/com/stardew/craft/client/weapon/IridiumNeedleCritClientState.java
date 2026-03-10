package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class IridiumNeedleCritClientState {

    private static int stacks = 0;
    private static long flashEndTick = 0L;

    private IridiumNeedleCritClientState() {}

    public static void setStacks(int value) {
        int clamped = Math.max(0, Math.min(2, value));
        int prev = stacks;
        stacks = clamped;
        if (prev == 2 && clamped == 0) {
            triggerFlash();
        }
    }

    public static void clear() {
        stacks = 0;
        flashEndTick = 0L;
    }

    public static int getStacks(Player player) {
        if (player == null || player.level() == null) {
            return 0;
        }
        return stacks;
    }

    public static boolean isFlashActive(Player player) {
        if (player == null || player.level() == null) {
            return false;
        }
        return player.level().getGameTime() < flashEndTick;
    }

    private static void triggerFlash() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            flashEndTick = mc.level.getGameTime() + 6L;
        }
    }

    public static void clearIfNoPlayer() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            clear();
        }
    }
}
