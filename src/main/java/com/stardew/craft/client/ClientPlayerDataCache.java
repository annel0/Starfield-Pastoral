package com.stardew.craft.client;

import com.stardew.craft.player.SkillType;
import com.stardew.craft.player.ProfessionType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端玩家数据缓存
 * 用于在HUD等客户端界面显示同步的服务端数据
 */
public class ClientPlayerDataCache {
    
    private static int health = 100;
    private static int maxHealth = 100;
    private static float energy = 270.0f;
    private static int maxEnergy = 270;
    private static int money = 500;  // 默认500金币
    private static int[] experience = new int[5];
    private static int[] skillLevels = new int[5];
    private static List<String> professions = new ArrayList<>();
    private static final java.util.Set<String> unlockedRecipes = new java.util.HashSet<>();
    private static final java.util.Map<String, Integer> recipeCraftCounts = new java.util.HashMap<>();
    private static final java.util.Set<String> mailFlags = new java.util.HashSet<>();

    // 临时Buff（客户端显示/计算用）
    private static int tempFishingLevelBonus = 0;
    private static int tempLuckBonus = 0;
    private static int tempMaxEnergyBonus = 0;
    private static int tempFarmingLevelBonus = 0;
    private static int tempForagingLevelBonus = 0;
    private static int tempMiningLevelBonus = 0;

    // 农场名
    private static String farmName = "";

    // 装备槽
    private static String equippedLeftRing = "";
    private static String equippedRightRing = "";
    private static String equippedBoots = "";

    /**
     * True after the server has sent at least one PlayerDataSyncPacket for this session.
     * Gates logic that would otherwise false-negative on an empty cache (e.g. cutscene
     * {@code not_mail}/{@code not_flag} preconditions incorrectly passing before sync).
     */
    private static volatile boolean syncedFromServer = false;

    public static boolean isSynced() { return syncedFromServer; }

    /**
     * 从NBT更新缓存
     */
    public static void updateFromNBT(CompoundTag nbt) {
        health = nbt.getInt("Health");
        maxHealth = nbt.getInt("MaxHealth");
        energy = nbt.getFloat("Energy");
        tempMaxEnergyBonus = nbt.contains("TempMaxEnergyBonus") ? nbt.getInt("TempMaxEnergyBonus") : 0;
        maxEnergy = nbt.getInt("MaxEnergy") + tempMaxEnergyBonus;
        money = nbt.getInt("Money");

        tempFishingLevelBonus = nbt.contains("TempFishingLevelBonus") ? nbt.getInt("TempFishingLevelBonus") : 0;
        tempLuckBonus = nbt.contains("TempLuckBonus") ? nbt.getInt("TempLuckBonus") : 0;
        tempFarmingLevelBonus = nbt.contains("TempFarmingLevelBonus") ? nbt.getInt("TempFarmingLevelBonus") : 0;
        tempForagingLevelBonus = nbt.contains("TempForagingLevelBonus") ? nbt.getInt("TempForagingLevelBonus") : 0;
        tempMiningLevelBonus = nbt.contains("TempMiningLevelBonus") ? nbt.getInt("TempMiningLevelBonus") : 0;
        
        // 同步更新HUD的金币显示
        com.stardew.craft.client.hud.StardewTimeHud.updateClientMoney(money);
        
        // 读取经验数组
        int[] expArray = nbt.getIntArray("Experience");
        if (expArray.length == 5) {
            experience = expArray;
        }

        int[] levelArray = nbt.getIntArray("SkillLevels");
        if (levelArray.length == 5) {
            skillLevels = levelArray;
        } else if (expArray.length == 5) {
            int[] fallback = new int[5];
            for (int i = 0; i < fallback.length; i++) {
                fallback[i] = calculateLevel(expArray[i]);
            }
            skillLevels = fallback;
        }
        
        // 读取职业列表（服务端标准格式为 int[]，同时兼容旧 string-list）
        professions.clear();
        int[] professionIds = nbt.getIntArray("Professions");
        if (professionIds.length > 0) {
            for (int professionId : professionIds) {
                ProfessionType profession = ProfessionType.fromId(professionId);
                if (profession != null) {
                    professions.add(profession.getName());
                }
            }
        } else {
            ListTag profList = nbt.getList("Professions", Tag.TAG_STRING);
            for (int i = 0; i < profList.size(); i++) {
                professions.add(profList.getString(i));
            }
        }

        // 读取已解锁的配方
        unlockedRecipes.clear();
        if (nbt.contains("UnlockedRecipes")) {
            ListTag recipesList = nbt.getList("UnlockedRecipes", Tag.TAG_STRING);
            for (int i = 0; i < recipesList.size(); i++) {
                unlockedRecipes.add(recipesList.getString(i));
            }
        }

        // 读取邮件标记（含 CC 献祭进度：ccPantry/ccCraftsRoom/...）
        mailFlags.clear();
        if (nbt.contains("MailFlags")) {
            ListTag flagList = nbt.getList("MailFlags", Tag.TAG_STRING);
            for (int i = 0; i < flagList.size(); i++) {
                String flag = flagList.getString(i);
                if (!flag.isBlank()) mailFlags.add(flag);
            }
        }

        // 农场名
        if (nbt.contains("FarmName")) {
            farmName = nbt.getString("FarmName");
        }

        recipeCraftCounts.clear();
        if (nbt.contains("RecipeCraftCounts")) {
            ListTag countList = nbt.getList("RecipeCraftCounts", Tag.TAG_COMPOUND);
            for (int i = 0; i < countList.size(); i++) {
                CompoundTag entry = countList.getCompound(i);
                if (!entry.contains("Recipe", Tag.TAG_STRING)) {
                    continue;
                }
                String recipeId = entry.getString("Recipe");
                int count = Math.max(0, entry.getInt("Count"));
                if (!recipeId.isBlank() && count > 0) {
                    recipeCraftCounts.put(recipeId, count);
                }
            }
        }

        syncedFromServer = true;
    }

