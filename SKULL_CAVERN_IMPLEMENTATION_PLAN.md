# Skull Cavern（骷髅矿洞）完整实现规划

> 基于 MineFloorGenerator.java 现有架构扩展，保持代码一致性。
> SDV 原版逻辑参照：源文件/StardewValley.Locations/MineShaft.cs

---

## 一、总体架构

### 1.1 复用现有系统

骷髅矿洞 **不创建新维度**，复用 `stardew_mining` 维度，floor 121+ 继续使用 Z 轴偏移（`floor × 200`）。

| 模块 | 方案 | 改动量 |
|------|------|--------|
| 维度 & 坐标 | 复用 `STARDEW_MINING` + `MiningCoordinates` | 无 |
| 楼层数据 | 复用 `MineFloorDataManager`（int key 天然兼容） | 小 — 加重置逻辑 |
| 梯子概率 | 复用 `LadderProbabilityCalculator` | 无 |
| 破石触发 | 复用 `MiningBlockBreakHandler` | 小 — 加 `isCountableStone` 扩展 |
| 怪物掉落 | 复用 `MineMonsterDropHandler`（Mummy/Serpent 已预留） | 无 |
| 梯子方块 | 复用 `MineLadderBlock` | 小 — 去上限 + 重置触发 |
| 梯子高亮 | 复用 `LadderHighlightRenderer` | 小 — 颜色按区域切换 |

### 1.2 新增/扩展

| 模块 | 方案 |
|------|------|
| `FloorTheme.SKULL_CAVERN` | 新枚举值 |
| 洞窟生成 — 骷髅矿专属地形 | `generateCaves` 内加 Skull Cavern 分支 |
| 主题环境 — 沙漠危险地形 | `generateThemeFeatures` 新增 `generateSkullCavernFeatures()` |
| 特殊房间 — 骷髅矿独有 | `generateSpecialRoom` 扩展 |
| 怪物表 | `pickMonsterForFloor` 扩展 121+ 分支 |
| 矿石表 | `pickOreKeyForFloor` 扩展 121+ 分支 |
| 怪物属性 | `MineMonsterSpawnHandler` 新增 Drowned→Mummy, Vex→Serpent, Hoglin→Dino, MagmaCube→BigSlime 映射 |
| 入口系统 | 新增骷髅矿入口方块 + 沙漠传送逻辑 |
| 会话重置 | 新增 `SkullCavernSessionManager` 管理进入/离开/清理 |
| 区块清理 | 异步卸载已离开楼层的区块数据 |
| 生物群系 | `MineBiomePatcher` 新增 121+ 分支 |

---

## 二、地形系统 — 沙漠矿洞的危险美学

### 2.1 FloorTheme 扩展

```java
public enum FloorTheme {
    EARTH,          // 1-39
    FROST,          // 40-79
    LAVA,           // 80-119
    SUMMIT,         // 120
    SKULL_CAVERN    // 121+
}
```

`getThemeForFloor()` 扩展：
```java
if (floorNumber > 120) return FloorTheme.SKULL_CAVERN;
```

### 2.2 石头选择 — 复用现有方块

骷髅矿是沙漠地下的远古洞窟，混合多种地质层：

| 用途 | 方块 | 理由 |
|------|------|------|
| **主石头** | `LAVA_BASALT` | 沙漠地下玄武岩层，深色有压迫感 |
| **Dark 变体** | `DARK_LAVA_BASALT` | 阴暗区域，30%+ 层是 dark |
| **装饰石A** | `SCORIA` + `CRACKED_SLATE` | 火山渣 + 裂纹石板 = 危险崩塌感 |
| **装饰石B** | `MOSSY_SANDSTONE` + `SALT_ROCK` | 远古沙漠沉积 + 盐结晶 = 地下荒漠感 |
| **原版点缀** | `SANDSTONE` + `RED_SANDSTONE` + `MAGMA_BLOCK` | 强化沙漠主题 |

**Dark 层概率**：SDV 原版骷髅矿 mapNumber >= 30 为 dark（30/40 = 75% 时非 dark），我们设为 **30% dark**（比常规矿的 15% 更高，但不至于多数层都暗）。

