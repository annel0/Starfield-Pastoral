# Cutscene Event Authoring Guide

> 供 AI Agent 和开发者参考的完整剧情注册流程。

---

## 1. 文件结构总览

一个剧情事件涉及 **3 个文件**：

| 文件 | 用途 |
|------|------|
| `src/main/resources/data/stardewcraft/cutscene_events/<event_id>.json` | 事件定义（触发条件 + 命令序列） |
| `src/main/resources/assets/stardewcraft/lang/en_us.json` | 英文对话文本 |
| `src/main/resources/assets/stardewcraft/lang/zh_cn.json` | 中文对话文本 |

## 2. 事件 JSON 结构

```jsonc
{
  "id": "willy_fishing_rod",          // ★ 必须是英文编码，不用 SDV 数字 ID
  "skippable": true,                   // 是否可跳过
  "trigger": {
    "type": "enter_area",
    "location": "beach",               // 地点 ID
    "area_min": [-387, -19, -252],     // 触发区域最小角 [x, y, z]
    "area_max": [-177, 14, -140]       // 触发区域最大角 [x, y, z]
  },
  "preconditions": [
    { "type": "not_saw_event", "id": "willy_fishing_rod" },
    { "type": "time", "min": 600, "max": 1710 },
    { "type": "weather", "weather": "sunny" },
    { "type": "not_mail", "id": "JojaMember" },
    { "type": "is_host" }
  ],
  "commands": [ ... ]
}
```

### ⚠️ 关键规则

- **`id` 必须与文件名一致**（如 `willy_fishing_rod.json` → `"id": "willy_fishing_rod"`）
- **`not_saw_event` 的 `id` 也必须与事件 `id` 一致**
- **不要用 SDV 的数字事件 ID**（如 611439），要用描述性英文编码

## 3. 命令参考（EventCommandFactory）

### 必需字段用 `*` 标记，可选字段带默认值

| cmd | 字段 | 说明 |
|-----|------|------|
| `lock_player` | — | 锁定玩家输入 |
| `unlock_player` | — | 解锁玩家输入 |
| `pause` | `ticks`* | 暂停 N tick（1 tick = 50ms） |
| `skippable` | — | 从此处开始允许跳过 |
| `end` | — | 事件结束 |
| `comment` | `text` | 注释，会被跳过 |
| `fade` | `mode`(="out"), `ticks`(=20) | "in" 或 "out" |
| **角色** | | |
| `spawn_actor` | `actor`*, `npc_id`*, `x`(=0), `y`(=0), `z`(=0), `relative`(=false), `facing`(=0) | 生成角色 |
| `remove_actor` | `actor`* | 移除角色 |
| `hide_npc` | `npc_id`* | 隐藏真实 NPC |
| `show_npc` | `npc_id`* | 显示真实 NPC |
| `move_actor` | `actor`*, `x`, `y`, `z`, `ticks`*, `relative`(=**true**) | ⚠️ 默认 relative=true |
| `move_player` | `x`, `y`, `z`, `ticks`*, `relative`(=false) | 移动真实玩家 |
| `face_actor` | `actor`*, `yaw`?, `face_actor`? | 转向（二选一） |
| `animate` | `actor`*, `anim`*, `loop`(=false) | 播放动画 |
| `warp` | `actor`*, `x`, `y`, `z`, `relative`(=false) | 瞬移 |
| **镜头** | | |
| `camera` | `x`, `y`, `z`, `yaw`(=0), `pitch`(=30), `relative`(=false), `ticks`(=0) | ticks>0 时平滑移动 |
| `camera` | `action`: "reset" | 重置镜头 |
| `camera_shake` | `intensity`(=0.5), `ticks`(=20) | 镜头震动 |
| `camera_follow` | `actor`*, `dx`(=0), `dy`(=3), `dz`(=5), `yaw`(=0), `pitch`(=30), `ticks`(=60) | 镜头跟随 |
| **对话/UI** | | |
| `speak` | `npc_id`*, `text`* | text 是 lang key |
| `message` | `text`* | 系统消息 |
| `emote` | `actor`*, `emote`* | 表情（"surprised", "sad" 等） |
| `question` | 见 QuestionCommand | 分支选择 |
| **音效/音乐** | | |
| `music` | `track`* | 播放音乐（ModSounds 字段名） |
| `stop_music` | — | 停止音乐 |
| `play_sound` | `sound`*, `volume`(=1.0), `pitch`(=1.0) | 播放音效 |
| **状态** | | |
| `add_mail` | `id`* | ⚠️ 字段是 `"id"` 不是 `"mail_id"` |
| `add_quest` | `quest_id`* | 添加任务 |
| `set_flag` | `flag`* | 设置标记 |
| `add_friendship` | `npc`*, `points`(=250) | 加好感 |
| `add_item` | `item`*, `count`(=1) | 给物品 |
| **视觉效果** | | |
| `jump` | `actor`*, `strength`(=0.5) | 跳跃 |
| `screen_flash` | `ticks`(=4) | 闪屏 |
| `particle` | `type`*, `x`, `y`, `z`, `count`(=3), `ticks`(=20) | 粒子 |
| `hold_item` | `actor`*, `item`*, `ticks`(=60), `offset_y`(=0) | 举起物品 |
| **组合** | | |
| `simultaneous` | `commands`*: [...] | 同时执行多个命令 |

