# 沙漠节赛跑极致重做规划

## 0. 结论先行

当前赛跑模块必须按“正式星露谷小游戏界面”重做，而不是继续修补现有调试式 GUI。

重做目标不是“能下注、能跑、能结算”，而是：玩家打开界面后，一眼知道自己在哪里、正在做什么、下一步能做什么；画面像星露谷节日 UI，赛车运动顺滑、可读、有声音、有反馈；单人竞猜、多人下注、Shady Guy 干扰三条流程互不混杂。

本规划只写方案，不改代码。

## 1. 当前版本必须承认的问题

### 1.1 赛车偶尔可见偶尔不可见

问题本质不是“贴图偶发没加载”，而是渲染坐标、裁剪窗口、层级和同步方式没有形成稳定规则。

可能原因：

- GUI 使用赛道裁剪背景，但 racer 的世界坐标转换和背景裁剪原点绑定过于脆弱。
- drawAboveMap、跳跃高度、Y 排序和裁剪边界没有形成两层渲染模型。
- 服务端低频同步位置，客户端直接显示收到的位置，没有平滑插值，所以瞬移、卡顿和短暂越界会被放大。
- 原版 racer draw 是地图内 SpriteBatch 渲染，带 viewport 和 layerDepth；当前 GUI 是屏幕空间重画，必须专门重建“地图空间到 GUI 空间”的一致规则。

重做要求：

- racer 渲染必须 100% 基于统一坐标系。
- 背景、赛道点、racer、阴影、公告文本全部使用同一个 `RaceViewportTransform`。
- 服务端只决定真实状态，客户端必须做渲染插值。
- racer 不允许因为 `drawAboveMap` 或跳跃高度掉出绘制流程；只允许进入“普通层”和“高层”两次绘制队列。

### 1.2 移动卡顿

问题本质是把网络同步频率当成动画帧率。

重做要求：

- 服务端保持权威 race state 和 racer 物理。
- 客户端以 render tick 每帧绘制。
- 网络包传 `serverTick`、位置、方向、跳跃、摔倒、frame phase、progress。
- 客户端保存上一帧 snapshot 和当前 snapshot，按 partial tick 或本地时间插值。
- animation frame、阴影呼吸、公告抖动、按钮 hover、UI 入场动画必须本地驱动，不依赖服务端包刷新。

验收标准：

- 60 FPS 环境下赛车连续移动，没有“一格一格跳”。
- 网络同步降低到 5-10Hz 时，GUI 仍然看起来顺滑。
- 开始、跳跃、摔倒、冲线四个瞬间不会闪烁或消失。

### 1.3 GUI 是调试界面，不是游戏界面

当前 GUI 把所有功能堆到一个屏幕：竞猜、干扰、房间、下注、锁房、开始、倍率、领奖，全都在一块面板里。这违反玩家理解，也违反原版交互语义。

重做原则：

- Race Man 负责比赛、竞猜、领奖、观赛。
- Shady Guy 负责干扰，必须是另一个交互入口，不能塞进 Race Man 的比赛界面。
- 单人竞猜和多人下注先选模式。
- 多人下注必须有正常游戏房间流程：房间列表、创建房间、进入房间、房间内下注、房主准备/开始或等待下一场。
- 比赛观赛界面只显示比赛本身和必要状态，不显示一堆管理按钮。

## 2. 最终体验目标

### 2.1 玩家从 Race Man 打开后的第一屏

第一屏是星露谷风格的“赛跑柜台”界面，不是调试面板。

布局：

- 背景：烘焙后的原版沙漠赛道局部，带轻微暗化。
- 前景：星露谷木牌/羊皮纸风格窗口。
- 标题：沙漠赛跑。
- 两个大按钮：
  - 单人竞猜
  - 多人下注
- 底部小按钮：观赛、领取奖励、关闭。
- 如果没有可领奖励，领取按钮置灰。
- 如果当前正在比赛，单人竞猜按钮置灰，多人房间下注入口置灰，但观赛可用。
- 如果当天最后一场已结束，下注入口置灰，只保留观赛结果和领奖。

