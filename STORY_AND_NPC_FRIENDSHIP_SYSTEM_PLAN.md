# 剧情与 NPC 好感系统复刻规划

> 目标：将 Stardew Valley 的主线剧情、NPC 日常对话、心事件、好感规则、信件与剧情状态尽可能按原版体验复刻到 StardewCraft。
> 状态：规划草案，基于现有 cutscene / NPC / mail 系统调研。
> 最后更新：2026/05/20

---

## 1. 总目标与硬约束

### 1.1 复刻目标

- 复刻对象包括：主线剧情、地点事件、早晨事件、NPC 心事件、日常对话、送礼反馈、好感奖励邮件、任务/解锁邮件、剧情 mail flags、特殊 NPC 交互。
- 玩家体验目标不是“有类似功能”，而是尽量接近原版剧情节奏：触发条件、镜头调度、NPC 走位、站位、朝向、对白顺序、音乐、音效、表情、粒子、画面淡入淡出、奖励和后续状态都要逐条核对。
- 项目已有 cutscene runtime 是基础设施；后续大头是内容迁移、事件分镜设计、音画资源对齐、坐标落点确认、多人语义和验收。

### 1.2 Source-of-truth 约束

- 所有剧情逻辑以 `源文件/` 下原版 C# 源码和 `Content` 数据为准。
- 实现某条剧情前，必须先阅读对应原版事件数据、相关 C# handler、NPC 对话、邮件、音频和状态变更逻辑。
- 不允许只凭 wiki、记忆或当前项目已有简化版本补剧情；wiki 只能作为索引，不作为最终依据。
- 发现项目当前实现与原版不一致时，先记录差异，再按原版语义修正或明确标记为暂不复刻。

### 1.3 逐条剧情人工复刻约束

- 不把原版事件批量转换成项目 JSON 后直接上线。
- 自动化脚本只允许用于清单、统计、缺口报告、预条件/命令覆盖率分析；不能替代人工分镜设计。
- 每条剧情都要单独形成“剧情设计卡”，至少包含：原版事件 id、触发地点、触发条件、演员表、镜头、走位、对白、音乐、音效、视觉效果、状态变更、奖励、坐标需求、多人语义和验收步骤。
- NPC 走位、镜头路径、站位、朝向、临时演员位置不能从原版 tile 坐标或旧坐标推算；必须用 StardewCraft 已确认坐标或走坐标迁移流程。
- 缺少坐标、资产、音效、模型或无法还原的交互时，该剧情不能用临时占位糊过去，必须记录阻塞项并等待确认。

### 1.4 多人与存档语义约束

- 默认按玩家个人保存：已看剧情、NPC 好感、日常对话次数、送礼限制、个人邮件、个人 mail flags、个人 heart event 触发。
- 默认按世界/队伍共享：社区中心/Joja 进度、博物馆全局陈列基础状态、公共地图解锁、真正的世界状态。
- 剧情事件不要新增 `is_host` 作为偷懒条件；非 host 玩家满足个人剧情条件时也应该能触发个人剧情。
- 服务器必须是权威状态写入方；客户端 cutscene 只负责表现与输入。

### 1.5 邮件与 flag 约束

- 可读信件必须注册到 `MailRegistry` 可加载的数据中，才能进入 mailbox。
- 纯剧情状态或解锁状态应使用 mailFlags / tomorrow flags，不得把未注册 mail id 塞进 mailbox。
- 每条原版 `mail.json` 条目都要分类：可读信、配方信、任务信、邀请信、感谢信、无信状态 flag、多人共享状态。

### 1.6 资产与音画约束

- 音乐、音效、UI、肖像、临时 sprite、粒子和物品图标优先从 `源文件/Content`、`源文件/音频文件` 和项目已有资源体系迁移。
- 不做临时音效、临时贴图、临时分镜替代；缺资产时记录资产需求。
- 每条剧情验收时要明确：播放的音乐/音效是否与原版一致，缺失项是否有记录。

---

## 2. 当前事实基线

### 2.1 原版内容规模

