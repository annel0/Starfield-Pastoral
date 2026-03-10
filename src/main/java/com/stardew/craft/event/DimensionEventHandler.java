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
 * - 当dayTime >= 20000（2:00 AM）时，跳到下一天并重置dayTime到0
 * 
 * MC dayTime 映射：
 * - dayTime 0 = 6:00 AM（日出）
 * - dayTime 6000 = 12:00 PM（正午）
 * - dayTime 12000 = 6:00 PM（日落）
 * - dayTime 18000 = 12:00 AM（午夜）
 * - dayTime 20000 = 2:00 AM（星露谷一天结束）
 * 
 * 转换公式：星露谷分钟 = (dayTime / 1000.0 + 6) * 60
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public class DimensionEventHandler {
    
    // 2:00 AM 对应的MC dayTime（这时候跳到下一天）
    private static final long DAY_END_TIME = 20000;
    
    // 防止重复触发新的一天
    private static boolean dayAdvancing = false;
    private static int lastAnimalTenMinuteDayKey = Integer.MIN_VALUE;
    private static int lastAnimalTenMinuteSlot = Integer.MIN_VALUE;

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

            if (ModDimensions.STARDEW_VALLEY.equals(event.getTo())) {
                net.minecraft.core.BlockPos fixedPos = new net.minecraft.core.BlockPos(150, -13, 119);
                preloadChunksAround(level, fixedPos, 4);
                StardewCraft.LOGGER.info("[VALLEY_MAP] Teleport to fixed point: x=150, y=-13, z=119");
                player.teleportTo(level, 150.0D, -13.0D, 119.0D, player.getYRot(), player.getXRot());
            }

            // 开启原版昼夜循环
            if (!level.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
                level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, level.getServer());
                StardewCraft.LOGGER.info("[TIME] Enabled doDaylightCycle for dimension {}", 
                    level.dimension().location());
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

            StardewCraft.LOGGER.info("[DIMENSION] Player {} entering mining dimension", player.getName().getString());
            
            // 获取玩家的矿井数据
            com.stardew.craft.mining.MiningPlayerData playerData = 
                com.stardew.craft.mining.MiningDataManager.getPlayerData(player);
            
            int currentFloor = playerData.getCurrentFloor();
            StardewCraft.LOGGER.info("[DIMENSION] Player current floor: {}", currentFloor);

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
            StardewCraft.LOGGER.debug("[TIME] Player in Stardew dimension, enabling daylight cycle");
        } else if (!anyPlayerInStardew && daylightEnabled) {
            // 没有玩家在维度内，关闭昼夜循环（时间静止）
            serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
            StardewCraft.LOGGER.debug("[TIME] No player in Stardew dimension, pausing time");
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
        
        // 检查是否到达2:00 AM（dayTime >= 20000）
        if (dayTime >= DAY_END_TIME && !dayAdvancing) {
            dayAdvancing = true;
            
            StardewCraft.LOGGER.info("[TIME] Reached 2:00 AM (dayTime={}), advancing to next day", dayTime);
            
            // 触发新的一天
            timeManager.advanceDay();
            
            // 计算新的dayTime（下一天的6:00 AM）
            long currentDay = serverLevel.getDayTime() / 24000;
            long newDayTime = (currentDay + 1) * 24000; // 下一天的6:00 AM (dayTime 0)
            
            // 设置所有维度的时间（MC dayTime是全局共享的）
            for (ServerLevel level : server.getAllLevels()) {
                level.setDayTime(newDayTime);
            }
            
            // 立即发送时间包到所有玩家（让客户端天空立即变成早上）
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerLevel playerLevel = player.serverLevel();
                player.connection.send(new ClientboundSetTimePacket(
                    playerLevel.getGameTime(),
                    newDayTime,
                    playerLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
                ));
            }
            
            // 同步星露谷时间到所有玩家（HUD显示）
            PacketDistributor.sendToAllPlayers(TimeSyncPacket.fromTimeManager(timeManager));

            lastAnimalTenMinuteDayKey = Integer.MIN_VALUE;
            lastAnimalTenMinuteSlot = Integer.MIN_VALUE;
            
            dayAdvancing = false;
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
            // 触发星露谷时间的新一天逻辑（这会包含作物生长等）
            StardewTimeManager timeManager = StardewTimeManager.get();
            timeManager.advanceDay();
            
            // 计算新的dayTime（下一天的6:00 AM）
            var server = level.getServer();
            long currentDay = level.getDayTime() / 24000;
            long newDayTime = (currentDay + 1) * 24000;
            
            // 设置所有维度的时间
            for (ServerLevel sl : server.getAllLevels()) {
                sl.setDayTime(newDayTime);
            }
            
            // 立即发送时间包到所有玩家
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerLevel playerLevel = player.serverLevel();
                player.connection.send(new ClientboundSetTimePacket(
                    playerLevel.getGameTime(),
                    newDayTime,
                    playerLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
                ));
            }
            
            // 同步星露谷时间
            PacketDistributor.sendToAllPlayers(TimeSyncPacket.fromTimeManager(timeManager));

            lastAnimalTenMinuteDayKey = Integer.MIN_VALUE;
            lastAnimalTenMinuteSlot = Integer.MIN_VALUE;
            
            StardewCraft.LOGGER.info("Sleep finished, advanced Stardew day.");
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
            
            StardewCraft.LOGGER.info("[TIME] Command: Stardew={} → MC dayOfTime={}, total={}", 
                timeManager.getCurrentTime(), mcTimeOfDay, newDayTime);
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