视觉要求：

- 不使用纯色调试矩形。
- 不使用大量普通 Minecraft 文本按钮。
- 按钮要像 SDV 菜单按钮：浅木色/羊皮纸底、深棕描边、hover 时微亮。
- 三花蛋数量用三花蛋图标加数字，不写一长串解释。
- 当前时间/下一场状态用短句，不写教程。

### 2.2 单人竞猜流程

入口：Race Man -> 单人竞猜。

流程：

1. 显示三名参赛选手卡片。
2. 每张卡片包含：
   - racer sprite 或头像裁切。
   - 原版名字：极速公鸡、毒刺蝎王、靴靴饼干、闪电蜗牛、爬爬仙人掌。
   - 小型状态装饰：速度感图标、下注标记、已选高亮。
3. 玩家点击一个选手。
4. 下方按钮变为“确定竞猜”。
5. 确定后回到赛跑柜台，显示“已竞猜：某某”。
6. 比赛开始后，竞猜锁定。
7. 比赛结束后，如果猜中：Race Man 界面显示可领取奖励。

重要限制：

- 单人竞猜不消耗三花蛋，和原版一致。
- 单人竞猜不能出现“干扰”按钮。
- 不能出现“房间、锁房、开始房间”等多人功能。
- 如果已经竞猜，选手卡只读，并显示已选。

### 2.3 Shady Guy 干扰流程

入口：地图上 Shady Guy 单独交互点。

流程：

1. 如果当天第一次交互，显示三段式简介对话，语气对标原版。
2. 之后显示是否花 1 个三花蛋做点手脚。
3. 玩家选择是，才进入选手选择界面。
4. 选中一个 racer，消耗 1 个三花蛋。
5. 显示已安排的目标。
6. 比赛已开始、今日已结束、已经安排过、三花蛋不足时给对应短提示。

重做要求：

- Shady Guy 可以复用选手卡组件，但必须是独立 `ShadyGuyScreen` 或独立 dialog flow。
- Race Man 的 GUI 里绝不出现干扰按钮。
- 干扰效果只进入服务端 `sabotages`，比赛开始时转成 racer 的 trip chance。

### 2.4 多人下注总流程

入口：Race Man -> 多人下注。

必须拆成两个层级：

1. 房间列表界面。
2. 房间详情界面。

不能把房间列表、下注、锁房、开始比赛全部堆在同一个屏幕。

## 3. 多人下注房间设计

### 3.1 房间列表界面

布局：

- 标题：多人下注。
- 左侧或中间：房间列表。
- 每个房间卡片显示：
  - 房间名或房主名。
  - 状态：开放、已锁定、比赛中、已结算。
  - 当前奖池三花蛋数量。
  - 玩家数/下注人数。
  - 是否自己已下注。
- 底部按钮：创建房间、加入房间、返回。
- 右侧小预览：今日参赛选手和当前下一场状态。

行为：

- 点击房间卡片选中。
- 双击或点“加入房间”进入详情。
- 创建房间后直接进入自己房间。
- 房间比赛中时可进入观看，但不能下注。
- 已结算房间可进入领取或查看结果。

### 3.2 创建房间流程

创建房间不该只是一个按钮立刻生成空对象，而要有正常游戏感。

流程：

1. 点击创建房间。
2. 弹出小型星露谷命名框：默认“某某的赛跑房间”。
3. 可选设置：
   - 开放下注到下一场开始。
   - 房主手动锁定。
   - 最低下注额，默认 1。
   - 最高下注额，默认 999。
4. 创建后进入房间详情。

最小可落地版本：

- 房间名先用房主名自动生成。
- 最低下注 1，最高下注 999。
- 后续可加命名输入，但 UI 位置先预留。

### 3.3 房间详情界面

布局：

- 顶部：房间名、房主、状态、总奖池。
- 中间：三名参赛选手下注卡。
- 每张卡显示：
  - racer sprite。
  - 名字。
  - 当前该选手奖池。
  - 当前动态赔率。
  - 玩家自己的下注标记。
