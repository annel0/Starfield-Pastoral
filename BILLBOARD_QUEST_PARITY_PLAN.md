# 公告栏 / 每日任务 / 日历 — SDV Parity 规划

## 0. 背景

现状：quest 骨架完整（15 个 Java 文件，~2900 行），9 种任务类型都能跑，事件钩子覆盖完整，daily quest 能生成能完成。但**生成节奏、领奖流程、GUI 像素、日历内容**都和 SDV 原版偏离，玩家体验"像但不是"。

目标：在不重写现有架构的前提下，分多个可独立落地的阶段，把行为和观感贴回 SDV 原版。

参考源码：
- [Billboard.cs](源文件/StardewValley.Menus/Billboard.cs) — 公告栏菜单（日历 + 每日任务）
- [Utility.cs:3195](源文件/StardewValley/Utility.cs#L3195) `getQuestOfTheDay()` — 生成概率表
- [Town.cs:1080-1090](源文件/StardewValley.Locations/Town.cs#L1080-L1090) — 世界内 "!/?" 提示
- [Quest.cs](源文件/StardewValley.Quests/Quest.cs) + 各 `*Quest.cs`

---

## 1. 优先级总览

| 阶段 | 任务 | 影响 | 工作量 | 依赖 |
|---|---|---|---|---|
| **P0** | 每日任务生成概率对齐 SDV | 节奏感 | S | 无 |
| **P0** | `daysLeft` tick + 过期销毁 | 功能正确性 | S | 无 |
| **P0** | 世界内 "!" 感叹号提示 | 信息可发现性 | S | 无 |
| **P1** | 统一领奖流程 | UX 一致性 | M | 需要决定策略 |
| **P1** | 任务文本全面本地化 | 多语言体验 | M | 无 |
| **P1** | NPC target 固化（非随机） | SDV 忠实度 | S | 需要 NPC↔物品映射 |
| **P2** | GUI 像素级对齐 SDV | 视觉还原度 | M | 已有 billboard.png 纹理 |
| **P2** | 日历加节日/婚礼/书商 | 功能补齐 | L | 依赖节日系统（暂不存在） |
| **P3** | Weeding / LostItem 任务类型 | 1.6 完整性 | M | 非必需 |

S=半天，M=1-2天，L=3天+

---

## 2. P0.1 — 每日任务生成概率对齐

### 现状问题
[DailyQuestGenerator.java:89-108](src/main/java/com/stardew/craft/quest/DailyQuestGenerator.java#L89-L108) 用 25/25/25/25 四类均分，每天都有任务，无前置条件。

### SDV 原版规则（[Utility.cs:3195](源文件/StardewValley/Utility.cs#L3195)）

```
gameDay ≤ 1                             → null（首日无任务）
d < 0.08                                → ResourceCollection
d < 0.20 AND 已解锁矿井 AND 天数 > 5    → SlayMonster
d < 0.50                                → null（不给任务）
d < 0.60                                → Fishing
d < 0.66 AND 今天周一 AND 此存档没做过  → Socialize
d < 0.66 AND 否则                       → ItemDelivery
else                                    → ItemDelivery
```

约累计 70% 天数会有任务。

### 实施

1. 重写 `DailyQuestGenerator.generate(gameDay, worldSeed, playerContext)` 增加玩家上下文入参
2. 需要的上下文：`lowestMineFloorReached`、`daysPlayed`、`dayOfWeek`、`hasDoneSocializeQuest`
3. `lowestMineFloorReached` 从 `MiningPlayerData.getLowestFloorReached()` 读
4. `dayOfWeek` 从 `StardewTimeManager` 读（season*28 + day 对 7 取模）
5. 保留现有子生成函数（`generateFishingQuest` 等），只改顶层分派
6. 如果返回 `null`，`QuestManager.onDayStarted` 里 `dailyQuest = null` 不要强制跑满

---

## 3. P0.2 — `daysLeft` tick + 过期销毁

### 现状问题
`StardewQuest.daysLeft` 字段存在，但没找到哪里递减。玩家接了任务永远不过期，或者第二天自动完成消失但没声音。

### SDV 原版
- 接受时 `daysLeft.Value = 2`
- 每天早上（新一天）：`daysLeft--`
- `daysLeft == 0` 且未完成 → `destroy = true`，从 questLog 移除
- 已完成的不受此影响

### 实施

1. `QuestManager.onDayStarted(ServerPlayer, int gameDay)` 顶部加循环：
   ```java
   Iterator<StardewQuest> it = questLog.iterator();
   while (it.hasNext()) {
       StardewQuest q = it.next();
       if (!q.isDailyQuest() || q.isCompleted()) continue;
       if (q.getDayQuestAccepted() == gameDay) continue;  // 接受当天不扣
       q.setDaysLeft(q.getDaysLeft() - 1);
       if (q.getDaysLeft() <= 0) {
           q.setDestroy(true);
           it.remove();
           // 发出"任务已过期"的 actionbar 提示？
       }
   }
   ```
2. `QuestManager.cleanupDestroyed()` 已经在做类似清理，复用它即可

---

## 4. P0.3 — 世界内 "!" 感叹号提示

### SDV 原版（[Town.cs:1086-1090](源文件/StardewValley.Locations/Town.cs#L1086-L1090)）

- 条件：`CanAcceptDailyQuest()` = daily quest 生成且未 accept 且未 complete
- 精灵：`mouseCursors[395,497,3,8]`（3×8 像素 "!"）
- 位置：浮在公告栏顶上方
- 动画：`yOffset = 4 * sin(ms / 250)` 上下漂浮
- 额外脉动缩放：`scale = 4 + max(0, 0.25 - yOffset/16)`

### 我们的实施方案（推荐方案 C）

**方案 C：客户端 RenderLevelStageEvent 扫描 BulletinBoardBlock**

复用 [PortalHintRenderer](src/main/java/com/stardew/craft/client/render/PortalHintRenderer.java) 的架构：

1. 新建 `BillboardQuestIndicatorRenderer` @EventBusSubscriber CLIENT
2. `onRenderLevel(RenderLevelStageEvent)` Stage=AFTER_ENTITIES：
   - 只在 STARDEW_VALLEY / OVERWORLD 维度跑
   - 读客户端玩家位置周围 16 格半径的方块，找 `ModBlocks.BULLETIN_BOARD`
   - 检查 `ClientQuestData.hasUnclaimedDailyQuest()` — 若 false 直接 return
   - 对每个找到的 bulletin board：在方块顶上 1.2 格处 billboard-style 渲染 "!"
3. "!" 可以用：
   - **方案 C-1**：Component 文字画"!"（最快，但字体不太像 SDV 的像素风）
   - **方案 C-2**：创建一张 `textures/gui/quest_indicator.png`（一个 "!" 像素图，白黄色），用 `GuiGraphics.blit` 渲染
   - **方案 C-3**：两个字符叠加（白底+黄字），模拟描边
   - 推荐 C-2，纹理路径：`stardewcraft:textures/gui/quest_indicator.png`，3×8 像素
4. 正弦漂浮：`yOffset = 0.08 * sin(time * 0.004)`
5. 缩放脉动：`scale = 0.15 * (1 + max(0, 0.25 - yOffset/0.32))`

### 相关客户端 API 需要扩展
`ClientQuestData.hasUnclaimedDailyQuest()`：
```java
public static boolean hasUnclaimedDailyQuest() {
    StardewQuest q = getDailyQuest();
    return q != null && !q.isAccepted() && !q.isCompleted();
}
```

---

## 5. P1.1 — 统一领奖流程

### 现状问题
- ItemDelivery：自动领（给物品时 intercept）
- Fishing / SlayMonster / Resource：两阶段，完成后聊 NPC 自动领
- 但 `ClaimRewardPayload` 还存在 → GUI 可能有"领取"按钮，和 NPC 聊天领奖并存

### 决策点
两选一：

**方案 A：走原版 NPC 对话领奖**
- 删掉 `ClaimRewardPayload` 和 GUI 里的"领取奖励"按钮
- 三类任务统一走 `onNpcSocialized` → 自动转账 + 销毁
- ItemDelivery 已经是这个模式
- **优点**：最贴 SDV 原版
- **缺点**：我们的 NPC 对话系统简化过，没有 SDV 那种完整 targetMessage 返回对话；需要先补上"交付后 NPC 说这句话"的流程

**方案 B：走按钮领奖**
- 保留 `ClaimRewardPayload`
- 三类任务完成后**不**在 `onNpcSocialized` 里自动转账，只标记 `completed = true`
- GUI 里显示"领取奖励"按钮，点 → 发包 → 服务端转账销毁
- **优点**：简单，和我们的 ItemDelivery 分开处理（ItemDelivery 因为"给物品"本身就是确认动作，可继续自动）
- **缺点**：和 SDV 略偏（SDV 无按钮领奖）

**推荐 B**，因为：
- 我们 NPC 对话系统不支持"任务返回对话"；补这个要单独做 dialogue branching，工作量大
- 玩家已经习惯了 MC 的 GUI 按钮语义
- "领取"按钮可以显示奖励金额，体验反而更清晰

### 实施（按方案 B）
1. 修改 FishingQuest / SlayMonster / Resource 的 `onNpcSocialized`：只做 `questComplete()`，**不**做 `player.addMoney()` 或 `destroy = true`
2. `BillboardScreen` / `QuestLogScreen` 对 `completed && !destroy && moneyReward > 0` 的 quest 画领取按钮
3. `ClaimRewardPayload` 保持不变
4. 删除 `MoneyTransferredOnSocialize` 类似的字段

---

## 6. P1.2 — 任务文本本地化

### 现状问题
[DailyQuestGenerator.java](src/main/java/com/stardew/craft/quest/DailyQuestGenerator.java) 里：
```java
q.setTitle(npcName + "'s Request");
q.setDescription(npcName + " needs a " + itemName + ". Can you bring one?");
q.setObjectiveText("Bring " + itemName + " to " + npcName);
```
硬编码英文 + `_` 分隔转空格的物品名。中文环境下 "stardewcraft:carp" → "carp" 裸露。

### 实施

1. 新翻译键（`zh_cn.json` / `en_us.json`）：
   ```
   stardewcraft.quest.delivery.title      = %1$s 的请求 / %1$s's Request
   stardewcraft.quest.delivery.desc       = %1$s 想要 %2$s，你能帮忙带一个吗？
   stardewcraft.quest.delivery.objective  = 把 %2$s 送给 %1$s
   stardewcraft.quest.fishing.title       = 威利的钓鱼请求
   stardewcraft.quest.fishing.desc        = 威利需要 %1$d 条 %2$s 交给一位顾客。
   stardewcraft.quest.fishing.objective   = 钓 %1$d 条 %2$s（%3$d/%1$d）
   stardewcraft.quest.resource.title      = 资源收集
   stardewcraft.quest.resource.desc       = %1$s 需要 %2$d 块 %3$s 锻造新工具。
   stardewcraft.quest.resource.objective  = 收集 %2$d 块 %3$s（%4$d/%2$d）
   stardewcraft.quest.monster.title       = 怪物清剿
   stardewcraft.quest.monster.desc        = 冒险家公会想请人清剿 %1$d 只 %2$s。
   stardewcraft.quest.monster.objective   = 击败 %1$d 只 %2$s（%3$d/%1$d）
   ```

2. `StardewQuest.title/description/objectiveText` 字段类型从 `String` 改为 `Component`（或加平行的 `Component titleComponent`）

3. `DailyQuestGenerator` 构造时用 `Component.translatable(key, args)`；物品名传 `new ItemStack(item).getHoverName()` 让客户端端翻译

4. 进度动态文本（如 "5/10"）需要在 render 时重新构造，而不是存一份固定的 objectiveText。让 `*Quest.java` 暴露一个 `Component getCurrentObjective()` 方法，每次调用根据 current progress 生成

5. NBT 序列化 Component：用 `Component.Serializer.toJson()` / `fromJson()`

### 影响范围
- `StardewQuest` 基类字段类型
- 所有 9 个子类的 objective 生成
- `QuestLogSyncPayload` 序列化
- `ClientQuestData` 反序列化
- `BillboardScreen` + `QuestLogScreen` 渲染

工作量中等，因为要改基础 schema。可分两步：先新增 `Component` 字段并行存在，再逐步迁移。

---

## 7. P1.3 — NPC target 固化

### 现状问题
[DailyQuestGenerator.java:115](src/main/java/com/stardew/craft/quest/DailyQuestGenerator.java#L115) 随机选 21 个 NPC 之一送货；FishingQuest 硬编码 Willy；ResourceCollection 硬编码 Clint；SlayMonster 硬编码 Lewis。

SDV 原版送货 NPC 根据物品类型分配（如水果 → Emily、金属 → Clint、作物 → Pierre/Marnie），钓鱼任务的 NPC 按季节变（春/秋 Willy、夏/冬 可能是别人）。

### 实施

1. 新建 `DailyQuestNpcMapping.java`（或数据文件 JSON）：
   ```java
   // 物品 → NPC（送货）
   static final Map<String, String> DELIVERY_NPC_BY_ITEM = Map.of(
     "stardewcraft:parsnip", "pierre",
     "stardewcraft:melon", "emily",
     "stardewcraft:copper_bar", "clint",
     ...
   );
   // 怪物 → NPC（讨伐）
   static final Map<String, String> SLAY_NPC_BY_MONSTER = Map.of(
     "sd_mob_slime", "lewis",
     "sd_mob_bat", "marlon",  // Adventurer's Guild
     "sd_mob_crab", "lewis",
     ...
   );
   ```
2. 也可以直接放 JSON 数据文件 `data/stardewcraft/quest/daily_npc_mapping.json`，加载到 `QuestDataLoader`
3. 保留一个 fallback（`clint`）以防数据漏

---

## 8. P2.1 — GUI 像素级对齐

### 现状问题
- 原版没有 Tab 切换（日历/每日任务是两个独立方块打开），我们把它们放在一个界面切 Tab
- 原版 Billboard 纹理是 `338×396`（两个面板上下叠放），我们的 `billboard.png` 可能 UV 对上了但排版差异
- 今日日期没画蓝框
- 过去的日子没盖 25% 灰
- Accept 按钮没有 hover 动画
- 每 3 个任务奖励一个礼盒的计数没画

### 决策点
**要不要拆成两个独立菜单（日历 + 任务）？**

- **拆开**：更贴原版，视觉上 Tab 消失
- **保留 Tab**：玩家更习惯，一个交互点看全部
- **推荐保留 Tab**，因为我们只有一个公告栏方块，不想造两个物件。但 Tab 美化成原版风格的两个纸张堆叠感。

### 实施
1. 今日蓝框：用 `mouseCursors[379,357,3,3]` 9-slice 绘制，颜色 `Color.Blue`
2. 过去灰盖：`g.fill(x, y, x+w, y+h, 0x40808080)` — 25% alpha 灰
3. Accept 按钮 hover：`scale = hover ? 1.5 : 1.0`，配 `Cowboy_gunshot` 音效（如有）
4. 礼盒计数：`Game1.stats.Get("BillboardQuestsDone") % 3` 画 0-2 颗星星（`billboard.png[140,397,10,11]`）

---

## 9. P2.2 — 日历事件扩展

### 现状
[Billboard.cs:17-34](源文件/StardewValley.Menus/Billboard.cs#L17-L34) 定义了 6 种日历事件类型：Birthday / Festival / PassiveFestival / FishingDerby / Wedding / Bookseller。

我们只做了 Birthday。

### 做不做？
- **Festival / PassiveFestival** — 依赖节日系统（我们暂未实现节日事件），**暂不做**
- **FishingDerby** — 夏 20-21 / 冬 12-13 日硬编码，可先画占位图标
- **Wedding** — 依赖结婚系统（未实现），**暂不做**
- **Bookseller** — 单独的物品流通系统（未实现），**暂不做**

建议 P2 阶段**只加 FishingDerby 的占位图标**，其他等对应子系统做完再补。

---

## 10. P3 — Weeding / LostItem 任务

SDV 1.6 新增。定义好但未实现。优先级最低，等主流程贴齐了再补。

---

## 11. 建议执行顺序

**Sprint 1（半天 - 1 天）**: P0 全做完
- 每日任务概率表重写
- daysLeft tick
- 世界内 "!" 提示（3×8 纹理手画一张）

完成后玩家感知的"节奏不对"问题会消失。

**Sprint 2（1-2 天）**: P1.1 + P1.2
- 决定领奖流程（建议方案 B，按钮领）
- 任务文本全面本地化

完成后中文玩家体验闭环。

**Sprint 3（1 天）**: P1.3 + P2.1
- NPC 映射表
- GUI 像素对齐

Sprint 4+ 按需排。

---

## 12. 验证 checklist

每个 Sprint 结束后人工测：

- [ ] 第 1 天（春 1）无任务 — P0.1
- [ ] 没进过矿井的前几天没有怪物任务 — P0.1
- [ ] 存档里一半天数没任务 — P0.1（概率命中"null"分支）
- [ ] 周一有 Socialize 任务（首次）— P0.1
- [ ] 接受任务后第 3 天自动从 log 里消失 — P0.2
- [ ] 公告栏有任务时头顶浮"!"感叹号，接受后消失 — P0.3
- [ ] 任务接受后，标题/描述/进度都是中文 — P1.2
- [ ] 送某个物品的任务总是同一个 NPC（重启存档依然固定）— P1.3
- [ ] GUI 里今天那格有蓝框 — P2.1
- [ ] 过去的日期灰蒙蒙 — P2.1

---

*更新时间：2026-04-18*
