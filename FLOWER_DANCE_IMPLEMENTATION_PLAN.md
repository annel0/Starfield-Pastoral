# Flower Dance Implementation Plan

本文档记录 StardewCraft 花舞节复刻规划。当前阶段只做规划，不进入代码实现。目标是在没有专门模型动画预算的前提下，尽量保留原版 Flower Dance 的核心体验：春 24 森林会场、寻找舞伴、玩家与玩家互邀、NPC 接受/拒绝、Pierre 节日商店、群舞仪式、音乐与结束回家。

## 1. 当前实现前提

### 1.1 已有地基

- `FestivalRegistry` 已登记 `spring24`，类型为主动节日，地点 `Forest`，时间 `900-1400`，overlay id `Forest-FlowerFestival`，mechanic id `flower_dance`。
- `FestivalService`、`FestivalWorldData`、`FestivalMapOverlayManager`、`FestivalSystem` 已能支撑主动节日 session、地图 overlay、时间冻结、恢复和 debug 生命周期。
- Egg Festival 已经提供主动节日样板：入场确认、NPC actor 接管、节日对白、商店、cutscene、ready gate、结束回家。
- `festival_dialogue_keys.json` 和语言文件已经迁入 `spring24` 会场 NPC 对话。
- 可婚 NPC 的花舞邀请接受/拒绝文本已存在于语言文件中，key 形如 `stardewcraft.npc.<npc>.flowerdance_accept`、`flowerdance_accept_spouse`、`flowerdance_decline`。
- NPC 好感系统已经存在，可以读取和增加玩家对 NPC 的 friendship points。

### 1.2 当前缺口

- 已注册 `Forest-FlowerFestival` overlay。
- 已按原版 `FlowerDance=0000012d` 迁入并注册 `music_flower_dance`；主舞 cutscene 使用专用曲目。
- `FlowerDanceService`、花舞 NPC actor service、NPC 舞伴状态、NPC 邀舞确认、入场和 Pierre 商店首版已实现。
- 群舞 cutscene 首版已实现。
- StardewCraft 森林会场入口、商店点、自由阶段 NPC 点位和 3x6 舞蹈队列规则已确认；离场触发/会场边界是下一步允许补齐的重点，但坐标必须由用户确认后才能写入代码。
- 项目暂无完整婚姻/roommate 系统；配偶分支需要预留，首版默认按未婚处理，除非先补婚姻状态。

## 2. 原版事实基线

### 2.1 基础数据

- 原版 id：`spring24`。
- 名称：Flower Dance / 花舞节。
- 条件：`Forest/900 1400`。
- 地图：第一年 `Forest-FlowerFestival`，第二年 `Forest-FlowerFestival2`。
- set-up：玩家入场后加载 `Set-Up` actors，自由阶段为 `playerControl flowerFestival`。
- 主事件：与 Lewis 对话确认后加载 `mainEvent`，播放 `FlowerDance` 音乐，展开男女组群舞，最后 `waitForOtherPlayers festivalEnd/end`。
- 结束：原版 active festival 结束后推进到晚上，普通花舞节为 22:00。

### 2.2 舞伴邀请

原版源码关键语义：

- 玩家只能有一个 `dancePartner`。
- NPC 已被其他在线玩家邀请成功时，后续邀请者会得到“已经有舞伴”的回应。
- 邀请普通可婚 NPC 成功条件：NPC 可跳舞、未婚、未被占用、玩家对该 NPC friendship points `>= 1000`。
- 邀请成功后，对该 NPC 增加 `+250` friendship points。
- 邀请失败时优先使用 NPC 的 `FlowerDance_Decline` / 项目对应 `flowerdance_decline` 文本。
- 配偶/roommate 在原版中可直接接受，并使用 `FlowerDance_Accept_Spouse` 或 roommate 文本；StardewCraft 首版先预留接口。
- 多人玩家之间可以相互邀请跳舞，原版通过 proposal 系统同步双方选择。

