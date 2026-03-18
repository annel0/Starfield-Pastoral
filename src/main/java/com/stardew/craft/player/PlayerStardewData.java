package com.stardew.craft.player;

import com.stardew.craft.deco.DecorationStyleRegistry;
import com.stardew.craft.deco.DecorationType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final List<ProfessionChoicePrompt> pendingProfessionChoices = new ArrayList<>();
    
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

    private int tempFarmingLevelBonus;
    private long tempFarmingLevelBonusEndTick;

    private int tempForagingLevelBonus;
    private long tempForagingLevelBonusEndTick;

    private int tempMiningLevelBonus;
    private long tempMiningLevelBonusEndTick;

    private int tempAttackBonus;
    private long tempAttackBonusEndTick;

    private int tempDefenseBonus;
    private long tempDefenseBonusEndTick;

    private int tempMagneticRadiusBonus;
    private long tempMagneticRadiusBonusEndTick;

    // ============ 装饰解锁 ============
    private final Set<String> unlockedWallpaperStyles = new HashSet<>();
    private final Set<String> unlockedFlooringStyles = new HashSet<>();

    // ============ 配方解锁 ============
    private final Set<String> unlockedRecipes = new HashSet<>();
    private final Map<String, Integer> recipeCraftCounts = new HashMap<>();

    // ============ 钓鱼记录 ============
    // 用于对齐原版 CatchLimit（例如传奇鱼一次性）
    private final Map<String, Integer> fishCatchCounts = new HashMap<>();

    // ============ 出货统计（夜间结算） ============
    // 对齐原版 player.shippedBasic 与 stats.ItemsShipped 的基础数据结构。
    private final Set<String> shippedBasic = new HashSet<>();
    private final Map<String, Integer> itemsShipped = new HashMap<>();

    // ============ 特殊订单规则 ============
    // 对齐原版 PLAYER_SPECIAL_ORDER_RULE_ACTIVE 条件分支。
    private final Set<String> activeSpecialOrderRules = new HashSet<>();
    
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
        this.tempFarmingLevelBonus = 0;
        this.tempFarmingLevelBonusEndTick = 0L;
        this.tempForagingLevelBonus = 0;
        this.tempForagingLevelBonusEndTick = 0L;
        this.tempMiningLevelBonus = 0;
        this.tempMiningLevelBonusEndTick = 0L;
        this.tempAttackBonus = 0;
        this.tempAttackBonusEndTick = 0L;
        this.tempDefenseBonus = 0;
        this.tempDefenseBonusEndTick = 0L;
        this.tempMagneticRadiusBonus = 0;
        this.tempMagneticRadiusBonusEndTick = 0L;

        this.unlockedWallpaperStyles.add(DecorationStyleRegistry.getDefaultStyleId(DecorationType.WALLPAPER));
        this.unlockedFlooringStyles.add(DecorationStyleRegistry.getDefaultStyleId(DecorationType.FLOORING));
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

        data.pendingProfessionChoices.clear();
        if (tag.contains("PendingProfessionChoices")) {
            ListTag list = tag.getList("PendingProfessionChoices", 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                if (!entry.contains("SkillId") || !entry.contains("Level")) {
                    continue;
                }
                SkillType skill = SkillType.fromId(entry.getInt("SkillId"));
                int level = entry.getInt("Level");
                if ((level == 5 || level == 10) && !data.hasPendingProfessionChoice(skill, level)) {
                    data.pendingProfessionChoices.add(new ProfessionChoicePrompt(skill, level));
                }
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
        data.tempFarmingLevelBonus = tag.contains("TempFarmingLevelBonus") ? tag.getInt("TempFarmingLevelBonus") : 0;
        data.tempFarmingLevelBonusEndTick = tag.contains("TempFarmingLevelBonusEndTick") ? tag.getLong("TempFarmingLevelBonusEndTick") : 0L;
        data.tempForagingLevelBonus = tag.contains("TempForagingLevelBonus") ? tag.getInt("TempForagingLevelBonus") : 0;
        data.tempForagingLevelBonusEndTick = tag.contains("TempForagingLevelBonusEndTick") ? tag.getLong("TempForagingLevelBonusEndTick") : 0L;
        data.tempMiningLevelBonus = tag.contains("TempMiningLevelBonus") ? tag.getInt("TempMiningLevelBonus") : 0;
        data.tempMiningLevelBonusEndTick = tag.contains("TempMiningLevelBonusEndTick") ? tag.getLong("TempMiningLevelBonusEndTick") : 0L;
        data.tempAttackBonus = tag.contains("TempAttackBonus") ? tag.getInt("TempAttackBonus") : 0;
        data.tempAttackBonusEndTick = tag.contains("TempAttackBonusEndTick") ? tag.getLong("TempAttackBonusEndTick") : 0L;
        data.tempDefenseBonus = tag.contains("TempDefenseBonus") ? tag.getInt("TempDefenseBonus") : 0;
        data.tempDefenseBonusEndTick = tag.contains("TempDefenseBonusEndTick") ? tag.getLong("TempDefenseBonusEndTick") : 0L;
        data.tempMagneticRadiusBonus = tag.contains("TempMagneticRadiusBonus") ? tag.getInt("TempMagneticRadiusBonus") : 0;
        data.tempMagneticRadiusBonusEndTick = tag.contains("TempMagneticRadiusBonusEndTick") ? tag.getLong("TempMagneticRadiusBonusEndTick") : 0L;

        data.unlockedWallpaperStyles.clear();
        data.unlockedFlooringStyles.clear();
        data.unlockedRecipes.clear();
        data.recipeCraftCounts.clear();
        if (tag.contains("UnlockedWallpaperStyles")) {
            ListTag list = tag.getList("UnlockedWallpaperStyles", 8);
            for (int i = 0; i < list.size(); i++) {
                data.unlockedWallpaperStyles.add(list.getString(i));
            }
        }
        if (tag.contains("UnlockedFlooringStyles")) {
            ListTag list = tag.getList("UnlockedFlooringStyles", 8);
            for (int i = 0; i < list.size(); i++) {
                data.unlockedFlooringStyles.add(list.getString(i));
            }
        }
        if (tag.contains("UnlockedRecipes")) {
            ListTag list = tag.getList("UnlockedRecipes", 8);
            for (int i = 0; i < list.size(); i++) {
                data.unlockedRecipes.add(list.getString(i));
            }
        }
        if (tag.contains("RecipeCraftCounts")) {
            ListTag list = tag.getList("RecipeCraftCounts", 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                if (!entry.contains("Recipe")) {
                    continue;
                }
                String recipeId = entry.getString("Recipe");
                int count = Math.max(0, entry.getInt("Count"));
                if (!recipeId.isBlank() && count > 0) {
                    data.recipeCraftCounts.put(recipeId, count);
                }
            }
        }
        data.unlockedWallpaperStyles.add(DecorationStyleRegistry.getDefaultStyleId(DecorationType.WALLPAPER));
        data.unlockedFlooringStyles.add(DecorationStyleRegistry.getDefaultStyleId(DecorationType.FLOORING));

        data.fishCatchCounts.clear();
        if (tag.contains("FishCatchCounts")) {
            ListTag list = tag.getList("FishCatchCounts", 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                if (!entry.contains("Item")) {
                    continue;
                }
                String itemId = entry.getString("Item");
                int count = Math.max(0, entry.getInt("Count"));
                if (!itemId.isBlank() && count > 0) {
                    data.fishCatchCounts.put(itemId, count);
                }
            }
        }

        data.shippedBasic.clear();
        if (tag.contains("ShippedBasic")) {
            ListTag list = tag.getList("ShippedBasic", 8);
            for (int i = 0; i < list.size(); i++) {
                String itemId = list.getString(i);
                if (!itemId.isBlank()) {
                    data.shippedBasic.add(itemId);
                }
            }
        }

        data.itemsShipped.clear();
        if (tag.contains("ItemsShipped")) {
            ListTag list = tag.getList("ItemsShipped", 10);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                if (!entry.contains("Item")) {
                    continue;
                }
                String itemId = entry.getString("Item");
                int count = Math.max(0, entry.getInt("Count"));
                if (!itemId.isBlank() && count > 0) {
                    data.itemsShipped.put(itemId, count);
                }
            }
        }

        data.activeSpecialOrderRules.clear();
        if (tag.contains("ActiveSpecialOrderRules")) {
            ListTag list = tag.getList("ActiveSpecialOrderRules", 8);
            for (int i = 0; i < list.size(); i++) {
                String rule = list.getString(i);
                if (!rule.isBlank()) {
                    data.activeSpecialOrderRules.add(rule);
                }
            }
        }

        // 安全钳制：避免异常值导致越界（Buff 允许 maxEnergy 临时上升）
        data.maxHealth = Math.max(100, data.maxHealth);
        data.maxEnergy = Math.max(270, data.maxEnergy);
        int effectiveMaxEnergy = data.maxEnergy + Math.max(0, data.tempMaxEnergyBonus);
        data.health = Math.max(0, Math.min(data.health, data.maxHealth));
        data.energy = Math.max(0, Math.min(data.energy, effectiveMaxEnergy));

        data.repairMissingProfessionChoices();

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

        ListTag pendingChoicesTag = new ListTag();
        for (ProfessionChoicePrompt prompt : pendingProfessionChoices) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("SkillId", prompt.skill().getId());
            entry.putInt("Level", prompt.level());
            pendingChoicesTag.add(entry);
        }
        tag.put("PendingProfessionChoices", pendingChoicesTag);
        
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
        tag.putInt("TempFarmingLevelBonus", tempFarmingLevelBonus);
        tag.putLong("TempFarmingLevelBonusEndTick", tempFarmingLevelBonusEndTick);
        tag.putInt("TempForagingLevelBonus", tempForagingLevelBonus);
        tag.putLong("TempForagingLevelBonusEndTick", tempForagingLevelBonusEndTick);
        tag.putInt("TempMiningLevelBonus", tempMiningLevelBonus);
        tag.putLong("TempMiningLevelBonusEndTick", tempMiningLevelBonusEndTick);
        tag.putInt("TempAttackBonus", tempAttackBonus);
        tag.putLong("TempAttackBonusEndTick", tempAttackBonusEndTick);
        tag.putInt("TempDefenseBonus", tempDefenseBonus);
        tag.putLong("TempDefenseBonusEndTick", tempDefenseBonusEndTick);
        tag.putInt("TempMagneticRadiusBonus", tempMagneticRadiusBonus);
        tag.putLong("TempMagneticRadiusBonusEndTick", tempMagneticRadiusBonusEndTick);

        ListTag unlockedWallpapers = new ListTag();
        for (String styleId : unlockedWallpaperStyles) {
            unlockedWallpapers.add(StringTag.valueOf(styleId));
        }
        tag.put("UnlockedWallpaperStyles", unlockedWallpapers);

        ListTag unlockedFloorings = new ListTag();
        for (String styleId : unlockedFlooringStyles) {
            unlockedFloorings.add(StringTag.valueOf(styleId));
        }
        tag.put("UnlockedFlooringStyles", unlockedFloorings);

        ListTag unlockedRecipesTag = new ListTag();
        for (String recipeId : unlockedRecipes) {
            unlockedRecipesTag.add(StringTag.valueOf(recipeId));
        }
        tag.put("UnlockedRecipes", unlockedRecipesTag);

        ListTag recipeCraftCountsTag = new ListTag();
        for (Map.Entry<String, Integer> entry : recipeCraftCounts.entrySet()) {
            String recipeId = entry.getKey();
            int count = entry.getValue() == null ? 0 : entry.getValue();
            if (recipeId == null || recipeId.isBlank() || count <= 0) {
                continue;
            }
            CompoundTag recipeTag = new CompoundTag();
            recipeTag.putString("Recipe", recipeId);
            recipeTag.putInt("Count", count);
            recipeCraftCountsTag.add(recipeTag);
        }
        tag.put("RecipeCraftCounts", recipeCraftCountsTag);

        ListTag fishCounts = new ListTag();
        for (Map.Entry<String, Integer> entry : fishCatchCounts.entrySet()) {
            String itemId = entry.getKey();
            int count = entry.getValue() == null ? 0 : entry.getValue();
            if (itemId == null || itemId.isBlank() || count <= 0) {
                continue;
            }
            CompoundTag fishCountTag = new CompoundTag();
            fishCountTag.putString("Item", itemId);
            fishCountTag.putInt("Count", count);
            fishCounts.add(fishCountTag);
        }
        tag.put("FishCatchCounts", fishCounts);

        ListTag shippedBasicTag = new ListTag();
        for (String itemId : shippedBasic) {
            if (itemId != null && !itemId.isBlank()) {
                shippedBasicTag.add(StringTag.valueOf(itemId));
            }
        }
        tag.put("ShippedBasic", shippedBasicTag);

        ListTag itemsShippedTag = new ListTag();
        for (Map.Entry<String, Integer> entry : itemsShipped.entrySet()) {
            String itemId = entry.getKey();
            int count = entry.getValue() == null ? 0 : entry.getValue();
            if (itemId == null || itemId.isBlank() || count <= 0) {
                continue;
            }
            CompoundTag shippedTag = new CompoundTag();
            shippedTag.putString("Item", itemId);
            shippedTag.putInt("Count", count);
            itemsShippedTag.add(shippedTag);
        }
        tag.put("ItemsShipped", itemsShippedTag);

        ListTag activeRules = new ListTag();
        for (String rule : activeSpecialOrderRules) {
            if (rule != null && !rule.isBlank()) {
                activeRules.add(StringTag.valueOf(rule));
            }
        }
        tag.put("ActiveSpecialOrderRules", activeRules);
        
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
        int oldLevelByExp = calculateLevel(experiencePoints[skillId]);

        experiencePoints[skillId] += amount;
        int newLevelByExp = calculateLevel(experiencePoints[skillId]);

        markDirty();
        return newLevelByExp > oldLevelByExp;
    }

    public int getLevelByExperience(SkillType skill) {
        return calculateLevel(experiencePoints[skill.getId()]);
    }

    public List<SkillLevelUp> applyPendingSkillLevelUps() {
        List<SkillLevelUp> applied = new ArrayList<>();

        for (SkillType skill : SkillType.values()) {
            int skillId = skill.getId();
            int oldLevel = skillLevels[skillId];
            int targetLevel = Math.min(10, calculateLevel(experiencePoints[skillId]));
            if (targetLevel <= oldLevel) {
                continue;
            }

            for (int level = oldLevel + 1; level <= targetLevel; level++) {
                if (level == 5 && !hasLevel5Profession(skill) && !hasPendingProfessionChoice(skill, 5)) {
                    pendingProfessionChoices.add(new ProfessionChoicePrompt(skill, 5));
                }
                if (level == 10 && hasLevel5Profession(skill) && !hasLevel10Profession(skill) && !hasPendingProfessionChoice(skill, 10)) {
                    pendingProfessionChoices.add(new ProfessionChoicePrompt(skill, 10));
                }

                if (skill == SkillType.COMBAT && level != 5 && level != 10) {
                    maxHealth += 5;
                    health = Math.min(health + 5, maxHealth);
                }

                applied.add(new SkillLevelUp(skill, level));
            }

            skillLevels[skillId] = targetLevel;
        }

        if (!applied.isEmpty()) {
            markDirty();
        }
        return applied;
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
        
        float progress = (float)(currentExp - currentLevelExp) / (nextLevelExp - currentLevelExp);
        if (progress < 0.0f) {
            return 0.0f;
        }
        return Math.min(progress, 1.0f);
    }
    
    // ============ 职业相关方法 ============
    
    /**
     * 添加职业
     */
    public boolean addProfession(ProfessionType profession) {
        if (profession == null || professions.contains(profession.getId())) {
            return false;
        }
        professions.add(profession.getId());

        // 应用职业效果
        applyProfessionEffect(profession);
        markDirty();
        return true;
    }
    
    /**
     * 检查是否拥有职业
     */
    public boolean hasProfession(ProfessionType profession) {
        return professions.contains(profession.getId());
    }

    public boolean removeProfession(ProfessionType profession) {
        if (profession == null || !professions.remove(Integer.valueOf(profession.getId()))) {
            return false;
        }
        revertProfessionEffect(profession);
        markDirty();
        return true;
    }

    public boolean clearProfessions() {
        if (professions.isEmpty()) {
            return false;
        }
        List<Integer> copy = new ArrayList<>(professions);
        professions.clear();
        for (int professionId : copy) {
            ProfessionType profession = ProfessionType.fromId(professionId);
            if (profession != null) {
                revertProfessionEffect(profession);
            }
        }
        pendingProfessionChoices.clear();
        repairMissingProfessionChoices();
        markDirty();
        return true;
    }

    public List<ProfessionChoicePrompt> getPendingProfessionChoices() {
        return new ArrayList<>(pendingProfessionChoices);
    }

    public boolean hasPendingProfessionChoices() {
        return !pendingProfessionChoices.isEmpty();
    }

    public boolean choosePendingProfession(ProfessionType selectedProfession) {
        if (selectedProfession == null || pendingProfessionChoices.isEmpty()) {
            return false;
        }

        ProfessionChoicePrompt prompt = pendingProfessionChoices.get(0);
        SkillType promptSkill = prompt.skill();
        int promptLevel = prompt.level();

        if (selectedProfession.getSkillType() != promptSkill || selectedProfession.getRequiredLevel() != promptLevel) {
            return false;
        }

        if (promptLevel == 5) {
            ProfessionType[] options = ProfessionType.getLevel5Options(promptSkill);
            if (selectedProfession != options[0] && selectedProfession != options[1]) {
                return false;
            }
        } else if (promptLevel == 10) {
            ProfessionType level5Profession = getLevel5Profession(promptSkill);
            if (level5Profession == null) {
                return false;
            }
            ProfessionType[] options = ProfessionType.getLevel10Options(promptSkill, level5Profession);
            if (selectedProfession != options[0] && selectedProfession != options[1]) {
                return false;
            }
        } else {
            return false;
        }

        if (!addProfession(selectedProfession)) {
            return false;
        }

        pendingProfessionChoices.remove(0);

        if (promptLevel == 5 && getRawSkillLevel(promptSkill) >= 10 && !hasLevel10Profession(promptSkill) && !hasPendingProfessionChoice(promptSkill, 10)) {
            pendingProfessionChoices.add(0, new ProfessionChoicePrompt(promptSkill, 10));
        }

        markDirty();
        return true;
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

    @SuppressWarnings("incomplete-switch")
    private void revertProfessionEffect(ProfessionType profession) {
        switch (profession) {
            case FIGHTER -> {
                maxHealth = Math.max(100, maxHealth - 15);
                health = Math.min(health, maxHealth);
            }
            case DEFENDER -> {
                maxHealth = Math.max(100, maxHealth - 25);
                health = Math.min(health, maxHealth);
            }
        }
    }

    private boolean hasPendingProfessionChoice(SkillType skill, int level) {
        for (ProfessionChoicePrompt prompt : pendingProfessionChoices) {
            if (prompt.skill() == skill && prompt.level() == level) {
                return true;
            }
        }
        return false;
    }

    private boolean hasLevel5Profession(SkillType skill) {
        return getLevel5Profession(skill) != null;
    }

    private ProfessionType getLevel5Profession(SkillType skill) {
        for (int professionId : professions) {
            ProfessionType profession = ProfessionType.fromId(professionId);
            if (profession != null && profession.getSkillType() == skill && profession.getRequiredLevel() == 5) {
                return profession;
            }
        }
        return null;
    }

    private boolean hasLevel10Profession(SkillType skill) {
        for (int professionId : professions) {
            ProfessionType profession = ProfessionType.fromId(professionId);
            if (profession != null && profession.getSkillType() == skill && profession.getRequiredLevel() == 10) {
                return true;
            }
        }
        return false;
    }

    public void repairMissingProfessionChoices() {
        pendingProfessionChoices.removeIf(prompt -> getRawSkillLevel(prompt.skill()) < prompt.level());

        for (SkillType skill : SkillType.values()) {
            int rawLevel = getRawSkillLevel(skill);
            if (rawLevel >= 5 && !hasLevel5Profession(skill) && !hasPendingProfessionChoice(skill, 5)) {
                pendingProfessionChoices.add(new ProfessionChoicePrompt(skill, 5));
            }
            if (rawLevel >= 10 && hasLevel5Profession(skill) && !hasLevel10Profession(skill) && !hasPendingProfessionChoice(skill, 10)) {
                pendingProfessionChoices.add(new ProfessionChoicePrompt(skill, 10));
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
        return switch (skill) {
            case FISHING -> Math.max(0, base + tempFishingLevelBonus);
            case FARMING -> Math.max(0, base + tempFarmingLevelBonus);
            case FORAGING -> Math.max(0, base + tempForagingLevelBonus);
            case MINING -> Math.max(0, base + tempMiningLevelBonus);
            default -> base;
        };
    }

    public int getRawSkillLevel(SkillType skill) {
        return skillLevels[skill.getId()];
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
        repairMissingProfessionChoices();
        
        markDirty();
    }
    
    public List<Integer> getProfessions() {
        return new ArrayList<>(professions);
    }

    public record ProfessionChoicePrompt(SkillType skill, int level) {
    }

    public record SkillLevelUp(SkillType skill, int newLevel) {
    }

    public boolean isDecorationUnlocked(DecorationType type, String styleId) {
        if (styleId == null || styleId.isBlank()) {
            return false;
        }
        return (type == DecorationType.WALLPAPER ? unlockedWallpaperStyles : unlockedFlooringStyles).contains(styleId);
    }

    public boolean unlockDecoration(DecorationType type, String styleId) {
        if (styleId == null || styleId.isBlank()) {
            return false;
        }
        boolean changed = (type == DecorationType.WALLPAPER ? unlockedWallpaperStyles : unlockedFlooringStyles).add(styleId);
        if (changed) {
            markDirty();
        }
        return changed;
    }

    public boolean lockDecoration(DecorationType type, String styleId) {
        if (styleId == null || styleId.isBlank()) {
            return false;
        }
        String defaultStyle = DecorationStyleRegistry.getDefaultStyleId(type);
        if (defaultStyle.equals(styleId)) {
            return false;
        }
        boolean changed = (type == DecorationType.WALLPAPER ? unlockedWallpaperStyles : unlockedFlooringStyles).remove(styleId);
        if (changed) {
            markDirty();
        }
        return changed;
    }

    public Set<String> getUnlockedDecorations(DecorationType type) {
        return new HashSet<>(type == DecorationType.WALLPAPER ? unlockedWallpaperStyles : unlockedFlooringStyles);
    }

    public boolean isRecipeUnlocked(String recipeId) {
        if (recipeId == null || recipeId.isBlank()) {
            return false;
        }
        return unlockedRecipes.contains(recipeId);
    }

    public boolean unlockRecipe(String recipeId) {
        if (recipeId == null || recipeId.isBlank()) {
            return false;
        }
        boolean changed = unlockedRecipes.add(recipeId);
        if (changed) {
            markDirty();
        }
        return changed;
    }

    public boolean lockRecipe(String recipeId) {
        if (recipeId == null || recipeId.isBlank()) {
            return false;
        }
        boolean changed = unlockedRecipes.remove(recipeId);
        if (changed) {
            markDirty();
        }
        return changed;
    }

    public Set<String> getUnlockedRecipes() {
        return new HashSet<>(unlockedRecipes);
    }

    public int getRecipeCraftCount(String recipeId) {
        if (recipeId == null || recipeId.isBlank()) {
            return 0;
        }
        return Math.max(0, recipeCraftCounts.getOrDefault(recipeId, 0));
    }

    public Map<String, Integer> getAllRecipeCraftCounts() {
        return new HashMap<>(recipeCraftCounts);
    }

    public boolean recordRecipeCrafted(String recipeId, int craftedAmount) {
        if (recipeId == null || recipeId.isBlank() || craftedAmount <= 0) {
            return false;
        }

        int current = Math.max(0, recipeCraftCounts.getOrDefault(recipeId, 0));
        int next = current + craftedAmount;
        if (next == current) {
            return false;
        }

        recipeCraftCounts.put(recipeId, next);
        markDirty();
        return true;
    }

    public int getFishCatchCount(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return 0;
        }
        return Math.max(0, fishCatchCounts.getOrDefault(itemId, 0));
    }

    public boolean addFishCatchCount(String itemId, int amount) {
        if (itemId == null || itemId.isBlank() || amount <= 0) {
            return false;
        }
        int next = Math.max(0, fishCatchCounts.getOrDefault(itemId, 0)) + amount;
        fishCatchCounts.put(itemId, next);
        markDirty();
        return true;
    }

    public boolean isSpecialOrderRuleActive(String ruleId) {
        if (ruleId == null || ruleId.isBlank()) {
            return false;
        }
        return activeSpecialOrderRules.contains(ruleId);
    }

    public boolean recordShippedItem(String itemId, int amount) {
        if (itemId == null || itemId.isBlank() || amount <= 0) {
            return false;
        }

        boolean changed = shippedBasic.add(itemId);
        int next = Math.max(0, itemsShipped.getOrDefault(itemId, 0)) + amount;
        if (next != itemsShipped.getOrDefault(itemId, 0)) {
            itemsShipped.put(itemId, next);
            changed = true;
        }

        if (changed) {
            markDirty();
        }
        return changed;
    }

    public Set<String> getShippedBasic() {
        return new HashSet<>(shippedBasic);
    }

    public int getItemsShippedCount(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return 0;
        }
        return Math.max(0, itemsShipped.getOrDefault(itemId, 0));
    }

    public boolean setSpecialOrderRuleActive(String ruleId, boolean active) {
        if (ruleId == null || ruleId.isBlank()) {
            return false;
        }
        boolean changed = active ? activeSpecialOrderRules.add(ruleId) : activeSpecialOrderRules.remove(ruleId);
        if (changed) {
            markDirty();
        }
        return changed;
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

    public int getTempFarmingLevelBonus() {
        return tempFarmingLevelBonus;
    }

    public int getTempForagingLevelBonus() {
        return tempForagingLevelBonus;
    }

    public int getTempMiningLevelBonus() {
        return tempMiningLevelBonus;
    }

    public int getTempAttackBonus() {
        return tempAttackBonus;
    }

    public int getTempDefenseBonus() {
        return tempDefenseBonus;
    }

    public int getTempMagneticRadiusBonus() {
        return tempMagneticRadiusBonus;
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

    public boolean setTempFarmingLevelBonusDirect(int bonus, long endTick) {
        bonus = Math.max(0, bonus);
        endTick = Math.max(0L, endTick);
        if (tempFarmingLevelBonus == bonus && tempFarmingLevelBonusEndTick == endTick) {
            return false;
        }
        tempFarmingLevelBonus = bonus;
        tempFarmingLevelBonusEndTick = endTick;
        markDirty();
        return true;
    }

    public boolean setTempForagingLevelBonusDirect(int bonus, long endTick) {
        bonus = Math.max(0, bonus);
        endTick = Math.max(0L, endTick);
        if (tempForagingLevelBonus == bonus && tempForagingLevelBonusEndTick == endTick) {
            return false;
        }
        tempForagingLevelBonus = bonus;
        tempForagingLevelBonusEndTick = endTick;
        markDirty();
        return true;
    }

    public boolean setTempMiningLevelBonusDirect(int bonus, long endTick) {
        bonus = Math.max(0, bonus);
        endTick = Math.max(0L, endTick);
        if (tempMiningLevelBonus == bonus && tempMiningLevelBonusEndTick == endTick) {
            return false;
        }
        tempMiningLevelBonus = bonus;
        tempMiningLevelBonusEndTick = endTick;
        markDirty();
        return true;
    }

    public boolean setTempAttackBonusDirect(int bonus, long endTick) {
        bonus = Math.max(0, bonus);
        endTick = Math.max(0L, endTick);
        if (tempAttackBonus == bonus && tempAttackBonusEndTick == endTick) {
            return false;
        }
        tempAttackBonus = bonus;
        tempAttackBonusEndTick = endTick;
        markDirty();
        return true;
    }

    public boolean setTempDefenseBonusDirect(int bonus, long endTick) {
        bonus = Math.max(0, bonus);
        endTick = Math.max(0L, endTick);
        if (tempDefenseBonus == bonus && tempDefenseBonusEndTick == endTick) {
            return false;
        }
        tempDefenseBonus = bonus;
        tempDefenseBonusEndTick = endTick;
        markDirty();
        return true;
    }

    public boolean setTempMagneticRadiusBonusDirect(int bonus, long endTick) {
        bonus = Math.max(0, bonus);
        endTick = Math.max(0L, endTick);
        if (tempMagneticRadiusBonus == bonus && tempMagneticRadiusBonusEndTick == endTick) {
            return false;
        }
        tempMagneticRadiusBonus = bonus;
        tempMagneticRadiusBonusEndTick = endTick;
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

    public boolean clearTempFarmingLevelBonus() {
        return setTempFarmingLevelBonusDirect(0, 0L);
    }

    public boolean clearTempForagingLevelBonus() {
        return setTempForagingLevelBonusDirect(0, 0L);
    }

    public boolean clearTempMiningLevelBonus() {
        return setTempMiningLevelBonusDirect(0, 0L);
    }

    public boolean clearTempAttackBonus() {
        return setTempAttackBonusDirect(0, 0L);
    }

    public boolean clearTempDefenseBonus() {
        return setTempDefenseBonusDirect(0, 0L);
    }

    public boolean clearTempMagneticRadiusBonus() {
        return setTempMagneticRadiusBonusDirect(0, 0L);
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

    public void applyTempFarmingLevelBonus(int bonus, long endTick) {
        this.tempFarmingLevelBonus = Math.max(this.tempFarmingLevelBonus, bonus);
        this.tempFarmingLevelBonusEndTick = Math.max(this.tempFarmingLevelBonusEndTick, endTick);
        markDirty();
    }

    public void applyTempForagingLevelBonus(int bonus, long endTick) {
        this.tempForagingLevelBonus = Math.max(this.tempForagingLevelBonus, bonus);
        this.tempForagingLevelBonusEndTick = Math.max(this.tempForagingLevelBonusEndTick, endTick);
        markDirty();
    }

    public void applyTempMiningLevelBonus(int bonus, long endTick) {
        this.tempMiningLevelBonus = Math.max(this.tempMiningLevelBonus, bonus);
        this.tempMiningLevelBonusEndTick = Math.max(this.tempMiningLevelBonusEndTick, endTick);
        markDirty();
    }

    public void applyTempAttackBonus(int bonus, long endTick) {
        this.tempAttackBonus = Math.max(this.tempAttackBonus, bonus);
        this.tempAttackBonusEndTick = Math.max(this.tempAttackBonusEndTick, endTick);
        markDirty();
    }

    public void applyTempDefenseBonus(int bonus, long endTick) {
        this.tempDefenseBonus = Math.max(this.tempDefenseBonus, bonus);
        this.tempDefenseBonusEndTick = Math.max(this.tempDefenseBonusEndTick, endTick);
        markDirty();
    }

    public void applyTempMagneticRadiusBonus(int bonus, long endTick) {
        this.tempMagneticRadiusBonus = Math.max(this.tempMagneticRadiusBonus, bonus);
        this.tempMagneticRadiusBonusEndTick = Math.max(this.tempMagneticRadiusBonusEndTick, endTick);
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
        if (tempFarmingLevelBonus != 0 && gameTime >= tempFarmingLevelBonusEndTick) {
            tempFarmingLevelBonus = 0;
            tempFarmingLevelBonusEndTick = 0L;
            changed = true;
        }
        if (tempForagingLevelBonus != 0 && gameTime >= tempForagingLevelBonusEndTick) {
            tempForagingLevelBonus = 0;
            tempForagingLevelBonusEndTick = 0L;
            changed = true;
        }
        if (tempMiningLevelBonus != 0 && gameTime >= tempMiningLevelBonusEndTick) {
            tempMiningLevelBonus = 0;
            tempMiningLevelBonusEndTick = 0L;
            changed = true;
        }
        if (tempAttackBonus != 0 && gameTime >= tempAttackBonusEndTick) {
            tempAttackBonus = 0;
            tempAttackBonusEndTick = 0L;
            changed = true;
        }
        if (tempDefenseBonus != 0 && gameTime >= tempDefenseBonusEndTick) {
            tempDefenseBonus = 0;
            tempDefenseBonusEndTick = 0L;
            changed = true;
        }
        if (tempMagneticRadiusBonus != 0 && gameTime >= tempMagneticRadiusBonusEndTick) {
            tempMagneticRadiusBonus = 0;
            tempMagneticRadiusBonusEndTick = 0L;
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
