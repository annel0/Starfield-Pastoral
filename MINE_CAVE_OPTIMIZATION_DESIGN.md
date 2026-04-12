# 矿洞洞窟视觉优化设计方案

> 目标：解决当前矿洞"丑"和"单调"两个核心问题。  
> 改动范围：仅 `MineFloorGenerator.java`（主逻辑）+ 少量 `ModBlocks.java`（如需新方块）  
> 不动：梯子概率、怪物系统、矿石概率、木桶掉落——这些都已完成。

---

## 现状诊断

### 当前生成流水线
```
fillInterior() → 整层实心填充（87%主石 + 9%装饰 + 4%原版，逐格随机）
  ↓
generateDecorPatches() → 6-11个球形斑块替换主石头
  ↓
generateCaves() → 2-N条虫蚀隧道 + 1-N个大型椭球房间
  ↓
generateLighting() → 随机散布地面火把/光源
  ↓
generateBarrels() → 随机散布木桶
  ↓
generateOres() → 椭圆矿脉
```

### 五大问题

| # | 问题 | 原因 |
|---|------|------|
| 1 | **石头分布像噪点** | `fillInterior()` 每格独立 roll 87/9/4%，装饰石和原版石完全随机散点，没有空间连续性 |
| 2 | **洞窟形状千篇一律** | 只有一种算法：球链隧道 + 椭球大房间，没有方形/L形/不规则走廊 |
| 3 | **空间完全平坦** | 洞窟内地面全是同一Y，没有台阶/平台/坑洞，天花板也是光滑椭球 |
| 4 | **墙/地/顶没区分** | 洞窟内壁、地面、天花板全用同一种方块画法，视觉上无法区分"哪是墙哪是地" |
| 5 | **缺少环境点缀** | 没有水洼、熔岩池、蜘蛛网、藤蔓、碎石堆等——只有木桶 |

---

## 已有可用方块清单

在动手之前先盘点已注册的方块资源（来自 `ModBlocks.java`），确认不需要"白注册"新方块：

### 主石头 × 3 主题 × 2 明暗 = 6 种
| 方块 | 主题 | 用途 |
|------|------|------|
| `EARTH_SHALE` / `DARK_EARTH_SHALE` | Earth 1-39 | 实心填充主体 |
| `FROST_GNEISS` / `DARK_FROST_GNEISS` | Frost 40-79 | 实心填充主体 |
| `LAVA_BASALT` / `DARK_LAVA_BASALT` | Lava 80-119 | 实心填充主体 |

### 主石头变体（Slab / Stairs / Wall）× 6 种 = 18 种
**已注册但完全没用到！** 这是最大的浪费——台阶和半砖可以直接用来做地形高低差。
| 方块 | 存在 |
|------|------|
| `EARTH_SHALE_SLAB` / `_STAIRS` / `_WALL` | ✅ |
| `FROST_GNEISS_SLAB` / `_STAIRS` / `_WALL` | ✅ |
| `LAVA_BASALT_SLAB` / `_STAIRS` / `_WALL` | ✅ |
| `DARK_EARTH_SHALE_SLAB` / `_STAIRS` / `_WALL` | ✅ |
| `DARK_FROST_GNEISS_SLAB` / `_STAIRS` / `_WALL` | ✅ |
| `DARK_LAVA_BASALT_SLAB` / `_STAIRS` / `_WALL` | ✅ |

### 装饰石头 × 6 种（不计数但可挖）
| 方块 | 当前分配 |
|------|----------|
| `LIMESTONE` | Earth 洞窟 |
| `MOSSY_SANDSTONE` | Earth 潮湿角落 |
| `CRACKED_SLATE` | 三段通用边缘 |
| `BANDED_MARBLE` | Frost 洞窟装饰 |
| `SALT_ROCK` | Frost 特殊洞窟 |
| `SCORIA` | Lava 火山渣岩 |

### 原版方块（已在用）
| 主题 | 方块 |
|------|------|
| Earth | `ANDESITE`, `DIRT` |
| Frost | `BLUE_ICE`, `PACKED_ICE`, `PRISMARINE_BRICKS` |
| Lava | `MAGMA_BLOCK`, `NETHERRACK` |

