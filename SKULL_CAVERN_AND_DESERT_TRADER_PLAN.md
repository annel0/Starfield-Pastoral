# 骷髅矿井宝箱 + 沙漠骆驼商人 实现规划书

> 范围：让骷髅矿井（Skull Cavern）与原版（SDV 1.6）一致地提供"冲层动力"——在指定深度生成宝箱并随机掉落 26 选 1 奖励；同时新增沙漠骆驼商人（Desert Trader）作为"纯触发器 NPC"，提供与原版完全一致的以物易物商店。
>
> 工程量较大，先确认物品清单 → 截图原版资产 → 批量注册物品 → 复用现有宝箱系统接入骷髅矿井 → 新建触发器 NPC 类 → 注册商店。

---

## 0. 总览：执行顺序（强制串行）

| 阶段 | 内容 | 依赖 | 是否需用户确认 |
|---|---|---|---|
| **P1** | 物品清单确认（本文档第 2 节表格）| — | ✅ 必须 |
| **P2** | 从 SDV 原版提取每个新物品的 sprite，逐个截图给用户 | P1 | ✅ 必须（确认贴图无误）|
| **P3** | 在 `ModItems.java` 批量注册新物品（含贴图、模型、语言键、tooltip 类型）| P2 | ❌ |
| **P4** | 实现骷髅矿井宝箱（扩展 `MineChestLootTable` + `SkullCavernTreasurePool`，接入 220/320/420 楼层）| P3 | ❌ |
| **P5** | 新建 `MerchantNpcEntity`（触发器 NPC，无社交/无礼物/无好感/无日程），放置于沙漠 tile (41,42) | P3 | ❌ |
| **P6** | 在 `ShopRegistry` 注册 `desert_trader` 商店（每日轮换 10 选 N，使用现有 `tradeItemId` 字段实现以物易物）| P5 | ❌ |
| **P7** | `./gradlew classes` 验证 + 进游戏自测 | P6 | — |

---

## 1. 楼层编号澄清（回应 "我没看懂 我们的floor和sdv不也是一样的吗"）

**结论：是一样的。** 我们项目里 `floor` 直接就是 SDV 的 `mineLevel`：

| 区域 | mineLevel 范围 | 备注 |
|---|---|---|
| 普通矿井 | 1 – 120 | 第 120 层是矿井底 |
| 骷髅矿井 | 121 – ∞ | SDV 内部继续用 `mineLevel`，玩家看到的"骷髅矿井深度" = `mineLevel - 120` |

**SDV 宝箱触发条件（来自 `MineShaft.cs::getTreasureRoomItem`）**：
- `mineLevel == 220` → 骷髅矿井深度 100，**1 个**宝箱
- `mineLevel == 320` → 骷髅矿井深度 200，**3 个**宝箱
- `mineLevel == 420` → 骷髅矿井深度 300，**3 个**宝箱

我们直接用 `floor == 220 / 320 / 420` 判断即可，**无需任何偏移**。同时这三层会用粉色（255,210,200）/ 青绿色（216,255,240）的特殊染色标记。

---

## 2. 物品清单（重新核查后的最终版）

> 表格规则：✅ = `ModItems.XXX` 已注册；🟢 = `ModItems.VANILLA_CATEGORY_ITEMS.get("xxx")` 已注册（无静态字段）；🆕 = 需新建；⚠ = 已有近似但不完全一致，需独立条目。
>
> 字段命名遵循现有约定：`SCREAMING_SNAKE_CASE`，注册 ID 用 `snake_case`。

### 2A. 骷髅矿井宝箱奖励池（SDV `getTreasureRoomItem()` 全量复刻）

