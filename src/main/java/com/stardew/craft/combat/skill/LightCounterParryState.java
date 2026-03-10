package com.stardew.craft.combat.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class LightCounterParryState {

    public static final int DEFAULT_WINDOW_TICKS = 20; // 1.0s
    public static final int COUNTER_ANIM_TICKS = 8;

    private static final String TAG_END_TICK = "StardewLightCounterEnd";
    private static final String TAG_WEAPON_ID = "StardewLightCounterWeapon";
    private static final String TAG_ACTIVE = "StardewLightCounterActive";

    private LightCounterParryState() {}

    @SuppressWarnings("null")
    public static void start(Player player, long nowTick, int windowTicks, String weaponId) {
        CompoundTag root = player.getPersistentData();
        root.putLong(TAG_END_TICK, nowTick + Math.max(1, windowTicks));
        root.putString(TAG_WEAPON_ID, weaponId);
        root.putBoolean(TAG_ACTIVE, true);
    }

    public static boolean isActive(Player player, long nowTick) {
        CompoundTag root = player.getPersistentData();
        if (!root.getBoolean(TAG_ACTIVE)) {
            return false;
        }
        long endTick = root.getLong(TAG_END_TICK);
        return nowTick <= endTick;
    }

    public static String getWeaponId(Player player) {
        CompoundTag root = player.getPersistentData();
        return root.contains(TAG_WEAPON_ID) ? root.getString(TAG_WEAPON_ID) : null;
    }

    public static void clear(Player player) {
        CompoundTag root = player.getPersistentData();
        root.remove(TAG_END_TICK);
        root.remove(TAG_WEAPON_ID);
        root.remove(TAG_ACTIVE);
    }
}