## 4. 镜头数据来源

**必须从 `run/config/stardewcraft_cameras.json` 读取 sccam 预设值。**

在游戏中用 `/sccam save T1` 保存镜头位置，然后在 JSON 中精确引用：

```json
{ "cmd": "camera", "x": -190.116, "y": -1.353, "z": 124.487,
  "yaw": 2.0, "pitch": 29.2, "relative": false }
```

**绝对不要自己编造镜头坐标。**

## 5. 对话 key 命名规范

```
event.<event_id>.<dialogue_tag>
```

示例：
```json
// JSON 中
{ "cmd": "speak", "npc_id": "lewis", "text": "event.lewis_cc_tour.oh_hi" }

// en_us.json
"event.lewis_cc_tour.oh_hi": "Oh, hi there."

// zh_cn.json
"event.lewis_cc_tour.oh_hi": "哦，你好。"
```

## 6. 对话文本来源

**必须使用 SDV 原版文本。** 原版数据位于：
- 英文：`源文件/Content/Data/Events/<Location>.json`
- 中文：`源文件/Content/Data/Events/<Location>.zh-CN.json`

**绝对不要自己编造对话内容。**

## 7. 朝向系统

MC 的 yaw 与 SDV 方向的映射：

| SDV 方向 | SDV 数字 | MC yaw | 说明 |
|----------|----------|--------|------|
| 北 (up, -Y) | 0 | 0 | 面朝 +Z（朝 CC 方向） |
| 东 | 1 | -90 | |
| 南 (down, +Y) | 2 | 180 | |
| 西 | 3 | 90 | |

> SDV 的 "north"（-Y，向上）在我们的 3D 世界中对应 +Z 方向。

## 8. 调试命令

```
/stardew event play <event_id>    # 手动触发事件（跳过 preconditions）
/stardew event reset <event_id>   # 重置已看标记
/sccam save <name>                # 保存镜头位置
/sccam tp <name>                  # 传送到镜头位置
/sccam list                       # 列出所有镜头预设
```

## 9. 注册流程 Checklist

- [ ] 在 `源文件/Content/Data/Events/` 中找到原版事件脚本
- [ ] 在游戏中用 `/sccam save` 设置并保存所有镜头位
- [ ] 创建 `cutscene_events/<event_id>.json`，id 与文件名一致
- [ ] 从 `run/config/stardewcraft_cameras.json` 读取精确镜头坐标
- [ ] 使用原版对话文本，添加到 `en_us.json` 和 `zh_cn.json`
- [ ] 对话 key 格式：`event.<event_id>.<tag>`
- [ ] 确认所有命令字段名与 `EventCommandFactory` 一致
- [ ] `./gradlew classes` 编译通过
- [ ] `/stardew event play <event_id>` 测试播放

## 10. 常见踩坑记录

