# 社区中心献祭系统 — 完整实现规划

> **目标**: 将星露谷原版社区中心献祭系统的**全部流程、动画、剧情**完整复刻到 Minecraft 模组中  
> **基准**: 已完整阅读 `CommunityCenter.cs`(1311行), `JunimoNoteMenu.cs`(1826行), `Bundle.cs`(458行), `Junimo.cs`(730行), `BundleGenerator.cs`(267行), 以及 Town/WizardHouse 事件脚本

---

## 现状评估

### 已完成 ✅ (~60%)

| 模块 | 文件 | 完成度 |
|------|------|--------|
| Bundle 数据加载 | `BundleDataManager`, `BundleDefinition`, `BundleIngredient` | 100% |
| 物品映射 | `BundleItemResolver`, `bundle_item_map.json` | 100% (87+物品) |
| 服务端献祭逻辑 | `BundleMenu.tryDeposit()`, `tryPurchaseVault()` | 100% |
| 数据持久化 | `CommunityCenterSavedData` (NBT) | 100% |
| 区域解锁顺序 | `CommunityCenterProgress.shouldNoteAppearInArea()` | 100% |
| 网络同步 | 5个 Payload (Deposit/Purchase/Sync/Reward/Open) | 100% |
| GUI 概览页 | `BundleScreen` 区域圆球 + 详情页 | 95% |
| Bundle 完成横幅 | `ScreenSwipe` (3阶段展开/暂停/滑出) | 100% |
| 奖励领取 | `BundleRewardMenu` + `BundleRewardScreen` | 90% |
| Junimo 实体骨架 | `JunimoEntity` (GeckoLib, 颜色, 持物) | 40% |

### 完全缺失 ❌ (~40%)

| 模块 | 原版对应 | 重要性 |
|------|----------|--------|
| **剧情线: 初次发现 CC** | Event 611439 (Lewis 带路+Junimo 闪现) | 🔴 核心 |
| **剧情线: 巫师解锁** | Event 112 (巫师药水+森林幻视+canReadJunimoText) | 🔴 核心 |
| **乱码文字系统** | `scrambledText`, Junimo 象形字体 | 🟠 重要 |
| **Junimo 搬运动画** | 完成 bundle → Junimo 出现拿包裹走回小屋 | 🔴 核心 |
| **区域修复过场** | `doRestoreAreaCutscene` 4阶段 (暂停→出现→辉光→闪白+替换) | 🔴 核心 |
| **星星放置动画** | Junimo 取星→寻路到星盘→旋转星星 | 🟠 重要 |
| **全部完成: 告别舞蹈** | 6只 Junimo 跳舞→淡出→加载 Junimo Hut | 🟠 重要 |
| **全部完成: 镇上庆祝** | Event 191393 (颁奖+Morris+Pierre) | 🟡 锦上添花 |
| **CC 建筑实体** | 废墟→修复区域的方块替换系统 | 🔴 核心 |
| **遗漏奖励箱** | 离开时未领取 → 下次在(22,10)生成箱子 | 🟡 细节 |
| **Junimo AI 行为** | 友好跟随/非友好逃跑/寻路到小屋 | 🟠 重要 |

---

## 架构设计

### 核心问题: SDV → MC 的根本差异

| SDV 概念 | MC 适配方案 |
|----------|------------|
| CC 是一个固定地图位置 | **Interior Subspace** — CC 注册为远坐标室内结构，与其他建筑同一体系 |
| `loadArea()` 覆盖瓦片 | **从 refurbished.schem 缓存局部覆盖** — 两套等大结构，按区域绝对坐标范围取方块覆写 |
| 自顶而下 2D 视角 | 3D 建筑内部 — 需要将 2D 区域概念映射到 3D 房间 |
| `globalFadeToBlack` 屏幕效果 | **客户端 Overlay** — 自定义 `GuiLayer` 实现淡入淡出/闪白 |
| `Game1.freezeControls` | `player.setFrozen(true)` 或自定义 capability 冻结输入 |
| 固定 NPC 调度系统 | MC 无 NPC 调度 — Lewis/巫师剧情需要**对话触发**或**区域进入事件** |
| `specificTemporarySprite` | MC **粒子系统** + **临时实体** |
| 像素精灵动画 | **GeckoLib 模型动画** (Junimo 已有) |
| CC 内部通过门进入 | **Portal 系统** — 室外 interaction entity → 传送到远坐标室内 |

### 包结构规划

