# Luau Implementation Plan

## 0. Scope

目标：在现有主动节日框架中实现星露谷夏季 11 日 Luau / 夏威夷宴会，使玩家可以进入海滩节日、对话、购物、向百乐汤投料、触发镇长开场与州长尝汤主事件，并按原版源码规则给出结果与影响。

非目标：本阶段不重写主动节日框架，不另起节日系统；不凭 wiki 或记忆补机制；不在未经确认的情况下落任何 3D 点位、NPC 站位、交互体积、展示实体位置或镜头锚点。

核心原则：Luau 的机制以原版 `源文件` 为准；项目实现以现有主动节日生命周期为准；所有实际点位都必须是项目世界里的 3D 坐标或 3D 交互体积，原版 TMX tile 坐标只作为源码账本和布局关系参考，不能直接写入运行时代码或 JSON。

## 1. Source Ledger

### 1.1 Original Game Data

- `源文件/Content/Data/Festivals/summer11.json`
  - 节日名：`Luau`。
  - 条件：`Beach/900 1400`。
  - 第 1 年 setup 使用 `changeToTemporaryMap Beach-Luau`。
  - 第 2 年及后续按原版 year-key 规则使用 `set-up_y2` / `mainEvent_y2` 与 `Beach-Luau2`。
  - setup 阶段：`loadActors Set-Up`、生成 Governor、NPC 动作/动画、`playerControl luau`。
  - main event：`loadActors MainEvent`、Governor tasting animation、`cutscene governorTaste`。
  - 结果分支：`governorReaction0` 到 `governorReaction6`。

- `源文件/Content/Maps/Beach-Luau.tmx`
  - 原版奇数年/基础临时地图。
  - 地图属性：`Music=ocean`、`Outdoors=T`、warp 指向 Town。
  - `Set-Up` / `MainEvent` 图层提供 NPC 站位与朝向，执行规则见 `Event.LoadActors`。
  - `Buildings` object layer 上有原版 `Shop Festival_Luau_Pierre` 与 `LuauSoup` tile action。

- `源文件/Content/Maps/Beach-Luau2.tmx`
  - 原版偶数年临时地图。
  - 同样有 `Set-Up` / `MainEvent` 图层、Pierre 商店 action、汤锅 action。
  - 与 `Beach-Luau` 的 NPC 分布、商店位置、汤锅位置不同。

- `源文件/Content/Data/Shops.json`
  - `Festival_Luau_Pierre` 是原版 Pierre Luau 商店。
  - 原版售卖项：
    - `(F)2654` Wall Palm，1000g，无限。
    - `(F)2627` Jungle Decal，800g，无限。
    - `(F)2628` Jungle Decal，800g，无限。
    - `(F)2629` Jungle Decal，800g，无限。
    - `(F)2630` Jungle Decal，800g，无限。
    - `(F)1817` Ceiling Leaves，400g，无限。
    - `(F)1818` Ceiling Leaves，400g，无限。
    - `(F)1819` Ceiling Leaves，400g，无限。
    - `(F)1295` Totem Pole，1000g，无限。
    - `(O)268` Starfruit，3000g，每玩家限 1。
    - `(F)2397` Plain Torch，700g，无限。

- `源文件/Content/Data/Furniture.json`
  - 解析 Luau 商店中 `(F)` 家具 ID 的原版名称、类型和基础价。

- `源文件/Content/Data/Objects.json`
  - `(O)268` 是 `Starfruit`，原版 `Price=750`、`Edibility=50`、`Category=-79`。

- `源文件/Content/Strings/UI.zh-CN.json`
  - `Chat_LuauSoup`：`{0}把“{1}”放进了百乐汤。`

### 1.2 Original Execution Code

