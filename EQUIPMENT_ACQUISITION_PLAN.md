# 武器/装备获取方式对齐计划

> **目标**：让每件武器、戒指、靴子的获取方式与 SDV 原版完全一致。
> 当前状态：几乎所有装备都无脑塞进马龙商店，无条件全年可买——这严重破坏了游戏体验和进度感。

---

## 目录

1. [现状总览](#1-现状总览)
2. [马龙商店修正](#2-马龙商店修正)
3. [Gil 怪物猎杀奖励](#3-gil-怪物猎杀奖励)
4. [钓鱼宝箱补全](#4-钓鱼宝箱补全)
5. [矿洞宝箱奖励](#5-矿洞宝箱奖励)
6. [怪物武器掉落](#6-怪物武器掉落)
7. [Galaxy 武器获取](#7-galaxy-武器获取)
8. [可合成戒指配方](#8-可合成戒指配方)
9. [从商店移除的物品](#9-从商店移除的物品)
10. [缺失物品注册](#10-缺失物品注册)
11. [实施路线图](#11-实施路线图)

---

## 1. 现状总览

| 系统 | 当前状态 | 原版行为 |
|------|---------|---------|
| 马龙商店 | 所有武器/靴子/戒指无条件全年可购买 | 按矿层进度解锁，部分物品不卖 |
| Gil 奖励 | ❌ 不存在 | 13 项怪物猎杀目标，各有奖励 |
| 钓鱼宝箱 | 无武器/戒指/靴子 | Neptune's Glaive、Broken Trident、戒指、靴子 |
| 矿洞宝箱 | ❌ 不存在 | 整 10 层给武器/靴子/星之果实/骷髅钥匙 |
| 怪物掉落 | 0 武器掉落 | Skeleton→Bone Sword(4%)、Haunted Skull→Dark Sword |
| Galaxy 武器 | 商店直接购买 | 棱彩碎片→法师对话→Galaxy Sword；之后商店可回购 |
| Infinity 锻造 | 商店直接购买 | Galaxy 武器 + 3×Galaxy Soul 锻造升级 |
| 合成戒指 | 5 个已有配方 | 7 个可合成戒指 |
| 棍棒/锤 | 只有 Femur 1 把 | 9 把（含 Galaxy Hammer、Infinity Gavel） |
| 弹弓 | ❌ 完全缺失 | 3 把（Slingshot、Master Slingshot、Galaxy Slingshot） |

---

## 2. 马龙商店修正

### 2.1 添加矿层解锁条件

原版中马龙的武器库存随玩家矿井进度解锁。需要在 `ShopRegistry.java` 中为每个物品添加 `condition` 字段（检查玩家已到达的最深矿井层数）。

**SDV 原版解锁条件**：

| 物品 | 价格 | 解锁条件 |
|------|------|---------|
| Rusty Sword | 250g | 无 |
| Wooden Blade | 250g | 无 |
| Femur | 350g | 矿井 10 层 |
| Iron Dirk | 500g | 矿井 15 层 |
| Wind Spire | 500g | 矿井 15 层 |
| Silver Saber | 750g | 矿井 20 层 |
| Elf Blade | 750g | 矿井 20 层 |
| Steel Smallsword | 750g | 矿井 20 层 |
| Pirate's Sword | 850g | 矿井 25 层 |
| Cutlass | 1,500g | 矿井 25 层 |
| Burglar's Shank | 1,500g | 矿井 25 层 |
| Wood Mallet *(新注册)* | 2,000g | 矿井 40 层 |
| Claymore | 2,000g | 矿井 45 层 |
| Templar's Blade | 4,000g | 矿井 55 层 |
| Crystal Dagger | 4,500g | 矿井 60 层 |
| Bone Sword | 6,000g | 矿井 75 层 |
| Wicked Kris | 3,000g | 矿井 80 层 |
| Steel Falchion | 9,000g | 矿井 90 层 |
| Obsidian Edge | 9,000g | 矿井 90 层 |
| Lava Katana | 25,000g | 矿井 120 层 |

**弹弓（新注册后加入）**：

| 物品 | 价格 | 解锁条件 |
|------|------|---------|
| Slingshot *(新注册)* | 500g | 矿井 40 层 |
| Master Slingshot *(新注册)* | 1,000g | 矿井 70 层 |

**Galaxy 系列（需先获取 Galaxy Sword 后才出现）**：

| 物品 | 价格 | 解锁条件 |
|------|------|---------|
| Galaxy Sword | 50,000g | 已通过法师获取过 Galaxy Sword（`galaxySword` 标记） |
| Galaxy Dagger | 35,000g | 同上 |
| Galaxy Hammer *(新注册)* | 75,000g | 同上 |

### 2.2 从商店移除的物品

以下物品在原版中**不在马龙商店出售**，应移除：

| 物品 | 正确获取方式 | 处理 |
|------|------------|------|
| Neptune's Glaive | 钓鱼宝箱 (5%×luck) | ❌ 移除 |
| Broken Trident | 钓鱼宝箱 (5%×luck) | ❌ 移除 |
| Insect Head | Gil 奖励（杀 80 虫子） | ❌ 移除 |
| Dark Sword | Haunted Skull 掉落 | ❌ 移除 |
| Bone Sword | Skeleton 4% 掉落 + 商店 | ✅ 保留（原版两种方式） |
| Yeti Tooth | 矿洞宝箱 | ❌ 移除（暂无矿洞宝箱系统，可保留标注 TODO） |
| Meowmere | 特殊事件 | ❌ 移除 |
| Ossified Blade | 火山掉落 | ❌ 移除（暂无火山系统） |
| Dwarf Sword/Dagger | 火山掉落 | ❌ 移除（暂无火山系统） |
| Dragontooth 系列 | 火山掉落/锻造 | ❌ 移除（暂无火山系统） |
| Iridium Needle | 火山/骷髅洞掉落 | ❌ 移除（暂无对应系统） |
| Infinity Blade/Dagger | Galaxy + 3×Galaxy Soul 锻造 | ❌ 移除（需实现锻造系统） |
| Holy Blade | SDV 无此武器（mod 原创？） | ❌ 移除或标注 mod 独创 |

### 2.3 Gil 奖励戒指——从商店移除

以下戒指在原版中**不在商店出售**，只能通过 Gil 奖励获取：

| 戒指 | 原版来源 |
|------|---------|
| Slime Charmer Ring | Gil：杀 1000 粘液怪 |
| Savage Ring | Gil：杀 150 暗影人 |
| Vampire Ring | Gil：杀 200 蝙蝠 |
| Burglar's Ring | Gil：杀 500 灰尘精灵 |
| Crabshell Ring | Gil：杀 60 螃蟹 |
| Napalm Ring | Gil：杀 250 毒蛇 |

### 2.4 可合成戒指——从商店移除

以下戒指在原版中**通过合成获得**，不在商店出售：

| 戒指 | 原版获取 |
|------|---------|
| Iridium Band | 合成（Combat 9） |
| Ring of Yoba | 合成（Combat 7） |
| Glowstone Ring | 合成（Mining 4） |
| Sturdy Ring | 合成（Combat 1） |
| Warrior Ring | 合成（Combat 4） |
| Thorns Ring | 合成（Combat 7） |

### 2.5 仅限矿洞获取的戒指——从商店移除

| 戒指 | 原版获取 |
|------|---------|
| Small Glow Ring | 矿洞宝箱/掉落 |
| Glow Ring | 矿洞宝箱/掉落 |
| Small Magnet Ring | 矿洞宝箱/掉落 |
| Magnet Ring | 矿洞宝箱/掉落 |
| Lucky Ring | 特殊掉落（极稀有） |

### 2.6 火山/Ginger Island 物品——暂时移除

由于火山系统未实现，以下物品暂时从商店移除（待火山系统实现后通过正确途径获取）：

Hot Java Ring、Protection Ring、Soul Sapper Ring、Phoenix Ring

---

## 3. Gil 怪物猎杀奖励

### 3.1 系统设计

集成在马龙的冒险商店界面中（不需要单独的 Gil NPC）。在商店界面添加一个"怪物猎杀目标"标签页或侧边栏，显示：
- 各目标进度（已击杀/需要击杀）
- 已完成目标可领取奖励

### 3.2 数据存储

新增 `MonsterSlayerData`（`SavedData`），存储：
- `Map<String, Integer> monsterKillCounts` — 各目标怪物的击杀计数
- `Set<String> claimedRewards` — 已领取的奖励 ID

### 3.3 击杀计数

在 `MineMonsterDropHandler` 中，怪物死亡时递增对应的击杀计数。

### 3.4 完整目标列表（来自 SDV 源码 `MonsterSlayerQuests.json`）

| 目标 ID | 显示名 | 目标怪物 | 击杀数 | 奖励 |
|---------|--------|---------|--------|------|
| Slimes | 粘液怪 | Green Slime, Frost Jelly, Sludge, Tiger Slime | 1000 | Slime Charmer Ring |
| Shadows | 暗影人 | Shadow Brute, Shadow Shaman | 150 | Savage Ring |
| Bats | 蝙蝠 | Bat, Frost Bat, Lava Bat, Iridium Bat | 200 | Vampire Ring |
| Skeletons | 骷髅 | Skeleton, Skeleton Mage | 50 | Skeleton Mask (帽子) |
| Insects | 洞穴虫 | Grub, Fly, Bug | 80 | Insect Head (武器) |
| Duggy | 地鼠 | Duggy, Magma Duggy | 30 | Hard Hat (帽子) |
| DustSpirits | 灰尘精灵 | Dust Spirit | 500 | Burglar's Ring |
| Crabs | 岩蟹 | Rock Crab, Lava Crab | 60 | Crabshell Ring |
| Mummies | 木乃伊 | Mummy | 100 | Arcane Hat (帽子) |
| Dinos | 恐龙 | Pepper Rex | 50 | Knight's Helmet (帽子) |
| Serpents | 毒蛇 | Serpent, Royal Serpent | 250 | Napalm Ring |
| FlameSpirits | 岩浆精灵 | Magma Sprite, Magma Sparker | 150 | 解锁公会电话功能 |

> **注意**：帽子系统如果没有实现，对应奖励可以暂时用 placeholder 或跳过。
> 优先实现有戒指/武器奖励的目标（Slimes, Shadows, Bats, Insects, DustSpirits, Crabs, Serpents）。

### 3.5 实现步骤

1. 创建 `MonsterSlayerData.java`（`SavedData`）
2. 在 `MineMonsterDropHandler` 的怪物死亡逻辑中，按怪物类型递增计数
3. 在冒险商店 UI 添加"怪物猎杀"标签页
4. 奖励领取逻辑：检查计数是否达标 + 是否已领取
5. 对应翻译键

---

## 4. 钓鱼宝箱补全

### 4.1 当前状态

`fishing_treasure.json` 仅有矿石/宝石/文物/饵料，完全缺失武器/戒指/靴子/食物等。

### 4.2 需要添加的物品（来自 SDV 源码 `FishingRod.openTreasureMenuEndFunction`）

#### 武器（Sub Case 2 → 武器/装备/稀有分支）

| 物品 | 概率 | 条件 |
|------|------|------|
| **Neptune's Glaive** | 5% × luckModifier | specialItem，不重复获取 |
| **Broken Trident** | 5% × luckModifier | specialItem，不重复获取 |

#### 戒指

| 物品 | 概率 | 条件 |
|------|------|------|
| Small Glow Ring / Glow Ring | 7% × luck, 1/3 选中 | Glow 升级受运气影响 |
| Small Magnet Ring / Magnet Ring | 7% × luck, 1/3 选中 | Magnet 升级受运气影响 |
| 随机宝石戒指 (Amethyst/Topaz/Aquamarine/Jade/Emerald/Ruby Ring) | 7% × luck, 1/3 选中 | — |
| Iridium Band | 1% × luck | — |

#### 靴子

| 物品 | 概率 | 条件 |
|------|------|------|
| 随机靴子 (Sneakers~Space Boots) | 1% × luck | 随机选 1 |

#### 稀有物品

| 物品 | 概率 | 条件 |
|------|------|------|
| Prismatic Shard | 0.1% × luck | 钓鱼等级 > 5 |
| Treasure Chest 装饰物 | 2% × luck | — |
| Strange Doll (green) | 1% × luck | — |
| Strange Doll (yellow) | 1% × luck | — |

#### 每轮固定检查

| 物品 | 概率 | 条件 |
|------|------|------|
| Rice Shoot | 10% | 春季 + 非沙滩 |
| Wild Bait | 50% | 多鱼 + 已学配方 |

#### 金色宝箱专属（50% 触发 13 选 1）

| 物品 | 说明 |
|------|------|
| Iridium Bar ×1-5 | — |
| Fairy Dust ×3-5 | — |
| Dressed Spinner | — |
| Challenge Bait ×3-5 | — |
| Magnet ×3-5 | — |
| Sonar Bobber | — |
| Fish Smoker | — |
| Pearl | — |

> 部分物品（Stardrop Tea, Skill Book, Raccoon Seed, Wood Chipper 等）如果 Mod 中不存在则跳过。

### 4.3 实现步骤

1. 修改 `fishing_treasure.json`：添加新的战利品池
2. 修改 `TreasureLootManager.java`：在主循环中加入 case 2（武器/装备分支）的逻辑
3. 添加 `specialItem` 去重机制（玩家数据标记已获取的特殊物品）
4. 添加 `luckModifier` 概率计算（基于运气 + clearWaterDistance）
5. 金色宝箱专属池子的 13 选 1 逻辑

---

## 5. 矿洞宝箱奖励

### 5.1 整 10 层奖励宝箱

原版中矿洞的 10/20/40/50/60/70/80/90/100/110/120 层有**固定宝箱奖励**。这些层不需要挖掘即可直接抵达（电梯层）。

| 楼层 | 奖励物品 | 物品 ID | 备注 |
|------|---------|---------|------|
| 10 | Sneakers (运动鞋) | `(B)506` | 靴子 |
| 20 | Steel Smallsword (钢短剑) | `(W)11` | 武器 |
| 40 | Cutlass (弯刀) | `(W)32` | 武器 |
| 50 | Thermal Boot (热力靴) | `(B)509` | 靴子 |
| 60 | Crystal Dagger (水晶匕首) | `(W)21` | 匕首 |
| 70 | Claymore (阔剑) | `(W)33` | 武器 |
| 80 | Space Boots (太空靴) | `(B)512` | 靴子 |
| 90 | Obsidian Edge (黑曜石之刃) | `(W)8` | 武器 |
| 100 | **Stardrop (星之果实)** | `(O)434` | 永久增加最大能量 |
| 110 | Space Boots 变体 | `(B)514` | 靴子 |
| 120 | **Skull Key (骷髅钥匙)** | special | 粉色宝箱，解锁骷髅洞穴 |

### 5.2 矿洞钓鱼

原版中每 20 层（20, 40, 60, 80, 100, 120...）有水域，可以钓鱼。矿井有专属鱼：

| 矿井区域 | 层数 | 特殊鱼 | 基础概率 |
|----------|------|--------|---------|
| 区域 0/10 | 1-39 层 | Stonefish 石鱼 | 2% + 1%×运气 |
| 区域 40 | 40-79 层 | Ice Pip 冰柱鱼 | 1.5% + 0.9%×运气 |
| 区域 80 | 80-119 层 | Lava Eel 岩浆鳗 | 1% + 0.8%×运气 |
| 区域 80 | 80-119 层 | Cave Jelly 洞穴水母 | 5% + 5%×运气等级 |

### 5.3 实现步骤

1. 创建 `MineChestRewardManager.java` — 管理各层宝箱奖励数据（JSON 驱动或硬编码）
2. 在电梯层/整 10 层的矿洞生成逻辑中放置宝箱方块
3. 宝箱交互时：检查该层奖励是否已领取（存储在玩家数据中），未领取则给予物品
4. 矿洞水域：在 20 的倍数层添加水源方块，注册矿洞专属鱼到 `FishingDataManager`

---

## 6. 怪物武器掉落

### 6.1 当前状态

`MineMonsterDropHandler` 中 17 种怪物的掉落表**完全没有武器**。

### 6.2 需要添加的武器掉落（来自 SDV 源码）

| 怪物 | 掉落武器 | 概率 | 备注 |
|------|---------|------|------|
| **Skeleton** | Bone Sword | **4%** | `getExtraDropItems()` |
| **Haunted Skull** | Dark Sword (带 Vampiric 附魔) | **~1.3%** | Bat 变种 |

> 原版中只有这两种怪物掉落武器。Shadow Brute/Shaman **不掉落** Dark Sword（这是常见误解）。

### 6.3 实现步骤

1. 在 `MineMonsterDropHandler` 的 Skeleton 掉落表中添加 `bone_sword` (4%)
2. 如果 Haunted Skull 怪物存在，添加 `dark_sword` 掉落（需确认 Mod 是否有此怪物）
3. 如果 Haunted Skull 不存在，暂时搁置 Dark Sword 掉落

---

## 7. Galaxy 武器获取

### 7.1 设计（按用户要求适配）

**原版**：沙漠三柱 + 棱彩碎片 → Galaxy Sword
**Mod 适配**：玩家拿到棱彩碎片后去找法师 → 对话 → 获得 Galaxy Sword

### 7.2 获取流程

```
玩家获得 Prismatic Shard
  ├─ 捐赠给 Gunther → Gunther 提示："法师好像对这种物质很感兴趣"
  │   （添加 Gunther 对话分支，设置 hint 标记）
  └─ 手持 Prismatic Shard 与法师对话
      → 法师发现棱彩碎片的力量
      → 消耗 1 个 Prismatic Shard
      → 获得 Galaxy Sword
      → 设置 `galaxySword` 标记
      → 马龙商店解锁 Galaxy Dagger + Galaxy Hammer 购买
```

### 7.3 实现步骤

1. 在 `WizardQuestHandler.java` 或新建 `GalaxyWeaponHandler.java` 中添加逻辑：
   - 检查玩家手持物品是否为 Prismatic Shard
   - 未获取过 Galaxy Sword（`!galaxySword` 标记）
   - 触发对话序列 → 消耗碎片 → 给予 Galaxy Sword
2. 在 `GuntherService.java` 捐赠逻辑中添加：
   - 如果捐赠物为 Prismatic Shard → 对话提示法师感兴趣
3. 在 `ShopRegistry.java` 修改 Galaxy 武器条件为需要 `galaxySword` 标记
4. 翻译键：法师对话、Gunther 提示

### 7.4 Infinity 武器（暂不实现）

原版需要锻造系统（Galaxy 武器 + 3×Galaxy Soul），目前 Mod 无锻造台。
**从商店移除 Infinity Blade/Dagger**，待锻造系统实现后再开放。

---

## 8. 可合成戒指配方

### 8.1 需要确认/添加的合成配方

来自 SDV 源码 `CraftingRecipes.json`：

| 戒指 | 材料 | 解锁条件 | 现有状态 |
|------|------|---------|---------|
| Sturdy Ring | Copper Bar×2 + Bug Meat×25 + Slime×25 | Combat 1 | ✅ 已有 |
| Warrior Ring | Iron Bar×10 + Coal×25 + Frozen Tear×10 | Combat 4 | ✅ 已有 |
| Ring of Yoba | Gold Bar×5 + Iron Bar×5 + Diamond×1 | Combat 7 | ✅ 已有 |
| Thorns Ring | Bone Fragment×50 + Stone×50 + Gold Bar×1 | Combat 7 | ✅ 已有 |
| Iridium Band | Iridium Bar×5 + Solar Essence×50 + Void Essence×50 | Combat 9 | ✅ 已有 |
| Glowstone Ring | Solar Essence×5 + Iron Bar×5 | Mining 4 | ✅ 已有（需验证） |

### 8.2 从商店移除这些可合成戒指

上述 6 个戒指需从马龙商店移除。玩家只能通过合成获得。

---

## 9. 从商店移除的物品汇总

### 9.1 武器——移除

| 物品 | 原因 |
|------|------|
| neptunes_glaive | 钓鱼宝箱获取 |
| broken_trident | 钓鱼宝箱获取 |
| insect_head | Gil 奖励（杀 80 虫子） |
| dark_sword | Haunted Skull 怪物掉落 |
| meowmere | 特殊事件，非商店 |
| yeti_tooth | 矿洞宝箱（暂保留？或移除） |
| ossified_blade | 火山系统（未实现） |
| dwarf_sword | 火山系统（未实现） |
| dwarf_dagger | 火山系统（未实现） |
| dragontooth_cutlass | 火山系统（未实现） |
| dragontooth_shiv | 火山系统（未实现） |
| iridium_needle | 火山/骷髅洞（未实现） |
| infinity_blade | 需锻造系统 |
| infinity_dagger | 需锻造系统 |
| holy_blade | SDV 无此武器 |
| shadow_dagger | 掉落获取，非商店 |

### 9.2 戒指——移除

| 物品 | 原因 |
|------|------|
| slime_charmer_ring | Gil 奖励 |
| savage_ring | Gil 奖励 |
| vampire_ring | Gil 奖励 |
| burglar's_ring | Gil 奖励 |
| crabshell_ring | Gil 奖励 |
| napalm_ring | Gil 奖励 |
| ring_of_yoba | 合成获取 |
| sturdy_ring | 合成获取 |
| warrior_ring | 合成获取 |
| thorns_ring | 合成获取 |
| iridium_band | 合成获取 |
| glowstone_ring | 合成获取（需验证） |
| small_glow_ring | 矿洞/掉落获取 |
| glow_ring | 矿洞/掉落获取 |
| small_magnet_ring | 矿洞/掉落获取 |
| magnet_ring | 矿洞/掉落获取 |
| lucky_ring | 特殊掉落 |
| hot_java_ring | 火山（未实现） |
| protection_ring | 火山（未实现） |
| soul_sapper_ring | 火山（未实现） |
| phoenix_ring | 火山（未实现） |

### 9.3 最终保留在商店的物品

**武器**：Rusty Sword, Wooden Blade, Silver Saber, Steel Smallsword, Pirate's Sword, Forest Sword, Cutlass, Iron Edge, Claymore, Templar's Blade, Tempered Broadsword, Obsidian Edge, Steel Falchion, Lava Katana, Bone Sword + 新注册的棍棒 + Galaxy 系列（条件解锁）

**匕首**：Carving Knife, Iron Dirk, Wind Spire, Elf Blade, Burglar's Shank, Crystal Dagger, Wicked Kris + Galaxy Dagger（条件解锁）

**靴子**：Sneakers, Rubber Boots, Leather Boots, Work Boots, Combat Boots, Tundra Boots, Thermal Boots, Dark Boots, Firewalker Boots, Space Boots, Cowboy Boots

**戒指**：Immunity Band + 宝石戒指 (Amethyst/Topaz/Aquamarine/Jade/Emerald/Ruby Ring)

---

## 10. 缺失物品注册

### 10.1 棍棒/锤类武器（优先级：高）

| 物品 | SDV ID | 属性 (Min-Max/KB/Spd/Def/Crit) | 获取方式 |
|------|--------|-------------------------------|---------|
| Wood Club | (W)24 | 9-16, KB1.5, Spd-8 | 商店 + 掉落 |
| Wood Mallet | (W)27 | 15-24, KB1.3, Spd-4 | 商店（矿井 40 层） |
| Lead Rod | (W)26 | 18-27, KB1.5, Spd-16 | 商店/掉落 |
| Kudgel | (W)46 | 27-40, KB1.6, Spd-10 | 商店/掉落 |
| The Slammer | (W)28 | 40-55, KB1.5, Spd-12 | 矿洞宝箱 |
| Galaxy Hammer | (W)29 | 70-90, KB1.0, Spd-4 | 商店（需 galaxySword 标记） |
| Infinity Gavel | (W)63 | 100-120, KB1.0, Spd-4, Def1 | 锻造（暂不实现） |

> Dwarf Hammer / Dragontooth Club — 火山系统物品，暂不注册。

### 10.2 弹弓（优先级：待定——用户待确认）

| 物品 | SDV ID | 说明 |
|------|--------|------|
| Slingshot | (W)32 | 基础弹弓 |
| Master Slingshot | (W)33 | 进阶弹弓 |
| Galaxy Slingshot | (W)34 | 最高级弹弓 |

> 弹弓系统较为独立（使用弹药物品发射），需要单独的射击机制实现。用户表示"待会再看看"。

---

## 11. 实施路线图

### Phase A — 商店修正 + 物品注册（基础）

- [ ] a1. 注册缺失的棍棒/锤类武器（Wood Club, Wood Mallet, Lead Rod, Kudgel, The Slammer, Galaxy Hammer）到 `ModItems.java` + `WeaponRegistry.java`
- [ ] a2. 修改 `ShopRegistry.java`：
  - 为现有武器添加矿层解锁条件
  - 移除不应出售的武器/戒指（§9 列表）
  - 新增棍棒武器到商店（带解锁条件）
  - Galaxy 系列改为需要 `galaxySword` 标记
- [ ] a3. 添加矿层进度检查机制（`PlayerStardewDataAPI` 添加 `deepestMineLevel` 字段）
- [ ] a4. 构建验证

### Phase B — Gil 怪物猎杀奖励系统

- [ ] b1. 创建 `MonsterSlayerData.java`（SavedData）— 击杀计数 + 已领取奖励
- [ ] b2. 在 `MineMonsterDropHandler` 中添加怪物死亡计数逻辑
- [ ] b3. 创建 `MonsterSlayerGoalRegistry.java` — 13 项目标定义（JSON 驱动）
- [ ] b4. 在冒险商店 UI 添加"怪物猎杀目标"标签页
- [ ] b5. 奖励领取交互逻辑
- [ ] b6. 翻译键 + 构建验证

### Phase C — 钓鱼宝箱补全

- [ ] c1. 修改 `fishing_treasure.json`：添加武器/戒指/靴子/稀有物品池
- [ ] c2. 修改 `TreasureLootManager.java`：
  - 添加 case 2 武器/装备分支（Neptune's Glaive, Broken Trident, 戒指, 靴子）
  - 实现 specialItem 去重（`playerData.hasObtainedSpecialItem()`）
  - 实现 luckModifier 概率计算
- [ ] c3. 金色宝箱专属池 13 选 1 逻辑
- [ ] c4. 构建验证

### Phase D — 怪物武器掉落

- [ ] d1. 在 `MineMonsterDropHandler` Skeleton 掉落表添加 `bone_sword` (4%)
- [ ] d2. 确认 Haunted Skull 是否存在，如有则添加 `dark_sword` 掉落
- [ ] d3. 构建验证

### Phase E — Galaxy 武器获取

- [ ] e1. 创建 `GalaxyWeaponHandler.java` — 法师对话处理
  - 玩家手持 Prismatic Shard + 与法师交互
  - 消耗碎片 → 给予 Galaxy Sword → 设置 `galaxySword` 标记
- [ ] e2. 修改 `GuntherService.java` — 捐赠 Prismatic Shard 时的提示对话
- [ ] e3. 添加法师/Gunther 对话翻译键（中英文）
- [ ] e4. 构建验证

### Phase F — 矿洞宝箱（较大工程，与矿洞系统联动）

- [ ] f1. 创建宝箱方块（可交互，领取后消失/变空）
- [ ] f2. 在整 10 层生成逻辑中放置宝箱
- [ ] f3. 创建 `MineChestRewardData.java` — 各层奖励定义 + 已领取标记
- [ ] f4. 矿洞水域：每 20 层添加水源 + 注册矿洞专属鱼（Stonefish/Ice Pip/Lava Eel）
- [ ] f5. 构建验证

---

## 附录：SDV 马龙商店完整对照表

| SDV 物品 | SDV 价格 | SDV 解锁条件 | Mod 当前 | 修改计划 |
|----------|---------|-------------|---------|---------|
| Rusty Sword | 250g | 无 | ✅ 250g | ✅ 保持 |
| Wooden Blade | 250g | 无 | ✅ 250g | ✅ 保持 |
| Femur | 350g | 矿井 10 | ✅ 350g | 加条件 |
| Carving Knife | 100g | 无 | ✅ 100g | ✅ 保持 |
| Iron Dirk | 500g | 矿井 15 | ✅ 500g | 加条件 |
| Wind Spire | 500g | 矿井 15 | ✅ 500g | 加条件 |
| Silver Saber | 750g | 矿井 20 | ✅ 850g | 改价+条件 |
| Elf Blade | 750g | 矿井 20 | ✅ 600g | 改价+条件 |
| Steel Smallsword | 750g | 矿井 20 | ✅ 500g | 改价+条件 |
| Pirate's Sword | 850g | 矿井 25 | ✅ 750g | 改价+条件 |
| Cutlass | 1,500g | 矿井 25 | ✅ 750g | 改价+条件 |
| Burglar's Shank | 1,500g | 矿井 25 | ✅ 1500g | 加条件 |
| Wood Mallet | 2,000g | 矿井 40 | ❌ 缺失 | 新注册+加入 |
| Claymore | 2,000g | 矿井 45 | ✅ 2000g | 加条件 |
| Templar's Blade | 4,000g | 矿井 55 | ✅ 2500g | 改价+条件 |
| Crystal Dagger | 4,500g | 矿井 60 | ✅ 4500g | 加条件 |
| Bone Sword | 6,000g | 矿井 75 | ✅ 2000g | 改价+条件 |
| Wicked Kris | 3,000g | 矿井 80 | ✅ 3000g | 加条件 |
| Steel Falchion | 9,000g | 矿井 90 | ✅ 9000g | 加条件 |
| Obsidian Edge | 9,000g | 矿井 90 | ✅ 6000g | 改价+条件 |
| Lava Katana | 25,000g | 矿井 120 | ✅ 25000g | 加条件 |
| Galaxy Sword | 50,000g | galaxySword 标记 | ✅ 50000g | 改条件 |
| Galaxy Dagger | 35,000g | galaxySword 标记 | ✅ 35000g | 改条件 |
| Galaxy Hammer | 75,000g | galaxySword 标记 | ❌ 缺失 | 新注册+加入 |
| Slingshot | 500g | 矿井 40 | ❌ 缺失 | 待定 |
| Master Slingshot | 1,000g | 矿井 70 | ❌ 缺失 | 待定 |