| 内容 | 原版规模 | 当前项目规模 | 结论 |
| --- | ---: | ---: | --- |
| `Content/Data/Events` 基础事件 | 45 个文件 / 258 条事件 | 12 个 cutscene JSON | 剧情主体仍待逐条迁移 |
| `Content/Characters/Dialogue` 日常对话 | 52 个文件 / 3301 个 key | 35 个文件 / 2020 个 key | 已有一批，但仍需 NPC-by-NPC 对齐 |
| `Content/Data/mail.json` 邮件 | 179 条 | 116 条项目邮件 | 需要完整分类对照 |

这些数字只表示清单规模，不表示可以自动迁移。真正工作量在每条事件的分镜、声音、走位、坐标和状态语义。

### 2.2 原版源代码锚点

- `源文件/StardewValley/GameLocation.cs`：地点事件扫描与 `checkEventPrecondition` 入口。
- `源文件/StardewValley/Event.cs`：事件 key 拆分、事件命令解析、命令 handler、预条件 handler 注册。
- `源文件/StardewValley/Preconditions.cs`：原版事件预条件别名与判断逻辑。
- `源文件/StardewValley/NPC.cs`：NPC 交互、送礼、礼物反馈、生日、花束、婚恋、特殊物品交互。
- `源文件/StardewValley/Farmer.cs`：好感变化、每日好感衰减、每周送礼奖励、事件已看、mail 状态。
- `源文件/StardewValley/Friendship.cs`：好感状态、dating/marriage/divorce 状态。
- `源文件/StardewValley/Dialogue.cs`：对话解析、性别 token、问题回答、对话事件。
- `源文件/Content/Data/Events/`：地点事件脚本。
- `源文件/Content/Characters/Dialogue/`：NPC 日常对话、礼物反馈、特殊对话。
- `源文件/Content/Data/mail.json`：原版邮件与 mail flag 数据。
- `源文件/Content/Data/NPCGiftTastes.json`：礼物口味与 NPC-specific response。

### 2.3 项目已有基础设施

- `src/main/java/com/stardew/craft/cutscene/data/EventRegistry.java`：加载和索引 cutscene JSON。
- `src/main/java/com/stardew/craft/cutscene/data/EventData.java`：事件数据模型。
- `src/main/java/com/stardew/craft/cutscene/data/EventTrigger.java`：触发器模型，目前覆盖 `manual`、`enter_area`、`interact_npc`、`time_check`、`wake_up` 等使用场景。
- `src/main/java/com/stardew/craft/cutscene/runtime/PreconditionEvaluator.java`：客户端预条件判断。
- `src/main/java/com/stardew/craft/cutscene/server/ServerPreconditionEvaluator.java`：服务端预条件判断。
- `src/main/java/com/stardew/craft/cutscene/command/EventCommandFactory.java`：项目 cutscene 命令注册。
- `src/main/java/com/stardew/craft/cutscene/server/WakeUpEventScheduler.java`：早晨事件队列与离线补发。
- `src/main/java/com/stardew/craft/npc/runtime/NpcInteractionService.java`：NPC 对话、送礼、heart event 入口、礼物反馈。
- `src/main/java/com/stardew/craft/npc/runtime/NpcFriendshipDataManager.java`：每玩家 NPC 好感持久化。
- `src/main/java/com/stardew/craft/npc/runtime/NpcFriendshipRewardService.java`：好感阈值奖励。
- `src/main/java/com/stardew/craft/mail/MailRegistry.java`：数据驱动邮件注册。
- `src/main/java/com/stardew/craft/mail/MailService.java`：邮件投递、明日邮件、flag 与附件发放。

---

## 3. 剧情设计卡规范

每条剧情在落地前都要建一张设计卡。设计卡可以先放在后续 ledger 文档中，字段如下。

