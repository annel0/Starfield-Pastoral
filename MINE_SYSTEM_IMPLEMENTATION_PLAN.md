# 矿井系统：完全复刻 Stardew Valley（主矿井 1–120）开发计划

> 目标：在 Minecraft（NeoForge 1.21.1）里 **1:1 复刻**《星露谷物语》主矿井（1–120 层）的“地图不可破坏 + 表面石头/矿点铺设 + 怪物直接生成 + 砸石头/杀怪出梯子 + 每 5 层电梯”体验。
>
> 重要约束：**不参考 StardewCore 数据包**，仅以原版源码行为为准。

## 已确认的复刻决策（来自你）

- 模板制作：先在 MC 里搭好每层，然后 **导出为结构文件**；每层对应一个结构资源。
- 破坏规则：矿井维度内 **直接禁止破坏**（不做回滚）。
- 怪物策略：先用 MC 原版怪物做 **占位**，接口与刷怪表保持一致，后续再换自定义模型/贴图。

---

## 0. 复刻原则（必须遵守）

- **模板驱动**：每层使用固定的“地图模板”，地图主体不可破坏；随机性来自模板上“铺设的节点/怪物/掉落”。
- **点位驱动**：所有石头/矿点/桶/怪物都只能生成在模板的“可放置点位”上（SV 里是 `Back` 层 `Type=Stone` 的格子）。
- **推进可控**：下楼梯子（173）主要由“砸石头/杀怪事件”按概率触发；必须清怪层在清怪后强制生成梯子。
- **电梯节奏一致**：每 5 层为电梯层（与宝箱层/休息层类似的节奏），UI/交互必须与“到达即解锁”一致。

---

## 0.5 资源需求清单（你需要准备的模型/贴图/方块）

你提出的思路“地图靠新增方块还原其地图材质 + 基本每个东西都有单独模型”是可行的。为了后续结构导出与 1:1 复刻行为，我们把资源拆成两类：

### A) 结构用方块（决定地图外观）

这些方块会被你直接放进每层结构里（结构文件保存的就是方块）。

- **地面/墙体/边角**：至少按 3 个矿区各一套（0/40/80），并覆盖：地面、墙、墙内侧、拐角、边缘收口、裂纹/破损变体。
- **特殊地面/标识**：例如暗层/危险矿井（可先占位），以及少量“装饰贴片”类（比如地面污渍/碎石，能让房间不那么平）。
- **平台与装饰物块**：桶平台、矿车轨道/矿车位、木栅栏/障碍、灯/火把（如果你要贴近 SV 观感）。

实现建议（不强制）：
- 方块 ID 不一定要爆炸式增长：可以用“同一个方块 + 多个 blockstate/model 变体”来承载贴图差异；但如果你更想直观管理，也可以拆成多个方块 ID。

### B) 运行时会被“铺设/替换”的交互节点（决定玩法）

这些通常不直接做进结构里（结构里只放标记点位），运行时按 SV 规则铺设：

- **点位标记方块（最关键）**：
  - `stone_spawn_marker`：表示 SV 的 `Back.Type=Stone` 点位（可放石头/矿点/宝石石头/怪物/物品）。
  - `entry_marker`：入口点位（梯子上来位置）。
  - `elevator_marker`：电梯点位（每 5 层的那个固定点）。
  - （可选）`diggable_marker`：等价 SV `Back` 上的 `Diggable` 属性点位（用于 Duggy 等刷怪分支）。

- **可砸节点方块（至少这些）**：
  - 普通石头节点（建议按矿区至少 3 套外观：上层/冰层/熔岩层）。
  - 铜/铁/金矿点节点（SV 的“矿点是地表节点”，不是墙里矿石）。
  - 宝石富集石节点（可以“一个方块 + blockstate 表示宝石种类”，避免太多方块 ID）。

- **推进交互**：
  - 下楼梯子（运行时生成/移除）。
  - 电梯（固定存在但仅在电梯层启用交互）。

### C) 物品与实体资源（可先占位，后续补齐）

- **掉落物品贴图**：你说“所有的物品贴图都可以直接给我”，那我们第一阶段就按你的贴图直接上，不需要用原版物品占位。
- **掉落物品模型**：你说“所有的模型得缓一缓”，因此第一阶段建议先用“默认 generated item model / 简单方块模型”顶住；等逻辑稳定后再逐个替换精细模型。
- **怪物模型/贴图**：仍按既定策略——先用 MC 原版怪物占位，接口与刷怪表对齐后再替换自定义怪。

