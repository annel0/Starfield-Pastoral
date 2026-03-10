package com.stardew.craft.combat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class DamageNumberContextStore {

    private static final String TAG_ROOT = "StardewDamageMeta";
    private static final String TAG_SKILL = "SkillId";
    private static final String TAG_CRIT = "Crit";
    private static final String TAG_EXPIRE = "Expire";

    private DamageNumberContextStore() {}

    public static void set(Player player, String skillId, boolean crit, long expireTick) {
        CompoundTag root = player.getPersistentData();
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_SKILL, skillId == null ? "" : skillId);
        tag.putBoolean(TAG_CRIT, crit);
        tag.putLong(TAG_EXPIRE, expireTick);
        root.put(TAG_ROOT, tag);
    }

    public static Meta consume(Player player, long nowTick) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(TAG_ROOT)) return null;

        CompoundTag tag = root.getCompound(TAG_ROOT);
        long expire = tag.getLong(TAG_EXPIRE);
        if (nowTick > expire) {
            root.remove(TAG_ROOT);
            return null;
        }

        String skillId = tag.getString(TAG_SKILL);
        boolean crit = tag.getBoolean(TAG_CRIT);
        if (skillId != null && skillId.isEmpty()) skillId = null;
        return new Meta(skillId, crit);
    }

    public static Meta peek(Player player, long nowTick) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(TAG_ROOT)) return null;

        CompoundTag tag = root.getCompound(TAG_ROOT);
        long expire = tag.getLong(TAG_EXPIRE);
        if (nowTick > expire) {
            root.remove(TAG_ROOT);
            return null;
        }

        String skillId = tag.getString(TAG_SKILL);
        boolean crit = tag.getBoolean(TAG_CRIT);
        if (skillId != null && skillId.isEmpty()) skillId = null;
        return new Meta(skillId, crit);
    }

    public record Meta(String skillId, boolean crit) {}
}
