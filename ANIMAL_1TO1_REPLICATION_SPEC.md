# 星露谷动物系统 1:1 复刻执行文档（V2 全链路）

> 目标：本文件作为动物系统唯一落地规范，覆盖状态、公式、事件、UI、交互、数据一致性与验收矩阵。
>
> 范围：鸡舍系（白鸡/金鸡/鸭/虚空鸡/兔/鸵鸟/恐龙）优先；后续牛棚系沿同一规则扩展。

---

## 1. 不可变硬约束

1) 购买入口由命令触发，购买主界面像素级复刻原版。  
2) 选择建筑阶段暂用自研 GUI，但逻辑与校验必须等价原版。  
3) 动物不可被杀死，只能售出；必须全局唯一 animalId。  
4) 普通右键=抚摸；Shift+右键=信息面板（像素级复刻）。  
5) 动画资源仅 `idle/walk/eat`，但 AI 与数值规则按原版 1:1。
6) 吃草行为触发时必须播放 `eat` 动作，且吃草时机与数值影响严格按原版。  
7) 产物只允许在动物建筑内部地面产出，采用“单格单产物占位 + 右键拾取”管理模式。

---

## 2. 源码对照清单（实现必须逐条可追溯）

- 核心状态/日更/行为：`源文件/StardewValley/FarmAnimal.cs`
  - `pet`, `dayUpdate`, `OnDayStarted`, `getMoodMessage`
  - `GetProduceID`, `getSellPrice`, `CanLiveIn`, `CanHavePregnancy`
  - `updatePerTenMinutes`, `behaviors`, `Eat`, `SleepIfNecessary`
- 交互入口：`源文件/StardewValley/Game1.cs` + `源文件/StardewValley/GameLocation.cs`
  - `CheckPetAnimal`, `CheckInspectAnimal`
- 信息面板：`源文件/StardewValley.Menus/AnimalQueryMenu.cs`
- 购买菜单：`源文件/StardewValley.Menus/PurchaseAnimalsMenu.cs`
- 孵化与收养：`源文件/StardewValley/AnimalHouse.cs` + `源文件/StardewValley/Object.cs`
  - `addNewHatchedAnimal`, `adoptAnimal`, `OutputIncubator`
- 购买库存与解锁：`源文件/StardewValley/Utility.cs`
  - `getPurchaseAnimalStock`, `_HasBuildingOrUpgrade`
- 繁殖事件：`源文件/StardewValley.Events/QuestionEvent.cs`
- 夜间女巫事件：`源文件/StardewValley.Events/WitchEvent.cs`
- 数据修复：`源文件/StardewValley/Utility.cs` -> `fixAllAnimals`

---

## 3. 状态模型（强制字段 + 取值约束）

## 3.1 AnimalRuntimeState
- `animalId: long`（全局唯一，不复用）
- `animalTypeId: string`
- `displayName: string`
- `buildingId: string`
- `ownerPlayerId: long/UUID`
- `ageDays: int`
- `daysOwned: int`
- `friendship: int`（0..1000）
- `happiness: int`（0..255）
- `fullness: int`（0..255）
- `wasPetToday: bool`
- `wasAutoPetToday: bool`
- `allowReproduction: bool`
- `currentProduceId: string?`
- `produceQuality: int`（0/1/2/4）
- `daysSinceLastProduce: int`
- `moodMessage: int`（0..6）
- `parentId: long`（无父母=-1）
- `health: int`（逻辑保留，但外部伤害无效）

## 3.2 BuildingRuntimeState
- `buildingId`
- `validOccupantTypes`
- `maxOccupants`
- `currentOccupants`
- `animalDoorOpen`
- `allowAnimalPregnancy`
- `animalsThatLiveHere: Set<long>`

---

## 4. 交互链路（右键与 Shift+右键）

原版触发顺序（必须保持）：

1) 点击时先走 `CheckPetAnimal`：只命中 `wasPetToday == false` 的动物，执行 `pet(who)`。  
2) 同帧若是右键，再走 `CheckInspectAnimal`：只命中 `wasPetToday == true` 的动物，再次调用 `pet(who)`，从而打开 `AnimalQueryMenu`。  

`pet(who)` 关键规则：

- 19:00 后且动物静止：提示“正在睡觉”，不抚摸成功。
- 首次抚摸成功：
  - `friendship += 15`（自动抚摸存在减益路径）
  - `happiness += max(5, 30 + happinessDrain)`
  - 播放叫声，玩家畜牧经验 +5
- 若已抚摸且非自动抚摸：打开信息面板（`AnimalQueryMenu`）。
- 金色动物饼干在 `pet()` 内消费与标记（`hasEatenAnimalCracker`）。

本模组映射：

