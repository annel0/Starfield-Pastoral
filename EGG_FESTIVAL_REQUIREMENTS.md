# Egg Festival Requirements

本文档只记录 Egg Festival 开工前需要确认的原版事实和 StardewCraft 空间输入。这里不填写任何 StardewCraft 坐标，不从原版 tile、TMX、旧 pregen 或记忆推算点位。

## 原版事实基线

- 日期与入口：春 13，`spring13`，原版条件为 `Town/900 1400`。
- 临时地图：原版 `changeToTemporaryMap Town-EggFestival`，第二年循环使用 `Town-EggFestival2`。
- 初始阶段：`set-up` 加载 `Set-Up` actors，玩家可自由走动，Lewis 作为主持人，`playerControl eggFestival`。
- 主事件：与 Lewis 触发 `mainEvent`，加载 `MainEvent` actors，`waitForOtherPlayers startContest` 后开始找蛋；StardewCraft 必须通过现有 cutscene/event 系统播放，不另写独立剧情播放器。
- 找蛋阶段：原版 `playerControl eggHunt`，`festivalTimer = 52000` 毫秒。
- 蛋生成：原版从当前节日地图 `Paths` 层读取 tilesheet id 以 `fest` 开头的格子，生成可点击节日 props。
- 计分：玩家点击蛋所在 tile 后 `festivalScore++`，蛋被移除并同步到队伍。
- 多人胜利阈值：在线玩家数扣除 dedicated host 后，1 人需要 9 个蛋，2 人 6 个，3 人 5 个，4 人及以上 4 个。
- 结算：倒计时结束后自动进入 `afterEggHunt`，最高分玩家并列计入 winners；若最高分低于阈值，则原版走 NPC 获胜分支。不得弹出“是否颁奖/是否回家”等非原版确认框。
- 奖励：第一次获胜给 `(H)4` 草帽并记录 `Egg Festival` mail；之后获胜给 `(O)PrizeTicket`。
- 商店：`Festival_EggFestival_Pierre`，原版物品 id 包括 `(BC)36`、`(BC)107`、`(O)745`、`(BC)184`、`(BC)188`、`(F)1684`、`(F)2624`、`(F)2632`。
- UI：找蛋阶段左上角显示倒计时和当前蛋数，多人时显示队伍分数列表；StardewCraft 当前按用户要求用 actionbar + Minecraft sidebar scoreboard 复刻。
- 音乐音效：会场/主事件/找蛋阶段涉及 `fallFest`、`event1`、`tickTock`、`whistle`、`coin` 等原版调用；StardewCraft 已按原版 Wavebank ID 接入真实资源。

## NPC 对话与 actor 复刻判定

- 当前 StardewCraft 状态：Egg Festival 会场 NPC 点击对白已接入 `stardewcraft.festival.spring13.dialogue.*`；`stardewcraft.shop.eggfestival.dialogue` 仍只用于 Pierre 节日商店打开时的店铺对白。
- 当前普通 NPC 对话系统只会按介绍、天气、日期、星期、季节、default 等日常 key 选择；没有“玩家正在 active festival session”的优先分支，因此不能仅靠添加 `spring_13` 日常对白来复刻会场。
- 原版 active festival 会把 `spring13` 作为 Event 加载，`loadActors Set-Up/MainEvent` 从临时节日地图 actor layer 放置 NPC，点击 NPC 时从 festival data 取 `NPC名` 对话。
- 原版对话变体按年份轮换选择：存在 `NPC名_y2` 时，偶数年使用 y2，奇数年回到基础 key；当前 Egg Festival 运行时已按服务端 key 索引支持 `_y2` 回落。
- 原版配偶会优先取 `NPC名_spouse` 或 roommate 变体；全部节日的 spouse / roommate 翻译 key 已迁入语言文件，但项目暂缺婚姻/roommate 状态判定，运行时暂不切换到 spouse 变体。
- 原版节日点击会场 NPC 后仍会调用正常交谈友情逻辑；复刻时应明确是否同步“当天已交谈”和友情点。
- Dwarf、Wizard、Sandy、`???`/Krobus 在 `spring13` 数据里有节日日对白，但不是 Egg Festival 会场固定参会 actor；它们更像“节日当天在自己地点的特殊对白”，不要误放进会场，除非用户另行指定。
- Leo 是条件 actor：原版只有玩家已触发 `leoMoved` 后才会在节日 actor layer 加载 Leo；复刻时需要决定是否保留这个解锁门槛。