### 2.3 原版默认配对

如果玩家没有占用某个默认舞伴，原版会用默认 6 对可婚 NPC 填满队列：

- 女组：Abigail、Penny、Leah、Maru、Haley、Emily。
- 男组：Sebastian、Sam、Elliott、Harvey、Alex、Shane。
- 默认配对来自 `Utility.getLoveInterest(female)`，玩家对会优先排在舞阵内侧，再把剩余 NPC 对填满。

StardewCraft 复刻时保留这套配对语义，但实际世界坐标不能从原版 tile 推算，必须由用户确认舞蹈站位。

### 2.4 商店

原版商店 id：`Festival_FlowerDance_Pierre`。

原版商品：

| 原版 id | 价格 | 库存 | 说明 |
| --- | ---: | ---: | --- |
| `(BC)48` | 350 | 无限 | 季节性装饰相关 |
| `(BC)192` | 350 | 无限 | 季节性装饰相关 |
| `(BC)204` | 350 | 无限 | 季节性装饰相关 |
| `(BC)108` | 250 | 无限 | 一簇鲜花 |
| `(BC)108 (Recipe)` | 1000 | 无限 | 一簇鲜花配方 |
| `(O)18` | 50 | 10 | 水仙花 |
| `(O)22` | 50 | 10 | 蒲公英 |
| `(BC)137` | 2500 | 1 | 稀有限购装饰 |
| `(F)1973` | 800 | 无限 | 季节性盆栽/家具 |
| `(F)1974` | 800 | 无限 | 季节性盆栽/家具 |
| `(F)1820` | 400 | 无限 | 季节性盆栽/家具 |
| `(F)1821` | 400 | 无限 | 季节性盆栽/家具 |

首版商品必须按项目现有物品能力映射；不能用假物品占位。

## 3. 核心取舍：没有模型动画预算时怎么跳舞

### 3.1 不做的内容

- 不新做每个 NPC 的花舞节专用骨骼动画。
- 不要求 12 个可婚 NPC、玩家模型和普通 NPC 都拥有完整 SDV 舞步动画。
- 不用临时粗糙模型动画充数。
- 不把舞蹈 cutscene 做成大量一次性硬编码的模型帧动画。

### 3.2 可接受表现层级

花舞节可以分三档表现，按预算和时间逐步推进。

#### A. 低预算正式版：站位 + 朝向 + 音乐 + 简短位移

这是推荐首版。

表现方式：

- 所有舞者进入确认好的双排或环形站位。
- 舞伴互相面对，Lewis 主持后退场或站侧边。
- 播放 `FlowerDance` 音乐。
- 每组舞者做非常小的服务端控制动作：转向、向前/向后半步、左右错步、短暂停顿。
- 男组可在原版 `positionOffset Guys 0 -2` 语义上转成轻微跳跃或抬步，但只使用实体移动/跳跃，不做模型动画。
- 女组可用朝向切换和半步移动表达舞步，不做专用手臂/裙摆动画。
- 镜头采用固定观礼镜头或轻微移动，避免玩家看到每个动作的粗糙处。
- 结束前淡出，回到黑屏消息与回家流程。

优点：

- 不需要新动画资产。
- 玩家能看见自己和舞伴参与仪式。
- 服务端权威，易同步。
- 比全程黑屏更有节日感。

风险：

- 动作不会像原版真正跳舞，视觉更像集体仪式队形。
- 需要确认舞阵坐标，否则无法开始。

#### B. 镜头遮羞版：开场站位 + 黑屏/切屏舞蹈 + 结束站位

这是保底方案。

表现方式：

- 玩家选好舞伴后，所有人入场站成队列。
- 镜头展示舞伴配对和会场。
- Lewis 宣布开始后淡黑，播放 `FlowerDance` 音乐片段。
- 黑屏期间服务端保持玩家锁定和 ready gate。
- 音乐播放一段时间后淡入，所有舞者回到结束队形。
- 弹原版结束消息并回家。

