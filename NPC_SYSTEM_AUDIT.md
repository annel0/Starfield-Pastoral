# NPC 系统全面审计报告

> 审计日期: 2026-04-12  
> 审计范围: `com.stardew.craft.npc.*`, `com.stardew.craft.entity.npc.*` (21 个文件)  
> 分类: 🔴 严重 / 🟠 高 / 🟡 中 / 🟢 低

---

## 一、架构概览

```
NpcSystem.java                       ← 入口：服务器 tick 事件分发
├── NpcScheduleRuntimeService        ← 日程解析 (时间、天气、事件 → 目标位置)
├── NpcSpawnManager                  ← 实体生成与跟踪 (UUID → Entity 映射)
├── NpcCentralMovementService        ← 移动执行 (plan → A* → velocity)
│   ├── NpcRoutePlanner              ← 路线规划 (schedule target → 步骤序列)
│   ├── NpcPathfinder                ← A* 寻路引擎
│   └── NpcChunkForceManager         ← 强制加载路径区块
├── NpcInteractionService            ← 对话、礼物、好感度
├── NpcFriendshipDataManager         ← 好感度持久化 (SavedData)
└── NpcRuntimeDataManager            ← 运行时状态持久化 (SavedData)

NpcDataManager / NpcDataRegistry     ← 数据加载层 (JSON → 内存)
NpcLocationGraph                     ← 位置连接图 (BFS 路由回退)
```

**统计**: 32 个已实现 NPC, 28 个启用寻路, ~4500 行运行时代码

---

## 二、🔴 严重问题 (必须立即修复)

### BUG-01: COLLISION_PENALTIES HashMap 在异步 A* 中无线程安全保护

**文件**: `NpcPathfinder.java:61, 89, 564, 735`

`COLLISION_PENALTIES` 是一个普通 `HashMap`。主线程通过 `addCollisionPenalty()` (L89) 和 `cleanupExpiredPenalties()` (L79) 进行写入，而异步寻路线程通过 `edgePenaltyOnSnapshot()` (L735) 进行读取。

`HashMap` 在结构修改（put/remove）期间的并发读取是**未定义行为**——在旧版 JDK 上可导致死循环，在新版上可导致 `ConcurrentModificationException` 或返回垃圾数据。

**影响**: 随机崩溃或寻路结果错误  
**修复**: 改用 `ConcurrentHashMap`

---

### BUG-02: NpcDataRegistry 多步替换存在瞬态不一致窗口

**文件**: `NpcDataRegistry.java:23-30`

数据重载时，`capabilities`、`schedules`、`dialogues` 等字段分别用 volatile 赋值。在 `replaceCapabilities()` 执行后、`replaceSchedules()` 执行前的间隙，NPC 系统可能读到新的 capabilities 但旧的 schedules。

```java
// 线程A (reload): 
NpcDataRegistry.replaceCapabilities(newCaps);   // ← 生效
// ... 此时线程B读到新caps + 旧schedules ...
NpcDataRegistry.replaceSchedules(newSchedules);  // ← 还没生效
```

**影响**: 数据重载后 NPC 行为短暂异常（闪烁一帧后恢复）  
**修复**: 将所有数据打包为一个不可变对象，原子替换

---

### BUG-03: NpcLocationGraph.adjacency 竞态条件

**文件**: `NpcLocationGraph.java:48, 87`

`adjacency` 是普通静态字段。`reload()` 重建图后赋值，`findRoute()` 读取。无 volatile 或同步。

**影响**: 数据重载时路由查询可能读到半构建的图  
**修复**: 将 `adjacency` 声明为 `volatile`

---

### BUG-04: BlockSnapshot 对 200 格路径的内存开销过大

**文件**: `NpcPathfinder.java:851-876`

`BlockSnapshot.capture()` 为起点到目标的整个包围盒 +12 格 padding 拍摄快照。对 200 格路径：`(200+24) × 12 × (200+24) = 602,112` 个 BlockState 引用。

异步线程池有 2 个线程，同时运行 2 个长距离快照 = ~120 万对象引用 = ~10MB 堆内存。虽不至于 OOM，但对 GC 压力不小。

**影响**: 长距离异步寻路短时间产生大量对象  
**修复**: 限制快照区域为 64×12×64，超出范围返回 null（fallback 到同步 A*）