### 原版装饰方块（可直接用，无需注册）
| 方块 | 潜在用途 |
|------|----------|
| `COBWEB` | 蜘蛛网 — 废弃区域氛围 |
| `MOSS_CARPET` / `MOSS_BLOCK` | 苔藓 — Earth段地面/墙面 |
| `HANGING_ROOTS` | 悬挂根须 — Earth段天花板 |
| `GLOW_LICHEN` | 发光地衣 — 微弱墙面光照 |
| `POINTED_DRIPSTONE` / `DRIPSTONE_BLOCK` | 钟乳石/石笋 — 天花板/地面装饰 |
| `SNOW` / `POWDER_SNOW` | 雪层 — Frost段地面覆盖 |
| `CHAIN` | 铁链 — 天花板悬挂 |
| `LAVA` (fluid) | 熔岩池 — Lava段 |
| `WATER` (fluid) | 水洼 — Earth段 |
| `SCULK_VEIN` | 黑暗纹路 — Dark层墙面 |
| `AMETHYST_CLUSTER` | 紫水晶簇 — 宝石洞窟点缀 |
| `BONE_BLOCK` | 骨块 — 深层洞窟 |
| `CAMPFIRE` / `SOUL_CAMPFIRE` | 篝火 — 光源+氛围 |
| `LANTERN` / `SOUL_LANTERN` | 灯笼 — 替代裸火把 |
| `CANDLE` | 蜡烛 — 安静角落 |

---

## P0 — 立刻能感受到变化

### P0-1: 石头分布重写 — 地层化 + Perlin带状分布

**目标**：消除"噪点感"，让石头分布有肉眼可辨的大尺度纹理。

#### 当前问题
```java
// fillInterior() — 每个方块独立 roll
float roll = random.nextFloat();
if (roll < 0.87f) block = getMainStone(...);      // 逐格随机 → 噪点
else if (roll < 0.96f) block = getDecorativeStone(...);
else block = getVanillaBlock(...);
```
87% 主石 + 9% 装饰 + 4% 原版，**逐格独立随机**——结果是装饰石和原版石像洒芝麻一样散布在主石头里，完全没有空间连续性。

#### 新方案：分层 + Perlin噪声带状分布

**核心思路**：用 2D Perlin 噪声 (XZ平面) 决定**"地质带"**，同一带内使用相同的方块，创造连续、大面积的纹理变化。

```
┌──────────────────────────────────────┐
│  主石头带                             │
│    ┌──────────────┐                  │
│    │ 装饰石带      │                  │
│    │  (limestone) │  主石头带          │
│    └──────────────┘                  │
│          ┌──────────────────┐        │
│          │ 原版方块带         │        │
│          │  (dirt/andesite) │        │
│          └──────────────────┘        │
│  ┌────────────┐                      │
│  │ Dark变体带  │  主石头带             │
│  └────────────┘                      │
└──────────────────────────────────────┘
```

**实现方式**：

```
对每个 (x, z) 坐标：
  noise = perlinNoise(x * 0.08, z * 0.08, floorSeed)  // 值域 [-1, 1]
  
  if noise < -0.5:          → 原版方块带（dirt/andesite/ice...）
  else if noise < -0.15:    → 装饰石带（limestone/scoria...）
  else if noise < 0.15:     → 主石头（earth_shale...）
  else if noise < 0.5:      → Dark变体带（dark_earth_shale...）
  else:                     → 装饰石带（另一种）
```

**Y轴分层**（垂直地质带）：
```
Y = FLOOR_Y_END ~ END-2:    天花板层 — 偏 Dark变体 + 装饰石
Y = 中间层:                  主体层 — 按 Perlin 噪声
Y = START ~ START+2:        地基层 — 偏原版方块(泥土/碎石) + Dark变体
```

**关键参数**：
- Perlin频率：`0.06~0.10`（越小条带越宽，越大越碎）
- 每层使用不同的 seed offset（`floorNumber * 1000`），保证层间差异
- 保留 `isDark` 标记：Dark层整体提升 Dark变体占比（noise阈值偏移）

**对比效果**：
| 属性 | 旧 | 新 |
|------|----|----|
| 装饰石分布 | 随机散点(每格9%) | 连续带状区域(Perlin) |
| 空间连续性 | 无 | 3-8格宽带状 |
| 垂直分层 | 无 | 天花板/主体/地基三层不同 |
| Dark变体 | 30%概率混入主石 | 独立噪声带 + Dark层偏移 |

#### `generateDecorPatches()` 改造

