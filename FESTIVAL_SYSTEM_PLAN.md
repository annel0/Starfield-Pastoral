# StardewCraft Festival System Plan

本文档记录 StardewCraft 节日复刻的总体规划、开发原则和验收标准。目标不是简单实现“某天触发一个活动”，而是长期、逐个节日地高还原 Stardew Valley 的节日体验，并保证多人、地图恢复、NPC、对话、商店、玩法状态都能稳定扩展。

## 1. 总目标与硬约束

### 1.1 复刻目标

- 按 Stardew Valley 原版时间顺序逐个复刻节日。
- 先完成节日基础系统，再一个节日一个节日补齐地图、NPC、对话、商店、玩法和奖励。
- 复刻目标以“玩家体验一致”为准，包括节日前提醒、地图变化、NPC 站位与特殊对话、节日商店、专属小游戏、结束后时间推进、奖励和后续状态。
- 允许分阶段开发单个节日，但每一阶段都要有明确可验收目标。

### 1.1.1 Source-of-truth 约束

- 所有节日逻辑以原版源代码和原版 Content 数据为准。
- 动手实现某个节日前，必须先阅读对应原版 C# 逻辑、`Content/Data/Festivals/*`、`Content/Data/PassiveFestivals.json`、商店、NPC 对话、音效、音乐和相关小游戏代码。
- 不允许凭记忆或 wiki 摘要补逻辑；wiki 只能作为索引或辅助核对，不能替代源代码。
- 发现项目当前实现与原版不一致时，应优先记录差异，再按原版语义修正。

### 1.1.2 资产复刻约束

- 不做临时贴图、临时 UI、临时音效、临时视觉效果占位。
- 需要贴图、UI、声音、音乐、粒子或视觉素材时，优先从 `源文件/Content` 和 `源文件/音频文件` 查找并迁移。
- 音频编号和名称可使用 Stardew Valley 音频索引页面辅助查询，并通过项目工具如 `convert_music.py` 转换。
- 如果某个节日需要新模型或当前项目无法从源资产中直接还原，应先列出需求并询问用户，不擅自用替代模型。
- 每个资产迁移都要记录来源、目标路径、用途和是否已验收。

### 1.1.3 UI 复刻约束

- 所有节日 UI 必须尽量与原版一致，包括纹理、九宫格、字体布局、按钮状态、hover、音效和缩放行为。
- 需要的 UI 组件必须单独从原版 UI 资产中截出，一个组件一个 PNG，并建立清晰文件管理。
- 不同 GUI scale 下必须稳定显示：文字不能溢出，长文本要自动缩放或换行，按钮和面板不能错位。
- 项目中已有的原版 MC UV 网格下还原 Stardew UI 的实现是优先参考样板。

### 1.1.4 玩法复刻约束

- 所有节日小游戏和玩法逻辑都要按原版实现，包括倒计时、计分、NPC 模拟成绩、奖励、失败/平局边界和多人语义。
- Cutscene 只承载表现，玩法胜负和奖励必须由服务端 mechanic 控制。
- 每个玩法实现前必须先列出原版源码中的关键变量、状态流和结算规则。

### 1.2 最高体验约束

- 玩家自始至终应感觉自己仍在同一张 Stardew Valley 地图里。
- 主动节日不能通过“传送到另一张节日副本地图”来解决。
- 小镇、海滩、森林、沙漠等公共区域可以在节日期间临时变样，节日结束或过夜后恢复到普通公共地图状态。
- 农场是玩家拥有区域，原则上不参与节日地图覆盖。公共区域是系统拥有区域，允许按原版状态重置。

### 1.3 坐标与地图资产约束

- 不从原版 tile、旧坐标、TMX 或 pregen 位置自行推算新坐标。
- 所有新点位、NPC 站位、交互点、蛋点位、商店点位、传送点，都应走 `PREGEN_COORDINATE_MIGRATION_LEDGER.md` 或项目已经确认的坐标体系。
- 节日地图 overlay 的世界坐标必须来自已迁移的 StardewCraft 坐标，不允许临时猜坐标。
- 任何 schem 资产替换都要明确 origin、尺寸、作用区域、恢复模板和版本。
- 所有点位、建筑、overlay origin、NPC 站位、蛋点位、商店点、入口点和结束点都必须由用户提供或明确确认；缺少这些空间定义时，节日入口必须拒绝启动，不能用默认值、占位点或原版 tile 坐标推算替代。

### 1.4 当前执行路线

当前路线以 Egg Festival 作为第一套主动节日样板。目标不是一次性把所有节日铺满，而是先把原版底层语义补齐，再把 Egg Festival 的主事件和找蛋玩法做成可复用模板。

1. 先补完原版节日底层差异表：把原版主动节日、被动节日、时间冻结、临时地图、NPC 接管、多人 ready gate、结束回家、商店和普通交互锁全部列成账本。
2. 再补 Egg Festival 主事件：Lewis 开场、`startContest` ready gate、哨声、`tickTock` 音乐、找蛋计时、`endContest` ready gate、获胜/奖励、`festivalEnd` ready gate。
3. 找蛋玩法只在用户提供并确认蛋点、玩家站位、NPC 参赛路线、场地边界后实现；缺任何空间点位时只写待办，不推算。
4. Egg Festival / Flower Dance 验收过程中先抽公共主动节日 handler 注册层：tick、debug apply/restore、NPC 控制权、主事件调试、时间冻结都通过主动节日注册表分发；后续再继续下沉事件脚本阶段、actor scene、ready gate、interaction lock、festival shop、end restore。
5. 主动节日框架稳定后，再做被动节日框架：多日状态、开放时间、地图替换、每日 setup/cleanup、钓鱼条件和 NPC 日程覆盖。

