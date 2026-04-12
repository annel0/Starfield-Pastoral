# 矿洞怪物系统 & 矿洞优化 — 综合设计文档

> 基于 SDV 源码 (`源文件/StardewValley.Monsters/`, `MineShaft.cs`) 和现有代码库全面分析。  
> 日期：2026-04-08

---

## 一、现状审计

### 1.1 矿洞生成系统 (`MineFloorGenerator.java` v7)

| 维度 | 现状 | 问题 |
|------|------|------|
| 房间尺寸 | 80×80 ~ 120×120，高度 20 层 (Y=62-81) | **远超 SDV 原版**（SDV 单层约 40×30 格）。MC 中 80×80×20 = 128,000 方块，每层生成极慢 |
| 间距 | `floor * 500 + 14`（Z方向），但代码注释写 `floor * 100` | **注释与代码不一致**；500 间距浪费空间 |
| 洞窟 | 虫蚀通道 2-5 条 + 大型洞穴室 1-5 个 | 洞穴室半径 6-12，在 80×80 房间里比例合理，但 **光照全黑**（void 世界无天光） |
| 矿石 | 铜/铁/金/铱/煤 + 宝石矿 + 表面矿物 | ✅ 完善 |
| 安全区 | 中心 3×3×3，3 个 mine_exit，每 5 层电梯 | ✅ 基本可用 |
| 怪物 | **完全没有生成逻辑** | ❌ 关键缺失 |
| 每日刷新 | 检查游戏日，同日不重新生成 | ✅ 正确 |
| 光照 | 无任何光源 | ❌ void 世界全黑，玩家无法看到任何东西 |

### 1.2 战斗系统 (`combat/`)

| 组件 | 状态 | 说明 |
|------|------|------|
| `DamageCalculator` | ✅ 完善 | 6 区乘算：Base × Crit × Profession × Skill × Buff × Variance - Defense |
| `MonsterStats` | ✅ 框架完成 | NBT 存储 damage/resilience/missChance/experience/isDangerous |
| `WeaponCombatEvents` | ✅ 完善 | 已有 `sd_mob_*` tag 识别体系 + 经验值映射 |
| `EquipmentStats` | ✅ 完善 | 戒指/靴子属性合并 |
| 武器系统 | ✅ 完善 | 4 类武器 + 51+ 技能追踪器 |
| 怪物掉落钩子 | ✅ `BurglarLootHooks` | 框架存在但未接入 |
| 自定义怪物实体 | ❌ 没有 | `ModEntities` 无怪物注册 |

### 1.3 物品注册 (`ModItems.java`)

| 类别 | 状态 |
|------|------|
| 矿石/锭 (铜铁金铱煤) | ✅ 已注册 |
| 宝石 (紫水晶/黄玉/红宝石等) | ✅ 已注册 |
| 钻石/棱彩碎片 | ✅ 已注册 |
| 晶洞 | ✅ 已注册 |
| 矮人卷轴 I-IV | ✅ 已注册 |
| 电池组 | ✅ 已注册 |
| 布料 | ✅ 已注册 |
| 绿藻/白藻 | ✅ 已注册（fishing/misc 包） |
| 鱿鱼墨汁 | ✅ 已注册（VanillaCategoryItemRegistrar） |
| 树液 (Sap) | ✅ 已注册 |
| 兔子脚 | ✅ 已注册 |
| **史莱姆泥 (Slime)** | ❌ 缺失 |
| **蝙蝠翅膀 (Bat Wing)** | ❌ 缺失 |
| **太阳精华 (Solar Essence)** | ❌ 缺失 |
| **虚空精华 (Void Essence)** | ❌ 缺失 |
| **骨头碎片 (Bone Fragment)** | ❌ 缺失 |
| **虫肉 (Bug Meat)** | ❌ 缺失 |
| **炸弹系列 (Cherry Bomb / Bomb / Mega Bomb)** | ❌ 缺失 |
| **怪物鱼子酱 (Monster Musk)** | ❌ 缺失（低优先级） |

---

## 二、怪物映射方案 — MC 原版怪物 + SDV 属性

### 2.1 设计原则

1. **不创建自定义实体**：使用 MC 原版怪物 + Entity Tag (`sd_mob_*` + `sd_tier_*`) + NBT (`MonsterStats`) 标记身份
2. **不改显示名**：MC 原版名字保留（Slime 就是 Slime，Skeleton 就是 Skeleton）
3. **属性覆盖**：通过 `AttributeModifier` 在生成时覆盖 HP/攻击/速度/护甲
4. **掉落表**：用数据包 loot_table 挂载，按 tag 条件切换
5. **利用现有战斗系统**：`WeaponCombatEvents.getCombatExperienceOnKill()` 已经按 `sd_mob_*` 分派经验

### 2.2 完整映射表

#### 第 1-39 层 · Earth 段

