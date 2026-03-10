# 星露谷天气系统实现详解

## 📋 系统概述

天气系统完全复刻星露谷物语的天气机制，使用 **Minecraft 原版天气系统** 作为底层支持，确保与钓鱼、作物等系统完美兼容。

---

## 🌦️ 天气类型 (7种)

| 天气类型 | 英文名 | MC原版对应 | 视觉效果 | 季节 |
|---------|--------|-----------|---------|------|
| ☀️ 晴天 | Sun | 晴朗 | 默认天空 | 全季节 |
| 🌧️ 雨天 | Rain | 下雨 | 雨滴粒子 | 春/夏/秋 |
| ⛈️ 雷暴 | Storm | 雷暴 | 雨+闪电 | 夏季 |
| ❄️ 雪天 | Snow | 下雨(冬季) | 雪花粒子 | 冬季 |
| 🌸  春季微风 | WindSpring | 晴朗 | 樱花花瓣飘落 | 春季 |
| 🍂  秋季微风 | WindFall | 晴朗 | 落叶飘落 | 秋季 |
| 🎉 节日 | Festival | 晴朗 | 强制晴天 | 节日当天 |

---

## 🎲 天气概率系统

### 基础概率（每个季节不同）

#### 🌱 春季 (Spring)
- 晴天: **40%**
- 雨天: **40%**
- 春季微风: **20%**

#### ☀️ 夏季 (Summer)
- 晴天: **80%**
- 雨天: **18%**
- 雷暴: **2%**

#### 🍂 秋季 (Fall)
- 晴天: **40%**
- 雨天: **40%**
- 秋季微风: **20%**

#### ❄️ 冬季 (Winter)
- 晴天: **50%**
- 雪天: **50%**

### 概率计算逻辑

```java
// WeatherManager.predictTomorrowWeather()
// 使用世界种子 + 游戏天数作为随机种子
Random random = new Random(level.getSeed() + daysPlayed);
double roll = random.nextDouble();

// 春季示例
if (roll < 0.40) return "Sun";
else if (roll < 0.80) return "Rain";
else return "WindSpring";
```

---

## 📅 天气修改规则（优先级高于概率）

天气系统在预测概率之后，会应用以下强制规则：

### 规则1: 每月第一天必定晴天
```java
if (dayOfMonth == 1 || daysPlayed <= 4) {
    weather = "Sun";
}
```
- 每个季节的第1天强制晴天
- 游戏前4天新手教程期间也是晴天

### 规则2: 第3天必定下雨（教程）
```java
if (daysPlayed == 3) {
    weather = "Rain";
}
```
- 完全复刻星露谷物语的新手教程

### 规则3: 夏季第13天和第26天必定雷暴
```java
if ("summer".equalsIgnoreCase(season) && (dayOfMonth == 13 || dayOfMonth == 26)) {
    weather = "Storm";
}
```
- 夏季13日: 第一次雷暴
- 夏季26日: 第二次雷暴

### 规则4: 碎片天气根据季节自动转换
```java
if (weather.startsWith("Wind")) {
    if ("spring".equalsIgnoreCase(season)) {
        weather = "WindSpring";
    } else if ("fall".equalsIgnoreCase(season)) {
        weather = "WindFall";
    }
}
```

---

## 🔄 天气更新流程

### 1. 每日时间推进时
```
StardewTimeManager.advanceDay()
  ↓
WeatherManager.updateWeatherForNewDay()  // 预测明天
  ↓
WeatherManager.predictTomorrowWeather()  // 使用概率
  ↓
WeatherManager.getWeatherModificationsForDate()  // 应用规则
  ↓
保存到 weatherForTomorrow
```

### 2. 新的一天开始时
```
StardewTimeManager.advanceDay()
  ↓
WeatherManager.applyWeatherForNewDay()
  ↓
weatherType = weatherForTomorrow  // 明天变今天
  ↓
WeatherState.applyToLevel()  // 应用到MC世界
  ↓
level.setWeatherParameters(...)  // MC原版API
```

### 3. 持续同步（每秒一次）
```
WeatherManager.onLevelTick()
  ↓
每20 ticks检查一次
  ↓
确保MC天气与系统状态一致
```

---

## 🎨 视觉效果实现

### MC原版天气（Rain, Storm, Snow）
```java
// WeatherState.applyToLevel()
case "Rain":
    level.setWeatherParameters(0, 6000, true, false);
    // clearTime=0, rainTime=6000, raining=true, thundering=false
    break;

case "Storm":
    level.setWeatherParameters(0, 6000, true, true);
    // 雨 + 闪电
    break;

case "Snow":
    level.setWeatherParameters(0, 6000, true, false);
    // 冬季生物群系会显示为雪
    break;
```

### 自定义粒子效果（WindSpring, WindFall）