优点：

- 完全避开模型动画短板。
- 实现和多人同步风险最低。

风险：

- 玩家会明显感觉“舞蹈被跳过”。
- 如果只黑屏太久，体验会空；建议只作为早期调试或动画不可用时的 fallback。

#### C. 中预算增强版：静态模型 + 轻量舞蹈状态机

这是后续增强。

表现方式：

- 在 A 档基础上，给 actor 增加几个通用姿势或手持花束、花环效果。
- 不为每个 NPC 做独立舞蹈，只做 2-3 个通用动作状态。
- 用粒子、花瓣、音效和镜头补氛围。

优点：

- 视觉提升明显。
- 不需要完整逐角色动画。

风险：

- 仍然需要美术和渲染排期。

### 3.3 推荐结论

首版采用 A 档，并保留 B 档 fallback。

也就是说：不是所有人全程面对面傻站，也不是直接整段黑屏。我们先做“能被服务端同步的低预算编舞”：站位、面向、半步移动、跳跃、淡入淡出、音乐。若某个 actor 无法稳定执行动作，单个阶段可以切黑屏兜底。

## 4. 多人玩家互邀设计

### 4.1 舞伴状态模型

服务端新增 `FlowerDancePartner` 概念，可表达三种状态：

- `none`：未选择舞伴。
- `npc:<npcId>`：选择 NPC 舞伴。
- `player:<uuid>`：选择另一个玩家作为舞伴。

状态保存位置：

- 当日运行时放在 `FlowerDanceService` 或 `FestivalSessionState` 的花舞子状态中。
- 只需要当天持久化到 session；新日必须清除。
- 服务器重启后，如果 session 仍为 OPEN/MAIN_EVENT，应能恢复舞伴选择。

约束：

- A 选择 B 后，B 也必须同步为 A。
- 任意一方已存在舞伴时，新的邀请失败。
- NPC 同一时间只能被一个玩家占用。
- 玩家断线时，如果对方舞伴是该玩家，按原版语义清空对方舞伴。
- 主舞蹈开始后锁定舞伴，不再允许邀请或取消。

### 4.2 NPC 邀请流程

玩家右键可跳舞 NPC：

1. 如果玩家已有舞伴，只显示普通节日对白或“已经有舞伴”状态，不再弹邀请。
2. 如果 NPC 已被别人邀请并同意，后续玩家不能再邀请成功；已看过该 NPC 花舞节对白后再次互动时，不再打开邀请确认，直接显示该 NPC 的 `flowerdance_taken` 文案。原版源码在 `who.HasPartnerForDance` 分支使用全局 “I'm sorry... I already have a partner.”，StardewCraft 为了可读性保留每个可跳舞 NPC 的独立占用文案。
3. 如果 NPC 不可跳舞，只显示普通节日对白。
4. 如果 NPC 是可婚 NPC，读取玩家对 NPC friendship points。
5. 首次互动显示普通花舞节对话；同一会场内第二次互动弹“邀请某某作你的舞伴？”确认。
6. 确认后若 `points >= 1000`，设置舞伴，增加 `+250` friendship points，显示 `flowerdance_accept`。
7. 若不足 1000，显示 `flowerdance_decline`。

首版暂不接 spouse/roommate 判定；后续婚姻系统完成后再接 `flowerdance_accept_spouse`。

当前实现状态：NPC 邀请首版已实现，包含独立 S2C/C2S payload、当日舞伴状态、NPC 占用检查、1000 好感阈值、成功 +250 好感和接受/拒绝文本；NPC 被其他玩家占用后使用每人不同的 `flowerdance_taken` 文案。

### 4.3 玩家互邀流程

玩家右键另一个节日参与玩家，或通过靠近交互：

