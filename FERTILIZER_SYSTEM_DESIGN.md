# 星露谷化肥系统设计文档

## 概述
根据星露谷物语源代码分析，肥料系统通过 `HoeDirt` 类实现，存储在耕地上并影响作物生长。

## 肥料类型

### 1. **品质肥料**（Quality Fertilizer）
提高作物品质概率：

| ID | 名称 | 品质等级 | 效果 |
|----|------|---------|------|
| 368 | 初级肥料 | 1 | 提高银星/金星概率 |
| 369 | 高级肥料 | 2 | 大幅提高品质 |
| 919 | 顶级肥料 | 3 | 保证最低银星，可出铱星 |

### 2. **生长激素**（Speed-Gro）
加快作物成长速度：

| ID | 名称 | 加速比例 |
|----|------|---------|
| 465 | 生长激素 | 10% |
| 466 | 高级生长激素 | 25% |
| 918 | 顶级生长激素 | 33% |

### 3. **保湿土壤**（Retaining Soil）
隔夜保持湿润：

| ID | 名称 | 保水概率 |
|----|------|---------|
| 370 | 初级保湿土壤 | 33% |
| 371 | 高级保湿土壤 | 66% |
| 920 | 顶级保湿土壤 | 100% |

## 核心实现机制

### 1. 肥料存储
```java
// HoeDirt 类中
public String fertilizer; // 存储肥料 ID (如 "368", "(O)465")
```

### 2. 品质计算（收获时）
```csharp
// Crop.harvest() 方法
int fertilizerQualityLevel = soil.GetFertilizerQualityBoostLevel();
double chanceForGoldQuality = 0.2 * (farmingLevel / 10.0) + 
                               0.2 * fertilizerQualityLevel * ((farmingLevel + 2.0) / 12.0) + 
                               0.01;
double chanceForSilverQuality = Math.Min(0.75, chanceForGoldQuality * 2.0);

// 顶级肥料特殊处理：有机会产出铱星
if (fertilizerQualityLevel >= 3 && random < chanceForGoldQuality / 2.0) {
    cropQuality = 4; // 铱星
}
```

### 3. 生长速度加成（种植时应用）
```csharp
// applySpeedIncreases() 方法
float speedIncrease = GetFertilizerSpeedBoost();
int daysToRemove = (int)Math.Ceiling(totalDaysOfCropGrowth * speedIncrease);

// 从各成长阶段减少天数
while (daysToRemove > 0) {
    for (int j = 0; j < crop.phaseDays.Count; j++) {
        if (crop.phaseDays[j] > 0 && crop.phaseDays[j] != 99999) {
            crop.phaseDays[j]--;
            daysToRemove--;
        }
    }
}
```

### 4. 保水逻辑（每日更新）
```csharp
// dayUpdate() 方法
if (!random.NextBool(GetFertilizerWaterRetentionChance())) {
    state = 0; // 变干
} else {
    // 保持湿润状态
}
```

## 使用规则

### 施用限制
1. **每格耕地只能有一种肥料**
2. **生长激素必须在种子发芽前使用**（ID 368, 369 检查 `crop.currentPhase != 0`）
3. **品质肥料可以随时使用**
4. **保湿土壤可以随时使用**

### 肥料持久性
- ✅ 温室中永久保留（除非用镐移除）
- ✅ 跨季作物保留肥料
- ❌ 换季时消失（除非有作物）
- ❌ 作物死亡后消失

### 检查逻辑
```csharp
public HoeDirtFertilizerApplyStatus CheckApplyFertilizerRules(string fertilizerId) {
    if (HasFertilizer()) {
        if (fertilizerId == this.fertilizer) {
            return HasThisFertilizer; // 已有相同肥料
        }
        return HasAnotherFertilizer; // 已有不同肥料
    }
    
    // 生长激素需要在发芽前使用
    if (crop != null && crop.currentPhase != 0 && 
        (fertilizerId == "368" || fertilizerId == "369")) {
        return CropAlreadySprouted;
    }
    
    return Okay;
}
```

## 品质概率表（部分）

### 无肥料（耕种等级 10）
- 普通：46%
- 银星：33%
- 金星：21%

### 初级肥料（耕种等级 10）
- 普通：15%
- 银星：44%
- 金星：41%

### 高级肥料（耕种等级 10）
- 普通：10%
- 银星：29%
- 金星：61%

### 顶级肥料（耕种等级 10）
- 普通：0%
- 银星：11%
- 金星：48%
- 铱星：41%

## 实现清单

### Minecraft 模组需要实现：

1. ✅ **在 HoeDirt/FarmBlock 存储肥料 ID**
2. ✅ **品质计算公式**（收获时）
3. ✅ **生长加速逻辑**（种植时）
4. ✅ **保水概率系统**（每日更新）
5. ✅ **肥料使用规则检查**
6. ✅ **季节更替时清除肥料**
7. ✅ **温室特殊处理**
8. ⚠️ **肥料纹理渲染**（浇水前/后不同）

## 参考代码位置

- **HoeDirt.cs**: 第 62-96 行（常量定义）
- **HoeDirt.cs**: 第 1070-1135 行（肥料效果方法）
- **HoeDirt.cs**: 第 606-650 行（速度加成逻辑）
- **HoeDirt.cs**: 第 911-940 行（每日更新）
- **Crop.cs**: 第 560-650 行（品质计算）
