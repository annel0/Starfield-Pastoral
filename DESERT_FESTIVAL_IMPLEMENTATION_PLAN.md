# StardewCraft Desert Festival Implementation Plan

本文档记录 StardewCraft 沙漠节复刻的详细规划。它基于原版 Stardew Valley 1.6 源码与 Content 数据调查，并结合 StardewCraft 当前节日系统、地图 overlay 路线和用户确认的实现取舍。

核心目标不是一次写出一个庞大硬编码活动，也不是先捏一个“临时三花蛋闭环”。沙漠节必须按原版子系统逐个落地：被动节日和 overlay 先稳定，随后是场地交互、三花蛋显示、EggShop、日替摊位、Marlon/骷髅洞评分、任务、赛跑和年度奖励。`Calico Egg` 是这些系统共同产生和消耗的临时物品货币，不应被单独做成一个脱离原版来源的经济循环。

## 1. 当前结论

沙漠节在原版中不是普通主动节日，也不是一个单独小游戏。它是一个持续三天的被动节日，核心循环如下：

1. 春 15-17，只要沙漠可访问，当天加入 `ActivePassiveFestivals`。
2. 每天 10:00 开放并广播提示。
3. `Desert` 地图替换为 `DesertFestival` 地图。
4. 每天抽两个村民开日替摊位。
5. 玩家通过骷髅洞、任务、答题、赛跑、垃圾桶、商店等方式获得或消耗 `CalicoEgg`。
6. `CalicoEgg` 是真实物品货币，节日结束后隔夜清除。
7. 骷髅洞在节日期间开启评分、蛋矿石、Calico Statue 效果和评分领奖；StardewCraft 没有独立 Gil NPC，原版 Gil 的所有沙漠节评分领奖功能统一绑定到 Marlon。
8. 第三天结束后清理临时货币、临时订单和节日状态。

StardewCraft 需要保留这套循环，但做两处明确项目化取舍：

- 赛跑不做 3D 世界实体绕场跑。赛跑交互打开一个 2D 屏幕，下注、破坏、比赛过程、结果和领奖都在 2D UI 内完成。
- 原版换装事件不直接复刻服装系统。改成每年一次的节日换装奖励：给玩家一套随机颜色、随机纹样、纹样也随机颜色的皮革套装；或更进一步，给一套可复用的装备纹理模板。
- 原版 Gil 的沙漠节评分领奖不生成独立 Gil NPC。StardewCraft 统一由 Marlon 承载：玩家与沙漠节 Marlon 交互时进入评分提交、领奖和已领取提示流程；普通怪物杀手目标仍保留原有 Marlon/Gil goals 菜单语义。
- 帽子和家具不上架、不新增；Cactus Man 不做。
- `Calico Egg Stone` 只保留当前单种方块，不做三变体资源。

## 2. 原版事实基线

### 2.1 数据入口

原版被动节日数据来自 `源文件/Content/Data/PassiveFestivals.json`：

- `id`: `DesertFestival`
- `Season`: `Spring`
- `StartDay`: `15`
- `EndDay`: `17`
- `StartTime`: `1000`
- `Condition`: `LOCATION_ACCESSIBLE Desert`
- `ShowOnCalendar`: `true`
- `StartMessage`: `Strings\\1_6_Strings:DesertFestival_NowOpen`
- `MapReplacements`: `Desert -> DesertFestival`
- `DailySetupMethod`: `StardewValley.Locations.DesertFestival.SetupFestivalDay`
- `CleanupMethod`: `StardewValley.Locations.DesertFestival.CleanupFestival`

StardewCraft 当前 `FestivalRegistry` 已登记这些日期事实：

- `DesertFestival` 是 `PASSIVE`。
- 日期为春 15-17。
- 开放时间为 10:00。
- 条件为 `LOCATION_ACCESSIBLE Desert`。
- overlay id 为 `DesertFestival`。
- mechanic id 为 `desert_festival`。

StardewCraft 当前已登记 `DesertFestival` overlay，使用 `data/stardewcraft/structures/festivals/desert_festival.schem` 覆盖沙漠公共区域。后续工作重点从“能否应用场地”转为“场地内交互点、日替状态、HUD 和各子系统是否按原版语义接入”。

### 2.2 原版核心源码

原版逻辑分布如下：

| 原版文件 | 负责内容 |
| --- | --- |
| `源文件/StardewValley.Locations/DesertFestival.cs` | 沙漠节主地点类、交互、日替摊主、赛跑、蛋商店、Gil、Marlon、Willy、学者、料理、仙人掌、换装、HUD、setup/cleanup |
| `源文件/StardewValley.Locations/Racer.cs` | 赛跑者状态、速度、跳跃、摔倒、完成顺序 |
| `源文件/StardewValley.Locations/MineShaft.cs` | 骷髅洞评分、Calico Statue、蛋矿石、蛋 HUD、雕像效果对矿洞生成的影响 |
| `源文件/StardewValley/FarmerTeam.cs` | `calicoEggSkullCavernRating`、`highestCalicoEggRatingToday`、`calicoStatueEffects` |
| `源文件/StardewValley/Farmer.cs` | 雕像效果影响受伤、昏迷掉蛋、食物回复 |
| `源文件/StardewValley.Monsters/Monster.cs` | 沙漠节骷髅洞怪物掉 Calico Egg |
| `源文件/StardewValley.Objects/BreakableContainer.cs` | 沙漠节骷髅洞箱子掉 Calico Egg |
| `源文件/StardewValley/GameLocation.cs` | 沙漠节期间允许无 Skull Key 进骷髅洞、普通石头掉蛋 |
| `源文件/StardewValley.Tools/FishingRod.cs` | 第三天 Willy 任务强制宝箱出 Golden Bobber |
| `源文件/StardewValley/Game1.cs` | 被动节日状态刷新、每日 setup、cleanup、地图替换、开放消息 |
| `源文件/StardewValley/Utility.cs` | `IsPassiveFestivalDay`、`IsPassiveFestivalOpen`、`GetDayOfPassiveFestival` |

### 2.3 原版内容数据

| 数据文件 | 负责内容 |
| --- | --- |
| `源文件/Content/Data/Objects.json` | `CalicoEgg`、`GoldenBobber`、`CalicoEggStone_0/1/2` |
| `源文件/Content/Data/Shops.json` | `DesertFestival_EggShop` 和 27 个 `DesertFestival_<NPC>` 商店 |
| `源文件/Content/Data/SpecialOrders.json` | 三个 `DesertFestivalMarlon` 一日订单 |
| `源文件/Content/Data/Locations.json` | `DesertFestival` 地点创建，地图为 `Maps\\Desert-Festival` |
| `源文件/Content/Data/GarbageCans.json` | 沙漠节垃圾桶必出 5-8 个 Calico Egg |
| `源文件/Content/Data/LocationContexts.json` | 沙漠节当天昏迷复活点改到沙漠 |
| `源文件/Content/Data/mail*.json` | 春 14 沙漠节提醒邮件 |
| `源文件/Content/Strings/1_6_Strings*.json` | 沙漠节 UI、对话、赛跑、学者、料理、Gil 等文本 |

