# 多人独立农场系统（QQ农场模式）总体规划

> **灵感**：类似 QQ 农场，玩家共享镇子/矿井/海滩等所有公共区域，但每个玩家拥有**自己的独立农场**。

---

## 一、现状分析

### 1.1 当前架构全景

```
┌─────────────────────────────────────────────────────────────────┐
│                    Stardew Valley 维度                           │
│  ┌──────────────────────────┐  ┌──────────────────────────────┐ │
│  │     主世界区域           │  │   室内亚空间区域              │ │
│  │  (X<10000, Z<10000)      │  │  (X: 10001-19000)            │ │
│  │                          │  │  (Z: 10001-19000)            │ │
│  │  农场  X: 36-311         │  │  19个建筑室内                │ │
│  │        Z: 37-154         │  │  含温室室内                  │ │
│  │  镇子  X: -537 ~ -85    │  │                              │ │
│  │  海滩  X: -464 ~ -175   │  │  Y: 70 (固定)               │ │
│  │  山区  X: -537 ~ -174   │  │                              │ │
│  │                          │  │                              │ │
│  │  出生点: (150, -12, 119) │  │                              │ │
│  └──────────────────────────┘  └──────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

### 1.2 关键绑死问题清单

| 类别 | 文件 | 硬编码内容 | 影响程度 |
|------|------|-----------|---------|
| **农场边界** | `FarmAreaHelper` | X: 36-311, Y: -18-103, Z: 37-154 | 🔴 核心 |
| **农场初始化** | `FarmInitializer` | 邮箱(141,-12,129), 出货箱(139,-12,135), 碎片区域 | 🔴 核心 |
| **出生点** | `PassOutService` / `DimensionEventHandler` | (150, -12, 119) | 🔴 核心 |
| **系统图腾柱** | `SystemTotemManager` | 农场柱(135,-12,136), 山区柱(-290,-14,256), 海滩柱(-189,-14,-142) | 🟡 中等 |
| **图腾传送** | `TeleportTotemItem` | 默认目标=系统柱坐标 | 🟡 中等 |
| **温室位置** | `GreenhouseManager` | 农场(232,-12,112), 室内(18816,70,19392) | 🟡 中等 |
| **睡觉限制** | `SleepInteractionHandler` | 仅农场区域内可睡觉 | 🟡 中等 |
| **建筑保护** | `FarmAreaProtectionEvents` | 仅农场区域内可建造 | 🟡 中等 |
| **出货箱** | `ShippingBinBlockEntity` | 全局 OvernightSettlementTracker | 🟡 中等 |
| **雨天浇水** | `FarmlandMoistureHandler` | 32格半径扫描 | 🟢 低 |
| **防踩踏** | `FarmlandEventHandler` | 维度级判断 | 🟢 低 |
| **地图加载** | `StardewValleyMapBootstrap` | 单一 main.schem 加载 | 🟢 低（公共区域不变）|

### 1.3 当前 SavedData 架构

#### 需要变为「每玩家独立」的数据：

| SavedData | 当前范围 | 说明 |
|-----------|---------|------|
| `CropGrowthManager` | 世界级 | 作物位置+生长阶段 → 每个农场独立 |
| `TreeGrowthManager` | 世界级 | 树苗+days_grown → 每个农场独立 |
| `WildTreeSeedManager` | 世界级 | 树种子标记 → 每个农场独立 |
| `AnimalWorldData` | 世界级 | 动物+建筑+干草 → 每个农场独立 |
| `AnimalGrowthManager` | 世界级 | 动物生产周期 → 每个农场独立 |
| `FertilizerManager` | 世界级 | 肥料映射 → 每个农场独立 |
| `SprinklerManager` | 世界级 | 喷头位置 → 每个农场独立 |
| `PastureGrassGrowthManager` | 世界级 | 牧草扩散 → 每个农场独立 |
| `WildLeavesPlacedManager` | 世界级 | 叶块标记 → 每个农场独立 |
| `FarmInitData` | 世界级 | 初始化标记 → 每个农场独立 |
| `GreenhouseManager` | 世界级 | 温室状态 → 每个农场独立 |
| `TotemPoleTracker` | 世界级 | 部分独立（农场柱归玩家，山区/海滩柱共享）|

#### 保持「全局共享」的数据：

| SavedData | 说明 |
|-----------|------|
| `StardewTimeManager` | 全局时间，所有玩家同步 |
| `WeatherSavedData` | 天气全局共享 |
| `InteriorSubspaceSavedData` | 室内结构（公共建筑）共享 |
| `MapSavedData` | 公共区域地图生成状态 |
| `PlayerDataManager` | 玩家属性本来就是 per-UUID |
| `NpcFriendshipDataManager` | per-UUID 好友度 |
| `ForageSpawnService` | 公共区域采集物 |
| `MuseumDonationData` | 博物馆捐赠（共享）|

### 1.4 区块加载现状

| 系统 | 区块数 | 条件 |
|------|--------|------|
| 主区域保活 | 169 (13×13) | 有玩家在室内时 |
| 室内结构 | ~49/结构 (7×7) | 有玩家在该室内时 |
| NPC目标 | 1/NPC | NPC有活跃路线 |
| NPC走廊 | 5-30/NPC | 路线<16区块 |
| 农场预加载 | ~80 | 首次进入 |
| **峰值估计** | **300+** | 现有单农场 |

---

## 二、核心设计方案

### 2.1 农场实例化模型：「同维度网格分配」

> **为什么不用独立维度？**  
> MC 每个维度有独立的 `ServerLevel`、区块IO线程、存档文件夹、`SavedData` 存储，创建维度的开销巨大且无法动态增减。上百个维度会导致 tick 循环、内存、文件描述符都爆炸。

**方案：在 Stardew Valley 维度中，利用远坐标区域(Z > 20000)为每个玩家分配独立农场实例。**

```
┌─────────────────── Stardew Valley 维度坐标布局 ───────────────────┐
│                                                                    │
│  Z < 10000    公共区域（镇子、海滩、山区、原农场区域保留但不使用）  │
│                                                                    │
│  Z: 10001-19000  室内亚空间（现有19个建筑室内）                    │
│                                                                    │
│  Z ≥ 20001   ████ 玩家农场实例区域 ████                            │
│              每个农场实例占 512×512 的网格槽位                      │
│              Player 0: (20001, *, 20001) 起点                      │
│              Player 1: (20513, *, 20001) 起点                      │
│              Player 2: (21025, *, 20001) 起点                      │
│              ...                                                   │
│              每行 N 个后换行: Z += 512                              │
│                                                                    │
│  实际农场区域: 276×118 (现有 X:36~311, Z:37~154)                   │
│  加上缓冲 → 每槽 512×512 足够容纳                                 │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

