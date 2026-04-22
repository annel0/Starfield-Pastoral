# StardewCraft 事件/剧情系统设计

## 一、总体架构

### 设计目标
- 1:1 还原星露谷物语的心事件 (Heart Events)、剧情过场 (Cutscenes)
- 纯客户端演出：触发者看到完整剧情，其他玩家无感知
- 多人服务器完全兼容：每个玩家独立追踪事件进度
- 数据驱动：事件脚本用 JSON 定义，不硬编码

### 核心思路
星露谷的事件本质是一个**脚本播放器**：
1. 玩家满足条件 → 触发事件
2. 锁定玩家输入，进入"观影模式"
3. 生成纯客户端的 Actor 实体（NPC 副本 + 玩家副本）
4. 按脚本逐条执行命令（移动、对话、动画、镜头…）
5. 播放完毕 → 标记已看 → 恢复正常

### 玩家视角
- 玩家进入剧情后变成"旁观者"：能看到自己的 Actor 副本在演戏
- 真实玩家实体原地冻结并在客户端隐藏
- 镜头由脚本控制，独立于玩家位置
- 其他在线玩家看到的是：触发者站着不动，NPC 照常活动

---

## 二、与现有系统的关系

### 可直接复用的系统

| 现有系统 | 复用方式 |
|---------|---------|
| `ClientNpcVisibilityState` | 剧情开始时隐藏参与的真实 NPC，结束后恢复 |
| `NpcVisibilityPayload` (S→C) | 服务端通知客户端隐藏/显示 NPC |
| `StardewNpcEntity` + GeckoLib | Actor 复用同一模型和动画系统 |
| `StardewNpcDialogueScreen` | 剧情中的对话 UI 直接复用（打字机、肖像、情感标记） |
| `ScreenFade` | 淡入淡出效果直接复用 |
| `NpcFriendshipDataManager` | 前置条件检查好感度 |
| `PlayerDataManager` | 前置条件检查技能/金钱等 |
| `StardewTimeManager` | 前置条件检查时间/季节/天数 |
| `PacketHandler` | 注册新的事件相关网络包 |
| `CameraShakeMixin` | 已有镜头 mixin 基础，可扩展 |
| `EmoteBubbleClientState` | 剧情中表情气泡 |

### 需要新建的系统

| 新系统 | 说明 |
|-------|------|
| `EventData` | 事件 JSON 数据模型 |
| `EventRegistry` | 加载和索引所有事件数据 |
| `EventPlayer` | 客户端脚本执行引擎（状态机） |
| `EventCommand` 接口 + 实现类 | 每种脚本命令一个实现 |
| `EventTriggerChecker` | 客户端检测触发条件 |
| `EventSeenData` | 服务端 per-player 已看事件存储 |
| `EventCameraController` | 客户端镜头接管 |
| `EventActorEntity` | 纯客户端 Actor 实体（或复用 NPC 实体 + 标记） |
| 网络包 (3-4个) | 事件同步相关 |

---

## 三、数据格式设计

### 事件 JSON 文件结构

文件位置：`data/stardewcraft/events/{event_id}.json`

```jsonc
{
  // ─── 元信息 ───
  "id": "abigail_heart_2",
  "skippable": true,

  // ─── 触发方式 ───
  "trigger": {
    "type": "enter_area",          // enter_area | interact_npc | time_check
    "location": "mountain",        // 区域标识
    "area": {                      // 可选 AABB 范围
      "min": [50, 60, 20],
      "max": [70, 80, 30]
    }
  },
  // 或者交互触发:
  // "trigger": {
  //   "type": "interact_npc",
  //   "npc": "abigail"
  // },

  // ─── 前置条件（全部满足才触发）───
  "preconditions": [
    {"type": "friendship", "npc": "abigail", "min": 500},
    {"type": "saw_event", "id": "abigail_heart_1"},
    {"type": "not_saw_event", "id": "abigail_heart_2"},
    {"type": "time", "min": 1800, "max": 2200},
    {"type": "season", "season": "spring"},
    {"type": "weather", "weather": "sunny"},
    {"type": "day_of_week", "days": ["Mon", "Thu"]},
    {"type": "days_played", "min": 14},
    {"type": "money", "min": 5000},
    {"type": "skill", "skill": "farming", "min": 4},
    {"type": "mail", "id": "some_mail_flag"},
    {"type": "is_host"}
  ],

  // ─── 脚本命令序列 ───
  "commands": [
    // ... 见下方命令列表
  ]
}
```

