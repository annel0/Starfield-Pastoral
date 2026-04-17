# 温室系统设计文档

## 概述

还原星露谷物语的温室系统。温室在农场初始化时以 **破损形态** 放置，玩家完成社区中心 **Pantry（食品室）** 全部献祭后，破损温室替换为 **修复形态**，并在入口生成传送交互实体，可进入温室内部种植作物。

温室内部 **无视季节限制**，作物全年可种可长。即使玩家不进入温室，洒水器照常工作、作物照常生长。

---

## SDV 原版机制回顾

| 要素 | SDV 实现 |
|---|---|
| 解锁条件 | `ccPantry` 邮件标志 (area 0 完成) |
| 门交互 | `GreenhouseBuilding.OnUseHumanDoor()` 检查标志，未解锁显示 "Farm_GreenhouseRuins" 对话 |
| 季节豁免 | `GameLocation.SeedsIgnoreSeasonsHere()` → 返回 `IsGreenhouse` 布尔 |
| 受影响系统 | Crop、HoeDirt、FruitTree、Tree、Bush 全部调用 `SeedsIgnoreSeasonsHere()` |
| 温室特性 | 作物不随季节死亡、土壤保持浇灌颜色、肥料不随换季清除 |

---

## 架构设计

### 文件资产（用户提供）

| 资产 | 说明 | 用途 |
|---|---|---|
| `greenhouse_broken.schem` | 温室外观 - 破损版 | 农场初始化时放置 |
| `greenhouse_repaired.schem` | 温室外观 - 修复版 | Pantry 献祭完成后替换 |
| `greenhouse_interior.schem` | 温室内部 | 注册到 InteriorSubspaceManager |

路径：`resources/data/stardewcraft/structures/greenhouse/`

### 坐标规划

#### 外观（农场维度）
- **放置位置**：用户提供 — 在 FarmInitializer 中的 `GREENHOUSE_ORIGIN` 常量
- 破损与修复 schem 必须 **同尺寸、同原点**，以确保原地替换

#### 内部（StarDew Valley 维度 - Interior 子空间）
- **Origin**：在 InteriorSubspaceManager 中注册新条目，使用下一个可用坐标段
- 当前最后注册的内部空间为 CC `(18816, 69, 18816)`
- **建议温室内部 Origin**: `(18816, 70, 19392)` — Z 方向 +576 (与其他内部间距一致)
- **Spawn offset**：根据 schem 确定入口位置后填入

---

## 模块设计

### 1. GreenhouseManager.java

**位置**：`com.stardew.craft.greenhouse.GreenhouseManager`

核心单例，负责温室生命周期管理。

```java
public class GreenhouseManager extends SavedData {
    // 温室状态
    public enum State { BROKEN, REPAIRED }
    
    private State state = State.BROKEN;
    private BlockPos farmOrigin;       // 农场维度放置坐标
    private int schemWidth, schemHeight, schemLength;  // schem 尺寸 (破损=修复=相同)
    
    // 状态查询
    public boolean isRepaired() { return state == State.REPAIRED; }
    
    // 修复温室（CC Pantry 完成时调用）
    public void repair(ServerLevel farmLevel) { ... }
    
    // SavedData 持久化
    public static GreenhouseManager get(ServerLevel level) { ... }
}
```

### 2. 农场初始化 — 放置破损温室

**修改文件**：`FarmInitializer.java`

在 `ensureInitialized()` 末尾新增：

```java
// 放置破损温室
private static final BlockPos GREENHOUSE_ORIGIN = new BlockPos(/*用户提供*/);

private static void placeGreenhouseRuins(ServerLevel level) {
    StructureLoader.loadAndPlace(level, 
        "data/stardewcraft/structures/greenhouse/greenhouse_broken.schem",
        GREENHOUSE_ORIGIN);
    
    GreenhouseManager mgr = GreenhouseManager.get(level);
    mgr.setFarmOrigin(GREENHOUSE_ORIGIN);
    mgr.setDirty();
}
```

### 3. CC Pantry 奖励 — 修复温室

