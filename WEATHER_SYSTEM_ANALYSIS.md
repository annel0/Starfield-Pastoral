# 星露谷物语天气系统分析

基于反编译源代码的完整天气系统分析（排除绿雨系统）

## 1. 天气类型概览

### 1.1 基础天气类型
星露谷物语定义了以下天气类型（常量定义在 `Game1.cs`）：

```csharp
public const string weather_sunny = "Sun";        // 晴天
public const string weather_rain = "Rain";        // 雨天
public const string weather_debris = "Wind";      // 风/碎片天气（春天花瓣、秋天落叶）
public const string weather_lightning = "Storm";  // 雷暴
public const string weather_snow = "Snow";        // 下雪（冬天）
// 还有 "Wedding" 和 "Festival" 是特殊天气
```

### 1.2 天气状态标志
`LocationWeather` 类维护以下布尔标志：

```csharp
public bool IsRaining      // 是否在下雨
public bool IsSnowing      // 是否在下雪
public bool IsLightning    // 是否有雷电
public bool IsDebrisWeather // 是否有碎片天气（风吹花瓣/落叶）
public bool IsGreenRain    // 是否绿雨（排除）
```

## 2. 天气系统核心架构

### 2.1 LocationWeather 类
每个位置上下文（Location Context）都有自己的天气对象。

**关键属性：**
```csharp
public string Weather               // 当前天气ID
public string WeatherForTomorrow    // 明天的天气ID
public int monthlyNonRainyDayCount  // 本月非雨天数统计
```

### 2.2 天气更新流程

#### 2.2.1 预测明天天气 (`UpdateWeatherForNewDay`)
```csharp
public static void UpdateWeatherForNewDay()
{
    // 1. 应用天气修改规则
    weatherForTomorrow = getWeatherModificationsForDate(Date, weatherForTomorrow);
    
    // 2. 婚礼当天强制婚礼天气
    if (weddingToday)
        weatherForTomorrow = "Wedding";
    
    // 3. 为每个位置上下文更新天气
    foreach (var pair in locationContextData)
    {
        netWorldState.Value.GetWeatherForLocation(pair.Key)
            .UpdateDailyWeather(pair.Key, pair.Value, random);
    }
    
    // 4. 处理天气复制（某些位置从其他位置复制天气）
    foreach (var pair in locationContextData)
    {
        if (pair.Value.CopyWeatherFromLocation != null)
        {
            // 复制天气
        }
    }
}
```

#### 2.2.2 应用今天天气 (`ApplyWeatherForNewDay`)
```csharp
public static void ApplyWeatherForNewDay()
{
    LocationWeather weatherForLocation = netWorldState.Value.GetWeatherForLocation("Default");
    
    // 将预测的天气应用到今天
    weatherForTomorrow = weatherForLocation.WeatherForTomorrow;
    isRaining = weatherForLocation.IsRaining;
    isSnowing = weatherForLocation.IsSnowing;
    isLightning = weatherForLocation.IsLightning;
    isDebrisWeather = weatherForLocation.IsDebrisWeather;
    
    // 如果是碎片天气，填充碎片数组
    if (isDebrisWeather)
        populateDebrisWeatherArray();
    
    // 统计非雨天数
    foreach (var key in netWorldState.Value.LocationWeather.Keys)
    {
        LocationWeather locationWeather = netWorldState.Value.LocationWeather[key];
        if (dayOfMonth == 1)
            locationWeather.monthlyNonRainyDayCount.Value = 0;
        if (!locationWeather.IsRaining)
            locationWeather.monthlyNonRainyDayCount.Value++;
    }
}
```

#### 2.2.3 每日天气更新 (`LocationWeather.UpdateDailyWeather`)
```csharp
public void UpdateDailyWeather(string locationContextId, LocationContextData data, Random random)
{
    InitializeDayWeather(); // 先清空所有标志
    
    // 根据 WeatherForTomorrow 设置标志
    switch (WeatherForTomorrow)
    {
        case "Rain":
            IsRaining = true;
            break;
        case "Storm":
            IsRaining = true;
            IsLightning = true;
            break;
        case "Wind":
            IsDebrisWeather = true;
            break;
        case "Snow":
            IsSnowing = true;
            break;
    }
    
    // 默认明天晴天
    WeatherForTomorrow = "Sun";
    
    WorldDate tomorrow = new WorldDate(Game1.Date);
    tomorrow.TotalDays++;
    
    // 节日天气
    if (Utility.isFestivalDay(tomorrow.DayOfMonth, tomorrow.Season, locationContextId))
    {
        WeatherForTomorrow = "Festival";
        return;
    }
    
    // 被动节日天气
    if (Utility.TryGetPassiveFestivalDataForDay(...))
    {
        WeatherForTomorrow = "Sun";
        return;
    }
    
    // 遍历天气条件配置
    foreach (WeatherCondition weatherCondition in data.WeatherConditions)
    {
        if (GameStateQuery.CheckConditions(weatherCondition.Condition, ...))
        {
            WeatherForTomorrow = weatherCondition.Weather;
            break;
        }
    }
}
```