当前的球形斑块生成仍然保留，但改为**只在Perlin装饰带区域内**加强密度（叠加效果），不再在主石带里随机插入。这样斑块不再是孤立的噪点，而是在已有装饰带上的"浓缩区"。

---

### P0-2: 洞窟形状多样化 — 引入多种Carver

**目标**：打破"全是圆形"的单调感，引入方形、L形、不规则走廊。

#### 新的洞窟类型系统

```java
enum CaveShape {
    TUNNEL,           // 现有的虫蚀球链隧道（保留，占比降低）
    RECTANGULAR_ROOM, // 方形/矩形空间（带圆角）
    L_CORRIDOR,       // L形走廊（两段直角连接）
    RAVINE,           // 窄缝裂隙（高而窄）
    IRREGULAR_CAVE    // 不规则洞穴（多个重叠偏移椭球）
}
```

每层生成时从池中随机抽取：

```
每层生成的洞窟组合：
  - 1-3 个 TUNNEL（现有算法，缩短长度）
  - 0-2 个 RECTANGULAR_ROOM
  - 0-1 个 L_CORRIDOR
  - 0-1 个 RAVINE
  - 1-3 个 IRREGULAR_CAVE（替代旧的 generateCaveRoom）
  - 隧道之间允许互相连通
```

#### 各形状实现要点

**RECTANGULAR_ROOM**（方形房间）：
```
在随机位置放置一个 W×H×D 的矩形空间
  W = 8-20, H = 4-8, D = 8-20（可长条形）
圆角处理：四个竖直边角用 r=2 的球体切削
可选：内部生成 1-2 个石柱（保留的主石头柱子，1×H×1 到 2×H×2）
```

**L_CORRIDOR**（L形走廊）：
```
两段矩形走廊，宽 3-5 格，在拐角处正交连接
拐角处用一个小型椭球平滑过渡
走廊方向：随机选择 东西→南北 或 南北→东西
```

**RAVINE**（窄缝裂隙）：
```
宽度：2-3 格（很窄！）
高度：利用大部分房间高度（Y_START+1 到 Y_END-1）
长度：15-40 格
方向：沿随机角度的直线，带轻微弯曲
效果：创造"一线天"般的峡谷感
```

**IRREGULAR_CAVE**（不规则洞穴）：
```
在中心点周围放置 4-8 个椭球，每个椭球偏移 2-6 格
椭球半径各不相同（3-8），朝不同方向拉伸
结果：形成不规则的犬牙交错的边缘
远优于单个大椭球的光滑感
```

---

### P0-3: 地形高低差 — 台阶 / 平台 / 坑洞

**目标**：让洞窟内的地面不再是一马平川。利用已注册但从未用过的 Slab 和 Stairs。

#### 三种微地形

**1. 抬升平台（Raised Platform）**
```
在洞窟地面随机选一块 3×3 到 6×6 的区域
将地面升高 1 格（放置主石头方块）
边缘用 Stairs 做过渡坡道（朝外）
顶部用 Slab 做半格微调

俯视图：          侧面图：
  ██████            ▓▓▓▓
  ██████          ╱▓▓▓▓▓▓╲
  ██████        ══════════════
```

**2. 下沉坑洞（Sunken Pit）**
```
在洞窟地面选一块 2×2 到 5×5 的区域
将地面降低 1 格（向下挖一格，底部放主石头）
边缘用 Stairs 做下坡（朝内）
Earth段坑底可放水(小水洼)，Lava段可放熔岩

侧面图：
  ══════╲    ╱══════
         ╲▓▓╱
          ▓▓    ← 坑底（可选流体）
```

**3. 地面碎石/半砖散布**
```
洞窟地面上随机放置 Slab（bottomHalf）
视觉效果：像地上散落的碎石，半格高
不影响行走（跳上去），但打破地面的平坦感
密度：洞窟地面方块的 5-10%
```

#### 天花板微地形

**悬挂柱体（Stalactite）**
```
从天花板向下延伸 1-3 格的石柱
使用原版 POINTED_DRIPSTONE（Earth/Frost）或 CHAIN（Lava）
密度：每个大房间 2-5 根
```

**石柱（Pillar）**
```
在大房间（半径 > 8）中，保留或生成 1-3 个从地到顶的石柱
材质：主石头（不被洞窟挖空）
尺寸：1×1 到 2×2
视觉效果：打破大房间的空旷感，增加空间层次
```

