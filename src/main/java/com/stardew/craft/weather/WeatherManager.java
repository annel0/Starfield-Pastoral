package com.stardew.craft.weather;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.core.ModDimensions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.Random;

/**
 * 星露谷天气管理器
 * 管理每个维度的天气状态，使用 Minecraft 原版天气系统
 */
@EventBusSubscriber(modid = StardewCraft.MODID)
public class WeatherManager {

    private static boolean isStardewLevel(ServerLevel level) {
        return ModDimensions.STARDEW_VALLEY.equals(level.dimension());
    }

    /**
     * 获取维度的天气状态（从SavedData中）
     */
    private static WeatherState getWeatherState(ServerLevel level) {
        WeatherSavedData data = WeatherSavedData.get(level);
        return data.getWeatherState(level.dimension());
    }

    /**
     * 更新明天的天气预测
     */
    public static void updateWeatherForNewDay(ServerLevel level, int dayOfMonth, String season, int daysPlayed) {
        if (!isStardewLevel(level)) {
            return;
        }
        WeatherState state = getWeatherState(level);

        state.updateTomorrow(level, dayOfMonth, season, daysPlayed);
        WeatherSavedData.get(level).setDirty();

        StardewCraft.LOGGER.debug("Updated weather for dimension {}: tomorrow (day {}) will be {}", 
            level.dimension().location(), dayOfMonth + 1, state.weatherForTomorrow);
        
        // 同步到客户端
        syncToAllPlayers(level, state);
    }

    /**
     * 使用与“天数”相关的稳定随机种子，避免连续天数产生相关输出
     */
    private static Random createDayRandom(ServerLevel level, int daysPlayed) {
        long seed = level.getSeed();
        long mixed = seed ^ (long) daysPlayed * 0x9E3779B97F4A7C15L;
        mixed ^= (mixed >>> 30);
        mixed *= 0xBF58476D1CE4E5B9L;
        mixed ^= (mixed >>> 27);
        mixed *= 0x94D049BB133111EBL;
        mixed ^= (mixed >>> 31);
        return new Random(mixed);
    }

    /**
     * 应用今天的天气（在新的一天开始时调用）
     */
    public static void applyWeatherForNewDay(ServerLevel level, int dayOfMonth) {
        if (!isStardewLevel(level)) {
            return;
        }
        WeatherState state = getWeatherState(level);
        state.applyToday(level, dayOfMonth);
        WeatherSavedData.get(level).setDirty();

        syncToAllPlayers(level, state);
    }

    /**
     * 星露谷物语的天气修改规则
     */
    private static String getWeatherModificationsForDate(int dayOfMonth, String season, int daysPlayed, String defaultWeather) {
        String weather = defaultWeather;

        // 1. 每月第一天必定晴天
        if (dayOfMonth == 1 || daysPlayed <= 4) {
            weather = "Sun";
        }

        // 2. 第3天（教程）必定下雨
        if (daysPlayed == 3) {
            weather = "Rain";
        }

        // 3. 夏天每13天一次雷暴（夏天的第13天和第26天）
        if ("summer".equalsIgnoreCase(season) && (dayOfMonth == 13 || dayOfMonth == 26)) {
            weather = "Storm";
        }

        // 4. 碎片天气根据季节区分
        if (weather.startsWith("Wind")) {
            if ("spring".equalsIgnoreCase(season)) {
                weather = "WindSpring";
            } else if ("fall".equalsIgnoreCase(season)) {
                weather = "WindFall";
            }
        }

        // TODO: 5. 节日天气（需要节日系统支持）
        // TODO: 6. 被动节日天气（需要节日系统支持）

        return weather;
    }

