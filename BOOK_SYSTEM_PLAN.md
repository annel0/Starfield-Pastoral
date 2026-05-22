# 书籍系统 1:1 复刻接入规划

> 目标：将 Stardew Valley 1.6 的书籍系统完整接入 StardewCraft，包含读书行为、永久能力、重复阅读经验、书商/兑换/掉落来源、Powers/成就状态与客户端反馈。
> 状态：规划阶段，尚未开始代码实现。
> 最后更新：2026/05/21

> 2026/05/22 项目规则覆盖：`Book_PriceCatalogue` 已从本项目书籍系统移除，不再注册物品/模型/贴图，也不再控制 tooltip 卖价显示；`Book_AnimalCatalogue` 改为不可消耗的特殊目录书，阅读完成后直接打开 Marnie 动物购买界面，不再作为永久能力或 Well Read 条件。当前可注册书籍为 25 本，永久能力书为 17 本。

> 2026/05/23 缺口规划更新：核心阅读、书籍状态、读书表现和多数永久效果 hook 已落地；阶段 1-3 正在补齐书籍生态闭环：书摊按原版季节候选日出现、书摊购买库存改为每日限购、现有可承载路径接入 `Book_Woodcutting` / `Book_Roe` / `Book_Void` / `Book_Defense` / `Book_Mystery` / `PurpleBook` 的掉落来源。`Book_QueenOfSauce` 先按 Year 3 书摊临时出售，TODO：后续有 Golden Walnuts 世界状态后改回 100 Golden Walnuts。礼盒/兑奖券/浣熊/鱿鱼节/火山商店/沙漠节等缺底层系统的来源本阶段不做；Well Read 暂不做正式 achievement/advancement。

---

## 一、硬约束

1. **唯一事实来源是 SDV 源代码与数据文件**
   - 行为逻辑以 `源文件/` 下 C# 源码为准。
   - 物品、价格、类别、sprite、context tag 以 `源文件/Content/Data/Objects.json` 为准。
   - 商店库存、条件、价格、兑换以 `源文件/Content/Data/Shops.json` 为准。
   - Powers 展示以 `源文件/Content/Data/Powers.json` 为准。
   - Wiki、记忆和主观体验只能辅助检索，不能作为最终实现依据。
2. **不得做“差不多”的数值或条件**
   - 概率、倍率、经验、年份、商店条件、重复阅读规则必须逐字映射。
   - 如果 MC 侧系统尚不存在，先补齐承载系统或明确阻塞，不能用无效占位冒充已接入。
3. **所有书籍必须有用**
   - 每本书至少完成：物品注册、读书行为、首次阅读效果、重复阅读规则、获取来源、tooltip/已读状态、存档持久化。
   - 20 本 `Book_*` 中除 `Book_QueenOfSauce` 外，19 本必须接入 Well Read/Powers 统计。
   - `Book_PriceCatalogue` 与 `Book_AnimalCatalogue` 遵从原版：已读后不可通过重复阅读转经验。
4. **读书视觉可以复用 MC 附魔台书，但只作为 3D 载体**
   - 视觉目标：玩家面前出现一本类似原版附魔台上方的书，打开、翻页、合上。
   - 实现方式：客户端通过 Minecraft 内置 book model / texture / render path 渲染，不复制 Mojang 源文件；只调用可用客户端 API 或复刻必要的参数驱动。
   - 这属于 StardewCraft 的 3D 适配层；运行时读书翻页视觉以 MC 附魔台书模型为准，不播放 SDV 的 `LooseSprites\\Book_Animation` 临时 sprite。
   - MC 附魔台声音、附魔粒子、附魔台方块实体逻辑不得混入读书反馈。
5. **所有素材必须从原版资产中单独截取并纳入本项目资源**
   - 书籍物品贴图、彩虹爆星、声音等 SDV 保留元素必须从 SDV 原版资产中按元素截出，放入 `src/main/resources/assets/stardewcraft/` 对应目录。
   - 不允许运行时直接引用 Cursor/外部工作区/原版安装目录中的 loose asset；构建产物必须自包含。
   - 不允许用 MC 原版贴图或临时占位替代 SDV 书籍 item icon、声音和彩虹爆星；MC 附魔台书只负责读书时的 3D 翻页展示。
   - 每个被截取的 sprite/sound 必须在源码锁定表记录：SDV 源资产路径、矩形/帧号/编号、导入后的 StardewCraft 资源路径、用途。
