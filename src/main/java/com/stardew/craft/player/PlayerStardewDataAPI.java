package com.stardew.craft.player;

import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.network.payload.SkillExperienceGainPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Random;

/**
 * 玩家星露谷数据 API
 * 提供便捷的静态方法来访问和操作玩家数据
 */
@SuppressWarnings("null")
public class PlayerStardewDataAPI {

    /**
     * lucky 命令允许设置的范围（包含 Spirit Altar 的 ±0.12）
     */
    public static final double DAILY_LUCK_MIN = -0.12D;
    public static final double DAILY_LUCK_MAX = 0.12D;
    
    // ============ 数据获取 ============
    
    /**
     * 获取玩家数据
     */
    public static PlayerStardewData getData(ServerPlayer player) {
        return PlayerDataManager.getPlayerData(player);
    }

    // ============ 运气相关 ============

    /**
     * 获取今日运气（per-player）。
     *
     * 规则：以星露谷时间（StardewTimeManager 的日期）为准；当日期变化时惰性重新掷一次。
     */
    public static double getDailyLuck(ServerPlayer player) {
        PlayerStardewData data = getData(player);
        StardewTimeManager timeManager = StardewTimeManager.get();
        int dateKey = computeDateKey(timeManager);

        if (data.getDailyLuckDateKey() != dateKey) {
            double rolled = rollDailyLuck(player, dateKey);
            data.setDailyLuckForDate(rolled, dateKey);
            PlayerDataEventHandler.syncPlayerData(player, data);
        }

        return data.getDailyLuck();
    }

