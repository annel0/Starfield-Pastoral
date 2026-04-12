# 晕倒 / 倒地系统实现规划

> 对标 SDV 原版 Farmer.cs / Game1.cs / Event.cs，排除姜岛（未实装）。
> 创造模式对以下所有惩罚完全豁免。

---

## 一、系统概览

两条独立触发路径，共用部分基础设施（金币扣除、邮件、体力惩罚、屏幕过渡）。

| 场景 | 触发条件 | 金币惩罚 | 物品丢失 | 次日体力 |
|------|---------|---------|---------|---------|
| A. 战斗死亡（矿井） | SDV HP ≤ 0 | `rand(Money/40, Money/8)`, 上限 15000g | 最多 3 件, 基础 22% | 压到 2 |
| B. 战斗死亡（非矿井） | SDV HP ≤ 0 | 固定上限 1000g | 最多 3 件, 基础 22% | 压到 2 |
| C. 2:00 AM 晕倒 | 时间 ≥ 2600 | `min(1000, Money/10)` | 无 | 按公式递减 |
| D. 体力耗尽晕倒 | stamina ≤ -15 | 同 C | 无 | 同 C |

---

## 二、场景 A/B：战斗死亡（HP 归零）

### 2.1 触发点

**现有代码**：`PlayerDataEventHandler.onPlayerHurt()` → HP ≤ 0 时调用 `StardewDamageHooks.onHealthDepleted(player, source)`。

**改动**：在 `StardewDamageHooks` 注入真正的 `KnockoutHandler` 实现 → `PassOutService.onCombatDeath(player, source)`。

### 2.2 创造模式豁免

```java
if (player.isCreative()) {
    // 恢复满血，不做任何惩罚
    data.setHealth(data.getMaxHealth());
    return;
}
```

### 2.3 金币惩罚

```
// 矿井维度
int moneyToLose = random.nextInt(money / 40, money / 8 + 1);
moneyToLose = Math.min(moneyToLose, 15000);
moneyToLose -= (int)(luckLevel * 0.01 * moneyToLose);
moneyToLose -= moneyToLose % 100; // 向下取整到百位

// 非矿井（星露谷维度地面等）
int moneyToLose = Math.min(1000, money);
```

- `luckLevel` = `PlayerStardewDataAPI.getLuckBuffLevel(player)` (已有)
- `money` = `PlayerStardewData.getMoney()`

### 2.4 物品丢失 — `LoseItemsOnDeath`

```
double itemLossRate = 0.22 - luckLevel * 0.04 - dailyLuck;
int lost = 0;
// 从背包末尾向前遍历
for (slot = inventory.size - 1; slot >= 0; slot--) {
    if (lost >= 3) break;
    ItemStack stack = inventory.getItem(slot);
    if (stack.isEmpty()) continue;
    if (!canBeLostOnDeath(stack)) continue;
    if (random.nextDouble() < itemLossRate) {
        lostItems.add(stack.copy());
        inventory.setItem(slot, ItemStack.EMPTY);
        lost++;
    }
}
```

**`canBeLostOnDeath(stack)` 判定**（SDV 原版逻辑）：
- 返回 **false**（不可丢失）的条件：
  - 是工具（Tool 类型）
  - 是武器（MeleeWeapon / Slingshot）
  - 是戒指（Ring）
  - 是靴子（Boots）
- 其他所有物品均可丢失
- MC 映射：检查 `stack.getItem()` 是否属于 `StardewToolItem` / `CombatWeaponItem` / `StardewRingItem` / `StardewBootsItem` 等基类

**丢失物品存储**：存入 `PlayerStardewData.itemsLostLastDeath: List<ItemStack>`，序列化到 NBT。

### 2.5 Marlon 物品找回

- 死亡次日发送邮件 `MarlonRecovery`：「我在矿井门口发现了你丢的一些东西。来冒险家公会看看，我可以帮你找回一件，不过得收点费用。」
- `MarlonService` 新增 `openRecoveryShop(player)` 方法
- 显示 `itemsLostLastDeath` 列表，每件物品售价 = 原价 × 5（SDV 公式，最低 250g）
- 玩家只能买回 **1 件**，购买后清空 `itemsLostLastDeath`
- 如果没丢东西则不发邮件

### 2.6 次日体力惩罚

```java
// 战斗死亡 → 次日能量压到 2
playerData.setEnergy(Math.min(playerData.getEnergy(), 2.0f));
```

在 `StardewTimeManager.advanceDayWithSleepTime()` 中，在调用 `sleep()` 之后检查 `passedOutFromCombat` 标志并覆盖能量。

### 2.7 复活位置

| 倒地维度 | 复活位置 |
|---------|---------|
| 矿井维度 (`STARDEW_MINING`) | 传送回星露谷维度 → 农场出生点 `(150, -12, 119)` |
| 星露谷维度 (`STARDEW_VALLEY`) | 原地恢复 → 传送到农场出生点 |

### 2.8 救援邮件

