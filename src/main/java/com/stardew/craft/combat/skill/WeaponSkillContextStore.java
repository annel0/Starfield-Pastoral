package com.stardew.craft.combat.skill;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * 存储/读取玩家即将触发的技能上下文（用于下一次伤害计算）
 */
public final class WeaponSkillContextStore {

    private static final String TAG_ROOT = "StardewSkillContext";
    private static final String TAG_SKILL_ID = "SkillId";
    private static final String TAG_TIER = "Tier";
    private static final String TAG_DAMAGE_MULT = "DamageMultiplier";
    private static final String TAG_IGNORE_DEF = "IgnoreDefense";
    private static final String TAG_GUARANTEED_CRIT = "GuaranteedCrit";
    private static final String TAG_EXPIRE_TICK = "ExpireTick";

    private WeaponSkillContextStore() {}

    @SuppressWarnings("null")
    public static void setPending(Player player, SkillContext context, long expireTick) {
        CompoundTag root = player.getPersistentData();
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_SKILL_ID, context.getSkillId());
        tag.putInt(TAG_TIER, context.getTier().ordinal());
        tag.putFloat(TAG_DAMAGE_MULT, context.getDamageMultiplier());
        tag.putBoolean(TAG_IGNORE_DEF, context.isIgnoreDefense());
        tag.putBoolean(TAG_GUARANTEED_CRIT, context.isGuaranteedCrit());
        tag.putLong(TAG_EXPIRE_TICK, expireTick);
        root.put(TAG_ROOT, tag);
    }

    public static SkillContext consume(Player player, long nowTick) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(TAG_ROOT)) {
            return null;
        }

        CompoundTag tag = root.getCompound(TAG_ROOT);
        long expire = tag.getLong(TAG_EXPIRE_TICK);
        if (expire < nowTick) {
            root.remove(TAG_ROOT);
            return null;
        }

        String skillId = tag.getString(TAG_SKILL_ID);
        int tierOrdinal = tag.getInt(TAG_TIER);
        float damageMult = tag.getFloat(TAG_DAMAGE_MULT);
        boolean ignoreDef = tag.getBoolean(TAG_IGNORE_DEF);
        boolean guaranteedCrit = tag.getBoolean(TAG_GUARANTEED_CRIT);

        SkillContext.SkillTier tier = SkillContext.SkillTier.NORMAL;
        if (tierOrdinal >= 0 && tierOrdinal < SkillContext.SkillTier.values().length) {
            tier = SkillContext.SkillTier.values()[tierOrdinal];
        }

        root.remove(TAG_ROOT);

        return SkillContext.builder()
                .skillId(skillId)
                .tier(tier)
                .damageMultiplier(damageMult)
                .ignoreDefense(ignoreDef)
                .guaranteedCrit(guaranteedCrit)
                .build();
    }

    public static boolean hasPending(Player player, long nowTick) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(TAG_ROOT)) {
            return false;
        }
        CompoundTag tag = root.getCompound(TAG_ROOT);
        long expire = tag.getLong(TAG_EXPIRE_TICK);
        if (expire < nowTick) {
            root.remove(TAG_ROOT);
            return false;
        }
        return true;
    }
}
