# 刘易斯的紫色短裤实现规划

> 当前文件只写规划，不实现 Java、资源或数据改动。目标是把原版《星露谷物语》里刘易斯紫色短裤相关彩蛋，按 StardewCraft 现有系统拆成可以逐步落地、可验证、可回滚的小阶段。

## 1. 当前目标

实现 `Lucky Purple Shorts` 这一组功能：

- 注册紫色短裤物品，按任务物品处理。
- 物品图标严格从原版资产 `(O)789` 裁切，视觉与原版一致。
- tooltip/物品展示要像星之果实一样有特殊紫色动态效果，代码风格复用现有 `SpecialConsumableClientFx`。
- 物品可 `Shift + 右键` 放置为短裤方块。
- 短裤方块使用 `tmp_models/紫色短裤.json` 和 `tmp_models/紫色短裤.png`，并像实用设施一样使用模型逐像素碰撞。
- 空手右键短裤方块可拾取回短裤物品。
- 玛妮房间、刘易斯地窖、鱼竿短裤浮标、地窖短裤怪物全部接入。

## 2. 用户已确认坐标与区域

| 用途 | 坐标 / 区域 | 规则 |
| --- | --- | --- |
| 玛妮房间任务短裤 | `-86 34 12` | 只有接到任务的玩家可见、可拾取。该玩家拾取后自己不可再见/不可再拿，不影响其他玩家进度。 |
| 刘易斯家楼梯触发区域 | `64 58 19` 到 `72 51 27` | 玩家手持矿井梯子右键时触发，不放置楼梯。实现时归一化为 `x 64..72, y 51..58, z 19..27`。 |
| 楼梯传送目标 | `67 44 20` | 消耗 1 个矿井梯子，传送后面朝南。创造模式不消耗。 |
| 地窖短裤 | `81 44 32` | 所有人可拿，但每个玩家每次下地窖只能拿一次。本次拿完隐藏，下次下地窖刷新。 |

仍需注意：地窖“区域内没有玩家时清理怪物”需要一个明确的地窖 bounds。如果项目已经有 `lewis_basement` 室内 region，就复用；如果没有，实现前需要再确认边界，不能凭视觉或地图猜。

## 3. 原版依据摘要

| 原版模块 | 已确认事实 | StardewCraft 对应落点 |
| --- | --- | --- |
| `Content/Data/Quests.json` | 任务 `102` 是刘易斯找回 `(O)789` Lucky Purple Shorts。 | quest data / quest manager 后续接入。 |
| `Content/Data/mail.json` | `summer_3_1` 通过邮件发放任务 `102`。 | mail registry/date-trigger mail 后续接入。 |
| `GameLocation.cs` | `LewisBasement` 会刷 `(O)789`，拾取后召唤特殊敌人。 | 地窖短裤点、拾取后召怪。 |
| `Object.cs` | ManorHouse 内使用 `(BC)71` Staircase 是直接 warp，不真正放置楼梯。 | 右键矿井梯子消耗并传送。 |
| `FishingRod.cs` | `(O)789` 可作为 tackle，装上后 bobber style 变成短裤，不消耗。 | `FishingRodItem` 短裤渔具 + hook renderer/mixin。 |
| `Objects.json` | `(O)789` Lucky Purple Shorts，Type Quest，Price 0。 | `ModItems.LUCKY_PURPLE_SHORTS`，类型 label 为 quest。 |

不要复制原版文本；任务描述、邮件正文、物品说明等若要补齐，使用已有本地化策略和项目数据格式重写/迁移。

## 4. StardewCraft 现状