### 2.3 洞窟生成 — 骷髅矿专属变体

骷髅矿的洞窟系统在现有三阶段基础上做以下调整，制造**更开阔、更危险**的空间感：

#### Phase 1: 隧道系统调整
- **起源密度 ×1.5**：更多隧道 → 更多联通 → 更混乱
- **隧道厚度 ×1.3**：更宽的通道，暗示巨型生物曾经通过
- **分叉概率 ×1.5**：更多 Y 形分支 → 迷宫感

#### Phase 2: 峡谷调整
- **峡谷概率 85%**（vs 常规 70%）：几乎每层都有深裂缝
- **峡谷更深更窄**：垂直感更强，制造"一失足成千古恨"的紧张感

#### Phase 3: Cheese Cave 调整
- **数量 2-4 个**（vs 常规 1-3）：更大的开阔空间
- **半径更大**：让玩家感受到地下巨穴的震撼

### 2.4 主题环境特色 — `generateSkullCavernFeatures()`

这是骷髅矿区别于其他区域的核心。设计理念：**远古沙漠文明的地下遗迹 + 极端自然环境**。

#### 特色 1: 熔岩裂隙网络（Lava Fissure Network）
**描述**：地面上的裂缝，内部流淌着熔岩，随机蜿蜒穿过洞窟。

- 每层 1-3 条裂隙
- 宽度 1-2 格，深度 2-3 格
- 裂隙用 `MAGMA_BLOCK` 为底，上方放 `LAVA`（1格深，源头方块）
- 裂隙边缘用 `SCORIA` 装饰
- 玩家掉入 = 踩岩浆（伤害 + 着火）
- 明亮光源，照亮洞窟

**生成算法**：从随机起点出发，每步随机转向 ±45°，持续 15-30 步。每步在地面挖 1-2 格深的沟槽。

#### 特色 2: 沙岩柱林（Sandstone Pillar Forest）
**描述**：从地面到天花板的粗大石柱，有些断裂倾斜，有些顶部坍塌。

- 每层 2-5 根柱子
- 直径 2-3 格，高度到天花板
- 基座用 `SANDSTONE`，中段 `LAVA_BASALT`，顶部用主石头融入天花板
- **30% 概率"断裂柱"**：只有下半截，顶部碎裂成台阶/半砖散落
- 柱子间形成天然掩体和视线遮挡

#### 特色 3: 悬空岩桥（Suspended Rock Bridge）
**描述**：横跨大型洞穴的窄石桥，下方是深渊或熔岩池。

- 仅在 Cheese Cave 大型洞穴内生成
- 宽度 2-3 格，两端连接洞壁
- 中段概率缺损（1-2 格空洞）→ 需要跳跃
- 桥下如果足够深（>5格）放置熔岩池
- 制造"高空走钢丝"的紧张感

#### 特色 4: 骨骸化石层（Fossil Layer）
**描述**：墙壁中暴露出巨大骨骼化石，暗示远古巨兽。

- 使用 `Blocks.BONE_BLOCK`（带方向属性）嵌入墙壁
- 3-7 块骨头排列成弧形，模拟肋骨或脊椎
- 每层 0-2 组化石
- 纯装饰，强化"远古深渊"氛围
- 化石旁偶尔放置 `SOUL_LANTERN`（幽蓝灯光）

#### 特色 5: 沙瀑（Sand Fall）
**描述**：从天花板的裂缝中不断落下沙子的粒子效果区域。

- 使用 MC 原版 `Blocks.SAND` + `Blocks.RED_SAND` 在天花板缝隙处
- 但不让 sand 实际掉落（下方放隐形 barrier 或用半砖接住）
- 实际实现：在天花板裂缝处放置 `SANDSTONE`，地面散落少量 `SAND`（用半砖状态或地毯模拟薄沙层）
- 若可行：用 `Blocks.SUSPICIOUS_SAND`（考古沙）作为可互动的考古挖掘点