### 2.3 天气修改规则 (`getWeatherModificationsForDate`)

特殊日期的天气覆盖规则：

```csharp
public static string getWeatherModificationsForDate(WorldDate date, string default_weather)
{
    string weather = default_weather;
    int day_offset = date.TotalDays - Date.TotalDays;
    
    // 1. 每月第一天必定晴天
    if (date.DayOfMonth == 1 || stats.DaysPlayed + day_offset <= 4)
        weather = "Sun";
    
    // 2. 第3天（教程）必定下雨
    if (stats.DaysPlayed + day_offset == 3)
        weather = "Rain";
    
    // 3. 夏天每13天一次雷暴
    if (date.Season == Season.Summer && date.DayOfMonth % 13 == 0)
        weather = "Storm";
    
    // 4. 节日天气
    if (Utility.isFestivalDay(date.DayOfMonth, date.Season))
        weather = "Festival";
    
    // 5. 被动节日期间，相关位置晴天
    foreach (PassiveFestivalData festival in DataLoader.PassiveFestivals(content).Values)
    {
        if (/* 节日正在进行 && 地图被替换 */)
            weather = "Sun";
    }
    
    return weather;
}
```

## 3. 视觉效果系统

### 3.1 雨水效果

#### 3.1.1 雨滴数据结构
```csharp
public struct RainDrop
{
    public float x;
    public float y;
    public int frame;      // 动画帧
    public int accumulator; // 时间累加器
}
```

#### 3.1.2 雨水更新逻辑 (`updateWeather`)
```csharp
public static void updateWeather(GameTime time)
{
    if (currentLocation.IsOutdoors && currentLocation.IsRainingHere())
    {
        // 更新每个雨滴
        for (int i = 0; i < rainDrops.Length; i++)
        {
            // 雨滴下落
            rainDrops[i].position.Y += (isDebrisWeather ? 1f : 4f);
            
            // 雨滴动画
            if (rainDrops[i].frame == 0)
            {
                // 还在下落
                if (random.NextDouble() < 0.1)
                    rainDrops[i].frame++; // 开始溅起动画
            }
            else
            {
                // 溅起动画
                rainDrops[i].accumulator += time.ElapsedGameTime.Milliseconds;
                if (rainDrops[i].accumulator > 70)
                {
                    rainDrops[i].frame = (rainDrops[i].frame + 1) % 4;
                    rainDrops[i].accumulator = 0;
                    if (rainDrops[i].frame == 0)
                    {
                        // 重置到屏幕顶部
                        rainDrops[i].position = new Vector2(
                            random.Next(viewport.Width), 
                            random.Next(viewport.Height)
                        );
                    }
                }
            }
            
            // 超出屏幕底部，重置
            if (rainDrops[i].position.Y > viewport.Height + 64)
                rainDrops[i].position.Y = -64f;
        }
    }
}
```

#### 3.1.3 雨滴位置更新 (`updateRaindropPosition`)
```csharp
public static void updateRaindropPosition()
{
    if (IsRainingHere())
    {
        // 根据视口移动调整雨滴位置
        int xOffset = viewport.X - (int)previousViewportPosition.X;
        int yOffset = viewport.Y - (int)previousViewportPosition.Y;
        
        for (int i = 0; i < rainDrops.Length; i++)
        {
            rainDrops[i].position.X -= xOffset * 1f;
            rainDrops[i].position.Y -= yOffset * 1f;
            
            // 边界检查和循环
            if (rainDrops[i].position.Y > viewport.Height + 64)
                rainDrops[i].position.Y = -64f;
            else if (rainDrops[i].position.X < -64f)
                rainDrops[i].position.X = viewport.Width;
            else if (rainDrops[i].position.Y < -64f)
                rainDrops[i].position.Y = viewport.Height;
            else if (rainDrops[i].position.X > viewport.Width + 64)
                rainDrops[i].position.X = -64f;
        }
    }
}
```

### 3.2 碎片天气（Wind/Debris）