**网格分配算法**：

```java
public class FarmInstanceAllocator {
    static final int FARM_REGION_Z_START = 20001;
    static final int FARM_SLOT_SIZE = 512;      // 每个农场槽位的大小
    static final int FARMS_PER_ROW = 20;        // 每行放20个农场
    
    // slotIndex = 从 FarmInstanceRegistry 中分配的序号(0, 1, 2, ...)
    public static BlockPos getFarmOrigin(int slotIndex) {
        int col = slotIndex % FARMS_PER_ROW;
        int row = slotIndex / FARMS_PER_ROW;
        int x = FARM_REGION_Z_START + col * FARM_SLOT_SIZE;
        int z = FARM_REGION_Z_START + row * FARM_SLOT_SIZE;
        return new BlockPos(x, -12, z);  // Y 与现有农场一致
    }
}
```

### 2.2 农场实例注册表：`FarmInstanceRegistry`

```java
/**
 * 核心 SavedData：管理所有玩家的农场实例。
 * 存储在 Overworld 的 DataStorage 中，全局唯一。
 */
public class FarmInstanceRegistry extends SavedData {
    private static final String DATA_NAME = "stardew_farm_instances";
    
    // 玩家UUID → 农场实例信息
    private final Map<UUID, FarmInstance> instances = new HashMap<>();
    // 已分配的槽位索引，用于快速查找下一个空闲槽
    private int nextSlotIndex = 0;

    public record FarmInstance(
        UUID ownerUUID,
        String ownerName,          // 缓存的玩家名
        int slotIndex,             // 网格槽位序号
        BlockPos origin,           // 农场原点坐标
        String farmType,           // "standard" / "riverland" / "forest" 等
        boolean initialized,       // 是否已完成初始化（放置邮箱等）
        long createdTimestamp,     // 创建时间
        // 农场边界（相对于 origin 的偏移，与现有 FarmAreaHelper 对应）
        int farmMinX, int farmMaxX,
        int farmMinY, int farmMaxY,
        int farmMinZ, int farmMaxZ
    ) {}
    
    // --- 核心方法 ---
    public FarmInstance getOrCreateFarm(UUID playerUUID, String playerName, String farmType);
    public FarmInstance getFarm(UUID playerUUID);
    public boolean hasFarm(UUID playerUUID);
    public BlockPos getFarmSpawnPoint(UUID playerUUID);
    public boolean isInPlayerFarm(UUID playerUUID, BlockPos pos);
    public @Nullable UUID getOwnerAt(BlockPos pos); // 根据坐标反查属于哪个玩家
}
```

### 2.3 农场生命周期

