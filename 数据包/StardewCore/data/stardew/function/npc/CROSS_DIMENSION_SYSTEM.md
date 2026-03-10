# NPC跨维度日程系统 v3.0 - 快照同步

## 核心思想
**不使用强制加载,完全基于快照同步**:
- 玩家离开主世界 → NPC日程在后台继续"计算"(只更新状态,不实际移动)
- 玩家返回主世界 → NPC瞬间传送到"应该在的位置"

## 工作原理

### 1. 时间驱动的状态更新
```
每分钟 (time/calc)
  ↓
更新NPC应该在哪里 (schedule/check)
  ↓
如果触发新日程 → 设置target_schedule
  ↓
直接传送到目的地 (teleport_to_target)
  ↓
完成! (无需区块加载,无需逐步移动)
```

### 2. 玩家返回检测
```
每tick检测玩家维度
  ↓
发现玩家从其他维度返回主世界
  ↓
同步附近128格内的所有NPC
  ↓
NPC瞬移到正确位置
```

## 系统架构

### 文件结构
```
main.mcfunction
  └─> npc/detect_dimension_change.mcfunction (每tick)
        └─> npc/sync_on_return.mcfunction (玩家返回时)

time/calc.mcfunction (每分钟)
  └─> npc/cross_dimension_update.mcfunction
        └─> npc/abigail/schedule/update_target_location.mcfunction
              ├─> schedule/check.mcfunction (判断应该在哪)
              └─> schedule/teleport_to_target.mcfunction (瞬移到目的地)
```

## 关键代码

**time/calc.mcfunction** (每分钟):
```mcfunction
function stardew:npc/cross_dimension_update
```

**npc/cross_dimension_update.mcfunction**:
```mcfunction
execute as @e[tag=npc.abigail] run function stardew:npc/abigail/schedule/update_target_location
```

**npc/abigail/schedule/update_target_location.mcfunction**:
```mcfunction
function stardew:npc/abigail/schedule/check
execute if score @s stardew.npc.target_schedule matches 1.. run function stardew:npc/abigail/schedule/teleport_to_target
```

**npc/detect_dimension_change.mcfunction** (每tick):
```mcfunction
execute as @a[predicate=stardew:in_overworld] run tag @s add in_overworld_now
execute as @a[tag=in_overworld_now,tag=!was_in_overworld] run function stardew:npc/sync_on_return
```

**npc/sync_on_return.mcfunction**:
```mcfunction
execute as @e[tag=npc.abigail,distance=..128] at @s if score @s stardew.npc.target_schedule matches 1.. run function stardew:npc/abigail/schedule/teleport_to_target
```

## 优势对比

| 特性 | 旧方案(强制加载) | 新方案(快照同步) |
|------|----------------|----------------|
| 区块加载 | 永久/临时加载 | ❌ 完全不需要 |
| 跨维度支持 | 部分支持 | ✅ 完全支持 |
| 性能影响 | 中等 | ⭐ 极低 |
| NPC移动 | 每分钟逐步 | 每分钟瞬移 |
| 实现复杂度 | 高 | ⭐ 极低 |
| 视觉效果 | 自然移动 | 瞬移(可添加粒子) |

## 移动方式

- **玩家在附近**: NPC正常逐步移动(每tick更新)
- **玩家不在附近**: NPC不移动,只记录"应该在哪里"
- **玩家返回**: NPC瞬间传送到正确位置(可添加传送粒子效果)

## 性能优化

1. **零区块加载** - 不需要任何forceload
2. **按需同步** - 只在玩家返回时才同步NPC
3. **状态计算** - 后台只计算状态,不实际移动实体
4. **范围限制** - 只同步玩家附近128格内的NPC

## 扩展到多个NPC

在`cross_dimension_update.mcfunction`中添加:
```mcfunction
execute as @e[tag=npc.emily] run function stardew:npc/emily/schedule/update_target_location
execute as @e[tag=npc.alex] run function stardew:npc/alex/schedule/update_target_location
```

在`sync_on_return.mcfunction`中添加:
```mcfunction
execute as @e[tag=npc.emily,distance=..128] at @s if score @s stardew.npc.target_schedule matches 1.. run function stardew:npc/emily/schedule/teleport_to_target
```

## 使用方法

1. **重新加载数据包**:
   ```
   /reload
   ```

2. **重新生成NPC**:
   ```
   /kill @e[tag=npc.abigail]
   /execute positioned <x> <y> <z> run function stardew:npc/abigail/spawn
   ```

3. **测试跨维度**:
   - 观察NPC当前位置和时间
   - 进入建筑内部维度
   - 等待游戏时间经过(如9:00 → 13:00)
   - 返回主世界
   - **NPC应该瞬间出现在新位置**

## 可选优化

### 添加传送粒子效果
在`teleport_to_target.mcfunction`中取消注释:
```mcfunction
particle minecraft:portal ~ ~1 ~ 0.3 0.5 0.3 0.5 20
playsound minecraft:entity.enderman.teleport master @a ~ ~ ~ 0.5 1.5
```

### 调整同步范围
修改`sync_on_return.mcfunction`中的distance值:
```mcfunction
execute as @e[tag=npc.abigail,distance=..256] ...  # 更大范围
```

## 调试命令

```mcfunction
# 查看强制加载的区块
/forceload query

# 手动触发跨维度更新
/function stardew:npc/cross_dimension_update

# 诊断NPC状态
/function stardew:npc/abigail/debug/diagnose
```

## 注意事项

1. ✅ NPC日程会在玩家离开时继续"计算"
2. ✅ 玩家返回时NPC瞬间同步到正确位置
3. ✅ 完全不需要强制加载区块
4. ✅ 极低性能消耗
5. ⚠️ NPC会瞬移而不是逐步移动(可添加粒子效果)
6. ⚠️ 如果玩家一直不返回主世界,NPC实际上不会移动(但状态会更新)

## 工作示例

**场景**: 9:00时NPC在家,玩家进入建筑内部

```
9:00 - NPC在家,玩家进入建筑
      ↓
9:01 - 时间calc触发
      - check: "9:01应该在镇中心"
      - 设置target_schedule=2
      - 但NPC还在家(没有玩家在附近)
      ↓
13:00 - 玩家还在建筑里
      - 时间继续流动
      - check: "13:00应该在墓地"
      - 设置target_schedule=3
      ↓
13:05 - 玩家返回主世界
      - detect_dimension_change检测到
      - sync_on_return触发
      - NPC瞬移到墓地(target_schedule=3的位置)
      - 完成!
```

## 已知问题与解决方案

| 问题 | 状态 |
|------|------|
| 跨维度日程停止 | ✅ 已修复 |
| 强制加载区块过多 | ✅ 已移除,不再使用 |
| NPC移动不流畅 | ✅ 改为快照同步 |
| 性能消耗高 | ✅ 极低消耗 |
| 实现复杂 | ✅ 非常简单 |
