# 木材加工台 & 石材加工台 — 完整设计文档

## 一、概述

玩家使用星露谷的 `wood_normal`（普通木材）和 `stone`（石材）作为原料，在加工台上兑换为 MC 原版或其他模组的建材方块。填补当前"星露谷资源无法转化为 MC 建材"的缺口，让玩家能用自己种/砍/挖的资源来建造漂亮的家。

---

## 二、经济平衡分析

### 2.1 当前资源收入

| 资源 | 来源 | 每次产量 | 周期/条件 |
|------|------|---------|----------|
| **wood_normal** | 砍 1 棵普通树 | 17–25（含树桩） | 树生长周期 28 天 |
| wood_normal | Forester 职业加持 | 21–31 | 同上，×1.25 |
| wood_normal | Robin 商店 | 无限 | 10g/个 |
| **stone** | 挖 1 块矿井石头 | 1（1:1） | 需下矿 |
| stone | Robin 商店 | 无限 | 20g/个 |
| **wood_hard** | 砍 1 棵 Mahogany 树 | 9–16（含树桩） | 稀有树种 |
| wood_hard | Robin 商店 | 无限 | 200g/个 |

### 2.2 当前资源消耗

| 用途 | wood_normal | stone | wood_hard |
|------|-------------|-------|-----------|
| 鸡舍 (Coop) | 300 | 100 | — |
| 畜棚 (Barn) | 350 | 150 | — |
| 筒仓 (Silo) | — | 100 | — |
| 木箱 (Wooden Chest) | 50 | — | — |
| 石箱 (Stone Chest) | — | 50 | — |
| 保存罐 (Preserves Jar) | 50 | 40 | — |
| 小桶 (Keg) | 30 | — | — |
| 织布机 (Loom) | 60 | — | — |
| 熔炉 (Furnace) | — | 25 | — |
| 宝石复制机 (Crystalarium) | — | 99 | — |
| 奶酪压制机 | 45 | 45 | 10 |
| 榨油机 (Oil Maker) | — | — | 20 |
| 每日任务平均需求 | 50 | 40 | — |

### 2.3 平衡目标

**核心原则：加工台是「资源换建材」的转化器，不是印钞机。**

- **砍 1 棵树（~20 wood）** → 应该能做出"一小面墙"的量，而不是整座房子
- **挖 1 层矿井（~30 stone）** → 类似规模
- **建造一栋 10×10×5 的小房子** → 需要约 500–800 块建材 → 需要砍 50–80 棵树 或挖 50–80 层矿
- **建材 vs 生产设施** → 玩家必须在"建房子"和"造机器/建鸡舍"之间做取舍

### 2.4 金币等价验证

| 兑换 | 木材成本 (g) | MC 等价物实际价值 |
|------|-------------|----------------|
| 5 wood → 1 原木 | 50g | 合理（原木是高级建材） |
| 2 wood → 1 木板 | 20g | 合理（基础建材） |
| 1 wood → 1 台阶 | 10g | 便宜但台阶是半块 |

| 兑换 | 石材成本 (g) | MC 等价物实际价值 |
|------|-------------|----------------|
| 1 stone → 1 圆石 | 20g | 合理（最基础石材） |
| 2 stone → 1 石头 | 40g | 合理（需要烧制的中级石材） |
| 3 stone → 1 石砖 | 60g | 合理（精加工产品） |

---

## 三、兑换比例（最终方案）

### 3.1 木材加工台

**输入材料**：`stardewcraft:wood_normal`（普通木材）
**高级替代**：`stardewcraft:wood_hard`（硬木），1 硬木 = 5 普通木材

