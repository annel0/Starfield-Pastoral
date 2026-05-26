# 剧情 Event DSL 缺口报告

> 目标：对照原版 `Content/Data/Events` 的 258 条故事事件，评估 StardewCraft 当前 cutscene DSL 与预条件系统的缺口。
> 状态：阶段 0 诊断文档，供后续补 runtime 能力和逐条剧情设计卡使用。
> 最后更新：2026/05/20

---

## 1. 结论摘要

当前项目已经有可用的 cutscene runtime，但它使用的是 StardewCraft 自己的 JSON DSL；原版事件使用的是 Stardew Valley 脚本 token。两者不能直接等号对应。

因此后续路线不是“把原版脚本批量转换后运行”，而是：

1. 用本报告识别高频 precondition / command 缺口。
2. 先补 P0 runtime 能力。
3. 每条剧情仍按 `STORY_EVENT_MIGRATION_LEDGER.md` 建设计卡，人工设计分镜、音效、走位和对白。
4. 再把设计卡写成项目 JSON cutscene。

最急的缺口有两类：

- **安全性缺口**：客户端和服务端 precondition evaluator 当前 unknown default 都是 `true`。大规模迁移时，这会造成剧情误触发。
- **语义缺口**：原版大量使用 `p/o/O/z/d/A/Hn/Hl/y/i/G` 等预条件，以及 `faceDirection/showFrame/positionOffset/specificTemporarySprite/textAboveHead/fork/quickQuestion/switchEvent/advancedMove/addConversationTopic/addWorldState/removeQuest/removeItem/broadcastEvent` 等命令语义，项目需要通用表达层或明确的人工映射规范。

---

## 2. 数据来源

- 原版事件数据：`源文件/Content/Data/Events/*.json`，基础事件 258 条。
- 当前迁移总账：`STORY_EVENT_MIGRATION_LEDGER.md`。
- 项目命令工厂：`src/main/java/com/stardew/craft/cutscene/command/EventCommandFactory.java`。
- 客户端预条件：`src/main/java/com/stardew/craft/cutscene/runtime/PreconditionEvaluator.java`。
- 服务端预条件：`src/main/java/com/stardew/craft/cutscene/server/ServerPreconditionEvaluator.java`。

---

## 3. 当前项目已支持能力

### 3.1 Trigger 基础

项目已有的事件触发方向可以覆盖一部分原版场景：

| 项目 Trigger | 用途 | 备注 |
| --- | --- | --- |
| `enter_area` | 进入区域触发 | 适合 Town/Beach/Forest 等地点事件，但坐标必须人工确认。 |
| `interact_npc` | 与 NPC 交互触发 | 适合心事件入口。 |
| `wake_up` | 起床/早晨事件 | 适合 Farm/FarmHouse 早晨事件。 |
| `manual` | 手动触发 | 适合系统或剧情专用入口。 |
| `time_check` | 时间检查 | 可用于少量特殊时间事件。 |

缺口：原版 tile precondition `a`、NPC visible `p/v`、location-specific action 等还不能完整由 trigger 取代。

### 3.2 当前 JSON Command

项目当前显式支持的 JSON command 包括：

| 类型 | 已有命令 |
| --- | --- |
| 流程 | `lock_player`, `unlock_player`, `pause`, `skippable`, `end`, `comment`, `simultaneous` |
| 演员 | `spawn_actor`, `remove_actor`, `move_player`, `move_actor`, `face_actor`, `animate`, `hide_npc`, `show_npc`, `warp`, `hold_item` |
| 镜头/画面 | `camera`, `camera_follow`, `camera_shake`, `fade`, `screen_flash`, `shake_actor`, `particle` |
| 音频 | `play_sound`, `music`, `stop_music` |
| 对话 | `speak`, `message`, `question` |
| 状态/奖励 | `add_quest`, `set_flag`, `grant_rusty_key`, `mark_opened_sewer`, `add_friendship`, `add_item`, `add_mail`, `add_mail_now`, `add_mail_for_tomorrow`, `add_recipe`, `apply_unlock_source`, `set_cave_choice` |
| 实体/特殊 | `spawn_entity`, `teleport_cc` |

### 3.3 当前 Precondition

项目当前显式支持的 JSON precondition 包括：

