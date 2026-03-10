+# 星露谷物语 Minecraft Mod - 玩家系统顶层设计

## 一、系统概述

本文档定义了玩家核心数据系统的顶层架构，包括生命值、能量值、金币、技能等级和经验系统。

## 二、数据结构设计

### 2.1 核心数据类 `PlayerStardewData`

基于 Minecraft 的 NBT 数据系统和星露谷物语的设计，我们设计如下数据结构：

```java
public class PlayerStardewData {
    // ============ 基础属性 ============
    private int health;              // 当前生命值
    private int maxHealth;           // 最大生命值（基础100，可通过战斗等级和特殊物品提升）
    
    private float energy;            // 当前能量值（精力值）
    private int maxEnergy;           // 最大能量值（基础270，可通过星之果实等物品提升）
    private boolean exhausted;       // 是否处于疲惫状态
    
    private int money;               // 金币数量
    
    // ============ 技能系统 ============
    // 五种技能等级（0-10级）
    private int farmingLevel;        // 农业等级
    private int miningLevel;         // 采矿等级
    private int foragingLevel;       // 觅食等级
    private int fishingLevel;        // 钓鱼等级
    private int combatLevel;         // 战斗等级
    
    // 五种技能经验值
    private int[] experiencePoints = new int[5];  // farming(0), fishing(1), foraging(2), mining(3), combat(4)
    
    // 职业选择（5级和10级时选择）
    private List<Integer> professions;  // 存储已选择的职业ID
    
    // ============ 其他数据 ============
    private long lastSyncTime;       // 最后一次同步时间
    private boolean dirty;           // 数据是否需要保存
}
```

### 2.2 经验值升级表（基于星露谷物语）

| 等级 | 本级经验 | 累计经验 |
|------|---------|---------|
| 1    | 100     | 100     |
| 2    | 380     | 380     |
| 3    | 770     | 770     |
| 4    | 1,300   | 1,300   |
| 5    | 2,150   | 2,150   |
| 6    | 3,300   | 3,300   |
| 7    | 4,800   | 4,800   |
| 8    | 6,900   | 6,900   |
| 9    | 10,000  | 10,000  |
| 10   | 15,000  | 15,000  |

### 2.3 生命值系统

- **初始值**: 100点
- **增长规则**:
  - 战斗技能每提升1级（除了5级和10级）: +5点最大生命值
  - 战斗技能5级选择"战士"职业: +15点
  - 战斗技能10级选择"防御者"职业: +25点（需先选战士）
  - 特殊任务奖励（如骷髅洞穴100层）: +25点
- **最大可达**: 约200点（基础100 + 战斗8级×5 + 战士15 + 防御者25 + 任务25）

### 2.4 能量值系统

- **初始值**: 270点
- **增长规则**:
  - 获得星之果实: +34点（游戏中共7个星之果实）
  - 最大值: 508点（270 + 7×34）
- **消耗规则**:
  - 使用工具消耗能量（镐、斧、锄、水壶等）
  - 基础消耗: 2点
  - 技能等级提升会减少消耗（每级-0.1点）
- **恢复规则**:
  - 睡觉恢复满
  - 吃食物恢复
  - 温泉恢复（每0.1秒+1点）

### 2.5 技能经验获取规则

#### 农业 (Farming)
- 收获作物获得经验
- 照顾动物、挤奶、剪毛: +5经验
- 不同作物给予不同经验值

#### 采矿 (Mining)
- 破坏岩石获得经验
- 不同岩石类型给予不同经验
- 破坏方式不影响经验获取（镐/炸弹都可以）

#### 觅食 (Foraging)
- 拾取地面物品: +7经验
- 砍树: +14经验（树倒下时）
- 移除树桩: +2经验
- 采摘浆果: 每个+1经验

#### 钓鱼 (Fishing)
- 成功钓到鱼: 基础经验 = (品质+1)×3 + 难度÷3
- 完美钓鱼: 额外+40%经验
- 钓到宝箱: 额外+20%经验

#### 战斗 (Combat)
- 击败怪物获得经验
- 不同怪物给予不同经验值
- Boss战给予更多经验

## 三、数据持久化设计

### 3.1 存储方式

使用 Minecraft 的 **世界存储系统**（类似 `stardew_time_data.dat`）

```
world/
  data/
    stardew_player_data.dat  // 主世界玩家数据
    
或者使用玩家独立存储：
world/
  playerdata/
    <uuid>_stardew.dat  // 每个玩家一个文件
```

### 3.2 NBT 数据格式

```nbt
StardewPlayerData: {
    // 基础属性
    Health: 100,
    MaxHealth: 100,
    Energy: 270.0f,
    MaxEnergy: 270,
    Exhausted: 0b,
    Money: 500,
    
    // 技能等级
    Skills: {
        Farming: 0,
        Mining: 0,
        Foraging: 0,
        Fishing: 0,
        Combat: 0
    },
    
    // 技能经验
    Experience: [0, 0, 0, 0, 0],  // [farming, fishing, foraging, mining, combat]
    
    // 职业
    Professions: [0, 4, 18],  // 已选择的职业ID列表
    
    // 元数据
    LastSyncTime: 1234567890L,
    Dirty: 1b
}
```

### 3.3 数据同步策略

1. **定时保存**: 每5分钟自动保存一次
2. **事件触发保存**:
   - 玩家退出游戏
   - 经验值改变
   - 等级提升
   - 金币变动
