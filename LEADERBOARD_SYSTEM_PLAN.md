# 排行榜系统规划

## 目标

在 V 键菜单中把当前占位的“选项”页替换为“排行榜”页，让玩家可以在游戏内查看全服玩家的星露谷式长期排行：金币、采矿、钓鱼、出货、战斗、生活趣味榜等。

这个系统不应做成单独悬空的 UI，而应接在现有 `StardewGameMenuScreen` 里。当前 V 键入口、tab 绘制、菜单边框、滚动列表、服务端请求数据、客户端缓存显示，这些能力项目里已经有雏形。排行榜第一版应该沿用这些已有模式，减少新系统的 UI 风格漂移和代码风险。

## 当前实施状态

当前代码只完成了第一版的基础闭环，不代表本规划全部完成。

已实现：

- V 键菜单 Tab 8 从“选项”替换为“排行榜”。
- 服务端按榜单生成排行榜快照。
- 客户端请求/接收排行榜数据并缓存。
- UI 按页显示每页 10 名、当前玩家排名、刷新按钮、上一页/下一页按钮和榜单切换按钮。
- UI 显示统计人数、快照更新时间、按钮 tooltip、榜单物品图标、前三名奖章图标和细分服务端错误状态。
- 排行榜顶部已拆成标题行、控制行、分类行，分类按钮按可用宽度自动换行，避免中文/GUI scale 下压字。
- 已接入金币、矿井最深层、手挖矿洞方块、爆破矿洞方块、钓鱼总数、出货数量、出货价值、怪物击杀、垃圾桶探索。
- `LeaderboardMetric` 已包含 category 和 descending 元数据，服务端排序读取 metric 排序方向。
- 离线玩家名会在登录时写入 `lastKnownName`，排行榜优先使用该持久化名称。
- 服务端排行榜快照已有短 TTL 缓存；玩家数据或矿井数据变更时会主动清空缓存，避免等待 TTL。

第一版仍需实机复核：

- UI 需要继续实机校准，尤其不同 GUI scale 下的表头、分隔线、滚动条和底部自己排名区。

明确不属于第一版的扩展：日榜、周榜、季榜、农场资产榜。这些需要周期快照/资产估值系统，不能混进当前总榜实现里临时硬算。

## 第二版扩展规划：更多榜单 + 日/周/季/总榜

第二版目标不是把第一版简单加几个 enum，而是把排行榜从“9 个总榜”升级成一个稳定的排行框架：同一套 UI、同一套网络请求、同一套缓存，可以按榜单和周期查看数据。

新增规划必须遵守当前排行榜 UI：榜单入口继续使用当前侧边图标页签，不改成大块文字按钮；图标使用我们自己的物品/方块/自定义图标；标题、说明、列表、自己排名仍在当前主面板内显示。后续实现前必须先以当前 `StardewGameMenuScreen` 的 `LeaderboardLayout`、`LeaderboardMetricButtonBounds`、`leaderboardMetricButtonBounds(...)` 为准，不能按旧 UI 记忆重写。

### 周期维度

第二版新增 4 个周期：

| 周期 | 含义 | 数据来源建议 | 重置/结算点 |
| --- | --- | --- | --- |
| 总榜 | 玩家历史累计/当前状态 | 现有累计字段或当前状态字段 | 不重置 |
| 季榜 | 当前星露谷季节内累计 | 新增周期统计 | 换季时自动归零/换 key |
| 周榜 | 当前星露谷周内累计 | 新增周期统计 | 每 7 天换 key |
| 日榜 | 当前星露谷日内累计 | 新增周期统计 | 每天换 key |

不要用“当前总数差值”临时推日/周/季榜。这样会在服务器重启、玩家离线、旧存档迁移、回档、统计字段修正时变得很脆。第二版应新增一个小型周期统计层，按事件发生时同时写入：总量字段、日 key、周 key、季 key。

建议新增概念：

```text
LeaderboardPeriod
    TOTAL
    SEASON
    WEEK
    DAY

PlayerLeaderboardPeriodStats
    int dayKey
    int weekKey
    int seasonKey
    Map<String, Long> dayValues
    Map<String, Long> weekValues
    Map<String, Long> seasonValues
```

请求排行榜时，客户端应同时传 `metricId + periodId + page`。客户端缓存也要从当前的 `metric + page` 扩展成 `metric + period + page`，否则切换“日/周/季/总”会串数据。

### 第二版榜单清单

第二版至少扩到这些榜单：

