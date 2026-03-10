# 矿洞注册清单（先列清单，不落地实现）

目标：把 Stardew Mining 维度需要的 **方块/物品/实体/数据表** 一次性列齐。

> 你已确认的前提（不在此展开实现）：
> - 楼层用网格摆放。
> - 电梯保留：首次到达整 5 层解锁；可前往任意已解锁整 5 层。
> - 楼层“当天持久化、隔天重置”。
> - 怪物池与原版一致；容器/loottable 与原版一致。

---

## 资源文件夹规划（贴图/模型放哪里）

以下路径全部以工程根目录为基准：

### A) 贴图（Resource Pack）
- 方块贴图：src/main/resources/assets/stardewcraft/textures/block/mining/
- 物品贴图：src/main/resources/assets/stardewcraft/textures/item/mining/

### B) 模型与方块状态（Resource Pack）
- 方块 blockstate：src/main/resources/assets/stardewcraft/blockstates/
- 方块模型 JSON：src/main/resources/assets/stardewcraft/models/block/
- 物品模型 JSON：src/main/resources/assets/stardewcraft/models/item/

### C) 数据（Data Pack）
- 方块 loot table：src/main/resources/data/stardewcraft/loot_table/blocks/
- 战利品表（自定义集合/逻辑用）：src/main/resources/data/stardewcraft/loot_table/mining/
- 熔炼/配方：src/main/resources/data/stardewcraft/recipe/
- 维度：src/main/resources/data/stardewcraft/dimension/
- 维度类型：src/main/resources/data/stardewcraft/dimension_type/

### D) 美术源文件（建议新建，仅用于你自己管理）
这些不是游戏加载的资源，只是为了让你的工程里“源文件”和“导出物”分离：
- Blockbench：art_source/mining/blockbench/
- 贴图工程文件（PSD/ASE/等）：art_source/mining/textures/

电梯模型约定：
- 你给我源文件：art_source/mining/blockbench/mine_elevator.bbmodel
- 你导出给游戏的模型 JSON：src/main/resources/assets/stardewcraft/models/block/mine_elevator.json
- 电梯贴图：src/main/resources/assets/stardewcraft/textures/block/mining/mine_elevator.png

## P0（先注册出来就能跑起来的最小集合）

### P0-1 方块（Blocks）

#### 0) 矿井方块调色板（你画材质用的“风格说明”）

这一节是“给美术看的”：每个方块我都写了名字、主视觉元素、可选细节。

通用约束（建议）：
- 尺寸：全部按 MC 标准 16×16。
- 读取性：矿洞内光照低，主石头对比不要太低；矿石的“矿点”必须一眼可见。
- 变体：同一个方块建议先做 1 张主贴图；后续再加 2–4 张轻微噪声变体（颜色/裂纹位置微变）。

#### A. 必须自制贴图/模型（你要画/建模）
1) `mine_barrier`（矿洞封印外壳方块）
- 用途：包裹每层房间的墙/顶/底 + 中心梯子背后保护墙。
- 需求：材质（可带符文/裂纹），并且“看得见”。

建议长相：
- 主色：深黑/深靛/深灰。
- 结构：像“熔铸过的黑曜岩”，但有 **细金线/符文刻痕**（表现封印）。
- 细节：边缘微发光（很弱即可），裂纹里有一层“雾状能量”。

资源路径（你照着放文件就行）：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/mine_barrier.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/mine_barrier.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/mine_barrier.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/mine_barrier.json

2) 三段主题主石头（建议改名 + 说明长相）

你说“不会画”主要是因为我只给了抽象名。这里把主石头重新命名并描述：

2.1 `earth_shale`（土段主石头：页岩）
- 气质：干燥、层理明显、像被挤压过的沉积岩。
- 主视觉：横向/斜向的薄层纹理 + 少量碎裂。
- 色调：灰褐/土灰，局部有暗褐色条带。

资源路径：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/earth_shale.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/earth_shale.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/earth_shale.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/earth_shale.json

2.2 `frost_gneiss`（冰段主石头：片麻岩/冻裂岩）
- 气质：更“硬”、带冰冻侵蚀痕迹。
- 主视觉：淡灰底 + 浅蓝/青灰的条带（片麻纹），边缘有冻裂细缝。
- 色调：冷灰、带一点点蓝；不要像纯冰块那么亮。

资源路径：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/frost_gneiss.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/frost_gneiss.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/frost_gneiss.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/frost_gneiss.json

2.3 `lava_basalt`（熔岩段主石头：玄武岩）
- 气质：火山喷发后凝固的致密岩。
- 主视觉：深灰/黑灰底，局部有 **暗红细孔/气泡孔** 或“熔流纹”。
- 色调：黑灰为主，暗红点缀（不能太亮，避免像发光矿）。

