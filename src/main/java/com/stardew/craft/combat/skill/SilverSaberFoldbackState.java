package com.stardew.craft.combat.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public final class SilverSaberFoldbackState {

    public static final int DEFAULT_DURATION_TICKS = 20; // 1.0s

    private static final String TAG_ACTIVE = "StardewSilverFoldbackActive";
    private static final String TAG_END_TICK = "StardewSilverFoldbackEnd";
    private static final String TAG_DURATION = "StardewSilverFoldbackDuration";
    private static final String TAG_WEAPON_ID = "StardewSilverFoldbackWeapon";
    private static final String TAG_ORIGIN_X = "StardewSilverFoldbackOriginX";
    private static final String TAG_ORIGIN_Y = "StardewSilverFoldbackOriginY";
    private static final String TAG_ORIGIN_Z = "StardewSilverFoldbackOriginZ";

    private SilverSaberFoldbackState() {}

    public static void start(Player player, long nowTick, int durationTicks, String weaponId, Vec3 origin) {
        CompoundTag root = player.getPersistentData();
        int duration = Math.max(1, durationTicks);
        root.putBoolean(TAG_ACTIVE, true);
        root.putLong(TAG_END_TICK, nowTick + duration);
        root.putInt(TAG_DURATION, duration);
        root.putString(TAG_WEAPON_ID, weaponId == null ? "" : weaponId);
        root.putDouble(TAG_ORIGIN_X, origin.x);
        root.putDouble(TAG_ORIGIN_Y, origin.y);
        root.putDouble(TAG_ORIGIN_Z, origin.z);
    }

    public static boolean isActive(Player player, long nowTick) {
        CompoundTag root = player.getPersistentData();
        if (!root.getBoolean(TAG_ACTIVE)) {
            return false;
        }
        long endTick = root.getLong(TAG_END_TICK);
        if (nowTick > endTick) {
            clear(player);
            return false;
        }
        return true;
    }

    public static boolean isActiveRaw(Player player) {
        CompoundTag root = player.getPersistentData();
        return root.getBoolean(TAG_ACTIVE);
    }

    public static long getEndTick(Player player) {
        CompoundTag root = player.getPersistentData();
        return root.getLong(TAG_END_TICK);
    }

    public static String getWeaponId(Player player) {
        CompoundTag root = player.getPersistentData();
        return root.contains(TAG_WEAPON_ID) ? root.getString(TAG_WEAPON_ID) : null;
    }

    public static Vec3 getOrigin(Player player) {
        CompoundTag root = player.getPersistentData();
        double x = root.getDouble(TAG_ORIGIN_X);
        double y = root.getDouble(TAG_ORIGIN_Y);
        double z = root.getDouble(TAG_ORIGIN_Z);
        return new Vec3(x, y, z);
    }

    public static int getDurationTicks(Player player) {
        CompoundTag root = player.getPersistentData();
        int duration = root.getInt(TAG_DURATION);
        return Math.max(1, duration);
    }

    public static void clear(Player player) {
        CompoundTag root = player.getPersistentData();
        root.remove(TAG_ACTIVE);
        root.remove(TAG_END_TICK);
        root.remove(TAG_DURATION);
        root.remove(TAG_WEAPON_ID);
        root.remove(TAG_ORIGIN_X);
        root.remove(TAG_ORIGIN_Y);
        root.remove(TAG_ORIGIN_Z);
    }
}