| 分类 | 榜单 | 总榜可行性 | 日/周/季可行性 | 数据口径 |
| --- | --- | --- | --- | --- |
| 财富 | 金币榜 | 已有 | 日/周/季不建议做“当前金币” | 当前持有金币只适合总榜/当前榜，不适合周期累计。 |
| 财富 | 出货价值榜 | 已有 | 可做 | 夜间出货结算时累加本周期出货价值。 |
| 采矿 | 最深矿层榜 | 已有 | 周/季可做，日榜意义一般 | 总榜看历史最深；周期榜可记录周期内新抵达最深层。 |
| 采矿 | 手挖方块榜 | 已有总量 | 可做 | 镐子挖矿洞方块数。 |
| 采矿 | 爆破方块榜 | 已有总量 | 可做 | 星露谷炸弹炸开的矿洞方块数。 |
| 采矿 | 石头/矿石/宝石矿/矿物节点细分榜 | 已有总量字段 | 可做 | 已有细分字段，周期榜需在同一埋点补周期值。 |
| 钓鱼 | 钓鱼总数榜 | 已有 | 可做 | 成功钓起数量。 |
| 出货 | 出货数量榜 | 已有 | 可做 | 夜间出货物品数量。 |
| 出货 | 出货种类榜 | 可直接从 `itemsShipped` 算 | 不建议第一批做周期 | 不同物品出货种类数，偏收藏向。 |
| 战斗 | 怪物击杀榜 | 已有 | 可做 | 所有怪物击杀总数。 |
| 技能 | 农耕/钓鱼/采集/采矿/战斗技能榜 | 已有 | 周期榜建议用经验增长 | 总榜用经验值排序，比等级更少并列；显示可写“Lv.10 / 经验 15000”。 |
| 社交 | 送礼次数榜 | 需要新增累计 | 可做 | 只统计真正成功送出的普通礼物；任务交付不算礼物。星之果茶是否计入需要单独口径。 |
| 烹饪 | 烹饪次数榜 | 基础字段已有，但入口需补记录 | 可做 | `RecipeCatalogData.getCookingRecipeIds()` 可区分烹饪；烹饪锅成功产出后记录数量。 |
| 动物 | 动物拥有榜 | 可从 `AnimalWorldData` 汇总 | 当前拥有不适合日榜；季/周可做新增/最高拥有 | 按玩家/农场拥有的动物数量，默认算农场 owner 口径。 |
| 动物 | 动物产物榜 | 需要补领取统计 | 可做 | 手动拾取、挤奶、剪毛、自动采集器都要进入同一个统计口径。 |
| 生活 | 垃圾桶探索榜 | 已有 | 可做 | 翻垃圾桶次数。 |
| 生活 | 昏倒/死亡榜 | 需要新增累计 | 可做 | 可拆“战斗死亡”“2AM 昏倒”“体力耗尽昏倒”，也可先做合计。 |

### 数据现状与埋点路线

已能直接接总榜或低成本扩展：

- 技能榜：`PlayerStardewData.getSkillLevel(...)` / `getSkillExperience(...)` 已有。总榜建议按经验排序，显示等级和经验时需要排行榜 payload 支持副文本，或者先只显示经验。
- 烹饪榜：`recipeCraftCounts` 已持久化，`RecipeCatalogData.getCookingRecipeIds()` 能区分烹饪配方。但当前烹饪锅成功产出路径需要确认是否调用 `recordRecipeCrafted(...)`；若没有，就在 `CookingPotCookSubmitPayload` 成功产出后补记录。
- 动物拥有榜：`AnimalWorldData` 有 building owner 和 `memberAnimalIds()`，可以按 owner/农场汇总当前动物数。
- 采矿细分榜：石头、矿石、宝石矿、矿物节点字段已经存在，只差暴露成 metric 和图标。

需要新增长期累计字段：

- 送礼次数：当前 `NpcFriendshipDataManager.FriendshipState` 只有 `giftsThisWeek`、`lastGiftDayKey` 等限制计数，没有历史总次数。应在 `receiveGift(...)` 真正通过限制并消耗礼物时，给玩家统计 `giftsGivenTotal`，并同步写周期统计。
- 死亡/昏倒：`PassOutService.onCombatDeath(...)`、`on2AMPassOut(...)`、`onExhaustionPassOut(...)` 是明确入口，但 `PlayerStardewData` 当前只有状态和上次丢失物品，没有累计次数。应新增 `combatDeaths`、`passOuts2Am`、`passOutsExhaustion` 或一个 map。
- 动物产物：领取入口分散在 `AnimalProduceSpotBlock`、挤奶、剪毛、`AutoGrabberBlockEntity`。必须统一封一个记录方法，例如 `recordAnimalProduceCollected(playerId, stackCount)`，避免手动领取统计了、自动采集器漏掉。

### UI 规划：沿用当前侧边图标页签

当前排行榜 UI 已经是：主面板显示标题/控制/列表/自己排名，左侧用固定 `56x56` 图标页签切换榜单，激活页签向主面板方向延伸 `8`。第二版沿用这个结构。

但新增榜单后，侧边图标会超过屏幕高度，所以第二版不能继续简单 `for (LeaderboardMetric.values())` 从上往下全画。建议改为：

1. 左侧主图标栏仍然是榜单图标，尺寸、颜色、激活偏移沿用当前实现。
2. 图标栏支持滚动，只滚动侧边图标，不滚动主排行榜列表。
3. 图标按分类排序，中间用很细的分隔线或小间距，不放大段文字。
4. hover tooltip 显示榜单名和说明，主面板标题也同步显示当前榜单名。
5. 周期选择也用图标，不用文字按钮。可以放在主图标栏顶部的一组小图标，或在主图标栏旁边做一条较窄的周期栏：总榜、季榜、周榜、日榜。
6. 所有图标从 metric/period 元数据解析，不在 `drawLeaderboardMetricButtons(...)` 里散落 switch。这样新增榜单只改元数据，不反复改 UI 绘制。

