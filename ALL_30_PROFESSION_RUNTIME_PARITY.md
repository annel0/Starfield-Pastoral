# 30 个职业运行逻辑与原版对齐清单

本清单把 `ProfessionType` 中的每个职业映射为：
- 当前仓库里的实际运行行为
- 主要代码位置
- 与星露谷原版对比
- 状态（`已实现` / `部分实现` / `未实现`）

职业枚举来源：`src/main/java/com/stardew/craft/player/ProfessionType.java`

## 农业（6）

| 职业 | 当前运行逻辑 | 主要代码位置 | 与原版对比 | 状态 |
|---|---|---|---|---|
| RANCHER | 动物产品售价乘区 `x1.20`。 | `src/main/java/com/stardew/craft/economy/sell/ProfessionSellPriceService.java` | 与原版 Rancher（动物产品 +20%）一致。 | 已实现 |
| TILLER | 作物售价乘区 `x1.10`（不再覆盖种子）。 | `src/main/java/com/stardew/craft/economy/sell/ProfessionSellPriceService.java` | 与原版 Tiller（作物 +10%）一致。 | 已实现 |
| COOPMASTER | 鸡舍动物抚摸友好度加成、动物日更职业分支加成，并为鸡舍内孵化器提供孵化时间减半。 | `src/main/java/com/stardew/craft/manager/AnimalGrowthManager.java`，`src/main/java/com/stardew/craft/entity/animal/BaseCoopAnimalEntity.java`，`src/main/java/com/stardew/craft/blockentity/IncubatorBlockEntity.java` | 对齐原版 Coopmaster 两个核心效果：鸡舍动物好感提升更快、孵化更快。 | 已实现 |
| SHEPHERD | 畜棚动物抚摸友好度加成；绵羊按职业分支缩短产出周期。 | `src/main/java/com/stardew/craft/manager/AnimalGrowthManager.java`，`src/main/java/com/stardew/craft/entity/animal/BaseCoopAnimalEntity.java` | 对齐原版 Shepherd 核心效果：畜棚动物好感更快、羊毛产出更快。 | 已实现 |
| ARTISAN | 工匠品售价乘区 `x1.40`。 | `src/main/java/com/stardew/craft/economy/sell/ProfessionSellPriceService.java` | 与原版 Artisan（工匠品 +40%）一致。 | 已实现 |
| AGRICULTURIST | 作物生长速度额外 `+10%`（并入每日生长计算）。 | `src/main/java/com/stardew/craft/block/crop/StardewCropBlock.java` | 与原版 Agriculturist 生长加速方向一致。 | 已实现 |

## 钓鱼（6）

| 职业 | 当前运行逻辑 | 主要代码位置 | 与原版对比 | 状态 |
|---|---|---|---|---|
| FISHER | 鱼类售价乘区 `x1.25`（若无 Angler 覆盖）。 | `src/main/java/com/stardew/craft/economy/sell/ProfessionSellPriceService.java` | 与原版 Fisher（鱼价 +25%）一致。 | 已实现 |
| TRAPPER | 暂无运行时效果，仅保留 TODO，原因是制造/配方系统尚未开始。 | `TRAPPER_PROFESSION_TODO.md` | 原版会改蟹笼造价；当前缺失。 | 未实现 |
| ANGLER | 鱼类售价乘区 `x1.50`（高于 Fisher）。 | `src/main/java/com/stardew/craft/economy/sell/ProfessionSellPriceService.java` | 与原版 Angler（鱼价 +50%）一致。 | 已实现 |
| PIRATE | 钓鱼宝箱概率额外增加一次基础值（`+0.15`）。 | `src/main/java/com/stardew/craft/fishing/server/FishingSession.java` | 与原版公式方向一致（Pirate 叠加基础宝箱率）。 | 已实现 |
| MARINER | 蟹笼日更新中将垃圾概率固定为 `0.0`。 | `src/main/java/com/stardew/craft/blockentity/CrabPotBlockEntity.java` | 与原版 Mariner（蟹笼不出垃圾）一致。 | 已实现 |
| LUREMASTER | 蟹笼可无饵工作，且不消耗鱼饵。 | `src/main/java/com/stardew/craft/blockentity/CrabPotBlockEntity.java` | 与原版 Luremaster 意图一致。 | 已实现 |

## 觅食（6）

