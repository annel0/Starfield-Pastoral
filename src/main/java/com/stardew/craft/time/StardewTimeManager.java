package com.stardew.craft.time;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.network.overnight.OvernightSettlementPayload;
import com.stardew.craft.network.overnight.OvernightSettlementTracker;
import com.stardew.craft.player.PlayerStardewData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * 星露谷时间管理系统
 * 
 * 新设计：时间直接从MC的dayTime同步，不再自己计数tick
 * 
 * 时间规则：
 * - 星露谷一天 = 6:00 AM 到 2:00 AM (次日)
 * - 使用MC原版昼夜循环（20分钟一天）
 * - MC dayTime 0 = 6:00 AM, dayTime 20000 = 2:00 AM
 * - 当到达2:00 AM时，跳到下一天（dayTime重置为0）
 */
public class StardewTimeManager extends SavedData {
    
    private static final String DATA_NAME = "stardew_time_data";
    
    // 时间常量
    public static final int MORNING_START = 360;     // 早上6:00 (6 * 60 = 360)
    public static final int MIDNIGHT = 1440;         // 0:00 (24:00)
    public static final int PASS_OUT_TIME = 1560;    // 凌晨2:00 (26:00)
    public static final int MINUTES_PER_HOUR = 60;
    public static final int MINUTES_PER_DAY = 1440;
    
    // 时间状态
    private int currentTime = MORNING_START;  // 当前时间（分钟），从MC dayTime同步
    private int currentDay = 1;                // 当前日期
    private int currentSeason = 0;             // 当前季节 (0=春, 1=夏, 2=秋, 3=冬)
    private int currentYear = 1;               // 当前年份
    
    // 事件标记（防止重复触发）
    private boolean event1800Triggered = false;  // 18:00 事件
    private boolean event2200Triggered = false;  // 22:00 事件
    private boolean event0000Triggered = false;  // 0:00 事件
    private boolean event0130Triggered = false;  // 1:30 事件
    
    public StardewTimeManager() {
    }
    
    /**
     * 从MC时间设置当前星露谷时间（由DimensionEventHandler调用）
     * @param stardewMinutes 星露谷分钟（360 = 6:00 AM）
     */
    public void setCurrentTimeFromMC(int stardewMinutes) {
        // 只有时间变化时才更新
        if (stardewMinutes != currentTime) {
            currentTime = stardewMinutes;
            
            // 检查关键时间点
            checkTimeEvents();
            
            setDirty();
        }
    }
    
    /**
     * 检查并触发时间事件
     */
    private void checkTimeEvents() {
        // 18:00 (1080分钟)
        if (currentTime >= 1080 && !event1800Triggered) {
            event1800Triggered = true;
            // 这里可以触发动物回家等事件
        }
        
        // 22:00 (1320分钟)
        if (currentTime >= 1320 && !event2200Triggered) {
            event2200Triggered = true;
        }
        
        // 0:00 (1440分钟)
        if (currentTime >= 1440 && !event0000Triggered) {
            event0000Triggered = true;
        }
        
        // 1:30 (1530分钟)
        if (currentTime >= 1530 && !event0130Triggered) {
            event0130Triggered = true;
        }
    }
    
    /**
     * 进入下一天
     */
    public void advanceDay() {
        advanceDayWithSleepTime(currentTime);
    }