```
玩家首次进入 Stardew Valley 维度
    │
    ▼
法师塔内对话 → 选择农场类型 (标准/河流/森林/...)
    │
    ▼
FarmInstanceRegistry.getOrCreateFarm(uuid, name, type)
    ├─ 分配 slotIndex
    ├─ 计算 origin = getFarmOrigin(slotIndex)
    ├─ 返回 FarmInstance
    │
    ▼
FarmInstanceInitializer.initializeFarm(level, farmInstance)
    ├─ 加载对应类型的 farm.schem 到 origin 位置
    ├─ 放置邮箱 (origin + 相对偏移)
    ├─ 放置出货箱 (origin + 相对偏移)
    ├─ 放置农场图腾柱 (origin + 相对偏移)
    ├─ 生成自然碎片（树/草/石头）
    ├─ 放置温室（破损版）
    ├─ 生成入口/出口传送交互实体
    └─ 标记 initialized = true
    │
    ▼
传送玩家到农场出生点 (origin + spawnOffset)
```

### 2.4 传送机制设计

#### 进入农场（3个入口）

玩家从公共区域进入自己农场的传送点，沿用现有的**交互实体传送**模式：

| 入口位置 | 公共区域坐标 | 对应农场内位置 | 说明 |
|----------|-------------|---------------|------|
| 农场南入口 | 镇子北边缘 | 农场南边缘 | 主入口（从镇子回家）|
| 农场西入口 | 森林/牧场方向 | 农场西边缘 | 从树林方向进入 |
| 农场北入口 | 山区/木匠方向 | 农场北边缘 | 从山区方向进入 |

#### 离开农场（3个出口）

| 出口位置 | 农场内坐标 | 公共区域目标 | 说明 |
|----------|-----------|-------------|------|
| 农场南出口 | 农场南边缘 | 镇子北入口 | 去镇子 |
| 农场西出口 | 农场西边缘 | 森林入口 | 去森林/牧场 |
| 农场北出口 | 农场北边缘 | 山区入口 | 去山区/木匠 |

#### 传送实体管理

```java
/**
 * 每个农场在初始化时，于公共区域和农场内创建交互实体。
 * 
 * 公共区域侧：所有玩家可见同一组交互实体（在农场入口位置）
 *            但右键时，服务端根据交互玩家的UUID查找其农场实例，
 *            传送到对应农场的入口位置。
 *
 * 农场内侧：每个农场独立的交互实体（在农场边缘）
 *           传送回固定的公共区域出口。
 */
```

**关键设计点**：公共区域的入口实体是**共享的**（不需要每个玩家一个），因为传送逻辑在服务端根据玩家UUID动态路由。

### 2.5 农场图腾柱方案

```
系统图腾柱（共享，不变）：
  山区柱: (-290, -14, 256) → 共享，所有人传送到同一位置
  海滩柱: (-189, -14, -142) → 共享，所有人传送到同一位置

农场图腾柱（每玩家独立）：
  玩家自己的农场柱: (origin + 相对偏移) → 存在于玩家自己的农场
  
TeleportTotemItem 修改：
  FARM 类型：默认传送到自己农场的农场柱位置
  MOUNTAIN/BEACH 类型：不变
```

---

## 三、需要修改的核心系统

### 3.1 `FarmAreaHelper` → `FarmAreaResolver`

**现状**：静态常量定义唯一农场边界  
**改造**：根据坐标或玩家UUID动态查询农场边界

```java
public class FarmAreaResolver {
    // 快速判断：坐标是否在任何玩家的农场区域内
    public static boolean isInAnyFarm(Level level, BlockPos pos) {
        if (pos.getZ() < FarmInstanceAllocator.FARM_REGION_Z_START) return false;
        // 通过网格数学快速定位 slotIndex，再验证边界
        return getOwnerAt(level, pos) != null;
    }
    
    // 判断坐标是否在某个特定玩家的农场内
    public static boolean isInPlayerFarm(Level level, UUID playerUUID, BlockPos pos) {
        FarmInstance farm = FarmInstanceRegistry.get(level).getFarm(playerUUID);
        return farm != null && farm.contains(pos);
    }
    
    // 坐标反查归属玩家（O(1) 网格计算，不需要遍历所有农场）
    public static @Nullable UUID getOwnerAt(Level level, BlockPos pos) {
        int slotIndex = FarmInstanceAllocator.getSlotIndexAt(pos);
        if (slotIndex < 0) return null;
        return FarmInstanceRegistry.get(level).getOwnerBySlot(slotIndex);
    }
    
    // 保留旧农场区域判断（用于公共区域的旧农场地块，可作为装饰/入口区域）
    public static boolean isInLegacyFarmArea(BlockPos pos) { ... }
}
```

### 3.2 SavedData 改造策略