- 下方：下注金额选择器。
- 底部按钮：下注、准备/锁房、观赛、离开房间。

动态赔率规则：

- 使用 pari-mutuel 奖池逻辑。
- 总奖池 = 所有玩家下注总额。
- 某 racer 赔率展示 = `总奖池 / 该 racer 池`。
- 玩家若押中，返还 = `玩家下注额 * 总奖池 / 胜者池`。
- 展示时保留整数倍或一位小数，实际结算整数三花蛋。
- 没人押某 racer 时，显示“冷门”或 `--`，不显示 0 倍。

房主操作：

- 房主可锁房。
- 房主可手动开始一场房间赛，但前提是当前全局 race state 为可开始。
- 如果不手动开始，房间可绑定下一场全局赛跑。
- 房间锁定后不能再下注。

非房主操作：

- 可下注。
- 可离开。
- 可观赛。
- 不显示或置灰锁房/开始按钮。

### 3.4 多人和原版单人赛的关系

为了避免“多人房间另开一套奇怪模拟赛”，多人房间应绑定同一套原版赛跑状态机。

推荐规则：

- 每天原版周期赛仍然存在。
- 多人房间下注默认押“下一场官方赛”。
- 房主“开始”按钮不是新建一个假赛，而是在允许时触发下一场官方赛提前进入倒计时。
- 所有房间共享同一场比赛结果，但每个房间有独立奖池。
- 这样画面、胜者、干扰、单人竞猜都和原版 race state 保持一致。

这样做的好处：

- 不会出现多个 GUI 同时跑不同比赛的混乱。
- 服务端只维护一套 racer 物理。
- 多个房间只是下注池，不是多个比赛实例。

## 4. 观赛界面设计

### 4.1 核心原则

观赛界面只做一件事：看比赛。

不在观赛界面塞：

- 单人竞猜按钮。
- 干扰按钮。
- 创建房间。
- 锁房。
- 复杂赔率列表。

观赛界面只显示：

- 原版赛道背景。
- 三名 racer。
- 当前公告文字。
- 玩家押注/竞猜的小徽章。
- 三花蛋数量和可领奖状态。
- 关闭按钮。

### 4.2 画面构图

推荐使用“原版赛道全景窗口”：

- 背景使用独立烘焙 PNG。
- 不只是现在的粗糙裁切，而是重新确定 viewport：
  - 覆盖起点、弯道、跳跃点、终点。
  - 保留赛道周围节日摊位和环境细节。
  - 让玩家能理解 racer 在绕哪条路跑。
- GUI 中地图占屏幕 75%-85%。
- 下方是一条窄状态栏，不能压住赛道。

### 4.3 racer 渲染

必须完整保留原版 sprite 逻辑：

- 方向：上、下、左、右。
- 左向使用右向 sprite 翻转。
- frame 走路帧切换。
- tripping 使用摔倒帧。
- jumping 使用 height 抬升。
- 阴影随 height 淡化。
- racer 2 的小跳动保留。
- racer 3 的慢速和短 burst 保留。

渲染层级：

- 第一层：背景 map base。
- 第二层：非 aboveMap racer 阴影和本体。
- 第三层：前景遮挡层，如果需要重新烘焙分层。
- 第四层：aboveMap racer。
- 第五层：公告文字。
- 第六层：状态栏。

当前只有一张合成背景会导致“原版遮挡关系不完整”。极致版应烘焙两张背景：

- `race_track_back.png`：Back、Back2、Buildings、Buildings2、Front、Front2 中不需要遮挡 racer 的部分。
- `race_track_front.png`：AlwaysFront、AlwaysFront2 和需要压在 racer 前面的前景。

如果前景分层很难自动判断，就至少把原版 `drawAboveMap` 需要的段落处理正确，保证跳跃点不消失。

### 4.4 公告文字

沿用原版 Race_* 文本：

- Race_Begin
- Race_Ready
- Race_Set
- Race_Go
- Race_Finish
- Race_Winner
- Racer_0 到 Racer_4
- RESULT
- Race_Win / Race_Lose
- Race_Close / Race_Close_LastDay

