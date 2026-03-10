package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.manager.CropGrowthManager;
import com.stardew.craft.network.TimeSyncPacket;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 时间调试命令
 */
public class TimeDebugCommand {
    
    @SuppressWarnings("null")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("stardewtime")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("get")
                    .executes(TimeDebugCommand::getTime)
                )
                .then(Commands.literal("set")
                    .then(Commands.literal("time")
                        .then(Commands.argument("time", IntegerArgumentType.integer(0, 1560))
                            .executes(TimeDebugCommand::setTime)
                        )
                    )
                    .then(Commands.literal("date")
                        .then(Commands.argument("day", IntegerArgumentType.integer(1, 28))
                            .executes(TimeDebugCommand::setDate)
                        )
                    )
                    .then(Commands.literal("season")
                        .then(Commands.literal("spring").executes(ctx -> setSeason(ctx, 0)))
                        .then(Commands.literal("summer").executes(ctx -> setSeason(ctx, 1)))
                        .then(Commands.literal("fall").executes(ctx -> setSeason(ctx, 2)))
                        .then(Commands.literal("winter").executes(ctx -> setSeason(ctx, 3)))
                    )
                    .then(Commands.literal("year")
                        .then(Commands.argument("year_val", IntegerArgumentType.integer(1))
                            .executes(TimeDebugCommand::setYear)
                        )
                    )
                )
                .then(Commands.literal("add")
                    .then(Commands.argument("minutes", IntegerArgumentType.integer(1))
                        .executes(TimeDebugCommand::addTime)
                    )
                )
                .then(Commands.literal("newday")
                    .executes(TimeDebugCommand::newDay)
                )
                .then(Commands.literal("reset")
                    .executes(TimeDebugCommand::resetTime)
                )
        );
    }
    
    @SuppressWarnings("null")
    private static int getTime(CommandContext<CommandSourceStack> context) {
        StardewTimeManager time = StardewTimeManager.get();
        
        boolean ignored = false;
        if (!ignored) {
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
        }
        
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
        // 更新所有作物的状态（如果在加载的区块中）
        // TODO: 考虑是否需要强制tick作物，或者让下次作物检查时自动销毁非季节作物
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
        
        // 同步到客户端
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
        
        // 立即更新MC时间
        com.stardew.craft.event.DimensionEventHandler.updateMCTime(time);
        
        // 同步到客户端
        PacketDistributor.sendToAllPlayers(TimeSyncPacket.fromTimeManager(time));
        
        context.getSource().sendSuccess(() ->
            Component.literal("时间已重置到 Spring 1, Year 1, 6:00 AM"),
            true
        );
        
        return 1;
    }
}
