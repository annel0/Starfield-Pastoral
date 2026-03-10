package com.stardew.craft.player;

import net.minecraft.nbt.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 玩家星露谷数据
 * 存储玩家的所有星露谷相关数据（生命、能量、技能、金币等）
 */
public class PlayerStardewData {
    
    // 玩家UUID
    private UUID playerUUID;
    
    // ============ 基础属性 ============
    private int health;              // 当前生命值
    private int maxHealth;           // 最大生命值（基础100）
    
    private float energy;            // 当前能量值（精力值）
    private int maxEnergy;           // 最大能量值（基础270）
    private boolean exhausted;       // 是否处于疲惫状态
    
    private int money;               // 金币数量
    
    // ============ 技能系统 ============
    // 技能等级（0-10级）
    private final int[] skillLevels = new int[5];  // farming(0), fishing(1), foraging(2), mining(3), combat(4)
    
    // 技能经验值
    private final int[] experiencePoints = new int[5];
    
    // 职业选择
    private final List<Integer> professions = new ArrayList<>();
    
    // ============ 其他数据 ============
    private long lastSyncTime;       // 最后一次同步时间
    private boolean dirty;           // 数据是否需要保存

    // ============ 运气系统 ============
    // 每日运气（per-player），按星露谷日期刷新（由 PlayerStardewDataAPI 惰性刷新）
    private double dailyLuck;
    private int dailyLuckDateKey;

    // ============ 临时Buff（食物增益） ============
    // 说明：使用 level.getGameTime() 作为时间基准，支持跨保存/重启继续倒计时。
    private int tempFishingLevelBonus;
    private long tempFishingLevelBonusEndTick;

    private int tempLuckBonus;
    private long tempLuckBonusEndTick;

    private int tempMaxEnergyBonus;
    private long tempMaxEnergyBonusEndTick;
    
    // 经验值升级表（根据星露谷物语）
    private static final int[] EXP_TO_LEVEL = {
        0,      // Level 0
        100,    // Level 1
        380,    // Level 2
        770,    // Level 3
        1300,   // Level 4
        2150,   // Level 5
        3300,   // Level 6
        4800,   // Level 7
        6900,   // Level 8
        10000,  // Level 9
        15000   // Level 10
    };
    
    /**
     * 创建新的玩家数据（默认值）
     */
    public PlayerStardewData(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.health = 100;
        this.maxHealth = 100;
        this.energy = 270f;
        this.maxEnergy = 270;
        this.exhausted = false;
        this.money = 500;  // 初始金币
        this.lastSyncTime = System.currentTimeMillis();
        this.dirty = false;

        this.dailyLuck = 0.0;
        this.dailyLuckDateKey = -1;

        this.tempFishingLevelBonus = 0;
        this.tempFishingLevelBonusEndTick = 0L;
        this.tempLuckBonus = 0;
        this.tempLuckBonusEndTick = 0L;
        this.tempMaxEnergyBonus = 0;
        this.tempMaxEnergyBonusEndTick = 0L;
    }
    
