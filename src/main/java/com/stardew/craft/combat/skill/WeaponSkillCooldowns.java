package com.stardew.craft.combat.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import com.stardew.craft.combat.network.SkillCooldownSyncPayload;

/**
 * 技能独立冷却（按技能ID）
 */
public final class WeaponSkillCooldowns {

    private static final String TAG_ROOT = "StardewSkillCooldowns";

    private WeaponSkillCooldowns() {}

    public static boolean isOnCooldown(Player player, String weaponId, String skillId, long nowTick) {
        long remaining = getRemainingTicks(player, weaponId, skillId, nowTick);
        return remaining > 0;
    }

    @SuppressWarnings("null")
    public static int getRemainingTicks(Player player, String weaponId, String skillId, long nowTick) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(TAG_ROOT)) {
            return 0;
        }
        CompoundTag cd = root.getCompound(TAG_ROOT);
        String key = getKey(weaponId, skillId);
        if (!cd.contains(key)) {
            return 0;
        }
        @SuppressWarnings("null")
        long nextTick = cd.getLong(key);
        long remaining = nextTick - nowTick;
        return (int) Math.max(0, remaining);
    }

    @SuppressWarnings("null")
    public static void setCooldown(Player player, String weaponId, String skillId, long nowTick, int durationTicks) {
        CompoundTag root = player.getPersistentData();
        CompoundTag cd = root.contains(TAG_ROOT) ? root.getCompound(TAG_ROOT) : new CompoundTag();
        cd.putLong(getKey(weaponId, skillId), nowTick + durationTicks);
        root.put(TAG_ROOT, cd);
        
        // 同步冷却信息到客户端
        if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, 
                new SkillCooldownSyncPayload(weaponId, skillId, durationTicks, durationTicks));
        }
    }

    private static String getKey(String weaponId, String skillId) {
        return weaponId + "|" + skillId;
    }
}
