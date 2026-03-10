# 玩家数据系统 - 使用指南

## 系统概述

玩家数据系统实现了星露谷物语的核心玩家属性管理，包括：
- 生命值系统
- 能量值系统
- 金币系统
- 技能等级和经验系统（5种技能）
- 职业系统

所有数据通过 **NBT 格式**持久化保存到世界存储中（`world/data/stardew_player_data.dat`）。

## 核心类

### 1. PlayerStardewData
玩家数据类，存储单个玩家的所有星露谷相关数据。

### 2. PlayerDataManager
数据管理器，继承自 `SavedData`，负责所有玩家数据的存储和加载。

### 3. PlayerStardewDataAPI
静态 API 类，提供便捷的数据访问方法。

### 4. SkillType 枚举
5种技能类型：
- FARMING (农业)
- FISHING (钓鱼)
- FORAGING (觅食)
- MINING (采矿)
- COMBAT (战斗)

### 5. ProfessionType 枚举
30种职业，每个技能在5级和10级时可选择职业。

## API 使用示例

### 获取玩家数据
```java
PlayerStardewData data = PlayerStardewDataAPI.getData(player);
```

### 经验和等级
```java
// 添加经验
boolean leveledUp = PlayerStardewDataAPI.addExperience(player, SkillType.FARMING, 100);

// 获取等级
int level = PlayerStardewDataAPI.getSkillLevel(player, SkillType.FARMING);

// 获取经验
int exp = PlayerStardewDataAPI.getSkillExperience(player, SkillType.FARMING);

// 获取升级进度
float progress = PlayerStardewDataAPI.getLevelProgress(player, SkillType.FARMING);
```

### 能量系统
```java
// 获取当前能量
float energy = PlayerStardewDataAPI.getEnergy(player);

// 消耗能量
boolean success = PlayerStardewDataAPI.consumeEnergy(player, 5.0f);

// 恢复能量
PlayerStardewDataAPI.restoreEnergy(player, 10.0f);

// 检查是否疲惫
boolean exhausted = PlayerStardewDataAPI.isExhausted(player);

// 治愈疲惫
PlayerStardewDataAPI.cureExhaustion(player);

// 睡觉恢复（sleepTime 是游戏时间分钟数）
PlayerStardewDataAPI.sleep(player, 1440); // 0:00 AM 入睡
```

### 生命值系统
```java
// 获取生命值
int health = PlayerStardewDataAPI.getHealth(player);
int maxHealth = PlayerStardewDataAPI.getMaxHealth(player);

// 设置生命值
PlayerStardewDataAPI.setHealth(player, 50);

// 设置最大生命值
PlayerStardewDataAPI.setMaxHealth(player, 150);
```

### 金币系统
```java
// 获取金币
int money = PlayerStardewDataAPI.getMoney(player);

// 添加金币
PlayerStardewDataAPI.addMoney(player, 100);

// 移除金币
boolean success = PlayerStardewDataAPI.removeMoney(player, 50);

// 设置金币
PlayerStardewDataAPI.setMoney(player, 1000);
```

### 职业系统
```java
// 添加职业
PlayerStardewDataAPI.addProfession(player, ProfessionType.FIGHTER);

// 检查是否拥有职业
boolean hasProfession = PlayerStardewDataAPI.hasProfession(player, ProfessionType.FIGHTER);
```

## 测试命令

系统提供了完整的测试命令（需要 OP 权限）：

### 查看玩家数据
```
/stardew player info
```

### 经验相关
```
/stardew player exp add farming 100
/stardew player exp add fishing 500
/stardew player exp add foraging 200
/stardew player exp add mining 300
/stardew player exp add combat 1000
```

### 金币相关
```
/stardew player money add 1000        # 添加金币
/stardew player money remove 500      # 移除金币
/stardew player money set 5000        # 设置金币
```

### 能量相关
```
/stardew player energy add 50         # 恢复能量
/stardew player energy consume 20     # 消耗能量
/stardew player energy cure           # 治愈疲惫
```

### 生命值相关
```
/stardew player health set 50         # 设置生命值
/stardew player health setmax 150     # 设置最大生命值
```

### 重置数据
```
/stardew player reset                 # 重置玩家所有数据
```

## 数据自动保存

系统实现了多重保存机制：

1. **定时保存**: 每 5 分钟自动保存一次（6000 ticks）
2. **事件触发保存**:
   - 玩家登出时
   - 服务器关闭时
   - 数据被标记为 dirty 时
3. **延迟写入**: 使用 dirty 标记避免频繁 IO

## 事件处理

系统自动处理以下事件：

### PlayerEvent.PlayerLoggedInEvent
玩家登录时自动加载数据

### PlayerEvent.PlayerLoggedOutEvent
玩家登出时自动保存数据

### LivingDeathEvent
玩家死亡时：
- 损失 10% 金币（最多 1000）
- 重置生命值为满

## NBT 数据结构

```nbt
StardewPlayerData: {
    Health: 100,
    MaxHealth: 100,
    Energy: 270.0f,
    MaxEnergy: 270,
    Exhausted: 0b,
    Money: 500,
    
    SkillLevels: [0, 0, 0, 0, 0],  // farming, fishing, foraging, mining, combat
    Experience: [0, 0, 0, 0, 0],
    Professions: [],
    
    LastSyncTime: 1234567890L
}
```

## 经验值升级表

| 等级 | 累计经验 |
|------|---------|
| 1    | 100     |
| 2    | 380     |
| 3    | 770     |
| 4    | 1,300   |
| 5    | 2,150   |
| 6    | 3,300   |
| 7    | 4,800   |
| 8    | 6,900   |
| 9    | 10,000  |
| 10   | 15,000  |

## 注意事项

1. **服务端专用**: 所有数据管理在服务端进行，客户端需要通过网络包同步（待实现）
2. **线程安全**: 当前实现不是线程安全的，仅在服务器主线程调用
3. **数据迁移**: 如果修改 NBT 结构，需要考虑旧数据的兼容性

## 下一步计划

- [ ] 客户端网络同步
- [ ] HUD 显示（生命条、能量条、金币）
- [ ] 技能等级 UI
- [ ] 工具能量消耗集成
- [ ] 食物系统集成
- [ ] 职业效果实现