6. **所有声音必须和原版一致**
   - 已在项目资源中的声音直接对照 SDV cue 使用。
   - 项目中没有的声音，先到 `https://zh.stardewvalleywiki.com/%E6%A8%A1%E7%BB%84:%E9%9F%B3%E9%A2%91` 查对应 cue/编号，再用 `convert_music.py` 或项目确认后的等价音频转换脚本导出到本项目资源。
   - 音频转换脚本位置：`scripts/convert_music.py`。若后续新增 cue，优先使用该脚本的 `--cue NAME HEX_ID SUBDIR` 参数。
   - 音效文件名、触发时点、作用范围、是否广播必须以 SDV 源码为准。
7. **多人安全**
   - 书籍 stat 是 per-player 数据，不写世界全局。
   - 书商日期是世界级同步日期；库存可按 SDV synced random 规则复刻，但购买限制仍按原版条目区分 per-player / global。
   - 所有读书行为必须服务端判定、服务端改存档、客户端只负责表现。

---

## 二、用户问题结论

### 1. 能不能直接搬 MC 原版附魔台翻书效果？

可以作为**读书视觉载体**使用，而且很合适；但它不能替代 SDV 源码里的读书特效。

实施上不把书做成方块，也不要求玩家站在附魔台旁边。右键书籍后：服务端完成 SDV 读书判定，向该玩家发送 `ReadBookVisualPayload`，客户端在玩家视角前方短暂生成一本“附魔台书式”的悬浮书，执行打开、翻页、合上的动画。这样视觉上是“书在玩家面前翻过”，但逻辑上仍是 SDV 书籍。

边界：

- 可以使用 MC 原版书模型和贴图作为渲染素材。
- 不使用 MC 附魔台交互、附魔台方块实体、附魔声音或附魔粒子。
- SDV 的 `book_read` 音效、rainbow star explosion、toast/消息、buff/能力学习反馈必须按源码接入；读书翻页画面改用 MC 附魔台书模型。
- 书籍物品 sprite、彩虹爆星与相关视觉元素要从 SDV 原版资源中截出，导入本项目资产目录后再渲染；不能运行时直链外部资源。
- 若项目中没有 `book_read`，先按音频 wiki 找 cue/编号，再用 `convert_music.py` 或确认后的项目转换脚本导出原版音频。

### 2. 如何确保所有书籍接入后都有用？

采用“书籍完成矩阵”作为验收门槛。任何一本书缺少下面任意一项，都不算完成：

- 物品可获得。
- 右键可读，且读书条件、消耗、消息、音效按 SDV。
- 首次阅读效果生效。
- 重复阅读效果按 SDV 生效。
- tooltip/已读说明按 SDV。
- 存档重进后状态仍在。
- 多人下只影响对应玩家。

### 3. 如何保证一切以源代码为准、不得有任何区别？

每个实现点必须在代码或规划表中绑定 SDV 源码锚点。实现前先补齐“源码锁定表”，实现后按表逐项验证。若 MC 侧没有完全对应的系统，只允许做“等价承载层”，不能改概率、条件或效果含义。

示例：`Book_Crabbing` 原版条件是蟹笼收获时 `CreateDaySaveRandom(...).NextDouble() < 0.25` 且能接收双倍物品；StardewCraft 实现时必须保留 `25%`、收获时点、库存容量判定和 per-day deterministic random 语义，不能改成“随机多给一个鱼”。

---

## 三、SDV 源码锚点

### 核心读书行为

| 逻辑 | SDV 来源 | 必须复刻 |
| --- | --- | --- |
| 读书入口 | `源文件/StardewValley/Object.cs` `readBook(GameLocation location)` | 只在正常可行动状态读；阅读后消耗物品；播放读书反馈。 |
| 读书动画 | `Object.readBook` 开头 | `canMove=false`、`freezePause=1030`、面向 2、农夫 frame 57 持续 1000ms、`MusicDuckTimer=4000f`、`playSound("book_read")`。 |
| 读书 3D 翻页 | StardewCraft 适配层 + MC enchantment table book model | 右键读书时在玩家面前渲染一本 MC 附魔台式悬浮书，打开、翻页、合上；不使用附魔台方块实体逻辑、附魔声音或附魔粒子。 |
| 读完特效 | `Object.readBook` frame end behavior | 移除 id `1987654` 临时 sprite，并在玩家站位 `(-40,-156)` 触发 `Utility.addRainbowStarExplosion(..., 8)`。 |
| 技能书 | `Object.readBook` category `-103` | `SkillBook_0..4` 给对应技能 250 XP；`PurpleBook` 五技能各 250 XP。 |
| 能力书首次阅读 | `Object.readBook` category `-102` | `stats.Increment(itemId)`，显示 LearnedANewPower，添加 `read_a_book`，检查成就。 |
| 能力书重复阅读 | `Object.readBook` + `ContextTags` | 已读且不是 `Book_PriceCatalogue`/`Book_AnimalCatalogue`：有 `book_xp_*` tag 给该技能 100 XP，否则五技能各 20 XP。 |
| 酱料女皇书 | `Object.readBook` `Book_QueenOfSauce` 分支 | 学会所有尚未掌握的 Queen of Sauce recipes，显示学会数量。 |
| 已读描述 | `Object.cs` display/description 分支 | 已读能力书显示重复阅读经验说明。 |
| Well Read 成就 | `源文件/StardewValley/Stats.cs` `checkForBooksReadAchievement` | 只要求 19 本永久能力书，不含技能书、星之书、酱料女皇。 |