- `源文件/StardewValley/Event.cs`
  - `Event.tryToLoadFestivalData` / `setUpFestivalMainEvent`：读取节日数据与 setup。
  - `Event.LoadActors`：从临时地图 `Set-Up` / `MainEvent` 图层读取 actor tile。规则是 `actorIndex = tileIndex / 4`、`facing = tileIndex % 4`，actor index 来自 `Data/Characters` 的 `FestivalVanillaActorIndex`。
  - tile action `festival_summer11` 映射到 `Festival_Luau_Pierre`。
  - tile action `LuauSoup` 打开投料菜单。
  - `addItemToLuauSoup`：把玩家选择的一个物品加入 `who.team.luauIngredients`，本地设置已投料标记，发送聊天信息，短裤特殊处理。
  - `IsItemMayorShorts`：接受 `(O)789` 与 `(O)71`。
  - `governorTaste`：计算汤结果、友好度变化、成就与结果分支。

- `源文件/StardewValley/Utility.cs`
  - `highlightLuauSoupItems(Item i)`：只有 `Object` 可投；如果 `edibility == -300` 或 `Category == -7`，只有镇长短裤可投；否则可投。

- `源文件/StardewValley/FarmerTeam.cs`
  - `luauIngredients` 是 team-synced collection，新一天清空。

## 2. Project Baseline

### 2.1 Existing Active Festival Framework

- `FestivalRegistry` 已有 `summer11` 主动节日定义：夏 11 日、9:00-14:00、location `Beach`、overlay id `Beach-Luau`、event key `luau_soup`。
- `FestivalService` 负责日期检测、debug festival、新一天 overlay、active festival entry、session open/end、overlay restore。
- `FestivalSessionState` / `FestivalSessionPhase` 持久化节日会话阶段。
- `FestivalMapOverlayRegistry` / `FestivalMapOverlayManager` 负责 overlay patch apply/restore。
- `ActiveFestivalHandler` / `ActiveFestivalHandlers` 是主动节日统一入口。
- `NpcInteractionService` 已把 Pierre 节日商店与 Lewis 主事件入口委托给 active festival handler。

### 2.2 Existing Festival Implementations to Reuse

- Egg Festival：可参考进入、主事件确认、比赛/结束、多玩家 session 管理、Pierre 节日商店。
- Flower Dance：可参考主动节日 NPC 对话、主事件确认、音乐状态、重连状态恢复、节日结束。
- Desert Festival：不作为主动节日生命周期模板，但可参考 vendor/shop 组织、节日特殊交互服务的边界拆分。

### 2.3 Existing Luau Data Already Present

- `src/main/resources/data/stardewcraft/festival_dialogue_keys.json` 已含 `summer11` 对话 key。
- `assets/stardewcraft/lang/en_us.json` / `zh_cn.json` 已含大量 Luau 对话文本。
- 当前缺 Luau active handler、overlay registration、汤锅投料、评分、主事件结果、商店注册。

## 3. 3D Point Policy

### 3.1 Rule

项目内所有 Luau 点位都必须用 3D 表达：`BlockPos`、`Vec3`、有半径/包围盒的交互体积、或项目已有坐标数据结构。不得把原版 TMX 的二维 tile 直接写成运行时点位。

### 3.2 Original Tile Data Usage

原版 TMX tile 只用于回答这些问题：

- 哪些功能存在：Pierre 商店、汤锅、入口/出口、NPC setup、NPC main event、Governor、镜头关注点。
- 不同年份布局是否不同：Luau 有 `Beach-Luau` 与 `Beach-Luau2` 两套。
- NPC 相对关系：谁站在汤锅周围、谁在商店旁、谁在海边、谁在舞台/食物区。
- 主事件中哪些 actor 会重新布置。

这些信息只能进入规划账本或待确认清单，不能绕过用户确认变成 3D 坐标。

### 3.3 Required 3D Confirmations Before Implementation

实现前必须由用户确认以下 3D 点位或体积：