---

### P0-4: 洞窟内壁分化 — 墙面 / 地面 / 天花板

**目标**：让玩家能视觉区分"这是墙"、"这是地"、"这是顶"。

#### 洞窟装饰后处理 `decorateCaveInterior()`

在所有洞窟挖空完成后，新增一个 **后处理步骤**，扫描所有洞窟空气方块的六个邻居：

```java
for each airBlock in cave:
    for each neighbor of airBlock:
        if neighbor is solid stone:
            classify neighbor as:
                CEILING  — if neighbor is ABOVE air (neighbor.y > air.y)
                FLOOR    — if neighbor is BELOW air (neighbor.y < air.y)
                WALL     — if neighbor is on same Y (side neighbor)
            apply decoration based on classification + theme
```

**地面处理**（石头上方是空气）：
| 主题 | 处理方式 |
|------|----------|
| Earth | 15% 替换为 `MOSSY_SANDSTONE`（苔斑地面），5% 上放 `MOSS_CARPET` |
| Frost | 10% 上放 `SNOW`（薄雪层），5% 替换为 `PACKED_ICE`（光滑冰面） |
| Lava | 10% 替换为 `MAGMA_BLOCK`（岩浆块地面，会伤害无靴子玩家），5% 替换为 `NETHERRACK` |

**天花板处理**（石头下方是空气）：
| 主题 | 处理方式 |
|------|----------|
| Earth | 8% 下方悬挂 `HANGING_ROOTS`，3% 下方放 `POINTED_DRIPSTONE`(down tip) |
| Frost | 5% 下方放 `POINTED_DRIPSTONE`(down tip)，3% 下方放 `ICE` 柱(1-2格) |
| Lava | 5% 下方放 `CHAIN`，3% 替换为 `SHROOMLIGHT`（发光天花板） |

**墙面处理**（石头侧面是空气）：
| 主题 | 处理方式 |
|------|----------|
| Earth | 8% 替换为 `CRACKED_SLATE`，3% 侧面贴 `GLOW_LICHEN` |
| Frost | 6% 替换为 `SALT_ROCK`，4% 替换为 `BLUE_ICE` |
| Lava | 8% 替换为 `SCORIA`，3% 替换为 `NETHERRACK` |
| Dark层 ×任何主题 | 额外 5% 侧面贴 `SCULK_VEIN`（暗黑纹路） |

---

## P1 — 主题差异化

### P1-1: Earth段（1-39层）环境特色

**小水洼**：
```
在下沉坑洞底部或地面低洼处放置 WATER（静水 1 格深）
每层 0-2 个水洼，面积 2×2 到 4×4
水洼周围地面替换为 MOSSY_SANDSTONE
仅在非Dark层生成
```

**根须/藤蔓**：
```
天花板 HANGING_ROOTS 密度提升到 12%（已在 P0-4 基础上）
墙面：低层（1-15）额外 3% 放置 VINE（藤蔓）
GLOW_LICHEN 作为微弱自然光源
```

**泥土地面区域**：
```
利用 Perlin 噪声（P0-1）中的原版方块带，Earth段地基层：
  偏好 DIRT + COARSE_DIRT + ROOTED_DIRT
  洞窟地面处的泥土上概率放 MOSS_CARPET 或 SHORT_GRASS（矮草）
```

**碎石堆**（新装饰点）：
```
在大房间角落或走廊死胡同，堆叠 2-4 个方块：
  底层 2×2 主石头，上方 1-2 个 COBBLESTONE 或 Slab
  大约每层 1-3 堆
  视觉上像塌方碎石
```

### P1-2: Frost段（40-79层）环境特色

**冰面区域**：
```
地面有 10-20% 面积替换为 PACKED_ICE / BLUE_ICE
冰面上行走会打滑（原版特性，无需额外代码）
冰面不作为"可计数石头"
```

**冰柱/冰晶**：
```
天花板：POINTED_DRIPSTONE(down) 密度提升到 10%
地面：POINTED_DRIPSTONE(up) 密度 5%（石笋）
大房间中：1-2 个 PACKED_ICE 柱（1×1×3~5 从地到接近天花板）
```

**霜雪覆盖**：
```
洞窟地面 SNOW 层覆盖率 15%（P0-4 的 10% 提升）
非洞窟的实心区域顶面也有概率覆盖（但是在石头里面看不到）
→ 优化：只在地面有空气的位置放 SNOW
```