当前已落地但仍需在后续继续扩展的样板：Egg Festival / Flower Dance 会场 overlay/session、时间冻结、22:00 结束回家、Pierre 节日商店、节日 NPC actor、节日对话、普通交互锁、手动 debug 状态查询、authored NPC route 走中央 movement service；主动节日运行时入口已通过 `ActiveFestivalHandler` / `ActiveFestivalHandlers` 注册，中央 NPC 日程让渡已通过 `FestivalNpcController` 统一查询。

## 2. 原版节日事实基线

### 2.1 主动节日

主动节日来自原版 `Content/Data/Festivals/FestivalDates.json`，节日脚本来自 `Content/Data/Festivals/{season}{day}.json`。

| 日期 | 原版 id | 节日 | 主要区域 | 主要系统 |
| --- | --- | --- | --- | --- |
| 春 13 | `spring13` | Egg Festival | Town | 节日商店、NPC 会场、找蛋小游戏、奖励草帽 |
| 春 24 | `spring24` | Flower Dance | Forest | 舞伴邀请、好感条件、群舞 cutscene |
| 夏 11 | `summer11` | Luau | Beach | 汤锅投料、评分、总督反应、好感奖励 |
| 夏 28 | `summer28` | Dance of the Moonlight Jellies | Beach | 夜间会场、码头/水母 cutscene |
| 秋 16 | `fall16` | Stardew Valley Fair | Town | 星星币、展览评分、小游戏、兑换商店 |
| 秋 27 | `fall27` | Spirit's Eve | Town | 万灵节地图、迷宫、金南瓜、商店 |
| 冬 8 | `winter8` | Festival of Ice | Forest | 冰钓比赛、NPC 参赛、奖励 |
| 冬 25 | `winter25` | Feast of the Winter Star | Town | 秘密朋友、送礼、回礼、冬星邮件 |

主动节日的特征：日期命中后天气通常显示 Festival；进入节日后普通日程被节日会场接管；主事件由特定 NPC 或交互触发；结束后通常推进到晚上或直接结束一天。

### 2.2 被动节日

被动节日来自原版 `Content/Data/PassiveFestivals.json`。

| 日期 | id | 节日 | 开始时间 | 地图变化 | 主要系统 |
| --- | --- | --- | --- | --- | --- |
| 春 15-17 | `DesertFestival` | Desert Festival | 1000 | Desert 变节日沙漠 | 卡利可蛋、沙漠商店、赛跑、任务、矿洞加成 |
| 夏 20-21 | `TroutDerby` | Trout Derby | 0610 | 基本无大地图替换 | 钓鱼条件、虹鳟奖励、活动计数 |
| 冬 12-13 | `SquidFest` | SquidFest | 0610 | 基本无大地图替换 | 钓鱿鱼计数、奖励、活动提示 |
| 冬 15-17 | `NightMarket` | Night Market | 1700 | Beach 变 BeachNightMarket | 夜市商店、潜水艇钓鱼、装饰、船只 |

被动节日的特征：不一定显示为普通主动节日；有 `IsPassiveFestivalDay`、`IsPassiveFestivalOpen`、`GetDayOfPassiveFestival` 语义；可能持续多天；可能只在指定时间后开放；NPC 日程可能按 `FestivalId_第几天` 覆盖。

### 2.3 原版底层语义差异表

本表用于记录“原版怎么做”和“StardewCraft 当前怎么做”的差距。后续任何节日实现都应先对照本表，避免只复刻表面剧情却漏掉底层状态。

