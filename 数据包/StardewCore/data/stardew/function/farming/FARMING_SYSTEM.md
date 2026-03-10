# 农耕系统完整实现文档 (Farming System)

## 📋 系统概览

农耕系统现已完全按照星露谷物语官方机制实现,包括:
- ✅ 作物收获经验系统
- ✅ 动物照顾经验系统  
- ✅ 自动等级提升检测
- ✅ 等级奖励与配方解锁

---

## 🌾 经验值获取方式

### 1. 作物收获 (基于官方公式)
**公式:** XP = 16 × ln(0.018 × 作物价格 + 1)

**示例作物经验值:**
- 防风草 (35g) = **8 XP**
- 土豆 (80g) = **14 XP**
- 草莓 (120g) = **18 XP**
- 花椰菜 (175g) = **23 XP**
- 杨桃 (750g) = **43 XP**
- 甜宝石莓 (3000g) = **64 XP**

**实现位置:**
- `farming/xp/crop_xp_table.mcfunction` - 作物经验表
- `farming/api/harvest_crop.mcfunction` - 收获时自动调用经验表

**特性:**
- 作物品质不影响经验值
- 多产作物(蓝莓/蔓越莓)只给第一个产物经验
- 自动从作物ID提取名称并匹配经验表

---

### 2. 动物照顾 (每次 +5 XP)
**官方规则:** 每次照顾动物操作都给予 **5 XP**

**触发操作:**
- 抚摸动物 (每天每只1次)
- 挤奶 (牛/山羊)
- 剪羊毛 (绵羊)
- 拾取产物 (鸡蛋/鸭蛋/兔子羊毛等)

**实现位置:**
- `farming/xp/animal_care.mcfunction` - 统一的动物照顾经验函数
- 集成到以下文件:
  - `animal/interact/milk_cow_success.mcfunction`
  - `animal/interact/shear_sheep_success.mcfunction`
  - `animal/interact/pet_animal_as_animal.mcfunction`
  - `animal/produce/pickup_egg.mcfunction`

**视觉反馈:**
- Actionbar显示: `[农耕] +5 XP (照顾动物)`
- 经验球音效

---

## 📊 等级系统

### 升级所需总经验值表
| 等级 | 所需总经验 | 增量 |
|------|-----------|------|
| 1    | 100       | 100  |
| 2    | 380       | 280  |
| 3    | 770       | 390  |
| 4    | 1300      | 530  |
| 5    | 2150      | 850  |
| 6    | 3300      | 1150 |
| 7    | 4800      | 1500 |
| 8    | 6900      | 2100 |
| 9    | 10000     | 3100 |
| 10   | 15000     | 5000 |

**实现位置:**
- `farming/level_up_check.mcfunction` - 每tick检测经验是否满足升级
- `farming/level_up_action.mcfunction` - 执行升级并消耗经验
- `farming/level_up.mcfunction` - 主入口(从main.mcfunction调用)

---

## 🎁 等级奖励

### 工具熟练度 (每级)
- 锄头能量消耗: `-0.1` (Level 0: 2能量 → Level 10: 1能量)
- 洒水壶能量消耗: `-0.1` (Level 0: 2能量 → Level 10: 1能量)

### 配方解锁
| 等级 | 解锁内容 |
|------|---------|
| 1    | 稻草人、基础肥料 |
| 2    | 蛋黄酱机、石栅栏、洒水器 |
| 3    | 蜂房、生长激素、农夫午餐 |
| 4    | 保鲜罐、基础保水土壤、铁栅栏 |
| 5    | **职业选择: Rancher vs Tiller** |
| 6    | 奶酪压榨机、硬木栅栏、优质洒水器 |
| 7    | 织布机、优质保水土壤 |
| 8    | 油制机、小桶、高级生长激素 |
| 9    | 种子制造机、铱金洒水器、优质肥料 |
| 10   | **高级职业选择** |

### 职业系统 (TODO - 待实现)
**Level 5 职业:**
- **Rancher (牧场主):** 动物产品价值 +20%
- **Tiller (农夫):** 作物价值 +10%

**Level 10 高级职业:**
- Rancher → **Coopmaster** or **Shepherd**
- Tiller → **Artisan** or **Agriculturist**

---

## 🔧 技术实现细节

### 文件结构
```
stardew/function/farming/
├── level_up.mcfunction              # 主入口
├── level_up_check.mcfunction        # 检测是否可升级
├── level_up_action.mcfunction       # 执行升级动作
├── api/
│   └── harvest_crop.mcfunction      # [已修改] 使用经验表
└── xp/
    ├── crop_xp_table.mcfunction     # [新增] 作物经验表
    └── animal_care.mcfunction       # [新增] 动物照顾经验
```

### 核心逻辑流程

