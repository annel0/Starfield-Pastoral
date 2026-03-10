# 战斗系统 (Combat System) - 完整文档

## 📋 系统概述

本文档描述了基于 **Stardew Valley 官方Wiki** 数据实现的完整战斗经验和升级系统。

### 数据来源
- **官方Wiki**: https://stardewvalleywiki.com/Combat
- **实施日期**: 2025年12月27日

---

## 🎯 系统架构

### 核心组件

1. **怪物经验值表** (`combat/xp/monster_xp_table.mcfunction`)
   - 定义所有怪物类型的经验值
   - 基于官方Wiki的精确数据
   - 支持怪物等级变体（如蓝史莱姆、霜冻蝙蝠等）

2. **经验获取系统** (`monsters/drops/give_xp.mcfunction`)
   - 怪物死亡时自动触发
   - 根据怪物类型给予精确经验值
   - 显示经验获取提示和音效

3. **升级检测系统** (`combat/level_up_check.mcfunction`)
   - 每tick检测玩家经验是否达到升级要求
   - 使用官方经验阈值表
   - 支持跨等级升级

4. **升级奖励系统** (`combat/level_up_action.mcfunction`)
   - 扣除消耗的经验值
   - 提升玩家等级
   - 给予属性奖励和解锁配方
   - 显示升级特效和消息

---

## 📊 经验值表 (XP Table)

### 官方怪物经验值

| 怪物名称 | 经验值 | 标签 |
|---------|-------|------|
| **史莱姆系列** |
| 绿史莱姆 (Green Slime) | 3 XP | `sd_mob_slime`, `sd_tier_1` |
| 蓝史莱姆 (Blue Slime) | 5 XP | `sd_mob_slime`, `sd_tier_2` |
| 红史莱姆 (Red Slime) | 6 XP | `sd_mob_slime`, `sd_tier_3` |
| 紫史莱姆 (Purple Slime) | 10 XP | `sd_mob_slime`, `sd_tier_4` |
| 虎纹史莱姆 (Tiger Slime) | 20 XP | `sd_mob_slime`, `sd_tier_5` |
| **蝙蝠系列** |
| 蝙蝠 (Bat) | 5 XP | `sd_mob_bat` |
| 霜冻蝙蝠 (Frost Bat) | 7 XP | `sd_mob_bat`, `sd_tier_2` |
| 熔岩蝙蝠 (Lava Bat) | 10 XP | `sd_mob_bat`, `sd_tier_3` |
| 恶魔蝙蝠 (Iridium Bat) | 15 XP | `sd_mob_bat`, `sd_tier_4` |
| **虫系怪物** |
| 蛴螬 (Grub) | 2 XP | `sd_mob_grub` |
| 洞穴苍蝇 (Cave Fly) | 3 XP | `sd_mob_fly` |
| 虫子 (Bug) | 5 XP | `sd_mob_bug` |
| 变异虫 (Mutant Fly) | 10 XP | `sd_mob_bug`, `sd_tier_2` |
| **其他怪物** |
| 灰尘精灵 (Dust Sprite) | 3 XP | `sd_mob_dust_sprite` |
| 岩石蟹 (Rock Crab) | 5 XP | `sd_mob_crab` |
| Duggy | 5 XP | `sd_mob_duggy` |
| 熔岩蟹 (Lava Crab) | 8 XP | `sd_mob_crab`, `sd_tier_2` |
| 蛇 (Serpent) | 10 XP | `sd_mob_serpent` |
| 石魔 (Stone Golem) | 10 XP | `sd_mob_golem` |
| 鱿鱼小子 (Squid Kid) | 10 XP | `sd_mob_squid` |
| 铱星蟹 (Iridium Crab) | 12 XP | `sd_mob_crab`, `sd_tier_3` |
| 影子怪 (Shadow Brute) | 12 XP | `sd_mob_shadow` |
| 幽灵 (Ghost) | 15 XP | `sd_mob_ghost` |
| 骷髅 (Skeleton) | 15 XP | `sd_mob_skeleton` |
| 荒野石魔 (Wilderness Golem) | 15 XP | `sd_mob_golem`, `sd_tier_2` |
| 影子萨满 (Shadow Shaman) | 15 XP | `sd_mob_shadow`, `sd_tier_2` |
| 金属头 (Metal Head) | 15 XP | `sd_mob_metal_head` |
| 碳幽灵 (Carbon Ghost) | 20 XP | `sd_mob_ghost`, `sd_tier_2` |
| 木乃伊 (Mummy) | 20 XP | `sd_mob_mummy` |
| 骷髅法师 (Skeleton Mage) | 20 XP | `sd_mob_skeleton`, `sd_tier_3` |

