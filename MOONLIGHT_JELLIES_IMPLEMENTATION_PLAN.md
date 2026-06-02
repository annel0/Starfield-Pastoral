# Dance of the Moonlight Jellies Implementation Plan

本文档记录 StardewCraft 复刻夏 28 日主动节日 `Dance of the Moonlight Jellies` / `月光水母起舞` 的源码调研、项目架构对照、实现规划和坐标确认清单。

当前阶段只做调研与规划，不进入代码实现。任何 overlay、NPC 站位、交互体积、展示实体、相机、回退点和水母路径坐标，都必须由用户确认后才能写入 Java 或 JSON。

## 0. Scope

目标：在现有主动节日框架中实现夏季 28 日海滩夜间节日，使玩家可以在 22:00 到 24:00 进入海滩节日会场，与 NPC 对话，访问 Pierre 节日商店，触发 Lewis 放出烛灯船的主事件，观看月光水母出现，并在事件结束后按原版语义回到农场、时间推进到 24:00。

非目标：

- 本阶段不改主动节日框架的大方向，不新建一套平行 festival runtime。
- 不凭 wiki 或记忆补机制；所有行为以 `源文件` 中的 C# 源码和 Content 数据为准。
- 不把原版 TMX/tile 坐标、临时地图坐标或视觉印象直接写成 StardewCraft 运行时 3D 坐标。
- 不用临时贴图、临时音效或假模型占位。缺资源时先记录缺口，等用户确认是否迁移/制作。
- 不在用户确认计划和坐标前实现 Java/JSON/资源改动。

核心判断：这是一个主动节日。它不是被动节日，也不是普通夜间环境事件。原版使用 `changeToTemporaryMap Beach-Jellies` / `Beach-Jellies2`、`loadActors`、`playerControl jellies`、`specificTemporarySprite moonlightJellies` 和 `waitForOtherPlayers festivalEnd` 组织完整事件。StardewCraft 应沿用现有 active festival overlay + service + handler + cutscene 路线复刻。

## 1. Source Ledger

### 1.1 Original Festival Data

原版节日主数据：`源文件/Content/Data/Festivals/summer28.json`。

关键事实：

- `name`: `Dance Of The Moonlight Jellies`。
- `conditions`: `Beach/2200 2400`。
- 年 1 setup 使用 `changeToTemporaryMap Beach-Jellies`。
- 年 2+ setup 使用 `changeToTemporaryMap Beach-Jellies2`。
- setup 阶段：
  - `ocean` 音乐/环境。
  - 玩家初始进入节日临时地图。
  - `loadActors Set-Up` 装载自由阶段 NPC。
  - Vincent、Jas、Clint、Harvey 等在自由阶段有 `advancedMove` 路线。
  - `playerControl jellies` 后交还玩家控制。
- 主事件 `mainEvent`：
  - fade 后 `loadActors MainEvent`。
  - `changeLocation Beach`。
  - `warpFarmers` 把多人玩家排到观礼点。
  - spouse 若存在会进入观礼位置。
  - `specificTemporarySprite candleBoat` 创建烛灯船。
  - Lewis 移动、动画、放船。
  - `specificTemporarySprite candleBoatMove` 让烛灯船移动。
  - `specificTemporarySprite moonlightJellies` 创建月光水母群。
  - `playMusic moonlightJellies` 播放主事件音乐。
  - NPC 表情、跳跃、移动、镜头移动按脚本执行。
  - 结束消息：`The glow of summer has faded, now... and the moonlight jellies carry on toward the great unknown.`
  - `playMusic none/pause 2000/waitForOtherPlayers festivalEnd/end`。
- 主事件 `mainEvent_y2`：
  - 使用不同 actor 布置、玩家观礼点、NPC 行为和部分反应。
  - 结束消息和 `festivalEnd` gate 与年 1 一致。

中文源数据：`源文件/Content/Data/Festivals/summer28.zh-CN.json`。

- 中文节日名在该文件中为 `月光水母之舞`。
- `FestivalDates.zh-CN.json` 中显示为 `月光水母起舞`。
- 中文结束消息为：`夏天的光芒就此消散……而月光水母则继续走向伟大的未知之境。`

### 1.2 Original Mail and Calendar

原版邮件：`源文件/Content/Data/mail.json`。

- `summer_28`：Demetrius 当天早晨提醒玩家晚上 10 点左右月光水母会经过 Pelican Town，大家会在海滩集合观看。

StardewCraft 当前资源中存在 `summer_27` 邮件，内容是“水母起舞节就在明天”，但它不在当前日期邮件投递白名单中。原版是 `summer_28` 当天提醒，后续实现需要决定：

- 按原版补 `summer_28` 并加入可投递白名单。
- 或保留项目已有 `summer_27` 作为前一天提醒，同时记录与原版差异。

邮件硬约束：`PlayerStardewData.mailbox` 只能放 `MailRegistry` 已注册信件；不要直接塞未知 mailId。

### 1.3 Original Shop Data

原版商店：`源文件/Content/Data/Shops.json` 中 `Festival_DanceOfTheMoonlightJellies_Pierre`。

货币：普通金币 `Currency = 0`。

商品：

| 原版 item id | 原版名称 | 类型来源 | 价格 | 库存 |
| --- | --- | --- | --- | --- |
| `(F)2626` | Moonlight Jellies Banner | `Furniture.json` | 800 | 无限 |
| `(F)1687` | Cloud Decal | `Furniture.json` | 1200 | 无限 |
| `(F)1692` | Cloud Decal | `Furniture.json` | 1200 | 无限 |
| `(F)2631` | Starport Decal | `Furniture.json` | 1000 | 无限 |
| `(F)2875` | Modern Rug | `Furniture.json` | 4000 | 无限 |