| SDV 怪物 | MC 实体 | Tag | HP | 攻击 | 护甲 | 速度 | XP | 特殊行为 |
|----------|---------|-----|-----|------|------|------|-----|---------|
| Green Slime | `minecraft:slime` (size=1) | `sd_mob_slime` | 24 | 5 | 0 | 0.25 | 3 | — |
| Bat | `minecraft:bat` → **`minecraft:phantom`** (size=1) | `sd_mob_bat` | 24 | 6 | 0 | 0.3 | 5 | Phantom 天然飞行+俯冲 |
| Duggy | `minecraft:silverfish` | `sd_mob_duggy` | 40 | 6 | 0 | 0.2 | 5 | 从方块中冒出（silverfish 天然特性） |
| Rock Crab | `minecraft:silverfish` | `sd_mob_crab` | 30 | 5 | 8 | 0.2 | 5 | 高护甲模拟外壳 |
| Grub | `minecraft:endermite` | `sd_mob_grub` | 20 | 4 | 0 | 0.15 | 2 | 小型虫 |
| Fly | `minecraft:vex` | `sd_mob_fly` | 22 | 6 | 0 | 0.3 | 3 | 飞行小怪（去掉穿墙） |
| Rock Golem | `minecraft:zombie` + 铁护甲 | `sd_mob_golem` | 45 | 5 | 10 | 0.18 | 10 | 高防 |

#### 第 40-79 层 · Frost 段

| SDV 怪物 | MC 实体 | Tag | HP | 攻击 | 护甲 | 速度 | XP | 特殊行为 |
|----------|---------|-----|-----|------|------|------|-----|---------|
| Frost Jelly | `minecraft:slime` (size=2) | `sd_mob_slime` + `sd_tier_2` | 106 | 7 | 0 | 0.25 | 5 | — |
| Frost Bat | `minecraft:phantom` (size=2) | `sd_mob_bat` + `sd_tier_2` | 36 | 7 | 0 | 0.3 | 7 | — |
| Dust Spirit | `minecraft:endermite` | `sd_mob_dust_sprite` | 40 | 6 | 2 | 0.35 | 3 | 快速小怪 |
| Ghost | `minecraft:vex` | `sd_mob_ghost` | 96 | 10 | 2 | 0.3 | 15 | Vex 穿墙 |
| Skeleton | `minecraft:skeleton` | `sd_mob_skeleton` | 140 | 10 | 2 | 0.25 | 15 | 原版远程 |

#### 第 80-119 层 · Lava 段

| SDV 怪物 | MC 实体 | Tag | HP | 攻击 | 护甲 | 速度 | XP | 特殊行为 |
|----------|---------|-----|-----|------|------|------|-----|---------|
| Sludge | `minecraft:slime` (size=3) | `sd_mob_slime` + `sd_tier_3` | 205 | 16 | 0 | 0.25 | 6 | — |
| Lava Bat | `minecraft:phantom` (size=3) | `sd_mob_bat` + `sd_tier_3` | 80 | 15 | 0 | 0.35 | 15 | — |
| Lava Crab | `minecraft:silverfish` | `sd_mob_crab` + `sd_tier_2` | 120 | 15 | 12 | 0.25 | 8 | 极高护甲 |
| Metal Head | `minecraft:zombie` | `sd_mob_metal_head` | 80 | 15 | 16 | 0.2 | 15 | 超高护甲 |
| Shadow Brute | `minecraft:wither_skeleton` | `sd_mob_shadow` | 160 | 18 | 4 | 0.3 | 15 | — |
| Shadow Shaman | `minecraft:evoker` | `sd_mob_shadow` + `sd_tier_2` | 80 | 17 | 2 | 0.25 | 15 | 法术投射 |
| Squid Kid | `minecraft:blaze` | `sd_mob_squid` | 50 | 18 | 2 | 0.25 | 10 | 火球投射 |

> **注意**：
> - Phantom 取代原版 Bat（MC 蝙蝠是被动生物无法攻击），Phantom size 属性可控制大小
> - Slime 的 size 属性天然控制 HP 和碰撞箱，但我们需要通过 `AttributeModifier` 覆盖为 SDV 数值
> - Wither Skeleton 自带石剑和黑色外观，天然适合 Shadow Brute
> - Evoker 自带法术攻击（唤魔者之牙），模拟 Shadow Shaman

### 2.3 Entity Tag 体系

已有（`WeaponCombatEvents.java` 第 984-1014 行）的 tag：
```
sd_mob_slime, sd_mob_bat, sd_mob_fly, sd_mob_grub, sd_mob_bug,
sd_mob_dust_sprite, sd_mob_skeleton, sd_mob_ghost, sd_mob_mummy,
sd_mob_serpent, sd_mob_crab, sd_mob_golem, sd_mob_shadow,
sd_mob_duggy, sd_mob_metal_head, sd_mob_squid
```

层级 tag（tier 越高越强）：
```
sd_tier_2  — 中级变体 (Frost Jelly, Frost Bat, Lava Crab, Shadow Shaman)
sd_tier_3  — 高级变体 (Sludge, Lava Bat, Skeleton Mage)
sd_tier_4  — 铱级变体 (Iridium Bat)
sd_tier_5  — 传奇变体 (保留)
```

---

## 三、新增物品注册

### 3.1 怪物掉落物品清单

新增物品类型 key：`stardewcraft.type.monster_loot`（颜色：红色 `§c`）

