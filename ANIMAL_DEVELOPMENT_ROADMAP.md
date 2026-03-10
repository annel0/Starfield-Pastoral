# 动物模块开发路线图（含 GeckoLib 从 0 到 1）

> 目标：把“农场动物系统”拆成可执行的小步任务，先做可跑通的鸭子试点，再扩展到全动物。
>
> 约束：严格按原版 Stardew Valley 逻辑；只参考当前模组代码 + 源文件原版代码；不使用数据包方案。

---

## 1. 你当前状态与现实策略

你现在的情况：
- 动物系统逻辑目标很清晰；
- GeckoLib 使用经验为 0；
- 你已经有动物建模动画资源（这是最关键优势）。

最优策略：
- **先做“鸭子试点”**，用 1 个动物打通全链路；
- 通过鸭子把“资源导出规范 + 实体接入模板 + 动画状态驱动”一次定死；
- 后续其他动物只做“复制模板 + 换资源 + 数据配置”。

---

## 2. 分阶段开发规划（一步一步落实）

## Phase A（第 1 周）：逻辑底座，不碰复杂表现

目标：把动物系统核心数据和建筑绑定跑通。

任务：
1. 动物/建筑持久化
- 建立 AnimalWorldData（SavedData）
- 建立 AnimalId、BuildingId、强绑定约束

2. 建筑管理器
- 鸡舍管理器、畜棚管理器方块
- 范围扫描、规则校验、建筑注册、命名

3. 获取入口（先指令代替玛尼）
- /stardewanimal buy ...
- 校验链：价格、容量、建筑类型、解锁条件

完成标准：
- 能创建建筑；
- 能通过指令买到动物并绑定建筑；
- 重进世界数据不丢。

---

## Phase B（第 2 周）：鸭子试点（Gecko + 日常逻辑）

目标：只做“鸭子”一只，打通可视与逻辑。

任务：
1. GeckoLib 实体接入（鸭子）
- 实体类、模型类、渲染器类、客户端注册

2. 动画状态机
- idle / walk / eat / sleep / happy（最小集）
- 逻辑状态驱动动画，不反向依赖

3. 每日逻辑（鸭子）
- 喂食、心情、好感、产物生成
- 建筑绑定验证

完成标准：
- 鸭子能正常显示并播动画；
- 每日状态变化可观察；
- 产物链可跑通。

---

## Phase C（第 3 周）：原版关键事件与一致性校验

目标：补齐“严格一致”的核心难点。

任务：
1. 怀孕/孵化/夜间事件接入
2. 概率和阈值按原版代码对齐
3. 固定随机种子回归测试（与原版对比）

完成标准：
- 偏差项可列出并归因；
- 关键行为一致（来源限制、绑定、日更、事件分支）。

---

## Phase D（第 4 周+）：规模化扩展

目标：从鸭子扩展到鸡、牛、羊等。

任务：
- 复制鸭子模板接入更多动物；
- 扩展工具采收（奶桶/剪刀）；
- 逐步补齐日出外出/日落回家表现（可后置）。

---

## 3. 鸭子试点：最小可行实现（MVP）

## 3.1 先做什么

优先顺序：
1) 先把“鸭子资源文件”导出到正确路径；
2) 再写最小实体接入代码；
3) 最后接日更逻辑和产物。

## 3.2 这一步先不做什么

- 不先做全部动物；
- 不先做复杂 AI 路径；
- 不先做玛尼正式 UI。

---

## 4. GeckoLib 零基础上手（你现在就能做）

## 4.1 你需要的软件

- Blockbench（免费）
- 安装 GeckoLib 相关插件（Blockbench 插件市场）

## 4.2 在 Blockbench 里创建鸭子模型

建议流程：
1. 新建 Gecko 模型（Entity 类型）
2. 建骨骼：root/body/head/legs/wings
3. 绑定贴图（先 64x64 或 128x128，统一即可）
4. 做最小动画：
   - idle（循环）
   - walk（循环）
   - eat（循环或短循环）