| 底层项 | 原版 Stardew Valley 语义 | StardewCraft 当前状态 | 缺口/下一步 | 优先级 |
| --- | --- | --- | --- | --- |
| 主动节日日期来源 | `Content/Data/Festivals/FestivalDates.json` 列出 8 个主动节日：`spring13`、`spring24`、`summer11`、`summer28`、`fall16`、`fall27`、`winter8`、`winter25`。 | 已有节日注册表雏形，Egg Festival 以 `spring13` 接入。 | 注册表最终要覆盖全部主动节日，并保留原版 id。 | P0 |
| 主动节日脚本来源 | 每个主动节日读取 `Content/Data/Festivals/{season}{day}.json`；Egg Festival 的 `conditions` 是 `Town/900 1400`，脚本包含 `set-up`、`mainEvent`、`afterEggHunt`。 | Egg Festival 当前是 Java service + overlay + actor service 复刻，不运行原版脚本解释器。 | 每个节日前先把对应 JSON 的阶段、音乐、actor、warp、ready gate、结算拆成需求账本。 | P0 |
| 被动节日来源 | `Content/Data/PassiveFestivals.json` 定义 NightMarket、DesertFestival、TroutDerby、SquidFest，包含 `StartDay/EndDay`、`StartTime`、`MapReplacements`、`DailySetupMethod`、`CleanupMethod`。 | 文档已有分类，运行时还未完整实现被动节日状态。 | 建立 `isPassiveFestivalDay/open/getDayOfPassiveFestival`，并接入钓鱼、地图和 NPC 日程。 | P1 |
| 主动节日进入条件 | 原版 `conditions` 写在节日 JSON 第一层，如 Egg Festival `Town/900 1400`，表示指定地点和开放时间窗口。 | Egg Festival 有入口/参与状态，但仍偏项目内 service 判断。 | 入口判断需要数据化记录地点、开放时间、参与者状态；未到时间或缺点位时拒绝进入。 | P0 |
| 临时地图 vs overlay | 原版主动节日通常 `changeToTemporaryMap Town-EggFestival`，在 `temporaryLocation` 上跑节日地图。 | StardewCraft 采用同世界公共区域 overlay/backup/restore，不切到独立临时地图。 | 这是有意差异：体验保持同一 MC 世界；必须用严格备份恢复、崩溃恢复和 portal 保护弥补。 | P0 |
| 地图恢复 | 原版退出 event 后离开 temporary map，NPC 回默认位置；被动节日通过 map replacement 和 cleanup 方法恢复。 | Overlay manager 已有 apply/restore 和 runtime backup，Egg Festival 已使用。 | 需要为所有 overlay 记录 origin、bounds、版本、restore 策略；异常退出/重启后自动恢复。 | P0 |
| 时间冻结 | `Game1.shouldTimePass()` 在 `Game1.isFestival()` 为 true 时返回 false；婚礼、farm event 等也会冻结。 | Egg Festival participant 状态已冻结虚拟时间。 | 抽成通用 active festival session 时间冻结，不让各节日单独写。 | P0 |
| 结束时间 | `Event.exitEvent()` 对普通节日设置 `Game1.timeOfDayAfterFade = 2200`；`summer28` 和 `fall27` 为 `2400`。 | Egg Festival 结束已跳到 22:00。 | 在 `FestivalDefinition` 中显式记录 end time，Moonlight Jellies/Spirit's Eve 后续要用 24:00。 | P0 |
| 结束回家 | 原版 festival exit 把主机玩家送到 Farm 主屋入口，非主机送各自 porch spot，并重置 actor/村民。 | Egg Festival 当前结束回农场并 restore NPC/overlay。 | 多人时要按参与玩家分别处理回家点；不能只按单人路径。 | P0 |
| 普通交互锁 | 原版节日 `currentEvent.isFestival` 时，`GameLocation.checkAction` 转给 `currentEvent.checkAction`；普通 NPC/物件流程被节日事件接管。 | 已有 `FestivalInteractionLockEvents`，并在 NPC interaction 中拦截普通 gift/quest/shop。 | 抽通用规则：节日期间只放行节日 NPC 对话、节日商店、节日机制交互。 | P0 |
| 工具使用 | 原版 `Event.canPlayerUseTool()` 默认 false；冬 8 冰钓比赛在 timer 中是特例。 | Egg Festival 已锁 block/tool/item 操作。 | 未来 Festival of Ice 需要按原版特例放行比赛鱼竿，不影响其他节日。 | P1 |
| NPC actor 接管 | 原版 `loadActors Set-Up/MainEvent` 加载节日 actor，`advancedMove` 驱动节日路线，普通日程被会场状态覆盖。 | Egg Festival actor service 已接管第一批 NPC，并通过 central movement authored route 移动。 | 每个节日要有 actor scene 数据；所有站位和路线必须用户提供或确认，不从原版 tile 推算。 | P0 |
| NPC 路线精度 | 原版 tile pathing/`advancedMove` 允许路径导航自然完成；不是每个 waypoint 都要求 MC 世界绝对精确摆位。 | Authored route 已允许 `navDone` 且 1.5 格内接受 step 完成；普通日程 final placement 仍保持严格。 | 保持该规则只作用于 authored patrol，不扩散到普通 NPC schedule。 | P0 |
| 对话来源 | 节日 JSON 包含节日专属 NPC key，spouse/y2 等变体也在同文件内。 | Egg Festival dialogue key 已迁移并接入 actor 右键。 | 每个节日前先迁移 key，再做 actor；缺 key 时不写假对白。 | P0 |
| 商店 | 原版节日商店由节日事件/地点触发，物品、价格、限购来自原版数据或代码。 | Pierre Egg Festival shop 已按节日区域和 Pierre actor 触发。 | 抽 `FestivalShop`，每个 shop 记录来源、触发 actor/区域、货品、限购和关闭条件。 | P1 |
| 音乐/音效 | Egg Festival `set-up` 用 `fallFest`，主事件回 `event1`，hunt 用 `tickTock`，开始/结束用 `whistle`。其他节日也在脚本中显式 `playMusic/playSound`。 | 已记录 Egg Festival 音乐事实，资源接入未完成。 | 查 `源文件/音频文件` 并迁移真实资源；没有资源前只列需求，不用替代音。 | P1 |
| 多人 ready gate | 原版 `waitForOtherPlayers gateId` 在多人里用 `Game1.netReady` 和 `ReadyCheckDialog`；Egg Festival 有 `startContest`、`endContest`、`festivalEnd`。 | 当前只有近似 exit vote/会话状态，未复刻各 gate。 | 实现服务端 ready gate：gate id、参与者、不可取消对话、全部 ready 后推进阶段。 | P0 |
| 主事件阶段 | 原版 Egg Festival `mainEvent` 先 fade/loadActors/warpFarmers/Lewis 讲话，再 `startContest` gate，之后 whistle + `tickTock` + `playerControl eggHunt`。 | 当前还没有完整主事件阶段，只是会场自由阶段。 | 下一步优先补 Lewis 开场、ready gate、hunt phase、afterEggHunt。 | P0 |
| 玩法计时/结算 | 原版 `playerControl eggHunt` 控制倒计时、收蛋、NPC 模拟路线/成绩，之后 `afterEggHunt`、`eggHuntWinner`、`awardFestivalPrize`。 | Egg hunt 玩法未实现。 | 等用户提供蛋点、玩家站位、NPC 路线/场地边界后再做；奖励与胜负由服务端 mechanic 控制。 | P0 |
| Cutscene 职责 | 原版 event 脚本同时承载镜头、对白、NPC 移动和命令；部分胜负由 cutscene command 串联。 | 项目规划要求 cutscene 只负责表现，玩法胜负由服务端 mechanic 控制。 | 保持该架构差异，但结果、时机、奖励和视觉反馈按原版。 | P0 |
| 日历/提醒 | 主动节日显示在 `FestivalDates`；被动节日可 `ShowOnCalendar`，并有开始消息。 | 日历和提醒集成未完整。 | 接入日历、前一天/当天消息、被动节日开始消息。 | P2 |
| 天气/世界状态 | 主动节日通常影响当天显示和普通活动；被动节日不等同 `isFestival()`，可多日开放。 | Egg Festival 已有独立 participant/session 状态。 | 区分 `isFestivalDay`、`isFestivalSessionActive`、`isPassiveFestivalOpen`，避免混用。 | P0 |
| 钓鱼条件 | 原版被动节日会进入 GameStateQuery，如 `IS_PASSIVE_FESTIVAL_OPEN`，Trout Derby/SquidFest 依赖钓鱼计数。 | 当前钓鱼侧相关 query 曾有永远 false 的缺口。 | 被动节日 runtime 完成后接入 FishingDataManager/GameStateQuery。 | P1 |
| NPC 日程与被动地图替换 | 原版 NPC pathing 会识别 active passive festival 的 `MapReplacements`，目标地点可替换成节日地图。 | 当前主动 Egg actor 已单独接管；被动节日 NPC 替换未做。 | 被动节日阶段再做 schedule target location replacement。 | P2 |
| 资产来源 | 原版地图、UI、音频、对话都从 Content/源码读取；空间坐标在 StardewCraft 中不能直接套原版 tile。 | Egg Festival 文档已把用户确认点位单独记账。 | 继续保持：内容事实来自原版，世界点位来自用户确认的 StardewCraft 坐标。 | P0 |

