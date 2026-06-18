# Stardew Valley Fair Phase 1 Registration Plan

本文档记录 Fall 16 `Stardew Valley Fair` / `星露谷展览会` 第一阶段的注册规划。

当前阶段只讨论“需要注册什么”和“每个注册项的来源、命名、资源缺口、交互归属”。不实现 Java、JSON、贴图、模型、声音、overlay、坐标、NPC 站位或小游戏逻辑。任何会落到运行时的坐标、交互格、AABB、展示台位置、摊位位置、NPC 站位、camera anchor，都必须在后续单独确认后再写入代码或资源。

## 0. Scope

阶段一目标：

- 列出秋季展览会会场需要注册的方块、方块物品和节日相关物品。
- 区分“用户已经在做模型的会场方块”“原版商店/奖励需要的物品”“可能应延后到玩法阶段的运行时对象”。
- 先从本地 `源文件` 和项目已有注册里找资产与语义，不凭 wiki、视觉印象或原版 tile 坐标推断。
- 给出首批实现候选，等用户确认 ID、模型路径、注册范围后再进入实现。

非目标：

- 不接入 `FestivalRegistry`、`FestivalService`、`ActiveFestivalHandlers`。
- 不写 star token 经济、grange display、小游戏、评分、对话、商店 GUI。
- 不迁移或转换音频。
- 不创建坐标或交互点位。
- 不 commit / push。

当前仓库状态备注：执行规划前已读 `AGENTS.md` / `CLAUDE.md`，并执行 `git status --short --branch`。当前 `main` 相对 `origin/main` 显示 `ahead 1`，且已有多处 tracked/untracked 用户改动。本阶段只新增本 Markdown，不整理、不清理、不提交工作树。

## 1. Source Ledger

### 1.1 原版节日入口与日期

本地原版来源：

- `源文件/Content/Data/Festivals/FestivalDates.json`
- `源文件/Content/Data/Festivals/FestivalDates.zh-CN.json`

关键事实：

- `fall16`: `Stardew Valley Fair`
- 中文：`星露谷展览会`

阶段一影响：

- 注册项命名建议统一使用 `stardew_valley_fair` 或短前缀 `fair`，避免后续与其他秋季节日混淆。
- 方块/物品显示名先用中文规划，实际 lang key 后续按项目双语资源风格落地。

### 1.2 原版 Star Token 商店

本地原版来源：

- `源文件/Content/Data/Shops.json` 中 `Festival_StardewValleyFair_StarTokens`
- 该商店 `Currency = 1`，即原版 festivalScore / star token，不是金币。

原版固定商品：

| 原版 ID | 名称 | 价格 | 原版条件 | 阶段一注册判断 |
| --- | --- | ---: | --- | --- |
| `(F)1307` | Dried Sunflowers / 风干太阳花 | 100 | 每玩家限 1 | 项目未确认已有，建议列入待接入家具/装饰 |
| `(H)19` | Fedora / 软呢帽 | 500 | 每玩家限 1 | 项目 hat 系统未确认，建议先列入待调研 |
| `(BC)110` | Rarecrow | 800 | 每玩家限 1 | 项目已有 `scarecrow_6`，可优先映射确认 |
| `(O)434` | Stardrop / 星之果实 | 2000 | `!PLAYER_HAS_MAIL Current CF_Fair` | 项目已有 `stardrop`，奖励 gating 后续用 mail flag |
| `(O)PrizeTicket` | Prize Ticket / 兑奖券 | 1000 | 每玩家限 1 | 项目已有 `prize_ticket` |
| `(F)2488` | Light Green Rug / 淡绿色地毯 | 500 | 每玩家限 1 | 项目未确认已有，建议列入待接入家具/地毯 |

原版轮换商品：

