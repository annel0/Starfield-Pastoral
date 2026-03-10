# Stardew Valley 原版钓鱼系统：逆向笔记（用于 StardewCraft 还原）

> 目标：从原版 C# 逻辑中抽取“状态机 + 数值 + 数据源 + 结算/掉落”，再映射到 1.21.1 Fabric/Java 的实现。

## 1. 关键入口与文件

- 核心工具与状态机： [源代码/StardewValley.Tools/FishingRod.cs](源代码/StardewValley.Tools/FishingRod.cs)
- 钓鱼条小游戏 UI 与判定： [源代码/StardewValley.Menus/BobberBar.cs](源代码/StardewValley.Menus/BobberBar.cs)
- 节日“钓鱼大赛”小游戏（非核心但能看交互方式）： [源代码/StardewValley.Minigames/FishingGame.cs](源代码/StardewValley.Minigames/FishingGame.cs)

## 2. FishingRod 的状态机（原版）

在 [源代码/StardewValley.Tools/FishingRod.cs](源代码/StardewValley.Tools/FishingRod.cs) 中可以直接看到一组布尔字段承担状态机：

- `isTimingCast`：蓄力中（选择力度）
- `isCasting`：确定力度后进入抛竿动作
- `castedButBobberStillInAir`：浮标/抛物线飞行阶段（玩家可微调落点）
- `isFishing`：浮标已落水、等待咬钩
- `isNibbling`：鱼开始试探/咬钩窗口（有时间上限）
- `hit`：已“中鱼”并进入小游戏/后续流程（会受自动上钩等影响）
- `isReeling`：收线/拉起动画阶段
- `fishCaught`：已展示“钓上来了”的鱼在头顶，等待玩家确认收下
- `showingTreasure`：宝箱动画/开箱菜单阶段
- 结算相关：`treasureCaught`、`goldenTreasure`、`fishQuality`、`fishSize`、`whichFish`、`numberOfFishCaught`

### 2.1 resetState 清空一切

见 [源代码/StardewValley.Tools/FishingRod.cs](源代码/StardewValley.Tools/FishingRod.cs)：`resetState()` 会把上述全部状态与计时器归零，并清理网络事件队列与浮标位置。

### 2.2 DoFunction：同一个入口处理“落水开始等待”与“咬钩时收杆”

见 [源代码/StardewValley.Tools/FishingRod.cs](源代码/StardewValley.Tools/FishingRod.cs)：

- 当满足“未在钓鱼/未在飞行/未在开箱/未咬钩”等时：
  - 若落点可钓（`location.isTileFishable(tileX, tileY)`）：
    - `isFishing = true`
    - 计算 `clearWaterDistance = distanceToLand(...)`
    - `timeUntilFishingBite = calculateTimeUntilFishingBite(...)`
    - 若命中鱼群点（`fishSplashPoint`）会显著缩短咬钩时间
- 当 `isNibbling == true` 时：
  - 会调用 `location.getFish(...)` 生成“本次咬钩的鱼/垃圾”对象
  - 然后根据 `DataLoader.Fish` 中的难度字段判断是否为“可钓鱼对象”（否则按垃圾处理）
  - 后续会进入 BobberBar 或直接结算（取决于是否触发小游戏等）

## 3. 咬钩计时（bite time）公式

见 [源代码/StardewValley.Tools/FishingRod.cs](源代码/StardewValley.Tools/FishingRod.cs)：`calculateTimeUntilFishingBite(Vector2 bobberTile, bool isFirstCast, Farmer who)`。

关键点：

- 若在鱼塘建筑上钓鱼且鱼塘有鱼：直接返回 `FishPond.FISHING_MILLISECONDS`
- 否则：
  - 基础范围：`random(minFishingBiteTime, maxFishingBiteTime - 250*FishingLevel - reductionTime)`
  - `isFirstCast` 会把时间再乘 `0.75`
  - 若有饵料：整体再乘 `0.5`
    - 特定饵料进一步缩短（如 `ChallengeBait`、`DeluxeBait`）
  - 最低钳制：`>= 500ms`

## 4. 进入 BobberBar（钓鱼条小游戏）的入口与“宝箱概率”