### D) 结构制作时必须记住的“固定点位”

- 每层必须放：入口点位、（若该层是电梯层）电梯点位、足够密度的 `stone_spawn_marker`。
- 宝箱层（主矿井 1–120）：10/20/40/50/60/70/80/90/100/110/120。
  - 原版里宝箱放在一个固定坐标附近，并且 20/60/100 这类“每 20 层但不是 40 的倍数”的宝箱点会有 **Y 偏移 +4**（细节见 `addLevelChests()`）；这意味着你做结构时要么统一在标记点上生成宝箱，要么保证这几层的宝箱标记点坐标正确。

---

## 0.6 你现在应该做什么（超详细、按顺序、照着做就能开工）

你现在“不知道该干嘛”的核心原因是：矿井的“外观块（结构）”和“玩法节点（铺设/掉落/刷怪）”是两条线。我们先把它拆成 4 个你能直接执行的任务。

### 任务 1：先搞定“矿井方块调色板”（你今天就能开工）

目的：让你在 MC 里能搭出和原版一致的地面/墙体/边角/装饰。你不需要先做 3D 模型——地图方块都可以先用普通方块 cube 模型。

你要做的 MC 资源（第一批，最低要求）：
- 上层矿井（1–39）基础外观方块：地面、墙体、边角、边缘收口（至少 4–8 个方块就能把大多数房间搭出来）。
- 冰层矿井（40–79）基础外观方块：同上。
- 熔岩矿井（80–119）基础外观方块：同上。
- 木质/支撑/栅栏/平台（可先少量）：用于模板里出现的木板地、支撑物、障碍。

对应原版资源位置见 0.7（mine.png / mine_frost.png / mine_lava.png 等）。

### 任务 2：在结构里放“marker 点位方块”（这是玩法能 1:1 的关键）

目的：你搭结构时不用去读 TMX 的 Type 属性推点位，直接在 MC 里用 marker 明确标注。

你要做的 marker 方块（第一批，最低要求）：
- `stone_spawn_marker`：标注可铺设点位（石头/矿点/宝石石头/刷怪/随机物品都从这里出）。
- `entry_marker`：入口点位（楼梯上来落点）。
- `elevator_marker`：电梯点位（每 5 层用）。
- `chest_marker`：宝箱点位（10/20/40/.../120）。
- （可选）`diggable_marker`：如果你想严格复刻 Duggy 等条件刷怪，再加这个。

这些 marker 的贴图可以先用高对比“调试贴图”（红/绿/蓝/黄）以便你肉眼检查。

### 任务 3：开始做结构（按“模板编号”做，不按层号做）

目的：你现在手里 TMX 是“模板编号”，而主矿井会把多个层号映射到同一个模板编号。

建议你先做这几张模板（优先级从高到低）：
- `10.tmx`（会被 10/30/40/50/70/80/90/110 等多层复用）
- `20.tmx`（会被 20/60/100 复用）
- `120.tmx`（第 120 层结尾）
- `1.tmx`（最早的普通层之一，用来校验风格）

做结构时的硬规则：
- 房间主体外观照着 TMX 的 Back/Buildings/Front 搭出来。
- 把“可铺设点位”全部用 `stone_spawn_marker` 标出来（密度/分布照着原版 Stone 区域走）。
- 把入口/电梯/宝箱等固定点位也用 marker 标出来。

### 任务 4：物品贴图你可以直接给我（模型可以先不做）

你说“物品贴图都可以直接给我”，那你这边的交付形式我建议是：
- 给一份“物品名 → png 文件”的列表（你自定义命名即可）；我在工程里按命名接入。
- 模型可以延后：第一阶段我会用默认物品模型顶住。

---

## 0.7 原版资源定位清单（对应你本机素材目录，找不到就来问我）

下面这个清单的目标是：你任何时候都能回答“我在原版里要对照哪个文件？”

### A) 矿井地图模板（TMX）

目录：c:\Users\jk\Desktop\星露谷物语素材\Maps\Mines\

- 主矿井模板：1.tmx、2.tmx、3.tmx、4.tmx、5.tmx、6.tmx、7.tmx、8.tmx、9.tmx
- 常用模板：10.tmx、11.tmx、12.tmx、13.tmx、14.tmx、15.tmx、16.tmx、17.tmx、18.tmx、19.tmx、20.tmx
- 其它模板：21.tmx ... 29.tmx、31.tmx ... 39.tmx、40.tmx ... 60.tmx
- 结尾模板：120.tmx
- 采石场矿井：77377.tmx