#### 3.2.1 WeatherDebris 类
```csharp
public class WeatherDebris
{
    public const int pinkPetals = 0;   // 粉色花瓣（春天）
    public const int greenLeaves = 1;  // 绿色叶子（春天）
    public const int fallLeaves = 2;   // 秋天落叶
    public const int snow = 3;         // 雪花（冬天）
    
    public Vector2 position;           // 位置
    public Rectangle sourceRect;       // 纹理源矩形
    public int which;                  // 碎片类型
    public int animationIndex;         // 当前动画索引
    public int animationTimer;         // 动画计时器
    public int animationDirection;     // 动画方向（1或-1）
    public int animationIntervalOffset;// 动画间隔偏移（随机）
    public float dx;                   // X速度
    public float dy;                   // Y速度
    public static float globalWind = -0.25f; // 全局风力
    private bool blowing;              // 是否正在被吹
}
```

#### 3.2.2 碎片更新逻辑 (`WeatherDebris.update`)
```csharp
public void update(bool slow)
{
    // 移动
    position.X += dx + (slow ? 0f : globalWind);
    position.Y += dy - (slow ? 0f : -0.5f); // 重力
    
    if (dy < 0f && !blowing)
        dy += 0.01f; // 逐渐减速上升
    
    // 边界循环
    if (position.X < -80f)
    {
        position.X = Game1.viewport.Width;
        position.Y = Game1.random.Next(0, Game1.viewport.Height - 64);
    }
    if (position.Y > Game1.viewport.Height + 16)
    {
        position.X = Game1.random.Next(0, Game1.viewport.Width);
        position.Y = -64f;
        dy = Game1.random.Next(-15, 10) / (slow ? 200f : 50f);
        dx = Game1.random.Next(-10, 0) / (slow ? 200f : 50f);
    }
    
    // 随机阵风（春夏）
    if (blowing)
    {
        dy -= 0.01f; // 向上吹
        if (Game1.random.NextDouble() < 0.006 || dy < -2f)
            blowing = false;
    }
    else if (!slow && Game1.random.NextDouble() < 0.001 && 
             (Game1.IsSpring || Game1.IsSummer))
    {
        blowing = true;
    }
    
    // 动画更新
    animationTimer -= Game1.currentGameTime.ElapsedGameTime.Milliseconds;
    if (animationTimer <= 0)
    {
        animationTimer = 100 + animationIntervalOffset;
        animationIndex += animationDirection;
        
        // 动画逻辑（旋转、飘动效果）
        if (animationIndex > 10)
        {
            if (Game1.random.NextDouble() < 0.82)
            {
                animationIndex--;
                animationDirection = 0;
                dx += 0.1f;
                dy -= 0.2f;
            }
            else
            {
                animationIndex = 0;
            }
        }
        // ... 更多动画状态切换逻辑
        
        if (which != 3) // 非雪花
            sourceRect.X = 352 + animationIndex * 16;
    }
}
```

#### 3.2.3 秋天风力系统
```csharp
// 在 Game1.updateWeather 中
if (currentLocation.GetSeason() == Season.Fall)
{
    if (WeatherDebris.globalWind == 0f)
        WeatherDebris.globalWind = -0.5f;
    
    // 随机阵风
    if (random.NextDouble() < 0.001 && windGust == 0f && 
        WeatherDebris.globalWind >= -0.5f)
    {
        windGust += random.Next(-10, -1) / 100f;
        playSound("wind", out wind); // 播放风声
    }
    else if (windGust != 0f)
    {
        windGust = Math.Max(-5f, windGust * 1.02f); // 风力增强
        WeatherDebris.globalWind = -0.5f + windGust;
        
        if (windGust < -0.2f && random.NextDouble() < 0.007)
            windGust = 0f; // 风力消退
    }
    
    // 风力恢复正常
    if (WeatherDebris.globalWind < -0.5f)
    {
        WeatherDebris.globalWind = Math.Min(-0.5f, WeatherDebris.globalWind + 0.015f);
        
        // 更新风声音量和频率
        if (wind != null)
        {
            wind.SetVariable("Volume", -WeatherDebris.globalWind * 20f);
            wind.SetVariable("Frequency", -WeatherDebris.globalWind * 20f);
            
            if (WeatherDebris.globalWind == -0.5f)
                wind.Stop(AudioStopOptions.AsAuthored);
        }
    }
}
```

### 3.3 雪天效果

雪天使用 WeatherDebris 系统，类型为 `snow (3)`：
```csharp
case 3: // 雪花
    sourceRect = new Rectangle(391 + 4 * Game1.random.Next(5), 1236, 4, 4);
    break;
```

雪花特点：
- 更小的纹理 (4x4)
- 多种变体（5种不同的雪花图案）
- 使用相同的下落和风力逻辑