---

## 三、🟠 高优先级问题

### BUG-05: NpcSpawnManager 静态可变状态在服务器切换时可污染

**文件**: `NpcSpawnManager.java:51-59`

`TRACKED_NPC_UUIDS`、`TRACKED_MISS_COUNTS`、`GLOBAL_NPC_SCAN` 等全是静态字段。`ensureServerContext()` 会在检测到新服务器时清除，但如果从单人游戏退出到主菜单再进入另一个存档，UUID 映射可能残留。

**影响**: NPC 生成失败或跟踪错误  
**严重性**: 中等（实际上 `ensureServerContext` 会清除，但竞态窗口存在）

---

### BUG-06: NpcSpawnManager TRACKED_NPC_UUIDS 不清理已消失的 NPC

**文件**: `NpcSpawnManager.java:52-54`

`TRACKED_NPC_UUIDS` 映射只在 `getTrackedNpc()` 发现实体已移除时懒清除。如果某个 NPC 的 `getTrackedNpc()` 长时间不被调用（例如寻路被抑制的 NPC），其 UUID 条目永远保留。

**影响**: 长期运行的服务器内存缓慢泄漏（但量级很小）  
**修复**: 在 `tick()` 中定期校验 UUID 映射有效性

---

### BUG-07: NpcFriendshipDataManager 不清理离线玩家数据

**文件**: `NpcFriendshipDataManager.java:20`

`playerState` 映射按玩家 UUID 存储好感度状态。玩家断线后数据永久驻留内存。在多人服务器上，大量玩家进出会导致内存持续增长。

**影响**: 多人服务器内存泄漏  
**修复**: 在 SavedData 中只从 NBT 加载，不持久缓存；或定期清理

---

### BUG-08: InteractionService 中 StardewTimeManager.get() 未做 null 防护

**文件**: `NpcInteractionService.java:807`

`currentDayKey()` 直接调用 `StardewTimeManager.get()` 并在返回值上链式调用，如果 TimeManager 尚未初始化会 NPE。

**影响**: 极端时序下（世界加载早期）崩溃  
**修复**: 添加 null 检查

---

### PERF-01: NpcAnimationInspector 每次调用都重新读取 classpath JSON

**文件**: `NpcAnimationInspector.java:14-24`, `NpcDataManager.java:182`

`hasWalkAnimation()` 每次调用都执行 `getResourceAsStream()` → JSON 解析。数据加载时对每个 NPC 都调用一次（32 次）。

**影响**: 启动时不必要的 IO 开销  
**修复**: 结果缓存到静态 Map，重载时清除

---

### PERF-02: NpcContentFilter 每个品味条目做 3 次 Registry 查询

**文件**: `NpcContentFilter.java:60-95`

对每个品味物品 ID，依次尝试 `ResourceLocation.tryParse` + `BuiltInRegistries.ITEM.containsKey` 三个命名空间。30 NPC × 50+ 物品 = 4500+ 次注册表查询。

**影响**: 数据加载慢  
**修复**: 预构建 `Set<String>` 已知物品 ID 缓存

---

## 四、🟡 中等问题

### DESIGN-01: 移动系统 buildPlan() 的室内判定阈值

**文件**: `NpcCentralMovementService.java:186-188`

```java
if (indoorDist < 200.0D * 200.0D) { // = 40,000 平方距离
    // 视为同一室内
}
```

200 格直线距离对于"同一室内"来说太大了。当前室内空间通常在 50x50 以内。如果两个不同室内的直线距离恰好 < 200（例如坐标 12000 和 12150），会被误判为同一室内。

目前已知的室内空间间距都 > 200 格（种子店 ~12038 vs 酒吧 ~14210），所以**当前不会触发**。但这是一颗定时炸弹——如果后续添加相距 < 200 格的室内空间就会出问题。

**建议**: 改为基于 `InteriorSubspaceManager` 的区域 ID 判定（如果有），或将阈值降到 `80.0 * 80.0`

---

### DESIGN-02: NPC ID 规范化散落在多个类中

**文件**: `NpcSpawnManager.java:520+`, `NpcRoutePlanner.java:76+`, `NpcScheduleRuntimeService.java:272+`, `NpcLocationGraph.java:179+`