| JSON precondition | 对应原版方向 | 备注 |
| --- | --- | --- |
| `friendship` | `f` | 单 NPC 好感点数。原版可在一个条件里列多个 NPC。 |
| `saw_event` / `not_saw_event` | `e` / `k` / `!e` | 语义基本可表达。 |
| `time` | `t` | 可表达时间范围。 |
| `season` | `Season` | 只能表达正向季节；原版 `z` 是 NotSeason。 |
| `weather` | `w` | 可表达 sunny/rainy 等。 |
| `day_of_week` | `DayOfWeek` | 只能表达正向星期；原版 `d` 是 NotDayOfWeek。 |
| `day_of_month` | `u` | 可表达单日；多日需扩展。 |
| `days_played` | `j` | 语义需确认 `>` / `>=` 边界。 |
| `money` | `M` 或部分 `m` | 当前是持有金币；原版 `m` 是累计赚取。 |
| `skill` | `Skill` | 可表达基础技能等级。 |
| `mail` / `not_mail` | `n` / `l` | 当前读取 mailFlags；可读信与状态 flag 要区分。 |
| `flag` / `not_flag` | mail flag / active flag | 需要统一命名规范。 |
| `is_host` | `H` | 不建议继续新增依赖；剧情默认应 per-player。 |

---

## 4. 原版 Precondition 缺口

### 4.1 高频 token

| Token | 次数 | 原版含义 | 当前状态 | 优先级 |
| --- | ---: | --- | --- | --- |
| `f` | 132 | 好感点数 | 部分支持 | P0 |
| `t` | 98 | 时间范围 | 支持 | P0 |
| `e` | 61 | 已看事件 | 支持 | P0 |
| `p` | 53 | NPC 当前地点可见 | 缺 | P0 |
| `o` | 52 | 非配偶/未婚约该 NPC | 缺 | P1 |
| `w` | 47 | 天气 | 支持 | P0 |
| `n` | 28 | 本玩家 mail/flag | 支持，但需注册规则 | P0 |
| `O` | 28 | 配偶/婚约 NPC | 缺 | P1 |
| `z` | 20 | 不是某季节 | 缺 not-season | P0 |
| `d` | 18 | 不是某星期 | 缺 not-day-of-week | P0 |
| `A` | 15 | 没有 active dialogue event | 缺 | P1 |
| `H` | 14 | host/master game | 有但应避免 | P0 |
| `Hl` | 11 | host 没有 mail | 缺 shared/host mail 语义 | P0 |
| `Hn` | 11 | host 有 mail | 缺 shared/host mail 语义 | P0 |
| `y` | 8 | 年份 | 缺 | P0 |
| `k` | 8 | 未看事件 | 支持等价 `not_saw_event` | P0 |
| `i` | 7 | 玩家持有物品 | 缺 | P0 |
| `j` | 7 | 游戏天数 | 支持，需边界核对 | P0 |
| `L` | 5 | 房屋升级等级 | 缺 | P1 |
| `a` | 4 | 玩家站在指定 tile | 缺；应转 AABB/anchor | P0 |
| `m` | 4 | 累计赚取金币 | 缺，当前 `money` 是持有金币 | P0 |
| `G` | 3 | GameStateQuery | 缺 | P0/P1 |
| `l` | 2 | 本玩家没有 mail | 支持等价 `not_mail` | P0 |
| `h` | 2 | 缺宠物/宠物偏好 | 缺 | P2 |
| `D` | 2 | 正在 dating NPC | 缺 | P1 |
| `B` | 2 | 配偶床 | 缺 | P2 |
| `F` | 2 | 非节日日 | 缺 | P1 |
| `*n` | 1 | host 或本玩家有 mail | 缺 | P0 |
| `u` | 1 | 月内日期 | 部分支持 | P0 |
| `v` | 1 | NPC 任意地点可见 | 缺 | P1 |

### 4.2 P0 预条件实现建议

1. **Unknown precondition 安全化**
   - 当前 `default -> true` 必须在迁移期改成诊断模式。
   - 建议先做 report/warn，并在 dev config 下阻断触发。

2. **原版别名兼容层**
   - 增加一个 `VanillaEventPreconditionMapper` 或同等转换表。
   - 将 `f/e/k/t/w/n/l/j/u/i/y/m/Hn/Hl/*n/z/d/a/p` 转成项目 JSON 语义或明确标记无法转换。

3. **mail 语义拆分**
   - `n/l`: 玩家个人 mailFlags。
   - `Hn/Hl`: 世界/队伍共享 story flags，不能简单套 host gate。
   - `*n`: 玩家个人或共享状态任一命中。

4. **坐标/位置语义**
   - `a` 不直接迁原版 tile；转为 StardewCraft AABB 或 explicit anchor。
   - `p/v` 需要 NPC runtime 能查询当前 location 与 visibility。

---

## 5. 原版 Event Command 缺口

### 5.1 高频 command token

