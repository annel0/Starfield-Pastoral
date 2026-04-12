# 装备系统开发规划

## 概述

将 SDV 原版的戒指/靴子装备系统移植到 MC mod 中。  
装备通过 V 键打开的 `StardewGameMenuScreen` **Tab 0（背包页）** 穿戴。  
背包页需从头实现，包含：MC 9×4 背包网格 + 左侧装备槽（2 戒指 + 1 靴子）+ 玩家模型预览 + 垃圾桶。

---

## 第一阶段：数据层——装备物品注册

### 1.1 戒指物品类 `StardewRingItem`

继承 `Item`，不用 MC 原版装备系统，纯自定义逻辑。  
每个戒指用 enum 或 registry ID 区分。

**战斗相关戒指（跳过 Wedding Ring 801）：**

| ID | 注册名 | AddEquipmentEffects | onMonsterSlay |
|---|---|---|---|
| 516 | small_glow_ring | — | — |
| 517 | glow_ring | — | — |
| 518 | small_magnet_ring | magneticRadius +64 | — |
| 519 | magnet_ring | magneticRadius +128 | — |
| 520 | slime_charmer_ring | 特殊：史莱姆免伤 flag | — |
| 521 | warrior_ring | — | 10% 概率 Warrior Buff |
| 522 | vampire_ring | — | 击杀 +2 HP |
| 523 | savage_ring | — | 击杀获得 Savage Buff |
| 524 | ring_of_yoba | 特殊：受伤时概率护盾 | — |
| 525 | sturdy_ring | 特殊：减半负面持续时间 | — |
| 526 | burglars_ring | 特殊：怪物掉落 ×2 | — |
| 527 | iridium_band | magneticRadius +128, attackMult ×1.1 | — |
| 529 | amethyst_ring | knockbackMult ×1.1 | — |
| 530 | topaz_ring | defense +1 | — |
| 531 | aquamarine_ring | critChanceMult ×1.1 | — |
| 532 | jade_ring | critPowerMult ×1.1 | — |
| 533 | emerald_ring | weaponSpeedMult ×1.1 | — |
| 534 | ruby_ring | attackMult ×1.1 | — |
| 810 | crabshell_ring | defense +5 | — |
| 811 | napalm_ring | — | 击杀处爆炸(半径2) |
| 839 | thorns_ring | 特殊：受伤反射 | — |
| 859 | lucky_ring | luck +1 | — |
| 860 | hot_java_ring | — | 25% 咖啡豆 / 10% 白花 |
| 861 | protection_ring | 特殊：伤害保护 | — |
| 862 | soul_sapper_ring | — | 击杀 +4 体力/饱食度 |
| 863 | phoenix_ring | 特殊：复活 | — |
| 887 | immunity_band | immunity +4 | — |
| 888 | glowstone_ring | magneticRadius +128 | — |

**光源戒指**（516/517/527/888）在 MC 中可能无法精确复刻 SDV 动态光源。  
方案：使用 dynamic lights mod 兼容 API，或简化为夜视效果 / 忽略光照属性只保留其他 buff。

**组合戒指（880 Combined Ring）**暂不实现——SDV 的铁砧合成在 MC 里需要单独 UI。

### 1.2 靴子物品类 `StardewBootsItem`

继承 `Item`，提供 defense + immunity 两个数值。

**战斗相关靴子（跳过 Emily's Magic Boots 804）：**

| ID | 注册名 | 防御 | 免疫 | 来源 |
|---|---|---|---|---|
| 504 | sneakers | 1 | 0 | 初始 / 商店 |
| 505 | rubber_boots | 0 | 1 | 商店 |
| 506 | leather_boots | 1 | 1 | 矿 10 层 |
| 507 | work_boots | 2 | 0 | 矿 20 层 |
| 508 | combat_boots | 3 | 0 | 矿 40 层 |
| 509 | tundra_boots | 2 | 1 | 矿 50 层 |
| 510 | thermal_boots | 1 | 2 | 商店 |
| 511 | dark_boots | 4 | 2 | 矿 80 层 |
| 512 | firewalker_boots | 3 | 3 | 矿 80 层 |
| 513 | genie_shoes | 1 | 6 | 矿 100 层宝箱 |
| 514 | space_boots | 4 | 4 | 矿 110 层 |
| 515 | cowboy_boots | 2 | 2 | 商店 |
| 806 | leprechaun_shoes | 2 | 1 | 特殊掉落 |
| 853 | cinderclown_shoes | 6 | 5 | 矿井深层宝箱 |
| 854 | mermaid_boots | 5 | 8 | 矿井深层宝箱 |
| 855 | dragonscale_boots | 7 | 0 | 矿井深层宝箱 |
| 878 | crystal_shoes | 3 | 5 | 矿井深层宝箱 |

### 1.3 物品注册

在 `ModItems.java` 中注册所有戒指和靴子的 `DeferredHolder<Item>`。  
所有戒指/靴子都是 maxStackSize=1、不可放置的纯 inventory item。