| 产出分类 | 扫描 Tag | 每个消耗 (wood) | 每次产出 | 换算 | 设计理由 |
|---------|---------|----------------|---------|------|---------|
| 原木 (Log) | `#minecraft:logs`（不含 stripped） | **5** | 1 | 5:1 | 高级建材，1 棵树→4 根原木 |
| 去皮原木 (Stripped Log) | `#minecraft:logs`（含 stripped） | **5** | 1 | 5:1 | 同原木，装饰性强 |
| 木块 (Wood) | oak_wood 等 6 面树皮方块 | **5** | 1 | 5:1 | 同原木类 |
| 木板 (Planks) | `#minecraft:planks` | **2** | 1 | 2:1 | 核心建材，1 棵树→10 块木板 |
| 台阶 (Slab) | `#minecraft:wooden_slabs` | **1** | 1 | 1:1 | 半块，成本减半 |
| 楼梯 (Stairs) | `#minecraft:wooden_stairs` | **3** | 1 | 3:1 | 比木板贵（需切割加工） |
| 栅栏 (Fence) | `#minecraft:wooden_fences` | **1** | 1 | 1:1 | 便宜装饰物 |
| 栅栏门 (Fence Gate) | `#minecraft:fence_gates`（木质过滤） | **2** | 1 | 2:1 | 含铰链加工 |
| 门 (Door) | `#minecraft:wooden_doors` | **4** | 1 | 4:1 | 大件木制品 |
| 活板门 (Trapdoor) | `#minecraft:wooden_trapdoors` | **3** | 1 | 3:1 | 中等木制品 |
| 按钮 (Button) | `#minecraft:wooden_buttons` | **1** | 2 | 0.5:1 | 小零件批量出 |
| 压力板 (Pressure Plate) | `#minecraft:wooden_pressure_plates` | **1** | 1 | 1:1 | 小零件 |

**典型场景验证**：
- 砍 10 棵树 ≈ 200 wood → 40 原木 / 100 木板 / 200 栅栏 ✓
- 建一间 8×8×4 小木屋（约 300 木板）→ 需 600 wood ≈ 砍 30 棵树 ✓
- 对比：1 个 Coop 需要 300 wood，说明造房子和造鸡舍的资源代价相当 ✓

### 3.2 石材加工台

**输入材料**：`stardewcraft:stone`（石材）

| 产出分类 | 扫描方式 | 每个消耗 (stone) | 每次产出 | 换算 | 设计理由 |
|---------|---------|-----------------|---------|------|---------|
| 圆石 (Cobblestone) | 硬编码 | **1** | 1 | 1:1 | 最基础，几乎等价 |
| 苔石 (Mossy Cobblestone) | 硬编码 | **1** | 1 | 1:1 | 同圆石级 |
| 石头 (Stone) | 硬编码 | **2** | 1 | 2:1 | 需要"烧制" |
| 安山岩/闪长岩/花岗岩 | 硬编码 | **2** | 1 | 2:1 | 常见变种 |
| 磨制安山岩/闪长岩/花岗岩 | 硬编码 | **3** | 1 | 3:1 | 精加工 |
| 凝灰岩 (Tuff) | 硬编码 | **2** | 1 | 2:1 | 常见 |
| 磨制凝灰岩 | 硬编码 | **3** | 1 | 3:1 | 精加工 |
| 平滑石 (Smooth Stone) | 硬编码 | **3** | 1 | 3:1 | 精加工 |
| 石砖 (Stone Bricks) | 硬编码 | **3** | 1 | 3:1 | 精加工 |
| 苔石砖 | 硬编码 | **3** | 1 | 3:1 | 精加工 |
| 裂纹石砖 | 硬编码 | **3** | 1 | 3:1 | 精加工 |
| 雕纹石砖 | 硬编码 | **4** | 1 | 4:1 | 高级装饰 |
| 深板岩 (Deepslate) | 硬编码 | **3** | 1 | 3:1 | 深层材料 |
| 磨制深板岩 | 硬编码 | **3** | 1 | 3:1 | 精加工 |
| 深板岩砖 | 硬编码 | **4** | 1 | 4:1 | 高级加工 |
| 深板岩瓦 | 硬编码 | **4** | 1 | 4:1 | 高级加工 |
| 雕纹深板岩 | 硬编码 | **5** | 1 | 5:1 | 最高级装饰 |
| 砂岩 (Sandstone) | 硬编码 | **2** | 1 | 2:1 | 标准 |
| 红砂岩 | 硬编码 | **2** | 1 | 2:1 | 标准 |
| 平滑砂岩/红砂岩 | 硬编码 | **3** | 1 | 3:1 | 精加工 |
| 切制砂岩/红砂岩 | 硬编码 | **3** | 1 | 3:1 | 精加工 |
| 砖块 (Bricks) | 硬编码 | **3** | 1 | 3:1 | 需要烧制 |
| 泥砖 (Mud Bricks) | 硬编码 | **2** | 1 | 2:1 | 较简单 |
| 石台阶 (全部) | `#minecraft:slabs`（石质过滤） | **1** | 1 | 1:1 | 半块 |
| 石楼梯 (全部) | `#minecraft:stairs`（石质过滤） | **2** | 1 | 2:1 | 加工品 |
| 墙 (全部) | `#minecraft:walls` | **2** | 1 | 2:1 | 加工品 |
| 石按钮 | 硬编码 | **1** | 2 | 0.5:1 | 小零件 |
| 石压力板 (全部) | 硬编码 | **1** | 1 | 1:1 | 小零件 |
| 模组石材 | Tag 扫描 + 材质判定 | **2** | 1 | 2:1 | 默认标准价 |