- Luau overlay origin、bounds、restore bounds。
- 玩家进入节日后的 3D spawn 点和朝向。
- 玩家退出/节日结束后的返回点与朝向。
- Pierre 商店交互体积：第 1 年布局与第 2 年布局是否分开。
- 汤锅交互体积：第 1 年布局与第 2 年布局是否分开。
- Lewis 主事件触发交互体积。
- Governor setup 站位、main event 站位、尝汤移动目标点。
- NPC setup 站位与朝向。
- NPC main event 站位与朝向。
- 主事件镜头/观察点，如项目已有 cutscene camera anchor，也必须确认。
- 特殊展示实体位置：汤锅、食物桌、商店柜台、临时装饰、短裤投汤分支展示物。

## 4. Original Mechanics to Preserve

### 4.1 Entry and Session

- 日期：夏 11 日。
- 时间窗口：9:00 到 14:00。
- 地点：Beach。
- 进入后冻结节日时间，按主动节日 session 记录参与玩家。
- 同一天已结束的 Luau 不允许重新进入；必须遵守 `FestivalSessionState` 的 terminal phase 规则。

### 4.2 Year Variant

原版使用 year-key 查找规则：若存在 `_y2` 字段，第 2 年及之后使用 year 2 版本。项目建议支持两个 variant：

- `summer11_y1`：使用 Luau base layout。
- `summer11_y2`：使用 Luau alternate layout。

运行时不一定需要暴露这两个 ID，但内部 service 应能根据 `StardewTimeManager` 的年数选择 layout profile。

### 4.3 Dialogue

- 普通 NPC 对话使用 `stardewcraft.festival.summer11.dialogue.<npc>`。
- 第 2 年及之后优先使用 `<npc>_y2`。
- spouse 文本沿用既有 key。
- Governor 需要作为临时 actor 或专用 festival actor 支持对话。

### 4.4 Shop

- Pierre 在 Luau 期间打开 `Festival_Luau_Pierre`。
- 如果商店资源暂缺，不能 silently drop 原版售卖项。规划里应标记为三类：
  - 已有等价物，可直接映射。
  - 已有近似物，但需要用户确认是否占位。
  - 缺资源，先禁用或补资源后启用。

### 4.5 Soup Contribution

- 玩家在汤锅交互体积内触发投料。
- 每个参与玩家只可投一次。
- 投入的是一个单独物品，不是整组 stack。
- 原版把投料存在 team collection；项目中应使用 server-side durable state，支持多人、重连和主事件评分。
- 投料后广播聊天：`Chat_LuauSoup` 对应项目本地化 key。
- 若投的是短裤类物品，原版会特殊处理并可进入结果 6。项目中需要与当前 Lucky Purple Shorts 实现对齐。

### 4.6 Soup Eligibility

必须按原版意图实现，不凭物品印象：

- 只允许 Stardew object-like item 投汤。
- 食物、作物、鱼、动物产品、矿物等是否可投，取决于项目物品是否能提供原版所需的 `price`、`edibility`、`category` 或等价数据。
- `edibility == -300` 或 ring category 等不可食用类别默认不可投。
- 镇长短裤例外。

项目需要先确认物品数据来源：

- 优先使用项目已有 `IStardewItem` / vanilla object data / item registry 中的价格、食用值、分类。
- 若某些 item 缺 `edibility` 或原版 category，不能猜；先在 service 中判定为不可投，或补数据映射后再开放。

### 4.7 Governor Taste Scoring

评分必须保留原版阈值：

- 初始 `likeLevel = 5`。
- 遍历所有投料，取最差 `itemLevel`。
- 若任何投料是 Mayor Shorts，`likeLevel = 6` 并停止。
- 最佳汤 `itemLevel = 4`：
  - `quality >= 2 && price >= 160`；或
  - `quality == 1 && price >= 300 && edibility > 10`；
  - 全镇友好度 +120。
- 好汤 `itemLevel = 3`：
  - `edibility >= 20`；或
  - `price >= 100`；或
  - `price >= 70 && quality >= 1`；
  - 全镇友好度 +60。