资源路径：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/lava_basalt.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/lava_basalt.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/lava_basalt.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/lava_basalt.json

> 备注：如果你更想“对齐星露谷三段贴图的观感”，也可以把命名改回 earth/frost/lava，但贴图请按上面三种气质去画。

3) `mine_elevator`（电梯方块）
- 用途：大厅电梯 + 整 5 层电梯点。
- 需求：你会给模型；先把 registryName / 交互菜单需求定下来即可。

建议长相：
- 作为方块：像“钢框架 + 木质面板 + 铆钉”，中间有一个明显的按钮/指示灯区域。
- 作为模型：电梯门/轨道/轿厢都可以后续做，但注册名先定住。

资源路径：
- Blockbench 源文件（建议）：art_source/mining/blockbench/mine_elevator.bbmodel
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/mine_elevator.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/mine_elevator.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/mine_elevator.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/mine_elevator.json

#### B. 可以先照搬 MC 原版贴图（占位即可）
4) 装饰性石头（重新设计：至少 6 种，有趣且“像石头”）

这些石头的生态位：
- 用作矿井地面混铺/墙体点缀/洞窟小房间的主材。
- 后续都可以扩展成建材族（楼梯/台阶/墙/抛光/砖块）。

建议 6 种（都属于同一套“矿井石材族”）：

4.1 `banded_marble`（条带大理石）
- 长相：浅灰底，带几条较粗的深灰/蓝灰条带（像流水纹）。
- 适用：冰段、洞窟装饰、宝箱房更干净的地面。

资源路径：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/banded_marble.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/banded_marble.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/banded_marble.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/banded_marble.json

4.2 `rusted_limestone`（锈斑石灰岩）
- 长相：米灰/浅褐底，上面有铁锈色斑点与渗流痕。
- 适用：土段洞窟、靠近木桶/矿车区域（更“老矿井”）。

资源路径：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/rusted_limestone.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/rusted_limestone.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/rusted_limestone.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/rusted_limestone.json

4.3 `mossy_sandstone`（苔斑砂岩）
- 长相：暖黄灰底 + 零星绿色苔斑（别做成丛林石那种大块绿）。
- 适用：土段过渡、隐藏房间的“潮湿角落”。

资源路径：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/mossy_sandstone.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/mossy_sandstone.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/mossy_sandstone.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/mossy_sandstone.json

4.4 `cracked_slate`（龟裂板岩）
- 长相：深青灰底，密集细裂纹（像被压力挤裂），少量闪亮矿屑点。
- 适用：三段都能用，尤其适合做“边缘/墙面阴影区”。

资源路径：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/cracked_slate.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/cracked_slate.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/cracked_slate.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/cracked_slate.json

4.5 `scoria`（火山渣岩）
- 长相：黑灰底，孔洞明显（气泡孔），偶尔有暗红内壁。
- 适用：熔岩段洞窟、火山气息的装饰。

资源路径：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/scoria.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/scoria.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/scoria.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/scoria.json

4.6 `salt_rock`（盐霜岩）
- 长相：灰底，但表面有一层白色“粉霜/结晶边”，像潮湿后返盐。
- 适用：冰段、也可作为“特殊洞窟”的标志材。

资源路径：
- 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/salt_rock.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/salt_rock.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/salt_rock.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/salt_rock.json

占位贴图策略：
- 这些 6 种建议不要再直接用 MC 花岗岩那套了；你后续画出来会非常提升观感。

> 可选（P0.5）：每段主题再加 3–6 个地面变体块（只是贴图差异），提升“混铺”观感。

### 维度主题与“Dark 修饰器”（你已选 B）

你已选：B）Dark 作为随机修饰器（不是固定在某个楼层段）。

含义：
- 每层在生成时有概率被标记为 Dark（可叠加在 earth/frost/lava 三主题上）。
- Dark 主要改变“观感与氛围”（更暗、更脏、更压迫），不强制改变掉落/矿种；刷怪逻辑仍以亮度 < 7 为核心。

对资源的影响（先定资产口径，暂不写实现）：
- 最稳/最省心：额外注册一套 dark 变体方块（生成时直接选用 dark 版方块铺出来）。
  - 示例：`dark_earth_shale` / `dark_frost_gneiss` / `dark_lava_basalt`
  - 示例（可选）：`dark_earth_copper_ore` / `dark_frost_copper_ore` / `dark_lava_copper_ore`（如果你想连矿点也变暗）