**冰冻水洼**：
```
与 Earth 水洼类似，但水面被 ICE 覆盖（水面上放一层 ICE）
视觉上是冻结的浅水坑
```

### P1-3: Lava段（80-119层）环境特色

**熔岩池**：
```
在下沉坑洞底部放 LAVA（静止熔岩 1 格深）
每层 1-3 个熔岩池，面积 2×2 到 5×5
周围 1 格内地面替换为 MAGMA_BLOCK
提供光照 + 危险感（无火焰保护会着火）
池边有 OBSIDIAN 点缀（1-3 格）

‼️ 安全约束：距离安全区 > 15 格，距离梯子 > 8 格
```

**岩浆裂缝**：
```
1 格宽、5-12 格长的直线 LAVA 沟（地面下沉 1 格再放 LAVA）
方向随机，像地面裂开的缝隙
每层 0-2 条
```

**灼热空气效果**：
```
MAGMA_BLOCK 地面占比 15%（P0-4 的 10% 提升）
SHROOMLIGHT 天花板光源占比 5%
NETHERRACK 墙面占比 8%
深层（100+）额外增加 BLACKSTONE 混入
```

**黑曜石柱**：
```
大房间中 1-2 个 OBSIDIAN 柱（1×1 × 3-6 高）
视觉效果：冷却的岩浆柱，点缀在橙红色环境中很抓眼
```

---

## P2 — 兴趣点与特殊事件

### P2-1: 特殊房间类型

在 `generateCaves()` 中，每层有概率生成一个特殊洞穴室（替代普通 `generateCaveRoom`）。

**蘑菇房（Mushroom Room）**：
```
概率：8% per floor（Earth + Frost 段）
外观：中型方形房间（8×8 到 12×12），地面全部 MYCELIUM
内容：3-6 个红/棕蘑菇方块（RED_MUSHROOM_BLOCK / BROWN_MUSHROOM_BLOCK）
      2-4 个地面蘑菇植物（RED_MUSHROOM / BROWN_MUSHROOM）
      1 个紫蘑菇（自定义 PURPLE_MUSHROOM，如果已注册）
光照：2 个菌光体（SHROOMLIGHT）嵌入天花板
```

**矿石密集区（Rich Vein Chamber）**：
```
概率：10% per floor
外观：小型不规则洞穴（6×6 到 8×8）
内容：墙壁 30-50% 替换为该楼层的主矿石
      仿佛发现了一条矿脉的母体
      中心可能有 1 个 AMETHYST_GEODE（紫晶洞）
      内壁布满 AMETHYST_CLUSTER
```

**骨骸房（Bone Chamber）** — 仅 80+ 层：
```
概率：5% per floor（Lava 段）
外观：中型房间
内容：地面散布 BONE_BLOCK（4-8个）
      1-2 个 SKELETON_SKULL（头颅）
      墙壁有 SOUL_LANTERN 照明
      怪物密度在此区域 ×2
```

**宝箱暗室（Treasure Alcove）**：
```
概率：5% per floor
外观：小型方形房间（4×4 到 6×6），带一条短走廊连接主洞窟
内容：中心一个 BARREL（带特殊掉落标记）
      四角各一个 CANDLE 或 LANTERN
      地面使用 BANDED_MARBLE（冰段）或 LIMESTONE（土段）铺设
      入口走廊只有 2 格宽，有"发现密室"感
```

### P2-2: 环境装饰物散布

在 `generateBarrels()` 之后新增 `generateEnvironmentDecor()` 步骤：

**蜘蛛网（Cobwebs）**：
```
Earth 段：洞窟角落（两面墙交汇处的空气方块）放 COBWEB
密度：每层 3-8 个
判定：air block，至少两个水平邻居是石头
```

**骨头碎片**：
```
Lava + Deep Earth (30+层)：地面随机放置 BONE_BLOCK
密度：每层 0-3 个
```

**矿车轨道残片**：
```
Earth 段（20+ 层）：直线 3-6 格 RAIL（原版铁轨）嵌入地面
每层最多 1 条
不连接任何矿车——纯装饰，暗示这是一个曾被开采过的矿洞
```

**小型篝火**：
```
每 3 层有 1 个概率（15%）生成一个"矿工营地"：
  CAMPFIRE（一个）+ BARREL（一个）+ 空气空间
  营地附近的墙面不放 GLOW_LICHEN/蜘蛛网（干净区域）
  暗示有人曾在此休息
```