### 支持的前置条件类型

| type | 参数 | 说明 |
|------|------|------|
| `friendship` | npc, min | NPC 好感度 ≥ min 点 |
| `saw_event` | id | 已看过指定事件 |
| `not_saw_event` | id | 未看过指定事件 |
| `time` | min, max | 当前时间在范围内 |
| `season` | season | 当前季节匹配 |
| `weather` | weather | 天气匹配 |
| `day_of_week` | days[] | 星期几匹配 |
| `day_of_month` | day | 日期匹配 |
| `days_played` | min | 游戏总天数 ≥ min |
| `money` | min | 玩家金钱 ≥ min |
| `skill` | skill, min | 技能等级 ≥ min |
| `mail` | id | 已收到指定邮件标记 |
| `not_mail` | id | 未收到指定邮件标记 |
| `is_host` | — | 当前玩家是房主 |
| `dating` | npc | 正在与 NPC 约会 |
| `married` | npc | 已与 NPC 结婚 |
| `community_center_done` | — | 社区中心已完成 |

---

## 四、脚本命令系统

### 命令执行模型

EventPlayer 是一个 **tick-based 状态机**：
- 每个客户端 tick 调用 `EventPlayer.tick()`
- 当前命令的 `isComplete()` 返回 true → 推进到下一条
- 某些命令是即时的（spawn_actor），某些需要多帧（move, pause, fade）
- `--` 开头的命令为注释，自动跳过

### 命令分类和说明

#### 流程控制

| 命令 | 参数 | 说明 |
|------|------|------|
| `lock_player` | — | 锁定玩家输入，隐藏 HUD |
| `unlock_player` | — | 恢复玩家输入和 HUD |
| `pause` | ticks | 等待指定 tick 数 |
| `end` | — | 结束事件，清理所有 Actor |
| `skippable` | — | 从此处起允许按 ESC 跳过 |
| `unskippable` | — | 从此处起禁止跳过 |
| `comment` | text | 注释，不执行 |

#### 角色管理

| 命令 | 参数 | 说明 |
|------|------|------|
| `hide_npc` | npc | 隐藏真实 NPC（客户端侧） |
| `restore_npcs` | — | 恢复所有被隐藏的 NPC |
| `spawn_actor` | id, npc/player, pos, facing | 生成纯客户端 Actor |
| `remove_actor` | id | 移除指定 Actor |
| `remove_actors` | — | 移除所有 Actor |

#### 角色动作

| 命令 | 参数 | 说明 |
|------|------|------|
| `move` | actor, to, speed | Actor 行走到目标位置 |
| `warp` | actor, pos | Actor 瞬移到位置 |
| `face` | actor, direction | Actor 转向（north/south/east/west 或角度） |
| `animate` | actor, animation, loop? | 播放 GeckoLib 动画 |
| `stop_animate` | actor | 停止当前动画 |
| `emote` | actor, emote | 显示表情气泡 |
| `jump` | actor, height? | Actor 跳跃 |
| `speed` | actor, speed | 设置 Actor 移动速度 |

#### 对话

| 命令 | 参数 | 说明 |
|------|------|------|
| `speak` | actor, key | 显示对话（复用 StardewNpcDialogueScreen） |
| `message` | key | 显示无肖像的消息文本 |
| `question` | actor, key, options[] | 显示选择题，根据选项 fork |

#### 镜头

| 命令 | 参数 | 说明 |
|------|------|------|
| `camera` | pos, pitch, yaw | 设置镜头位置和角度 |
| `camera_move` | pos, pitch, yaw, ticks | 镜头平滑移动到目标 |
| `camera_follow` | actor | 镜头跟随某个 Actor |
| `camera_reset` | — | 恢复玩家正常镜头 |
| `camera_shake` | intensity, ticks | 镜头震动 |

#### 视觉效果

| 命令 | 参数 | 说明 |
|------|------|------|
| `fade_out` | ticks, color? | 淡出到黑/白 |
| `fade_in` | ticks | 淡入 |
| `flash` | color, ticks | 闪光效果 |
| `particle` | type, pos, count | 粒子效果 |

#### 音效

| 命令 | 参数 | 说明 |
|------|------|------|
| `music` | track | 播放背景音乐 |
| `stop_music` | — | 停止音乐 |
| `sound` | sound_id | 播放音效 |

#### 游戏状态（播放完成后执行）