| 原版 ID | 名称 | 价格 | 阶段一注册判断 |
| --- | --- | ---: | --- |
| `(O)253` | Triple Shot Espresso | 400 | 项目已有 `triple_shot_espresso` |
| `(O)215` | Pepper Poppers | 250 | 项目已有 `pepper_poppers` |
| `(O)888` | Magic Rock Candy | 1000 | 项目已有 `magic_rock_candy` |
| `(O)178` | Hay x100 | 500 | 项目已有 `hay` |
| `(O)770` | Mixed Seeds x24 | 1000 | 项目已有 `mixed_seeds` |

阶段一影响：

- Star Token 不建议先注册成普通背包物品。原版是节日临时货币，存于 `festivalScore`，商店用 `Currency = 1` 读取。StardewCraft 后续应优先做 runtime currency / overlay icon，而不是让玩家持有 `star_token` item。
- 如果后续 UI 需要图标，可以单独接 icon/GUI texture；是否注册 `star_token` item 需要用户确认，因为它会改变原版语义。

### 1.3 原版小游戏与交互来源

本地原版来源：

- `源文件/StardewValley/Event.cs`

已定位的 Fall 16 相关逻辑：

- `playerControlSequenceID == "fair"` 时绘制 Star Token HUD。
- `questionKey == "StarTokenShop"` 进入花金币购买 Star Token 的数量菜单。
- `questionKey == "starTokenShop"` 打开 `Festival_StardewValleyFair_StarTokens`。
- `questionKey == "wheelBet"` 打开下注数量菜单并进入 `WheelSpinGame`。
- `questionKey == "slingshotGame"` 花 50g 进入 `TargetGame`。
- `readFortune()` 处理算命帐篷，费用 100g，并写入 `fortuneTeller{year}` mail flag。
- `caughtFish()` / `perfectFishing()` 在 `fall16` fishing minigame 中累计分数。
- `forceFestivalContinue()` 对 `fall16` 不直接进入普通 mainEvent，而是 `initiateGrangeJudging()`。
- `draw()` 在 `fall16` 下把 `Game1.player.team.grangeDisplay` 的 9 个物品画到展示台上。

阶段一影响：

- 测力计、转盘、烤肉架、墓石类方块都可以先注册为会场方块，但其右键行为应放到后续 active festival handler 或专用 block interaction 中。
- Grange Display 不是普通装饰。它需要 9 格展示、放入/取回物品、多人队伍数据、评分与奖励。展示台方块是否第一批注册需要单独确认。
- 弹弓、钓鱼、转盘、测力计、算命都不是简单方块注册能完成的功能；阶段一只保留“交互入口方块/摊位方块”。

### 1.4 救生汉堡来源

本地原版来源：

- `源文件/Content/Data/Objects.json` 中 `(O)241`，`Name = Survival Burger`
- `源文件/Content/Data/CookingRecipes.json` 中 `Survival Burger`
- `源文件/Content/Strings/Objects.json` / `Objects.zh-CN.json`

项目现状：

- 已有 `stardewcraft:survival_burger`。
- 已有放置食物模型与 item 模型：`placed_food_survival_burger`、`models/item/survival_burger.json`。

阶段一影响：

- 烤肉架方块的后续右键目标可直接复用 `stardewcraft:survival_burger`。
- 需要确认行为语义：原版节日烧烤更接近“直接吃/获得食物效果”还是“给一个物品”。用户当前描述是“右键之后会给一个救生汉堡”，所以规划先按给物品记录；实现前再确认是否要改为直接食用。

### 1.5 墓石来源

本地原版来源：

- `源文件/Content/Data/BigCraftables.json` 有 Grave Stone 相关条目。
- `源文件/Content/Strings/BigCraftables.json` / `BigCraftables.zh-CN.json` 提供本地化。

阶段一影响：

- 用户提到“墓石一个 墓石一个”，这里可能是重复输入，也可能表示两个墓石变体。
- 如果只是会场装饰，建议注册为静态朝向方块；如果要复刻原版 BigCraftable，可对照原版 Grave Stone 数据接资产。
- 需要用户确认是一种墓石、两种墓石，还是一个墓石加另一个墓地/灵媒摊位相关装饰。

