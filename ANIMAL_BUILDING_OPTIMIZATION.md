# 动物与建筑系统优化清单

> 对照星露谷源码 `FarmAnimal.cs` / `AnimalHouse.cs` / `Building.cs` 逐项对标。  
> 女巫事件 / 夜间袭击不做。

---

## 一、门口卡住问题（最高优先级）

### 问题表现
动物进出建筑时大量聚集在门口位置，无法正常通过。虽然有 stuck 超时 → 强制传送兜底，但表现很差：  
- 动物会在门口反复抖动 3-4 秒才被传送  
- 多只动物同时进出时互相碰撞，全部触发 forceMoveInside/Outside  
- 视觉上不自然，和 SDV 的平滑进出完全不同

### 根因分析

#### 1. 所有动物共用同一个 DoorTarget
`resolveNearestDoorTarget()` 对所有动物返回相同的 `insidePos` / `outsidePos`（距门最近的可站立点）。多只动物导航到同一个 BlockPos 时互相碰撞，全部卡住。

**修复**：为每只动物随机偏移目标点。在 `findProjectedDoorSideTarget()` 成功后，加入 ±1-2 格随机偏移（保证 `canOccupy`）。或者维护一个已被占用目标位列表，后来者自动选下一个可用位。

#### 2. 导航目标是门对面的方块而非"穿过门"
当前目标 `insidePos` 通常是门的紧贴内侧（距门 1 格）。MC 的导航系统会让实体走到目标方块中心然后停下 — 但实体碰撞箱可能还在门上，导致下一 tick 再次路径规划 → 抖动。

**修复**：将 `findProjectedDoorSideTarget()` 的 `step` 起始值从 2 改为 3，让目标点离门更远（至少 3 格），使实体能完全穿过门框再停下。

#### 3. forceMoveInsideHome/Outside 是瞬移而非滑动
SDV 的动物到门口后有自然的走入动画。当前实现卡住后直接 `moveTo()` 瞬移，非常突兀。

**修复**：在 stuck 超时之前增加一个"推入"阶段 — 当动物距门 < 2 格但卡住时，直接沿门的方向做一次小幅 `setDeltaMovement()` 推力，帮助它穿过碰撞体。只有彻底失败才触发瞬移。

#### 4. 门方块本身可能有碰撞体
Minecraft 的 Door 方块即使处于 OPEN 状态，某些半格依然有碰撞（取决于门朝向）。动物碰撞箱宽度 > 0.5 时可能卡在开着的门上。

**修复**：检查动物经过门时的碰撞逻辑。可考虑在门开启时临时给经过的动物实体添加 `noPhysics` 标记或使用 `TemporaryPassableTiles`（SDV 就是这么做的：`isTileOccupiedByFarmer → TemporaryPassableTiles.Add(GetBoundingBox())`）。

#### 5. 缺少交错出门机制
SDV 的动物不会同时冲向门口。它们有随机延迟 — 每只动物在早上有不同的"准备出门"时间。

**修复**：在 `LeaveHomeInDayGoal.canUse()` 中加入基于 `animalId` 的随机延迟。例如 `if (currentMinutes < MINUTES_0600 + (managedAnimalId % 8) * 10) return false;`，让动物错开 0-70 分钟出门。同理 `ReturnHomeAtEveningGoal` 也错开。

---

## 二、开心度公式差异

### 问题
对照 `FarmAnimal.cs` 的 `dayUpdate()` 和 `updatePerTenMinutes()`, 当前实现有以下遗漏：

### 修复项

#### 2a. 抚摸时的开心度增加缺失
**SDV 源码**：`if (wasPet) happiness += Max(5, 30+happinessDrain)`  
**当前实现**：`applyAutoPetterEffect` 有这个，但手动抚摸时（`wasPetToday`）在 `applyDayUpdate` 中没有对应的 `+happiness` 逻辑。只扣了不被抚摸的 `-50`，没有被抚摸的 `+happinessDrain` 奖励。

**修复**：在 `applyDayUpdate` 的 `wasPetToday` 检查后添加：
```java
if (record.wasPetToday()) {
    int petHappiness = Math.max(5, 30 + profile.happinessDrain());
    record.addHappiness(petHappiness);
}
```