1. 发起者选择“邀请跳舞”。
2. 服务端检查双方都在同一个 `spring24` session，双方都未锁定舞伴，主事件尚未开始。
3. 被邀请玩家收到 Yes/No 对话。
4. 接受：双方互设舞伴，广播短提示。
5. 拒绝：只提示发起者，不影响任何状态。
6. 邀请 pending 时，如果任一方离开、断线、已有舞伴、主事件开始，则邀请自动失效。

需要新增 payload：

- S2C 打开玩家舞蹈邀请确认。
- C2S 回答玩家舞蹈邀请。
- S2C 同步当前玩家自己的舞伴状态。

可以复用项目现有确认屏幕样式，但语义上不要塞进 Egg Festival 的 `OpenFestivalConfirmPayload.Action`，应独立或扩展成通用 proposal payload。

当前实现状态：玩家互邀首版已实现。发起者右键目标玩家后先确认是否邀请；确认后目标玩家收到邀请确认；接受时双方互设舞伴，拒绝/过期/登出会清理 pending 状态。尚未做独立的客户端舞伴状态 HUD 同步。

NPC 邀请不需要额外坐标：进入会场后先消耗该 NPC 的节日对白，再次与可邀请对象互动时打开邀请确认。

### 4.4 多人舞阵排布

原版配对语义保留，MC 站位使用用户确认的三行六对舞台：

- 先按原版源码收集所有在线参与者的舞伴。
- 玩家对、玩家 + NPC 对按原版优先排入舞阵。
- 剩余默认 NPC 对按原版 `Abigail/Penny/Leah/Maru/Haley/Emily` 与对应 love interest 补齐。
- 舞台容量为 18 对：三行，每行六对。
- 行 1：S 侧 `z=114`，N 侧 `z=116`；X 依次为 `-241, -239, -237, -235, -233, -231`。
- 行 2：S 侧 `z=119`，N 侧 `z=121`；X 依次为 `-241, -239, -237, -235, -233, -231`。
- 行 3：S 侧 `z=124`，N 侧 `z=126`；X 依次为 `-241, -239, -237, -235, -233, -231`。
- NPC 舞者按性别站位：女 NPC 站 S-facing 位置，男 NPC 站 N-facing 位置。
- 超过 18 对舞伴时，18 对之后不显示。
- 没有舞伴的玩家，以及默认舞伴被玩家/多人配对拆开的原舞者 NPC，直接分配进观众席；观众席满后也不显示。

这相当于把原版 `warp Girls/Guys` 的“按源代码配对排序再横向展开”语义，适配到 StardewCraft 已确认的 3x6 MC 物理舞台。超过当前 18 对 / 10 观看席容量的扩展点位，后续必须由用户重新确认，不能从原版 tile 推算。

## 5. NPC 和会场规划

### 5.1 Actor 范围

会场 NPC 应先按原版 `spring24` 对话 key 覆盖以下角色：

- 常规会场：Abigail、Robin、Demetrius、Maru、Sebastian、Linus、Pierre、Caroline、Alex、George、Evelyn、Lewis、Clint、Penny、Pam、Emily、Haley、Jodi、Kent、Marlon、Sam、Leah、Shane、Marnie、Elliott、Gus、Harvey、Jas、Vincent、Willy、Leo。
- 特殊/非会场或条件角色：Dwarf、Wizard、Sandy 可先只保留文本，不放入会场，除非用户确认点位。
- Leo 是否出现需要以后按 `leoMoved` 类似状态接入；首版可暂不出现。

### 5.2 NPC 行为

首版只做两类行为：

- 静态站位：站在用户确认坐标，面向固定方向。
- 简单巡逻：少数原版 `advancedMove` 明确移动的 NPC 可用用户确认路线做来回/环线。

花舞节第一批不用追求原版 set-up 中所有 `advancedMove` 的停顿和朝向 token，但文档中要记录差异。

