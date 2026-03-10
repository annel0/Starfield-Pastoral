package com.stardew.craft.client.weapon;

import net.minecraft.world.entity.player.Player;

public final class SingularityClientState {

    private static int stacks = 0;

    private SingularityClientState() {}

    public static void setStacks(int val) {
        stacks = Math.max(0, val);
    }

    public static int getStacks(Player player) {
        return stacks;
    }

    public static void clear() {
        stacks = 0;
    }
}