| 命令 | 参数 | 说明 |
|------|------|------|
| `add_friendship` | npc, amount | 增减好感度 |
| `add_mail` | mail_id | 添加邮件标记 |
| `add_item` | item_id, count? | 给玩家物品 |
| `add_quest` | quest_id | 触发任务 |

#### 并行命令

| 命令 | 参数 | 说明 |
|------|------|------|
| `simultaneous_start` | — | 开始并行块：之后的命令同时执行 |
| `simultaneous_end` | — | 结束并行块：等待所有完成后继续 |

---

## 五、触发系统设计

### 三种触发方式

#### 1. 区域进入触发 (`enter_area`)

```
玩家位置变化
  → 检查是否进入了某个事件区域 (AABB)
  → 遍历该区域关联的所有未看事件
  → 验证前置条件
  → 第一个通过的事件触发
```

- 每 10 tick 检查一次（0.5秒），不是每帧
- 使用空间索引：按 location 分组，只检查当前区域的事件
- 进入后设置冷却，同一区域不会连续触发

#### 2. NPC 交互触发 (`interact_npc`)

```
玩家右键 NPC
  → NpcInteractionService 先检查该 NPC 是否有待触发事件
  → 有 → 触发事件（不打开普通对话）
  → 无 → 正常对话流程
```

- 这是星露谷最常见的心事件触发方式
- 优先级：事件 > 生日对话 > 普通对话

#### 3. 条件轮询触发 (`time_check`)

```
StardewTimeManager 10 分钟 tick
  → 服务端检查所有 time_check 类型事件的条件
  → 满足则发包通知客户端触发
```

- 用于"晚上到家自动触发"等场景
- 频率低（跟随 10 分钟 tick），无性能问题

### 触发流程（通用）

```
条件满足
  ↓
客户端: EventPlayer.start(eventData)
  ↓
1. 冻结玩家（禁用移动、交互、菜单）
2. 隐藏 HUD
3. 通过 NpcVisibilityPayload 隐藏参演 NPC
4. 开始执行 commands[]
  ↓
逐 tick 执行脚本命令...
  ↓
遇到 "end" 命令
  ↓
5. 移除所有 Actor
6. 恢复 NPC 可见
7. 恢复 HUD 和玩家控制
8. 发 C2S 包: MarkEventSeenPayload(eventId)
  ↓
服务端: 写入 EventSeenData → 同步回客户端确认
```

---

## 六、网络设计

### 需要的网络包

| 包名 | 方向 | 用途 |
|------|------|------|
| `SyncEventSeenPayload` | S→C | 玩家登录时同步已看事件列表 |
| `MarkEventSeenPayload` | C→S | 客户端通知服务端事件播放完毕 |
| `TriggerEventPayload` | S→C | 服务端触发 time_check 类事件 |
| `EventStatePayload` | C→S | 事件中的选择/分支回传（question 命令） |
| `FreezePlayerPayload` | S→C | 服务端请求冻结/解冻玩家（防作弊辅助） |

### 数据同步策略

- **已看事件 (eventsSeen)**：服务端 `EventSeenData`（SavedData），登录时全量同步到客户端缓存
- **好感度**：已有 `NpcFriendshipClientCache`，直接读取
- **时间/季节**：已有 `TimeSyncPacket`，客户端已有数据
- **玩家数据（技能/钱）**：已有 `ClientPlayerDataCache`，直接读取

客户端检查前置条件时，所有需要的数据都已有本地缓存，不需要额外请求。

---

## 七、Actor 实体设计

### 方案：复用 StardewNpcEntity 的渲染，新建轻量 Actor 类

```
EventActorEntity extends LivingEntity implements GeoEntity
  - 纯客户端实体（不注册到服务端）
  - 复用 StardewNpcEntity 的模型和动画控制器
  - 无 AI goals、无碰撞体积、无寻路
  - 由 EventPlayer 直接控制位置和动画
  - 玩家 Actor 使用玩家皮肤渲染
```

### Actor 类型

| 类型 | 模型来源 | 说明 |
|------|---------|------|
| NPC Actor | 复用对应 NPC 的 GeckoLib 模型 | 身份由 npcId 决定 |
| Player Actor | 玩家模型 + 皮肤 | 用于玩家需要出镜的场景 |
| Temp Actor | 指定模型路径 | 临时角色（回忆场景等） |

### Actor 移动

- `move` 命令：Actor 从当前位置平滑移动到目标位置
- 每 tick 按速度插值位置，到达后标记命令完成
- 移动时自动播放行走动画，到达后切回站立
- 不走 vanilla 寻路——直线移动即可（事件场景中位置都是预设的）