原版 shop id 分发逻辑在 `源文件/StardewValley/Event.cs`：当事件 id 为 `festival_summer28` 且 action 为通用 `shop` 时，映射到 `Festival_DanceOfTheMoonlightJellies_Pierre`。

StardewCraft 当前 `ShopRegistry` 已有 Egg Festival、Flower Dance、Luau 的 Pierre 节日商店，但没有 `Festival_DanceOfTheMoonlightJellies_Pierre`。后续需要按项目商品资源实际存在情况映射：

- 若已有等价 décor/furniture item：直接上架。
- 若资源缺失：先记录缺口，不用无关物品替代。
- 若用户允许补家具/装饰资源：按原版 furniture 数据和项目 décor 体系新增。

### 1.4 Original Music and Sound

原版 Jukebox track：`源文件/Content/Data/JukeboxTracks.json`。

- key: `moonlightjellies`。
- display name: `Strings\\StringsFromCSFiles:Utility.cs.5812`。
- 可用条件为空。

原版节日脚本中的音乐：

- setup 阶段使用 `ocean`。
- 主事件在水母出现后 `playMusic moonlightJellies`。
- 结束前 `playMusic none`。

StardewCraft 当前已有：

- `MUSIC_OCEAN_AMBIENCE`。
- `MUSIC_EVENT1` / `MUSIC_EVENT2` / `MUSIC_TICK_TOCK` / `MUSIC_FALL_FEST` / `MUSIC_FLOWER_DANCE`。
- `FestivalMusicStatePayload` 可同步 active festival 的 client-side cutscene music override。

缺口：

- `ModSounds.MUSIC_MOONLIGHT_JELLIES`。
- `assets/stardewcraft/sounds.json` 中 `music_moonlight_jellies`。
- `FestivalMusicStatePayload` 常量和 client switch 分支。
- 音频文件迁移/转换后的 `sounds/music/moonlight_jellies.ogg`。
- 可选：`JukeboxTrackRegistry` 添加 `moonlightjellies`，让唱片机曲目列表与原版一致。

### 1.5 Original Visual Behavior

原版视觉命令在 `源文件/StardewValley/Event.cs` 的 `specificTemporarySprite` 分支。

#### Candle Boat

`specificTemporarySprite candleBoat`：

- 从 `Maps\\Festivals` 取 sprite rect `(240,112,16,32)`。
- 放置于 tile `(22,36)`。
- scale 为 4。
- 创建 light id `candleBoat`，半径约 2。

`specificTemporarySprite candleBoatMove`：

- 找到 id 为 1 的烛灯船 sprite。
- motion 设置为 `(0,2)`。

StardewCraft 实现建议：用展示实体或小模型/方块组合表现烛灯船，带弱光源/粒子，沿用户确认路径移动。不能直接套原版 tile。

#### Moonlight Jellies

`specificTemporarySprite moonlightJellies`：

- 清空 `npcControllers`。
- `showGroundObjects = false`。
- 创建 `underwaterSprites` 列表。
- 大约 40 个 `TemporaryAnimatedSprite`：
  - 大部分使用 `Maps\\Festivals` 的 jelly sprite rect `(256,16,16,16)`。
  - 部分使用 `(304,16,16,16)`，运动速度更快。
  - 一个特殊大/绿水母使用 `(256,32,16,16)`，lightRadius 为 2。
  - 起点大多在 y = 49 tile 处，即从屏幕下方向上出现。
  - motion 主要为 `(0,-1)` 或 `(0,-1.5)`，特殊水母为 `(-0.5,-0.5)`。
  - `xPeriodic = true`，横向周期摆动。
  - delay 分布在 0 到约 32000ms，形成分批游入效果。
  - 每只水母都有独立 light id `moonlightJellies_{index}`。
  - 多数 lightRadius 为 1，特殊水母为 2。

StardewCraft 实现建议：

- 新增 Minecraft-native 水母视觉命令，不尝试把 2D sprite 系统硬搬进 MC。
- 可选表现方式：
  - 发光 display entity + 半透明材质。
  - 小型模型实体/方块展示实体。
  - 粒子云 + point light/动态光源，如当前项目已有可用光源方案。
- 运动语义保持原版：从海面/水下远处多批次出现，向上/靠近码头漂动，横向轻摆，特殊水母更醒目。
- 所有 spawn 区域、路径、y 高度、相机可见范围必须等用户确认。

### 1.6 Original Event Ending and Multiplayer

原版 `Event.exitEvent()` 对 festival 做通用结束逻辑：

- 普通节日结束后 `timeOfDayAfterFade = 2200`。
- 若是 `summer28` 或 `fall27`，结束后 `timeOfDayAfterFade = 2400`。
- 主机玩家回 `Farm` 主屋入口。
- 非主机玩家回各自 porch spot。
- 重置 NPC schedule/dialogue/controller。
- 对 farm objects 执行经过时间更新。

原版多人结束：

- `waitForOtherPlayers festivalEnd` 单机直接继续。
- 多人设置 `Game1.netReady` 的 `festivalEnd` gate。
- 其他玩家未 ready 时打开不可取消或可取消 ready dialog，具体由调用点决定。
- `TryStartEndFestivalDialogue` 也用 `festivalEnd` gate 处理主动离开节日。
- server-to-client message `endFest` 会调用 `forceEndFestival`。

StardewCraft 对应设计：