每个类都有自己的 `canonicalNpcId()` / `canonical()` 方法，实现相同（toLowerCase + trim）。

**建议**: 提取到共享 util 方法

---

### DESIGN-03: NpcInteractionService 中硬编码的店铺 NPC 检查

**文件**: `NpcInteractionService.java:60-83`

```java
if ("clint".equals(npcId)) { ... }
if ("harvey".equals(npcId)) { ... }
if ("gus".equals(npcId)) { ... }
```

**建议**: 改用 `NpcCapabilityProfile` 中的布尔标志或 Map 配置

---

### DESIGN-04: NpcScheduleRuntimeService.selectScheduleKey() 构建 50+ 候选键

**文件**: `NpcScheduleRuntimeService.java:139-223`

每次日程切换时构建大量字符串候选键逐一查找。虽然有缓存机制避免每 tick 重复，但字符串拼接开销可优化。

**影响**: 日程切换时短暂 CPU 峰值  
**建议**: 预构建候选键模板，复用 StringBuilder

---

### QUALITY-01: 魔法数字散落

| 数字 | 含义 | 出现位置 |
|------|------|----------|
| `250` | 每颗心的好感点数 | NpcInteractionService 多处 |
| `112` | 4季×28天 | NpcInteractionService L809 |
| `28` | 每季天数 | NpcInteractionService, NpcScheduleRuntimeService |
| `200` | A* 最大可步行距离 | NpcPathfinder, NpcCentralMovementService |

**建议**: 提取为具名常量

---

### QUALITY-02: NpcAnimationInspector InputStreamReader 未在 try-with-resources 中

**文件**: `NpcAnimationInspector.java:17-24`

```java
try (InputStream stream = ...) {
    JsonParser.parseReader(new InputStreamReader(stream, ...))  // ← 未托管
}
```

`InputStreamReader` 包装了 autoclose 的 `stream`，不会泄漏底层资源，但不够规范。

---

## 五、🟢 低优先级 / 改进建议

| 编号 | 建议 | 文件 |
|------|------|------|
| OPT-01 | `NpcSpawnManager.canonicalNpcId()` 添加结果缓存 | NpcSpawnManager.java:520 |
| OPT-02 | `NpcInteractionService.loadCurrentDialogue()` 中的多层心数变体循环，可预构建查找表 | NpcInteractionService.java:662 |
| OPT-03 | `NpcDataManager` 数据加载时的 `deepCopy()` 调用可延迟到实际需要修改时 | NpcDataManager.java:99 |
| OPT-04 | `NpcLocationGraph` 添加 JSON 边格式校验和缺失边警告 | NpcLocationGraph.java:66-79 |
| OPT-05 | `NpcDataDiagnostics` 增加深度校验：日程格式、对话完整性 | NpcDataDiagnostics.java:15-27 |
| OPT-06 | `NpcCapabilityProfile` record 构造器添加 age/manners/gender 合法值校验 | NpcCapabilityProfile.java |
| CLEAN-01 | `NpcPathfinder.canStandWide()` 恒返回 true，应移除或恢复实际逻辑 | NpcPathfinder.java:427 |
| CLEAN-02 | `NpcCentralMovementService.DEBUG_SNAPSHOTS` 无清理机制 | NpcCentralMovementService.java |

---

## 六、功能性缺失清单

| 功能 | 状态 | 说明 |
|------|------|------|
| 室内→不同室内路由 | ✅ 已修复 | buildPlan 现在检测跨室内并正确处理 |
| 室内→室外路由 (exit null fallback) | ✅ 已修复 | emergency warp fallback 已添加 |
| 室外→室内路由 | ✅ 正常工作 | WALK to door + WARP entry + WALK indoor |
| 室外→室外路由 | ✅ 正常工作 | 直接 A* |
| 多 NPC A* 预算分配 | ✅ 已修复 | 16 calls/tick, budget rejection 不计为失败 |
| NPC 对话系统 | ✅ 正常工作 | 心数变体、季节/天气/事件对话 |
| 礼物系统 | ✅ 正常工作 | 爱/喜/普/厌/恨 五级 |
| 好感度持久化 | ✅ 正常工作 | SavedData NBT |
| 日程系统 | ⚠️ 基本工作 | 复杂候选键机制可能有边缘遗漏 |
| 矿井维度 NPC (矮人) | ✅ 正常工作 | 独立 tick 路径 |
| NPC 生日事件 | ❓ 未验证 | 好感度中有生日标记，但未验证对话触发 |
| NPC 结婚系统 | ❓ 未在运行时代码中发现 | 可能尚未实现 |
| 店铺交互 | ✅ 正常工作 | 硬编码但功能完整 |
| NPC AI/flee/combat | 🔴 无 | NPC 是 PathfinderMob 但不使用原版 AI |
| 区块卸载后的 NPC 恢复 | ⚠️ 部分 | getTrackedNpc 懒清除，但 respawn 依赖 tick 周期 |