    /**
     * 进入下一天（可指定结算时的入睡时间）
     */
    public void advanceDayWithSleepTime(int sleepMinute) {
        int timeWentToSleepMinutes = sleepMinute;
        boolean seasonChanged = false;

        currentDay++;
        
        // 检查是否需要换季（每季28天）
        if (currentDay > 28) {
            currentDay = 1;
            currentSeason++;
            seasonChanged = true;
            
            // 检查是否需要换年
            if (currentSeason > 3) {
                currentSeason = 0;
                currentYear++;
            }
        }
        
        // 重置时间为早上6:00
        currentTime = MORNING_START;
        
        // 重置事件标记
        resetEventFlags();
        
        StardewCraft.LOGGER.info("New day: Year {} - {} Day {}", 
            currentYear, getSeasonName(), currentDay);
        
        // 触发每日作物生长
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            int absDay = (currentYear - 1) * (28 * 4) + currentSeason * 28 + currentDay;
            int totalDaysPlayed = absDay;
            
            // 需要在星露谷维度触发，而不是主世界
            @SuppressWarnings("null")
            ServerLevel stardewLevel = server.getLevel(ModDimensions.STARDEW_VALLEY);
            if (stardewLevel != null) {
                if (seasonChanged) {
                    com.stardew.craft.block.nature.WildWeedsBlock.refreshLoadedWeedsForSeason(stardewLevel, currentSeason);
                }

                // 对齐 Stardew 的日结算语义：先确定“今天”的天气，再结算昨夜生长。
                com.stardew.craft.weather.WeatherManager.applyWeatherForNewDay(stardewLevel, currentDay);

                com.stardew.craft.manager.CropGrowthManager.get(stardewLevel).growDaily(stardewLevel);
				com.stardew.craft.manager.TreeGrowthManager.get(stardewLevel).growDaily(stardewLevel);
                com.stardew.craft.manager.WildTreeSeedManager.get(stardewLevel).onNewDay(stardewLevel, absDay);
                com.stardew.craft.manager.SprinklerManager.get(stardewLevel).waterDaily(stardewLevel);
                com.stardew.craft.manager.PastureGrassGrowthManager.get(stardewLevel).growDaily(stardewLevel);
                com.stardew.craft.manager.AnimalGrowthManager.get(stardewLevel).growDaily(stardewLevel);
                
                // 预测明天的天气
                com.stardew.craft.weather.WeatherManager.updateWeatherForNewDay(
                    stardewLevel, currentDay, getSeasonName(), totalDaysPlayed
                );
            }

            // 次日恢复：生命回满；能量按 SV 原版 dayupdate 规则恢复（疲惫则减半）。
            // 目前没有“上床睡觉”系统，因此统一以触发 advanceDay 时的时间作为 timeWentToSleep。
            for (var player : server.getPlayerList().getPlayers()) {
                if (player.level().dimension() != ModDimensions.STARDEW_VALLEY
                    && player.level().dimension() != ModMiningDimensions.STARDEW_MINING) {
                    continue;
                }

                if (player.isCreative()) {
                    com.stardew.craft.player.PlayerStardewDataAPI.cureExhaustion(player);
                    com.stardew.craft.player.PlayerStardewDataAPI.restoreEnergy(player, com.stardew.craft.player.PlayerStardewDataAPI.getMaxEnergy(player));
                } else {
                    com.stardew.craft.player.PlayerStardewDataAPI.sleep(player, timeWentToSleepMinutes);
                }
                com.stardew.craft.player.PlayerStardewDataAPI.setHealth(player, com.stardew.craft.player.PlayerStardewDataAPI.getMaxHealth(player));

                OvernightSettlementPayload settlementPayload = OvernightSettlementTracker.consumePayload(player);
                com.stardew.craft.player.PlayerStardewDataAPI.recordOvernightShippedItems(player, settlementPayload.shippedItems());
                List<PlayerStardewData.SkillLevelUp> appliedLevelUps = com.stardew.craft.player.PlayerStardewDataAPI.applyPendingSkillLevelUps(player);
                com.stardew.craft.player.PlayerStardewDataAPI.applySkillLevelRecipeUnlocks(player, appliedLevelUps);

                // 同步到客户端（HUD 依赖客户端缓存）
                com.stardew.craft.player.PlayerDataEventHandler.syncPlayerData(player, com.stardew.craft.player.PlayerDataManager.getPlayerData(player));

                List<OvernightSettlementPayload.LevelUpData> overnightLevelUps = new ArrayList<>(settlementPayload.levelUps());
                for (PlayerStardewData.SkillLevelUp levelUp : appliedLevelUps) {
                    overnightLevelUps.add(new OvernightSettlementPayload.LevelUpData(levelUp.skill().getId(), levelUp.newLevel()));
                }

                OvernightSettlementPayload finalPayload = new OvernightSettlementPayload(
                    settlementPayload.shippedItems(),
                    List.copyOf(overnightLevelUps)
                );

                if (!finalPayload.shippedItems().isEmpty() || !finalPayload.levelUps().isEmpty()) {
                    PacketDistributor.sendToPlayer(player, finalPayload);
                }
            }
        }

        // Reset per-player shop stock for the new day (SDV: SynchronizedShopStock parity)
        com.stardew.craft.shop.ShopStockTracker.resetForNewDay();