```
com.stardew.craft.communitycenter/
├── block/
│   ├── JunimoNoteBlock.java          ← 已有
│   ├── CommunityCenterBlock.java     ← [新] CC 核心控制方块 (存储区域状态)
│   └── StarPlaqueBlock.java          ← [新] 星盘方块 (显示完成星星)
├── client/
│   ├── BundleScreen.java             ← 已有 (需增加乱码模式)
│   ├── BundleRewardScreen.java       ← 已有
│   ├── ScreenSwipe.java              ← 已有
│   ├── ScreenFade.java               ← [新] 淡入淡出/闪白 overlay
│   └── ScrambledTextRenderer.java    ← [新] Junimo 象形文字渲染
├── cutscene/
│   ├── CutsceneManager.java          ← [新] 演出状态机 (冻结玩家+摄像头)
│   ├── AreaRestoreCutscene.java      ← [新] 4阶段区域修复演出
│   ├── GoodbyeDanceCutscene.java     ← [新] 全部完成告别演出
│   └── CutscenePayload.java          ← [新] S→C 演出触发 packet
├── data/                             ← 已有 (完整)
├── junimo/
│   ├── JunimoBehavior.java           ← [新] Junimo CC 行为控制器
│   ├── JunimoSpawner.java            ← [新] 按区域/颜色生成临时 Junimo
│   └── JunimoGoals.java              ← [新] AI Goals (寻路到小屋/星盘/跟随)
├── menu/                             ← 已有 (完整)
├── network/                          ← 已有 (需扩展)
│   ├── ... (5 个已有 payload)
│   ├── CutscenePayload.java          ← [新] 演出触发
│   └── AreaRestorePayload.java       ← [新] 区域方块替换同步
├── state/                            ← 已有 (需扩展)
│   ├── CommunityCenterSavedData.java ← 已有 (需增加星星计数)
│   ├── CommunityCenterProgress.java  ← 已有
│   └── CCStoryFlags.java             ← [新] 故事进度标记管理
├── restore/
│   ├── CCAreaBounds.java             ← [新] 每个区域的绝对坐标范围 (用户提供)
│   ├── CCRefurbishedCache.java       ← [新] 缓存 refurbished.schem 方块数据
│   └── AreaRestoreHandler.java       ← [新] 执行区域方块替换 (从缓存取方块覆盖)
└── CommunityCenterSystem.java        ← 已有 (需扩展为系统入口)
```

---

## 分阶段实现计划

### Phase 1: 故事前置 — 乱码系统 + 巫师解锁 (优先级: 高)

> **目的**: 让献祭不是"打开就能用"，而是有故事驱动的解锁过程

#### 1.1 故事标记系统 `CCStoryFlags`

```java
public final class CCStoryFlags {
    // 使用 PlayerStardewData.mailFlags 存储
    public static final String CC_DOOR_UNLOCKED   = "ccDoorUnlock";     // CC 大门解锁
    public static final String SEEN_JUNIMO_NOTE    = "seenJunimoNote";  // 首次看到卷轴
    public static final String CAN_READ_JUNIMO     = "canReadJunimoText"; // 能读懂 Junimo 文字
    public static final String CC_IS_COMPLETE      = "ccIsComplete";    // 全部完成
    // 区域完成邮件
    public static final String CC_PANTRY      = "ccPantry";
    public static final String CC_CRAFTS_ROOM = "ccCraftsRoom";
    public static final String CC_FISH_TANK   = "ccFishTank";
    public static final String CC_BOILER_ROOM = "ccBoilerRoom";
    public static final String CC_VAULT       = "ccVault";
    public static final String CC_BULLETIN    = "ccBulletin";
}
```

**改动点**:
- `PlayerStardewData` 已有 `mailFlags` 系统 (`hasMailFlag`, `addMailFlag`)，直接复用
- `BundleScreen` 增加 `scrambledText` 检查: 无 `canReadJunimoText` flag 时显示乱码
- `JunimoNoteBlock.use()` 首次交互时添加 `seenJunimoNote` flag

#### 1.2 乱码文字渲染 `ScrambledTextRenderer`

**原版行为**: 
- `SpriteText.drawString(..., junimoText: true)` 将每个字符替换为 Junimo 象形文字
- 区域名在乱码模式下用英文显示（即使是中文版）
- Bundle 名称显示为 `"???"`

**MC 适配方案**:
- 自定义字体资源 `stardewcraft:junimo_script` — 将 a-z 映射到 Junimo 符号贴图
- `ScrambledTextRenderer.draw()`: 将翻译文本转为随机 Junimo 字符渲染
- `BundleScreen` 概览页: 区域标题用乱码字体 + 英文原名
- `BundleScreen` 详情页: bundle 名称显示 `"???"`，物品名称显示 `"???"`