    /**
     * 预测明天的天气（基于星露谷物语的概率系统）
     */
    public static String predictTomorrowWeather(ServerLevel level, String season, int dayOfMonth, Random random) {
        

        // 基础概率（可以后续从配置文件读取）
        // 星露谷物语的天气概率：
        // - 春天：晴天40%，雨天40%，微风20%
        // - 夏天：晴天80%，雨天18%，雷暴2%
        // - 秋天：晴天40%，雨天40%，微风20%
        // - 冬天：晴天50%，雪50%

        double roll = random.nextDouble();

        if ("winter".equalsIgnoreCase(season)) {
            // 冬天：50%雪天，50%晴天
            return roll < 0.50 ? "Snow" : "Sun";
        } else if ("spring".equalsIgnoreCase(season)) {
            // 春天：40%晴天，40%雨天，20%微风
            if (roll < 0.40) {
                return "Sun";
            } else if (roll < 0.80) {
                return "Rain";
            } else {
                return "WindSpring";
            }
        } else if ("summer".equalsIgnoreCase(season)) {
            // 夏天：80%晴天，18%雨天，2%雷暴
            if (roll < 0.80) {
                return "Sun";
            } else if (roll < 0.98) {
                return "Rain";
            } else {
                return "Storm";
            }
        } else if ("fall".equalsIgnoreCase(season)) {
            // 秋天：40%晴天，40%雨天，20%微风
            if (roll < 0.40) {
                return "Sun";
            } else if (roll < 0.80) {
                return "Rain";
            } else {
                return "WindFall";
            }
        }
        
        return "Sun";
    }