### 能力书效果锚点

| 书籍 | 原版效果 | SDV 来源 | StardewCraft 落点 |
| --- | --- | --- | --- |
| `Book_Trash` | 垃圾桶基础掉落概率 +0.2 | `GameLocation.cs` 垃圾桶逻辑 | `block/utility/GarbageCanLootTable.java` |
| `Book_Crabbing` | 蟹笼收获 25% 双倍 | `StardewValley.Objects/CrabPot.cs` | 蟹笼系统接入时必须同步完成 |
| `Book_Bombs` | 炸弹对玩家伤害 ×0.75 | `GameLocation.cs` 爆炸伤害逻辑 | 炸弹实体/伤害事件 |
| `Book_Roe` | 钓鱼宝箱 25% 额外鱼籽 | `StardewValley.Tools/FishingRod.cs` | `fishing/data/TreasureLootManager.java` |
| `Book_WildSeeds` | 杂草掉混合种子概率 5%→9% | `Object.cs` 杂草破坏逻辑 | `block/WildWeedsBlock` / 掉落逻辑 |
| `Book_Woodcutting` | 砍树 5% 双倍木头 | `StardewValley.TerrainFeatures/Tree.cs` | 野树砍伐事件 |
| `Book_Defense` | 防御 +1 | `Farmer.cs` defense getter | `player/PlayerDataEventHandler.java` 伤害计算 |
| `Book_Friendship` | 正向友谊增长 ×1.1 | `Farmer.cs` friendship gain | NPC 送礼/友谊 API |
| `Book_Void` | 怪物掉落 3% 复制已有掉落 | `GameLocation.cs` monster loot | 怪物掉落服务 |
| `Book_Speed` | 非骑马移速 +0.25 | `Farmer.cs` addedSpeed | 玩家 tick/属性修正 |
| `Book_Marlon` | 死亡找回价格减半 | `ItemQueryResolver.cs` MarlonRecovery | `shop/MarlonService.java` |
| `Book_PriceCatalogue` | tooltip 显示可售价格 | `IClickableMenu.cs` hover price | 物品 tooltip 渲染 |
| `Book_Diamonds` | 手动镐子凿石 0.66% 掉钻石 | `StardewValley.Tools/Pickaxe.cs` | `item/tool/StardewPickaxeItem.java` / 采矿事件 |
| `Book_Mystery` | Mystery Box 基础概率系数 0.66→0.88 | `Utility.cs` mystery box 逻辑 | `shop/GeodeLootService.java` / mystery box 掉落路径 |
| `Book_AnimalCatalogue` | Marnie 不在柜台也可开动物商店 | `GameLocation.cs` AnimalShop action | AnimalShop 交互条件 |
| `Book_Speed2` | 非骑马移速再 +0.25 | `Farmer.cs` addedSpeed | 玩家 tick/属性修正 |
| `Book_Artifact` | 古物售价 ×3 | `Object.cs` sell price | 售价计算/出货/商店回收 |
| `Book_Horse` | 骑马移速 +0.5 | `Farmer.cs` horse movement | 马/坐骑系统 |
| `Book_Grass` | 草/作物减速 -1→-0.33 | `Grass.cs` / `HoeDirt.cs` | 草、作物碰撞/移动减速 |
| `Book_QueenOfSauce` | 补齐酱料女皇菜谱 | `Object.readBook` | `PlayerStardewData.unlockedRecipes` |

---

## 四、书籍清单与完成矩阵

### 技能书与星之书