### B) 矿井 tilesheet（TMX 里引用的贴图源）

同目录：c:\Users\jk\Desktop\星露谷物语素材\Maps\Mines\

主矿井常用（优先做这三套外观就能开工）：
- mine.png（上层矿井基础）
- mine_frost.png（冰层）
- mine_lava.png（熔岩层）

暗层/危险矿井相关（后续再做也行）：
- mine_dark.png / mine_dark_dangerous.png
- mine_frost_dark.png / mine_frost_dark_dangerous.png
- mine_lava_dark.png / mine_lava_dark_dangerous.png
- mine_desert.png / mine_desert_dangerous.png / mine_desert_dark.png / mine_desert_dark_dangerous.png
- mine_dangerous.png

特殊主题（暂时不在“主矿井 1–120”第一期必做范围）：
- mine_quarryshaft.png（采石场矿井）
- mine_slime.png / mine_slime_dangerous.png（史莱姆主题）
- mine_dino.png（恐龙主题）
- volcano_dungeon.png / volcano_caldera.png（火山相关）

### C) 物品图标（给你参考，但你会用自定义贴图）

你本机素材里有：c:\Users\jk\Desktop\星露谷物语素材\Maps\springobjects.png（以及多语言变体）。

备注：第一期我们不强制用它，因为你会提供自己的物品贴图；但这个文件可以用来核对“原版有哪些矿物/晶洞/宝石会掉落”。

原版依据（你的本地源码）：
- 地图加载/层生成主流程：`MineShaft.generateContents()` → `loadLevel()` → `chooseLevelType()` → `findLadder()` → `populateLevel()`（见 `c:/Users/jk/Desktop/源代码/StardewValley.Locations/MineShaft.cs`）。
- 可放置点位判定：`isTileClearForMineObjects` 要求 `Back.Type == Stone` 且不被占用等。
- 节点铺设：`populateLevel()` 扫描所有可放置格，按概率决定放石头/怪物/物品。

---

## 1. 原版行为拆解（我们要照抄的“规则表”）

### 1.1 地图模板选择（主矿井 1–120）

结论：**主矿井 1–120 的地图模板几乎是“按层号确定”，不是随机抽卡。**

- `loadLevel(int level)`（源码）在主矿井的模板编号规则可以写成一个确定函数：
  - 若 `level == 120`：`map = 120`
  - 否则若 `level % 40 == 20`（即 20/60/100）：`map = 20`
  - 否则若 `level % 10 == 0`（10/30/40/50/70/80/90/110）：`map = 10`
  - 否则：`map = level % 40`（范围 1–39）

这就是“固定地图池”的来源：模板资源名形如 `Maps\\Mines\\<map>`。

补充（地图长啥样/在哪里）：你已经把原版矿井地图导出了 `.tmx`（Tiled 格式），这批文件本身就是“原版矿井模板”的权威外观与标记来源（例如 `Maps/Mines/1.tmx`、`10.tmx`、`20.tmx`、`40.tmx`、`120.tmx`、`77377.tmx` 等）。

重要澄清：模板文件名是“地图编号”，不是“层号”。因此没有 `80.tmx` 是正常的——当层号是 80 时，按 `level % 10 == 0` 规则会加载 `10` 号模板。

如何预览与用它指导你搭结构（建议流程）：
- 用 Tiled 打开对应 `.tmx`，重点看 3 个层：`Back` / `Buildings` / `Front`（矿井模板基本都具备这三层）。
- 你在 MC 里搭结构时，把 `Back` 当作“主要地面/可站立区域”的参考，把 `Buildings/Front` 当作“遮挡/装饰/不可穿越”的参考。
- 如果你需要严格复刻“哪些格子能铺石头/矿点/刷怪”，不要靠肉眼猜：按 `Back` 层 tile 的 tileset 属性 `Type=Stone` 来确定候选点位，并排除有 `Buildings/Front` 覆盖的格子（这与原版 `isTileClearForMineObjects` 条件一致）。