表现方式：

- 使用星露谷大字滚动/弹出风格。
- 居中在赛道上方，不压住 racer。
- GO、Finish、Racer 名字有轻微抖动。
- 文本淡入淡出。

## 5. 美术风格规划

### 5.1 UI 总风格

关键词：星露谷、节日、木牌、羊皮纸、像素、暖色、清晰。

禁止：

- 半透明黑色调试面板。
- 普通 Minecraft 灰按钮。
- 大量纯色矩形。
- 一屏堆满功能。
- 用说明文字解释操作。

需要：

- 裁切原版或项目已有 SDV 风格 UI 边框。
- 每个按钮有 hover、pressed、disabled 三态。
- 选手卡有选中描边和小动效。
- 房间列表像告示板或报名册。
- 金额选择器像小木牌上的 `- 数字 +`。
- 三花蛋图标用于所有金额和奖池。

### 5.2 需要烘焙或裁切的 UI 小资源

仍然遵守“不运行时引用大图”。只允许脚本裁切独立 PNG。

需要新增资源：

- `race_panel_wood.png`：主窗口木框。
- `race_button.png`：正常按钮。
- `race_button_hover.png`：hover 按钮。
- `race_button_disabled.png`：禁用按钮。
- `race_card.png`：选手卡底。
- `race_card_selected.png`：选手卡选中底。
- `race_room_card.png`：房间卡底。
- `race_room_card_selected.png`：房间卡选中底。
- `race_badge_guess.png`：已竞猜徽章。
- `race_badge_bet.png`：已下注徽章。
- `race_badge_winner.png`：胜者徽章。
- `race_amount_stepper.png`：金额步进器。
- `race_close_button.png`：关闭按钮。
- `race_tab_single.png` / `race_tab_multi.png`：模式选择标签。

如果原版没有完全对应的 UI 零件，可以从项目现有 SDV UI 组件中统一裁切或复用，但最终必须看起来像同一个美术系统。

### 5.3 racer 卡片美术

选手卡不要只是文字列表。

每张卡：

- 左侧显示 racer sprite 放大 4x。
- 名字居中或右侧。
- 下注/竞猜状态用图标。
- hover 时卡片轻微抬亮。
- selected 时出现金色描边或小星光。
- winner 时显示胜者缎带。

### 5.4 多人房间美术

房间列表应该像节日报名册。

房间卡字段：

- 房主名。
- 奖池。
- 状态章。
- 已下注图标。

房间详情应该像赌桌，不像后台管理表格。

下注区：

- 三个 racer 卡围成一排。
- 中间或下方是下注金额。
- 押注后卡片上出现自己的小标记。
- 赔率数字要醒目，但不能盖过选手名字。

## 6. 音效规划

### 6.1 原则

赛跑要有声音反馈，但不能滥加廉价音效。

音效分三类：

- UI 操作音。
- 比赛事件音。
- racer 动作音。

所有声音必须走 `ModSounds` 或项目已有 sound registry，不在 GUI 里硬编码不存在资源。

### 6.2 UI 操作音

需要：

- 打开 Race Man 界面：`bigSelect` 或项目已有 `BIG_SELECT`。
- 返回/关闭：`bigDeSelect`。
- hover 不一定播放，避免吵。
- 点击按钮：`button_press`。
- 确认竞猜：轻快 coin/select。
- 下注成功：coin 或 Calico Egg 收取音。
- 下注失败：低音错误提示。
- 领奖：coin + item pickup。

### 6.3 比赛事件音

Race_Begin：

- 短号角或节日提示音。
- 如果没有合适原声，先用项目现有短促提示音。

Race_Ready / Race_Set：

- 每次一个轻短 tick。

Race_Go：

- 明显开跑音。
- 同时 racer 开始移动。

Race_Finish：

- 冲线音或铃声。

Race_Winner + Racer 名字：

- 小型胜利提示音。

Race_Win：

- 奖励提示音。