| 职业 | 当前运行逻辑 | 主要代码位置 | 与原版对比 | 状态 |
|---|---|---|---|---|
| FORESTER | 非硬木树在倒树/树桩掉落路径中，木材数量乘 `1.25`。 | `src/main/java/com/stardew/craft/event/WildTreeChopEvents.java` | 与原版 Forester（木材 +25%）一致。 | 已实现 |
| GATHERER | 暂未发现明确的运行时职业分支。 | （除枚举/升级流程外未发现有效运行引用） | 原版有采集双倍概率；当前缺失。 | 未实现 |
| LUMBERJACK | 非硬木树有概率额外掉硬木（`25%` 概率 `+1`）。 | `src/main/java/com/stardew/craft/event/WildTreeChopEvents.java` | 与原版 Lumberjack 核心效果一致（普通树可额外掉硬木）。 | 已实现 |
| TAPPER | 树液/树脂类物品售价乘区 `x1.25`。 | `src/main/java/com/stardew/craft/economy/sell/ProfessionSellPriceService.java` | 与原版 Tapper（树液类 +25%）方向一致。 | 已实现 |
| BOTANIST | 暂未发现明确的运行时职业分支。 | （除枚举/升级流程外未发现有效运行引用） | 原版采集品质固定铱星；当前缺失。 | 未实现 |
| TRACKER | 暂未发现明确的运行时职业分支。 | （除枚举/升级流程外未发现有效运行引用） | 原版可显示采集点/蚯蚓点线索；当前缺失。 | 未实现 |

## 采矿（6）

| 职业 | 当前运行逻辑 | 主要代码位置 | 与原版对比 | 状态 |
|---|---|---|---|---|
| MINER | 矿点掉落数量公式含额外 `+1` 基础矿石。 | `src/main/java/com/stardew/craft/event/MinePickaxeEvents.java` | 与原版 Miner（每矿脉 +1 矿）意图一致。 | 已实现 |
| GEOLOGIST | 宝石矿有 50% 概率额外 `+1` 掉落（等价于双倍宝石概率）。 | `src/main/java/com/stardew/craft/event/MinePickaxeEvents.java` | 与原版 Geologist（宝石双倍概率）一致。 | 已实现 |
| BLACKSMITH | 金属锭售价乘区 `x1.50`。 | `src/main/java/com/stardew/craft/economy/sell/ProfessionSellPriceService.java` | 与原版 Blacksmith（金属锭 +50%）一致。 | 已实现 |
| PROSPECTOR | 石头/矿物路径中的煤炭掉率翻倍。 | `src/main/java/com/stardew/craft/event/MinePickaxeEvents.java` | 与原版 Prospector（煤炭概率翻倍）一致。 | 已实现 |
| EXCAVATOR | 晶球掉落概率乘区翻倍。 | `src/main/java/com/stardew/craft/event/MinePickaxeEvents.java` | 与原版 Excavator（晶球概率翻倍）一致。 | 已实现 |
| GEMOLOGIST | 宝石售价乘区 `x1.30`。 | `src/main/java/com/stardew/craft/economy/sell/ProfessionSellPriceService.java` | 与原版 Gemologist（宝石 +30%）一致。 | 已实现 |

## 战斗（6）

| 职业 | 当前运行逻辑 | 主要代码位置 | 与原版对比 | 状态 |
|---|---|---|---|---|
| FIGHTER | 全局职业伤害乘区包含 `x1.10`。 | `src/main/java/com/stardew/craft/combat/DamageCalculator.java` | 与原版 Fighter（伤害 +10%）一致。 | 已实现 |
| SCOUT | 暴击率乘区 `x1.5`。 | `src/main/java/com/stardew/craft/combat/DamageCalculator.java` | 与原版 Scout（暴击率 +50%）一致。 | 已实现 |
| BRUTE | 额外职业伤害乘区 `x1.15`。 | `src/main/java/com/stardew/craft/combat/DamageCalculator.java` | 与原版 Brute（伤害 +15%）一致。 | 已实现 |
| DEFENDER | 通过职业即时增益将最大生命值 `+25`，并同步恢复生命。 | `src/main/java/com/stardew/craft/player/PlayerStardewData.java` | 与原版 Defender（最大生命 +25）一致。 | 已实现 |
| ACROBAT | 武器技能冷却减半（`duration / 2`，最少 1 tick）。 | `src/main/java/com/stardew/craft/combat/skill/WeaponSkillCooldowns.java` | 与原版 Acrobat 冷却缩减方向一致。 | 已实现 |
| DESPERADO | 在基础暴击倍率后再翻倍。 | `src/main/java/com/stardew/craft/combat/DamageCalculator.java` | 与原版 Desperado（暴击更致命）方向一致。 | 已实现 |

## 汇总

- 职业总数：`30`
- 已实现：`26`
- 部分实现：`0`
- 未实现：`4`

未实现职业：
- `TRAPPER`
- `GATHERER`
- `BOTANIST`
- `TRACKER`

## 主要阻塞

1. 制造/配方系统尚未接入“按职业切换配方”能力（阻塞 `TRAPPER`）。
2. 觅食相关的质量强制与世界线索提示子系统尚未接线（阻塞 `GATHERER`/`BOTANIST`/`TRACKER`）。