| ID | 类型 | 首次/每次阅读 | 重复阅读 | 获取来源 |
| --- | --- | --- | --- | --- |
| `SkillBook_0` | 技能书 | Farming +250 XP | 同首次 | 书商随机、旅行货车随机等 |
| `SkillBook_1` | 技能书 | Fishing +250 XP | 同首次 | 书商随机、旅行货车随机等 |
| `SkillBook_2` | 技能书 | Foraging +250 XP | 同首次 | 书商随机、沙漠节蛋商店等 |
| `SkillBook_3` | 技能书 | Mining +250 XP | 同首次 | 书商随机、旅行货车随机等 |
| `SkillBook_4` | 技能书 | Combat +250 XP | 同首次 | 书商随机、旅行货车随机等 |
| `PurpleBook` | 星之书 | 五技能各 +250 XP | 同首次 | 书商 25%、谜之盒池等 |

### 能力书

| ID | 首次阅读 stat | 重复阅读 XP | Well Read | 主要来源 |
| --- | --- | --- | --- | --- |
| `Book_Trash` | `Book_Trash` | 全技能各 20 XP | 是 | 城镇一次性礼盒 / 相关掉落 |
| `Book_Crabbing` | `Book_Crabbing` | Fishing +100 XP | 是 | SquidFest 奖励 |
| `Book_Bombs` | `Book_Bombs` | Combat +100 XP | 是 | Dwarf 商店 |
| `Book_Roe` | `Book_Roe` | Fishing +100 XP | 是 | 钓鱼宝箱 |
| `Book_WildSeeds` | `Book_WildSeeds` | Foraging +100 XP | 是 | Raccoon 奖励 |
| `Book_Woodcutting` | `Book_Woodcutting` | Foraging +100 XP | 是 | 砍树掉落 |
| `Book_Defense` | `Book_Defense` | Combat +100 XP | 是 | 古物点/相关奖励 |
| `Book_Friendship` | `Book_Friendship` | 全技能各 20 XP | 是 | Prize Ticket |
| `Book_Void` | `Book_Void` | Combat +100 XP | 是 | 怪物掉落 |
| `Book_Speed` | `Book_Speed` | 全技能各 20 XP | 是 | Bookseller |
| `Book_Marlon` | `Book_Marlon` | Combat +100 XP | 是 | Adventure Guild 礼盒 |
| `Book_PriceCatalogue` | `Book_PriceCatalogue` | 无重复 XP | 是 | Bookseller |
| `Book_Diamonds` | `Book_Diamonds` | Mining +100 XP | 是 | VolcanoShop |
| `Book_Mystery` | `Book_Mystery` | 全技能各 20 XP | 是 | Mystery Box / Bookseller |
| `Book_AnimalCatalogue` | `Book_AnimalCatalogue` | 无重复 XP | 是 | AnimalShop Year 2+ |
| `Book_Speed2` | `Book_Speed2` | 全技能各 20 XP | 是 | Bookseller Year 3+ 随机 |
| `Book_Artifact` | `Book_Artifact` | 全技能各 20 XP | 是 | Bookseller Year 3+ 随机 |
| `Book_Horse` | `Book_Horse` | 全技能各 20 XP | 是 | Bookseller |
| `Book_Grass` | `Book_Grass` | Foraging +100 XP | 是 | Bookseller |
| `Book_QueenOfSauce` | `Book_QueenOfSauce` | 特殊：补菜谱 | 否 | Bookseller 100 walnuts 后 |

说明：上表的重复 XP 以 `Objects.json` 的 `ContextTags` 为准；实现时不得手写猜测，`BookDefinition` 初始化应从锁定表生成或逐项对照。

---

## 五、数据设计

### 玩家数据

在 `player/PlayerStardewData.java` 增加 SDV stats 风格存储：

```text
Map<String, Integer> stats
```

第一批使用 key：

```text
Book_Trash, Book_Crabbing, Book_Bombs, Book_Roe, Book_WildSeeds,
Book_Woodcutting, Book_Defense, Book_Friendship, Book_Void,
Book_Speed, Book_Marlon, Book_PriceCatalogue, Book_Diamonds,
Book_Mystery, Book_AnimalCatalogue, Book_Speed2, Book_Artifact,
Book_Horse, Book_Grass, Book_QueenOfSauce
```

设计原则：

- 不使用 `specialItems`，因为 SDV 书籍是 `stats.Get("Book_*")`，不是 special item。
- 不使用 mailbox 承载读书状态，避免未知 mail id 卡邮箱。
- NBT 保存为 compound：`Stats: { Book_Trash: 1, ... }`，缺省视为 0。
- API 统一走 `PlayerStardewDataAPI.getStat/setStat/incrementStat/hasBookPower`。

