# NPC系统重构对比

## 🔴 整理前的问题

### 调用混乱
```mcfunction
# main.mcfunction 中直接调用多个函数
function stardew:npc/abigail/interact/detect
function stardew:npc/abigail/animation/tick
```

### 文件分散
- `interact/detect.mcfunction` - 交互检测
- `animation/tick.mcfunction` - 动画管理
- `animation/check_motion.mcfunction` - 运动检测
- 没有统一的NPC入口点

### 职责不清
- 交互和动画分别独立调用
- 没有位置同步逻辑
- 难以添加新功能

---

## 🟢 整理后的改进

### 统一入口
```mcfunction
# main.mcfunction 只调用一个函数
function stardew:npc/tick  ← 所有NPC系统从这里开始
```

### 清晰层次
```
npc/tick.mcfunction
  └─> npc/abigail/tick.mcfunction
      ├─> 交互检测
      ├─> 动画更新
      └─> 位置同步
```

### 单一职责
- **npc/tick.mcfunction** - 调度所有NPC
- **npc/abigail/tick.mcfunction** - 管理阿比盖尔的所有逻辑
- **animation/update.mcfunction** - 只负责动画

---

## 📊 对比表格

| 方面 | 整理前 | 整理后 |
|------|--------|--------|
| 入口点 | 多个分散的函数调用 | 单一统一入口 |
| 层次 | 扁平，混乱 | 三层清晰架构 |
| 可维护性 | ⭐⭐ | ⭐⭐⭐⭐⭐ |
| 可扩展性 | 难以添加新NPC | 复制粘贴即可 |
| 代码复用 | 低 | 高 |
| 位置同步 | ❌ 无 | ✅ 自动 |

---

## 🎯 核心改进

### 1. 统一调度
**之前**: main.mcfunction 需要知道每个NPC的所有细节
```mcfunction
function stardew:npc/abigail/interact/detect
function stardew:npc/abigail/animation/tick
# 如果有10个NPC，就要20行代码
```

**现在**: main.mcfunction 只需要调用一个函数
```mcfunction
function stardew:npc/tick
# 无论有多少NPC，都只需要1行
```

### 2. 封装隔离
**之前**: 所有逻辑暴露在外层
```
main.mcfunction
  ├─> detect
  ├─> animation
  └─> ... (杂乱)
```

**现在**: 每个NPC管理自己的逻辑
```
main.mcfunction
  └─> npc/tick
      └─> abigail/tick (封装内部)
          ├─> detect
          ├─> animation
          └─> sync
```

### 3. 位置同步
**之前**: ❌ AJ模型和interaction可能不跟随村民

**现在**: ✅ 每tick自动同步位置
```mcfunction
execute as @e[tag=npc.abigail] at @s run tp @e[tag=npc.abigail.visual,limit=1,sort=nearest] ~ ~ ~
```

### 4. 动画整合
**之前**: 
- `animation/tick.mcfunction` - 主循环
- `animation/check_motion.mcfunction` - 检测运动
- 功能重复，职责模糊

**现在**:
- `animation/update.mcfunction` - 一个函数搞定所有

---

## 🚀 扩展性

### 添加新NPC只需3步

**之前**: 需要修改多个地方
1. 修改 main.mcfunction
2. 创建多个分散的文件
3. 手动管理调用关系

**现在**: 非常简单
1. 复制 `abigail/` 文件夹
2. 修改标签
3. 在 `npc/tick.mcfunction` 添加1行:
   ```mcfunction
   execute if entity @e[tag=npc.alex,limit=1] run function stardew:npc/alex/tick
   ```

---

## ✅ 文件变更总结

### 废弃的文件
- ❌ `npc/abigail/interact/detect.mcfunction` → 合并到 tick
- ❌ `npc/abigail/animation/tick.mcfunction` → 合并到 tick
- ❌ `npc/abigail/animation/check_motion.mcfunction` → 改名为 update

### 新增的文件
- ✅ `npc/tick.mcfunction` - 统一入口
- ✅ `npc/abigail/tick.mcfunction` - NPC入口
- ✅ `npc/abigail/animation/update.mcfunction` - 动画更新
- ✅ `npc/ARCHITECTURE.md` - 架构文档
- ✅ `npc/README.md` - 使用指南

### 修改的文件
- 📝 `main.mcfunction` - 简化调用
- 📝 所有animation相关逻辑优化

---

## 📝 总结

**整理前**: 像意大利面条一样杂乱无章 🍝
**整理后**: 像乐高积木一样结构清晰 🧱

核心理念: **分层、封装、单一职责**