- 资源路径规则仍同上：
  - 贴图：src/main/resources/assets/stardewcraft/textures/block/mining/<block_id>.png
  - 模型：src/main/resources/assets/stardewcraft/models/block/<block_id>.json
  - blockstate：src/main/resources/assets/stardewcraft/blockstates/<block_id>.json

如果你希望 Dark 只是“调色”而不想新增方块数量，需要额外的渲染/模型切换机制；实现阶段我们再评估。

#### C. 矿石方块（Ores：每个主题外观不同，但掉落同一种 SV ore 物品）

你已确认：矿石本身是方块，并且每个主题下外观不同；
- 挖掘后统一掉落 ore（物品不分主题）
- ore 通过烧制/冶炼变成 bar

建议按“矿种 × 主题”拆分方块（registryName 稳定，贴图可差异化）：

1) 铜矿石（掉落：铜锭/铜矿物）
- `earth_copper_ore`
- `frost_copper_ore`
- `lava_copper_ore`

2) 铁矿石（掉落：铁锭/铁矿物）
- `earth_iron_ore`
- `frost_iron_ore`
- `lava_iron_ore`

3) 金矿石（掉落：金锭/金矿物）
- `earth_gold_ore`
- `frost_gold_ore`
- `lava_gold_ore`

4) 铱矿石（掉落：铱锭/铱矿物）
- `earth_iridium_ore`
- `frost_iridium_ore`
- `lava_iridium_ore`

5) 煤矿石（三主题版本）
- `earth_coal_ore`
- `frost_coal_ore`
- `lava_coal_ore`

外观建议（同一种矿，不同主题“矿点”形状/颜色/亮度不同）：
- Earth：矿点更“泥/氧化”，边缘毛糙，颜色偏暗（铜偏绿锈、铁偏褐红、金偏土黄、铱偏紫灰）。
- Frost：矿点像被霜包了一层，边缘更锐利；可以给一点点偏蓝的高光，但不要自发光。
- Lava：矿点像被高温烧蚀过，周围有黑化/熔融边；少量暗红裂隙作为点缀。

资源路径（按同一规则批量放置）：
- 方块贴图：src/main/resources/assets/stardewcraft/textures/block/mining/<block_id>.png
- 方块模型：src/main/resources/assets/stardewcraft/models/block/<block_id>.json
- 物品模型：src/main/resources/assets/stardewcraft/models/item/<block_id>.json
- blockstate：src/main/resources/assets/stardewcraft/blockstates/<block_id>.json
- 方块 loot table：src/main/resources/data/stardewcraft/loot_table/blocks/<block_id>.json

示例（铜矿石的三主题贴图文件名）：
- earth：src/main/resources/assets/stardewcraft/textures/block/mining/earth_copper_ore.png
- frost：src/main/resources/assets/stardewcraft/textures/block/mining/frost_copper_ore.png
- lava：src/main/resources/assets/stardewcraft/textures/block/mining/lava_copper_ore.png

掉落规则（先写设计，不写实现）：
- 挖掉矿石方块 → 掉对应 ore 物品（不分主题）：
  - `earth_copper_ore` / `frost_copper_ore` / `lava_copper_ore` → `copper_ore`
  - `earth_iron_ore` / `frost_iron_ore` / `lava_iron_ore` → `iron_ore`
  - `earth_gold_ore` / `frost_gold_ore` / `lava_gold_ore` → `gold_ore`
  - `earth_iridium_ore` / `frost_iridium_ore` / `lava_iridium_ore` → `iridium_ore`
  - `earth_coal_ore` / `frost_coal_ore` / `lava_coal_ore` → `stardew_coal`（或你也可决定直接掉 MC `coal`）
- ore 通过烧制/冶炼变成 bar：
  - `copper_ore` → `copper_bar`
  - `iron_ore` → `iron_bar`
  - `gold_ore` → `gold_bar`
  - `iridium_ore` → `iridium_bar`

> 注：上面的 `copper_ore/iron_ore/gold_ore` 在这里指“星露谷体系的 ore 物品”，不等同于 MC 原版同名物品；为避免冲突，后续我们可以统一用 `stardew_*_ore` 作为物品 registryName。

### P0-2 物品（Items）

#### A. 直接照搬 MC 原版贴图/命名（最省事）
1) 铜/铁/金相关
- `copper_ore` / `raw_copper` / `copper_ingot`
- `iron_ore` / `raw_iron` / `iron_ingot`
- `gold_ore` / `raw_gold` / `gold_ingot`