- 主事件结束阶段复用 `ActiveFestivalConfirmState` 管理所有参与者 ready/vote。
- 主事件结束后跳到 24:00，不是 22:00。
- 所有当前节日参与者回农场；多人回各自已有农场/床/门口逻辑，若项目已有 Luau/Flower Dance 回家工具函数，应复用。
- 清理 music override、NPC actors、cutscene participants、overlay/session 状态。

### 1.7 Related But Non-Festival Source

`源文件/Content/Data/Events/Beach.json` 中事件 `7771191/t 2000 2500/f Krobus 3500/w sunny` 使用了 event music key `moonlightJellies`，但它是 Krobus/SeaMonster 海滩事件，不是夏 28 节日逻辑。

用途：只能作为 moonlight jellies 氛围/音乐复用的旁证，不能把该事件流程混入夏 28 节日。

## 2. Project Baseline

### 2.1 Existing Active Festival Framework

当前项目已有以下可复用框架：

- `FestivalRegistry`：已经注册 `summer28`，类型为 `ACTIVE`，日期为夏 28，时间为 2200-2400，location 为 `Beach`，overlay id 为 `Beach-Jellies`，feature key 为 `moonlight_jellies`。
- `FestivalService`：负责 active/passive 日期检测、session lifecycle、overlay lifecycle、start message、open/end、same-day terminal state。
- `FestivalMapOverlayManager`：负责 overlay apply/restore、runtime backup、drops suppress，以及 overlay applied 后通知 active handler。
- `ActiveFestivalHandlers`：当前注册了 Egg Festival、Flower Dance、Luau 三个 active handler；尚未注册 `summer28`。
- `ActiveFestivalConfirmState`：共享 active festival 确认/投票状态。Moonlight Jellies 必须复用它，不新增私有 dialog/vote set。
- `FestivalMusicStatePayload`：可向客户端同步节日 music override。
- `ServerCutsceneTracker` / cutscene command factory：支持手动 cutscene event 和自定义 Java command，例如 `luau_main_event`。
- `ShopRegistry` / `OpenShopScreenPayload`：支持节日商店打开。
- `NpcInteractionService` 与 active handler 集成：Pierre 节日商店、Lewis 主事件和 main event lock 可通过 handler 分发。

### 2.2 Missing Pieces for Summer 28

当前缺口：

- `MoonlightJelliesFestivalService`。
- `ActiveFestivalHandlers` 中的 `summer28` handler 注册。
- `FestivalMapOverlayRegistry` 中 `Beach-Jellies` overlay definition。
- `Beach-Jellies` overlay schematic 和 bounds。
- `Festival_DanceOfTheMoonlightJellies_Pierre` shop definition。
- 月光水母主事件 cutscene JSON 和自定义 command。
- `OpenFestivalConfirmPayload.Action` 中的主事件确认动作。
- 主事件/节日 start/finish/waiting 本地化 key。
- `MUSIC_MOONLIGHT_JELLIES` 音频资源和 payload 常量。
- 水母视觉表现系统或 custom cutscene command。
- 入口/退场/商店/主事件/NPC/水母/相机等 3D 坐标确认。

### 2.3 Existing Data Already Present

已存在：

- `src/main/resources/data/stardewcraft/festival_dialogue_keys.json` 已含 `summer28` dialogue key。
- `assets/stardewcraft/lang/en_us.json` / `zh_cn.json` 已含大量 summer28 节日对话，含 y2/spouse 变体。
- `FestivalRegistry` 已有 `summer28` active definition。
- `src/main/resources/data/stardewcraft/mail/summer_mail.json` 已有 `summer_27` 提醒邮件，但不是原版 `summer_28`，且当前白名单未投递。

已发现但不建议混入本任务的旁枝：

- `zh_cn.json` 中 `stardewcraft.festival.summer28.dialogue.dwarf_y2` 和 `sandy_y2` 仍有英文/typo 残留。可以在实现语言 key 时顺手修，但若用户只确认节日机制，不应顺带大范围清理无关文本。

## 3. 3D Point Policy

### 3.1 Rule

所有 StardewCraft 运行时点位必须是项目世界中的 3D 坐标或 3D 体积：`BlockPos`、`Vec3`、`AABB`、相机 anchor、展示实体路径、粒子/光源 bounds 等。

不得把以下数据直接写入运行时代码或 JSON：

- 原版 `summer28.json` 的 tile 坐标。
- `Beach-Jellies.tmx` 或 `Beach-Jellies2.tmx` 中的 object/tile 坐标。
- 原版 `warpFarmers` 坐标。
- 原版 `specificTemporarySprite` tile 坐标。
- pregen/region/TMX/视觉印象推算出的坐标。

原版坐标只能用于理解布局关系和行为存在性。实际落点必须由用户确认。

### 3.2 Required Confirmations Before Implementation

进入实现前必须确认以下空间数据。

#### Overlay

- `Beach-Jellies` overlay schematic 路径。
- overlay origin。
- restore/apply bounds min/max。
- 是否需要 `Beach-Jellies2` 独立 overlay，或先由同一 overlay 支持年 2+ actor/dialogue 变体。
- overlay 是否包含临时装饰、灯、码头变化、商店区、水面表现。

#### Entry and Exit

- 玩家从普通 Beach 进入节日的检测体积。
- 节日内入口/退场检测体积。
- 进入节日后的安全站位和朝向。
- 未到开放时间/节日已结束时的回退点。
- 玩家主动离开节日时的确认触发区。
- 主事件期间是否禁止离开及对应提示。

#### Shop and Main Event Interaction