        setDirty();
    }
    
    /**
     * 重置事件标记
     */
    private void resetEventFlags() {
        event1800Triggered = false;
        event2200Triggered = false;
        event0000Triggered = false;
        event0130Triggered = false;
    }
    
    /**
     * 获取当前小时（0-25）
     */
    public int getHour() {
        return currentTime / MINUTES_PER_HOUR;
    }
    
    /**
     * 获取当前分钟（0-59）
     */
    public int getMinute() {
        return currentTime % MINUTES_PER_HOUR;
    }

    /**
     * 获取格式化的时间字符串（例如："14:30"）
     */
    public String getFormattedTime() {
        int hour = getHour();
        int minute = getMinute();
        
        // 将24+小时转换回0-23
        if (hour >= 24) {
            hour -= 24;
        }
        
        return String.format("%02d:%02d", hour, minute);
    }
    
    /**
     * 获取12小时制时间字符串（例如："2:30 PM"）
     */
    public String getFormattedTime12Hour() {
        int hour = getHour();
        int minute = getMinute();
        
        if (hour >= 24) {
            hour -= 24;
        }
        
        String period = hour >= 12 ? "PM" : "AM";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;
        
        return String.format("%d:%02d %s", displayHour, minute, period);
    }
    
    /**
     * 获取季节名称
     */
    public String getSeasonName() {
        return switch (currentSeason) {
            case 0 -> "Spring";
            case 1 -> "Summer";
            case 2 -> "Fall";
            case 3 -> "Winter";
            default -> "Unknown";
        };
    }
    
    // Getters
    public int getCurrentTime() { return currentTime; }
    public int getCurrentDay() { return currentDay; }
    public int getCurrentSeason() { return currentSeason; }
    public int getCurrentYear() { return currentYear; }
    
    // Setters (用于调试或特殊情况)
    public void setCurrentTime(int time) { 
        this.currentTime = time; 
        setDirty(); 
    }
    
    public void setCurrentDay(int day) { 
        this.currentDay = day; 
        setDirty(); 
    }
    
    public void setCurrentSeason(int season) { 
        this.currentSeason = season; 
        setDirty(); 
    }
    
    public void setCurrentYear(int year) { 
        this.currentYear = year; 
        setDirty(); 
    }
    
    // SavedData 实现
    @Override
    public @javax.annotation.Nonnull CompoundTag save(@javax.annotation.Nonnull CompoundTag tag, @javax.annotation.Nonnull net.minecraft.core.HolderLookup.Provider provider) {
        tag.putInt("currentTime", currentTime);
        tag.putInt("currentDay", currentDay);
        tag.putInt("currentSeason", currentSeason);
        tag.putInt("currentYear", currentYear);
        
        tag.putBoolean("event1800", event1800Triggered);
        tag.putBoolean("event2200", event2200Triggered);
        tag.putBoolean("event0000", event0000Triggered);
        tag.putBoolean("event0130", event0130Triggered);
        
        return tag;
    }
    
    public static StardewTimeManager load(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        StardewTimeManager data = new StardewTimeManager();
        
        // 使用默认值，避免新存档时间从0开始
        data.currentTime = tag.contains("currentTime") ? tag.getInt("currentTime") : MORNING_START;
        data.currentDay = tag.contains("currentDay") ? tag.getInt("currentDay") : 1;
        data.currentSeason = tag.contains("currentSeason") ? tag.getInt("currentSeason") : 0;
        data.currentYear = tag.contains("currentYear") ? tag.getInt("currentYear") : 1;
        
        data.event1800Triggered = tag.getBoolean("event1800");
        data.event2200Triggered = tag.getBoolean("event2200");
        data.event0000Triggered = tag.getBoolean("event0000");
        data.event0130Triggered = tag.getBoolean("event0130");
        
        StardewCraft.LOGGER.info("[STARDEW TIME] Loaded SavedData: time={}, day={}, season={}, year={}", 
            data.currentTime, data.currentDay, data.currentSeason, data.currentYear);
        
        return data;
    }
    
    /**
     * 获取或创建时间管理器实例
     */
    @SuppressWarnings("null")
    public static StardewTimeManager get() {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return new StardewTimeManager(); // 客户端返回临时实例
        }
        
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(
            new SavedData.Factory<StardewTimeManager>(
                StardewTimeManager::new,
                StardewTimeManager::load,
                null  // DataFixTypes
            ),
            DATA_NAME
        );
    }
}
