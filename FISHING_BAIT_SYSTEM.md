# 星露谷物语钓鱼 - 鱼饵和渔具系统

## 鱼饵系统 (Bait)

### 鱼饵效果（来自 FishingRod.cs）

```csharp
Object bait = GetBait();  // attachments[BaitIndex]
double baitPotency = (bait != null) ? ((float)bait.Price / 10f) : 0f;
// 传递给 getFish(..., baitPotency, ...)
```

**核心逻辑**：
- 鱼饵效力 = 价格 / 10
- 传递给鱼生成函数，影响鱼的质量（提高获得高品质鱼的概率）

### 鱼饵列表

| 物品 ID | 名称 | 价格 | 效力 | 特殊效果 |
|---------|------|------|------|----------|
| 685 | Bait | 5g | 0.5 | 基础鱼饵 |
| 703 | Magnet | 100g | 10.0 | +15%宝箱概率 |
| 774 | Wild Bait | 50g | 5.0 | 可能同时咬两条鱼 |
| (魔法鱼饵) | Magic Bait | 5g | 0.5 | 忽略季节/天气/时间限制 |
| (豪华鱼饵) | Deluxe Bait | 5g | 0.5 | 不消耗耐久度（失败时） |
| (挑战鱼饵) | Challenge Bait | 5g | 0.5 | 只钓困难鱼 |

### 鱼饵消耗机制

```csharp
// 钓鱼成功或失败后
if (bait != null && (caughtFish || !hasEnchantment<PreservingEnchantment>()))
{
    bait.Stack--;
    if (bait.Stack <= 0)
        attachments[BaitIndex] = null;
}
```

- 成功：总是消耗
- 失败：除非有Preserving附魔，否则也消耗
- Deluxe Bait特殊：失败时不消耗

## 渔具系统 (Tackle)

### 渔具槽位

```csharp
public const int BaitIndex = 0;   // 鱼饵槽
public const int TackleIndex = 1; // 渔具槽（可扩展到多个）
```

### 渔具效果

| 渔具 ID | 名称 | 效果 |
|---------|------|------|
| 686 | Spinner | fishingBar更大 |
| 687 | Dressed Spinner | fishingBar更大 + 增加咬钩速度 |
| 691 | Barbed Hook | 减少逃跑速度 |
| 692 | Lead Bobber | 鱼不会向上游 |
| 693 | Treasure Hunter | 每个+5%宝箱概率 |
| 694 | Trap Bobber | 进度条下降速度减半 |
| 695 | Cork Bobber | fishingBar更大 |
| 856 | Curiosity Lure | 额外经验 |
| 877 | Quality Bobber | 提高鱼的品质 |

### 渔具耐久度

```csharp
public static int maxTackleUses = 20;

// 钓鱼成功后
for (int i = 1; i < attachments.Count; i++)
{
    if (attachments[i] != null)
    {
        attachments[i].uses.Value++;
        if (attachments[i].uses.Value >= maxTackleUses)
            attachments[i] = null;
    }
}
```

- 每次成功钓鱼，渔具使用次数+1
- 达到20次后损坏
- 失败不消耗耐久度

## 宝箱概率加成（详细）

```csharp
double extraTreasureChance = (double)Utility.getStringCountInList(tackleIds, "(O)693") * baseChanceForTreasure / 3.0;
// 693 = Treasure Hunter, 每个 +0.15/3 = +5%

bool treasure = Game1.random.NextDouble() < 
    baseChanceForTreasure           // 0.15 (15%)
    + who.LuckLevel * 0.005         // 运气等级 * 0.5%
    + (baitId == "(O)703" ? baseChanceForTreasure : 0.0)  // Magnet +15%
    + extraTreasureChance           // Treasure Hunter +5% per
    + who.DailyLuck / 2.0           // 每日运气 / 2
    + (who.professions.Contains(9) ? baseChanceForTreasure : 0.0); // Pirate职业 +15%
```

## Minecraft实现计划

### 1. 鱼竿容器系统
- FishingRodItem 添加 ItemStackHandler (2个槽位：鱼饵+渔具)
- Capability 或 DataComponent 存储附件
- GUI 界面允许玩家放置/取出鱼饵和渔具

### 2. 鱼饵效果实现
- `getBaitPotency()` 方法：读取鱼饵槽，返回 price/10
- 传递给 FishingSession
- 影响鱼的品质生成

### 3. 渔具效果实现
- TackleEffect 接口
- 每种渔具实现特定效果
- 在钓鱼小游戏中应用效果

### 4. 消耗机制
- 钓鱼结束时检查鱼饵/渔具
- 减少数量/耐久度
- 发送同步包更新客户端

## 当前任务优先级

1. ✅ 删除DEBUG日志
2. ✅ 恢复正常宝箱概率
3. ⏳ **实现鱼竿容器系统**（鱼饵槽+渔具槽）
4. ⏳ 实现鱼饵效力计算和传递
5. ⏳ 实现鱼饵消耗机制
6. ⏳ 实现基础渔具效果（宝箱概率加成）
7. ⏳ 实现高级渔具效果（小游戏难度修改）
