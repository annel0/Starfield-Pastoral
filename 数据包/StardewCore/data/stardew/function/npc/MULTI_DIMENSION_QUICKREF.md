# 多维度日程系统 - 快速参考

## 📌 核心概念

### 两个Scoreboard分离维度和地点
```mcfunction
stardew.npc.dimension  # 0=主世界, 1=皮埃尔家, 2=酒馆内部...
stardew.npc.schedule   # 1=家, 2=镇中心, 3=墓地, 4=酒馆...
```

### 状态组合示例
- `dimension=0, schedule=1` → 主世界的家门口
- `dimension=1, schedule=1` → 皮埃尔家内部的Abigail卧室
- `dimension=0, schedule=4` → 主世界的酒馆门口
- `dimension=2, schedule=4` → 酒馆内部

---

## ✅ 已完成的准备工作

### 1. Scoreboard已创建 ✅
```mcfunction
# 在 npc/system/init.mcfunction
scoreboard objectives add stardew.npc.dimension dummy "NPC当前维度"
```

### 2. 常量定义已创建 ✅
```mcfunction
# 文件: npc/constants.mcfunction
#dim.overworld = 0
#dim.pierre_house = 1
#dim.saloon_interior = 2
#dim.mine = 3

#loc.home = 1
#loc.town_square = 2
#loc.graveyard = 3
#loc.saloon = 4
```

### 3. Abigail已初始化 ✅
```mcfunction
# 在 npc/abigail/data/init.mcfunction
scoreboard players set @s stardew.npc.dimension 0
```

---

## 🚧 未来需要实现的部分

### 当皮埃尔家维度建好后：

#### 1. 创建跨维度传送函数
```mcfunction
# npc/abigail/teleport/enter_pierre_house.mcfunction
execute in stardew:pierre_house run tp @s 10.5 64.0 10.5 0 0
execute at @s run tp @e[tag=npc.abigail.visual,limit=1,sort=nearest] ~ ~ ~
execute at @s run tp @e[tag=npc.abigail.interaction,limit=1,sort=nearest] ~ ~ ~
scoreboard players set @s stardew.npc.dimension 1
scoreboard players set @s stardew.npc.schedule 1

# npc/abigail/teleport/exit_pierre_house.mcfunction
execute in minecraft:overworld run tp @s 73.5 -54.0 130.5 180 0
execute at @s run tp @e[tag=npc.abigail.visual,limit=1,sort=nearest] ~ ~ ~
execute at @s run tp @e[tag=npc.abigail.interaction,limit=1,sort=nearest] ~ ~ ~
scoreboard players set @s stardew.npc.dimension 0
scoreboard players set @s stardew.npc.schedule 1
```

#### 2. 扩展日程表检测维度
```mcfunction
# schedule/check.mcfunction

# 周一晚上22:00 - 进入家内部睡觉
execute if score Global sd_day_of_week matches 1 if score Global sd_time matches 1320.. unless score @s stardew.npc.dimension matches 1 run function stardew:npc/abigail/teleport/enter_pierre_house

# 周二早上06:00 - 离开家内部
execute if score Global sd_day_of_week matches 2 if score Global sd_time matches 360.. unless score @s stardew.npc.dimension matches 0 run function stardew:npc/abigail/teleport/exit_pierre_house
```

#### 3. 扩展goto_home函数
```mcfunction
# schedule/goto_home.mcfunction

# 新增：可以选择去家的"外部"还是"内部"
function stardew:npc/abigail/schedule/goto_home_exterior  # dimension=0
function stardew:npc/abigail/schedule/goto_home_interior  # dimension=1
```

---

## 💡 设计原则

### ✅ DO（推荐做法）
- **同维度**内移动 → 使用path系统（渐进式移动）
- **跨维度**移动 → 使用传送（瞬间切换）
- 维度转换时同步三个实体（villager, visual, interaction）
- 使用常量而不是魔法数字

### ❌ DON'T（避免做法）
- ❌ 不要跨维度使用path移动
- ❌ 不要忘记同步visual和interaction
- ❌ 不要在schedule中硬编码维度ID
- ❌ 不要直接修改dimension值（应该通过teleport函数）

---

## 🔍 调试命令

### 查看当前维度和地点
```mcfunction
scoreboard players get @e[tag=npc.abigail,limit=1] stardew.npc.dimension
scoreboard players get @e[tag=npc.abigail,limit=1] stardew.npc.schedule
```

### 手动设置维度（测试用）
```mcfunction
scoreboard players set @e[tag=npc.abigail] stardew.npc.dimension 0
scoreboard players set @e[tag=npc.abigail] stardew.npc.dimension 1
```

### 强制传送到指定维度
```mcfunction
function stardew:npc/abigail/teleport/enter_pierre_house
function stardew:npc/abigail/teleport/exit_pierre_house
```

---

## 📊 完整工作流程示例

### 未来的一天（有多维度）：

```
06:00  dimension=1, schedule=1  # 皮埃尔家内部 - 醒来
06:30  dimension=0, schedule=1  # 传送到主世界 - 家门口
09:00  dimension=0, schedule=2  # path1移动 - 去镇中心
13:00  dimension=0, schedule=3  # path2移动 - 去墓地
17:00  dimension=0, schedule=1  # path5移动 - 回家门口
17:30  dimension=1, schedule=1  # 传送到内部 - 进家
22:00  dimension=1, schedule=1  # 还在内部 - 睡觉
```

**关键点**：
- 同维度内用path移动（有行走动画）
- 进出建筑用传送（瞬间切换维度）

---

## ✨ 总结

**现在系统已经准备好了！**

当皮埃尔家维度建好后，只需要：
1. 创建teleport函数（进入/退出）
2. 扩展schedule/check.mcfunction添加维度检查
3. 测试！

**不需要改动**：
- ✅ 现有的path系统
- ✅ 现有的goto_xxx函数
- ✅ 现有的日程表结构

完全向后兼容！🎯