**典型场景验证**：
- 挖 10 层矿（~300 stone）→ 300 圆石 / 150 石头 / 100 石砖 ✓
- 建一间 8×8×4 石屋（约 300 石砖）→ 需 900 stone ≈ 挖 30 层矿 ✓
- 对比：Coop 需 100 stone + Crystalarium 需 99 stone，建筑材料价值量级相当 ✓

### 3.3 成本阶梯一览

```
木材加工台 (wood_normal 消耗):
  ┌─ T1 (1 wood) ─── 台阶、栅栏、按钮(×2)、压力板
  ├─ T2 (2 wood) ─── 木板、栅栏门
  ├─ T3 (3 wood) ─── 楼梯、活板门
  └─ T4 (4-5 wood) ─ 原木(5)、去皮原木(5)、木块(5)、门(4)

石材加工台 (stone 消耗):
  ┌─ T1 (1 stone) ── 圆石、苔石、石台阶、石按钮(×2)、压力板
  ├─ T2 (2 stone) ── 石头、安山岩系、砂岩系、凝灰岩、泥砖、楼梯、墙
  ├─ T3 (3 stone) ── 石砖系、平滑石、磨制系、深板岩、砖块、切制砂岩
  ├─ T4 (4 stone) ── 深板岩砖、深板岩瓦、雕纹石砖
  └─ T5 (5 stone) ── 雕纹深板岩
```

---

## 四、其他模组适配策略

### 4.1 木材加工台

**自动适配**：基于 Minecraft 标准 Tag 扫描。只要模组正确标注了以下 tag，其方块就会自动出现：
- `#minecraft:logs`, `#minecraft:planks`, `#minecraft:wooden_slabs`, `#minecraft:wooden_stairs`
- `#minecraft:wooden_fences`, `#minecraft:fence_gates`, `#minecraft:wooden_doors`, `#minecraft:wooden_trapdoors`
- `#minecraft:wooden_buttons`, `#minecraft:wooden_pressure_plates`

**成本规则**：其他模组物品的成本按所属 tag 类别自动归入对应 Tier。

### 4.2 石材加工台