## 3. 核心架构

节日系统应作为一个独立运行时，不应把判断散落在天气、钓鱼、NPC、商店、cutscene 里。

### 3.1 FestivalDefinition

每个节日需要一份定义数据，至少包括：

- `id`
- `displayNameKey`
- `type`: `ACTIVE` 或 `PASSIVE`
- `season`
- `startDay`
- `endDay`
- `startTime`
- `endTime`，如需要
- `locationKey`
- `calendarVisible`
- `weatherMode`
- `mapOverlayId`
- `entryRules`
- `npcSceneId`
- `shopIds`
- `mechanicId`
- `cleanupPolicy`

主动节日通常 `startDay == endDay`。被动节日可以持续多天，并且 `startTime` 与 `IsPassiveFestivalOpen` 相关。

### 3.2 FestivalRegistry

负责注册所有节日定义，并提供只读查询：

- 按日期查主动节日。
- 按日期查被动节日。
- 按 id 查定义。
- 按日历展示查节日。
- 按地图区域查可能影响该区域的节日。

第一阶段可以硬编码 Java 注册表，后续再数据化到 JSON。

### 3.3 ActiveFestivalHandlers

已落地第一层主动节日运行时注册表：

- `ActiveFestivalHandler` 描述一个主动节日的运行时入口：tick、overlay applied 回调、debug apply/restore、NPC debug、NPC 控制权、参与者判断、Pierre 节日商店、主事件调试、主事件交互锁、时间冻结和状态查询。
- `ActiveFestivalHandlers` 注册当前主动节日 handler；`FestivalSystem` 不再硬编码 `EggFestivalService.tick` / `FlowerDanceService.tick`。
- `FestivalNpcController` 作为中央 NPC 调度的让渡门面，`NpcCentralMovementService` 只问节日控制器是否接管 NPC，不再逐个节日点名。
- debug 命令的主动节日 apply/restore/npc/main/status 通过 handler 分发；NPC 交互中的 Pierre 商店、Lewis 主事件、主事件锁定也通过 handler 进入，舞伴邀请、找蛋对话等专属 mechanic 仍留在对应 service。

这一层只做分发和控制权管理，不改变 Egg / Flower 的玩法逻辑；后续再把 ready gate、actor scene、festival shop、end restore 继续抽成更细的公共模块。

### 3.4 FestivalService

负责原版语义查询和每日刷新：

- `isFestivalDay()`：只表示主动节日。
- `getActiveFestivalToday()`：返回今日主动节日。
- `isPassiveFestivalDay(id)`：日期和条件命中。
- `isPassiveFestivalOpen(id)`：日期命中且当前时间到达开放时间。
- `getDayOfPassiveFestival(id)`：返回多日节日第几天；按原版 `Utility.GetDayOfPassiveFestival` 语义，未命中返回 `-1`。
- `getOpenPassiveFestivals()`：返回当前开放的被动节日。
- `onNewDay()`：刷新 active/passive 状态，调度邮件、地图 overlay、清理状态。
- `onTimeChanged()`：检查被动节日开放时间、广播开场消息、应用晚间 overlay。

现有 `FishingDataManager` 中 `IS_PASSIVE_FESTIVAL_OPEN` 必须接入这里，不能继续永远 false。

### 3.5 FestivalSession

节日运行时会话由服务端权威维护，尤其用于多人。

建议状态：

- `SCHEDULED`：今日有节日，但尚未开放或尚未进入会场。
- `PREPARING_MAP`：正在应用地图 overlay。
- `OPEN`：节日会场开放，可交互。
- `MAIN_EVENT`：主事件或小游戏进行中。
- `ENDING`：奖励、收尾、传送或时间推进。
- `RESTORING_MAP`：恢复普通地图。
- `CLOSED`：节日结束。

Session 至少记录：

- `festivalId`
- `date`
- `state`
- `participants`
- `mapOverlayId`
- `mapPatchProgress`
- `npcActorState`
- `temporaryScores`
- `playerFestivalState`
- `teamFestivalState`
- `startedMainEvent`
- `cleanupComplete`

Session 必须进入 SavedData 或等价持久化结构。服务器崩溃后不能留下半张节日地图无法恢复。

### 3.6 FestivalMechanic

每个节日的专属玩法独立成 mechanic/service，不要硬塞进 cutscene 命令。

