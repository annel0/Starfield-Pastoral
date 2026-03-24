# Lewis 日程重规划（先做单 NPC）

参考：
- 全局策略与推进顺序见 ALL_NPC_SCHEDULE_PARITY_MASTER_PLAN.md。

## 范围
- 本阶段只做 Lewis。
- 事实源优先级：
  1) Stardew Wiki 的日程语义（key 优先级与命令含义）。
  2) 导入的原版 Lewis 日程条目。
  3) 由项目 owner 手动维护的路线点位。

## 已确认的 Lewis 原版条目（来自导入文件）
- spring: 800 ManorHouse -> 900 Town -> 940 Town -> 1040 CommunityCenter -> 1400 CommunityCenter -> 1440 CommunityCenter -> 1600 CommunityCenter -> 1700 CommunityCenter -> 1830 ManorHouse -> 2300 bed
- 1: 800 ManorHouse -> 1000 FishShop -> 1600 ManorHouse -> 2100 ManorHouse -> 2200 bed
- 3: 800 ManorHouse -> 1000 AnimalShop -> 1600 ManorHouse -> 2100 ManorHouse -> 2200 bed
- 6: 800 ManorHouse -> 1000 Blacksmith -> 1300 ArchaeologyHouse -> 1600 ManorHouse -> 2100 ManorHouse -> 2200 bed
- 15: 800 ManorHouse -> 1000 ScienceHouse -> 1600 ManorHouse -> 2100 ManorHouse -> 2200 bed
- 20: GOTO 6
- 24: GOTO 3
- Tue: 800 ManorHouse -> 1000 Town -> 1040 Town -> 1140 SeedShop -> 1600 ManorHouse -> 2100 ManorHouse -> 2200 bed
- Fri: 800 ManorHouse -> 1000 Town -> 1040 Town -> 1140 Town -> 1400 Town -> 1700 Saloon -> 2300 bed
- 其余特殊键：summer_Sun、fall_Sun、winter_Sun、rain、fall_9、winter_16、GreenRain、DesertFestival_1/2/3

## key 选择规则（按 wiki 语义强制）
普通（非婚后）日程按以下顺序首个命中：
1. <festival_id>_<festival_day>, <festival_id>
2. <season>_<day>
3. <day>_<hearts>
4. <day>
5. bus（Pam 特例）
6. rain2
7. rain
8. <season>_<dayOfWeek>_<hearts>
9. <season>_<dayOfWeek>
10. <dayOfWeek>_<hearts>
11. <dayOfWeek>
12. <season>
13. spring_<dayOfWeek>
14. spring
15. default

补充：
- key 不区分大小写。
- Sun 只代表星期日，不代表天气。
- GOTO 必须递归解析并有循环保护。
- 找不到 key 时：回退 spring，再回退 default。

## 映射/路线合同（新）
禁止锚点猜测，禁止 tile 偏移猜测。

每个日程节点都使用 owner 明确提供的映射：
- schedule_location：日程原始地点（例如 CommunityCenter）
- start_point_id：owner 指定起点（可选覆盖）
- end_point_id：owner 指定终点（必填）
- travel_mode：WALK 或 INSTANT_WARP

点位表（手工维护，owner 控制）：
- point_id
- dimension
- x, y, z
- tags（例如 indoor、door、plaza）

如果 end_point_id 缺失：运行时不移动 NPC，并输出硬警告日志。

## 当前点位录入（你已提供，先按室外版执行）
说明：本节是临时执行版，后续会替换为完整室内版本。除 Town_Garden 外，其余点位都标记为“待改为室内目标”。

1. ManorHouse_Door
- 类型：室外门点
- 坐标：(-196, -17, -22)
- 备注：当前无室内点，后续补。

2. Town_Garden
- 类型：纯室外目标
- 坐标：(-206, -18, -24)
- 备注：该点当前可作为稳定目标，不需要室内替换。

3. CommunityCenter_Hall（临时）
- 当前替代点：CommunityCenter_OuterDoor
- 坐标：(-190, -10, 138)
- 备注：待补室内 Hall 点后切换。