**自动适配规则**：
1. 方块在 `#minecraft:walls` tag → 自动加入，cost = 2
2. 方块在 `#minecraft:slabs` tag 且材质为 `STONE`/`HEAVY_METAL` → 自动加入，cost = 1
3. 方块在 `#minecraft:stairs` tag 且材质为石质 → 自动加入，cost = 2
4. 方块的 `BlockBehaviour.Properties.mapColor` 与石头相关 + 材质为石质 → 纳入默认 cost = 2

**手动扩展**：通过 JSON 配置文件添加 `extra_items` 或 `blacklist`，服务器管理员/整合包作者可自由调整。

### 4.3 JSON 配置（数据驱动）

路径：`config/stardewcraft/workbench_wood.json` 和 `config/stardewcraft/workbench_stone.json`

```json
{
  "input_item": "stardewcraft:wood_normal",
  "bonus_input_item": "stardewcraft:wood_hard",
  "bonus_multiplier": 5,
  "categories": [
    {
      "name": "logs",
      "display_name": "原木",
      "scan_tag": "minecraft:logs",
      "cost_per_item": 5,
      "output_per_craft": 1,
      "filter": "exclude_stripped"
    },
    {
      "name": "planks",
      "display_name": "木板",
      "scan_tag": "minecraft:planks",
      "cost_per_item": 2,
      "output_per_craft": 1
    }
  ],
  "extra_items": [
    { "id": "somemod:custom_planks", "cost": 2, "category": "planks" }
  ],
  "blacklist": [
    "minecraft:bamboo_block"
  ]
}
```

---

## 五、GUI 设计

### 5.1 整体布局

采用 **纯 Screen 模式**（同 ShopScreen / GeodeMenuScreen），不使用 MC Container 系统。
通过网络包与服务端交互。

```
┌─────────────────────────────────────────────────────────┐
│  [图标] 木材加工台                     木材: 🪵 ×523    │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  [全部] [原木] [木板] [台阶] [楼梯] [栅栏] [门]  ← Tab │
│  ─────────────────────────────────────────────────────  │
│                                                         │
│  ┌────────────────────────────┐  ┌───────────────────┐  │
│  │ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ │  │                   │  │
│  │ │🪵│ │🪵│ │🪵│ │🪵│ │🪵│ │  │    ┌─────────┐    │  │
│  │ └──┘ └──┘ └──┘ └──┘ └──┘ │  │    │         │    │  │
│  │ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ │  │    │  4× 大  │    │  │
│  │ │🪵│ │🪵│ │🪵│ │🪵│ │🪵│ │  │    │  预览图  │    │  │
│  │ └──┘ └──┘ └──┘ └──┘ └──┘ │  │    │         │    │  │
│  │ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ │  │    └─────────┘    │  │
│  │ │🪵│ │🪵│ │🪵│ │🪵│ │🪵│ │  │                   │  │
│  │ └──┘ └──┘ └──┘ └──┘ └──┘ │  │   橡木原木         │  │
│  │ ┌──┐ ┌──┐ ┌──┐ ┌──┐ ┌──┐ │  │   minecraft        │  │
│  │ │🪵│ │🪵│ │🪵│ │  │ │  │ │  │                   │  │
│  │ └──┘ └──┘ └──┘ └──┘ └──┘ │  │   消耗: 木材 ×5    │  │
│  │                            │  │   产出: ×1         │  │
│  │       ◀  1 / 3  ▶         │  │                   │  │
│  └────────────────────────────┘  │  ┌─────────────┐  │  │
│                                   │  │  ▶ 制 作 ◀  │  │  │
│                                   │  └─────────────┘  │  │
│                                   └───────────────────┘  │
│                                                         │
│  ┌─────────────────────────────────────────────────────┐│
│  │  玩家背包 (SDV 风格 9×4 格子)                        ││
│  └─────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────┘
```

### 5.2 布局参数 (SDV sprite px)