---

## 七、性能热点分析 (30 NPC 场景)

| 热点 | 每 tick 开销 | 风险 |
|------|------------|------|
| `NpcCentralMovementService.tick()` | O(30) × route resolve + plan execute | ✅ 可接受 |
| `NpcPathfinder.planPath()` (同步) | 最多 16 次/tick × 12K visits = 192K nodes | ⚠️ 尖峰较高 |
| `NpcPathfinder.planPathAsync()` | 2 线程 × 12K visits = 24K nodes (后台) | ✅ 可接受 |
| `NpcChunkForceManager` | 30 NPC × chunk line 计算 | ✅ 轻量 |
| `NpcSpawnManager.tick()` | 全实体扫描每 20 tick | ⚠️ 实体多时较重 |
| `NpcScheduleRuntimeService.tick()` | 缓存后 O(1)；日程切换时 O(50) 键构建 | ✅ 可接受 |
| `InteriorSubspaceManager.isInteriorRegion()` | 纯坐标比较 O(1) | ✅ 无问题 |
| `BlockSnapshot.capture()` | 长距离路径时 ~600K block 读取 | 🟠 需限制 |

**总体评估**: 正常运行时 CPU 开销可接受。尖峰出现在：(1) 日程切换时所有 NPC 同时重建路线，(2) 长距离 A* 同步调用。

---

## 八、修复优先级建议

### 第一批 (立即修复 — 影响稳定性)

1. **BUG-01**: `COLLISION_PENALTIES` → `ConcurrentHashMap` (1 行改动)
2. **BUG-03**: `NpcLocationGraph.adjacency` → `volatile` (1 行改动)
3. **CLEAN-01**: `canStandWide()` 恢复实际宽度检查或改为 cost-only (已有 `isNarrowCorridor` 函数)

### 第二批 (尽快修复 — 影响可维护性和边缘稳定性)

4. **BUG-02**: NpcDataRegistry 原子替换
5. **BUG-08**: InteractionService null 防护
6. **PERF-01**: NpcAnimationInspector 结果缓存
7. **DESIGN-01**: 室内距离阈值调整

### 第三批 (版本迭代中处理)

8. **BUG-06**: TRACKED_NPC_UUIDS 定期清理
9. **BUG-07**: FriendshipDataManager 离线玩家清理
10. **QUALITY-01**: 提取魔法数字为常量
11. **DESIGN-02**: 统一 NPC ID 规范化
12. **BUG-04**: BlockSnapshot 区域限制

---

## 九、已确认的误报清单

以下是审计中排除的非问题：

| 怀疑点 | 结论 |
|--------|------|
| NpcSystem.java L50 矿井维度设置 anyPlayerInStardew=true | ✅ 正确设计：矿井玩家在场时仍需 tick 星露谷 NPC |
| smoothPath 中 normalize 零向量导致 NaN | ✅ 非问题：Vec3.normalize() 对零向量返回 (0,0,0)，dot = 0 < 0.995 → 保留点 |
| 实体 despawn 后 movement 崩溃 | ✅ 非问题：getTrackedNpc() 检查 isRemoved() + isAlive() |
| NpcScheduleRuntimeService.invalidateCache() 从未调用 | ✅ 非问题：在 NpcSystem 和 CentralMovementService 中共 4 处调用 |
| BlockSnapshot 200MB 内存 | ✅ 夸大：实际约 ~5MB/快照 (602K refs × 8B)，2 线程 ~10MB |
