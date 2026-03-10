package com.stardew.craft.combat.debuff;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

/**
 * 易伤：提高目标受到的伤害
 */
public final class VulnerableDebuff {

    private static final String TAG_ROOT = "StardewVulnerable";
    private static final String TAG_UNTIL = "Until";
    private static final String TAG_MULTIPLIER = "Multiplier";

    private VulnerableDebuff() {}

    public static void apply(LivingEntity target, float bonusMultiplier, int durationTicks) {
        if (target == null) return;
        CompoundTag root = target.getPersistentData();
        CompoundTag tag = new CompoundTag();
        tag.putLong(TAG_UNTIL, target.level().getGameTime() + durationTicks);
        tag.putFloat(TAG_MULTIPLIER, Math.max(0.0f, bonusMultiplier));
        root.put(TAG_ROOT, tag);
    }

    public static float getMultiplier(LivingEntity target) {
        if (target == null) return 1.0f;
        CompoundTag root = target.getPersistentData();
        if (!root.contains(TAG_ROOT)) return 1.0f;

        CompoundTag tag = root.getCompound(TAG_ROOT);
        long until = tag.getLong(TAG_UNTIL);
        long now = target.level().getGameTime();
        if (now > until) {
            root.remove(TAG_ROOT);
            return 1.0f;
        }

        float bonus = tag.getFloat(TAG_MULTIPLIER);
        return 1.0f + Math.max(0.0f, bonus);
    }
}