#### 特色 6: 毒气区域（Toxic Mist Zone）
**描述**：部分洞窟区域地面飘着绿色迷雾。

- 生成在封闭性较好的洞穴凹地中
- 地面铺 `Blocks.SCULK`（深色，视觉上有"有毒"感）
- 上方 2-3 格空气中持续生成 `DRAGON_BREATH` 或自定义粒子
- 进入区域给予 `POISON` 效果 2 秒（通过 AreaEffectCloud 或 tick 检测）
- 每层 0-1 个毒气区，面积 4×4~6×6
- 迫使玩家绕行或快速穿越

### 2.5 特殊房间 — 骷髅矿独有

| 房间类型 | 概率 | 描述 |
|----------|------|------|
| **铱矿密集洞** | 12% | 小型洞穴，墙壁 40-60% 铱矿石（SDV 宝藏房概念） |
| **远古武器室** | 5% | 废弃房间，中央有宝箱（高级战利品），四周骷髅装饰 |
| **Dino 巢穴** | 3%（仅 floor≥146） | 菌丝地面 + 恐龙蛋 + DinoMonster 专属刷新 |
| **岩浆湖洞穴** | 8% | 大型开阔洞穴，中央是巨大熔岩湖，周围有矿石环 |

### 2.6 光照系统

| 类型 | 方块 | 间距 |
|------|------|------|
| 普通层 | `SOUL_LANTERN`（地面）+ `MAGMA_BLOCK`（天花板，利用熔岩裂隙自然照明） | 10 格 |
| Dark 层 | 仅 `SOUL_LANTERN`，密度降至 1/4 | 20 格 |
| 安全区 | `SOUL_TORCH` ×4 | 固定 |

**设计意图**：骷髅矿比常规矿更暗，Soul 系列灯光的幽蓝色调增强恐怖感，但熔岩裂隙提供橙红色自然光源，形成冷暖对比。

---

## 三、怪物系统

### 3.1 怪物表 — `pickMonsterForFloor()` 扩展

完全遵循 SDV MineShaft.getMonsterForThisLevel() 骷髅矿分支的瀑布概率：

```
if (floor > 120) {  // Skull Cavern
    // ── Dark 层特殊怪物 ──
    if (isDark) {
        if (18%, distance > 8) → Carbon Ghost (HUSK + sd_tier_skull)
        else                   → Mummy (DROWNED + sd_mob_mummy)
    }
    // ── 通用怪物（按 SDV 原版概率瀑布）──
    if (floor%20==0, distance>10)   → Bat (PHANTOM)
    if (floor%16==0, !monsterArea)  → Bug (SPIDER)
    if (33%, distance>10)           → Serpent (VEX + sd_mob_serpent)
    if (33%, distance>10, floor≥171)→ Bat (PHANTOM)
    if (4%, floor≥146, distance>10) → DinoMonster (HOGLIN + sd_mob_dino)
    if (33%, !monsterArea)          → Bug (SPIDER)
    if (25%)                        → GreenSlime (SLIME)
    if (floor≥146, 25%)             → Iridium Crab (SILVERFISH + sd_tier_4)
    fallback                        → BigSlime (MAGMA_CUBE + sd_mob_bigslime)
}
```

**飞行怪物**：Vex(Serpent) 和 Phantom(Bat) 在空中生成。Hoglin(Dino) 在地面。

**距离判断**：SDV 的 `distanceFromLadder > 10` 映射为距安全区中心 > 15 格（考虑我们房间更大）。

### 3.2 MC 实体 → SDV 怪物映射（新增）

| MC 实体 | SDV 怪物 | 标签 | HP | ATK | 备注 |
|---------|---------|------|-----|-----|------|
| **Drowned** | Mummy | `sd_mob_mummy` | 260 | 30 | 被击倒后 10s 复活（除非火焰击杀） |
| **Vex** | Serpent | `sd_mob_serpent` | 150 | 23 | 飞行，速度快 |
| **Vex** (变体) | Royal Serpent | `sd_mob_royal_serpent` | 300 | 30 | 难度加成时替代普通 Serpent |
| **Hoglin** | DinoMonster | `sd_mob_dino` | 320 | 35 | 稀有地面大型怪 |
| **Magma Cube** | Big Slime (Skull) | `sd_mob_bigslime_skull` | 200 | 20 | 分裂出小 Slime |
| **Silverfish** | Iridium Crab | `sd_tier_4` | 300 | 28 | 复用 RockCrab 映射 |
| **Husk** | Carbon Ghost | `sd_tier_skull` | 190 | 25 | 复用 Ghost 映射 + 标签区分 |