### 3.4 雷暴效果

#### 3.4.1 闪电系统 (`Utility.performLightningUpdate`)
```csharp
public static void performLightningUpdate(int time_of_day)
{
    Random random = Utility.CreateRandom(...);
    
    if (random.NextDouble() < 0.125 + Game1.player.team.AverageDailyLuck())
    {
        Farm farm = Game1.getFarm();
        LightningStrikeEvent lightningEvent = new LightningStrikeEvent();
        
        // 随机选择目标
        if (random.NextDouble() < 0.25)
        {
            // 击中果树、作物等
            // 可能生成煤炭、摧毁作物、创建避雷针奖励
        }
        
        // 小闪光概率
        if (random.NextDouble() < 0.1)
        {
            lightningEvent.smallFlash = true;
        }
        else
        {
            lightningEvent.bigFlash = true;
        }
        
        farm.lightningStrikeEvent.Fire(lightningEvent);
    }
}
```

#### 3.4.2 雷电效果处理 (`Farm.doLightningStrike`)
```csharp
private void doLightningStrike(LightningStrikeEvent lightning)
{
    if (lightning.smallFlash)
    {
        if (Game1.currentLocation.IsOutdoors && 
            Game1.currentLocation.IsLightningHere())
        {
            // 小闪光
            Game1.flashAlpha = 0.5f + Game1.random.NextDouble();
            
            if (Game1.random.NextBool())
            {
                DelayedAction.screenFlashAfterDelay(
                    0.3f + Game1.random.NextDouble(), 
                    Game1.random.Next(500, 1000)
                );
            }
            
            // 延迟播放雷声
            DelayedAction.playSoundAfterDelay(
                "thunder_small", 
                Game1.random.Next(500, 1500)
            );
        }
    }
    else if (lightning.bigFlash && Game1.currentLocation.IsOutdoors)
    {
        // 大闪光
        Game1.flashAlpha = 0.5f + Game1.random.NextDouble();
        Game1.playSound("thunder");
    }
    
    // 创建闪电螺栓（如果指定了位置）
    if (lightning.createBolt)
    {
        // 创建闪电视觉效果
        // 生成物品（煤炭等）
    }
}
```

#### 3.4.3 夜间雷电 (`Utility.overnightLightning`)
```csharp
public static void overnightLightning(int timeWentToSleep)
{
    if (Game1.IsMasterGame)
    {
        // 从玩家睡觉时间到晚上11点，每小时处理一次
        int numberOfLoops = (2300 - timeWentToSleep) / 100;
        for (int i = 1; i <= numberOfLoops; i++)
        {
            performLightningUpdate(timeWentToSleep + i * 100);
        }
    }
}
```

## 4. 天气图标系统

### 4.1 天气图标更新 (`updateWeatherIcon`)
```csharp
public static void updateWeatherIcon()
{
    if (IsSnowingHere())
        weatherIcon = 7;
    else if (IsRainingHere())
        weatherIcon = 4;
    else if (IsDebrisWeatherHere() && IsSpring)
        weatherIcon = 3;
    else if (IsDebrisWeatherHere() && IsFall)
        weatherIcon = 6;
    else if (IsDebrisWeatherHere() && IsWinter)
        weatherIcon = 7;
    else if (weddingToday)
        weatherIcon = 0;
    else
        weatherIcon = 2; // 晴天
    
    if (IsLightningHere())
        weatherIcon = 5;
    
    if (Utility.isFestivalDay())
        weatherIcon = 1;
}
```

### 4.2 图标索引
- 0: 婚礼
- 1: 节日
- 2: 晴天
- 3: 春天碎片天气
- 4: 雨天
- 5: 雷暴
- 6: 秋天碎片天气
- 7: 雪天

## 5. 电视天气预报系统

### 5.1 天气预报文本 (`TV.getWeatherForecast`)
```csharp
protected virtual string getWeatherForecast()
{
    WorldDate tomorrow = new WorldDate(Game1.Date);
    tomorrow.TotalDays++;
    
    string forecast = Game1.getWeatherModificationsForDate(
        tomorrow, 
        Game1.weatherForTomorrow
    );
    
    return getWeatherForecast(forecast);
}

protected virtual string getWeatherForecast(string weatherId)
{
    switch (weatherId)
    {
        case "Festival":
            // 返回节日信息（地点、时间）
            return "明天是[节日名]...";
        
        case "Snow":
            return Game1.content.LoadString("Strings\\StringsFromCSFiles:TV.cs." + 
                Game1.random.Choose("13180", "13181"));
        
        case "Rain":
            return Game1.content.LoadString("Strings\\StringsFromCSFiles:TV.cs.13184");
        
        case "Storm":
            return Game1.content.LoadString("Strings\\StringsFromCSFiles:TV.cs.13185");
        
        case "Wind":
            return Game1.season switch
            {
                Season.Spring => "春天的微风...",
                Season.Fall => "秋天的大风...",
                _ => "刮风天...",
            };
        
        default: // "Sun"
            return Game1.content.LoadString("Strings\\StringsFromCSFiles:TV.cs." + 
                Game1.random.Choose("13182", "13183"));
    }
}
```