#### 春季樱花花瓣
```java
// WeatherDebrisRenderer.java
@SubscribeEvent
public static void onClientTick(ClientTickEvent.Post event) {
    if (WeatherManager.isSpringWind(level)) {
        spawnCherryLeaves(player, level);
        // 生成粉色樱花花瓣粒子
        // 使用 CHERRY_LEAVES 方块粒子
    }
}
```

#### 秋季落叶
```java
if (WeatherManager.isFallWind(level)) {
    spawnFallLeaves(player, level);
    // 生成橙色/黄色落叶粒子
    // 使用 FALLING_BLOCK 粒子
}
```

**粒子特性:**
- 每2 ticks生成2-3个粒子
- 在玩家周围24格范围内随机位置
- 从高处（玩家上方4-12格）缓慢飘落
- 只在室外生成（检查头顶是否有方块）

---

## 🔧 技术实现细节

### 数据结构
```java
// WeatherState - 每个维度的天气状态
static class WeatherState {
    String weatherType;           // 当前天气
    String weatherForTomorrow;    // 明天天气预测
    int monthlyNonRainyDayCount; // 本月非雨天计数
    
    void applyToLevel(ServerLevel level) {
        // 将天气应用到MC世界
    }
}

// 维度级别存储
Map<ResourceKey<Level>, WeatherState> weatherByDimension
```

### 与钓鱼系统集成
```java
// FishingDataManager.java - 检测雨天
boolean isRaining = level.isRaining();  // ✅ 使用MC原版API

// 天气系统确保：
// Rain → level.isRaining() = true
// Storm → level.isRaining() = true
// Snow → level.isRaining() = true
// Wind → level.isRaining() = false
```

### 与作物系统集成
```java
// FarmBlockMixin.java - 作物浇水
boolean isRaining = level.isRainingAt(pos);  // ✅ 使用MC原版API

// 雨天自动浇水：
// Rain/Storm/Snow → 作物自动补充水分
```

### HUD显示
```java
// StardewTimeHud.java
String currentWeather = WeatherManager.getCurrentWeather(mc.level);
ResourceLocation weatherIcon = getWeatherIcon(currentWeather);
graphics.blit(weatherIcon, ...);  // 渲染天气图标
```

---

## 🛠️ Debug指令

### 查看当前天气
```
/stardewweather get
```
显示：当前日期、今天天气、明天天气、MC原版天气状态

### 设置天气
```
/stardewweather set <天气类型>
```
可用天气：`sun`, `rain`, `storm`, `snow`, `windspring`, `windfall`, `festival`

示例：
```
/stardewweather set storm     # 设置为雷暴
/stardewweather set windspring # 设置为春季微风
```

### 查看明天预测
```
/stardewweather tomorrow
```

### 测试概率分布
```
/stardewweather test
```
模拟当前季节100天的天气分布，验证概率是否正确

### 显示帮助
```
/stardewweather help
```

---

## ✅ 与原版对齐情况

| 原版特性 | 实现状态 | 备注 |
|---------|---------|------|
| 7种天气类型 | ✅ 完全实现 | Sun/Rain/Storm/Snow/WindSpring/WindFall/Festival |
| 季节概率差异 | ✅ 完全实现 | 春40/40/20, 夏80/18/2, 秋40/40/20, 冬50/50 |
| 每月1日晴天 | ✅ 完全实现 | 强制晴天规则 |
| 教程第3天下雨 | ✅ 完全实现 | 新手教程 |
| 夏季固定雷暴 | ✅ 完全实现 | 第13天和第26天 |
| 春季樱花飘落 | ✅ 完全实现 | 客户端粒子效果 |
| 秋季落叶飘落 | ✅ 完全实现 | 客户端粒子效果 |
| 节日强制晴天 | ⚠️ 部分实现 | 需要节日系统支持 |
| TV天气预报 | ❌ 未实现 | 计划中 |
| 绿雨天气 | ❌ 未实现 | 1.6新特性，暂不支持 |

---

## 🎯 核心优势

1. **完全兼容MC系统**: 使用 `level.isRaining()` 确保所有原版机制正常工作
2. **维度独立**: 每个维度有独立的天气状态
3. **确定性**: 使用世界种子保证相同种子相同天气序列
4. **可调试**: 提供完整的debug指令
5. **可扩展**: 轻松添加新天气类型或修改规则

---

## 📝 代码位置

- **核心逻辑**: `WeatherManager.java`
- **粒子渲染**: `WeatherDebrisRenderer.java`
- **HUD显示**: `StardewTimeHud.java`
- **Debug指令**: `WeatherDebugCommand.java`
- **时间集成**: `StardewTimeManager.java` (advanceDay方法)

---

## 🔮 未来计划

- [ ] TV天气预报GUI
- [ ] 节日系统集成
- [ ] 绿雨天气（1.6新特性）
- [ ] 天气过渡动画
- [ ] 配置文件支持（自定义概率）
- [ ] 天气影响NPC行为