重要细节（通关后变体布局）：当玩家已到达 120（`lowestLevelReached >= 120`）后，主矿井的非 5 倍数层存在 **6% 概率**切换到一个“40–60 区间的替代模板”（`map = alt_random.Next(40, 61)`），并且某些模板会把本层标记为 `preventMonsterLevel=true`（避免刷成清怪层/史莱姆层）。这是原版的“后期布局变体”，完全复刻时要保留。

我们在 MC 的实现约束：
- 对 1–120：**层号 → 模板 ID** 的映射必须固定、可复现（同一存档同一天/多次进入一致）。

我们在 MC 的资源组织建议（按你的“建好再导出”流程）：
- 每层结构导出为 `data/<modid>/structures/mines/<level>.nbt`（或等价命名）。
- 运行时：按上面的 `map` 规则选择要放置的结构。

### 1.2 生成主循环：铺石头/矿点/怪物/物品

`populateLevel()` 基础概率（每次进入本层先 roll 一组）：
- `stoneChance = Next(10,30)/100`（0.10–0.29）
- `monsterChance = 0.002 + Next(200)/10000`（0.002–0.0219）
- `itemChance = 0.0025`
- `gemStoneChance = 0.003`

然后进入 `adjustLevelChances()` 修正：
- 第 1 层：怪物/物品/宝石概率为 0（新手保护）。
- 每 5 层（非骷髅洞）：`itemChance=0`，`gemStoneChance=0`；每 10 层还会 `monsterChance=0`。
- 必须清怪层（`mustKillAllMonstersToAdvance()`）：
  - `monsterChance = 0.025`
  - `itemChance = 0.001`
  - `stoneChance = 0`
  - `gemStoneChance = 0`

铺设方式：对每一个可放置格（Stone 点位）
- 命中 `stoneChance`：调用 `createLitterObject(...)` 生成一个“可砸石头/矿点对象”。
- 否则命中 `monsterChance` 且离入口足够远：调用 `getMonsterForThisLevel(...)` 生成怪物。
- 否则命中 `itemChance`：生成随机物品（`getRandomItemForThisLevel`）。

### 1.3 “地表石头/矿点”类型：createLitterObject

`createLitterObject(...)` 决定在一个 Stone 点位上生成：
- 普通可砸石头（不同矿区/暗层/危险矿井有不同视觉 ID 和耐久）
- 矿点（主矿井中约 **2.9%** 概率生成）：
  - 区域 0：铜矿点（SV object id `751`）
  - 区域 40：铁矿点（`290`）
  - 区域 80：金矿点（`764`）
- 宝石富集石（受 `gemStoneChance`、幸运、层数影响）：命中则返回 `getRandomGemRichStoneForThisLevel(level)` 对应的宝石石头。

我们在 MC 的实现约束：
- “矿点是地表节点（可砸），不是墙体矿石方块”。
- 每个节点的耐久/掉落/音效/粒子要能与 SV 匹配。

### 1.4 推进：梯子生成（最关键）

#### A) 砸石头出梯子（核心公式）
在 `checkStoneForItems(stoneId,x,y,who)`：
- 每砸碎一个石头：`stonesLeftOnThisLevel--`
- 计算下楼梯子概率：

$$p = 0.02 + \frac{1}{\max(1,\text{stonesLeft})} + \frac{\text{LuckLevel}}{100} + \frac{\text{DailyLuck}}{5}$$

- 若 `EnemyCount == 0`，额外 `+ 0.04`
- 若有 buff `dwarfStatue_1`，概率乘 `1.25`
- 若本层未生成过梯子且非清怪层：满足条件则在砸碎位置创建梯子。

#### B) 杀怪出梯子
在 `monsterDrop(...)`：
- 如果满足（非清怪层或清怪接近结束）并且随机命中，则在怪物死亡位置生成梯子。

#### C) 清怪层强制梯子
- `mustKillAllMonstersToAdvance()` 为真时：当 `EnemyCount==0`，在入口附近生成梯子并提示。

我们在 MC 的实现约束：
- 梯子生成必须是“事件驱动”（砸节点/杀怪）而不是“探路/挖洞”。
- 必须保留 `stonesLeftOnThisLevel` 的概念，因为它是概率曲线的关键。

### 1.5 掉落：矿石种类随层数变化（普通掉落）

`getOreIdForLevel(mineLevel, r)`：
- `<40`：默认铜；`>=20` 有 10% 概率出铁。
- `<80`：默认铁；`>=60` 有 10% 概率出金；否则 75% 铁 / 25% 铜。
- `<120`：默认金；否则 75% 金；剩余里 75% 铁 / 25% 铜。