---

## 第二阶段：玩家装备数据持久化

### 2.1 `PlayerEquipmentData`

存 3 个 ItemStack：leftRing、rightRing、boots。  
使用 `IAttachmentHolder` / player persistent data（与现有 `MiningPlayerData` 同模式）。

```
PlayerEquipmentData {
    ItemStack leftRing;   // 可为空
    ItemStack rightRing;  // 可为空
    ItemStack boots;      // 可为空
}
```

### 2.2 存档 & 网络同步

- **存档**：序列化到 player capability / saved data（与 MiningPlayerData 同方式）
- **同步**：装备变更时发 packet 到客户端，客户端缓存用于 UI 渲染

---

## 第三阶段：装备效果系统

### 3.1 Buff 型效果

装备/卸下戒指/靴子时，重新计算总 `EquipmentStats`（已有框架）：

```java
EquipmentStats total = EquipmentStats.merge(
    leftRing.getStats(),
    rightRing.getStats(),
    boots.getStats()
);
```

通过 `AttributeModifier` 应用到玩家：
- `defense` → MC Armor attribute
- `immunity` → 自定义：减少 debuff 概率（已有 ImmunitySystem）
- `attackMult` / `critChanceMult` 等 → 武器系统已有的乘数
- `magneticRadius` → 增大拾取范围（MC 不原生支持，需 event hook）
- `luck` → 自定义幸运属性

### 3.2 击杀触发型效果

在已有的 `LivingDeathEvent` 或怪物死亡 handler 中检测玩家装备的戒指：

```java
if (hasRing("vampire_ring"))  player.heal(2);
if (hasRing("savage_ring"))   applyBuff(SAVAGE);
if (hasRing("warrior_ring"))  if (random(0.1)) applyBuff(WARRIOR);
if (hasRing("napalm_ring"))   explodeAt(monster.pos, 2);
if (hasRing("soul_sapper_ring"))  player.getFoodData().eat(4);
if (hasRing("hot_java_ring")) { 25%→coffee_bean, 10%→triple_shot_espresso }
```

### 3.3 特殊标记型效果

| 戒指 | 实现方式 |
|---|---|
| Slime Charmer (520) | 在 DamageEvent 中检查来源是否 Slime，是则 cancel |
| Yoba Ring (524) | 受伤时概率(~50%)触发无敌帧 buff |
| Sturdy Ring (525) | debuff 施加时持续时间 ×0.5 |
| Burglar's Ring (526) | 怪物掉落计算时 double rolls |
| Thorns Ring (839) | 受伤时反射部分伤害给攻击者 |
| Protection Ring (861) | 受伤时概率减少伤害 |
| Phoenix Ring (863) | 死亡时一次性复活（冷却时间内） |

---

## 第四阶段：V 键背包页 UI

### 4.1 布局设计

`StardewGameMenuScreen` Tab 0 目前是 placeholder。需实现 `drawInventoryPage()`。

**MC 适配 SDV 布局：**