| 物品 ID | SDV ID | 英文名 | 中文名 | 售价(g) | 描述(EN) | 描述(ZH) | 可堆叠 |
|---------|--------|--------|--------|---------|----------|----------|--------|
| `slime` | 766 | Slime | 史莱姆泥 | 5 | It's slimy. | 黏黏的东西。 | 999 |
| `bat_wing` | 767 | Bat Wing | 蝙蝠翅膀 | 15 | Rather leathery. | 相当有韧性的皮质。 | 999 |
| `solar_essence` | 768 | Solar Essence | 太阳精华 | 40 | The glowing face is warm to the touch. | 这张发光的脸摸起来很温暖。 | 999 |
| `void_essence` | 769 | Void Essence | 虚空精华 | 50 | It's \"essence of void\". | 这就是"虚空的精华"。 | 999 |
| `bone_fragment` | 881 | Bone Fragment | 骨头碎片 | 12 | Thisite could be useful for crafting. | 也许可以用来做些东西。 | 999 |
| `bug_meat` | 684 | Bug Meat | 虫肉 | 8 | It's gross, but some creatures love the taste. | 虽然恶心，但某些生物喜欢这味道。 | 999 |

### 3.2 炸弹系列（功能物品，非纯掉落）

| 物品 ID | SDV ID | 英文名 | 中文名 | 售价(g) | 说明 |
|---------|--------|--------|--------|---------|------|
| `cherry_bomb` | 286 | Cherry Bomb | 樱桃炸弹 | 50 | 小范围爆炸（3×3），可合成 |
| `bomb` | 287 | Bomb | 炸弹 | 50 | 中范围爆炸（5×5），可合成 |
| `mega_bomb` | 288 | Mega Bomb | 超级炸弹 | 50 | 大范围爆炸（7×7），可合成 |

> 炸弹类型暂为 `stardewcraft.type.resource`（灰色），功能实现（右键放置→引爆→破坏石头）为后续任务。

### 3.3 纹理资产来源

所有纹理从 SDV 原版 `Cursors.png` 或 `ObjectInformation.xnb` 截取：

```
src/main/resources/assets/stardewcraft/textures/item/
├── monster_loot/          ← 新建目录
│   ├── slime.png          ← SDV 766: 16×16
│   ├── bat_wing.png       ← SDV 767: 16×16
│   ├── solar_essence.png  ← SDV 768: 16×16
│   ├── void_essence.png   ← SDV 769: 16×16
│   ├── bone_fragment.png  ← SDV 881: 16×16
│   └── bug_meat.png       ← SDV 684: 16×16
├── bomb/                  ← 新建目录
│   ├── cherry_bomb.png    ← SDV 286: 16×16
│   ├── bomb.png           ← SDV 287: 16×16
│   └── mega_bomb.png      ← SDV 288: 16×16
```

### 3.4 物品模型 JSON

每个物品需创建 `models/item/<id>.json`：
```json
{
  "parent": "minecraft:item/generated",
  "textures": {
    "layer0": "stardewcraft:item/monster_loot/slime"
  }
}
```

### 3.5 注册代码模板

```java
// ── 怪物掉落物品 ──
public static final DeferredItem<Item> SLIME_DROP = ITEMS.register("slime",
    () -> new SimpleStardewItem("stardewcraft.type.monster_loot", 5,
        new Item.Properties().stacksTo(999)));

public static final DeferredItem<Item> BAT_WING = ITEMS.register("bat_wing",
    () -> new SimpleStardewItem("stardewcraft.type.monster_loot", 15,
        new Item.Properties().stacksTo(999)));

public static final DeferredItem<Item> SOLAR_ESSENCE = ITEMS.register("solar_essence",
    () -> new SimpleStardewItem("stardewcraft.type.monster_loot", 40,
        new Item.Properties().stacksTo(999)));

public static final DeferredItem<Item> VOID_ESSENCE = ITEMS.register("void_essence",
    () -> new SimpleStardewItem("stardewcraft.type.monster_loot", 50,
        new Item.Properties().stacksTo(999)));

public static final DeferredItem<Item> BONE_FRAGMENT = ITEMS.register("bone_fragment",
    () -> new SimpleStardewItem("stardewcraft.type.monster_loot", 12,
        new Item.Properties().stacksTo(999)));

public static final DeferredItem<Item> BUG_MEAT = ITEMS.register("bug_meat",
    () -> new SimpleStardewItem("stardewcraft.type.monster_loot", 8,
        new Item.Properties().stacksTo(999)));
```

---

## 四、掉落表 (Loot Table) 设计

### 4.1 方案：数据包条件掉落

使用 MC 原版 loot_table + entity_properties 条件。由于 MC 不支持直接匹配 Entity Tag（scoreboard tag），我们需要一个轻量 `LivingDropsEvent` 监听器。

### 4.2 掉落事件处理器

```
MineMonsterDropHandler.java
├── @SubscribeEvent onLivingDrops(LivingDropsEvent)
│   ├── 检查实体是否在矿洞维度
│   ├── 读取 sd_mob_* tag 确定怪物类型
│   ├── 读取 sd_tier_* 确定变体
│   ├── 按 SDV 概率表 roll 掉落
│   └── 添加 ItemEntity 到 drops 列表
```

### 4.3 精确掉落表（从 SDV `parseMonsterInfo` + `getExtraDropItems` 提取）

#### Green Slime / Frost Jelly / Sludge (`sd_mob_slime`)

| 物品 | 概率 | 条件 |
|------|------|------|
| Slime (766) | 75% | 所有层级 |
| Slime (766) | 5% | 额外一个 |
| Green Algae (153) | 10% | tier 1 |
| ??? (66=Amethyst) | 1.5% | tier 1 |
| Sap (92) | 15% | 所有 |
| Dwarf Scroll I (96) | 0.5% | 所有 |
| Dwarf Scroll IV (99) | 0.1% | 所有 |