**方案A：每个 Manager 内部增加 per-UUID 分层**（推荐）

```java
public class CropGrowthManager extends SavedData {
    // 改前: Map<GlobalPos, CropState> crops;
    // 改后: 
    private final Map<UUID, Map<GlobalPos, CropState>> cropsByOwner = new HashMap<>();
    
    // 公共区域的作物（温室等可选共享型）
    private final Map<GlobalPos, CropState> sharedCrops = new HashMap<>();
    
    public void addCrop(UUID farmOwner, GlobalPos pos, CropState state) { ... }
    public void growDaily(ServerLevel level) {
        // 只处理在线玩家的农场 + 已加载区块内的作物
        for (UUID owner : getOnlineFarmOwners(level)) {
            growPlayerFarm(level, owner);
        }
    }
}
```

**方案B：每个农场实例对应一个独立 SavedData 实例**

```java
// 独立命名的 SavedData
String dataName = "stardew_farm_crops_" + playerUUID;
CropGrowthManager.get(level, playerUUID);
```

**推荐方案A**，原因：
- 避免爆炸式增长的 SavedData 实例（100玩家 × 10个Manager = 1000个文件）
- 更容易做跨农场查询（如统计排行榜）
- 日滚转时可以批量处理

### 3.3 事件处理器修改清单

| 事件处理器 | 修改内容 |
|-----------|---------|
| `FarmAreaProtectionEvents` | `isInFarmArea()` → `isInPlayerFarm(player.getUUID(), pos)` |
| `FarmlandEventHandler` | 维度级判断不变，但防践踏可扩展到农场区域 |
| `FarmlandMoistureHandler` | 扫描范围限定在玩家所在农场 |
| `SleepInteractionHandler` | 仅允许在自己农场内睡觉 |
| `DimensionEventHandler` | 进入维度时不再硬编码传送到 (150,-12,119)，而是传送到玩家自己的农场 |
| `PassOutService` | 昏倒后传送到自己农场的出生点 |
| `InteriorPortalInteractionEvents` | 出入门逻辑不变，但进入农场的门需要路由到正确实例 |

### 3.4 图腾与传送系统修改

```java
// TeleportTotemItem 修改
case FARM:
    // 改前: 硬编码 (135, -12, 136)
    // 改后:
    FarmInstance farm = FarmInstanceRegistry.get(level).getFarm(player.getUUID());
    if (farm != null) {
        destination = farm.getTotemPolePos();
    }
    break;
case MOUNTAIN:
case BEACH:
    // 不变，仍传送到公共区域
    break;
```

### 3.5 温室系统修改

每个农场有独立的温室实例：

```java
public class GreenhouseManager extends SavedData {
    // 改前: 单一温室
    // 改后: 每玩家温室
    private final Map<UUID, GreenhouseState> greenhouses = new HashMap<>();
    
    // 温室室内也需要独立的亚空间坐标
    // 方案：在 Z > 19000 的室内区域为每个玩家分配温室内部空间
}
```

---

## 四、性能设计（最关键）

### 4.1 核心原则：「谁在场，谁加载」

```
玩家在自己农场     → 仅加载该农场的区块
玩家在公共区域     → 卸载该农场的区块（延迟卸载，30秒缓冲）
玩家在室内亚空间   → 视情况保持农场加载（作物/动物需要 tick）
玩家离线          → 完全卸载，下次上线时补算生长
```

### 4.2 区块加载策略

```java
public class FarmChunkManager {
    // 每个农场的区块状态
    enum FarmChunkState {
        UNLOADED,       // 完全卸载
        FORCE_LOADED,   // 强制加载（玩家在场）
        PENDING_UNLOAD  // 延迟卸载中（玩家刚离开）
    }
    
    // 当玩家进入农场
    public void onPlayerEnterFarm(ServerPlayer player, FarmInstance farm) {
        cancelPendingUnload(farm);
        forceLoadFarmChunks(farm);
    }
    
    // 当玩家离开农场
    public void onPlayerLeaveFarm(ServerPlayer player, FarmInstance farm) {
        // 检查是否还有其他玩家在此农场（未来访客功能）
        if (noPlayersInFarm(farm)) {
            schedulePendingUnload(farm, 30_SECONDS);
        }
    }
    
    // 延迟卸载：30秒后如果还没人回来，真正释放
    private void schedulePendingUnload(FarmInstance farm, long delay) {
        pendingUnloads.put(farm.slotIndex(), System.currentTimeMillis() + delay);
    }
    
    // 强制加载 — 只加载农场实际使用的区块，非整个 512×512 槽位
    private void forceLoadFarmChunks(FarmInstance farm) {
        // 农场实际区域: 约 276×118 blocks = 18×8 = 144 个区块
        // 但实际需要加载的只有有活动的区域
        // 策略：仅加载玩家周围 + 喷头/动物覆盖的区块
    }
}
```

