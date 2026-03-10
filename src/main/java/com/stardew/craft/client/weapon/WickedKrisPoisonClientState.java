package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class WickedKrisPoisonClientState {

    private static int stacks = 0;
    private static long endTick = 0L;
    private static int totalTicks = 0;

    private static boolean detonationActive = false;
    private static long detonateEndTick = 0L;
    private static int detonateTotalTicks = 0;

    private WickedKrisPoisonClientState() {}

    public static void updatePoison(long nowTick, int durationTicks, int stackCount) {
        if (durationTicks <= 0 || stackCount <= 0) {
            clearPoison();
            return;
        }
        stacks = Math.max(0, stackCount);
        totalTicks = Math.max(1, durationTicks);
        endTick = nowTick + totalTicks;
    }

    public static void updateDetonation(long nowTick, int remainingTicks, int totalTicksOverride) {
        if (remainingTicks <= 0) {
            clearDetonation();
            return;
        }
        detonationActive = true;
        detonateTotalTicks = Math.max(1, totalTicksOverride > 0 ? totalTicksOverride : remainingTicks);
        detonateEndTick = nowTick + remainingTicks;
    }

    public static void clearPoison() {
        stacks = 0;
        endTick = 0L;
        totalTicks = 0;
    }

    public static void clearDetonation() {
        detonationActive = false;
        detonateEndTick = 0L;
        detonateTotalTicks = 0;
    }

    public static boolean hasPoison(Player player) {
        if (stacks <= 0 || player == null || player.level() == null) {
            return false;
        }
        long nowTick = player.level().getGameTime();
        if (nowTick > endTick) {
            clearPoison();
            return false;
        }
        return true;
    }

    public static int getStacks(Player player) {
        if (!hasPoison(player)) {
            return 0;
        }
        return stacks;
    }

    public static int getRemainingTicks(Player player) {
        if (!hasPoison(player)) {
            return 0;
        }
        long nowTick = player.level().getGameTime();
        return (int) Math.max(0, endTick - nowTick);
    }

    public static int getTotalTicks() {
        return Math.max(1, totalTicks);
    }

    public static boolean hasDetonation(Player player) {
        if (!detonationActive || player == null || player.level() == null) {
            return false;
        }
        long nowTick = player.level().getGameTime();
        if (nowTick > detonateEndTick) {
            clearDetonation();
            return false;
        }
        return true;
    }

    public static int getDetonationRemainingTicks(Player player) {
        if (!hasDetonation(player)) {
            return 0;
        }
        long nowTick = player.level().getGameTime();
        return (int) Math.max(0, detonateEndTick - nowTick);
    }

    public static int getDetonationTotalTicks() {
        return Math.max(1, detonateTotalTicks);
    }

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            clearPoison();
            clearDetonation();
            return;
        }
        var level = mc.level;
        if (level == null) {
            clearPoison();
            clearDetonation();
            return;
        }
        long nowTick = level.getGameTime();
        if (stacks > 0 && nowTick > endTick) {
            clearPoison();
        }
        if (detonationActive && nowTick > detonateEndTick) {
            clearDetonation();
        }
    }
}
