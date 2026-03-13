# 星露谷物语钓鱼宝箱系统完整逻辑

## 核心算法

```csharp
// 宝箱物品生成采用概率递减循环
float chance = 1f; // 初始100%概率
float chanceMult = 0.4f; // 普通宝箱
// float chanceMult = 0.6f; // 金色宝箱

while (Game1.random.NextDouble() <= chance)
{
    chance *= chanceMult; // 每轮递减
    // 生成一个物品...
}
```

**关键点**：
- 第一个物品：100%概率
- 第二个物品：40%概率（普通）/ 60%概率（金色）
- 第三个物品：16%概率（普通）/ 36%概率（金色）
- 第四个物品：6.4%概率（普通）/ 21.6%概率（金色）

## 物品类别系统

每次循环随机选择一个类别（0-3）：

### Case 0: 矿物和基础资源
根据 `clearWaterDistance` 决定矿物质量：
- `<= 20`: Coal (378)
- `<= 40`: Iron Ore (380)  
- `<= 80`: Gold Ore (384)
- `> 80`: Iridium Ore (386)
- 还可能获得 Wood (388) 或 Stone (390)

**数量**: 2-16个（随机）

### Case 1: 鱼饵和浮标
- Dressed Spinner (687): 33%
- Wild Bait (774): 33%
- Sonar Bobber: 如果玩家已知配方
- Deluxe Bait: 如果已解锁
- Bait (685): 默认选项

**数量**: 大部分是1个

### Case 2: 书籍和文物（钓鱼等级 >= 2）
- Lost Book (102): 如果未收集全10本
- Artifacts (585-588): 如果尚未捐赠
- Random Artifacts (103-120): 如果尚未捐赠
- Geode (535)

### Case 3: 宝石和高级物品
**基础池（钓鱼等级 >= 2）**:
- Geode (535-537): 根据clearWaterDistance
- Random Gems (60-86): 宝石组
- Diamond (72): 5%额外概率，且有5%翻倍
- Glow Ring/Magnet Ring (516-519): 7% * luckModifier
- Iridium Band等 (529-534): 7% * luckModifier
- Treasure Chest (166): 2% * luckModifier
- Prismatic Shard (74): 0.1% * luckModifier（钓鱼等级>5）
- Amethyst (66): 1% * luckModifier
- Aquamarine (62): 1% * luckModifier
- Lucky Ring (527): 1% * luckModifier
- Boots (504-513): 1% * luckModifier
- Golden Egg (928): 1% * luckModifier（需要 Farm_Eternal 成就）

**特殊武器（需要luckModifier）**:
- Neptune's Glaive (14): 5% * luckModifier
- Broken Trident (51): 5% * luckModifier

**技能书**: 
- 如果玩家已获得3+个宝箱，有5%几率获得技能书
- 获得技能书后，本次循环结束（chance = 0）

**保底机制**: 如果Case 3只有1个物品，额外给一个钻石(72)

## 金色宝箱特殊逻辑

50%概率额外获得一个特殊物品：
- Iridium Bar (337): 25%
- Skill Books: 20%
- Challenge Bait (908): 15%
- Magnet (703): 10%  
- Fairy Dust (872): 10%
- Dressed Spinner (687): 5%
- Stardrop Tea (928): 5%
- Pearl (797): 5%
- Lucky Ring (859): 2.5%
- Prismatic Shard (74): 2%
- Sonar Bobber (SonarBobber): 0.5%

## 季节性物品

**春天特殊**:
- Rice Shoots (273): 10%概率

## 特殊任务和条件

1. **Wild Bait配方**: 如果玩家没有配方，可能获得
2. **Qi Beans**: 特殊订单活跃时
3. **Mystery Boxes**: 基于运气值随机生成
4. **Mastery Rewards**: 特定等级掌握奖励（Golden Crackers等）
5. **Trout Derby Tag**: 如果参加鳟鱼节
6. **Golden Bobber**: 沙漠节任务特殊奖励
7. **Roe**: 25%概率（需要Book_Roe）

## Luck Modifier 计算

```csharp
float luckModifier = (1f + (float)who.DailyLuck) * ((float)clearWaterDistance / 5f);
```

影响因素：
- 玩家每日运气值
- 清水距离（水质越好，运气加成越高）

## 保底机制

如果所有循环结束后宝箱为空：
```csharp
treasures.Add("(O)685", 5-15个); // Bait
```

## Minecraft对应物品映射

| 星露谷物品 ID | 名称 | Minecraft 对应 |
|--------------|------|----------------|
| 378 | Coal | minecraft:coal |
| 380 | Iron Ore | minecraft:raw_iron |
| 384 | Gold Ore | minecraft:raw_gold |
| 386 | Iridium Ore | minecraft:netherite_scrap |
| 388 | Wood | minecraft:oak_planks |
| 390 | Stone | minecraft:cobblestone |
| 687 | Dressed Spinner | minecraft:fishing_rod (附魔) |
| 774 | Wild Bait | minecraft:tropical_fish |
| 685 | Bait | minecraft:cod |
| 102 | Lost Book | minecraft:book |
| 535-537 | Geodes | minecraft:amethyst_cluster |
| 60 | Emerald | minecraft:emerald |
| 62 | Aquamarine | minecraft:prismarine_crystals |
| 64 | Ruby | minecraft:redstone |
| 66 | Amethyst | minecraft:amethyst_shard |
| 68 | Topaz | minecraft:gold_nugget |
| 70 | Jade | minecraft:lime_dye |
| 72 | Diamond | minecraft:diamond |
| 74 | Prismatic Shard | minecraft:nether_star |
| 166 | Treasure Chest | minecraft:chest (with loot) |
| 516-519 | Rings | minecraft:iron_horse_armor |
| 527 | Lucky Ring | minecraft:golden_horse_armor |
| 529-534 | Special Rings | minecraft:diamond_horse_armor |

## 实现关键点

1. **必须使用 while 循环**，不能用固定数量
2. **chanceMult = 0.4f** 是核心平衡参数
3. **clearWaterDistance** 决定矿石质量（需要根据钓鱼位置计算）
4. **luckModifier** 影响稀有物品概率（玩家运气 + 水质）
5. **保底机制**确保至少有鱼饵
6. **物品数量**通常在 1-4 个之间，罕见情况 5+ 个

## 当前实现的问题

❌ **错误**: 使用固定 minItems=3, maxItems=6
✅ **正确**: 使用 while 循环和概率递减

❌ **错误**: 没有实现 Case 分类系统
✅ **正确**: 4个Case，每次随机选择

❌ **错误**: 没有 luckModifier 和 clearWaterDistance
✅ **正确**: 必须根据钓鱼环境计算这些值

## TODO（后续再做）

- [ ] 钓鱼宝箱戒指类物品按原版 1:1 补齐（516-519、527、529-534）