#### 2b. 抚摸时的好感度增加缺失
**SDV 源码**：`friendshipTowardFarmer += 15`（被抚摸时）  
**当前实现**：只有 `autoPetter` 路径加了好感度，手动抚摸在 entity 侧的 `pet()` 方法中是否有 `+15 friendship`？需要确认。

**修复**：确保 `BaseCoopAnimalEntity.mobInteract()`（抚摸逻辑）里调用 `record.addFriendship(15)`。

---

## 三、产品品质公式差异

### 问题
对照 `FarmAnimal.cs` 的品质计算：

### 修复项

#### 3a. `professionForQualityBoost` 全部填 -1
当前 PROFILES 里所有动物的 `professionForQualityBoost` 都是 `-1`，意味着**没有任何职业能提升品质**。

**SDV 源码**：每种动物有数据驱动的 `ProfessionForQualityBoost`（鸡→Coopmaster, 牛→Shepherd 等）。

**修复**：按 SDV 数据为每种动物填入正确的 `professionForQualityBoost`：
- 鸡舍动物：`ProfessionType.COOPMASTER.getId()`
- 畜棚动物：`ProfessionType.SHEPHERD.getId()`

#### 3b. `professionForFasterProduce` 也几乎全 -1
除了 sheep 以外，所有动物都没有设置加速产出职业。

**SDV 源码**：鸡舍动物被 Coopmaster 加速，畜棚动物被 Shepherd 加速。

**修复**：
- 鸡舍动物的 `professionForFasterProduce` 设为 Coopmaster
- 畜棚动物的 `professionForFasterProduce` 设为 Shepherd

---

## 四、繁殖系统

### 问题
`FarmAnimalRecord.allowReproduction` 字段存在但完全没有逻辑接入。SDV 中畜棚动物（牛、羊、猪、山羊）可以怀孕。

### 修复项

#### 4a. Overnight 怀孕检查
**SDV 源码**（`Building.cs` / `AnimalHouse.cs` 联动）：
- 每晚检查 `!isFull() && canGetPregnant()`
- 概率 = `friendship / 1200.0`
- 只有畜棚动物、非幼仔、`allowReproduction == true`
- 成功后玩家收到通知、次日建筑内出现幼仔

**修复**：在 `AnimalGrowthManager.growDaily()` 末尾新增 `tryReproduction()` 方法：
1. 遍历所有 barn 类建筑
2. 如果没满 && 有成年动物 && `allowReproduction`
3. 掷骰 `friendship / 1200.0`
4. 成功 → 创建同类幼仔 `FarmAnimalRecord`，`ageDays = 0`，绑定同建筑
5. 向建筑拥有者发 Chat 消息通知

#### 4b. 幼仔名称输入
SDV 中新生幼仔需要玩家命名。可以用 `AnimalQueryScreen` 的改版弹出命名框，也可以先用默认名（`"Baby " + parentName`）。

---

## 五、整夜留外惩罚不完整

### 问题
当前实现只做了 `happiness /= 2`，但 SDV 还有更多逻辑。

### 修复项

#### 5a. 门开着 + 动物在外 = 自动归位
**SDV 源码**：如果 `home.animalDoorOpen && !IsHome`，动物被强制移回室内（`setRandomPosition(insideHome)`），且 18:00 后 happiness 减半。  
**当前实现**：这个分支有 (`animalOutdoors && doorOpen`) 但只做了 `happiness /= 2` 没有移动动物。

**修复**：这个分支中需要调用 `forceMoveInsideHome()`（或标记需要同步回室内）。

#### 5b. moodMessage 设置
**SDV 源码**：门关着 + 留外 → `moodMessage = 6`（"留在外面整夜"） ✅ 已有  
**当前还缺**：这个 mood message 对应的中英文翻译文本未添加到 lang JSON。

---

## 六、Auto-Petter 逻辑不完整

### 问题
`applyAutoPetterEffect()` 有好感度加成但逻辑和 SDV 不一致。

### 修复项