### 默认经验值
- 未匹配到任何标签的怪物: **3 XP**

---

## 📈 升级经验需求

基于官方Wiki的经验阈值表（累计经验值）:

| 等级 | 所需累计经验 | 距上一级 |
|-----|------------|---------|
| 1 | 100 | 100 |
| 2 | 380 | 280 |
| 3 | 770 | 390 |
| 4 | 1,300 | 530 |
| 5 | 2,150 | 850 |
| 6 | 3,300 | 1,150 |
| 7 | 4,800 | 1,500 |
| 8 | 6,900 | 2,100 |
| 9 | 10,000 | 3,100 |
| 10 | 15,000 | 5,000 |

---

## 🎁 升级奖励

### 每级奖励

| 等级 | 生命值 | 暴击率 | 特殊奖励 |
|-----|-------|-------|---------|
| 1 | +5 HP | - | 解锁配方: 皮革靴子 |
| 2 | +5 HP | +1% | - |
| 3 | +5 HP | - | 解锁配方: 黄晶指环 |
| 4 | +5 HP | +1% | - |
| 5 | +5 HP | - | 解锁配方: 战士戒指 + **选择专精** |
| 6 | +5 HP | +1% | - |
| 7 | +5 HP | - | 解锁配方: 吸血戒指 |
| 8 | +5 HP | +1% | - |
| 9 | +5 HP | - | 解锁配方: 铱星戒指 |
| 10 | +5 HP | +2% | **选择最终专精** |

### 累计属性加成 (等级10)
- **生命值**: +50 HP
- **暴击率**: +6%

### 专精系统 (Professions)

#### 等级5专精选择
1. **战士 (Fighter)**
   - 所有攻击 +10% 伤害
   
2. **侦察兵 (Scout)**
   - 暴击率 +50%

#### 等级10专精选择

**战士分支:**
- **蛮力 (Brute)**: 伤害 +15%
- **防御者 (Defender)**: 生命值 +25

**侦察兵分支:**
- **特技杀手 (Acrobat)**: 暴击伤害 +50%
- **绝杀 (Desperado)**: 怪物有几率被一击必杀

---

## 🔧 技术实现

### 记分板 (Scoreboards)

```mcfunction
# 战斗经验和等级
sd_combat_xp          # 当前累计经验值
sd_combat_level       # 当前战斗等级 (0-10)
sd_level_xp_req       # 下一等级所需经验值（临时）

# 战斗属性
sd_max_health         # 最大生命值
sd_crit_chance        # 暴击率（百分比）
sd_attack_damage      # 攻击伤害
```

### 调用流程

```
1. 玩家击杀怪物
   └─> monsters/core/death_detect.mcfunction
       └─> monsters/drops/loot_table.mcfunction
           └─> monsters/drops/give_xp.mcfunction
               ├─> combat/xp/monster_xp_table.mcfunction (查询经验值)
               └─> 给予玩家经验值 (sd_combat_xp)

2. 每tick检测升级
   └─> main.mcfunction
       └─> combat/level_up.mcfunction
           └─> combat/level_up_check.mcfunction
               └─> combat/level_up_action.mcfunction (如果达到要求)
                   ├─> 扣除经验值
                   ├─> 提升等级
                   ├─> 给予奖励
                   └─> 显示特效和消息
```

---

## 🧪 测试方法

### 1. 初始化玩家数据
```mcfunction
/scoreboard players set @p sd_combat_level 0
/scoreboard players set @p sd_combat_xp 0
/scoreboard players set @p sd_max_health 100
/scoreboard players set @p sd_crit_chance 5
```

### 2. 给予测试经验值
```mcfunction
# 升到等级1 (100 XP)
/scoreboard players set @p sd_combat_xp 100

# 升到等级5 (2150 XP)
/scoreboard players set @p sd_combat_xp 2150

# 升到等级10 (15000 XP)
/scoreboard players set @p sd_combat_xp 15000
```

### 3. 测试怪物经验获取
```mcfunction
# 生成测试怪物（确保它们有正确的标签）
/summon slime ~ ~ ~ {Size:2,Tags:["sd_monster","sd_mob_slime","sd_tier_1"]}
/summon phantom ~ ~ ~ {Tags:["sd_monster","sd_mob_bat","sd_tier_2"],Size:0}
/summon drowned ~ ~ ~ {Tags:["sd_monster","sd_mob_ghost"]}

# 击杀后应获得:
# - 绿史莱姆: 3 XP
# - 霜冻蝙蝠: 7 XP
# - 幽灵: 15 XP
```