#### 1.3 巫师解锁流程

**原版流程**: 首次看到卷轴 → 次日收到巫师信 → 去巫师塔 → 事件(药水+幻视) → 解锁

**MC 适配** (简化版，无需完整事件系统):

| 步骤 | 触发 | 效果 |
|------|------|------|
| 1. 首次右键 JunimoNote | `!hasMailFlag("seenJunimoNote")` | 添加 `seenJunimoNote`，显示乱码界面 |
| 2. 次日登录/睡觉后 | Overnight 检查 `seenJunimoNote && !canReadJunimoText` | MailSystem 发送巫师邀请信 |
| 3. 找到巫师 NPC 对话 | 对话触发或使用特殊物品 | 播放简短粒子效果 + 添加 `canReadJunimoText` |
| 4. 再次打开 JunimoNote | `hasMailFlag("canReadJunimoText")` | 文字变为可读 |

**备选方案** (如果巫师 NPC 尚未实现):
- 使用「巫师药水」物品 — 合成/找到后直接右键使用即可解锁
- 或者在巫师塔特定方块位置放置一个交互方块

---

### Phase 2: Junimo 搬运动画 (优先级: 高)

> **目的**: Bundle 完成后不能无声无息，需要 Junimo 出来拿走包裹

#### 2.1 Junimo AI Goals

当前 `JunimoEntity.registerGoals()` 是空的。需要添加:

```
JunimoGoals:
├── CarryBundleToHutGoal   — 持有 bundle 时寻路到小屋位置并消失
├── FetchStarGoal          — 从小屋取星星寻路到星盘
├── FollowPlayerGoal       — 友好状态下跟随玩家 (距离5格内)
├── FleeFromPlayerGoal     — 非友好状态远离玩家并淡出
└── StayPutGoal            — 演出时原地不动
```

#### 2.2 Junimo 生成器 `JunimoSpawner`

```java
// 在 bundle 完成后由服务端调用
public static JunimoEntity spawnBundleCarrier(ServerLevel level, BlockPos noteBlockPos, int areaId) {
    JunimoEntity junimo = new JunimoEntity(ModEntities.JUNIMO.get(), level);
    junimo.setJunimoColor(getColorForArea(areaId));  // 用原版颜色映射
    junimo.setHolding(true);
    // 在玩家附近随机位置生成
    BlockPos spawnPos = findOpenAdjacentPos(level, noteBlockPos);
    junimo.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
    junimo.setAlpha(0); // fadeIn 效果
    level.addFreshEntity(junimo);
    // 设置寻路目标: CC 内的 Junimo 小屋位置
    junimo.getNavigation().moveTo(hutPos.getX(), hutPos.getY(), hutPos.getZ(), 0.5);
    return junimo;
}
```

#### 2.3 区域颜色映射 (来自 `Junimo.cs` 构造函数)

| Area | 颜色 | RGB |
|------|------|-----|
| 0 Pantry | LimeGreen | `0x32CD32` |
| 1 Crafts Room | Orange | `0xFFA500` |
| 2 Fish Tank | Turquoise | `0x40E0D0` |
| 3 Boiler Room | Tan | `0xD2B48C` |
| 4 Vault | Gold | `0xFFD700` |
| 5 Bulletin Board | BlanchedAlmond | `0xFFEBCD` |
| 6 Abandoned Joja | Purple | `0xA014DC` |

#### 2.4 触发时机

在 `BundleMenu.tryDeposit()` 中，当 `isBundleComplete()` 为 true 且 `!areAllBundlesInAreaComplete()` 时:
1. 发送 `JunimoSpawnPayload` (S→所有附近C)
2. 客户端播放 "junimoMeep1" 音效
3. Junimo 实体 fadeIn → 拿着彩色包裹 → 寻路到小屋 → 到达后播放 "Ship" 音效 → 移除实体
4. 25% 概率在头顶显示感谢文字泡泡

---

### Phase 3: 区域修复过场 (优先级: 高)

> **目的**: 献祭一个区域的所有 bundle 后，整个区域华丽修复

#### 3.1 CC 建筑结构系统 — 复用 Interior Subspace 体系

**核心发现**: 项目已有成熟的 `InteriorSubspaceManager` + `StructureLoader` 体系，CC 应直接套用。