周期图标建议：

| 周期 | 图标方向 |
| --- | --- |
| 总榜 | 星露谷奖杯/金星/宝箱类图标 |
| 季榜 | 季节相关图标，优先用四季/日历风格资源 |
| 周榜 | 日历页/告示牌类图标 |
| 日榜 | 太阳/时钟/今日标记类图标 |

指标图标建议：

| 榜单 | 图标方向 |
| --- | --- |
| 技能榜 | 对应工具：锄头/鱼竿/斧头/镐子/剑，或统一技能星图标分五个变体。 |
| 送礼次数 | 礼物盒图标，优先复用社交页礼物视觉。 |
| 烹饪次数 | 烹饪锅或具体料理。 |
| 动物拥有 | 鸡/牛/动物建筑图标。 |
| 动物产物 | 蛋/奶/羊毛图标，合计榜用混合产物图标。 |
| 死亡/昏倒 | 医院/床/头骨不建议太现代，优先用床、急救、矿井倒下语义图标。 |

### 推荐落地顺序

1. 先更新排行榜框架：新增 `LeaderboardPeriod`，请求/同步/cache 加 period 维度，UI 只先显示总榜周期，不新增大量埋点。
2. 扩总榜低风险指标：技能经验榜、采矿细分榜、烹饪总次数榜、动物当前拥有榜。
3. 补明确埋点：送礼次数、死亡/昏倒次数、动物产物领取次数。
4. 接周期统计层：所有“事件型指标”写入日/周/季；当前状态型指标只保留总榜或另设“当前榜”。
5. 最后做 UI 容量升级：侧边图标栏滚动、周期图标栏、tooltip、翻译、图标补齐。

### 不建议混在第二版第一批里的内容

- 农场资产估值榜：动物、建筑、库存、机器、装饰、作物都需要定价规则，容易变成另一套经济系统。
- 当前金币的日/周/季榜：金币是状态，不是周期行为，做“日金币榜”会让玩家误解为“今天赚的钱”。如果要周期财富榜，应做“今日收入/本周收入/本季收入”，数据来自出货、售卖、任务奖励等收入事件。
- 过细的单 NPC 送礼榜、单动物品类产物榜：第一批先做总次数，等框架稳定再做细分。

## 已确认的现有基础

- V 键绑定在 `src/main/java/com/stardew/craft/client/ModKeyMappings.java`，`GAME_MENU` 使用 `GLFW_KEY_V`。
- V 键打开 `src/main/java/com/stardew/craft/client/gui/menu/StardewGameMenuScreen.java`。
- `StardewGameMenuScreen` 当前有 10 个 tab，Tab 8 是 `stardewcraft.game_menu.tab.options`，目前只显示占位内容。
- `StardewGameMenuScreen` 已有 `ui(int stardewPixels)`、`StardewRenderMapping`、`mapping.s4()`、`StardewGuiUtil.drawDialogueBoxFrame(...)` 这套星露谷 UI 缩放/边框体系。
- 农场管理 tab 已经有“点击 tab 后请求服务端数据，然后客户端缓存渲染”的模式：`RequestFarmPermPayload` -> `FarmPermSyncPayload` -> `FarmPermissionClientCache`。
- 玩家星露谷数据由 `PlayerDataManager` 统一用 `SavedData` 持久化，`PlayerStardewData` 已有金币、钓鱼、出货、怪物击杀、垃圾桶等统计。
- 矿井进度由 `MiningDataManager` / `MiningPlayerData` 持久化，已有最深层数 `maxFloorReached`。

## 设计原则

1. 服务端权威：排行榜排序只在服务端做，客户端只显示服务端给出的快照。
2. 不发全量玩家数据：客户端请求某个榜单，服务端只返回该榜前若干名和当前玩家自己的排名。
3. 数据口径清楚：每个榜单必须明确“算什么、不算什么”。
4. 先做总榜：第一版只做历史总榜，日榜/周榜/季榜以后再加。
5. UI 风格跟 V 键菜单一致：羊皮纸边框、棕色文字、像素图标、轻量 hover，高密度但不现代后台风。
6. 坐标和 hitbox 共用同一套常量：渲染区域、鼠标点击区域、滚动裁剪区域不能各算各的。
7. 新字段兼容旧存档：所有新增统计默认 0，不影响老档加载。

## 第一版榜单范围

第一版目标是“可用、真实、有多人服务器传播感”，不要一次做完整赛季系统。

### 必做榜单

