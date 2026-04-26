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

    /**
     * 维度时间隔离偏移量。
     * 星露谷的"虚拟 dayTime" = overworld.getDayTime() + dayTimeOffset。
     * 修改此偏移量代替直接修改 overworld 的 dayTime，从而不影响主世界。
     */
    private long dayTimeOffset = 0;
    
    // 事件标记（防止重复触发）
    private boolean event1800Triggered = false;  // 18:00 事件
    private boolean event2200Triggered = false;  // 22:00 事件
    private boolean event0000Triggered = false;  // 0:00 事件
    private boolean event0130Triggered = false;  // 1:30 事件

    /** 最近触发过 10 分钟 tick 的时间桶（=currentTime/10），-1 表示本日尚未触发过。 */
    private int lastTenMinuteBucket = -1;
    
    public StardewTimeManager() {
    }

    // ── dayTimeOffset API（维度时间隔离）──

    /**
     * 获取星露谷维度的"虚拟 dayTime"（不修改主世界 dayTime）。
     * virtualDayTime = overworld.getDayTime() + dayTimeOffset
     */
    public long getVirtualDayTime(ServerLevel anyLevel) {
        return anyLevel.getServer().overworld().getDayTime() + dayTimeOffset;
    }

    /** 获取当前偏移量。 */
    public long getDayTimeOffset() { return dayTimeOffset; }

    /** 直接设置偏移量（用于补偿主世界时间变化）。 */
    public void setDayTimeOffsetRaw(long offset) {
        dayTimeOffset = offset;
        setDirty();
    }

    /**
     * 将星露谷虚拟 dayTime 跳到目标值（修改 offset，不动 overworld）。
     */
    public void setVirtualDayTime(ServerLevel anyLevel, long targetDayTime) {
        dayTimeOffset = targetDayTime - anyLevel.getServer().overworld().getDayTime();
        setDirty();
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
        // 每 10 分钟 tick — SDV parity: GameLocation.performTenMinuteUpdate
        // 当前用于 ore-pan-point 生成（ccFishTank 奖励）。
        int currentBucket = currentTime / 10;
        if (currentBucket != lastTenMinuteBucket) {
            lastTenMinuteBucket = currentBucket;
            var server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                com.stardew.craft.communitycenter.reward.panning.OrePanPointManager
                    .performTenMinuteUpdate(server);
                com.stardew.craft.fishing.splash.FishSplashTicker
                    .performTenMinuteUpdate(server);
                com.stardew.craft.weather.LightningStrikeScheduler
                    .performTenMinuteUpdate(server);
            }
        }

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
                    // 先恢复公共区域被砍的杂草，再刷新季节外观
                    com.stardew.craft.farm.PublicAreaBlockTracker.get().restoreAll(stardewLevel);
                    com.stardew.craft.block.nature.WildWeedsBlock.refreshLoadedWeedsForSeason(stardewLevel, currentSeason);
                    com.stardew.craft.manager.JunimoGreenhouseRuneManager.get(stardewLevel).removeExpiredRunes(stardewLevel, currentSeason);
                }

                // 对齐 Stardew 的日结算语义：先确定“今天”的天气，再结算昨夜生长。
                com.stardew.craft.weather.WeatherManager.applyWeatherForNewDay(stardewLevel, currentDay);
                // 确保所有室内区块（含温室）在日结算期间已加载，
                // 否则 growDaily / waterDaily 会因 isLoaded(pos)==false 跳过温室作物。
                com.stardew.craft.interior.InteriorSubspaceManager.setInteriorChunksForced(stardewLevel, true, "daily_settlement");
                com.stardew.craft.farm.FarmDailyProcessHelper.beginDailyProcess(stardewLevel);
                try {
                com.stardew.craft.manager.CropGrowthManager.get(stardewLevel).growDaily(stardewLevel);
				com.stardew.craft.manager.TreeGrowthManager.get(stardewLevel).growDaily(stardewLevel);
                com.stardew.craft.manager.WildTreeSeedManager.get(stardewLevel).onNewDay(stardewLevel, absDay);
                com.stardew.craft.manager.SprinklerManager.get(stardewLevel).waterDaily(stardewLevel);
                com.stardew.craft.manager.PastureGrassGrowthManager.get(stardewLevel).growDaily(stardewLevel);
                com.stardew.craft.manager.AnimalGrowthManager.get(stardewLevel).growDaily(stardewLevel);
                com.stardew.craft.manager.ForageSpawnService.onNewDay(stardewLevel, currentSeason);
                com.stardew.craft.manager.ForageSpawnService.onNewDayForestFarms(stardewLevel, currentSeason);
                com.stardew.craft.manager.ArtifactSpotSpawnService.onNewDay(stardewLevel, currentSeason);
                com.stardew.craft.manager.QuarrySpawnService.onNewDay(stardewLevel, getCurrentYear());
                com.stardew.craft.manager.FarmCaveDailyService.onNewDay(stardewLevel);
                } finally {
                    // 日结算完成后立即释放室内区块，避免 784 区块永久 force-loaded
                    com.stardew.craft.interior.InteriorSubspaceManager.setInteriorChunksForced(stardewLevel, false, "daily_settlement_done");
                    com.stardew.craft.farm.FarmDailyProcessHelper.endDailyProcess();
                }
                
                // 多人农场：更新所有在线玩家的 lastOnlineDay
                {
                    com.stardew.craft.farm.FarmInstanceRegistry farmReg =
                            com.stardew.craft.farm.FarmInstanceRegistry.get();
                    for (net.minecraft.server.level.ServerPlayer sp : server.getPlayerList().getPlayers()) {
                        com.stardew.craft.farm.FarmInstance fi = farmReg.getFarm(sp.getUUID());
                        if (fi != null) {
                            fi.setLastOnlineDay(absDay);
                            fi.setLastOnlineSeason(currentSeason);
                        }
                    }
                    farmReg.setDirty();
                }

                // 预测明天的天气
                com.stardew.craft.weather.WeatherManager.updateWeatherForNewDay(
                    stardewLevel, currentDay, getSeasonName(), totalDaysPlayed
                );
            }

            // 次日恢复：生命回满；能量按 SV 原版 dayupdate 规则恢复（疲惫则减半）。
            // SDV parity: 结算前先将所有出货箱 buffer 里剩余的物品记录到出货追踪器
            com.stardew.craft.blockentity.ShippingBinBlockEntity.flushAllForOvernight();
            com.stardew.craft.farm.FarmInstanceRegistry overnightFarmRegistry =
                com.stardew.craft.farm.FarmInstanceRegistry.get();
            for (var player : server.getPlayerList().getPlayers()) {
                if (player.level().dimension() != ModDimensions.STARDEW_VALLEY
                    && player.level().dimension() != ModMiningDimensions.STARDEW_MINING) {
                    continue;
                }
                // 新玩家（尚未创建/加入农场）豁免一切夜间结算：不扣体力、不发结算画面、不投递邮件流程
                if (!overnightFarmRegistry.hasFarm(player.getUUID())) {
                    com.stardew.craft.network.overnight.OvernightSettlementTracker.consumePayload(player);
                    com.stardew.craft.player.PassOutService.consumePassOutResult(player.getUUID());
                    continue;
                }

                if (player.isCreative()) {
                    com.stardew.craft.player.PlayerStardewDataAPI.cureExhaustion(player);
                    com.stardew.craft.player.PlayerStardewDataAPI.restoreEnergy(player, com.stardew.craft.player.PlayerStardewDataAPI.getMaxEnergy(player));
                } else {
                    com.stardew.craft.player.PlayerStardewDataAPI.sleep(player, timeWentToSleepMinutes);
                    // 战斗死亡次日体力压到2
                    com.stardew.craft.player.PassOutService.applyCombatDeathEnergyPenalty(player);
                }
                com.stardew.craft.player.PlayerStardewDataAPI.setHealth(player, com.stardew.craft.player.PlayerStardewDataAPI.getMaxHealth(player));

                // SDV parity: Farmer.dayupdate → daysLeftForToolUpgrade--
                com.stardew.craft.shop.BlacksmithService.onNewDay(player);
                com.stardew.craft.shop.BlacksmithService.showToolUpgradeNotification(player);

                OvernightSettlementPayload settlementPayload = OvernightSettlementTracker.consumePayload(player);
                com.stardew.craft.player.PlayerStardewDataAPI.recordOvernightShippedItems(player, settlementPayload.shippedItems());
                List<PlayerStardewData.SkillLevelUp> appliedLevelUps = com.stardew.craft.player.PlayerStardewDataAPI.applyPendingSkillLevelUps(player);
                com.stardew.craft.player.PlayerStardewDataAPI.applySkillLevelRecipeUnlocks(player, appliedLevelUps);

                // SDV parity: getLevelPerk — 升级后回满体力和生命值
                if (!appliedLevelUps.isEmpty()) {
                    com.stardew.craft.player.PlayerStardewDataAPI.restoreEnergy(player, com.stardew.craft.player.PlayerStardewDataAPI.getMaxEnergy(player));
                    com.stardew.craft.player.PlayerStardewDataAPI.setHealth(player, com.stardew.craft.player.PlayerStardewDataAPI.getMaxHealth(player));
                }

                // 同步到客户端（HUD 依赖客户端缓存）
                com.stardew.craft.player.PlayerDataEventHandler.syncPlayerData(player, com.stardew.craft.player.PlayerDataManager.getPlayerData(player));

                // Quest: day started
                com.stardew.craft.quest.StardewQuestEvents.fireDayStarted(player, currentDay);

                List<OvernightSettlementPayload.LevelUpData> overnightLevelUps = new ArrayList<>(settlementPayload.levelUps());
                for (PlayerStardewData.SkillLevelUp levelUp : appliedLevelUps) {
                    overnightLevelUps.add(new OvernightSettlementPayload.LevelUpData(levelUp.skill().getId(), levelUp.newLevel()));
                }

                // 消费该玩家的 2AM 晕倒结果（如果有的话）合并进结算包
                com.stardew.craft.player.PassOutService.PassOutResult passOutResult =
                    com.stardew.craft.player.PassOutService.consumePassOutResult(player.getUUID());
                int passOutType = passOutResult != null ? passOutResult.type().getId() : -1;
                int passOutMoneyLost = passOutResult != null ? passOutResult.moneyLost() : 0;
                java.util.List<net.minecraft.world.item.ItemStack> passOutLostItems =
                    passOutResult != null ? passOutResult.lostItems() : java.util.List.of();

                OvernightSettlementPayload finalPayload = new OvernightSettlementPayload(
                    settlementPayload.shippedItems(),
                    List.copyOf(overnightLevelUps),
                    passOutType,
                    passOutMoneyLost,
                    passOutLostItems
                );

                // 始终发送结算包，即使没有出货/升级，以便客户端显示夜间结算过渡画面
                PacketDistributor.sendToPlayer(player, finalPayload);

                // 扫描并排队所有 wake_up 剧情，等客户端关闭结算画面后按序播放
                com.stardew.craft.cutscene.server.WakeUpEventScheduler.enqueueAtNightSettlement(player);
            }
        }

        // Reset per-player shop stock for the new day (SDV: SynchronizedShopStock parity)
        com.stardew.craft.shop.ShopStockTracker.resetForNewDay();

        // 邮件系统：将 mailForTomorrow 队列投递到 mailbox
        if (server != null) {
            com.stardew.craft.mail.MailService.deliverAllTomorrowMail(server);

            // SDV 日期触发邮件
            for (net.minecraft.server.level.ServerPlayer sp : server.getPlayerList().getPlayers()) {
                scheduleMailByDate(sp, currentSeason, currentDay);
            }
        }

        setDirty();
    }

    /**
     * SDV 日期触发邮件 — 根据当前季节/日期安排邮件。
     * 模拟 SDV Game1._newDayAfterFade：优先匹配 season_day_year，再匹配 season_day。
     * 同时处理里程碑邮件（父母信等按天数触发的邮件）。
     */
    private void scheduleMailByDate(net.minecraft.server.level.ServerPlayer player, int season, int day) {
        // 初始化玩家的首次加入天数（用于里程碑/父母信件的相对天数计算）
        com.stardew.craft.player.PlayerStardewData pData =
                com.stardew.craft.player.PlayerDataManager.getPlayerData(player);
        int globalDays = (currentYear - 1) * (28 * 4) + currentSeason * 28 + currentDay;
        if (pData.getFirstJoinDay() < 0) {
            pData.setFirstJoinDay(globalDays);
        }

        // 玩家个人年份（用于年份限定邮件，如 spring_2_1 = 春2日个人第1年）
        int personalDays = Math.max(0, globalDays - pData.getFirstJoinDay());
        int personalYear = personalDays / (28 * 4) + 1;

        // 个人日历：从加入当天起算的"季节/日"。这样 _<year> 邮件总是按
        // "玩家进入服务器后的第 N 个游戏日"触发，避免玩家如果在春5日才进服
        // 就永远收不到 spring_4_1 这类信。
        int personalSeason = (personalDays / 28) % 4;
        int personalDay = (personalDays % 28) + 1;
        String personalSeasonName = switch (personalSeason) {
            case 0 -> "spring";
            case 1 -> "summer";
            case 2 -> "fall";
            case 3 -> "winter";
            default -> "";
        };

        String seasonName = switch (season) {
            case 0 -> "spring";
            case 1 -> "summer";
            case 2 -> "fall";
            case 3 -> "winter";
            default -> "";
        };

        // SDV parity: season_day_year 用个人日历触发（spring_4_1 = 进服后第 4 个游戏日早晨）
        String keyWithYear = personalSeasonName + "_" + personalDay + "_" + personalYear;
        if (com.stardew.craft.mail.MailRegistry.contains(keyWithYear)) {
            com.stardew.craft.mail.MailService.addMail(player, keyWithYear);
        }

        // season_day（如 spring_12 = 春12日节日通知）仍按服务器全局日历触发
        String keyNoYear = seasonName + "_" + day;
        if (com.stardew.craft.mail.MailRegistry.contains(keyNoYear)) {
            com.stardew.craft.mail.MailService.addMail(player, keyNoYear);
        }

        // 父母信件 — 按玩家个人天数里程碑触发
        scheduleParentMail(player, personalDays);
    }

    /**
     * 父母来信 — 按玩家个人天数里程碑触发。
     * mom1/dad1: 15天, mom2/dad2: 50天, mom3/dad3: 80天, mom4/dad4: 120天
     */
    private void scheduleParentMail(net.minecraft.server.level.ServerPlayer player, int personalDays) {
        String[][] parentMails = {
            {"mom1", "dad1"},  // 15 days
            {"mom2", "dad2"},  // 50 days
            {"mom3", "dad3"},  // 80 days
            {"mom4", "dad4"},  // 120 days
        };
        int[] dayThresholds = {15, 50, 80, 120};
        for (int i = 0; i < parentMails.length; i++) {
            if (personalDays >= dayThresholds[i]) {
                // SDV: randomly pick mom or dad
                String mailId = parentMails[i][new java.util.Random(player.getUUID().hashCode() + i).nextInt(2)];
                com.stardew.craft.mail.MailService.addMail(player, mailId);
            }
        }

        // 里程碑提示邮件 — NPC 在特定天数给出建议
        scheduleMilestoneMail(player, personalDays);
    }

    /**
     * 里程碑邮件 — NPC 在特定天数自动发送提示/建议信。
     * 使用玩家个人天数（从首次加入起算），而非服务器全局天数。
     */
    private void scheduleMilestoneMail(net.minecraft.server.level.ServerPlayer player, int personalDays) {
        // 罗宾：建筑建议
        if (personalDays >= 7) com.stardew.craft.mail.MailService.addMail(player, "robinWell");
        if (personalDays >= 20) com.stardew.craft.mail.MailService.addMail(player, "robinCoop");
        if (personalDays >= 40) com.stardew.craft.mail.MailService.addMail(player, "robinBarn");
        // 德米特里厄斯：山洞
        if (personalDays >= 10) com.stardew.craft.mail.MailService.addMail(player, "demetriusCave");
        // 皮埃尔：一般提示
        if (personalDays >= 14) com.stardew.craft.mail.MailService.addMail(player, "pierreGeneral");
        // 莱纳斯
        if (personalDays >= 12) com.stardew.craft.mail.MailService.addMail(player, "linusTip");
        if (personalDays >= 30) com.stardew.craft.mail.MailService.addMail(player, "linusTrash");
        // 玛妮：动物
        if (personalDays >= 25) com.stardew.craft.mail.MailService.addMail(player, "marnieAnimal");
        // 格斯：烹饪
        if (personalDays >= 35) com.stardew.craft.mail.MailService.addMail(player, "gusRecipe");
        // 艾利奥特
        if (personalDays >= 45) com.stardew.craft.mail.MailService.addMail(player, "elliottBook");
    }

    /**
     * 重置事件标记
     */
    private void resetEventFlags() {
        event1800Triggered = false;
        event2200Triggered = false;
        event0000Triggered = false;
        event0130Triggered = false;
        lastTenMinuteBucket = -1;
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

    /** 绝对游戏日（year/season/day 合成，用于跨年单调递增的天数键）。 */
    public int getAbsoluteDay() {
        return (currentYear - 1) * (28 * 4) + currentSeason * 28 + currentDay;
    }
    
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
        tag.putLong("dayTimeOffset", dayTimeOffset);
        
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
        
        data.dayTimeOffset = tag.getLong("dayTimeOffset");
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