**修改文件**：`AreaRestoreCutscene.java` 或 CC 奖励分发逻辑

当 `areaId == 0`（Pantry）完成时：

```java
case 0 -> {
    // 在农场维度执行温室替换
    ServerLevel farmLevel = server.overworld(); // 或 SDV 维度
    GreenhouseManager mgr = GreenhouseManager.get(farmLevel);
    mgr.repair(farmLevel);
}
```

**`repair()` 方法流程**：
1. 使用 StructureLoader 在 `farmOrigin` 处放置 `greenhouse_repaired.schem`（覆盖破损版）
2. 在修复温室入口位置生成传送交互实体（与 InteriorPortalRegistry 模式一致）
3. 更新状态为 `REPAIRED`
4. 播放修复音效 + 粒子效果

### 4. 内部空间注册

**修改文件**：`InteriorSubspaceManager.java`

新增温室内部注册：

```java
// 在 FIXED_STRUCTURES 列表中添加
register("greenhouse", 
    "data/stardewcraft/structures/greenhouse/greenhouse_interior.schem",
    18816, 70, 19392);

// SpawnOffset  根据 schem 内入口坐标确定
SPAWN_OFFSETS.put("greenhouse", new BlockPos(/*入口偏移*/));
```

### 5. 传送门注册

**修改文件**：`InteriorPortalRegistry.java`

```java
// 农场 → 温室内部（温室修复后在入口放置交互实体触发）
register("greenhouse_enter", 
    GREENHOUSE_INTERIOR_ORIGIN.getX() + spawnOffsetX + 0.5,
    GREENHOUSE_INTERIOR_ORIGIN.getY() + spawnOffsetY,
    GREENHOUSE_INTERIOR_ORIGIN.getZ() + spawnOffsetZ + 0.5,
    /*yaw*/, 0, PortalMode.ENTRANCE);

// 温室内部 → 农场（内部出口处放置交互实体）
register("greenhouse_exit",
    GREENHOUSE_ORIGIN.getX() + exitOffsetX + 0.5,
    GREENHOUSE_ORIGIN.getY() + exitOffsetY,
    GREENHOUSE_ORIGIN.getZ() + exitOffsetZ + 0.5,
    /*yaw*/, 0, PortalMode.ENTRANCE);
```

### 6. 季节豁免 — seedsIgnoreSeasonsHere

**修改文件**：项目初始化（如 `StardewCraft` 主类或 `GreenhouseManager` 静态初始化）

利用已有的 `SeasonLocationRules` 插件式注册：

```java
SeasonLocationRules.registerIgnoreSeasonsRule((level, pos) -> {
    // 检查 pos 是否在温室内部子空间范围内
    return GreenhouseRegion.isInGreenhouseInterior(level, pos);
});
```

**`GreenhouseRegion.isInGreenhouseInterior()`**：
```java
public static boolean isInGreenhouseInterior(Level level, BlockPos pos) {
    // 必须在 SDV 维度
    if (level.dimension() != StardewCraftDimensions.SDV_DIMENSION) return false;
    
    // 检查 pos 是否在温室内部 origin → origin + size 范围内
    BlockPos origin = GREENHOUSE_INTERIOR_ORIGIN;
    int x = pos.getX(), z = pos.getZ();
    return x >= origin.getX() && x < origin.getX() + WIDTH
        && z >= origin.getZ() && z < origin.getZ() + LENGTH
        && pos.getY() >= origin.getY() && pos.getY() < origin.getY() + HEIGHT;
}
```

### 7. 区块常加载 + 无玩家时作物/洒水器工作

**核心问题**：CropGrowthManager 和 SprinklerManager 的 `growDaily()` / `waterDaily()` 遍历所有注册的 GlobalPos，但调用 `level.getBlockState(pos)` 和 `level.setBlock()` 需要区块已加载。

**方案**：温室内部区块 **永久强制加载**（与现有 InteriorSubspaceManager 方式一致）