| # | 中文名 | SDV 名 | SDV ID | 字段 / 取用方式 | 状态 |
|---|---|---|---|---|---|
| 1 | 全能地灵石 ×10 | Omni Geode | (O)749 | `OMNI_GEODE` | ✅ ModItems.java:1264 |
| 2 | 宝箱（家具）| Treasure Chest | (BC)166 | `TREASURE_CHEST` | ✅ ModItems.java:1055 |
| 3 | 神秘箱 ×3 | Mystery Box | (O)MysteryBox | `MYSTERY_BOX` | ✅ ModItems.java:1049 |
| 4 | 金色神秘箱 | Golden Mystery Box | (O)GoldenMysteryBox | `GOLDEN_MYSTERY_BOX` | ✅ ModItems.java:1051 |
| 5 | 古代鼓 | Ancient Drum | (O)123 | `ANCIENT_DRUM` | ✅ ModItems.java:1436 |
| 6 | 铱锭 ×5 | Iridium Bar | (O)337 | `IRIDIUM_BAR` | ✅ ModItems.java:1234 |
| 7 | 铱矿 ×50 | Iridium Ore | (O)386 | `IRIDIUM_ORE` | ✅ ModItems.java:1225 |
| 8 | 棱彩碎片 | Prismatic Shard | (O)74 | `PRISMATIC_SHARD` | ✅ ModItems.java:1294 |
| 9 | 钻石 ×3 | Diamond | (O)72 | `DIAMOND` | ✅ ModItems.java:1292 |
| 10 | 翡翠 ×5 | Jade | (O)70 | `JADE` | ✅ ModItems.java:1290 |
| 11 | 红宝石 ×5 | Ruby | (O)64 | `RUBY` | ✅ ModItems.java:1284 |
| 12 | 黄玉 ×5 | Topaz | (O)68 | `TOPAZ` | ✅ ModItems.java:1288 |
| 13 | 紫水晶 ×5 | Amethyst | (O)66 | `AMETHYST` | ✅ ModItems.java:1286 |
| 14 | 海蓝宝石 ×5 | Aquamarine | (O)62 | `AQUAMARINE` | ✅ ModItems.java:1282 |
| 15 | 翠绿宝石 ×5 | Emerald | (O)60 | `EMERALD` | ✅ ModItems.java:1280 |
| 16 | 冬季根 ×5 | Winter Root | (O)412 | `VANILLA_CATEGORY_ITEMS.get("winter_root")` | 🟢 |
| 17 | 古物宝藏 | Artifact Trove | (O)275 | `ARTIFACT_TROVE` | 🆕 |
| 18 | 稀有蘑菇 | Rare Mushroom | (O)422 | `RARE_MUSHROOM` | 🆕 |
| 19 | 考古学家的地图 | Archaeologist's Map | (O)ArchaeologistsMap | `ARCHAEOLOGISTS_MAP` | 🆕 |
| 20 | 壁纸（家具）| Wallpaper | (F)1838 | `WALLPAPER_FURNITURE` | ⚠ 已有 `WALLPAPER_BLOCK`/`WALLPAPER_ICON`，宝箱是家具壁纸不同 |
| 21 | 暗色壁纸 | Dark Wallpaper | (F)1840 | `DARK_WALLPAPER` | 🆕 |
| 22 | 砖块地板 | Brick Floor | (F)1308 | `BRICK_FLOOR` | 🆕 |
| 23 | 大地方尖碑 | Earth Obelisk | (BC)EarthObelisk | `EARTH_OBELISK` | 🆕 |
| 24 | 森林方尖碑 | Forest Obelisk | (BC)ForestObelisk | `FOREST_OBELISK` | 🆕 |
| 25 | 黄金钟楼 | Gold Clock | (BC)GoldClock | `GOLD_CLOCK` | ⚠ 已有 `GRANDFATHER_CLOCK`，需独立条目 |
| 26 | 结婚戒指 | Wedding Ring | (O)801 | `WEDDING_RING` | 🆕 |
| 27 | 展示架 | Display Stand | (BC)DisplayStand | `DISPLAY_STAND` | 🆕 |
| 28 | 炉灶 | Stove | (BC)Stove | `STOVE` | 🆕（注：项目内已有 `FURNACE` 是熔炉，不同）|
| 29 | 漏斗 | Hopper | (BC)Hopper | `HOPPER` | ⚠ 已有 `HAY_HOPPER`（饲料），独立条目 |
| 30 | 楼梯 | Staircase | (O)71 | `STAIRCASE` | 🆕 |
| 31 | 质朴木板地板 | Rustic Plank Floor | (F)RusticPlankFloor | `RUSTIC_PLANK_FLOOR` | 🆕 |
| 32 | 饰品盒 | Trinket Box | (O)Trinket | `TRINKET_BOX` | 🆕 |

### 2B. 骆驼商人收购物（玩家拿来换的物品）

来源：`Content/Data/Shops.json::DesertTrade.TradeItemId`。

