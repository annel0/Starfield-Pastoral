package com.stardew.craft.weather;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * 客户端天气缓存
 */
public class ClientWeatherCache {
    private static final Map<ResourceKey<Level>, String> weatherByDimension = new HashMap<>();
    private static final Map<ResourceKey<Level>, String> weatherForTomorrowByDimension = new HashMap<>();
    
    public static void setWeather(ResourceKey<Level> dimension, String weatherType, String tomorrowWeather) {
        weatherByDimension.put(dimension, weatherType);
        weatherForTomorrowByDimension.put(dimension, tomorrowWeather);
    }
    
    public static String getCurrentWeather(ResourceKey<Level> dimension) {
        return weatherByDimension.getOrDefault(dimension, "Sun");
    }
    
    public static String getTomorrowWeather(ResourceKey<Level> dimension) {
        return weatherForTomorrowByDimension.getOrDefault(dimension, "Sun");
    }
    
    public static void clear() {
        weatherByDimension.clear();
        weatherForTomorrowByDimension.clear();
    }
}