### 定义表

新增 `book/BookDefinition.java`：

```text
id: String                    // SDV item id, e.g. Book_Trash
registryName: String          // stardewcraft:book_trash
category: BookCategory        // SKILL, POWER, SPECIAL
skillBookTarget: SkillType?   // SkillBook_0..4
statKey: String?              // Book_* for power/special
repeatXpSkill: SkillType?     // from book_xp_* tag
sellPrice: int                // Objects.json Price
spriteIndex: int              // Objects.json SpriteIndex
wellReadRequired: boolean
repeatXpAllowed: boolean      // false for PriceCatalogue / AnimalCatalogue
```

新增 `book/StardewBookItem.java`：

- 右键入口。
- 服务端判定并消耗 1 本。
- 调用 `BookService.readBook(player, definition)`。
- 客户端 tooltip 显示原版描述/已读重复阅读说明。

新增 `book/BookService.java`：

- `readSkillBook`。
- `readPowerBookFirstTime`。
- `readPowerBookRepeated`。
- `readQueenOfSauceBook`。
- `checkWellReadAchievement`。
- 发消息、播放 SDV 音效、触发视觉 payload。

---

## 六、视觉、声音与特效

### 读书视觉

新增客户端效果：

```text
client/book/ReadingBookClientEffect.java
network/payload/ReadBookVisualPayload.java
```

行为：

1. 服务端开始读书时给读书玩家和旁观追踪客户端发翻书 payload；读完成功后再发完成 payload。
2. 客户端在镜头前方或玩家身体前方生成临时书模型。
3. 动画阶段：打开 → 快速翻页 → 停顿 → 合上。
4. 结束后自动移除，不生成实体，不进存档。

注意：

- 可以复用 MC 原版附魔台书的模型/贴图和 page flip 参数。
- 不使用附魔台 GUI，不要求方块实体，不触发 enchantment table sound。
- 读书结果消息和音效由 `BookService` 驱动。

### SDV 声音与消息

实现前必须从 `Object.readBook` 和 strings 文件锁定：

- 首次学会能力的 message key。
- 已读重复阅读经验 message key。
- 技能书经验 message key。
- 酱料女皇学会菜谱数量 message key。
- 原版读书 sound cue。

若对应音频资产尚未在项目资源中存在，不用 MC 声音替代；先到音频 wiki 查 cue/编号，再用 `scripts/convert_music.py` 导出。资源缺口必须单独列入资产任务，完成前该声音相关体验不能标记为 1:1 完成。

### 原版资产导入规则

1. 书籍 item icon：以 `Objects.json` 的 `SpriteIndex` 定位 SDV object sheet，逐本截取为独立 PNG。
2. 读书翻页：使用 MC 附魔台书模型/纹理/动画参数在客户端临时渲染，不导入或播放 `LooseSprites\\Book_Animation`。
3. 结束特效：`Utility.addRainbowStarExplosion(..., 8)` 涉及的 sprite/sound 要继续追源码，锁定原版资源后截取。
4. 声音：先查项目资源；没有则查音频 wiki 的 cue/编号，再用转换脚本导出。
5. 所有导入资产必须在 `BOOK_SYSTEM_SOURCE_LEDGER.md` 记录来源和目标路径。

---

## 七、商店与获取来源

### Bookseller 出没

SDV 来源：`Utility.getDaysOfBooksellerThisSeason()`。

规则：

```text
seed = CreateRandom(year * 11, uniqueIDForThisGame, seasonIndex)
每季候选日：
spring: 11, 12, 21, 22, 25
summer: 9, 12, 18, 25, 27
fall:   4, 7, 8, 9, 12, 19, 22, 25
winter: 5, 11, 12, 19, 22, 24
选 index1，再选 index1 + possible_days.Length / 2 环绕后的第二天
```

接入：

- 新增 `shop/BooksellerSchedule.java`，按世界 seed、年、季计算两天。
- 当天早晨广播 `BooksellerInTown`。
- 日历/告示板后续如已有 UI，再接 Bookseller 标记。
- NPC/点位交互当天打开书商；如果玩家有 `read_a_book` mail flag，显示 Buy/Trade 两项，否则直接 Buy。

### ShopRegistry 扩展

当前 `ShopRegistry` 已有 `TRAVELING_CART_SKILL_BOOKS`，但书籍物品尚未注册。书籍接入后需要：

- 注册 `Bookseller`。
- 注册 `BooksellerTrade`。
- 为 `ShopItemEntry` 增加 SDV 条件表达能力，至少支持：
  - year/min year。
  - player stat current value。
  - mail flag。
  - available stock limit。
  - random item group / synced random。
  - trade item cost。
