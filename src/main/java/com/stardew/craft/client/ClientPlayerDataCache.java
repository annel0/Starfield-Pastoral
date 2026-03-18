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

    // 临时Buff（客户端显示/计算用）
    private static int tempFishingLevelBonus = 0;
    private static int tempLuckBonus = 0;
    private static int tempMaxEnergyBonus = 0;
    private static int tempFarmingLevelBonus = 0;
    private static int tempForagingLevelBonus = 0;
    private static int tempMiningLevelBonus = 0;
    
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
        tempFishingLevelBonus = 0;
        tempLuckBonus = 0;
        tempMaxEnergyBonus = 0;
        tempFarmingLevelBonus = 0;
        tempForagingLevelBonus = 0;
        tempMiningLevelBonus = 0;
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