Race_Lose：

- 轻微低落音，不要刺耳。

### 6.4 racer 动作音

脚步声不要每帧播，会吵。

建议：

- 只在比赛 Go 状态每 8-12 tick 播一个混合轻脚步，音量很低。
- 摔倒时播放 thud。
- 跳跃落地时播放轻 thud。
- racer 3 可有很轻的黏滑/滑动音，但如果没有好素材则不做，避免违和。

### 6.5 Shady Guy 音效

- 打开对话：低调 select。
- 消耗三花蛋：coin down。
- 干扰安排完成：小型暗色确认音。
- 三花蛋不足：错误音。

## 7. 特效规划

### 7.1 UI 特效

轻量、像素、不要现代网页感。

需要：

- 界面打开时窗口从 96% 到 100% 轻微弹入。
- 卡片 hover 微亮。
- 选中 racer 时一圈金色像素描边闪一下。
- 下注成功时三花蛋图标轻微跳动。
- 领奖时奖励数字向上浮动。

### 7.2 比赛特效

需要：

- GO 文字轻微抖动。
- racer 摔倒时 1-2 个小尘土粒子。
- 跳跃时阴影缩小/变淡。
- 冲线时胜者头顶小星星或小缎带，持续 1 秒。

注意：

- 粒子必须在 GUI 层本地渲染，不生成世界实体。
- 特效数量固定且轻量。
- 不遮挡 racer。

## 8. 技术重构方案

### 8.1 必须拆分 GUI 状态

不要一个 `DesertFestivalRaceScreen` 管全部。

推荐拆成：

- `DesertFestivalRaceHubScreen`
  - Race Man 第一屏。
  - 选择单人/多人/观赛/领奖。
- `DesertFestivalRaceSingleBetScreen`
  - 单人竞猜。
- `DesertFestivalRaceRoomListScreen`
  - 多人房间列表。
- `DesertFestivalRaceRoomScreen`
  - 房间详情和下注。
- `DesertFestivalRaceWatchScreen`
  - 只看比赛。
- `DesertFestivalShadyGuyScreen`
  - Shady Guy 干扰。

可以共用组件：

- `RaceRacerCard`。
- `RaceButton`。
- `RacePanel`。
- `RaceAmountStepper`。
- `RaceViewportRenderer`。
- `RaceAnnouncementRenderer`。

### 8.2 服务端状态保持

服务端继续权威：

- race state。
- racer movement。
- active guesses。
- next guesses。
- sabotages。
- rewards。
- room pools。
- payouts。

需要补强：

- 房间状态持久化。
- 玩家断线重连后的房间下注状态恢复。
- 当前比赛快照带 server tick。
- 房间列表单独轻量同步，不要每个列表界面都收 racer 高频位置。

### 8.3 网络包拆分

当前统一 snapshot 过大，应该拆。

推荐：

- `OpenRaceHubPayload`
  - 打开 Race Man hub。
- `RaceCommonStatePayload`
  - 当前比赛状态、参赛者、奖励、玩家竞猜。
- `RaceRacerStatePayload`
  - 高频 racer 位置，只发给观赛界面。
- `RaceRoomListPayload`
  - 多人房间列表。
- `RaceRoomDetailPayload`
  - 单个房间详情。
- `RaceActionPayload`
  - 竞猜、下注、创建房间、锁房、开始、领奖。
- `OpenShadyGuyPayload`
  - 打开 Shady Guy。
- `ShadyGuyActionPayload`
  - 选择干扰目标。

这样可以避免房间列表也收到每帧 racer 数据。

### 8.4 客户端插值模型

新增客户端类：`ClientRaceInterpolator`。

保存：

- previous racer snapshot。
- current racer snapshot。
- previous receive time。
- current receive time。
- local render time。

渲染时：

- 位置线性插值。
- direction 用 current。
- tripping/jumping 用 current，但 height 可插值。
- frame 本地按移动距离或时间推进，不能只等服务端布尔值。

边界：