- `ShopPurchasePayload` 支持书商兑换，不破坏现有普通商店购买。

### 非商店来源

必须逐项接入：

| 来源 | 书籍 | SDV 来源 |
| --- | --- | --- |
| Town 一次性礼盒 | `Book_Trash` | `Town.cs` |
| Adventure Guild 礼盒 | `Book_Marlon` | `AdventureGuild.cs` |
| Raccoon 奖励 | `Book_WildSeeds` / `PurpleBook` | `Raccoon.cs` |
| Prize Ticket | `Book_Friendship` | `PrizeTicketMenu.cs` |
| 砍树 | `Book_Woodcutting` | `Tree.cs` |
| 钓鱼宝箱 | `Book_Roe` | `FishingRod.cs` |
| 怪物掉落 | `Book_Void` | `GameLocation.cs` |
| 古物点 | `Book_Defense` | `Object.cs` |
| Mystery Box | `Book_Mystery` / `PurpleBook` | `Utility.cs` |
| VolcanoShop | `Book_Diamonds` | `Shops.json` |
| Dwarf | `Book_Bombs` | `Shops.json` |
| AnimalShop | `Book_AnimalCatalogue` | `Shops.json` |

---

## 八、实施分阶段

### 阶段 0 — 源码锁定表

**目标**：实现前冻结全部数据，避免边做边猜。

1. 生成 `BOOK_SYSTEM_SOURCE_LEDGER.md` 锁定表。
2. 对 26 本书记录：`Objects.json` id、display、description、category、price、sprite、tags。
3. 对商店记录：shop id、price、condition、stock、trade item。
4. 对每个效果记录：源码文件、方法、行号、数值、触发时点。

**验收**：任意书籍都能从表中追到 SDV 源数据；无“待确认”数值。

### 阶段 1 — 数据层与物品注册

**目标**：所有书都成为真实 MC 物品，并能持久化阅读状态。

1. `PlayerStardewData` 增加 stats map + NBT 读写 + copy/sync。
2. `PlayerStardewDataAPI` 增加 stats/book helper。
3. 新增 `BookDefinition` / `BookService` / `StardewBookItem`。
4. `ModItems` 注册 26 本书。
5. 添加 lang、item model、texture。
6. 创造模式/调试获取入口按项目现有规则接入。

**验收**：给玩家任意书，重进服务器后物品存在、stats 保存正常。

### 阶段 2 — 读书闭环

**目标**：右键读书 1:1 复刻 `Object.readBook`。

1. 技能书 XP。
2. 星之书五技能 XP。
3. 能力书首次阅读 stat + message + `read_a_book`。
4. 能力书重复阅读 XP。
5. `Book_PriceCatalogue` / `Book_AnimalCatalogue` 重复阅读不转 XP。
6. `Book_QueenOfSauce` 补齐菜谱。
7. Well Read 检查。
8. 客户端附魔台书式读书视觉。✅ 已接入 `ReadBookVisualPayload` + `ReadingBookClientEffect`，读书时在玩家面前打开、快速翻页、合上。
9. SDV 声音/消息/特效。✅ `book_read`、约 1 秒读书冻结段、`MusicDuckTimer=4000f` 对应音乐压低、读书消息、SDV 彩虹爆星帧渲染已接入；矮人语手册复用同一套阅读体验；精确 QoS 食谱表仍在后续细化项。

读书表现差距记录：MC 没有 SDV `FarmerSprite` frame 57 的同构角色帧系统，当前以玩家冻结 + 面前附魔台书模型翻页替代；原版 `LooseSprites\\Book_Animation` 不播放是刻意取舍，因为本项目读书翻页载体按需求改为 MC 附魔台书模型。

读后效果审计：✅ 5 本技能书、`PurpleBook`、17 本永久能力书、`Book_QueenOfSauce`、不可消耗 `Book_AnimalCatalogue` 均有读完结算；17 本永久能力均已接入实际玩法入口。`Book_AnimalCatalogue` 阅读后直接打开 Marnie 动物购买界面；`Book_PriceCatalogue` 已按项目规则移除。速度、骑马速度、草/作物/杂草减速属于 MC 属性/移动系统的百分比映射，数值锚点仍按 SDV 效果表维护。

**验收**：26 本书全部能读；经验、stat、消耗、重复阅读规则与 SDV 对照表一致。

### 阶段 3 — 永久能力效果

**目标**：19 本能力书全部有实际功能。

