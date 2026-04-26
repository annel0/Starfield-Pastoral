package com.stardew.craft.event;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.dimension.StardewValleyMapBootstrap;
import com.stardew.craft.dimension.StardewValleyPrebuiltRegionInstaller;
import com.stardew.craft.interior.InteriorSubspaceManager;
import com.stardew.craft.network.TimeSyncPacket;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;

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
            // 唤醒所有正在睡觉的玩家（先于日结算，防止残留睡眠状态）
            for (ServerPlayer sp : sourceLevel.getServer().getPlayerList().getPlayers()) {
                if (sp.isSleeping()) {
                    sp.stopSleeping();
                }
            }

            StardewTimeManager timeManager = StardewTimeManager.get();

            // === 通过 offset 推进虚拟 dayTime，不修改主世界 ===
            long currentVirtual = timeManager.getVirtualDayTime(sourceLevel);
            long currentDay = currentVirtual / 24000;
            long newDayTime = (currentDay + 1) * 24000;
            timeManager.setVirtualDayTime(sourceLevel, newDayTime);

            // TimeSyncPacket（在 advanceDayWithSleepTime 后面）会携带最新 virtualDayTime，
            // 客户端 StardewClientTimeState 自动同步。

            // 日结算逻辑（作物生长、天气、玩家恢复等）
            try {
                timeManager.advanceDayWithSleepTime(sleepMinute);
            } catch (Exception e) {
                StardewCraft.LOGGER.error("Error during advanceDayWithSleepTime (day still advanced to prevent freeze)", e);
            }

            // 日结算后：乌鸦袭击（针对成熟作物，按稻草人保护范围豁免）
            try {
                com.stardew.craft.manager.CrowAttackScheduler.processOvernight(sourceLevel);
            } catch (Exception e) {
                StardewCraft.LOGGER.error("Error during CrowAttackScheduler.processOvernight", e);
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

        // 先让玩家躺在床上（客户端自动弹出原版 InBedChatScreen）
        net.minecraft.core.BlockPos clickedPos = SleepInteractionHandler.consumePendingBedPos(player);
        if (clickedPos != null) {
            // 自定义床（MapDecorStaticBlock）可能是 1×2（单人床）或 2×2（双人床），
            // 需要根据点击格子在床内的相对位置，计算出该玩家的「床位列」与朝向：
            //   1. 把点击点（main 或任一 extension）规范化为 main 格；
            //   2. 列出床的所有水平 extension 方向，第一个为长轴（头-脚方向），第二个为宽轴（双人床左右两列）；
            //      约定：MAIN→长轴 extension 端为「床头」（headboard 所在端）；
            //   3. 用点击点相对 main 在宽轴方向的分量决定睡眠列（0=MAIN 列，1=对侧列）；
            //   4. 让玩家朝向长轴方向（头朝床头），并把身体居中到该列两格中点。
            // 双人床因此可同时由两个玩家分别躺左右两列，互不冲突。
            // 原版 BedBlock.getBedOrientation 对非 BedBlock 返回 null，渲染器回落到玩家 yRot。
            net.minecraft.world.level.block.state.BlockState clickedState = player.level().getBlockState(clickedPos);
            net.minecraft.core.BlockPos sleepPos = clickedPos;
            if (clickedState.getBlock() instanceof com.stardew.craft.block.decor.MapDecorStaticBlock decor) {
                // ── 1. 规范化到 MAIN ──
                net.minecraft.core.BlockPos mainPos = decor.findMainPos(player.level(), clickedPos, clickedState);
                if (mainPos == null) mainPos = clickedPos;
                net.minecraft.world.level.block.state.BlockState mainState = player.level().getBlockState(mainPos);
                net.minecraft.core.Direction lengthAxis = null;
                net.minecraft.core.Direction widthAxis = null;
                if (mainState.getBlock() instanceof com.stardew.craft.block.decor.MapDecorStaticBlock mainDecor) {
                    net.minecraft.core.Direction facing = mainState.getValue(
                            com.stardew.craft.block.decor.MapDecorStaticBlock.FACING);
                    java.util.List<net.minecraft.core.Direction> axes = mainDecor.findAllHorizontalExtensionDirections(facing);
                    // 长轴：第一个水平 extension（约定为床头方向 / MAIN→headboard）
                    if (!axes.isEmpty()) lengthAxis = axes.get(0);
                    // 宽轴：第二个、与长轴垂直的水平 extension（仅双人床有）
                    if (axes.size() >= 2) {
                        for (net.minecraft.core.Direction d : axes) {
                            if (d.getAxis() != lengthAxis.getAxis()) { widthAxis = d; break; }
                        }
                    }
                }

                // ── 2. 决定睡眠列 ──
                sleepPos = mainPos;
                if (widthAxis != null) {
                    int dx = clickedPos.getX() - mainPos.getX();
                    int dz = clickedPos.getZ() - mainPos.getZ();
                    int widthComp = dx * widthAxis.getStepX() + dz * widthAxis.getStepZ();
                    if (widthComp >= 1) {
                        sleepPos = mainPos.relative(widthAxis);
                    }
                }

                // ── 3. yRot：头朝长轴方向（床头端 = MAIN + lengthAxis 的 headboard 那侧） ──
                if (lengthAxis != null) {
                    player.setYRot(lengthAxis.toYRot());
                    player.yRotO = player.getYRot();
                    player.setYHeadRot(player.getYRot());
                }

                // 先 startSleeping（会把位置设为 sleepPos + (0.5, 0.6875, 0.5)）
                player.startSleeping(sleepPos);

                // ── 4. 把身体居中到「该列两格」中点 ──
                if (lengthAxis != null) {
                    double cx = sleepPos.getX() + 0.5 + lengthAxis.getStepX() * 0.5;
                    double cz = sleepPos.getZ() + 0.5 + lengthAxis.getStepZ() * 0.5;
                    player.setPos(cx, sleepPos.getY() + 0.6875, cz);
                }
            } else {
                player.startSleeping(clickedPos);
            }
        }

        // 多人投票：只有所有 Stardew 维度玩家都投票后才推进
        if (SleepVoteTracker.castVote(player, sleepMinute)) {
            int effectiveSleepMinute = SleepVoteTracker.getLatestSleepMinute();
            SleepVoteTracker.clearVotes();
            SleepInteractionHandler.clearAllPendingBedPositions();
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
     * 进入星露谷维度前校验地图是否已就绪（仅检查，不安装）。
     * <p>
     * pregen 安装只在 {@code ServerAboutToStart} 阶段执行（level 尚未加载时），
     * 这里只做状态检查 + 拦截。在 level 已加载后调用 {@code installIfAvailable()}
     * 覆盖 .mca 文件是不安全的——内存中的区块不会刷新，且后续 ensurePlaced()
     * 放置的方块会在重启后被新 .mca 覆盖丢失。
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

        // 只检查 pregen 是否已就绪，不再调用 installIfAvailable()。
        // pregen 安装已在 ServerAboutToStart 完成；manager 重置已在 ServerStarting 完成。
        boolean prebuiltReady = StardewValleyPrebuiltRegionInstaller.hasInstalledPrebuilt(player.server);

        if (prebuiltReady && !StardewValleyMapBootstrap.isGenerationComplete(stardewLevel)) {
            StardewValleyMapBootstrap.markAsPreGenerated(stardewLevel);
        }

        // 如果 pregenJustInstalled 仍为 true（说明 ServerStarting 时 level 为 null，
        // 没能执行 portal replacement），在这里补做。
        if (com.stardew.craft.StardewCraft.pregenJustInstalled) {
            com.stardew.craft.StardewCraft.pregenJustInstalled = false;
            com.stardew.craft.interior.InteriorSubspaceManager.replaceAllPortalsIfReady(
                stardewLevel, "pregen_region_reinstalled_deferred");

            // manager 版本号在 ServerStarting 已经无条件重置，
            // 但如果 ServerStarting 时 level 为 null 也跳过了，这里兜底重置。
            com.stardew.craft.farm.FarmEntryBarrierManager.get(stardewLevel).resetForMigration();
            com.stardew.craft.communitycenter.quarry.QuarryAccessManager.get(stardewLevel).resetForMigration();
            com.stardew.craft.minecart.MinecartStationManager.get(stardewLevel).resetForMigration();
            com.stardew.craft.manager.QuarrySpawnService.resetInitialSpawn(stardewLevel);

            StardewCraft.LOGGER.info("[VALLEY_PREGEN] Deferred portal replacement + manager reset (ServerStarting missed level)");
        }

        if (!prebuiltReady) {
            event.setCanceled(true);
            player.displayClientMessage(net.minecraft.network.chat.Component.literal("§c星露谷地图未预加载完成，已禁止进入。请先打包并接入 pregen region。"), false);
            StardewCraft.LOGGER.error("[VALLEY_MAP] Denied travel to Stardew Valley: prebuilt region not installed for this save");
        }
    }

    /**
     * 玩家进入维度时确保昼夜循环开启
     */
    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        // 从矿井维度离开 → 重置楼层为 0（无论是否回到星露谷维度）
        // 这样通过 WarpWand / TeleportTotem / MineTotem 等任意传送方式离开矿井时
        // 楼层都会归零，下次挖矿从第 0 层开始
        if (ModMiningDimensions.STARDEW_MINING.equals(event.getFrom())
                && !ModMiningDimensions.STARDEW_MINING.equals(event.getTo())
                && event.getEntity() instanceof ServerPlayer serverPlayer) {
            com.stardew.craft.mining.MiningPlayerData miningData =
                    com.stardew.craft.mining.MiningDataManager.getPlayerData(serverPlayer);
            if (miningData != null && miningData.getCurrentFloor() != 0) {
                miningData.setCurrentFloor(0);
                com.stardew.craft.mining.MiningDataManager.savePlayerData(serverPlayer, miningData);
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                    serverPlayer,
                    new com.stardew.craft.network.MiningFloorSyncPacket(0)
                );
            }
        }

        // 星露谷维度和矿井维度都需要确保昼夜循环开启
        if (ModDimensions.STARDEW_VALLEY.equals(event.getTo()) || ModMiningDimensions.STARDEW_MINING.equals(event.getTo())) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            ServerLevel level = player.serverLevel();

            // Quest: warped to location
            String location = event.getTo().location().toString();
            com.stardew.craft.quest.StardewQuestEvents.fireWarped(player, location);

            if (ModDimensions.STARDEW_VALLEY.equals(event.getTo())) {
                // 如果是 ModTeleport / CrossDimensionTeleporter 主动传送，不覆盖目标位置
                if (!com.stardew.craft.interior.CrossDimensionTeleporter.consumeSkipAutoTeleport(player.getUUID())) {
                    StardewCraft.LOGGER.warn("[DIM] auto-redirect fired for {} -> stardew_valley: no SKIP flag, assuming vanilla source (/tp, /execute in, respawn). Mod code must teleport via ModTeleport.",
                            player.getName().getString());
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

                // 把各 ensurePlaced() 分散到后续 tick 执行，防止同帧堆叠触发 watchdog。
                // 每个任务本身有 SavedData 版本检查，已完成的会立即跳过（< 1ms）。
                scheduleDeferredInit(level);
            }

            // 发送星露谷时间同步包（包含 virtualDayTime，客户端 StardewClientTimeState 处理天空）
            StardewTimeManager timeManager = StardewTimeManager.get();
            PacketDistributor.sendToPlayer(player, TimeSyncPacket.fromTimeManager(timeManager));

            // 发送星露谷天气渲染包（覆盖原版 sendLevelInfo 发的主世界天气）
            {
                var weatherData = com.stardew.craft.weather.WeatherSavedData.get(level);
                var weatherState = weatherData.getWeatherState(level.dimension());
                com.stardew.craft.weather.WeatherManager.WeatherState.sendWeatherPackets(
                        player, weatherState.isRaining(), weatherState.isThundering());
            }
            
            // 同步玩家数据到客户端
            com.stardew.craft.player.PlayerDataEventHandler.syncPlayerData(
                player, 
                com.stardew.craft.player.PlayerDataManager.getPlayerData(player)
            );
            
            // 同步当前时间到客户端（timeManager 已在前面获取）
            PacketDistributor.sendToPlayer(player, TimeSyncPacket.fromTimeManager(timeManager));

            // 同步淘金点 — 切到星露谷/矿井维度时把该维度下已记录的点位推给客户端
            try {
                com.stardew.craft.communitycenter.reward.panning.OrePanPointManager
                        .get(level).syncToClient(player);
            } catch (Exception ignored) { /* 不阻塞维度切换 */ }

            // 同步气泡（fish splash points）— 仅在星露谷主维度有意义
            try {
                com.stardew.craft.fishing.splash.FishSplashState fs =
                        com.stardew.craft.fishing.splash.FishSplashState.getStardewState(level);
                if (fs != null) fs.sendFullSnapshot(player);
            } catch (Exception ignored) {}
        }
        // 离开星露谷/矿井 → 其它维度时，清空客户端淘金点 beam
        if ((ModDimensions.STARDEW_VALLEY.equals(event.getFrom()) || ModMiningDimensions.STARDEW_MINING.equals(event.getFrom()))
                && !ModDimensions.STARDEW_VALLEY.equals(event.getTo())
                && !ModMiningDimensions.STARDEW_MINING.equals(event.getTo())
                && event.getEntity() instanceof ServerPlayer leavingPlayer) {
            try {
                com.stardew.craft.communitycenter.reward.panning.OrePanPointManager.syncEmpty(leavingPlayer);
            } catch (Exception ignored) {}
            // 清空客户端气泡缓存
            try {
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(
                        leavingPlayer,
                        com.stardew.craft.network.payload.FishSplashSyncPayload.snapshot(new java.util.LinkedHashMap<>()));
            } catch (Exception ignored) {}
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

            // 如果是 ModTeleport / CrossDimensionTeleporter 主动传送，不覆盖目标位置
            if (!com.stardew.craft.interior.CrossDimensionTeleporter.consumeSkipAutoTeleport(player.getUUID())) {
                StardewCraft.LOGGER.warn("[DIM] auto-redirect fired for {} -> stardew_mining floor {}: no SKIP flag, assuming vanilla source. Mod code must teleport via ModTeleport.",
                        player.getName().getString(), currentFloor);
                // 传送到当前层数的出生点
                com.stardew.craft.mining.MiningCoordinates.teleportPlayerToFloor(player, level, currentFloor);
            }
            
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

        var server = serverLevel.getServer();

        // 注意：非主世界维度使用 DerivedLevelData，其 setDayTime() 是空操作、
        // getDayTime() 直接代理到主世界。不要尝试在这里调用 setDayTime。
        // 正确的虚拟时间通过 TimeSyncPacket + 客户端 StardewClientTimeState 处理。
        StardewTimeManager timeManager = StardewTimeManager.get();
        long virtualDayTime = timeManager.getVirtualDayTime(serverLevel);

        // 检查是否有玩家在星露谷相关维度
        var allPlayers = server.getPlayerList().getPlayers();
        java.util.List<ServerPlayer> stardewPlayers = new java.util.ArrayList<>();
        for (var player : allPlayers) {
            if (ModDimensions.STARDEW_VALLEY.equals(player.level().dimension())
                || ModMiningDimensions.STARDEW_MINING.equals(player.level().dimension())) {
                stardewPlayers.add(player);
            }
        }
        boolean anyPlayerInStardew = !stardewPlayers.isEmpty();
        
        // 没有玩家在星露谷相关维度时，跳过后续时间处理（UI、警告、晕倒等）
        if (!anyPlayerInStardew) {
            return;
        }

        long dayTime = virtualDayTime % 24000;
        
        // 从虚拟 MC 时间计算星露谷分钟
        int stardewMinutes = mcTimeToStardewMinutes(dayTime);
        
        // 更新TimeManager的时间（用于UI显示和其他系统）
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
            // 1. 对每个玩家执行晕倒惩罚（跳过已投睡觉票的玩家——他们选择了睡觉，不算晕倒；
            //    同时跳过尚未拥有农场的新玩家：他们没有农场出生点，强制传送会 fallback 到旧农场导致卡死）
            com.stardew.craft.farm.FarmInstanceRegistry farmRegistry =
                    com.stardew.craft.farm.FarmInstanceRegistry.get();
            for (ServerPlayer sp : stardewPlayers) {
                    if (!votedPlayers.contains(sp.getUUID()) && farmRegistry.hasFarm(sp.getUUID())) {
                        com.stardew.craft.player.PassOutService.on2AMPassOut(sp);
                    }
            }
            // 2. 推进到次日（内部会消费 PassOutResult 并合并进结算包发送给客户端）
            SleepVoteTracker.clearVotes(); // 2AM 强制推进，清空所有未完成的投票
            advanceToNextMorning(serverLevel, stardewMinutes, "pass_out_2am");
            // 3. 只传送未投票且已拥有农场的玩家回农场出生点（已投票玩家正常过夜；新玩家没有农场不应被传送）
            for (ServerPlayer sp : stardewPlayers) {
                    if (!votedPlayers.contains(sp.getUUID()) && farmRegistry.hasFarm(sp.getUUID())) {
                        com.stardew.craft.player.PassOutService.teleportToFarmSpawn(sp);
                    }
            }
        }
        
        // 每秒（20 ticks）同步UI时间+虚拟天空时间到客户端（仅发给星露谷维度玩家）
        // virtualDayTime 已包含在 TimeSyncPacket 中，客户端 StardewClientTimeState
        // 会每 tick 强制覆盖 ClientLevel.dayTime，不再需要 ClientboundSetTimePacket。
        if (serverLevel.getGameTime() % 20 == 0) {
            var syncPacket = TimeSyncPacket.fromTimeManager(timeManager);
            for (ServerPlayer sp : stardewPlayers) {
                PacketDistributor.sendToPlayer(sp, syncPacket);
            }
        }

        // 每5秒（100 ticks）验证并修复玩家附近的传送触发方块
        if (serverLevel.getGameTime() % 100 == 0) {
            for (ServerPlayer sp : stardewPlayers) {
                ServerLevel playerLevel = sp.serverLevel();
                InteriorSubspaceManager.verifyAndRepairNearby(playerLevel, sp);
            }
        }
    }
    
    /**
     * 监听睡觉完成事件。
     * 星露谷维度：触发日结算。
     * 主世界：补偿 offset，防止主世界睡觉推进影响星露谷虚拟时间。
     */
    @SuppressWarnings("null")
    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        if (level.dimension() == ModDimensions.STARDEW_VALLEY) {
            // 我们通过自定义投票系统处理星露谷维度的睡眠推进，
            // 忽略原版 SleepFinishedTimeEvent 防止双重推进。
            return;
        } else {
            // 主世界（或其他维度）睡觉完成 → 原版会推进 overworld dayTime。
            // 为保持星露谷虚拟时间不变，需反向补偿 offset。
            // SleepFinishedTimeEvent.getNewTime() 是原版将要设置的新 dayTime。
            StardewTimeManager timeManager = StardewTimeManager.get();
            long oldOverworldDayTime = level.getServer().overworld().getDayTime();
            long newOverworldDayTime = event.getNewTime();
            long delta = newOverworldDayTime - oldOverworldDayTime;
            if (delta != 0) {
                // offset -= delta，使 virtualDayTime = (newOverworldDayTime) + (offset - delta) 保持不变
                long currentOffset = timeManager.getDayTimeOffset();
                timeManager.setDayTimeOffsetRaw(currentOffset - delta);
            }
        }
    }

    /**
     * 立即更新MC时间（命令调用）— 通过 offset 隔离，不修改主世界
     */
    @SuppressWarnings("null")
    public static void updateMCTime(StardewTimeManager timeManager) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            long mcTimeOfDay = stardewMinutesToMcTime(timeManager.getCurrentTime());
            
            // 获取当前虚拟天数，用于计算新的虚拟绝对时间
            long currentVirtual = timeManager.getVirtualDayTime(server.overworld());
            long currentDay = currentVirtual / 24000;
            long newVirtualDayTime = currentDay * 24000 + mcTimeOfDay;
            
            // 修改 offset 而非 overworld dayTime
            timeManager.setVirtualDayTime(server.overworld(), newVirtualDayTime);
            
            // TimeSyncPacket 携带 virtualDayTime，客户端 StardewClientTimeState 自动同步。
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                ServerLevel playerLevel = player.serverLevel();
                if (isStardewDimension(playerLevel)) {
                    PacketDistributor.sendToPlayer(player, TimeSyncPacket.fromTimeManager(timeManager));
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

    // ── 分帧延迟初始化：把重方块操作分散到多个 tick，每 tick 只跑一个任务，防止 watchdog ──
    /** 标记是否已有待执行的初始化队列（避免多个玩家同时进入重复调度）。 */
    private static volatile boolean deferredInitScheduled = false;

    /**
     * 将 6 个 ensurePlaced / ensureInitialSpawn 操作排入 TickTask 队列，
     * 每 tick 执行一个，总共 6 tick 完成，不会卡主线程。
     *
     * <p>每个任务内部有 SavedData 版本号检查（placedVersion >= CURRENT_VERSION 则跳过），
     * 所以多次调度不会重复放置。这里的 deferredInitScheduled 只防止同一批多玩家
     * 并发触发时重复排队。
     */
    private static void scheduleDeferredInit(ServerLevel level) {
        if (deferredInitScheduled) return;
        deferredInitScheduled = true;

        var server = level.getServer();
        int baseTick = server.getTickCount() + 1;

        // 任务 1: 农场入口屏障（~731 方块）
        server.tell(new net.minecraft.server.TickTask(baseTick, () -> {
            try {
                ServerLevel sdv = server.getLevel(ModDimensions.STARDEW_VALLEY);
                if (sdv != null) {
                    com.stardew.craft.farm.FarmEntryBarrierManager.get(sdv).ensureBarriersPlaced(sdv);
                }
            } catch (Exception e) {
                StardewCraft.LOGGER.error("[DEFERRED_INIT] FarmEntryBarrier failed", e);
            }
        }));

        // 任务 2: 采石场屏障墙 + 传送触发（~2万方块，最重的操作）
        server.tell(new net.minecraft.server.TickTask(baseTick + 1, () -> {
            try {
                ServerLevel sdv = server.getLevel(ModDimensions.STARDEW_VALLEY);
                if (sdv != null) {
                    com.stardew.craft.communitycenter.quarry.QuarryAccessManager.get(sdv).ensurePlaced(sdv);
                }
            } catch (Exception e) {
                StardewCraft.LOGGER.error("[DEFERRED_INIT] QuarryAccess failed", e);
            }
        }));

        // 任务 3: 矿车站点实体 + 铁轨
        server.tell(new net.minecraft.server.TickTask(baseTick + 2, () -> {
            try {
                ServerLevel sdv = server.getLevel(ModDimensions.STARDEW_VALLEY);
                if (sdv != null) {
                    com.stardew.craft.minecart.MinecartStationManager.get(sdv).ensurePlaced(server);
                }
            } catch (Exception e) {
                StardewCraft.LOGGER.error("[DEFERRED_INIT] MinecartStation failed", e);
            }
        }));

        // 任务 4: 地面采集物初始化
        server.tell(new net.minecraft.server.TickTask(baseTick + 3, () -> {
            try {
                ServerLevel sdv = server.getLevel(ModDimensions.STARDEW_VALLEY);
                if (sdv != null) {
                    int season = com.stardew.craft.time.StardewTimeManager.get().getCurrentSeason();
                    com.stardew.craft.manager.ForageSpawnService.ensureInitialSpawn(sdv, season);
                    com.stardew.craft.manager.ArtifactSpotSpawnService.ensureInitialSpawn(sdv, season);
                    com.stardew.craft.manager.CoalForestClumpSpawnService.ensureInitialSpawn(sdv);
                }
            } catch (Exception e) {
                StardewCraft.LOGGER.error("[DEFERRED_INIT] Forage/Artifact failed", e);
            }
        }));

        // 任务 5: 采石场石头铺设（强制加载区块 + 5千格扫描）
        server.tell(new net.minecraft.server.TickTask(baseTick + 4, () -> {
            try {
                ServerLevel sdv = server.getLevel(ModDimensions.STARDEW_VALLEY);
                if (sdv != null) {
                    int year = com.stardew.craft.time.StardewTimeManager.get().getCurrentYear();
                    com.stardew.craft.manager.QuarrySpawnService.ensureInitialSpawn(sdv, year);
                }
            } catch (Exception e) {
                StardewCraft.LOGGER.error("[DEFERRED_INIT] QuarrySpawn failed", e);
            }
        }));

        // 任务 6: 清除调度标记
        server.tell(new net.minecraft.server.TickTask(baseTick + 5, () -> {
            deferredInitScheduled = false;
            StardewCraft.LOGGER.info("[DEFERRED_INIT] All deferred init tasks completed.");
        }));
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