| 元素 | SDV 宽 | SDV 高 | 备注 |
|------|--------|--------|------|
| 整体面板 | 1080 | 860 | 含边框 |
| 分类 Tab 栏 | 1000 | 48 | 顶部分类切换 |
| 物品网格 (左) | 580 | 440 | 5 列 × 4 行，格子 64×64，间距 16 |
| 预览面板 (右) | 340 | 440 | 物品大图 + 名称 + 成本 |
| 翻页区 | 580 | 40 | 页码 + 左右箭头 |
| 制作按钮 | 200 | 60 | 在预览面板底部 |
| 背包区 | 1000 | 280 | 9×4 SDV 格子 |

### 5.3 交互设计

| 操作 | 行为 |
|------|------|
| 左键点击物品格 | 选中该产出物，右侧显示预览 |
| 左键点击「制作」 | 制作 ×1 |
| 右键点击「制作」 | 制作 ×5 |
| Shift + 左键点击「制作」 | 制作最大数量（材料够多少做多少） |
| 鼠标悬停物品格 | 显示 tooltip（物品名 + 来源模组 + 成本） |
| 悬停物品格 | 格子轻微放大 + 高亮边框（同 CookingPotScreen） |
| 点击分类 Tab | 筛选对应分类 |
| 点击翻页箭头 | 翻页 |
| 鼠标滚轮 | 翻页 |

### 5.4 视觉风格

- 背景遮罩：`0xD8111116`（同 CookingPotScreen 暗色覆盖）
- 边框：`StardewGuiUtil.drawTextureBox()` 9-slice SDV 木质边框
- 格子背景：`vanilla_inventory_slot_tile10.png`（暖黄色格子）
- 选中高亮：cursors.png 中的黄色选框精灵
- Tab 样式：cursors.png 中的标签页精灵
- 制作按钮：SDV 风格绿色按钮（可从 cursors.png 取）
- 材料不足时：制作按钮灰色 + 成本数字显示红色
- 制作成功：播放 `coin` 音效 + 产出物品闪光动画

---

## 六、方块设计

### 6.1 基础属性

| 属性 | 木材加工台 `wood_workbench` | 石材加工台 `stone_workbench` |
|------|---------------------------|---------------------------|
| 材质 | WOOD | STONE |
| 硬度 | 2.5 | 3.5 |
| 挖掘工具 | 斧头 | 镐 |
| 碰撞箱 | 标准方块 | 标准方块 |
| 方块状态 | FACING (水平朝向) | FACING (水平朝向) |
| 交互 | 右键打开 GUI | 右键打开 GUI |
| 掉落物 | 自身 | 自身 |

### 6.2 获取方式

| 途径 | 木材加工台 | 石材加工台 |
|------|----------|----------|
| Robin 商店 | 500g | 500g |
| 合成配方 | 50 wood_normal + 2 iron_bar | 50 stone + 2 iron_bar |
| 初始赠送 | 否 | 否 |

### 6.3 模型/贴图（需美术制作）

- `textures/block/wood_workbench_top.png` — 俯视图，锯子+木屑纹理
- `textures/block/wood_workbench_front.png` — 正面，抽屉+工具架
- `textures/block/wood_workbench_side.png` — 侧面，木质桌腿
- `textures/block/stone_workbench_top.png` — 俯视图，凿子+石粉纹理
- `textures/block/stone_workbench_front.png` — 正面，石质台面
- `textures/block/stone_workbench_side.png` — 侧面，石柱桌腿

---

## 七、网络层

### 7.1 Payload 定义

| Payload | 方向 | 内容 |
|---------|------|------|
| `OpenWorkbenchPayload` | S→C | `type` (wood/stone) + `materialCount` (背包中材料数) + `bonusMaterialCount` (硬木数，仅木材台) |
| `WorkbenchCraftPayload` | C→S | `type` + `targetItemId` (ResourceLocation) + `count` (制作数量) + `useBonusMaterial` (是否优先用硬木) |
| `WorkbenchCraftResultPayload` | S→C | `success` + `remainingMaterial` + `remainingBonus` + `craftedItem` (ItemStack) + `craftedCount` |