## 3. StardewCraft 实现取舍

### 3.1 保留的原版语义

以下内容必须按原版语义保留：

- 春 15-17，沙漠可访问才开放。
- 10:00 后才真正开放。
- 第 1/2/3 天的 day-of-festival 语义必须正确。
- 沙漠公共区域临时变成节日状态，节日结束后恢复。
- `Calico Egg` 是临时物品货币，不是永久数值。
- 节日结束后清除玩家身上的 `Calico Egg`。
- 每天抽两个村民开摊，且日替摊位不能和当天已有专属日程冲突。
- 骷髅洞评分是全队共享状态。
- Marlon 每天只能按当天最高评分代发一次原版 Gil 沙漠节奖励。
- Marlon 订单是沙漠节专属一日订单。
- Willy 任务每天只能接一次，第三天包含 Golden Bobber。
- 学者奖励每年只能领一次。
- 赛跑猜中给蛋奖励，破坏赛车消耗蛋。

### 3.2 明确不照搬的原版部分

以下内容不直接照搬原版表现，但要保留玩家体验意图：

| 原版内容 | StardewCraft 取舍 | 理由 |
| --- | --- | --- |
| 世界地图上渲染 Racer 并沿 tile 赛道移动 | 改为打开 2D 赛跑屏幕 | 避免 3D 实体路径、碰撞、同步和美术负担；更稳定，也更符合 MC GUI 能力 |
| Emily/Sandy 换装事件直接替换 SDV 衣服 | 改为每年一次领取随机皮革套装或装备纹理模板 | MC 装备系统和 SDV 衣服系统不同；给可见装备奖励更实用 |
| 原版所有小动画完全在地图中复刻 | 第一阶段只复刻核心交互，视觉动画按资源逐步补齐 | 不能使用临时占位资源，缺资源时先记录需求 |
| 原版 HUD 直接从大图集取 source rect | StardewCraft HUD 必须先裁成独立小图再引用 | 不允许在 HUD/GUI 代码中直接引用 `cursors_1_6.png` 等大图集来取局部区域 |
| EggShop 帽子和家具商品 | 当前版本不实现、不注册、不上架 | 用户已明确帽子和家具都不用做；不能用假商品或半成品占位 |

### 3.3 子系统完成边界

每个进入实现的子系统都按最终语义做完整闭环，不使用“临时首版”或假替代。当前沙漠节整体必须做到：

- 沙漠节日期、开放时间、条件和三日状态正确。
- 地图 overlay 应用和恢复可靠。
- Calico Egg 作为真实临时物品存在，可交易、可清理。
- 沙漠节场地内三花蛋 HUD 可用，位置按 StardewCraft 取舍放在 moneybox 下方，不能遮挡任务组件，视觉使用从原版裁出的独立小图。
- EggShop 可用，但只上架当前项目已有且行为正确的商品；原版帽子和家具在当前范围内不做。
- 两个日替 NPC 商店可用；未实现商品隐藏并记录缺口，不用假物品替代。
- 骷髅洞评分、掉蛋、Calico Egg Stone、Calico Statue、Marlon 代发评分奖励和 Marlon 一日订单可用。
- 赛跑 2D 屏幕可用。
- 换装年度奖励可用。
- 过夜和服务器重启不会留下半节日状态。

明确后置或不做：

- 所有 27 个 NPC 店铺的每个稀有家具完全还原。
- EggShop 中原版帽子和家具商品。
- 所有料理 buff 的完整动画。
- 所有学者题库本地化细节。
- Calico Statue 的缺失视觉特效后置，但效果规则进入该子系统时要完整实现。
- 原版赛跑逐像素动画完全一致。

## 4. 总体架构

### 4.1 新增服务建议

建议新增 `com.stardew.craft.festival.desert` 包，避免把沙漠节逻辑塞进通用 `FestivalService`。

候选类：

| 类 | 职责 |
| --- | --- |
| `DesertFestivalService` | 沙漠节总入口，处理每日 setup、cleanup、交互路由、开放判断 |
| `DesertFestivalState` | 当年当天全局状态：dayOfFestival、日替商人、race lineup、race cycle、session flags |
| `DesertFestivalPlayerState` | 玩家个人状态：年度换装、学者、Willy 任务、Marlon 代发评分领奖、赛跑下注和待领奖 |
| `CalicoEggService` | Calico Egg 发放、消费、隔夜清理、数量查询 |
| `DesertFestivalCurrencyHud` | 沙漠节场地内三花蛋数量显示；引用独立裁图，不直接引用大图集 |
| `DesertFestivalShopService` | EggShop 和 NPC 日替摊位数据解析与交易 |
| `DesertFestivalRaceService` | 服务端赛跑状态机、下注、破坏、奖励结算 |
| `DesertFestivalRaceScreen` | 客户端 2D 赛跑屏幕 |
| `DesertFestivalMineService` | 骷髅洞评分、Calico Statue、矿洞掉蛋规则 |
| `DesertFestivalMakeoverService` | 年度换装奖励生成和发放 |
| `DesertFestivalQuestService` | Willy、Marlon、学者、料理等支线入口 |

### 4.2 状态存储原则

沙漠节有三类状态，不能混在一起：

#### 全局当天状态

存放在 `FestivalWorldData` 或沙漠节子状态里：

- 当前年份、季节、日。
- 当前是否为 `DesertFestival`。
- `dayOfFestival`: 1、2、3。
- 今天抽中的两个 NPC 摊主。
- 今天的赛车手列表，原版是 5 个里抽 3 个。
- 当前赛跑阶段和下一场开始时间。
- 当前沙漠 overlay 状态。
- 当前 Marlon 可用订单池。

#### 全队共享状态

适合放在 team/festival world state 中：

- `calicoEggSkullCavernRating`
- `highestCalicoEggRatingToday`
- `calicoStatueEffects`
- 当日 Calico Statue 激活次数。
- 当前骷髅洞沙漠节运行最深层。

#### 玩家个人状态

适合放在 `PlayerStardewData` 的 mail flags 或专门字段中：

- `Y<year>_DesertMakeoverClaimed`
- `Y<year>_Scholar`
- `lastGotDesertFestivalRatingPrizeFromMarlon`
- `lastDesertFestivalFishingQuest`
- 当前赛跑猜测。
- 当前赛跑破坏目标。
- 待领取赛跑奖励次数。
- 是否已领取特殊赛车奖励。

