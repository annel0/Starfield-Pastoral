package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.dimension.StardewValleyMapBootstrap;
import com.stardew.craft.dimension.StardewValleyPrebuiltRegionInstaller;
import com.stardew.craft.network.TimeSyncPacket;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;

/**
 * 维度事件处理器 - 使用MC原版昼夜循环
 * 
 * 新时间系统设计：
 * - 开启原版doDaylightCycle，让MC自然推进dayTime
 * - 从MC dayTime计算星露谷时间（分钟）
 * - dayTime >= 20000 (2:00 AM) 时强制晕倒并跳到下一天
 * 
 * MC dayTime 映射：
 * - dayTime 0 = 6:00 AM（日出）
 * - dayTime 6000 = 12:00 PM（正午）
 * - dayTime 12000 = 6:00 PM（日落）
 * - dayTime 18000 = 12:00 AM（午夜）
 * - dayTime 20000 = 2:00 AM（强制晕倒，一天结束）
 * 
 * 转换公式：星露谷分钟 = (dayTime / 1000.0 + 6) * 60
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public class DimensionEventHandler {

    // ── 深夜时间点常量（MC dayTime）──
    // 对标 SDV: case 2400 (midnight), case 2500 (1AM), case 2600 (2AM), case 2800 (2:40AM)
    private static final long MIDNIGHT_TIME = 18000;       // 0:00 AM
    private static final long ONE_AM_TIME = 19000;         // 1:00 AM
    private static final long DAY_END_TIME = 20000;        // 2:00 AM — 强制晕倒
    @SuppressWarnings("unused")
    private static final long DAY_HARD_END_TIME = 22000;   // 2:40 AM — 强制关菜单安全网
    
    // 防止重复触发新的一天
    private static boolean dayAdvancing = false;
    // 深夜警告标记（每天重置一次）
    private static boolean midnightWarned = false;   // 0:00 警告
    private static boolean oneAMWarned = false;      // 1:00 警告
    private static boolean twoAMWarned = false;      // 2:00 警告
    private static int lastAnimalTenMinuteDayKey = Integer.MIN_VALUE;
    private static int lastAnimalTenMinuteSlot = Integer.MIN_VALUE;

    @SuppressWarnings("null")
    private static void advanceToNextMorning(ServerLevel sourceLevel, int sleepMinute, String reason) {
        if (dayAdvancing) {
            return;
        }
        dayAdvancing = true;
        try {
            var server = sourceLevel.getServer();

            // === 关键：先推进 dayTime，防止 advanceDayWithSleepTime 抛异常时
            //     dayTime 仍 >= 20000 导致 2AM 检查每 tick 重复触发 ===
            long currentDay = sourceLevel.getDayTime() / 24000;
            long newDayTime = (currentDay + 1) * 24000;

            for (ServerLevel level : server.getAllLevels()) {
                level.setDayTime(newDayTime);
            }

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerLevel playerLevel = player.serverLevel();
                player.connection.send(new ClientboundSetTimePacket(
                    playerLevel.getGameTime(),
                    newDayTime,
                    playerLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
                ));
            }

            // 日结算逻辑（作物生长、天气、玩家恢复等）
            StardewTimeManager timeManager = StardewTimeManager.get();
            try {
                timeManager.advanceDayWithSleepTime(sleepMinute);
            } catch (Exception e) {
                StardewCraft.LOGGER.error("Error during advanceDayWithSleepTime (day still advanced to prevent freeze)", e);
            }

            PacketDistributor.sendToAllPlayers(TimeSyncPacket.fromTimeManager(timeManager));
            lastAnimalTenMinuteDayKey = Integer.MIN_VALUE;
            lastAnimalTenMinuteSlot = Integer.MIN_VALUE;
            // 重置深夜警告标记（新的一天）
            midnightWarned = false;
            oneAMWarned = false;
            twoAMWarned = false;
            StardewCraft.LOGGER.info("Stardew day advanced to next morning by {} (sleepMinute={})", reason, sleepMinute);
        } finally {
            dayAdvancing = false;
        }
    }

    @SuppressWarnings("null")
    public static void requestSleepAdvance(ServerPlayer player, int sleepMinute) {
        if (player == null) {
            return;
        }
        if (player.level().dimension() != ModDimensions.STARDEW_VALLEY
            && player.level().dimension() != ModMiningDimensions.STARDEW_MINING) {
            return;
        }
        ServerLevel stardewLevel = player.server.getLevel(ModDimensions.STARDEW_VALLEY);
        if (stardewLevel == null) {
            return;
        }
        advanceToNextMorning(stardewLevel, sleepMinute, "sleep_confirm");
    }

    /**
     * 从MC dayTime计算星露谷分钟
     * dayTime 0 = 6:00 AM = 360分钟
     * dayTime 1000 = 7:00 AM = 420分钟
     * 公式：分钟 = (dayTime / 1000.0 + 6) * 60
     */
    public static int mcTimeToStardewMinutes(long dayTime) {
        // 确保dayTime在0-24000范围内
        dayTime = dayTime % 24000;
        if (dayTime < 0) dayTime += 24000;
        
        // 计算星露谷分钟
        // dayTime / 1000 = MC小时（从6:00开始）
        // +6 是因为dayTime 0 = 6:00 AM
        // *60 转换为分钟
        return (int) ((dayTime / 1000.0 + 6) * 60);
    }
    
    /**
     * 从星露谷分钟计算MC dayTime
     * 360分钟 (6:00 AM) = dayTime 0
     * 720分钟 (12:00 PM) = dayTime 6000
     * 公式：dayTime = (分钟 / 60.0 - 6) * 1000
     */
    public static long stardewMinutesToMcTime(int stardewMinutes) {
        // 分钟 / 60 = 小时
        // -6 是因为dayTime 0 = 6:00 AM
        // *1000 转换为MC ticks
        long dayTime = (long) ((stardewMinutes / 60.0 - 6) * 1000);
        if (dayTime < 0) dayTime += 24000;
        return dayTime;
    }

    /**
     * 进入星露谷维度前校验地图是否已就绪（仅预烘焙逻辑）。
     */
    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onTravelToDimension(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!ModDimensions.STARDEW_VALLEY.equals(event.getDimension())) {
            return;
        }

        ServerLevel stardewLevel = player.server.getLevel(ModDimensions.STARDEW_VALLEY);
        if (stardewLevel == null) {
            return;
        }

        StardewValleyPrebuiltRegionInstaller.InstallResult result = StardewValleyPrebuiltRegionInstaller.installIfAvailable(player.server);
        boolean prebuiltReady = result == StardewValleyPrebuiltRegionInstaller.InstallResult.INSTALLED
            || result == StardewValleyPrebuiltRegionInstaller.InstallResult.ALREADY_PRESENT
            || StardewValleyPrebuiltRegionInstaller.hasInstalledPrebuilt(player.server);

        if (prebuiltReady && !StardewValleyMapBootstrap.isGenerationComplete(stardewLevel)) {
            StardewValleyMapBootstrap.markAsPreGenerated(stardewLevel);
        }

        if (!prebuiltReady) {
            event.setCanceled(true);
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("§c星露谷地图未预加载完成，已禁止进入。请先打包并接入 pregen region。"), false);
            StardewCraft.LOGGER.error("[VALLEY_MAP] Denied travel to Stardew Valley: prebuilt region not installed for this save (installResult={})", result);
        }
    }

    /**
     * 玩家进入维度时确保昼夜循环开启
     */
    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // 星露谷维度和矿井维度都需要确保昼夜循环开启
        if (ModDimensions.STARDEW_VALLEY.equals(event.getTo()) || ModMiningDimensions.STARDEW_MINING.equals(event.getTo())) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            ServerLevel level = player.serverLevel();

            // Quest: warped to location
            String location = event.getTo().location().toString();
            com.stardew.craft.quest.StardewQuestEvents.fireWarped(player, location);

            if (ModDimensions.STARDEW_VALLEY.equals(event.getTo())) {
                // 如果是 CrossDimensionTeleporter 主动传送（如巫师塔入口），不覆盖目标位置
                if (!com.stardew.craft.interior.CrossDimensionTeleporter.consumeSkipAutoTeleport(player.getUUID())) {
                    net.minecraft.core.BlockPos fixedPos = new net.minecraft.core.BlockPos(150, -12, 119);
                    // 传送前清理
                    player.closeContainer();
                    player.stopUsingItem();

                    preloadChunksAround(level, fixedPos, 2);
                    player.teleportTo(level, 150.0D, -12.0D, 119.0D, player.getYRot(), player.getXRot());
                }

                // 确保农场默认方块和自然碎片已放置（在 preloadChunks 之后，
                // FarmInitializer 内部也会预加载农场区域区块）
                com.stardew.craft.dimension.FarmInitializer.ensureInitialized(level);

                // 确保第一天有 forage / artifact spot 生成（SavedData 保证只执行一次）
                int season = com.stardew.craft.time.StardewTimeManager.get().getCurrentSeason();
                com.stardew.craft.manager.ForageSpawnService.ensureInitialSpawn(level, season);
                com.stardew.craft.manager.ArtifactSpotSpawnService.ensureInitialSpawn(level, season);
            }

            // 开启原版昼夜循环
            if (!level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, level.getServer());
            }
            
            // 同步玩家数据到客户端
            com.stardew.craft.player.PlayerDataEventHandler.syncPlayerData(
                player, 
                com.stardew.craft.player.PlayerDataManager.getPlayerData(player)
            );
            
            // 同步当前时间到客户端
            StardewTimeManager timeManager = StardewTimeManager.get();
            PacketDistributor.sendToPlayer(player, TimeSyncPacket.fromTimeManager(timeManager));
        }

        // 玩家进入矿井维度：确保结构生成并传送到对应层数
        if (ModMiningDimensions.STARDEW_MINING.equals(event.getTo())) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            ServerLevel level = player.serverLevel();

            // 获取玩家的矿井数据
            com.stardew.craft.mining.MiningPlayerData playerData = 
                com.stardew.craft.mining.MiningDataManager.getPlayerData(player);
            
            int currentFloor = playerData.getCurrentFloor();

            // 确保大厅已生成（固定中心 0,64,0）
            com.stardew.craft.mining.MineEntranceBootstrap.ensureGenerated(level);
            
            // 确保当前楼层已生成
            if (currentFloor > 0) {
                com.stardew.craft.mining.MineFloorGenerator.generateFloor(level, currentFloor);
            }
            
            // 传送到当前层数的出生点
            com.stardew.craft.mining.MiningCoordinates.teleportPlayerToFloor(player, level, currentFloor);
            
            // 同步层数到客户端（显示UI）
            com.stardew.craft.network.MiningFloorSyncPacket packet = 
                new com.stardew.craft.network.MiningFloorSyncPacket(currentFloor);
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, packet);
        }
    }
    
    /**
     * 服务端维度Tick事件 - 从MC时间同步星露谷时间
     */
    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }
        
        // 只在星露谷维度处理（矿井维度会自动跟随）
        if (!ModDimensions.STARDEW_VALLEY.equals(serverLevel.dimension())) {
            return;
        }

        // 检查是否有玩家在星露谷相关维度
        var server = serverLevel.getServer();
        boolean anyPlayerInStardew = false;
        for (var player : server.getPlayerList().getPlayers()) {
            if (ModDimensions.STARDEW_VALLEY.equals(player.level().dimension())
                || ModMiningDimensions.STARDEW_MINING.equals(player.level().dimension())) {
                anyPlayerInStardew = true;
                break;
            }
        }
        
        // 根据是否有玩家在维度内控制昼夜循环
        boolean daylightEnabled = serverLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
        if (anyPlayerInStardew && !daylightEnabled) {
            // 有玩家在维度内，开启昼夜循环
            serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, server);
        } else if (!anyPlayerInStardew && daylightEnabled) {
            // 没有玩家在维度内，关闭昼夜循环（时间静止）
            serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
            return; // 时间静止时不需要后续处理
        } else if (!anyPlayerInStardew) {
            // 没有玩家且已经关闭，直接返回
            return;
        }
        
        // 获取当前MC dayTime
        long dayTime = serverLevel.getDayTime() % 24000;
        
        // 从MC时间计算星露谷分钟
        int stardewMinutes = mcTimeToStardewMinutes(dayTime);
        
        // 更新TimeManager的时间（用于UI显示和其他系统）
        StardewTimeManager timeManager = StardewTimeManager.get();
        timeManager.setCurrentTimeFromMC(stardewMinutes);

        int tenMinuteSlot = stardewMinutes / 10;
        int timeOfDayHHMM = stardewMinutesToTimeOfDay(stardewMinutes);
        int dayKey = timeManager.getCurrentYear() * 1000 + timeManager.getCurrentSeason() * 100 + timeManager.getCurrentDay();
        if (!dayAdvancing && (dayKey != lastAnimalTenMinuteDayKey || tenMinuteSlot != lastAnimalTenMinuteSlot)) {
            lastAnimalTenMinuteDayKey = dayKey;
            lastAnimalTenMinuteSlot = tenMinuteSlot;
            com.stardew.craft.manager.AnimalGrowthManager.get(serverLevel).updatePerTenMinutes(serverLevel, timeOfDayHHMM);
        }
        
        // ── 午夜 0:00（对标 SDV case 2400）── 时钟抖动 + 困倦表情 + "It's getting late..." 消息
        if (dayTime >= MIDNIGHT_TIME && !dayAdvancing && !midnightWarned) {
            midnightWarned = true;
            for (ServerPlayer sp : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (sp.level().dimension() == ModDimensions.STARDEW_VALLEY
                        || sp.level().dimension() == ModMiningDimensions.STARDEW_MINING) {
                    // 时钟抖动 + 全局消息
                    PacketDistributor.sendToPlayer(sp, new com.stardew.craft.network.TimeWarningPayload(
                        com.stardew.craft.network.TimeWarningPayload.MIDNIGHT));
                    // 困倦表情气泡（sleep emote, iconIndex=24）
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(sp,
                        new com.stardew.craft.network.payload.EmoteBroadcastPayload(sp.getId(), 24));
                }
            }
        }

        // ── 凌晨 1:00（对标 SDV case 2500）── 时钟抖动 + 困倦表情（无文字消息）
        if (dayTime >= ONE_AM_TIME && !dayAdvancing && !oneAMWarned) {
            oneAMWarned = true;
            for (ServerPlayer sp : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (sp.level().dimension() == ModDimensions.STARDEW_VALLEY
                        || sp.level().dimension() == ModMiningDimensions.STARDEW_MINING) {
                    PacketDistributor.sendToPlayer(sp, new com.stardew.craft.network.TimeWarningPayload(
                        com.stardew.craft.network.TimeWarningPayload.ONE_AM));
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(sp,
                        new com.stardew.craft.network.payload.EmoteBroadcastPayload(sp.getId(), 24));
                }
            }
        }

        // ── 2:00 AM 强制晕倒 + 日推进（对标 SDV timeOfDay >= 2600）──
        if (dayTime >= DAY_END_TIME && !dayAdvancing) {
            // 时钟抖动
            if (!twoAMWarned) {
                twoAMWarned = true;
                for (ServerPlayer sp : serverLevel.getServer().getPlayerList().getPlayers()) {
                    if (sp.level().dimension() == ModDimensions.STARDEW_VALLEY
                            || sp.level().dimension() == ModMiningDimensions.STARDEW_MINING) {
                        PacketDistributor.sendToPlayer(sp, new com.stardew.craft.network.TimeWarningPayload(
                            com.stardew.craft.network.TimeWarningPayload.TWO_AM));
                    }
                }
            }
            // 对标 SDV case 2600: 强制下马、停止坐下、中断工具使用
            for (ServerPlayer sp : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (sp.level().dimension() == ModDimensions.STARDEW_VALLEY
                        || sp.level().dimension() == ModMiningDimensions.STARDEW_MINING) {
                    // 强制下马
                    if (sp.getVehicle() != null) {
                        sp.stopRiding();
                    }
                    // 强制停止使用物品/工具
                    if (sp.isUsingItem()) {
                        sp.stopUsingItem();
                    }
                }
            }
            // 1. 对每个玩家执行晕倒惩罚（结果暂存，合并进 OvernightSettlementPayload）
            for (ServerPlayer sp : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (sp.level().dimension() == ModDimensions.STARDEW_VALLEY
                        || sp.level().dimension() == ModMiningDimensions.STARDEW_MINING) {
                    com.stardew.craft.player.PassOutService.on2AMPassOut(sp);
                }
            }
            // 2. 推进到次日（内部会消费 PassOutResult 并合并进结算包发送给客户端）
            advanceToNextMorning(serverLevel, stardewMinutes, "pass_out_2am");
            // 3. 传送所有星露谷/矿井维度玩家回农场出生点
            for (ServerPlayer sp : serverLevel.getServer().getPlayerList().getPlayers()) {
                if (sp.level().dimension() == ModDimensions.STARDEW_VALLEY
                        || sp.level().dimension() == ModMiningDimensions.STARDEW_MINING) {
                    com.stardew.craft.player.PassOutService.teleportToFarmSpawn(sp);
                }
            }
        }
        
        // 每秒（20 ticks）同步UI时间到客户端
        if (serverLevel.getGameTime() % 20 == 0) {
            PacketDistributor.sendToAllPlayers(TimeSyncPacket.fromTimeManager(timeManager));
        }
    }
    
    /**
     * 监听睡觉完成事件，手动触发新的一天
     */
    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == ModDimensions.STARDEW_VALLEY) {
            StardewTimeManager timeManager = StardewTimeManager.get();
            advanceToNextMorning(level, timeManager.getCurrentTime(), "vanilla_sleep_finished");
        }
    }

    /**
     * 立即更新MC时间（命令调用）
     */
    @SuppressWarnings("null")
    public static void updateMCTime(StardewTimeManager timeManager) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            long mcTimeOfDay = stardewMinutesToMcTime(timeManager.getCurrentTime());
            
            // 获取主世界当前天数，用于计算新的绝对时间
            ServerLevel overworld = server.overworld();
            long currentDay = overworld.getDayTime() / 24000;
            long newDayTime = currentDay * 24000 + mcTimeOfDay;
            
            // 设置所有维度的时间（MC dayTime是全局共享的）
            for (ServerLevel level : server.getAllLevels()) {
                level.setDayTime(newDayTime);
            }
            
            // 发送时间包到所有玩家
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerLevel playerLevel = player.serverLevel();
                player.connection.send(new ClientboundSetTimePacket(
                    playerLevel.getGameTime(),
                    newDayTime,
                    playerLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
                ));
            }
            
        }
    }

    private static void preloadChunksAround(ServerLevel level, net.minecraft.core.BlockPos center, int radiusChunks) {
        int centerChunkX = center.getX() >> 4;
        int centerChunkZ = center.getZ() >> 4;
        for (int dz = -radiusChunks; dz <= radiusChunks; dz++) {
            for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
                level.getChunk(centerChunkX + dx, centerChunkZ + dz);
            }
        }
    }

    private static int stardewMinutesToTimeOfDay(int stardewMinutes) {
        int hour = Math.max(0, stardewMinutes / 60);
        int minute = Math.max(0, stardewMinutes % 60);
        return hour * 100 + minute;
    }
}