| Token | 次数 | 当前表达 | 缺口判断 | 优先级 |
| --- | ---: | --- | --- | --- |
| `pause` | 4375 | `pause` | 支持 | P0 |
| `faceDirection` | 1643 | `face_actor` | 需转换方向语义 | P0 |
| `speak` | 1573 | `speak` | 支持，但原版对白 token/portrait 需解析 | P0 |
| `move` | 1046 | `move_actor` / `move_player` | 需转换 actor/path/step 语义 | P0 |
| `playSound` | 794 | `play_sound` | 需音效 id 映射 | P0 |
| `showFrame` | 672 | `animate` 部分替代 | 缺精确 sprite frame 控制 | P0 |
| `positionOffset` | 669 | 无 | 缺 actor 渲染偏移 | P0 |
| `emote` | 499 | `emote` | 支持，需 id 映射 | P0 |
| `viewport` | 347 | `camera` | 需原版 viewport 语义转换 | P0 |
| `animate` | 341 | `animate` | 部分支持，需 frame 序列/loop parity | P0 |
| `warp` | 277 | `warp` | 部分支持，需 location/actor 语义 | P0 |
| `jump` | 277 | `jump` | 支持，需力度/方向调参 | P0 |
| `stopAnimation` | 253 | 无 | 缺停止动画命令 | P0 |
| `end` | 232 | `end` | 支持 | P0 |
| `farmer` | 182 | 事件 actor 概念 | 需转换为 player actor 初始化 | P0 |
| `specificTemporarySprite` | 181 | 无/`particle` 不等价 | 缺临时 sprite 系统 | P0 |
| `playMusic` | 175 | `music` | 需 music cue 映射 | P0 |
| `skippable` | 156 | `skippable` | 支持 | P0 |
| `textAboveHead` | 153 | 无 | 缺头顶文字 | P1 |
| `shake` | 145 | `shake_actor` / `camera_shake` | 部分支持，需原版对象 shake | P1 |
| `message` | 124 | `message` | 支持，需原版 message token 解析 | P0 |
| `speed` | 109 | 无 | 缺 actor 临时速度 | P0 |
| `globalFade` | 106 | `fade` | 部分支持，需全局淡入淡出流程 | P0 |
| `fork` | 58 | `question` 分支部分替代 | 缺原版 fork/switchEvent 流控制 | P0 |
| `fade` | 50 | `fade` | 支持/需核对 | P0 |
| `question` | 46 | `question` | 部分支持，需回答记录和 `$q/$r` parity | P0 |
| `advancedMove` | 41 | 无 | 缺高级移动 | P0 |
| `doAction` | 40 | 无 | 缺 location action / scripted action | P1 |
| `updateMinigame` | 35 | 无 | 缺小游戏事件桥接 | P2 |
| `quickQuestion` | 33 | 无 | 缺快速问题命令 | P0 |
| `switchEvent` | 27 | 无 | 缺切换到同文件其它事件片段 | P0 |
| `makeInvisible` | `26` | `hide_npc` 部分替代 | 缺任意 actor 隐身 | P0 |
| `glow` | `26` | 无 | 缺发光/轮廓效果 | P1 |
| `addConversationTopic` | 20 | 无 | 缺对话话题状态 | P0 |
| `changeLocation` | 19 | `warp` 部分替代 | 需语义确认 | P1 |

### 5.2 P0 命令实现建议

1. **Actor 基础 parity**
   - 增加 `faceDirection` 等价 command 或扩展 `face_actor`。
   - 增加 `stopAnimation`、`speed`、`makeInvisible`、`showFrame`、`positionOffset`。
   - 这些命令是 NPC 走位/分镜复刻的基础，优先级最高。

2. **Camera / viewport parity**
   - 当前 `camera` 可以手写镜头，但原版 `viewport` 出现 347 次。
   - 建议建立 `viewport` 转换规范：target actor / absolute point / duration / snap or pan。

3. **对话分支 parity**
   - 当前 `question` 能做简单分支，但原版有 `fork`、`quickQuestion`、`switchEvent`、`$q/$r`、回答记录。
   - 先补通用分支 runtime，否则心事件和多段剧情会反复卡。

4. **临时 sprite 与视觉效果**
   - `specificTemporarySprite` 出现 181 次，不应全部用 particle 糊过去。
   - 建议建立 `temporary_sprite` command，支持 sprite id、位置、帧序列、持续时间、layer、scale。

5. **状态变更 command**
   - `addConversationTopic`、`addWorldState`、`addMailReceived`、`removeQuest`、`removeItem`、`friendship` 需要统一服务端命令。
   - 邮件相关必须区分可读信、mail flag、tomorrow flag。

