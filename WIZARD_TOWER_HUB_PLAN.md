# 巫师塔维度枢纽 — 完整落实方案

## 一、核心构想

巫师塔是连接**主世界 (Overworld)** 与**星露谷维度 (Stardew Valley)** 的唯一通道。
玩家在主世界探索时发现一座由 `wizard_tower_exterior.schem` 生成的巫师塔结构，
右键门口交互实体后传送到**巫师塔内部亚空间**（已有的 `wizard_tower` 室内子空间）。
巫师塔内部作为**双向枢纽**，有两个出口：回到主世界、前往星露谷。

首次进入时触发巫师剧情对话，完成"交付末影之眼"任务后永久解锁星露谷入口。

---

## 二、整体流程

### 2.1 首次流程

```
主世界探索 → 发现巫师塔结构（自然生成）
  → 右键门口交互实体（X+6, Z+3, Y+0~+1）
  → 传送到巫师塔内部亚空间（18242, 71, 17097）

巫师塔内部（首次）→ 巫师自动触发剧情对话：
  [第1页] "嗯…你身上有一种独特的气场。让我看看……你不是这个世界原本的居民。$s"
  [第2页] "你的祖父——是的，我认识他。他曾经跨越维度来到鹈鹕镇，在那里拥有一座农场。$h"
  [第3页] "在他离开之前，他把农场的继承权留给了后代。而你，就是那份遗产的继承人。"
  [第4页] "我是拉斯莫迪斯，一名巫师。我在不同的维度间穿行，研究魔法与自然的奥秘。$b"
  [第5页] "我可以打开通往鹈鹕镇的传送门，但维度传送需要一种特殊的催化材料……"
  [第6页-query] "给我带一颗末影之眼来。那其中蕴含的虚空能量，正好可以稳定维度裂缝。
                  你愿意帮我吗？"
  → 选项: [好的，我去找!] / [让我再想想]

  → 玩家获得末影之眼后再次与巫师对话
  → 巫师收走末影之眼，开启星露谷传送门（设置解锁标志）
  → 从此巫师塔内部出现"前往星露谷"交互入口
```

### 2.2 日常流程

```
              主世界巫师塔外部
                    │
                   右键
                    ↓
          ┌─ 巫师塔内部（枢纽）─┐
          │                       │
     [交互实体A]            [交互实体B]
    "回到主世界"          "前往星露谷"
    （原exit位置）        （解锁后出现）
          │                       │
          ↓                       ↓
   主世界：玩家记录         星露谷维度：
   的来源坐标             巫师塔门口(340,-1,-43)
```

### 2.3 从星露谷维度返回主世界

```
星露谷维度巫师塔门口(340,-1,-42)
    → 右键进入巫师塔内部

巫师塔内部
    → 与巫师对话
    → query选项: "我想回到原来的世界" / "没什么事"
    → 选"回去" → 跨维度传送到主世界记录的坐标
    → 选"没事" → 关闭对话，留在巫师塔

或者直接右键 [交互实体A "回到主世界"] → 跨维度传送
```

---

## 三、技术架构

### 3.1 涉及的系统

| 系统 | 用途 | 现状 |
|------|------|------|
| 主世界结构生成 | 巫师塔自然生成 | **新增** |
| 跨维度传送 | Overworld ↔ Stardew Valley | **新增** |
| 巫师塔内部亚空间 | 已有的 `wizard_tower` 子空间 | **改造**（双出口） |
| 玩家数据持久化 | 解锁标志 + 来源坐标 | **扩展** `PlayerStardewData` |
| NPC 对话系统 | 巫师剧情对话 + query 选项 | **新增**对话节点 |
| 交互实体 & Portal Hints | 门口渲染提示 | **复用** + 新增条目 |

### 3.2 文件清单