## Egg Festival NPC 特殊处理清单

以下只记录原版逻辑要求，不记录 StardewCraft 坐标。所有站位、路线点、偏移都必须由用户逐项提供。

- 会场自由阶段第一年循环移动：Vincent、Jas、Leo、Pam、Clint、Demetrius、Willy、Shane、Harvey。
- 会场自由阶段第二年循环移动：Vincent、Jas、Leo、Demetrius、Gus、Shane、Abigail、Haley、Jodi、Elliott、Penny。
- 第二年自由阶段需要初始位置偏移：Sam、Sebastian、Leah、Maru、Marnie、Jodi、Kent?、Linus；`Kent?` 表示原版可选 actor，未加载时不报错。
- 主事件和找蛋阶段第一年参赛移动路线：Maru、Abigail、Jas、Sam、Vincent、Leo。
- 主事件和找蛋阶段第二年参赛移动路线：Abigail、Sam、Jas、Vincent、Maru、Leo。
- 第二年找蛋阶段仍有背景循环移动：Haley、Gus。
- 主持和镜头脚本特殊 actor：Lewis 需要主持站位、讲话朝向切换、开赛跳跃、结算领奖移动、结束讲话移动。
- 儿童响应动作：Jas、Vincent 在 Lewis 宣布前会跳跃，并在讲话期间改变朝向。
- 结算分支特殊 actor：Abigail 在获胜分支会跳跃、移动领奖；Vincent 和 Lewis 会配合朝向切换；未达阈值时走 Jas/Vincent 胜利文本逻辑。
- Pierre 是商店 actor：当前已纳入 Egg Festival NPC actor 管理，并通过确认的 shopBounds 打开 `Festival_EggFestival_Pierre`。
- 静态参会 NPC：其余会场 NPC 至少需要自由阶段站位/朝向、主事件站位/朝向、可交互范围；第二年视觉或站位差异另列。

## 用户后续需要按 NPC 提供的字段

- `setupPosition`、`setupFacing`：自由逛会场时的站位和朝向。
- `setupRoute`：自由阶段循环移动路线；只给需要移动的 NPC。
- `mainEventPosition`、`mainEventFacing`：开赛讲话/结算时的站位和朝向。
- `eggHuntRoute`：找蛋阶段路线；只给参赛或背景移动 NPC。
- `year2PositionOffset` 或直接给 year2 完整站位：第二年差异不能从原版 tile 推算。
- `interactionBounds`：站在摊位、柜台或障碍物后的 NPC 需要额外交互盒。
- `spawnGate`：Leo、Kent 等条件或可选参会 NPC 的加载条件。

## 第一批 NPC 点位填写表

本表只收 Egg Festival 自由逛会场阶段的点位。特殊主事件、找蛋路线、结算移动、第二年差异先不做；每个 NPC 先只需要一个站位、一个朝向、一个可选交互范围。坐标格式统一用 `x y z`，朝向写 `北/南/西/东`。

| 编号 | NPC | 第一批处理方式 | 你需要给的内容 |
| --- | --- | --- | --- |
| EF-NPC-01 | Abigail | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-02 | Alex | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-03 | Caroline | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-04 | Clint | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-05 | Demetrius | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-06 | Elliott | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-07 | Emily | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-08 | Evelyn | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-09 | George | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-10 | Gus | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-11 | Haley | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-12 | Harvey | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-13 | Jodi | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-14 | Kent | 静态站位，若暂不出现可写 `跳过` | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-15 | Leah | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-16 | Linus | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-17 | Marlon | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-18 | Marnie | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-19 | Maru | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-20 | Pam | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-21 | Penny | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-22 | Pierre | 已有临时商店点，后续纳入 NPC 表 | 若沿用当前点写 `沿用`；否则给新 `setupPosition`、`setupFacing`、`shopBounds` |
| EF-NPC-23 | Robin | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-24 | Sam | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-25 | Sebastian | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-26 | Shane | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-27 | Willy | 静态站位 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-28 | Leo | 条件 NPC，若暂不出现可写 `跳过` | `setupPosition`、`setupFacing`、可选 `interactionBounds`、是否需要 `leoMoved` 门槛 |
| EF-NPC-29 | Lewis | 先按静态站位处理，主持脚本后做 | `setupPosition`、`setupFacing`、可选 `interactionBounds` |
| EF-NPC-30 | Vincent | 绕圈队列，见下表 | `loopId`、`startOrder` |
| EF-NPC-31 | Jas | 绕圈队列，见下表 | `loopId`、`startOrder` |