- 如果 racer 刚冲线或重置，检测距离过大，直接 snap，不插值穿屏。
- 如果超过 500ms 没收到包，继续用最后速度做极短预测，然后停住。

### 8.5 地图烘焙升级

当前脚本已经能从 TMX 烘焙一张合成图，但极致版要分层。

脚本升级目标：

- 读取 TMX。
- 输出：
  - `race_track_back.png`
  - `race_track_front.png`
  - `race_track_full.png`
  - `desert_racers.png`
  - UI 裁切资源。
- 输出一个 JSON metadata：
  - cropX。
  - cropY。
  - cropWidth。
  - cropHeight。
  - mapTileSize。
  - raceTrack local points。

这样 Java 不再手写 crop 原点，避免坐标错乱。

## 9. 服务端语义细节

### 9.1 比赛时间

原版：被动沙漠节开放时，每 2 小时开一场，`timeOfDay % 200 == 0 && timeOfDay < 2400`。

StardewCraft 当前时间系统是分钟，不是 HHMM。因此要明确映射：

- 6:00 = 360 分钟。
- 8:00 = 480。
- 10:00 = 600。
- 12:00 = 720。
- 14:00 = 840。
- 16:00 = 960。
- 18:00 = 1080。
- 20:00 = 1200。
- 22:00 = 1320。

极致版应写清楚：

- 沙漠节开放后的第一场是否从 10:00 开。
- 最后一场是否 22:00。
- 当前 `START_TIME = 600` 是否正确对齐本项目沙漠节开放时间。

不要用“分钟取模 120”糊弄原版 HHMM 语义，必须按项目 FestivalService 的开放时间明确写死或由 festival schedule 提供。

### 9.2 奖励

单人原版：

- 猜中普通 racer：20 三花蛋。
- 猜中 racer 3 且从未领过特殊奖励：100 三花蛋。
- racer 3 特殊奖励只领一次。
- 奖励通过 Race Man 领取。

多人房间：

- 不改变单人原版奖励。
- 多人下注奖励来自房间奖池。
- 房间奖池赔付也通过 Race Man 或房间详情领取。

### 9.3 干扰

- 每位玩家每场最多安排一次。
- 花费 1 三花蛋。
- 比赛开始前有效。
- 比赛开始时汇总到 racer.sabotages。
- racer trip chance 使用原版近似：`min(0.05 + sabotages * 0.2, 0.5)`，并只在非最后一名等条件满足时触发。

### 9.4 房间清理

- 非沙漠节日清理所有房间。
- 每天开始清理未结算的临时房间。
- 已结算但有玩家未领奖的房间保留到当天结束。
- 沙漠节结束后未领多人奖池如何处理必须明确：建议当天有效，过夜清空。

## 10. 交互入口规划

### 10.1 Race Man

Race Man target id：`desert_festival_race_man`。

行为：

- 打开 Race Hub。
- 如果有奖励，Hub 上突出显示领取。
- 如果比赛进行中，可直接进入观赛。

### 10.2 Shady Guy

Shady Guy target id：`desert_festival_shady_guy`。

行为：

- 打开 Shady Guy 对话/界面。
- 不打开 Race Hub。
- 不显示多人房间。

### 10.3 世界提示

Portal hint：

- Race Man：显示“沙漠赛跑”。
- Shady Guy：显示“可疑男人”。
- 两者 icon/style 不要都当普通传送门。

## 11. 验收标准

### 11.1 视觉验收

- 第一屏看起来像星露谷节日小游戏，不像调试 UI。
- 没有大面积纯黑调试面板。
- 按钮、卡片、房间列表有统一像素风格。
- 文本不挤、不重叠、不解释一堆废话。
- 单人、多人、观赛、Shady Guy 四个场景边界清楚。

### 11.2 动画验收

- racer 在整场比赛中始终可见。
- racer 移动平滑。
- 冲线、跳跃、摔倒不闪烁。
- 低网络同步频率下也不明显卡顿。

### 11.3 声音验收

- 打开、关闭、点击、竞猜、下注、领奖都有反馈。
- Ready/Set/Go 有明确节奏。
- 冲线和胜者公布有反馈。
- 音效不过度吵闹。