矿井死亡次日邮件（随机 1 封）：

| 权重 | 发件人 | 邮件 ID | 内容 |
|-----|--------|---------|------|
| 25% | Robin | `mineDeath_Robin` | 「我在矿井里发现你昏倒了。差点把你当成异域木材！」 |
| 25% | Clint | `mineDeath_Clint` | 「我在矿井发现你晕过去了。把你拖上来的时候差点闪了腰。」 |
| 25% | Maru | `mineDeath_Maru` | 「我在矿井找到了你，你伤得不轻。我做了些基本的医疗处理。」 |
| 25% | Linus | `mineDeath_Linus` | 「我在矿井发现你昏迷不醒。你真走运，我正好路过！」 |

非矿井死亡：

| 发件人 | 邮件 ID | 内容 |
|--------|---------|------|
| Harvey | `hospitalDeath` | 「有人把你送到诊所来了。你之前晕倒了！医疗费 {cost}g。」 |

> 邮件中 `{cost}` 用实际扣除金额替换。

### 2.9 即时效果动作序列

1. 取消 MC 死亡事件（已有）
2. 冻结玩家输入 3 秒（`player.setDeltaMovement(0,0,0)` + 禁止移动）
3. 服务端：计算金币扣除 + 物品丢失 + 设标志
4. 发送 `PassOutPayload` 到客户端（含：类型=combat_death, 金币损失, 丢失物品列表）
5. 客户端：播放死亡音效 + 屏幕渐黑 + 显示惩罚摘要
6. 渐黑完成后：传送回农场 → 触发隔夜结算流程

---

## 三、场景 C/D：2:00 AM 晕倒 / 体力耗尽

### 3.1 触发点

**现有代码**：`DimensionEventHandler.onLevelTick()` L336-338 已有 `dayTime >= 20000` 检测，目前直接调 `advanceToNextMorning("pass_out_2am")`。

**改动**：在调用 `advanceToNextMorning` 之前插入 passout 惩罚逻辑。

**体力耗尽触发**：在 `PlayerStardewData.consumeEnergy()` 中，当 `energy ≤ -15` 时设置 `shouldPassOut = true` 标志，下一个 tick 触发晕倒。

### 3.2 创造模式豁免

```java
if (player.isCreative()) {
    // 直接推日，不扣钱，不倒地
    advanceToNextMorning(sleepMinute);
    return;
}
```

### 3.3 金币惩罚

```java
int maxCost = 1000; // Default 上下文
int moneyToLose = Math.min(maxCost, player.getMoney() / 10);
```

**安全位置豁免（不扣钱）**：
- 在自己的农舍建筑内部（室内子空间系统判定）

### 3.4 无物品丢失

2AM 晕倒不丢物品。

### 3.5 次日体力惩罚

已在 `PlayerStardewData.sleep()` 中实现：越晚睡恢复越少。2:00 AM 晕倒等价于 `sleepTime = 1560`（最晚），体力恢复最少。

### 3.6 晕倒邮件

随机 1 封（等概率）：

| 发件人 | 邮件 ID | 内容 | 条件 |
|--------|---------|------|------|
| Morris (Joja) | `passedOut1` | 「JojaMart 员工发现你昏倒了。已从你账户扣除 {cost}g 医疗服务费。」 | 社区中心未完成 |
| Linus | `passedOut2` | 「好在我昨晚找到了你！我看到有人在翻你的口袋……不知道被偷了多少钱。」 | 默认 |
| Harvey | `passedOut3` | 「有人把你送来诊所了。你因过度劳累晕倒了！医疗费 {cost}g。」 | Harvey 存在且非配偶 |
| Marlon | `passedOut4` | 「凌晨巡逻时发现你趴在泥地里。下次早点回家！」 | 33% + 社区中心完成 |

> 因为我们暂无社区中心系统，简化为：随机选 `passedOut2`（Linus, 40%） / `passedOut3`（Harvey, 40%） / `passedOut4`（Marlon, 20%）

### 3.7 即时效果动作序列

1. 冻结玩家操作
2. 服务端：计算金币扣除 + 设标志
3. 发送 `PassOutPayload` 到客户端（类型=exhaustion_2am, 金币损失）
4. 客户端：屏幕渐黑
5. 传送回农场 → 触发隔夜结算流程

---

## 四、客户端表现

### 4.1 屏幕渐黑过渡

新增 `PassOutOverlayScreen`（全屏黑色渐变）：
- 收到 `PassOutPayload` 后触发
- 0→1 秒：屏幕从正常到全黑
- 1→2 秒：黑屏保持，显示文字「你倒下了……」
- 2→3 秒：淡出 → 进入隔夜结算屏幕队列

### 4.2 惩罚摘要显示

在隔夜结算屏幕队列**最前面**插入 `PassOutSummaryScreen`：
- 显示倒地原因（"你在矿井中被击倒了" / "你因过度疲劳倒下了"）
- 显示金币损失（如有）
- 显示丢失物品（如有）
- 点击或 3 秒后继续到正常隔夜结算