## Vincent/Jas 绕圈输入表

你不需要设计完整 AI。只要给一条闭环路线，我来实现两个小孩沿同一条路线排队绕圈。最省心格式是四个角点，点位按行走顺序写；如果不是矩形，也可以给 5 个以上路点。

| 编号 | 项目 | 你需要给的内容 |
| --- | --- | --- |
| EF-LOOP-VJ-01 | 绕圈路线名 | 建议写 `egg_kids_loop` |
| EF-LOOP-VJ-02 | 路点 A | 第一个角点或起点，格式 `x y z` |
| EF-LOOP-VJ-03 | 路点 B | 第二个路点，格式 `x y z` |
| EF-LOOP-VJ-04 | 路点 C | 第三个路点，格式 `x y z` |
| EF-LOOP-VJ-05 | 路点 D | 第四个路点，格式 `x y z` |
| EF-LOOP-VJ-06 | 行走方向 | `A->B->C->D->A` 或反过来 |
| EF-LOOP-VJ-07 | 队伍顺序 | 例如 `Vincent 在前，Jas 在后` |
| EF-LOOP-VJ-08 | 间距 | 建议填 `1格` 或 `2格` |

## 用户已提供的第一批 NPC 点位草稿

以下为用户按 StardewCraft 世界坐标提供的自由逛会场阶段草稿点位。`A -> B 来回` 表示 NPC 在两端之间巡逻，端点朝向分别按用户给定朝向记录；`静态` 表示暂不移动。

| 编号 | NPC | 类型 | 点位或路线 |
| --- | --- | --- | --- |
| EF-NPC-01 | Abigail | 来回巡逻 | `-21 64 -2 S -> -21 64 -8 W` |
| EF-NPC-02 | Alex | 静态 | `21 64 -5 S` |
| EF-NPC-03 | Caroline | 静态 | `11 64 10 E` |
| EF-NPC-04 | Clint | 来回巡逻 | `7 64 -3 W -> 13 64 -7 S` |
| EF-NPC-05 | Demetrius | 来回巡逻 | `-8 64 0 S -> 5 64 -4 S` |
| EF-NPC-06 | Elliott | 静态 | `13 64 3 S` |
| EF-NPC-07 | Emily | 静态 | `-5 64 5 E` |
| EF-NPC-08 | Evelyn | 静态 | `13 64 11 W` |
| EF-NPC-09 | George | 静态 | `13 64 10 W` |
| EF-NPC-10 | Gus | 来回巡逻 | `1 64 -3 E -> 5 64 0 N` |
| EF-NPC-11 | Haley | 静态 | `-1 64 8 N` |
| EF-NPC-12 | Harvey | 静态 | `2 64 2 S` |
| EF-NPC-13 | Jodi | 静态 | `10 64 13 N` |
| EF-NPC-15 | Leah | 静态 | `5 64 12 E` |
| EF-NPC-16 | Linus | 静态 | `-11 64 11 E` |
| EF-NPC-17 | Marlon | 静态 | `-34 64 -6 S` |
| EF-NPC-18 | Marnie | 静态 | `6 64 4 S` |
| EF-NPC-19 | Maru | 静态 | `7 64 12 W` |
| EF-NPC-20 | Pam | 来回巡逻 | `4 64 -2 E -> -2 64 1 S` |
| EF-NPC-21 | Penny | 静态 | `-3 64 14 S` |
| EF-NPC-23 | Robin | 静态 | `-8 64 0 E` |
| EF-NPC-24 | Sam | 静态 | `22 64 -4 W` |
| EF-NPC-25 | Sebastian | 静态 | `20 64 -4 E` |
| EF-NPC-26 | Shane | 来回巡逻 | `1 64 -8 S -> 5 64 -7 E` |
| EF-NPC-27 | Willy | 静态 | `14 64 15 W` |
| EF-NPC-29 | Lewis | 静态 | `7 64 4 S` |
| EF-NPC-30 | Vincent | 队列绕圈 | 使用 `EF-LOOP-VJ` 路线 |
| EF-NPC-31 | Jas | 队列绕圈 | 使用 `EF-LOOP-VJ` 路线 |