    // Getters
    public static int getHealth() {
        return health;
    }
    
    public static int getMaxHealth() {
        return maxHealth;
    }
    
    public static float getEnergy() {
        return energy;
    }
    
    public static int getMaxEnergy() {
        return maxEnergy;
    }
    
    public static int getMoney() {
        return money;
    }

    public static String getFarmName() {
        return farmName;
    }

    /** Update cached money on client (e.g. immediate feedback for geode cost deduction). */
    public static void setMoney(int value) {
        money = value;
    }
    
    public static int[] getExperience() {
        return experience.clone();
    }
    
    public static int getExperience(SkillType skill) {
        return experience[skill.ordinal()];
    }
    
    public static int getSkillLevel(SkillType skill) {
        int level = skillLevels[skill.ordinal()];
        if (skill == SkillType.FISHING) level += tempFishingLevelBonus;
        if (skill == SkillType.FARMING) level += tempFarmingLevelBonus;
        if (skill == SkillType.FORAGING) level += tempForagingLevelBonus;
        if (skill == SkillType.MINING) level += tempMiningLevelBonus;
        return Math.max(0, level);
    }

    public static int getTempLuckBonus() {
        return Math.max(0, tempLuckBonus);
    }
    
    public static List<String> getProfessions() {
        return new ArrayList<>(professions);
    }

    public static boolean hasProfession(ProfessionType profession) {
        if (profession == null) {
            return false;
        }

        String expectedName = profession.getName();
        for (String name : professions) {
            if (expectedName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
    
    public static java.util.Set<String> getUnlockedRecipes() {
        return new java.util.HashSet<>(unlockedRecipes);
    }

    public static boolean hasRecipe(String recipeId) {
        return unlockedRecipes.contains(recipeId);
    }

    public static int getRecipeCraftCount(String recipeId) {
        if (recipeId == null || recipeId.isBlank()) {
            return 0;
        }
        return Math.max(0, recipeCraftCounts.getOrDefault(recipeId, 0));
    }

    public static java.util.Map<String, Integer> getRecipeCraftCounts() {
        return new java.util.HashMap<>(recipeCraftCounts);
    }

    public static boolean hasMailFlag(String flag) {
        return flag != null && mailFlags.contains(flag);
    }

    public static java.util.Set<String> getMailFlags() {
        return new java.util.HashSet<>(mailFlags);
    }

    // Equipment getters/setters
    public static String getEquippedLeftRing() { return equippedLeftRing; }
    public static String getEquippedRightRing() { return equippedRightRing; }
    public static String getEquippedBoots() { return equippedBoots; }
    public static void setEquippedLeftRing(String id) { equippedLeftRing = id == null ? "" : id; }
    public static void setEquippedRightRing(String id) { equippedRightRing = id == null ? "" : id; }
    public static void setEquippedBoots(String id) { equippedBoots = id == null ? "" : id; }

    /**
     * 重置所有缓存（用于退出世界时）
     */
    public static void reset() {
        health = 100;
        maxHealth = 100;
        energy = 270.0f;
        maxEnergy = 270;
        money = 0;
        experience = new int[5];
        skillLevels = new int[5];
        professions.clear();
        unlockedRecipes.clear();
        recipeCraftCounts.clear();
        mailFlags.clear();
        tempFishingLevelBonus = 0;
        tempLuckBonus = 0;
        tempMaxEnergyBonus = 0;
        tempFarmingLevelBonus = 0;
        tempForagingLevelBonus = 0;
        tempMiningLevelBonus = 0;
        equippedLeftRing = "";
        equippedRightRing = "";
        equippedBoots = "";
        syncedFromServer = false;
        NpcFriendshipClientCache.reset();
    }
    
    /**
     * 根据经验值计算等级
     */
    private static int calculateLevel(int exp) {
        int[] expTable = {
            100, 380, 770, 1300, 2150,
            3300, 4800, 6900, 10000, 15000
        };
        
        for (int level = 0; level < 10; level++) {
            if (exp < expTable[level]) {
                return level;
            }
        }
        return 10;
    }
}