示例：

- `EggHuntService`
- `FlowerDanceService`
- `LuauSoupService`
- `FairService`
- `SpiritEveMazeService`
- `IceFishingContestService`
- `WinterStarGiftService`
- `NightMarketService`
- `DerbyFishingService`
- `DesertFestivalService`

Cutscene 只负责表现层：锁玩家、镜头、对话、音乐、演员动作。胜负、计分、奖励、多人同步应由 mechanic 控制。

## 4. 地图策略：原地 Overlay Patch

### 4.1 不使用独立节日地图

玩家不能被传送到另一个节日副本地图。节日应发生在原 Stardew Valley 维度中的同一片小镇、海滩、森林或沙漠公共区域。

### 4.2 不推荐全量覆盖整张地图

直接 paste 整张节日 schem，然后 paste 整张普通 schem 恢复，虽然简单，但有问题：

- setBlock 数量过大，容易卡服。
- 方块更新和光照更新成本不可控。
- 崩溃后恢复状态难追踪。
- 很多节日只改变局部区域，全量覆盖浪费。
- 会覆盖公共区域之外的潜在系统方块或动态实体。

### 4.3 推荐方案：schem diff overlay

每个地图 overlay 准备普通模板和节日模板：

- `base.schem`：普通公共区域状态。
- `festival.schem`：节日公共区域状态。
- `patch`：两者差异，包含要改成节日状态的方块和恢复为 base 状态的方块。

应用节日时只写入差异格。恢复时只恢复差异格对应的 base 状态。

这样可以做到：

- 玩家仍在同一张地图。
- 只改必要方块。
- 节日结束恢复官方公共地图。
- 可以分批应用，降低卡顿。
- 可以持久化进度，支持崩溃恢复。

### 4.4 Overlay 数据结构

`FestivalMapOverlayDefinition`：

- `overlayId`
- `regionKey`: `Town` / `Beach` / `Forest` / `Desert`
- `origin`
- `baseSchemPath`
- `festivalSchemPath`
- `bounds`
- `safePositions`
- `blockedDuringApply`
- `requiresBlackFade`
- `cleanupDroppedItems`
- `cleanupTaggedEntities`

`FestivalMapPatchEntry`：

- `relativePos`
- `baseBlockState`
- `festivalBlockState`
- `baseBlockEntityTag`
- `festivalBlockEntityTag`
- `updateMode`

`FestivalMapOverlayState`：

- `overlayId`
- `state`: `APPLYING` / `APPLIED` / `RESTORING` / `RESTORED`
- `cursor`
- `forcedChunks`
- `date`
- `festivalId`
- `dirty`

### 4.5 应用与恢复流程

节日开始：

1. FestivalSession 进入 `PREPARING_MAP`。
2. 计算 overlay 涉及 chunks。
3. force-load 相关 chunks。
4. 清理会挡住节日结构的掉落物和临时实体。
5. 分 tick 应用 patch。
6. 恢复或写入 block entity NBT。
7. 批量通知客户端更新。
8. 释放 chunks 或保留最低限度强加载。
9. Session 进入 `OPEN`。

节日结束或过夜：

1. 停止节日交互。
2. 清理 `festivalId/sessionId` 标记的 NPC、展示实体、交互实体、掉落物。
3. 如果玩家仍在恢复区域内，先移动到安全点或在过夜流程中统一送回家。
4. force-load overlay chunks。
5. 分 tick 恢复 base patch。
6. 恢复 block entity NBT。
7. 批量通知客户端。
8. 清空 overlay state。
9. Session 进入 `CLOSED`。

### 4.6 公共区域重置原则

节日公共区域按 Stardew Valley 规则由系统拥有，可以恢复到官方 base 状态。

- 公共区域不承诺保留玩家放置或破坏。
- 农场和玩家私有空间不被节日 overlay 覆盖。
- 节日区域建议配合保护系统，避免玩家在 overlay 期间破坏关键结构。
- 如果存在公共区域可采集物、杂草、树枝、石头等，应在 overlay 前明确是清理、保留还是重新生成。

### 4.7 崩溃恢复原则

地图 overlay 必须是事务化的。

- 开始应用前写入 SavedData。
- 每批 patch 后更新 cursor。
- 服务器重启时如果发现 `APPLYING`，继续应用或回滚。
- 如果发现 `APPLIED` 但节日日期已过，立即进入恢复。
- 如果发现 `RESTORING`，继续恢复。
- 不允许出现“节日结构半覆盖、系统不知道”的状态。

## 5. NPC、日程与对话

### 5.1 主动节日 NPC

主动节日中，NPC 不应继续普通日程。会场 NPC 应由 `FestivalNpcController` 接管。

需要定义：

- NPC 是否参加该节日。
- 节日会场站位。
- 朝向。
- 动画或姿态。
- 是否可交互。
- 主事件前对话。
- 主事件后对话。
- 玩法结果相关对话。

主动节日开始后：

- 暂停参与 NPC 的普通日程。
- 在节日区域摆放或移动 NPC actor。
- 节日结束后清理 actor 状态，恢复普通日程。

### 5.2 被动节日 NPC

被动节日更接近原版日程覆盖。日程优先级建议为：

1. `marriage_<festivalId>_<dayOfFestival>`
2. `marriage_<festivalId>`
3. `<festivalId>_<dayOfFestival>`
4. `<festivalId>`
5. 原有季节、日期、天气、星期、默认日程

该逻辑应接入现有 `NpcScheduleRuntimeService`，而不是在每个 NPC 里写分支。

### 5.3 节日特殊对话

新增 `FestivalDialogueResolver`，对话优先级建议为：