实现：
```java
// InteriorSubspaceManager.setInteriorChunksForced() 已有此机制
// 温室注册后自动包含在 FIXED_STRUCTURES 中
// → 日常维护的 chunk forcing 会覆盖温室区块
```

这意味着：
- SprinklerManager.waterDaily() 遍历到温室内的洒水器时，`level.isLoaded(pos)` 返回 true → 正常浇水
- CropGrowthManager.growDaily() 遍历到温室内的作物时，可正常读写方块状态 → 正常生长
- **无需额外代码**，现有系统天然支持

**注意**：当前 SprinklerManager 有 `if (!level.isLoaded(pos)) continue;` 检查。只要温室区块被强制加载，此条件自然通过。

### 8. 不可破坏性

温室外观方块（破损和修复版）应当不可被玩家破坏。

**方案 A — schem 使用已有的不可破坏方块**（如 barrier、bedrock 等已有方块）

**方案 B — 添加区域保护规则**：

```java
// 在 BlockBreak 事件中检查
@SubscribeEvent
public static void onBlockBreak(BlockEvent.BreakEvent event) {
    BlockPos pos = event.getPos();
    if (GreenhouseRegion.isInGreenhouseExterior(event.getLevel(), pos)) {
        event.setCanceled(true);
    }
}
```

**推荐方案 B**：更灵活，不依赖 schem 内方块类型。保护范围 = `GREENHOUSE_ORIGIN` → `GREENHOUSE_ORIGIN + schemSize`。

---

## 完整流程

```
游戏启动 / 维度首次加载
    │
    ├── FarmInitializer.ensureInitialized()
    │       └── placeGreenhouseRuins()  ← 放置破损温室外观
    │
    ├── InteriorSubspaceManager.ensureLoaded()
    │       └── 放置温室内部 schem（与其他内部一起）
    │       └── 强制加载温室内部区块
    │
    └── SeasonLocationRules.registerIgnoreSeasonsRule()
            └── 注册温室内部季节豁免规则

玩家完成 CC Pantry 献祭 (area 0)
    │
    ├── AreaRestoreCutscene (播放动画)
    │
    ├── GreenhouseManager.repair()
    │       ├── StructureLoader 覆盖放置 repaired schem
    │       └── 生成入口传送交互实体
    │
    └── InteriorPortalRegistry 已预注册好传送目标

每日更新 (NewDayEvent)
    │
    ├── SprinklerManager.waterDaily()
    │       └── 温室内洒水器正常浇水（区块常加载）
    │
    └── CropGrowthManager.growDaily()
            └── 温室内作物正常生长（区块常加载 + 季节豁免）
```

---

## 需要用户提供的信息

| 项目 | 说明 |
|---|---|
| 三个 schem 文件 | `greenhouse_broken.schem`、`greenhouse_repaired.schem`、`greenhouse_interior.schem` |
| 农场放置坐标 | `GREENHOUSE_ORIGIN` 在农场中的 (x, y, z) |
| 入口偏移 | 温室入口门在 schem 中的相对位置（用于传送实体放置） |
| 内部入口/出口偏移 | 温室内部 schem 中玩家进入点和退出交互点的位置 |

---

## 涉及文件清单

| 文件 | 操作 |
|---|---|
| `GreenhouseManager.java` | **新建** — 温室状态管理 + SavedData |
| `GreenhouseRegion.java` | **新建** — 坐标范围判定工具类 |
| `FarmInitializer.java` | **修改** — 添加破损温室放置 |
| `InteriorSubspaceManager.java` | **修改** — 注册温室内部 |
| `InteriorPortalRegistry.java` | **修改** — 注册温室传送门 |
| `SeasonLocationRules.java` | 无需修改（使用现有 API 注册规则） |
| `AreaRestoreCutscene.java` 或奖励分发 | **修改** — area 0 完成触发温室修复 |
| `StardewCraft.java` 或事件类 | **修改** — 注册季节豁免规则 + 方块保护事件 |
| `SprinklerManager.java` | 无需修改（天然支持） |
| `CropGrowthManager.java` | 无需修改（天然支持） |
