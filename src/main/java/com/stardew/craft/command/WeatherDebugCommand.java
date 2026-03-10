package com.stardew.craft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.time.StardewTimeManager;
import com.stardew.craft.weather.WeatherManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/**
 * 天气调试命令
 * /stardewweather - 查看所有天气命令
 */
public class WeatherDebugCommand {
    
    @SuppressWarnings("null")
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("stardewweather")
                .requires(source -> source.hasPermission(2))
                
                // 查询当前天气
                .then(Commands.literal("get")
                    .executes(WeatherDebugCommand::getWeather)
                )
                
                // 设置天气
                .then(Commands.literal("set")
                    .then(Commands.literal("sun").executes(ctx -> setWeather(ctx, "Sun")))
                    .then(Commands.literal("rain").executes(ctx -> setWeather(ctx, "Rain")))
                    .then(Commands.literal("storm").executes(ctx -> setWeather(ctx, "Storm")))
                    .then(Commands.literal("snow").executes(ctx -> setWeather(ctx, "Snow")))
                    .then(Commands.literal("windspring").executes(ctx -> setWeather(ctx, "WindSpring")))
                    .then(Commands.literal("windfall").executes(ctx -> setWeather(ctx, "WindFall")))
                    .then(Commands.literal("festival").executes(ctx -> setWeather(ctx, "Festival")))
                )
                
                // 测试天气概率（模拟100天）
                .then(Commands.literal("test")
                    .executes(WeatherDebugCommand::testWeatherProbability)
                )
                
                // 查看明天天气预测
                .then(Commands.literal("tomorrow")
                    .executes(WeatherDebugCommand::getTomorrowWeather)
                )
                
                // 显示帮助
                .then(Commands.literal("help")
                    .executes(WeatherDebugCommand::showHelp)
                )
        );
    }
    
    /**
     * 获取当前天气
     */
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
    
    /**
     * 设置天气
     */
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
                "§a天气已设置为: %s §7(%s)",
                weatherType,
                getWeatherDisplayName(weatherType)
            )),
            true
        );
        
        return 1;
    }
    
    /**
     * 获取明天的天气预测
     */
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
    
    /**
     * 测试天气概率分布（模拟100天）
     */
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
        int[] weatherCount = new int[7]; // Sun, Rain, Storm, Snow, WindSpring, WindFall, Festival
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
    
    /**
     * 显示帮助信息
     */
    @SuppressWarnings("null")
    private static int showHelp(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> 
            Component.literal(
                "§e=== 星露谷天气系统指令 ===\n" +
                "§b/stardewweather get §f- 查看当前天气信息\n" +
                "§b/stardewweather set <天气> §f- 设置天气\n" +
                "  §7可用天气: sun, rain, storm, snow, windspring, windfall, festival\n" +
                "§b/stardewweather tomorrow §f- 查看明天的天气预测\n" +
                "§b/stardewweather test §f- 测试当前季节的天气概率分布\n" +
                "§b/stardewweather help §f- 显示此帮助信息\n\n" +
                "§7天气类型说明:\n" +
                "§f- Sun (晴天): 默认天气\n" +
                "§f- Rain (雨天): MC原版下雨\n" +
                "§f- Storm (雷暴): MC原版雷暴\n" +
                "§f- Snow (雪天): MC原版下雨（冬季显示为雪）\n" +
                "§f- WindSpring (春季微风): 樱花花瓣飘落\n" +
                "§f- WindFall (秋季微风): 落叶飘落\n" +
                "§f- Festival (节日): 强制晴天"
            ),
            false
        );
        
        return 1;
    }
    
    /**
     * 获取天气的中文显示名称
     */
    private static String getWeatherDisplayName(String weatherType) {
        return switch (weatherType) {
            case "Sun" -> "晴天";
            case "Rain" -> "雨天";
            case "Storm" -> "雷暴";
            case "Snow" -> "雪天";
            case "WindSpring" -> "春季微风";
            case "WindFall" -> "秋季微风";
            case "Festival" -> "节日";
            default -> "未知";
        };
    }
}