1. 当前主动节日、NPC、阶段、玩法状态。
2. 当前被动节日、NPC、第几天、开放前后。
3. 节日结果状态，例如舞伴已答应、汤锅评价、展览会获奖、冬星已送礼。
4. NPC 普通节日 fallback。
5. 普通日常对话。

对话状态必须支持多人：同一个 NPC 对不同玩家可能说不同内容，例如花舞节邀请结果、冬星秘密朋友、个人是否已完成小游戏。

## 6. 多人原则

### 6.1 服务端权威

节日状态、地图 overlay、NPC actor、小游戏计分、奖励发放都必须由服务端权威决定。

客户端只做：

- 显示 UI。
- 发起交互请求。
- 接收同步结果。
- 播放本地表现。

### 6.2 参与者模型

主动节日需要参与者集合：

- 玩家进入节日区域或确认参加后加入 `participants`。
- 主事件开始后，迟到玩家不能直接插入进行中的小游戏。
- 离线或掉线玩家保留基本参与状态，但结算规则要按节日单独定义。
- 奖励按参与者发放，不能给未参与玩家误发。

被动节日通常是全局开放、玩家独立参与：

- 夜市买东西是个人行为。
- Trout Derby / SquidFest 的钓鱼计数一般是个人状态。
- Desert Festival 中部分状态可能是 team/shared，例如某些全队活动；部分是个人状态，例如个人任务、购买限制。

### 6.3 主事件启动

主事件启动应由服务端检查：

- 当前 session 是否 `OPEN`。
- 玩家是否在节日区域。
- 是否满足启动 NPC 或交互条件。
- 是否已有主事件进行中。
- 多人是否需要确认或只由触发者启动。

鸡蛋节、冰雪节等多人小游戏应明确：

- 参与者是谁。
- 倒计时何时开始。
- 中途退出如何处理。
- 平局如何处理。
- NPC 竞争者是否模拟计分。
- 奖励是否只发胜者。

### 6.4 过夜与恢复

节日恢复原则上应绑定服务端过夜流程。

- 主动节日结束后，可以按原版推进时间到晚上，再由睡觉或日结算恢复地图。
- 被动节日根据原版时间窗口，可能在当天晚间结束，也可能持续到过夜。
- 过夜前必须停止所有节日小游戏。
- 过夜结算时清理节日临时实体和地图 overlay。
- 多人里只要服务端进入新日，节日状态必须统一推进，不能每个客户端各自恢复。

## 7. 系统集成点

### 7.1 时间系统

接入 `StardewTimeManager`：

- 新日刷新节日状态。
- 指定时间开启被动节日。
- 主动节日当天发送提醒。
- 节日结束后参与过夜结算。
- 清理临时状态。

### 7.2 天气与 HUD

当前项目部分逻辑用 weather `Festival` 判断节日，这不是长期方案。

- 主动节日天气可显示 Festival，但业务逻辑应查 `FestivalService`。
- 被动节日不一定是 Festival 天气，不能被天气漏掉。
- 天气图标、电视天气、动物喂食补偿、铁匠通知等应逐步改为查统一节日 API。

### 7.3 钓鱼

`FishingDataManager` 已有 `IS_PASSIVE_FESTIVAL_OPEN` 条件入口，第一阶段应接入 `FestivalService`。

后续：

- Night Market 启用 `BeachNightMarket` 钓鱼数据。
- Trout Derby / SquidFest 添加计数、奖励、提示。
- Desert Festival 添加沙漠节 fishing quest 和特殊奖励。

### 7.4 商店

节日商店应支持：

- 只在指定节日开放。
- 只在节日第几天开放。
- 按玩家状态显示或限购。
- 使用特殊货币，例如星星币、卡利可蛋。
- 关闭普通商店或替换 NPC 交互。

第一阶段可硬编码商店入口，后续逐步数据化。

### 7.5 Cutscene

Cutscene 用于节日表现，但不承载所有玩法逻辑。

需要新增或复用命令：

- 锁定/解锁玩家。
- 相机移动。
- NPC actor 动作。
- 音乐切换。
- 对话与选择。
- 黑屏/淡入淡出。
- 调用 mechanic 开始或结束。

### 7.6 邮件与日历

节日前邮件、日历展示和公告应统一查 `FestivalRegistry`。

冬星节尤其需要：

- 秘密朋友抽选。
- 邮件中显示收礼对象。
- 只在合适日期显示真实对象，收藏查看可隐藏。

沙漠节需要注意原版条件：完成公交/沙漠访问后才有相关提醒和可见活动。

## 8. 单个节日复刻路线

### 8.1 春 13 Egg Festival

目标：第一个完整主动节日样板。

开发阶段：

1. 日期识别、节日前提醒、入口开放。
2. Town 节日 overlay patch。
3. NPC 会场站位和节日对话。
4. Pierre 节日商店。
5. Lewis 触发找蛋主事件。
6. 锁玩家、倒计时、蛋点位、捡蛋计数。
7. NPC 竞争者模拟计分，Abigail 胜负规则。
8. 奖励草帽或后续奖励规则。
9. 结束时间推进、清理 overlay、多人验收。

关键验收：多人同时找蛋不会重复拾取同一颗蛋；掉线、迟到、离开区域不会破坏 session；节日结束小镇恢复普通状态。

### 8.2 春 15-17 Desert Festival

目标：先做被动节日框架验证，再逐步补完整 1.6 内容。

阶段建议：

1. `DesertFestival` 日期和开放时间。
2. 沙漠 overlay patch 和开放提示。
3. 沙漠节 NPC/商店基础。
4. 卡利可蛋作为临时货币。
5. 沙漠节每日差异、第几天逻辑。
6. Willy 任务、赛跑、矿洞卡利可蛋、雕像效果等高级内容。