### 5.2 电视屏幕天气动画 (`TV.setWeatherOverlay`)
```csharp
protected virtual void setWeatherOverlay(string weatherId)
{
    switch (weatherId)
    {
        case "Snow":
            screenOverlay = new TemporaryAnimatedSprite(
                "LooseSprites\\Cursors", 
                new Rectangle(465, 346, 13, 13), 
                100f, 4, 999999, 
                getScreenPosition() + new Vector2(3f, 3f) * getScreenSizeModifier(),
                ...
            );
            break;
        
        case "Rain":
            // 雨天动画
            break;
        
        case "Storm":
            // 雷暴动画
            break;
        
        case "Wind":
            // 根据季节显示不同的碎片动画
            break;
        
        case "Sun":
        default:
            // 太阳动画
            break;
    }
}
```

## 6. 在 Minecraft 中实现天气系统的建议

### ⚠️ 重要发现：钓鱼系统使用原版天气

**当前钓鱼系统检测方式：**
```java
// FishingDataManager.java line 68
boolean isRaining = level.isRaining();

// SpawnFishRule.java
public boolean matchesWeather(boolean isRaining) {
    if ("rainy".equalsIgnoreCase(weather)) {
        return isRaining;  // 检测 MC 原版下雨
    }
    if ("sunny".equalsIgnoreCase(weather)) {
        return !isRaining; // 检测 MC 原版晴天
    }
    return true;
}
```

**结论：必须使用 Minecraft 原版天气系统（`/weather rain/thunder/clear`），否则钓鱼系统会失效！**

### 6.1 简化设计方案

由于需要兼容钓鱼系统，我们直接使用 MC 原版天气 + 额外数据存储：

```mcfunction
# 只需存储星露谷特有的天气信息
scoreboard objectives add stardew.weather dummy
# 0 = 跟随MC原版 (sunny/rainy/thunder)
# 1 = 碎片天气 (Wind - 春天花瓣/秋天落叶)
# 2 = 节日/婚礼 (强制晴天但有特殊标记)

scoreboard objectives add stardew.weather_tomorrow dummy
scoreboard objectives add stardew.days_played dummy
scoreboard objectives add stardew.non_rainy_days dummy
```

### 6.2 天气更新系统（Java 端实现）

#### 6.2.1 维度天气管理器
在 Java 中为每个维度维护天气状态：

```java
public class WeatherManager {
    // 每个维度的天气状态
    private static final Map<ResourceKey<Level>, WeatherState> weatherByDimension = new HashMap<>();
    
    public static class WeatherState {
        private String weatherType = "Sun";  // Sun, Rain, Storm, Wind
        private String weatherTomorrow = "Sun";
        private int nonRainyDayCount = 0;
        
        // 应用到 MC 世界
        public void applyToLevel(ServerLevel level) {
            switch (weatherType) {
                case "Rain" -> level.setWeatherParameters(0, 6000, true, false);
                case "Storm" -> level.setWeatherParameters(0, 6000, true, true);
                case "Wind" -> level.setWeatherParameters(0, 6000, false, false); // 晴天+粒子
                default -> level.setWeatherParameters(6000, 0, false, false); // 晴天
            }
        }
    }
    
    // 每日更新天气
    public static void updateWeatherForNewDay(ServerLevel level, int dayOfMonth, 
                                              String season, int daysPlayed) {
        WeatherState state = weatherByDimension.computeIfAbsent(
            level.dimension(), k -> new WeatherState()
        );
        
        // 应用星露谷天气规则
        String weather = state.weatherTomorrow;
        
        // 特殊日期规则
        if (dayOfMonth == 1 || daysPlayed <= 4) {
            weather = "Sun";
        }
        if (daysPlayed == 3) {
            weather = "Rain";
        }
        if ("summer".equals(season) && dayOfMonth % 13 == 0) {
            weather = "Storm";
        }
        
        // 应用天气
        state.weatherType = weather;
        state.applyToLevel(level);
        
        // 预测明天天气（随机或基于规则）
        state.weatherTomorrow = predictTomorrowWeather(level, season, dayOfMonth + 1);
     ✅ 6.3.1 雨雪效果 - 使用 MC 原版
**无需额外实现！** MC 原版的雨雪效果已经足够好，钓鱼系统也依赖原版：
- 雨天：`/weather rain`
- 雷暴：`/weather thunder`  
- 晴天：`/weather clear`

**作物浇水检测也用原版：**
```java
// FarmBlockMixin.java line 57
if (!level.isRainingAt(pos.above())) {
    // 需要浇水
}
```

#### 🎨 6.3.2 碎片天气粒子（额外实现）
这是星露谷特有的，需要在 Java 中实现：

```java
// WeatherDebrisRenderer.java
public class WeatherDebrisRenderer {
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        
        Level level = player.level();
        String season = getCurrentSeason(level);
        