| # | 中文名 | SDV 名 | SDV ID | 字段 / 取用方式 | 状态 |
|---|---|---|---|---|---|
| 1 | 铱矿 | Iridium Ore | (O)386 | `IRIDIUM_ORE` | ✅ |
| 2 | 铱锭 | Iridium Bar | (O)337 | `IRIDIUM_BAR` | ✅ |
| 3 | 全能地灵石 | Omni Geode | (O)749 | `OMNI_GEODE` | ✅ |
| 4 | 地灵石 | Geode | (O)535 | `GEODE` | ✅ ModItems.java:1258 |
| 5 | 冰冻地灵石 | Frozen Geode | (O)536 | `FROZEN_GEODE` | ✅ |
| 6 | 岩浆地灵石 | Magma Geode | (O)537 | `MAGMA_GEODE` | ✅ |
| 7 | 蝎子鲤鱼 | Scorpion Carp | (O)165 | `SCORPION_CARP` | ✅ ModItems.java:1890 |
| 8 | 沙鱼 | Sandfish | (O)164 | `SANDFISH` | ✅ ModItems.java:1887 |
| 9 | 棱彩碎片 | Prismatic Shard | (O)74 | `PRISMATIC_SHARD` | ✅ |
| 10 | 仙人掌果 | Cactus Fruit | (O)90 | `VANILLA_CATEGORY_ITEMS.get("cactus_fruit")` | 🟢 |
| 11 | 椰子 | Coconut | (O)88 | `VANILLA_CATEGORY_ITEMS.get("coconut")` | 🟢 |

### 2C. 骆驼商人售卖物（玩家用 2B 换出来的物品）

| # | 中文名 | SDV 名 | SDV ID | 字段 / 取用方式 | 状态 |
|---|---|---|---|---|---|
| 1 | 仙人掌果种子 | Cactus Seeds | (O)802 | `CACTUS_SEEDS` | 🆕 |
| 2 | 椰子 | Coconut | (O)88 | `VANILLA_CATEGORY_ITEMS.get("coconut")` | 🟢 |
| 3 | 棱彩碎片 | Prismatic Shard | (O)74 | `PRISMATIC_SHARD` | ✅ |
| 4 | 稀有种子 | Rare Seed | (O)347 | `RARE_SEED` | 🆕 |
| 5 | 沙漠传送图腾 | Warp Totem: Desert | (O)688 | `WARP_TOTEM_DESERT` | 🆕（与 `WARP_TOTEM_FARM/MOUNTAIN/BEACH` 同体系）|
| 6 | 户外烧烤套装 | Cookout Kit | (BC)278 | `COOKOUT_KIT` | 🆕 |
| 7 | 余烬碎片 | Cinder Shard | (O)848 | `CINDER_SHARD` | 🆕 |
| 8 | 宝藏鉴定指南 | Treasure Appraisal Guide | (O)Book_Trash | `TREASURE_APPRAISAL_GUIDE` | 🆕 |
| 9 | 蛙卵 | Frog Egg | (O)819 | `FROG_EGG` | 🆕 |
| 10 | 神秘箱 | Mystery Box | (O)MysteryBox | `MYSTERY_BOX` | ✅ |

### 2D. 实际需要新建的物品清单（最终版，共 **23 项**）

去除掉所有已注册（包括 `VANILLA_CATEGORY_ITEMS`）后，下面才是真正要写代码注册的：

#### 来自 2A（宝箱奖励，16 项）

```
ARTIFACT_TROVE              // (O)275      Object
RARE_MUSHROOM               // (O)422      Object
ARCHAEOLOGISTS_MAP          // (O)Map      Object
WALLPAPER_FURNITURE         // (F)1838     Furniture（≠ 已有 WALLPAPER_BLOCK / WALLPAPER_ICON）
DARK_WALLPAPER              // (F)1840     Furniture
BRICK_FLOOR                 // (F)1308     Furniture
EARTH_OBELISK               // (BC)        BigCraftable
FOREST_OBELISK              // (BC)        BigCraftable
GOLD_CLOCK                  // (BC)        BigCraftable（≠ 已有 GRANDFATHER_CLOCK）
WEDDING_RING                // (O)801      Object
DISPLAY_STAND               // (BC)        BigCraftable
STOVE                       // (BC)        BigCraftable（≠ 已有 FURNACE）
HOPPER                      // (BC)        BigCraftable（≠ 已有 HAY_HOPPER）
STAIRCASE                   // (O)71       Object
RUSTIC_PLANK_FLOOR          // (F)         Furniture
TRINKET_BOX                 // (O)Trinket  Object（开盒物品占位）
```

#### 来自 2C（商店售卖，7 项）