## 5. 地图与空间

### 5.1 Overlay 路线

`DesertFestival` 必须走已有 `FestivalMapOverlayManager`，不能直接 paste 整张地图。

需要新增：

- `FestivalMapOverlayDefinition` id: `DesertFestival`
- regionKey: `Desert`
- base schem: 普通 Calico Desert 公共区域模板
- festival schem: 沙漠节状态模板
- bounds: 沙漠节影响范围
- origin: 由用户在 StardewCraft 坐标体系中确认
- safe positions: 玩家、NPC 和恢复前临时转移点

未注册 overlay 时，当前门禁逻辑应保持：被动节日 session 可以存在，但不进入真正开放地图状态。

### 5.2 坐标硬约束

以下点位必须由用户提供或在坐标迁移账本中确认：

- `DesertFestival` overlay origin。
- overlay bounds。
- 巴士到达点。
- 沙漠节入口安全点。
- EggShop 交互点。
- 两个日替 NPC 摊位点。
- Sandy/Emily 换装点。
- Scholar 交互点。
- Cook 交互点。
- Race Man 交互点。
- Shady Guy 交互点。
- Marlon 交互点：同时承载原版 Marlon 一日订单和原版 Gil 沙漠节评分领奖。
- Willy 钓鱼板点。
- Calico Statue 在矿洞中的生成规则或可替代坐标规则。

不得从原版 tile 坐标直接推算 MC 世界坐标。

### 5.3 Overlay 清理范围

沙漠节公共区域在节日期间属于系统区域。进入 overlay 前建议清理：

- 会被节日结构覆盖的掉落物。
- 节日范围内的临时实体。
- 节日交互点附近的非系统方块变动。

不应覆盖：

- 农场或玩家私有区域。
- 沙漠中项目已有的永久系统点位。
- 不在 bounds 内的玩家建筑或调试结构。

## 6. Calico Egg 临时货币

### 6.1 物品定义

需要新增物品：

- `calico_egg`
- 最大堆叠建议遵循项目物品习惯，通常为 999。
- 不可作为普通礼物或永久经济物品。
- Tooltip 标注为沙漠节临时货币。
- 节日结束后从所有玩家背包、副手、容器式临时存储中移除。

原版还需要：

- `calico_egg_stone_0`
- `calico_egg_stone_1`
- `calico_egg_stone_2`
- `golden_bobber`

`golden_bobber` 只用于第三天 Willy 任务，不应变成通用高价值鱼漂，除非后续确认要扩展。

### 6.2 获取来源

实现要求按原版来源分层接入；进入某一来源时必须完成对应语义，不做临时替代：

| 来源 | 原版语义 | 实现要求 |
| --- | --- | --- |
| 骷髅洞怪物 | 评分和层数提高掉蛋概率 | P0，实现 |
| 骷髅洞石头 | 普通石头概率掉蛋 | P0，实现 |
| Calico Egg Stone | 生成蛋矿石，敲掉掉蛋 | P0，按用户确认只保留当前单种方块，不做三变体 |
| 矿洞箱子 | 评分和层数提高掉蛋概率 | P1 |
| 垃圾桶 | 沙漠节垃圾桶给 5-8 蛋 | P1，已接入现有垃圾桶系统 |
| artifact spot | 原版沙漠节挖点给 3-6 蛋 | P2，已接入现有远古斑点系统 |
| Cactus Man | 每年一次奖励仙人掌，不是蛋 | 不做 |
| Scholar | 每年一次答题成功给 50 蛋 | P2 |
| Willy 任务 | 第 1/2/3 天分别 25/50/30 蛋 | P1 |
| Marlon 订单 | 35/50/40 蛋 | P1，已接入 Marlon 菜单、每日两选一、进度追踪和领奖 |
| Marlon 代发评分奖励 | 承载原版 Gil 评分领奖，根据评分给 1-500 蛋和额外物品 | P0 |
| 赛跑 | 猜中给 20 或特殊 100 蛋 | P0 |

### 6.3 清理规则

原版 `CleanupFestival` 把 `CalicoEgg` 加入 `itemsToRemoveOvernight`。

StardewCraft 应在春 18 新日开始前清理：

- 玩家主背包。
- 副手。
- 当前打开容器里的临时移动状态，如果项目有。
- 沙漠节奖励待领取状态中的蛋奖励可以在清理前发放，也可以清掉并记录未领取消失。建议发放时机明确：过夜后未领取赛跑奖励作废。

不要清理：

- 普通鸡蛋、金蛋、恐龙蛋等非 `calico_egg` 物品。

### 6.4 场地 HUD

原版 `DesertFestival.drawOverlays` 使用：

- `SpecialCurrencyDisplay.Draw(...)`
- `MoneyDial(4, playSound: false)`
- `Game1.player.Items.CountId("CalicoEgg")`

StardewCraft 取舍：

- 原版左上角位置会与项目小地图冲突，因此改放在 moneybox 下方。
- 必须避开 `QuestIconHud` 的任务按钮、ping 动画和快捷键提示。
- 原版是在 `DesertFestival.drawOverlays` 中随地点 overlay 绘制：玩家当前地点是 `DesertFestival` 时直接显示，0 个蛋也显示，不以“持有蛋”为前提。
- 视觉必须来自原版 `SpecialCurrencyDisplay`：4 位数字槽、三花蛋图标和 `MoneyDial` 数字风格。
- HUD/GUI 代码不得直接引用 `cursors_1_6.png`、`cursors2.png` 这类大图集取局部区域；必须先把需要的 source rect 裁成独立小图，例如 `textures/gui/desert_festival/calico_currency_bg.png`。
- 该 HUD 显示的是玩家背包中 `calico_egg` 的数量，不是骷髅洞评分。
- StardewCraft 当前用“玩家在 Desert 区域 + 春 15-17”近似原版地点条件；调试残留蛋只允许在 Desert 区域内显示，避免跑到其他地图上。
- 骷髅洞里的 `rating + 1` 蛋 HUD 属于 `DesertFestivalMineService`，另按 `MineShaft.drawOverlays` 复刻。

## 7. 商店系统

### 7.1 EggShop

原版 `DesertFestival_EggShop` 有 22 个商品，包含 Mystery Box、帽子、家具、草莓种子、炸弹、宝箱、按星期变化商品等。

当前版本约束：

- 使用 `Calico Egg` 作为 `TradeItemId`。
- 支持 day-of-week 或 day-of-festival 条件。
- 只接入项目已有且行为正确的物品。
- 帽子和家具全部不做、不注册、不上架。
- 原版缺失商品不得用假物品替代。

必须优先支持的商品：