1. 移速：`Book_Speed`、`Book_Speed2`。
2. 骑马：`Book_Horse`。
3. 战斗：`Book_Defense`、`Book_Bombs`、`Book_Void`。
4. 采集/采矿：`Book_Woodcutting`、`Book_WildSeeds`、`Book_Diamonds`、`Book_Grass`。
5. 钓鱼/蟹笼：`Book_Roe`、`Book_Crabbing`。
6. 经济/tooltip：`Book_Marlon`、`Book_PriceCatalogue`、`Book_Artifact`。
7. 社交/动物：`Book_Friendship`、`Book_AnimalCatalogue`。
8. 杂项概率：`Book_Trash`、`Book_Mystery`。

**验收**：每本书有对应测试或手测命令；没有“只记录 stat 不生效”的书。

当前接入进度：

- ✅ `Book_Speed` / `Book_Speed2`：非骑马时每本 +0.25 / 基础 5 SDV speed，按百分比换算为每本 +5% MC movement speed。
- ✅ `Book_Horse`：骑乘 MC horse 系实体时 +0.5 / 原版骑马速度 9.6 SDV speed，按百分比换算为约 +5.208% horse movement speed。
- ✅ `Book_Defense`：玩家防御结算 +1。
- ✅ `Book_Bombs`：爆炸伤害进入 SDV 血量结算前 ×0.75。
- ✅ 炸弹伤害判定：伤害数值保留 SDV `radius*3` / `radius*6~8`，命中范围采用 StardewCraft 的 MC 3D 适配层：水平圆柱跟随可见爆炸半径，垂直范围随炸弹等级增长但不按方盒穿层。
- ✅ `Book_Trash`：垃圾桶基础掉落概率 +0.2。
- ✅ `Book_Artifact`：古物与品质古物售卖基础价 ×3。
- ✅ `Book_Marlon`：马龙死亡物品找回价格 ×0.5。
- ✅ `Book_WildSeeds`：割除杂草时 Mixed Seeds 概率 5% → 9%。
- ✅ `Book_Woodcutting`：砍树/树桩木材掉落 5% 概率双倍。
- ✅ `Book_Diamonds`：手动镐普通矿井石头 0.66% 概率额外掉钻石。
- ✅ `Book_Crabbing`：蟹笼收获时 25% 概率复制产物。
- ✅ `Book_Roe`：钓鱼宝箱 25% 概率额外加入对应鱼籽。
- ✅ `Book_Friendship`：所有已接入的正向友谊增长按原版 `(int)(amount * 1.1F)` 调整。
- ✅ `Book_Void`：矿井怪物掉落池非空时 3% 概率复制已有掉落。
- ✅ `Book_Mystery`：钓鱼宝箱、淘盘、矿桶的 Mystery Box 概率走原版 0.66/0.88 系数。
- ✅ `Book_AnimalCatalogue`：项目规则改为不可消耗目录书，阅读后直接打开 Marnie 动物购买界面。
- ✅ `Book_Grass`：草、作物、杂草移动减速按 SDV `temporarySpeedBuff=-1/-0.33` 映射为 80% / 93.4% MC 水平移动速度。
- ❌ `Book_PriceCatalogue`：项目规则已移除，不再注册或显示卖价 tooltip。

### 阶段 4 — 获取来源

**目标**：书籍不是只靠命令获得，而是进入原版来源链。

当前缺口优先级：

1. **书摊日历与出没**：原版每季按 `Utility.getDaysOfBooksellerThisSeason()` 从候选日中确定两天；StardewCraft 使用世界 seed 作为 `uniqueIDForThisGame` 等价输入，按同一候选日/半长环绕规则确定日期；非书摊日不生成/交互书摊老板。已接入 `BooksellerSchedule`、`BooksellerEvents` 和早晨 `BooksellerInTown` 等价提示；日历 Billboard 图标与场景灯光仍待补。
2. **书摊库存与限购**：原版 Bookseller 主商店多数为 `Player 1` 当日限购，BooksellerTrade 为 `Global -1` 无限兑换。StardewCraft 主商店条目已改为每日限购 1，兑换保留无限；`Book_QueenOfSauce` 按拍板临时改为 Year 3 后书摊 50000g 无限出售。TODO：补 Golden Walnuts 世界状态后改回 `GoldenWalnutsFound >= 100`；`Book_PriceCatalogue` 按项目规则不接入。
3. **非商店获取途径**：已接入现有系统能承载的砍树 `Book_Woodcutting`、钓鱼宝箱 `Book_Roe`、怪物 `Book_Void`、远古斑点 `Book_Defense`、Mystery Box `Book_Mystery` / `PurpleBook`。城镇礼盒、冒险家公会礼盒、浣熊奖励、兑奖券、SquidFest、VolcanoShop、DesertFestival_EggShop 本阶段均不做。
4. **Powers/成就展示**：Powers 页可后续直接接入书本 stat；Well Read 暂不做正式 achievement/advancement，保留现有读齐提示。
5. **严格随机语义校准**：后续统一把普通 `RandomSource` 接入 SDV `CreateRandom` / `CreateDaySaveRandom` 对齐表，尤其是书摊 synced random 和每日掉落随机。