3. **延迟写入**: 使用 `dirty` 标记避免频繁IO
4. **客户端同步**: 数据改变时通过网络包同步到客户端

## 四、核心类架构

```
com.stardew.craft.player/
├── PlayerStardewData.java           // 玩家数据类
├── PlayerDataManager.java           // 数据管理器（单例）
├── SkillType.java                   // 技能类型枚举
├── ProfessionType.java              // 职业类型枚举
├── ExperienceHelper.java            // 经验值计算辅助类
├── PlayerDataStorage.java           // 数据存储层
├── network/
│   ├── SyncPlayerDataPacket.java   // 同步数据包
│   └── UpdateSkillExpPacket.java   // 更新经验数据包
└── event/
    ├── SkillLevelUpEvent.java      // 技能升级事件
    ├── ProfessionChoiceEvent.java  // 职业选择事件
    └── PlayerEnergyEvent.java      // 能量变化事件
```

## 五、API 设计

### 5.1 核心 API

```java
public class PlayerStardewDataAPI {
    // 获取玩家数据
    public static PlayerStardewData getData(Player player);
    
    // 经验相关
    public static void addExperience(Player player, SkillType skill, int amount);
    public static int getSkillLevel(Player player, SkillType skill);
    public static int getSkillExperience(Player player, SkillType skill);
    
    // 能量相关
    public static float getEnergy(Player player);
    public static void consumeEnergy(Player player, float amount);
    public static void restoreEnergy(Player player, float amount);
    public static boolean isExhausted(Player player);
    
    // 生命值相关
    public static void setMaxHealth(Player player, int maxHealth);
    public static int getMaxHealth(Player player);
    
    // 金币相关
    public static void addMoney(Player player, int amount);
    public static boolean removeMoney(Player player, int amount);
    public static int getMoney(Player player);
    
    // 职业相关
    public static void addProfession(Player player, ProfessionType profession);
    public static boolean hasProfession(Player player, ProfessionType profession);
}
```

### 5.2 事件系统

```java
// 技能升级事件
public class SkillLevelUpEvent extends PlayerEvent {
    private final SkillType skill;
    private final int oldLevel;
    private final int newLevel;
    // 可以取消升级
}

// 经验获取事件
public class GainExperienceEvent extends PlayerEvent {
    private final SkillType skill;
    private int amount;  // 可修改
    // 可以取消
}
```

## 六、UI/HUD 显示

### 6.1 HUD 元素

1. **能量条**: 在生命值下方显示（蓝色）
2. **金币显示**: 屏幕右上角
3. **经验条**: 技能升级时短暂显示
4. **等级提示**: 升级时弹出提示

### 6.2 技能菜单

- 按键打开（如 'K' 键）
- 显示所有技能等级和进度
- 点击查看详细信息和职业树

## 七、与 Minecraft 系统集成

### 7.1 生命值映射

- Stardew 生命值独立于 Minecraft 生命值
- 受到伤害时同时扣除两个系统的生命
- Stardew 生命值为0时：
  - Minecraft 不会死亡
  - 传送回床/家
  - 损失部分金币和物品

### 7.2 能量值与饥饿值

- 能量值独立于 Minecraft 饥饿值
- 工具使用消耗能量值而非饥饿值
- 能量为0时进入疲惫状态：
  - 移动速度减慢
  - 无法使用部分工具

### 7.3 经验值系统

- Stardew 技能经验独立于 Minecraft 经验值
- 可配置是否同时奖励 Minecraft 经验

## 八、配置文件

```json
{
  "player_system": {
    "starting_health": 100,
    "starting_energy": 270,
    "starting_money": 500,
    
    "energy_system": {
      "exhaustion_enabled": true,
      "exhaustion_speed_penalty": 0.5,
      "tool_energy_cost_base": 2.0,
      "skill_energy_reduction_per_level": 0.1
    },
    
    "sync_settings": {
      "auto_save_interval_ticks": 6000,
      "sync_on_exp_change": true,
      "sync_on_money_change": true
    },
    
    "integration": {
      "override_minecraft_hunger": false,
      "grant_minecraft_exp_on_skill_up": true,
      "death_money_loss_percentage": 0.1
    }
  }
}
```

## 九、实现优先级

### Phase 1: 核心数据系统（最优先）
1. ✅ PlayerStardewData 数据类
2. ✅ PlayerDataManager 管理器
3. ✅ 数据存储和加载（NBT）
4. ✅ 基础 API

### Phase 2: 技能系统
1. ✅ 技能等级和经验
2. ✅ 经验值计算
3. ✅ 等级提升逻辑
4. ✅ 职业系统基础

### Phase 3: 能量系统
1. ✅ 能量消耗和恢复
2. ✅ 疲惫状态
3. ✅ 工具集成

### Phase 4: UI 系统
1. ✅ HUD 显示
2. ✅ 技能菜单
3. ✅ 升级提示

### Phase 5: 网络同步
1. ✅ 数据包设计
2. ✅ 客户端同步

## 十、参考资料

- 星露谷物语官方 Wiki: https://stardewvalleywiki.com/
  - Skills: https://stardewvalleywiki.com/Skills
  - Energy: https://stardewvalleywiki.com/Energy
  - Health: https://stardewvalleywiki.com/Health
- 星露谷物语源代码: `Farmer.cs`, `SkillsPage.cs`

---

**文档版本**: 1.0  
**创建日期**: 2026-01-08  
**最后更新**: 2026-01-08