| 编号 | 项目 | 已提供内容 |
| --- | --- | --- |
| EF-LOOP-VJ-01 | 绕圈路线名 | `egg_kids_loop` |
| EF-LOOP-VJ-02 | 路点 A | `-3 64 21 W` |
| EF-LOOP-VJ-03 | 路点 B | `-8 64 21 W` |
| EF-LOOP-VJ-04 | 路点 C | `-8 64 25 S` |
| EF-LOOP-VJ-05 | 路点 D | `-3 64 25 E` |
| EF-LOOP-VJ-06 | 行走方向 | `A -> B -> C -> D -> A` |
| EF-LOOP-VJ-07 | 队伍顺序 | `Vincent 在前，Jas 在后` |
| EF-LOOP-VJ-08 | 间距 | `1格` |

仍缺或需要确认：

- `EF-NPC-14` Kent：第一批不做。
- `EF-NPC-16` Linus：已补充 `-11 64 11 E`。
- `EF-NPC-22` Pierre：本批未提供；当前已有临时商店点 `-5 64 -10 S` 与 shopBounds `-7 64 -8` 到 `-2 67 -6`，若沿用可写 `沿用`。
- `EF-NPC-28` Leo：第一批不做。

## 已确认的主事件与颁奖 cutscene 输入

当前代码把 Egg Festival 主事件接入现有 `cutscene_events` 系统：`egg_festival_main_event` 播放开赛剧情，结束回调后自动进入 52 秒找蛋；`egg_festival_award` 播放 `afterEggHunt` 颁奖剧情，并在黑屏期间通过 cutscene server action 统一结束节日、传回农场。

| 编号 | 内容 | 已确认内容 |
| --- | --- | --- |
| EF-MAIN-01 | Lewis 主持站位 | `7 64 4 S`，主事件和颁奖均沿用 |
| EF-MAIN-02 | 参赛 NPC 排队 | 从 `1 64 7` 开始，每 2 格一个：`1/3/5/7/9/11 64 7`，下一排从 `1 64 9` 继续；朝北 |
| EF-MAIN-03 | 玩家排队 | 玩家按在线参与者排序接在参赛 NPC 后继续占位；多人必须同屏可见 |
| EF-MAIN-04 | cutscene 镜头 | `{x: 7.531, y: 68.513, z: 13.252, yaw: 179.4, pitch: 46.5, relative: false}` |
| EF-MAIN-05 | 领奖点 | `9 64 4 S`，运行时按方块中心 `9.5 64 4.5` 用于演员移动 |
| EF-MAIN-06 | 流程限制 | 不允许自写非原版 query；找蛋结束后自动 whistle、黑屏、颁奖、结束回家 |
| EF-MAIN-07 | UI 例外 | 找蛋计时使用 actionbar，分数表使用 Minecraft sidebar scoreboard |

## 暂不做的特殊项

| 编号 | 暂不做内容 | 涉及 NPC |
| --- | --- | --- |
| EF-SPECIAL-01 | 第二年主事件精确差异 | Lewis、Jas、Vincent、参赛 NPC；首版复用第一年已确认队列和镜头 |
| EF-SPECIAL-02 | 找蛋阶段 NPC 精确跑路线 | Abigail、Maru、Jas、Sam、Vincent、Leo；当前用正式蛋点随机游走占位 |
| EF-SPECIAL-03 | 草帽/兑奖券实物奖励 | 奖励物品缺失时先只播放原版发奖台词，不发实物 |
| EF-SPECIAL-04 | 第二年站位、第二年路线、第二年对白优先级 | 全体有 `_y2` 的 NPC |
| EF-SPECIAL-05 | spouse/roommate 节日对白覆盖 | 可结婚 NPC |
| EF-SPECIAL-06 | 非会场节日当天特殊对白 | Dwarf、Wizard、Sandy、Krobus/`???` |

## 当前完成门槛

当前第一版 Egg Festival 已能完成会场、商店、主事件 cutscene、找蛋、计分、颁奖和回家流程。按当前完成门槛，剩余两项已处理完成。

| 编号 | 内容 | 当前状态 |
| --- | --- | --- |
| EF-DONE-01 | 多人 cutscene 外观精确同步 | 已完成：cutscene state 同步排序后的参赛玩家 UUID；客户端 actor 按 UUID 取真实玩家皮肤、slim/wide 模型和装备 |
| EF-DONE-02 | NPC 获胜 fallback 分支细节 | 已完成：原版 `eggHuntWinner` 低于阈值时使用 `Event.cs.1862 = Abigail!`，随后 `fork AbbyWin`；StardewCraft 现保留 Abigail 获胜文本，并补齐 Abigail 跳跃、Vincent 朝向、Abigail 领奖/返回动作 |