### 7.2 服务端验证

`WorkbenchCraftPayload` 处理时必须校验：
1. 玩家距离方块 ≤ 5 格
2. 目标物品在合法产出列表中（不接受客户端伪造的任意物品 ID）
3. 背包中材料数量 ≥ 所需消耗
4. 产出物品能放入背包（背包满则拒绝）
5. 制作数量 > 0 且 ≤ 999

---

## 八、数据管理

### 8.1 WorkbenchRecipeManager

- **职责**：加载 JSON 配置 + Tag 扫描 + 缓存产出列表
- **生命周期**：服务器启动时初始化，`TagsUpdatedEvent` 时刷新
- **缓存**：`Map<WorkbenchType, List<WorkbenchEntry>>` — 每个条目含 itemId、category、cost、outputCount
- **排序**：MC 原版 (minecraft) 排前面 → 本模组 (stardewcraft) → 其他模组按 namespace 字母序

### 8.2 WorkbenchEntry

```java
record WorkbenchEntry(
    ResourceLocation itemId,    // 产出物品 ID
    String category,            // 分类 key (logs/planks/slabs...)
    int cost,                   // 消耗材料数
    int outputCount,            // 产出数量 (通常 1，按钮为 2)
    String namespace            // 来源模组 namespace
) {}
```

---

## 九、文件清单

### 新建文件

```
// 方块
block/utility/WoodWorkbenchBlock.java
block/utility/StoneWorkbenchBlock.java

// 数据管理
data/WorkbenchRecipeManager.java          — JSON 加载 + tag 扫描 + 产出列表缓存
data/WorkbenchType.java                   — 枚举: WOOD, STONE

// 网络
network/payload/OpenWorkbenchPayload.java
network/payload/WorkbenchCraftPayload.java
network/payload/WorkbenchCraftResultPayload.java

// 客户端 GUI
client/gui/WorkbenchScreen.java           — 木材/石材共用，通过 WorkbenchType 区分

// 配置
config/stardewcraft/workbench_wood.json
config/stardewcraft/workbench_stone.json

// 贴图 (需美术)
textures/block/wood_workbench_top.png
textures/block/wood_workbench_front.png
textures/block/wood_workbench_side.png
textures/block/stone_workbench_top.png
textures/block/stone_workbench_front.png
textures/block/stone_workbench_side.png

// 方块模型
models/block/wood_workbench.json
models/block/stone_workbench.json
models/item/wood_workbench.json
models/item/stone_workbench.json

// 方块状态
blockstates/wood_workbench.json
blockstates/stone_workbench.json

// 掉落表
loot_tables/blocks/wood_workbench.json
loot_tables/blocks/stone_workbench.json

// 语言
更新 en_us.json / zh_cn.json
```

### 需修改的文件

```
ModBlocks.java          — 注册 WOOD_WORKBENCH, STONE_WORKBENCH
ModItems.java           — 注册对应 BlockItem
ModClientSetup.java     — 无（纯 Screen 模式不需要注册 MenuType）
PayloadRegistry.java    — 注册 3 个新 Payload
ShopRegistry.java       — Robin 商店添加两个加工台
StardewCraftingRecipeData.java — 添加合成配方（可选）
```

---

## 十、实施顺序

1. **Phase 1 — 数据层**：WorkbenchType 枚举 + WorkbenchRecipeManager + JSON 配置
2. **Phase 2 — 方块层**：WoodWorkbenchBlock + StoneWorkbenchBlock + 注册 + 临时贴图
3. **Phase 3 — 网络层**：3 个 Payload + 服务端制作逻辑
4. **Phase 4 — GUI 层**：WorkbenchScreen 完整实现
5. **Phase 5 — 集成**：Robin 商店添加 + 合成配方 + 语言文件
6. **Phase 6 — 美术**：替换正式贴图和模型