### 4.3 离线补算系统（关键性能优化）

```java
/**
 * 玩家上线时，不需要回放每一天。
 * 直接根据离线天数批量计算最终状态。
 */
public class OfflineFarmCatchUp {
    
    public void catchUp(ServerLevel level, UUID playerUUID, int daysMissed) {
        FarmInstance farm = registry.getFarm(playerUUID);
        
        // 1. 作物：直接推进 N 天的生长阶段
        cropManager.batchGrow(playerUUID, daysMissed);
        
        // 2. 树木：days_grown += daysMissed，检查是否达到成熟
        treeManager.batchGrow(playerUUID, daysMissed);
        
        // 3. 动物：累计幸福度衰减 + 产出计算
        animalManager.batchProcess(playerUUID, daysMissed);
        
        // 4. 牧草：概率性扩散计算(不需要精确模拟每一天)
        grassManager.batchSpread(playerUUID, daysMissed);
        
        // 5. 喷头：如果区块未加载则标记"最后浇水日"
        sprinklerManager.markWateredUpTo(playerUUID, currentDay);
        
        // 6. 季节变更：如果跨季了，处理作物枯萎/清除
        if (crossedSeason(daysMissed)) {
            handleSeasonTransition(playerUUID);
        }
    }
}
```

### 4.4 区块预算控制

```java
/**
 * 服务器全局区块预算监控。
 * 硬上限：确保所有农场总计不超过 N 个强制加载区块。
 */
public class ChunkBudgetGuard {
    static final int MAX_FORCED_FARM_CHUNKS = 800;  // 硬上限
    static final int CHUNKS_PER_FARM = 144;          // 每个满载农场
    static final int REDUCED_CHUNKS_PER_FARM = 48;   // 缩减模式（仅核心区域）
    
    public boolean canFullLoad(FarmInstance farm) {
        int currentTotal = countAllForcedFarmChunks();
        return (currentTotal + CHUNKS_PER_FARM) <= MAX_FORCED_FARM_CHUNKS;
    }
    
    // 当超预算时，降级为缩减模式
    public void degradeToReduced(FarmInstance farm) {
        unloadNonEssentialChunks(farm);
        // 仅保留：出生点周围 + 动物建筑覆盖区域
    }
}
```

### 4.5 农场 Schematic 异步加载

```java
/**
 * 农场创建时的 schematic 加载使用异步预算模式。
 * 借鉴现有 StardewValleyMapBootstrap 的 PlacementJob 机制。
 */
public class FarmPlacementJob {
    static final int BLOCKS_PER_TICK = 60_000;    // 比公共区域少一半预算
    static final long TIME_BUDGET_NS = 5_000_000L; // 5ms/tick（不能占太多服务器时间）
    
    // 玩家创建农场时排入队列
    private static final Queue<FarmPlacementJob> pendingJobs = new ConcurrentLinkedQueue<>();
    
    // 每 tick 处理一个活跃 job
    public static void tickGlobal(ServerLevel level) {
        FarmPlacementJob current = pendingJobs.peek();
        if (current != null && current.tick(level)) {
            pendingJobs.poll(); // 完成了
        }
    }
}
```

---

## 五、数据模型变更细节

### 5.1 `FarmInstanceRegistry` NBT 结构

```nbt
{
    "nextSlotIndex": 42,
    "instances": {
        "<uuid-string>": {
            "ownerName": "PlayerName",
            "slotIndex": 0,
            "originX": 20001,
            "originY": -12,
            "originZ": 20001,
            "farmType": "standard",
            "initialized": true,
            "createdTimestamp": 1718000000000,
            "lastOnlineDay": 28,        // 最后在线时的游戏天数
            "lastOnlineSeason": 2,      // 最后在线时的季节
            "farmBounds": {
                "minX": 20037, "maxX": 20312,
                "minY": -18,  "maxY": 103,
                "minZ": 20038, "maxZ": 20155
            }
        }
    }
}
```

### 5.2 各 Manager 的 per-UUID 适配模式

以 `CropGrowthManager` 为例：