> SDV 66 = Amethyst（紫水晶）— 已注册

#### Bat / Frost Bat / Lava Bat (`sd_mob_bat`)

| 物品 | 概率 | 条件 |
|------|------|------|
| Bat Wing (767) | 90% | 所有 |
| Bat Wing (767) | 40%/55%/70% | tier 1/2/3 额外 |
| ??? (108=Rare Disc) | 0.1% | 所有 |
| Bomb (287) | 2% | 所有 |
| Dwarf Scroll I/II/III (96/97/98) | 0.5% | 按 tier |
| Dwarf Scroll IV (99) | 0.1% | 所有 |

> SDV 108 = Ancient Disc（古老光盘）— 需要确认是否已注册为 artifact

#### Iridium Bat (`sd_mob_bat` + `sd_tier_4`)

| 物品 | 概率 |
|------|------|
| Iridium Ore (386) | 90% + 50% + 25% + 10% (多次roll) |
| Mega Bomb (288) | 5% |
| Solar Essence (768) | 50% |
| ??? (773=Life Elixir) | 5% |
| ??? (349=Energy Tonic) | 5% |
| Battery Pack (787) | 5% |
| Iridium Bar (337) | 0.8% |

#### Duggy (`sd_mob_duggy`)

| 物品 | 概率 |
|------|------|
| Cherry Bomb (286) | 25% |
| Geode (535) | 25% |
| ??? (280=Crystal Fruit) | 3% |
| ??? (105=Snail) | 2% |
| ??? (86=Earth Crystal) | 10% |
| Diamond (72) | 1% |
| Dwarf Scroll I (96) | 0.5% |
| Dwarf Scroll IV (99) | 0.1% |

> SDV 86 = Earth Crystal → 已注册 (`ModBlocks.EARTH_CRYSTAL`)  
> SDV 535 = Geode → 已注册  
> SDV 280 = ??? (需查) → 可能暂时跳过

#### Rock Crab / Lava Crab (`sd_mob_crab`)

| 物品 | 概率 | 条件 |
|------|------|------|
| Crab (717) | 15%/25% | tier 1/2 |
| Cherry Bomb (286) / Bomb (287) | 40% | tier 1/2 |
| Dwarf Scroll I/III (96/98) | 0.5% | 按 tier |
| Dwarf Scroll IV (99) | 0.1% | 所有 |

> SDV 717 = Crab → 已注册 (`CrabItem`)

#### Ghost (`sd_mob_ghost`)

| 物品 | 概率 |
|------|------|
| Solar Essence (768) | 95% + 10% |
| Ghost Fish (156) | 8% |
| Refined Quartz (338) | 8% |
| Dwarf Scroll II (97) | 0.5% |
| Dwarf Scroll IV (99) | 0.1% |

> SDV 156 = Dwarf Gadget — 需确认；也可能是 Ghost Fish → 需查

#### Metal Head (`sd_mob_metal_head`)

| 物品 | 概率 |
|------|------|
| Solar Essence (768) | 65% |
| Copper Ore (378) | 10% × 2 |
| Iron Ore (380) | 10% × 2 |
| Gold Ore (382) | 10% |
| Dwarf Scroll III (98) | 0.5% |
| Dwarf Scroll IV (99) | 0.1% |

#### Shadow Brute / Shadow Shaman (`sd_mob_shadow`)

| 物品 | 概率 |
|------|------|
| Void Essence (769) | 75% + 10%/20% |
| Iridium Bar (337) | 0.2% |
| Gold Bar (336) | 1% |
| Iron Bar (335) | 2% |
| Copper Bar (334) | 4% |
| Pumpkin Soup (203) | 4% (Brute only) |
| Rare Disc (108) | 0.3% |
| Dwarf Scroll III (98) | 0.5% |
| Dwarf Scroll IV (99) | 0.1% |
| Prismatic Shard (74) | 0.05% |

#### Skeleton (`sd_mob_skeleton`)

| 物品 | 概率 |
|------|------|
| Bone Fragment (881) | 50% + 40% + 20% |
| Prehistoric Tibia (579) | 0.5% |

> SDV 579 = Prehistoric Tibia → 应该已注册为 artifact

#### Squid Kid (`sd_mob_squid`)

| 物品 | 概率 |
|------|------|
| Solar Essence (768) | 75% |
| Squid Ink (814) | 20% |
| Gold Bar (336) | 5% |
| Bomb (287) | 10% |
| Mega Bomb (288) | 5% |
| Dwarf Scroll III (98) | 0.5% |
| Dwarf Scroll IV (99) | 0.1% |

---

## 五、矿洞系统优化方案

### 5.1 房间尺寸优化

**现状**：80-120 格边长，高度 20 层。生成较慢但空间宽裕。

**决策**：保持原始尺寸不变，空间宽裕，内容丰富。仅提升 GENERATION_VERSION 以触发重新生成。

```
当前/保持:  MIN_SIZE=80,  MAX_SIZE=120, FLOOR_HEIGHT=20
```

| 参数 | 值 | 说明 |
|------|------|------|
| MIN_SIZE | 80 | 保持原样 |
| MAX_SIZE | 120 | 保持原样 |
| FLOOR_HEIGHT | 20 | 保持原样 |
| GENERATION_VERSION | 7 → 8 | 触发已有楼层重新生成 |

### 5.2 光照系统

**问题**：void 世界无天光，矿洞内全黑。SDV 矿洞有火把和自然发光矿物。

