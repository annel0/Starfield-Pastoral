package com.stardew.craft.client.hud;

import com.stardew.craft.StardewCraft;
import com.stardew.craft.client.ClientPlayerDataCache;
import com.stardew.craft.core.ModDimensions;
import com.stardew.craft.time.StardewTimeManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/**
 * 星露谷物语原版风格HUD
 * 完全按照原版坐标还原
 */
@EventBusSubscriber(modid = StardewCraft.MODID, value = Dist.CLIENT)
public class StardewTimeHud {
    
    // 纹理资源
    @SuppressWarnings("null")
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/background.png");
    @SuppressWarnings("null")
    private static final ResourceLocation POINTER = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/cursor.png");
    @SuppressWarnings("null")
    private static final ResourceLocation SEASON_SPRING = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/spring.png");
    @SuppressWarnings("null")
    private static final ResourceLocation SEASON_SUMMER = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/summer.png");
    @SuppressWarnings("null")
    private static final ResourceLocation SEASON_FALL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/fall.png");
    @SuppressWarnings("null")
    private static final ResourceLocation SEASON_WINTER = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/winter.png");
    @SuppressWarnings("null")
    private static final ResourceLocation WEATHER_SUNNY = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/sunny.png");
    @SuppressWarnings("null")
    private static final ResourceLocation WEATHER_RAINY = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/rainy.png");
    @SuppressWarnings("null")
    private static final ResourceLocation WEATHER_STORMY = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/stormy.png");
    @SuppressWarnings("null")
    private static final ResourceLocation WEATHER_SNOWY = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/snowy.png");
    @SuppressWarnings("null")
    private static final ResourceLocation WEATHER_WINDY_SPRING = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/windy_spring.png");
    @SuppressWarnings("null")
    private static final ResourceLocation WEATHER_WINDY_FALL = ResourceLocation.fromNamespaceAndPath(StardewCraft.MODID, "textures/gui/windy_fall.png");
    
    // UI尺寸
    private static final int BG_WIDTH = 72;
    private static final int BG_HEIGHT = 57;
    private static final int POINTER_WIDTH = 7;
    private static final int POINTER_HEIGHT = 19;
    private static final int ICON_WIDTH = 12;
    private static final int ICON_HEIGHT = 8;
    
    // 指针旋转中心（在背景图内的坐标）
    private static final int POINTER_PIVOT_X = 22;  // 右移3格
    private static final int POINTER_PIVOT_Y = 21;  // 下移1格
    
    // 图标位置（在背景图内的坐标）
    private static final int WEATHER_X = 29;
    private static final int WEATHER_Y = 16;
    private static final int SEASON_X = 53;
    private static final int SEASON_Y = 16;
    
    private static StardewTimeManager clientTimeCache = new StardewTimeManager();
    private static MoneyDial moneyDial = new MoneyDial(8, true);
    private static int moneyShakeTimer = 0;
    @SuppressWarnings("unused")
    private static boolean moneyInitialized = false;

    // HUD锚点边距（右上角）
    private static final int HUD_MARGIN_RIGHT = 10;
    private static final int HUD_MARGIN_TOP = 10;
    // 为右上角原版 Buff 图标留出空间，避免和日期/金钱HUD重叠
    private static final int HUD_TOP_SAFE_OFFSET = 24;
    
    public static void updateClientTime(StardewTimeManager timeData) {
        clientTimeCache = timeData;
    }
    
    public static void updateClientMoney(int money) {
        // MoneyDial会在draw时自动处理动画，这里只需要标记已初始化
        moneyInitialized = true;
    }
    
    public static void triggerMoneyShake() {
        moneyShakeTimer = 100;
    }
    
    public static StardewTimeManager getClientTimeCache() {
        return clientTimeCache;
    }
    
    /**
     * 根据季节编号获取对应的季节图标
     * @param season 季节编号 (0=春季, 1=夏季, 2=秋季, 3=冬季)
     * @return 对应季节的ResourceLocation
     */
    private static ResourceLocation getSeasonIcon(int season) {
        return switch (season) {
            case 0 -> SEASON_SPRING;
            case 1 -> SEASON_SUMMER;
            case 2 -> SEASON_FALL;
            case 3 -> SEASON_WINTER;
            default -> SEASON_SPRING; // 默认春季
        };
    }
    