```
CACTUS_SEEDS                // (O)802      Seed
RARE_SEED                   // (O)347      Seed
WARP_TOTEM_DESERT           // (O)688      Object（补完 totem 系列）
COOKOUT_KIT                 // (BC)278     BigCraftable
CINDER_SHARD                // (O)848      Object
TREASURE_APPRAISAL_GUIDE    // (O)Book_Trash  Book
FROG_EGG                    // (O)819      Object
```

> **总计：23 项新建 + 0 项重命名**。`STARDROP` 在 SDV 沙漠商人不参与交易（只是任务奖励物），从清单移除。`WINTER_ROOT` / `COCONUT` / `CACTUS_FRUIT` 复用 `VANILLA_CATEGORY_ITEMS` map 即可。

---

## 3. P2 阶段：SDV 资产提取计划

每项新物品需提取以下文件并截取相应 16×16 sprite：

| 类别 | 资源文件 | 坐标公式（SDV ID = N）|
|---|---|---|
| (O) Object | `源文件/Content/Maps/springobjects.png` | `x = (N % 24) × 16, y = (N / 24) × 16` |
| (BC) BigCraftable | `源文件/Content/TileSheets/Craftables.png` | `x = (N % 8) × 16, y = (N / 8) × 32`，尺寸 16×32 |
| (F) Furniture | `源文件/Content/TileSheets/furniture.png` | 见 `Data/Furniture.json` 的 source rect |

执行方式：用 `view_image` 每次显示一张完整 sheet → 我目测裁切坐标 → 截屏告诉你 → 你确认贴图正确性。

---

## 4. P4 阶段：骷髅矿井宝箱实现概要

**复用**：`MineChestBlock` / `MineChestBlockEntity` / `MineFloorGenerator.placeMineChest()`。

**新增**：
1. `SkullCavernTreasurePool.java` — 26 项加权随机池（与 SDV `getTreasureRoomItem()` 1:1 等价）
2. `MineChestLootTable.isChestFloor()` 增加 `floor == 220 || floor == 320 || floor == 420`
3. `MineChestLootTable.getRewardForFloor()` 中遇到 220/320/420 时调用 `SkullCavernTreasurePool.roll(random, floor)`
4. `MineFloorGenerator` 在 220/320/420 楼层放置数量正确的宝箱（1/3/3）并按需着色（粉色/青绿色 tag → blockstate 或 BE 数据）

---

## 5. P5/P6 阶段：骆驼商人触发器 NPC + 商店

**新增类**：
- `com.stardew.craft.npc.entity.MerchantNpcEntity extends StardewNpcEntity`
  - `@Override isSocial() → false`、`canReceiveGifts() → false`、`getSchedule() → null`
  - 重写右键交互：直接 `openShop("desert_trader")`
  - 不在好感度同步包内、不在社交菜单内（在 `NpcContentFilter` / `RequestNpcFriendshipOverviewPayload` 处过滤）
- 注册：`ModEntities.MERCHANT_NPC`
- 实体放置：服务端 `WorldEvents.onLevelLoad` 检测沙漠维度 (41,42) 处若无该实体则 spawn

**新增商店**：
- `ShopRegistry.register("desert_trader", new ShopDefinition(...))`
- 库存：13 项（2C 全表）每日按 SDV 规则轮换（用 `dayOfMonth + season` 哈希选 N 项）
- 每个 `ShopItemEntry` 必须填 `tradeItemId` + `tradeItemCount`，**不带 G 价格**
- 商店 UI：复用现有 `ShopScreen.requiresTrade()` 分支（已为铁匠商店实现）

---

## 6. 当前请确认的事项 ✋

请回答以下 4 个问题，我就开始 P2 资产提取：

1. **第 2D 节的 25 项物品命名/范围有无要改？**（特别是 `WALLPAPER_FURNITURE` / `HOPPER` / `GOLD_CLOCK` 这种与现有命名相近的项）
2. **`CACTUS_SEEDS` vs `Djinn Pepper Seeds`** — SDV 沙漠商人卖的种子在 1.6 是 Cactus Seeds (O802)，请确认是这个不是别的
3. **`TRINKET_BOX`** — SDV 的 Trinket 是随机产物（一组饰品），我们是做"一个统一的开盒物品"还是"直接做几个具体 Trinket"？
4. **资产截图方式** — 一次发一张完整 sheet（让你看全貌），还是我裁好 25 个独立小图分别发？

---

> 文件位置：`SKULL_CAVERN_AND_DESERT_TRADER_PLAN.md`（已写入工作区根）
> 下一步：等你回复以上 4 问 → 进入 P2。