4. Saloon_Table（临时）
- 当前替代点：Saloon_OuterDoor
- 坐标：(-163, -17, 14)
- 备注：Saloon_Table 是纯室内目标，当前先走 saloon 室外门。

5. SeedShop_Counter（分段目标）
- 第一段：SeedShop_OuterDoor = (-159, -18, 54)
- 第二段：SeedShop_InnerEntry = (12038, 71, 12038)
- 第三段：SeedShop_Counter = (12049, 71, 12038)

6. FishShop_Counter（分段目标）
- 第一段：FishShop_OuterDoor = (-237, -15, -212)
- 第二段：FishShop_InnerEntry = 待补
- 第三段：FishShop_Counter = 待补

## 室内外过渡规则（强制）
去任意目标时按以下固定流程执行，禁止跳步：

1. 若起点在室内：
- 先移动到“当前室内出口点”（该点与对应入口点同坐标）。
- 再传送/切换到“当前建筑室外门点”。

2. 跨建筑移动：
- 从当前建筑室外门点出发。
- 走到目标建筑室外门点。

3. 目标在室内时：
- 先进入目标建筑室内入口点。
- 再从室内入口点移动到室内目标点（如 counter/table/hall）。

4. 目标是纯室外时：
- 直接以室外目标点为终点，不进入室内。

## 寻路合同（新）
- 不使用生物 AI 寻路。
- 实现自定义 tile/grid 路径服务：
  - 基于已加载区块构建可通行网格快照。
  - 在格点中心执行 A*。
  - 做路径平滑，减少折返抖动。
  - 目标变化或遇阻时重算。
- 路径代价必须做“道路优先”：
  - 草径、凝灰岩苔石等项目内定义的“道路方块”赋予更低代价。
  - 普通草方块等“野路”赋予更高代价。
  - 在可达前提下优先选道路，不允许穿墙。
- 地形约束：
  - 需要支持翻越地形高差（在可通行规则内处理爬升/下降）。
  - 禁止生成穿越实体墙体和不可通行方块的路径。
- 移动控制器仅消费已计算 waypoint。
- 若 NPC 明显偏离当前路径：
  - 先触发重算路径，不立即传送。
  - 只有“偏差过大”或“时间突变”时才允许 TP 到路径终点。
  - TP 是最后兜底策略，默认关闭。

## Lewis 单体实施阶段
1. 除 Lewis 外其余 NPC 运行时冻结。
2. key 解析器替换为严格 wiki 顺序并增加 trace。
3. 增加 Lewis 专属点位表和路线映射表。
4. Lewis 移动接入自定义路径服务（无 mob navigation）。
5. 增加确定性调试输出：
   - key 候选列表与命中原因
   - 当前节点、起终点 id
   - 路径长度、下一 waypoint、预计到达时间
6. 在春季第 1 天和第 2 天做 checkpoint 验证。

## 需要补充的信息（下一步）
当前批次（2-7）先不做室内点位，以下项延后：
1. CommunityCenter_Hall 的室内坐标。
2. Saloon_Table 的室内坐标。
3. FishShop_InnerEntry 与 FishShop_Counter 的室内坐标。
4. 每个建筑的“室内出口点（=入口点）”最终坐标确认。

本批次必须完成：
1. key 选择顺序严格 trace（含候选和拒绝原因）。
2. 端点缺失硬告警与停止移动策略。
3. no_path 重算与兜底同步的可观测指标。
4. 单 Lewis 唯一实体与 entity=<missing> 回归防护证据输出。

系统将按你给的分段端点自动生成中间路径，不需要你手工录入全路径。

## 验收标准（Lewis 阶段）
- 世界内始终只有一个 Lewis 实体。
- 每个 checkpoint 调试输出都能给出确定性 key 与节点。
- Lewis 能在容差范围到达每个端点。
- 不允许回退到“猜测映射”。
- 正常日程过程中不再出现 entity=<missing>。
- 室内点位未纳入本批次验收，待后续室内 parity 批次单独验收。