### 11.4 语义验收

- 单人竞猜不消耗三花蛋。
- Shady Guy 干扰只在 Shady Guy 入口。
- 多人房间奖池独立。
- 同一场比赛结果同时服务单人竞猜和所有多人房间。
- racer 3 特殊奖励只触发一次。
- 比赛进行中不能改猜、不能新下注、不能新干扰。

### 11.5 技术验收

- `./gradlew classes` 通过。
- 语言 JSON 通过解析，无重复 key。
- asset bake 脚本可重复运行。
- 不运行时引用原版大图。
- GUI 在 16:9、16:10、小窗口、GUI scale 2/3/4 下不重叠。

## 12. 实施顺序

### 阶段一：停止当前 GUI 扩写，拆出正确界面结构

目标：先把体验边界修正确。

任务：

- 删除或废弃当前单屏调试式 `DesertFestivalRaceScreen` 的控制区设计。
- 新建 Hub、SingleBet、RoomList、RoomDetail、Watch、ShadyGuy 六个界面。
- Race Man 只打开 Hub。
- Shady Guy 只打开 ShadyGuy。
- 单人界面不出现多人和干扰。
- 多人列表不出现 racer 高频渲染。
- 观赛界面不出现管理按钮。

验证：

- 能从 Race Man 正确进入单人/多人/观赛。
- 能从 Shady Guy 正确进入干扰。
- UI 流程不混。

### 阶段二：重做地图和 racer 渲染

目标：解决可见性和卡顿。

任务：

- 升级 bake 脚本输出 back/front/full/metadata。
- 实现 `RaceViewportTransform`。
- 实现 `RaceViewportRenderer`。
- 实现客户端插值。
- racer 两层绘制。
- 阴影、跳跃、摔倒完整。

验证：

- 比赛全程 racer 不消失。
- 录屏检查移动平滑。
- 调低同步频率仍顺。

### 阶段三：星露谷风格 UI 美术落地

目标：去掉调试感。

任务：

- 裁切/绘制 SDV 风格按钮和卡片资源。
- 重做 Hub。
- 重做 racer 卡。
- 重做房间列表卡。
- 重做房间详情下注区。
- 做 disabled/hover/selected 状态。

验证：

- 截图评审，第一眼像正式小游戏。
- 不同窗口尺寸无重叠。

### 阶段四：声音和特效

目标：让模块有游戏感。

任务：

- 梳理可用 `ModSounds`。
- 缺失声音则新增独立 ogg 并注册。
- UI 操作音接入。
- Ready/Set/Go/Finish/Winner 接入。
- 下注、领奖、失败提示接入。
- 本地 GUI 粒子接入。

验证：

- 每个关键操作都有合适反馈。
- 连续比赛不吵。

### 阶段五：多人房间完善

目标：让多人下注像正常游戏房间。

任务：

- 房间列表轻量 payload。
- 房间详情 payload。
- 房间状态持久化。
- 玩家断线重连恢复。
- 房主锁房/开始权限。
- 奖池赔付和领取。

验证：

- 两个客户端能创建、进入、下注、观赛、结算、领奖。
- 房间比赛中不能下注。
- 断线重进仍显示自己的下注和奖励。

### 阶段六：原版语义对齐复查

目标：把细节补到位。

任务：

- 复查 `DesertFestival.cs` race state。
- 复查 `Racer.cs` 移动。
- 复查所有 Race_*、Shady_Guy_* 文本。
- 复查特殊奖励。
- 复查最后一场关闭逻辑。

验证：

- 与原版源码逐条对照。
- 写一份 race parity checklist。

## 13. 需要明确的设计取舍

### 13.1 多人房间是否允许手动提前开赛

推荐：允许，但它启动的是官方 race countdown，不是单独模拟赛。

理由：

- 用户期待多人房间有“开始”按钮。
- 但多场独立比赛会让全局观赛、单人竞猜、Shady Guy 干扰全部变复杂。

### 13.2 多个房间同时押同一场