---

## 八、镜头控制设计

### 实现方式

通过 Mixin 注入 `GameRenderer` 或 `Camera`：

```
EventCameraController (客户端单例)
  - active: boolean          // 是否接管镜头
  - position: Vec3           // 镜头世界坐标
  - pitch, yaw: float        // 镜头角度
  - targetPos/Pitch/Yaw      // 平滑移动目标
  - followTarget: Entity     // 跟随目标
  - lerpSpeed: float         // 插值速度
```

当 `active = true` 时，Mixin 覆写 `Camera.setup()`：
- 用 `EventCameraController` 的位置/角度替代玩家视角
- 支持平滑插值（lerp）实现镜头移动效果

### 镜头模式

| 模式 | 说明 |
|------|------|
| 固定 | `camera` 命令设置固定坐标和角度 |
| 移动 | `camera_move` 平滑过渡到新位置 |
| 跟随 | `camera_follow` 跟随某个 Actor，保持一定偏移 |
| 释放 | `camera_reset` 恢复正常玩家视角 |

---

## 九、跳过机制

### 按 ESC 跳过

- `skippable` 命令后，玩家按 ESC 可跳过剩余脚本
- 跳过时：立即执行所有"游戏状态"类命令（add_friendship 等），跳过演出类命令
- 执行 `end` 命令的完整清理流程

### SetSkipActions（对齐 SDV）

- 可在脚本中用 `set_skip_actions` 指定跳过时额外执行的命令
- 确保无论是否跳过，游戏状态变更一致

---

## 十、事件数据示例

### 示例：Abigail 2 心事件

```jsonc
{
  "id": "abigail_heart_2",
  "skippable": true,
  "trigger": {
    "type": "enter_area",
    "location": "mountain",
    "area": {
      "min": [45, 60, 15],
      "max": [75, 80, 35]
    }
  },
  "preconditions": [
    {"type": "friendship", "npc": "abigail", "min": 500},
    {"type": "saw_event", "id": "abigail_heart_1"},
    {"type": "time", "min": 1200, "max": 1900},
    {"type": "weather", "weather": "sunny"}
  ],
  "commands": [
    {"cmd": "lock_player"},
    {"cmd": "fade_out", "ticks": 15},

    {"cmd": "hide_npc", "npc": "abigail"},
    {"cmd": "spawn_actor", "id": "abigail", "type": "npc", "npc": "abigail",
     "pos": [55.5, 65, 22.5], "facing": 180},
    {"cmd": "spawn_actor", "id": "player", "type": "player",
     "pos": [52.5, 65, 22.5], "facing": 90},

    {"cmd": "camera", "pos": [54, 68, 20], "pitch": 30, "yaw": 0},
    {"cmd": "music", "track": "stardewcraft:abigail_theme"},
    {"cmd": "fade_in", "ticks": 15},
    {"cmd": "skippable"},

    {"cmd": "pause", "ticks": 20},
    {"cmd": "speak", "actor": "abigail", "key": "event.abigail.heart2.1"},

    {"cmd": "simultaneous_start"},
    {"cmd": "move", "actor": "abigail", "to": [54.5, 65, 22.5], "speed": 0.04},
    {"cmd": "camera_move", "pos": [54, 67, 21], "pitch": 25, "yaw": 0, "ticks": 40},
    {"cmd": "simultaneous_end"},

    {"cmd": "face", "actor": "abigail", "direction": "west"},
    {"cmd": "speak", "actor": "abigail", "key": "event.abigail.heart2.2"},
    {"cmd": "emote", "actor": "abigail", "emote": "happy"},
    {"cmd": "pause", "ticks": 30},

    {"cmd": "speak", "actor": "player", "key": "event.abigail.heart2.3"},
    {"cmd": "animate", "actor": "abigail", "animation": "laugh"},
    {"cmd": "pause", "ticks": 40},

    {"cmd": "add_friendship", "npc": "abigail", "amount": 50},

    {"cmd": "fade_out", "ticks": 15},
    {"cmd": "stop_music"},
    {"cmd": "remove_actors"},
    {"cmd": "restore_npcs"},
    {"cmd": "camera_reset"},
    {"cmd": "fade_in", "ticks": 15},
    {"cmd": "unlock_player"},
    {"cmd": "end"}
  ]
}
```

---

## 十一、代码模块与包结构