| 榜单 | 数据来源 | 备注 |
| --- | --- | --- |
| 福布斯金币榜 | `PlayerStardewData.money` | 当前持有金币，不是历史总收入。 |
| 最深矿层榜 | `MiningPlayerData.maxFloorReached` | 采矿玩家最直观目标。 |
| 挖掘方块榜 | 新增统计 | 只算玩家用镐子挖掉的矿洞方块。 |
| 爆破方块榜 | 新增统计 | 只算玩家用星露谷炸弹炸掉的矿洞方块，不污染手挖榜。 |
| 钓鱼总数榜 | `PlayerStardewData.preciseFishCaught` | 已有总钓鱼数量。 |
| 出货数量榜 | `PlayerStardewData.itemsShipped` 汇总 | 第一版算数量，出货总价值第二版做。 |
| 出货价值榜 | `PlayerStardewData.totalShippingGold` | 夜间结算时累加，避免按当前价格回算历史。 |
| 怪物讨伐榜 | `PlayerStardewData.monsterKillCounts` 汇总 | 第一版算所有怪物总数。 |
| 垃圾桶探索榜 | `PlayerStardewData.trashCansChecked` | 趣味榜，星露谷味很强。 |

### 暂缓榜单

- 宝石矿/铱矿榜：需要细分矿石类型统计。
- 本日/本周/本季榜：需要周期统计快照和重置逻辑，第一版先不做。
- 农场资产榜：需要对建筑、动物、库存、装饰、作物估值，范围过大。

## 数据层规划

### 可复用现有字段

`PlayerStardewData` 中已有：

- `money`：当前金币。
- `fishCatchCounts`：按鱼/物品 id 记录捕获数量。
- `preciseFishCaught`：累计钓鱼数量。
- `itemsShipped`：按物品 id 记录出货数量。
- `trashCansChecked`：垃圾桶检查次数。
- `monsterKillCounts`：按 Gil 目标 key 记录怪物击杀数量。

`MiningPlayerData` 中已有：

- `currentFloor`：当前矿层。
- `maxFloorReached`：历史最深矿层。

### 第一版建议新增字段

新增字段可以先直接放进 `PlayerStardewData`，字段少、读写路径短。等排行榜扩展到赛季榜和大量细分榜后，再考虑拆出 `PlayerLeaderboardStats`。

建议字段：

```java
private int mineBlocksBrokenTotal;
private int mineStonesBroken;
private int mineOresBroken;
private int mineGemOresBroken;
private int mineMineralNodesBroken;
private int mineBlocksBombed;
```

当前 UI 展示 `mineBlocksBrokenTotal` 和 `mineBlocksBombed`。石头、矿石、宝石矿、矿物节点细分字段已持久化，后续做矿石榜/宝石榜时不用迁移数据。

### 挖掘统计接入点

手挖矿洞方块：

- 接 `src/main/java/com/stardew/craft/event/MinePickaxeEvents.java` 的 `onBlockBreak(BlockEvent.BreakEvent event)`。
- 在所有权限、工具、能量校验通过后，和采矿经验一起记录。
- 只统计非创造模式玩家。
- 只统计 `isStardewMineBlock(state)` 为 true 的方块。

炸弹破坏方块：

- 接 `src/main/java/com/stardew/craft/entity/bomb/StardewBombEntity.java` 中调用 `MiningBlockBreakHandler.handleStoneBreak(...)` / `MinePickaxeEvents.applyPlayerStyleBombDrops(...)` 的路径。
- 炸弹数量不要加到 `mineBlocksBrokenTotal`，应加到 `mineBlocksBombed`。
- UI 单独开“爆破方块榜”，读取 `mineBlocksBombed`。

### 出货价值字段

已实现：

```java
private long totalShippingGold;
```

该字段在 `PlayerStardewDataAPI.recordOvernightShippedItems(...)` 的夜间结算路径累加，排行榜直接读取历史累计值。

设计原因：不要每次排行榜请求时回算 `itemsShipped`，因为历史售价、职业加成、平衡调整都可能变化，回算会导致老数据漂移。

历史规划：

第二版建议新增：

```java
private long totalShippingGold;
```

接 `PlayerStardewDataAPI.recordOvernightShippedItems(...)`，在夜间结算发金币时同步累加。不要每次排行榜请求时回算 `itemsShipped`，因为历史售价、职业加成、平衡调整都可能变化，回算会导致老数据漂移。

### 玩家显示名

排行榜必须显示离线玩家，所以需要保存玩家最后一次登录名。

建议新增：

```java
private String lastKnownName;
```

在玩家登录时写入 `PlayerStardewData`。如果旧存档为空，排行榜显示短 UUID 兜底，例如 `Player-1a2b3c4d`。

## 服务端排行榜层

新增包建议：

```text
src/main/java/com/stardew/craft/leaderboard/
  LeaderboardMetric.java
  LeaderboardCategory.java
  LeaderboardEntry.java
  LeaderboardSnapshot.java
  LeaderboardService.java
```

### LeaderboardMetric

第一版枚举：

```java
MONEY,
MINE_DEPTH,
MINE_BLOCKS_BROKEN,
MINE_BLOCKS_BOMBED,
FISH_CAUGHT,
ITEMS_SHIPPED,
MONSTERS_SLAIN,
TRASH_CANS_CHECKED
```

每个 metric 应提供：