推荐：允许。

理由：

- 房间只是独立奖池。
- 结果来自同一场比赛。

### 13.3 房间是否需要密码

首个极致版不做密码。

理由：

- MC 小服场景优先。
- 可以后续加，但不影响“正常房间流程”。

### 13.4 是否显示复杂赔率公式

不显示公式。

只显示：

- 总奖池。
- 每个 racer 当前池。
- 当前赔率。
- 自己已下注多少。

## 14. 最终文件目标

### 14.1 Java 新增/重构

- `DesertFestivalRaceService`
- `DesertFestivalRaceRoomService` 或 RaceService 内部 room 模块
- `ClientRaceInterpolator`
- `RaceViewportTransform`
- `RaceViewportRenderer`
- `DesertFestivalRaceHubScreen`
- `DesertFestivalRaceSingleBetScreen`
- `DesertFestivalRaceRoomListScreen`
- `DesertFestivalRaceRoomScreen`
- `DesertFestivalRaceWatchScreen`
- `DesertFestivalShadyGuyScreen`
- `RaceButton`
- `RaceRacerCard`
- `RaceAmountStepper`

### 14.2 Payload

- `OpenDesertFestivalRaceHubPayload`
- `DesertFestivalRaceCommonStatePayload`
- `DesertFestivalRaceRacerStatePayload`
- `OpenDesertFestivalShadyGuyPayload`
- `DesertFestivalShadyGuyActionPayload`
- `DesertFestivalRaceRoomListPayload`
- `DesertFestivalRaceRoomDetailPayload`
- `DesertFestivalRaceActionPayload`

### 14.3 资源

- `race_track_back.png`
- `race_track_front.png`
- `race_track_full.png`
- `race_metadata.json`
- `desert_racers.png`
- `race_panel_wood.png`
- `race_button.png`
- `race_button_hover.png`
- `race_button_disabled.png`
- `race_card.png`
- `race_card_selected.png`
- `race_room_card.png`
- `race_room_card_selected.png`
- `race_badge_guess.png`
- `race_badge_bet.png`
- `race_badge_winner.png`
- `race_amount_stepper.png`
- `race_close_button.png`

## 15. 不再重复犯的硬规则

- 不把所有功能堆进一个 GUI。
- 不把 Shady Guy 功能放进 Race Man 界面。
- 不把网络刷新率当动画帧率。
- 不用调试矩形当正式 UI。
- 不让玩家看不懂下一步。
- 不在运行时引用原版大图。
- 不为了“先能用”牺牲星露谷风格。
- 不新增帽子、家具。

## 16. 最终玩家路径示例

### 16.1 单人竞猜

Race Man -> 沙漠赛跑 Hub -> 单人竞猜 -> 选“极速公鸡” -> 确定 -> Hub 显示已竞猜 -> 观赛 -> 比赛结束 -> 猜中则领取 20 三花蛋。

### 16.2 Shady Guy

Shady Guy -> 对话 -> 花 1 三花蛋 -> 选“毒刺蝎王” -> 显示已安排 -> 下一场比赛开始时该 racer trip chance 增加。

### 16.3 多人下注

Race Man -> 沙漠赛跑 Hub -> 多人下注 -> 房间列表 -> 创建房间 -> 房间详情 -> 选择 racer -> 设置 5 三花蛋 -> 下注 -> 等待其他玩家 -> 锁房 -> 观赛 -> 结算 -> 领取奖池赔付。

### 16.4 加入别人房间

Race Man -> 多人下注 -> 房间列表 -> 选择“Alex 的房间” -> 加入 -> 房间详情 -> 查看赔率 -> 下注 -> 观赛 -> 结算。

## 17. 规划验收句

重做完成后，玩家不应该感觉自己在操作一个临时调试菜单，而应该感觉自己站在沙漠节赛道旁，先去 Race Man 那里报名竞猜或进房间下注，再去 Shady Guy 那里偷偷做手脚，最后坐下来看一场顺滑、有声音、有结果反馈的星露谷节日赛跑。