关键验收：多日状态准确，过夜不提前清错；第 1/2/3 天内容不同；节日结束清除临时货币或按原版规则处理。

### 8.3 春 24 Flower Dance

目标：验证社交条件、邀请、舞伴状态、多人舞蹈 cutscene。

关键内容：

- Forest overlay。
- NPC 节日站位。
- 花舞节邀请对话。
- 好感不足拒绝、足够接受。
- 多人舞伴冲突处理。
- 群舞表现。
- 结束后清理 `dancePartner`。

### 8.4 夏 11 Luau

目标：验证投料、评分和全局结果。

关键内容：

- Beach overlay。
- 汤锅交互。
- 每个玩家投料或团队投料规则。
- 按原版物品评分表。
- 总督评价 cutscene。
- 好感奖励。

多人重点：多人投料时到底一锅一件、每人一件还是团队共享，要对齐原版多人语义。

### 8.5 夏 20-21 Trout Derby

目标：较轻量被动节日，验证 `IS_PASSIVE_FESTIVAL_OPEN` 和钓鱼奖励。

关键内容：

- 日期和开放时间。
- 地点提示和轻量装饰。
- 虹鳟捕获计数。
- 奖励 ticket 或对应物品。
- 多日计数清理。

### 8.6 夏 28 Moonlight Jellies

目标：展示型主动节日，验证夜间 overlay 和大型 cutscene。

关键内容：

- Beach 夜间 overlay。
- NPC 站位。
- 触发水母出现。
- 水面实体/粒子/动画。
- 音乐和相机。
- 结束后推进时间。

### 8.7 秋 16 Stardew Valley Fair

目标：大型主动节日，建议放在基础系统成熟后。

关键内容：

- Town overlay。
- 星星币临时货币。
- 展览台提交物。
- 展览评分。
- 转盘、钓鱼、打靶、力量测试等小游戏。
- 兑换商店。
- 多人分数和提交物规则。

### 8.8 秋 27 Spirit's Eve

目标：地图变化和迷宫玩法。

关键内容：

- Town 万灵节 overlay。
- 迷宫路径、碰撞和隐藏通道。
- 金南瓜奖励。
- 节日商店。
- NPC 对话。

### 8.9 冬 8 Festival of Ice

目标：钓鱼比赛型主动节日。

关键内容：

- Forest 冰雪 overlay。
- 比赛参与者和站位。
- 限时钓鱼。
- NPC 模拟成绩。
- 胜负、奖励、多人处理。

### 8.10 冬 12-13 SquidFest

目标：被动钓鱼活动，复用 Trout Derby 框架。

关键内容：

- 日期和开放时间。
- Beach 活动提示。
- 鱿鱼计数。
- 奖励和多日清理。

### 8.11 冬 15-17 Night Market

目标：大型被动节日，验证晚间地图 overlay 和多商店。

关键内容：

- 1700 开放。
- Beach 夜市 overlay。
- 多船商店。
- 潜水艇钓鱼入口和 `BeachNightMarket` fishing location。
- 每日不同商品。
- 夜市结束恢复。

### 8.12 冬 25 Feast of the Winter Star

目标：礼物系统和个人状态复杂节日。

关键内容：

- Town overlay。
- 秘密朋友抽选。
- 邮件提醒和对象显示。
- 玩家送礼给指定 NPC。
- NPC 回礼。
- 已送礼/未送礼状态。
- 多人中每个玩家独立秘密朋友。

## 9. 开发阶段总路线

### Phase 0：资料与坐标准备

- 确认原版节日数据和脚本入口。
- 为每个节日建立坐标需求清单。
- 将点位录入 `PREGEN_COORDINATE_MIGRATION_LEDGER.md`。
- 明确每个 overlay 的 origin 和 bounds。

### Phase 1：地图 overlay 原型

- 新建 schem 读取和 diff patch 生成能力。
- 使用小范围测试 patch 验证应用和恢复。
- 支持 block entity。
- 支持 SavedData 进度。
- 支持崩溃后继续恢复。
- 支持分 tick 应用，避免卡服。

### Phase 2：节日基础运行时

- `FestivalDefinition`
- `FestivalRegistry`
- `FestivalService`
- `FestivalSession`
- 主动/被动节日日期查询。
- 与 `StardewTimeManager` 接入。
- 与天气、钓鱼、邮件、日历的最小接入。

### Phase 3：NPC、对话、商店接入

- `FestivalNpcController`
- `FestivalDialogueResolver`
- 被动节日日程优先级。
- 节日商店开放条件。
- 主动节日会场 NPC actor。

### Phase 4：第一个完整样板，Egg Festival

- 完成春 13 的所有基础体验。
- 用它验证主动节日完整闭环。
- 输出主动节日开发模板。

### Phase 5：被动节日样板

- 先做 Trout Derby 或 SquidFest 验证轻量被动玩法。
- 再做 Night Market 或 Desert Festival 的 overlay 型被动节日。

### Phase 6：按时间顺序逐节日推进

- 春季：Egg Festival、Desert Festival 基础、Flower Dance。
- 夏季：Luau、Trout Derby、Moonlight Jellies。
- 秋季：Fair、Spirit's Eve。
- 冬季：Festival of Ice、SquidFest、Night Market、Winter Star。

## 10. 验收标准

每个节日至少按以下清单验收：

- 日期正确。
- 节日前提醒正确。
- 日历显示正确。
- 节日期间天气或 HUD 显示正确。
- 地图 overlay 应用正确。
- 节日结束后地图恢复正确。
- NPC 站位、朝向、对话正确。
- 普通日程不会干扰节日会场。
- 商店只在正确阶段开放。
- 专属玩法可完成。
- 奖励正确。
- 多人下状态一致。
- 玩家掉线/重连不破坏 session。
- 服务器重启不留下半覆盖地图。
- 过夜后临时状态清理正确。
- `gradle classes` 或更高验证通过。

