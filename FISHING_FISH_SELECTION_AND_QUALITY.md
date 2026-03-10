# 星露谷物语：选鱼逻辑与品质系统完整逆向

> 本文档基于 Stardew Valley 1.6.x 反编译源码 (C#)，用于 StardewCraft 模组还原。

---

## 📚 目录

1. [核心流程概览](#1-核心流程概览)
2. [Data/Fish 数据格式](#2-datafish-数据格式)
3. [Data/Locations 与 SpawnFishData 结构](#3-datalocations-与-spawnfishdata-结构)
4. [选鱼算法：GetFishFromLocationData](#4-选鱼算法getfishfromlocationdata)
5. [Data/Fish 二次校验：CheckGenericFishRequirements](#5-datafish-二次校验checkgenericfishrequirements)
6. [fishSize（尺寸百分比）计算](#6-fishsize尺寸百分比计算)
7. [fishQuality（品质）计算](#7-fishquality品质计算)
8. [完美捕获 (Perfect) 品质提升](#8-完美捕获-perfect-品质提升)
9. [特殊饵料/浮标/附魔的影响](#9-特殊饵料浮标附魔的影响)
10. [StardewCraft 实现建议](#10-stardewcraft-实现建议)

---

## 1. 核心流程概览

### 调用链

```
玩家咬钩 (isNibbling == true)
    ↓
FishingRod.DoFunction()
    ↓
location.getFish(nibbleAccum, baitId, waterDepth, who, baitPotency, bobberTile)
    ↓
GameLocation.GetFishFromLocationData(locationName, bobberTile, waterDepth, player, ...)
    ↓
遍历 SpawnFishData 规则列表，按 Precedence 排序 + 随机打散
    ↓
对每条规则：
    1) 检查基础过滤条件（Season, FishAreaId, PlayerPosition, BobberPosition, MinLevel, Depth 等）
    2) 计算 chance 并 roll
    3) 若通过，调用 ItemQueryResolver.TryResolveRandomItem(spawn, ...) 生成 Item
    4) 调用 CheckGenericFishRequirements() 二次校验 Data/Fish 的 时间/天气/概率/难度 等
    5) 通过 → 返回这条鱼
    ↓
FishingRod.startMinigameEndFunction(fish)
    ↓
计算 fishSize (0~1)、treasure、创建 BobberBar
    ↓
BobberBar 构造函数
    ↓
从 DataLoader.Fish 读取 difficulty, motionType, minFishSize, maxFishSize
    ↓
计算 fishQuality (基于 fishSize 和 bobber/bait 加成)
    ↓
小游戏结算 → rod.pullFishFromWater(fishId, fishSize, fishQuality, difficulty, ...)
    ↓
doPullFishFromWater：若 wasPerfect 则品质再提升
    ↓
创建最终 Item 并加入背包
```

---

## 2. Data/Fish 数据格式

`DataLoader.Fish(content)` 返回 `Dictionary<string, string>`，key 是 fish 的内部 ID (如 `"128"` 表示 Pufferfish)。

### 字段格式 (以 `/` 分隔)

| 索引 | 名称 | 类型 | 说明 |
|------|------|------|------|
| 0 | Name | string | 鱼的显示名称 |
| 1 | Difficulty | int | 难度值 (5~110+)，影响小游戏和训练竿可用性 |
| 2 | MotionType | string | `mixed`/`dart`/`smooth`/`floater`/`sinker` |
| 3 | MinSize | int | 最小尺寸（英寸） |
| 4 | MaxSize | int | 最大尺寸（英寸） |
| 5 | TimeSpans | string | 可钓时间段，格式 `startTime endTime [startTime endTime ...]` |
| 6 | Seasons | string | 可钓季节（space-separated），被 SpawnFishData 覆盖时可忽略 |
| 7 | Weather | string | `sunny`/`rainy`/`both`(空) |
| 8 | ? | string | (未确认用途) |
| 9 | MaxDepth | int | 用于概率衰减计算的"最大深度" |
| 10 | BaseChance | float | 基础出现概率 (0~1) |
| 11 | DepthMultiplier | float | 深度衰减系数 |
| 12 | MinFishingLevel | int | 最低钓鱼等级 |
| 13 | IsTutorialFish | bool | 是否作为新手教程鱼 (默认 false) |

### 示例

```
# Pufferfish (ID 128)
Pufferfish/80/floater/1/36/1200 1600/summer/sunny/690 .4 685/4/.3/.5/0
```

解析：
- 难度 = 80
- 运动类型 = floater
- 尺寸范围 = 1~36 英寸
- 可钓时间 = 12:00~16:00
- 季节 = summer
- 天气 = sunny
- (fields[8] = "690 .4 685")
- MaxDepth = 4
- BaseChance = 0.3
- DepthMultiplier = 0.5
- MinFishingLevel = 0

---

## 3. Data/Locations 与 SpawnFishData 结构

`Data/Locations` 是一个 `Dictionary<string, LocationData>`。

### LocationData (钓鱼相关字段)

```csharp
class LocationData {
    List<SpawnFishData> Fish;              // 该地点的刷鱼规则
    Dictionary<string, FishAreaData> FishAreas;  // 可选：鱼区划分
}
```

### SpawnFishData (刷鱼规则)

基于代码使用推断的完整字段表：

| 字段 | 类型 | 说明 |
|------|------|------|
| `Id` | string | 规则 ID（仅日志用） |
| `Precedence` | int | 优先级（数值越小越先尝试） |
| `ItemId` | string | 物品 ID 或 Item Query |
| `FishAreaId` | string? | 要求的鱼区 ID |
| `Season` | Season? | 季节限制（魔法饵忽略） |
| `CanBeInherited` | bool | 是否可被 `LOCATION_FISH` 继承 |
| `PlayerPosition` | Rectangle? | 玩家 tile 必须在此范围 |
| `BobberPosition` | Rectangle? | 浮标 tile 必须在此范围 |
| `MinFishingLevel` | int | 最低钓鱼等级 |
| `MinDistanceFromShore` | int | 最小离岸距离 (waterDepth) |
| `MaxDistanceFromShore` | int | 最大离岸距离，-1 表示不限 |
| `RequireMagicBait` | bool | 是否强制需要魔法饵 |
| `Condition` | string? | GameStateQuery 条件表达式 |
| `UseFishCaughtSeededRandom` | bool | 是否用 PreciseFishCaught 种子随机 |
| `ApplyDailyLuck` | bool | 是否把每日运气应用到概率 |
| `ChanceModifiers` | List&lt;QuantityModifier&gt; | 概率修正列表 |
| `ChanceModifierMode` | enum | 修正模式 |
| `CuriosityLureBuff` | float | 好奇拟饵加成（-1 表示用默认曲线） |
| `CatchLimit` | int | 该鱼可抓取上限（-1 表示不限） |
| `IgnoreFishDataRequirements` | bool | 是否跳过 Data/Fish 的二次校验 |
| `CanUseTrainingRod` | bool? | 训练竿能否钓到 |
| `SetFlagOnCatch` | string? | 捕获时设置的 mail flag |
| `IsBossFish` | bool | 是否为传说鱼 |

### 概率计算方法

```csharp
float GetChance(bool hasCuriosityLure, double dailyLuck, int luckLevel, 
                Func<float, IList<QuantityModifier>, QuantityModifierMode, float> applyModifiers,
                bool isTargetedBait)
```

原版会在这个方法内综合：
- 基础概率
- CuriosityLure 加成
- 运气相关加成
- 目标饵料 1.66x 加成

---

## 4. 选鱼算法：GetFishFromLocationData

### 源码位置
`GameLocation.cs` line 13882

### 算法步骤

```csharp
internal static Item GetFishFromLocationData(...) {
    // 1. 获取 LocationData
    LocationData locationData = location?.GetData() ?? GetData(locationName);
    
    // 2. 加载 Data/Fish 供后续校验
    Dictionary<string, string> allFishData = DataLoader.Fish(Game1.content);
    
    // 3. 获取当前季节
    Season season = Game1.GetSeasonForLocation(location);
    
    // 4. 获取当前 bobberTile 所在的 FishAreaId
    if (!location.TryGetFishAreaForTile(bobberTile, out fishAreaId, out _)) {
        fishAreaId = null;
    }
    
    // 5. 检测饵料/拟饵状态
    bool usingMagicBait = rod.HasMagicBait();
    bool hasCuriosityLure = rod.HasCuriosityLure();
    string baitTargetFish = null;  // SpecificBait 的目标鱼
    bool usingGoodBait = bait != null && bait.QualifiedItemId != "(O)685";  // 非普通饵
    
    // 6. 合并 Default + 当前地点的 Fish 规则
    IEnumerable<SpawnFishData> possibleFish = Game1.locationData["Default"].Fish
        .Concat(locationData.Fish);
    
    // 7. 按 Precedence 排序，同优先级随机打散
    possibleFish = possibleFish
        .OrderBy(p => p.Precedence)
        .ThenBy(_ => Game1.random.Next());
    
    // 8. 目标饵特殊逻辑
    int targetedBaitTries = 0;
    Item firstNonTargetFish = null;
    
    // 9. 双重循环（usingGoodBait 时第二轮有额外机会）
    for (int i = 0; i < 2; i++) {
        foreach (SpawnFishData spawn in possibleFish) {
            // === 快速过滤 ===
            if (isInherited && !spawn.CanBeInherited) continue;
            if (spawn.FishAreaId != null && fishAreaId != spawn.FishAreaId) continue;
            if (spawn.Season.HasValue && !usingMagicBait && spawn.Season != season) continue;
            if (spawn.PlayerPosition?.Contains(playerTile) == false) continue;
            if (spawn.BobberPosition?.Contains(bobberTile) == false) continue;
            if (player.FishingLevel < spawn.MinFishingLevel) continue;
            if (waterDepth < spawn.MinDistanceFromShore) continue;
            if (spawn.MaxDistanceFromShore > -1 && waterDepth > spawn.MaxDistanceFromShore) continue;
            if (spawn.RequireMagicBait && !usingMagicBait) continue;
            
            // === 概率 Roll ===
            float chance = spawn.GetChance(hasCuriosityLure, dailyLuck, luckLevel, ..., 
                                           spawn.ItemId == baitTargetFish);
            
            if (spawn.UseFishCaughtSeededRandom) {
                // 用 PreciseFishCaught 构造 seeded random
                if (!Utility.CreateRandom(...).NextBool(chance)) continue;
            } else {
                if (!Game1.random.NextBool(chance)) continue;
            }
            
            // === GameStateQuery 条件 ===
            if (spawn.Condition != null) {
                HashSet<string> ignoreKeys = usingMagicBait ? MagicBaitIgnoreQueryKeys : null;
                if (!GameStateQuery.CheckConditions(spawn.Condition, ..., ignoreKeys)) continue;
            }
            
            // === 生成物品 ===
            Item item = ItemQueryResolver.TryResolveRandomItem(spawn, ...);
            if (item == null) continue;
            
            // 设置特殊标记
            if (!string.IsNullOrWhiteSpace(spawn.SetFlagOnCatch)) {
                item.SetFlagOnPickup = spawn.SetFlagOnCatch;
            }
            if (spawn.IsBossFish) {
                item.SetTempData("IsBossFish", true);
            }
            
            // === CatchLimit 校验 ===
            if (spawn.CatchLimit > -1) {
                if (player.fishCaught.TryGetValue(item.QualifiedItemId, out values)) {
                    if (values[0] >= spawn.CatchLimit) continue;
                }
            }
            
            // === Data/Fish 二次校验 ===
            if (!CheckGenericFishRequirements(item, allFishData, ...)) continue;
            
            // === 目标饵逻辑 ===
            if (baitTargetFish != null && item.QualifiedItemId != baitTargetFish) {
                if (targetedBaitTries < 2) {
                    if (firstNonTargetFish == null) firstNonTargetFish = item;
                    targetedBaitTries++;
                    continue;  // 再试一次找目标鱼
                }
            }
            
            return item;  // ✅ 成功选中
        }
        
        // 只有 usingGoodBait 时才进行第二轮
        if (!usingGoodBait) i++;
    }
    
    // 回退：返回第一个非目标鱼，或教程鱼
    if (firstNonTargetFish != null) return firstNonTargetFish;
    if (isTutorialCatch) return ItemRegistry.Create("(O)145");  // Sunfish
    return null;
}
```

---

## 5. Data/Fish 二次校验：CheckGenericFishRequirements

### 源码位置
`GameLocation.cs` line 14030

### 校验项目

```csharp
internal static bool CheckGenericFishRequirements(Item fish, ...) {
    // 1. 非 Object 类型直接跳过（非标准鱼）
    if (!fish.HasTypeObject()) return !isTutorialCatch;
    
    // 2. 查找 Data/Fish 条目
    if (!allFishData.TryGetValue(fish.ItemId, out rawData)) return !isTutorialCatch;
    
    string[] fields = rawData.Split('/');
    
    // 3. 陷阱类物品（fields[1] == "trap"）不作为鱼
    if (fields[1] == "trap") return !isTutorialCatch;
    
    // 4. 训练竿难度限制
    if (isTrainingRod) {
        if (spawn.CanUseTrainingRod.HasValue) {
            if (spawn.CanUseTrainingRod != true) return false;
        } else {
            int difficulty = int.Parse(fields[1]);
            if (difficulty >= 50) return false;  // 训练竿只能钓难度 < 50 的鱼
        }
    }
    
    // 5. 教程鱼校验
    if (isTutorialCatch) {
        bool isTutorialFish = ArgUtility.GetOptionalBool(fields, 13, false);
        if (!isTutorialFish) return false;
    }
    
    // 6. 如果 IgnoreFishDataRequirements == true，跳过以下所有
    if (spawn.IgnoreFishDataRequirements) return true;
    
    // === 时间校验（魔法饵忽略）===
    if (!usingMagicBait) {
        string[] timeSpans = fields[5].Split(' ');
        bool found = false;
        for (int i = 0; i < timeSpans.Length; i += 2) {
            int start = int.Parse(timeSpans[i]);
            int end = int.Parse(timeSpans[i + 1]);
            if (Game1.timeOfDay >= start && Game1.timeOfDay < end) {
                found = true;
                break;
            }
        }
        if (!found) return false;
    }
    
    // === 天气校验（魔法饵忽略）===
    if (!usingMagicBait) {
        string weather = fields[7];
        if (weather == "rainy" && !location.IsRainingHere()) return false;
        if (weather == "sunny" && location.IsRainingHere()) return false;
    }
    
    // === 最低钓鱼等级 ===
    int minFishingLevel = int.Parse(fields[12]);
    if (player.FishingLevel < minFishingLevel) return false;
    
    // === 深度概率衰减 ===
    int maxDepth = int.Parse(fields[9]);
    float baseChance = float.Parse(fields[10]);
    float depthMultiplier = float.Parse(fields[11]);
    
    float dropOffAmount = depthMultiplier * baseChance;
    float chance = baseChance - Math.Max(0, maxDepth - waterDepth) * dropOffAmount;
    chance += player.FishingLevel / 50f;
    
    if (isTrainingRod) chance *= 1.1f;
    chance = Math.Min(chance, 0.9f);
    
    // === 好奇拟饵对低概率鱼的加成 ===
    if (chance < 0.25 && hasCuriosityLure) {
        if (spawn.CuriosityLureBuff > -1f) {
            chance += spawn.CuriosityLureBuff;
        } else {
            // 默认曲线：把低概率鱼拉到 ~0.08~0.17 区间
            float max = 0.25f, min = 0.08f;
            chance = (max - min) / max * chance + (max - min) / 2f;
        }
    }
    
    // === 目标饵料加成 ===
    if (usingTargetBait) chance *= 1.66f;
    
    // === 每日运气 ===
    if (spawn.ApplyDailyLuck) chance += player.DailyLuck;
    
    // === ChanceModifiers ===
    if (spawn.ChanceModifiers?.Count > 0) {
        chance = Utility.ApplyQuantityModifiers(chance, spawn.ChanceModifiers, spawn.ChanceModifierMode);
    }
    
    // === 最终 Roll ===
    return Game1.random.NextBool(chance);
}
```

---

## 6. fishSize（尺寸百分比）计算

### 源码位置
`FishingRod.startMinigameEndFunction()` line 891

### 计算公式

```csharp
float fishSize = 1f;

// 1. 离岸距离加成（clearWaterDistance 范围通常 0~5）
fishSize *= (float)clearWaterDistance / 5f;

// 2. 钓鱼等级随机项
int minimumSizeContribution = 1 + who.FishingLevel / 2;
fishSize *= (float)Game1.random.Next(minimumSizeContribution, Math.Max(6, minimumSizeContribution)) / 5f;

// 3. 偏爱饵料加成
if (favBait) {
    fishSize *= 1.2f;
}

// 4. ±10% 随机波动
fishSize *= 1f + (float)Game1.random.Next(-10, 11) / 100f;

// 5. Clamp 到 [0, 1]
fishSize = Math.Max(0f, Math.Min(1f, fishSize));
```

### fishSize 影响

1. **实际鱼尺寸**（英寸）：
   ```csharp
   int actualSize = minFishSize + (int)((maxFishSize - minFishSize) * fishSize);
   actualSize++;  // 额外 +1
   ```

2. **初始品质**（见下节）

---

## 7. fishQuality（品质）计算

### 品质等级

| 值 | 名称 | 显示 |
|-----|------|------|
| 0 | Normal | 无星 |
| 1 | Silver | 银星 |
| 2 | Gold | 金星 |
| 4 | Iridium | 铱星 |

### 初始品质（BobberBar 构造函数）

```csharp
// fishSize 是 0~1 的百分比
if (fishSize < 0.33) {
    fishQuality = 0;  // Normal
} else if (fishSize < 0.66) {
    fishQuality = 1;  // Silver
} else {
    fishQuality = 2;  // Gold
}
```

### 品质石浮标加成 (O)877

```csharp
// 每个品质石浮标 +1 品质（最高到 Iridium）
int qualityBobberCount = Utility.getStringCountInList(bobbers, "(O)877");
for (int i = 0; i < qualityBobberCount; i++) {
    fishQuality++;
    if (fishQuality > 2) {
        fishQuality = 4;  // 跳过 3，直接到 Iridium
    }
}
```

### 训练竿强制

```csharp
if (beginnersRod) {
    fishQuality = 0;  // 训练竿强制 Normal 品质
    fishSize = minFishSize;
}
```

---

## 8. 完美捕获 (Perfect) 品质提升

### 触发条件
在 BobberBar 中，如果整个小游戏过程中鱼**始终在绿条内**，则 `perfect = true`。

### 品质提升规则

```csharp
// doPullFishFromWater() 中
if (wasPerfect) {
    if (fishQuality >= 2) {
        fishQuality = 4;  // Gold → Iridium
    } else if (fishQuality >= 1) {
        fishQuality = 2;  // Silver → Gold
    }
    // Normal 不提升
}
```

### 品质提升表

| 原始品质 | 完美后品质 |
|----------|------------|
| Normal (0) | Normal (0) |
| Silver (1) | Gold (2) |
| Gold (2) | Iridium (4) |
| Iridium (4) | Iridium (4) |

---

## 9. 特殊饵料/浮标/附魔的影响

### 饵料 (Bait) - 槽位 0

| 物品 ID | 名称 | 效果 |
|---------|------|------|
| (O)685 | Bait | 普通饵，无特殊加成 |
| (O)774 | Wild Bait | 25% 概率双倍鱼 (+ DailyLuck/2) |
| (O)908 | Magic Bait | 忽略季节/时间/天气限制 |
| (O)ChallengeBait | Challenge Bait | 一次钓 3 条鱼 |
| (O)DeluxeBait | Deluxe Bait | 绿条 +12 高度 |
| (O)703 | ? | 增加宝箱概率 |
| (O)SpecificBait | Targeted Bait | 1.66x 目标鱼概率，尝试 2 次优先目标 |

### 浮标/拟饵 (Tackle) - 槽位 1+

| 物品 ID | 名称 | 效果 |
|---------|------|------|
| (O)691 | Barbed Hook | 条跟随鱼移动，重力减弱 |
| (O)692 | Dressed Spinner | 反弹衰减 |
| (O)693 | Trap Bobber | 宝箱 +概率，宝箱在条内时不扣进度 |
| (O)694 | Cork Bobber | 每个 -0.003→-0.001 惩罚速度 |
| (O)695 | Lead Bobber | 每个 +24 绿条高度 |
| (O)856 | Curiosity Lure | 低概率鱼提升 |
| (O)877 | Quality Bobber | 每个 +1 品质 |
| (O)SonarBobber | Sonar Bobber | 显示当前鱼的图标 |

### 钓鱼等级对绿条高度的影响

```csharp
int bobberBarHeight = 96 + player.FishingLevel * 8;  // 基础 96，每级 +8

// 训练竿新手加成
if (player.FishingLevel < 5 && beginnersRod) {
    bobberBarHeight += 40 - player.FishingLevel * 8;
}
```

---

## 10. StardewCraft 实现建议

### 10.1 数据结构改进

```java
// 扩展 SpawnFishRule，增加缺失字段
public record SpawnFishRule(
    String id,
    int precedence,
    String itemId,
    float chance,
    int difficulty,
    int motionType,
    int minFishingLevel,
    int minDistanceFromShore,
    int maxDistanceFromShore,
    
    // === 新增字段 ===
    String condition,           // 条件表达式（可简化或使用 Predicate）
    String season,              // 季节限制: "spring"/"summer"/"fall"/"winter"/null
    String weather,             // 天气限制: "sunny"/"rainy"/null
    int[] timeSpans,            // 可钓时间段 [start1, end1, start2, end2, ...]
    boolean isBossFish,         // 传说鱼标记
    int catchLimit,             // 捕获上限 (-1 = 无限)
    float curiosityLureBuff,    // 好奇拟饵加成 (-1 = 默认曲线)
    boolean ignoreFishDataReqs, // 跳过二次校验
    
    // === Fish Data 字段 (合并以简化) ===
    int maxDepth,               // 用于概率衰减
    float depthMultiplier       // 深度衰减系数
) { ... }
```

### 10.2 鱼品质计算类

```java
public final class FishQualityCalculator {
    
    /**
     * 计算 fishSize 百分比 (0~1)
     */
    public static float calculateFishSizePercent(
            int waterDepth,
            int fishingLevel,
            boolean hasFavoriteBait,
            RandomSource random) {
        
        float size = 1f;
        
        // 离岸距离加成
        size *= Math.min(waterDepth, 5) / 5f;
        
        // 钓鱼等级随机项
        int minContrib = 1 + fishingLevel / 2;
        size *= random.nextIntBetweenInclusive(minContrib, Math.max(6, minContrib)) / 5f;
        
        // 偏爱饵料
        if (hasFavoriteBait) {
            size *= 1.2f;
        }
        
        // ±10% 波动
        size *= 1f + random.nextIntBetweenInclusive(-10, 10) / 100f;
        
        return Math.max(0f, Math.min(1f, size));
    }
    
    /**
     * 根据 fishSize 百分比计算初始品质
     */
    public static int calculateInitialQuality(float fishSizePercent) {
        if (fishSizePercent < 0.33f) return 0;  // Normal
        if (fishSizePercent < 0.66f) return 1;  // Silver
        return 2;  // Gold
    }
    
    /**
     * 应用浮标加成
     */
    public static int applyBobberBonus(int quality, int qualityBobberCount) {
        for (int i = 0; i < qualityBobberCount; i++) {
            quality++;
            if (quality > 2) quality = 4;  // 跳到 Iridium
        }
        return quality;
    }
    
    /**
     * 应用完美捕获加成
     */
    public static int applyPerfectBonus(int quality, boolean wasPerfect) {
        if (!wasPerfect) return quality;
        if (quality >= 2) return 4;  // Gold → Iridium
        if (quality >= 1) return 2;  // Silver → Gold
        return quality;  // Normal 不变
    }
    
    /**
     * 计算实际鱼尺寸（英寸）
     */
    public static int calculateActualSize(int minSize, int maxSize, float sizePercent) {
        return minSize + (int)((maxSize - minSize) * sizePercent) + 1;
    }
}
```

### 10.3 选鱼逻辑改进

```java
public Optional<FishSelection> selectFish(
        ServerPlayer player,
        ServerLevel level,
        int waterDepth,
        int timeOfDay,        // 新增：MC 时间 (0-24000)
        boolean isRaining,    // 新增：天气
        String season,        // 新增：季节
        boolean hasMagicBait,
        boolean hasCuriosityLure,
        String targetBaitFish,
        RandomSource random) {
    
    // ... 获取规则列表，按 precedence 排序 + 同优先级随机打散 ...
    
    int targetBaitTries = 0;
    FishSelection firstNonTarget = null;
    
    for (SpawnFishRule rule : orderedRules) {
        // 1. 基础过滤
        if (!rule.matches(fishingLevel, waterDepth)) continue;
        
        // 2. 季节/天气/时间 (魔法饵忽略)
        if (!hasMagicBait) {
            if (rule.season() != null && !rule.season().equals(season)) continue;
            if ("rainy".equals(rule.weather()) && !isRaining) continue;
            if ("sunny".equals(rule.weather()) && isRaining) continue;
            if (!isInTimeSpan(timeOfDay, rule.timeSpans())) continue;
        }
        
        // 3. 概率计算
        float chance = rule.chance();
        chance = applyDepthPenalty(chance, rule, waterDepth);
        chance += fishingLevel / 50f;
        if (hasCuriosityLure && chance < 0.25f) {
            chance = applyCuriosityLureBonus(chance, rule);
        }
        if (targetBaitFish != null && targetBaitFish.equals(rule.itemId())) {
            chance *= 1.66f;
        }
        
        // 4. Roll
        if (random.nextFloat() >= chance) continue;
        
        // 5. 生成物品
        ItemStack stack = createFishStack(rule);
        if (stack.isEmpty()) continue;
        
        // 6. 目标饵逻辑
        if (targetBaitFish != null && !targetBaitFish.equals(rule.itemId())) {
            if (targetBaitTries < 2) {
                if (firstNonTarget == null) {
                    firstNonTarget = new FishSelection(stack, rule.difficulty(), rule.motionType());
                }
                targetBaitTries++;
                continue;
            }
        }
        
        return Optional.of(new FishSelection(stack, rule.difficulty(), rule.motionType()));
    }
    
    if (firstNonTarget != null) return Optional.of(firstNonTarget);
    return Optional.empty();
}
```

### 10.4 推荐实现路线

1. **M1：扩展数据结构**
   - 更新 `SpawnFishRule` 增加 season/weather/timeSpans/quality 相关字段
   - 创建 `FishQualityCalculator` 工具类
   - 更新 JSON 数据格式

2. **M2：选鱼逻辑完善**
   - 实现 MC 时间 → 星露谷时间映射
   - 实现季节系统或与 MC biome 挂钩
   - 实现天气检测

3. **M3：品质系统集成**
   - 在 `FishingSession` 中计算 fishSizePercent
   - 传递给客户端 BobberBar
   - 小游戏结算时计算最终品质

4. **M4：特殊物品支持**
   - 实现品质石浮标
   - 实现魔法饵/目标饵
   - 实现好奇拟饵

---

## 附录：关键源码引用

| 功能 | 文件 | 行号 |
|------|------|------|
| 选鱼入口 | `GameLocation.cs` | 13845 |
| 选鱼核心 | `GameLocation.cs` | 13882 |
| 二次校验 | `GameLocation.cs` | 14030 |
| fishSize 计算 | `FishingRod.cs` | 891 |
| 品质初始化 | `BobberBar.cs` | 186 |
| 完美加成 | `FishingRod.cs` | 1176 |
| 经验计算 | `FishingRod.cs` | 1190 |