> 说明（已确认）：矿井里掉落的 ore/bar 属于“星露谷体系”，不是 MC 原版同名物品。
> 为避免 registryName 撞车，建议最终命名为：
> - `stardew_copper_ore` / `stardew_iron_ore` / `stardew_gold_ore` / `stardew_iridium_ore`
> - `copper_bar` / `iron_bar` / `gold_bar` / `iridium_bar`

资源路径（当你开始做“星露谷体系”的 ore/bar 自己贴图时）：
- 物品贴图：src/main/resources/assets/stardewcraft/textures/item/mining/<item_id>.png
- 物品模型：src/main/resources/assets/stardewcraft/models/item/<item_id>.json
- 熔炼配方：src/main/resources/data/stardewcraft/recipe/
  - 示例：stardew_copper_ore → copper_bar（对应一个 smelting/blasting recipe JSON）

2) 煤
- 你说“我们自己的煤炭”：建议先定名
  - `stardew_coal`（物品）
  - 贴图可先用 `minecraft:item/coal` 占位，后续再换。

资源路径（当你不再占位、开始做自己的煤炭贴图时）：
- 贴图：src/main/resources/assets/stardewcraft/textures/item/mining/stardew_coal.png
- 模型：src/main/resources/assets/stardewcraft/models/item/stardew_coal.json

3) 石头掉落物
- 方案 A：直接掉 `minecraft:cobblestone`（最少注册）
- 方案 B：我们自己的 `stardew_stone`（后续可用于售价/任务/配方对齐星露谷）

#### B. 星露谷特有（需要我们注册；贴图可先占位）
4) 晶洞体系（建议先把 registryName 定齐）
- `geode`
- `frozen_geode`
- `magma_geode`
- `omni_geode`

> 贴图占位：可以先用一套简单图标；后续你再按原版画。

资源路径（晶洞物品）：
- 贴图：src/main/resources/assets/stardewcraft/textures/item/mining/<item_id>.png
- 模型：src/main/resources/assets/stardewcraft/models/item/<item_id>.json

### P0-3 维度/数据（Datapack 资源）
1) 维度：`data/stardewcraft/dimension/stardew_mining.json`
2) 维度类型：`data/stardewcraft/dimension_type/stardew_mining.json`
3) 生物群系（如果需要）：最小 1 个 `mining_void`（用于统一环境参数）

---

## P1（对齐原版体验所需：容器/矿车/可交互物）

### P1-1 容器方块（Blocks）
1) 木桶（barrel）
2) 箱子（crate）
3) 矿车（minecart，可能是装饰实体或方块）
4) “箱子矿车”（minecart with chest）的等价物（取决于你想用方块还是实体）

> 注：如果目标是“完全对齐原版 SV”，容器最好有**自己的 loot table**（见下节）。

### P1-2 对齐原版的 loot tables（数据表清单）

你要求“木桶/箱子/矿车 loot 与原版一模一样”。这意味着我们需要把 SV 的掉落表整理成可实现的数据。

需要整理出的表（先列清单，不填内容）：
1) `mining/stone_break_drops.json`（挖石头：基础掉落 + 稀有掉落 + 晶洞概率 + 随楼层变化）
2) `mining/container_drops.json`（木桶/箱子/矿车）
3) `mining/monster_drops.json`（怪物掉落）
4) `mining/treasure_rooms.json`（宝箱层/特殊层奖励）

---

## P2（怪物与数值体系：先把“需要哪些”列出来）

### P2-1 怪物实体注册（Entities）
- 目标：怪物池与原版一致。
- 这里需要一份“SV 怪物列表 → 我们的 entity registryName” 的对照表。

### P2-2 数值重标定（你提到的重点）
你说：维度内怪物伤害/玩家伤害要按我们的数值；维度外恢复 MC。
这块建议单独开一个设计文档，列：
- MC 武器/护甲/生命值 与 SV 数值的映射（线性？分段？）
- 只在维度内生效的规则开关
- 玩家离开维度时如何恢复（避免装备被永久改写）

---

## 命名与资源占位原则（建议）

1) registryName 尽量稳定：先把名字定死，贴图/模型后换不影响存档。
2) 能照搬就照搬：铜/铁/金可先直接用 MC 原版物品与贴图（你说的“多数都能照搬”）。
3) 必须自制的优先：`mine_barrier`、三段主题主石头、电梯。

---

## 下一步我可以做什么（仍然只做清单/对照，不写实现）

A) 生成“SV 原版矿洞掉落/容器/怪物”对照表骨架（填空表），你确认后再补数据。
B) 把 P0/P1 的 registryName 固化成一张表（用于你画材质、我后续写注册）。

你想先从哪块开始：
- 1）先把“矿井物品/方块 registryName 清单”定死
- 2）先把“loottable 对照表骨架”拉出来