```
新增:
  src/main/java/com/stardew/craft/wizard/
    WizardTowerManager.java          -- 主世界巫师塔管理（结构放置、交互实体）
    WizardQuestHandler.java          -- 末影之眼交付逻辑 + 解锁判断
    CrossDimensionTeleporter.java    -- Overworld ↔ Stardew Valley 跨维度传送
  src/main/resources/data/stardewcraft/worldgen/
    structure/wizard_tower_overworld.json          -- 结构定义
    structure_set/wizard_tower_overworld.json       -- 生成规则（随机间距）
    template_pool/wizard_tower_overworld.json       -- 模板池

修改:
  InteriorSubspaceManager.java       -- 巫师塔内部新增第二个交互实体（去星露谷）
  InteriorPortalInteractionEvents.java -- 支持跨维度传送逻辑
  PlayerStardewData.java             -- 新增 wizardQuestComplete + overworldReturnPos
  PortalHintPositions.java           -- 新增主世界侧 + 内部新出口的位置
  wizard.json (dialogue)             -- 新增剧情对话节点
  en_us.json / zh_cn.json            -- 新增翻译键
```

---

## 四、详细设计

### 4.1 主世界巫师塔结构生成

**结构文件**: `wizard_tower_exterior.schem`（已有）

**生成方式**: NeoForge 1.21.1 数据驱动结构生成

```json
// data/stardewcraft/worldgen/structure/wizard_tower_overworld.json
{
  "type": "minecraft:single_pool_element",
  "biomes": "#minecraft:is_forest",          // 森林系生物群系
  "spawn_overrides": {},
  "step": "surface_structures",
  "start_pool": "stardewcraft:wizard_tower_overworld",
  "size": 1,
  "max_distance_from_center": 80,
  "start_height": { "type": "minecraft:world_surface" },
  "project_start_to_heightmap": "WORLD_SURFACE_WG",
  "terrain_adaptation": "beard_thin"
}

// data/stardewcraft/worldgen/structure_set/wizard_tower_overworld.json
{
  "structures": [
    {
      "structure": "stardewcraft:wizard_tower_overworld",
      "weight": 1
    }
  ],
  "placement": {
    "type": "minecraft:random_spread",
    "spacing": 48,        // 平均每48个区块出现一个
    "separation": 24,     // 最少间隔24个区块
    "salt": 19960408
  }
}
```

**交互实体放置**: 结构加载后，在 `X+6, Z+3, Y+0` 和 `Y+1` 放置两个 Interaction 实体（1宽×2高）。
- Tag: `sdv_portal_target:wizard_tower_overworld_enter`
- 功能: 右键后传送到巫师塔内部亚空间（18242, 71, 17097）

> **注意**: 由于是主世界结构，不能在 `InteriorSubspaceManager.ensurePortalInteractions()` 中处理。
> 需要在结构放置时通过 structure processor 或在 `StructureLoader` 后处理中生成交互实体。
> 备选方案: 使用 `StructureProcessor` 在加载时自动替换特定标记方块为交互实体。

### 4.2 跨维度传送系统

**`CrossDimensionTeleporter.java`**

```java
public class CrossDimensionTeleporter {
    /**
     * 从主世界传送到星露谷维度的巫师塔内部。
     * 记录玩家在主世界的位置，供返回时使用。
     */
    public static void overworldToWizardInterior(ServerPlayer player) {
        // 1. 记录主世界坐标到 PlayerStardewData
        saveOverworldReturnPos(player);

        // 2. 获取星露谷维度
        ServerLevel stardewLevel = player.server.getLevel(ModDimensions.STARDEW_VALLEY_KEY);

        // 3. 确保巫师塔内部已加载
        InteriorSubspaceManager.ensureLoaded(stardewLevel, "wizard_overworld_portal");

        // 4. 跨维度传送到巫师塔内部
        BlockPos spawnAbs = WIZARD_TOWER_ORIGIN.offset(WIZARD_TOWER_INDOOR_SPAWN_OFFSET);
        player.teleportTo(stardewLevel,
            spawnAbs.getX() + 0.5, spawnAbs.getY(), spawnAbs.getZ() + 0.5,
            180.0F, 0.0F);

        // 5. 设置室内标志
        applyInteriorFlag(player, PortalMode.ENTRANCE);
    }

    /**
     * 从星露谷维度返回主世界。
     * 读取之前记录的主世界坐标。
     */
    public static void wizardInteriorToOverworld(ServerPlayer player) {
        ServerLevel overworld = player.server.getLevel(Level.OVERWORLD);
        BlockPos returnPos = loadOverworldReturnPos(player);

        if (returnPos == null) {
            // Fallback: 使用世界出生点
            returnPos = overworld.getSharedSpawnPos();
        }

        player.teleportTo(overworld,
            returnPos.getX() + 0.5, returnPos.getY(), returnPos.getZ() + 0.5,
            player.getYRot(), player.getXRot());

        // 清除室内标志
        applyInteriorFlag(player, PortalMode.EXIT);
    }
}
```