地图 overlay 额外验收：

- 应用前后 diff 数量符合预期。
- 不覆盖农场或玩家私有区域。
- 不破坏公共区域永久系统点位。
- block entity 恢复正确。
- 掉落物和临时实体清理正确。
- 分 tick 应用期间不会产生明显服务端卡顿。

## 11. 开发原则

- 先地基，后节日；先闭环，后细节。
- 节日判断统一查 `FestivalService`，不要在各系统里重复写日期判断。
- 地图变化统一走 `FestivalMapOverlayManager`，不要让某个节日自己直接 paste/restore。
- 主动节日 NPC 由节日 session 接管，被动节日 NPC 走日程优先级。
- 玩法逻辑独立成 mechanic，cutscene 只做表现。
- 多人状态服务端权威，客户端不自行决定胜负、奖励或地图状态。
- 公共地图可以恢复到官方 base，农场和私有空间不能被节日覆盖。
- 所有临时实体、交互点、掉落物必须带 `festivalId/sessionId` 标记，便于清理。
- 所有点位先进入坐标迁移流程，再写入代码或 JSON。
- 每个节日分阶段做，但每阶段都要可玩、可测、可恢复。
- 不为某个节日写一次性脆弱逻辑；如果第二个节日会复用，就沉到底层系统。
- 不在未验证的情况下大范围覆盖地图；先用小 patch 测性能和恢复。
- 不用 weather `Festival` 作为业务判断的唯一来源。
- 不把被动节日当主动节日处理；多日、开放时间、第几天都必须保留。

## 12. 第一批具体任务建议

1. 建立 `festival` 包和基础模型：definition、registry、service、session state。
2. 建立 `FestivalMapOverlayManager` 原型，只支持一个小测试 patch。
3. 接入 `StardewTimeManager`，实现主动/被动节日查询。
4. 接入 `FishingDataManager` 的 `IS_PASSIVE_FESTIVAL_OPEN`。
5. 为 Egg Festival 建立坐标需求清单，不直接猜点位。
6. 制作 Egg Festival Town overlay 资产和 diff patch。
7. 完成 Egg Festival 的入口、NPC、商店、基础对话。
8. 再实现 Egg Hunt mechanic。

## 13. 当前结论

StardewCraft 的节日复刻应采用“同图原地变化”的路线。技术核心不是传送玩家到节日副本，而是服务端权威的节日 session 加事务化地图 overlay。

这条路线的优点：

- 符合玩家始终在同一张地图的体验要求。
- 公共区域可以按原版重置，降低恢复复杂度。
- diff patch 比整图 paste 更稳、更省性能。
- 多人和崩溃恢复有明确状态机。
- 后续每个节日都能复用同一套日期、地图、NPC、对话、商店和玩法框架。

## 14. 当前地基落地状态

截至当前实现，节日系统已经具备开始制作第一个节日 Egg Festival 前的基础代码入口：

- `FestivalDefinition` / `FestivalRegistry`：已登记原版 8 个主动节日和 4 个被动节日的日期、时间、地点、overlay id、mechanic id。
- `FestivalService`：已提供主动节日查询、被动节日查询、`isPassiveFestivalOpen`、`getDayOfPassiveFestival`、主动节日入口打开、节日结束恢复等 API。
- `FestivalWorldData`：已持久化当天被动节日、节日 session、参与者、session 阶段和 overlay 阶段。
- `FestivalSessionState`：已支持 `SCHEDULED`、`PREPARING_MAP`、`OPEN`、`MAIN_EVENT`、`ENDING`、`RESTORING_MAP`、`CLOSED`。
- `FestivalMapOverlayManager`：已支持读取 base/festival schem、生成 diff patch、按 tick 分批应用、按 tick 分批恢复、block entity 恢复、chunk force-load 与释放、崩溃后通过 SavedData 游标继续。
- `FestivalSystem`：已接入服务端 tick，驱动 overlay 和 session 状态推进。
- `StardewTimeManager`：新日会刷新节日状态；时间变化会打开到点的被动节日。
- `FishingDataManager`：`IS_PASSIVE_FESTIVAL_OPEN` 已接入真实节日服务。
- `NpcScheduleRuntimeService`：被动节日当天会优先尝试 `<festivalId>_<day>` 和 `<festivalId>` 日程 key。

### Egg Festival 开始前剩余准备

正式制作 Egg Festival 前，下一批任务应按顺序进行：

1. 从原版 `spring13.json`、`Event.cs`、相关商店、音效、NPC 对话中列出 Egg Festival 完整逻辑清单。
2. 通过坐标迁移 ledger 确认 Town overlay origin、会场 NPC 点位、Lewis 入口点、蛋点位、商店点位、玩家入场/结束安全点。
3. 准备 `Town` base schem 与 `Town-EggFestival` festival schem，不做占位结构。
4. 注册 `Town-EggFestival` 的 `FestivalMapOverlayDefinition`。
5. 接入 Egg Festival 入口交互，调用 `FestivalService.openActiveFestival(player, "spring13")`。
6. 接入 Pierre 节日商店与 NPC 会场对话。
7. 再开始实现 `EggHuntService`，严格按原版 `playerControl eggFestival`、`afterEggHunt`、奖励和多人规则复刻。

当前代码门禁原则：`FestivalRegistry` 可以先登记原版日期事实，但只要某个节日声明了 `mapOverlayId`，实际入口就必须等对应 `FestivalMapOverlayDefinition` 注册后才能打开。未注册 overlay 时，主动节日 `openActiveFestival` 返回空，被动节日 session 保持待开放状态，不会把缺失场地当成已开放节日。