- Pierre shop 交互体积。
- Pierre actor 站位和朝向。
- Lewis 主事件触发交互体积。
- Lewis actor 自由阶段站位和朝向。
- 若主事件不是和 Lewis 对话触发，而是特定码头/烛灯船交互触发，需要确认该交互体积。

#### NPC Actor Scene

- 年 1 `Set-Up` 阶段 NPC 站位、朝向和巡逻路线。
- 年 2+ `Set-Up` 阶段 NPC 站位、朝向和巡逻路线。
- 年 1 `MainEvent` 阶段 NPC 站位、朝向、动画反应点。
- 年 2+ `MainEvent` 阶段 NPC 站位、朝向、动画反应点。
- spouse 观礼位置及 spouse support 取舍。
- Leo/Sandy/Dwarf/Wizard/Marlon 等特殊 NPC 是否按当前项目解锁状态出现。

#### Cutscene Staging

- 玩家主事件观礼站位，多人站位序列。
- camera 初始点、镜头移动路径、镜头锁定范围。
- 烛灯船展示实体 spawn 点、移动路径、结束点、朝向。
- 烛灯船光源/粒子范围。
- 月光水母群 spawn 区域、漂移方向、批次时间、可视高度。
- 特殊绿/大型水母 spawn 点和路径。
- 水母光源/粒子半径和数量上限。
- 结束 fade 前相机点和回家后朝向。

## 4. Implementation Strategy

### 4.1 Service Shape

新增 `MoonlightJelliesFestivalService`，整体结构参考 `LuauFestivalService`，但机制更简单：没有投料、评分或奖励分支，核心是夜间会场、NPC 对话/商店、主事件投票和 cutscene 完成。

建议常量：

- `FESTIVAL_ID = "summer28"`
- `OVERLAY_ID = "Beach-Jellies"`
- `SHOP_ID = "Festival_DanceOfTheMoonlightJellies_Pierre"`
- `MAIN_EVENT_CUTSCENE_ID = "moonlight_jellies_main_event"`
- `FESTIVAL_START_MINUTE = 22 * 60`
- `FESTIVAL_END_MINUTE = 24 * 60`
- `RETURN_MINUTE = 24 * 60`
- `MOVEMENT_OWNER = "moonlight_jellies"`
- participant/music/cutscene tags similar to Luau。

主要职责：

- `tick(ServerLevel level)`：
  - 检查节日 session 是否 open/main/ending。
  - 维护参与者状态和边界。
  - 同步音乐状态。
  - 驱动 NPC actors。
  - 主事件阶段检查 cutscene completion。
- `startDebugFestival` / `restoreDebugFestival`：接入 debug 命令。
- `requestDebugNpcs` / `restoreNpcs` / `debugStatus`：与当前 active handler 风格一致。
- `onMapOverlayApplied`：overlay 应用完成后激活 NPC actor。
- `onPlayerLogin` / `onPlayerLogout`：处理重连、music sync、participant tag、未完成 cutscene。
- `handlesConfirmation` / `onPlayerConfirmed`：复用 `ActiveFestivalConfirmState`。
- `tryOpenPierreFestivalShop`：玩家在确认 shop AABB 内且是参与者时打开 shop。
- `tryStartMainEvent`：Lewis/触发区打开开始主事件确认。
- `isMainEventActive` / `isTimeFreezeActive` / `applyTimeFreeze`：接入中央 active handler。
- `finishFestival`：主事件结束后跳 24:00、回家、清状态、恢复 NPC、结束 session。

### 4.2 Active Handler Registration

在 `ActiveFestivalHandlers` 注册 `summer28`。

handler 字段建议：

- display name: `Dance of the Moonlight Jellies`。
- tick: `MoonlightJelliesFestivalService::tick`。
- debug start/restore/status：service 对应函数。
- tick NPC actors：service 对应函数。
- NPC debug start/restore/status/controls：service 对应函数。
- participant check：`MoonlightJelliesFestivalService::isParticipant`。
- login/logout：service 对应函数。
- Pierre shop：`MoonlightJelliesFestivalService::tryOpenPierreFestivalShop`。
- main event lock：`MoonlightJelliesFestivalService::isMainEventActive`。
- start main event：`MoonlightJelliesFestivalService::tryStartMainEvent`。
- time freeze：service 对应函数。

### 4.3 Festival Service Integration

`FestivalService.activeStartMessageKey` 目前只包含 spring13、spring24、summer11。建议补：

- `summer28 -> message.stardewcraft.festival.moonlight_jellies.started`

文案需表达晚上 10 点海滩节日开始。注意原版有当天邮件，不一定有全服广播；StardewCraft 已对其他 active festival 做了开始提示，补 summer28 可保持项目一致性。

结束时间必须是 24:00。若 `FestivalDefinition` 当前 endTime 已为 2400，service 结束时仍需明确调用项目已有时间跳转逻辑，不要套用 Luau 的 22:00 常量。

### 4.4 Confirmation Actions

`OpenFestivalConfirmPayload.Action` 当前有：

- `ENTER`
- `EXIT`
- `START_CONTEST`
- `START_DANCE`
- `LUAU_ADD_SOUP`
- `LUAU_START`
- `END_CONTEST`
- `FESTIVAL_END`

建议新增：

- `MOONLIGHT_JELLIES_START`

对应客户端文案：

- en: `Should we launch the candle-boat?`
- zh: `要放出烛灯船了吗？`

多人等待文案：

- en: `Waiting for other players to begin the Dance of the Moonlight Jellies: %s/%s`
- zh: `正在等待其他玩家开始月光水母起舞：%s/%s`

取消文案：

