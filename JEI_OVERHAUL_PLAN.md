# JEI 全面重构计划 — StardewCraft

> **总目标**：让 JEI 成为玩家的百科全书。每一个物品都能在 JEI 中查到"怎么获得、能做什么、在哪买、值多少钱"。
> 美术风格对标 SDV 原版 UI——温暖的木框面板、NPC肖像、彩色季节图标、像素箭头，让人一看就是星露谷。

---

## 目录

1. [现有问题清单](#1-现有问题清单)
2. [美术资源盘点](#2-美术资源盘点)
3. [JEI 分类总览（共 12 个）](#3-jei-分类总览)
4. [每个分类详细设计](#4-每个分类详细设计)
5. [全局功能改进](#5-全局功能改进)
6. [翻译键规划](#6-翻译键规划)
7. [实施路线图](#7-实施路线图)

---

## 1. 现有问题清单

### 1.1 硬编码英文字符串（必须全部消灭）

| 文件 | 问题 | 修复 |
|------|------|------|
| `FishingInfoCategory.java` | `getLocationDescription()` 硬编码 "Beach"/"Ocean"/"River" 等 15 个位置名 | 改为翻译键 `stardewcraft.jei.location.beach` 等 |
| `FishingInfoCategory.java` | `getTimeDescription()` 硬编码 "All Day" | 改为 `stardewcraft.jei.time.all_day` |
| `GeodeProcessingCategory.java` | 晶球名硬编码 "Geode"/"Frozen Geode"/"Magma Geode"/"Omni Geode" | 改为物品自身 `getName()` 或翻译键 |
| `ShopInfoCategory.java` | 商店名直接使用 `def.ownerNpcId()` 即英文 NPC 名 ("Pierre"/"Willy") | 改为翻译键 `stardewcraft.npc.name.pierre` 等 |
| `ShopInfoCategory.java` | `"(recipe)"` 硬编码 | 改为 `stardewcraft.jei.shop.recipe_tag` |
| `ShopInfoCategory.java` | `"g"` 后缀硬编码 | 改为 `stardewcraft.jei.gold_suffix` |
| `JeiDrawHelper.java` | 季节缩写 "Sp"/"Su"/"Fa"/"Wi" 硬编码 | 改为翻译键 `stardewcraft.jei.season.spring.abbr` 等 |
| `JeiDrawHelper.java` | 时间单位 "d"/"h"/"m" 硬编码 | 改为翻译键 `stardewcraft.jei.time.day`/`hour`/`minute` |

### 1.2 覆盖缺失

- 保存品（果冻/腌菜/鱼子酱/干果/干蘑菇） — 有子类型但无制作过程展示
- 熏鱼 — 同上
- 目标鱼饵 — 同上
- 烹饪配方 — 完全缺失
- 回收机 — 完全缺失
- 蜂房 — 完全缺失
- 树液采集器 — 完全缺失
- 蟹笼 — 完全缺失
- 木桶陈酿 — 完全缺失
- 蠕虫箱/避雷针/诱饵制作机 — 完全缺失
- 怪物掉落 — 完全缺失（但无怪物贴图，降级为文字）

### 1.3 美术太简陋

- ShopInfoCategory 没有 NPC 肖像 — 有 101 张 NPC 肖像 PNG 完全没用
- 所有分类仅用代码绘制的简单面板，没有使用 cursors.png 中丰富的 UI 元素
- 缺少视觉层次感——所有分类长得一样

---

## 2. 美术资源盘点

### 2.1 NPC 肖像（`textures/portraits/`）— 101 张 PNG

所有商店老板均有肖像可用于 JEI：

| 商店 | 老板 | 肖像文件 | 肖像尺寸 |
|------|------|----------|----------|
| 种子店（Pierre） | Pierre | `pierre.png` | 128×192（3行表情，取左上64×64） |
| 鱼店（Willy） | Willy | `willy.png` | 128×128（2行表情，取左上64×64） |
| 动物店（Marnie） | Marnie | `marnie.png` | 128×192 |
| 绿洲（Sandy） | Sandy | `sandy.png` | 128×128 |
| 铁匠铺（Clint） | Clint | `clint.png` | 128×128 |
| 酒吧（Gus） | Gus | `gus.png` | 128×128 |
| 诊所（Harvey） | Harvey | `harvey.png` | 128×128 |
| 冒险家（Marlon） | Marlon | `marlon.png` | 128×128 |
| 木匠店（Robin） | Robin | `robin.png` | 128×128 |

> **肖像裁剪方案**：每张肖像是 2×N 表情网格（每格 64×64 像素）。JEI 中使用 **左上角第一格**（0,0 → 64×64）作为默认表情，缩放到 28×28 或 32×32 显示。

### 2.2 SDV 大精灵表（`textures/gui/cursors.png`）— 704×2256

从 cursors.png 中可以裁剪使用的 UI 元素（坐标参考 SDV 源码 `Cursors` 常量）：

| UI 元素 | 位置（大致） | 用途 |
|---------|------------|------|
| 季节图标（花/太阳/叶/雪） | 406,441 ~ 16×16 每个 | 替代季节圆点 |
| 天气图标（晴/雨/雷/雪） | ~325,421 区域 | 钓鱼天气展示 |
| 品质星星（银/金/铱） | ~338,400 区域 | 木桶陈酿品质展示 |
| 时钟图标 | 约 434,475 | 处理时间展示 |
| 金币堆 | 约 280,411 | 替代 gold_icon.png |
| 心形（红/灰） | ~211,428 区域 | 友谊/礼物展示 |
| 技能图标 | ~10,428 区域 | 职业解锁条件 |
| 箭头（各种方向） | ~12,494 区域 | 进度指示 |
| 书本/卷轴图标 | 各处 | 配方来源 |
| 装饰性边角花纹 | ~403,373 区域 | 面板装饰 |

### 2.3 其他可用纹理

| 纹理 | 路径 | 用途 |
|------|------|------|
| 金币图标 | `textures/gui/gold_icon.png` (16×16) | 价格显示（已使用） |
| 能量图标 | `textures/gui/energy.png` (16×16) | 食物能量显示 |
| 生命图标 | `textures/gui/health.png` (16×16) | 食物生命显示 |
| 空槽位框 | `textures/gui/jei/empty_slot.png` (29×29) | 物品框装饰（已使用） |
| 钓鱼背景 | `textures/gui/jei/fishing_info.png` (160×100) | 钓鱼分类背景（TODO: 启用） |

---

## 3. JEI 分类总览

### 保留 & 改进（5 个现有分类 → 拆分后 17 个）

| # | 分类 ID | 名称 | 改进内容 |
|---|---------|------|----------|
| 1 | `fishing_info` | 钓鱼信息 | 消除硬编码; 难度星号可视化 |
| 2 | ~~`artisan_recipe`~~ | ~~机器加工~~ | **拆分为 13 个独立分类（每台机器一个）** |
| 2a | `machine/keg` | 酿酒桶 | 独立分类, 含动态 CROP_TYPE 展开 |
| 2b | `machine/preserves_jar` | 腌菜瓶 | 独立分类, 含动态 CROP_TYPE 展开 |
| 2c | `machine/cheese_press` | 压酪机 | 独立分类 |
| 2d | `machine/mayonnaise_machine` | 蛋黄酱机 | 独立分类 |
| 2e | `machine/furnace` | 熔炉 | 独立分类 |
| 2f | `machine/crystalarium` | 宝石复制机 | 独立分类 |
| 2g | `machine/fish_smoker` | 鱼烟熏器 | 独立分类, 含动态 FISH_TYPE 展开 |
| 2h | `machine/dehydrator` | 脱水机 | 独立分类, 含动态展开 |
| 2i | `machine/oil_maker` | 榨油机 | 独立分类 |
| 2j | `machine/loom` | 织布机 | 独立分类 |
| 2k | `machine/charcoal_kiln` | 烧炭窑 | 独立分类 |
| 2l | `machine/seed_maker` | 种子制造机 | 独立分类 |
| 2m | `machine/incubator` | 孵化器 | 独立分类 |
| 3 | `shop_info` | 商店信息 | **加入 NPC 肖像**; NPC名翻译; 去除硬编码 |
| 4 | `geode_processing` | 晶球加工 | 晶球名改用物品翻译名 |
| 5 | `stardew_crafting` | 星露谷制作 | 无重大改动 |

### 新增分类（7 个）

| # | 分类 ID | 名称 | 优先级 |
|---|---------|------|--------|
| 6 | `cooking_recipe` | 烹饪 | P0 |
| 7 | `recycling` | 回收机 | P0 |
| 8 | `bee_house` | 蜂房产蜜 | P1 |
| 9 | `tapper` | 树液采集 | P1 |
| 10 | `crab_pot` | 蟹笼捕获 | P1 |
| 11 | `cask_aging` | 木桶陈酿 | P1 |
| 12 | `monster_drop` | 怪物掉落 | P2 |

---

## 4. 每个分类详细设计

---

### 4.1 `fishing_info` — 钓鱼信息（改进）

**当前状态**：已实现，有 2x 放大鱼图标 + 文字信息。

**改进项**：

```
┌─[SDV 暖色面板]──────────────────────────┐
│                                          │
│  ╔═══════╗   难度: ★★★☆☆ 中等 (45)       │
│  ║       ║   行为: 冲刺                   │
│  ║  🐟   ║   📍 海滩 / 河流               │
│  ║  2x   ║   🕐 6:00 - 19:00            │
│  ╚═══════╝   🌸🌻🍂 春/夏/秋             │
│              ☀️ 晴天                      │
│              📊 钓鱼等级 ≥ 3              │
│ ┌──────┐                                 │
│ │ 🎣   │ 催化剂                           │
│ └──────┘                                 │
└──────────────────────────────────────────┘
```

**具体改动**：
1. `getLocationDescription()` 中 15 个位置全部改为翻译键
2. `getTimeDescription()` 的 "All Day" 改为翻译键
3. 从 `cursors.png` 裁剪**季节小图标**（花/太阳/枫叶/雪花，各约 16×16），替代纯文本季节名
4. 从 `cursors.png` 裁剪**天气图标**（☀/🌧），替代纯文本天气名
5. 难度可视化：用 ★ 符号 + 颜色梯度（绿→黄→橙→红→紫）表示难度等级
6. 可选：使用 `fishing_info.png` 作为背景（目前已存在但未启用）

---

### 4.2 `machine/*` — 机器加工（重大重构：拆分为独立分类）

**原问题**：所有 13 台机器的配方全部混在一个 `artisan_recipe` 分类中，玩家点击任意机器都会看到全部配方翻一大堆。这在 JEI 中非常反直觉——正确做法是每台机器有自己独立的 JEI 分类。

**重构方案**：`ArtisanRecipeCategory` 改为工厂模式，为每台机器动态创建独立的 `RecipeType` 和分类实例。

**架构设计**：

```java
// 每台机器一个 RecipeType — 通过 machineKey 动态创建
// RecipeType.create("stardewcraft", "machine/keg", DisplayRecipe.class)
// RecipeType.create("stardewcraft", "machine/preserves_jar", DisplayRecipe.class)
// ...

// ArtisanRecipeCategory 支持多实例化：
// new ArtisanRecipeCategory(guiHelper, "keg", kegIcon, kegRecipeType)
// new ArtisanRecipeCategory(guiHelper, "preserves_jar", jarIcon, jarRecipeType)
```

**13 台机器 × 独立分类**：

| 机器 | 分类标题翻译键 | 催化剂 | 配方来源 |
|------|--------------|--------|---------|
| keg | `stardewcraft.jei.machine.keg` | `stardewcraft:keg` | keg.json + CROP_TYPE 展开 |
| preserves_jar | `stardewcraft.jei.machine.preserves_jar` | `stardewcraft:preserves_jar` | preserves_jar.json + CROP_TYPE 展开 |
| cheese_press | `stardewcraft.jei.machine.cheese_press` | `stardewcraft:cheese_press` | cheese_press.json |
| mayonnaise_machine | `stardewcraft.jei.machine.mayonnaise_machine` | `stardewcraft:mayonnaise_machine` | mayonnaise_machine.json |
| furnace | `stardewcraft.jei.machine.furnace` | `stardewcraft:furnace` | furnace.json |
| crystalarium | `stardewcraft.jei.machine.crystalarium` | `stardewcraft:crystalarium` | crystalarium.json |
| fish_smoker | `stardewcraft.jei.machine.fish_smoker` | `stardewcraft:fish_smoker` | fish_smoker.json + FISH_TYPE 展开 |
| dehydrator | `stardewcraft.jei.machine.dehydrator` | `stardewcraft:dehydrator` | dehydrator.json + 动态展开 |
| oil_maker | `stardewcraft.jei.machine.oil_maker` | `stardewcraft:oil_maker` | oil_maker.json |
| loom | `stardewcraft.jei.machine.loom` | `stardewcraft:loom` | loom.json |
| charcoal_kiln | `stardewcraft.jei.machine.charcoal_kiln` | `stardewcraft:charcoal_kiln` | charcoal_kiln.json |
| seed_maker | `stardewcraft.jei.machine.seed_maker` | `stardewcraft:seed_maker` | seed_maker.json |
| incubator | `stardewcraft.jei.machine.incubator` | `stardewcraft:incubator` | incubator.json |

**注册流程**（StardewJeiPlugin 中）：
```
for each machineKey in getAllMachineKeys():
  1. 创建 RecipeType<DisplayRecipe>("stardewcraft", "machine/" + machineKey)
  2. 创建 ArtisanRecipeCategory 实例(guiHelper, machineKey, icon, recipeType)
  3. registration.addCategory(category)
  4. registration.addRecipes(recipeType, buildRecipesForMachine(machineKey))
  5. registration.addRecipeCatalyst(machineStack, recipeType)
```

**视觉不变**：每个分类的布局仍然是 `[输入] → [机器图标] → [输出] + 时间`，只是现在每个分类只显示该机器自己的配方。

---

### 4.3 `shop_info` — 商店信息（重大改进）

**当前问题**：没有 NPC 肖像，纯文字，商店名硬编码英文。

**新设计（大改）**：

```
┌─[SDV 暖色面板 166×52]──────────────────┐
│ ┌──────┐                                │
│ │      │  Pierre 的种子店                │
│ │ 肖像 │  ──────────────────             │
│ │ 28×28│  [物品槽]  🪙 120g  [×10]      │
│ └──────┘  🟢🟡🟠 春/夏/秋   Y2+        │
└─────────────────────────────────────────┘
```

**具体改动**：

1. **NPC 肖像显示**：
   - 从 `textures/portraits/{npcId}.png` 裁剪左上角 64×64 区域（第一个表情）
   - 缩放到 **28×28** 显示在面板左侧
   - 每个商店 ID 对应一个 NPC — 通过 `ownerNpcId` 匹配文件名（全小写）
   - 在 JEI 注册时预加载 9 个肖像 IDrawable（缓存在 Map 中）

2. **商店名翻译**：
   - `def.ownerNpcId()` → 翻译键 `stardewcraft.jei.shop.name.{shopId}`
   - 例：`stardewcraft.jei.shop.name.SeedShop` → "皮埃尔的种子店" / "Pierre's Seed Shop"

3. **价格显示改进**：
   - Trade 物品显示：用 trade 物品图标 + 数量替代纯金币
   - Recipe 标签用翻译键

4. **GUI 尺寸**：增大到 `166×52`，给肖像留空间

---

### 4.4 `geode_processing` — 晶球加工（小改）

**改动**：
- 晶球名改用 `recipe.geode().getHoverName().getString()` 获取物品本地化名称
- 价格用翻译键包裹

---

### 4.5 `stardew_crafting` — 星露谷制作（小改）

**改动**：
- 解锁条件文字用翻译键
- 无其他改动

---

### 4.6 `cooking_recipe` — 烹饪配方（新增 P0）

星露谷的烹饪系统是核心玩法之一，必须有 JEI 展示。

**设计**：

```
┌─[SDV 暖色面板 166×60]──────────────────┐
│  [标题: 菜名]                           │
│  ┌──┐┌──┐┌──┐┌──┐   →   ╔════╗        │
│  │材│││材│││材│││材│       ║产出║        │
│  │料│││料│││料│││料│       ║金光║        │
│  └──┘└──┘└──┘└──┘       ╚════╝        │
│  ❤12  ⚡30       🪙 200g  来源: Gus   │
└─────────────────────────────────────────┘
```

**数据源**：`VanillaCookingRecipeData`

**DisplayRecipe record**：
```java
record DisplayRecipe(
    String recipeId,
    List<ItemStack> ingredients,  // 最多 4 个材料
    ItemStack output,
    int energy,       // 从 IStardewItem.getEnergy()
    int health,       // 从 IStardewItem.getHealth()
    int sellPrice,    // 从 IStardewItem.getSellPrice()
    String source     // 解锁来源
)
```

**特殊处理**：
- Token `-4`（任何鱼）→ 显示自定义 "Any Fish" 占位物品或多物品轮播
- Token `-5`（任何蛋）→ 显示鸡蛋 + 鸭蛋 + 恐龙蛋等
- Token `-6`（任何奶）→ 显示牛奶 + 羊奶
- 能量/生命值用 `energy.png` / `health.png` 图标 + 数值显示
- 售价用金币图标

**催化剂**：`stardewcraft:cooking_pot`

---

### 4.7 `recycling` — 回收机（新增 P0）

**设计**：

```
┌─[SDV 暖色面板 160×44]────────────────┐
│  [垃圾] → [回收机] → [产出]  ⏱ 1h    │
│                        30%           │
└──────────────────────────────────────┘
```

**数据**（硬编码映射）：

| 输入 | 输出 | 概率 | 数量 |
|------|------|------|------|
| Trash | Coal | 30% | 1-3 |
| Trash | Iron Ore | 30% | 1-3 |
| Trash | Stone | 40% | 1-3 |
| Driftwood | Coal | 25% | 1-3 |
| Driftwood | Wood | 75% | 1-3 |
| Broken Glasses | Refined Quartz | 100% | 1 |
| Broken CD | Refined Quartz | 100% | 1 |
| Soggy Newspaper | Cloth | 10% | 1 |
| Soggy Newspaper | Torch | 90% | 3 |

**展示方式**：类似 ArtisanRecipeCategory，三槽位 + 箭头。在输出物品下方/右侧显示百分比概率。

**DisplayRecipe record**：
```java
record DisplayRecipe(
    ItemStack input,
    ItemStack output,
    int outputMinCount,
    int outputMaxCount,
    int chancePercent,  // 100=确定，30=30%
    int minutes
)
```

**催化剂**：`stardewcraft:recycling_machine`

---

### 4.8 `bee_house` — 蜂房产蜜（新增 P1）

**设计（重点美观）**：

```
┌─[SDV 暖色面板 166×48]──────────────────┐
│  🌻 向日葵蜂蜜                          │
│  [花方块] + [蜂房] → [蜂蜜]            │
│             4天      🪙 260g            │
│  ☀ 春夏秋可产蜜（冬季停产）              │
└─────────────────────────────────────────┘
```

**数据**：7 条规则（6种花 + 野花蜂蜜）

| 花 | 蜂蜜价值 | 采集范围 |
|----|---------|---------|
| 仙子玫瑰 | 680g | 5格 |
| 罂粟 | 380g | 5格 |
| 夏之嫣 | 280g | 5格 |
| 向日葵 | 260g | 5格 |
| 蓝色爵士乐 | 200g | 5格 |
| 郁金香 | 160g | 5格 |
| 无花（野花） | 100g | — |

**DisplayRecipe record**：
```java
record DisplayRecipe(
    ItemStack flower,       // 花方块/物品（无花时为空气/蜂房）
    ItemStack beeHouse,
    ItemStack honey,
    int value,
    int cycleDays
)
```

**催化剂**：`stardewcraft:bee_house`

---

### 4.9 `tapper` — 树液采集（新增 P1）

**设计**：

```
┌─[SDV 暖色面板 160×44]────────────────┐
│  [橡树原木] → [采集器] → [橡木树脂]   │
│                 7天                   │
└──────────────────────────────────────┘
```

**数据**：5 条规则

| 树 | 产物 | 天数 | 数量 |
|----|------|------|------|
| 橡树 | 橡木树脂 | 7天 | 1 |
| 枫树 | 枫糖浆 | 9天 | 1 |
| 松树 | 松焦油 | 5天 | 1 |
| 桃花心木 | 树液 | 1天 | 3-8 |
| 神秘树 | 神秘糖浆 | 7天 | 1 |

**展示**：复用 ArtisanRecipeCategory 的三槽位布局。用树的原木方块作为输入物品。

**催化剂**：`stardewcraft:tapper`

---

### 4.10 `crab_pot` — 蟹笼捕获（新增 P1）

**设计**：

```
┌─[SDV 暖色面板 166×48]──────────────────┐
│  蟹笼捕获                               │
│  [鱼饵] + [蟹笼] → [捕获物]            │
│  📍 海洋 / 淡水                         │
│  垃圾概率: 20%                          │
└─────────────────────────────────────────┘
```

**数据源**：`stardewcraft:crab_pot_items` 标签 + biome 过滤

**DisplayRecipe record**：
```java
record DisplayRecipe(
    ItemStack bait,         // 鱼饵类型（普通/高级/野生）
    ItemStack crabPot,
    ItemStack catch,        // 捕获物
    String waterType,       // "ocean"/"freshwater"
    int trashChancePercent
)
```

展示每种蟹笼可捕获的物品（从 tag 遍历），标注水域类型。

**催化剂**：`stardewcraft:crab_pot`

---

### 4.11 `cask_aging` — 木桶陈酿（新增 P1）

**设计（重点美观——品质渐变）**：

```
┌─[SDV 暖色面板 166×50]──────────────────┐
│  木桶陈酿                               │
│  [物品☆]  →  [木桶]  →  [物品★★★]     │
│                                         │
│  ☆→☆☆  14d │ ☆☆→★  14d │ ★→★★★ 14d  │
│  总计: 42 天 （速率 4.0x）               │
└─────────────────────────────────────────┘
```

**数据**：16 种可陈酿物品

**品质 UI**：从 cursors.png 裁剪品质星星图标（银星、金星、铱星），或用彩色 ★ 字符：
- ☆ 普通（白色）
- ★ 银色（#C0C0C0）
- ★ 金色（#FFD700）
- ★ 铱色（#B044DD）

展示方式：输入为普通品质物品 → 输出为铱星品质物品。下方用进度条或阶梯图展示品质升级过程和所需天数。

**DisplayRecipe record**：
```java
record DisplayRecipe(
    ItemStack input,        // 普通品质
    ItemStack output,       // 铱星品质
    double agingRate,
    int daysToSilver,
    int daysToGold,
    int daysToIridium
)
```

**催化剂**：`stardewcraft:cask`

---

### 4.12 `monster_drop` — 怪物掉落（新增 P2）

**设计**：

```
┌─[SDV 暖色面板 166×56]──────────────────┐
│  史莱姆 (Slime)              Lv 1-39   │
│  ─────────────────────────────────────  │
│  [史莱姆] 75%  [树液] 15%  [蛋] 0.9%   │
│  [紫水晶] 1.5%(T1)  [太阳精华] 25%(T3) │
│  [矮人卷轴I] 0.5%                      │
└─────────────────────────────────────────┘
```

**数据源**：`MineMonsterDropHandler`（17 种怪物，硬编码掉落表）

> ⚠ 没有怪物贴图（`textures/entity/` 下无怪物 PNG），所以不能显示怪物图标。
> **替代方案**：用代表性掉落物作为 icon（如 slime 用 slime_ball，bat 用 bat_wing），怪物名用翻译文字。

**DisplayEntry record**：
```java
record DisplayEntry(
    String monsterId,
    String monsterName,     // 翻译键
    ItemStack iconDrop,     // 代表性掉落物作图标
    List<DropInfo> drops,
    String floorRange       // "1-39" / "40-79" 等
)
record DropInfo(ItemStack item, double chance, String tier)
```

**催化剂**：`stardewcraft:adventurer_sword` 或某把剑

---

## 5. 全局功能改进

### 5.1 JeiDrawHelper 增强

**新增方法**：

```java
// NPC 肖像绘制（从 portraits/{npcId}.png 裁剪 64×64 → 缩放到 size×size）
void drawNpcPortrait(GuiGraphics gg, String npcId, int x, int y, int size)

// 概率文本（带颜色编码：100%绿色, 50%黄色, 10%橙色, 1%红色）
void drawChanceText(GuiGraphics gg, Font font, int x, int y, int chancePercent)

// 品质星星图标
void drawQualityStar(GuiGraphics gg, int x, int y, int quality)

// 能量/生命图标+数值
void drawEnergyHealth(GuiGraphics gg, Font font, int x, int y, int energy, int health)

// 从 cursors.png 裁剪的季节图标（替代圆点）
void drawSeasonIcon(GuiGraphics gg, int x, int y, int season)

// 从 cursors.png 裁剪的天气图标
void drawWeatherIcon(GuiGraphics gg, int x, int y, String weather)
```

### 5.2 NPC 肖像缓存系统

```java
// JeiPortraitCache — 在 registerCategories 时预加载所有需要的肖像
class JeiPortraitCache {
    private static final Map<String, IDrawable> PORTRAITS = new HashMap<>();

    // 从 portraits/{npcId}.png 裁剪左上 64×64 构建 IDrawable
    static void preload(IGuiHelper guiHelper, String... npcIds)
    static IDrawable get(String npcId)
}
```

### 5.3 物品信息 Tooltip 增强

在 `registerRecipes` 中使用 `registration.addItemStackInfo()` 为以下物品添加 JEI 描述（右键物品 → 显示"如何获得"）：

- 所有矿物/宝石：标注哪些晶球可以开出
- 所有树液/树脂：标注由什么树产出
- 蜂蜜变体：标注需要什么花
- 所有怪物掉落物：标注由什么怪物掉落
- 博物馆捐赠品：标注"可捐赠给博物馆"

---

## 6. 翻译键规划

### 6.1 分类标题（12 个）

| 键 | en_us | zh_cn |
|----|-------|-------|
| `stardewcraft.jei.fishing_info` | Fishing Info | 钓鱼信息 |
| `stardewcraft.jei.artisan_recipe` | Machine Processing | 机器加工 |
| `stardewcraft.jei.shop_info` | Shops | 商店 |
| `stardewcraft.jei.geode_processing` | Geode Processing | 晶球加工 |
| `stardewcraft.jei.stardew_crafting` | Stardew Crafting | 星露谷制作 |
| `stardewcraft.jei.cooking_recipe` | Cooking | 烹饪 |
| `stardewcraft.jei.recycling` | Recycling | 回收机 |
| `stardewcraft.jei.bee_house` | Bee House | 蜂房产蜜 |
| `stardewcraft.jei.tapper` | Tree Tapper | 树液采集 |
| `stardewcraft.jei.crab_pot` | Crab Pot | 蟹笼 |
| `stardewcraft.jei.cask_aging` | Cask Aging | 木桶陈酿 |
| `stardewcraft.jei.monster_drop` | Monster Drops | 怪物掉落 |

### 6.2 商店名（9 个）

| 键 | en_us | zh_cn |
|----|-------|-------|
| `stardewcraft.jei.shop.name.SeedShop` | Pierre's Seed Shop | 皮埃尔的种子店 |
| `stardewcraft.jei.shop.name.FishShop` | Willy's Fish Shop | 威利的鱼店 |
| `stardewcraft.jei.shop.name.AnimalShop` | Marnie's Ranch | 玛妮的牧场 |
| `stardewcraft.jei.shop.name.OasisShop` | Sandy's Oasis | 桑迪的绿洲商店 |
| `stardewcraft.jei.shop.name.Blacksmith` | Clint's Blacksmith | 克林特的铁匠铺 |
| `stardewcraft.jei.shop.name.Saloon` | Gus's Saloon | 格斯的酒吧 |
| `stardewcraft.jei.shop.name.Hospital` | Harvey's Clinic | 哈维的诊所 |
| `stardewcraft.jei.shop.name.AdventureShop` | Marlon's Guild | 马龙的冒险家公会 |
| `stardewcraft.jei.shop.name.CarpenterShop` | Robin's Carpenter | 罗宾的木匠店 |

### 6.3 钓鱼位置（~15 个）

| 键 | en_us | zh_cn |
|----|-------|-------|
| `stardewcraft.jei.location.beach` | Beach | 海滩 |
| `stardewcraft.jei.location.ocean` | Ocean | 海洋 |
| `stardewcraft.jei.location.river` | River | 河流 |
| `stardewcraft.jei.location.mountain_lake` | Mountain Lake | 山顶湖泊 |
| `stardewcraft.jei.location.forest_pond` | Forest Pond | 森林池塘 |
| `stardewcraft.jei.location.secret_woods` | Secret Woods | 秘密森林 |
| `stardewcraft.jei.location.sewers` | Sewers | 下水道 |
| `stardewcraft.jei.location.mines` | Mines %s | 矿井 %s |
| `stardewcraft.jei.location.desert` | Desert | 沙漠 |
| `stardewcraft.jei.location.witch_swamp` | Witch Swamp | 女巫沼泽 |
| `stardewcraft.jei.location.night_market` | Night Market | 夜市 |
| `stardewcraft.jei.location.volcano` | Volcano | 火山 |
| `stardewcraft.jei.location.ginger_island` | Ginger Island | 姜岛 |
| `stardewcraft.jei.location.pirate_cove` | Pirate Cove | 海盗湾 |
| `stardewcraft.jei.location.any` | Any Location | 任意地点 |

### 6.4 通用 UI 文本

| 键 | en_us | zh_cn |
|----|-------|-------|
| `stardewcraft.jei.time.all_day` | All Day | 全天 |
| `stardewcraft.jei.time.day_unit` | d | 天 |
| `stardewcraft.jei.time.hour_unit` | h | 小时 |
| `stardewcraft.jei.time.minute_unit` | m | 分钟 |
| `stardewcraft.jei.shop.recipe_tag` | (Recipe) | （配方） |
| `stardewcraft.jei.chance` | %s%% | %s%% |
| `stardewcraft.jei.total_days` | Total: %s days | 共计: %s 天 |
| `stardewcraft.jei.aging_rate` | Rate: %sx | 速率: %sx |
| `stardewcraft.jei.trash_chance` | Trash: %s%% | 垃圾: %s%% |
| `stardewcraft.jei.water_type.ocean` | Ocean | 海洋 |
| `stardewcraft.jei.water_type.freshwater` | Freshwater | 淡水 |
| `stardewcraft.jei.source` | Source: %s | 来源: %s |
| `stardewcraft.jei.energy` | Energy: %s | 能量: %s |
| `stardewcraft.jei.health` | Health: %s | 生命: %s |
| `stardewcraft.jei.winter_inactive` | Inactive in Winter | 冬季停产 |
| `stardewcraft.jei.flower_range` | Flower range: %s blocks | 花朵范围: %s 格 |
| `stardewcraft.jei.quality.normal` | Normal | 普通 |
| `stardewcraft.jei.quality.silver` | Silver | 银星 |
| `stardewcraft.jei.quality.gold` | Gold | 金星 |
| `stardewcraft.jei.quality.iridium` | Iridium | 铱星 |
| `stardewcraft.jei.any_fish` | Any Fish | 任意鱼类 |
| `stardewcraft.jei.any_egg` | Any Egg | 任意蛋 |
| `stardewcraft.jei.any_milk` | Any Milk | 任意奶 |
| `stardewcraft.jei.season.spring.abbr` | Sp | 春 |
| `stardewcraft.jei.season.summer.abbr` | Su | 夏 |
| `stardewcraft.jei.season.fall.abbr` | Fa | 秋 |
| `stardewcraft.jei.season.winter.abbr` | Wi | 冬 |

### 6.5 怪物名称（17 个）

| 键 | en_us | zh_cn |
|----|-------|-------|
| `stardewcraft.jei.monster.slime` | Slime | 史莱姆 |
| `stardewcraft.jei.monster.bat` | Bat | 蝙蝠 |
| `stardewcraft.jei.monster.iridium_bat` | Iridium Bat | 铱蝙蝠 |
| `stardewcraft.jei.monster.fly` | Fly / Cave Fly | 洞穴苍蝇 |
| `stardewcraft.jei.monster.grub` | Grub | 蛆虫 |
| `stardewcraft.jei.monster.bug` | Bug | 虫子 |
| `stardewcraft.jei.monster.dust_sprite` | Dust Sprite | 灰尘精灵 |
| `stardewcraft.jei.monster.skeleton` | Skeleton | 骷髅 |
| `stardewcraft.jei.monster.ghost` | Ghost | 幽灵 |
| `stardewcraft.jei.monster.crab` | Rock Crab | 岩蟹 |
| `stardewcraft.jei.monster.golem` | Wilderness Golem | 荒野魔像 |
| `stardewcraft.jei.monster.shadow` | Shadow Brute | 暗影怪 |
| `stardewcraft.jei.monster.duggy` | Duggy | 地鼠 |
| `stardewcraft.jei.monster.metal_head` | Metal Head | 铁头怪 |
| `stardewcraft.jei.monster.squid_kid` | Squid Kid | 鱿鱼小子 |
| `stardewcraft.jei.monster.mummy` | Mummy | 木乃伊 |
| `stardewcraft.jei.monster.serpent` | Serpent | 飞蛇 |

---

## 7. 实施路线图

### Phase 0 — 基础设施（预计 1 轮会话）

- [ ] a. 全面消除所有硬编码英文字符串（Location/Time/SeasonAbbr/TimeUnit/ShopName/RecipeTag）
- [ ] b. 添加所有 §6 翻译键到 en_us.json 和 zh_cn.json
- [ ] c. `JeiDrawHelper` 增加 `drawNpcPortrait()`、`drawChanceText()`、`drawQualityStar()`、`drawEnergyHealth()` 方法
- [ ] d. 创建 `JeiPortraitCache` 类——NPC 肖像预加载和缓存
- [ ] e. `JeiDrawHelper.formatTime()` 使用翻译键
- [ ] f. 构建验证

### Phase 1 — 现有分类改进（预计 1 轮会话）

- [x] a. **ShopInfoCategory** 大改：加入 NPC 肖像、翻译化商店名、交易物品图标展示
- [x] b. **ArtisanRecipeCategory** 补全动态配方展开（果冻/腌菜/酒/果汁/熏鱼/鱼子/干制品）
- [ ] b2. **ArtisanRecipeCategory 拆分重构**：从单一 `artisan_recipe` 改为 13 个独立 `machine/{key}` 分类，每台机器有自己的 RecipeType、Category 实例、催化剂。ArtisanRecipeCategory 改为工厂模式支持多实例化。
- [x] c. **FishingInfoCategory** 改进：位置/时间翻译键、难度 ★ 可视化
- [x] d. **GeodeProcessingCategory** 改进：晶球名取物品翻译名
- [ ] e. 构建验证

### Phase 2 — P0 新增分类（预计 1 轮会话）

- [ ] a. 创建 **CookingRecipeCategory**（烹饪配方）
- [ ] b. 创建 **RecyclingCategory**（回收机）
- [ ] c. Plugin 注册新分类 + 配方 + 催化剂
- [ ] d. 翻译键补全
- [ ] e. 构建验证

### Phase 3 — P1 新增分类（预计 1 轮会话）

- [ ] a. 创建 **BeeHouseCategory**（蜂房产蜜）
- [ ] b. 创建 **TapperCategory**（树液采集）
- [ ] c. 创建 **CrabPotCategory**（蟹笼捕获）
- [ ] d. 创建 **CaskAgingCategory**（木桶陈酿）
- [ ] e. Plugin 注册 + 催化剂
- [ ] f. 翻译键补全
- [ ] g. 构建验证

### Phase 4 — P2 新增分类 + 物品信息（预计 1 轮会话）

- [ ] a. 创建 **MonsterDropCategory**（怪物掉落）
- [ ] b. 使用 `addItemStackInfo()` 为关键物品添加获取方式描述
- [ ] c. 蠕虫箱/避雷针/诱饵制作机 — 考虑作为简易 tooltip 添加而非独立分类
- [ ] d. 全面测试所有 12 个分类
- [ ] e. 最终构建验证

---

## 附录：物品覆盖率检查表

每种物品在 JEI 中应至少能通过以下方式之一查到：

| 物品类型 | 获取方式 JEI 分类 | 用途 JEI 分类 |
|----------|-----------------|---------------|
| 作物 | 商店（种子）+ 制作（种子制造机） | 机器加工（果冻/腌菜/酒/果汁/干制品）+ 烹饪 |
| 鱼 | 钓鱼信息 | 机器加工（熏鱼/鱼子）+ 烹饪 + 诱饵制作 |
| 矿物/宝石 | 晶球加工 + 怪物掉落 | 商店（售卖） |
| 工艺品 | 怪物掉落 / 钓鱼 | — |
| 手工品（酒/果冻/腌菜/蜂蜜） | 机器加工 | 木桶陈酿（酒/蜂蜜） |
| 食物 | 烹饪 + 商店 | — |
| 树液/树脂 | 树液采集 | 机器加工 + 制作 |
| 蟹笼捕获物 | 蟹笼 | 机器加工 |
| 武器/戒指 | 商店（冒险家）+ 怪物掉落 | — |
| 种子 | 商店 + 机器加工（种子制造机） | — |
| 垃圾 | 钓鱼 / 蟹笼 | 回收机 |
| 蜂蜜 | 蜂房 | 机器加工（蜂蜜酒）+ 木桶陈酿 |
| 动物产品 | — (动物系统无JEI) | 机器加工（奶酪/蛋黄酱）|
| 鱼饵 | 蠕虫箱 / 诱饵制作机 | 蟹笼 / 钓鱼 |
| 电池 | 避雷针 | 制作 |