**现有系统工作方式**:
- 所有室内结构作为 `.schem` 文件放在 `resources/data/stardewcraft/structures/interior/`
- `InteriorSubspaceManager` 以 `(origin, spawnOffset, exitPortalOffset)` 三元组注册每个建筑
- `StructureLoader.loadAndPlaceSchematic()` 解析 `.schem` 格式并逐块放置
- 室内坐标在 Stardew 维度远坐标区 (X>10000, Z>10000)，通过 Portal 系统进出
- 版本号控制重新装载 (`LAYOUT_VERSION`)

**CC 适配方案**: 两套等大 `.schem`，同一原点覆盖放置

```
resources/data/stardewcraft/structures/interior/
├── community_center_ruins.schem        ← 废墟状态 (初始装载)
└── community_center_refurbished.schem  ← 修复状态 (区域修复时局部覆盖)
```

**关键设计**: 两个 `.schem` 文件**尺寸完全一致**，放在**同一个原点**。
修复某个区域时，不需要替换整个结构——只需要把 refurbished 的对应区域方块覆盖到 ruins 上。

**区域坐标管理方案**:

用户将提供每个区域的**绝对坐标范围** (min/max)。代码中存储为:

```java
public record CCAreaBounds(BlockPos absoluteMin, BlockPos absoluteMax) {
    /** 从绝对坐标获取相对于结构原点的偏移 */
    public BlockPos relativeMin(BlockPos structureOrigin) {
        return absoluteMin.subtract(structureOrigin);
    }
    public BlockPos relativeMax(BlockPos structureOrigin) {
        return absoluteMax.subtract(structureOrigin);
    }
    public int sizeX() { return absoluteMax.getX() - absoluteMin.getX() + 1; }
    public int sizeY() { return absoluteMax.getY() - absoluteMin.getY() + 1; }
    public int sizeZ() { return absoluteMax.getZ() - absoluteMin.getZ() + 1; }
}
```

代码使用时自动从绝对坐标换算相对偏移:
```java
// 用户给出绝对坐标，代码内部自动处理
public static final BlockPos CC_ORIGIN = new BlockPos(xxxxx, 70, xxxxx); // 待定
public static final CCAreaBounds[] AREA_BOUNDS = {
    new CCAreaBounds(new BlockPos(绝对X1, Y1, Z1), new BlockPos(绝对X2, Y2, Z2)), // Area 0 - Pantry
    new CCAreaBounds(new BlockPos(绝对X1, Y1, Z1), new BlockPos(绝对X2, Y2, Z2)), // Area 1 - Crafts Room
    // ... 用户提供绝对坐标后填入
};
```

**修复流程**:
1. 从 `community_center_refurbished.schem` 读取完整方块数据（缓存）
2. 取出目标区域的 `absoluteMin ~ absoluteMax` 范围内的方块
3. 逐块覆盖到世界中（同一位置，因为两套 schem 共享原点和尺寸）
4. 无需额外的偏移计算——绝对坐标直接对应世界坐标

```java
public static void restoreArea(ServerLevel level, int areaId) {
    CCAreaBounds bounds = AREA_BOUNDS[areaId];
    // 从缓存的 refurbished 方块数据中，取出该区域对应的方块
    // 直接 level.setBlock() 到绝对坐标
    for (int x = bounds.absoluteMin.getX(); x <= bounds.absoluteMax.getX(); x++)
        for (int y = bounds.absoluteMin.getY(); y <= bounds.absoluteMax.getY(); y++)
            for (int z = bounds.absoluteMin.getZ(); z <= bounds.absoluteMax.getZ(); z++) {
                BlockState state = refurbishedCache.getBlock(x - origin.getX(), y - origin.getY(), z - origin.getZ());
                level.setBlock(new BlockPos(x, y, z), state, 3);
            }
}
```

#### 3.2 过场状态机 `AreaRestoreCutscene`

```
Phase 0: FIRST_PAUSE (1000ms)
  ├─ 冻结玩家输入 (停止移动/交互)
  ├─ 锁定摄像头朝向修复区域中心
  └─ 等待 1000ms

Phase 1: JUNIMO_APPEAR (3000ms)
  ├─ 玩家跳跃 + 粒子效果 (惊讶)
  ├─ 在修复区域内随机位置生成 3-6 只临时 Junimo (该区域对应颜色)
  ├─ 每只 Junimo 带闪光粒子 fadeIn
  └─ 播放 "junimoMeep1" 随机音效

Phase 2: JUNIMO_DANCE (可变时长, ~4000ms)
  ├─ 屏幕辉光渐强 (ScreenFade glow overlay, 白色半透明叠加)
  ├─ 播放风声音效
  ├─ 显示 Junimo 名言消息 (actionbar 或 title)
  │   ├─ "我们感谢你的慷慨..."
  │   ├─ "森林精灵赐予这片土地新生..."
  │   └─ (从已翻译的 Junimo 语录池随机选取)
  ├─ 灰尘粒子效果
  └─ 播放 "junimoMeep1" 间歇音效

Phase 3: RESTORE (瞬间 + 2000ms 收尾)
  ├─ 全屏闪白 (ScreenFade flash, 500ms 渐淡)
  ├─ CCStructureManager.restoreArea(areaId) ← 替换方块
  ├─ 播放 "wand" 音效 (魔法棒) → mc:entity.evoker.cast_spell 或自定义
  ├─ 播放 "junimoStarSong" 音乐 → 自定义循环 SoundEvent
  ├─ Junimo 开始 fadeOut 并逐个消失
  └─ 解冻玩家
```