（这是“普通石头触发的矿石掉落”来源；矿点节点本身掉落由 `GameLocation.breakStone` 决定。）

### 1.6 刷怪：getMonsterForThisLevel（主矿井部分）

核心结论：**怪物类型由矿区（0/40/80）、层数区间、点位属性（Diggable）、离入口距离等条件分支决定**。

额外机制：清怪层/史莱姆层（必须清怪才能推进）
- `loadLevel()` 在满足模板条件时，有 `4.4%` 概率将本层标记为 `isMonsterArea` 或 `isSlimeArea`（二选一），从而 `mustKillAllMonstersToAdvance()` 返回 true。
- 这些层会触发 `adjustLevelChances()`：`stoneChance=0`，主要刷怪，并在清怪后强制生成梯子。

主矿井刷怪表（按源码条件归纳，方便 1:1 照抄）

- 史莱姆层（`isSlimeArea`）优先：
  - 非危险矿井：20% BigSlime，否则 GreenSlime
  - 危险矿井：
    - <20：GreenSlime
    - <30：BlueSquid
    - <40：RockGolem
    - <50：远离入口（distance>=10）15% Fly，否则 Grub
    - <70：Leaper

- 恐龙层（`isDinoArea`，主矿井一般不出现，骷髅洞/特殊逻辑用）：Bat/Fly/DinoMonster

- 上层矿井（矿区 0/10，对应 1–39）：
  - 全区：25% Bug（且非清怪层时）
  - 1–14：
    - 若点位 `Diggable`：Duggy
    - 否则 15% RockCrab
    - 否则 GreenSlime
  - 15–30：
    - 若点位 `Diggable`：Duggy
    - 否则 15% RockCrab
    - 否则（非危险矿井且 distance>10）5% Fly
    - 否则 45% GreenSlime
    - 否则（非危险矿井）Grub
    - 否则（危险矿井且 distance>9）BlueSquid
    - 否则（危险矿井）1% RockGolem
    - 否则 GreenSlime
  - 31–40：
    - （distance>10）10% Bat
    - 危险矿井下额外（10%）Carbon Ghost
    - 否则 RockGolem

- 冰层矿井（矿区 40，对应 40–79）：
  - 70+：若（75% 或 危险矿井）
    - 75% Skeleton（危险矿井下 Skeleton 可能变体）
    - 否则 Bat(77377)
  - 否则：
    - 30% DustSpirit（带一个 0.8 的参数分支）
    - （distance>10）30% Bat
    - （>50 且本层未刷过 ghostAdded 且 distance>10）30% Ghost（危险矿井下为 Putrid Ghost）
    - 危险矿井额外：1% StickBug RockCrab；>=50 可能 Leaper；否则偏向 Grub/Slime

- 熔岩矿井（矿区 80，对应 80–119）：
  - 暗层（isDarkArea）25% Bat
  - 史莱姆：15%（危险矿井时 5%）GreenSlime(80)
  - 15% MetalHead
  - 25% ShadowBrute
  - 危险矿井额外：25% Shooter("Shadow Sniper")
  - 25% ShadowShaman
  - 25% Lava Crab
  - （mineLevel>=90 且 distance>8 且 tile 满足 back 有/ front 无）20% SquidKid

我们要做的：把主矿井 1–120 的分支逐条抄成一张“区间→怪物池→权重”的表，并在 MC 里按权重抽取。

你已读到的关键片段（示例，不是完整表）：
- 1–15：可能出 Duggy（要求 Diggable 点位）、Rock Crab、Green Slime。
- 15–30：增加 Grub/Fly 等（且 Fly 依赖 distanceFromLadder > 10）。
- 31–40：开始 Bat/RockGolem 等。
- 40–79（冰层）：DustSpirit、Bat、Ghost（一次层只加一个 ghostAdded 之类的限制）等。
- 80–119（熔岩层）：ShadowBrute、ShadowShaman、MetalHead、LavaCrab、SquidKid（对 tile 条件/距离有要求）等。

注：上面是“主矿井复刻”的刷怪核心。骷髅洞（121+）与采石场矿井（77377）有大量额外规则，若你也要完全复刻，我们会另开一个文档/章节分离实现。

---

## 2. MC 侧架构设计（如何把 SV 规则“装进 Minecraft”）

### 2.1 “矿井维度/实例”的选择