### P2-3: 光照系统重做

将 `generateLighting()` 从"纯随机散布"改为"基于洞窟结构的智能光照"。

**规则**：
```
1. 火把/灯笼只放在洞窟内的墙面旁（侧面有石头的空气方块）
   → 不再凭空放在地面中央
   → Earth: TORCH (wall), Frost: SOUL_LANTERN, Lava: 不额外放（SHROOMLIGHT + MAGMA 自带光）

2. 每个洞窟空间以一定间距（8-12格）放置一个光源
   → 不再 random.nextDouble() < 0.009 逐格判定
   → 走廊间距缩小到 6-8 格，大房间中心放 1-2 个吊灯（LANTERN + CHAIN）

3. Dark层：光源密度 × 0.3，且只用 SOUL_LANTERN / SOUL_CAMPFIRE（蓝色暗光）

4. 走廊两端入口处必放一盏灯（标记入口感）

5. GLOW_LICHEN 作为不占方块位的微弱墙面光源（P0-4 已处理）
```

---

## 实现计划（改动范围）

### Phase 1 — P0 实现

只改 `MineFloorGenerator.java` 一个文件：

| 方法 | 改动 | 描述 |
|------|------|------|
| `fillInterior()` | **重写** | 引入 Perlin 噪声分层 + Y轴地质带 |
| `generateDecorPatches()` | **改造** | 只在 Perlin 装饰带区域内加强生成 |
| `generateCaves()` | **重写** | 引入 CaveShape 多类型系统 |
| `carveCaveTunnel()` | **保留+缩短** | 降低使用频率，缩短默认长度 |
| `generateCaveRoom()` | **替换** | 用 `IRREGULAR_CAVE` 和 `RECTANGULAR_ROOM` 替代 |
| 新增 `generateRavine()` | **新增** | 窄缝裂隙生成 |
| 新增 `generateLCorridor()` | **新增** | L形走廊生成 |
| 新增 `generateMicroTerrain()` | **新增** | 台阶/平台/坑洞/碎石散布 |
| 新增 `decorateCaveInterior()` | **新增** | 墙面/地面/天花板后处理分化 |
| 新增 `generatePillars()` | **新增** | 大房间石柱 |
| 新增 `PerlinNoise` 内部类 | **新增** | 简单的 2D Perlin 噪声实现（~40行） |
| `generateLighting()` | **重写** | 基于洞窟结构的智能光照 |

### Phase 2 — P1 实现

| 方法 | 改动 |
|------|------|
| `decorateCaveInterior()` | 扩展主题特化装饰（水洼/冰面/熔岩池） |
| 新增 `generateWaterPool()` | Earth 段水洼 |
| 新增 `generateIceFeatures()` | Frost 段冰柱/冰面 |
| 新增 `generateLavaFeatures()` | Lava 段熔岩池/裂缝/黑曜石柱 |

### Phase 3 — P2 实现

| 方法 | 改动 |
|------|------|
| 新增 `generateSpecialRoom()` | 蘑菇房/矿石密集区/骨骸房/宝箱暗室 |
| 新增 `generateEnvironmentDecor()` | 蜘蛛网/骨头/轨道/篝火 |
| `generateLighting()` | 智能光照（墙面灯、间距控制、入口标记灯） |

### 注意事项

1. **不动梯子概率**：`countStones()` / `isCountableStone()` 保持不变，新装饰方块不计入 stonesLeft
2. **不动怪物系统**：`spawnMonsters()` 的地面扫描逻辑可能需要微调（台阶/半砖上也能放怪物）
3. **不动矿石概率**：`generateOres()` 的 `isOreReplaceable()` 需要扩展以包含新方块（MOSS_BLOCK 等可被矿石替换）
4. **性能**：Perlin 噪声需要在 fillInterior 的大循环中计算，但 2D Perlin 单次计算约 ~50ns，80×80×20 = 128k 次 ≈ 6ms，完全可接受
5. **Slab/Stairs 的挖掘计数**：Slab 和 Stairs 算不算"石头"？建议**不算**——它们是装饰性的，不应影响梯子概率
6. **流体安全**：水/熔岩放置后不会流动，因为用的是静止源方块且被石头围住；熔岩池需要检查不会流向安全区