以下内容保留为延期项或资料备忘，不作为当前完成门槛：草帽 / Prize Ticket 实物奖励、找蛋阶段 NPC 精确路线、第二年地图/站位/路线差异、spouse / roommate 节日对白、非会场节日当天特殊对白。

## 后续空间输入备忘

以下空间输入只作为长期资料，不列入当前 Egg Festival 完成门槛。

- `Town` 普通 base schem：来源、版本、覆盖范围、是否只包含公共区域。
- `Town-EggFestival` 节日 schem：来源、版本、覆盖范围、是否包含所有节日建筑和装饰。
- `Town-EggFestival2` 是否要首版同步支持；如果暂不支持，需要明确第一版只做哪一年视觉。
- overlay origin：StardewCraft 世界坐标中的粘贴原点。
- overlay bounds：允许 diff 替换和恢复的公共区域边界。
- 入口交互点：玩家在 Town 哪里触发进入节日会场。
- 玩家入场点：单人和多人玩家进入会场后的安全站位与朝向。
- 玩家主事件站位：找蛋开始前 cutscene/主持阶段的安全站位与朝向。
- 节日结束点：节日结束后玩家回到 Town 或时间推进后的安全落点。
- Lewis 主持点：自由逛会场时、主事件时、结算发奖时的站位与朝向。
- Pierre 商店点：玩家能打开节日商店的交互位置、朝向和碰撞范围。
- NPC 会场站位：每个参会 NPC 的站位、朝向、可交互范围；第二年差异另列。
- Egg Hunt 可移动范围：玩家找蛋期间允许进入的区域和禁止越界区域。
- Egg Hunt 蛋点位：每个蛋的世界坐标、视觉模型/方块/交互体、是否需要遮挡或高度偏移。
- Egg Hunt NPC 路线锚点：Abigail、Maru、Jas、Sam、Vincent、Leo 等参赛 NPC 的移动路线需要由你确认到 StardewCraft 坐标后再实现。
- 临时实体清理规则：哪些方块、实体、掉落物、交互体属于 `spring13` session，结束后必须清理。
- 恢复验收点：节日前后需要截图或手动验收的关键公共区域位置。

## 当前已确认的地图切换测试输入

- 用户已提供测试 schem：`src/main/resources/data/stardewcraft/structures/festivals/egg_festival_town.schem`，原文件名为临时占位名，已移入 datapack 资源路径。
- 用户已提供测试替换区域：从 `-38 68 -23` 到 `67 63 52`。
- 当前注册的 overlay origin：`-38 63 -23`，也就是该区域的低点原点。
- 当前注册的 overlay bounds：`-38 63 -23` 到 `67 68 52`。
- 当前模式：没有普通 base schem；节日当天应用前会从世界现状运行时备份该区域，第二天或手动 restore 时按备份恢复。
- 总调试命令：`/stardew festival apply spring13` 会把当前日期临时视为 Egg Festival，应用 `Town-EggFestival` overlay，登记当前玩家位置，把玩家传入会场，并让 NPC 在 overlay 应用完成后进入节日 actor 点位与节日对白逻辑。
- 总恢复命令：`/stardew festival restore spring13` 会清除临时节日日期覆盖，恢复 NPC 当前日程，恢复调试前玩家位置，并恢复 `Town-EggFestival` overlay。
- 底层调试命令：`/stardew festival overlay apply Town-EggFestival`、`/stardew festival overlay restore Town-EggFestival`、`/stardew festival overlay status`、`/stardew festival npc apply spring13`、`/stardew festival npc restore spring13`。这些只用于拆分排查，正常测试 Egg Festival 应优先使用总调试命令。

## 当前已确认的蛋蛋节会场输入

- 节日会场包围盒：从 `-46 84 -36` 到 `137 64 53`，运行时按 min `-46 64 -36`、max `137 84 53` 处理。
- 非节日可进入时间：玩家不能进入会场包围盒；若强行进入，像秘密森林阻挡一样推回并显示原版文本 `今天的节庆还在布置中，过会儿再来吧。`
- 节日开始提示：春 13 到 9:00 时，按原版源码广播 Egg Festival 已在小镇广场开始。
- 节日可进入时间：玩家走入会场包围盒时弹出 `要进入蛋蛋节吗？`；确认后传送到 `-39 64 -34`，面朝南。
- 节日期间时间停止；进入节日后服务端时间、HUD 同步时间和客户端天空都固定到节日开始时间 9:00。
- 玩家开始离开会场包围盒时弹出离开确认；单人确认后结束节日，多人需要所有在线参与者确认后才结束节日。
- 节日结束后，所有在线参与者返回各自所属农场出生点。
- 节日期间隐藏右上时间/金钱 HUD 和任务按钮。
- Pierre 节日实体点：`-5 64 -10`，面朝南。
- Pierre 商店交互区域：从 `-7 64 -8` 到 `-2 67 -6`，进入该区域视为打开 `Festival_EggFestival_Pierre` 商店。
- 其他 NPC 会场点位、NPC 路线、室内空间限制仍等待用户提供；不得从原版 tile、TMX、旧 pregen 或记忆推算。