### 3.3 怪物数量缩放

SDV 骷髅矿怪物密度比常规矿更高：
- 基础数量：`area / 500`（vs 常规 `area / 600`）
- 上限：35（vs 常规 30）
- Monster Area（4.4% 概率）：数量 ×1.5

### 3.4 Mummy 特殊机制

SDV 核心机制：Mummy 被"杀死"后倒地，10 秒后复活，只有炸弹（爆炸伤害）才能永久击杀。

**MC 实现**：
- Drowned 被击杀时，检查死因是否为 `EXPLOSION`
- 非爆炸死亡 → 取消 `LivingDropsEvent` + 延迟 10s 在原位重新 spawn 一只（携带 `sd_mummy_revive` 标签）
- 爆炸死亡 → 正常掉落 + 不复活
- 视觉反馈：倒地时播放粒子 + 音效提示"它还没死"

### 3.5 难度缩放

随深度递增（`floor - 120` 为基础）：
```
scalingFactor = 1.0 + (floor - 120) * 0.008  // floor 200 → 1.64×
```
影响：HP、ATK、Speed 全部乘以 scalingFactor。上限 2.5×。

---

## 四、矿石系统

### 4.1 矿石概率表 — SDV Skull Cavern 精确还原

基础矿石出现率随深度递增：
```
baseOreRate = 0.02 + skullLevel * 0.0005  // skullLevel = floor - 120
```

权重分配（SDV createLitterObject 精确数值）：

| 矿石 | 基础权重 | 递增 | 备注 |
|------|---------|------|------|
| **铱矿** | `min(100, skullLevel) × 0.0003` + iridiumBoost | boost 随深度，上限 0.004 | 骷髅矿核心资源 |
| **金矿** | `0.01 + (floor - min(150, skullLevel)) × 0.0005` | 中后期主力 | |
| **铁矿** | `min(0.5, 0.1 + (floor - min(200, skullLevel)) × 0.005)` | 前期填充 | |
| **铜矿** | 保底 | 剩余概率 | |
| **煤矿** | 0.006 固定 | — | 全层通用 |

### 4.2 矿脉尺寸

| 矿石 | 矿脉大小 | 备注 |
|------|---------|------|
| 铱矿 | 2-4 | 小而精 |
| 金矿 | 3-6 | 中等 |
| 铁矿 | 4-7 | 较大 |
| 铜矿 | 4-8 | 填充用 |
| 煤矿 | 3-6 | 通用 |

### 4.3 矿石方块

复用 Lava 段矿石方块（`LAVA_*_ORE`），与 `LAVA_BASALT` 主石头视觉一致。

### 4.4 A类矿（表面直采矿物）

SDV: 30% Earth Crystal, 30% Frozen Tear, 40% Fire Quartz（骷髅矿混合三段）
→ 复用现有 `ModBlocks.FIRE_QUARTZ`(40%) + `EARTH_CRYSTAL`(30%) + `FROZEN_TEAR`(30%)

### 4.5 B类矿（宝石节点）

全品类解锁：Amethyst + Topaz + Aquamarine + Jade + Ruby + Emerald + Diamond
Diamond 概率略提升（骷髅矿深层奖励感）。

---

## 五、梯子系统

### 5.1 MineLadderBlock 改动

```java
// 去掉硬上限
// 旧代码：if (nextFloor > 120) { 提示最深处; return FAIL; }
// 新代码：无上限，骷髅矿层直接继续
```

### 5.2 梯子高亮颜色