当前实现沿用复活节 NPC actor 模式：由 `FlowerDanceNpcService` 提交 authored movement，中央日常 NPC 调度在花舞节控制 NPC 时让路，避免日常路线抢回 NPC。Emily、Gus 为两点来回，Haley 按用户点位原路返回，Vincent 按五点连续环线，Jas 按 N/E/W/S 持续转向。

### 5.3 需要用户确认的点位

以下点位不得从 TMX、pregen、视觉印象或原版 tile 推算：

- Forest 普通 base/festival overlay origin。
- Forest-FlowerFestival overlay bounds。
- 玩家进入会场触发区域。
- 玩家入场安全点和朝向。
- 玩家离开/结束回家前的安全处理点。
- Pierre 商店站位、朝向、shopBounds。
- Lewis 自由阶段站位、主事件主持站位。
- 每个会场 NPC 的 setupPosition、setupFacing、interactionBounds。
- 所有默认舞者的舞阵站位。
- 玩家舞者插入点位和多人扩展点位。
- cutscene 镜头位置。

缺任何必要点位时，对应阶段必须拒绝启动。

## 6. 资产规划

### 6.1 已有 tmp_models 资产

`tmp_models` 当前有：

- `一簇鲜花.geo.json`
- `一簇鲜花.png`
- `季节性装饰.geo.json`
- `季节性装饰.png`

规划：

- 将其迁入正式资源路径，命名使用英文 snake_case，例如 `flower_cluster`、`seasonal_decor`。
- geo 模型用于世界方块/装饰显示。
- 因 geo 模型不能自动生成对应方块模型，物品图标必须单独制作。

### 6.2 物品图标切图规范

用户要求：从原版里面截出来，居中放在刚好能容纳原版图标的正方形透明 PNG 里，防止形变。

执行规范：

- 不从模型贴图直接拉伸生成 item icon。
- 从原版 Content 资产中定位真实物品/家具图标。
- 按源码或数据确认 source rect；不要目测裁剪。
- 裁剪后计算非透明像素 bbox。
- 输出为正方形透明 PNG，边长取能容纳原图标的最小合理尺寸，图标居中。
- 不缩放、不拉伸原始图标；如必须缩放，必须记录原因并单独确认。
- 写入后用脚本校验 PNG 尺寸、透明边距和 JSON 引用。

候选输出路径：

- `src/main/resources/assets/stardewcraft/textures/item/festival/flower_cluster.png`
- `src/main/resources/assets/stardewcraft/textures/item/festival/seasonal_decor.png`
- `src/main/resources/assets/stardewcraft/models/item/flower_cluster.json`
- `src/main/resources/assets/stardewcraft/models/item/seasonal_decor.json`

### 6.3 原版资产来源待核对

需要后续逐项核对：

- `(BC)108` 一簇鲜花的原版图标来源和 source rect。
- `(BC)48`、`(BC)192`、`(BC)204` 的季节性装饰图标来源和 source rect。
- `(BC)137`、`(F)1973`、`(F)1974`、`(F)1820`、`(F)1821` 是否已有项目可用物品或是否本阶段跳过。
- `FlowerDance` 音乐已核对为 `源文件/音频文件/0000012d.wav`，并转换到 `assets/stardewcraft/sounds/music/flower_dance.ogg`。

## 7. 花舞节实现分期

### Phase 0：规划与点位表

产出：

- 本规划文档。
- `FLOWER_DANCE_REQUIREMENTS.md` 或本文件后续章节中的点位表。
- 原版 source ledger：数据、源码、商店、音频、资产 source rect。

验收：

- 没有任何未确认坐标进入代码。
- 明确首版舞蹈表现选择 A 档，并保留 B 档 fallback。

### Phase 1：会场与自由阶段

实现内容：

- 注册 `Forest-FlowerFestival` overlay。
- 新增 `FlowerDanceService` tick 入口。
- 入场确认、时间冻结、HUD 隐藏、离场确认、结束 22:00 回家。离场确认需要等用户提供 `venue_bounds` / `exit_trigger` 点位后补齐，不能复用 overlay bounds 推算。
- 新增 `FlowerDanceNpcService`，只做 NPC actor 站位/简单路线/对白。
- Pierre 花舞节商店。
- 会场音乐或环境音乐。

