# 多维度日程系统设计文档

## 📋 需求分析

### 当前问题
- 现在所有NPC都在**主世界 (Overworld)** 活动
- 日程表只记录"在哪个地点"，没有记录"在哪个维度"
- 未来需要专门的维度来做皮埃尔的家、Abigail的卧室等内部场景

### 未来场景示例
1. **皮埃尔的家内部** - 独立维度
   - Abigail的卧室
   - 皮埃尔和Caroline的卧室
   - 客厅、厨房等
   
2. **其他建筑内部** - 可能也需要独立维度
   - 酒馆内部
   - Joja商店内部
   - 诊所内部

---

## 🏗️ 架构设计

### 方案1: Schedule状态分离维度和地点 ⭐ 推荐

#### Scoreboard结构
```mcfunction
# 当前位置状态（不变）
stardew.npc.schedule = 地点ID
  1 = 家
  2 = 镇中心
  3 = 墓地
  4 = 酒馆
  5 = 矿洞
  ...

# 新增：当前维度状态
stardew.npc.dimension = 维度ID
  0 = 主世界 (minecraft:overworld)
  1 = 皮埃尔家内部 (stardew:pierre_house)
  2 = 酒馆内部 (stardew:saloon_interior)
  3 = 矿洞 (stardew:mine)
  ...

# 组合使用
dimension=0, schedule=1  → 主世界的"家门口"
dimension=1, schedule=1  → 皮埃尔家内部的"Abigail卧室"
dimension=0, schedule=4  → 主世界的"酒馆门口"
dimension=2, schedule=4  → 酒馆内部
```

#### 优点
- ✅ 清晰分离维度和地点概念
- ✅ 向后兼容（dimension默认为0）
- ✅ 支持同一"地点"在不同维度有不同含义
- ✅ 扩展性强

---

### 方案2: Schedule状态合并编码

#### Scoreboard结构
```mcfunction
stardew.npc.schedule = 维度*100 + 地点
  1 = 主世界的家
  2 = 主世界的镇中心
  101 = 皮埃尔家内部的Abigail卧室
  102 = 皮埃尔家内部的客厅
  201 = 酒馆内部的吧台区
  ...
```

#### 优点
- ✅ 只需一个scoreboard
- ✅ 状态判断简单

#### 缺点
- ❌ 数字含义不直观
- ❌ 扩展性较差
- ❌ 需要重新规划所有现有的schedule编号

---

## 🎯 推荐实现方案

### 使用方案1：分离维度和地点

---

## 📝 实现步骤

### 阶段1: 准备阶段（现在可以做）✅

#### 1.1 创建维度常量定义
```mcfunction
# data/stardew/function/npc/constants.mcfunction

# 维度ID定义
scoreboard players set #dim.overworld stardew.const 0
scoreboard players set #dim.pierre_house stardew.const 1
scoreboard players set #dim.saloon_interior stardew.const 2
scoreboard players set #dim.mine stardew.const 3

# 地点ID定义（保持不变）
scoreboard players set #loc.home stardew.const 1
scoreboard players set #loc.town_square stardew.const 2
scoreboard players set #loc.graveyard stardew.const 3
scoreboard players set #loc.saloon stardew.const 4
```

#### 1.2 初始化维度scoreboard
```mcfunction
# data/stardew/function/init.mcfunction

# 添加新的scoreboard
scoreboard objectives add stardew.npc.dimension dummy "NPC当前维度"
```

#### 1.3 为现有NPC设置默认维度
```mcfunction
# data/stardew/function/npc/abigail/summon.mcfunction

# 在召唤时初始化维度为主世界
scoreboard players set @e[tag=npc.abigail,limit=1] stardew.npc.dimension 0
```

---

### 阶段2: 跨维度传送系统（维度建好后实现）

#### 2.1 创建维度传送门检测
```mcfunction
# 例如：家门口 → 皮埃尔家内部
# data/stardew/function/npc/abigail/schedule/goto_home_interior.mcfunction

# 检查是否已经在目标位置
execute if score @s stardew.npc.schedule matches 1 if score @s stardew.npc.dimension matches 1 run return 0

# 如果在主世界的家门口，传送进入内部维度
execute if score @s stardew.npc.schedule matches 1 if score @s stardew.npc.dimension matches 0 run function stardew:npc/abigail/teleport/enter_pierre_house

# 如果在其他地方，先回到家门口
execute unless score @s stardew.npc.schedule matches 1 run function stardew:npc/abigail/schedule/goto_home
```