## 2. Project Architecture 对照

阶段一可能触达的项目注册点，后续确认后再改：

- `src/main/java/com/stardew/craft/block/ModBlocks.java`
- `src/main/java/com/stardew/craft/item/ModItems.java`
- `src/main/resources/assets/stardewcraft/blockstates/*.json`
- `src/main/resources/assets/stardewcraft/models/block/...`
- `src/main/resources/assets/stardewcraft/models/item/...`
- `src/main/resources/assets/stardewcraft/lang/en_us.json`
- `src/main/resources/assets/stardewcraft/lang/zh_cn.json`
- 可能的 loot table / tag / creative tab 资源，按当前注册模式补齐。

现有可复用模式：

- 装饰/会场静态方块：优先复用 `MapDecorStaticBlock` 或项目已有朝向装饰 block 模式。
- thin/wall 类装饰：如果模型需要贴墙，再参考 `MapDecorWallStaticBlock` / `MapDecorWallThinBlock`。
- 方块物品：已有 `StardewBlockItem` 风格，通常带 `stardewcraft.type.furniture`。
- 食物物品：救生汉堡等已由 cooking 注册，不需要重新注册。
- 商店物品：后续由 `ShopRegistry` 映射，不要求阶段一全部接商店逻辑。

阶段一建议不新建平行 runtime。所有方块注册都应该服务于后续 active festival 架构，而不是每个方块各自写一套节日状态。

## 3. Block Registration Candidate Ledger

命名只是建议，最终 ID 需要用户确认。

| 候选 ID | 中文名 | 来源/用途 | 阶段一建议 | 后续交互归属 | 待确认 |
| --- | --- | --- | --- | --- | --- |
| `fair_strength_tester` | 展览会测力计 | 用户已有新模型；原版 strength game 摊位入口 | 第一批注册 | 后续右键进入测力计小游戏或打开确认/提示 | 模型路径、朝向、碰撞体积、是否只在节日可用 |
| `fair_spin_wheel` | 展览会转盘 | 用户已有新模型；原版 `wheelBet` / `WheelSpinGame` 入口 | 第一批注册 | 后续右键打开 Star Token 下注 UI | 模型路径、是否需要动画/转盘状态、朝向 |
| `fair_gravestone` | 展览会墓石 | 用户已有新模型或原版 Grave Stone 资产；会场装饰/算命区域氛围 | 第一批注册或等用户确认数量后注册 | 通常无交互；若属于算命摊位则交互放 fortune teller 入口 | 是一个还是两个变体；是否复刻原版 BigCraftable |
| `fair_gravestone_2` | 展览会墓石变体 | 仅当用户确认“墓石一个 墓石一个”是两个不同方块 | 暂列候选 | 同上 | 是否真的需要第二个 ID |
| `fair_barbecue_grill` | 展览会烤肉架 | 用户已有新模型；右键给救生汉堡 | 第一批注册 | 后续右键发放或直接食用 `survival_burger`，需节日内限制/冷却 | 模型路径、是否允许节日外使用、给物品还是直接吃 |

### 3.1 可能需要但不建议默认塞进第一批的方块

| 候选 ID | 用途 | 建议 |
| --- | --- | --- |
| `fair_grange_display_stand` | 9 格农庄展示台，放置/取回/评分核心玩法 | 如果用户已有模型且希望 Phase 4 不再补注册，可第一批只注册空展示台；否则等 Grange Display 设计确认 |
| `fair_star_token_counter` | 买 Star Token / Prize shop 入口 | 可先用 overlay/NPC/既有摊位交互实现，不一定需要方块 |
| `fair_slingshot_booth` | 弹弓小游戏入口 | 等小游戏阶段确认，因为可能只是 NPC/摊位 AABB |
| `fair_fishing_booth` | 钓鱼小游戏入口 | 等小游戏阶段确认 |
| `fair_fortune_teller_booth` | 算命入口 | 等算命阶段确认；墓石不应默认承担算命交互 |
| `fair_prize_shop_counter` | Star Token 商店入口 | 商店可先由 NPC/摊位交互打开，不急于注册 |