### 4.3 巫师塔内部改造（双出口）

**现有布局**:
- 入口交互实体: `wizard_tower_exit` @ origin+(1,1,9) → 传送到星露谷室外 (340,-1,-43)

**新增**:
- 第二个交互实体: `wizard_tower_to_stardew` → 传送到星露谷室外 (340,-1,-43)
  - 位置: 内部另一侧（具体坐标待确认结构布局，暂定 origin+(3,1,9) 或对面墙）
  - 条件: 仅在 `wizardQuestComplete=true` 时可用
- 修改原有 `wizard_tower_exit`:
  - 重命名语义为"回到来源世界"
  - 如果玩家是从主世界进来的 → 跨维度传送回主世界
  - 如果玩家是从星露谷进来的 → 传送到星露谷室外 (340,-1,-43)（保持现有行为）

**简化方案**: 不新增第二个交互实体。将现有 exit 改为智能出口：
- 检查玩家来源维度 → 自动传送回正确的位置
- 内部只保留一个出口交互实体（就是现在的 `wizard_tower_exit`）
- 巫师对话的 query 选项覆盖"前往星露谷"功能（首次解锁 + 日常快捷传送）

### 4.4 玩家数据扩展

**`PlayerStardewData.java` 新增字段**:

```java
// 巫师任务完成状态
private boolean wizardQuestComplete = false;

// 玩家从主世界进入时的返回坐标
@Nullable
private BlockPos overworldReturnPos = null;

// 玩家进入巫师塔内部时的来源维度
@Nullable
private ResourceKey<Level> wizardSourceDimension = null;
```

**NBT 序列化**:
```java
// save
tag.putBoolean("wizardQuestComplete", wizardQuestComplete);
if (overworldReturnPos != null) {
    tag.putInt("owReturnX", overworldReturnPos.getX());
    tag.putInt("owReturnY", overworldReturnPos.getY());
    tag.putInt("owReturnZ", overworldReturnPos.getZ());
}
if (wizardSourceDimension != null) {
    tag.putString("wizardSourceDim", wizardSourceDimension.location().toString());
}

// load
wizardQuestComplete = tag.getBoolean("wizardQuestComplete");
if (tag.contains("owReturnX")) {
    overworldReturnPos = new BlockPos(tag.getInt("owReturnX"), tag.getInt("owReturnY"), tag.getInt("owReturnZ"));
}
```

### 4.5 巫师 NPC 对话系统

#### 4.5.1 对话节点设计

在 `wizard.json` 中新增以下节点（使用现有 `$q`/`$r` 语法）：

| 节点 ID | 触发条件 | 内容 |
|---------|---------|------|
| `wizard_intro_1` | 首次进入巫师塔，未完成任务 | 剧情第1-5页 |
| `wizard_intro_ask` | 接上 | query: 带末影之眼？ |
| `wizard_has_eye` | 玩家持有末影之眼，与巫师对话 | "啊，你带来了…太好了" |
| `wizard_portal_open` | 交付完成 | "传送门已稳定，你可以自由通行了" |
| `wizard_daily` | 日常已解锁 | query: "去星露谷" / "回主世界" / "聊聊天" |
| `wizard_daily_locked` | 日常未解锁 | "去找末影之眼来吧" |

#### 4.5.2 对话触发逻辑

```
玩家与巫师交互
  ├─ wizardQuestComplete == false?
  │   ├─ 首次见面（无对话历史）→ wizard_intro_1 → ... → wizard_intro_ask
  │   ├─ 已见过但未交付 → 检查背包是否有末影之眼
  │   │   ├─ 有 → wizard_has_eye → 收走1个 → 设置 wizardQuestComplete=true → wizard_portal_open
  │   │   └─ 无 → wizard_daily_locked
  │   └─
  └─ wizardQuestComplete == true?
      └─ wizard_daily (query选项)
          ├─ "带我去鹈鹕镇" → 传送到星露谷外部(340,-1,-43)
          ├─ "我想回去" → 跨维度传送回主世界
          └─ "没什么事" → 关闭
```

#### 4.5.3 服务端处理