    /**
     * 根据天气类型获取对应的天气图标
     * @param weather 天气类型 (Sun, Rain, Storm, Snow, WindSpring, WindFall, Festival)
     * @return 对应天气的ResourceLocation
     */
    private static ResourceLocation getWeatherIcon(String weather) {
        return switch (weather) {
            case "Rain" -> WEATHER_RAINY;
            case "Storm" -> WEATHER_STORMY;
            case "Snow" -> WEATHER_SNOWY;
            case "WindSpring" -> WEATHER_WINDY_SPRING;
            case "WindFall" -> WEATHER_WINDY_FALL;
            case "Festival" -> WEATHER_SUNNY; // 节日用晴天图标
            default -> WEATHER_SUNNY; // 默认晴天
        };
    }
    
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        
        if (mc.player == null || mc.level == null) {
            return;
        }
        
        @SuppressWarnings("null")
        boolean isStardewDimension = mc.level.dimension() == ModDimensions.STARDEW_VALLEY 
            || mc.level.dimension() == com.stardew.craft.core.ModMiningDimensions.STARDEW_MINING;
        if (!isStardewDimension) {
            return;
        }
        
        renderStardewHUD(event.getGuiGraphics());
    }
    
    @SuppressWarnings("null")
    private static void renderStardewHUD(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        
        // HUD位置（右上角）
        int hudX = screenWidth - BG_WIDTH - HUD_MARGIN_RIGHT;
        int hudY = HUD_MARGIN_TOP + HUD_TOP_SAFE_OFFSET;
        
        // 1. 渲染背景
        graphics.blit(BACKGROUND, hudX, hudY, 0, 0, BG_WIDTH, BG_HEIGHT, BG_WIDTH, BG_HEIGHT);
        
        // 2. 渲染天气图标（位置29,16）
        String currentWeather = com.stardew.craft.weather.ClientWeatherCache.getCurrentWeather(mc.level.dimension());
        ResourceLocation weatherIcon = getWeatherIcon(currentWeather);
        graphics.blit(weatherIcon, hudX + WEATHER_X, hudY + WEATHER_Y, 0, 0, 
            ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);
        
        // 3. 渲染季节图标（位置53,16）
        ResourceLocation seasonIcon = getSeasonIcon(clientTimeCache.getCurrentSeason());
        graphics.blit(seasonIcon, hudX + SEASON_X, hudY + SEASON_Y, 0, 0, 
            ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);
        
        // 4. 渲染旋转指针
        renderPointer(graphics, hudX, hudY);
        
        // 5. 渲染文字信息
        renderText(graphics, hudX, hudY, mc.font);
    }
    
    /**
     * 渲染时钟指针
     * 旋转中心在(19, 20)，从0度逆时针转到180度
     * 6:00 AM = 0度（正着），2:00 AM = 180度（倒着）
     */
    @SuppressWarnings("null")
    private static void renderPointer(GuiGraphics graphics, int hudX, int hudY) {
        float angle = calculatePointerAngle();
        
        graphics.pose().pushPose();
        
        // 移动到指针底部旋转中心
        graphics.pose().translate(
            hudX + POINTER_PIVOT_X, 
            hudY + POINTER_PIVOT_Y, 
            0
        );
        
        // 逆时针旋转（负角度）
        graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-angle));
        
        // 绘制指针（底部中心对齐）
        graphics.blit(POINTER, 
            -POINTER_WIDTH / 2, -POINTER_HEIGHT,  // 底部中心对齐
            0, 0, 
            POINTER_WIDTH, POINTER_HEIGHT, 
            POINTER_WIDTH, POINTER_HEIGHT);
        
        graphics.pose().popPose();
    }
    
    /**
     * 计算指针角度
     * 6:00 AM (360分钟) = 180度（反向起点）
     * 2:00 AM (1560分钟) = 0度（正向终点）
     * 顺时针旋转180度
     */
    private static float calculatePointerAngle() {
        int currentTime = clientTimeCache.getCurrentTime();
        
        int offset = currentTime - 360;  // 从6:00 AM开始
        if (offset < 0) offset = 0;
        if (offset > 1200) offset = 1200;
        
        // 从180度开始，顺时针转到0度
        return 180.0f - (offset / 1200.0f) * 180.0f;
    }
    
    /**
     * 渲染文字信息：日期、时间、金钱
     */
    private static void renderText(GuiGraphics graphics, int hudX, int hudY, Font font) {
        Minecraft.getInstance();
        
        // 获取时间数据
        int currentTime = clientTimeCache.getCurrentTime();
        int currentDay = clientTimeCache.getCurrentDay();
        
        // 1. 日期显示 "X日 星期X" (27,5 到 65,12) 黑色细体，缩小0.7倍居中
        String weekdayName = getWeekdayName(currentDay);
        String dateStr = net.minecraft.client.resources.language.I18n.get("stardewcraft.hud.date_format", currentDay, weekdayName);
        graphics.pose().pushPose();
        graphics.pose().scale(0.7f, 0.7f, 1.0f);
        @SuppressWarnings("null")
        int dateWidth = font.width(dateStr);
        int dateAreaLeft = (int)((hudX + 27) / 0.7f);
        int dateAreaWidth = (int)(38 / 0.7f);  // 区域宽度 65-27=38
        int dateCenterX = dateAreaLeft + (dateAreaWidth - dateWidth) / 2;
        int dateY = (int)((hudY + 8) / 0.7f);  // 区域高度8像素，从5到12，中心约8
        graphics.drawString(font, dateStr, dateCenterX, dateY, 0x000000, false);
        graphics.pose().popPose();
        
        // 2. 时间显示 "XX:XX" (27,28 到 65,34) 居中 黑色细体，缩小0.7倍，整10分钟显示
        int hours = (currentTime / 60) % 24;
        int minutes = currentTime % 60;
        int displayMinutes = minutes - (minutes % 10);  // 向下取整到10的倍数
        String timeStr = String.format("%02d:%02d", hours, displayMinutes);
        graphics.pose().pushPose();
        graphics.pose().scale(0.7f, 0.7f, 1.0f);
        @SuppressWarnings("null")
        int timeWidth = font.width(timeStr);
        int timeAreaLeft = (int)((hudX + 27) / 0.7f);
        int timeAreaWidth = (int)(38 / 0.7f);  // 区域宽度 65-27=38
        int timeCenterX = timeAreaLeft + (timeAreaWidth - timeWidth) / 2;
        int timeY = (int)((hudY + 31) / 0.7f);  // 区域高度6像素，从28到34，中心约31
        graphics.drawString(font, timeStr, timeCenterX, timeY, 0x000000, false);
        graphics.pose().popPose();
        
        // 3. 金钱显示 - 使用 MoneyDial 实现滚动和抖动效果
        // MoneyDial 的值由 ClientPlayerDataCache.updateFromNBT 直接设置
        
        // 更新抖动计时器
        if (moneyShakeTimer > 0) {
            moneyShakeTimer -= (int)(Minecraft.getInstance().getTimer().getRealtimeDeltaTicks() * 50);
        }
        
        // 计算抖动偏移
        int shakeX = (moneyShakeTimer > 0) ? (int)(Math.random() * 7 - 3) : 0;
        int shakeY = (moneyShakeTimer > 0) ? (int)(Math.random() * 7 - 3) : 0;
        
        // 使用你给的坐标：金币框从(17, 46)开始
        float moneyX = hudX + 17 + shakeX;
        float moneyY = hudY + 46 + shakeY;
        
        // 从缓存中获取金币值并传递给MoneyDial
        int currentMoney = ClientPlayerDataCache.getMoney();
        moneyDial.draw(graphics, moneyX, moneyY, currentMoney);
    }
    
    /**
     * 获取星期名称 (每月1日是周一)
     */
    @SuppressWarnings("null")
    private static String getWeekdayName(int day) {
        String[] weekdayKeys = {
            "stardewcraft.hud.monday",
            "stardewcraft.hud.tuesday", 
            "stardewcraft.hud.wednesday",
            "stardewcraft.hud.thursday",
            "stardewcraft.hud.friday",
            "stardewcraft.hud.saturday",
            "stardewcraft.hud.sunday"
        };
        int index = (day - 1) % 7;
        return net.minecraft.client.resources.language.I18n.get(weekdayKeys[index]);
    }
}