建议第一批只做用户明确提到的 4 到 5 个方块，加上用户确认后才加入展示台。这样注册面小，后续如果场地布局变动，不会产生一堆未使用方块。

## 4. Item Registration Candidate Ledger

### 4.1 已有项目物品，可复用

| StardewCraft ID | 原版来源 | 用途 |
| --- | --- | --- |
| `stardewcraft:survival_burger` | `(O)241` | 烤肉架右键奖励 |
| `stardewcraft:stardrop` | `(O)434` | Star Token 商店 2000 token 奖励 |
| `stardewcraft:prize_ticket` | `(O)PrizeTicket` | Star Token 商店 1000 token 商品 |
| `stardewcraft:scarecrow_6` | `(BC)110` Rarecrow | Star Token 商店 800 token 商品，需确认 rarecrow 编号映射 |
| `stardewcraft:triple_shot_espresso` | `(O)253` | 轮换商品 |
| `stardewcraft:pepper_poppers` | `(O)215` | 轮换商品 |
| `stardewcraft:magic_rock_candy` | `(O)888` | 轮换商品 |
| `stardewcraft:hay` | `(O)178` | 轮换商品，数量 x100 |
| `stardewcraft:mixed_seeds` | `(O)770` | 轮换商品，数量 x24 |

### 4.2 需要从原版找资产再决定接入的物品/家具

| 建议 ID | 原版 ID | 中文名 | 类型 | 阶段一建议 |
| --- | --- | --- | --- | --- |
| `dried_sunflowers` | `(F)1307` | 风干太阳花 | furniture / decor | 如果要做完整 Star Token 商店，需接家具/装饰资产 |
| `light_green_rug` | `(F)2488` | 淡绿色地毯 | furniture / rug | 如果项目已有 rug 方块模式，可接；否则先列缺口 |
| `fedora` | `(H)19` | 软呢帽 | hat | 先调研项目是否已有 hat 装备/外观系统；不要用普通 item 假装帽子，除非用户同意 |

### 4.3 不建议阶段一注册为普通物品

| 候选 | 原因 |
| --- | --- |
| `star_token` | 原版不是背包物品，而是 `festivalScore` 货币。注册普通物品会改变经济语义。后续 UI 可做 icon/texture，不一定做 item。 |
| grange display 展示物副本 | 原版使用玩家队伍数据保存展示物，不应为每个展示槽注册新物品。 |
| mini-game tickets | 原版小游戏费用直接扣金币或 Star Token，不需要额外 ticket item。 |

## 5. Phase 1 Implementation Shape After Confirmation

用户确认后，第一批实际实现建议拆成小批：

### Batch A: 用户模型方块

注册：

- `fair_strength_tester`
- `fair_spin_wheel`
- `fair_barbecue_grill`
- `fair_gravestone`
- 可选 `fair_gravestone_2`

资源：

- blockstates
- block models 指向用户已有模型路径
- item models
- en_us / zh_cn lang
- loot tables 或项目当前等价掉落规则
- creative tab / item group 归类

代码：

- 如果阶段一只要求能放置和显示，优先使用现有静态装饰方块类。
- 如果烤肉架需要保留后续右键状态，注册时可以直接用专用 `FairBarbecueGrillBlock`，但行为先最小化，等用户确认后再实现。
- 测力计/转盘可以先静态注册，右键小游戏入口后续在 active festival handler 或专用 block 中接。

验证：

- `./gradlew classes`
- 资源是否被模型路径引用到，需要后续实际运行或资源加载检查确认。

### Batch B: 原版商店缺口物品

注册/接入：

- `dried_sunflowers`
- `light_green_rug`
- `fedora`

前置调研：

