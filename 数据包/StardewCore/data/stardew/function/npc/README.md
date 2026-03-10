# NPC系统使用指南

## 📁 整理后的系统架构

### 核心概念
NPC系统采用**三层架构**，每层职责清晰：

```
主循环 → 调度层 → 执行层
```

### 调用链
```
main.mcfunction
  └─> npc/tick.mcfunction          ← 【统一入口】
      └─> npc/abigail/tick.mcf     ← 【单个NPC入口】
          ├─> 交互检测
          ├─> 动画更新
          └─> 位置同步
```

## 🎯 系统组成

每个NPC = 3个实体：
1. **Villager** (逻辑) - NoAI, 隐身, 数据载体
2. **Animated Java** (视觉) - 模型和动画
3. **Interaction** (交互) - 检测玩家点击

## 📂 文件结构

```
npc/
├── tick.mcfunction          ← 主入口
├── system/init.mcfunction   ← 初始化
└── abigail/
    ├── tick.mcfunction      ← 阿比盖尔入口
    ├── spawn.mcfunction
    ├── remove.mcfunction
    ├── data/                ← 数据配置
    ├── interact/            ← 交互逻辑
    ├── animation/           ← 动画管理
    └── gifts/               ← 礼物系统
```

## 🔄 执行流程

```
每tick:
  1. 检测interaction交互 → talk/gift
  2. 检测位置变化 → 切换动画
  3. 同步AJ模型和interaction位置
```

## ⚙️ 使用方法

```mcfunction
# 初始化
function stardew:npc/system/init

# 召唤
execute positioned ~ ~ ~ run function stardew:npc/abigail/spawn

# 移除
function stardew:npc/abigail/remove
```

## 🔧 添加新NPC

1. 复制 `abigail/` 文件夹
2. 修改所有标签
3. 在 `npc/tick.mcfunction` 添加调用

## 📊 关键数据

- `stardew.animation` - 1=idle, 2=walk
- `stardew.friendship` - 友谊点数
- `stardew.npc.pos_x/y/z` - 位置追踪

## ✨ 优化亮点

- ✅ 统一入口，清晰层次
- ✅ 自动位置同步
- ✅ 自动动画切换
- ✅ 易于扩展新NPC

详细文档: ARCHITECTURE.md