- 普通汤 `itemLevel = 2`：
  - `price > 20 && edibility >= 10`；或
  - `price >= 40 && edibility >= 5`。
- 差汤 `itemLevel = 1`：
  - `edibility >= 0`；
  - 全镇友好度 -50。
- 糟糕汤 `itemLevel = 0`：
  - `edibility > -300 && edibility < 0`；
  - 全镇友好度 -100。
- 如果不是短裤结果，且团队投料数少于有效玩家数，最终结果强制为 5。
- 结果 4 触发原版 achievement 38；项目里若成就系统未映射，先做 TODO/ledger，不伪造。

### 4.8 Main Event and End

- Lewis 触发主事件确认。
- 主事件进入 `MAIN_EVENT` phase。
- 玩家控制应按现有主动节日主事件规则禁用或限制。
- Governor 尝汤前播放或展示固定流程。
- 根据 `governorTaste` 分支进入 0-6 结果文本。
- 结果结束后执行统一 active festival end：设置时间、恢复地图/NPC/music/HUD，关闭 session。

## 5. Proposed Architecture

### 5.1 New Service Boundary

建议新增 `LuauFestivalService`，只管理 Luau 主动节日业务，不修改通用框架语义。

职责：

- 判断当前 Luau variant。
- 响应 active handler：tick、login、logout、shop、main event、time freeze、cleanup。
- 管理 Luau 投料状态。
- 调用 scoring service。
- 调用 cutscene/result presentation。
- 在节日结束时清理 runtime-only state。

### 5.2 Active Handler

在 `ActiveFestivalHandlers` 注册 Luau delegate：

- `festivalId = "summer11"`。
- `tryOpenPierreFestivalShop` 委托 Luau shop。
- `tryStartMainEvent` 委托 Luau main event。
- `getParticipating` 用 session participants。
- `applyTimeFreeze` 与 Egg/Flower 保持一致。
- `onMapOverlayApplied` 用于初始化 runtime actors/markers，但只在 3D 点位确认后启用。

### 5.3 Persistent State

需要一个 server-side Luau state，建议不要把复杂 ItemStack 只压成 id/count。

字段建议：

- `festivalInstanceKey`：year + season + day + festivalId。
- `variant`：`Y1` / `Y2`。
- `contributions`：player UUID -> contributed stack snapshot。
- `contributionOrder`：保留多人投料顺序，便于日志/调试。
- `mainEventStarted`。
- `resultLevel`：尚未评分时为空，评分后为 0-6。
- `friendshipApplied`：避免重进/重连重复加减友好度。

ItemStack 保存原则：保留 DataComponents，尤其是任何自定义物品变体数据。项目已有相关经验：不要只保存 id/count/damage。

### 5.4 Soup Interaction Service

建议拆出 `LuauSoupInteractionService`：

- 根据当前 3D 汤锅交互体积判断玩家是否可投。
- 打开投料 UI 或服务端选择流程。
- 过滤可投物品。
- 从玩家背包扣 1 个。
- 写入 Luau state。
- 广播聊天。
- 返回投料结果 UI。

如果项目已有可复用物品选择 UI，可复用；如果没有，第一阶段可做保守交互：玩家主手持可投物时右键汤锅交互体积即投 1 个。该取舍需要确认，因为原版是打开 ItemGrabMenu 让玩家从背包选择。

### 5.5 Scoring Service

建议拆出 `LuauScoringService`，纯函数化，便于测试：

- 输入：有效玩家数、投料 stack 列表、每个 stack 的 Luau item facts。
- 输出：`LuauTasteResult`，包含 `level`、friendship delta、achievement flag、debug reason。

`LuauItemFacts` 字段：

- `itemId`。
- `quality`。
- `price`。
- `edibility`。
- `category`。
- `isMayorShorts`。
- `isEligibleForSoup`。

### 5.6 Main Event Presentation

建议分两层：