| 原版商品 | 原版价格 | StardewCraft 建议 |
| --- | ---: | --- |
| Mystery Box | 20 蛋 | 已有 `mystery_box`，P0 |
| Strawberry Seeds | 5/6 蛋 | 已有 `strawberry_seeds`，P0 |
| Mega Bomb、Mixed Seeds、Magic Rock Candy、随机食物组 | 原版价格 | 项目已有对应物，P0/P1 |
| SkillBook_2 | 100 蛋 | 项目已有 skill book，P1 |
| 家具和帽子 | 10-120 蛋 | 当前版本不做 |

### 7.2 NPC 日替摊位

原版每天从有 `DesertFestival_<NPC>` shop 的村民中抽两个摊主。共有 27 个 NPC 店铺。

StardewCraft 建议：

1. 新增 `desert_festival_npc_shops.json` 数据表。
2. 每个 shop 保留原版 id，例如 `DesertFestival_Abigail`。
3. 每天 setup 用世界种子、年份、日期做稳定随机，确保重启后不变。
4. 如果抽中的 NPC 有当天专属沙漠节日程 key，尊重日程覆盖。
5. 如果某个 shop 里的商品未实现，只隐藏该商品，不让整个摊位失效。

当前优先实现并补齐数据映射：

- Clint: 矿物/煤。
- Vincent: 低价杂物。
- Pierre: 可买 `Calico Egg`，以及已有食材。
- Alex/Gus/Emily 等已有物品较多的摊位。

需要后置或明确不做的：

- NPC 专属武器。
- NPC 专属家具、帽子、墙纸、地板、装饰品当前不上架；后续只有重新确认范围后才处理。

## 8. 2D 赛跑系统

### 8.1 设计结论

赛跑全部在 2D 屏幕内完成，不生成 3D 赛车实体，不让赛车在 MC 世界里沿路径移动。

世界中只保留两个交互点：

- Race Man：打开赛跑屏幕、下注、领奖。
- Shady Guy：在赛跑屏幕或独立对话中选择破坏目标。

推荐做成一个统一屏幕：`DesertFestivalRaceScreen`。玩家点击 Race Man 后打开，屏幕内有：

- 今日赛车手列表。
- 下一场比赛倒计时。
- 当前比赛状态。
- 下注按钮。
- 破坏按钮。
- 赛道动画。
- 结果展示。
- 待领奖按钮。

### 8.2 原版规则保留

- 原版 `totalRacers = 5`，每天抽 `racerCount = 3`。
- 赛跑在开放后每两个小时启动一场，原版条件为 `timeOfDay % 200 == 0 && timeOfDay < 2400`。
- 玩家可在比赛开始前猜赢家。
- Shady Guy 破坏目标消耗 1 个 Calico Egg。
- 破坏提高对应赛车手摔倒概率。
- 猜中普通赢家：待领取奖励 +1，每次 20 蛋。
- 猜中特殊赛车手，原版 index 3 首次给 100 蛋，之后走普通奖励。
- 22:00 后如果比赛已经开始，不再允许下一场竞猜。

### 8.3 服务端状态机

建议服务端状态：

```text
IDLE
BETTING
READY
SET
GO
FINISHED
RESULT
CLOSED
```

每场比赛记录：

- `raceId`
- `festivalDate`
- `racers`: 3 个 racer id
- `state`
- `stateStartTick`
- `winner`
- `progressByRacer`
- `sabotageCountByRacer`
- `guessesByPlayer`
- `randomSeed`

服务端权威推进比赛。客户端只显示当前状态，不自行决定赢家。

### 8.4 2D 动画方案

推荐资源：

- 从原版 `LooseSprites\\DesertRacers` 提取赛车手 sprite。
- 从 `Maps\\desert_festival_tilesheet` 或手工 UI 图提取赛道/背景。
- 赛道可以不是完整世界地图，只要表现为沙漠节赛跑即可。

动画实现：

- 每个 racer 在 2D polyline 上移动。
- 速度由服务端随机种子和 sabotage 修正计算。
- 客户端插值显示 progress。
- racer 摔倒时播放 shake/停顿动画。
- 结束后显示排名和玩家猜中/猜错。

### 8.5 多人语义

- 同一天同一世界只有一个 race schedule。
- 所有玩家看到同一场比赛。
- 每个玩家有自己的猜测、破坏目标、待领奖状态。
- 多个玩家可破坏同一个 racer，服务端累加 sabotage count。
- 玩家离线后，已下注结果仍保留；领奖需要回来找 Race Man。
- 春 18 清理未领取奖励。

### 8.6 UI 验收

- GUI scale 变化时，赛道、按钮、文字不溢出。
- 赛跑屏幕不调用默认模糊背景。
- 关闭屏幕后玩家仍在沙漠节地图中。
- 比赛进行中重新打开屏幕能看到当前进度。
- 网络延迟下客户端不能伪造下注、破坏或领奖。

## 9. 换装奖励系统

### 9.1 设计目标

原版换装事件是 Emily 或 Sandy 给玩家做随机 makeover。StardewCraft 改为更适合 MC 的奖励：

- 每个玩家每年只能领取一次。
- 领取后获得一套随机视觉主题装备。
- 奖励必须实际可见、可穿戴、可保存。
- 颜色和纹样有足够随机性，让它像一次节日换装，而不是固定礼包。

### 9.2 推荐方案 A：随机皮革套装

这是最稳的完整可落地方案。

奖励内容：

- 皮革头盔。
- 皮革胸甲。
- 皮革护腿。
- 皮革靴子。

随机数据：

- `baseColor`: 任意 RGB，但建议避开过暗、过灰、过透明感的颜色。
- `patternId`: 从项目提供的若干纹样中随机。
- `patternColor`: 任意 RGB，可与 baseColor 做对比度检查。
- `accentColor`: 可选，用于边线/扣子/小装饰。

实现方式：

1. 如果只使用 vanilla 皮革染色，只能表达 baseColor；这不能冒充完整纹样换装。
2. 如果要表达任意纹样和纹样颜色，需要新增自定义装备渲染或自定义模型/材质层。
3. 纹样数据应写入 ItemStack DataComponents 或 CUSTOM_DATA，不能只写 display name。
4. 随机 seed 建议使用玩家 UUID + 年份 + 世界 seed，重启后不会变。

领取状态：

- flag: `Y<year>_DesertMakeoverClaimed`
- 如果背包满，优先放入背包，放不下则掉落在玩家脚下。
- 已领取后，当年再次交互只显示已领取对话。

### 9.3 推荐方案 B：装备纹理模板

这是更可扩展的方案，适合后续做完整外观系统。

奖励内容不是直接发四件装备，而是发：

- `desert_makeover_template`

模板记录：

- `baseColor`
- `patternId`
- `patternColor`
- `accentColor`
- `generatedName`