1. Bookseller 日期、广播、交互、Buy/Trade。
2. AnimalShop/Dwarf/VolcanoShop/Traveler/DesertFestival/Raccoon。
3. 礼盒、掉落、宝箱、Prize Ticket。
4. 购买限制、读过条件、年份条件、随机库存。

**验收**：按 SDV 来源逐项模拟，能拿到对应书；商店条件不提前、不遗漏。

### 阶段 5 — Powers UI / tooltip / 成就收尾

**目标**：玩家能看见自己已学会哪些能力，UI 表现与原版含义一致。

1. Powers 页面读取 `Powers.json`。
2. 已读能力在 V 菜单或现有 Power 入口显示。
3. tooltip 根据 `Book_PriceCatalogue` 显示卖价。
4. Well Read 成就/进度提示。
5. 已读书描述动态更新。

**验收**：读完 19 本触发 Well Read；读过书在 UI 中可查；卖价 tooltip 生效。

---

## 九、验证标准

### 编译

- 每个阶段至少运行 `./gradlew classes --stacktrace`。

### 数据验证

- 用脚本比较 `BookDefinition` 与 `Objects.json`：id、category、price、sprite、tags 不得漂移。
- 用脚本比较商店书籍条目与 `Shops.json`：价格、条件、兑换项不得漂移。

### 行为验证

- 技能书：读前后对应技能 XP 精确 +250。
- 星之书：五技能各 +250。
- 首读能力书：stat 从 0 到 1；重复读不再次触发新能力。
- 重复阅读：tag 指定技能 +100；无 tag 五技能各 +20。
- `Book_PriceCatalogue` / `Book_AnimalCatalogue`：重复读无 XP。
- Well Read：只在 19 本永久能力书全读后触发。
- 多人：A 读书不会给 B stat；书商日期全服一致。

### 效果验证

每本能力书必须有一个独立验收项：

- `Book_Defense`：同一伤害源下最终扣血少 1 点防御对应值。
- `Book_Bombs`：读过后炸弹对玩家伤害为原伤害 75%。
- `Book_Void`：怪物掉落池在 3% 判定时复制已有掉落。
- `Book_PriceCatalogue`：项目规则已移除。
- `Book_Artifact`：古物售价三倍，普通物品不变。
- 其余按第三节源码锚点逐项测。

---

## 十、风险与未决资产

1. **SDV 音频资产**
   - 如果项目中没有合法可用的读书 sound cue，不能临时换 MC 附魔声音。
   - 处理方式：资源缺口单列，逻辑先留 hook。
2. **书籍 item texture**
   - 当前项目只有书堆/书架贴图，没有 26 本独立书物品贴图。
   - 处理方式：优先从 SDV object sprite 表按 `SpriteIndex` 切出合法资源；若资源尚未导入，先由用户确认占位策略。
3. **尚未完整存在的系统**
   - 蟹笼、马/坐骑、动物商店、Powers UI 若缺基础系统，不允许把对应书标为完成。
   - 处理方式：每个缺口作为该书接入的一部分补齐最小承载层。
4. **随机数语义**
   - SDV 大量使用 `CreateDaySaveRandom` / synced random。
   - 处理方式：新增 `StardewRandom` helper，按源码 seed 参数逐个复刻，不用普通 `level.random` 替代。

---

## 十一、建议开工顺序

1. 先做阶段 0，生成完整锁定表。
2. 做阶段 1-2，让 26 本书全部能注册、能读、能存档、能重复阅读。
3. 做阶段 3，把 19 本永久能力逐本接到真实系统。
4. 做阶段 4，把书商和来源补齐。
5. 做阶段 5，补 UI、Powers 和 Well Read 体验。

这样做的原因很简单：读书闭环是所有后续效果的地基；只要 `stats`、`BookDefinition` 和 `BookService` 稳了，后面的每一本书都是按源码挂钩子，而不是重新发明一套系统。