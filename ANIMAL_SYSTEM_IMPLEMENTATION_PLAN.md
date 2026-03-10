# 动物系统实现总设计（严格对齐 Stardew Valley 原版）

> 目标：在当前 NeoForge 1.21.1 工程中实现“农场动物大模块”，行为逻辑以原版源码为唯一标准，保证可验证的一致性。
>
> 约束：本方案只参考两类内容——
> 1) 当前模组现有代码；
> 2) 源文件目录中的 Stardew Valley 原版源码与数据。
>
> 不使用、不依赖任何数据包方案。

---

## 0. 总原则（必须遵守）

### 0.1 一致性原则
- 动物行为规则以原版源码为准，不以 Wiki 文案或体感为准。
- Wiki 仅用于术语说明与玩家向解释，不作为最终判定依据。
- 所有“概率、阈值、优先级、分支顺序”都要可追溯到原版代码位置。

### 0.2 工程原则
- 优先复用当前工程已有模式：SavedData 管理器、命令调试、时间系统日更入口、DeferredRegister 注册方式。
- 先实现逻辑正确，再接入完整美术资源与交互 UI。
- 任何“临时替代”（例如玛尼购买先用指令）必须设计成可平滑替换，不改核心域模型。

### 0.3 范围原则
- 本阶段只做“农场动物（鸡舍/畜棚）”，不包含宠物系统。
- 产物机器（蛋黄酱机、奶酪机等）只保留对接点，不在本文展开实现细节。

---

## 1. 原版行为基线（必须 1:1 对齐）

以下是后续实现必须覆盖的主链：

1) 获取动物：
- 购买（玛尼）
- 怀孕（过夜概率触发）
- 孵化（鸡舍孵化器）

2) 绑定建筑：
- 每只动物必须绑定一个合法建筑（鸡舍或畜棚）
- 建筑有容量上限
- 建筑门状态影响日常行为

3) 日常循环：
- 新一天时更新：喂食、心情、好感、产物生成、成长
- 采收方式受动物类型限制（手取 / 工具取）

4) 事件链：
- 夜间怀孕事件
- 夜间袭击事件（门与在外动物相关）
- 女巫事件（特殊蛋注入）

5) 管理交互：
- 命名
- 搬家
- 出售
- 繁殖开关

---

## 2. 落地到 MC 的总体架构

## 2.1 子系统拆分

建议新增以下包（命名风格与现有工程一致）：

- com.stardew.craft.animal.data
  - AnimalDataManager（加载并缓存动物配置，映射原版 Data/FarmAnimals）
  - AnimalTypeConfig / ProduceRule / PregnancyRule 等 DTO

- com.stardew.craft.animal.model
  - FarmAnimalInstance（单只动物运行时状态）
  - AnimalBuildingBinding（动物-建筑绑定关系）
  - AnimalMoodState / AnimalProduceState

- com.stardew.craft.animal.building
  - AnimalBuildingManager（建筑 ID、命名、容量、成员、门状态）
  - CoopBuildingSpec / BarnBuildingSpec（分级规则）
  - AnimalBuildingValidator（管理器范围内结构校验）

- com.stardew.craft.animal.acquire
  - AnimalPurchaseService（先命令入口，后接 UI/NPC）
  - AnimalPregnancyService（过夜怀孕判定）
  - AnimalIncubationService（孵化流程）

- com.stardew.craft.animal.daily
  - AnimalDailyUpdateService（日更主入口）
  - AnimalFeedingService
  - AnimalProduceService
  - AnimalOutdoorBehaviorService（可选：日出外出/日落回家）

- com.stardew.craft.animal.event
  - AnimalNightEventService（夜袭/女巫/怀孕调度）

- com.stardew.craft.animal.command
  - AnimalDebugCommand
  - AnimalPurchaseCommand（替代玛尼购买）

---

## 2.2 持久化方案（对齐当前工程 SavedData 风格）

新增 SavedData：