```java
// === 改前 ===
private final Map<GlobalPos, CropData> crops = new HashMap<>();

public void addCrop(ServerLevel level, BlockPos pos, ...) {
    GlobalPos gp = GlobalPos.of(level.dimension(), pos);
    crops.put(gp, new CropData(...));
    setDirty();
}

public void growDaily(ServerLevel level) {
    for (Map.Entry<GlobalPos, CropData> entry : crops.entrySet()) {
        // 处理所有作物
    }
}

// === 改后 ===
private final Map<UUID, Map<GlobalPos, CropData>> cropsByOwner = new HashMap<>();
private final Map<GlobalPos, CropData> sharedCrops = new HashMap<>(); // 公共区域(温室等)

public void addCrop(ServerLevel level, BlockPos pos, @Nullable UUID farmOwner, ...) {
    GlobalPos gp = GlobalPos.of(level.dimension(), pos);
    if (farmOwner != null) {
        cropsByOwner.computeIfAbsent(farmOwner, k -> new HashMap<>()).put(gp, new CropData(...));
    } else {
        sharedCrops.put(gp, new CropData(...));
    }
    setDirty();
}

public void growDaily(ServerLevel level) {
    // 1. 共享作物（公共温室等）始终处理
    growCrops(level, sharedCrops);
    
    // 2. 在线玩家的农场作物
    for (ServerPlayer player : getOnlineStardewPlayers(level)) {
        UUID uuid = player.getUUID();
        Map<GlobalPos, CropData> playerCrops = cropsByOwner.get(uuid);
        if (playerCrops != null) {
            growCrops(level, playerCrops);
        }
    }
    
    // 3. 离线玩家的作物在他们上线时通过 OfflineFarmCatchUp 处理
}
```

### 5.3 NBT 序列化变更

```java
@Override
public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
    // 共享作物
    tag.put("sharedCrops", serializeCrops(sharedCrops));
    
    // 按玩家分组
    CompoundTag ownersTag = new CompoundTag();
    for (Map.Entry<UUID, Map<GlobalPos, CropData>> entry : cropsByOwner.entrySet()) {
        ownersTag.put(entry.getKey().toString(), serializeCrops(entry.getValue()));
    }
    tag.put("cropsByOwner", ownersTag);
    return tag;
}
```

---

## 六、公共区域入口设计

### 6.1 入口交互实体（公共区域侧）

在公共区域的 3 个固定位置放置交互实体，所有玩家共用同一组实体：

```java
public class FarmPortalManager {
    // 公共区域的 3 个农场入口（固定位置，靠近原来的镇子/森林/山区出口）
    static final PortalDef[] PUBLIC_ENTRIES = {
        // 从镇子去农场（南入口）
        new PortalDef("farm_entry_south", new BlockPos(-100, -17, 37), Direction.NORTH),
        // 从森林去农场（西入口）  
        new PortalDef("farm_entry_west",  new BlockPos(36, -14, 80),   Direction.EAST),
        // 从山区去农场（北入口）
        new PortalDef("farm_entry_north", new BlockPos(100, -12, 215), Direction.SOUTH),
    };
    
    /**
     * 玩家交互入口实体时：
     * 1. 查询 FarmInstanceRegistry 获取该玩家的农场
     * 2. 如果还没有农场 → 打开农场选择界面
     * 3. 如果有农场 → 传送到 farm.origin + 对应入口偏移
     */
    public void onPortalInteract(ServerPlayer player, String portalId) {
        FarmInstance farm = FarmInstanceRegistry.get(level).getFarm(player.getUUID());
        if (farm == null) {
            // 开启"选择农场类型"GUI
            openFarmSelectionScreen(player);
            return;
        }
        
        BlockPos target = switch (portalId) {
            case "farm_entry_south" -> farm.origin().offset(SOUTH_ENTRY_OFFSET);
            case "farm_entry_west"  -> farm.origin().offset(WEST_ENTRY_OFFSET);
            case "farm_entry_north" -> farm.origin().offset(NORTH_ENTRY_OFFSET);
            default -> farm.getSpawnPoint();
        };
        
        // 触发区块加载 → 传送
        FarmChunkManager.onPlayerEnterFarm(player, farm);
        player.teleportTo(level, target.getX() + 0.5, target.getY(), target.getZ() + 0.5, yaw, 0);
    }
}
```

### 6.2 出口交互实体（农场内侧）

每个农场初始化时在其边缘创建 3 个出口实体，传送回固定的公共区域位置：

```java
// 农场初始化时
FarmInstanceInitializer.spawnExitPortals(level, farm) {
    // 南出口 → 镇子
    spawnInteraction(farm.origin.offset(SOUTH_EXIT_OFFSET),
                     "sdv_portal_target:farm_exit_south",
                     TARGET_TOWN_POS);
    // 西出口 → 森林
    spawnInteraction(farm.origin.offset(WEST_EXIT_OFFSET),
                     "sdv_portal_target:farm_exit_west",
                     TARGET_FOREST_POS);
    // 北出口 → 山区
    spawnInteraction(farm.origin.offset(NORTH_EXIT_OFFSET),
                     "sdv_portal_target:farm_exit_north",
                     TARGET_MOUNTAIN_POS);
}
```

---

