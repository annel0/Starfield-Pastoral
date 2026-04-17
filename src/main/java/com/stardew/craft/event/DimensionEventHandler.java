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
    /**
     * Public entry point for triggering day advance from outside this class
     * (e.g. from logout handler when sleep votes are satisfied).
     */
    public static void triggerAdvance(ServerLevel stardewLevel, int sleepMinute, String reason) {
        advanceToNextMorning(stardewLevel, sleepMinute, reason);
    }

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

            // 必须在主世界（overworld）上调用 setDayTime —— 非主世界维度使用
            // DerivedLevelData，其 setDayTime() 是空操作（no-op）。
            // 所有维度的 getDayTime() 都委托给主世界的 PrimaryLevelData，
            // 所以在 overworld 上设置一次即可影响所有维度。
            server.overworld().setDayTime(newDayTime);

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerLevel playerLevel = player.serverLevel();
                if (isStardewDimension(playerLevel)) {
                    player.connection.send(new ClientboundSetTimePacket(
                        playerLevel.getGameTime(),
                        newDayTime,
                        playerLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
                    ));
                }
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

        // 多人投票：只有所有 Stardew 维度玩家都投票后才推进
        if (SleepVoteTracker.castVote(player, sleepMinute)) {
            int effectiveSleepMinute = SleepVoteTracker.getLatestSleepMinute();
            SleepVoteTracker.clearVotes();
            advanceToNextMorning(stardewLevel, effectiveSleepMinute, "sleep_confirm");
        }
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
                    // 查询玩家的农场出生点
                    com.stardew.craft.farm.FarmInstanceRegistry registry = com.stardew.craft.farm.FarmInstanceRegistry.get();
                    net.minecraft.core.BlockPos spawnPos = registry.getFarmSpawnPoint(player.getUUID());
                    if (spawnPos == null) {
                        spawnPos = new net.minecraft.core.BlockPos(150, -12, 119);
                    }
                    // 传送前清理
                    player.closeContainer();
                    player.stopUsingItem();

                    preloadChunksAround(level, spawnPos, 2);
                    player.teleportTo(level, spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D, player.getYRot(), player.getXRot());
                }

                // 旧 FarmInitializer 已废弃，内置农场区域不再初始化（玩家现在有自己的个人农场）
                // com.stardew.craft.dimension.FarmInitializer.ensureInitialized(level);

                // 确保农场入口屏障墙和交互实体已放置
                com.stardew.craft.farm.FarmEntryBarrierManager.get(level).ensureBarriersPlaced(level);

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

            // 首次进入矿井赠送矿洞图腾
            if (!playerData.hasReceivedMineTotem()) {
                net.minecraft.world.item.ItemStack totem = new net.minecraft.world.item.ItemStack(
                    com.stardew.craft.item.ModItems.MINE_TOTEM.get());
                if (!player.getInventory().add(totem)) {
                    player.drop(totem, false);
                }
                playerData.setReceivedMineTotem(true);
                com.stardew.craft.mining.MiningDataManager.savePlayerData(player, playerData);
                player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("item.stardewcraft.mine_totem.gift")
                        .withStyle(net.minecraft.ChatFormatting.GOLD), false);
            }

            // 延迟 3 tick 强制刷新客户端光照
            final int floor = currentFloor;
            level.getServer().tell(new net.minecraft.server.TickTask(
                level.getServer().getTickCount() + 3,
                () -> com.stardew.craft.mining.MineFloorGenerator.forceClientLightRefresh(level, floor)
            ));
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
        var allPlayers = server.getPlayerList().getPlayers();
        // 预收集星露谷维度内的玩家列表（后续警告/晕倒复用，避免多轮遍历）
        java.util.List<ServerPlayer> stardewPlayers = new java.util.ArrayList<>();
        for (var player : allPlayers) {
            if (ModDimensions.STARDEW_VALLEY.equals(player.level().dimension())
                || ModMiningDimensions.STARDEW_MINING.equals(player.level().dimension())) {
                stardewPlayers.add(player);
            }
        }
        boolean anyPlayerInStardew = !stardewPlayers.isEmpty();
        
        // 没有玩家在星露谷相关维度时，跳过时间处理（但不关闭全局 doDaylightCycle，
        // 否则主世界时间也会被冻结）
        if (!anyPlayerInStardew) {
            return;
        }

        // 确保昼夜循环开启（可能被其他逻辑/命令关闭过）
        boolean daylightEnabled = serverLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT);
        if (!daylightEnabled) {
            serverLevel.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, server);
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
            for (ServerPlayer sp : stardewPlayers) {
                    // 时钟抖动 + 全局消息
                    PacketDistributor.sendToPlayer(sp, new com.stardew.craft.network.TimeWarningPayload(
                        com.stardew.craft.network.TimeWarningPayload.MIDNIGHT));
                    // 困倦表情气泡（sleep emote, iconIndex=24）
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(sp,
                        new com.stardew.craft.network.payload.EmoteBroadcastPayload(sp.getId(), 24));
            }
        }

        // ── 凌晨 1:00（对标 SDV case 2500）── 时钟抖动 + 困倦表情（无文字消息）
        if (dayTime >= ONE_AM_TIME && !dayAdvancing && !oneAMWarned) {
            oneAMWarned = true;
            for (ServerPlayer sp : stardewPlayers) {
                    PacketDistributor.sendToPlayer(sp, new com.stardew.craft.network.TimeWarningPayload(
                        com.stardew.craft.network.TimeWarningPayload.ONE_AM));
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(sp,
                        new com.stardew.craft.network.payload.EmoteBroadcastPayload(sp.getId(), 24));
            }
        }

        // ── 2:00 AM 强制晕倒 + 日推进（对标 SDV timeOfDay >= 2600）──
        if (dayTime >= DAY_END_TIME && !dayAdvancing) {
            // 时钟抖动
            if (!twoAMWarned) {
                twoAMWarned = true;
                for (ServerPlayer sp : stardewPlayers) {
                        PacketDistributor.sendToPlayer(sp, new com.stardew.craft.network.TimeWarningPayload(
                            com.stardew.craft.network.TimeWarningPayload.TWO_AM));
                }
            }
            // 对标 SDV case 2600: 强制下马、停止坐下、中断工具使用
            for (ServerPlayer sp : stardewPlayers) {
                    // 强制下马
                    if (sp.getVehicle() != null) {
                        sp.stopRiding();
                    }
                    // 强制停止使用物品/工具
                    if (sp.isUsingItem()) {
                        sp.stopUsingItem();
                    }
            }
            // 关键：先保存已投票玩家快照，再清空投票
            java.util.Set<java.util.UUID> votedPlayers = SleepVoteTracker.getVotedPlayerSnapshot();
            // 1. 对每个玩家执行晕倒惩罚（跳过已投睡觉票的玩家——他们选择了睡觉，不算晕倒）
            for (ServerPlayer sp : stardewPlayers) {
                    if (!votedPlayers.contains(sp.getUUID())) {
                        com.stardew.craft.player.PassOutService.on2AMPassOut(sp);
                    }
            }
            // 2. 推进到次日（内部会消费 PassOutResult 并合并进结算包发送给客户端）
            SleepVoteTracker.clearVotes(); // 2AM 强制推进，清空所有未完成的投票
            advanceToNextMorning(serverLevel, stardewMinutes, "pass_out_2am");
            // 3. 只传送未投票的玩家回农场出生点（已投票玩家正常过夜，不惩罚不传送）
            for (ServerPlayer sp : stardewPlayers) {
                    if (!votedPlayers.contains(sp.getUUID())) {
                        com.stardew.craft.player.PassOutService.teleportToFarmSpawn(sp);
                    }
            }
        }
        
        // 每秒（20 ticks）同步UI时间到客户端（仅发给星露谷维度玩家）
        if (serverLevel.getGameTime() % 20 == 0) {
            var syncPacket = TimeSyncPacket.fromTimeManager(timeManager);
            for (ServerPlayer sp : stardewPlayers) {
                PacketDistributor.sendToPlayer(sp, syncPacket);
            }
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
            SleepVoteTracker.clearVotes();
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
            
            // 必须在 overworld 上调用 setDayTime（DerivedLevelData.setDayTime 是 no-op）
            overworld.setDayTime(newDayTime);
            
            // 发送时间包给星露谷维度的玩家
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerLevel playerLevel = player.serverLevel();
                if (isStardewDimension(playerLevel)) {
                    player.connection.send(new ClientboundSetTimePacket(
                        playerLevel.getGameTime(),
                        newDayTime,
                        playerLevel.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)
                    ));
                }
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

    private static boolean isStardewDimension(ServerLevel level) {
        return ModDimensions.STARDEW_VALLEY.equals(level.dimension())
                || ModMiningDimensions.STARDEW_MINING.equals(level.dimension());
    }

    private static int stardewMinutesToTimeOfDay(int stardewMinutes) {
        int hour = Math.max(0, stardewMinutes / 60);
        int minute = Math.max(0, stardewMinutes % 60);
        return hour * 100 + minute;
    }
}