- en: `The candle-boat can wait a little longer.`
- zh: `烛灯船可以再等一会儿。`

结束 ready gate 可以复用现有 `EXIT` 或 `FESTIVAL_END`，但要保持语义清楚：

- 主动离开节日：`EXIT`。
- 主事件结束后回家：可复用 `FESTIVAL_END`，但现有文案偏 Egg Festival。若复用，需要改成通用文案或新增通用 key。

### 4.5 Overlay

新增 `FestivalMapOverlayRegistry` 定义：

- overlay id: `Beach-Jellies`
- location: `Beach`
- structure: `data/stardewcraft/structures/festivals/moonlight_jellies_beach.schem` 或用户确认名称。
- origin/bounds：等待用户确认。
- backup/restore/drop suppress：与 Luau overlay 一致，默认 true。

年 2+ 的处理选项：

1. 完整复刻：新增 `Beach-Jellies2` overlay/profile，二年目起切换 overlay 和 actor scene。
2. 分阶段：第一阶段只用 `Beach-Jellies` overlay，二年目起仅切换对话/部分 actor route，等用户确认第二套 overlay 后补 `Beach-Jellies2`。

建议：先把文档和代码设计成支持 `LayoutProfile`，实现时可先只启用 y1 profile，避免以后重写 service。

### 4.6 NPC Actors

原版 `summer28.json` 已给出自由阶段和主事件 actor 行为，但 StardewCraft 不能直接使用 tile 坐标。建议建立两层数据：

- `LayoutProfile.Y1`
- `LayoutProfile.Y2_PLUS`

每个 profile 包含：

- setup actors。
- main event actors。
- optional routes。
- dialogue key suffix rule。
- shop actor。
- main event trigger actor。

NPC 对话 key 规则：

- 基础：`stardewcraft.festival.summer28.dialogue.<npc>`。
- 第 2 年及之后优先：`<npc>_y2`。
- spouse 优先：`<npc>_spouse` 或 `<npc>_spouse_y2`，前提是项目已有 spouse/marriage runtime。

出现条件建议：

- 常规 NPC：如果项目中该 NPC 已注册/可生成，节日 actor 可生成。
- Sandy、Dwarf、Wizard、Leo、Marlon 等特殊 NPC：按项目现有解锁/可用规则决定；如果项目没有对应 NPC runtime，先跳过并记录差异。
- Governor 不参与本节日。

NPC route 实现原则：

- 自由阶段 patrol/advancedMove 使用中央 movement service 的 authored route，参考 repo memory 中 festival NPC actor 规则。
- 不逐 waypoint 猜测坐标；每条 route 都等用户确认。
- 主事件 cutscene 中的临时移动由 cutscene command 控制，结束后清理 actor 或恢复自由阶段状态。

### 4.7 Shop

新增 `Festival_DanceOfTheMoonlightJellies_Pierre` 到 `ShopRegistry`。

商品映射待确认：

| 原版商品 | 项目状态 | 计划 |
| --- | --- | --- |
| Moonlight Jellies Banner | 当前搜索未发现等价 item/block | 需要新增 décor 或暂不上架并记录缺口 |
| Cloud Decal `(F)1687` | 当前搜索未发现等价 item/block | 需要新增 décor 或暂不上架并记录缺口 |
| Cloud Decal `(F)1692` | 当前搜索未发现等价 item/block | 需要新增 décor 或暂不上架并记录缺口 |
| Starport Decal | 当前搜索未发现等价 item/block | 需要新增 décor 或暂不上架并记录缺口 |
| Modern Rug | 当前搜索未确认等价 item/block | 需要核对 furniture/decor 系统 |

实现前需要用户决定：

- 是否本阶段补齐这些 furniture/decor 资源。
- 若不补齐，Pierre shop 是空、禁用，还是只显示已存在商品。

不建议用 `luau_totem`、`pastel_banner` 等不相关物品替代原版商品。

### 4.8 Cutscene Event

新增资源：

- `src/main/resources/data/stardewcraft/cutscene_events/moonlight_jellies_main_event.json`

建议内容结构类似：

```json
{
  "id": "moonlight_jellies_main_event",
  "skippable": false,
  "trigger": { "type": "manual" },
  "commands": [
    { "cmd": "moonlight_jellies_main_event", "variant": "auto" }
  ]
}
```

新增 Java command：

- `MoonlightJelliesMainEventCommand`
- 在 `EventCommandFactory` 中注册 `moonlight_jellies_main_event`

命令职责：

- 锁定玩家控制和 HUD。
- 按用户确认站位摆放/隐藏/控制 actor。
- 设置相机。
- 播放 ocean ambience。
- 创建/移动烛灯船。
- 在合适延迟后创建水母群视觉。
- 切换 `MUSIC_MOONLIGHT_JELLIES`。
- 播放 NPC 表情/跳跃/转向/轻微移动。
- 显示结束消息。
- 停止音乐或 silence。
- 通知服务端 cutscene completed，由 service 处理 festivalEnd gate 和 finish。

注意：主事件结束/回家不要只靠客户端 cutscene 自己做，必须回服务端 service 统一处理。

### 4.9 Moonlight Jelly Visual Command

可以把水母视觉拆成独立 command 或内嵌在 `MoonlightJelliesMainEventCommand`。

建议拆出内部 helper：

- `spawnJellyWave(level, profile, viewer)`。
- `JellySpec`：spawn pos、target/motion、delay ticks、duration ticks、scale、light radius、color/type。
- `CandleBoatSpec`：spawn pos、path、duration、light。

设计取舍：

