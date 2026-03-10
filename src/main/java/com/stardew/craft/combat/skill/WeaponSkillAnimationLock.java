package com.stardew.craft.combat.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * 技能动画期间的普攻锁定
 */
public final class WeaponSkillAnimationLock {

    private static final String TAG_ROOT = "StardewSkillAnimLock";

    private WeaponSkillAnimationLock() {}

    public static boolean isLocked(Player player, long nowTick) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(TAG_ROOT)) {
            return false;
        }
        long endTick = root.getLong(TAG_ROOT);
        return nowTick < endTick;
    }

    public static void setLock(Player player, long nowTick, int durationTicks) {
        CompoundTag root = player.getPersistentData();
        root.putLong(TAG_ROOT, nowTick + Math.max(1, durationTicks));
    }
}