- id：网络传输与语言 key 使用。
- category：财富、采矿、钓鱼、出货、战斗、生活。
- titleKey：显示名。
- descriptionKey：鼠标悬停说明。
- unitKey 或 formatter：金币、层、个、次。
- descending：排序方向，当前总榜均为降序。

### LeaderboardService

职责：

1. 读取所有 `PlayerStardewData`。
2. 读取矿层时额外查 `MiningDataManager`。
3. 按 metric 取数值。
4. 过滤没有有效数据的玩家，或者保留 0 值玩家由 UI 决定。
5. 排序。
6. 生成前 N 名。
7. 计算请求玩家自己的排名。
8. 返回不可变快照。

排序建议：

1. value 降序。
2. 玩家名升序。
3. UUID 升序兜底。

并列名次建议第一版不做复杂并列，直接 1、2、3 排名。原因是 UI 简洁，玩家也更容易理解。后续如果要同分同名次，再改 `rank` 计算。

### 缓存策略

第一版可以每次请求实时排序，因为玩家量通常不大。但要保留扩展空间：

- 每个 metric 缓存最近一次结果。
- 数据更新时不立刻重算，只标记 dirty。
- 请求时如果缓存没过期且不 dirty，直接返回。
- 缓存 TTL 可以先设 5 秒，防止玩家快速切 tab 造成重复排序。

## 网络包规划

沿用农场管理页模式。

新增：

```text
src/main/java/com/stardew/craft/network/payload/RequestLeaderboardPayload.java
src/main/java/com/stardew/craft/network/payload/LeaderboardSyncPayload.java
src/main/java/com/stardew/craft/client/LeaderboardClientCache.java
```

### RequestLeaderboardPayload

客户端到服务端：

```java
String metricId;
int page;
```

`page` 已用于服务端分页请求，每页返回 10 名。客户端切换上一页/下一页时会重新请求该页快照。

### LeaderboardSyncPayload

服务端到客户端：

```java
String metricId;
int page;
List<Entry> rows;
Entry selfEntry;
int totalPlayers;
long generatedAtMillis;
```

`Entry`：

```java
UUID playerId;
String playerName;
long value;
int rank;
boolean online;
boolean self;
```

### 客户端缓存状态

`LeaderboardClientCache` 需要支持：

- 当前 metric。
- rows。
- selfEntry。
- loading 状态。
- lastUpdated。
- error/empty 状态。

切换到排行榜 tab 或切换 metric 时：

1. 设置 loading。
2. 发送 `RequestLeaderboardPayload`。
3. 收到 `LeaderboardSyncPayload` 后更新缓存。

## UI 参考与取舍

### 主要参考：FarmJoinSelectScreen

`src/main/java/com/stardew/craft/client/gui/FarmJoinSelectScreen.java` 是最适合排行榜的参考。

可复用思路：

- 羊皮纸外框：`StardewGuiUtil.drawDialogueBoxFrame(...)`。
- 标题居中。
- 标题下方用 `drawHorizontalPartition(...)` 分隔。
- 列表区使用 `enableScissor(...)` 裁剪。
- 行高固定。
- 选中行用 `0x44EADB8C` 这类浅金透明底。
- hover 行有柔和过渡。
- 右侧细滚动条。
- 底部显示一行提示。

排行榜页的主列表可以直接借这个视觉语言：左侧名次徽章，中间玩家名，右侧数值。

### 次要参考：GilGoalsScreen

`src/main/java/com/stardew/craft/client/gui/GilGoalsScreen.java` 适合参考紧凑信息表达。

可借：

- 标题 + 分隔线 + 裁剪列表。
- 每行交替背景。
- 右侧状态/按钮/图标。
- 进度条风格可用于以后“距离上一名还差多少”。

但第一版排行榜不建议塞进度条，以免信息过密。

### 不作为主参考：FarmAdminScreen

`src/main/java/com/stardew/craft/client/gui/FarmAdminScreen.java` 是后台管理风，黑色面板、密集表格、小按钮多，适合管理员工具，不适合普通玩家排行榜。

可借：

- 表头列对齐。
- 行内固定列宽。
- 行背景奇偶交替。
- 文本截断。

不借：

- 黑色面板色彩。
- 行内过多操作按钮。
- 管理后台感。

## 排行榜页面布局

排行榜页放在 `StardewGameMenuScreen` 的 Tab 8。外层继续由当前菜单统一绘制：

```java
StardewGuiUtil.drawDialogueBoxFrame(graphics, menuX, menuY, activeMenuWidth(), menuHeight);
drawTabs(graphics);
drawCloseButton(graphics);
drawLeaderboardPage(graphics, mouseX, mouseY);
```

### 推荐结构

```text
┌────────────────────────────────────────────┐
│                  排行榜                     │
│ 财富  采矿  钓鱼  出货  战斗  生活  [刷新] │
├────────────────────────────────────────────┤
│ #   玩家                         数值       │
│ 1   Alice                      120000 G     │
│ 2   Bob                         86000 G     │
│ 3   Carol                       64000 G     │
│ ...                                        │
│                                            │
├────────────────────────────────────────────┤
│ 我的排名  #17  Jiayuhan          3500 G     │
└────────────────────────────────────────────┘
```

### 区域划分