- 若项目已有 display entity cleanup 工具：使用 tag 统一清理。
- 若没有动态光源系统：用发光材质/发光粒子/光源方块临时放置需非常谨慎，必须保证 restore。优先 display/particle，不破坏地图。
- 数量应可控，默认不直接生成 40 个复杂实体给所有玩家造成性能压力。可以按“视觉密度接近原版”分批生成 20-40 个轻量展示/粒子。
- 多人情况下由服务端统一生成可见实体，或每个客户端本地 visual，二者要选择一种，不混用。

### 4.10 Music Sync

自由阶段：

- 进入节日后播放/release 到 beach/ocean ambience，或用 `FestivalMusicStatePayload` 同步 `OCEAN_AMBIENCE`。当前 payload 没有 `OCEAN_AMBIENCE` 常量，但 `MusicCommand` 可解析 `OCEAN_AMBIENCE`。
- 为避免影响普通背景音乐，建议 active festival participant 期间统一由 `FestivalMusicStatePayload` 管理。

主事件：

- 先 silence 或 ocean ambience。
- 水母出现后切换 `MOONLIGHT_JELLIES`。
- 结束时发送 `NONE` 或 `RELEASE`，与 Luau finish 一致。

新增：

- `FestivalMusicStatePayload.MOONLIGHT_JELLIES`。
- 可选 `FestivalMusicStatePayload.OCEAN_AMBIENCE`，如果自由阶段也需要强制同步。

### 4.11 Time and Weather

时间：

- 节日开放窗口：22:00-24:00。
- 参与者进入后冻结主动节日时间，参考 Luau/Flower Dance。
- 主事件结束后跳到 24:00。
- 24:00 不是过夜到下一天；它仍处于夏 28 的 24:00/午夜状态，后续 2:00 pass out/睡觉系统按项目时间规则处理。

天气：

- 原版 active festival day 会把天气显示为 `Festival`。
- 若 StardewCraft 当前天气系统已有 festival day hook，确认 `summer28` 注册后是否自动覆盖。
- 不要为 Moonlight Jellies 单独写一套 weather exception，除非发现现有 `FestivalService` 没有覆盖 active festival day。

### 4.12 Mail

建议新增或修正：

- 添加 `summer_28` mail entry，文本按原版 Demetrius 当天提醒。
- 加入 `IMPLEMENTED_DATE_TRIGGERED_MAIL` 白名单，确保夏 28 当天可投递。

待用户决定：

- 是否保留现有 `summer_27` 前一天提醒。
- 若保留，它应作为项目扩展，不替代原版 `summer_28`。

### 4.13 Localization

新增/确认 key：

- `message.stardewcraft.festival.moonlight_jellies.setup`
- `message.stardewcraft.festival.moonlight_jellies.unavailable`
- `message.stardewcraft.festival.moonlight_jellies.started`
- `message.stardewcraft.festival.moonlight_jellies.start_confirm`
- `message.stardewcraft.festival.moonlight_jellies.start_vote_waiting`
- `message.stardewcraft.festival.moonlight_jellies.start_cancelled`
- `message.stardewcraft.festival.moonlight_jellies.finished`
- `event.moonlight_jellies.message.end`
- `stardewcraft.shop.moonlight_jellies.dialogue`
- `stardewcraft.jukebox.moonlightjellies`

现有 summer28 NPC 对话大体已在 lang 中，但实现前应做 JSON parse 校验。中文里含英文残留的 y2 对话可列入修正清单。

## 5. File-Level Plan

### 5.1 Java Files to Add

| 文件 | 用途 |
| --- | --- |
| `src/main/java/com/stardew/craft/festival/MoonlightJelliesFestivalService.java` | active festival runtime、参与者、确认、NPC、商店、主事件、结束 |
| `src/main/java/com/stardew/craft/cutscene/command/MoonlightJelliesMainEventCommand.java` | 主事件 cutscene 表现 |
| 可选 `src/main/java/com/stardew/craft/cutscene/command/MoonlightJelliesVisualCommand.java` | 若水母视觉独立成命令 |

### 5.2 Java Files to Modify

| 文件 | 修改点 |
| --- | --- |
| `ActiveFestivalHandlers.java` | 注册 `summer28` handler |
| `FestivalMapOverlayRegistry.java` | 注册 `Beach-Jellies` overlay，等待坐标确认 |
| `FestivalService.java` | 补 active start message key，核对结束/terminal 行为 |
| `OpenFestivalConfirmPayload.java` | 新增 `MOONLIGHT_JELLIES_START` action 和客户端文案映射 |
| `FestivalConfirmPayload` 使用方 | 确认新增 action 可往服务端分发 |
| `FestivalMusicStatePayload.java` | 新增 `MOONLIGHT_JELLIES`，可选 `OCEAN_AMBIENCE` |
| `EventCommandFactory.java` | 注册 `moonlight_jellies_main_event` |
| `ModSounds.java` | 注册 `MUSIC_MOONLIGHT_JELLIES` |
| `JukeboxTrackRegistry.java` | 可选添加 `moonlightjellies` 曲目 |
| `ShopRegistry.java` | 新增 `Festival_DanceOfTheMoonlightJellies_Pierre` |
| `StardewTimeManager.java` | 若补原版 mail，则加入 `summer_28` 白名单 |
| `NpcInteractionService` | 仅当现有 active handler 分发无法覆盖 Lewis/Pierre 时修改；优先不改 |

### 5.3 Resource Files to Add or Modify