`LadderHighlightRenderer.java` 修改：
```java
// 常规矿 (floor 1-120): 紫色 R=180, G=80, B=255
// 骷髅矿 (floor 121+):  红色 R=255, G=60, B=60
```

气泡文字也变为红色系。

### 5.3 梯子概率（无修改）

SDV 骷髅矿的梯子概率公式与常规矿完全一致（`LadderProbabilityCalculator` 不需要改动）。

### 5.4 Shaft（矿井跳坑）— 取消

按用户决策：不做 Shaft 机制，统一使用 Ladder。

---

## 六、会话与重置系统

### 6.1 核心规则（仿 SDV 原版）

1. 玩家从入口进入 → 传送到 floor 121 → 标记**骷髅矿会话开始**
2. 每层按需生成（下梯子时触发 `generateFloor`）
3. **最后一个玩家离开骷髅矿区域时** → 触发全部 121+ 楼层重置
4. 下次任何玩家进入 → 重新从 floor 121 开始

### 6.2 `SkullCavernSessionManager`（新增）

```java
public class SkullCavernSessionManager {
    // 追踪当前在骷髅矿内的玩家集合
    private static final Set<UUID> playersInSkullCavern = ConcurrentHashMap.newKeySet();
    
    // 当前会话的最深层（用于清理范围）
    private static int sessionDeepestFloor = 121;
    
    // 进入骷髅矿
    public static void onPlayerEnter(ServerPlayer player) { ... }
    
    // 离开骷髅矿（传送走/死亡/断线）
    public static void onPlayerLeave(ServerPlayer player, ServerLevel level) { ... }
    
    // 全部离开 → 异步清理
    private static void resetSession(ServerLevel level) { ... }
}
```

### 6.3 重置流程

```
resetSession(level):
  1. 遍历 floor 121 → sessionDeepestFloor:
     a. MineFloorDataManager.clearFloorData(floor)
     b. MineFloorDataManager.floorGenerationDays.remove(floor)
  2. 异步清除已生成楼层的方块数据（setBlock → AIR）
     — 分 tick 执行，每 tick 清理 1 层，避免卡服
  3. sessionDeepestFloor = 121
  4. playersInSkullCavern.clear()
```

### 6.4 触发时机

| 事件 | 处理 |
|------|------|
| `PlayerEvent.PlayerChangedDimensionEvent` | 若从 `STARDEW_MINING` 离开 → `onPlayerLeave` |
| `PlayerEvent.PlayerLoggedOutEvent` | 若在 `STARDEW_MINING` 且 floor > 120 → `onPlayerLeave` |
| `PlayerEvent.PlayerRespawnEvent` | 若死在骷髅矿 → `onPlayerLeave`（死亡踢出骷髅矿） |
| 梯子交互（floor=121 且来自入口） | → `onPlayerEnter` |
| 每 10 分钟 tick | 检查是否有"游离"会话需要清理 |

### 6.5 区块生命周期管理（服务器安全）

**核心策略**：骷髅矿楼层不常驻区块，只保留玩家附近的。

1. **当玩家下到 floor N 时**：
   - force-load floor N 和 N-1 的区块（当前层 + 上一层）
   - 如果 N-3 或更早层没有其他玩家 → unforce 这些层的区块
   
2. **`forceChunkLoading(floor)` / `unforceChunkLoading(floor)`**：
   - 骷髅矿每层占据一个区域：中心 ± 60 格 → 约 8×8 chunk 区域
   - 使用 `level.getChunkSource().updateChunkForced()` 控制

3. **重置时**：
   - 按顺序 unforce 所有区块
   - 异步清除方块（每 tick 1 层）
   - 完成后让 MC 自然卸载这些 chunk

**内存预估**：每层 8×8=64 个 chunk，每 chunk 约 8KB（mostly air + barrier shell）= ~512KB/层。
玩家在 floor 200 时，若只保留 2 层 = ~1MB。重置后归零。

---

## 七、入口系统

### 7.1 骷髅矿入口方块（新增）

`SkullCavernEntranceBlock`：放置在沙漠地图中，右键交互传送到 floor 121。