```
┌─────────────────────────────────────────────────────────┐
│                    800px 内容区                          │
│                                                         │
│  ┌──────┐                           ┌──────────────┐    │
│  │戒指 L│  ┌──────────┐             │  农场名       │    │
│  ├──────┤  │ 玩家模型  │             │  金币数       │    │
│  │戒指 R│  │  预览      │             │  总收入       │    │
│  ├──────┤  │ (64×96)   │             │  日期         │    │
│  │ 靴子 │  └──────────┘             └──────────────┘    │
│  └──────┘      玩家名                                    │
│                                                         │
│  ─────────── 分割线 ────────────────────────  [整理] [🗑] │
│                                                         │
│  ┌──┬──┬──┬──┬──┬──┬──┬──┬──┐                          │
│  │  │  │  │  │  │  │  │  │  │  ← 背包行 1 (slot 9-17)  │
│  ├──┼──┼──┼──┼──┼──┼──┼──┼──┤                          │
│  │  │  │  │  │  │  │  │  │  │  ← 背包行 2 (slot 18-26) │
│  ├──┼──┼──┼──┼──┼──┼──┼──┼──┤                          │
│  │  │  │  │  │  │  │  │  │  │  ← 背包行 3 (slot 27-35) │
│  ├──┼──┼──┼──┼──┼──┼──┼──┼──┤                          │
│  │  │  │  │  │  │  │  │  │  │  ← 快捷栏 (slot 0-8)     │
│  └──┴──┴──┴──┴──┴──┴──┴──┴──┘                          │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

**差异适配（SDV → MC）：**

| SDV | MC mod |
|---|---|
| 12 列 × 3 行 + 12 快捷栏 = 36 槽 | **9 列 × 3 行 + 9 快捷栏 = 36 槽**（总数一样） |
| Hat / Shirt / Pants 装备槽 | **不实现**（MC 已有原生盔甲系统） |
| Trinket 槽 | **不实现**（暂无） |
| 左右各 3 个装备槽竖排 | **左侧 3 个竖排：戒指L / 戒指R / 靴子** |
| 玩家模型居中 | 玩家模型居中（渲染 MC 玩家实体） |
| 农场名/金币/日期 右侧 | 右侧信息区 |

### 4.2 交互逻辑

复刻 SDV `InventoryPage.receiveLeftClick()` 的逻辑：

1. **点击装备槽**：
   - 如果手持对应类型物品（Ring/Boots）→ 交换（放入槽位，取出旧的）
   - 如果手持为空 → 取出已装备物品
   - 类型不匹配 → 无操作
   - 音效：戒指 `crit`，靴子 `sandyStep`×2

2. **点击背包格**：
   - 标准 MC inventory 交互（拿取/放下/合并堆叠）
   - Shift+点击：自动装备到空的对应槽位

3. **垃圾桶**：
   - 拖放物品到垃圾桶 → 销毁
   - 桶盖旋转动画（已有实现）

4. **整理按钮**：
   - 点击自动排序背包

### 4.3 网络通信

装备操作需要服务端验证：
- `EquipmentActionPayload`：客户端发送装备/卸下请求
- 服务端验证物品类型 → 执行装备 → 同步回客户端
- 使用与 `CraftingMenuInventoryActionPayload` 类似的模式

---

## 第五阶段：获取途径

### 5.1 矿井掉落

在已有的 `MineBarrelBlock.dropBarrelLoot()` 和怪物掉落中添加戒指/靴子：

**靴子来源（按层级）：**
- 10-39 层：Leather Boots (506)
- 40-79 层：Combat Boots (508), Tundra Boots (509)
- 80-109 层：Dark Boots (511), Firewalker Boots (512)
- 110-120 层：Space Boots (514)
- 深层宝箱：Cinderclown/Mermaid/Dragonscale/Crystal

**戒指来源：**
- 矿石宝石戒指（529-534）：对应宝石 + 金锭合成
- 功能戒指（520-526）：冒险家公会商店购买
- 高级戒指（810/811/839/859-863/887/888）：矿井宝箱/特殊掉落

### 5.2 冒险家公会商店

在已有的 `ShopRegistry` 中添加戒指/靴子商品。

### 5.3 合成配方

宝石戒指系列：
- Amethyst Ring = 紫水晶 ×5 + 金锭 ×5
- Topaz Ring = 黄晶 ×5 + 金锭 ×5
- Aquamarine Ring = 海蓝宝石 ×5 + 金锭 ×5
- Jade Ring = 翡翠 ×5 + 金锭 ×5
- Emerald Ring = 祖母绿 ×5 + 金锭 ×5
- Ruby Ring = 红宝石 ×5 + 金锭 ×5
- Iridium Band = 铱锭 ×5 + 红宝石 ×5 + 紫水晶 ×5
- Glowstone Ring = Glow Ring + Magnet Ring

---

## 第六阶段：贴图 & 模型

### 6.1 贴图文件

每个戒指/靴子需要 16×16 物品贴图：
- `textures/item/ring/` 下 28 张戒指贴图
- `textures/item/boots/` 下 17 张靴子贴图

### 6.2 模型 JSON

标准 MC item model，指向对应贴图。

### 6.3 装备槽空位图标

需要 3 张 64×64 SDV 风格空位图标：
- `ring_slot_empty.png` — 戒指轮廓
- `boots_slot_empty.png` — 靴子轮廓

---

## 开发顺序优先级

| 优先级 | 任务 | 依赖 |
|---|---|---|
| **P0** | `StardewRingItem` + `StardewBootsItem` 物品类 | 无 |
| **P0** | `PlayerEquipmentData` 持久化 | 无 |
| **P0** | `ModItems` 注册全部戒指/靴子 | P0 物品类 |
| **P1** | Tab 0 背包页 UI（背包网格 + 装备槽 + 交互） | P0 全部 |
| **P1** | 装备 buff 效果（EquipmentStats 集成） | P0 数据层 |
| **P1** | 击杀/受伤事件 hook | P0 数据层 |
| **P2** | 矿井掉落/商店/合成获取途径 | P0 注册 |
| **P2** | 贴图绘制 | P0 注册 |
| **P3** | 组合戒指(880) | P1 UI |
| **P3** | 动态光源（glow ring 系列） | 视 API 可用性 |

---

## 技术注意点

1. **不使用 MC 原版盔甲系统**：戒指/靴子是纯自定义 Item，不继承 ArmorItem
2. **装备槽在 V 键 UI 中操作**，不在 E 键背包操作——避免与 MC 原版冲突
3. **buff 计算**：每次装备变更时全量重算 `EquipmentStats.merge()`，通过 `AttributeModifier` 应用
4. **背包交互**直接操作 `player.getInventory()`，不创建新 Container/Menu——因为这是纯客户端渲染+网络同步模式（与 crafting tab 一致）
5. **MC 背包 9×4 = 36 格**（slot 0-8 快捷栏，9-35 背包），与 SDV 36 格数量一致，区别仅在列数（9 vs 12）