### 4. 查询当前状态
```mcfunction
# 查看经验值
/scoreboard players get @p sd_combat_xp

# 查看等级
/scoreboard players get @p sd_combat_level

# 查看属性
/scoreboard players get @p sd_max_health
/scoreboard players get @p sd_crit_chance
```

---

## 📝 与官方游戏的对照

### ✅ 已实现
- ✅ 怪物经验值表（基于官方数据）
- ✅ 升级经验需求（100→15000）
- ✅ 每级生命值奖励 (+5 HP/级)
- ✅ 暴击率提升 (2、4、6、8级 +1%, 10级 +2%)
- ✅ 配方解锁提示
- ✅ 专精系统提示（5级和10级）

### ⚠️ 待完善
- ⚠️ 专精系统实际效果（需要战斗伤害计算系统支持）
- ⚠️ 更多配方的实际解锁逻辑
- ⚠️ 特殊怪物（Boss）的经验值

### 🔄 与原系统的差异

**原系统 (已替换):**
```mcfunction
# 基于层数的经验计算
经验值 = 5 + floor_y / 2
```

**新系统 (当前):**
```mcfunction
# 基于怪物类型的固定经验值
绿史莱姆 = 3 XP
幽灵 = 15 XP
木乃伊 = 20 XP
...
```

**改进点:**
1. ✅ 符合官方游戏平衡性
2. ✅ 不同怪物有差异化奖励
3. ✅ 更容易扩展新怪物
4. ✅ 避免了"深层刷怪"的exploit

---

## 🐛 已知问题

1. **史莱姆分裂**: 小史莱姆死亡也会给经验，这可能导致经验获取过快
   - **解决方案**: 考虑给分裂的史莱姆添加 `sd_slime_split` 标签并降低经验或不给经验

2. **经验共享**: 当前所有15格内的玩家都会获得经验
   - **现状**: 与官方游戏一致（多人游戏中经验共享）

3. **暴击率显示**: 暴击率存储为整数（5% = 5），需要在伤害计算时转换
   - **状态**: 已在战斗系统中正确处理

---

## 📚 相关文件清单

### 新建文件
1. `combat/xp/monster_xp_table.mcfunction` - 怪物经验值表
2. `combat/level_up.mcfunction` - 升级系统入口
3. `combat/level_up_check.mcfunction` - 升级条件检测
4. `combat/level_up_action.mcfunction` - 升级奖励执行

### 修改文件
1. `monsters/drops/give_xp.mcfunction` - 替换经验计算逻辑
2. `main.mcfunction` - 添加战斗升级检测调用

### 依赖文件（无需修改）
1. `combat/init.mcfunction` - 已有记分板初始化
2. `monsters/core/death_detect.mcfunction` - 已有怪物死亡检测
3. `monsters/drops/loot_table.mcfunction` - 已有掉落系统

---

## 🎮 游戏体验建议

### 经验获取速度
- 绿史莱姆 (3 XP) × 34只 ≈ 等级1
- 骷髅/幽灵 (15 XP) × 7只 ≈ 等级1
- 木乃伊 (20 XP) × 5只 ≈ 等级1

### 升级建议流程
1. **等级 0→1**: 击杀 30-40只 绿史莱姆或虫子
2. **等级 1→3**: 前往矿洞深层，击杀蝙蝠和岩石蟹
3. **等级 3→5**: 挑战骷髅和幽灵
4. **等级 5→10**: 击杀高级怪物（木乃伊、石魔、蛇等）

### 平衡性评估
- 等级1到等级5: 约需击杀 140-150 只基础怪物（史莱姆/虫子）
- 等级5到等级10: 约需击杀 640-850 只基础怪物
- **建议**: 通过击杀高价值目标（幽灵15 XP、木乃伊20 XP）加速升级

---

## 📞 维护和更新

### 添加新怪物的步骤
1. 在 `combat/xp/monster_xp_table.mcfunction` 中添加新的经验值规则
2. 确保怪物生成时有正确的标签（如 `sd_mob_new_monster`）
3. 测试经验获取是否正常

### 修改经验值
- 直接修改 `monster_xp_table.mcfunction` 中的对应数值
- 参考官方Wiki保持平衡性

### 调整升级速度
- 修改 `level_up_check.mcfunction` 中的经验阈值
- 或调整 `monster_xp_table.mcfunction` 中的怪物经验值

---

## ✅ 实现完成确认

- [x] 怪物经验值表创建完成
- [x] 经验获取逻辑修改完成
- [x] 升级检测系统创建完成
- [x] 升级奖励系统创建完成
- [x] 集成到主循环完成
- [x] 文档编写完成

**实现日期**: 2025年12月27日
**版本**: 1.0
**状态**: ✅ 完成并可测试