见 [源代码/StardewValley.Tools/FishingRod.cs](源代码/StardewValley.Tools/FishingRod.cs)：`startMinigameEndFunction(Item fish)`。

### 4.1 fishSize（尺寸）不是 Data/Fish 的 min/max 直接随机，而是混合多个因素得到 0~1

- 与 `clearWaterDistance` 成正比
- 与“渔技等级”相关的一个随机项有关
- 若 `favBait`（偏爱饵）会乘 `1.2`
- 再乘一个 ±10% 的随机
- 最后 clamp 到 [0,1]

### 4.2 treasure（宝箱是否出现）的基础概率

见同函数：

- `baseChanceForTreasure = 0.15`
- 额外宝箱概率：某些拟饵（如 `(O)693`）会加成（按数量累加）
- 其他加成：
  - LuckLevel
  - 当日运气 DailyLuck
  - 特定饵（如 `(O)703`）
  - 特定职业（如 professions 包含 9）

最后：`Game1.activeClickableMenu = new BobberBar(...)`，把 fish id、fishSize(0~1)、是否宝箱、拟饵列表、bossFish 等传进去。

## 5. BobberBar（小游戏）核心要点

见 [源代码/StardewValley.Menus/BobberBar.cs](源代码/StardewValley.Menus/BobberBar.cs)。

### 5.1 从 DataLoader.Fish 读“难度/运动类型/尺寸范围”

构造函数读取 `DataLoader.Fish(Game1.content)`：

- `difficulty = fields[1]`
- `motionType = fields[2]`（mixed/dart/smooth/floater/sinker）
- `minFishSize = fields[3]`, `maxFishSize = fields[4]`
- 实际鱼尺寸：`fishSize = min + (max-min)*fishSize01; fishSize++`

### 5.2 初始鱼品质（quality）来自 fishSize01 区间 + 浮标/拟饵加成

- `fishQuality` 初始由 fishSize01：
  - <0.33 -> 0
  - <0.66 -> 1
  - 否则 -> 2
- 某些 bobbers（例如 `(O)877`）会提升品质，最高到 4
- 初学者鱼竿（beginnersRod）会强制品质 0

### 5.3 结算触发点

在 `update()` 中，当 `fadeOut` 缩到 0：

- 若 `distanceFromCatching > 0.9`：视为钓上来 -> `rod.pullFishFromWater(...)`
- 否则：视为失败 -> `rod.doneFishing(...)`

### 5.4 宝箱条（treasureCatchLevel）

- 宝箱会在随机延时后出现（`treasureAppearTimer`）
- 宝箱在条内时 `treasureCatchLevel` 递增
- 达到 1.0 视为抓到宝箱：`treasureCaught = true`

## 6. 拉起动画与最终入包/掉地

见 [源代码/StardewValley.Tools/FishingRod.cs](源代码/StardewValley.Tools/FishingRod.cs)：

- `pullFishFromWater(...)` 通过网络事件把结果广播给所有人
- `doPullFishFromWater(...)` 在本地：
  - 根据“完美/品质”提升最终 `fishQuality`
  - 本地玩家获得经验（与品质/难度/宝箱/完美/bossFish 相关）
  - 播放抛物线动画，动画结束回调 `playerCaughtFishEndFunction(...)`
- `playerCaughtFishEndFunction(...)`：
  - 记录图鉴/记录大小/首钓提示等
  - 若节日：交给 event 处理并 `doneFishing`
- `doneHoldingFish(...)`：玩家点击确认后：
  - 如果没宝箱：直接创建物品入背包（满了则掉地/弹菜单）
  - 若有宝箱/特殊标签：进入 `showingTreasure` + 播放开箱，再 `openTreasureMenu...` 结算

---

## 7. Data/Locations：钓鱼规则数据结构（推断版）

> 说明：当前这套反编译源码树里能看到 `LocationData / SpawnFishData / FishAreaData` 的“使用点”，但没有找到它们的类型定义源码文件（很可能在另一个程序集/项目中）。因此本节字段表是**基于使用点推断**，用于 StardewCraft 复刻时设计同构的数据结构。

### 7.1 数据从哪来

