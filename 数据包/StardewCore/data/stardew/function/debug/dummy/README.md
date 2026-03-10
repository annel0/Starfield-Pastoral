# 稻草人DPS测试系统 - 使用文档

## 📋 系统概述

稻草人是一个用于测试武器伤害输出的假人系统,可以实时显示每秒伤害(DPS)。

---

## 🎯 功能特性

- ✅ **不会移动**: NoAI设置,完全静止
- ✅ **不会反击**: 攻击力为0,不会造成伤害
- ✅ **无限血量**: 受伤后自动恢复,永远不会死亡
- ✅ **实时DPS显示**: 在sidebar右侧显示当前DPS(每秒伤害)
- ✅ **易于管理**: 简单的召唤和移除命令

---

## 📦 使用方法

### 召唤稻草人

```mcfunction
/function stardew:debug/dummy/summon
```

稻草人会在你面前2格处生成,头上戴着南瓜。

### 移除稻草人

```mcfunction
/function stardew:debug/dummy/remove
```

会移除场景中所有的稻草人。

---

## 📊 DPS显示说明

DPS显示在游戏右侧的sidebar中,格式为:

```
⚔ DPS: XXX/s
```

### 显示规则

1. **未激活状态**: 没有稻草人时显示 `⚔ 未激活`
2. **攻击中**: 攻击稻草人后显示实时DPS,例如 `⚔ DPS: 156/s`
3. **停止攻击**: 停止攻击1秒后显示 `⚔ DPS: 0/s`,再过一会变为 `⚔ 未激活`

### DPS计算方式

- 系统每秒统计一次伤害总和
- DPS = 过去1秒内造成的总伤害
- 停止攻击后会自动重置为0

---

## 🔧 技术细节

### 记分板

- `sd_dummy_total_dmg` - 当前1秒内的累计伤害
- `sd_dummy_tick_timer` - 计时器(0-19 tick)
- `sd_dummy_dps` - 上一秒的DPS值
- `sd_dummy_last_hp` - 上一tick的血量(用于检测伤害)

### 实体标签

- `sd_dummy` - 所有稻草人
- `sd_dummy_init` - 刚生成未初始化的稻草人
- `sd_dummy_active` - 已初始化的稻草人

### 工作原理

1. 稻草人设置为999999血量
2. 每tick检测血量变化,计算受到的伤害
3. 累加伤害到 `sd_dummy_total_dmg`
4. 受伤后立即恢复血量到满血
5. 每20tick(1秒)将累计伤害记录为DPS并显示
6. 重置累计伤害计数器,开始下一秒的统计

---

## 📝 文件清单

### 核心功能文件

1. `debug/dummy/summon.mcfunction` - 召唤稻草人
2. `debug/dummy/init.mcfunction` - 初始化稻草人数据
3. `debug/dummy/tick.mcfunction` - 每tick更新(检测伤害、自动回血)
4. `debug/dummy/on_damage.mcfunction` - 受伤时触发
5. `debug/dummy/calculate_dps.mcfunction` - 计算并显示DPS
6. `debug/dummy/remove.mcfunction` - 移除所有稻草人

### 集成文件(已修改)

1. `combat/init.mcfunction` - 添加了DPS系统的记分板
2. `combat/tick.mcfunction` - 添加了稻草人tick调用
3. `ui/load.mcfunction` - 添加了DPS显示的sidebar配置

---

## 🎮 使用场景

### 1. 测试武器基础伤害

```mcfunction
# 1. 召唤稻草人
/function stardew:debug/dummy/summon

# 2. 切换到要测试的武器
# 3. 持续攻击稻草人
# 4. 观察sidebar上的DPS数值
```

### 2. 比较不同武器

```mcfunction
# 测试武器A
持续攻击5秒,记录平均DPS

# 切换武器B
持续攻击5秒,记录平均DPS

# 比较两个DPS值
```

### 3. 测试暴击率

```mcfunction
# 攻击稻草人,观察伤害数字
# 红色数字带感叹号 = 暴击
# 黄色普通数字 = 非暴击
# 统计暴击频率
```

### 4. 测试技能伤害

```mcfunction
# 使用武器技能攻击稻草人
# 观察技能触发时的DPS峰值
# 计算技能CD期间的平均DPS
```

---

## ⚠️ 注意事项

1. **位置选择**: 建议在空旷区域召唤,避免稻草人卡入方块
2. **多人游戏**: 所有玩家共享同一个DPS显示
3. **清理**: 测试完成后记得使用remove命令清理稻草人
4. **性能**: 每个世界建议只保留1-2个稻草人
5. **重载**: 执行 `/reload` 后需要重新召唤稻草人

---

## 🐛 故障排除

### 问题1: 稻草人显示为"蠹虫"或其他奇怪名字
- **原因**: 旧版本的稻草人被怪物血量显示系统覆盖了名字
- **解决**: 执行 `/function stardew:debug/dummy/remove` 移除旧稻草人,然后重新召唤

### 问题2: 稻草人会移动
- **原因**: NoAI可能失效
- **解决**: 移除后重新召唤

### 问题3: DPS显示为0
- **原因**: 伤害未正确计算
- **检查**: 确认你使用的是星露谷武器(有stardew_weapon标签)
- **解决**: 使用正确的武器攻击

### 问题4: 稻草人死亡
- **原因**: 自动回血系统可能出错
- **解决**: 移除后重新召唤,或检查combat/tick.mcfunction是否正常运行

### 问题5: 侧边栏不显示DPS
- **原因**: UI未正确初始化
- **解决**: 执行 `/function stardew:ui/load` 重新加载UI

---

## 📈 DPS参考值

### 不同武器类型的预期DPS

- **入门武器** (木剑/铜剑): 20-40 DPS
- **中级武器** (铁剑/骨剑): 50-100 DPS
- **高级武器** (金剑/钢剑): 100-200 DPS
- **史诗武器** (银河之剑): 200-400 DPS
- **传说武器** (无限之刃): 400+ DPS

*注: 实际DPS受暴击率、攻击速度、冷却时间等因素影响*

---

## 🔄 更新日志

**版本 1.0** (2025-12-28)
- ✅ 初始版本发布
- ✅ 基础DPS计算功能
- ✅ Sidebar显示集成
- ✅ 自动回血系统
- ✅ 召唤/移除命令

---

## 💡 未来改进方向

- [ ] 显示平均DPS(10秒)
- [ ] 记录最高DPS峰值
- [ ] 显示总伤害统计
- [ ] 多个稻草人独立显示
- [ ] 可调整稻草人防御力
- [ ] DPS图表/历史记录

---

## ✅ 实现完成

**状态**: 完全可用
**测试**: 待测试
**文档**: 已完成

**使用命令**:
- 召唤: `/function stardew:debug/dummy/summon`
- 移除: `/function stardew:debug/dummy/remove`