**`WizardQuestHandler.java`**:
- 注册到 `NpcInteractionService` 的特殊处理链中
- 在 `handleClientQuestionAnswer` 的回调中检测 `npcId=="wizard"` + 特定 `nextNodeId`
- `nextNodeId == "wizard_go_stardew"` → 调用 `CrossDimensionTeleporter.wizardInteriorToStardewOutdoor()`
- `nextNodeId == "wizard_go_overworld"` → 调用 `CrossDimensionTeleporter.wizardInteriorToOverworld()`

### 4.6 InteriorPortalInteractionEvents 改造

现有系统只处理 `Stardew Valley` 维度内的传送。需要扩展：

```java
// 现有限制
if (!ModDimensions.STARDEW_VALLEY.equals(player.serverLevel().dimension())) {
    return;  // ← 主世界的交互实体会被忽略！
}
```

**改造方案**:

```java
// 扩展：允许主世界的巫师塔交互实体
ResourceKey<Level> dim = player.serverLevel().dimension();
if (!ModDimensions.STARDEW_VALLEY.equals(dim) && !Level.OVERWORLD.equals(dim)) {
    return;
}

// 如果在主世界，只处理巫师塔相关的 portal target
if (Level.OVERWORLD.equals(dim)) {
    Optional<String> targetId = findTagValue(target.getTags(), TAG_TARGET_PREFIX);
    if (targetId.isEmpty() || !targetId.get().equals("wizard_tower_overworld_enter")) {
        return;
    }
    // 跨维度传送到星露谷维度的巫师塔内部
    CrossDimensionTeleporter.overworldToWizardInterior(player);
    event.setCanceled(true);
    return;
}
```

### 4.7 Portal Hint 更新

**`PortalHintPositions.java`** 新增：

```java
// 巫师塔内部 → 暂时不加新的交互实体，通过巫师对话传送
// 如果后续需要在内部加"回到主世界"的交互实体，在此新增

// 主世界巫师塔的 hint 不处理（不在星露谷维度，不渲染）
```

> 主世界的 Portal Hint 渲染需要移除 `ModDimensions.STARDEW_VALLEY` 维度限制，
> 或者为主世界单独处理。但考虑到主世界只有一个巫师塔入口，优先级较低。

---

## 五、对话文本

### 5.1 中文 (zh_cn.json)

```json
{
  "stardewcraft.npc.wizard.intro_1": "嗯…你身上有一种独特的气场。让我看看…你不是这个世界原本的居民。$s",
  "stardewcraft.npc.wizard.intro_2": "你的祖父——是的，我认识他。他曾经跨越维度来到鹈鹕镇，在那里拥有一座农场。$h",
  "stardewcraft.npc.wizard.intro_3": "在他离开之前，他把农场的继承权留给了后代。而你，就是那份遗产的继承人。",
  "stardewcraft.npc.wizard.intro_4": "我是拉斯莫迪斯，一名巫师。我在不同的维度间穿行，记录自然与魔法的奥秘。$b",
  "stardewcraft.npc.wizard.intro_5": "我可以打开通往鹈鹕镇的传送门，但维度传送需要一种特殊的催化材料…",
  "stardewcraft.npc.wizard.intro_ask": "给我带一颗末影之眼来。那其中蕴含的虚空能量，正好可以稳定维度裂缝。你愿意帮忙吗？$q wizard_intro_ask wizard_accept#$r wizard_accept 0 wizard_accepted#好的，我去找！#$r wizard_decline 0 null#让我再想想",
  "stardewcraft.npc.wizard.accepted": "很好。末影之眼…你应该知道怎么获取它。找到之后来见我。$h",
  "stardewcraft.npc.wizard.declined": "不着急，等你准备好了再来找我。",
  "stardewcraft.npc.wizard.reminder": "末影之眼…你找到了吗？那是稳定维度裂缝的关键。",
  "stardewcraft.npc.wizard.has_eye": "啊，你带来了末影之眼！让我看看…$h 是的，虚空能量非常充沛。传送门很快就能稳定下来。",
  "stardewcraft.npc.wizard.portal_open": "传送门已经稳定了。从现在起，你可以自由往返于两个世界之间。$h 鹈鹕镇的居民们会很高兴见到新面孔的。祝你好运，农夫。",
  "stardewcraft.npc.wizard.daily_hub": "你想去哪里？$q wizard_daily wizard_go_stardew#$r wizard_go_stardew 0 null#带我去鹈鹕镇#$r wizard_go_overworld 0 null#我想回去#$r wizard_stay 0 null#没什么事",
  "stardewcraft.npc.wizard.daily_locked": "末影之眼…你找到了吗？没有它，传送门无法稳定。"
}
```