    /**
     * 为“今天”强制设置运气（调试用）。下一天会被正常刷新覆盖。
     */
    public static void setDailyLuckForToday(ServerPlayer player, double value) {
        PlayerStardewData data = getData(player);
        StardewTimeManager timeManager = StardewTimeManager.get();
        int dateKey = computeDateKey(timeManager);
        data.setDailyLuckForDate(value, dateKey);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    private static int computeDateKey(StardewTimeManager timeManager) {
        // year 从 1 开始，day 为 1-28，season 为 0-3
        int year = timeManager.getCurrentYear();
        int season = timeManager.getCurrentSeason();
        int day = timeManager.getCurrentDay();
        return ((year * 4) + season) * 28 + (day - 1);
    }

    private static double rollDailyLuck(ServerPlayer player, int dateKey) {
        // SV 原版 sharedDailyLuck: random.Next(-100, 101) / 1000.0 并 cap 到 0.1
        // 这里改为 per-player：用 UUID + dateKey 做确定性 seed（方便重启/重连一致）。
        long seed = player.getUUID().getMostSignificantBits()
            ^ player.getUUID().getLeastSignificantBits()
            ^ (long) dateKey * 0x9E3779B97F4A7C15L;
        Random random = new Random(seed);
        int r = random.nextInt(201) - 100; // [-100, 100]
        double luck = r / 1000.0;
        return Math.min(luck, 0.1);
    }
    
    // ============ 经验相关 ============
    
    /**
     * 添加经验值
     * @return 是否升级
     */
    public static boolean addExperience(ServerPlayer player, SkillType skill, int amount) {
        if (amount <= 0) {
            return false;
        }
        PlayerStardewData data = getData(player);
        int beforeExp = data.getSkillExperience(skill);
        int beforeLevel = data.getSkillLevel(skill);
        boolean leveledUp = data.addExperience(skill, amount);
        int afterExp = data.getSkillExperience(skill);
        int afterLevel = data.getSkillLevel(skill);
        PlayerDataEventHandler.syncPlayerData(player, data);
        PacketDistributor.sendToPlayer(player, new SkillExperienceGainPayload(
            skill.getId(),
            amount,
            beforeExp,
            afterExp,
            beforeLevel,
            afterLevel
        ));
        return leveledUp;
    }
    
    /**
     * 设置经验值（绝对值）
     */
    public static void setExperience(ServerPlayer player, SkillType skill, int amount) {
        PlayerStardewData data = getData(player);
        data.setSkillExperience(skill, Math.max(0, amount));
        PlayerDataEventHandler.syncPlayerData(player, data);
    }
    
    /**
     * 移除经验值
     */
    public static void removeExperience(ServerPlayer player, SkillType skill, int amount) {
        PlayerStardewData data = getData(player);
        int currentExp = data.getSkillExperience(skill);
        data.setSkillExperience(skill, Math.max(0, currentExp - amount));
        PlayerDataEventHandler.syncPlayerData(player, data);
    }
    
    /**
     * 获取技能等级
     */
    public static int getSkillLevel(ServerPlayer player, SkillType skill) {
        return Math.max(0, getData(player).getSkillLevel(skill));
    }

    /**
     * 获取“运气等级”(Luck Buff)。
     * 仅用于还原星露谷中 Luck Buff 对钓鱼/宝箱概率等的影响。
     */
    public static int getLuckLevel(ServerPlayer player) {
        return getData(player).getLuckLevel();
    }
    
    /**
     * 获取技能经验
     */
    public static int getSkillExperience(ServerPlayer player, SkillType skill) {
        return getData(player).getSkillExperience(skill);
    }
    
    /**
     * 获取升级进度（0.0-1.0）
     */
    public static float getLevelProgress(ServerPlayer player, SkillType skill) {
        return getData(player).getLevelProgress(skill);
    }
    
    /**
     * 获取升级所需经验
     */
    public static int getExpForNextLevel(ServerPlayer player, SkillType skill) {
        return getData(player).getExpForNextLevel(skill);
    }
    
    // ============ 能量相关 ============
    
    /**
     * 获取当前能量
     */
    public static float getEnergy(ServerPlayer player) {
        return getData(player).getEnergy();
    }
    
    /**
     * 获取最大能量
     */
    public static int getMaxEnergy(ServerPlayer player) {
        return getData(player).getMaxEnergy();
    }
    
    /**
     * 消耗能量
     * @return 是否成功（能量是否足够）
     */
    public static boolean consumeEnergy(ServerPlayer player, float amount) {
        PlayerStardewData data = getData(player);
        boolean success = data.consumeEnergy(amount);
        PlayerDataEventHandler.syncPlayerData(player, data);
        return success;
    }
    
    /**
     * 恢复能量
     */
    public static void restoreEnergy(ServerPlayer player, float amount) {
        PlayerStardewData data = getData(player);
        data.restoreEnergy(amount);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }
    
    /**
     * 是否疲惫
     */
    public static boolean isExhausted(ServerPlayer player) {
        return getData(player).isExhausted();
    }
    
    /**
     * 治愈疲惫状态
     */
    public static void cureExhaustion(ServerPlayer player) {
        getData(player).cureExhaustion();
    }
    
    /**
     * 睡觉恢复能量
     */
    public static void sleep(ServerPlayer player, int sleepTime) {
        getData(player).sleep(sleepTime);
    }
    
    // ============ 生命值相关 ============
    
    /**
     * 获取当前生命值
     */
    public static int getHealth(ServerPlayer player) {
        return getData(player).getHealth();
    }
    
    /**
     * 设置生命值
     */
    public static void setHealth(ServerPlayer player, int health) {
        PlayerStardewData data = getData(player);
        data.setHealth(health);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }
    
    /**
     * 获取最大生命值
     */
    public static int getMaxHealth(ServerPlayer player) {
        return getData(player).getMaxHealth();
    }

    // ============ 临时 Buff（食物增益） ============

    public static int getLuckBuffLevel(ServerPlayer player) {
        return getData(player).getTempLuckBonus();
    }

    public static int getMagneticRadiusBuff(ServerPlayer player) {
        return getData(player).getTempMagneticRadiusBonus();
    }

    @SuppressWarnings("null")
    public static void applyFishingLevelBuff(ServerPlayer player, int bonus, int durationTicks) {
        if (bonus == 0 || durationTicks <= 0) {
            return;
        }

        // 以 MobEffect 形式显示（支持等级/图标）
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                com.stardew.craft.effect.ModMobEffects.SEA_KING_BLESSING,
                durationTicks,
                Math.max(0, bonus - 1)
        ));

