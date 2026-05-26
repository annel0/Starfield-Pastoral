# 精通系统 (Mastery) 1:1 复刻规划

> 目标：将 Stardew Valley 1.6 的精通系统按原版数值与体验完整移植到 StardewCraft 模组。
> 状态：规划已确认，等待方块模型资源到位后启动实施。
> 最后更新：2026/05/17

---

## 一、 现状对齐

- **山洞地形**：已在 StardewValley overworld 的固定区域内（不是独立 dimension）。warp 坐标待手测。
- **资产留口**：
  - [client/gui/common/CommonGuiTextures.java:120](src/main/java/com/stardew/craft/client/gui/common/CommonGuiTextures.java#L120) 的 `spring_onion_mastery` 属于 Power 入口，与本系统**无关**，不复用。
  - 神秘树 (`mystic_tree_seed`) 已落地（[tree/WildTrees.java](src/main/java/com/stardew/craft/tree/WildTrees.java)、[manager/WildTreeSeedManager.java](src/main/java/com/stardew/craft/manager/WildTreeSeedManager.java)），阶段 3 直接复用。
  - **方块模型/贴图**由用户提供，规划侧只负责注册与行为。
- **占位代码**（待阶段 5 替换）：
  - [fishing/data/TreasureLootManager.java:127,133](src/main/java/com/stardew/craft/fishing/data/TreasureLootManager.java#L127)
  - [item/tool/PanItem.java:171](src/main/java/com/stardew/craft/item/tool/PanItem.java#L171)
  - [shop/ShopRegistry.java](src/main/java/com/stardew/craft/shop/ShopRegistry.java)
  - [shop/GeodeLootService.java](src/main/java/com/stardew/craft/shop/GeodeLootService.java)
  - [mining/SkullCavernTreasurePool.java](src/main/java/com/stardew/craft/mining/SkullCavernTreasurePool.java)
- **SDV 源参考锚点**：
  - [源文件/StardewValley.Menus/MasteryTrackerMenu.cs](源文件/StardewValley.Menus/MasteryTrackerMenu.cs) — 573 行 UI + 奖励表
  - [源文件/StardewValley/GameLocation.cs#L8744-L8790](源文件/StardewValley/GameLocation.cs) — 6 个 Action
  - [源文件/StardewValley/Game1.cs#L9135](源文件/StardewValley/Game1.cs) — 邮件提示触发
  - [源文件/StardewValley/Farmer.cs#L3041](源文件/StardewValley/Farmer.cs) — exp 累计

---

## 二、 数值与公式（必须 1:1 锁死）

| 项 | 数值 | 来源 |
|---|---|---|
| 等级 1 阈值 | 10,000 exp | MasteryTrackerMenu.cs:441-453 |
| 等级 2 | 25,000 | 同上 |
| 等级 3 | 45,000 | 同上 |
| 等级 4 | 70,000 | 同上 |
| 等级 5（满） | 100,000 | 同上 |
| 单次 exp 来源 | 任一技能在已 Lv10 后再获得技能 exp：Farming=howMuch/2，其余=howMuch | Farmer.cs:3041 |
| 进入条件 | 5 项技能全部 Lv10 | Game1.cs:9135 / GameLocation.cs:8777 |
| MasteryRoom→Cave 传送 | (7,11,0) | GameLocation.cs:8783 |
| 神秘盒升级 (Mastery2≠0) | MysteryBox → GoldenMysteryBox | Pan.cs:232, Tree.cs:702, BreakableContainer.cs:252 |
| 金鱼宝箱升级 (Mastery1≠0) | 25%+luck 概率宝箱→金宝箱 | FishingRod.cs:935 |
| 矿石翻倍 (Mastery3≠0) | 矿石 ID 60/62/64/66/68/70/72 掉落 ×2 | GameLocation.cs:14930 |
| 黄金动物饼干 (Mastery0≠0) | 钓鱼 5%、Utility 路径 0.1% | FishingRod.cs:2577, Utility.cs:6444 |
| Mastery4 解锁 | `stats.Set("trinketSlots", 1)` | MasteryTrackerMenu.cs |

---

## 三、 已拍板的设计决策

1. **进门方式**：精通山洞门 = **独立方块**（用户已做模型）。右键交互：
   - 不满足 5×Lv10 → 拦截，显示 `mastery.requirement` 提示，不开门。
   - 满足 → 切换方块状态到 `open=true`、播放 `doorClose` 音效（原版就是这个名）、玩家可走过去。
   - 第一次走入触发首访问 cutscene（见决策 4）。
2. **饰品栏 (Trinket Slot)**：**做**。V 键背包界面（已有）加一个槽位，受 `unlockedTrinketSlots` 字段控制（默认 0，Mastery4 领奖后置 1）。
3. **神秘树**：**已存在**，阶段 3 直接发 `mystic_tree_seed` 物品作为奖励，不重做。
4. **完成 cutscene**：**做**。包含蜡烛点亮、音乐切到 `grandpas_theme`、祖父信息显示。复用现有 [cutscene/runtime/](src/main/java/com/stardew/craft/cutscene/runtime/) 框架。

---

## 四、 实施分阶段

### 阶段 0 — 数据层骨架
**目标**：玩家档案能存读 mastery exp / level / 已花等级 / 已领奖励 / 饰品槽数。

1. [player/PlayerStardewData.java](src/main/java/com/stardew/craft/player/) 新增字段：
   - `long masteryExp`
   - `int masteryLevelsSpent`
   - `EnumSet<Skill> claimedSkillRewards` （5 bit）
   - `boolean gotMasteryHint`
   - `boolean visitedMasteryCave`
   - `int unlockedTrinketSlots` （Mastery4 领奖后=1）
2. 新建 `mastery/MasteryProgress.java`：纯函数 `currentLevel(long exp)`、`expForLevel(int)`、`unspentLevels(exp, spent)`。公式逐字搬 MasteryTrackerMenu.cs:441-467。
3. 新建 `mastery/MasteryService.java`：服务端校验、发奖、广播。
4. 网络包：
   - `SyncMasteryStatePayload` (S→C)：全量推送，登入/变更时。
   - `RequestClaimMasteryRewardPayload` (C→S)：pedestal 菜单点击领取。
5. 技能 exp 增量路径上钩入：`skillLevel==10 && newGain>0` → 累计 mastery exp、广播升级 toast（i18n `mastery.newlevel`）。

**验证**：cheat 命令拉满 5 技能 → 给 exp → 客户端 GUI 数值正确。

---

### 阶段 1 — 方块与方块实体

**目标**：注册 7 个方块（用户已备模型），手动放进山洞即可工作。

| 方块 | 用途 | 状态机 |
|---|---|---|
| `mastery_cave_door` | 山洞大门 | `open: bool` 属性；右键 → 条件检查 → 满足则置 `open=true` + 音效；不满足拦截 |
| `mastery_pedestal_central` | 中央祭坛 | 右键 → 打开 `MasteryTrackerMenu(-1)` 总览 |
| `mastery_pedestal_farming` | 农业祭坛 | `MasteryTrackerMenu(0)` |
| `mastery_pedestal_fishing` | 钓鱼祭坛 | `MasteryTrackerMenu(1)` |
| `mastery_pedestal_foraging` | 觅食祭坛 | `MasteryTrackerMenu(2)` |
| `mastery_pedestal_mining` | 采矿祭坛 | `MasteryTrackerMenu(3)` |
| `mastery_pedestal_combat` | 战斗祭坛 | `MasteryTrackerMenu(4)` |

附加：
- `MasteryPedestalBlockEntity`：用于领奖后渲染上方悬浮的"纪念物 / skill flair plaque"（参考 MasteryTrackerMenu.cs `addSkillFlairPlaque`）。
- 渲染参考 [client/render/CrystalariumBlockEntityRenderer.java](src/main/java/com/stardew/craft/client/render/CrystalariumBlockEntityRenderer.java)。
- 所有方块走标准 1.21.1 BlockState + 碰撞 + interaction handler，不复用已有方块。

**验证**：/setblock 放置 7 方块 → 右键各祭坛弹出对应菜单页签；门在条件不满足时拦截、满足后可开。

---

### 阶段 2 — MasteryTrackerMenu UI

**目标**：1:1 还原原版圆形 5 技能进度盘 + 单技能领取页。

新建 [client/gui/menu/MasteryTrackerMenu.java](src/main/java/com/stardew/craft/client/gui/menu/)：

- 构造参数 `int skillIndex`（-1=总览，0-4=技能页）。
- **总览页**（参考 MasteryTrackerMenu.cs:529-537）：进度条 + 5 颗星（已领=亮）+ 当前 exp 数字。
- **技能页**（写死的 reward 表 cs:36-151）：左大物品图标 + 工具/家具/配方三栏 + 右下 Claim 按钮（`unspentLevels > 0 && !claimed[skill]` 才可点）。
- Claim → 发 `RequestClaimMasteryRewardPayload` → 服务端校验 → 发奖（详见阶段 3）。
- 完成全部 5 项 → 触发完成 cutscene（阶段 6）。
- 复用 [client/gui/common/CommonGuiTextures.java](src/main/java/com/stardew/craft/client/gui/common/CommonGuiTextures.java) 的 9-patch 风格。

**验证**：手动给 100k exp + 5 unspent → 每页都能正常领、领完按钮变灰、纪念物出现在祭坛顶上。

---

### 阶段 3 — 奖励物品/配方注册

**目标**：让 Claim 真能发到东西。

新建 `mastery/MasteryRewards.java`（静态表，避免散落）：

| 技能 | 物品 1 | 物品 2 | 配方解锁 | 备注 |
|---|---|---|---|---|
| Farming(0) | `iridium_scythe` | `statue_of_blessings` | — | scythe 若无则新建带 AOE |
| Fishing(1) | `advanced_iridium_rod` | `challenge_bait` ×N | — | 钓鱼挂钩 |
| Foraging(2) | `mystic_tree_seed` ✅已存在 | `treasure_totem` | — | 复用 |
| Mining(3) | `statue_of_the_dwarf_king` | `heavy_furnace` | — | Heavy Furnace = 熔炉变种方块 |
| Combat(4) | `anvil` | `mini_forge` | — | + 同步置 `unlockedTrinketSlots=1` |

落点：
- [item/ModItems.java](src/main/java/com/stardew/craft/item/ModItems.java) 注册新物品
- [block/ModBlocks.java](src/main/java/com/stardew/craft/block/ModBlocks.java) 注册 `statue_of_blessings` / `statue_of_the_dwarf_king` / `heavy_furnace` / `anvil` / `mini_forge`（家具类方块）
- `data/stardewcraft/recipe/mastery/*` 配方文件

**验证**：每个领取后 inventory 检查物品 ID 正确；Combat 领完后 V 键背包饰品槽出现。

---

### 阶段 4 — 进入条件 + 邮件提示

**目标**：还原"睡觉后收到邮件"的体验。

1. [player/PlayerDataEventHandler.java](src/main/java/com/stardew/craft/player/PlayerDataEventHandler.java) 的"次日"钩子里检测：
   - 5 技能全 Lv10 && !gotMasteryHint && !visitedMasteryCave → 投 `mastery_hint` 邮件 + 置标记。
2. 邮件内容走 [mail/](src/main/java/com/stardew/craft/mail/)，i18n key `mail.mastery_hint.subject/body`。
3. `mastery_cave_door` 右键交互校验同条件。
4. 玩家首次走入山洞内部 → 设 `visitedMasteryCave=true` → 触发 firstVisit cutscene（阶段 6 内嵌）。

**验证**：新档→/setlevel 拉满→睡觉→收件→去门可开→进洞触发首访 cutscene。

---

### 阶段 5 — exp 累计接入 & 替换占位

**目标**：真正让游戏行为反映 Mastery 状态，删掉所有"Lv10 冒充 mastery"占位。

替换清单（每处都改成读 `PlayerStardewData.hasMastery(skill)`）：
- [fishing/data/TreasureLootManager.java:127,133](src/main/java/com/stardew/craft/fishing/data/TreasureLootManager.java#L127) — Fishing mastery
- [item/tool/PanItem.java:171](src/main/java/com/stardew/craft/item/tool/PanItem.java#L171) — Foraging mastery → GoldenMysteryBox
- [shop/GeodeLootService.java](src/main/java/com/stardew/craft/shop/GeodeLootService.java) — 黄金动物饼干（Farming mastery）
- [mining/SkullCavernTreasurePool.java](src/main/java/com/stardew/craft/mining/SkullCavernTreasurePool.java) — 矿石 ×2（Mining mastery）
- [shop/ShopRegistry.java](src/main/java/com/stardew/craft/shop/ShopRegistry.java) — 蓝草种子上架条件

新增的主路径钩子：
- **砍树掉落** ([event/WildTreeChopEvents.java](src/main/java/com/stardew/craft/event/WildTreeChopEvents.java))：Foraging mastery → 神秘盒升级。
- **采矿掉落** ([event/MinePickaxeEvents.java](src/main/java/com/stardew/craft/event/MinePickaxeEvents.java))：Mining mastery → 矿石数量 ×2。
- **钓鱼宝箱** ([fishing/server/FishingSession.java](src/main/java/com/stardew/craft/fishing/server/FishingSession.java))：Fishing mastery → 25% 升级金宝箱。

**验证**：开关 mastery 标记前后对比掉落差异；单元测试每条规则。

---

### 阶段 6 — Cutscene 与收尾

**首次进入山洞 cutscene**（参考 Farmer.cs:2289 `firstVisit_MasteryCave`）：
- 镜头扫过 5 个 pedestal
- 各技能图标依次浮现（对应原版 `ShowSkillMastery()`）
- 玩家解锁控制
- 走 [cutscene/runtime/](src/main/java/com/stardew/craft/cutscene/runtime/) 现有框架，参考 [cutscene/runtime/EventScreenFade.java](src/main/java/com/stardew/craft/cutscene/runtime/EventScreenFade.java) 风格。

**5 项全完成 cutscene**（参考 MasteryTrackerMenu.cs:292-308）：
- 5 个祭坛上方蜡烛点亮（用方块实体动画 + 客户端粒子）
- 玩家冻结 2 秒
- 音乐切到 `grandpas_theme`（需检查 [sound/ModSounds.java](src/main/java/com/stardew/craft/sound/ModSounds.java) 是否已注册；未注册则补）
- 4 秒后显示 `mastery.complete_toast`
- 投祖父邮件 `mail.grandpa_mastery_note`

**本地化**（≈40 条，[lang/en_us.json](src/main/resources/assets/stardewcraft/lang/en_us.json) / [lang/zh_cn.json](src/main/resources/assets/stardewcraft/lang/zh_cn.json)）：
- `stardewcraft.mastery.title`
- `stardewcraft.mastery.skill.{0..4}`
- `stardewcraft.mastery.newlevel`
- `stardewcraft.mastery.requirement`
- `stardewcraft.mastery.claim`
- `stardewcraft.mastery.complete_toast`
- `stardewcraft.block.mastery_*` × 7
- `stardewcraft.item.{iridium_scythe,...}` × 10
- `stardewcraft.mail.mastery_hint.{subject,body}`
- `stardewcraft.mail.grandpa_mastery_note.{subject,body}`

**饰品栏 UI**：
- 修改 V 键背包界面 → 增加一个 Slot（位置参考原版 `MenuTab Inventory` 装备区右侧）。
- 仅在 `unlockedTrinketSlots >= 1` 时可见+可放。
- 饰品系统本身（trinket effect、商店）**不在本期范围**，本期只做"槽位 + 物品可存放"。

**文档**：
- 本文件作为最终设计文档保留。
- 完成后补 `docs/MASTERY_SYSTEM.md`（如有需要的话）记录字段、阈值、扩展点。

---

## 五、 文件落点速查

```
新建：
  src/main/java/com/stardew/craft/mastery/MasteryProgress.java
  src/main/java/com/stardew/craft/mastery/MasteryRewards.java
  src/main/java/com/stardew/craft/mastery/MasteryService.java
  src/main/java/com/stardew/craft/block/mastery/MasteryCaveDoorBlock.java
  src/main/java/com/stardew/craft/block/mastery/MasteryPedestalBlock.java         // 6 个变体或一个带枚举属性
  src/main/java/com/stardew/craft/blockentity/MasteryPedestalBlockEntity.java
  src/main/java/com/stardew/craft/client/gui/menu/MasteryTrackerMenu.java
  src/main/java/com/stardew/craft/client/render/MasteryPedestalBlockEntityRenderer.java
  src/main/java/com/stardew/craft/network/payload/SyncMasteryStatePayload.java
  src/main/java/com/stardew/craft/network/payload/RequestClaimMasteryRewardPayload.java
  src/main/java/com/stardew/craft/cutscene/MasteryFirstVisitCutscene.java
  src/main/java/com/stardew/craft/cutscene/MasteryCompletionCutscene.java
  src/main/resources/assets/stardewcraft/{models,textures}/block/mastery_*        // 模型由用户提供
  src/main/resources/data/stardewcraft/loot_table/mastery/*

修改：
  src/main/java/com/stardew/craft/player/PlayerStardewData.java                   // 新字段 + 序列化
  src/main/java/com/stardew/craft/player/PlayerDataEventHandler.java              // 邮件触发 + exp 累计
  src/main/java/com/stardew/craft/block/ModBlocks.java                            // 注册 7 方块 + 家具类
  src/main/java/com/stardew/craft/item/ModItems.java                              // 注册奖励物品
  src/main/java/com/stardew/craft/sound/ModSounds.java                            // grandpas_theme (如缺)
  src/main/java/com/stardew/craft/mail/*                                          // 2 封邮件
  src/main/java/com/stardew/craft/fishing/data/TreasureLootManager.java           // 删占位
  src/main/java/com/stardew/craft/item/tool/PanItem.java                          // 删占位
  src/main/java/com/stardew/craft/shop/ShopRegistry.java                          // 删占位
  src/main/java/com/stardew/craft/shop/GeodeLootService.java                      // 删占位
  src/main/java/com/stardew/craft/mining/SkullCavernTreasurePool.java             // 矿石×2 接入
  src/main/java/com/stardew/craft/fishing/server/FishingSession.java              // 金宝箱升级
  src/main/java/com/stardew/craft/event/WildTreeChopEvents.java                   // 砍树神秘盒升级
  src/main/java/com/stardew/craft/event/MinePickaxeEvents.java                    // 采矿×2
  src/main/java/com/stardew/craft/client/gui/menu/*Inventory*.java                // 饰品槽位
  src/main/resources/assets/stardewcraft/lang/{en_us,zh_cn}.json                  // ~40 条
```

---

## 六、 阶段依赖图

```
阶段 0 (数据层)
   ↓
阶段 1 (方块) ────┐
   ↓             │
阶段 2 (UI) ─────┤
   ↓             │
阶段 3 (奖励) ───┤
                  ↓
              阶段 4 (邮件+进门条件)
                  ↓
              阶段 5 (占位替换+主路径钩子)
                  ↓
              阶段 6 (Cutscene+本地化+饰品槽)
```

阶段 0 必须先做完；1/2/3 在 0 完成后可并行推进；4 依赖 1；5/6 可在 1-4 完成后开。

---

## 七、 待用户后续提供

- 7 个方块的模型/贴图（mastery_cave_door + 6 pedestal）
- 各奖励物品的图标贴图（10 件）
- 完成 cutscene 的"祖父信"文案（如希望与原版有差异）
