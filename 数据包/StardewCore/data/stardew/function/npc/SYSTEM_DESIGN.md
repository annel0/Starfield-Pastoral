# StardewCraft NPC 系统设计文档 v2.0

## 系统架构

### 1. 实体组成
每个NPC由以下实体组成：
- **Villager（村民）**：逻辑实体（NoAI=1），携带所有NBT数据
- **Animated Java模型**：视觉实体，绑定到村民位置
- **Interaction实体**：交互检测，绑定到村民位置

### 2. 核心功能

#### 交互系统
- **右键点击**：触发对话（每日一次）
- **Shift+右键点击**：赠送礼物
- **检测方式**：通过Interaction实体的attack/interaction数据

#### 对话系统
- 集成官方对话文本（JSON格式）
- 根据星期、季节、天气、友谊等级选择对话
- 每日只能对话一次（+20友谊值）

#### 礼物系统
- 基于loot table的物品识别
- 礼物偏好等级：Love (+80), Like (+45), Neutral (+20), Dislike (-20), Hate (-40)
- 每周最多送2次礼物
- 生日礼物效果x8

#### 日程系统
- 根据时间、星期、季节、天气控制NPC移动
- 自动切换idle/walk动画
- 路径点导航

#### 友谊系统
- 10心制（结婚候选人14心）
- 每250分=1心
- 每日未对话会衰减

### 3. 数据存储

#### NBT结构（存储在Villager实体）
```json
{
  npc: {
    id: "abigail",
    name: "阿比盖尔",
    birthday: {season: "fall", day: 13},
    current_location: "abigail_room",
    animation_state: "idle",
    last_update_time: 0
  },
  player_data: {
    <UUID>: {
      friendship: 0,
      hearts: 0,
      last_talk_day: 0,
      gifts_this_week: 0,
      has_bouquet: false
    }
  }
}
```

#### Storage结构
```
storage stardew:npc_data abigail.{
  dialogue: {...},  # 所有对话文本
  gifts: {...},     # 礼物偏好
  schedule: {...}   # 日程表
}
```

### 4. 计分板

```mcfunction
stardew.npc.id              # NPC唯一ID
stardew.interact.type       # 交互类型 (0=无, 1=对话, 2=送礼)
stardew.friendship          # 友谊值（每个玩家）
stardew.hearts              # 心数等级
stardew.daily_talked        # 今日是否已对话
stardew.gifts_given         # 本周赠礼次数
stardew.time.hour           # 游戏内小时
stardew.time.day            # 游戏内天数
stardew.season              # 季节(1=春,2=夏,3=秋,4=冬)
```

### 5. 文件结构

```
stardew/function/npc/
├── system/
│   ├── init.mcfunction                 # 初始化
│   ├── tick.mcfunction                 # 主循环
│   ├── detect_interaction.mcfunction   # 检测交互
│   ├── handle_talk.mcfunction          # 处理对话
│   ├── handle_gift.mcfunction          # 处理送礼
│   └── update_schedule.mcfunction      # 更新日程
├── abigail/
│   ├── spawn.mcfunction                # 召唤NPC
│   ├── data.mcfunction                 # 数据初始化
│   ├── dialogue/
│   │   ├── parse_dialogue.mcfunction   # 解析对话
│   │   ├── select_dialogue.mcfunction  # 选择对话
│   │   └── ... (各种对话条件)
│   ├── gifts/
│   │   ├── check_gift.mcfunction       # 检查礼物
│   │   └── gift_reaction.mcfunction    # 礼物反应
│   ├── schedule/
│   │   ├── spring.mcfunction           # 春季日程
│   │   ├── summer.mcfunction           # 夏季日程
│   │   ├── fall.mcfunction             # 秋季日程
│   │   ├── winter.mcfunction           # 冬季日程
│   │   └── move_to.mcfunction          # 移动到目标点
│   └── animation/
│       ├── update.mcfunction           # 更新动画
│       ├── play_idle.mcfunction        # 播放idle
│       └── play_walk.mcfunction        # 播放walk
└── data/
    └── dialogue_texts.json             # 对话文本数据
```

## 实现步骤

1. ✅ 创建系统文档
2. ⏳ 获取地图坐标
3. ⏳ 实现核心NPC生成系统
4. ⏳ 实现交互检测（右键/Shift+右键）
5. ⏳ 集成对话系统
6. ⏳ 实现礼物系统
7. ⏳ 实现日程系统
8. ⏳ 实现动画控制
9. ⏳ 测试和优化
