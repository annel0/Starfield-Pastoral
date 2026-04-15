package com.stardew.craft.player;

import com.stardew.craft.deco.DecorationStyleRegistry;
import com.stardew.craft.deco.DecorationType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.stardew.craft.quest.QuestManager;

/**
 * 玩家星露谷数据
 * 存储玩家的所有星露谷相关数据（生命、能量、技能、金币等）
 */
@SuppressWarnings("null")
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

    // SDV parity: newLevels — 白天获得的等级提升列表，睡觉时消费（用于升级动画 + 职业选择）
    private final List<SkillLevelUp> pendingNewLevels = new ArrayList<>();
    
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

    // ============ 垃圾桶统计 ============
    private int trashCansChecked;

    // ============ 邮件标记（SDV mailReceived parity） ============
    private final Set<String> mailFlags = new HashSet<>();

    // ============ 邮箱系统（SDV mailbox / mailForTomorrow parity） ============
    // mailbox: 当前可读的邮件ID队列（SDV Farmer.mailbox）
    private final List<String> mailbox = new ArrayList<>();
    // mailForTomorrow: 明天投递的邮件ID队列（SDV Farmer.mailForTomorrow）
    private final List<String> mailForTomorrow = new ArrayList<>();

    // ============ 任务系统 ============
    private final QuestManager questManager = new QuestManager();

    // ============ 怪物讨伐（SDV MonsterSlayerQuests parity） ============
    private final Map<String, Integer> monsterKillCounts = new HashMap<>(); // goalKey → kills
    private final Set<String> claimedSlayerRewards = new HashSet<>();      // goalKeys already claimed

    // ============ 特殊物品去重（SDV Farmer.specialItems parity） ============
    // Tracks unique items already obtained (e.g. Neptune's Glaive from fishing treasure)
    private final Set<String> specialItems = new HashSet<>();

    // ============ 巫师塔枢纽（维度传送） ============
    private boolean wizardQuestComplete;          // 末影之眼已交付，星露谷入口已解锁
    private boolean wizardFirstMet;               // 是否已经看过巫师的初次剧情
    private boolean starterToolsGiven;            // 是否已给予新手工具六件套
    private boolean bilibiliRewardClaimed;          // 是否已领取B站关注奖励（彩虹猫之刃）
    @Nullable
    private BlockPos overworldReturnPos;           // 玩家从主世界进入巫师塔时记录的返回坐标
    @Nullable
    private ResourceKey<Level> wizardSourceDimension;  // 进入巫师塔内部时的来源维度

    // ============ 装备系统（戒指 + 靴子） ============
    // SDV parity: Farmer.leftRing + Farmer.rightRing + Farmer.boots
    // 存储物品注册ID，如 "stardewcraft:vampire_ring"，空字符串表示未装备
    private String equippedLeftRing = "";
    private String equippedRightRing = "";
    private String equippedBoots = "";
    private long lastPhoenixReviveDay = -1;  // 凤凰戒指：上次复活的游戏日（每天只能复活一次）

    // ============ 工具升级（铁匠铺） ============
    // SDV parity: Farmer.toolBeingUpgraded + Farmer.daysLeftForToolUpgrade
    private String toolBeingUpgraded = "";   // item ID e.g. "stardewcraft:copper_axe", empty = none
    private int daysLeftForToolUpgrade;      // 0 = ready for pickup
    private boolean toolUpgradeNotified;     // true = already notified player this morning

    // ============ 晕倒/死亡系统 ============
    // 战斗死亡标志：次日 sleep() 后压体力到 2
    private boolean passedOutFromCombat;
    // 上次死亡丢失的物品（供 Marlon 物品找回商店使用）
    private final List<net.minecraft.world.item.ItemStack> itemsLostLastDeath = new ArrayList<>();
    
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

        // 晕倒/死亡系统
        data.passedOutFromCombat = tag.getBoolean("PassedOutFromCombat");
        data.itemsLostLastDeath.clear();
        if (tag.contains("ItemsLostLastDeath")) {
            ListTag lostItemsTag = tag.getList("ItemsLostLastDeath", 10); // 10 = CompoundTag
            for (int li = 0; li < lostItemsTag.size(); li++) {
                CompoundTag itemTag = lostItemsTag.getCompound(li);
                var itemRL = ResourceLocation.tryParse(itemTag.getString("Id"));
                if (itemRL != null && net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(itemRL)) {
                    net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemRL);
                    data.itemsLostLastDeath.add(new net.minecraft.world.item.ItemStack(item, itemTag.getInt("Count")));
                }
            }
        }
        
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

        // pendingNewLevels
        data.pendingNewLevels.clear();
        if (tag.contains("PendingNewLevels")) {
            ListTag nlList = tag.getList("PendingNewLevels", 10);
            for (int i = 0; i < nlList.size(); i++) {
                CompoundTag entry = nlList.getCompound(i);
                if (!entry.contains("SkillId") || !entry.contains("Level")) continue;
                SkillType skill = SkillType.fromId(entry.getInt("SkillId"));
                data.pendingNewLevels.add(new SkillLevelUp(skill, entry.getInt("Level")));
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

        data.trashCansChecked = tag.contains("TrashCansChecked") ? tag.getInt("TrashCansChecked") : 0;

        // 邮件标记
        if (tag.contains("MailFlags", 9)) { // 9 = TAG_List
            ListTag mailList = tag.getList("MailFlags", 8); // 8 = TAG_String
            for (int i = 0; i < mailList.size(); i++) {
                String flag = mailList.getString(i);
                if (!flag.isBlank()) data.mailFlags.add(flag);
            }
        }

        // 邮箱队列
        if (tag.contains("Mailbox", 9)) {
            ListTag mboxList = tag.getList("Mailbox", 8);
            for (int i = 0; i < mboxList.size(); i++) {
                String mid = mboxList.getString(i);
                if (!mid.isBlank()) data.mailbox.add(mid);
            }
        }
        if (tag.contains("MailForTomorrow", 9)) {
            ListTag mftList = tag.getList("MailForTomorrow", 8);
            for (int i = 0; i < mftList.size(); i++) {
                String mid = mftList.getString(i);
                if (!mid.isBlank()) data.mailForTomorrow.add(mid);
            }
        }

        // 怪物讨伐击杀计数
        if (tag.contains("MonsterKillCounts", 10)) { // 10 = TAG_Compound
            CompoundTag kills = tag.getCompound("MonsterKillCounts");
            for (String key : kills.getAllKeys()) {
                data.monsterKillCounts.put(key, kills.getInt(key));
            }
        }
        // 已领取的讨伐奖励
        if (tag.contains("ClaimedSlayerRewards", 9)) {
            ListTag claimed = tag.getList("ClaimedSlayerRewards", 8);
            for (int i = 0; i < claimed.size(); i++) {
                String key = claimed.getString(i);
                if (!key.isBlank()) data.claimedSlayerRewards.add(key);
            }
        }
        // 特殊物品去重
        if (tag.contains("SpecialItems", 9)) {
            ListTag specials = tag.getList("SpecialItems", 8);
            for (int i = 0; i < specials.size(); i++) {
                String id = specials.getString(i);
                if (!id.isBlank()) data.specialItems.add(id);
            }
        }

        // 工具升级
        // 装备
        data.equippedLeftRing = tag.contains("EquippedLeftRing") ? tag.getString("EquippedLeftRing") : "";
        data.equippedRightRing = tag.contains("EquippedRightRing") ? tag.getString("EquippedRightRing") : "";
        data.equippedBoots = tag.contains("EquippedBoots") ? tag.getString("EquippedBoots") : "";
        data.lastPhoenixReviveDay = tag.contains("LastPhoenixReviveDay") ? tag.getLong("LastPhoenixReviveDay") : -1;

        data.toolBeingUpgraded = tag.contains("ToolBeingUpgraded") ? tag.getString("ToolBeingUpgraded") : "";
        data.daysLeftForToolUpgrade = tag.contains("DaysLeftForToolUpgrade") ? tag.getInt("DaysLeftForToolUpgrade") : 0;
        data.toolUpgradeNotified = tag.getBoolean("ToolUpgradeNotified");

        // 巫师塔枢纽
        // 任务系统
        if (tag.contains("QuestManager", 10)) {
            data.questManager.load(tag.getCompound("QuestManager"));
        }

        data.wizardQuestComplete = tag.getBoolean("WizardQuestComplete");
        data.wizardFirstMet = tag.getBoolean("WizardFirstMet");
        data.starterToolsGiven = tag.getBoolean("StarterToolsGiven");
        data.bilibiliRewardClaimed = tag.getBoolean("BilibiliRewardClaimed");
        if (tag.contains("OverworldReturnX")) {
            data.overworldReturnPos = new BlockPos(
                tag.getInt("OverworldReturnX"),
                tag.getInt("OverworldReturnY"),
                tag.getInt("OverworldReturnZ")
            );
        }
        if (tag.contains("WizardSourceDim")) {
            String dimStr = tag.getString("WizardSourceDim");
            if (!dimStr.isBlank()) {
                @SuppressWarnings("null")
                var parsedKey = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dimStr));
                data.wizardSourceDimension = parsedKey;
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

        // 晕倒/死亡系统
        tag.putBoolean("PassedOutFromCombat", passedOutFromCombat);
        if (!itemsLostLastDeath.isEmpty()) {
            ListTag lostItemsTag = new ListTag();
            for (net.minecraft.world.item.ItemStack stack : itemsLostLastDeath) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putString("Id", net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());
                itemTag.putInt("Count", stack.getCount());
                lostItemsTag.add(itemTag);
            }
            tag.put("ItemsLostLastDeath", lostItemsTag);
        }
        
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

        // pendingNewLevels（白天获得的升级，睡觉时消费）
        ListTag newLevelsTag = new ListTag();
        for (SkillLevelUp levelUp : pendingNewLevels) {
            CompoundTag entry = new CompoundTag();
            entry.putInt("SkillId", levelUp.skill().getId());
            entry.putInt("Level", levelUp.newLevel());
            newLevelsTag.add(entry);
        }
        tag.put("PendingNewLevels", newLevelsTag);
        
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

        tag.putInt("TrashCansChecked", trashCansChecked);

        // 邮件标记
        if (!mailFlags.isEmpty()) {
            ListTag mailList = new ListTag();
            for (String flag : mailFlags) {
                mailList.add(StringTag.valueOf(flag));
            }
            tag.put("MailFlags", mailList);
        }

        // 邮箱队列
        if (!mailbox.isEmpty()) {
            ListTag mboxList = new ListTag();
            for (String mid : mailbox) {
                mboxList.add(StringTag.valueOf(mid));
            }
            tag.put("Mailbox", mboxList);
        }
        if (!mailForTomorrow.isEmpty()) {
            ListTag mftList = new ListTag();
            for (String mid : mailForTomorrow) {
                mftList.add(StringTag.valueOf(mid));
            }
            tag.put("MailForTomorrow", mftList);
        }

        // 怪物讨伐击杀计数
        if (!monsterKillCounts.isEmpty()) {
            CompoundTag kills = new CompoundTag();
            for (var entry : monsterKillCounts.entrySet()) {
                kills.putInt(entry.getKey(), entry.getValue());
            }
            tag.put("MonsterKillCounts", kills);
        }
        // 已领取的讨伐奖励
        if (!claimedSlayerRewards.isEmpty()) {
            ListTag claimed = new ListTag();
            for (String key : claimedSlayerRewards) {
                claimed.add(StringTag.valueOf(key));
            }
            tag.put("ClaimedSlayerRewards", claimed);
        }
        // 特殊物品去重
        if (!specialItems.isEmpty()) {
            ListTag specials = new ListTag();
            for (String id : specialItems) {
                specials.add(StringTag.valueOf(id));
            }
            tag.put("SpecialItems", specials);
        }

        // 装备
        if (!equippedLeftRing.isEmpty()) tag.putString("EquippedLeftRing", equippedLeftRing);
        if (!equippedRightRing.isEmpty()) tag.putString("EquippedRightRing", equippedRightRing);
        if (!equippedBoots.isEmpty()) tag.putString("EquippedBoots", equippedBoots);
        if (lastPhoenixReviveDay >= 0) tag.putLong("LastPhoenixReviveDay", lastPhoenixReviveDay);

        // 工具升级
        tag.putString("ToolBeingUpgraded", toolBeingUpgraded != null ? toolBeingUpgraded : "");
        tag.putInt("DaysLeftForToolUpgrade", daysLeftForToolUpgrade);
        tag.putBoolean("ToolUpgradeNotified", toolUpgradeNotified);

        // 任务系统
        tag.put("QuestManager", questManager.save());

        // 巫师塔枢纽
        tag.putBoolean("WizardQuestComplete", wizardQuestComplete);
        tag.putBoolean("WizardFirstMet", wizardFirstMet);
        tag.putBoolean("StarterToolsGiven", starterToolsGiven);
        tag.putBoolean("BilibiliRewardClaimed", bilibiliRewardClaimed);
        if (overworldReturnPos != null) {
            tag.putInt("OverworldReturnX", overworldReturnPos.getX());
            tag.putInt("OverworldReturnY", overworldReturnPos.getY());
            tag.putInt("OverworldReturnZ", overworldReturnPos.getZ());
        }
        if (wizardSourceDimension != null) {
            tag.putString("WizardSourceDim", wizardSourceDimension.location().toString());
        }
        
        return tag;
    }
    
    // ============ 经验和等级相关方法 ============
    
    /**
     * 添加经验值 — SDV parity: 等级立即生效（Farmer.gainExperience）
     * @return 是否升级
     */
    public boolean addExperience(SkillType skill, int amount) {
        if (amount <= 0) return false;

        int skillId = skill.getId();
        int oldLevel = skillLevels[skillId];

        experiencePoints[skillId] += amount;
        int newLevel = Math.min(10, calculateLevel(experiencePoints[skillId]));

        if (newLevel > oldLevel) {
            skillLevels[skillId] = newLevel;
            for (int lvl = oldLevel + 1; lvl <= newLevel; lvl++) {
                pendingNewLevels.add(new SkillLevelUp(skill, lvl));
                // 战斗升级立即加血上限（SDV parity）
                if (skill == SkillType.COMBAT && lvl != 5 && lvl != 10) {
                    maxHealth += 5;
                    health = Math.min(health + 5, maxHealth);
                }
            }
        }

        markDirty();
        return newLevel > oldLevel;
    }

    public int getLevelByExperience(SkillType skill) {
        return calculateLevel(experiencePoints[skill.getId()]);
    }

    /**
     * 消费白天积累的 pendingNewLevels（睡觉时调用）。
     * SDV parity: skillLevels 已在 addExperience 中实时更新，
     * 此方法只处理职业选择提示并返回列表供升级动画使用。
     */
    public List<SkillLevelUp> applyPendingSkillLevelUps() {
        List<SkillLevelUp> applied = new ArrayList<>(pendingNewLevels);

        for (SkillLevelUp levelUp : applied) {
            SkillType skill = levelUp.skill();
            int level = levelUp.newLevel();
            if (level == 5 && !hasLevel5Profession(skill) && !hasPendingProfessionChoice(skill, 5)) {
                pendingProfessionChoices.add(new ProfessionChoicePrompt(skill, 5));
            }
            if (level == 10 && hasLevel5Profession(skill) && !hasLevel10Profession(skill) && !hasPendingProfessionChoice(skill, 10)) {
                pendingProfessionChoices.add(new ProfessionChoicePrompt(skill, 10));
            }
        }

        pendingNewLevels.clear();
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

        // SDV 原版：能量可以扣到负数（≤ -15 时触发体力耗尽晕倒）。
        // exhausted 在能量 ≤ 0 时设置，用于次日恢复减半等逻辑。
        boolean enough = energy >= amount;
        energy = energy - amount;

        if (energy <= 0) {
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

    // ---- 晕倒/死亡系统 ----
    public boolean isPassedOutFromCombat() { return passedOutFromCombat; }
    public void setPassedOutFromCombat(boolean value) { passedOutFromCombat = value; markDirty(); }

    public List<net.minecraft.world.item.ItemStack> getItemsLostLastDeath() { return itemsLostLastDeath; }
    public void setItemsLostLastDeath(List<net.minecraft.world.item.ItemStack> items) {
        itemsLostLastDeath.clear();
        itemsLostLastDeath.addAll(items);
        markDirty();
    }
    public void clearItemsLostLastDeath() { itemsLostLastDeath.clear(); markDirty(); }
    
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

    public int getTrashCansChecked() { return trashCansChecked; }

    public void incrementTrashCansChecked() {
        trashCansChecked++;
        markDirty();
    }

    // ──── Mail Flags (SDV mailReceived parity) ────
    public Set<String> getMailFlags() { return Collections.unmodifiableSet(mailFlags); }
    public boolean hasMailFlag(String flag) { return mailFlags.contains(flag); }
    public void addMailFlag(String flag) { if (mailFlags.add(flag)) markDirty(); }
    public void removeMailFlag(String flag) { if (mailFlags.remove(flag)) markDirty(); }

    // ──── Mailbox Queue (SDV Farmer.mailbox parity) ────
    public List<String> getMailbox() { return Collections.unmodifiableList(mailbox); }
    public boolean hasMailInMailbox() { return !mailbox.isEmpty(); }
    /**
     * 从队列头部取出一封邮件（SDV Farmer.mailbox 的 pop 语义）。
     */
    @Nullable
    public String popMailFromMailbox() {
        if (mailbox.isEmpty()) return null;
        String id = mailbox.remove(0);
        markDirty();
        return id;
    }
    public void addToMailbox(String mailId) {
        if (!mailbox.contains(mailId)) {
            mailbox.add(mailId);
            markDirty();
        }
    }

    // ──── Mail For Tomorrow (SDV Farmer.mailForTomorrow parity) ────
    public List<String> getMailForTomorrow() { return Collections.unmodifiableList(mailForTomorrow); }
    public void addMailForTomorrow(String mailId) {
        if (!mailForTomorrow.contains(mailId)) {
            mailForTomorrow.add(mailId);
            markDirty();
        }
    }
    /**
     * 夜间结算时调用：把 mailForTomorrow 全部移入 mailbox。
     */
    public void deliverTomorrowMail() {
        if (mailForTomorrow.isEmpty()) return;
        for (String mid : mailForTomorrow) {
            if (!mailbox.contains(mid)) {
                mailbox.add(mid);
            }
        }
        mailForTomorrow.clear();
        markDirty();
    }

    // ──── Monster Slayer (Gil Goals) ────
    public int getMonsterKills(String goalKey) { return monsterKillCounts.getOrDefault(goalKey, 0); }
    public Map<String, Integer> getAllMonsterKills() { return Collections.unmodifiableMap(monsterKillCounts); }
    public void addMonsterKills(String goalKey, int count) {
        monsterKillCounts.merge(goalKey, count, Integer::sum);
        markDirty();
    }
    public boolean hasClaimedSlayerReward(String goalKey) { return claimedSlayerRewards.contains(goalKey); }
    public Set<String> getClaimedSlayerRewards() { return Collections.unmodifiableSet(claimedSlayerRewards); }
    public void claimSlayerReward(String goalKey) { if (claimedSlayerRewards.add(goalKey)) markDirty(); }

    // ──── Special Items (unique treasure dedup) ────
    public boolean hasSpecialItem(String itemId) { return specialItems.contains(itemId); }
    public void addSpecialItem(String itemId) { if (specialItems.add(itemId)) markDirty(); }

    // ──── Equipment (Rings + Boots) ────
    public String getEquippedLeftRing() { return equippedLeftRing; }
    public void setEquippedLeftRing(String id) { this.equippedLeftRing = id != null ? id : ""; markDirty(); }
    public String getEquippedRightRing() { return equippedRightRing; }
    public void setEquippedRightRing(String id) { this.equippedRightRing = id != null ? id : ""; markDirty(); }
    public String getEquippedBoots() { return equippedBoots; }
    public void setEquippedBoots(String id) { this.equippedBoots = id != null ? id : ""; markDirty(); }
    public long getLastPhoenixReviveDay() { return lastPhoenixReviveDay; }
    public void setLastPhoenixReviveDay(long day) { this.lastPhoenixReviveDay = day; markDirty(); }

    /**
     * 检查玩家是否装备了指定类型的戒指（左或右）
     */
    public boolean hasRingEquipped(String itemId) {
        return itemId.equals(equippedLeftRing) || itemId.equals(equippedRightRing);
    }

    /**
     * 获取所有装备的戒指ID列表
     */
    public java.util.List<String> getEquippedRingIds() {
        java.util.List<String> rings = new java.util.ArrayList<>(2);
        if (!equippedLeftRing.isEmpty()) rings.add(equippedLeftRing);
        if (!equippedRightRing.isEmpty()) rings.add(equippedRightRing);
        return rings;
    }

    // ──── Quest System ────
    public QuestManager getQuestManager() { return questManager; }

    // ──── Tool Upgrade (Blacksmith) ────
    public String getToolBeingUpgraded() { return toolBeingUpgraded; }
    public void setToolBeingUpgraded(String id) { this.toolBeingUpgraded = id != null ? id : ""; markDirty(); }
    public int getDaysLeftForToolUpgrade() { return daysLeftForToolUpgrade; }
    public void setDaysLeftForToolUpgrade(int days) { this.daysLeftForToolUpgrade = days; markDirty(); }
    public boolean isToolUpgradeNotified() { return toolUpgradeNotified; }
    public void setToolUpgradeNotified(boolean notified) { this.toolUpgradeNotified = notified; markDirty(); }

    // ──── Wizard Tower Hub (Cross-dimension) ────
    public boolean isWizardQuestComplete() { return wizardQuestComplete; }
    public void setWizardQuestComplete(boolean complete) { this.wizardQuestComplete = complete; markDirty(); }
    public boolean isWizardFirstMet() { return wizardFirstMet; }
    public void setWizardFirstMet(boolean met) { this.wizardFirstMet = met; markDirty(); }
    @Nullable
    public BlockPos getOverworldReturnPos() { return overworldReturnPos; }
    public void setOverworldReturnPos(@Nullable BlockPos pos) { this.overworldReturnPos = pos; markDirty(); }
    @Nullable
    public ResourceKey<Level> getWizardSourceDimension() { return wizardSourceDimension; }
    public void setWizardSourceDimension(@Nullable ResourceKey<Level> dim) { this.wizardSourceDimension = dim; markDirty(); }
    public boolean isStarterToolsGiven() { return starterToolsGiven; }
    public void setStarterToolsGiven(boolean given) { this.starterToolsGiven = given; markDirty(); }
    public boolean isBilibiliRewardClaimed() { return bilibiliRewardClaimed; }
    public void setBilibiliRewardClaimed(boolean claimed) { this.bilibiliRewardClaimed = claimed; markDirty(); }

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