为了 1:1 复刻，建议：
- 建一个独立维度 `stardew_mines`（或你的命名），由我们完全控制地形与破坏规则。
- 每个“层”是一个固定大小的房间/迷宫区域（对应一个模板）。

关键：SV 的矿井不是连续世界，而是一层一层的“关卡”。

### 2.2 模板承载方式（地图池）

我们需要一个 MC 侧的“模板载体”，满足：
- 模板不可破坏（或几乎不可破坏）
- 模板内有点位标记（Stone 点位、入口、梯子位、电梯位、特殊平台位等）

可选承载：
- **结构模板（Structure / jigsaw 不用拼）**：每层直接放置一个结构。
- 或者：预生成一张“层地图”到 chunk（更像关卡）。

无论哪种，必须支持：
- 在模板上读取“点位标签”（等价于 SV 的 `Back.Type=Stone`）。

### 2.3 点位标签方案（等价于 Back.Type=Stone）

建议的最小方案（可测试、可视化、可与美术协作）：
- 模板里用一种“标记方块/方块状态”表示 Stone 点位。
- 运行时把标记方块替换成真正地面方块，同时把坐标记录为 `spawnableStoneTiles`。

同理还需要：
- `entryTile`（入口/楼梯上方的那一格）
- `elevatorTile`（电梯下方那一格，如果存在）
- `containerPlatformTiles`（桶平台）
- `ladderCandidateTiles`（可放梯子的位置，通常就是 Stone 点位）

补充（从 TMX 精确导出 Stone 点位的规则）：

你给的 `.tmx` 里，原版的点位判定信息来自两部分：
- `Back` 层的 tile gid（CSV 数字）
- tileset 中 tile 的 properties（比如 `<property name="Type" value="Stone"/>`）

而原版代码的放置条件（`isTileClearForMineObjects` / `isTileOnClearAndSolidGround`）等价约束为：
- Back 层该格子的 `Type == Stone`
- 该格子 **没有** `Front` 与 `Buildings` 的遮挡
- Back 层 tile index 不能是 `77`（在 TMX 的 gid 体系里通常表现为 gid=78 这类“不可站立/不可放置”的地块）

因此你做结构时的最稳方案是：
- 结构里用 `stone_spawn_marker` 直接标注“可铺设点位”，避免从外观块反推属性导致漏点。
- 入口/电梯/（可选）Diggable 点位也同理用 marker 明确标注。

### 2.4 节点系统（石头/矿点/宝石点/桶）

我们需要一套“矿井节点方块”体系来承载 SV 的 Object：
- `stone_node`：普通石头
- `ore_node_copper`：铜矿点（对应 SV 751）
- `ore_node_iron`：铁矿点（290）
- `ore_node_gold`：金矿点（764）
- `ore_node_coal`：煤矿点（BasicCoalNode*，后续拓展）
- `gem_rich_node_*`：宝石石头（8/10/12/14/6/4/2 等，或用一个方块 + blockstate 表示宝石种类）

节点行为：
- 耐久/挖掘时间/工具需求要能配置（贴近 SV 的 `MinutesUntilReady` / “石头血量”概念）。
- 被破坏时触发：
  - 更新本层 `stonesLeft`
  - 按 `checkStoneForItems` 的逻辑 roll：梯子、矿石、煤、晶洞、石头碎片、经验
  - 调用等价 `GameLocation.breakStone` 的“矿点固定掉落”

### 2.5 怪物系统

实现策略：
- 每层维护 `EnemyCount`（或动态统计）。
- `populateLevel` 阶段按 `monsterChance` 在点位上生成怪。
- 怪物类型选择按 `getMonsterForThisLevel` 的规则分支复刻：
  - 主矿井分 0/40/80 三个矿区
  - 再按层区间 + 位置条件（距离入口、是否 Diggable 点位、是否空地）决定怪物
- `mustKillAllMonstersToAdvance` 的特殊层：
  - 禁止梯子随机生成（直到清怪）
  - 清怪后强制生成梯子并提示

### 2.6 梯子与电梯

- 梯子：一个交互方块 `mine_ladder_down`。
  - 右键：传送到下一层（level+1）并触发“生成下一层”。
- 电梯：一个交互方块 `mine_elevator`。
  - 仅出现在 `level % 5 == 0` 且 `level <= 120`。
  - 右键打开“已解锁楼层列表”（5、10、15…、当前最大）并传送。