        // 只在碎片天气时生成
        if (!isDebrisWeather(level)) return;
        
        // 春天：粉色花瓣
        if ("spring".equals(season)) {
            spawnCherryLeaves(player);
        }
        // 秋天：黄色落叶
        else if ("fall".equals(season)) {
            spawnFallLeaves(player);
        }
    }
    
    private static void spawnCherryLeaves(Player player) {
        Level level = player.level();
        RandomSource random = level.random;
        
        // 在玩家周围随机位置生成花瓣
        for (int i = 0; i < 2; i++) {
            double x = player.getX() + (random.nextDouble() - 0.5) * 30;
            double y = player.getY() + random.nextDouble() * 10 + 5;
            double z = player.getZ() + (random.nextDouble() - 0.5) * 30;
            
            level.addParticle(ParticleTypes.CHERRY_LEAVES,
                x, y, z,
                -0.1, -0.05, -0.1  // 飘落速度
            );
        }
    }
    
    private static void spawnFallLeaves(Player player) {
        Level level = player.level();
        RandomSource random = level.random;
        
        for (i（电视/HUD）

#### 6.4.1 电视天气预报 - GUI 实现
在 Java 中创建电视交互界面：

```java
// TVScreen.java
public class TVWeatherForecast extends Screen {
    private String tomorrowWeather;
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 绘制电视背景
        guiGraphics.blit(TV_TEXTURE, x, y, 0, 0, 176, 166);
        
        // 显示明天天气
        Component forecast = switch (tomorrowWeather) {
            case "Rain" -> Component.translatable("stardew.weather.forecast.rain")
                    .withStyle(ChatFormatting.BLUE);
            case "Storm" -> Component.translatable("stardew.weather.forecast.storm")
                    .withStyle(ChatFormatting.DARK_PURPLE);
            case "Wind" -> Component.translatable("stardew.weather.forecast.wind")
                    .withStyle(ChatFormatting.GREEN);
            default -> Component.translatable("stardew.weather.forecast.sunny")
                    .withStyle(ChatFormatting.YELLOW);
        };
        
        guiGraphics.drawCenteredString(this.font, forecast, centerX, y + 40, 0xFFFFFF);
        
        // 绘制天气图标
        renderWeatherIcon(guiGraphics, tomorrowWeather, centerX - 8, y + 60);
    }
}
```

#### 6.4.2 HUD 天气图标
使用自定义 HUD overlay：

```java
// WeatherHudOverlay.java
@SubscribeEvent
public static void onRenderGameOverlay(RenderGuiOverlayEvent.Post event) {
    if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;
    
    Minecraft mc = Minecraft.getInstance();
    if (mc.player == null) return;
    
    String weather = WeatherManager.getCurrentWeather(mc.level);
    
    GuiGraphics guiGraphics = event.getGuiGraphics();
    int screenWidth = mc.getWindow().getGuiScaledWidth();
    （已实现）

#### ✅ 6.5.1 作物浇水 - 已实现
**已在 FarmBlockMixin 中实现：**
```java
// FarmBlockMixin.java line 57
if (!level.isRainingAt(pos.above())) {
    // 下雨自动浇水，无需手动
}
```
**使用的是 MC 原版的 `isRainingAt()` 方法！**

#### ✅ 6.5.2 钓鱼天气检测 - 已实现
**已在 FishingDataManager 中实现：**
```java
// FishingDataManager.java line 68
boolean isRaining = level.isRaining();  // 使用 MC 原版天气

// SpawnFishRule.java line 123
public boolean matchesWeather(boolean isRaining) {
    if (性能优化

#### 6.6.1 使用原生系统的优势
✅ **雨雪效果**：MC 原生渲染，性能优化完善  
✅ **天气检测**：直接调用 `level.isRaining()`，无性能开销  
✅ **作物浇水**：使用原生 `isRainingAt(pos)` 方法  
✅ **钓鱼系统**：已经在用原生 API，无需改动

#### 6.6.2 碎片天气粒子优化
```java
// 只在玩家视野范围内生成
private static boolean isInPlayerView(Player player, Vec3 pos) {
    Vec3 look = player.getLookAngle();
    Vec3 toPos = pos.subtract(player.getEyePosition());
    return look.dot(toPos.normalize()) > 0.5;  // 视野内
}

// 根据距离调整粒子密度
private static int getParticleCount(Player player, double distance) {
    if (distance > 30) return 0;
    if (distance > 20) return 1;
    if (distance > 10) return 2;
    return 3;
}
```

#### 6.6.3 维度天气同步
确保维度内天气统一（重要！）：

```java
// 每个维度独立维护天气状态
public static void syncWeatherInDimension(ServerLevel level) {
    WeatherState state = weatherByDimension.get(level.dimension());
    if (state != null) {
        state.applyToLevel(level);
    }
}

// 在维度加载时同步
@SubscribeEvent
pub✅ **使用 MC 原版天气**：钓鱼和作物系统依赖 `level.isRaining()`
2. ✅ **维度天气统一**：同一维度内所有位置天气相同
3. ✅ **Java 端管理**：在 Java 中维护天气状态，调用原版 API
4. 🎨 **额外视觉效果**：只需添加星露谷特有的碎片天气粒子
5. ⚡ **性能优秀**：大部分使用原版系统，开销极小

### 7.2 MC 实现方案（简化版）

#### ✅ 已经可以使用的（无需改动）
1. **雨雪效果** - MC 原版 `/weather rain/clear/thunder`
2. **作物浇水** - `level.isRainingAt(pos)` 已在 FarmBlockMixin 中使用
3. **钓鱼天气检测** - `level.isRaining()` 已在 FishingDataManager 中使用

#### 🔨 需要实现的（Java 端）
1. **天气管理器** - 维护每个维度的天气状态
2. **每日更新** - 在睡觉后触发，应用星露谷天气规则
3. **碎片天气粒子** - 春天花瓣、秋天落叶的客户端渲染
4. **电视预报UI** - 显示明天天气的 GUI 界面
5. **HUD 天气图标** - 屏幕右上角显示当前天气

### 7.3 实现优先级

#### 🔴 高优先级（核心功能）
1. **WeatherManager** - Java 天气状态管理
2. **每日天气更新** - 监听睡觉事件，应用规则
3. **MC 天气同步** - 调用 `setWeatherParameters()`
4. **维度天气统一** - 确保同维度天气一致

#### 🟡 中优先级（视觉增强）
1. **碎片天气粒子** - 春天/秋天的叶子效果
2. **电视天气预报** - GUI 显示明天天气
3. **HUD 天气图标** - 实时显示当前天气

#### 🟢 低优先级（可选）
1. **闪电避雷针** - 雷暴天击中避雷针生成电池
2. **天气音效** - 风声等环境音（原版已有雨声）
3. **节日天气** - 节日强制晴天等特殊规则

### 7.4 技术优势
- **兼容性好**：使用原版 API，不会破坏钓鱼/作物系统
- **性能优秀**：原版天气系统经过充分优化
- **维护简单**：只需管理天气状态，不用重写渲染
- **扩展性强**：可以轻松添加新的天气类型

### 7.5 推荐实现路径
```
第1步：创建 WeatherManager 类
   ↓
第2步：监听睡觉事件，触发 updateWeatherForNewDay()
   ↓
第3步：应用星露谷天气规则（月初晴天、第3天下雨等）
   ↓
第4步：调用 MC 原版 setWeatherParameters() 同步天气
   ↓
第5步：添加碎片天气粒子渲染（客户端）
   ↓
第6步：实现电视预报 GUI
   ↓
第7步：添加 HUD 天气图标
```

### 7.6 核心代码框架

```java
// 1. 天气管理器
public class WeatherManager {
    private static final Map<ResourceKey<Level>, WeatherState> WEATHER_MAP = new HashMap<>();
    
    // 每日更新
    public static void updateWeatherForNewDay(ServerLevel level, GameData gameData) {
        WeatherState state = WEATHER_MAP.computeIfAbsent(level.dimension(), k -> new WeatherState());
        state.updateForNewDay(gameData.getDayOfMonth(), gameData.getSeason(), gameData.getDaysPlayed());
        state.applyToLevel(level);  // 调用 MC 原版 API
    }
    
    // 获取当前天气（供钓鱼等系统使用）
    public static boolean isRaining(Level level) {
        return level.isRaining();  // 直接用原版
    }
}

// 2. 监听睡觉事件
@SubscribeEvent
public static void onWakeUp(PlayerWakeUpEvent event) {
    if (event.getEntity().level() instanceof ServerLevel level) {
        GameData gameData = GameDataManager.getGameData(level);
        WeatherManager.updateWeatherForNewDay(level, gameData);
    }
}

// 3. 客户端粒子渲染
@SubscribeEvent
public static void onClientTick(TickEvent.ClientTickEvent event) {
    if (WeatherManager.isDebrisWeather()) {
        renderDebrisParticles();  // 花瓣/落叶
    }
}
```

**结论：使用 MC 原版天气系统 + Java 端管理，简单、高效、兼容性好！**
```mcfunction
execute if score @s stardew.weather matches 0 run title @s actionbar {"text":"A","font":"stardew:weather_icons"}
execute if score @s stardew.weather matches 1 run title @s actionbar {"text":"B","font":"stardew:weather_icons"}
```

### 6.5 天气影响游戏玩法

#### 6.5.1 作物浇水
```mcfunction
# 雨天自动浇水所有作物
execute if score #is_raining stardew.weather matches 1 as @e[type=marker,tag=stardew.crop] run tag @s add watered
```

#### 6.5.2 雷电击中
```mcfunction
# 检测避雷针范围内的闪电
execute as @e[type=lightning_bolt] at @s as @e[type=marker,tag=stardew.lightning_rod,distance=..10,limit=1,sort=nearest] run function stardew:items/lightning_rod/struck
```

#### 6.5.3 天气特定事件
```mcfunction
# 雷暴天在海滩找到鹦鹉螺壳
execute if score #is_lightning stardew.weather matches 1 as @e[type=marker,tag=stardew.beach_forage_spawn] run data modify entity @s data.loot_table set value "stardew:forage/beach_storm"
```

### 6.6 优化建议

#### 6.6.1 粒子优化
- 只在玩家视野范围内生成粒子
- 根据玩家设置调整粒子密度
- 使用区块加载检测避免在未加载区域生成

```mcfunction
# 检查玩家是否在户外
execute as @a at @s if block ~ ~1 ~ #stardew:transparent run tag @s add in_outdoor
execute as @a at @s unless block ~ ~1 ~ #stardew:transparent run tag @s remove in_outdoor

# 只对户外玩家生成粒子
execute if score #weather stardew.weather matches 1 as @a[tag=in_outdoor] at @s run function stardew:weather/effects/rain
```

#### 6.6.2 性能考虑
- 天气更新每天只运行一次（在 sleep 后）
- 视觉效果使用 schedule 分散执行
- 使用 predicate 替代复杂的 execute if 链

```mcfunction
# predicates/stardew/weather/is_raining.json
{
  "condition": "minecraft:entity_scores",
  "entity": "this",
  "scores": {
    "stardew.is_raining": {
      "min": 1
    }
  }
}

# 使用 predicate
execute as @a[predicate=stardew:weather/is_raining] at @s run ...
```

## 7. 总结

### 7.1 关键设计要点
1. **分离预测和应用**：天气预测（WeatherForTomorrow）与当前天气（Weather）分离
2. **位置独立**：每个位置上下文有独立的天气状态
3. **规则优先级**：特殊日期规则 > 随机天气条件
4. **视觉反馈丰富**：雨滴、碎片、闪电都有详细的动画系统
5. **性能优化**：只在室外且玩家可见区域渲染效果

### 7.2 MC 实现核心功能优先级
1. **高优先级**：
   - 基础天气切换系统
   - MC 原生天气同步
   - 每日天气预测
   - 特殊日期规则

2. **中优先级**：
   - 碎片天气粒子效果
   - 电视天气预报UI
   - HUD天气图标
   - 作物浇水影响

3. **低优先级**：
   - 复杂的风力系统
   - 详细的雨滴动画
   - 闪电特效优化
   - 被动节日天气

### 7.3 技术挑战
- **粒子效果**：MC 粒子系统与星露谷的精灵系统差异大
- **动画细节**：碎片的旋转飘动很难用MC粒子完美复现
- **屏幕闪光**：需要使用shader或特殊效果实现闪电闪光
- **UI显示**：电视屏幕动画需要创新方案（text display/item display）

### 7.4 推荐实现路径
1. 先实现基础的天气类型切换和规则系统
2. 同步MC原生天气（rain/thunder/clear）
3. 添加简单的粒子效果（碎片天气）
4. 实现电视天气预报UI
5. 添加天气对游戏玩法的影响
6. 最后优化视觉效果和性能