- `DataLoader.Locations(...)` 会加载 `Data\\Locations` 为 `Dictionary<string, LocationData>`（见 [源代码/StardewValley/DataLoader.cs](源代码/StardewValley/DataLoader.cs) 中 `Locations()`）。
- 运行时缓存为 `Game1.locationData`（`IDictionary<string, LocationData>`）。
- 钓鱼时主入口会把：
  - `Game1.locationData["Default"].Fish`（全局默认规则）
  - 与 `location.GetData().Fish`（当前地点规则）
  合并后按 `Precedence` + 随机打散顺序逐条尝试（见 [源代码/StardewValley/GameLocation.cs](源代码/StardewValley/GameLocation.cs) 的 `GetFishFromLocationData(...)`）。

### 7.2 LocationData（与钓鱼相关的字段）

基于 `GameLocation` 使用推断，`LocationData` 至少包含：

- `List<SpawnFishData> Fish`：该地点可刷新的“鱼生成规则”列表。
- `Dictionary<string, FishAreaData> FishAreas`：可选；定义“鱼区”（Fishing Area），用于将同一地图不同水域划分成不同刷鱼池。

### 7.3 FishAreaData（鱼区）

基于 `TryGetFishAreaForTile(...)` / `GetFishingAreaDisplayName(...)` 使用推断，`FishAreaData` 至少包含：

- `Position`：可选区域（应能执行 `Position?.Contains(x, y)`）。
  - 若 `Position` 有值：该鱼区仅在矩形内生效。
  - 若 `Position` 为空：会被当作“默认鱼区候选”（当没有任何矩形命中时回退到第一个默认）。
- `DisplayName`：可选；用于 UI 展示的名称（会经过 `TokenParser.ParseText(...)` 解析）。
- `CrabPotFishTypes`：可选 `List<string>`；若设置则覆盖该 tile 上蟹笼可出的物品列表。

鱼区选择逻辑：

- 若地点定义了 `FishAreas`：
  1) 先遍历，找第一个 `Position` 命中 tile 的鱼区，返回该鱼区 id。
  2) 若没命中，则回退到遍历时遇到的第一个 `Position == null` 的鱼区作为默认。
- 若没有 `FishAreas`：则 `fishAreaId = null`，后续刷鱼规则里 `spawn.FishAreaId != null` 的规则全部不会命中。

### 7.4 SpawnFishData（刷鱼规则）

`SpawnFishData` 是 `Data/Locations` 中的“单条刷鱼规则”，并且它能直接被 `ItemQueryResolver.TryResolveRandomItem(spawn, ...)` 消费，说明它本质上也是一条 Item Query（可生成不止一种物品，不限于单一鱼 ID）。

从 `GetFishFromLocationData(...)` / `CheckGenericFishRequirements(...)` 能确定的字段与语义如下（推断）：

- **基础/排序**
  - `Id`：规则 ID（仅用于报错日志定位）。
  - `Precedence`：优先级（数值越小越先尝试；同优先级会再随机打散）。

- **刷鱼池归属**
  - `FishAreaId`：要求所在鱼区的 id；若不为 null，则必须与当前 bobberTile 解析出的 `fishAreaId` 相等。
  - `Season? Season`：可选季节限制；若玩家使用魔法饵（Magic Bait）则季节限制会被忽略。
  - `bool CanBeInherited`：当调用来自“间接取鱼”（例如 `LOCATION_FISH` item query）时，若为 false 则跳过。

- **位置/等级/深度门槛**
  - `Rectangle? PlayerPosition`：可选；玩家 tile 必须落在该矩形内。
  - `Rectangle? BobberPosition`：可选；浮标 tile 必须落在该矩形内。
  - `int MinFishingLevel`：最低钓鱼等级。
  - `int MinDistanceFromShore`：最小离岸距离（对应 `waterDepth`）。
  - `int MaxDistanceFromShore`：最大离岸距离；`-1` 表示不限制。
  - `bool RequireMagicBait`：是否强制需要魔法饵。