    /**
     * 从NBT加载数据
     */
    public static PlayerStardewData fromNBT(CompoundTag tag, UUID playerUUID) {
        PlayerStardewData data = new PlayerStardewData(playerUUID);
        
        // 基础属性
        data.health = tag.contains("Health") ? tag.getInt("Health") : 100;
        data.maxHealth = tag.contains("MaxHealth") ? tag.getInt("MaxHealth") : 100;
        data.energy = tag.contains("Energy") ? tag.getFloat("Energy") : 270f;
        data.maxEnergy = tag.contains("MaxEnergy") ? tag.getInt("MaxEnergy") : 270;
        data.exhausted = tag.getBoolean("Exhausted");
        data.money = tag.contains("Money") ? tag.getInt("Money") : 500;
        
        // 技能等级
        if (tag.contains("SkillLevels")) {
            int[] levels = tag.getIntArray("SkillLevels");
            System.arraycopy(levels, 0, data.skillLevels, 0, Math.min(levels.length, 5));
        }
        
        // 技能经验
        if (tag.contains("Experience")) {
            int[] exp = tag.getIntArray("Experience");
            System.arraycopy(exp, 0, data.experiencePoints, 0, Math.min(exp.length, 5));
        }
        
        // 职业
        data.professions.clear();
        if (tag.contains("Professions")) {
            int[] profs = tag.getIntArray("Professions");
            for (int prof : profs) {
                data.professions.add(prof);
            }
        }
        
        // 元数据
        data.lastSyncTime = tag.getLong("LastSyncTime");

        // 运气
        data.dailyLuck = tag.contains("DailyLuck") ? tag.getDouble("DailyLuck") : 0.0;
        data.dailyLuckDateKey = tag.contains("DailyLuckDateKey") ? tag.getInt("DailyLuckDateKey") : -1;

        // 临时Buff
        data.tempFishingLevelBonus = tag.contains("TempFishingLevelBonus") ? tag.getInt("TempFishingLevelBonus") : 0;
        data.tempFishingLevelBonusEndTick = tag.contains("TempFishingLevelBonusEndTick") ? tag.getLong("TempFishingLevelBonusEndTick") : 0L;
        data.tempLuckBonus = tag.contains("TempLuckBonus") ? tag.getInt("TempLuckBonus") : 0;
        data.tempLuckBonusEndTick = tag.contains("TempLuckBonusEndTick") ? tag.getLong("TempLuckBonusEndTick") : 0L;
        data.tempMaxEnergyBonus = tag.contains("TempMaxEnergyBonus") ? tag.getInt("TempMaxEnergyBonus") : 0;
        data.tempMaxEnergyBonusEndTick = tag.contains("TempMaxEnergyBonusEndTick") ? tag.getLong("TempMaxEnergyBonusEndTick") : 0L;

        // 安全钳制：避免异常值导致越界（Buff 允许 maxEnergy 临时上升）
        data.maxHealth = Math.max(100, data.maxHealth);
        data.maxEnergy = Math.max(270, data.maxEnergy);
        int effectiveMaxEnergy = data.maxEnergy + Math.max(0, data.tempMaxEnergyBonus);
        data.health = Math.max(0, Math.min(data.health, data.maxHealth));
        data.energy = Math.max(0, Math.min(data.energy, effectiveMaxEnergy));

        data.dirty = false;
        
        return data;
    }
    
    /**
     * 保存到NBT
     */
    @SuppressWarnings("null")
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        
        // 基础属性
        tag.putInt("Health", health);
        tag.putInt("MaxHealth", maxHealth);
        tag.putFloat("Energy", energy);
        tag.putInt("MaxEnergy", maxEnergy);
        tag.putBoolean("Exhausted", exhausted);
        tag.putInt("Money", money);
        
        // 技能等级
        tag.putIntArray("SkillLevels", skillLevels);
        
        // 技能经验
        tag.putIntArray("Experience", experiencePoints);
        
        // 职业
        int[] profsArray = professions.stream().mapToInt(Integer::intValue).toArray();
        tag.putIntArray("Professions", profsArray);
        
        // 元数据
        tag.putLong("LastSyncTime", lastSyncTime);

        // 运气
        tag.putDouble("DailyLuck", dailyLuck);
        tag.putInt("DailyLuckDateKey", dailyLuckDateKey);

        // 临时Buff
        tag.putInt("TempFishingLevelBonus", tempFishingLevelBonus);
        tag.putLong("TempFishingLevelBonusEndTick", tempFishingLevelBonusEndTick);
        tag.putInt("TempLuckBonus", tempLuckBonus);
        tag.putLong("TempLuckBonusEndTick", tempLuckBonusEndTick);
        tag.putInt("TempMaxEnergyBonus", tempMaxEnergyBonus);
        tag.putLong("TempMaxEnergyBonusEndTick", tempMaxEnergyBonusEndTick);
        