## 助手当前可以继续做的无坐标工作

- 完成源码考古，把 `spring13.json` 和 `Event.cs` 的状态流拆成 Egg Festival mechanic 任务。
- 建立配置格式草案，只定义字段名和校验规则，不填坐标值。
- 加强门禁校验：缺少 overlay、入口点、商店点、NPC 站位或蛋点位时拒绝启动。
- 迁移和核对节日商店物品、奖励 id、音频 id。

## 当前已接入的 Pierre 节日商店

- 触发：玩家作为 Egg Festival 参与者站在 `-7 64 -8` 到 `-2 67 -6` 的 shopBounds 内右键 Pierre 时打开 `Festival_EggFestival_Pierre`；不在 shopBounds 内右键 Pierre 时只走 Pierre 的节日会场对白。
- 已售商品：`stardewcraft:lawn_flamingo` 400g、`stardewcraft:plush_bunny` 2000g 且限购 1、`stardewcraft:strawberry_seeds` 100g、`stardewcraft:pastel_banner` 1000g、`stardewcraft:standing_hoe` 1000g。
- 按用户要求暂不做：原版两个季节性盆栽/作物、色块组画；原版装饰耙子映射为项目已有 `standing_hoe`。
- 普通 `SeedShop` 春季清单已移除 `stardewcraft:strawberry_seeds`，草莓种子只通过 Egg Festival 商店出售。

## 当前已迁入的节日 NPC 对话 key

- 已从 `源文件/Content/Data/Festivals` 的 8 个基准节日 JSON 机械迁入 683 条 NPC 对话 key；英文来自无语言后缀基准文件，中文来自 `.zh-CN.json`。
- 生成的运行时索引：`src/main/resources/data/stardewcraft/festival_dialogue_keys.json`。
- 当前实际运行接入：Egg Festival 会场 actor 点击对白，支持基础 key 与 `_y2` 年份回落。
- 尚未运行接入：其他节日 actor/session、配偶/roommate 变体选择、非会场 NPC 的节日当天特殊对白。

## 原版 Egg Festival 音乐与移动事实

- 自由逛会场阶段：`spring13.json` 的 `set-up` 第一条命令是 `fallFest`，也就是会场阶段使用原版 `fallFest` 音乐。
- 主事件和结算阶段：`mainEvent` / `afterEggHunt` 会显式 `playMusic event1`，部分讲话前会短暂 `playMusic none`。
- 找蛋阶段：Lewis 宣布开始后 `playSound whistle`，随后 `playMusic tickTock`。
- 当前资源状态：已按 Stardew Wiki Modding:Audio 与本地 `源文件/音频文件` 接入 `whistle=0000012a`、`event1=0000012b`、`tickTock=0000012c`、`fallFest=00000130`；`coin` 已有项目资源并已用于拾蛋。
- StardewCraft sound event：`stardewcraft:whistle`、`stardewcraft:music_event1`、`stardewcraft:music_tick_tock`、`stardewcraft:music_fall_fest`。
- 原版自由阶段 scripted move：第一年 `advancedMove` 明确驱动 Vincent、Jas、Leo、Pam、Clint、Demetrius、Willy、Shane、Harvey；其中若干 token 是“朝向 + 等待毫秒”，所以会有走到端点后转向/停顿的表现。当前 StardewCraft 第一批 actor 只向 `NpcCentralMovementService` 提交用户给定的完整节日巡逻路线，实际行走继续复用项目 NPC 中央寻路、repath、stuck detection、开门和 chunk forcing 逻辑；来回巡逻 NPC 的路线为“终点 -> 起点”循环，Vincent/Jas 为各自连续环线，端点按给定朝向短暂停顿。
- UI/计时/计分/胜负/多人 ready gate 已由服务端状态框架承载；NPC 主事件点位和路线仍等待用户确认。