**方案**：在矿洞生成阶段放置光源方块

```java
// 在 generateFloor() 步骤 5 之后添加：
// 11. 放置光源（火把 / 萤石 / 自定义光源方块）
generateLighting(level, random, centerX, centerZ, size, theme);
```

光源策略：
1. **洞窟入口区域**：安全区固定放 4 个火把
2. **洞窟内部**：每个洞穴室中心放 1 个光源（MC 火把/萤石/海晶灯按主题）
3. **通道交叉点**：低概率(10%)放火把
4. **Dark 层特殊处理**：光源密度降至 1/3（`isDark=true` 时）
5. **主题光源**：
   - Earth: `torch`（火把）
   - Frost: `sea_lantern`（海晶灯）
   - Lava: `shroomlight` 或 `magma_block`（自发光）

### 5.3 楼梯（下层入口）动态生成系统 ★ 重点优化

#### 5.3.1 现状与问题

**当前实现**：
- 楼梯在 `generateFloor()` 阶段**预先放置**（`generateLevelExit()` 方法）
- 随机选取距中心最远的表面石头位置，直接放置 `MINE_LADDER` 方块
- `LadderProbabilityCalculator` 已完整实现 SDV 公式但**完全没有使用**
- `MiningBlockBreakHandler` 只做 `stonesLeft` 计数递减，不触发任何梯子逻辑
- 高亮使用 `Display.BlockDisplay` 实体 + 原版 `setGlowingTag(true)`（黄色轮廓，渲染距离有限，视觉效果差）

**问题**：
1. 预放置楼梯没有挖掘成就感，玩家进入楼层就已经有出口
2. SDV 核心乐趣是「挖石头→概率出楼梯→越挖概率越高」的渐进正反馈
3. 原版发光效果只有黄色、只在一定距离内渲染，且是 1px 线框，远距离看不到
4. 没有「提醒玩家楼梯已出现」的全局指示

#### 5.3.2 新方案：动态楼梯生成

**核心流程**：

```
玩家挖掘石头 (MiningBlockBreakHandler.onBlockBreak)
    │
    ├── stonesLeft-- (已有)
    │
    ├── 如果 ladderFound = true → 跳过（该层楼梯已出现）
    │
    ├── 调用 LadderProbabilityCalculator.shouldGenerateLadder()
    │   │
    │   ├── p = 0.012 + 0.6/max(1, stonesLeft) + luckLevel/100 + dailyLuck/5
    │   ├── 若该层无敌人: p += 0.02
    │   ├── 若有矮人Buff: p *= 1.15
    │   └── roll < p → 触发！
    │
    ├── 楼梯放置位置 = 刚被挖掉的石头的位置（原地生成！）
    │   （SDV 原版就是在你挖碎的石头原位置出现楼梯）
    │
    ├── level.setBlock(breakPos, MINE_LADDER, 3)
    ├── floorData.setLadderFound(true)
    ├── floorData.setLadderPos(breakPos)
    │
    ├── 播放发现音效 (ModSounds.SHINY4 或类似)
    │
    └── 发送 C2S 数据包 → 客户端开始渲染楼梯高亮
```

**关键设计点**：
- **原地生成**：楼梯出现在玩家刚挖碎的石头位置，不是预设位置。这比「在某个角落预放好」有更强的发现感
- **概率递增**：初始概率极低(~1.2%)，随着 stonesLeft 减少概率迅速升高，保证不会挖太久
- **杀敌加成**：如果该层所有怪物都被清除，概率 +2%（鼓励战斗）
- **保底机制**：当 stonesLeft ≤ 5 时概率接近 100%（不会永远找不到）

#### 5.3.3 移除预放置逻辑

```java
// MineFloorGenerator.generateFloor() 中：
// ❌ 移除：generateLevelExit() 调用
// ❌ 移除：spawnLadderHighlightDisplay() 调用
// ✅ 保留：MineFloorData 的 stonesLeft 初始化

// MineFloorData 初始状态：
//   ladderFound = false
//   ladderPos = null
//   stonesLeft = countStones(...)
```

#### 5.3.4 楼梯高亮渲染 — PortalHint 风格穿墙轮廓

**参考系统**：`PortalHintRenderer.java` 已实现完美的穿墙 3D 高亮效果：
- **X-ray 层**：穿墙可见，1/3 透明度，细线框
- **深度测试层**：不穿墙时全不透明度，粗线框
- **浮动气泡**：Billboard 文字标签，始终面向摄像机

**楼梯高亮实现方案**：

新建 `LadderHighlightRenderer.java`（客户端），复用 `PortalHintRenderer` 的渲染管线：

```
LadderHighlightRenderer.java (CLIENT)
├── @SubscribeEvent onRenderLevel(RenderLevelStageEvent)
│   ├── Stage = AFTER_TRANSLUCENT_BLOCKS
│   ├── 检查玩家是否在矿洞维度
│   ├── 从 ClientMiningState 读取当前层的 ladderFound 和 ladderPos
│   ├── 如果 ladderFound = false → 不渲染
│   ├── 如果 ladderFound = true：
│   │   ├── 计算楼梯方块 AABB (1×1×1)
│   │   ├── 渲染 X-ray 穿墙轮廓（颜色：紫色/蓝紫 #8B5CF6，呼应 MINE_LADDER 的紫色贴图）
│   │   │   ├── 6 面半透明面片（alpha = faceA/2）→ 穿墙可见
│   │   │   └── 12 边缘线条（camera-facing quads，alpha = edgeA/3）
│   │   ├── 渲染深度测试轮廓（近距离全不透明）
│   │   ├── 渲染浮动气泡（文字 "▼ 楼梯" / "▼ Ladder"）
│   │   │   ├── Billboard 旋转始终面向玩家
│   │   │   ├── Y 方向轻微上下浮动：sin(time * 2π) * 0.06
│   │   │   └── 缩放随距离自适应（近处不会太大、远处不会太小）
│   │   └── 渲染距离：**无限**（只要在同一层就显示）
│   └── 玩家离开该层（floor 改变）→ 清除高亮状态
```