建议使用 Stardew 像素坐标定义，再统一走 `ui(...)` 转 GUI 坐标。

```java
LEADERBOARD_PAD_X_SDV = 64;
LEADERBOARD_PAD_TOP_SDV = 56;
LEADERBOARD_TITLE_H_SDV = 40;
LEADERBOARD_CATEGORY_H_SDV = 48;
LEADERBOARD_HEADER_H_SDV = 36;
LEADERBOARD_ROW_H_SDV = 56;
LEADERBOARD_SELF_ROW_H_SDV = 48;
LEADERBOARD_SCROLLBAR_W_SDV = 12;
```

布局推导：

```java
int contentX = menuX + ui(64);
int contentY = menuY + ui(56);
int contentW = menuWidth - ui(128);
int contentBottom = menuY + menuHeight - ui(48);

int titleY = contentY;
int categoryY = titleY + ui(40);
int headerY = categoryY + ui(48);
int listY = headerY + ui(36);
int selfRowH = ui(48);
int selfY = contentBottom - selfRowH;
int listBottom = selfY - ui(12);
int rowH = ui(56);
int visibleRows = Math.max(1, (listBottom - listY) / rowH);
```

不要用多个地方分别写 `menuY + xxx`。所有渲染和点击都从这些派生变量拿。

## UI 严丝合缝规则

这一节是实现排行榜页时最重要的约束。

### 1. 单一坐标源

每一帧 `render(...)` 开始调用 `recalcLayout()`，然后调用一个 `computeLeaderboardLayout()`，返回布局对象。

示意：

```java
private LeaderboardLayout leaderboardLayout() {
    int contentX = menuX + ui(64);
    ...
    return new LeaderboardLayout(...);
}
```

渲染、鼠标点击、滚轮、hover 检测都使用同一个 `LeaderboardLayout`。不要在 `mouseClicked` 里重新手写一套坐标。

### 2. 所有 SDV 像素只转一次

常量用 SDV 像素表示，落到屏幕坐标时只调用一次 `ui(...)`。

正确：

```java
int rowH = ui(LEADERBOARD_ROW_H_SDV);
int rowY = layout.listY + visibleIndex * rowH;
```

避免：

```java
int rowY = ui(layout.listY + visibleIndex * LEADERBOARD_ROW_H_SDV);
```

后者容易把已经是 GUI 坐标的值再次缩放，导致不同 GUI scale 下错位。

### 3. 顶部区域必须分行计算

排行榜顶部不要把标题、分页、刷新、统计信息、分类按钮塞进同一条 y 轴。

当前实现按这些区域依次计算：

```java
titleY -> controlY -> metricY -> headerY -> listY -> selfY
```

- 标题独占标题行。
- 统计信息在控制行左侧，按分页按钮左边界做像素截断。
- 上一页/下一页/页码/刷新在控制行右侧。
- 分类按钮使用 `contentW` 自动换行；`headerY` 必须由实际分类行数推导，不能假定只有一行。

### 4. 列宽固定，右列右对齐

排行榜是表格，列必须固定：

```java
rankX = contentX + ui(12);
iconX = contentX + ui(54);
nameX = contentX + ui(94);
valueRightX = contentX + contentW - ui(32);
```

数值用右对齐：

```java
graphics.drawString(font, valueText, valueRightX - font.width(valueText), y, color, false);
```

不要按玩家名宽度推导数值位置，不然每行会抖。

### 5. 文本按像素截断

不要用 `substring(0, 18)` 这种字符数截断，因为中文、英文、符号宽度不同。

需要一个工具方法：

```java
private String ellipsize(String text, int maxWidth) {
    if (font.width(text) <= maxWidth) return text;
    String ellipsis = "...";
    int target = Math.max(0, maxWidth - font.width(ellipsis));
    String result = text;
    while (!result.isEmpty() && font.width(result) > target) {
        result = result.substring(0, result.length() - 1);
    }
    return result + ellipsis;
}
```

玩家名列必须限制宽度：

```java
int nameMaxW = valueRightX - nameX - ui(24);
```

### 6. Scissor 裁剪必须覆盖完整列表

列表渲染：

```java
graphics.enableScissor(layout.contentX, layout.listY, layout.contentX + layout.contentW, layout.listBottom);
...
graphics.disableScissor();
```

注意：滚动条不要画在 scissor 里面，否则 thumb 可能被裁掉。

### 7. 行背景不要顶到边框

行背景左右留 4-8 SDV 像素，让它不压住羊皮纸边框。

```java
int rowX = contentX + ui(4);
int rowW = contentW - ui(16);
graphics.fill(rowX, rowY + 1, rowX + rowW, rowY + rowH - 1, rowColor);
```

### 8. 分隔线职责要分清

大区域分隔用：

```java
StardewGuiUtil.drawHorizontalPartitionSmall(graphics, contentX, y, contentW, mapping.s4());
```

不要同时用贴图分隔线和 `fill` 分隔线混搭。

