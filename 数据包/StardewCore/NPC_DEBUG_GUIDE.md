# NPC日程系统调试指南

## 问题诊断

NPC日程系统不工作的主要原因是**星期映射错误**:

### 错误的映射 (之前)
```
schedule/check.mcfunction 中:
- 0 = 周日 (Sunday) ❌ 错误!
- 1 = 周一 (Monday)
- ...
- 6 = 周六 (Saturday)
```

### 正确的映射 (修复后)
```
系统约定 (init.mcfunction, new_day.mcfunction):
- sd_day_of_week = sd_day % 7
- 0 = 第0天 (游戏开始日)
- 1 = 周一 (Monday)
- 2 = 周二 (Tuesday)
- ...
- 6 = 周六 (Saturday)
```

## 修复内容

### 1. schedule/check.mcfunction
- 修正注释: 0=周日 → 0=第0天
- 保持所有时间判定逻辑不变(因为游戏开始日也应该在家)

### 2. 新增调试工具

#### `/function stardew:debug/check_npc_schedule`
显示完整的NPC日程诊断信息:
- 当前时间、天数、星期几
- NPC实体是否存在
- 当前日程状态 (schedule)
- 目标日程 (target_schedule)
- 期望日程 (根据时间计算)
- 路径状态 (path_id, path_index)

#### `/function stardew:debug/force_schedule_update`
手动触发一次NPC日程更新(调用 cross_dimension_update)

#### `/function stardew:debug/fix_npc_schedule`
重新初始化NPC日程系统:
- 重置 schedule = 1 (在家)
- 清除 target_schedule
- 清除路径状态
- 立即触发一次日程检查

## 使用方法

1. **检查NPC状态**
   ```
   /function stardew:debug/check_npc_schedule
   ```
   查看诊断信息,确认:
   - NPC实体存在
   - sd_day_of_week 已初始化 (0-6)
   - sd_time 正常运行
   - schedule 状态合理

2. **如果NPC卡住不动**
   ```
   /function stardew:debug/fix_npc_schedule
   ```
   重置NPC日程系统

3. **手动测试日程更新**
   ```
   /function stardew:debug/force_schedule_update
   ```
   立即触发一次日程检查(不需要等1分钟)

4. **测试时间跳转**
   使用游戏内菜单的时间作弊功能:
   - 设置不同时间段 (9:00, 13:00, 18:00)
   - 使用 force_schedule_update 触发更新
   - 观察NPC是否移动到正确位置

## Abigail的每周日程

- **第0天**: 整天在家
- **周一**: 09:00-13:00 镇中心, 13:00-17:00 墓地
- **周二**: 09:00-18:00 镇中心
- **周三**: 09:00-13:00 镇中心, 13:00-17:00 墓地
- **周四**: 整天在家
- **周五**: 17:00-23:30 酒馆
- **周六**: 10:00-18:00 墓地

## 技术细节

### 日程检查流程
```
time/calc.mcfunction (每分钟)
  → npc/cross_dimension_update.mcfunction
    → npc/abigail/schedule/update_target_location.mcfunction
      → schedule/check.mcfunction (检查时间+星期)
        → goto_home/goto_town_square/goto_graveyard/goto_saloon
      → teleport_to_target.mcfunction (实际传送)
```

### 传送机制
系统使用**快照+瞬移**机制:
- goto_* 函数设置 target_schedule (目标位置)
- teleport_to_target 直接传送到目标坐标
- 应用 schedule = target_schedule
- 清除路径状态

### 日程状态
- `stardew.npc.schedule` = 当前位置 (1=家,2=镇中心,3=墓地,4=酒馆)
- `stardew.npc.target_schedule` = 目标位置 (移动中时存在)
- `stardew.npc.path_id` = 路径ID (可选,用于路径行走)
- `stardew.npc.path_index` = 路径点索引 (可选)

## 常见问题

### Q: NPC一直不动
A: 
1. 检查 sd_day_of_week 是否初始化 (`/scoreboard players get Global sd_day_of_week`)
2. 检查 NPC 实体是否存在 (`/tag @e[tag=npc.abigail] add test`)
3. 使用 `/function stardew:debug/fix_npc_schedule` 重置

### Q: 时间到了但NPC没移动
A:
1. 确认 time/calc.mcfunction 正在运行 (检查 sd_time 是否增长)
2. 手动触发更新 `/function stardew:debug/force_schedule_update`
3. 检查是否在正确的时间段 (需要过了transition时间)

### Q: NPC传送到错误位置
A:
1. 检查 sd_day_of_week 值是否正确
2. 查看 schedule/check.mcfunction 逻辑
3. 使用诊断工具查看"期望日程"

## 修复历史

**2024修复**: 
- 修正 schedule/check.mcfunction 中的星期注释错误
- 添加完整的调试工具套件
- 统一星期映射定义