    /**
     * 获取当前天气类型
     */
    public static String getCurrentWeather(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return "Sun";
        }
        WeatherState state = getWeatherState(serverLevel);
        return state.weatherType;
    }

    /**
     * 获取明天的天气预测
     */
    public static String getTomorrowWeather(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return "Sun";
        }
        WeatherState state = getWeatherState(serverLevel);
        return state.weatherForTomorrow;
    }

    /**
     * 检查是否是碎片天气（用于渲染花瓣/落叶）
     */
    public static boolean isDebrisWeather(Level level) {
        String weather = getCurrentWeather(level);
        return "WindSpring".equals(weather) || "WindFall".equals(weather);
    }

    /**
     * 检查是否是春季微风
     */
    public static boolean isSpringWind(Level level) {
        return "WindSpring".equals(getCurrentWeather(level));
    }

    /**
     * 检查是否是秋季微风
     */
    public static boolean isFallWind(Level level) {
        return "WindFall".equals(getCurrentWeather(level));
    }

    /**
     * 检查是否是下雪天（客户端用于渲染雪花粒子）
     */
    public static boolean isSnowing(Level level) {
        return "Snow".equals(getCurrentWeather(level));
    }

    /**
     * 手动设置天气（用于调试命令）
     */
    public static void setWeather(ServerLevel level, String weatherType) {
        WeatherState state = getWeatherState(level);
        state.weatherType = weatherType;
        state.applyToLevel(level);
        WeatherSavedData.get(level).setDirty();
        
        // 同步到客户端
        syncToAllPlayers(level, state);
    }
    
    /**
     * 同步天气到所有玩家
     */
    @SuppressWarnings("null")
    private static void syncToAllPlayers(ServerLevel level, WeatherState state) {
        String dimStr = level.dimension().location().toString();
        com.stardew.craft.network.WeatherSyncPacket packet = new com.stardew.craft.network.WeatherSyncPacket(
            dimStr,
            state.weatherType,
            state.weatherForTomorrow
        );
        
        for (net.minecraft.server.level.ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, packet);
        }
    }

    /**
     * 同步维度天气（在维度加载时调用）
     */
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (!isStardewLevel(level)) {
                return;
            }
            WeatherState state = getWeatherState(level);
            state.applyToLevel(level);
            syncToAllPlayers(level, state);
        }
    }

    /**
     * 持续确保天气保持一致（每秒检查一次）
     */
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!isStardewLevel(level)) {
            return;
        }

        tickCounter++;
        // 每20 ticks（1秒）检查一次
        if (tickCounter >= 20) {
            tickCounter = 0;

            WeatherState state = getWeatherState(level);
            // 每秒强制同步，确保整天保持目标天气
            state.applyToLevel(level);
        }
    }

    /**
     * 天气状态类
     */
    public static class WeatherState {
        private String weatherType = "Sun";           // 当前天气
        private String weatherForTomorrow = "Sun";    // 明天的天气
        private int monthlyNonRainyDayCount = 0;      // 本月非雨天数
        
        /**
         * 保存到NBT
         */
        @SuppressWarnings("null")
        public void saveToNBT(net.minecraft.nbt.CompoundTag tag) {
            tag.putString("weatherType", weatherType);
            tag.putString("weatherForTomorrow", weatherForTomorrow);
            tag.putInt("monthlyNonRainyDayCount", monthlyNonRainyDayCount);
        }
        
        /**
         * 从NBT加载
         */
        public void loadFromNBT(net.minecraft.nbt.CompoundTag tag) {
            weatherType = tag.getString("weatherType");
            weatherForTomorrow = tag.getString("weatherForTomorrow");
            monthlyNonRainyDayCount = tag.getInt("monthlyNonRainyDayCount");
        }

        /**
         * 应用天气到 Minecraft 世界
         */
        @SuppressWarnings("null")
        public void applyToLevel(ServerLevel level) {
            level.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(true, level.getServer());
            int durationTicks = 24000 * 365;
            boolean raining = false;
            boolean thundering = false;
            switch (weatherType) {
                case "Rain":
                    raining = true;
                    level.setWeatherParameters(0, durationTicks, true, false);
                    break;
                case "Storm":
                    raining = true;
                    thundering = true;
                    level.setWeatherParameters(0, durationTicks, true, true);
                    break;
                case "Snow":
                    // 雪天：晴天（不润湿耕地），客户端渲染器会显示雪花粒子
                    level.setWeatherParameters(durationTicks, 0, false, false);
                    break;
                case "WindSpring":
                case "WindFall":
                case "Festival":
                case "Sun":
                default:
                    // 晴天（包括风天和节日）
                    level.setWeatherParameters(durationTicks, 0, false, false);
                    break;
            }

            if (level.getLevelData() instanceof ServerLevelData data) {
                data.setClearWeatherTime(raining ? 0 : durationTicks);
                data.setRainTime(raining ? durationTicks : 0);
                data.setRaining(raining);
                data.setThunderTime(thundering ? durationTicks : 0);
                data.setThundering(thundering);
            }

            if (level.dimension() != Level.OVERWORLD) {
                ServerLevel overworld = level.getServer().overworld();
                overworld.setWeatherParameters(raining ? 0 : durationTicks, raining ? durationTicks : 0, raining, thundering);
                if (overworld.getLevelData() instanceof ServerLevelData overworldData) {
                    overworldData.setClearWeatherTime(raining ? 0 : durationTicks);
                    overworldData.setRainTime(raining ? durationTicks : 0);
                    overworldData.setRaining(raining);
                    overworldData.setThunderTime(thundering ? durationTicks : 0);
                    overworldData.setThundering(thundering);
                }
            }
        }

        public boolean isRaining() {
            return "Rain".equals(weatherType) || "Storm".equals(weatherType);
        }

        public boolean isWindy() {
            return "WindSpring".equals(weatherType) || "WindFall".equals(weatherType);
        }

        public boolean isThundering() {
            return "Storm".equals(weatherType);
        }

        public String getWeatherType() {
            return weatherType;
        }

        public String getWeatherForTomorrow() {
            return weatherForTomorrow;
        }

        public int getMonthlyNonRainyDayCount() {
            return monthlyNonRainyDayCount;
        }

        private void applyToday(ServerLevel level, int dayOfMonth) {
            // 将明天的天气变为今天的天气
            weatherType = weatherForTomorrow;

            // 应用到 Minecraft 世界
            applyToLevel(level);

            // 统计非雨天数
            if (dayOfMonth == 1) {
                monthlyNonRainyDayCount = 0;
            }
            if (!"Rain".equals(weatherType) && !"Storm".equals(weatherType)) {
                monthlyNonRainyDayCount++;
            }
        }

        private void updateTomorrow(ServerLevel level, int dayOfMonth, String season, int daysPlayed) {
            // 初始化明天天气（原版会在更新前清空为晴天）
            weatherForTomorrow = "Sun";

            // 预测明天的天气（基于随机和规则）
            Random random = createDayRandom(level, daysPlayed);
            String predictedWeather = predictTomorrowWeather(level, season, dayOfMonth + 1, random);

            // 应用星露谷物语的天气修改规则（会覆盖预测）
            weatherForTomorrow = getWeatherModificationsForDate(
                dayOfMonth + 1,
                season,
                daysPlayed + 1,
                predictedWeather
            );
        }
    }
}