**SDV 源码**的 auto-pet 逻辑：
- `friendshipTowardFarmer += max(0, 7 + autoPetReduction)`  
- 如果是 profession 加持（Coopmaster/Shepherd），额外 `+= 15` friendship

**当前实现**：
```java
if (record.wasAutoPetToday()) {
    record.addFriendship(autoPetReduction);  // 7
} else {
    record.addFriendship(15 - autoPetReduction);  // 8
}
```

逻辑分支不正确。`wasAutoPetToday` 在这个方法内部设置为 true，所以第一次调用永远走 else 分支，不会走 if 分支。

**修复**：简化为 SDV 原版的逻辑：
```java
record.addFriendship(Math.max(0, 7));  // autoPet 固定 +7
record.setWasAutoPetToday(true);
if (hasHappinessProfession) {
    record.addFriendship(15);
}
int happinessGain = Math.max(5, 30 + profile.happinessDrain());
record.addHappiness(happinessGain);
```

---

## 七、饲料消耗细节

### 问题

#### 7a. 吃草后应设置 fullness = 255
**SDV 源码**：`Eat()` 方法中 `fullness.Value = 255`  
**当前实现**：`EatPastureGrassGoal` 中是否正确设置了 record 的 fullness？需要确认 entity 侧吃草与 record 数据的同步。

**修复**：确保动物吃草后 `FarmAnimalRecord.setFullness(255)` 被调用。

#### 7b. 节日日全天饱食
**SDV 源码**：`isFestivalDay() → fullness = 250` ✅ 已有

#### 7c. GrassEatAmount 数据驱动
**SDV 源码**：每种动物有不同的 `GrassEatAmount`（默认 2）。`if (GrassEatAmount < 1) fullness = 255`（不需要吃草的动物自动满）。  
**当前实现**：`EatPastureGrassGoal` 中是否按动物类型调整吃草量？

**修复**：确保草地消耗量和 SDV 一致。

---

## 八、杂项修复

### 8a. 好感度上限保护
**SDV 源码**：好感度 clamp 到 `[0, 1000]`，开心度 clamp 到 `[0, 255]`。  
**当前实现**：`FarmAnimalRecord.addFriendship()` / `addHappiness()` 是否有 clamp？需要确认所有修改路径都做了边界保护。

### 8b. 松露位置随机化
当前松露固定生成在猪旁边。SDV 中 truffles 出现在猪经过的位置，有自然散布效果。  
**修复**：`AnimalProducePlacementService.placeNearAnimal()` 的半径从 4 稍微随机化，且在非阻挡位置产生。

### 8c. 孤儿动物清理
建筑被拆除（ManagerBlock 被移除）后，`FarmAnimalRecord` 仍然存在且 `buildingId` 指向不存在的建筑。这些动物永远不会被同步到世界中。  
**修复**：在 `AnimalEntitySyncService.syncAll()` 中检查 `building == null` 的情况，将这些动物的实体移除或标记为"流浪"。

### 8d. 动物饼干双倍产出放置
**SDV 源码**：吃了 Animal Cracker 的动物产出 `Stack = 2`，但 DROP_OVERNIGHT 类型会分两次放置（各 1 个）。  
**当前实现**：`produceStack.grow(1)` 直接设为 2，但放置时作为一个 stack 放？

**修复**：检查 `placeInHome` 是否正确 — SDV 分 spawn 两次单个物品，确保 Animal Cracker 效果正确。

---

## 执行顺序建议

| 优先级 | 任务 | 预计复杂度 |
|--------|------|------------|
| P0 | 门口卡住问题（交错出门 + 目标偏移 + 推力穿门） | 高 |
| P1 | 品质职业加成修正（professionForQualityBoost / professionForFasterProduce） | 低 |
| P1 | 抚摸开心度 + 好感度缺失 | 低 |
| P1 | Auto-Petter 逻辑修正 | 低 |
| P2 | 繁殖系统接入 | 中 |
| P2 | 整夜留外自动归位 | 低 |
| P3 | 孤儿动物清理 | 低 |
| P3 | 松露随机化 | 低 |
| P3 | 吃草同步 + 好感度边界 | 低 |
