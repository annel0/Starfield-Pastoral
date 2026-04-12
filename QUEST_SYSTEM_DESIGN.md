# Quest System 设计文档（Phase 1：普通任务）

> **范围**：仅涵盖 SDV `Quest` 体系（11 种类型）。`SpecialOrder`（特殊订单）留至 Phase 2。

---

## 目录

1. [总体架构](#1-总体架构)
2. [公告栏方块（BulletinBoardBlock）](#2-公告栏方块bulletinboardblock)
3. [数据模型（Quest 类层次）](#3-数据模型quest-类层次)
4. [JSON 数据驱动（Quests.json）](#4-json-数据驱动questsjson)
5. [QuestManager（运行时管理器）](#5-questmanager运行时管理器)
6. [事件派发与钩子](#6-事件派发与钩子)
7. [网络同步](#7-网络同步)
8. [玩家数据持久化（NBT）](#8-玩家数据持久化nbt)
9. [公告栏 UI —— 日历视图（左键）](#9-公告栏-ui--日历视图左键)
10. [公告栏 UI —— 每日任务面板（右键）](#10-公告栏-ui--每日任务面板右键)
11. [任务日志 UI（按键 U）](#11-任务日志-ui按键-u)
12. [音效系统](#12-音效系统)
13. [按键绑定](#13-按键绑定)
14. [Creative Tab 注册](#14-creative-tab-注册)
15. [SDV → MC 渲染差异备忘](#15-sdv--mc-渲染差异备忘)
16. [文件清单与包结构](#16-文件清单与包结构)
17. [实施里程碑](#17-实施里程碑)

---

## 1. 总体架构

```
┌──────────────┐     ┌──────────────────┐     ┌──────────────────┐
│ BulletinBoard│     │   QuestManager   │     │PlayerStardewData │
│    Block     │────▶│  (per-player)    │◀───▶│  (NBT persist)   │
└──────┬───────┘     └───────┬──────────┘     └──────────────────┘
       │ click               │ dispatch
       ▼                     ▼
┌──────────────┐     ┌──────────────────┐
│  Billboard   │     │   Event Hooks    │
│   Screen     │     │ (Monster/Fish/   │
│ (Calendar /  │     │  Ship/Craft/...) │
│  DailyQuest) │     └──────────────────┘
└──────────────┘
       │ keybind U
       ▼
┌──────────────┐
│  QuestLog    │
│   Screen     │
└──────────────┘
```

**核心原则**：
- 所有 Quest 数据从 `data/stardewcraft/quests.json` 读取，格式与 SDV `Data/Quests` 1:1 对应
- 运行时通过 `QuestManager`（附着在 `PlayerStardewData` 上）管理 accepted / completed 状态
- UI 严格复刻 SDV 像素，使用 `StardewRenderMapping` 做 SDV→MC 坐标变换
- 事件分发复用现有 NeoForge 事件总线 + 自定义 Quest 钩子接口

---

## 2. 公告栏方块（BulletinBoardBlock）

### 2.1 模型与纹理

| 项 | 值 |
|---|---|
| Blockbench 模型源 | `tmp_models/1.json` |
| 纹理源 | `tmp_models/1.png` |
| 目标模型路径 | `assets/stardewcraft/geo/block/decor/bulletin_board.geo.json` |
| 目标纹理路径 | `assets/stardewcraft/textures/block/decor/bulletin_board.png` |

- 模型为扁平面板（Z 厚度 0.77px），贴附北墙放置
- 外形尺寸：X 27.04 × Y 15.45 × Z 0.77 像素

### 2.2 方块类

```
类名: BulletinBoardBlock extends MapDecorWallStaticBlock
包:  com.stardew.craft.block.decor
```

| 属性 | 值 |
|---|---|
| 材质 | `MapColor.COLOR_BROWN` |
| 音效 | `SoundType.WOOD` |
| 硬度 | `-1` (不可在生存模式破坏) |
| 抗爆 | `3600000.0f` |
| noOcclusion | `true` |
| 模型ID | `"stardewcraft:block/decor/bulletin_board"` |

**交互逻辑**（override `useWithoutItem`）：

| 动作 | 触发 |
|---|---|
| **左键（攻击）** | 打开 **日历 Screen**（`BillboardCalendarScreen`） |
| **右键（使用）** | 打开 **每日任务 Screen**（`BillboardQuestScreen`） |

> SDV 原版中左键 = calendar、右键 = daily quest board。因为 MC 左键 = attack，需要 override `attack` 方法在 Block 中。但攻击方块默认走 `Block.attack()`，只在服务端触发。
>
> **方案**：改为都用右键。右键打开一个 **双 Tab 面板**（顶部有"日历 / 任务"切换），默认显示日历页。或沿用 SDV 左右键映射：
> - 右键 → `useWithoutItem` → 打开每日任务面板
> - 左键 → 客户端拦截 `InputEvent.InteractionKeyMappingTriggered`，检测对公告栏的攻击并打开日历
>
> **推荐方案**：右键一次进入双 Tab 界面，Tab 0 = 日历、Tab 1 = 每日任务。这更符合 MC 交互习惯。

### 2.3 注册

```java
// ModBlocks.java
public static final DeferredBlock<Block> BULLETIN_BOARD = BLOCKS.register("bulletin_board",
    () -> new BulletinBoardBlock(Block.Properties.of()
        .mapColor(MapColor.COLOR_BROWN)
        .sound(SoundType.WOOD)
        .strength(-1.0f, 3600000.0f)
        .noOcclusion()
        .noLootTable(),
    "stardewcraft:block/decor/bulletin_board"));
```

### 2.4 BlockItem

```java
// ModItems.java
public static final DeferredItem<BlockItem> BULLETIN_BOARD = ITEMS.register("bulletin_board",
    () -> new BlockItem(ModBlocks.BULLETIN_BOARD.get(), new Item.Properties()));
```

---

## 3. 数据模型（Quest 类层次）

### 3.1 基类 StardewQuest

```
包: com.stardew.craft.quest
```

```java
public class StardewQuest {
    // ─── 类型常量（与 SDV 1:1） ───
    public static final int TYPE_BASIC      = 1;
    public static final int TYPE_CRAFTING   = 2;
    public static final int TYPE_DELIVERY   = 3;
    public static final int TYPE_MONSTER    = 4;
    public static final int TYPE_SOCIALIZE  = 5;
    public static final int TYPE_LOCATION   = 6;
    public static final int TYPE_FISHING    = 7;
    public static final int TYPE_BUILDING   = 8;
    public static final int TYPE_HARVEST    = 9;
    public static final int TYPE_RESOURCE   = 10;
    public static final int TYPE_WEEDING    = 11;  // 未使用，保留

    // ─── 核心字段（复刻 SDV NetFields） ───
    protected String id;
    protected int questType;
    protected String title;
    protected String description;
    protected String objectiveText;
    protected String rewardDescription;
    protected int moneyReward;
    protected boolean accepted;
    protected boolean completed;
    protected boolean dailyQuest;           // 每日公告栏任务
    protected boolean showNew;              // UI 显示 "!" 标记
    protected boolean canBeCancelled;
    protected boolean destroy;              // 标记待移除
    protected int daysLeft;                 // 剩余天数, -1=无限
    protected int dayQuestAccepted;         // 接受日 (game day)
    protected List<String> nextQuests;      // 完成后触发的后续任务 ID

    // ─── 虚方法 ───
    public void onAccept(ServerPlayer player) {}
    public void onMonsterSlain(ServerPlayer player, String monsterType) {}
    public void onFishCaught(ServerPlayer player, String itemId, int count) {}
    public void onItemReceived(ServerPlayer player, String itemId, int count) {}
    public void onItemOfferedToNpc(ServerPlayer player, String npcId, String itemId) {}
    public void onRecipeCrafted(ServerPlayer player, String recipeId) {}
    public void onNpcSocialized(ServerPlayer player, String npcId) {}
    public void onWarped(ServerPlayer player, String location) {}
    public void onBuildingExists(ServerPlayer player, String buildingType) {}

    public List<String> getObjectiveDescriptions() { ... }
    public boolean isTimedQuest() { return daysLeft > 0; }
    public boolean shouldDisplayAsNew() { return showNew; }
    public boolean shouldDisplayAsComplete() { return completed; }
    public boolean hasReward() { return moneyReward > 0 || rewardDescription != null; }

    // ─── NBT 序列化 ───
    public CompoundTag save() { ... }
    public static StardewQuest load(CompoundTag tag) { ... }

    // ─── 完成逻辑 ───
    public void questComplete(ServerPlayer player) {
        completed = true;
        // 播放 questcomplete 音效
        // 如果 dailyQuest → incrementStat("BillboardQuestsDone")
        // 触发 nextQuests
        // 如果 moneyReward == 0 且无 rewardDescription → destroy = true
    }
}
```

### 3.2 子类清单

| 子类 | questType | 核心字段 | 完成钩子 | 奖励逻辑 |
|---|---|---|---|---|
| `ItemDeliveryQuest` | 3 | `targetNpc`, `itemId`, `number` | `onItemOfferedToNpc` | `item.price * 3` |
| `SlayMonsterQuest` | 4 | `monsterName`, `targetNpc`, `numberToKill`, `numberKilled` | `onMonsterSlain` | 固定 `reward` |
| `FishingQuest` | 7 | `targetNpc`, `itemId`, `numberToFish`, `numberFished` | `onFishCaught` | `numberToFish * fishPrice` |
| `ResourceCollectionQuest` | 10 | `targetNpc`, `itemId`, `number`, `numberCollected` | `onItemReceived` | 固定 `reward` |
| `CraftingQuest` | 2 | `itemId` | `onRecipeCrafted` | 从 JSON |
| `SocializeQuest` | 5 | `whoToGreet` (List), `total` | `onNpcSocialized` | 100 友谊/人 |
| `GoSomewhereQuest` | 6 | `whereToGo` | `onWarped` | 从 JSON |
| `HaveBuildingQuest` | 8 | `buildingType` | `onBuildingExists` | 从 JSON |
| `ItemHarvestQuest` | 9 | `itemId`, `number` | `onItemReceived` | 从 JSON |
| `LostItemQuest` | — | `npcName`, `locationId`, `itemId`, `tileX/Y`, `itemFound` | 特殊 | 从 JSON |
| `SecretLostItemQuest` | — | `npcName`, `itemId`, `friendshipReward`, `exclusiveQuestId` | 特殊 | 友谊 |

> `LostItemQuest` 和 `SecretLostItemQuest` Phase 1 可暂缓，优先实现前 8 种。

### 3.3 每日任务生成

SDV 每天从 4 类任务中随机选 1 种挂到公告栏：

1. **ItemDeliveryQuest**：随机 NPC + 随机物品 + 2天期限
2. **FishingQuest**：按季节鱼种 + Willy 作为 target
3. **ResourceCollectionQuest**：铜/铁/金/铱/木/石 + Clint/Robin
4. **SlayMonsterQuest**：按矿洞层数选怪 + 2天期限

生成逻辑在 `QuestManager.refreshDailyQuest(int gameDayOfYear, long seed)` 中实现，确保同一天同一世界种子 = 同一任务。

---

## 4. JSON 数据驱动（Quests.json）

### 4.1 文件路径

```
data/stardewcraft/quests.json
```

### 4.2 格式

与 SDV `Data/Quests` 完全一致，以 questId 为 key，value 为 `/` 分隔字符串：

```json
{
  "1": "Location/Meet The Wizard/在黑暗仪式之后去拜访巫师塔里的巫师。/拜访巫师塔/WizardHouse//-1//",
  "6": "Crafting/开始新生活/用锄头耕种土地然后种下防风草种子。////-1//false/",
  "9": "Social/自我介绍/去镇上和大家打个招呼。////-1/500/false/"
}
```

**字段索引**（`/` 分隔）：

| 索引 | 含义 | 说明 |
|---|---|---|
| 0 | questType 字符串 | `Basic`, `Crafting`, `ItemDelivery`, `Monster`, `Social`, `Location`, `Fishing`, `Building`, `ItemHarvest`, `LostItem`, `SecretLostItem` |
| 1 | title | 任务标题 |
| 2 | description | 描述文字 |
| 3 | objective | 目标文字（可空） |
| 4 | conditions | 空格分隔条件参数（类型相关） |
| 5 | nextQuests | 空格分隔后续任务 ID，`h` 前缀 = 仅主机 |
| 6 | moneyReward | 金钱奖励，`-1` = 无 |
| 7 | rewardDescription | 奖励描述文字 |
| 8 | canBeCancelled | `true`/`false` |
| 9 | targetMessage | 交付/讨伐完成后 NPC 对话（仅部分类型） |

### 4.3 解析

```java
public class QuestDataLoader {
    // 在 ServerStartedEvent 或 AddReloadListenerEvent 时加载
    // ResourceLocation = "stardewcraft:quests"
    // 解析为 Map<String, QuestData> 缓存
}
```

---

## 5. QuestManager（运行时管理器）

```
包: com.stardew.craft.quest
```

```java
public class QuestManager {
    private final ServerPlayer player;

    // ─── 运行时状态 ───
    private List<StardewQuest> questLog = new ArrayList<>();  // 当前接受的任务
    private StardewQuest dailyQuest;                          // 今日公告栏任务
    private int lastDailyQuestDay = -1;                       // 上次刷新日

    // ─── 核心 API ───
    public void acceptQuest(String questId) { ... }
    public void acceptQuest(StardewQuest quest) { ... }
    public void removeQuest(String questId) { ... }
    public boolean hasQuest(String questId) { ... }
    public StardewQuest getQuest(String questId) { ... }
    public List<StardewQuest> getQuestLog() { return questLog; }

    // ─── 每日任务 ───
    public StardewQuest getDailyQuest() { ... }
    public void refreshDailyQuest(int gameDay, long seed) { ... }
    public boolean isDailyQuestAccepted() { ... }

    // ─── 事件分发入口 ───
    public void onMonsterSlain(String monsterType) { ... }
    public void onFishCaught(String itemId, int count) { ... }
    public void onItemReceived(String itemId, int count) { ... }
    public void onItemOfferedToNpc(String npcId, String itemId) { ... }
    public void onRecipeCrafted(String recipeId) { ... }
    public void onNpcSocialized(String npcId) { ... }
    public void onWarped(String location) { ... }
    public void onBuildingExists(String buildingType) { ... }

    // ─── 天数推进 ───
    public void onDayStarted(int gameDay) {
        // 移除过期 dailyQuest
        // 减少所有 timed quest 的 daysLeft
        // 刷新今日 dailyQuest
        // 清除 destroyed quests
    }

    // ─── 持久化 ───
    public CompoundTag save() { ... }
    public void load(CompoundTag tag) { ... }
}
```

**获取方式**：通过 `PlayerStardewData.getQuestManager()` 访问。

---

## 6. 事件派发与钩子

复用项目中现有的事件发射点：

| SDV 钩子 | MC 对应事件/触发位置 | 现有实现 |
|---|---|---|
| `OnMonsterSlain` | `LivingDeathEvent` (已有 MonsterKillTracker) | ✅ 现有 `monsterKillCounts` |
| `OnFishCaught` | 钓鱼 minigame 完成回调 | ✅ 项目已有钓鱼系统 |
| `OnItemReceived` | `ItemPickupEvent` / 手动触发 | 部分现有 |
| `OnItemOfferedToNpc` | NPC 对话中赠送物品流程 | ✅ 已有 gift 系统 |
| `OnRecipeCrafted` | 自定义工作台合成完成 | ✅ 已有 crafting 系统 |
| `OnNpcSocialized` | 首次对话 / 打招呼逻辑 | 需新增 |
| `OnWarped` | `PlayerEvent.PlayerChangedDimensionEvent` 或自定义子空间进入 | 需适配 |
| `OnBuildingExists` | 建筑放置完成事件 | ✅ 已有 building 系统 |

**新增中转事件类**（在 NeoForge 事件总线上）：

```java
public class StardewQuestEvents {
    // 静态 helper 方法，从各触发点调用
    public static void fireMonsterSlain(ServerPlayer player, String monsterType) {
        PlayerStardewData.get(player).getQuestManager().onMonsterSlain(monsterType);
    }
    // ... 其他类似
}
```

---

## 7. 网络同步

### 7.1 包列表

| 方向 | 包名 | 内容 | 触发时机 |
|---|---|---|---|
| S→C | `QuestLogSyncPacket` | 完整 questLog 序列化 | 登录 / 任务变更 |
| S→C | `DailyQuestSyncPacket` | 今日公告栏任务数据 | 打开公告栏 / 日切换 |
| C→S | `AcceptQuestPacket` | questId | 玩家点击"接受" |
| C→S | `CancelQuestPacket` | questId | 玩家点击"放弃" |
| S→C | `QuestCompletePacket` | questId, moneyReward | 任务完成 |
| C→S | `ClaimRewardPacket` | questId | 玩家领取奖励 |

### 7.2 注册

```java
// ModNetwork.java (或现有网络注册位置)
// 使用 NeoForge PayloadRegistrar
```

---

## 8. 玩家数据持久化（NBT）

在 `PlayerStardewData` 中新增字段：

```java
// ─── Quest 系统 ───
private QuestManager questManager;

// save()
CompoundTag questTag = questManager.save();
tag.put("QuestManager", questTag);

// load()
if (tag.contains("QuestManager")) {
    data.questManager.load(tag.getCompound("QuestManager"));
}
```

**QuestManager NBT 结构**：

```
QuestManager: {
    QuestLog: [
        { Id: "1", Type: 6, Accepted: true, Completed: false, DaysLeft: -1, ... },
        { Id: "daily_37", Type: 3, DailyQuest: true, DaysLeft: 2, ... }
    ],
    LastDailyQuestDay: 15,
    BillboardQuestsDone: 3,
    CompletedQuestIds: ["1", "6", "9"]  // 永久记录
}
```

---

## 9. 公告栏 UI —— 日历视图（左键 / Tab 0）

### 9.1 纹理来源

| 纹理 | ResourceLocation | 说明 |
|---|---|---|
| Billboard 背景 | `textures/gui/billboard.png` | 从 SDV `LooseSprites/Billboard` 提取 |

### 9.2 SDV 原版尺寸（像素）

| 元素 | SDV 数值 |
|---|---|
| 日历背景 UV | `(0, 198, 301, 198)` 在 billboard.png 中 |
| 缩放 | 4× |
| 最终尺寸 | 1204 × 792 |
| 居中定位 | `Utility.getTopLeftPositionForCenteringOnScreen(1204, 792)` |

### 9.3 MC 中的坐标映射

```java
// 使用 StardewRenderMapping
// SDV 原始 viewport = 1280×720
// MC 实际 viewport = minecraft.window.guiScaledWidth × guiScaledHeight
//
// 缩放因子 = min(mcWidth / 1280.0, mcHeight / 720.0) × userScale
// 所有 SDV 坐标 × 缩放因子 = MC 绘制坐标
```

### 9.4 日历格子布局

```
28天 = 7列 × 4行
每格 SDV 大小 = 32×32 → 缩放后 128×128
X 起始偏移 = 152  (从窗口左边)
Y 起始偏移 = 200  (从窗口顶部)

格子[day] 位置:
  x = windowX + 152 + (day-1)%7 * 128
  y = windowY + 200 + (day-1)/7 * 128
  w = 124, h = 124
```

### 9.5 日历格子内容

| 内容 | 条件 | 渲染 |
|---|---|---|
| 日期数字 | 始终 | 格子左上角，Game1.dialogueFont |
| NPC 生日头像 | 当天有NPC生日 | 格子内 (+48, +28)，4×缩放 mugshot |
| 节日图标 | 当天有节日 | 动画帧 `billboard.png (1+frame*14, 398, 14, 12)` |
| 今日高亮 | `day == currentDay` | cursors `(379, 357, 3, 3)` 蓝框描边 |
| 每日任务星标 | 当天有已完成的每日任务 | billboard.png `(140, 397, 10, 11)` |

### 9.6 关闭按钮

```
UV: cursors (337, 494, 12, 12) → 4×
位置: (windowX + width - 20, windowY - 8)  (SDV 坐标)
```

---

## 10. 公告栏 UI —— 每日任务面板（右键 / Tab 1）

### 10.1 背景

```
UV: billboard.png (0, 0, 338, 198) → 4×
最终尺寸: 1352 × 792
```

### 10.2 文字布局

| 元素 | 位置 (SDV 4× 坐标) | 说明 |
|---|---|---|
| 无任务时提示 | `(windowX + 384, windowY + 320)` | "今天没有可用的任务" |
| 任务描述 | `(windowX + 320 + 32, windowY + 256)` | parseText 宽度 640 |
| 任务目标 | 描述下方 | 带箭头图标 |
| 奖励金额 | 右下区域 | 金币图标 + 数字 |

### 10.3 接受按钮

```
9-slice 按钮框: cursors (403, 373, 9, 9) → 4×
位置: (windowX + width/2 - 128, windowY + height - 128)
文字: "接受任务" 居中
```

### 10.4 已接受状态

任务已被此玩家接受后，面板显示"你已经接受了这个任务"，不再显示接受按钮。

### 10.5 社区中心徽章（条件渲染）

```
当社区中心完成时:
UV: billboard.png (0, 427, 39, 54) → 4×
位置: (windowX + 290*4, windowY + 59*4)
```

### 10.6 3次任务奖励（PrizeTicket）

```
每完成 3 次每日任务，给予 PrizeTicket
图标: Objects_2 (80, 128, 16, 16)
位置: (windowX + 215*4, windowY + 144*4)
```

> Phase 1 简化：不实现 PrizeTicket 物品，仅计数 `BillboardQuestsDone`。

---

## 11. 任务日志 UI（按键 U）

### 11.1 窗口规格

| 属性 | SDV 值 | 说明 |
|---|---|---|
| 宽度 | 832 | 固定 |
| 高度 | 576 | （中/法语 +64） |
| 每页任务数 | 6 | `questsPerPage = 6` |
| 背景框 | cursors `(384, 373, 18, 18)` 9-slice | `drawTextureBox` |

### 11.2 组件 UV 精确值

**所有 UV 坐标来自 SDV 源码 QuestLog.cs，必须严格复刻：**

| 组件 | 纹理 | UV (x, y, w, h) | 缩放 | 位置公式 (SDV) |
|---|---|---|---|---|
| 关闭按钮 | CURSORS | `(337, 494, 12, 12)` | 4× | `(x+width-20, y-8)` |
| 后退按钮 | CURSORS | `(352, 495, 12, 11)` | 4× | `(x-64, y+8)` |
| 前进按钮 | CURSORS | `(365, 495, 12, 11)` | 4× | `(x+width+64-48, y+height-48)` |
| 奖励盒 | CURSORS | `(293, 360, 24, 24)` | 4× | `(x+width/2-80, y+height-32-96)` 96×96 |
| 取消按钮 | CURSORS | `(322, 498, 12, 12)` | 4× | `(x+4, y+height+4)` |
| 上滚箭头 | CURSORS | `(421, 459, 11, 12)` | 4× | `(x+width+16, y+96)` |
| 下滚箭头 | CURSORS | `(421, 472, 11, 12)` | 4× | `(x+width+16, y+height-64)` |
| 滚动条 | CURSORS | `(435, 463, 6, 10)` | 4× | 宽24 高40 |
| 新任务标记 `!` | CURSORS | `(317, 410, 23, 9)` | 4× | 按钮内 `(+64+4, +44)` + scale动画 |
| 完成标记 `✓` | CURSORS | `(341, 410, 23, 9)` | 4× | 同上 |
| 非定时图标 | CURSORS | `(395, 497, 3, 8)` | 4× | 按钮内 `(+32, +28)` |
| 定时图标 ⏰ | CURSORS | `(410, 501, 9, 9)` | 4× | 同上 |
| 目标箭头 | CURSORS | `(412, 495, 5, 4)` | 4× | 旋转 π/2 |
| 金币图标 | CURSORS | `(280, 410, 16, 16)` | 4× | 奖励盒内 `(+16, +16)` |
| 进度条背景 | CURSORS2 | `(0, 224, 47, 12)` | — | 目标文字右侧 |
| 进度条刻度 | CURSORS2 | `(47, 224, 1, 12)` | — | 按进度填充 |
| 任务条目框 | CURSORS | `(384, 396, 15, 15)` | 9-slice | hover时Wheat色 |

### 11.3 列表页布局

```
任务按钮 [i] (i=0..5):
  x = windowX + 16
  y = windowY + 16 + i * ((height - 32) / 6)
  w = width - 32
  h = (height - 32) / 6 + 4

每个按钮内:
  图标 (定时/非定时): (+32, +28)
  任务标题: (+96, +20)    -- SpriteText
  新/完成标记: (+64+4, +44) -- 带 scale 动画 bounce
```

### 11.4 详情页布局

```
标题: SpriteText 水平居中于 (windowX + width/2, windowY + 32)
      drawStringWithScrollCenteredAt 带黄色底纹

时间图标 + 剩余天数: (windowX + 32, windowY + 48 - 8)  -- 仅 timed quest

描述: (windowX + 64, windowY + 96)
      parseText 宽度 = width - 128

目标列表: 描述下方 + 32px
  每个目标:
    箭头图标 (旋转90°) + 目标文字
    如果有数量进度 → 进度条

奖励区域 (完成后显示):
  "奖励" 文字
  奖励盒图标 (293, 360, 24, 24)
  金币图标 + 金额数字

取消按钮: (windowX + 4, windowY + height + 4) -- 仅 canBeCancelled

滚动条: 右侧边栏 (windowX + width + 16, ...)
  scissor rect: (windowX+32, windowY+96) 到 (windowY+height-32)
  scrollAmount: float, 每次±32px
```

### 11.5 标题滚轴

SDV 的 `SpriteText.drawStringWithScrollCenteredAt` 在标题下方绘制一个黄色卷轴背景。

UV 来自 `SpriteText` 内部精灵字体系统——本项目需使用 `StardewGuiUtil.drawTextureBox` 模拟或自定义实现。

### 11.6 滚动逻辑

```java
float scrollAmount;
float _contentHeight;   // 总内容高度
float _scissorRectHeight; // 可见区域高度

boolean needsScroll() { return _contentHeight > _scissorRectHeight; }

// 鼠标滚轮
scrollAmount = clamp(scrollAmount - scrollDelta * 32, 0, _contentHeight - _scissorRectHeight);

// 滚动条拖拽
scrollAmount = map(mouseY, scrollBarTop, scrollBarBottom, 0, maxScroll);
```

---

## 12. 音效系统

### 12.1 新增注册

```java
// ModSounds.java
public static final DeferredHolder<SoundEvent, SoundEvent> QUEST_COMPLETE =
    register("questcomplete");
```

对应音频文件已存在：`assets/stardewcraft/sounds/questcomplete.ogg`

### 12.2 sounds.json

```json
{
  "questcomplete": {
    "sounds": [{ "name": "stardewcraft:questcomplete", "stream": false }]
  }
}
```

> 需检查 `sounds.json` 中是否已有此条目，如无则新增。

### 12.3 使用

```java
// 任务完成时（服务端触发 S→C 同步后客户端播放）
player.playSound(ModSounds.QUEST_COMPLETE.get(), 1.0f, 1.0f);
```

---

## 13. 按键绑定

### 13.1 新增键位

```java
// ModKeyMappings.java
public static final KeyMapping QUEST_LOG = new KeyMapping(
    "key.stardewcraft.quest_log",
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_U,
    CATEGORY
);
```

### 13.2 客户端处理

```java
// KeyInputHandler.java (或现有按键处理位置)
// 在 ClientTickEvent 中检测
if (ModKeyMappings.QUEST_LOG.consumeClick()) {
    Minecraft.getInstance().setScreen(new QuestLogScreen());
}
```

### 13.3 语言文件

```json
// zh_cn.json
"key.stardewcraft.quest_log": "任务日志"

// en_us.json
"key.stardewcraft.quest_log": "Quest Log"
```

---

## 14. Creative Tab 注册

在 `StardewCraft.java` 的 `STARDEW_TAB` 注册 lambda 中追加：

```java
// misc 分区已有类似物品，直接在末尾添加
output.accept(ModBlocks.BULLETIN_BOARD.get());
```

公告栏属于 `misc` 类别物品。

---

## 15. SDV → MC 渲染差异备忘

### 15.1 坐标系

| 项 | SDV | MC |
|---|---|---|
| Y 轴方向 | 向下为正 | 向下为正（GUI 一致） |
| 缩放基准 | 固定 4× | 依赖 GUI Scale 设置 |
| 默认分辨率 | 1280×720 逻辑像素 | guiScaledWidth × guiScaledHeight |
| 字体 | SpriteFontTexture (SpriteText) | MC 原生 Font + 自定义 SpriteText 模拟 |

### 15.2 缩放策略

```
// SDV 用固定 4× 缩放
// MC 中需要动态适配:
float sdvScale = 4.0f;
float mcToSdvRatio = Math.min(
    (float) screenWidth / 1280f,
    (float) screenHeight / 720f
);
float finalScale = sdvScale * mcToSdvRatio;
```

参考项目已有的 `StardewRenderMapping` 做统一映射。

### 15.3 9-Slice 纹理框

SDV 通过 `IClickableMenu.drawTextureBox` 实现，项目中 `StardewGuiUtil.drawTextureBox` 已完整复刻。9-slice 参数：

- 主框：CURSORS `(384, 373, 18, 18)` → 边框 = 4px，中心 = 10px
- 按钮框：CURSORS `(403, 373, 9, 9)` → 边框 = 2px，中心 = 5px
- 任务条目框：CURSORS `(384, 396, 15, 15)` → 边框 = 4px，中心 = 7px

### 15.4 文字渲染

| SDV 函数 | MC 对应 | 注意 |
|---|---|---|
| `Game1.smallFont` | `Font` (0.75× 或直接用) | SDV smallFont ≈ 26px |
| `SpriteText.drawString` | 需自定义实现 | SDV 精灵字体是位图，本项目若已有 `SpriteText` 可复用 |
| `parseText(text, font, width)` | `font.split(FormattedText.of(text), width)` | MC 自带换行 |
| 文字阴影 | `font.drawShadow` 或 `guiGraphics.drawString(..., true)` | SDV 默认带阴影 |

### 15.5 Scissor / 裁剪

SDV 用 `_SetScissorRect(Rectangle)` 做区域裁剪（QuestLog 滚动使用）。MC 中使用：

```java
guiGraphics.enableScissor(x1, y1, x2, y2);
// ... 绘制内容 ...
guiGraphics.disableScissor();
```

---

## 16. 文件清单与包结构

### 16.1 新增 Java 文件

```
com.stardew.craft.quest/
├── StardewQuest.java               // 基类
├── ItemDeliveryQuest.java          // 子类
├── SlayMonsterQuest.java
├── FishingQuest.java
├── ResourceCollectionQuest.java
├── CraftingQuest.java
├── SocializeQuest.java
├── GoSomewhereQuest.java
├── HaveBuildingQuest.java
├── ItemHarvestQuest.java
├── LostItemQuest.java              // Phase 1 可选
├── SecretLostItemQuest.java        // Phase 1 可选
├── QuestManager.java               // 运行时管理
├── QuestDataLoader.java            // JSON 加载
└── StardewQuestEvents.java         // 事件分发

com.stardew.craft.block.decor/
└── BulletinBoardBlock.java         // 公告栏方块

com.stardew.craft.client.gui.quest/
├── BillboardScreen.java            // 公告栏主 Screen (含日历+任务两个Tab)
└── QuestLogScreen.java             // 任务日志 Screen

com.stardew.craft.network.quest/
├── QuestLogSyncPacket.java
├── DailyQuestSyncPacket.java
├── AcceptQuestPacket.java
├── CancelQuestPacket.java
├── QuestCompletePacket.java
└── ClaimRewardPacket.java
```

### 16.2 新增资源文件

```
assets/stardewcraft/
├── geo/block/decor/bulletin_board.geo.json     // Blockbench → GeckoLib 转换
├── textures/block/decor/bulletin_board.png      // 公告栏纹理
├── textures/gui/billboard.png                   // SDV Billboard 精灵图
└── sounds/questcomplete.ogg                     // ✅ 已存在

data/stardewcraft/
└── quests.json                                  // 任务数据
```

### 16.3 修改现有文件

| 文件 | 修改内容 |
|---|---|
| `ModBlocks.java` | 注册 `BULLETIN_BOARD` |
| `ModItems.java` | 注册 `BULLETIN_BOARD` BlockItem |
| `ModSounds.java` | 注册 `QUEST_COMPLETE` |
| `ModKeyMappings.java` | 注册 `QUEST_LOG` 键位 |
| `PlayerStardewData.java` | 新增 `QuestManager` 字段 + NBT |
| `StardewCraft.java` | Creative Tab 添加公告栏 |
| `zh_cn.json` / `en_us.json` | 添加键位 / UI 翻译文本 |
| `sounds.json` | 添加 `questcomplete` 条目 |
| 事件处理类 | 各钩子位置调用 `StardewQuestEvents.fireXxx()` |

---

## 17. 实施里程碑

### Phase 1-A：基础框架（优先级最高）

1. **Quest 数据模型**：`StardewQuest` 基类 + 前 5 种子类
2. **QuestDataLoader**：解析 `quests.json`
3. **QuestManager**：接受 / 完成 / 移除 / 天数推进
4. **PlayerStardewData 集成**：NBT 持久化
5. **网络包**：QuestLogSync + AcceptQuest + QuestComplete
6. **键位注册**：`QUEST_LOG` → U
7. **公告栏方块**：注册 + 模型 + 纹理 + 交互

### Phase 1-B：UI 界面

8. **QuestLogScreen**：列表页 + 详情页 + 滚动
9. **BillboardScreen 日历页**：28天网格 + 今日高亮 + NPC生日
10. **BillboardScreen 任务页**：每日任务展示 + 接受按钮

### Phase 1-C：事件钩子集成

11. **怪物击杀**：接入 MonsterKillTracker
12. **钓鱼**：接入钓鱼完成回调
13. **物品收集 / 采摘**：接入背包变化
14. **合成**：接入工作台完成
15. **NPC 赠礼 / 对话**：接入 gift / dialogue 系统
16. **每日任务刷新**：接入天数推进系统

### Phase 1-D：打磨

17. **音效**：`questcomplete` 注册 + 播放
18. **每日任务随机生成**：4 类随机生成器
19. **进度条**：数量型任务进度显示
20. **翻译**：完整中英文本

---

## 附录 A：SDV Billboard.png 纹理布局

```
┌─────────────────────────────────┐
│ (0,0)        338×198            │  ← 每日任务面板背景
│                                 │
│                                 │
├─────────────────────────────────┤
│ (0,198)      301×198            │  ← 日历背景
│                                 │
│                                 │
├──────┬──────────────────────────┤
│(1,398)  节日动画帧 6×(14×12)    │  ← 节日图标帧
│(140,397) 星标 10×11             │  ← 每日任务完成星
├──────┴──────────────────────────┤
│(0,427)  社区中心徽章 39×54       │
└─────────────────────────────────┘
```

## 附录 B：CURSORS UV 速查表（Quest 相关）

| 用途 | 纹理 | UV (x, y, w, h) |
|---|---|---|
| 关闭按钮 (×) | CURSORS | 337, 494, 12, 12 |
| 后退按钮 (◄) | CURSORS | 352, 495, 12, 11 |
| 前进按钮 (►) | CURSORS | 365, 495, 12, 11 |
| 取消按钮 | CURSORS | 322, 498, 12, 12 |
| 奖励盒 | CURSORS | 293, 360, 24, 24 |
| 上滚箭头 | CURSORS | 421, 459, 11, 12 |
| 下滚箭头 | CURSORS | 421, 472, 11, 12 |
| 滚动条 | CURSORS | 435, 463, 6, 10 |
| 新任务标记 (!) | CURSORS | 317, 410, 23, 9 |
| 完成标记 (✓) | CURSORS | 341, 410, 23, 9 |
| 非定时图标 | CURSORS | 395, 497, 3, 8 |
| 定时图标 (⏰) | CURSORS | 410, 501, 9, 9 |
| 目标箭头 | CURSORS | 412, 495, 5, 4 |
| 金币图标 | CURSORS | 280, 410, 16, 16 |
| 9-slice 主框 | CURSORS | 384, 373, 18, 18 |
| 9-slice 按钮框 | CURSORS | 403, 373, 9, 9 |
| 9-slice 任务条目框 | CURSORS | 384, 396, 15, 15 |
| 今日高亮框 | CURSORS | 379, 357, 3, 3 |
| 被动节日点 | CURSORS | 346, 392, 8, 8 |
| 进度条背景 | CURSORS2 | 0, 224, 47, 12 |
| 进度条刻度 | CURSORS2 | 47, 224, 1, 12 |