#### 3.3 客户端 Overlay `ScreenFade`

```java
public class ScreenFade {
    public enum Mode { FADE_TO_BLACK, FADE_FROM_BLACK, FLASH_WHITE, GLOW }
    
    // 注册为 GuiLayerManager overlay
    // FADE_TO_BLACK: 黑色矩形 alpha 0→1
    // FADE_FROM_BLACK: 黑色矩形 alpha 1→0
    // FLASH_WHITE: 白色矩形 alpha 1→0 (500ms)
    // GLOW: 白色矩形 alpha 0→0.4 (渐强, 用于 Phase 2 辉光)
}
```

#### 3.4 网络协议

```
CutscenePayload (S→C):
  - byte cutsceneType (0=AreaRestore, 1=GoodbyeDance)
  - byte phase (0-3)
  - int areaId
  - BlockPos centerPos (摄像头目标)
  
AreaRestorePayload (S→C):
  - int areaId
  - BlockPos origin
  - CompoundTag structureData (或直接由服务端放置方块，客户端自动接收方块更新)
```

**注意**: 方块替换本身由服务端执行，MC 会自动将方块变更同步给客户端。所以 `AreaRestorePayload` 可能不需要传结构数据，只需通知客户端"某区域已修复"来更新本地状态。

---

### Phase 4: 星星系统 (优先级: 中)

#### 4.1 星盘方块 `StarPlaqueBlock`

- 自定义 BlockEntity，渲染 0-6 颗星星
- 每个星星是一个小型旋转发光模型 (GeckoLib 或手动 `RenderType.EYES`)
- `numberOfStars` 从 `CommunityCenterSavedData` 读取

#### 4.2 星星放置动画

当区域修复过场结束后:
1. 一只 Junimo 从小屋位置出发 (`holdingStar = true`)
2. 寻路到星盘位置
3. 到达后: 播放旋转星星粒子 + "yoba" 音效 (→ `mc:block.beacon.activate`) + 闪白
4. 星盘方块 `numberOfStars++`
5. Junimo 消失

---

### Phase 5: 全部完成 — 告别 + 庆祝 (优先级: 中)

#### 5.1 告别舞蹈 `GoodbyeDanceCutscene`

**触发**: 最后一个区域修复过场结束后检查 `areAllAreasComplete()`

```
Phase 0: FADE_IN (2000ms)
  ├─ 屏幕渐黑
  ├─ 将玩家传送到 CC 中央观察位置
  └─ 摄像头缓慢移向 Junimo 小屋

Phase 1: JUNIMO_APPEAR (3000ms)
  ├─ 6 只不同颜色的 Junimo 在中央位置生成 (每个区域一种颜色)
  ├─ 逐个 fadeIn
  └─ 每只说一句告别语 (actionbar 轮流显示)
      "再见!" / "谢谢你!" / "做得好!" / "我们永远感谢你!"
      "这里又美起来了!" / "森林精灵会记住你的!"

Phase 2: DANCE (5000ms)
  ├─ Junimo 原地跳跃动画 (GeckoLib jump 循环)
  ├─ 彩色粒子特效
  └─ 播放欢快音乐

Phase 3: FAREWELL (4000ms)
  ├─ 所有 Junimo fadeOut
  ├─ 等待 3600ms (原版 timing)
  ├─ 闪白 + 加载 JunimoHut 区域 (Area 8 方块替换)
  ├─ "wand" 音效
  ├─ 显示消息: "祝尼魔们回到了自己的世界...但他们说会一直关注这里。"
  └─ 添加 mailFlag `ccIsComplete`
```

#### 5.2 镇上庆祝 (简化适配)

原版在次日进入镇上触发大型 NPC 集会事件。MC 适配考虑:

**方案 A (推荐 — 如果有 NPC 系统)**:
- 次日 Lewis NPC 对话触发庆祝对话链
- 赠予 "星露谷英雄奖" 物品