- AnimalWorldData（维度级）
  - Map<AnimalId, FarmAnimalInstance>
  - Map<BuildingId, AnimalBuildingRecord>
  - nextAnimalId
  - nextBuildingId

数据主键约束：
- AnimalId：全局唯一（long）
- BuildingId：全局唯一（string 或 long + 前缀）
- Animal 必须且仅能绑定 1 个 BuildingId

与现有系统对接：
- 在 StardewTimeManager.advanceDay() 的每日流程中插入动物日更入口。
- 与天气系统共享当天天气上下文（雨雪影响室外觅食逻辑）。

---

## 3. 建筑系统方案（按你的“管理器 + 范围校验”思路）

## 3.1 核心概念

你的思路非常可行：
- 玩家先获得“建筑管理器方块”（例如 Coop Manager、Barn Manager）。
- 放置后在指定范围内按规则搭建结构。
- 系统扫描并验证结构满足对应等级规则后，注册为一个动物建筑实例。

这等价于把 SV 的“罗宾建造”替换为 MC 场景下的“结构达标注册”，但动物核心逻辑保持不变。

## 3.2 建筑分级规则（建议）

你提出的规则方向是对的，建议做成“可配置规范”，不要硬编码在逻辑里：

- Coop Tier 1
  - 必需：管理器、围栏数量下限、有效室内空间下限
  - 容量：对应原版一级鸡舍

- Coop Tier 2
  - 在 Tier 1 基础上新增：自动投喂槽位数量下限
  - 容量：对应原版二级鸡舍

- Coop Tier 3
  - 在 Tier 2 基础上新增：孵化器数量下限
  - 容量：对应原版三级鸡舍

- Barn Tier 1/2/3
  - 同理，按畜棚分级规则定义结构要件与容量。

注意：
- “结构要求”是 MC 适配层；
- “容量、可养动物类型、可怀孕、自动喂养能力”必须按原版等级行为对齐。

## 3.3 建筑注册流程

1) 放置管理器方块
2) 玩家触发“校验并注册”
3) AnimalBuildingValidator 扫描范围并判定等级
4) 生成 BuildingId，允许玩家自定义名称
5) 写入 AnimalWorldData

失败要有明确错误原因：
- 缺少必要构件
- 容量不足
- 孵化器/喂食槽条件不满足
- 与其它建筑范围冲突

---

## 4. 动物获取路径（严格限制来源）

你要求“动物只能通过原版路径获得”，本方案完全遵守：

## 4.1 购买（玛尼）

阶段 1（临时）：
- 使用指令模拟购买流程，不直接刷生物。
- 命令执行必须经过完整校验链：
  - 动物类型是否解锁
  - 玩家货币是否足够
  - 是否存在可容纳且合法的目标建筑
  - 建筑类型是否匹配（鸡舍/畜棚）

阶段 2（正式）：
- 将 AnimalPurchaseService 接入 NPC/UI（玛尼商店交互）。
- 仅替换入口层，不改服务层逻辑。

## 4.2 过夜怀孕

- 在“夜间事件阶段”执行概率判定，条件与原版一致：
  - 建筑允许怀孕
  - 动物类型可怀孕
  - 建筑未满
  - 触发概率与分支顺序对齐原版
- 触发后进入“次日出生/确认”流程。

## 4.3 孵化

- 仅在满足孵化器条件的建筑中允许。
- 按蛋 ID 映射动物类型，映射关系与原版数据一致。
- 孵化完成后必须绑定到对应建筑。

---

## 5. 动物与建筑绑定规则（强约束）

每只动物状态机必须包含：
- animalId
- animalTypeId（对应原版 Data/FarmAnimals key）
- buildingId（不可空）
- displayName
- age / friendship / happiness
- currentProduce / produceQuality
- flags（是否喂食、是否抚摸、是否可产出、是否怀孕开关等）

强约束：
- 创建动物时立即绑定建筑。
- 建筑删除前必须迁出或处理所有动物。
- 迁居必须经过容量与类型校验。

---