**颜色主题**：
```java
// 楼梯高亮色：紫色调（与 MINE_LADDER 方块贴图呼应）
float r = 0.545f;  // 139/255
float g = 0.361f;  // 92/255
float b = 0.965f;  // 246/255

// 气泡背景色：深紫半透明
int bubbleBg = 0xCC2D1B69;
```

**与 PortalHintRenderer 的区别**：
| 特性 | PortalHintRenderer | LadderHighlightRenderer |
|------|-------------------|------------------------|
| 渲染距离 | 5 格内 | **无限**（同层内） |
| 触发条件 | 玩家靠近门 | ladderFound=true |
| 生命周期 | 始终检查 | 仅在矿洞维度+当前层 |
| 轮廓大小 | 多格门框 | 1×1×1 方块 |
| 浮动文字 | "▶ 进入" / "◀ 离开" | "▼ 楼梯" |
| 脉冲动画 | 无 | ✅ 轮廓 alpha 缓慢呼吸脉冲（强调注意力） |

#### 5.3.5 客户端数据同步

需要一个 C2S/S2C 数据包让客户端知道楼梯是否已出现：

```
MineLadderSyncPacket.java
├── floorNumber: int
├── ladderFound: boolean
├── ladderPos: BlockPos (nullable)
│
├── 服务端发送时机：
│   ├── 楼梯首次生成时 → ladderFound=true + pos
│   ├── 玩家进入新楼层时 → 发送该层当前状态
│   └── 玩家离开楼层时 → ladderFound=false（清除客户端状态）
│
└── 客户端接收 → 更新 ClientMiningState
    ├── ClientMiningState.currentFloor
    ├── ClientMiningState.ladderFound
    └── ClientMiningState.ladderPos
```

**注意**：已有 `MiningFloorSyncPacket`，可以在其中扩展字段（加入 ladderFound + ladderPos），无需新建数据包。

#### 5.3.6 呼吸脉冲动画

为了让远距离也能注意到楼梯高亮，轮廓 alpha 做缓慢呼吸效果：

```java
float pulse = 0.7f + 0.3f * (float)Math.sin(gameTime * 0.003 * Math.PI * 2); // ~3秒一个周期
float finalFaceAlpha = baseFaceAlpha * pulse;
float finalEdgeAlpha = baseEdgeAlpha * pulse;
```

### 5.4 怪物刷怪方案 — MC 原生刷怪

**核心思路**：利用矿洞内无光照 + void 世界的特性，让 MC 原生刷怪系统自动生成怪物，然后通过事件在生成时注入 SDV 属性和 tag。

#### 5.3.1 刷怪事件处理

```
MineMonsterSpawnHandler.java
├── @SubscribeEvent onEntityJoinLevel(EntityJoinLevelEvent)
│   ├── 检查是否在矿洞维度
│   ├── 检查实体类型是否在允许列表中
│   ├── 根据 Z 坐标计算当前楼层 floor = (z - 14) / FLOOR_SPACING
│   ├── 根据楼层确定怪物主题（Earth/Frost/Lava）
│   ├── 按 SDV 概率决定怪物 tag（sd_mob_* + sd_tier_*）
│   ├── 覆盖属性：HP/攻击/速度/护甲
│   ├── 写入 MonsterStats NBT
│   └── 取消不在允许列表中的实体生成
│
├── @SubscribeEvent onCheckSpawn(MobSpawnEvent.FinalizeSpawn)
│   └── 限制同层怪物上限（8-12 只）
```

#### 5.3.2 允许的实体类型映射

矿洞维度 **只允许** 以下实体自然生成：

```java
Map<EntityType<?>, String> ALLOWED_SPAWNS = Map.of(
    EntityType.SLIME,            "sd_mob_slime",
    EntityType.PHANTOM,          "sd_mob_bat",      // 需要特殊处理：Phantom 正常不在地下生成
    EntityType.SILVERFISH,       "sd_mob_duggy",     // 或 sd_mob_crab，按楼层分
    EntityType.ENDERMITE,        "sd_mob_grub",      // 或 sd_mob_dust_sprite
    EntityType.VEX,              "sd_mob_fly",       // 或 sd_mob_ghost
    EntityType.SKELETON,         "sd_mob_skeleton",
    EntityType.ZOMBIE,           "sd_mob_golem",     // 或 sd_mob_metal_head
    EntityType.WITHER_SKELETON,  "sd_mob_shadow",
    EntityType.EVOKER,           "sd_mob_shadow",    // tier_2
    EntityType.BLAZE,            "sd_mob_squid"
);
```

#### 5.3.3 Phantom 特殊处理