| 系统 | 当前状态 | 规划结论 |
| --- | --- | --- |
| 物品注册 | `ModItems` 里没有 Lucky Purple Shorts。 | 新增手写注册，不靠 `VanillaCategoryItemRegistrar`。 |
| 原版对象数据 | `data/stardewcraft/npc/vanilla/data/Objects.json` 已有 `789` 和 `71` 数据。 | 可作名称、类型、sprite index 的来源，但不能自动生成可用物品。 |
| quest | `LostItem` 当前在 loader 中被简化为 basic。 | 要完整任务需新增/修正 lost item 语义，或将 102 映射为交付任务。 |
| mail | `summer_3_1` 现有内容不是短裤信，且日期白名单未接。 | 需要单独处理兼容策略，不能静默覆盖已有自定义威利信。 |
| tooltip | `SpecialConsumableClientFx` 已有星之果实紫色 palette 和流动文字。 | 直接扩展或新增 `LuckyPurpleShortsClientFx`，保持同一代码风格。 |
| 方块碰撞 | 实用设施用 `ModelVoxelShapeCache.horizontalShapes(...)` 从模型生成 shape。 | 短裤方块应走同类方案，`getShape` 和 `getCollisionShape` 保持一致。 |
| 鱼竿附件 | `FishingRodItem` 已保存 bait/tackle 到 `DataComponents.CUSTOM_DATA`。 | 短裤可作为特殊 tackle；非 damageable，不消耗。 |
| 浮标渲染 | 当前生成 vanilla `FishingHook`，已有多个 `FishingHookRenderer` mixin。 | 短裤浮标不是简单换 item texture，需要 renderer/mixin 覆盖 bobber 绘制。 |
| 用户模型 | `tmp_models/紫色短裤.json/png` 存在。 | 可迁移到正式 assets；需校验 Blockbench JSON 的旋转/负坐标烘焙兼容。 |

## 5. 推荐实现分期

### Phase A：物品、图标、tooltip

目标：先让紫色短裤成为稳定的物品。

- 新增 `stardewcraft:lucky_purple_shorts`。
- 类型使用 `stardewcraft.type.quest`，价格为 0，堆叠按项目 quest item 习惯处理。
- 从原版对象图集中按 `(O)789` 的 sprite index 裁切 `textures/item/lucky_purple_shorts.png`。
- 增加 item model、`en_us` / `zh_cn` 翻译。
- 给 tooltip 加特殊紫色动态边框/背景：复用 `SpecialConsumableClientFx.applyPalette` 思路，颜色可比星之果实更偏深紫/金紫。
- 如果物品类型 label 也要炫酷，新增类似 `stardropTypeLabel` 的 `luckyPurpleShortsTypeLabel`。

验证：

- `./gradlew classes`
- item model/lang JSON parse。
- 游戏内 `/give` 查看图标、名称、类型、tooltip 动态边框。

### Phase B：短裤方块与放置/拾取

目标：让玩家能把短裤当成一个可交互方块。

- 迁移 `tmp_models/紫色短裤.json` 到 `assets/stardewcraft/models/block/...`。
- 迁移 `tmp_models/紫色短裤.png` 到 `assets/stardewcraft/textures/block/...`。
- 新增 `LuckyPurpleShortsBlock`：
  - 水平朝向。
  - `getShape` / `getCollisionShape` 使用 `ModelVoxelShapeCache.horizontalShapes(...)`。
  - 空手右键拾取，给玩家 `Lucky Purple Shorts`，并移除方块。
  - 非空手右键不抢占其他交互，除非后续要加特殊反应。
- 新增 item 的 `Shift + 右键` 放置逻辑：
  - 只有按住 shift 才尝试放置，避免普通右键与鱼竿附件/任务交付冲突。
  - 放置成功后消耗 1 个短裤物品，创造模式不消耗。
  - 普通放置的短裤是世界共享方块，所有玩家都能看到和拾取。

资源风险：

- 当前模型 JSON 包含低于 0 的元素坐标，以及 Blockbench 的 `rotated` 字段。Minecraft 原生 block model 烘焙可能忽略或不支持部分字段。
- 实现时要先测试资源加载。如果模型在 MC 中错位/缺面，优先做兼容转换后的 runtime model，不直接破坏 `tmp_models` 原始文件。

验证：

- `./gradlew classes`
- JSON parse：lang、model、blockstate。
- 游戏内放置、旋转、碰撞、空手拾取、创造模式消耗规则。

### Phase C：玛妮房间任务短裤

目标：实现“每个玩家自己的任务短裤”，不是全服共享实体。

固定点：`-86 34 12`。

可见性规则：

- 玩家已接刘易斯短裤任务 `102`：可见、可交互。
- 玩家未接任务：不可见、不可交互。
- 玩家已在玛妮房间拿过该短裤：不可见、不可交互。
- A 玩家拿走不影响 B 玩家，B 仍可按自己的任务进度看到和拾取。

实现策略：