- 项目已有家具/地毯/帽子模式。
- 原版 sprite/asset 的实际位置与切图方式。
- 是否要在 Phase 1 就接完整 Star Token 商店，还是先让商店引用已有物品，缺口延后。

建议：

- `dried_sunflowers` 和 `light_green_rug` 可以作为 decor/furniture 优先评估。
- `fedora` 风险最高，因为可能涉及穿戴系统。若项目没有 hat 系统，建议延后或以“未实现商店项”标注。

### Batch C: 可选展示台与摊位入口

只有用户确认后才加入：

- `fair_grange_display_stand`
- `fair_prize_shop_counter`
- `fair_star_token_counter`
- 其他小游戏 booth 方块

建议理由：

- 展示台和摊位入口会绑定坐标、AABB、UI 和玩家状态，过早注册容易变成闲置或错误语义方块。
- 如果用户已经做了模型，并希望先统一注册，也可以先做纯展示方块，但文档/命名要标明后续行为未接。

## 6. Confirmation Checklist

实现前需要用户确认：

- 方块 ID 是否采用短前缀 `fair_*`，还是长前缀 `stardew_valley_fair_*`。
- 测力计模型路径、默认朝向、是否 1 block 占地。
- 转盘模型路径、默认朝向、是否需要独立旋转动画模型。
- 墓石到底是一种还是两种；如果两种，两个显示名和模型路径分别是什么。
- 墓石是否按原版 Grave Stone 接资产，还是使用用户新模型作为节日专用装饰。
- 烤肉架模型路径、默认朝向、碰撞体积。
- 烤肉架右键语义：给 `survival_burger` 物品，还是直接触发食用效果。
- 烤肉架是否只能在展览会期间生效，节日外右键是否无效果。
- 是否把 `fair_grange_display_stand` 纳入 Phase 1 注册。
- 是否 Phase 1 同步接 `dried_sunflowers`、`light_green_rug`、`fedora`，还是先只登记缺口。
- 是否注册 `star_token` 为普通 item。当前建议是否。
- 现有 `scarecrow_6` 是否就是原版 `(BC)110` Rarecrow 的目标映射。

## 7. Recommended First Scope

建议第一批实现范围：

1. 注册用户明确提到且模型基本完成的方块：
   - `fair_strength_tester`
   - `fair_spin_wheel`
   - `fair_barbecue_grill`
   - `fair_gravestone`
   - 如果确认有第二个墓石变体，再加 `fair_gravestone_2`
2. 暂不注册 `star_token` item，先保留为 runtime currency 设计。
3. 暂不实现右键玩法，只在烤肉架文档中保留后续目标 `survival_burger`。
4. 物品商店先列缺口，不立即接 `dried_sunflowers`、`light_green_rug`、`fedora`，除非用户希望 Phase 1 同时补完原版商店资产。
5. 展示台是否加入第一批，等用户看过模型/交互拆分后再定。

这样 Phase 1 的代码面会非常小：注册和资源引用先站稳，避免提前把小游戏、商店经济和展示台数据结构耦到方块上。

## 8. Next Discussion Packet

请用户优先确认下面几项，然后才能进入实现：

| 决策 | 推荐值 | 原因 |
| --- | --- | --- |
| ID 前缀 | `fair_*` | 简短，项目内资源路径不臃肿 |
| 第一批方块 | 测力计、转盘、烤肉架、墓石 1 个 | 与用户已完成模型一致，风险低 |
| 第二墓石 | 暂不注册，等确认 | 用户输入可能只是重复 |
| Star Token | 不注册 item | 贴近原版 `festivalScore` |
| 烤肉架行为 | 后续先做“给一个救生汉堡” | 符合用户当前描述，直接复用已有物品 |
| 展示台 | 暂缓 | 需要 9 格展示、取回、评分、多人状态 |
| 原版商店缺口物品 | 先列缺口，后续单独接资产 | Fedora/家具/地毯涉及不同系统，不宜混进方块注册首批 |