## 6. 日常行为实现（按优先级拆阶段）

## 6.1 必做（第一阶段）

- 日更：成长、喂食状态、好感/心情变化、产物生成
- 采收：
  - 工具采收类型（挤奶桶、剪刀）
  - 非工具直接拾取类型
- 建筑容量与门状态同步

## 6.2 可选（第二阶段）

- 日出外出吃草、日落回家

说明：
- 这部分在 MC 路径与导航上实现成本较高，可以先做“逻辑等价”再做“表现等价”。
- 即第一阶段先确保吃草/喂食结果与原版一致，第二阶段补齐可视行为轨迹。

---

## 7. GeckoLib 动物模型接入规范

你已经完成建模动画，这是极大优势。当前工程有 GeckoLib 依赖，但还没有动物实体接入层，建议按以下规范提供资源：

## 7.1 你需要提供的资产清单（每个动物）

- 几何模型文件（geo）
- 动画文件（animation）
- 贴图文件（texture）
- 命名规范文档（建议统一前缀）
- 动画状态说明（idle/walk/eat/sleep/harvest/happy 等）

## 7.2 代码侧接入骨架

- 实体类：继承当前项目实体体系，挂接 GeckoLib animatable
- 模型类：按动物类型映射资源路径
- 渲染器：注册到客户端事件总线
- 动画控制器：由 FarmAnimalInstance 状态驱动（不是由随机动画驱动）

## 7.3 状态驱动原则

- 动画必须由逻辑状态触发：
  - 吃草时播放 eat
  - 回家路径播放 walk
  - 产物可采收时可叠加提示动画
- 不能反向由动画决定逻辑（避免不同步）。

---

## 8. 与现有工程的具体对接点

## 8.1 每日推进入口

- 在 StardewTimeManager.advanceDay() 内新增：
  - AnimalDailyUpdateService.runDaily(stardewLevel, dateContext)
  - AnimalNightEventService.resolveNight(stardewLevel, dateContext)

## 8.2 命令注册

- 参考现有 CommandEventHandler 注册方式新增：
  - AnimalPurchaseCommand（临时玛尼入口）
  - AnimalDebugCommand（状态查看、修复、迁居、重算）

## 8.3 方块/物品注册

- 新增管理器方块与其物品：
  - coop_manager_t1 / t2 / t3
  - barn_manager_t1 / t2 / t3
- 可选新增：
  - feeder_slot（结构校验构件）
  - incubator_block（结构校验构件）

---

## 9. 一致性校验机制（保证“不跑偏”）

必须建立“原版对照测试清单”，每条行为可验证。

## 9.1 校验维度

- 获取一致性：购买/怀孕/孵化只能走合法路径
- 建筑一致性：容量、等级、类型限制一致
- 数值一致性：
  - 好感/心情增减
  - 产物节奏
  - 品质计算
  - 怀孕概率
- 事件一致性：夜袭、女巫、怀孕事件分支顺序一致

## 9.2 推荐测试方式

- 固定随机种子 + 固定日期 + 固定天气 + 固定玩家状态
- 与原版同条件跑 N 天，比较日志快照：
  - 每天动物状态
  - 产物数量与品质
  - 事件触发结果

## 9.3 偏差处理机制

- 一旦发现偏差，先定位“数据差异 vs 逻辑差异”。
- 禁止“凭感觉调数值”，必须找到对应原版分支并修正。

---

## 10. 分阶段开发计划（建议执行顺序）

## Phase A：领域模型与持久化（基础）
- 建立 AnimalWorldData、FarmAnimalInstance、AnimalBuildingRecord
- 完成 BuildingId/AnimalId 生成与存取

## Phase B：建筑管理器系统
- 管理器方块 + 范围扫描 + 分级验证 + 建筑注册/命名
- 完成建筑-动物绑定约束

## Phase C：获取链（先指令）
- 完成购买指令服务链（含校验、扣费、命名、绑定）
- 完成孵化与怀孕事件最小闭环