- 不建议直接在世界里放一个普通 block，因为 Minecraft 方块状态是全局的，无法天然做到 per-player visible/pickup。
- 建议做 `LuckyPurpleShortsWorldPointService`：
  - 服务端维护固定点定义。
  - 客户端按玩家状态显示/隐藏短裤模型，或服务端按玩家发送 fake block / display entity payload。
  - 交互在服务端验证：位置、玩家任务状态、是否已领取。
  - 领取后给物品，并写入玩家个人 flag，例如 `lucky_purple_shorts_marnie_claimed`。
- 如果短期想降低复杂度，可先用普通方块完成单人流程，但这不满足多人要求，不能作为最终方案。

验证：

- 两个玩家分别测试：未接任务不可见，接任务可见，A 拾取不影响 B。
- 拾取后退出重进仍保持个人隐藏。
- 交付刘易斯前后不重复刷物。

### Phase D：任务与邮件接入

目标：让原版“找短裤”任务自然发放和完成。

任务：

- 新增或修正 quest `102`。
- 理想方案：实现 `LostItemQuest`，支持“拾取目标 item 后交给目标 NPC”。
- 快速方案：把 `102` 映射到现有 `ItemDeliveryQuest`，目标 NPC = Lewis，目标 item = `stardewcraft:lucky_purple_shorts`，奖励 750g。
- 交付入口复用 `NpcInteractionService` 当前任务交付优先逻辑。

邮件：

- 原版用 `summer_3_1` 发任务，但项目里这个 id 当前不是短裤信。
- 需要实施前决定：
  - 方案 1：恢复 `summer_3_1` 为刘易斯短裤信，把现有威利信迁移到新 id。
  - 方案 2：新增 StardewCraft 自定义 id，例如 `summer_3_1_lewis_shorts`，日期触发时发这个 id。
- 无论选哪种，必须在 `MailRegistry` 中注册可读 mail；不要向 `mailbox` 塞未知 mail id。
- `StardewTimeManager` 日期白名单要接入对应 mail。

验证：

- 夏 3 早晨收到可读邮件。
- 打开邮件后接受任务 `102`。
- 玛妮短裤只在接任务后出现。
- 把短裤交给 Lewis 后任务完成并给奖励。

### Phase E：刘易斯家矿井梯子入口

目标：复刻原版“不放楼梯，直接进地窖”的彩蛋。

触发条件：

- 玩家位于区域 `x 64..72, y 51..58, z 19..27`。
- 玩家主手或交互手持 `MINE_LADDER` / 对应矿井梯子物品。
- 玩家右键。

行为：

- 取消普通放置逻辑。
- 非创造模式消耗 1 个矿井梯子。
- 传送玩家到 `67 44 20`。
- 设置朝向为南。
- 播放合适的 warp / ladder 音效。
- 初始化本次地窖 visit state：`basementShortsClaimedThisVisit=false`。

实现落点：

- 不建议改 `MineLadderBlock` 的矿井内下降逻辑。
- 优先在 item use / right-click block / block place attempt 的事件层截获，限定在刘易斯家区域。
- 这样不会影响矿井维度正常楼梯行为。

验证：

- 区域内右键会传送并消耗。
- 区域外仍按原矿井梯子逻辑处理。
- 创造模式不消耗。
- 传送后朝南。

### Phase F：刘易斯地窖短裤

目标：所有人都能拿地窖短裤，但每人每次下地窖只能拿一次。

固定点：`81 44 32`。

规则：

- 进入地窖时，本次 visit 的短裤可见。
- 玩家本次 visit 拾取后：
  - 给 1 个 `Lucky Purple Shorts`。
  - 本玩家本次 visit 内该点隐藏。
  - 触发短裤怪物生成。
- 玩家离开地窖并再次进入：刷新本次 visit，可再次看到/拾取。
- 多人情况下，每个玩家按自己的 visit state 计算；不建议全服共享“拿过”。

实现策略：

- 和玛妮房间类似，地窖点也不适合用单一普通方块表达，因为“一次下地窖一次”的状态是 per-player visit state。
- 建议复用 `LuckyPurpleShortsWorldPointService`，增加 point type：`BASEMENT_REPEATABLE_PER_VISIT`。
- visit state 可以存在内存中，按玩家 UUID 记录，离开地窖或断线清理。
- 如果服务端重启，visit state 自然重置是合理的，因为规则本来就是“本次下地窖”。

验证：

