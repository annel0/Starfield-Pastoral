# Stardew Valley 原版矿井相关源码索引（仅定位/导航，不复制源码）

> 说明：我可以帮你把“矿井相关逻辑”在本地源码中的**位置全部标出来**，但由于版权原因，我不能把原版源码大段“扒出来/复制成新文件”。
> 你可以直接点击下面的链接在 VS Code 里打开对应文件与行号。

## 1) 核心：矿井楼层/刷怪/掉落/梯子

- 核心类：MineShaft（几乎所有：楼层生成、主题区段、怪物选择、石头掉落、梯子/竖井、特殊房间）
  - 文件：[源代码/StardewValley.Locations/MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs)
  - 关键常量区（区段/底层/概率常量）：[MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L34-L80)
  - 梯子判定与创建入口（createLadderAt/递归找位）：[MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L1965-L2088)
  - 竖井/下楼梯的网络事件与落点创建（createLadderDown/doCreateLadderDown）：[MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L3587-L3638)
  - “敲石头后是否刷出梯子”的核心概率公式（chanceForLadderDown）：[MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L3614-L3636)
  - “敲石头出东西/矿/宝石”的主入口（checkStoneForItems + breakStone 调用点）：[MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L3614-L3642)
  - 当前楼层主题分段（getMineArea）：[MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L3787)
  - 是否黑暗层（isDarkArea）：[MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L2601)
  - 是否需要清怪才能下楼（mustKillAllMonstersToAdvance）：[MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L1956)
  - 楼层“自然生成分布参数”调节（adjustLevelChances）：[MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L1231)
  - 楼层矿脉/矿石块放置：
    - getAppropriateOre： [MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L1733)
    - tryToAddOreClumps： [MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L1810)
  - 楼层随机散落物（石头/紫石/神秘石/宝石石等）生成：createLitterObject：
    - [MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L4351)
  - 楼层随机掉落物（箱子/木桶/矿车/散物等）的“物品选择”：getRandomItemForThisLevel：
    - [MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L3879)
  - 怪物选择主函数：getMonsterForThisLevel：
    - [MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L4029)

## 2) 矿洞入口地点（山洞入口那张图所在的 Location）

- Mine（入口/日更清理等）：[源代码/StardewValley.Locations/Mine.cs](源代码/StardewValley.Locations/Mine.cs)

## 3) 电梯（UI、交互、玩家状态）

- 电梯菜单 UI：
  - [源代码/StardewValley.Menus/MineElevatorMenu.cs](源代码/StardewValley.Menus/MineElevatorMenu.cs)
- 玩家是否“乘电梯中”状态：
  - 字段 ridingMineElevator：[源代码/StardewValley/Farmer.cs](源代码/StardewValley/Farmer.cs#L529)
- 触发 MineElevator 的地图 Action 分发：
  - [源代码/StardewValley/GameLocation.cs](源代码/StardewValley/GameLocation.cs#L9796-L9804)
- 从 MineShaft 打开电梯菜单的逻辑点（activeClickableMenu = new MineElevatorMenu）：
  - [MineShaft.cs](源代码/StardewValley.Locations/MineShaft.cs#L3069)

## 4) 进入矿洞/切层/当前层数 API

- 当前矿层（CurrentMineLevel）：[源代码/StardewValley/Game1.cs](源代码/StardewValley/Game1.cs#L1899)
- 进入指定矿层（enterMine）：[源代码/StardewValley/Game1.cs](源代码/StardewValley/Game1.cs#L10569)

## 5) 订单/任务对矿层的监听

- 到达矿层目标（含 SkullCave 变体）：
  - [源代码/StardewValley.SpecialOrders.Objectives/ReachMineFloorObjective.cs](源代码/StardewValley.SpecialOrders.Objectives/ReachMineFloorObjective.cs)

## 6) 矿井可破坏容器（木桶/箱子等）

- BreakableContainer（注释里明确写了用于 mines / Skull Cavern）：
  - [源代码/StardewValley.Objects/BreakableContainer.cs](源代码/StardewValley.Objects/BreakableContainer.cs)

## 7) SkullCave（骷髅洞穴）与“矿井系统”的关联点

> 原版里 SkullCave 与普通矿洞共享大量 MineShaft 逻辑，只是 location 名称/层数/难度与部分生成规则不同。

- SkullCave 相关传送/队伍逻辑：
  - [源代码/StardewValley/FarmerTeam.cs](源代码/StardewValley/FarmerTeam.cs#L508)
  - “把 >=121 视为 SkullCave 的生成层”判断点：
    - [源代码/StardewValley/FarmerTeam.cs](源代码/StardewValley/FarmerTeam.cs#L1193)
- 地图覆盖/Action 中对 SkullCave 的处理：
  - [源代码/StardewValley/GameLocation.cs](源代码/StardewValley/GameLocation.cs#L1056)

## 8) 怪物实现（MineShaft 负责挑选，这里是具体怪物行为）

- 怪物目录（可按你要“映射到我们自己的怪物”逐个对照）：
  - [源代码/StardewValley.Monsters](源代码/StardewValley.Monsters)

## 9) 其它：火山地牢（如果你后续也想对齐/参考）

- VolcanoDungeon：
  - [源代码/StardewValley.Locations/VolcanoDungeon.cs](源代码/StardewValley.Locations/VolcanoDungeon.cs)

---

## 建议你接下来怎么“扒逻辑”而不是“扒代码”

- 先从 MineShaft.cs 抽 4 条主线：
  1) 楼层生成（地图/布局/填充/主题）
  2) 石头/矿物/宝石/容器的生成与掉落
  3) 梯子/竖井的生成概率与触发点
  4) 刷怪：按主题/黑暗层/特殊层的怪物挑选
- 你告诉我：你要对齐的是“普通矿洞(1-120)”还是也包括“SkullCave(121+)”。我就按这份索引继续把**你关心的公式/表**逐个标记出来（仍然不复制源码，只给位置与解释）。