### 5.2 英文 (en_us.json)

```json
{
  "stardewcraft.npc.wizard.intro_1": "Hmm... there's a peculiar aura about you. Let me see... You're not originally from this world.$s",
  "stardewcraft.npc.wizard.intro_2": "Your grandfather — yes, I knew him. He once crossed dimensions to reach Pelican Town, where he owned a farm.$h",
  "stardewcraft.npc.wizard.intro_3": "Before he left, he passed the inheritance of that farm to his descendants. And you... you are that heir.",
  "stardewcraft.npc.wizard.intro_4": "I am Rasmodius, a wizard. I travel between dimensions, studying the mysteries of nature and magic.$b",
  "stardewcraft.npc.wizard.intro_5": "I can open a portal to Pelican Town, but dimensional travel requires a special catalyst...",
  "stardewcraft.npc.wizard.intro_ask": "Bring me an Eye of Ender. The void energy within it is exactly what I need to stabilize the dimensional rift. Will you help?$q wizard_intro_ask wizard_accept#$r wizard_accept 0 wizard_accepted#Sure, I'll find one!#$r wizard_decline 0 null#Let me think about it",
  "stardewcraft.npc.wizard.accepted": "Good. An Eye of Ender... you should know how to obtain one. Come back when you have it.$h",
  "stardewcraft.npc.wizard.declined": "No rush. Come find me when you're ready.",
  "stardewcraft.npc.wizard.reminder": "The Eye of Ender... have you found one? It's the key to stabilizing the dimensional rift.",
  "stardewcraft.npc.wizard.has_eye": "Ah, you brought an Eye of Ender! Let me see...$h Yes, the void energy is quite potent. The portal should stabilize soon.",
  "stardewcraft.npc.wizard.portal_open": "The portal is stable now. From this point on, you may travel freely between the two worlds.$h The residents of Pelican Town will be happy to see a new face. Good luck, farmer.",
  "stardewcraft.npc.wizard.daily_hub": "Where would you like to go?$q wizard_daily wizard_go_stardew#$r wizard_go_stardew 0 null#Take me to Pelican Town#$r wizard_go_overworld 0 null#I want to go back#$r wizard_stay 0 null#Nothing, just visiting",
  "stardewcraft.npc.wizard.daily_locked": "The Eye of Ender... have you found it? Without it, the portal cannot be stabilized."
}
```

---

## 六、开发阶段划分

### Phase 1: 核心传送管道（优先级最高）

1. **`PlayerStardewData` 扩展**
   - 新增 `wizardQuestComplete`、`overworldReturnPos`、`wizardSourceDimension` 字段
   - NBT 序列化/反序列化
   - 预计改动: ~30 行

2. **`CrossDimensionTeleporter.java`**
   - `overworldToWizardInterior()`: 记录来源坐标 → 跨维度到星露谷巫师塔内部
   - `wizardInteriorToOverworld()`: 读取记录 → 跨维度回主世界
   - `wizardInteriorToStardewOutdoor()`: 传送到星露谷室外 (340,-1,-43)
   - 预计: ~80 行

3. **`InteriorPortalInteractionEvents.java` 改造**
   - 移除主世界维度限制（仅对 `wizard_tower_overworld_enter` 放行）
   - 主世界交互触发跨维度传送
   - 预计改动: ~20 行

4. **主世界巫师塔交互实体**
   - 初期方案: 使用命令手动放置测试
   - 生产方案: Structure Processor 自动生成
   - 预计: ~40 行

### Phase 2: 巫师剧情对话

5. **`WizardQuestHandler.java`**
   - NPC 交互拦截: 检查 `wizardQuestComplete` 状态
   - 对话分支路由: intro / remind / has_eye / daily
   - 末影之眼检测 + 扣除
   - 处理 `AnswerNpcQuestionPayload` 中的传送指令
   - 预计: ~120 行