#### 2.2 跨维度传送函数
```mcfunction
# data/stardew/function/npc/abigail/teleport/enter_pierre_house.mcfunction

# 传送到皮埃尔家内部的Abigail卧室
execute in stardew:pierre_house run tp @s 10.5 64.0 10.5 0 0

# 同步visual和interaction实体
execute at @s run tp @e[tag=npc.abigail.visual,limit=1,sort=nearest] ~ ~ ~
execute at @s run tp @e[tag=npc.abigail.interaction,limit=1,sort=nearest] ~ ~ ~

# 更新维度状态
scoreboard players set @s stardew.npc.dimension 1

# 位置状态保持为"家"
scoreboard players set @s stardew.npc.schedule 1
```

---

### 阶段3: 日程表扩展（维度建好后实现）

#### 3.1 扩展schedule/check.mcfunction
```mcfunction
# 例如：周一晚上在家内部（卧室）
execute if score Global sd_day_of_week matches 1 if score Global sd_time matches 1200.. unless score @s stardew.npc.dimension matches 1 run function stardew:npc/abigail/schedule/goto_home_interior

# 例如：周二白天在商店（主世界）
execute if score Global sd_day_of_week matches 2 if score Global sd_time matches 540..1079 unless score @s stardew.npc.dimension matches 0 unless score @s stardew.npc.schedule matches 2 run function stardew:npc/abigail/schedule/goto_town_square_exterior
```

---

## 🔧 路径系统适配

### 跨维度路径处理

#### 情况1: 同一维度内移动
- 使用现有的path系统 ✅
- 不需要改动

#### 情况2: 跨维度移动
- **不使用**渐进式path移动
- **直接传送**到目标维度的入口点
- 示例：
  ```mcfunction
  # 从主世界酒馆门口 → 酒馆内部
  execute if score @s stardew.npc.schedule matches 4 if score @s stardew.npc.dimension matches 0 run function stardew:npc/abigail/teleport/enter_saloon
  
  # 从酒馆内部 → 主世界酒馆门口
  execute if score @s stardew.npc.schedule matches 4 if score @s stardew.npc.dimension matches 2 run function stardew:npc/abigail/teleport/exit_saloon
  ```

---

## 📊 状态组合示例

### Abigail的完整日程（未来版本）

```mcfunction
# 周一
06:00-09:00  dimension=1, schedule=1  # 皮埃尔家内部 - 卧室
09:00-09:05  dimension=0, schedule=1  # 传送到主世界 - 家门口
09:05-13:00  dimension=0, schedule=2  # 主世界 - 商店
13:00-17:00  dimension=0, schedule=3  # 主世界 - 墓地
17:00-17:05  dimension=0, schedule=1  # 主世界 - 家门口
17:05-22:00  dimension=1, schedule=1  # 传送到皮埃尔家内部 - 卧室
22:00+       dimension=1, schedule=1  # 睡觉
```

---

## ⚠️ 注意事项

### 1. 实体同步
- 跨维度传送时，必须同步以下实体：
  - `@e[tag=npc.abigail]` (villager)
  - `@e[tag=npc.abigail.visual]` (AJ模型)
  - `@e[tag=npc.abigail.interaction]` (interaction)

### 2. 维度加载
- 使用 `execute in <dimension>` 确保目标维度已加载
- 跨维度传送前可能需要forceload目标区域

### 3. 玩家跟随
- 如果玩家正在与NPC对话，NPC传送维度时需要处理：
  - 选项A: 阻止NPC传送（玩家对话中）
  - 选项B: 强制关闭对话
  - 选项C: 玩家一起传送

### 4. 动画状态
- 跨维度传送时，暂停动画或强制切换到idle

---

## 🚀 总结

### 当前系统（Phase 1）
- ✅ 只在主世界活动
- ✅ 使用 `stardew.npc.schedule` 记录地点
- ✅ 使用path系统移动

### 未来系统（Phase 2+）
- ⭐ 添加 `stardew.npc.dimension` 记录维度
- ⭐ 跨维度使用传送，同维度使用path
- ⭐ schedule/check.mcfunction同时检查维度和地点
- ⭐ 完全向后兼容现有系统

### 现在可以做的准备
1. ✅ 创建维度常量定义文件
2. ✅ 添加 `stardew.npc.dimension` scoreboard
3. ✅ 为现有NPC初始化dimension=0
4. ✅ 在代码注释中标注未来扩展点
5. ✅ 保持goto_xxx函数的模块化设计

---

**结论**: 使用分离的维度和地点系统，现在就可以做好准备工作，未来扩展时不需要大规模重构！