## 七、分阶段实施计划

### Phase 0: 基础设施（预估改动量：中）

1. **`FarmInstanceRegistry`** — 新建 SavedData，管理玩家→农场实例映射
2. **`FarmInstanceAllocator`** — 网格坐标分配算法
3. **`FarmAreaResolver`** — 替代 `FarmAreaHelper`，支持动态农场查询
4. **`FarmChunkManager`** — 农场区块加载/卸载生命周期管理

### Phase 1: 农场创建与传送（改动量：大）

5. **`FarmInstanceInitializer`** — 替代 `FarmInitializer`，per-farm 版本
   - Schematic 加载（异步）
   - 邮箱/出货箱/图腾柱放置（相对坐标）
   - 碎片生成（相对坐标）
6. **入口/出口传送实体** — 公共区域入口 + 农场内出口
7. **`DimensionEventHandler` 修改** — 进入维度时路由到自己的农场
8. **农场选择界面** — 法师对话后的农场类型选择 GUI

### Phase 2: SavedData 迁移（改动量：最大，最核心）

9. **`CropGrowthManager`** — 改为 per-UUID 子映射
10. **`TreeGrowthManager`** — 同上
11. **`WildTreeSeedManager`** — 同上
12. **`AnimalWorldData`** — 同上（已有 `hayByOwner`，扩展到全部）
13. **`AnimalGrowthManager`** — 同上
14. **`FertilizerManager`** — 同上
15. **`SprinklerManager`** — 同上
16. **`PastureGrassGrowthManager`** — 同上
17. **`WildLeavesPlacedManager`** — 同上
18. **`GreenhouseManager`** — 每玩家温室实例 + 室内空间分配

### Phase 3: 事件处理器适配（改动量：中）

19. **`FarmAreaProtectionEvents`** — 使用 `FarmAreaResolver`
20. **`SleepInteractionHandler`** — 限制在自己农场内睡觉
21. **`FarmlandMoistureHandler`** — 限定扫描在当前农场
22. **`PassOutService`** — 传送到自己农场出生点
23. **`FarmlandEventHandler`** — 扩展判断逻辑

### Phase 4: 图腾与特殊系统（改动量：中）

24. **`SystemTotemManager`** — 每个农场放置自己的农场柱；山区/海滩柱不变
25. **`TeleportTotemItem`** — FARM 类型传送到自己农场
26. **`TotemPoleTracker`** — 农场柱 per-UUID 注册

### Phase 5: 离线补算与性能优化（改动量：中）

27. **`OfflineFarmCatchUp`** — 上线时批量推进生长/产出
28. **`ChunkBudgetGuard`** — 全局区块预算监控
29. **`FarmPlacementJob`** — 异步农场创建
30. **日更新优化** — 仅 tick 在线玩家的农场数据

### Phase 6: 未来扩展（可选，不在首版）

31. **访客系统** — 允许拜访其他玩家的农场
32. **权限系统** — 农场主授权他人操作
33. **农场排行榜** — 收入/收藏品/好友度排名
34. **多农场类型** — 河流/森林/荒野等不同 schematic

---

## 八、风险评估与对策

### 8.1 性能风险

| 风险 | 严重度 | 对策 |
|------|--------|------|
| 100+玩家同时在线，区块爆炸 | 🔴 高 | ChunkBudgetGuard 硬上限 + 降级模式 |
| 农场创建阻塞服务器 | 🔴 高 | 异步 PlacementJob，5ms/tick 预算 |
| 日更新遍历所有玩家农场 | 🟡 中 | 仅处理在线玩家 + 离线补算 |
| SavedData 单文件过大 | 🟡 中 | 监控文件大小，必要时拆分 |
| 离线补算首次上线卡顿 | 🟡 中 | 分帧处理，动画过渡 |

### 8.2 兼容性风险

| 风险 | 对策 |
|------|------|
| 旧存档升级 | 检测旧 FarmInitData，将原农场转为第一个玩家的农场实例（slotIndex=0 放在旧坐标） |
| SavedData 格式变更 | 版本号标记，load() 中做迁移逻辑 |
| 公共区域原农场冲突 | 旧农场区域改为装饰性公共空间或入口广场 |

### 8.3 设计风险

| 风险 | 对策 |
|------|------|
| 温室室内空间不够分配 | 室内区域 X/Z 范围扩展到 30000+ |
| 农场类型 schematic 尺寸不统一 | 所有农场类型统一最大尺寸，槽位按最大的算 |
| 时间同步问题（睡觉投票） | 维持全局时间，所有在线玩家都需要投票 |

---

## 九、不需要修改的系统

以下系统**天然兼容**多人农场，无需修改：