使用方式：

1. 玩家右键模板，选择应用到当前穿着的皮革套装。
2. 或模板作为制作材料，合成/转换出对应套装。
3. 模板本身可以收藏、交易或复制规则另行决定。

优点：

- 不会强塞一套低防御装备占背包。
- 后续能兼容更高级装备外观。
- 玩家可以保留“这次节日造型”的模板。

缺点：

- 需要额外 UI 或物品交互。
- 需要自定义装备渲染/数据组件支持。
- 工作量高于直接发皮革套装。

### 9.4 建议路线

采用方案 A 时也必须把数据结构按方案 B 预留：

- 先发一套皮革套装。
- 每件装备写入同一份 makeover id 和视觉数据。
- 如果当前渲染层还不能显示纹样，该功能不能声称完整完成；数据仍保留 pattern 字段，等渲染层补齐后再上线完整表现。
- 一旦装备纹理模板系统做好，可以把同一份数据迁移为模板物品。

### 9.5 需要的美术资源

如果要真正显示“任意纹样、纹样任意颜色”，需要你提供或确认：

- 皮革套装基础遮罩或允许使用原版 MC leather armor 层。
- 纹样 mask 集合，建议至少 8-12 个：星星、沙丘、太阳、漩涡、格纹、斜纹、花、骷髅、蛋纹、点阵等。
- 每个纹样需要 helmet/chestplate/leggings/boots 四件对应的 UV 或统一贴图规则。
- 是否允许程序给纹样 mask 动态染色。
- 是否需要装备图标也显示同样纹样。
- 如果走模板物品，需要模板物品 icon。

没有这些资源时，只能稳定实现“随机颜色皮革套装”，不能承诺纹样可见。

## 10. 骷髅洞与 Calico Statue

### 10.1 评分规则

需要实现全队共享评分：

- `calicoEggSkullCavernRating` 初始为 0。
- HUD 显示为 `rating + 1`。
- 进入新最深层时，如果层数是 5 的倍数，评分 +1。
- 掉井跨过新的 5 层区间，评分按跨越区间数增加。
- 激活 Calico Statue 评分 +1。
- 当天最高评分更新到 `highestCalicoEggRatingToday`。
- 离开全部 active mines 后或新日时清零。

### 10.2 Calico Statue 效果

原版效果 id：

| id | 原版含义 | StardewCraft 实现要求 |
| ---: | --- | --- |
| 0 | Ghost invasion | P2 |
| 1 | Serpent invasion | P2 |
| 2 | Skeleton invasion | P2 |
| 3 | Bat invasion | P2 |
| 4 | Assassin Bugs | P2 |
| 5 | Thin Shells，昏迷掉更多蛋 | P1 |
| 6 | Meager Meals，食物回复减半 | P1 |
| 7 | Monster Surge | P1 |
| 8 | Sharp Teeth，受伤增加 | P1 |
| 9 | Mummy Curse | P2 |
| 10 | Speed Boost | P1 |
| 11 | Refresh，回满血和体力 | P1 |
| 12 | 50 Egg Treasure | P0 |
| 13 | No Effect | P0 |
| 14 | Tooth File，受伤减少 | P1 |
| 15 | 25 Egg Treasure | P0 |
| 16 | 10 Egg Treasure | P0 |
| 17 | 100 Egg Treasure | P0 |

Calico Statue 进入实现时必须覆盖奖励类和数值类效果；入侵类如果受矿洞怪物生态限制，需要明确记录为依赖阻塞，不能用“无效果”冒充。

### 10.3 掉蛋规则

需要接入：

- 矿洞怪物死亡掉蛋。
- 普通石头掉蛋。
- Calico Egg Stone 生成和掉蛋。
- 矿洞箱子掉蛋。
- 评分越高，掉蛋概率越高。

不要把掉蛋写成固定高概率，否则会破坏商店价格平衡。原版是通过评分逐步推高收益，让玩家越深入越容易滚起蛋经济。

## 11. Marlon 代发评分奖励

### 11.1 原版奖励段位

原版由 Gil 使用 `highestCalicoEggRatingToday + 1` 作为显示评分并给奖励。StardewCraft 没有独立 Gil NPC，因此该流程完整迁移到沙漠节 Marlon 身上：奖励段位、每日一次、确认提交、已领取提示和未达到评分提示都按原版 Gil 语义实现，只替换交互承载者。

| 评分 | Calico Egg | 额外奖励 |
| ---: | ---: | --- |
| 1-4 | 1 | 小奖励 |
| 5-9 | 10 | 小奖励 |
| 10-14 | 25 | 小奖励 |
| 15-19 | 50 | 小奖励 |
| 20-24 | 100 | 小奖励 |
| 25-54 | 200 | 首次 Gil's Hat，否则其他奖励 |
| 55+ | 500 | 高级奖励 |

StardewCraft 实现要求：

- 蛋奖励和可映射的额外奖励一起实现，不能只做一个临时给蛋按钮。
- 额外奖励优先映射到项目已有物品；缺失奖励必须在资源缺口中记录，不能用假物品替代。
- Gil's Hat 如果没有资源，记录为资源缺口，不注册临时帽子。
- 每个玩家每天只能通过 Marlon 领取一次评分奖励。
- 普通 Marlon 怪物杀手目标菜单仍保留；沙漠节期间 Marlon 需要额外提供“评分领奖/一日订单/普通菜单”的清晰入口。

### 11.2 交互流程

1. 玩家点击沙漠节 Marlon。
2. 如果今天已领，显示 come back/next year 文本。
3. 如果今天评分为 0，提示没有 rating。
4. 否则询问是否提交评分。
5. 确认后发放奖励并记录 `lastGotDesertFestivalRatingPrizeFromMarlon`。

## 12. Marlon 一日订单

### 12.1 原版订单

原版有三个 `OrderType = DesertFestivalMarlon` 的一日订单：

| 订单 | 目标 | 奖励 |
| --- | --- | ---: |
| `DesertFestivalMarlon1` | 杀 10 个随机指定怪：Serpent/Sludge/Mummy | 35 蛋 |
| `DesertFestivalMarlon2` | Skull Cave 下 30 层 | 50 蛋 |
| `DesertFestivalMarlon3` | 收集 12 omni geode 或 15 iridium ore | 40 蛋 |

### 12.2 StardewCraft 路线

- 如果项目特殊订单系统已经可用，新增 `DesertFestivalMarlon` board type。
- 当前特殊订单板还不能承载完整交互，已由 Marlon 沙漠节挑战对话承载：每天从三类原版订单中稳定生成两个可选挑战，玩家当天只能接一个。
- 玩家状态保存 `desertFestivalMarlonChallengeDay/id/progress/rewardClaimed`，与未来订单系统迁移兼容。
- 杀怪、拾取、骷髅矿井深度进度都由服务端事件更新。
- 完成 `DesertFestivalMarlon` 不给兑奖券，因为原版 SpecialOrder 对 desert festival 排除了 prize ticket。