---

## 6. P0 主流程阻塞项

这些缺口会直接影响主流程剧情迁移，建议先做：

| 阻塞项 | 原因 | 建议落点 |
| --- | --- | --- |
| unknown precondition 不得默认通过 | 防止剧情误触发 | `PreconditionEvaluator`, `ServerPreconditionEvaluator` |
| 原版 precondition alias 转换表 | 总账里全是 `f/t/e/...`，需要可诊断 | 新建转换/诊断工具 |
| `Hn/Hl/*n` 共享状态语义 | CC/Joja/主线大量使用 | Player/world story flag 层 |
| `z/d/y/i/m/p/a` | 早期剧情和主流程都会遇到 | precondition evaluator + trigger/AABB 规范 |
| `faceDirection/showFrame/positionOffset/stopAnimation/speed` | 分镜和走位基础 | EventCommandFactory 新命令 |
| `viewport/globalFade/playMusic/playSound` 映射规范 | 镜头和音画基础 | camera/audio command 扩展和 cue map |
| `specificTemporarySprite` | Junimo、特效、道具表现 | temporary sprite renderer |
| `fork/quickQuestion/switchEvent` | 原版分支剧情大量使用 | question/flow runtime |
| `addConversationTopic/addWorldState/addMailReceived` | 剧情后续状态 | server state commands |
| `removeQuest/removeItem/addQuest/friendship/money` | 奖励与消耗 | server authoritative commands |

---

## 7. P1/P2 后置项

### P1：NPC 心事件与婚恋常用

- `o/O/D/A/L/v` 等婚恋、配偶、active dialogue、房屋升级、NPC visible 预条件。
- `textAboveHead`、`shake`、`glow`、`doAction`。
- gift / bouquet / mermaid pendant 等状态机与 dialogue event。

### P2：后期与特殊系统

- `h/B/F/N/S/J/C/X/U` 等宠物、配偶床、节日、核桃、秘密纸条、Joja/CC 完成状态。
- `updateMinigame`、`cutscene`、特殊临时地图、节日/小游戏桥接。
- Ginger Island、Qi、Movie Theater、Desert Festival 等后期内容依赖的世界状态。

---

## 8. 推荐落地顺序

### 第一步：安全化与诊断

1. unknown precondition 改为诊断，不再静默通过。
2. 增加只读命令：扫描所有 cutscene JSON，输出 unknown command / unknown precondition / unregistered mail。
3. 在迁移期允许配置成 warn；进入验收时 unknown 视为失败。

### 第二步：预条件 P0

1. 支持 `not_season`、`not_day_of_week`、`year`、`has_item`、`earned_money`。
2. 支持 `npc_visible_here` / `npc_visible_anywhere`，或明确由触发器替代。
3. 支持 `shared_mail` / `not_shared_mail` / `player_or_shared_mail`，替代直接 host gate。
4. 明确 `tile` 到 AABB/anchor 的人工迁移规范。

### 第三步：分镜 P0 command

1. `faceDirection`、`showFrame`、`stopAnimation`、`speed`、`positionOffset`、`makeInvisible`。
2. `viewport` 语义规范。
3. `temporary_sprite`。
4. `textAboveHead` 可放 P1，但如果 P0 事件遇到就提前补。

### 第四步：分支与状态 command

1. `quickQuestion`、`fork`、`switchEvent`。
2. `addConversationTopic`、`addWorldState`。
3. `addMailReceived` 的 no-letter / readable mail / tomorrow flag 区分。
4. `removeQuest`、`removeItem`、`money`、`friendship`。

### 第五步：开始 P0 剧情设计卡

在上述能力补齐一批后，再从 `STORY_EVENT_MIGRATION_LEDGER.md` 里挑 P0 主流程逐条建设计卡。每条卡仍必须人工确认分镜、声音、走位、对白和坐标。

---

## 9. 验收标准

本报告进入可执行状态的标准：

- 所有 P0 precondition 都有项目 JSON 表达或明确迁移规范。
- 所有 P0 command 都有项目 command 或明确人工分镜替代规则。
- unknown precondition / command 不会静默通过。
- 邮件命令能区分可读信与状态 flag。
- 坐标相关 token 不会自动从原版 tile 推算。
- 每条剧情迁移前可以根据 report 判断是否缺 runtime 能力。

---

## 10. 备注

现有项目剧情不需要反向审计作为本阶段前置。它们在总账中标记为 `已迁移-待复核` 的含义是：后续补剧情设计卡时，可以补齐来源、状态、音画和验收记录；不是说当前剧情质量有问题。