```
com.stardew.craft.event
├── data/
│   ├── EventData.java              // 事件 JSON 数据模型
│   ├── EventPrecondition.java      // 前置条件数据模型
│   ├── EventTrigger.java           // 触发方式数据模型
│   └── EventRegistry.java          // 加载 + 索引所有事件 JSON
├── command/
│   ├── EventCommand.java           // 接口: tick(), isComplete(), onSkip()
│   ├── EventCommandFactory.java    // JSON cmd 字符串 → 实例
│   ├── LockPlayerCommand.java
│   ├── UnlockPlayerCommand.java
│   ├── PauseCommand.java
│   ├── SpawnActorCommand.java
│   ├── RemoveActorCommand.java
│   ├── MoveCommand.java
│   ├── WarpCommand.java
│   ├── FaceCommand.java
│   ├── SpeakCommand.java
│   ├── QuestionCommand.java
│   ├── CameraCommand.java
│   ├── CameraMoveCommand.java
│   ├── CameraFollowCommand.java
│   ├── FadeOutCommand.java
│   ├── FadeInCommand.java
│   ├── MusicCommand.java
│   ├── SoundCommand.java
│   ├── EmoteCommand.java
│   ├── AnimateCommand.java
│   ├── SimultaneousStartCommand.java
│   ├── SimultaneousEndCommand.java
│   ├── AddFriendshipCommand.java
│   ├── AddMailCommand.java
│   ├── AddItemCommand.java
│   └── EndCommand.java
├── runtime/
│   ├── EventPlayer.java            // 客户端状态机：逐 tick 执行
│   ├── EventActor.java             // 纯客户端 Actor 实体
│   ├── EventActorRenderer.java     // Actor 渲染器
│   ├── EventCameraController.java  // 镜头接管
│   └── EventTriggerChecker.java    // 触发条件检测
├── server/
│   ├── EventSeenData.java          // SavedData: per-player eventsSeen
│   └── EventSeenService.java       // 读写已看事件 + 同步
└── network/
    ├── SyncEventSeenPayload.java   // S→C 同步已看列表
    ├── MarkEventSeenPayload.java   // C→S 标记已看
    ├── TriggerEventPayload.java    // S→C 触发事件
    ├── EventStatePayload.java      // C→S 选择回传
    └── FreezePlayerPayload.java    // S→C 冻结/解冻
```

---

## 十二、实施计划（分阶段）

### Phase 1：基础框架（最小可运行）
- [ ] `EventData` + `EventRegistry`：JSON 加载和解析
- [ ] `EventSeenData`：服务端存储 + 同步包
- [ ] `EventPlayer`：状态机骨架（只支持 lock/unlock/pause/end）
- [ ] `FreezePlayerPayload`：冻结玩家移动
- [ ] 基础测试：一个硬编码事件能锁定→等待→解锁玩家

### Phase 2：Actor 系统
- [ ] `EventActor` 实体 + 渲染器
- [ ] `spawn_actor` / `remove_actor` 命令
- [ ] `move` / `warp` / `face` 命令
- [ ] `hide_npc` / `restore_npcs` 命令
- [ ] 测试：生成 NPC Actor 和玩家 Actor，能移动

### Phase 3：对话和镜头
- [ ] `speak` 命令（对接 StardewNpcDialogueScreen）
- [ ] `EventCameraController` + Mixin
- [ ] `camera` / `camera_move` / `camera_follow` / `camera_reset` 命令
- [ ] `fade_in` / `fade_out` 命令（对接 ScreenFade）
- [ ] 测试：完整播放一段有对话和镜头移动的事件

### Phase 4：触发系统
- [ ] `EventTriggerChecker`：区域进入检测
- [ ] NPC 交互触发（hook 进 NpcInteractionService）
- [ ] 前置条件检查器（读取现有缓存数据）
- [ ] 测试：走进区域自动触发事件

### Phase 5：完善命令集
- [ ] `music` / `stop_music` / `sound` 命令
- [ ] `emote` / `animate` / `jump` 命令
- [ ] `question` 命令 + 分支逻辑
- [ ] `simultaneous_start/end` 并行执行
- [ ] `add_friendship` / `add_mail` / `add_item` 等状态命令
- [ ] 跳过机制（skippable/unskippable/set_skip_actions）

### Phase 6：正式事件内容
- [ ] 编写第一批心事件 JSON（从简单的 2 心事件开始）
- [ ] 对话翻译 key 对齐 lang 文件
- [ ] 逐步补充所有 NPC 的心事件