MC Phantom 只在玩家 3 天未睡觉时生成于天空。在矿洞维度中需要 **手动生成**：

```java
// 在 MineMonsterSpawnHandler 中，如果楼层需要蝙蝠但没有自然生成的 Phantom：
// 使用定时任务每 30 秒检查一次，不足则手动 spawn
if (currentBatCount < targetBatCount) {
    Phantom phantom = EntityType.PHANTOM.create(level);
    phantom.setPhantomSize(getPhantomSizeForFloor(floor)); // 1/2/3
    phantom.moveTo(randomAirPos);
    configureMobForFloor(phantom, floor);
    level.addFreshEntity(phantom);
}
```

#### 5.3.4 楼层怪物分配

```java
static String assignMobIdentity(EntityType<?> type, int floor, RandomSource random) {
    // Earth (1-39)
    if (floor <= 39) {
        if (type == EntityType.SLIME) return "sd_mob_slime";
        if (type == EntityType.SILVERFISH) return random.nextFloat() < 0.5 ? "sd_mob_duggy" : "sd_mob_crab";
        if (type == EntityType.ENDERMITE) return "sd_mob_grub";
        if (type == EntityType.VEX) return "sd_mob_fly";
        if (type == EntityType.ZOMBIE) return floor > 30 ? "sd_mob_golem" : null; // 30层以下无Rock Golem
    }
    // Frost (40-79)
    if (floor <= 79) {
        if (type == EntityType.SLIME) return "sd_mob_slime"; // + sd_tier_2
        if (type == EntityType.ENDERMITE) return "sd_mob_dust_sprite";
        if (type == EntityType.VEX) return floor > 50 ? "sd_mob_ghost" : "sd_mob_fly";
        if (type == EntityType.SKELETON) return floor >= 70 ? "sd_mob_skeleton" : null;
    }
    // Lava (80-119)
    if (floor <= 119) {
        if (type == EntityType.SLIME) return "sd_mob_slime"; // + sd_tier_3
        if (type == EntityType.SILVERFISH) return "sd_mob_crab"; // + sd_tier_2
        if (type == EntityType.ZOMBIE) return "sd_mob_metal_head";
        if (type == EntityType.WITHER_SKELETON) return "sd_mob_shadow";
        if (type == EntityType.EVOKER) return "sd_mob_shadow"; // + sd_tier_2
        if (type == EntityType.BLAZE) return "sd_mob_squid";
    }
    return null; // null = 取消此生成
}
```

### 5.5 楼层间距调整

**问题**：代码用 `floorNumber * 500 + 14`，但注释写 `floor * 100`。

**方案**：统一为 `floor * 200`（房间最大 80 格，200 间距足够隔离且减少空间浪费）。

```java
// MiningCoordinates.java
public static final int FLOOR_SPACING = 200; // 从 500 改为 200
```

### 5.6 矿洞维度刷怪规则

需要自定义维度的 spawn settings，只允许指定的怪物类型。方案有两个：

**方案 A（推荐）：事件过滤**  
不修改维度 JSON，在 `MobSpawnEvent.FinalizeSpawn` 中对非允许实体调用 `event.setSpawnCancelled(true)`。简单直接。

**方案 B：自定义 Biome**  
创建 `stardewcraft:mine_biome`，设定 spawn 列表只含允许的怪物。更"正统"但需额外注册。

→ 推荐 **方案 A**，最小改动。

### 5.7 洞窟通道优化

**现状**：洞窟数量按 `(size*size) / 800 + rand(4)` 计算。在 80×80 = 6400 → 8+rand(4) = 8-12 条通道。

**优化后**：60×60 = 3600 → 4+rand(4) = 4-8 条通道（公式自动适配，无需修改）。

洞穴室数量类似：`(size*size) / 800 + rand(5)` → 4+rand(5) = 4-9 个。合理。

### 5.8 生成性能优化

**问题**：当前使用 `level.setBlock(..., 3)` 逐个放置方块。flag=3 = UPDATE + NOTIFY，每个方块都触发方块更新和通知，极慢。

**方案**：
1. 使用 `flag=2`（仅 NOTIFY，跳过 block update 级联）或 `flag=18`（NOTIFY + 不发送到客户端，批量完成后统一发包）
2. 在生成完成后统一调用 `SectionPos.betweenClosedStream()` 发送 chunk 更新
3. 或使用 `ChunkAccess` 直接操作（更快但更复杂）

**最小改动**：将所有 `level.setBlock(pos, state, 3)` 改为 `level.setBlock(pos, state, 2)`，生成最后统一刷新光照和客户端。

---

## 六、实现优先级

### P0 — 矿洞核心可玩（本轮目标）

1. **调整房间尺寸** (MIN_SIZE=60, MAX_SIZE=80, FLOOR_HEIGHT=14)
2. **添加光照** (火把/主题光源)
3. **动态楼梯生成** — 移除预放置，接入 LadderProbabilityCalculator + MiningBlockBreakHandler 触发
4. **楼梯穿墙高亮渲染** — LadderHighlightRenderer（PortalHint 风格 x-ray 轮廓 + 浮动气泡 + 呼吸脉冲）
5. **楼梯状态同步** — 扩展 MiningFloorSyncPacket（ladderFound + ladderPos 字段）
6. **注册 6 个怪物掉落物品** (slime, bat_wing, solar_essence, void_essence, bone_fragment, bug_meat) + 纹理/模型/翻译
7. **新增 `stardewcraft.type.monster_loot` 类型** (红色)
8. **怪物刷怪事件处理** (MineMonsterSpawnHandler — 过滤+属性注入+tag)
9. **怪物掉落事件处理** (MineMonsterDropHandler — 按 SDV 概率掉落)
10. **Phantom 手动刷怪** (定时检查蝙蝠数量)