## Phase D：日更与产物
- 完成喂食、心情、好感、产物生成、采收工具分流

## Phase E：夜间事件与一致性回归
- 完成夜袭/女巫/怀孕优先级与概率链
- 完成对照测试与偏差修正

## Phase F：表现层增强
- GeckoLib 动物实体接入
- 可选：日出外出与日落回家可视行为
- 购买入口从指令切换到正式交互

---

## 11. 第一批必须实现的命令（便于联调）

- /stardewanimal building create <coop|barn> <tier>
- /stardewanimal building validate <buildingId>
- /stardewanimal building rename <buildingId> <name>
- /stardewanimal buy <animalType> <buildingId> [name]
- /stardewanimal debug list
- /stardewanimal debug inspect <animalId>
- /stardewanimal debug move <animalId> <buildingId>
- /stardewanimal debug force_daily

说明：
- 这些命令是“开发调试接口”，不是最终玩家体验。
- 正式版本可隐藏或降权。

---

## 12. 风险与决策

## 12.1 关键风险
- 你要求“完全一毛一样”是高标准，最大风险在于：
  - MC 与 SV 引擎差异导致行为表达层不一致。

## 12.2 解决策略
- 定义“逻辑一致优先，表现一致次之”：
  - 数值与规则链必须一致；
  - 路径动画/碰撞细节允许分阶段接近。

## 12.3 不可妥协项
- 动物来源限制
- 建筑绑定强约束
- 核心概率/阈值/分支顺序
- 日更后状态结果

---

## 13. 参考基线文件（本项目允许范围内）

原版源码与数据（源文件目录）：
- StardewValley/FarmAnimal.cs
- StardewValley/AnimalHouse.cs
- StardewValley/Menus/PurchaseAnimalsMenu.cs
- StardewValley/Menus/AnimalQueryMenu.cs
- StardewValley/Menus/AnimalPage.cs
- StardewValley/Buildings/Building.cs
- StardewValley/Buildings/Barn.cs
- StardewValley/Buildings/Coop.cs
- StardewValley/Tools/MilkPail.cs
- StardewValley/Tools/Shears.cs
- StardewValley/Events/QuestionEvent.cs
- StardewValley/Events/SoundInTheNightEvent.cs
- StardewValley/Events/WitchEvent.cs
- StardewValley/DataLoader.cs
- Content/Data/FarmAnimals.json

当前模组代码基线：
- src/main/java/com/stardew/craft/time/StardewTimeManager.java
- src/main/java/com/stardew/craft/event/CommandEventHandler.java
- src/main/java/com/stardew/craft/command/WeatherDebugCommand.java
- src/main/java/com/stardew/craft/block/ModBlocks.java
- src/main/java/com/stardew/craft/item/ModItems.java
- src/main/java/com/stardew/craft/manager/SprinklerManager.java

---

## 14. 下一步执行建议（从现在开始）

1) 先实现 Phase A + Phase B（数据与建筑）
- 因为你的核心要求是“动物必须绑定建筑”，这两项是前置。

2) 再实现 Phase C（指令购买入口）
- 先跑通“玛尼购买替代指令 + 完整校验 + 绑定”。

3) 再推进 Phase D（日更与产物）
- 保证核心可玩闭环。

4) 最后接 Phase F（GeckoLib 表现）
- 避免先做视觉导致逻辑反复返工。

---

## 15. 结论

这份方案已经把你的想法固化为可落地工程路线：
- 动物获取路径严格受限于原版三渠道（购买/怀孕/孵化）；
- 动物创建时强制绑定合法建筑；
- 建筑通过“管理器 + 范围规则”在 MC 中完成原版建造逻辑替代；
- 所有核心行为以原版源码分支为准，配套一致性校验机制。

后续开发中，任何新需求都应先回答两个问题：
- 是否影响原版逻辑一致性？
- 是否破坏动物-建筑绑定强约束？

若答案为“是”，则该改动必须进入专项评审，不得直接实现。
