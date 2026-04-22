package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.core.ModMiningDimensions;
import com.stardew.craft.interior.InteriorSubspaceManager;
import com.stardew.craft.manager.CropGrowthManager;
import com.stardew.craft.mining.MineEntranceBootstrap;
import com.stardew.craft.mining.MineFloorGenerator;
import com.stardew.craft.mining.MiningCoordinates;
import com.stardew.craft.mining.MiningDataManager;
import com.stardew.craft.mining.MiningPlayerData;
import com.stardew.craft.network.MiningFloorSyncPacket;
import com.stardew.craft.network.TimeSyncPacket;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.tree.WildTrees;
import com.stardew.craft.tree.preset.TreePresetIO;
import com.stardew.craft.weather.WeatherManager;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 星露谷主命令 - 整合传送、树、时间、天气等子系统
 */
public class StardewTeleportCommand {
    
    @SuppressWarnings("null")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("stardew")
                // 注意：不要在 "stardew" 根节点加 .requires()，
                // 否则 FarmJoinCommand 的 accept/reject 也会被阻断。
                // 各子命令自行限制权限。
                // 传送相关
                .then(CommandTargets.executesWithTarget(
                    Commands.literal("tp")
                        .requires(source -> source.hasPermission(2))
                        // 兼容旧用法：/stardew tp 依然等价于 main
                        .then(CommandTargets.executesWithTarget(
                            Commands.literal("main"),
                            StardewTeleportCommand::teleportToStardew))
                        .then(CommandTargets.executesWithTarget(
                            Commands.literal("mine"),
                            StardewTeleportCommand::teleportToMine))
                        .then(CommandTargets.executesWithTarget(
                            Commands.literal("desert_mine"),
                            StardewTeleportCommand::teleportToDesertMine)),
                    StardewTeleportCommand::teleportToStardew))
                .then(CommandTargets.executesWithTarget(
                    Commands.literal("return")
                        .requires(source -> source.hasPermission(2)),
                    StardewTeleportCommand::returnToOverworld))
                .then(Commands.literal("mine")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.literal("set_floor")
                        .then(CommandTargets.executesWithTarget(
                            Commands.argument("floor", IntegerArgumentType.integer(0)),
                            StardewTeleportCommand::setMineFloor))
                    )
                    .then(Commands.literal("set_skull_floor")
                        .then(CommandTargets.executesWithTarget(
                            Commands.argument("floor", IntegerArgumentType.integer(1)),
                            StardewTeleportCommand::setDesertMineFloor))
                    )
                )
                .then(Commands.literal("interior")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.literal("ensure")
                        .executes(StardewTeleportCommand::ensureInteriorLoaded)
                    )
                    .then(Commands.literal("reload")
                        .executes(StardewTeleportCommand::forceReloadInterior)
                    )
                    .then(CommandTargets.executesWithTarget(
                        Commands.literal("tp_origin"),
                        StardewTeleportCommand::teleportToInteriorOrigin))
                    .then(CommandTargets.executesWithTarget(
                        Commands.literal("tp_spawn"),
                        StardewTeleportCommand::teleportToInteriorSpawn))
                )
                // 树预制导出
                .then(Commands.literal("tree")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("preset")
                                .then(Commands.literal("export")
                            .then(Commands.argument("name", StringArgumentType.string())
                                .then(Commands.argument("from", BlockPosArgument.blockPos())
                                    .then(Commands.argument("to", BlockPosArgument.blockPos())
                                        .then(Commands.argument("origin", BlockPosArgument.blockPos())
                                            .executes(ctx -> exportTreePreset(ctx, false))
                                            .then(Commands.argument("overwrite", BoolArgumentType.bool())
                                                .executes(ctx -> exportTreePreset(ctx, BoolArgumentType.getBool(ctx, "overwrite")))
                                            )
                                        )
                                    )
                                )
                            )
                                )
                        )
                )
                // 时间管理
                .then(Commands.literal("time")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.literal("get")
                        .executes(StardewTeleportCommand::getTime))
                    .then(Commands.literal("set")
                        .then(Commands.literal("time")
                            .then(Commands.argument("time", IntegerArgumentType.integer(0, 1560))
                                .executes(StardewTeleportCommand::setTime)))
                        .then(Commands.literal("date")
                            .then(Commands.argument("day", IntegerArgumentType.integer(1, 28))
                                .executes(StardewTeleportCommand::setDate)))
                        .then(Commands.literal("season")
                            .then(Commands.literal("spring").executes(ctx -> setSeason(ctx, 0)))
                            .then(Commands.literal("summer").executes(ctx -> setSeason(ctx, 1)))
                            .then(Commands.literal("fall").executes(ctx -> setSeason(ctx, 2)))
                            .then(Commands.literal("winter").executes(ctx -> setSeason(ctx, 3))))
                        .then(Commands.literal("year")
                            .then(Commands.argument("year_val", IntegerArgumentType.integer(1))
                                .executes(StardewTeleportCommand::setYear))))
                    .then(Commands.literal("add")
                        .then(Commands.argument("minutes", IntegerArgumentType.integer(1))
                            .executes(StardewTeleportCommand::addTime)))
                    .then(Commands.literal("newday")
                        .executes(StardewTeleportCommand::newDay))
                    .then(Commands.literal("reset")
                        .executes(StardewTeleportCommand::resetTime)))
                // 天气管理
                .then(Commands.literal("weather")
                    .requires(source -> source.hasPermission(2))
                    .then(Commands.literal("get")
                        .executes(StardewTeleportCommand::getWeather))
                    .then(Commands.literal("set")
                        .then(Commands.literal("sun").executes(ctx -> setWeather(ctx, "Sun")))
                        .then(Commands.literal("rain").executes(ctx -> setWeather(ctx, "Rain")))
                        .then(Commands.literal("storm").executes(ctx -> setWeather(ctx, "Storm")))
                        .then(Commands.literal("snow").executes(ctx -> setWeather(ctx, "Snow")))
                        .then(Commands.literal("windspring").executes(ctx -> setWeather(ctx, "WindSpring")))
                        .then(Commands.literal("windfall").executes(ctx -> setWeather(ctx, "WindFall")))
                        .then(Commands.literal("festival").executes(ctx -> setWeather(ctx, "Festival"))))
                    .then(Commands.literal("tomorrow")
                        .executes(StardewTeleportCommand::getTomorrowWeather))
                    .then(Commands.literal("test")
                        .executes(StardewTeleportCommand::testWeatherProbability))
                    .then(Commands.literal("diagnose")
                        .executes(StardewTeleportCommand::diagnoseRain)))
        );
    }

    @SuppressWarnings("null")
    private static int exportTreePreset(CommandContext<CommandSourceStack> context, boolean overwrite) {
        ServerLevel level;
        try {
            level = context.getSource().getLevel();
        } catch (Exception e) {
            sendFailureMsg(context, "只能在服务器世界中使用该命令");
            return 0;
        }

        String rawName = StringArgumentType.getString(context, "name");
        String fileName = sanitizePresetName(rawName);
        if (fileName.isBlank()) {
            sendFailureMsg(context, "name 不能为空");
            return 0;
        }

        BlockPos from = BlockPosArgument.getBlockPos(context, "from");
        BlockPos to = BlockPosArgument.getBlockPos(context, "to");
        BlockPos origin = BlockPosArgument.getBlockPos(context, "origin");

        // 确保选区相关坐标已加载
        if (!level.isLoaded(from) || !level.isLoaded(to) || !level.isLoaded(origin)) {
            sendFailureMsg(context, "from/to/origin 所在区块必须已加载（请先靠近选区再执行）");
            return 0;
        }

        @SuppressWarnings("null")
        BlockState originState = level.getBlockState(origin);
        WildTrees.Def def = WildTrees.findByTrunk0(originState);
        if (def == null) {
            sendFailureMsg(context, "origin 必须是任意野生树的 trunk0 方块（你给的 origin: " + origin + "）");
            return 0;
        }

        int minX = Math.min(from.getX(), to.getX());
        int minY = Math.min(from.getY(), to.getY());
        int minZ = Math.min(from.getZ(), to.getZ());
        int maxX = Math.max(from.getX(), to.getX());
        int maxY = Math.max(from.getY(), to.getY());
        int maxZ = Math.max(from.getZ(), to.getZ());

        long volume = (long) (maxX - minX + 1) * (long) (maxY - minY + 1) * (long) (maxZ - minZ + 1);
        if (volume > 200000) {
            sendFailureMsg(context, "选区太大: " + volume + " 方块（上限 200000），请缩小 from/to");
            return 0;
        }

        var preset = com.stardew.craft.tree.preset.TreePresetExporter.export(level, def, from, to, origin, fileName);
        if (preset == null) {
            sendFailureMsg(context, "导出失败：选区内没有任何属于该树种的方块");
            return 0;
        }

        boolean ok = TreePresetIO.writePreset(fileName, preset, overwrite);
        if (!ok && !overwrite) {
            sendFailureMsg(context, "文件已存在，未覆盖：" + TreePresetIO.presetPathByName(fileName).toAbsolutePath() + "（加参数 overwrite=true 可覆盖）");
            return 0;
        }

        context.getSource().sendSuccess(
                () -> Component.literal(
                        "已导出预制树: " + TreePresetIO.presetPathByName(fileName).toAbsolutePath()
                                + " | tree=" + def.id() + " | origin=" + origin
                ),
                false
        );
        return 1;
    }

    private static String sanitizePresetName(String raw) {
        if (raw == null) {
            return "";
        }
        String s = raw.trim();
        // 只允许文件名安全字符，其他全部替换为 _
        s = s.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (s.endsWith(".json")) {
            s = s.substring(0, s.length() - 5);
        }
        return s;
    }
    
    /**
     * 传送到星露谷维度
     */
    private static int teleportToStardew(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = CommandTargets.resolve(context);
            if (player == null) {
                sendFailureMsg(context, "需要指定目标玩家或由玩家执行");
                return 0;
            }
            @SuppressWarnings("null")
            ServerLevel stardewLevel = context.getSource().getServer()
                .getLevel(ModDimensions.STARDEW_VALLEY);
            
            if (stardewLevel == null) {
                sendFailureMsg(context, "星露谷维度未加载！");
                return 0;
            }

            InteriorSubspaceManager.ensureLoaded(stardewLevel, "tp_stardew");
            
            // 传送到星露谷维度的出生点（可以后续改为农场位置）
            BlockPos targetPos = new BlockPos(0, 70, 0);
            
            player.teleportTo(stardewLevel, 
                targetPos.getX() + 0.5, 
                targetPos.getY(), 
                targetPos.getZ() + 0.5,
                player.getYRot(), 
                player.getXRot()
            );
            
            context.getSource().sendSuccess(
                () -> Component.literal("欢迎来到星露谷！"),
                false
            );
            
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "传送失败: " + e.getMessage());
            return 0;
        }
    }

    private static int ensureInteriorLoaded(CommandContext<CommandSourceStack> context) {
        try {
            @SuppressWarnings("null")
            ServerLevel stardewLevel = context.getSource().getServer().getLevel(ModDimensions.STARDEW_VALLEY);
            if (stardewLevel == null) {
                sendFailureMsg(context, "星露谷维度未加载！");
                return 0;
            }

            InteriorSubspaceManager.forceReload(stardewLevel, "manual_command_force_reload");
            context.getSource().sendSuccess(
                () -> Component.literal("已强制重载室内结构/交互体（manual_command_force_reload）"),
                false
            );
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "触发室内加载失败: " + e.getMessage());
            return 0;
        }
    }

    private static int forceReloadInterior(CommandContext<CommandSourceStack> context) {
        try {
            @SuppressWarnings("null")
            ServerLevel stardewLevel = context.getSource().getServer().getLevel(ModDimensions.STARDEW_VALLEY);
            if (stardewLevel == null) {
                sendFailureMsg(context, "星露谷维度未加载！");
                return 0;
            }

            InteriorSubspaceManager.forceReload(stardewLevel, "manual_force_reload");
            context.getSource().sendSuccess(
                () -> Component.literal("已强制重载室内结构（manual_force_reload）"),
                false
            );
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "强制重载室内失败: " + e.getMessage());
            return 0;
        }
    }

    private static int teleportToInteriorOrigin(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = CommandTargets.resolve(context);
            if (player == null) {
                sendFailureMsg(context, "需要指定目标玩家或由玩家执行");
                return 0;
            }
            @SuppressWarnings("null")
            ServerLevel stardewLevel = context.getSource().getServer().getLevel(ModDimensions.STARDEW_VALLEY);
            if (stardewLevel == null) {
                sendFailureMsg(context, "星露谷维度未加载！");
                return 0;
            }

            InteriorSubspaceManager.ensureLoaded(stardewLevel, "manual_tp_origin");
            player.teleportTo(stardewLevel, 12032.5D, 70.0D, 12032.5D, player.getYRot(), player.getXRot());
            context.getSource().sendSuccess(() -> Component.literal("已传送到室内结构原点附近: 12032 70 12032"), false);
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "传送室内原点失败: " + e.getMessage());
            return 0;
        }
    }

    private static int teleportToInteriorSpawn(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = CommandTargets.resolve(context);
            if (player == null) {
                sendFailureMsg(context, "需要指定目标玩家或由玩家执行");
                return 0;
            }
            @SuppressWarnings("null")
            ServerLevel stardewLevel = context.getSource().getServer().getLevel(ModDimensions.STARDEW_VALLEY);
            if (stardewLevel == null) {
                sendFailureMsg(context, "星露谷维度未加载！");
                return 0;
            }

            InteriorSubspaceManager.ensureLoaded(stardewLevel, "manual_tp_spawn");
            player.teleportTo(stardewLevel, 12038.5D, 71.0D, 12038.5D, -90.0F, 0.0F);
            context.getSource().sendSuccess(() -> Component.literal("已传送到室内落点: 12038 71 12038（朝东）"), false);
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "传送室内落点失败: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 传送到矿井维度（调试用）
     * - 会确保入口大厅结构只生成一次
     * - 落点按大厅 schem 偏移固定到 (21,66,3)
     */
    private static int teleportToMine(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = CommandTargets.resolve(context);
            if (player == null) {
                sendFailureMsg(context, "需要指定目标玩家或由玩家执行");
                return 0;
            }
            @SuppressWarnings("null")
            ServerLevel mineLevel = context.getSource().getServer().getLevel(ModMiningDimensions.STARDEW_MINING);

            if (mineLevel == null) {
                sendFailureMsg(context, "矿井维度未加载！请确认 data/stardewcraft/dimension/stardew_mining.json 已存在且 datapack 已加载。");
                return 0;
            }

            MineEntranceBootstrap.ensureGenerated(mineLevel);
            player.teleportTo(mineLevel, 21.5, 66.0, 3.5, player.getYRot(), player.getXRot());

            context.getSource().sendSuccess(
                () -> Component.literal("已传送到矿井入口（如果是第一次进入，会自动生成大厅）"),
                false
            );
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "传送到矿井失败: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 传送到骷髅矿洞入口（floor 121 的安全区）
     */
    private static int teleportToDesertMine(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = CommandTargets.resolve(context);
            if (player == null) {
                sendFailureMsg(context, "需要指定目标玩家或由玩家执行");
                return 0;
            }
            @SuppressWarnings("null")
            ServerLevel mineLevel = context.getSource().getServer().getLevel(ModMiningDimensions.STARDEW_MINING);
            if (mineLevel == null) {
                sendFailureMsg(context, "矿井维度未加载！");
                return 0;
            }

            // 骷髅矿洞入口 = floor 121 safe zone
            int floor = 121;
            int floorZ = floor * com.stardew.craft.mining.MiningCoordinates.FLOOR_SPACING + 14;
            double safeY = 66.0;

            // 确保 floor 121 已生成
            com.stardew.craft.mining.MineFloorGenerator.generateFloor(mineLevel, floor);

            player.teleportTo(mineLevel, 0.5, safeY, floorZ + 0.5, player.getYRot(), player.getXRot());

            context.getSource().sendSuccess(
                () -> Component.literal("已传送到骷髅矿洞入口 (Floor 121)"),
                false
            );
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "传送到骷髅矿洞失败: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 直接设置矿井楼层并传送（调试/管理）
     * 用法：/stardew mine set_floor <floor>
     */
    @SuppressWarnings("null")
    private static int setMineFloor(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = CommandTargets.resolve(context);
            if (player == null) {
                sendFailureMsg(context, "需要指定目标玩家或由玩家执行");
                return 0;
            }
            int targetFloor = IntegerArgumentType.getInteger(context, "floor");

            @SuppressWarnings("null")
            ServerLevel mineLevel = context.getSource().getServer().getLevel(ModMiningDimensions.STARDEW_MINING);
            if (mineLevel == null) {
                sendFailureMsg(context, "矿井维度未加载！请确认 data/stardewcraft/dimension/stardew_mining.json 已存在且 datapack 已加载。");
                return 0;
            }

            MineEntranceBootstrap.ensureGenerated(mineLevel);
            if (targetFloor > 0) {
                MineFloorGenerator.generateFloor(mineLevel, targetFloor);
            }

            MiningPlayerData playerData = MiningDataManager.getPlayerData(player);
            playerData.setCurrentFloor(targetFloor);
            MiningDataManager.savePlayerData(player, playerData);

            MiningCoordinates.teleportPlayerToFloor(player, mineLevel, targetFloor);
            PacketDistributor.sendToPlayer(player, new MiningFloorSyncPacket(targetFloor));

            context.getSource().sendSuccess(
                () -> Component.literal("已传送到矿井第 " + targetFloor + " 层"),
                false
            );
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "设置矿井层数失败: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 设置骷髅矿井楼层（SDV 相对层，1 = 内部 floor 121）。
     * 用法：/stardew desert_mine set_floor <n>
     */
    @SuppressWarnings("null")
    private static int setDesertMineFloor(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = CommandTargets.resolve(context);
            if (player == null) {
                sendFailureMsg(context, "需要指定目标玩家或由玩家执行");
                return 0;
            }
            int relative = IntegerArgumentType.getInteger(context, "floor");
            int targetFloor = 120 + relative; // 骷髅矿井 1 -> 内部 121

            ServerLevel mineLevel = context.getSource().getServer().getLevel(ModMiningDimensions.STARDEW_MINING);
            if (mineLevel == null) {
                sendFailureMsg(context, "矿井维度未加载！");
                return 0;
            }

            MineEntranceBootstrap.ensureGenerated(mineLevel);
            MineFloorGenerator.generateFloor(mineLevel, targetFloor);

            MiningPlayerData playerData = MiningDataManager.getPlayerData(player);
            playerData.setCurrentFloor(targetFloor);
            MiningDataManager.savePlayerData(player, playerData);

            MiningCoordinates.teleportPlayerToFloor(player, mineLevel, targetFloor);
            PacketDistributor.sendToPlayer(player, new MiningFloorSyncPacket(targetFloor));

            final int displayFloor = relative;
            final int internal = targetFloor;
            context.getSource().sendSuccess(
                () -> Component.literal("已传送到骷髅矿井第 " + displayFloor + " 层（内部 floor " + internal + "）"),
                false
            );
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "设置骷髅矿井层数失败: " + e.getMessage());
            return 0;
        }
    }
    private static int returnToOverworld(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = CommandTargets.resolve(context);
            if (player == null) {
                sendFailureMsg(context, "需要指定目标玩家或由玩家执行");
                return 0;
            }
            @SuppressWarnings("null")
            ServerLevel overworld = context.getSource().getServer()
                .getLevel(Level.OVERWORLD);
            
            if (overworld == null) {
                sendFailureMsg(context, "主世界未加载！");
                return 0;
            }
            
            // 返回到主世界出生点
            BlockPos spawnPos = overworld.getSharedSpawnPos();
            
            player.teleportTo(overworld,
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                player.getYRot(),
                player.getXRot()
            );
            
            context.getSource().sendSuccess(
                () -> Component.literal("已返回主世界"),
                false
            );
            
            return 1;
        } catch (Exception e) {
            sendFailureMsg(context, "传送失败: " + e.getMessage());
            return 0;
        }
    }
    
    @SuppressWarnings("null")
    private static void sendFailureMsg(CommandContext<CommandSourceStack> context, String message) {
        context.getSource().sendFailure(Component.literal(message));
    }
    
    // ================== 时间管理方法 ==================
    
    @SuppressWarnings("null")
    private static int getTime(CommandContext<CommandSourceStack> context) {
        StardewTimeManager time = StardewTimeManager.get();
        
        context.getSource().sendSuccess(() -> 
            Component.literal(String.format(
                "当前时间: %s | %s 第%d天, 第%d年 | 游戏时间: %d分钟",
                time.getFormattedTime12Hour(),
                time.getSeasonName(),
                time.getCurrentDay(),
                time.getCurrentYear(),
                time.getCurrentTime()
            )),
            false
        );
        
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int setDate(CommandContext<CommandSourceStack> context) {
        int day = IntegerArgumentType.getInteger(context, "day");
        StardewTimeManager time = StardewTimeManager.get();
        time.setCurrentDay(day);
        syncTime(time);
        
        context.getSource().sendSuccess(() ->
            Component.literal("日期已设置为: 第" + day + "天"),
            true
        );
        return 1;
    }

    @SuppressWarnings("null")
    private static int setSeason(CommandContext<CommandSourceStack> context, int season) {
        StardewTimeManager time = StardewTimeManager.get();
        time.setCurrentSeason(season);
        syncTime(time);

        // 改季节后立即让非当季作物枯萎（已加载区块内）
        var server = context.getSource().getServer();
        if (server != null) {
            ServerLevel stardewLevel = server.getLevel(ModDimensions.STARDEW_VALLEY);
            if (stardewLevel != null) {
                CropGrowthManager.get(stardewLevel).killOutOfSeasonLoaded(stardewLevel);
            }
        }
        
        context.getSource().sendSuccess(() ->
            Component.literal("季节已设置为: " + time.getSeasonName()),
            true
        );
        return 1;
    }

    @SuppressWarnings("null")
    private static int setYear(CommandContext<CommandSourceStack> context) {
        int year = IntegerArgumentType.getInteger(context, "year_val");
        StardewTimeManager time = StardewTimeManager.get();
        time.setCurrentYear(year);
        syncTime(time);
        
        context.getSource().sendSuccess(() ->
            Component.literal("年份已设置为: 第" + year + "年"),
            true
        );
        return 1;
    }

    @SuppressWarnings("null")
    private static void syncTime(StardewTimeManager time) {
        // 立即更新MC时间
        com.stardew.craft.event.DimensionEventHandler.updateMCTime(time);
        // 同步到客户端
        PacketDistributor.sendToAllPlayers(TimeSyncPacket.fromTimeManager(time));
    }

    @SuppressWarnings("null")
    private static int setTime(CommandContext<CommandSourceStack> context) {
        int newTime = IntegerArgumentType.getInteger(context, "time");
        StardewTimeManager time = StardewTimeManager.get();
        time.setCurrentTime(newTime);
        syncTime(time);
        
        context.getSource().sendSuccess(() ->
            Component.literal("时间已设置为: " + time.getFormattedTime12Hour()),
            true
        );
        
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int addTime(CommandContext<CommandSourceStack> context) {
        int minutes = IntegerArgumentType.getInteger(context, "minutes");
        StardewTimeManager time = StardewTimeManager.get();
        time.setCurrentTime(time.getCurrentTime() + minutes);
        
        syncTime(time);
        
        context.getSource().sendSuccess(() ->
            Component.literal(String.format("增加了%d分钟，当前时间: %s", 
                minutes, time.getFormattedTime12Hour())),
            true
        );
        
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int newDay(CommandContext<CommandSourceStack> context) {
        StardewTimeManager time = StardewTimeManager.get();
        time.advanceDay();
        
        PacketDistributor.sendToAllPlayers(TimeSyncPacket.fromTimeManager(time));
        
        context.getSource().sendSuccess(() ->
            Component.literal(String.format("新的一天！%s 第%d天",
                time.getSeasonName(), time.getCurrentDay())),
            true
        );
        
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int resetTime(CommandContext<CommandSourceStack> context) {
        StardewTimeManager time = StardewTimeManager.get();
        time.setCurrentTime(600); // 重置到6:00 AM
        time.setCurrentDay(1);
        time.setCurrentSeason(0); // Spring
        time.setCurrentYear(1);
        
        com.stardew.craft.event.DimensionEventHandler.updateMCTime(time);
        PacketDistributor.sendToAllPlayers(TimeSyncPacket.fromTimeManager(time));
        
        context.getSource().sendSuccess(() ->
            Component.literal("时间已重置到 Spring 1, Year 1, 6:00 AM"),
            true
        );
        
        return 1;
    }
    
    // ================== 天气管理方法 ==================
    
    @SuppressWarnings("null")
    private static int getWeather(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            context.getSource().sendFailure(Component.literal("§c此命令只能在星露谷维度使用！"));
            return 0;
        }
        
        String currentWeather = WeatherManager.getCurrentWeather(level);
        String tomorrowWeather = WeatherManager.getTomorrowWeather(level);
        
        StardewTimeManager timeManager = StardewTimeManager.get();
        String season = timeManager.getSeasonName();
        int day = timeManager.getCurrentDay();
        
        context.getSource().sendSuccess(() -> 
            Component.literal(String.format(
                "§e=== 星露谷天气信息 ===\n" +
                "§b当前日期: §f%s 第%d天\n" +
                "§b今天天气: §f%s §7(%s)\n" +
                "§b明天天气: §f%s §7(%s)\n" +
                "§b原版天气: §f%s, 雷暴: %s",
                season, day,
                currentWeather, getWeatherDisplayName(currentWeather),
                tomorrowWeather, getWeatherDisplayName(tomorrowWeather),
                level.isRaining() ? "下雨" : "晴朗",
                level.isThundering() ? "是" : "否"
            )),
            false
        );
        
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int setWeather(CommandContext<CommandSourceStack> context, String weatherType) {
        ServerLevel level = context.getSource().getLevel();
        
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            context.getSource().sendFailure(Component.literal("§c此命令只能在星露谷维度使用！"));
            return 0;
        }
        
        WeatherManager.setWeather(level, weatherType);
        
        context.getSource().sendSuccess(() -> 
            Component.literal(String.format(
                "§a天气已设置为: %s §7(%s)\n§7MC天气: %s, 雷暴: %s",
                weatherType,
                getWeatherDisplayName(weatherType),
                level.isRaining() ? "下雨" : "晴朗",
                level.isThundering() ? "是" : "否"
            )),
            true
        );
        
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int getTomorrowWeather(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        
        if (!ModDimensions.STARDEW_VALLEY.equals(level.dimension())) {
            context.getSource().sendFailure(Component.literal("§c此命令只能在星露谷维度使用！"));
            return 0;
        }
        
        String tomorrowWeather = WeatherManager.getTomorrowWeather(level);
        
        context.getSource().sendSuccess(() -> 
            Component.literal(String.format(
                "§b明天的天气预测: §f%s §7(%s)",
                tomorrowWeather,
                getWeatherDisplayName(tomorrowWeather)
            )),
            false
        );
        
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int testWeatherProbability(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        
        if (level.dimension() != ModDimensions.STARDEW_VALLEY) {
            context.getSource().sendFailure(Component.literal("§c此命令只能在星露谷维度使用！"));
            return 0;
        }
        
        StardewTimeManager timeManager = StardewTimeManager.get();
        String currentSeason = timeManager.getSeasonName();
        
        // 模拟100天的天气
        int[] weatherCount = new int[7];
        String[] weatherNames = {"Sun", "Rain", "Storm", "Snow", "WindSpring", "WindFall", "Festival"};
        
        java.util.Random random = new java.util.Random(level.getSeed());
        for (int i = 0; i < 100; i++) {
            String weather = WeatherManager.predictTomorrowWeather(level, currentSeason, i % 28 + 1, random);
            for (int j = 0; j < weatherNames.length; j++) {
                if (weatherNames[j].equals(weather)) {
                    weatherCount[j]++;
                    break;
                }
            }
        }
        
        StringBuilder result = new StringBuilder("§e=== 天气概率测试（模拟100天）===\n");
        result.append(String.format("§b当前季节: §f%s\n\n", currentSeason));
        
        for (int i = 0; i < weatherNames.length; i++) {
            if (weatherCount[i] > 0) {
                result.append(String.format("§f%s: §a%d%% §7(%s)\n",
                    weatherNames[i],
                    weatherCount[i],
                    getWeatherDisplayName(weatherNames[i])
                ));
            }
        }
        
        String finalResult = result.toString();
        context.getSource().sendSuccess(() -> Component.literal(finalResult), false);
        
        return 1;
    }
    
    @SuppressWarnings("null")
    private static int diagnoseRain(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(Component.literal("\u00a7c\u6b64\u547d\u4ee4\u9700\u8981\u73a9\u5bb6\u6267\u884c"));
            return 0;
        }

        BlockPos playerPos = player.blockPosition();
        int hmMotionBlocking = level.getHeight(Heightmap.Types.MOTION_BLOCKING, playerPos.getX(), playerPos.getZ());
        int hmWorldSurface = level.getHeight(Heightmap.Types.WORLD_SURFACE, playerPos.getX(), playerPos.getZ());
        boolean canSeeSky = level.canSeeSky(playerPos);
        boolean isRaining = level.isRaining();
        boolean isRainingAtPlayer = level.isRainingAt(playerPos);
        boolean isRainingAtAbove = level.isRainingAt(playerPos.above());
        float rainLevel = level.getRainLevel(0f);
        float thunderLevel = level.getThunderLevel(0f);

        var biomeHolder = level.getBiome(playerPos);
        String biomeName = biomeHolder.unwrapKey()
                .map(k -> k.location().toString())
                .orElse("unknown");
        boolean hasPrecip = biomeHolder.value().hasPrecipitation();
        Biome.Precipitation precipType = biomeHolder.value().getPrecipitationAt(playerPos);

        String stardewWeather = WeatherManager.getCurrentWeather(level);
        boolean weatherCycleEnabled = level.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE);

        String msg = String.format(
            "\u00a7e=== \u5929\u6c14\u6e32\u67d3\u8bca\u65ad ===\n" +
            "\u00a7bStardew\u5929\u6c14: \u00a7f%s\n" +
            "\u00a7bMC isRaining: \u00a7f%s  \u00a7bthundering: \u00a7f%s\n" +
            "\u00a7brainLevel: \u00a7f%.3f  \u00a7bthunderLevel: \u00a7f%.3f\n" +
            "\u00a7bdoWeatherCycle: \u00a7f%s\n" +
            "\u00a77--- \u73a9\u5bb6\u4f4d\u7f6e ---\n" +
            "\u00a7bPos: \u00a7f%d, %d, %d\n" +
            "\u00a7bHeightmap MOTION_BLOCKING: \u00a7f%d  \u00a7bWORLD_SURFACE: \u00a7f%d\n" +
            "\u00a7bcanSeeSky: \u00a7f%s\n" +
            "\u00a7bisRainingAt(pos): \u00a7f%s  \u00a7bisRainingAt(pos.above): \u00a7f%s\n" +
            "\u00a77--- \u751f\u7269\u7fa4\u7cfb ---\n" +
            "\u00a7bBiome: \u00a7f%s\n" +
            "\u00a7bhasPrecipitation: \u00a7f%s  \u00a7bprecipType: \u00a7f%s",
            stardewWeather,
            isRaining, level.isThundering(),
            rainLevel, thunderLevel,
            weatherCycleEnabled,
            playerPos.getX(), playerPos.getY(), playerPos.getZ(),
            hmMotionBlocking, hmWorldSurface,
            canSeeSky,
            isRainingAtPlayer, isRainingAtAbove,
            biomeName,
            hasPrecip, precipType
        );

        context.getSource().sendSuccess(() -> Component.literal(msg), false);
        return 1;
    }

    private static String getWeatherDisplayName(String weatherType) {
        return switch (weatherType) {
            case "Sun" -> "晴天";
            case "Rain" -> "雨天";
            case "Storm" -> "暴风雨";
            case "Snow" -> "雪天";
            case "WindSpring" -> "春季有风";
            case "WindFall" -> "秋季有风";
            case "Festival" -> "节日晴天";
            default -> "未知";
        };
    }
}