- `LuauMainEventService`：服务端状态推进、确认、评分、结束。
- 客户端 payload/screen/cutscene：展示 Lewis/Governor 文本、音乐、镜头、动画。

第一版可以先实现结果文本和节日结束，后续补完整移动动画与镜头；但评分与状态必须先完整。

### 5.7 Shop Registration

在 `ShopRegistry` 中新增 `Festival_Luau_Pierre` 或项目命名一致的 `Festival_Luau_Pierre` 定义，并让 `FestivalRegistry` 的 `summer11` shopIds 填入该 id。

映射建议：

- `stardewcraft:starfruit`：已存在，可直接对应 `(O)268`，stock 1。
- `totem_pole`：项目有 farm/mountain/beach/desert 四类，不等同原版家具 `Totem Pole`，需要用户确认用哪个或补装饰版。
- `scarecrow_5` 与 Luau 商店无关，不要误加。
- Wall Palm / Jungle Decal / Ceiling Leaves / Plain Torch 需要资源确认。

### 5.8 Music

原版用：

- setup/main event 开头：`ocean`。
- best result：`SettlingIn`。
- good result：`jaunty`。
- 部分分支：`ocean` 或 none。

项目现状已有 `music_ocean_ambience`、`music_event1`、`music_tick_tock`、`music_flower_dance`、`music_fall_fest` 等。规划建议：

- 第一阶段将 `ocean` 映射到现有 ocean ambience 或补 `music_ocean`，需确认。
- `SettlingIn` / `jaunty` 若资源缺失，先用无音乐或现有相近曲目，并在 ledger 中记录。
- 不在代码中硬编码不存在的 sound event。

## 6. Implementation Phases

### Phase 1: Planning and 3D Authoring Inputs

产出：

- 本规划文档。
- Luau 3D 点位确认表：只列需要确认的点位/体积，不直接实现。
- Luau source ledger：保留原版文件、函数、字段、评分阈值、商店条目。

验收：

- 用户确认哪些点位可以落地。
- 用户确认资源取舍和 UI 取舍。

### Phase 2: Framework Wiring

改动范围：

- `FestivalMapOverlayRegistry`：注册 `Beach-Luau` overlay，按确认后的 3D bounds/origin。
- `ActiveFestivalHandlers`：注册 Luau handler。
- `FestivalRegistry`：补 Luau shop id，如采用 `Festival_Luau_Pierre`。
- 新增 `LuauFestivalService` skeleton。

验收：

- 夏 11 日 9:00-14:00 可以从 Beach 进入 Luau session。
- 退出/结束不会破坏同日 terminal phase 规则。
- `./gradlew classes` 通过。

### Phase 3: Dialogue and Shop

改动范围：

- 接入 Luau festival dialogue keys。
- 注册 Pierre Luau shop。
- 缺失商店资源按用户确认处理：补资源、占位、或暂时禁用对应条目。

验收：

- Pierre 在 Luau 中打开 Luau shop，不打开普通 SeedShop。
- 商店库存、价格、stock 与原版或确认后的取舍一致。
- NPC 对话在第 1 年/第 2 年能选择对应 key。

### Phase 4: Soup Contribution

改动范围：

- 新增 Luau 投料 state。
- 新增汤锅 3D 交互检测。
- 新增投料过滤与扣物品逻辑。
- 新增聊天广播。
- 处理多人每人一次投料。

验收：

- 未投料玩家可投 1 个合格物品。
- 已投料玩家再次交互收到提示，不重复扣物品。
- 不合格物品不会被扣。
- 重连后投料状态保留。
- 投料 stack 保存 DataComponents。

### Phase 5: Scoring

改动范围：

- 实现 `LuauScoringService`。
- 建立 item facts 提取。
- 短裤特殊分支对齐当前 Lucky Purple Shorts 系统。
- 友好度增减接入项目 NPC 友好度数据。

验收：

- 单元/轻量测试覆盖 0-6 结果。
- 缺投料时强制结果 5。
- 短裤优先级高于缺投料。
- 友好度只应用一次。