        return tag;
    }
    
    // ============ 经验和等级相关方法 ============
    
    /**
     * 添加经验值
     * @return 是否升级
     */
    public boolean addExperience(SkillType skill, int amount) {
        if (amount <= 0) return false;
        
        int skillId = skill.getId();
        int oldLevel = skillLevels[skillId];
        
        experiencePoints[skillId] += amount;
        
        // 检查是否升级
        int newLevel = calculateLevel(experiencePoints[skillId]);
        if (newLevel > oldLevel && newLevel <= 10) {
            skillLevels[skillId] = newLevel;
            
            // 战斗技能升级增加最大生命值
            if (skill == SkillType.COMBAT && newLevel != 5 && newLevel != 10) {
                maxHealth += 5;
                health = Math.min(health + 5, maxHealth);
            }
            
            markDirty();
            return true;
        }
        
        markDirty();
        return false;
    }
    
    /**
     * 根据经验值计算等级
     */
    private int calculateLevel(int exp) {
        for (int level = 10; level >= 1; level--) {
            if (exp >= EXP_TO_LEVEL[level]) {
                return level;
            }
        }
        return 0;
    }
    
    /**
     * 获取升级所需经验
     */
    public int getExpForNextLevel(SkillType skill) {
        int level = skillLevels[skill.getId()];
        if (level >= 10) return 0;
        return EXP_TO_LEVEL[level + 1];
    }
    
    /**
     * 获取当前等级的进度（0.0-1.0）
     */
    public float getLevelProgress(SkillType skill) {
        int skillId = skill.getId();
        int level = skillLevels[skillId];
        if (level >= 10) return 1.0f;
        
        int currentLevelExp = EXP_TO_LEVEL[level];
        int nextLevelExp = EXP_TO_LEVEL[level + 1];
        int currentExp = experiencePoints[skillId];
        
        return (float)(currentExp - currentLevelExp) / (nextLevelExp - currentLevelExp);
    }
    
    // ============ 职业相关方法 ============
    
    /**
     * 添加职业
     */
    public void addProfession(ProfessionType profession) {
        if (!professions.contains(profession.getId())) {
            professions.add(profession.getId());
            
            // 应用职业效果
            applyProfessionEffect(profession);
            markDirty();
        }
    }
    
    /**
     * 检查是否拥有职业
     */
    public boolean hasProfession(ProfessionType profession) {
        return professions.contains(profession.getId());
    }
    
    /**
     * 应用职业效果
     */
    @SuppressWarnings("incomplete-switch")
    private void applyProfessionEffect(ProfessionType profession) {
        switch (profession) {
            case FIGHTER -> {
                maxHealth += 15;
                health = Math.min(health + 15, maxHealth);
            }
            case DEFENDER -> {
                maxHealth += 25;
                health = Math.min(health + 25, maxHealth);
            }
        }
    }
    
    // ============ 能量相关方法 ============

    private int getEffectiveMaxEnergy() {
        return Math.max(270, maxEnergy + tempMaxEnergyBonus);
    }
    
    /**
     * 消耗能量
     * @return 是否成功消耗
     */
    public boolean consumeEnergy(float amount) {
        if (amount <= 0) return true;

        // 对齐需求：能量到 0 后不再继续扣到负数。
        // exhausted 仍然保留，用于“次日恢复减半”等逻辑。
        if (energy <= 0) {
            exhausted = true;
            markDirty();
            return false;
        }

        boolean enough = energy >= amount;
        energy = energy - amount;

        if (energy <= 0) {
            energy = 0;
            exhausted = true;
        }

        markDirty();
        return enough;
    }
    
    /**
     * 恢复能量
     */
    public void restoreEnergy(float amount) {
        energy = Math.min(energy + amount, getEffectiveMaxEnergy());
        
        // 恢复到0以上时解除疲惫（需要其他方式治愈）
        // 注意：星露谷中疲惫状态不会自动解除
        
        markDirty();
    }
    
    /**
     * 治愈疲惫状态
     */
    public void cureExhaustion() {
        exhausted = false;
        markDirty();
    }
    
    /**
     * 睡觉恢复能量
     * @param sleepTime 入睡时间（游戏分钟）
     */
    public void sleep(int sleepTime) {
        // 参照 Stardew Valley 原版 Farmer.dayupdate(int timeWentToSleep)
        // 我们的 sleepTime 使用“分钟(0..1560)”表示（6:00=360，2:00=1560），
        // 这里转换成 SV 的 clock 格式（例如 6:00 -> 600，2:00 -> 2600）。

        float oldEnergy = energy;

        int hour = sleepTime / 60;
        int minute = sleepTime % 60;
        int timeWentToSleepClock = (hour * 100) + minute;
        int bedTimeClock = timeWentToSleepClock;

        energy = getEffectiveMaxEnergy();

        boolean wasExhausted = exhausted;
        if (wasExhausted) {
            exhausted = false;
            energy = (getEffectiveMaxEnergy() / 2.0f) + 1.0f;
        }

        if (bedTimeClock > 2400) {
            float staminaRestorationReduction = (1.0f - (float) (2600 - Math.min(2600, bedTimeClock)) / 200.0f) * (getEffectiveMaxEnergy() / 2.0f);
            energy -= staminaRestorationReduction;
            if (timeWentToSleepClock > 2700) {
                energy /= 2.0f;
            }
        }

        if (timeWentToSleepClock < 2700 && oldEnergy > energy && !wasExhausted) {
            energy = oldEnergy;
        }

        energy = Math.max(0.0f, Math.min(energy, getEffectiveMaxEnergy()));
        markDirty();
    }
    
    // ============ 金币相关方法 ============
    
    /**
     * 添加金币
     */
    public void addMoney(int amount) {
        if (amount > 0) {
            money += amount;
            markDirty();
        }
    }
    
    /**
     * 移除金币
     * @return 是否成功（金币是否足够）
     */
    public boolean removeMoney(int amount) {
        if (amount <= 0) return true;
        if (money >= amount) {
            money -= amount;
            markDirty();
            return true;
        }
        return false;
    }
    
    // ============ Getters & Setters ============
    
    public UUID getPlayerUUID() { return playerUUID; }
    
    public int getHealth() { return health; }
    public void setHealth(int health) { 
        this.health = Math.max(0, Math.min(health, maxHealth));
        markDirty();
    }
    
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(100, maxHealth);
        this.health = Math.min(this.health, this.maxHealth);
        markDirty();
    }
    
    public float getEnergy() { return energy; }
    public void setEnergy(float energy) {
        this.energy = Math.max(0, Math.min(energy, getEffectiveMaxEnergy()));
        markDirty();
    }
    
    public int getMaxEnergy() { return getEffectiveMaxEnergy(); }
    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = Math.max(270, maxEnergy);
        this.energy = Math.min(this.energy, getEffectiveMaxEnergy());
        markDirty();
    }
    
    public boolean isExhausted() { return exhausted; }
    
    public int getMoney() { return money; }
    public void setMoney(int money) {
        this.money = Math.max(0, money);
        markDirty();
    }
    
    public int getSkillLevel(SkillType skill) {
        int base = skillLevels[skill.getId()];
        if (skill == SkillType.FISHING) {
            return Math.max(0, base + tempFishingLevelBonus);
        }
        return base;
    }

    /**
     * 星露谷“Luck Buff”等级（目前仅来自临时Buff）。
     * 注意：这不是 dailyLuck。
     */
    public int getLuckLevel() {
        return Math.max(0, tempLuckBonus);
    }
    
    public int getSkillExperience(SkillType skill) {
        return experiencePoints[skill.getId()];
    }
    
    public void setSkillExperience(SkillType skill, int experience) {
        int skillId = skill.getId();
        experiencePoints[skillId] = Math.max(0, experience);
        
        // 重新计算等级
        int newLevel = calculateLevel(experiencePoints[skillId]);
        skillLevels[skillId] = newLevel;
        
        markDirty();
    }
    
    public List<Integer> getProfessions() {
        return new ArrayList<>(professions);
    }
    
    public boolean isDirty() { return dirty; }

    public double getDailyLuck() { return dailyLuck; }

    public int getDailyLuckDateKey() { return dailyLuckDateKey; }

    public void setDailyLuckForDate(double dailyLuck, int dateKey) {
        this.dailyLuck = dailyLuck;
        this.dailyLuckDateKey = dateKey;
        markDirty();
    }

    // ============ 临时Buff相关 ============

    public int getTempFishingLevelBonus() {
        return tempFishingLevelBonus;
    }

    public int getTempLuckBonus() {
        return tempLuckBonus;
    }

    public int getTempMaxEnergyBonus() {
        return tempMaxEnergyBonus;
    }

    /**
     * 直接设置临时Buff（用于从 MobEffect 同步）。
     * 不使用 max 叠加规则；仅当值发生变化时才 markDirty。
     */
    public boolean setTempFishingLevelBonusDirect(int bonus, long endTick) {
        bonus = Math.max(0, bonus);
        endTick = Math.max(0L, endTick);
        if (tempFishingLevelBonus == bonus && tempFishingLevelBonusEndTick == endTick) {
            return false;
        }
        tempFishingLevelBonus = bonus;
        tempFishingLevelBonusEndTick = endTick;
        markDirty();
        return true;
    }

    public boolean setTempLuckBonusDirect(int bonus, long endTick) {
        bonus = Math.max(0, bonus);
        endTick = Math.max(0L, endTick);
        if (tempLuckBonus == bonus && tempLuckBonusEndTick == endTick) {
            return false;
        }
        tempLuckBonus = bonus;
        tempLuckBonusEndTick = endTick;
        markDirty();
        return true;
    }

    public boolean setTempMaxEnergyBonusDirect(int bonus, long endTick) {
        bonus = Math.max(0, bonus);
        endTick = Math.max(0L, endTick);
        if (tempMaxEnergyBonus == bonus && tempMaxEnergyBonusEndTick == endTick) {
            return false;
        }
        tempMaxEnergyBonus = bonus;
        tempMaxEnergyBonusEndTick = endTick;
        // maxEnergy buff 影响能量上限，确保当前能量不超过新的上限
        this.energy = Math.min(this.energy, getEffectiveMaxEnergy());
        markDirty();
        return true;
    }

    public boolean clearTempFishingLevelBonus() {
        return setTempFishingLevelBonusDirect(0, 0L);
    }

    public boolean clearTempLuckBonus() {
        return setTempLuckBonusDirect(0, 0L);
    }

    public boolean clearTempMaxEnergyBonus() {
        return setTempMaxEnergyBonusDirect(0, 0L);
    }

    public void applyTempFishingLevelBonus(int bonus, long endTick) {
        this.tempFishingLevelBonus = Math.max(this.tempFishingLevelBonus, bonus);
        this.tempFishingLevelBonusEndTick = Math.max(this.tempFishingLevelBonusEndTick, endTick);
        markDirty();
    }

    public void applyTempLuckBonus(int bonus, long endTick) {
        this.tempLuckBonus = Math.max(this.tempLuckBonus, bonus);
        this.tempLuckBonusEndTick = Math.max(this.tempLuckBonusEndTick, endTick);
        markDirty();
    }

    public void applyTempMaxEnergyBonus(int bonus, long endTick) {
        this.tempMaxEnergyBonus = Math.max(this.tempMaxEnergyBonus, bonus);
        this.tempMaxEnergyBonusEndTick = Math.max(this.tempMaxEnergyBonusEndTick, endTick);
        // maxEnergy buff 影响能量上限，确保当前能量不超过新的上限
        this.energy = Math.min(this.energy, getEffectiveMaxEnergy());
        markDirty();
    }

    /**
     * 在服务端 tick 中调用，用于清理过期 buff。
     * @return 是否发生变化（需要同步到客户端）
     */
    public boolean tickTimedBuffs(long gameTime) {
        boolean changed = false;

        if (tempFishingLevelBonus != 0 && gameTime >= tempFishingLevelBonusEndTick) {
            tempFishingLevelBonus = 0;
            tempFishingLevelBonusEndTick = 0L;
            changed = true;
        }
        if (tempLuckBonus != 0 && gameTime >= tempLuckBonusEndTick) {
            tempLuckBonus = 0;
            tempLuckBonusEndTick = 0L;
            changed = true;
        }
        if (tempMaxEnergyBonus != 0 && gameTime >= tempMaxEnergyBonusEndTick) {
            tempMaxEnergyBonus = 0;
            tempMaxEnergyBonusEndTick = 0L;
            // buff 结束后，能量上限降低，夹紧当前能量
            energy = Math.min(energy, getEffectiveMaxEnergy());
            changed = true;
        }

        if (changed) {
            markDirty();
        }
        return changed;
    }
    
    public void markDirty() {
        this.dirty = true;
        this.lastSyncTime = System.currentTimeMillis();
    }
    
    public void markClean() {
        this.dirty = false;
    }
}