- 普通右键：执行“首次抚摸逻辑”。
- Shift+右键：强制走“检查已抚摸并开信息面板”路径。
- 行为结果与原版保持一致，输入方式按你的约束固定为 Shift 修饰键。

---

## 5. 信息面板复刻范围（AnimalQueryMenu 1:1）

必须包含并复刻以下行为：

1) 名字编辑（与全局重名校验 `areThereAnyOtherAnimalsWithThisName`）。  
2) 爱心显示（按 friendship -> 半心/整心映射）。  
3) 年龄文本（周龄 + baby 后缀）。  
4) 心情文本（`getMoodMessage()`）。  
5) 售出确认二次弹框（Yes/No）。  
6) 搬家模式（高亮可用/不可用建筑，确认后转移 `animalsThatLiveHere`）。  
7) 生育开关按钮（`allowReproduction`，仅成体且可孕类型显示）。

售出行为必须等价：

- 玩家加钱 `+getSellPrice()`
- 从 `animalsThatLiveHere` 移除
- 动物标记删除（原版为 `health = -1`）
- 清理 reserved grass 与临时特效

---

## 6. 日更核心（dayUpdate）完整规则

`dayUpdate(environment)` 执行顺序不得打乱。

## 6.1 前置与回家分支
- 初始化 `daysOwned`（历史存档兼容）。
- `StopAllActions()`，`health = 3`。
- 若有家但不在家：
  - 门开：传回室内；若 >18:00 且未寻路到家，`happiness /= 2`。
  - 门关：`moodMessage = 6`（夜宿户外），`happiness /= 2`。
- 若在家且门关：`happiness += happinessDrain * 2`（上限 255）。

## 6.2 抚摸/喂食惩罚与成长
- `daysSinceLastLay++`
- 未被抚摸且未自动抚摸：
  - `friendship -= (10 - friendship / 200)`（下限 0）
  - `happiness -= 50`（下限 0）
- `wasPetToday=false`, `wasAutoPetToday=false`
- `daysOwned++`
- 若 `fullness < 200` 且在室内，尝试消耗槽中干草 `(O)178` 补满到 255。

成长判定（随机）：

- 若 `fullness > 200` 或 `rand < (fullness - 30) / 170`，则成长 + 心情奖励：
  - 到达成熟前一日则 `growFully()`
  - 否则 `age++`
  - `happiness += happinessDrain * 2`

饥饿惩罚：

- 若 `fullness < 200`：
  - `happiness -= 100`
  - `friendship -= 20`

## 6.3 产物生成与品质公式

速度修正：

- `produceSpeedBonus = 0`
- 若 `friendship >= FriendshipForFasterProduce`，`+1`
- 若主人有 `ProfessionForFasterProduce`，再 `+1`

当天产物判定：

- `produceToday = daysSinceLastLay >= (DaysToProduce - produceSpeedBonus)`
- 且 `rand < fullness / 200`
- 且 `rand < happiness / 70`
- 幼崽永不出产物

普通/豪华产物：

- 基础产物：`whichProduce = GetProduceID(r, deluxe=false)`
- 若 `rand < happiness / 150`，才进入豪华替换和品质计算
- 豪华判定：
  - `deluxeProduce = GetProduceID(r, deluxe=true)`
  - 需 `friendship >= DeluxeProduceMinimumFriendship`
  - 概率：
    - `((friendship + happinessModifier) / DeluxeProduceCareDivisor)`
    - `+ AverageDailyLuck * DeluxeProduceLuckMultiplier`

其中：

- `happinessModifier =`
  - `happiness * 1.5`（happiness > 200）
  - `happiness - 100`（happiness <= 100）
  - `0`（其余区间）

品质概率：

- `chanceForQuality = friendship / 1000 - (1 - happiness / 225)`
- 若主人有 `ProfessionForQualityBoost`，`chanceForQuality += 0.33`
- 铱星：`chance >= 0.95` 且 `rand < chance/2` -> 4
- 金星：`rand < chance/2` -> 2
- 银星：`rand < chance` -> 1
- 否则普通 -> 0

产出位置：

- 若 HarvestType != `DropOvernight` 且 produceToday，则写入 `currentProduce`。
- 否则在建筑室内执行地面产出（本项目关闭自动采集机优先逻辑，统一走地面产出）。
- 吃了金饼干时产量双倍（双份都按地面占位规则落地）。

### 6.3.1 地面产物管理（项目强约束）

- 产物仅可出现在动物建筑内部（`AnimalHouse` 及等价室内）。
- 产物以“地面占位实体”展示（表现类似 `item_display`，非漂浮掉落物）。
- 每个地块最多存在 1 个动物产物占位（`tile -> produceInstance` 一一映射）。
- 若目标地块已占用，按邻近可放置格搜索；搜索失败则该次产出记入延迟重试队列（不覆盖已有产物）。
- 玩家右键占位产物时直接拾取到背包；背包满则给出提示并保持占位不消失。
- 拾取后立即释放该 tile 占位，允许后续新产物生成。
- 品质、产物种类、产量计算仍完全遵循 6.3 原版公式与分支。