### P1 — 完善

11. 注册炸弹系列 (cherry_bomb, bomb, mega_bomb) — 仅物品，无功能
12. 同层怪物上限控制 (8-12 只)
13. 生成性能优化 (setBlock flag 改为 2)
14. 楼层间距统一 (FLOOR_SPACING 调整)

### P2 — 未来

15. 炸弹功能实现 (右键放置→定时引爆)
16. 特殊怪物机制 (Mummy 复活, Rock Crab 外壳)
14. 危险模式 (属性翻倍)
15. 121+ 层沙漠矿洞

---

## 七、文件清单

### 新建 Java 文件

```
src/main/java/com/stardew/craft/mining/
├── MineMonsterSpawnHandler.java     ← 生成时注入属性+tag
├── MineMonsterDropHandler.java      ← 死亡时掉落 SDV 物品

src/main/java/com/stardew/craft/client/render/
├── LadderHighlightRenderer.java     ← 楼梯穿墙高亮渲染（PortalHint风格）

src/main/java/com/stardew/craft/client/
├── ClientMiningState.java           ← 客户端楼梯状态（ladderFound/ladderPos/currentFloor）
```

### 修改 Java 文件

```
ModItems.java                        ← 添加 6 个怪物掉落 + 3 个炸弹
MineFloorGenerator.java              ← 调整尺寸 + 添加光照 + 移除预放置楼梯
MiningBlockBreakHandler.java         ← 接入 LadderProbabilityCalculator 动态生成楼梯
MiningFloorSyncPacket.java           ← 扩展 ladderFound + ladderPos 字段
MiningCoordinates.java               ← FLOOR_SPACING 调整（可选）
```

### 新建资源文件

```
assets/stardewcraft/
├── textures/item/monster_loot/     ← 6 张 16×16 PNG
├── textures/item/bomb/             ← 3 张 16×16 PNG
├── models/item/
│   ├── slime.json
│   ├── bat_wing.json
│   ├── solar_essence.json
│   ├── void_essence.json
│   ├── bone_fragment.json
│   ├── bug_meat.json
│   ├── cherry_bomb.json
│   ├── bomb.json
│   └── mega_bomb.json
├── lang/
│   ├── en_us.json                  ← 添加 9 个物品名 + 9 个描述 + 1 个类型名
│   └── zh_cn.json                  ← 同上
```

---

## 八、关键 SDV 物品 ID 速查

用于实现掉落表时的对照：

| SDV ID | 物品 | Mod 状态 | Mod ID |
|--------|------|----------|--------|
| 66 | Amethyst | ✅ 已注册 | `amethyst` |
| 72 | Diamond | ✅ 已注册 | `diamond` |
| 74 | Prismatic Shard | ✅ 已注册 | `prismatic_shard` |
| 86 | Earth Crystal | ✅ 已注册 | `earth_crystal` (方块物品) |
| 92 | Sap | ✅ 已注册 | `sap` |
| 96-99 | Dwarf Scroll I-IV | ✅ 已注册 | `dwarf_scroll_1` ~ `dwarf_scroll_4` |
| 108 | Ancient Disc | ⚠️ 需确认 | `ancient_disc`? |
| 153 | Green Algae | ✅ 已注册 | `green_algae` |
| 157 | White Algae | ✅ 已注册 | `white_algae` |
| 286 | Cherry Bomb | ❌ 待注册 | `cherry_bomb` |
| 287 | Bomb | ❌ 待注册 | `bomb` |
| 288 | Mega Bomb | ❌ 待注册 | `mega_bomb` |
| 334-337 | 铜/铁/金/铱锭 | ✅ 已注册 | `copper_bar` etc. |
| 338 | Refined Quartz | ⚠️ 需确认 | `refined_quartz`? |
| 349 | Energy Tonic | ⚠️ 需确认 | — |
| 378/380/382/386 | 铜/铁/金/铱矿 | ✅ 已注册 | `copper_ore` etc. |
| 428 | Cloth | ✅ 已注册 | `cloth` |
| 446 | Rabbit's Foot | ✅ 已注册 | `rabbits_foot` |
| 535 | Geode | ✅ 已注册 | `geode` |
| 579 | Prehistoric Tibia | ⚠️ artifact? | — |
| 684 | Bug Meat | ❌ 待注册 | `bug_meat` |
| 717 | Crab | ✅ 已注册 | `crab` |
| 766 | Slime | ❌ 待注册 | `slime` |
| 767 | Bat Wing | ❌ 待注册 | `bat_wing` |
| 768 | Solar Essence | ❌ 待注册 | `solar_essence` |
| 769 | Void Essence | ❌ 待注册 | `void_essence` |
| 773 | Life Elixir | ⚠️ 需确认 | — |
| 787 | Battery Pack | ✅ 已注册 | `battery_pack` |
| 814 | Squid Ink | ✅ 已注册 | `squid_ink` |
| 856 | ???(Slime Egg-Tiger?) | ❌ 低优先级 | — |
| 881 | Bone Fragment | ❌ 待注册 | `bone_fragment` |