6. **对话文本注入**
   - `wizard.json` 新增节点
   - `zh_cn.json` / `en_us.json` 新增翻译键
   - 预计: ~30 行

### Phase 3: 主世界结构自然生成

7. **Worldgen JSON 数据包**
   - `structure/wizard_tower_overworld.json`
   - `structure_set/wizard_tower_overworld.json`
   - `template_pool/wizard_tower_overworld.json`
   - 预计: 3 个 JSON 文件

8. **Structure Processor** （可选）
   - 自动将结构中的标记方块替换为交互实体
   - 或在 `StructurePlaceSettings` 的 post-process 中处理

### Phase 4: UI 润色

9. **Portal Hint 扩展**
   - 主世界巫师塔入口也显示"▶ 进入"提示
   - 条件渲染: 未解锁时显示"?"或不同颜色
   - 预计: ~20 行

10. **巫师塔内部视觉提示**
    - 解锁前: 星露谷方向的墙壁有"封印"粒子效果
    - 解锁后: 传送门粒子效果

---

## 七、关键技术点

### 7.1 跨维度传送 API

```java
// NeoForge 1.21.1 跨维度传送标准用法
ServerLevel targetLevel = server.getLevel(targetDimensionKey);
player.teleportTo(targetLevel, x, y, z, yaw, pitch);
// 会自动处理维度切换、区块加载、客户端同步
```

### 7.2 背包物品检测与扣除

```java
// 检查玩家是否持有末影之眼
ItemStack eyeSlot = null;
for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
    ItemStack stack = player.getInventory().getItem(i);
    if (stack.is(Items.ENDER_EYE)) {
        eyeSlot = stack;
        break;
    }
}
if (eyeSlot != null) {
    eyeSlot.shrink(1); // 扣除1个
}
```

### 7.3 NPC 对话拦截点

现有 `NpcInteractionService.handleInteraction()` 在 NPC 交互时触发。
需要在其中增加巫师特殊逻辑的钩子:

```java
// 在 handleInteraction 中:
if ("wizard".equals(npcId)) {
    if (WizardQuestHandler.handleWizardInteraction(player)) {
        return; // 已处理，跳过普通日常对话
    }
}
```

### 7.4 Structure Processor 放置交互实体

```java
// 在结构放置完成后的回调中:
// 根据结构原点 + 偏移量计算交互实体位置
BlockPos portalPos = structureOrigin.offset(6, 0, 3);
for (int dy = 0; dy < 2; dy++) {
    BlockPos pos = portalPos.offset(0, dy, 0);
    Interaction interaction = EntityType.INTERACTION.create(level);
    interaction.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
    interaction.addTag("sdv_portal_target:wizard_tower_overworld_enter");
    level.addFreshEntity(interaction);
}
```

---

## 八、风险与备选

| 风险 | 影响 | 备选方案 |
|------|------|---------|
| 主世界结构生成注册复杂 | Phase 3 延期 | 先用指令手动放置测试 |
| 交互实体在主世界重启后丢失 | 传送门不可用 | 使用 ForgeChunkManager 保持加载，或改用方块实体 |
| 玩家在巫师塔内下线再上线 | 来源维度信息可能丢失 | 持久化到 PlayerStardewData（已设计） |
| 多人服务器：多个玩家同时使用 | 返回坐标冲突 | 每个玩家独立的 overworldReturnPos（已设计） |

---

## 九、验收标准

- [ ] 主世界右键巫师塔门 → 传送到巫师塔内部亚空间
- [ ] 首次进入 → 巫师自动触发剧情对话（6页 + query 选项）
- [ ] 持有末影之眼与巫师对话 → 扣除1个，设置解锁标志
- [ ] 解锁后与巫师对话 → query 选项: 去星露谷 / 回主世界 / 取消
- [ ] 选"去星露谷" → 传送到星露谷维度 (340,-1,-43)
- [ ] 选"回主世界" → 跨维度传送回主世界记录的坐标
- [ ] 星露谷巫师塔入口 (340,-1,-42) → 正常进入巫师塔内部
- [ ] 巫师塔内部出口 → 智能判断（来自主世界回主世界，来自星露谷回星露谷）
- [ ] 玩家下线重连 → 解锁状态和返回坐标不丢失
- [ ] LAYOUT_VERSION 正确递增