## 6.4 日更收尾
- 若非夜宿户外，按 fullness/happiness 设置 `moodMessage`：
  - fullness<30 -> 4（饥饿）
  - happiness<30 -> 3
  - happiness<200 -> 2
  - else -> 1
- `fullness = 0`
- 节日天 `fullness = 250`

---

## 7. 日内逻辑（10分钟 tick + AI 行为）

## 7.1 每10分钟（updatePerTenMinutes）

仅在 18:00 后影响 happiness：

- 室外：
  - 19:00 后/下雨/冬季 -> `-happinessDrain`
  - 否则 -> `+happinessDrain`
- 室内且冬季且当前 happiness>150：
  - 有暖炉 `Heater` -> `+happinessDrain`
  - 无暖炉 -> `-happinessDrain`

## 7.2 进食与觅草

- `fullness < 195` 且室外时，有概率寻路到草。
- `Eat()`：
  - 消耗草地层数（默认 `GrassEatAmount`）
  - 进入进食状态时强制播放 `eat` 动作（仅资源层降级，不改逻辑时长与判定）
  - `fullness = 255`
  - 若非坏心情码（5/6）且非雨天：
    - `happiness = 255`
    - `friendship += 8`（蓝草类型则 +16）

### 7.2.1 吃草动作触发契约

- 触发点：`behaviorAfterFindingGrassPatch -> eatGrass -> Eat()` 全链路命中时。
- 动作开始：切换 `eat`，保持到进食段结束后回到 `idle/walk`。
- 禁止仅改数值不播动作；也禁止提前打断导致“没吃完就回 idle”。
- 进食动作的开始/结束与 `isEating` 状态机保持一致。

## 7.3 行为分支（behaviors）

- 幼崽跟随：幼崽可扫描并跟随同类型成体。
- 17:00 后室外回家：门开可寻路回家，或无玩家时直接切入室内。
- 睡觉：20:00 后强制睡姿、朝向下。
- 特殊挖掘产物（如松露类 DigUp）按概率触发挖掘动画与掉落。

---

## 8. 成长系统

- 幼崽判定：`age < DaysToMature`
- 成体判定：`age >= DaysToMature`
- `growFully()`：
  - 直接将 age 设到成熟
  - 若 `ProduceOnMature == true`，立即给 `currentProduce`
  - `daysSinceLastLay = 99`

---

## 9. 繁殖与夜间事件

## 9.1 动物繁殖（QuestionEvent type=2）

触发候选条件：

- 建筑 `AllowsAnimalPregnancy()` 为真
- 建筑是 `AnimalHouse` 且未满
- 概率与当前动物数相关：`rand < animalCount * 0.0055`
- 选中的母体动物需：
  - 非幼崽
  - `allowReproduction == true`
  - `CanHavePregnancy() == true`

触发后进入命名流程，`AnimalHouse.addNewHatchedAnimal(name)` 在无孵化器时走“生育分支”，创建与母体同类型幼崽并写 `parentId`。

## 9.2 女巫事件（WitchEvent）

- 夜间事件可向目标动物建筑内投放蛋：
  - 普通女巫蛋 `(O)305`
  - 金女巫蛋 `(O)928`
- 本模组需保留“建筑内随机可放置格投蛋”逻辑。

---

## 10. 孵化链路

## 10.1 机器入口（OutputIncubator）

- 输入必须可映射动物数据（`GetAnimalDataFromEgg`）
- 且动物 house 类型必须在建筑 `ValidOccupantTypes` 内
- 孵化时长：`IncubationTime`，缺省 9000 分钟

## 10.2 孵化完成（AnimalHouse）

- `resetSharedState` 检测孵化器 ready 且未满，触发命名事件
- `addNewHatchedAnimal(name)`：
  - 从蛋映射动物类型；失败回落白鸡
  - `adoptAnimal()` 收养并入驻

## 10.3 adoptAnimal 强制副作用

- 加入室内 `animals` map
- `animalsThatLiveHere` 添加 animalId
- 绑定 `homeInterior`
- 设随机站位

---

## 11. 购买系统复刻

## 11.1 可购买库存（Utility.getPurchaseAnimalStock）

- 遍历 `farmAnimalData`
- `PurchasePrice >= 0` 且满足 `UnlockCondition`
- 若缺少 `RequiredBuilding`：条目仍可见，但标记禁购描述（显示灰化与提示）

## 11.2 菜单行为（PurchaseAnimalsMenu）

- 动物条目点击后：
  - 钱足够才进入购买
  - 支持 `AlternatePurchaseTypes` 条件替换动物类型