---

## 五、网络协议

### 5.1 `PassOutPayload`（S→C）

```java
public record PassOutPayload(
    PassOutType type,       // COMBAT_MINE, COMBAT_OVERWORLD, EXHAUSTION_2AM, EXHAUSTION_STAMINA
    int moneyLost,          // 扣了多少钱
    List<ItemStack> lostItems  // 丢失的物品（仅战斗死亡）
) implements CustomPacketPayload
```

### 5.2 `PassOutAckPayload`（C→S）

客户端渐黑完成后发送确认，服务端收到后执行传送 + 推日。

---

## 六、数据持久化

### 6.1 PlayerStardewData 新增字段

```java
// 战斗死亡标志，次日 sleep() 后用于压体力到 2
boolean passedOutFromCombat;

// 上次死亡丢失的物品（供 Marlon 找回）
List<ItemStack> itemsLostLastDeath;
```

NBT 序列化：`passedOutFromCombat` → boolean, `itemsLostLastDeath` → ListTag of CompoundTag。

### 6.2 MailRegistry 新增邮件

在 `MailRegistry` 或对应的注册位置添加以上所有邮件 ID 和内容模板。

---

## 七、新增 & 修改文件清单

### 新增文件

| 文件 | 用途 |
|------|------|
| `PassOutService.java` | 核心逻辑：金币计算、物品丢失、标志设置、邮件安排 |
| `PassOutPayload.java` | S→C 网络包 |
| `PassOutAckPayload.java` | C→S 确认包 |
| `PassOutOverlayScreen.java` | 客户端渐黑过渡 |
| `PassOutSummaryScreen.java` | 客户端惩罚摘要屏幕 |

### 修改文件

| 文件 | 改动 |
|------|------|
| `StardewDamageHooks.java` | 注入 `PassOutService::onCombatDeath` 作为 KnockoutHandler |
| `PlayerDataEventHandler.java` | `onPlayerDeath` 中添加创造模式豁免 |
| `PlayerStardewData.java` | 新增 `passedOutFromCombat` / `itemsLostLastDeath` 字段 + NBT |
| `DimensionEventHandler.java` | 2AM 触发改为先走 `PassOutService.onExhaustion2AM()` |
| `StardewTimeManager.java` | `advanceDayWithSleepTime()` 中检查 `passedOutFromCombat` 标志覆盖体力 |
| `MarlonService.java` | 新增 `openRecoveryShop(player)` 物品找回功能 |
| `MailRegistry` / 邮件注册 | 注册所有 passout/death 邮件 |
| `ModSounds.java` | 注册 passout/death 音效（可选） |
| `ClientOvernightHandler.java` | 在 screenStack 前插入 PassOutSummaryScreen |
| 网络包注册 | 注册 PassOutPayload / PassOutAckPayload |

---

## 八、实现优先级

| 阶段 | 内容 | 预估工作量 |
|------|------|-----------|
| **P0** | `PassOutService` 核心逻辑（金币扣除 + 物品丢失 + 标志） | 中 |
| **P0** | 战斗死亡接入（StardewDamageHooks 注入） | 小 |
| **P0** | 2AM 晕倒接入（DimensionEventHandler 改造） | 小 |
| **P0** | 创造模式豁免 | 小 |
| **P1** | 网络包 + 客户端渐黑过渡 `PassOutOverlayScreen` | 中 |
| **P1** | `PassOutSummaryScreen` 惩罚摘要 | 中 |
| **P1** | 邮件注册 + 投递 | 小 |
| **P1** | 次日体力覆盖（战斗死亡→2, 2AM→公式） | 小 |
| **P2** | Marlon 物品找回商店 | 中 |
| **P2** | 体力 ≤ -15 晕倒 | 小 |
| **P2** | 音效（倒地/晕倒） | 小 |

---

## 九、SDV 原版公式速查

### 矿井死亡金币

```
moneyToLose = rand(Money/40, Money/8)
moneyToLose = min(moneyToLose, 15000)
moneyToLose -= (int)(luckLevel * 0.01 * moneyToLose)
moneyToLose -= moneyToLose % 100
```

### 非矿井死亡金币

```
moneyToLose = min(1000, Money)
```

### 物品丢失概率

```
itemLossRate = 0.22 - luckLevel * 0.04 - dailyLuck
maxLost = 3
// 不可丢失：工具、武器、戒指、靴子
```

### 2AM 晕倒金币

```
moneyToLose = min(1000, Money / 10)
// 在安全位置（农舍内）= 0
```

### 次日体力（2AM 晕倒）

```
// 已在 PlayerStardewData.sleep() 中实现
reduction = (1 - (2600 - min(2600, bedTime)) / 200) * (maxEnergy / 2)
energy -= reduction
if (timeWentToSleep > 2700) energy /= 2
```

### 次日体力（战斗死亡）

```
energy = min(currentEnergy, 2)
```