- 页面大分区可以用 `drawHorizontalPartitionSmall`。
- 排行榜表格内部只保留两条细线：表头下线和“我的排名”上线。
- 这两条表格线必须共用同一个 helper，x/width/height/color 完全一致，只允许 y 不同。
- 表头下线由 `listY` 反推，“我的排名”上线由 `selfY` 反推，不能再出现一个相对 `headerY`、一个相对 `selfY - 18` 的独立算法。

### 9. 高度来自剩余空间，不硬编码可见行数

排行榜在不同窗口、不同 GUI scale 下可见行数不同。用剩余高度算：

```java
visibleRows = Math.max(1, (listBottom - listY) / rowH);
maxScroll = Math.max(0, rows.size() - visibleRows);
scroll = Math.min(scroll, maxScroll);
```

### 10. 鼠标 hitbox 和绘制行一致

行点击检测必须使用同一个 `rowY`、`rowH`、`rowX`、`rowW`。

```java
boolean hovered = mouseX >= rowX && mouseX < rowX + rowW
    && mouseY >= rowY && mouseY < rowY + rowH;
```

不要把视觉上行背景高度 54，点击高度 64。

### 11. 滚动条公式固定

参考 `FarmJoinSelectScreen`：

```java
int barX = contentX + contentW - ui(6);
int barTotalH = visibleRows * rowH;
int thumbH = Math.max(ui(20), barTotalH * visibleRows / rows.size());
int maxScroll = Math.max(1, rows.size() - visibleRows);
int thumbY = listY + (barTotalH - thumbH) * scroll / maxScroll;
```

滚动条高度和列表实际显示高度一致，不要用 `listBottom - listY` 和 `visibleRows * rowH` 混着算。

### 12. 图标尺寸固定

榜单分类图标、前三名奖章、在线点都固定尺寸。

建议：

- 分类图标：`ui(28)`。
- 排名奖章：`ui(24)`。
- 在线点：`ui(6)`。
- 玩家头像如果做：`ui(24)` 或 `ui(32)`，不要随文字高度变化。

### 13. 不在 render 中发网络包

`render(...)` 只画 UI。请求榜单放在：

- 进入 leaderboard tab 时。
- 切换 metric 时。
- 点击刷新时。

避免 render 每帧发包。

### 14. Loading 不改变布局

加载中、空榜、错误状态都画在同一个 `listY-listBottom` 区域中央，不改变标题、分类、底部“我的排名”区域。这样页面不会因为数据回来后跳动。

### 15. 自己排名固定底栏

“我的排名”不参与滚动。它固定在列表下方，与列表用分隔线隔开。这样玩家永远能看到自己。

## UI 状态

排行榜页至少要有这些状态：

1. 未请求：进入页面后立即切到 loading。
2. Loading：显示“正在读取排行榜...”。
3. Empty：显示“还没有人上榜”。
4. Ready：显示 rows + self row。
5. Error：显示“排行榜暂时不可用”，并保留刷新按钮。

## 分类与视觉文案

建议分类：

- 财富：福布斯金币榜。
- 采矿：最深矿层榜、挖掘方块榜、爆破方块榜。
- 钓鱼：钓鱼总数榜。
- 出货：出货数量榜。
- 战斗：怪物讨伐榜。
- 生活：垃圾桶探索榜。

按钮文案要短，避免塞不下：

```text
财富 / 采矿 / 钓鱼 / 出货 / 战斗 / 生活
```

当前 UI 直接显示各 metric 的短名，并用 `LeaderboardMetric.category()` 保留分类元数据。

榜单标题：

```text
福布斯金币榜
矿井探底榜
挖掘方块榜
爆破方块榜
钓鱼总数榜
出货数量榜
出货价值榜
怪物讨伐榜
垃圾桶探索榜
```

榜单说明 tooltip：

```text
按当前持有金币排序。
按玩家历史到达过的最深矿井层数排序。
按玩家用镐子挖掉的矿洞方块数量排序，不包含炸弹。
按玩家用星露谷炸弹炸掉的矿洞方块数量排序，不计入手挖榜。
按玩家累计钓起的鱼/钓鱼产物数量排序。
按玩家通过出货箱累计出货的物品数量排序。
按夜间出货结算累计获得的金币排序。
按玩家累计击杀的矿洞怪物数量排序。
按玩家检查垃圾桶次数排序。
```

## 美术资源建议

优先复用现有资源：

- 金币：`src/main/resources/assets/stardewcraft/textures/gui/gold_icon.png`。
- 采矿：`src/main/resources/assets/stardewcraft/textures/gui/ui_info/mining.png`。
- 钓鱼：`src/main/resources/assets/stardewcraft/textures/gui/ui_info/fishing.png`。
- 战斗：`src/main/resources/assets/stardewcraft/textures/gui/ui_info/combat.png`。
- 农业/出货可临时用 `farming.png` 或箱子/出货箱物品图标。
- 生活趣味榜可先用垃圾桶方块/物品图标，找不到时用文字分类。

第一版不必新画整张排行榜背景图。只需要复用羊皮纸框、现有图标、少量色块即可。

当前实现复用 MC/模组物品图标：榜单标题和分类按钮显示对应物品图标，前三名用金/铁/铜锭作为奖章图标占位。