| 字段 | 要求 |
| --- | --- |
| 原版来源 | location 文件、event key、event id、相关 C# handler |
| 剧情类型 | 主线 / 地点事件 / NPC 心事件 / 早晨事件 / 邮件邀请 / 特殊交互 |
| 触发条件 | 原版 preconditions、项目表达方式、缺失条件 |
| 触发方式 | enter area / interact npc / wake up / manual / time check / special |
| 演员表 | 玩家、NPC、临时 actor、隐藏/显示规则 |
| 场景区域 | StardewCraft location、AABB、入口点、离场点、阻塞坐标 |
| 镜头设计 | 初始镜头、跟随目标、pan/zoom/停顿、淡入淡出 |
| 走位设计 | 每个 actor 的路径、朝向、速度、等待点、失败处理 |
| 对白 | 原版对白 key、项目翻译 key、问题分支、回答影响 |
| 音乐/音效 | 原版 music cue、sound cue、播放时机、缺失资产 |
| 视觉效果 | emote、jump、shake、screen flash、particle、临时 sprite |
| 状态变更 | seen event、mail/mailFlag、friendship、quest、item、recipe、unlockSource |
| 多人语义 | per-player / shared、旁观玩家处理、离线玩家处理 |
| 验收步骤 | 单人触发、服务器非 host 触发、重复触发拦截、跳过剧情后状态 |
| 阻塞项 | 坐标、资产、命令、预条件、运行时能力缺口 |

设计卡是后续实现和验收的最小单位。没有设计卡，不开始写该剧情 JSON。

---

## 4. Event DSL parity 规划

### 4.1 预条件覆盖

原版事件预条件由 `Preconditions.cs` 统一处理，支持别名、取反和复杂状态查询。项目当前已覆盖一部分常用条件，但批量剧情落地前需要做差异表。

优先补齐：

- 已看/未看事件：`e` / `!e`。
- 本地 mail、host/world mail、host-or-local mail：`n`、`Hn`、`*n` 及取反语义。
- 好感：`f NPC points`，支持同一条件内多个 NPC。
- 时间、天气、日期、星期、季节、年份：`t`、`w`、`u`、`DayOfWeek`、`Season`、`y`。
- 金钱：当前持有 `M` 与累计赚取 `m` 要区分。
- 世界状态：社区中心、Joja、矿洞底层、物品持有、对话回答、active dialogue event。
- NPC 可见性与当前位置：`v`、`p`。
- 技能：`Skill name minLevel`。

硬要求：未知 precondition 不能默认通过。迁移阶段应至少输出 warning report；进入正式验收后应视为该事件不可触发或构建失败。

### 4.2 命令覆盖

项目命令已支持大量常规 cutscene 表现，但原版事件还需要按清单补齐。

优先补齐：

- 对话分支：`question`、`quickQuestion`、`$q/$r`、`fork`、`splitSpeak` 的等价表达。
- 对话状态：conversation topic、active dialogue event、dialogue question answered。
- 演员运动：高级移动、路径队列、朝向、隐藏/显示、临时演员生命周期。
- 音画表现：temporary sprite、screen flash、shake、emote、jump、ambient light、music cue、sound cue。
- 奖励与状态：add/remove quest、award item、money、recipe、mail、mail for tomorrow、no-letter flag、friendship delta、unlock source。
- 地点变化：warp、location action、门/通道解锁、特殊区域开关。

原则：先补 runtime 能力，再写依赖该能力的事件；不要在单个 JSON 里用专用 hack 绕过通用命令缺口。

---

## 5. NPC 好感与日常对话规划

### 5.1 好感规则 parity

项目已有每玩家好感、送礼限制、生日倍率、星之果茶、礼物口味和好感奖励入口。后续需要逐项对齐原版：

- 每日未对话好感衰减。
- 每周送满两次的 +10 奖励。
- 每日一次、每周两次送礼限制的所有例外。
- Dwarf 语言未解锁时的对话/礼物好感语义。
- divorced / dating / spouse / roommate 状态。
- datable NPC 8 心 cap、Bouquet 解锁、10 心 cap、Mermaid Pendant / roommate proposal。
- 配偶好感变化倍率和嫉妒逻辑。
- gift taste 的 universal / NPC-specific / context tag / item id 优先级。
- 礼物反馈对白、生日反馈、特殊物品拒收对白。

### 5.2 日常对话迁移

日常对话不是简单把 key 搬过来。每个 NPC 都要按原版优先级核对：

- Introduction。
- 天气特殊对话。
- 季节 + 日期。
- 季节 + 星期。
- 星期。
- 日期。
- 季节默认。
- 好感阈值变体。
- 特殊 mail flag / event flag / active dialogue event 对话。
- 礼物反馈和拒收对话。
- 问答分支和 friendship delta。