| 文件 | 用途 |
| --- | --- |
| `src/main/resources/data/stardewcraft/cutscene_events/moonlight_jellies_main_event.json` | 手动 cutscene event |
| `src/main/resources/assets/stardewcraft/sounds.json` | 添加 `music_moonlight_jellies` |
| `src/main/resources/assets/stardewcraft/sounds/music/moonlight_jellies.ogg` | 主事件音乐资源 |
| `src/main/resources/assets/stardewcraft/lang/en_us.json` | 新增 message/event/shop/jukebox key |
| `src/main/resources/assets/stardewcraft/lang/zh_cn.json` | 新增中文 key，并可修 summer28 y2 残留 |
| `src/main/resources/data/stardewcraft/mail/summer_mail.json` | 如按原版补 `summer_28` |
| `src/main/resources/data/stardewcraft/structures/festivals/moonlight_jellies_beach.schem` | overlay schematic，等用户确认 |
| décor/furniture model/block/item/lang/loot files | 如果本阶段补 Pierre 商店商品资源 |

## 6. Phased Implementation Plan

### Phase 0: Confirmation Gate

目标：实现前确认所有会影响运行时代码的空间与资源取舍。

输入需要用户确认：

- overlay origin/bounds/schematic。
- 是否支持 `Beach-Jellies2` 独立 overlay。
- 入口/退场/商店/主事件 AABB。
- NPC setup/main event 点位和路线。
- 玩家多人观礼点。
- 烛灯船和水母视觉点位/路径。
- 商店 décor/furniture 是否补资源。
- 原版 `summer_28` mail 是否补入并启用。

输出：

- 用户确认后的坐标表。
- 商品资源取舍表。
- 是否分阶段支持 y2 的决定。

### Phase 1: Skeleton Service and Handler

目标：让 `summer28` 成为可 debug/open/close 的 active festival skeleton，但不启动主事件。

工作：

- 新增 `MoonlightJelliesFestivalService` 基础状态。
- 注册 active handler。
- 接入 entry/exit confirm。
- 接入 participant tag、time freeze、login/logout cleanup。
- 接入 active start message。
- 接入 overlay applied callback，但如果 overlay 坐标未确认则保持 fail-closed。

验收：

- 夏 28 22:00 才允许进入。
- 进入后时间冻结。
- 主动离开弹确认，多人等待投票。
- 同日结束后不能重新进入。
- `./gradlew classes` 通过。

### Phase 2: Overlay and Free-Phase NPCs

目标：应用 `Beach-Jellies` overlay，并在会场生成自由阶段 NPC。

工作：

- 注册 overlay definition。
- 准备 schematic。
- 激活 NPC actors。
- 接入 `festival_dialogue_keys` 的 summer28 对话。
- 实现 y1/y2 dialogue suffix。
- 实现确认过的自由阶段 routes。

验收：

- overlay 应用/恢复可靠。
- 服务器重启/restore 后不留下节日方块。
- NPC 对话正确，普通 gift/quest/shop 被节日锁拦截。
- NPC restore 后回普通日程。

### Phase 3: Pierre Shop

目标：复刻月光水母节 Pierre 商店。

工作：

- 注册 `Festival_DanceOfTheMoonlightJellies_Pierre`。
- 按资源取舍上架商品。
- Pierre shop AABB 接入 service。
- 添加 shop dialogue key。

验收：

- 只有节日参与者在确认区域内能打开商店。
- 商品价格与原版一致。
- 缺失资源不出现假商品。
- shop UI 买卖行为正常。

### Phase 4: Music and Main Event Confirm

目标：Lewis 交互可发起主事件投票，主事件开始后锁定参与者并同步音乐。

工作：

- 新增 `MOONLIGHT_JELLIES_START` confirmation action。
- 新增 start/wait/cancel 文案。
- 新增 `MUSIC_MOONLIGHT_JELLIES` 与 payload 常量。
- Lewis/主事件触发区接入 `tryStartMainEvent`。

验收：

- 单人确认后进入主事件。
- 多人全部确认后进入主事件，取消后继续自由阶段。
- 主事件期间普通交互和离开行为按设计锁定。
- 音乐可切入/释放。

### Phase 5: Cutscene and Visuals

目标：实现烛灯船 + 月光水母主事件表现。

工作：

- 新增 cutscene JSON。
- 新增 `MoonlightJelliesMainEventCommand`。
- 实现 actor staging。
- 实现 camera staging。
- 实现 candle boat 展示和移动。
- 实现 jelly wave 展示。
- 显示原版结束消息。
- cutscene 完成后通知 service。

验收：

- 主事件流程接近原版节奏。
- 烛灯船和水母可见、移动、不会残留实体。
- 多人玩家看到一致状态或合理的本地 cutscene。
- cutscene 中断/玩家掉线后能清理。

### Phase 6: Finish, Mail, Polish

目标：补齐结束、邮件和资源细节。

工作：

- 主事件结束 gate。
- 跳到 24:00。
- 回农场。
- 清理 overlay/NPC/music/participant state。
- 补 `summer_28` mail 或记录取舍。
- 修正必要的 zh_cn 文本残留。
- 可选添加 jukebox track。

验收：

- 主事件后时间为 24:00。
- 玩家回农场安全点。
- 节日 session terminal，不能重复进入。
- 第二天地图/NPC/音乐正常。

## 7. Risk Register

