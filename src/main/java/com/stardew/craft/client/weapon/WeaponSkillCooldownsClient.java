package com.stardew.craft.client.weapon;

import net.minecraft.client.Minecraft;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端本地技能冷却存储（由服务端同步）
 */
public final class WeaponSkillCooldownsClient {

    private static final Map<String, CooldownEntry> cooldowns = new HashMap<>();

    private WeaponSkillCooldownsClient() {}

    public static void setCooldown(String weaponId, String skillId, int totalTicks, int remainingTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        
        long startTick = mc.level.getGameTime();
        long endTick = startTick + remainingTicks;
        cooldowns.put(getKey(weaponId, skillId), new CooldownEntry(startTick, endTick, totalTicks));
    }

    public static boolean isOnCooldown(String weaponId, String skillId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return false;
        
        return getRemainingTicks(weaponId, skillId) > 0;
    }

    @SuppressWarnings("null")
    public static int getRemainingTicks(String weaponId, String skillId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return 0;
        
        CooldownEntry entry = cooldowns.get(getKey(weaponId, skillId));
        if (entry == null) return 0;
        
        long now = mc.level.getGameTime();
        int remaining = (int) Math.max(0, entry.endTick - now);
        return remaining;
    }

    public static int getTotalTicks(String weaponId, String skillId) {
        CooldownEntry entry = cooldowns.get(getKey(weaponId, skillId));
        return entry != null ? entry.totalTicks : 0;
    }

    public static void clear() {
        cooldowns.clear();
    }

    private static String getKey(String weaponId, String skillId) {
        return weaponId + "|" + skillId;
    }

    private record CooldownEntry(long startTick, long endTick, int totalTicks) {}
}