**方案 B (简化)**:
- 次日登录时发送邮件: Lewis 的信 + 奖励物品
- CC 外部出现庆祝装饰方块/旗帜

---

### Phase 6: CC 室内注册到 Interior Subspace (优先级: 高, 与美术并行)

> CC 室内和其他建筑一样，注册到 InteriorSubspaceManager

#### 6.1 注册方式

在 `InteriorSubspaceManager` 中新增 CC 条目，模式与现有建筑完全一致:

```java
// ---- 社区中心 ----
private static final String CC_RUINS_STRUCTURE_PATH = 
    "data/stardewcraft/structures/interior/community_center_ruins.schem";
private static final String CC_REFURBISHED_STRUCTURE_PATH = 
    "data/stardewcraft/structures/interior/community_center_refurbished.schem";
private static final BlockPos CC_ORIGIN = new BlockPos(xxxxx, 70, xxxxx); // 待分配
private static final BlockPos CC_INDOOR_SPAWN_OFFSET = new BlockPos(?, 1, ?);
private static final BlockPos CC_INDOOR_EXIT_PORTAL_OFFSET = new BlockPos(?, 1, ?);
private static final BlockPos CC_OUTDOOR_ENTRY_POS = new BlockPos(?, ?, ?); // 镇上 CC 门口
private static final BlockPos CC_OUTDOOR_INTERACTION_BASE = new BlockPos(?, ?, ?);
```

初始加载时放置 `ruins.schem`，与其他建筑一起按版本装载。
修复区域时从 `refurbished.schem` 缓存数据局部覆盖。

#### 6.2 Portal 设置

```
CC 室外门口: sdv_portal_target:community_center_enter (mode: entrance)
CC 室内出口: sdv_portal_target:community_center_exit  (mode: exit)
```

#### 6.3 每个房间放置 JunimoNoteBlock

每个区域的固定位置放一个 `JunimoNoteBlock`(AREA=0~5)，玩家进入 CC 室内后右键交互。
区域修复后该方块被 refurbished 结构覆盖消失（或变为装饰方块）。

#### 6.4 结构内的关键位置 (待用户填入绝对坐标)

| 位置 | 用途 | 绝对坐标 |
|------|------|---------|
| CC 结构原点 | `.schem` 放置锚点 | 待定 |
| 玩家出生点 | 入口传送落点 | 待定 |
| 出口 Portal | 返回室外 | 待定 |
| Junimo 小屋 | 搬运目的地 | 待定 |
| 星盘位置 | 放星星 | 待定 |
| Area 0 范围 | 食品储藏室 min~max | 待定 |
| Area 1 范围 | 工艺室 min~max | 待定 |
| Area 2 范围 | 鱼缸区 min~max | 待定 |
| Area 3 范围 | 锅炉房 min~max | 待定 |
| Area 4 范围 | 金库 min~max | 待定 |
| Area 5 范围 | 公告栏 min~max | 待定 |
| Area 8 范围 | Junimo Hut 中央 | 待定 |
| 各区 JunimoNote 位置 | 卷轴方块 | 待定 |

---

### Phase 7: 音效资源 (优先级: 中)

#### 需要的自定义音效

| 音效ID | 用途 | 来源建议 |
|--------|------|---------|
| `stardewcraft:junimo_meep` | Junimo 叫声 | 从 SDV 提取或类似合成音 |
| `stardewcraft:bundle_deposit` | 物品沉积 | 已有? 对应 "newArtifact" |
| `stardewcraft:area_restore_wand` | 区域修复闪光 | mc:entity.evoker.cast_spell |
| `stardewcraft:junimo_star_song` | 区域修复音乐 | 从 SDV 提取或原创 |
| `stardewcraft:star_place` | 星星放置 | mc:block.beacon.activate |
| `stardewcraft:yoba` | 星星动画完成 | mc:block.beacon.power_select |
| `stardewcraft:screen_swipe` | 横幅展开 | 已在 ScreenSwipe 中 |

---

## 工作量估算与优先级排序

### Tier 1 — 最小可玩循环 (使献祭"活起来")

| 任务 | 修改文件 | 新建文件 | 复杂度 |
|------|---------|---------|--------|
| T1.1 故事标记系统 | `BundleScreen`, `JunimoNoteBlock` | `CCStoryFlags` | ★☆☆ |
| T1.2 乱码文字 (简化版: 显示 ???) | `BundleScreen` | — | ★☆☆ |
| T1.3 巫师解锁 (物品/对话) | — | 视现有巫师系统 | ★★☆ |
| T1.4 Junimo 搬运 (bundle 完成后) | `BundleMenu`, `JunimoEntity` | `JunimoSpawner`, AI Goals | ★★★ |
| T1.5 区域完成通知 (邮件+消息) | `BundleMenu` | — | ★☆☆ |