## 13. Willy 钓鱼任务

### 13.1 原版规则

Willy 钓鱼板每天只能接一次：

- 第 1 天：钓 3 条指定鱼，奖励 25 蛋。
- 第 2 天：钓 1 条指定鱼，奖励 50 蛋。
- 第 3 天：交付 `GoldenBobber`，奖励 30 蛋。
- 第 3 天任务期间，如果玩家没有 Golden Bobber，钓鱼宝箱强制出 Golden Bobber。

### 13.2 StardewCraft 路线

- `FishingDataManager` 已接入 `IS_PASSIVE_FESTIVAL_OPEN`，可作为条件基础。
- 需要 `DesertFestivalQuestService` 记录 `lastDesertFestivalFishingQuest`。
- 需要 `golden_bobber` 物品和钓鱼宝箱插入逻辑。
- 任务 id 可以用 `desert_festival_willy_<day>`，内部兼容原版 `98765` 语义即可。

## 14. 其他节日交互

### 14.1 Cactus Man

用户已确认 Cactus Man 不做；不放交互入口、不注册仙人掌家具奖励，也不把它列为当前沙漠节收尾缺口。

### 14.2 Scholar

原版答 4 道题，答错本年失败，答对给 50 蛋。

StardewCraft 路线：

- 使用现有 Stardew 风格问答 UI。
- 题库文本从 `Strings/1_6_Strings` 迁移。
- 每年成功一次，flag: `Y<year>_Scholar`。
- 答错当天或本年失败需要按原版确认，建议按原版本年失败。

### 14.3 Cook

原版选择食材和酱汁，现场做一道临时食物，吃下后给 600 分钟 buff。

StardewCraft 路线：

- 可直接弹选择 UI，选择后给临时 buff；料理动画依赖资源，资源未齐时只后置动画表现，不削减 buff 语义。
- 动画依赖资源，后补。
- Buff 组合：Defense/Mining/Luck/Attack/Fishing/Speed。

### 14.4 Shady Guy

原版花 1 个 Calico Egg 破坏一个 racer，提高该 racer 摔倒概率。

StardewCraft 路线：

- 与 2D 赛跑屏幕合并最稳。
- Race Man 屏幕里放一个“破坏”入口，但消耗和结果由服务端确认。
- 世界中的 Shady Guy 交互也可以直接打开同一屏幕的 sabotage tab。

### 14.5 Traveling Cart 和公交

原版 Traveling Merchant 在沙漠节前半天有关闭文案，之后可对话。公交/路径会处理 `DesertFestival` 地图。

StardewCraft 路线：

- 先保证巴士到沙漠时落到节日 overlay 安全点。
- Traveling Cart 若项目未完整实现，可后置。

## 15. NPC 日程与对话

### 15.1 日程

沙漠节是被动节日，不应使用 Egg Festival 那种完全主动 actor 接管方式。

建议接入现有 `NpcScheduleRuntimeService` 优先级：

1. `DesertFestival_<day>`
2. `DesertFestival`
3. 普通日程

日替摊主使用 `SetupMerchantSchedule` 语义：

- 第一个摊主到摊位 1。
- 第二个摊主到摊位 2。
- 结束后回床。
- Leo 需要原版条件，搬到大陆后才参与。

### 15.2 对话

需要从 `Strings/1_6_Strings` 和 NPC 对话数据迁移：

- 开放前关闭文案。
- EggShop 关闭/开放文案。
- Race Man。
- Shady Guy。
- Marlon。
- Scholar。
- Cook。
- Willy。
- Sandy/Emily 换装。
- NPC 摊位默认对话。

缺文本时不写假对白，先记录缺口。

## 16. 资源需求清单

这是目前最需要你给或确认的内容。没有资源时，对应功能可以先写数据/服务，但不能用临时替代美术上线。

### 16.1 必需资源，做地图前必须确认

| 资源 | 用途 | 需要你提供/确认 |
| --- | --- | --- |
| 普通 Desert base schem | overlay restore | StardewCraft 当前沙漠公共区域模板、origin、bounds |
| DesertFestival festival schem | 节日沙漠地图 | 节日状态结构、摊位、装饰、碰撞和安全区域 |
| overlay origin | paste/diff 定位 | 必须走坐标迁移账本或你确认 |
| overlay bounds | 应用/恢复范围 | 必须确认，避免覆盖非公共区域 |
| 交互点坐标 | NPC、商店、任务触发 | EggShop、Race、Marlon、Willy、Cook、Scholar、Makeover、两个摊位 |
| 安全点 | 玩家传送/恢复保护 | 巴士到达、overlay 应用前临时点、恢复前临时点 |

### 16.2 必需资源，做 Calico Egg 经济前需要

| 资源 | 用途 |
| --- | --- |
| `calico_egg` item icon/model | 临时货币 |
| `calico_currency_bg.png` | 从原版 `SpecialCurrencyDisplay` 裁出的独立 HUD 小图，显示场地三花蛋数量 |
| `calico_egg_stone_0/1/2` block/item texture | 骷髅洞蛋矿石 |
| Calico Statue block/model/texture | 骷髅洞雕像 |
| `golden_bobber` item icon/model | Willy 第三天任务 |

资源硬规则：HUD/GUI 只能引用独立裁图，不允许直接引用原版大图集并在代码里写 source rect。

### 16.3 赛跑 2D UI 资源

| 资源 | 用途 |
| --- | --- |
| `LooseSprites/DesertRacers` 提取后的 racer sprites | 2D 赛车手动画 |
| 赛道背景图或赛道 tile 素材 | 2D 屏幕主体 |
| Calico Egg 小图标 | 奖励和花费显示 |
| UI 按钮/面板九宫格 | 猜测、破坏、领奖按钮 |
| 结果横幅/倒计时数字 | Ready/Set/Go/Result 表现 |

### 16.4 换装资源

| 资源 | 用途 |
| --- | --- |
| 皮革套装基础贴图或允许使用原版 leather armor | 随机基础颜色 |
| 纹样 mask 集合 | 随机纹样 |
| 纹样颜色染色规则 | 任意纹样颜色 |
| 装备图标生成规则 | 背包内显示对应外观 |
| 模板物品 icon | 如果走装备纹理模板方案 |

### 16.5 后续资源