- 进入建筑选择模式：
  - 仅允许 `CanLiveIn(building)` 且 `!isFull()`
  - 不符合时红字提示
- 命名确认后：`adoptAnimal` -> 扣费 -> 返回商店对话

## 11.3 本项目约束映射

- `stardew animal shop` 打开复刻购买主界面。
- 建筑选择阶段可暂用自研 GUI，但必须保留：
  - 可住类型校验
  - 容量校验
  - 名字重复校验
  - 扣费与创建时序

---

## 12. 数据一致性与修复

必须实现原版 `fixAllAnimals()` 同级能力：

1) 清理空引用动物和重复 ID。  
2) 修复 `home/homeInterior/animalsThatLiveHere` 不一致。  
3) 将无家动物重新安置到可住且未满建筑。  
4) 仍无家则放回农场开放位置并保留记录。  

建议触发点：

- 世界 DayStarted
- 存档加载完成
- 管理员修复命令（`stardew animal repair`）

---

## 13. 无敌约束实现

拦截事件：

- `LivingAttackEvent` -> cancel
- `LivingDamageEvent` -> cancel
- `LivingDeathEvent` -> cancel + 回满血

行为要求：

- 不受武器、爆炸、火焰、窒息、坠落、虚空伤害影响
- 不掉落、不死亡
- 唯一移除通道：售出

---

## 14. 指令规范（stardew animal）

- `stardew animal shop`
- `stardew animal rename <animalId> <name>`
- `stardew animal move <animalId> <buildingId>`
- `stardew animal sell <animalId>`
- `stardew animal list [buildingId]`
- `stardew animal info <animalId>`
- `stardew animal repair`

统一错误码：

- `ANIMAL_NOT_FOUND`
- `BUILDING_NOT_FOUND`
- `BUILDING_FULL`
- `ANIMAL_BUILDING_MISMATCH`
- `NAME_DUPLICATED`
- `ANIMAL_IMMORTAL`

---

## 15. AI 与动画映射

逻辑状态保留原版，动作映射固定：

- 行走/寻路 -> `walk`
- 进食 -> `eat`
- 其余（待机/睡觉/检查/静止）-> `idle`

说明：这是“表现层降级”，不是“行为层降级”。

---

## 16. 1:1 验收矩阵（必须全部通过）

| 维度 | 原版基线 | 本模组通过条件 |
|---|---|---|
| 交互入口 | `CheckPetAnimal/CheckInspectAnimal` | 普通右键抚摸，Shift+右键面板，重复抚摸开面板 |
| 面板功能 | `AnimalQueryMenu` | 改名/爱心/年龄/心情/售出确认/搬家/繁殖开关齐全 |
| 日更公式 | `dayUpdate` | 成长、饱食、心情、好感、产物、品质分支顺序一致 |
| 日内更新 | `updatePerTenMinutes` + `behaviors` | 18:00后心情变化、觅草、回家、睡眠时段一致 |
| 吃草表现 | `eatGrass/Eat` | 触发吃草时必播 `eat`，动作生命周期与 `isEating` 一致 |
| 产物系统 | `GetProduceID` + 品质判定 | 普通/豪华/品质/双产公式一致，且仅室内地面单格占位产出 |
| 孵化系统 | `OutputIncubator` + `addNewHatchedAnimal` | 蛋校验、孵化时长、命名后入驻一致 |
| 购买系统 | `getPurchaseAnimalStock` + `PurchaseAnimalsMenu` | 解锁、禁购提示、命名、选建筑、扣费时序一致 |
| 繁殖事件 | `QuestionEvent(2)` | 条件、概率、母体限制、命名分支一致 |
| 夜间事件 | `WitchEvent` | 女巫投蛋逻辑可触发并落地 |
| 一致性修复 | `fixAllAnimals` | 可修复孤儿动物、重复ID、错误归属 |
| 生存规则 | 无强制无敌（原版） | 按项目约束：绝对无敌，仅售出可移除 |

---

## 17. 实施顺序（严格）

阶段 A：交互与面板  
1) 右键/Shift+右键入口  
2) AnimalQuery 面板复刻（含售出/搬家/繁殖开关）

阶段 B：日更与 AI  
1) 完整迁移 dayUpdate/onDayStarted/updatePerTenMinutes  
2) behaviors + eat + 睡眠 + 回家

阶段 C：购买与孵化  
1) shop 主界面复刻 + 命令入口  
2) 选建筑 GUI 对接  
3) incubator 全链路

阶段 D：事件与修复  
1) QuestionEvent 生育链  
2) WitchEvent 投蛋  
3) fixAllAnimals 等价修复命令

阶段 E：无敌与收尾  
1) 全伤害拦截  
2) 回归测试 + 存档一致性验证