## 4.3 导出什么文件

从 Blockbench 导出 3 类文件：

1) geo 文件
- 例如：duck.geo.json

2) animation 文件
- 例如：duck.animation.json

3) texture 文件
- 例如：duck.png

## 4.4 放到你项目的目录

放置目标：
- src/main/resources/assets/stardewcraft/geo/entity/duck.geo.json
- src/main/resources/assets/stardewcraft/animations/entity/duck.animation.json
- src/main/resources/assets/stardewcraft/textures/entity/duck.png

---

## 5. 参考“汇流来世”能学什么

你给的汇流来世里，源码是编译后的 class（不是 java 源），但资源结构非常有参考价值：

- 资源目录分层清晰：
  - assets/confluence/geo/...
  - assets/confluence/animations/...

- 示例动画文件可直接对照格式：
  - [run/mods/学习资源/[汇流来世] ConfluenceOtherworld-1.2.2-260208/assets/confluence/animations/entity/unicorn.animation.json](run/mods/%E5%AD%A6%E4%B9%A0%E8%B5%84%E6%BA%90/%5B%E6%B1%87%E6%B5%81%E6%9D%A5%E4%B8%96%5D%20ConfluenceOtherworld-1.2.2-260208/assets/confluence/animations/entity/unicorn.animation.json)

- 示例 geo 文件格式可直接对照：
  - [run/mods/学习资源/[汇流来世] ConfluenceOtherworld-1.2.2-260208/assets/confluence/geo/entity/magic_dagger_projectile.geo.json](run/mods/%E5%AD%A6%E4%B9%A0%E8%B5%84%E6%BA%90/%5B%E6%B1%87%E6%B5%81%E6%9D%A5%E4%B8%96%5D%20ConfluenceOtherworld-1.2.2-260208/assets/confluence/geo/entity/magic_dagger_projectile.geo.json)

结论：
- 先按它的“资源目录组织方式”学；
- 代码层我们用你项目自己的结构来落地。

---

## 6. 你需要提供的资产清单（按优先级）

## P0（立刻要，才能开工鸭子）

1. 鸭子 geo 文件
2. 鸭子 animation 文件（至少 idle/walk/eat）
3. 鸭子贴图
4. 动画状态映射表（文本）
   - 例如：
     - moving=true -> walk
     - eating=true -> eat
     - sleeping=true -> sleep

## P1（鸭子跑通后）

1. 鸡/牛/羊同样三件套
2. 管理器方块贴图（coop/barn manager）
3. 建筑构件贴图（喂食槽、孵化器等）

## P2（后续优化）

1. 事件专用动画（受惊、产物提示等）
2. 音效资源
3. 品质层级视觉差异资源

---

## 7. 本周落地清单（你可以直接照着做）

Day 1
- 你导出鸭子三件套（geo/animation/texture）
- 放到 stardewcraft 对应资源目录

Day 2
- 我接入鸭子实体最小代码（可渲染、可生成）

Day 3
- 绑定最小动画状态机（idle/walk/eat）

Day 4
- 接入动物建筑绑定与最小日更

Day 5
- 做一轮偏差检查并修正（与原版逻辑对照）

---

## 8. 你现在最该做的第一步

先别焦虑 GeckoLib 代码，**先把鸭子的 3 个文件导出来**：
- duck.geo.json
- duck.animation.json
- duck.png

你把这三件给我，我就能直接开始“鸭子实体接入 + 动画驱动 + 建筑绑定联调”。

---

## 9. 与现有文档关系

本文件是执行路线图。

总设计仍以 [ANIMAL_SYSTEM_IMPLEMENTATION_PLAN.md](ANIMAL_SYSTEM_IMPLEMENTATION_PLAN.md) 为准，
本文件用于把它拆成“今天就能做”的任务序列。