### Phase 6: Main Event and Results

改动范围：

- Lewis 交互触发主事件确认。
- 进入 `MAIN_EVENT` phase。
- 调用评分。
- 展示 Governor 尝汤与结果文本。
- 调用 `FestivalService.endFestival` 或现有结束路径。

验收：

- 主事件期间普通 NPC 交互按主动节日规则限制。
- 结果 0-6 都能结束节日。
- 结束后时间、HUD、音乐、overlay、session 状态正常恢复。

### Phase 7: Actors, Animation, and Polish

改动范围：

- 根据确认后的 3D actor layout 放置 setup NPC。
- 根据确认后的 3D actor layout 放置 main event NPC。
- 补 Governor actor、朝向、尝汤移动、短裤分支临时展示。
- 补音乐映射、音效、镜头。

验收：

- 第 1 年/第 2 年布局按确认后的 3D 点位切换。
- NPC 不与场景、玩家入口、交互体积明显重叠。
- 主事件镜头/文本不造成玩家卡死。

### Phase 8: Validation

最低验证：

- `./gradlew classes`。
- JSON parse 校验：lang、models、blockstates、data 文件。
- 手动 runClient 路径：进入 Luau、Pierre shop、投料、Lewis 主事件、六类结果抽测、退出。

建议 debug commands：

- 设置日期到夏 11。
- 强制打开 `summer11` debug festival。
- 注入不同质量/价格/edibility 的测试物品。
- 强制多人有效玩家数模拟，验证缺投料结果。

## 7. Risks and Decisions

### 7.1 3D Layout Risk

Luau 原版地图是二维 tile 临时地图；项目是 3D 世界 overlay。不能自动从 TMX 转坐标。解决方式：先用原版关系做参考，再由用户确认项目 3D 点位表。

### 7.2 Item Data Risk

评分依赖 `quality`、`price`、`edibility`、`category`。项目物品如果缺任何字段，评分会失真。解决方式：先实现 facts extractor，并对缺字段物品保守处理或补映射。

### 7.3 Multiplayer State Risk

Luau 投料是团队状态；主事件可能跨重连。必须使用持久 session/state，不用客户端临时变量决定评分。

### 7.4 Resource Risk

商店家具和音乐可能未导入。不能因资源缺失改原版机制。应在商店条目中显式标记缺项，并由用户确认补资源或占位。

### 7.5 Existing Dirty Worktree

仓库已有大量未提交改动。实现时必须只改 Luau 相关文件，不回退、不格式化、不顺手清理无关改动。

## 8. Confirmation Checklist

进入实现前，需要确认：

- 是否第一版同时支持第 1 年和第 2 年 Luau layout。
- 是否采用完整背包选择 UI，还是先用主手物品右键投汤。
- `ocean`、`SettlingIn`、`jaunty` 的音乐映射。
- 商店缺失家具是补资源、占位，还是暂时隐藏。
- Lucky Purple Shorts 当前系统里哪些 item/block/variant 算作 Luau 短裤。
- Luau overlay 的 3D origin/bounds。
- 玩家进入/退出 3D 点位。
- Pierre shop、汤锅、Lewis trigger 的 3D 交互体积。
- Governor 与 NPC 的 setup/main event 3D 站位和朝向。
- 是否需要为 Luau 写独立 debug 命令或沿用现有 festival debug。

## 9. Recommended First Implementation Slice After Confirmation

确认后建议第一刀只做可验证的骨架：

1. 注册 Luau active handler 与 overlay。
2. 让夏 11 能进入/退出 Luau session。
3. 接 Pierre shop，但缺资源条目先按确认策略处理。
4. 加汤锅主手投料和 state 保存。
5. 加纯评分服务与测试/调试输出。

这一刀完成后再补完整主事件动画和 NPC 3D 布局，避免在点位、资源、机制三件事同时未定时把问题缠在一起。