| 风险 | 影响 | 处理 |
| --- | --- | --- |
| 坐标未确认 | 不能实现 overlay、NPC、商店、主事件 | fail-closed，只写计划和待确认清单 |
| 水母视觉资源缺失 | 主事件表现不足 | 先用确认过的 display/particle 方案；不使用假贴图 |
| 动态光源不可用 | 水母发光效果弱 | 用发光材质/粒子替代，或等待光源系统支持 |
| 商店家具资源缺失 | Pierre shop 不能完整复刻 | 补资源或不上架缺失商品，不做替代商品 |
| 多人 cutscene 不同步 | 主事件完成/回家状态错乱 | 由服务端 service 统一追踪 participants 和 cutscene done |
| 玩家中途掉线 | state 残留或无法完成 gate | login/logout 处理，online participants snapshot 语义参考 Luau |
| y2 overlay 不确定 | 二年目体验差异缺失 | service 设计 profile，第一阶段可只启用 y1 |
| 邮件与项目现有 `summer_27` 冲突 | 提醒时间不一致 | 明确保留/替换/并存策略 |
| 结束时间误用 22:00 | 与原版严重不符 | `summer28` finish 必须跳 24:00 |

## 8. Validation Plan

### 8.1 Static Validation

- `./gradlew classes`
- JSON parse：
  - `assets/stardewcraft/lang/en_us.json`
  - `assets/stardewcraft/lang/zh_cn.json`
  - `data/stardewcraft/cutscene_events/moonlight_jellies_main_event.json`
  - mail JSON
- 检查 `sounds.json` 能被 Minecraft 资源加载。

### 8.2 Manual Debug Checks

- 用 festival debug 启动 `summer28`。
- 检查 overlay apply。
- 检查 NPC actor 生成、对话、restore。
- 检查 Pierre shop。
- 检查 Lewis start prompt。
- 检查单人主事件完整播放。
- 检查多人 start gate 和 finish gate。
- 检查玩家掉线/重连。
- 检查节日结束后 24:00 回农场。
- 检查第二天 overlay 已恢复、NPC 归位、音乐释放。

### 8.3 Negative Checks

- 非夏 28 不能进入。
- 夏 28 22:00 前不能进入。
- 夏 28 24:00 后不能进入。
- 节日已结束后不能重新进入。
- 非参与者不能打开 Pierre 节日商店。
- 主事件进行中不能乱触发普通 NPC 交互。
- 缺 overlay 坐标/资源时不启动半成品节日。

## 9. User Confirmation Checklist

实现前请确认以下事项。

### 9.1 Required

- [ ] `Beach-Jellies` overlay origin。
- [ ] `Beach-Jellies` overlay bounds。
- [ ] overlay schematic 文件名和生成方式。
- [ ] 是否做 `Beach-Jellies2` 独立 overlay。
- [ ] 入口/退场 AABB。
- [ ] 玩家进入节日后的站位和朝向。
- [ ] 主动离开回退点。
- [ ] Pierre shop AABB。
- [ ] Lewis 主事件触发 AABB。
- [ ] 年 1 setup NPC 站位/路线。
- [ ] 年 2+ setup NPC 站位/路线，或确认后置。
- [ ] 主事件玩家观礼点，多人顺序。
- [ ] 主事件 NPC 站位/路线。
- [ ] 烛灯船 spawn/path。
- [ ] 水母群 spawn area/path/height。
- [ ] 特殊水母表现点位。
- [ ] cutscene camera anchors/path。
- [ ] 节日结束回家使用项目既有 farm return，还是指定点。

### 9.2 Resource Decisions

- [ ] 是否迁移/制作 `moonlight_jellies.ogg`。
- [ ] 是否新增 Moonlight Jellies Banner。
- [ ] 是否新增 Cloud Decal 两个变体。
- [ ] 是否新增 Starport Decal。
- [ ] 是否新增 Modern Rug。
- [ ] 若 furniture 不补齐，Pierre shop 如何处理。
- [ ] 是否补原版 `summer_28` Demetrius mail。
- [ ] 是否保留项目已有 `summer_27` 前一天提醒。
- [ ] 是否修复 `zh_cn` 中 summer28 y2 英文残留。

### 9.3 Implementation Order Decision

推荐顺序：

1. Skeleton service + handler。
2. Overlay + entry/exit。
3. NPC dialogue/free phase。
4. Pierre shop。
5. Music + main event confirm。
6. Cutscene visuals。
7. Mail/polish/y2。

如果用户希望最快看到主事件，可调整为：先确认一个最小观礼区 + 烛灯船 + 水母区域，做无 NPC 完整主事件，再补自由阶段 NPC。但这会降低第一版 parity，不作为默认推荐。

## 10. Open Questions

1. 是否将 `Beach-Jellies2` 纳入首批实现？
2. 月光水母视觉使用 display entity、粒子、模型实体，还是项目已有的其它视觉系统？
3. Pierre 商店家具资源是否本阶段补齐？
4. 是否按原版新增 `summer_28` 当天 Demetrius mail，并保留现有 `summer_27`？
5. 主事件结束后是否完全走已有 farm return helper，还是为多人 porch spot 做更贴近原版的细化？
6. 特殊 NPC 的出现条件按原版全员出现，还是按 StardewCraft 当前 NPC/解锁进度过滤？
7. y2/spouse 对话是否本阶段接入，还是先只接基础对话？

## 11. Current Status

已完成：

- 原版 `summer28` festival JSON 调研。
- 原版中文 festival JSON 调研。
- 原版 `Event.cs` festival load/end/multiplayer/source sprite 调研。
- 原版 weather/festival day 调研。
- 原版 shop/jukebox/mail 调研。
- StardewCraft active festival framework 调研。
- Luau active festival pattern 对照。

未开始：

- 任何 Java 实现。
- 任何 JSON/resource 实现。
- 任何 overlay/schematic 生成。
- 任何坐标写入。

下一步：等待用户确认本计划、坐标策略和资源取舍后，再进入 Phase 1。