#### 收获作物时:
```
harvest_crop.mcfunction
  ↓
提取作物ID (如 "spring/parsnip")
  ↓
移除路径前缀 (得到 "parsnip")
  ↓
调用 crop_xp_table.mcfunction
  ↓
根据作物名匹配经验值
  ↓
给附近玩家增加 sd_farming_xp
```

#### 照顾动物时:
```
milk_cow_success / shear_sheep_success / pet_animal / pickup_egg
  ↓
调用 farming/xp/animal_care
  ↓
给玩家增加 5 XP
  ↓
显示 actionbar 提示
```

#### 每 Tick 升级检测:
```
main.mcfunction
  ↓
execute as @a run farming/level_up
  ↓
如果等级 ≤ 9: 调用 level_up_check
  ↓
比较 sd_farming_xp >= sd_level_xp_req
  ↓
如果满足: 调用 level_up_action
  ↓
消耗经验 → 提升等级 → 显示特效 → 递归检查
```

---

## 📝 记分板使用

| Scoreboard | 用途 |
|-----------|------|
| `sd_farming_xp` | 玩家当前农耕经验值 |
| `sd_farming_lvl` | 玩家当前农耕等级 (0-10) |
| `sd_level_xp_req` | 临时存储下一级所需总经验 |
| `sd_config` | 存储全局配置(如各等级经验需求) |
| `sd_temp` | 临时计算用 |

---

## ✅ 与官方对比验证

| 特性 | 官方 | 当前实现 | 状态 |
|------|------|---------|------|
| 作物经验公式 | XP=16×ln(0.018×价格+1) | ✅ 完全一致 | ✅ |
| 动物照顾经验 | +5 XP/次 | ✅ 完全一致 | ✅ |
| 升级经验表 | 100→15000 | ✅ 完全一致 | ✅ |
| 工具熟练度 | -0.1能量/级 | ⚠️ 需实现 | 🔨 |
| 配方解锁 | 1-10级 | ✅ 提示已添加 | ✅ |
| 职业系统 | Level 5&10 | ⚠️ 待实现 | 🔨 |
| 作物品质加成 | 与等级相关 | ⚠️ 待实现 | 🔨 |

---

## 🎮 测试方法

### 1. 测试作物经验
```mcfunction
# 给自己防风草种子
/loot give @s loot stardew:items/seeds/spring/parsnip

# 种植并等待成熟
# 收获时应获得 8 XP

# 查看当前经验
/scoreboard players get @s sd_farming_xp
```

### 2. 测试动物经验
```mcfunction
# 生成一只牛
/function stardew:animal/spawn/cow

# 抚摸牛 (右键interaction实体)
# 应获得 5 XP + actionbar提示

# 查看经验
/scoreboard players get @s sd_farming_xp
```

### 3. 测试升级
```mcfunction
# 直接设置经验到升级阈值
/scoreboard players set @s sd_farming_xp 100

# 等待1 tick后应该自动升级到Level 1
# 应看到粒子特效和升级提示
```

### 4. 测试跨等级
```mcfunction
# 设置大量经验
/scoreboard players set @s sd_farming_xp 10000

# 应该连续升级到对应等级 (Level 9)
```

---

## 🐛 已知问题

1. **工具能量消耗未实现**: 等级提升后锄头/洒水壶能量消耗减少需要在工具使用系统中实现
2. **职业系统未实现**: Level 5和10的职业选择功能需要配合UI系统
3. **作物品质受等级影响未实现**: 高等级应该提高金银星作物概率

---

## 🔮 未来扩展

1. **职业选择UI**: 创建技能树选择界面
2. **作物品质系统**: 根据等级调整金银星概率
3. **特殊成就**: 如"收获10000个作物"等
4. **经验加速**: 食物buff临时提升农耕等级
5. **精通系统**: Level 10后的额外进度

---

## 📚 相关文件索引

### 核心文件
- `main.mcfunction` (Line ~110) - 调用 farming/level_up
- `farming/level_up.mcfunction` - 主入口
- `farming/level_up_check.mcfunction` - 升级检测
- `farming/level_up_action.mcfunction` - 升级执行

### 经验系统
- `farming/xp/crop_xp_table.mcfunction` - 作物经验表
- `farming/xp/animal_care.mcfunction` - 动物照顾经验
- `farming/api/harvest_crop.mcfunction` - 收获作物(已集成经验)

### 动物系统集成
- `animal/interact/milk_cow_success.mcfunction` - 挤奶(已集成)
- `animal/interact/shear_sheep_success.mcfunction` - 剪毛(已集成)
- `animal/interact/pet_animal_as_animal.mcfunction` - 抚摸(已集成)
- `animal/produce/pickup_egg.mcfunction` - 拾取产物(已集成)

---

*最后更新: 2025年12月27日*
*状态: ✅ 核心系统完成 | 🔨 职业系统待实现*