- 同一玩家本次下地窖只能拿一次。
- 出去再进后刷新。
- A 拿了不影响 B。
- 拾取后必定触发怪物。

### Phase G：紫色短裤模型怪物与地窖清理

目标：拾取地窖短裤后生成“紫色短裤模型的恼鬼”，但不能穿墙，且生命值等行为尽量与原版特殊敌人一致。

关键风险：

- Minecraft vanilla Vex 默认会穿墙，这和“不能穿墙”冲突。
- 因此最终实现不能简单生成 vanilla Vex 后换模型。

推荐方案：

- 新增自定义实体 `PurpleShortsVexEntity` 或类似名字。
- 视觉上使用紫色短裤模型/贴图。
- AI 上使用普通 pathfinder 追击玩家，不启用 no-clip、不使用穿墙 move control。
- 攻击目标限定为地窖内玩家。
- 生成时打 tag，例如 `stardewcraft:purple_shorts_basement_guard`。
- 实体 tick 中若离开地窖 bounds 或目标离开地窖，尝试回拉/重新寻路；超过容忍距离则移除，避免外泄。

“原版一样”的处理：

- 原版地窖拾短裤生成的是特殊敌人，源码中表现为特殊 id 的敌人逻辑。
- 实现前需要补一次源码核对：生命值、伤害、移动速度、抗性、音效、碰撞大小、掉落、是否 focused on farmers。
- 确认后把数值写入自定义实体属性，不用 Minecraft Vex 默认属性冒充。

地窖清理：

- 服务端 tick 检查地窖 bounds 内是否还有玩家。
- 当地窖 bounds 内玩家数为 0：
  - 移除所有带 `purple_shorts_basement_guard` tag 的短裤怪物。
  - 清理本轮地窖临时追击状态。
- 前提：需要明确地窖 bounds。没有 bounds 前不写死坐标盒。

验证：

- 拾取地窖短裤后生成怪物。
- 怪物追玩家，但不会穿墙。
- 怪物不会离开地窖污染其他区域。
- 所有玩家离开地窖后怪物清空。
- `./gradlew classes`，并手测路径/碰撞。

### Phase H：鱼竿短裤渔具与短裤浮标

目标：玩家能把紫色短裤装进鱼竿渔具槽，并让浮标变成短裤。

附件规则：

- `FishingRodItem.isTackleItem` 加入 `stardewcraft:lucky_purple_shorts`。
- 装入 tackle 槽时只放 1 个短裤。
- 短裤不是 damageable item，不消耗耐久。
- 若两个 tackle 槽都可用，按现有渔具插入规则走，不单独开新规则。

浮标渲染：

- 当前钓鱼系统会生成 vanilla `FishingHook` 来获得原版线和浮标渲染。
- 要把浮标单独变成短裤，需要在 `FishingHookRenderer` mixin 中判断 hook owner 当前使用的鱼竿是否含短裤 tackle。
- 若命中：
  - 保留鱼线渲染。
  - 覆盖或取消 vanilla bobber quad。
  - 在 hook 位置渲染短裤 item billboard 或短裤小模型。
- 第一版建议优先做稳定 item billboard：看起来像短裤浮标，成本低，和鱼线兼容性更高。
- 如果 billboard 效果不够好，再升级为专用小模型渲染。

验证：

- 短裤能装进鱼竿 tackle 槽。
- 鱼竿 tooltip 中显示短裤。
- 抛竿后线仍正常，浮标变短裤。
- 收竿、咬钩动画、bite dip 不被破坏。
- 短裤不会因为钓鱼被消耗。

## 6. 状态模型

| 状态 | 持久性 | 建议存储 | 说明 |
| --- | --- | --- | --- |
| `lucky_purple_shorts_marnie_claimed` | 持久 | `PlayerStardewData` mail flag 或专用 flag | 玩家是否已从玛妮房间拿过任务短裤。 |
| quest `102` accepted/completed | 持久 | 现有 QuestManager | 决定玛妮短裤是否可见、交付是否有效。 |
| basement visit id / claimed this visit | 临时 | 服务端内存 map | 玩家本次下地窖是否已拿过短裤。离开/重新进入后重置。 |
| active purple shorts guards | 临时 | 实体 tag + tick service | 地窖无人时清理。 |
| placed shorts blocks | 世界持久 | 普通 block state | 玩家自己放置的短裤方块，世界共享。 |
| fishing rod tackle shorts | 持久在 ItemStack | `DataComponents.CUSTOM_DATA` | 复用现有鱼竿附件保存格式。 |