| 错误 | 后果 | 正确做法 |
|------|------|----------|
| `add_mail` 用 `"mail_id"` | NPE，整个事件不播放 | 用 `"id"` |
| 事件 id 用 SDV 数字（如 "611439"） | 不直观，难以维护 | 用描述性英文编码 |
| 自己编造镜头坐标 | 镜头位置完全错误 | 从 sccam 预设读取 |
| 自己编造对话文本 | 与原版不一致 | 从 `源文件/` 目录读取原版文本 |
| `spawn_actor` 的 `facing` 写错方向 | 角色朝向不对 | 参考朝向系统表（yaw 值） |
| `move_actor` 默认 `relative=true` | 坐标被当作相对偏移 | 绝对坐标时显式写 `"relative": false` |
| `trigger.area` 用嵌套 `{ min, max }` 写法 | 旧解析器只认嵌套形式，新代码统一用扁平 `area_min` / `area_max` | 统一用扁平 `area_min` / `area_max`（嵌套也兼容，但不推荐） |
| `weather` 写 `"sunny"` 但忘了别名映射 | 比对失败永远不触发 | 已支持别名 `sunny/rainy/stormy/snowy/windy/festival`，内部正规化为 SDV 代号 `Sun/Rain/Storm/Snow/WindSpring/Festival` |
| `mail` 与 `not_mail` 方向搞反 | "已收到信才触发" 写成 `not_mail` | SDV 源码 `n` / `*n` = **HAS** received → 用 `mail`；`!n` / `!*n` = **NOT** received → 用 `not_mail` |
| 共享维度的 location 不写 AABB | `enter_area` 在整个维度任意位置触发，串台 | `STARDEW_VALLEY` 共享给 beach/town/farm/forest/mountain，**必须**写 `area_min`/`area_max`；只有 `mine` 等独占整个维度的可以省略 |
| `_<year>` 个人邮件用全局日历调度 | 玩家在春 5 加入永远收不到 `spring_4_1` | `_<year>` 后缀邮件按 `personalDays` 派发；`festival/spring_12` 这类无年份的按全局日历 |
| `days_played` precondition 当占位符直接 `return true` | 任何带 `j N` 的事件全部跳过校验 | 实现为 `(year-1)*112 + season*28 + day`，与 SDV `Game1.stats.DaysPlayed` 等价 |
| 老存档"满足条件却不触发" | 之前测试播放过的剧情已被写入 `EventSeenData` | 用 `/stardew event reset` 清空当前玩家的已看记录 |
| 在 `manual` 触发的 wizard/test 事件没及时清理 | 进世界/换维度时被误触发 | 测试用 JSON 删干净；正式事件用 `manual` 必须由专门 handler 调起，并依赖 `not_saw_event` 防重 |
| 跨维度时剧情仍在播放 | 主角已 warp 走，演员留在前一维度 | 客户端 `EventPlayer` 已加 `startDimension` 守卫，玩家维度变化即 abort；服务端命令应避免长 pause 中跨维度 |

## 11. 触发系统行为速览（runtime 实现细节）

| 守卫 | 位置 | 含义 |
|------|------|------|
| `JOIN_GRACE_TICKS = 10` | `EventTriggerChecker` | 进世界/切维度后 0.5s 内不检查 `enter_area`（让 seen-cache、玩家数据、区块加载到位） |
| `CHECK_INTERVAL = 10` | `EventTriggerChecker` | 每 0.5s 扫描一次 enter_area 触发 |
| `POST_EVENT_COOLDOWN = 40` | `EventTriggerChecker` | 一段剧情结束后 2s 内不允许再触发新剧情 |
| `ClientEventSeenCache.isSynced()` | `EventTriggerChecker` | 必须收到服务器同步的 seen 列表后才允许触发 |
| `mc.level.hasChunkAt(playerBlock)` | `EventTriggerChecker` | 玩家脚下区块没加载完不触发 |
| `WHOLE_DIMENSION_LOCATIONS = {"mine"}` | `EventTriggerChecker` | 列入此集合的 location 可省略 AABB；其余共享维度的 location **必须**有 AABB |
| `EventPlayer.startDimension` | `EventPlayer` | 播放期间检测到 `mc.level.dimension()` 变化即整段 abort |