推荐按 NPC 做 dossier：一个 NPC 一次性核对 dialogue、gift taste、schedule interaction、heart events、mail rewards，而不是全项目横向乱补。

### 5.3 好感奖励与邮件

现有 `friendship_rewards.json` 已有一批配方邮件规则，但需要与原版 `mail.json` 完整对照。

每条奖励邮件都要确认：

- 触发 NPC 与 heart 阈值。
- 是否需要 cooking recipe / crafting recipe。
- 是否立即邮件、明日邮件或只置 flag。
- 是否 once-only。
- 是否受前置剧情、季节、年份或 mail flag 影响。
- 老存档已有 flag 时是否能正确跳过。

---

## 6. 分阶段实施路线

### 阶段 0 — 总账与诊断工具

目标：先知道全部剧情和系统缺口，不进入盲写。

产物：

- 新建剧情迁移 ledger，列出原版 258 条事件的 id、location、类型、NPC、preconditions、关键命令、当前迁移状态。
- 新建 NPC dossier 清单，列出每个 NPC 的 dialogue、gift taste、heart events、mail rewards、special interactions 覆盖状态。
- 新建 mail parity 清单，分类 179 条原版邮件。
- 增加只读诊断命令或脚本：输出未知 precondition、未知 event command、未注册 mail id、缺坐标、缺音效、缺 asset。

验收：能一眼看到哪些剧情已完成、哪些缺 runtime、哪些缺坐标/资产、哪些不能开始。

### 阶段 1 — Cutscene DSL 与预条件安全化

目标：让项目 runtime 能稳定承载原版常见事件。

工作：

- 补齐高频 precondition。
- 未知 precondition 从默认通过改为诊断/阻断。
- 补齐 question/fork/splitSpeak/quickQuestion 等分支能力。
- 补齐常见 state command：conversation topic、dialogue answered、mail flag、quest、money、award item。
- 补齐常见表现 command：advanced movement、temporary sprite、音效/音乐 cue、screen flash、emote、jump。

验收：P0 主线事件不需要 per-event hack 就能表达。

### 阶段 2 — P0 主流程剧情

目标：先把影响正常游戏流程的剧情闭环做稳。

优先范围：

- 社区中心开门、Junimo note、法师线、社区中心/Joja 关键状态。
- 矿洞 intro、Marlon、Adventurer Guild、Rusty Sword 等早期推进。
- Willy 钓竿、Clint 熔炉、Demetrius 洞穴选择、Gunther/Rusty Key、下水道开启。
- Pierre / Robin / Marnie / Wizard 等关键系统解锁邮件。
- 所有已存在项目剧情 JSON 的原版复核与补差。

验收：新档从春 1 到核心系统解锁，剧情触发、状态、邮件、奖励、重复拦截都接近原版。

### 阶段 3 — NPC 好感核心规则

目标：先让好感数值和限制可信，再大规模上心事件。

工作：

- 对齐每日/每周好感规则。
- 对齐礼物口味和礼物反馈。
- 对齐生日、特殊礼物、拒收物品。
- 对齐 datable cap、Bouquet、Mermaid Pendant 的基础状态机。
- 补齐 social tab 显示/隐藏与 Introductions 语义。

验收：单个 NPC 的送礼、对话、好感上限、奖励邮件不会偏离原版核心规则。

### 阶段 4 — NPC-by-NPC 内容复刻

目标：一个 NPC 一个 NPC 做完整体验包。

每个 NPC 包含：

- 日常 dialogue parity。
- gift taste parity。
- 礼物反馈与特殊对话。
- 2/4/6/8/10 心等 heart events。
- 好感奖励邮件和配方。
- 与 schedule/location 相关的触发条件。
- 验收场景与回归记录。

推荐顺序：

1. 非婚恋但系统关键 NPC：Lewis、Robin、Pierre、Marnie、Clint、Willy、Gunther、Wizard、Marlon。
2. 早期高频村民：Linus、Caroline、Demetrius、Jodi、Pam、Gus、Evelyn、George。
3. 可恋爱 NPC 第一批：Abigail、Sebastian、Leah、Penny、Haley、Alex。
4. 可恋爱 NPC 第二批：Sam、Maru、Emily、Shane、Elliott、Harvey。
5. 特殊 NPC：Dwarf、Krobus、Sandy、Morris、Joja Cashier 等。