- 视觉：骷髅形状的洞穴入口（可用现有方块组合 + NBT 结构）
- 右键 → 确认对话 → 传送到 floor 121 安全区
- 传送前调用 `SkullCavernSessionManager.onPlayerEnter()`
- 如果骷髅矿正在被其他人使用 → 同样可以进入（多人共享同一个实例）

### 7.2 Mine Exit 在骷髅矿的行为

现有 `MineExitBlock` 在骷髅矿安全区中同样放置，但传送目标改为：
- **选项 1**：返回沙漠入口（而非矿井 Floor 0）
- **选项 2**：返回地表出生点
- 触发 `SkullCavernSessionManager.onPlayerLeave()`

---

## 八、生物群系补丁

`MineBiomePatcher` 扩展：
```java
if (floor > 120) {
    // 骷髅矿：使用沙漠矿井生物群系（影响钓鱼等）
    return ModBiomes.MINES_SKULL_CAVERN;  // 或复用 MINES_100
}
```

需要注册新 biome `mines_skull_cavern`（或复用 `mines_100`，钓鱼表类似 Lava 段）。

---

## 九、实现顺序（优先级排序）

### Phase 1: 核心框架（能进入、能探索）
1. `FloorTheme.SKULL_CAVERN` 枚举 + `getThemeForFloor` 扩展
2. `MineLadderBlock` 去掉 120 上限
3. `generateFloor()` 中骷髅矿石头/主题分支（复用现有方块）
4. `pickMonsterForFloor()` 骷髅矿怪物表
5. `pickOreKeyForFloor()` 骷髅矿矿石概率
6. `MineMonsterSpawnHandler` 新增 Drowned/Vex/Hoglin/MagmaCube 映射
7. 基础入口（临时：在矿井 Floor 120 放置通往 121 的梯子）

### Phase 2: 地形特色（视觉差异化）
8. `generateSkullCavernFeatures()` — 熔岩裂隙 + 沙岩柱林 + 化石层
9. 洞窟参数调整（密度/厚度/峡谷概率提升）
10. 光照系统 Soul 变体
11. `LadderHighlightRenderer` 红色描边
12. `MineBiomePatcher` 121+ 分支

### Phase 3: 重置与服务器安全
13. `SkullCavernSessionManager` 会话管理
14. 区块生命周期管理
15. `MineFloorDataManager` 重置逻辑
16. 多人服务器测试

### Phase 4: 高级地形与特殊房间
17. 悬空岩桥
18. 毒气区域
19. 沙瀑装饰
20. 铱矿密集洞 / 远古武器室 / Dino 巢穴 / 岩浆湖洞穴

### Phase 5: Mummy 复活机制 + 难度缩放
21. Mummy 击倒-复活逻辑
22. 深度难度缩放公式
23. Dark 层 / Monster Area / Dino Area 特殊逻辑

---

## 十、需要新注册的内容

| 类型 | ID | 备注 |
|------|-----|------|
| 方块 | `skull_cavern_entrance` | 骷髅矿入口 |
| 生物群系 | `mines_skull_cavern` | 钓鱼/环境音 |
| 音效 | — | 可复用现有 |
| 实体标签 | `sd_mob_mummy`, `sd_mob_serpent`, `sd_mob_royal_serpent`, `sd_mob_dino`, `sd_mob_bigslime_skull`, `sd_tier_skull` | 怪物属性注入用 |
| 网络包 | `SkullCavernSessionPacket` | 同步会话状态 |
| SavedData | — | 复用 `MineFloorDataManager` |

---

## 十一、不改动的部分

| 模块 | 理由 |
|------|------|
| `LadderProbabilityCalculator` | SDV 骷髅矿公式与常规矿一致 |
| `MiningCoordinates` | 坐标公式天然兼容 121+ |
| `MineMonsterDropHandler` | Mummy/Serpent 掉落已预留 |
| 维度 JSON | 无需改动 |
| `MineFloorData` | int key 天然兼容 |
| `MiningBlockBreakHandler` | 通用逻辑，可能只需扩展 `isCountableStone` |