| 系统 | 原因 |
|------|------|
| `PlayerDataManager` / `PlayerStardewData` | 已经是 per-UUID 设计 |
| `NpcFriendshipDataManager` | per-UUID × per-NPC |
| `InteriorSubspaceManager`（公共建筑部分） | 公共建筑共享，不受农场影响 |
| `StardewTimeManager` | 全局时间不变 |
| `WeatherSavedData` | 全局天气不变 |
| `PacketHandler` | 网络框架不变 |
| `ForageSpawnService` | 公共区域采集物不变 |
| `MiningDataManager` | 矿井系统不变 |
| 所有 NPC 系统 | 公共区域 NPC 不变 |
| 商店/烹饪/任务系统 | 纯交互逻辑，不涉及农场坐标 |

---

## 十、技术决策记录

| 决策 | 选项 | 结论 | 原因 |
|------|------|------|------|
| 农场位置方案 | A. 独立维度<br>B. 同维度网格<br>C. 小型服务器世界 | **B** | 维度开销太大；同维度网格可 O(1) 查找 |
| SavedData 分离方案 | A. Manager 内 per-UUID Map<br>B. 独立 SavedData 文件 | **A** | 减少文件数量，方便批量操作 |
| 区块加载策略 | A. 永久加载所有农场<br>B. 按玩家位置加载<br>C. 混合延迟卸载 | **C** | 平衡性能和即时性 |
| 离线处理策略 | A. 忽略离线<br>B. 后台 tick<br>C. 上线补算 | **C** | 后台 tick 浪费资源 |
| 公共入口方案 | A. 每玩家独立入口实体<br>B. 共享入口+服务端路由 | **B** | 实体数量可控 |
| 农场类型 schematic | A. 启动时全部加载<br>B. 按需加载+缓存 | **B** | 节省内存 |

---

## 十一、文件影响汇总

### 新增文件（约10个核心类）

```
farm/instance/FarmInstanceRegistry.java      — 核心注册表 SavedData
farm/instance/FarmInstanceAllocator.java      — 网格分配算法
farm/instance/FarmInstance.java               — 农场实例数据记录
farm/instance/FarmInstanceInitializer.java   — per-farm 初始化
farm/instance/FarmChunkManager.java          — 区块生命周期管理
farm/instance/FarmPortalManager.java         — 入口/出口传送管理
farm/instance/OfflineFarmCatchUp.java        — 离线补算
farm/instance/ChunkBudgetGuard.java          — 区块预算监控
farm/instance/FarmSelectionScreen.java       — 农场选择 GUI (客户端)
farm/instance/FarmSelectionPayload.java      — 农场选择网络包
```

### 需要修改的现有文件（约25个）

```
核心:
  core/FarmAreaHelper.java        → 重构为 FarmAreaResolver
  dimension/FarmInitializer.java  → 抽取通用逻辑到 FarmInstanceInitializer

事件:
  event/FarmAreaProtectionEvents.java
  event/FarmlandEventHandler.java
  event/FarmlandMoistureHandler.java
  event/SleepInteractionHandler.java
  event/DimensionEventHandler.java

管理器 (SavedData 分层):
  manager/CropGrowthManager.java
  manager/TreeGrowthManager.java
  manager/WildTreeSeedManager.java
  manager/PastureGrassGrowthManager.java
  manager/FertilizerManager.java
  manager/SprinklerManager.java
  manager/WildLeavesPlacedManager.java
  manager/AnimalGrowthManager.java

动物:
  animal/AnimalWorldData.java

温室:
  greenhouse/GreenhouseManager.java

图腾:
  totem/SystemTotemManager.java
  totem/TotemPoleTracker.java
  item/totem/TeleportTotemItem.java

玩家:
  player/PassOutService.java

传送:
  interior/CrossDimensionTeleporter.java
```

### 不受影响的文件（约1000+个）

所有客户端渲染、物品/方块定义、NPC系统、战斗系统、钓鱼系统、烹饪系统、商店系统、音效系统等。

---

## 十二、坐标计算参考

### 现有农场的关键偏移量（需转为相对坐标）

```
设原农场 origin = (103, -12, 37)  （实际生成区域左上角）

特征点相对偏移 (相对于 origin):
  出生点:     (+47,  0,  +82)   = (150, -12, 119)
  邮箱:       (+38,  0,  +92)   = (141, -12, 129)
  出货箱:     (+36,  0,  +98)   = (139, -12, 135)
  农场图腾柱: (+32,  0, +99)    = (135, -12, 136)
  温室:       (+129, 0, +75)    = (232, -12, 112)
  
新农场: 将上述偏移加到 FarmInstance.origin 即可
```

---

*文档版本: v1.0*  
*最后更新: 2026-04-15*  
*状态: 设计阶段 — 等待 review 后开始实施*
