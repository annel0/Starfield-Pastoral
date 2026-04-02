# 新增 NPC 开发指南（完整流程）

> 基于 Abigail / Caroline / Lewis 的开发经验总结，适用于 StardewCraft NeoForge 1.21.1 项目。  
> 假定：NPC 的 **3D 模型** (`.geo.json`) 和 **动画** (`.animation.json`) 已由美术组放入对应目录。

---

## 目录

1. [概览：需要改动的文件清单](#1-概览需要改动的文件清单)
2. [步骤 0：确定 NPC ID 与原版数据](#2-步骤-0确定-npc-id-与原版数据)
3. [步骤 1：能力配置 — base_profiles.json](#3-步骤-1能力配置--base_profilesjson)
4. [步骤 2：美术资源确认](#4-步骤-2美术资源确认)
5. [步骤 3：对话数据 — dialogue/{npcid}.json](#5-步骤-3对话数据--dialoguenpcidjson)
6. [步骤 4：翻译文件 — en_us.json / zh_cn.json](#6-步骤-4翻译文件--en_usjson--zh_cnjson)
7. [步骤 5：礼物偏好 — tastes/{npcid}.json](#7-步骤-5礼物偏好--tastesnpcidjson)
8. [步骤 6：日程表 — schedules/{npcid}.json](#8-步骤-6日程表--schedulesnpcidjson)
9. [步骤 7：生日 — npc_birthdays.json](#9-步骤-7生日--npc_birthdaysjson)
10. [步骤 8：默认出生点 — default_spawns.json](#10-步骤-8默认出生点--default_spawnsjson)
11. [步骤 9：社交界面（V键）接入](#11-步骤-9社交界面v键接入)
12. [步骤 10：编译验证](#12-步骤-10编译验证)
13. [步骤 11：游戏内调试命令](#13-步骤-11游戏内调试命令)
14. [附录 A：原版性格数据参考表](#附录-a原版性格数据参考表)
15. [附录 B：对话 Key 命名规范](#附录-b对话-key-命名规范)
16. [附录 C：Portrait 差分标记语法](#附录-c-portrait-差分标记语法)
17. [附录 D：完整文件路径速查](#附录-d完整文件路径速查)
18. [Checklist 模板](#checklist-模板)

---

## 1. 概览：需要改动的文件清单

以新增一个名为 `{npcid}`（如 `sam`）的 NPC 为例：

| # | 文件路径 | 操作 | 说明 |
|---|---------|------|------|
| 1 | `data/stardewcraft/npc/capabilities/base_profiles.json` | 编辑 | 添加 NPC 能力 & 性格条目 |
| 2 | `assets/stardewcraft/textures/entity/npc/{npcid}.png` | **确认存在** | 实体贴图（128×128 或更大） |
| 3 | `assets/stardewcraft/textures/portraits/{npcid}.png` | **确认存在** | 对话头像（128×320，2 列 × 5 行，每格 64×64） |
| 4 | `assets/stardewcraft/textures/mugshots/{npcid}.png` | **确认存在** | 社交菜单小头像（16×24） |
| 5 | `assets/stardewcraft/geo/entity/npc/{npcid}.geo.json` | **确认存在** | GeckoLib 模型 |
| 6 | `assets/stardewcraft/animations/entity/npc/{npcid}.animation.json` | **确认存在** | GeckoLib 动画（idle + walk） |
| 7 | `data/stardewcraft/npc/dialogue/{npcid}.json` | **新建** | 对话 key 映射 |
| 8 | `assets/stardewcraft/lang/en_us.json` | 编辑 | 添加英文翻译 |
| 9 | `assets/stardewcraft/lang/zh_cn.json` | 编辑 | 添加中文翻译 |
| 10 | `data/stardewcraft/npc/tastes/{npcid}.json` | **新建** | 礼物偏好 |
| 11 | `data/stardewcraft/npc/schedules/{npcid}.json` | **新建** | 日程表 |
| 12 | `data/stardewcraft/npc/events/npc_birthdays.json` | 编辑 | 添加生日 |
| 13 | `data/stardewcraft/npc/events/default_spawns.json` | 编辑 | 添加默认出生位置 |
| 14 | `StardewGameMenuScreen.java` 中 `DATEABLE_NPCS` | 编辑（可选） | 若可约会则添加 |

> **不需要**改动的文件：`ModEntities.java`、`StardewNpcEntity.java`、`NpcInteractionService.java`、`NpcSpawnManager.java`——系统是完全数据驱动的。

---

## 2. 步骤 0：确定 NPC ID 与原版数据

**NPC ID 规范**：全小写英文，无空格，无特殊字符。示例：`sam`, `sebastian`, `haley`。

从原版 `源文件/` 目录收集以下数据：

| 数据项 | 来源文件 | 示例 (Sam) |
|--------|---------|------------|
| 性格属性 | `源文件/StardewValley/NPC.cs` + `Content/Data/Characters.json` | age=1(teen), manners=0, socialAnxiety=0, optimism=0, gender=0(male), datable=true |
| 生日 | `Content/Data/Characters.json` | summer 17 |
| 礼物好感 | `Content/Data/NPCGiftTastes.json` | loved=[Cactus Fruit, Maple Bar, Pizza, Tigerseye, ...] |
| 对话原文 | `Content/Characters/Dialogue/Sam.json` | 全部 key-value 对话条目 |
| 日程表 | `Content/Characters/schedules/Sam.json` | 原版日程数据 |

---

## 3. 步骤 1：能力配置 — base_profiles.json

**文件**：`src/main/resources/data/stardewcraft/npc/capabilities/base_profiles.json`

在 `"npcs"` 数组中添加新条目：

```json
{
  "id": "sam",
  "implemented": true,
  "pathing_enabled": true,
  "animation_profile": "idle_walk",
  "age": 1,
  "manners": 0,
  "social_anxiety": 0,
  "optimism": 0,
  "gender": 0,
  "datable": true
}
```

### 字段说明

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| `id` | string | ✅ | NPC ID，全小写 |
| `implemented` | bool | ✅ | 设为 `true` 才会被系统加载和生成 |
| `pathing_enabled` | bool | ✅ | 是否启用 A* 寻路，需有 walk 动画 |
| `animation_profile` | string | ✅ | `"idle_walk"` 或 `"idle_only"`（无 walk 动画时） |
| `age` | int | ○ | 0=adult, 1=teen, 2=child（默认 0） |
| `manners` | int | ○ | 0=neutral, 1=polite, 2=rude（默认 0） |
| `social_anxiety` | int | ○ | 0=outgoing, 1=shy（默认 0） |
| `optimism` | int | ○ | 0=positive, 1=negative（默认 0） |
| `gender` | int | ○ | 0=male, 1=female（默认 0） |
| `datable` | bool | ○ | 是否可约会/结婚候选人（默认 false） |

> ⚠️ 若设为 `implemented: true` 但尚未放入 walk 动画，系统会自动降级 `pathing_enabled=false` 并打日志警告。

---

## 4. 步骤 2：美术资源确认

所有美术资源基础路径：`src/main/resources/assets/stardewcraft/`

### 4.1 实体贴图（3D 模型贴图）
- **路径**：`textures/entity/npc/{npcid}.png`
- **用途**：GeckoLib 实体在世界中的渲染
- **尺寸**：由模型决定，通常 128×128

### 4.2 对话肖像（Portrait 差分图）
- **路径**：`textures/portraits/{npcid}.png`
- **用途**：对话框右侧头像显示
- **尺寸**：**128×320**（2 列 × 5 行，共 10 格，每格 64×64）
- **布局**：

```
┌─────────┬─────────┐
│  idx 0  │  idx 1  │   行0: 0=default, 1=happy($h)
│ default │  happy  │
├─────────┼─────────┤
│  idx 2  │  idx 3  │   行1: 2=sad($s), 3=unique($u)
│   sad   │ unique  │
├─────────┼─────────┤
│  idx 4  │  idx 5  │   行2: 4=love($l), 5=angry($a)
│  love   │  angry  │
├─────────┼─────────┤
│  idx 6  │  idx 7  │   行3-4: 可选的额外差分
│ (extra) │ (extra) │
├─────────┼─────────┤
│  idx 8  │  idx 9  │
│ (extra) │ (extra) │
└─────────┴─────────┘
```

> 如果只有 128×128 的纹理（仅 1 行 2 列），系统也能正常工作，只是所有差分都会显示 index 0。

### 4.3 社交菜单小头像（Mugshot）
- **路径**：`textures/mugshots/{npcid}.png`
- **用途**：V 键社交界面列表中的小头像
- **尺寸**：**16×24**
- **⚠️关键**：如果缺少此文件，该 NPC **不会出现在社交界面**！

### 4.4 GeckoLib 模型
- **路径**：`geo/entity/npc/{npcid}.geo.json`
- **已由美术组放置**，确认文件存在即可

### 4.5 GeckoLib 动画
- **路径**：`animations/entity/npc/{npcid}.animation.json`
- **要求**：至少包含 `idle` 动画；如果 `pathing_enabled=true` 还需要 `walk` 动画
- **已由美术组放置**，确认文件存在即可

### 4.6 季节/场景差分肖像（可选）
- 路径格式：`textures/portraits/{npcid}_beach.png`、`{npcid}_winter.png`
- 目前不影响核心功能，后续扩展用

---

## 5. 步骤 3：对话数据 — dialogue/{npcid}.json

**文件**：`src/main/resources/data/stardewcraft/npc/dialogue/{npcid}.json`

**新建**此文件，结构如下：

```json
{
  "npc_id": "sam",
  "entries": {
    "Introduction": "stardewcraft.npc.sam.introduction",
    "Mon": "stardewcraft.npc.sam.mon",
    "Tue": "stardewcraft.npc.sam.tue",
    "Wed": "stardewcraft.npc.sam.wed",
    "Thu": "stardewcraft.npc.sam.thu",
    "Fri": "stardewcraft.npc.sam.fri",
    "Sat": "stardewcraft.npc.sam.sat",
    "Sun": "stardewcraft.npc.sam.sun",

    "spring": "stardewcraft.npc.sam.spring",
    "summer": "stardewcraft.npc.sam.summer",
    "fall": "stardewcraft.npc.sam.fall",
    "winter": "stardewcraft.npc.sam.winter",

    "spring_Mon": "stardewcraft.npc.sam.spring_mon",
    "rain": "stardewcraft.npc.sam.rain",

    "default": "stardewcraft.npc.sam.default",
    "default0": "stardewcraft.npc.sam.default0",
    "default4": "stardewcraft.npc.sam.default4",
    "default8": "stardewcraft.npc.sam.default8",

    "AcceptBirthdayGift_Positive": "stardewcraft.npc.sam.acceptbirthdaygift_positive",
    "AcceptBirthdayGift_Negative": "stardewcraft.npc.sam.acceptbirthdaygift_negative",
    "AcceptBirthdayGift_Loved": "stardewcraft.npc.sam.acceptbirthdaygift_loved"
  }
}
```

### 对话 Key 优先级（从高到低）

系统 (`NpcInteractionService.buildDialoguePrefixes`) 按以下顺序查找匹配的对话 key：

1. **天气** — `Rain`, `Storm` 等（由 `WeatherManager` 返回值转换）
2. **rain** — 固定的雨天 fallback
3. **季节_日期** — `spring_15`
4. **季节_星期** — `spring_Mon`
5. **星期** — `Mon`, `Tue`, `Wed`, `Thu`, `Fri`, `Sat`, `Sun`
6. **日期** — `15`（纯数字）
7. **季节** — `spring`, `summer`, `fall`, `winter`
8. **Introduction** — 首次对话
9. **default** — 兜底

### 心形变体

每个 key 可追加数字后缀表示心形等级门槛：
- `default0` — 0 心及以上显示
- `default4` — 4 心及以上显示
- `default8` — 8 心及以上显示
- `spring_Mon6` — 春季周一且 ≥6 心

系统选择 ≤ 当前心形等级的**最高**数字变体。

### 对话中的 Portrait 差分标记

在翻译文本中可嵌入标记来切换头像差分（见[附录 C](#附录-c-portrait-差分标记语法)）：

```
"这太棒了！$h"              → 切换到 happy 头像 (index 1)
"哦…这是什么…$s"            → 切换到 sad 头像 (index 2)  
"嗯？$u"                    → 切换到 unique 头像 (index 3)
"第一页内容$b第二页内容$h"   → $b 分页，第二页用 happy 头像
```

---

## 6. 步骤 4：翻译文件 — en_us.json / zh_cn.json

**文件**：
- `src/main/resources/assets/stardewcraft/lang/en_us.json`
- `src/main/resources/assets/stardewcraft/lang/zh_cn.json`

### 6.1 必须添加的 Key

```json
// ========== 实体名称 ==========
"entity.stardewcraft.npc.sam": "Sam",

// ========== 礼物好感反馈（如有 _msg 则自动使用，否则走 generic） ==========
"stardewcraft.npc.sam.gift_taste.loved": "Wow, you really like me or something?? This is awesome, thanks!",
"stardewcraft.npc.sam.gift_taste.liked": "Dude, is this for me? I love it!",
"stardewcraft.npc.sam.gift_taste.neutral": "Hey, thanks!",
"stardewcraft.npc.sam.gift_taste.disliked": "Hmm... not really my thing, but thanks I guess.",
"stardewcraft.npc.sam.gift_taste.hated": "This is... ugh. Why would you give me this?",

// ========== 所有对话条目 ==========
"stardewcraft.npc.sam.introduction": "Hey! I'm Sam. I play guitar and skateboard... you know, the usual.",
"stardewcraft.npc.sam.mon": "Monday already? I was up late playing music...",
"stardewcraft.npc.sam.default": "What's up? You should come jam with me sometime.$h",
// ... 所有 dialogue json 中引用的 key
```

### 6.2 Key 命名规范

```
stardewcraft.npc.{npcid}.{dialogue_key_lowercase}
```

- dialogue key 取自 `dialogue/{npcid}.json` 的 `entries` 中的 key，转小写
- 例如：`"AcceptBirthdayGift_Positive"` → `stardewcraft.npc.sam.acceptbirthdaygift_positive`

### 6.3 对话文本中的占位符

| 占位符 | 含义 | 替换时机 |
|--------|------|----------|
| `@` | 玩家名字 | 客户端显示时自动替换 |
| `$h` | 切换 happy 头像 | 客户端解析 |
| `$s` | 切换 sad 头像 | 客户端解析 |
| `$u` | 切换 unique 头像 | 客户端解析 |
| `$l` | 切换 love 头像 | 客户端解析 |
| `$a` | 切换 angry 头像 | 客户端解析 |
| `$b` | 翻页 | 客户端解析 |
| `$e` | 翻页（同 `$b`） | 客户端解析 |

### 6.4 工作量参考

以 Abigail 为例：约 **150 条**翻译 key（含季节/星期/心形变体/生日/礼物反馈/事件相关）。

---

## 7. 步骤 5：礼物偏好 — tastes/{npcid}.json

**文件**：`src/main/resources/data/stardewcraft/npc/tastes/{npcid}.json`

**新建**此文件：

```json
{
  "npc_id": "sam",
  "_vanilla_ref": "90=Cactus Fruit 220=Chocolate Cake 206=Pizza 541=Tigerseye",
  
  "loved": [
    "stardewcraft:cactus_fruit",
    "stardewcraft:pizza",
    "stardewcraft:tigerseye"
  ],
  "_loved_not_yet_in_mod": ["chocolate_cake(220)", "maple_bar(731)"],
  
  "liked": [
    "stardewcraft:joja_cola"
  ],
  
  "neutral": [],
  
  "disliked": [
    "stardewcraft:duck_egg",
    "stardewcraft:seaweed"
  ],
  
  "hated": [
    "stardewcraft:squid_ink"
  ],

  "loved_categories": [],
  "liked_categories": [],
  "disliked_categories": [],
  "hated_categories": [],

  "loved_msg": "Wow, you really like me or something?? This is awesome, thanks!",
  "liked_msg": "Dude, is this for me? I love it!",
  "neutral_msg": "Hey, thanks!",
  "disliked_msg": "Hmm... not really my thing, but thanks I guess.",
  "hated_msg": "This is... ugh. Why would you give me this?"
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `loved` / `liked` / `neutral` / `disliked` / `hated` | string[] | 物品 ID 列表 |
| `loved_categories` / `liked_categories` / ... | string[] | 物品类别标签匹配（读取 NBT `StardewCategory`） |
| `loved_msg` ~ `hated_msg` | string | NPC 专属好感反馈原文（系统自动映射到 lang key `stardewcraft.npc.{npcid}.gift_taste.{taste}`） |
| `_vanilla_ref` | string | 原版物品 ID 备注，开发参考用，不参与逻辑 |
| `_loved_not_yet_in_mod` | string[] | 尚未注册的物品备注 |

### 好感值计算公式（参考）

| 好感 | 基础 | 质量加成 | 生日加成 |
|------|------|----------|----------|
| Loved | +80 | ×quality | ×8 |
| Liked | +45 | ×quality | ×8 |
| Neutral | +20 | ×quality | ×8 |
| Disliked | -20 | 无 | ×8 |
| Hated | -40 | 无 | ×8 |

quality: silver=1.1, gold=1.25, iridium=1.5

### 匹配优先级

1. NPC 专属物品表 → 2. NPC 专属类别表 → 3. Universal 物品表 → 4. Universal 类别表 → 5. 默认 Neutral

---

## 8. 步骤 6：日程表 — schedules/{npcid}.json

**文件**：`src/main/resources/data/stardewcraft/npc/schedules/{npcid}.json`

**新建**此文件：

```json
{
  "npc_id": "sam",
  "override_vanilla": true,
  "_format": "time -> \"location @namedPointId facing [behavior]\"",
  
  "rain": {
    "600": "sam_house @sam_bedroom 2",
    "1200": "sam_house @sam_kitchen 1",
    "2000": "sam_house @sam_bedroom 3 sam_sleep"
  },
  
  "spring": {
    "600": "sam_house @sam_bedroom 2",
    "900": "town @sam_town_hangout 1",
    "1200": "saloon @sam_saloon_pool 0",
    "2000": "sam_house @sam_bedroom 3 sam_sleep"
  },
  
  "summer": {
    "600": "sam_house @sam_bedroom 2",
    "900": "beach @sam_beach_spot 2",
    "1700": "saloon @sam_saloon_pool 1",
    "2200": "sam_house @sam_bedroom 3 sam_sleep"
  },
  
  "fall": { "...": "..." },
  "winter": { "...": "..." }
}
```

### 时间点格式

```
"HHMM": "location_id @named_point_id facing_direction [behavior_token]"
```

- **时间**：4 位军用时间（`0600` ~ `2600`），必须为字符串
- **location_id**：对应 `NpcLocationAnchor` 注册的位置名
- **@named_point_id**：命名路径点（在 `npc_route_points.json` 中定义）
- **facing_direction**：`0`=北 `1`=东 `2`=南 `3`=西
- **behavior_token**（可选）：到达后执行的行为（如 `sam_sleep`）

### 条件优先级

系统 (`NpcScheduleRuntimeService`) 按以下顺序匹配 schedule：
1. `GreenRain`（绿雨特殊天气）
2. `rain`（下雨天）
3. `{season}_{dayInSeason}`（如 `spring_15`）
4. `{season}_{weekday}`（如 `spring_Mon`）
5. `{season}`（如 `spring`）
6. `default`（兜底）

### 注意事项

- **命名路径点**必须在 `npc_route_points.json` 和 `npc_route_profiles.json` 中预先注册 NPC 的位置锚点
- 还没有路径点时，可先用不带 `@` 的坐标格式或暂不创建 schedule（NPC 会停留在默认出生点）

---

## 9. 步骤 7：生日 — npc_birthdays.json

**文件**：`src/main/resources/data/stardewcraft/npc/events/npc_birthdays.json`

在 `"birthdays"` 对象中添加：

```json
"sam": {
  "season": "summer",
  "day": 17
}
```

- `season`：`spring` / `summer` / `fall` / `winter`（全小写）
- `day`：1 ~ 28

生日当天送礼好感值 **×8 倍**，并触发专用对话（`AcceptBirthdayGift_{Positive/Negative/Loved}`）。

---

## 10. 步骤 8：默认出生点 — default_spawns.json

**文件**：`src/main/resources/data/stardewcraft/npc/events/default_spawns.json`

在 `"spawns"` 对象中添加：

```json
"sam": {
  "x": 160.5,
  "y": -13.0,
  "z": 130.5,
  "yaw": 180.0
}
```

- `x`, `y`, `z`：Minecraft 世界坐标（先用 F3 在游戏内确认位置）
- `yaw`：朝向角度（`0`=南, `90`=西, `180`=北, `270`=东）
- 如果有 schedule 数据，系统优先使用 schedule 中的位置；此位置仅为初始后备

---

## 11. 步骤 9：社交界面（V键）接入

### 11.1 自动接入条件

社交界面显示 NPC 的条件是：

1. ✅ `base_profiles.json` 中 `implemented: true`
2. ✅ 存在 `textures/portraits/{npcid}.png`
3. ✅ 存在 `textures/mugshots/{npcid}.png`

三个条件全部满足后，该 NPC **自动出现在 V 键社交界面**，无需改代码。

### 11.2 可约会标记（❤️ 锁定显示）

如果该 NPC 是可约会/可结婚的角色，需要将其 ID 添加到硬编码集合：

**文件**：`src/main/java/com/stardew/craft/client/gui/menu/StardewGameMenuScreen.java`

```java
private static final Set<String> DATEABLE_NPCS = Set.of(
    "abigail", "alex", "elliott", "emily", "haley", "harvey",
    "leah", "maru", "penny", "sam", "sebastian", "shane"
    // ↑ 在此添加新的 dateable NPC ID
);
```

> **TODO**：后续应改为从 `NpcCapabilityProfile.datable()` 动态读取，避免硬编码。

### 11.3 社交界面显示的数据

| 显示项 | 数据来源 |
|--------|----------|
| NPC 名称 | `entity.stardewcraft.npc.{npcid}` 翻译 key |
| 小头像 | `textures/mugshots/{npcid}.png` |
| 心形数 | `NpcFriendshipDataManager` 计算的 hearts (0-14) |
| 本周送礼数 | `giftsThisWeek` (max 2) |
| 今日状态 | 是否已交谈/送礼 |

### 11.4 实体名称翻译（必须）

社交界面的 NPC 名字显示依赖翻译 key：

```json
// en_us.json
"entity.stardewcraft.npc.sam": "Sam"

// zh_cn.json
"entity.stardewcraft.npc.sam": "山姆"
```

---

## 12. 步骤 10：编译验证

```bash
./gradlew classes
```

编译通过后启动客户端：

```bash
./gradlew runClient
```

### 验证清单

- [ ] NPC 是否在默认出生点生成
- [ ] 右键对话是否正常（中英文显示正确）
- [ ] 对话 portrait 差分是否正确切换
- [ ] 送礼后好感反馈文本正确，portrait 情感标记显示
- [ ] 送礼后 NPC 头上是否有表情气泡（❤️/😊/😡/😢）
- [ ] V 键社交界面是否显示该 NPC
- [ ] 社交界面心形数量是否正确增减
- [ ] 生日送礼好感是否 ×8
- [ ] 日程表是否正常触发移动

---

## 13. 步骤 11：游戏内调试命令

项目内置了 NPC 调试命令（`NpcDebugCommand.java`），常用：

```
/stardew npc status             — 查看所有 NPC 状态
/stardew npc tp <npcid>         — 传送到 NPC 位置
/stardew npc friendship <npcid> — 查看好感数据
```

> 具体命令以最新代码为准，使用 Tab 补全查看可用子命令。

---

## 附录 A：原版性格数据参考表

| NPC | age | manners | socialAnxiety | optimism | gender | datable |
|-----|-----|---------|---------------|----------|--------|---------|
| Abigail | 0 (adult) | 0 (neutral) | 0 (outgoing) | 0 (positive) | 1 (F) | ✅ |
| Alex | 0 | 2 (rude) | 0 | 0 | 0 (M) | ✅ |
| Caroline | 0 | 1 (polite) | 0 | 0 | 1 | ❌ |
| Clint | 0 | 0 | 1 (shy) | 1 (negative) | 0 | ❌ |
| Demetrius | 0 | 0 | 0 | 0 | 0 | ❌ |
| Elliott | 0 | 1 | 0 | 0 | 0 | ✅ |
| Emily | 0 | 1 | 0 | 0 | 1 | ✅ |
| Evelyn | 0 | 1 | 0 | 0 | 1 | ❌ |
| George | 0 | 2 | 0 | 1 | 0 | ❌ |
| Gus | 0 | 1 | 0 | 0 | 0 | ❌ |
| Haley | 0 | 2 | 0 | 1 | 1 | ✅ |
| Harvey | 0 | 1 | 1 | 1 | 0 | ✅ |
| Jas | 2 (child) | 1 | 1 | 0 | 1 | ❌ |
| Jodi | 0 | 1 | 0 | 0 | 1 | ❌ |
| Kent | 0 | 0 | 0 | 1 | 0 | ❌ |
| Krobus | 0 | 0 | 1 | 1 | 0 | ❌ |
| Leah | 0 | 1 | 0 | 0 | 1 | ✅ |
| Lewis | 0 | 0 | 0 | 0 | 0 | ❌ |
| Linus | 0 | 1 | 1 | 1 | 0 | ❌ |
| Marnie | 0 | 1 | 0 | 0 | 1 | ❌ |
| Maru | 1 (teen) | 0 | 1 | 0 | 1 | ✅ |
| Pam | 0 | 2 | 0 | 1 | 1 | ❌ |
| Penny | 0 | 1 | 1 | 0 | 1 | ✅ |
| Pierre | 0 | 1 | 0 | 1 | 0 | ❌ |
| Robin | 0 | 0 | 0 | 0 | 1 | ❌ |
| Sam | 1 (teen) | 0 | 0 | 0 | 0 | ✅ |
| Sandy | 0 | 1 | 0 | 0 | 1 | ❌ |
| Sebastian | 1 (teen) | 2 | 1 | 1 | 0 | ✅ |
| Shane | 0 | 2 | 1 | 1 | 0 | ✅ |
| Vincent | 2 (child) | 0 | 0 | 0 | 0 | ❌ |
| Willy | 0 | 1 | 1 | 0 | 0 | ❌ |
| Wizard | 0 | 2 | 1 | 1 | 0 | ❌ |

> 数据来源：原版 `Content/Data/Characters.json`  
> age: 0=adult 1=teen 2=child | manners: 0=neutral 1=polite 2=rude | socialAnxiety: 0=outgoing 1=shy | optimism: 0=positive 1=negative | gender: 0=M 1=F

---

## 附录 B：对话 Key 命名规范

### 标准对话 Key（dialogue json entries）

| Key 格式 | 触发条件 | 示例 |
|----------|----------|------|
| `Introduction` | 首次对话 | |
| `default` | 无其他匹配时 | |
| `default{N}` | 心形≥N 的默认 | `default4`, `default8` |
| `Mon` ~ `Sun` | 星期几 | |
| `{season}` | 季节 | `spring`, `summer` |
| `{season}_{weekday}` | 季节+星期 | `spring_Mon` |
| `{season}_{day}` | 季节+日期 | `spring_15` |
| `rain` | 下雨天 | |

### 礼物相关 Key

| Key | 触发条件 |
|-----|----------|
| `AcceptBirthdayGift_Loved` | 生日 + Loved 礼物 |
| `AcceptBirthdayGift_Positive` | 生日 + Loved/Liked/Neutral |
| `AcceptBirthdayGift_Negative` | 生日 + Disliked/Hated |
| `AcceptGift_(O){ItemToken}` | 特定物品送礼反馈（如 `AcceptGift_(O)StardropTea`） |

### Lang Key 格式

```
stardewcraft.npc.{npcid}.{key_lowercase}
```

---

## 附录 C: Portrait 差分标记语法

在翻译文本中使用以下标记控制对话框头像：

| 标记 | Portrait Index | 含义 | 一般表情 |
|------|---------------|------|----------|
| `$h` | 1 | happy | 微笑、开心 |
| `$s` | 2 | sad | 难过、沮丧 |
| `$u` | 3 | unique | 特殊表情（因人而异） |
| `$l` | 4 | love | 爱慕、害羞 |
| `$a` | 5 | angry | 愤怒 |
| `$数字` | 自定义 | 自定义 index | 如 `$7` 用第 7 个差分 |
| `$b` | — | 翻页 | 对话分页 |
| `$e` | — | 翻页 | 同 `$b` |

**示例**：
```
"今天天气真好！$h$b我们去钓鱼吧？$u"
```
→ 第一页：happy 头像，"今天天气真好！"  
→ 第二页：unique 头像，"我们去钓鱼吧？"

**注意**：
- 标记放在文本**末尾**或 `$b` 分页符**之前**
- 送礼回复文本的 `$h`/`$s` 由系统自动添加（`buildGiftResponseText`），NPC 特定 dialogue 文本如果已包含则不会重复

---

## 附录 D：完整文件路径速查

以 `{npcid}` = `sam` 为例的所有文件：

```
# ====== 数据文件 (data/) ======
src/main/resources/data/stardewcraft/npc/capabilities/base_profiles.json    [编辑]
src/main/resources/data/stardewcraft/npc/dialogue/sam.json                  [新建]
src/main/resources/data/stardewcraft/npc/tastes/sam.json                    [新建]
src/main/resources/data/stardewcraft/npc/schedules/sam.json                 [新建]
src/main/resources/data/stardewcraft/npc/events/npc_birthdays.json          [编辑]
src/main/resources/data/stardewcraft/npc/events/default_spawns.json         [编辑]

# ====== 翻译文件 (lang/) ======
src/main/resources/assets/stardewcraft/lang/en_us.json                      [编辑]
src/main/resources/assets/stardewcraft/lang/zh_cn.json                      [编辑]

# ====== 美术资源 (assets/) —— 确认存在即可 ======
src/main/resources/assets/stardewcraft/textures/entity/npc/sam.png          [美术]
src/main/resources/assets/stardewcraft/textures/portraits/sam.png           [美术]
src/main/resources/assets/stardewcraft/textures/mugshots/sam.png            [美术]
src/main/resources/assets/stardewcraft/geo/entity/npc/sam.geo.json          [美术]
src/main/resources/assets/stardewcraft/animations/entity/npc/sam.animation.json [美术]

# ====== Java（仅 dateable 时需改） ======
src/main/java/com/stardew/craft/client/gui/menu/StardewGameMenuScreen.java [可选]
```

---

## Checklist 模板

复制以下 Checklist 用于跟踪每个 NPC 的开发进度：

```markdown
### NPC: _______ (ID: _______)

**数据配置**
- [ ] base_profiles.json — 能力 + 性格属性
- [ ] dialogue/{id}.json — 对话 key 映射
- [ ] tastes/{id}.json — 礼物偏好
- [ ] schedules/{id}.json — 日程表
- [ ] npc_birthdays.json — 生日
- [ ] default_spawns.json — 出生点

**翻译**
- [ ] en_us.json — entity 名称
- [ ] en_us.json — 礼物好感文本 (5条)
- [ ] en_us.json — 全部对话翻译 (约 ____ 条)
- [ ] zh_cn.json — entity 名称
- [ ] zh_cn.json — 礼物好感文本 (5条)
- [ ] zh_cn.json — 全部对话翻译 (约 ____ 条)

**美术资源（确认存在）**
- [ ] textures/entity/npc/{id}.png — 实体贴图
- [ ] textures/portraits/{id}.png — 对话头像 (128×320)
- [ ] textures/mugshots/{id}.png — 社交小头像 (16×24)
- [ ] geo/entity/npc/{id}.geo.json — 3D 模型
- [ ] animations/entity/npc/{id}.animation.json — 动画

**社交界面**
- [ ] mugshot + portrait 存在 → V键自动显示
- [ ] （若 datable）DATEABLE_NPCS 集合已添加

**验证**
- [ ] ./gradlew classes 编译通过
- [ ] 游戏内 NPC 正常生成
- [ ] 对话中英文显示正确
- [ ] portrait 差分正常切换
- [ ] 送礼 emote 气泡正常
- [ ] V键社交界面显示正确
- [ ] 生日好感 ×8 验证
- [ ] 日程移动正常
```