## 7. 多人语义

- 玛妮房间短裤：严格 per-player，不共享。
- 地窖短裤：per-player per-visit，不共享。
- 玩家手动放置的短裤方块：世界共享，谁都能看到和拾取。
- 短裤怪物：世界实体，但生成/清理限定在地窖；目标优先追触发玩家，也可攻击地窖内其他玩家，取决于原版源码核对结果。
- 任务交付：按玩家自己的 quest 状态完成。

## 8. 资源清单

| 资源 | 来源 | 目标 |
| --- | --- | --- |
| 物品图标 | 原版 `(O)789` sprite | `assets/stardewcraft/textures/item/lucky_purple_shorts.png` |
| 方块模型 | `tmp_models/紫色短裤.json` | `assets/stardewcraft/models/block/utility/lucky_purple_shorts.json` 或更合适路径 |
| 方块贴图 | `tmp_models/紫色短裤.png` | `assets/stardewcraft/textures/block/utility/lucky_purple_shorts.png` |
| blockstate | 新建 | 支持水平朝向 |
| item model | 新建 | 物品栏显示原版图标；放置物品仍使用短裤物品图标 |
| 实体模型 | 可先复用方块模型或转换为 entity model | 用于地窖短裤怪物 |
| lang | 新建/补充 | 中英名称、类型、提示文本 |

## 9. 不建议第一版做的内容

- 不先做 trimmed lucky purple shorts `(O)71` / wearable pants `(P)15`，除非后续专门做裁缝和服装系统。
- 不先做节日里穿短裤触发刘易斯反应，避免扩大 NPC/节日对话范围。
- 不静默覆盖现有 `summer_3_1` 威利信；邮件兼容策略要单独确认。
- 不用 vanilla Vex 默认 AI 直接冒充，因为它会穿墙。
- 不把玛妮房间短裤做成全局普通方块，否则多人进度会错。

## 10. 验证总表

每个阶段至少跑：

- `./gradlew classes`
- 如果改资源：批量 JSON parse `assets` lang/models/blockstates。

关键手测：

1. `/give` 紫色短裤，确认图标、tooltip、任务物品类型。
2. `Shift + 右键` 放置短裤，碰撞贴合模型，空手右键拾取。
3. 玛妮房间：未接任务不可见，接任务可见，拾取后仅自己隐藏。
4. 刘易斯家区域：矿井梯子右键消耗并传送到 `67 44 20`，朝南。
5. 地窖：每次进入可拿一次，拿后生成怪物，出地窖再进刷新。
6. 地窖无人：短裤怪物全部清理。
7. 鱼竿：短裤可装 tackle，浮标变短裤，钓鱼后短裤不消耗。
8. 多人：A 的玛妮/地窖短裤状态不影响 B。

## 11. 实施顺序建议

最稳的顺序是：

1. 物品 + tooltip + 图标。
2. 普通放置方块 + 空手拾取 + 模型碰撞。
3. 刘易斯家楼梯入口。
4. 地窖 per-visit 短裤点。
5. 地窖短裤怪物和清理。
6. 玛妮房间 per-player 任务短裤点。
7. 任务 `102` 和邮件。
8. 鱼竿 tackle。
9. 短裤浮标 renderer/mixin。

理由：先把可复用的物品/方块/传送/短裤点服务做稳，再接任务和浮标。浮标渲染最容易被 vanilla renderer 和已有 mixin 牵制，适合放后面单独验收。

## 12. 实现前待确认 / 待核对

| 项 | 为什么需要确认 |
| --- | --- |
| `lewis_basement` bounds | 清理怪物、判断玩家离开、本次 visit reset 都需要明确区域。 |
| 邮件 `summer_3_1` 兼容策略 | 项目里该 id 现在不是短裤信，不能静默覆盖。 |
| 原版特殊敌人数值 | 用户要求生命值等“全部都和原版一样”，实现前要补源码核对并落成属性表。 |
| 短裤模型烘焙结果 | 当前模型含负坐标和 Blockbench 扩展字段，需确认 MC 资源加载效果。 |
| 浮标呈现等级 | 第一版 item billboard 还是直接做专用短裤小模型，需要看实际视觉效果取舍。 |