| 资源 | 用途 |
| --- | --- |
| NPC 专属摊位家具/招牌 | 日替摊位视觉 |
| NPC 专属商品图标 | 27 个村民商店 |
| 沙漠节料理锅动画 | Cook 交互表现 |
| Gil's Hat 等专属奖励 | 原版 Gil 高段位奖励；StardewCraft 由 Marlon 代发 |
| 沙漠节音乐 `event2` | 节日音乐 |
| 赛跑音效、whistle、coin、openBox 等 | 交互反馈 |

当前版本明确不做 EggShop 帽子和家具商品；不要为它们登记临时资源或临时物品。

## 17. 分阶段实施路线

### Phase 0：补账本和资源清单

目标：把原版事实、项目差异、资源缺口列完整。

任务：

- 建立沙漠节 source ledger。
- 抽取 `PassiveFestivals.json`、`Shops.json`、`SpecialOrders.json`、`Objects.json` 关键数据。
- 建立 `DESERT_FESTIVAL_ASSET_LEDGER.md` 或在资源账本中新增章节。
- 列出所有需要坐标确认的点位。

验收：

- 文档中每个功能都能追溯到原版来源或明确写为 StardewCraft 取舍。
- 没有“凭记忆”的逻辑。

### Phase 1：被动节日开闭与 overlay 门禁

目标：让 `DesertFestival` 按日期和条件进入待开放状态，但缺 overlay 时不会误开放。

任务：

- 确认 `FestivalService.conditionsPass` 的 `LOCATION_ACCESSIBLE Desert` 与项目沙漠解锁一致。
- 新增沙漠节 debug 命令或复用现有 festival debug 查询。
- 新日刷新时记录 day-of-festival。
- 10:00 后广播沙漠节开放消息。
- 未注册 overlay 时保持拒绝开放。

验收：

- 春 15-17 且沙漠已解锁时，`isPassiveFestivalDay("DesertFestival") == true`。
- 10:00 前 `isPassiveFestivalOpen` 为 false，10:00 后为 true。
- 春 18 自动关闭。

### Phase 2：沙漠 overlay

目标：沙漠公共区域可切换为节日状态并恢复。

任务：

- 准备 base/festival schem。
- 注册 `FestivalMapOverlayDefinition("DesertFestival")`。
- 验证 diff patch 数量。
- 应用时清理节日范围临时实体和掉落物。
- 恢复时还原 base block 和 block entity。
- 服务器重启后继续 apply/restore。

验收：

- 10:00 后进入沙漠看到节日 overlay。
- 春 18 后恢复普通沙漠。
- 中途重启不会半覆盖。

### Phase 3：场地交互和三花蛋显示

目标：先把沙漠节场地变成可承载子系统的入口层，而不是先造一个脱离原版来源的三花蛋闭环。

任务：

- 注册 `calico_egg`。
- 注册基础 lang、model、texture。
- 新增 Calico Egg 发放/消费/清理工具。
- 绘制场地三花蛋 HUD：moneybox 下方、避开任务组件、使用独立裁图 `calico_currency_bg.png`。
- 建立沙漠节交互路由：EggShop、Race Man、Shady Guy、Scholar、Cook、Willy、Marlon、两个日替摊位先有明确入口；原版 Gil 评分领奖入口合并到 Marlon。
- 春 18 清理 Calico Egg。

验收：

- HUD 显示玩家背包 `calico_egg` 数量，不遮挡 quest button、ping 或快捷键提示。
- HUD 不直接引用大图集。
- 各交互入口能被调试命令或交互路由定位。
- 春 18 后蛋被清除。

### Phase 4：EggShop

目标：按原版 `DesertFestival_EggShop` 结构接入当前可正确行为闭环的商品。

任务：

- 使用 `Calico Egg` 作为 trade item。
- 支持星期条件、限购库存、购买数量。
- 上架当前项目已有商品：Mystery Box、Strawberry Seeds、Mega Bomb、Mixed Seeds、Magic Rock Candy、随机食物组、SkillBook_2。
- 帽子和家具全部不上架。

验收：

- EggShop 能用蛋交易。
- 蛋不足时交易失败。
- 星期商品在对应日期显示。
- 原版缺失商品不会被假替代。

### Phase 5：日替 NPC 摊位

目标：每天两个村民开摊，商店内容按原版数据映射。

任务：

- 建立 NPC shop 数据表。
- 每日 setup 抽两个摊主。
- 接入 NPC 日程或节日站位。
- 交互打开 `DesertFestival_<NPC>` 商店。
- 未实现商品隐藏并输出 debug 统计。

验收：

- 三天抽到的摊主稳定，不因重启改变。
- 每天最多两个日替摊位。
- 玩家能与摊主交易。

### Phase 6：骷髅洞评分和 Marlon 领奖

目标：打通沙漠节核心收益循环。

任务：

- 新增 team rating 状态。
- 下到新 5 层评分 +1。
- 掉井跨区间评分增加。
- 基础石头/怪物掉蛋。
- Marlon 按原版 Gil 评分段位代发奖励。
- 新日和离开矿洞清理评分。

验收：

- 玩家越深入评分越高。
- HUD 或 debug 能看到 rating。
- Marlon 评分奖励当天只能领一次。
- 评分影响蛋收益。

### Phase 7：Calico Statue

目标：补矿洞风险/收益调节器。

任务：

- 在骷髅洞生成 Calico Statue。
- 点击雕像增加评分。
- 实现奖励类效果：10/25/50/100 蛋、refresh、no effect。
- 实现数值类效果：speed、sharp teeth、tooth file、meager meals、thin shells。
- 资源齐后补入侵类怪物效果。

验收：

- 每次激活有清晰反馈。
- 效果当天有效，离开/新日清理。
- 多人共享状态一致。

### Phase 8：2D 赛跑

目标：完成用户指定的 2D 赛跑体验。

任务：

- 新增 race state 服务端状态机。
- 新增 `DesertFestivalRaceScreen`。
- 接入 Race Man 和 Shady Guy。
- 支持下注、破坏、比赛动画、结果、领奖。
- 支持多人同步。

验收：

- 赛跑全流程不依赖 3D 实体。
- 猜中给 20 蛋，特殊首次给 100 蛋。
- 破坏消耗 1 蛋并影响结果概率。
- 关闭重开屏幕状态不丢。

### Phase 9：换装奖励

目标：完成每年一次的节日换装奖励。

任务：

- 新增 `DesertFestivalMakeoverService`。
- 新增年度领取 flag。
- 实现随机颜色皮革套装。
- 预留 pattern 数据字段。
- 如果资源齐，接入纹样 mask 渲染。
- 如果走模板方案，新增 `desert_makeover_template`。

验收：

- 每个玩家每年只能领一次。
- 发放四件装备或一个模板。
- 背包满时不会吞物品。
- 重启后领取状态保留。

### Phase 10：Willy、Marlon、Scholar、Cook

目标：补完节日支线。

任务：