### Tier 2 — 核心演出 (区域修复)

| 任务 | 修改文件 | 新建文件 | 复杂度 |
|------|---------|---------|--------|
| T2.1 CC 结构建造 (美术) | — | `community_center_ruins.schem` + `community_center_refurbished.schem` | ★★★★ |
| T2.2 CC 注册到 InteriorSubspaceManager | `InteriorSubspaceManager`, `InteriorPortalRegistry` | `CCAreaBounds`, `CCRefurbishedCache`, `AreaRestoreHandler` | ★★☆ |
| T2.3 修复过场状态机 | `BundleMenu` | `AreaRestoreCutscene`, `CutsceneManager`, `ScreenFade` | ★★★★ |
| T2.4 过场网络协议 | `PacketHandler` | `CutscenePayload` | ★★☆ |
| T2.5 星盘方块 | `ModBlocks`, `ModBlockEntities` | `StarPlaqueBlock` + BE + Renderer | ★★☆ |

### Tier 3 — 完整性 (锦上添花)

| 任务 | 修改文件 | 新建文件 | 复杂度 |
|------|---------|---------|--------|
| T3.1 星星放置动画 | — | `FetchStarGoal` 扩展 | ★★☆ |
| T3.2 全部完成告别演出 | — | `GoodbyeDanceCutscene` | ★★★ |
| T3.3 Junimo AI (跟随/逃跑) | `JunimoEntity` | `FollowPlayerGoal`, `FleeGoal` | ★★☆ |
| T3.4 遗漏奖励箱 | `CommunityCenterSavedData` | — | ★☆☆ |
| T3.5 Junimo 象形字体 (完整版) | — | 字体 JSON + 贴图 | ★★☆ |
| T3.6 镇上庆祝/Lewis 对话 | 视 NPC 系统 | — | ★★★ |

---

## 关键设计决策

### 决策 1: CC 建筑管理方式 → 复用 Interior Subspace ✅ 已决定

CC 作为室内建筑注册到 `InteriorSubspaceManager`，与 Pierre House、Saloon 等完全一致:
- 两个 `.schem` 文件 (ruins + refurbished)，**大小完全一致，同一原点**
- 远坐标区 (X>10000, Z>10000) 内放置
- Portal 进出 (室外 CC 门口 ↔ 室内)
- `LAYOUT_VERSION` 递增触发重新装载

### 决策 2: 区域修复方式 → 从 refurbished schem 局部覆盖 ✅ 已决定

- 两套 schem 共享原点和尺寸
- 用户提供每个区域的**绝对坐标范围** (min, max)
- 修复时: 从 refurbished 的方块缓存中取出该区域范围的方块，逐个 `setBlock()` 覆盖
- 不需要 7 个单独的区域 schem 文件，一个完整的 refurbished 即可

### 决策 3: 区域坐标输入方式 → 用户给绝对坐标，代码自动换算

用户在游戏内用 F3 看到的坐标就是绝对坐标，直接提供即可。
代码中如需相对偏移: `relative = absolute - CC_ORIGIN`

这样用户不需要做任何换算，也不需要知道 CC_ORIGIN 是多少。

### 决策 4: 演出系统用什么架构?

| 方案 | 优点 | 缺点 |
|------|------|------|
| **A. 服务端状态机 + 客户端 overlay** (推荐) | 多人同步，服务端权威 | 需要 packet 设计 |
| B. 纯客户端 | 简单 | 多人不同步 |

**推荐 A**: 服务端 `CutsceneManager` 控制阶段推进，通过 `CutscenePayload` 通知客户端执行视觉效果。

---

## 数据文件补充

### 需要新增的 JSON 数据

#### `junimo_quotes.json` — Junimo 名言池

```json
{
  "area_restore": [
    "stardewcraft.junimo.restore.1",
    "stardewcraft.junimo.restore.2",
    "stardewcraft.junimo.restore.3"
  ],
  "bundle_thanks": [
    "stardewcraft.junimo.thanks.1",
    "stardewcraft.junimo.thanks.2"
  ],
  "goodbye": [
    "stardewcraft.junimo.goodbye.1",
    "stardewcraft.junimo.goodbye.2",
    "stardewcraft.junimo.goodbye.3",
    "stardewcraft.junimo.goodbye.4",
    "stardewcraft.junimo.goodbye.5",
    "stardewcraft.junimo.goodbye.6"
  ]
}
```