        PlayerStardewData data = getData(player);
        long endTick = player.level().getGameTime() + durationTicks;
        data.applyTempFishingLevelBonus(bonus, endTick);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    @SuppressWarnings("null")
    public static void applyLuckBuff(ServerPlayer player, int bonus, int durationTicks) {
        if (bonus == 0 || durationTicks <= 0) {
            return;
        }

        // 以 MobEffect 形式显示（支持等级/图标）
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                com.stardew.craft.effect.ModMobEffects.SPIRIT_BLESSING,
                durationTicks,
                Math.max(0, bonus - 1)
        ));

        PlayerStardewData data = getData(player);
        long endTick = player.level().getGameTime() + durationTicks;
        data.applyTempLuckBonus(bonus, endTick);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    @SuppressWarnings("null")
    public static void applyMaxEnergyBuff(ServerPlayer player, int bonus, int durationTicks) {
        if (bonus == 0 || durationTicks <= 0) {
            return;
        }

        // 以 MobEffect 形式显示（支持等级/图标）。当前规则：每级 +30。
        if (bonus > 0 && bonus % 30 == 0) {
            int amplifier = Math.max(0, (bonus / 30) - 1);
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                    com.stardew.craft.effect.ModMobEffects.VIGOROUS,
                    durationTicks,
                    amplifier
            ));
        }

        PlayerStardewData data = getData(player);
        long endTick = player.level().getGameTime() + durationTicks;
        data.applyTempMaxEnergyBonus(bonus, endTick);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    @SuppressWarnings("null")
    public static void applyFarmingLevelBuff(ServerPlayer player, int bonus, int durationTicks) {
        if (bonus == 0 || durationTicks <= 0) {
            return;
        }
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                com.stardew.craft.effect.ModMobEffects.FARMER_BLESSING,
                durationTicks,
                Math.max(0, bonus - 1)
        ));
        PlayerStardewData data = getData(player);
        long endTick = player.level().getGameTime() + durationTicks;
        data.applyTempFarmingLevelBonus(bonus, endTick);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    @SuppressWarnings("null")
    public static void applyForagingLevelBuff(ServerPlayer player, int bonus, int durationTicks) {
        if (bonus == 0 || durationTicks <= 0) {
            return;
        }
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                com.stardew.craft.effect.ModMobEffects.FORAGER_BLESSING,
                durationTicks,
                Math.max(0, bonus - 1)
        ));
        PlayerStardewData data = getData(player);
        long endTick = player.level().getGameTime() + durationTicks;
        data.applyTempForagingLevelBonus(bonus, endTick);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    @SuppressWarnings("null")
    public static void applyMiningLevelBuff(ServerPlayer player, int bonus, int durationTicks) {
        if (bonus == 0 || durationTicks <= 0) {
            return;
        }
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                com.stardew.craft.effect.ModMobEffects.MINER_BLESSING,
                durationTicks,
                Math.max(0, bonus - 1)
        ));
        PlayerStardewData data = getData(player);
        long endTick = player.level().getGameTime() + durationTicks;
        data.applyTempMiningLevelBonus(bonus, endTick);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    @SuppressWarnings("null")
    public static void applyAttackBuff(ServerPlayer player, int bonus, int durationTicks) {
        if (bonus == 0 || durationTicks <= 0) {
            return;
        }
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                com.stardew.craft.effect.ModMobEffects.WARRIOR_BLESSING,
                durationTicks,
                Math.max(0, bonus - 1)
        ));
        PlayerStardewData data = getData(player);
        long endTick = player.level().getGameTime() + durationTicks;
        data.applyTempAttackBonus(bonus, endTick);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    @SuppressWarnings("null")
    public static void applyDefenseBuff(ServerPlayer player, int bonus, int durationTicks) {
        if (bonus == 0 || durationTicks <= 0) {
            return;
        }
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                com.stardew.craft.effect.ModMobEffects.GUARDIAN_BLESSING,
                durationTicks,
                Math.max(0, bonus - 1)
        ));
        PlayerStardewData data = getData(player);
        long endTick = player.level().getGameTime() + durationTicks;
        data.applyTempDefenseBonus(bonus, endTick);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    @SuppressWarnings("null")
    public static void applyMagneticRadiusBuff(ServerPlayer player, int bonus, int durationTicks) {
        if (bonus == 0 || durationTicks <= 0) {
            return;
        }
        int amplifier = Math.max(0, bonus - 1);
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                com.stardew.craft.effect.ModMobEffects.MAGNETISM,
                durationTicks,
                amplifier
        ));
        PlayerStardewData data = getData(player);
        long endTick = player.level().getGameTime() + durationTicks;
        data.applyTempMagneticRadiusBonus(bonus, endTick);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }
    
    /**
     * 设置最大生命值
     */
    public static void setMaxHealth(ServerPlayer player, int maxHealth) {
        PlayerStardewData data = getData(player);
        data.setMaxHealth(maxHealth);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }
    
    // ============ 金币相关 ============
    
    /**
     * 获取金币
     */
    public static int getMoney(ServerPlayer player) {
        return getData(player).getMoney();
    }
    
    /**
     * 设置金币
     */
    public static void setMoney(ServerPlayer player, int money) {
        PlayerStardewData data = getData(player);
        data.setMoney(money);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }
    
    /**
     * 添加金币
     */
    public static void addMoney(ServerPlayer player, int amount) {
        PlayerStardewData data = getData(player);
        data.addMoney(amount);
        PlayerDataEventHandler.syncPlayerData(player, data);
    }
    
    /**
     * 移除金币
     * @return 是否成功(金币是否足够)
     */
    public static boolean removeMoney(ServerPlayer player, int amount) {
        PlayerStardewData data = getData(player);
        boolean success = data.removeMoney(amount);
        PlayerDataEventHandler.syncPlayerData(player, data);
        return success;
    }
    
    // ============ 职业相关 ============
    
    /**
     * 添加职业
     */
    public static boolean addProfession(ServerPlayer player, ProfessionType profession) {
        PlayerStardewData data = getData(player);
        boolean changed = data.addProfession(profession);
        if (changed) {
            PlayerDataEventHandler.syncPlayerData(player, data);
        }
        return changed;
    }

    /**
     * 移除职业
     */
    public static boolean removeProfession(ServerPlayer player, ProfessionType profession) {
        PlayerStardewData data = getData(player);
        boolean changed = data.removeProfession(profession);
        if (changed) {
            PlayerDataEventHandler.syncPlayerData(player, data);
        }
        return changed;
    }

    /**
     * 清空职业并重新根据等级补齐待选项
     */
    public static boolean clearProfessions(ServerPlayer player) {
        PlayerStardewData data = getData(player);
        boolean changed = data.clearProfessions();
        if (changed) {
            PlayerDataEventHandler.syncPlayerData(player, data);
        }
        return changed;
    }

    /**
     * 选择当前待处理职业
     */
    public static boolean choosePendingProfession(ServerPlayer player, ProfessionType profession) {
        PlayerStardewData data = getData(player);
        boolean changed = data.choosePendingProfession(profession);
        if (changed) {
            PlayerDataEventHandler.syncPlayerData(player, data);
        }
        return changed;
    }

    /**
     * 按当前等级修复漏掉的职业待选项
     */
    public static void repairMissingProfessionChoices(ServerPlayer player) {
        PlayerStardewData data = getData(player);
        data.repairMissingProfessionChoices();
        PlayerDataEventHandler.syncPlayerData(player, data);
    }

    public static java.util.List<PlayerStardewData.ProfessionChoicePrompt> getPendingProfessionChoices(ServerPlayer player) {
        return getData(player).getPendingProfessionChoices();
    }
    
    /**
     * 是否拥有职业
     */
    public static boolean hasProfession(ServerPlayer player, ProfessionType profession) {
        return getData(player).hasProfession(profession);
    }

    // ============ 钓鱼记录（CatchLimit） ============

    public static int getFishCatchCount(ServerPlayer player, String itemId) {
        return getData(player).getFishCatchCount(itemId);
    }

    public static void addFishCatchCount(ServerPlayer player, String itemId, int amount) {
        PlayerStardewData data = getData(player);
        if (data.addFishCatchCount(itemId, amount)) {
            PlayerDataEventHandler.syncPlayerData(player, data);
        }
    }

    public static boolean isSpecialOrderRuleActive(ServerPlayer player, String ruleId) {
        return getData(player).isSpecialOrderRuleActive(ruleId);
    }

    public static void setSpecialOrderRuleActive(ServerPlayer player, String ruleId, boolean active) {
        PlayerStardewData data = getData(player);
        if (data.setSpecialOrderRuleActive(ruleId, active)) {
            PlayerDataEventHandler.syncPlayerData(player, data);
        }
    }
}