验收：每完成一个 NPC，都应能从 0 心玩到当前项目支持的最高心数，不出现缺信、缺对白、误触发或重复触发。

### 阶段 5 — 婚恋、室友与高阶社交

目标：在基础 heart events 稳定后，再做婚恋系统。

工作：

- Bouquet / Wilted Bouquet。
- Mermaid Pendant / roommate proposal。
- Engagement dialogue。
- Marriage / spouse room / spouse dialogue。
- Divorce / ex-spouse dialogue。
- Jealousy / spouse gift reaction。

验收：婚恋状态不会污染普通好感剧情，已婚/离婚/室友分支对白与礼物反馈正确。

### 阶段 6 — 后期剧情与特殊系统

目标：处理不适合早期混入的大型内容。

范围：

- Special Orders 相关剧情。
- Ginger Island / Walnut / late-game world state。
- Movie Theater / Desert Festival / Night Market 等与节日或大系统绑定的剧情。
- Leo、Qi、后期 Joja/CC 分支内容。

验收：这些内容必须等对应系统本体存在后再做，不能提前用 flag 假装完成。

---

## 7. 验收标准

单条剧情验收：

- 原版来源已记录。
- 所有预条件项目可识别，未知项为 0。
- 所有命令项目可表达，无 per-event hack。
- 所有坐标来自已确认坐标体系或 ledger。
- 镜头、走位、对白、音乐、音效、视觉效果已逐项核对。
- 状态变更与奖励在服务端执行。
- 可跳过剧情时，关键状态仍正确落地。
- 已看后不会重复触发。
- 非 host 玩家在服务器上能按个人条件触发。
- 离线/重登不会丢失待发邮件、早晨事件或 seen event。

NPC 包验收：

- 日常对话 priority 与原版一致或差异已记录。
- gift taste 与特殊反馈已对齐。
- 好感阈值、奖励邮件、配方解锁正确。
- 心事件按顺序、条件和已看状态触发。
- 老存档默认值安全，不会生成空好感状态导致误判。

全局验收：

- 剧情 ledger 可显示完成度。
- mail registry 缺失为 0。
- unknown precondition / command 报告为 0。
- 坐标缺口和资产缺口都有明确状态。
- `./gradlew classes --stacktrace` 通过。

---

## 8. 风险与处理策略

| 风险 | 表现 | 处理 |
| --- | --- | --- |
| 原版事件数量大 | 258 条事件逐条审会很慢 | ledger 管理，按 P0/P1/P2 分批，不跳阶段 |
| 原版 DSL 与项目 DSL 不一致 | JSON 写不出原版语义 | 先补通用命令和预条件，再迁内容 |
| 坐标缺失 | NPC 站位/走位/镜头无法还原 | 进入坐标 ledger，不猜、不推算 |
| 音效/音乐缺失 | 分镜氛围不对 | 建 asset 缺口表，等待迁移或确认 |
| 多人误触发 | host-only、共享状态污染个人剧情 | 默认 per-player，世界状态单独列明 |
| 邮件卡死 | 未注册 mail id 进入 mailbox | 可读信必须注册，状态用 flags |
| 批量迁移误触发 | unknown precondition 默认通过 | 未知条件必须诊断/阻断 |

---

## 9. 推荐下一步

1. 新建 `STORY_EVENT_MIGRATION_LEDGER.md`，先把 258 条原版事件列成总账，不实现剧情。
2. 新建只读统计/诊断脚本，输出每条事件使用的 precondition 和 command 频率。
3. 从 P0 主流程中选 3 条已有项目 JSON 做反向审计：对照原版分镜、声音、走位、状态，形成第一批设计卡模板。
4. 根据审计结果补 Cutscene DSL 的最高频缺口。
5. 再开始逐条复刻 P0 主流程剧情。

这套流程会慢，但它能保证剧情不是“看起来有”，而是真的按原版体验逐条落地。