如果后续要加新美术，建议只新增小图标：

```text
textures/gui/leaderboard/medal_gold.png
textures/gui/leaderboard/medal_silver.png
textures/gui/leaderboard/medal_bronze.png
textures/gui/leaderboard/icon_leaderboard.png
textures/gui/leaderboard/icon_trash.png
textures/gui/leaderboard/icon_shipping.png
```

## 实现阶段

### 阶段 1：文档和最小 UI 骨架

- 新增排行榜规划文档。
- 在 V 键菜单 Tab 8 上画排行榜静态骨架。
- Tab 文案从“选项”改为“排行榜”。
- 使用真实网络数据、loading、empty、error 状态验证布局。
- 验证不同 GUI scale 下行、分隔线、滚动条不歪。

### 阶段 2：服务端榜单快照

- 新增 `LeaderboardMetric`。
- 新增 `LeaderboardCategory`，并给 metric 标注 category / descending。
- 新增 `LeaderboardEntry` / `LeaderboardSnapshot`。
- 新增 `LeaderboardService`。
- 实现金币、矿井、挖掘、爆破、钓鱼、出货、战斗、垃圾桶总榜。
- 支持当前玩家 self row。

### 阶段 3：网络同步

- 新增 `RequestLeaderboardPayload`。
- 新增 `LeaderboardSyncPayload`。
- 新增 `LeaderboardClientCache`。
- 在 `PacketHandler` 注册两个 payload。
- 进入排行榜 tab 时请求默认榜单。
- 切换榜单时重新请求。

### 阶段 4：采矿榜

- 新增挖矿统计字段。
- NBT 读写兼容默认 0。
- 接入 `MinePickaxeEvents` 手挖统计。
- 接入 `StardewBombEntity` 爆破统计，不污染手挖统计。
- 接入 `MiningDataManager` 最深矿层榜。
- UI 增加采矿分类。

### 阶段 5：出货和战斗榜

- 出货数量榜：汇总 `itemsShipped`。
- 出货价值榜：使用夜间结算累计的 `totalShippingGold`。
- 怪物讨伐榜：汇总 `monsterKillCounts`。

### 阶段 6：打磨和验收

- 添加中英文翻译。
- 添加榜单图标、前三名奖章图标和细分错误提示。
- 玩家数据/矿井数据变化时主动清空排行榜 TTL 缓存。
- 检查所有按钮和 tooltip 文本宽度。
- 检查窗口缩放、GUI scale、滚轮、键盘上下键。
- 运行 `./gradlew classes`。
- 进入游戏打开 V 键实测。

## 验收清单

### 功能验收

- V 键菜单中 Tab 8 显示为“排行榜”。
- 第一次打开排行榜页会请求服务端数据。
- 数据返回前显示 loading。
- 空数据时显示空榜提示。
- 每个榜单能切换。
- 每个榜单按页显示，每页 10 名。
- 上一页/下一页按钮会请求对应页，并显示当前页码。
- 当前玩家不在前 10 时，底部仍显示当前玩家排名。
- 在线玩家显示在线状态。
- 离线玩家显示最后已知名字。
- 刷新按钮不会每帧发包，只会点击时发包。
- 非法榜单 id 和服务端构建异常会显示不同错误提示。

### UI 验收

- GUI scale 1、2、3、4 下边框无明显错位。
- 列表行背景不压住外框。
- 表头、行、底部自己排名三块之间间距稳定。
- 标题、分页/刷新、分类按钮、表头之间不共享同一行，不互相覆盖。
- 分类按钮在中英文和不同 GUI scale 下按可用宽度换行，最后一项不越出内容区。
- 滚动条 thumb 不超出轨道。
- 鼠标 hover 的区域和视觉行一致。
- 长玩家名不会压到数值列。
- 金币、层数、数量等数值右对齐。
- Loading/Empty/Ready 状态切换时整体布局不跳。
- 榜单图标、奖章图标不改变按钮/行高度。

### 数据验收

- 金币榜和玩家实际金币一致。
- 钓鱼榜和 `preciseFishCaught` 一致。
- 垃圾桶榜和 `trashCansChecked` 一致。
- 最深矿层榜和 `maxFloorReached` 一致。
- 手挖矿洞方块会增加挖掘榜。
- 炸弹破坏方块会增加爆破榜，不会增加手挖榜。
- 矿洞石头、矿石、宝石矿、矿物节点细分统计会随手挖写入存档。
- 创造模式挖矿不增加统计。
- 老存档加载不崩溃，新字段默认 0。

## 推荐第一版最终体验

玩家按 V 打开菜单，点排行榜 tab，默认显示“福布斯金币榜”。上方可以切换财富、采矿、钓鱼、出货、战斗、生活。列表前 3 名有明显名次强调，普通名次整齐排列，右侧数值对齐，底部固定显示“我的排名”。页面看起来像现有农场列表和 V 键菜单自然长出来的一页，而不是另一个风格的后台系统。

第一版做完后，排行榜就已经能成为多人服务器的长期目标入口。后续再加赛季榜、奖励、公告板展示和称号，会比较顺。