- 解锁规则：到达某层即解锁该电梯档位（SV 在 `loadLevel` 中 `prepareElevator` 处理）。

---

## 3. 数据与持久化

SV 有 `permanentMineChanges`（按层持久化桶/宝箱/电梯等消耗次数）。我们需要 MC 等价物：

- 保存内容（SavedData / world data）：
  - `unlockedElevatorFloor`（最大解锁层，或已解锁集合）
  - 每层一次性内容：宝箱是否已开、桶平台剩余次数等
  - 本层实例的随机种子（如果需要做到“同一天重复进入一致”）

---

## 4. 分阶段开发计划（工程侧）

> 目标：尽快做到“能玩、节奏对、可迭代对齐概率”，再逐步补齐全部细节。

### Phase 1：最小可玩闭环（1–10 层）
- 模板加载：至少支持 1、2、3、4、5、10 层模板。
- 点位标签系统：能从模板中提取 Stone 点位。
- 节点系统：普通石头节点 + 铜矿点节点。
- 破坏事件：实现 `stonesLeft`、梯子概率公式、生成梯子。
- 基础怪物：Green Slime、Rock Crab、Bug、Duggy（可先用 MC 原版怪占位，但接口要对齐）。
- 电梯：5、10 解锁与菜单。

验收标准：玩家在 1–10 层体验到“砸石头 → 出梯子 → 下楼 → 5 层电梯”的 SV 节奏。

### Phase 2：完整主矿井（1–120）模板与分区
- 补齐模板映射（1–120）
- 完成 0/40/80 三个矿区的地面/光照/装饰差异（可先用统一材质，后续由美术替换）
- 补齐 `createLitterObject` 的节点类型（铁/金矿点、宝石石头、特殊石头等）

### Phase 3：掉落表 1:1 对齐
- 完整复刻：
  - `checkStoneForItems`（晶洞/煤/矿石/石头碎片/经验）
  - `GameLocation.breakStone` 对矿点的固定掉落
  - `getOreIdForLevel` 的层区间概率
- 添加“职业/幸运/buff”对概率的影响（先按源码实现，再调试）。

### Phase 4：刷怪 1:1 对齐
- 把 `getMonsterForThisLevel` 的主矿井分支整理成数据表（区间 + 条件 + 权重），实现抽取。
- 实现：
  - 冰层 Dust Spirit、Ghost
  - 熔岩层 Shadow 系列、Lava Crab、SquidKid
- 实现“must kill all monsters”层的完整规则与提示。

### Phase 5：特殊层与细节
- 宝箱层（每 10 层）的奖励表与一次性开箱逻辑
- 音乐/雾效/暗层（可后置）
- 桶平台、资源团（resource clumps）等额外内容

---

## 5. 美术与测试分工（你负责）

你负责：
- 矿井模板方块材质（墙/地/装饰）
- 节点模型与贴图：
  - 普通石头节点（按矿区三套）
  - 铜/铁/金矿点节点
  - 宝石石头节点（至少 6 种）
  - 梯子方块、电梯方块
- 怪物贴图/模型（若做成自定义怪）
- 测试与反馈：
  - 梯子出现体感是否符合 SV
  - 每 10 层宝箱节奏
  - 40/80 之后矿点与怪物更替是否明显

我负责：
- 模板系统、点位标签、节点方块与交互
- 掉落、概率、职业/幸运/buff 复刻
- 刷怪逻辑与层实例管理
- 电梯 UI、持久化、联机同步

---

## 6. 当前需要你确认的 3 个“复刻决策”（越早越好）

已确认（无需再决定）：
- 模板来源：B（先搭建再导出结构文件）。
- 不可破坏策略：A（直接禁止破坏）。
- 怪物形态：A（先用原版怪占位）。

---

## 7. 下一步（我将继续扒的源码清单）

为做到 100% 复刻，我会继续从源码提炼成“可直接写成配置表”的规则：
- `getMonsterForThisLevel` 主矿井全分支 → 生成“怪物表”
- `getRandomItemForThisLevel`、宝箱层奖励逻辑 → 生成“物品表”
- `loadLevel` 的模板映射细节 → 生成“模板映射表（1–120）`

我将优先把 `loadLevel` 的映射写成一段“可直接抄进代码的纯函数 + 示例表”，并把“通关后 6% 变体布局”的例外规则单独标注，避免实现时漏掉。