#### 翻译键 (zh_cn.json 补充)

```json
{
  "stardewcraft.junimo.restore.1": "我们感谢你的慷慨...",
  "stardewcraft.junimo.restore.2": "森林精灵赐予这片土地新生...",
  "stardewcraft.junimo.restore.3": "这里的气息正在改变...",
  "stardewcraft.junimo.thanks.1": "谢谢你！",
  "stardewcraft.junimo.thanks.2": "你真好！",
  "stardewcraft.junimo.goodbye.1": "再见！",
  "stardewcraft.junimo.goodbye.2": "拜拜~",
  "stardewcraft.junimo.goodbye.3": "做得好！",
  "stardewcraft.junimo.goodbye.4": "我们永远感谢你！",
  "stardewcraft.junimo.goodbye.5": "这里又美起来了！",
  "stardewcraft.junimo.goodbye.6": "森林精灵会记住你的！",

  "stardewcraft.cc.complete": "祝尼魔们回到了自己的世界...但他们说会一直关注这里。",
  "stardewcraft.cc.wizard_letter": "亲爱的 %s，\n我在你访问的那个破旧建筑中感受到了来自森林的古老力量。\n来我的塔找我吧，我有办法帮你解读那些神秘文字。\n—— 拉斯莫迪乌斯",
  
  "stardewcraft.mail.cc_pantry": "社区中心的食品储藏室已经完全修复了！温室现已开放使用。",
  "stardewcraft.mail.cc_crafts_room": "社区中心的工艺室已修复！通往采石场的桥梁已经修好。",
  "stardewcraft.mail.cc_fish_tank": "社区中心的鱼缸区已修复！",
  "stardewcraft.mail.cc_boiler_room": "社区中心的锅炉房已修复！矿车系统已恢复运行。",
  "stardewcraft.mail.cc_vault": "社区中心的金库已修复！通往沙漠的巴士已恢复运营。",
  "stardewcraft.mail.cc_bulletin": "社区中心的公告栏区域已修复！你与镇上所有人的友谊都更进了一步。"
}
```

---

## 实现顺序建议

```
Week 1-2: T1.1 + T1.2 + T1.5
  故事标记 → 乱码/??? 显示 → 区域完成邮件通知
  (最小改动，立即可见效果)

Week 3-4: T1.4
  Junimo AI Goals + 搬运动画
  (Bundle 完成后有视觉反馈)

Week 5-6: T2.1 (美术并行)
  CC 3D 建筑设计 + 结构蓝图导出
  (这是阻塞项，可提前开始)

Week 7-8: T2.2 + T2.3 + T2.4
  结构替换系统 + 修复过场 + 网络协议
  (区域修复核心)

Week 9-10: T2.5 + T3.1
  星盘方块 + 星星放置动画

Week 11-12: T3.2 + T1.3
  告别舞蹈 + 巫师解锁完善

后续: T3.3-T3.6
  AI 细化、遗漏箱、象形字体、庆祝事件
```

---

## 风险与注意事项

1. **ruins/refurbished 对齐**: 两套 `.schem` 必须**尺寸完全一致**，区域边界不能有偏差。建议在同一坐标先放 ruins，再原地修改为 refurbished 后导出，确保 1:1 对齐
2. **局部覆盖原子性**: 一个区域可能涉及上千方块替换。应在服务端分帧执行，或使用 `setBlock(pos, state, 2)` (无更新通知) + 最后手动 `sendBlockUpdated()`，避免逐块通知导致卡顿
3. **多人同步**: 过场动画需要所有在场玩家同时看到。`CutscenePayload` 应广播给 CC 室内区域内的所有玩家
4. **Portal 冲突**: 确保 CC 的 portal target ID 不与其他建筑冲突（命名空间: `community_center_enter` / `community_center_exit`）
5. **AI 寻路**: CC 内部是封闭空间，确保 Junimo 的导航网格正确。可能需要设置 `canOpenDoors = false` + 确保房间间有通路
6. **GeckoLib 动画**: Junimo 需要 idle/walk/hold_walk/jump 动画。当前模型已有这些 `RawAnimation` 定义，确保 `.geo.json` 和 `.animation.json` 文件齐全
7. **LAYOUT_VERSION**: CC 注册后记得递增版本号，否则老存档不会装载新结构
8. **JunimoNote 方块恢复**: 如果 refurbished 里该位置不是 JunimoNoteBlock，修复后卷轴自然消失。但要确保 ruins 里该位置放置了正确 AREA 值的 JunimoNoteBlock
