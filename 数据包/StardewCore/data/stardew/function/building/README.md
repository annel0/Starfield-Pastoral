# 鹈鹕镇室内维度管理系统

## 📋 系统概述

本系统使用单一维度 `stardew:town_interiors` 管理鹈鹕镇所有建筑的室内空间。

## 🏗️ 维度配置

- **维度ID**: `stardew:town_interiors`
- **维度类型**: 虚空维度（使用末地生成器）
- **特性**: 无怪物生成、可使用床、无天花板

## 📍 建筑坐标分配

### 商业建筑 (Z=0)
- 皮埃尔的杂货店: `0, 64, 0`
- 星之果实酒吧: `1000, 64, 0`
- 哈维的诊所: `2000, 64, 0`
- 铁匠铺: `3000, 64, 0`
- 博物馆: `4000, 64, 0`
- 社区中心: `5000, 64, 0`
- Joja超市: `6000, 64, 0`

### 居民住宅 (Z=1000)
- 河间大道1号: `0, 64, 1000` (乔治、艾芙琳、亚历克斯)
- 柳巷1号: `1000, 64, 1000` (乔迪、肯特、山姆、文森特)
- 柳巷2号: `2000, 64, 1000` (艾米丽、海莉)
- 拖车: `3000, 64, 1000` (潘姆、潘妮)
- 镇长庄园: `4000, 64, 1000` (刘易斯)

## 🚪 交互体标签系统

### 进入门标签
在镇上每个建筑门口放置interaction实体，使用以下标签：
- **必需标签**: `building_door` （用于系统检测）
- **具体建筑标签**:
  - `door_pierre_shop`
  - `door_saloon`
  - `door_clinic`
  - `door_blacksmith`
  - `door_museum`
  - `door_community_center`
  - `door_joja_mart`
  - `door_1_river_road`
  - `door_1_willow_lane`
  - `door_2_willow_lane`
  - `door_trailer`
  - `door_mayor_manor`

### 出口门标签
在每个室内出口处放置interaction实体，使用标签：
- `exit_door`

## 🎮 使用方法

### 进入建筑
1. 玩家靠近建筑门口（3格范围内）
2. **右键点击门** （不需要潜行）
3. 自动传送到对应的室内空间

### 离开建筑
1. 玩家在室内靠近出口门（3格范围内）
2. **右键点击出口** （不需要潜行）
3. 自动传送回镇上对应建筑门口

## 🔧 文件结构

```
data/stardew/
  dimension/
    town_interiors.json              # 维度定义
  dimension_type/
    town_interiors.json              # 维度类型
  function/
    building/
      door_interact.mcfunction       # 主检测函数
      check_door_interaction.mcfunction  # 进入逻辑
      coordinates.txt                # 坐标配置文档
      enter/                         # 进入各建筑的函数
        pierre_shop.mcfunction
        saloon.mcfunction
        clinic.mcfunction
        blacksmith.mcfunction
        museum.mcfunction
        community_center.mcfunction
        joja_mart.mcfunction
        house_1_river_road.mcfunction
        house_1_willow_lane.mcfunction
        house_2_willow_lane.mcfunction
        trailer.mcfunction
        mayor_manor.mcfunction
      exit/                          # 出口相关函数
        check_exit.mcfunction        # 检测出口
        return_to_town.mcfunction    # 返回主逻辑
        to_*.mcfunction              # 返回各建筑门口
```

## ⚙️ 集成到主循环

在 `main.mcfunction` 中添加：
```mcfunction
# 检测玩家右键门进入建筑
function stardew:building/door_interact
# 检测玩家右键出口离开建筑
function stardew:building/exit/check_exit
```

## 📝 玩家标签系统

进入建筑后，玩家会被添加以下标签：
- `inside_building` - 表示在室内
- `inside_<建筑名>` - 具体建筑标签，例如：
  - `inside_pierre_shop`
  - `inside_saloon`
  - `inside_1_river_road`
  - 等等...

离开建筑时，所有标签会被清除。

## 🎯 下一步工作

1. **建造室内结构**
   - 在创意模式中前往维度建造各建筑内部
   - 每个建筑间隔1000格，避免干扰

2. **放置交互体**
   - 在镇上每个门口放置进入交互体（需要两个标签）:
     ```
     /summon interaction ~ ~ ~ {width:1f,height:2f,Tags:["building_door","door_pierre_shop"]}
     ```
   - 在每个室内出口放置出口交互体:
     ```
     /summon interaction ~ ~ ~ {width:1f,height:2f,Tags:["exit_door"]}
     ```

3. **配置返回坐标**
   - 修改 `exit/to_*.mcfunction` 中的坐标
   - 设置为实际建筑门口的坐标

4. **添加视觉效果**
   - 传送时的粒子效果
   - 音效提示
   - 淡入淡出效果

5. **扩展功能**
   - NPC在室内的生成
   - 室内家具交互
   - 时间同步
   - 区块预加载

## ⚠️ 注意事项

- 确保每个建筑之间至少相隔1000格，避免视觉干扰
- 交互体的hitbox要合适，建议宽度1格，高度2格
- 出口交互体应该放在玩家容易看到的位置
- 返回坐标需要精确设置，确保玩家不会卡在方块里