验收：

- 春 24、9:00-14:00 可进入会场。
- 会场 NPC 能显示正确 `spring24` / `_y2` 对话。
- 商店可打开且库存不含假物品。
- 退出和过夜能恢复地图与 NPC。

### Phase 2：舞伴邀请

实现内容：

- `FlowerDancePartner` 状态。
- NPC 邀请：1000 友情阈值、+250 友情、已被占用、拒绝文本。（已实现首版）
- 玩家互邀：pending proposal、接受/拒绝、断线/超时取消。（已实现首版）
- 舞伴状态 debug status。（舞伴计数和 pending 玩家邀请计数已实现；面向客户端的舞伴状态 HUD 同步后置）

验收：

- 玩家不能重复拥有多个舞伴。
- 两个玩家不能抢同一个 NPC。
- 好感不足 NPC 会拒绝。
- 好感足够 NPC 接受并加友情。
- 两个玩家可以互邀并成为一对。

### Phase 3：主舞蹈 cutscene

当前实现状态：首版已接入。右键 Lewis 后打开开始确认；确认后启动 `flower_dance_main_event`，使用已确认摄像头 `(-225.957,68.369,129.878,yaw=138,pitch=41.5)`，按服务端生成的舞伴队列生成客户端 cutscene 演员，执行 3x6 舞阵和轻量摆步/转向/起伏动画，播放专用 `FlowerDance` 曲目；完成后显示原版结束文本并回家。

实现内容：

- Lewis 主持开始确认。
- 服务端生成舞阵：玩家对优先，默认 NPC 对补齐。
- 低预算编舞：站位、面对、半步移动、轻量起伏、镜头、临时音乐、淡出。
- `festivalEnd` ready gate。
- 结束消息、22:00 回家、清理舞伴状态。

验收：

- 单人 + NPC、玩家 + 玩家、多人混合都能进入主舞蹈。
- 舞蹈开始后不允许修改舞伴。
- 断线不破坏 session。
- 黑屏 fallback 可手动或异常触发。

### Phase 4：资产补齐与 polish

实现内容：

- 迁入 `一簇鲜花`、`季节性装饰` geo 与贴图。
- 制作原版居中正方形透明 item icon。
- 注册缺失音乐。
- 如资源允许，增加花瓣粒子、轻量镜头移动、花束/装饰展示。

验收：

- 所有 JSON 可解析。
- 物品图标不形变、不拉伸。
- `gradle classes` 通过。

## 8. 首版不做或后置

- 不做完整角色舞蹈骨骼动画。
- 不做婚姻/roommate 特判，除非先完成婚姻状态系统。
- 不做第二年地图/站位精确差异；本轮明确不补第二年内容。
- 不放置 Dwarf、Wizard、Sandy 等非会场角色，除非用户确认点位。
- 不做所有家具/装饰物品完整补齐；缺正式物品时从商店中暂不放入。
- 不根据原版 tile 自动推导任何 StardewCraft 坐标。

## 9. 下一批需要用户确认

为进入 Phase 1，至少需要：

1. `Forest-FlowerFestival` overlay schem 文件或制作路径。
2. overlay origin、bounds。
3. 玩家进入森林会场触发区域。
4. 玩家入场点和朝向。
5. Pierre 商店点、朝向、shopBounds。
6. Lewis 主持点。
7. 第一批 NPC 自由阶段站位表。

为进入 Phase 3，至少需要：

1. 18 对舞台站位算法。
2. 玩家舞者按源码配对顺序插入 3x6 舞台。
3. 超过 18 对、无舞伴观众席、观众席溢出的隐藏规则。
4. 主舞蹈镜头点。
5. 是否启用黑屏 fallback，以及黑屏时长。