- **概率与修正**
  - `float GetChance(bool hasCuriosityLure, double dailyLuck, int luckLevel, Func<float, IList<QuantityModifier>, QuantityModifierMode, float> applyQuantityModifiers, bool isTargetedBait)`：返回该规则命中的基础概率。
  - `bool UseFishCaughtSeededRandom`：若为 true，则用 `PreciseFishCaught` 计数构造 seeded random 来 roll 概率（使“精确钓鱼”可复现）。
  - `bool ApplyDailyLuck`：是否把 `player.DailyLuck` 应用到概率里（在 `Data/Fish` 校验阶段）。
  - `List<QuantityModifier> ChanceModifiers` + `ChanceModifierMode`：对概率额外做 modifiers（在 `Data/Fish` 校验阶段）。
  - `float CuriosityLureBuff`：好奇拟饵对“低概率鱼”的额外提升（默认 `-1` 表示使用内置曲线）。

- **条件系统**
  - `string Condition`：GameStateQuery 条件；若不满足则跳过。

- **生成物品与额外标记**
  - 规则可生成 `Item item = ItemQueryResolver.TryResolveRandomItem(...)`；若 item 为 null 则跳过。
  - `string SetFlagOnCatch`：若非空则写入 `item.SetFlagOnPickup`。
  - `bool IsBossFish`：若 true 则 `item.SetTempData("IsBossFish", true)`。

- **次数/教程/鱼表门槛**
  - `int CatchLimit`：同一种 fish 的可抓取上限；达到上限则该规则不会再返回该 fish。
  - `bool IgnoreFishDataRequirements`：若 true，跳过 `Data/Fish` 的时间/天气/最低等级/深度概率等门槛。
  - `bool? CanUseTrainingRod`：训练竿特判（若未显式设置，则回退到按 `Data/Fish` 难度 < 50 的规则）。

### 7.5 “目标饵（SpecificBait）”的两段式行为

当使用 `SpecificBait` 时，会把目标鱼设置为 `baitTargetFish`，并出现一个“最多两次优先尝试目标鱼”的逻辑：

- 若刷出来的鱼不是目标鱼：会暂存第一条非目标鱼 `firstNonTargetFish`，并把 `targetedBaitTries++`。
- 当 `targetedBaitTries < 2` 时，即使当前刷出来的鱼满足所有门槛，也不会立刻返回，仍继续找目标鱼。
- 若尝试两次仍没刷到目标鱼，则最终返回 `firstNonTargetFish`（如果存在）。

---

## 8. StardewCraft（Minecraft/Fabric）还原路线（建议里程碑）

> 我们按“可运行、可验证”的小步迭代来做，优先把状态机与数值跑通，再做 UI/动画。

### M1：服务端钓鱼状态机（不做小游戏 UI）

- 能抛竿 -> 落点判定水体 -> 等待咬钩 -> 咬钩窗口 -> 玩家右键收杆 -> 生成鱼/垃圾 -> 结算
- 先用文本/ActionBar 提示代替 BobberBar
- 先把 `calculateTimeUntilFishingBite`、`distanceToLand(clearWater)` 的概念迁移过来

### M2：加入 BobberBar 的“基础条玩法”（最简）

- 客户端打开一个 Screen（或 HUD）
- 同步参数：`difficulty`、`motionType`、`bobberBarHeight`、`treasure` 等
- 能判定成功/失败并回传服务器结算

### M3：宝箱与多钓（ChallengeBait / 二连等）

- 实现 treasureCatchLevel 与宝箱掉落池
- 实现 `numberOfFishCaught`（例如某些饵触发 2 条/3 条）

### M4：拟饵/浮标/附魔/职业的数值钩子

- 把 bobber/tackle/bait 的加成逐个接入：
  - 咬钩时间缩短
  - 条高度
  - 重力/稳定性
  - 宝箱概率

### M5：更完整的数据表与地点逻辑

- 引入 StardewCore/数据包里的鱼表映射（或自建 JSON）
- 支持地点/天气/时间/季节等筛选

---

## 9. 下一步（你选一个方向我就开始动手）

1) 继续深挖“生成哪条鱼”的逻辑：`GameLocation.getFish(...)`、`DataLoader.Fish`、地点/季节/天气过滤。
2) 在 Java 模组侧先落 M1：搭一个 `FishingController`（服务端状态机 + 定时器 + 结算），先跑通流程再做 UI。