- Willy 三日任务和 Golden Bobber。
- Marlon 三个一日订单。
- Scholar 四题问答。
- Cook buff 选择。

验收：

- 每项都有正确个人状态。
- 奖励发放和次数限制正确。
- 春 18 清理临时任务。

### Phase 11：全量内容、音效和 polish

目标：从可玩走向高还原。

任务：

- 补全 27 个 NPC 商店。
- 补全 EggShop 已确认范围内的所有商品；帽子和家具仍不做，除非后续重新确认范围。
- 补齐所有对话。
- 接入音乐 `event2` 和交互音效。
- 补齐 UI 动画和粒子。
- 补齐日历、邮件、天气/世界提示。

验收：

- 多人完整玩三天不出错。
- 春 18 后没有残留蛋、订单、overlay、状态。
- `gradle classes` 通过。
- 资源账本全部可追溯。

## 18. 数据和网络接口建议

### 18.1 服务端 payload

候选网络包：

- `OpenDesertFestivalRacePayload`
- `DesertFestivalRaceStatePayload`
- `DesertFestivalRaceGuessPayload`
- `DesertFestivalRaceSabotagePayload`
- `DesertFestivalRaceClaimPayload`
- `DesertFestivalMakeoverClaimPayload`
- `DesertFestivalCurrencySyncPayload`
- `DesertFestivalMarlonRatingClaimPayload`
- `OpenDesertFestivalMarlonChallengesPayload`
- `DesertFestivalMarlonChallengeChoicePayload`

原则：

- 客户端只发请求。
- 服务端检查时间、地点、物品、状态。
- 服务端返回结果和同步状态。
- 任何奖励都只在服务端发放。

### 18.2 Debug 命令

建议新增调试命令：

- 查询今天沙漠节状态。
- 强制设置日期为春 15/16/17。
- 给玩家 Calico Egg。
- 清理玩家 Calico Egg。
- 查看/重置 skull cavern rating。
- 强制开始下一场赛跑。
- 查看今天两个日替摊主。
- 重置玩家年度换装 flag。

这些命令只用于开发，不作为正式玩法。

## 19. 风险与防线

### 19.1 最大风险

| 风险 | 防线 |
| --- | --- |
| 地图 overlay 覆盖错误 | 坐标必须确认；先小范围 diff 验证；保留崩溃恢复 |
| Calico Egg 没清干净 | 建统一 `CalicoEggService.cleanupAllPlayers`，春 18 新日强制跑 |
| 赛跑 UI 客户端作弊 | 服务端权威，客户端只显示 |
| 多人下 race/reward 不一致 | 每个玩家个人 reward state，比赛全局 state |
| 骷髅洞评分刷爆经济 | 按原版概率曲线，不写固定高收益 |
| 换装纹样无法显示 | 不标记换装系统完整完成；纹样显示依赖资源和渲染层 |
| NPC 日替商店商品缺资源 | 未实现商品隐藏，不用假物品 |
| HUD 误用原版大图集 | 所有 HUD/GUI sprite 先裁成独立小图，代码只引用独立小图 |

### 19.2 不允许的捷径

- 不用临时贴图冒充 Calico Egg、Golden Bobber、Calico Statue。
- 不从原版 tile 坐标推算 MC 坐标。
- 不把 Calico Egg 做成永久货币。
- 不把 Calico Egg 做成脱离原版来源的“简单产出闭环”。
- 不把赛跑赢家交给客户端随机。
- 不在 HUD/GUI 中直接引用 `cursors_1_6.png`、`cursors2.png` 等大图集 source rect。
- 不注册、不上架 EggShop 帽子和家具商品。
- 不在缺 overlay 时把沙漠节标记为已开放。
- 不用天气 `Festival` 作为沙漠节业务判断。
- 不给未参加或不在节日状态的玩家误发奖励。

## 20. 验收清单

### 20.1 日期和状态

- 春 14 有提醒邮件，条件为沙漠已解锁。
- 春 15-17 是沙漠节日。
- 10:00 前节日未开放。
- 10:00 后广播开放消息。
- `getDayOfPassiveFestival("DesertFestival")` 返回 1/2/3。
- 春 18 返回 -1。

### 20.2 地图

- 沙漠 overlay 正确应用。
- 玩家仍在同一 Stardew Valley 维度。
- 节日结束后恢复普通沙漠。
- 重启后能继续 apply/restore。
- 不覆盖非公共区域。

### 20.3 Calico Egg

- 所有获取来源按阶段可用。
- 场地 HUD 使用独立裁图，位置在 moneybox 下方且不遮挡任务组件。
- 商店正确消费。
- 背包满时奖励不丢。
- 春 18 清理所有 `calico_egg`。

### 20.4 商店和 NPC

- EggShop 可交互。
- 两个日替摊主每天稳定。
- 未实现商品不会崩溃。
- 普通 NPC 交互不会覆盖节日交互。

### 20.5 赛跑

- Race Man 打开 2D 屏幕。
- 下注、破坏、比赛、结果、领奖完整。
- 多人看到同一场比赛。
- 客户端不能伪造结果。

### 20.6 换装

- 每年一次。
- 随机颜色稳定生成。
- 纹样数据写入装备或模板。
- 背包满不吞物品。
- 领取状态跨重启保留。

### 20.7 矿洞

- 评分随深入增加。
- HUD/debug 显示正确。
- 掉蛋概率受评分影响。
- Calico Statue 效果当天有效。
- Marlon 代发评分奖励每天只能领奖一次。

### 20.8 构建

- `./gradlew classes` 通过。
- 资源 JSON 可解析。
- lang JSON 可解析，中文引号必须正确转义。

## 21. 推荐下一步

下一步不要直接写一个“给蛋-花蛋”的闭环。建议按这个顺序推进：

1. 完成场地三花蛋 HUD：moneybox 下方、避开任务组件、独立裁图、4 位 MoneyDial。
2. 建立沙漠节交互路由，把 EggShop、Race Man、Shady Guy、Scholar、Cook、Willy、Marlon 和两个日替摊位入口列清楚；原版 Gil 评分领奖入口并入 Marlon。
3. 完成 EggShop 当前确认范围：已有商品、星期条件、库存、随机食物组；帽子和家具不做。
4. 做日替 NPC 摊位抽取和状态保存，但未实现商品隐藏，不用假替代。
5. 做 Marlon/骷髅洞评分系统，接入原版来源的蛋收益，并由 Marlon 承载原版 Gil 领奖语义。
6. 做 Calico Statue。
7. 做 2D 赛跑。
8. 做 Willy、Marlon、Scholar、Cook。
9. 做年度换装奖励和后续 polish。

这样推进时，`Calico Egg` 会自然从各个原版子系统中产生和消耗，而不是先被做成一个错误的独立经济。