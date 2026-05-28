# Changelog

## 0.4.5 - 2026-05-28

### Update Log (English)

#### Headline Features

- Added the first playable Luau implementation for Summer 11, including beach festival entry, active festival session handling, time freeze behavior, festival music, Pierre's festival shop, and end-of-event return flow.
- Added the Luau potluck soup system with held-item contribution prompts, Stardew-style ingredient validation and scoring, multiplayer contribution tracking, Governor reaction branches, and final friendship/reaction feedback.
- Added the Luau main event cutscene flow with Lewis, Marnie, the Governor, festival crowd actors, camera movement, reaction-specific dialogue, music transitions, and event cleanup.

#### Festival Content and Presentation

- Added the Luau beach overlay, soup pot, torches, speaker, totem decor, Governor NPC assets, event music resources, block/item models, textures, blockstates, and localization.
- Added protected Luau map replacement behavior so festival decor is applied and restored without dropping replaced blocks or allowing the unique soup pot to be broken or picked up.
- Added Luau NPC participation, dialogue routing, festival shop registration, Governor display name support, and cutscene actor placement polish.

#### Stability and Shared Festival Fixes

- Refactored active festival confirmations into shared state used by Egg Festival, Flower Dance, and Luau so entry, exit, and start votes behave consistently across active festivals.
- Fixed active festival entry timing and same-day terminal states so players are not offered setup prompts after the venue is actually open or already finished.
- Added festival-day no-rain behavior based on Stardew Valley source behavior and hardened Luau soup interactions so adding food to the pot no longer also starts eating the held item.

#### Changes

- Updated the public mod version to `0.4.5`.

### 更新日志（中文）

#### 重点内容

- 加入第一轮可玩的夏威夷宴会实现，覆盖夏 11 海滩入场、主动节日会话、时间冻结、节日音乐、皮埃尔节日商店，以及节日结束后的回家流程。
- 加入夏威夷宴会百乐餐大锅系统，包括手持物投汤确认、星露谷风格食材判定与评分、多人投料记录、州长反应分支，以及最终好感/结果反馈。
- 加入夏威夷宴会主事件 cutscene 流程，包括刘易斯、玛妮、州长、节日人群演员、镜头移动、分支对白、音乐切换和事件清理。

#### 节日内容与表现

- 加入夏威夷宴会海滩 overlay、大锅、火炬、音响、图腾装饰、州长 NPC 资源、事件音乐、方块/物品模型、贴图、方块状态和本地化。
- 加入受保护的 Luau 地图替换流程，让节日装饰应用和恢复时不会掉落被替换方块，并保证全服唯一的大锅不可破坏、不可拾取。
- 加入 Luau NPC 参与、对白路由、节日商店注册、州长显示名，以及 cutscene 演员站位打磨。

#### 稳定性与通用节日修复

- 将主动节日确认状态抽成通用逻辑，复活节、花舞节和夏威夷宴会共用入场、离场和开始投票状态，减少各节日行为漂移。
- 修复主动节日入场时间和当日终止状态处理，避免会场已经开放或结束时仍弹出错误的搭建中提示。
- 按星露谷源码行为加入节日当天无雨处理，并加固 Luau 投汤交互，避免向汤里投食物时同时触发吃掉手持物。

#### 变更

- 项目公开版本号更新为 `0.4.5`。

## 0.4.4 - 2026-05-28

### Update Log (English)

#### Headline Features

- Added the first playable Trout Derby implementation for the Summer 20-21 passive festival window, including Forest overlay support, Willy schedule/interaction handling, Golden Tag content, Rainbow Trout catch rolls, treasure integration, and booth reward exchange.
- Added Trout Derby presentation support with the project-owned Forest schematic overlay, synchronized item display entities, debug-safe apply/restore behavior, and no-drop bulk block replacement during overlay swaps.
- Added the Lucky Purple Shorts feature pass, including the quest item, special presentation hooks, placeable shorts block, Lewis/Marnie interaction paths, basement handling, and the purple-shorts fishing bobber renderer path.

#### Farming, UI, and Integration

- Added rice shoot crop content and related crop tags, models, textures, and item registration.
- Improved fertilizer client synchronization and Jade integration so crop/farmland fertilizer state can be inspected more consistently.
- Updated NPC model and animation resources touched by the current presentation pass.

#### Festival and Stability Fixes

- Hardened passive festival overlay lifecycle callbacks so passive handlers can respond when overlays start applying, finish applying, start restoring, and finish restoring.
- Fixed Trout Derby Golden Tag eligibility so debug/forced passive festival sessions use the passive festival open state instead of being blocked by the in-game calendar date alone.
- Added missing Jade config localization keys and fixed several festival/debug interaction paths touched by Flower Dance, Desert Festival, and Trout Derby work.

#### Changes

- Updated the public mod version to `0.4.4`.

### 更新日志（中文）

#### 重点内容

- 加入第一轮可玩的鳟鱼大赛实现，覆盖夏 20-21 被动节日窗口、森林 overlay、威利日程与交互、黄金标签物品、虹鳟鱼捕获掉落、宝箱整合，以及摊位兑换奖励。
- 加入鳟鱼大赛表现层支持，包括项目内正式保存的森林 schematic overlay、三个展示实体同步安装/清理、调试 apply/restore 兼容，以及 overlay 批量替换时禁止掉落物品。
- 加入刘易斯紫色短裤功能批次，包括任务物品、特殊表现钩子、可放置短裤方块、刘易斯/玛妮相关交互、地窖处理，以及紫色短裤鱼漂渲染路径。

#### 农业、界面与集成

- 加入稻苗作物内容，以及对应作物标签、模型、贴图和物品注册。
- 改进肥料客户端同步与 Jade 集成，让作物/耕地肥料状态能更稳定地被查看。
- 更新本轮表现层工作触及的 NPC 模型和动画资源。

#### 节日与稳定性修复

- 加固被动节日 overlay 生命周期回调，让 passive handler 能响应 overlay 开始应用、应用完成、开始恢复和恢复完成。
- 修复鳟鱼大赛黄金标签判定：调试/强制开启的被动节日会使用节日 open 状态，不再被游戏内日期单独挡掉。
- 补齐 Jade 配置本地化键，并修复花舞节、沙漠节和鳟鱼大赛本轮触及的若干节日/调试交互路径。

#### 变更

- 项目公开版本号更新为 `0.4.4`。

## 0.4.3 - 2026-05-26

### Update Log (English)

#### Headline Features

- Added the first active Flower Dance implementation pass for Spring 24, including festival entry, time freeze behavior, HUD hiding, the Forest-FlowerFestival overlay, festival music, Pierre's event shop, and end-of-event return handling.
- Added Flower Dance NPC participation systems with confirmed venue positions, festival dialogue routing, dance partner invitations, friendship threshold checks, successful invitation friendship rewards, player-player dance invitations, and occupied-partner rejection handling.
- Added the first Flower Dance main dance cutscene flow, including Lewis start confirmation, selected dance partners, client-side dancer/audience actors, camera setup, hidden real-NPC suppression, and festival-specific cutscene assets.

#### Festival Architecture

- Added a shared active festival handler layer so active festivals can centralize entry, main-event state, NPC interaction locks, Pierre festival shops, and future free-stage exit behavior.
- Added festival NPC control hooks so central NPC movement yields to active festival actors instead of overwriting festival positions and routes.
- Expanded festival network payload coverage for Flower Dance NPC invites, player invites, player ask prompts, and cutscene synchronization.

#### Content, Polish, and Planning

- Added Flower Dance decor blocks, block entities, models, textures, structure data, music resources, and the main cutscene event definition.
- Improved Egg Festival runtime support touched by the shared active-festival flow, including festival lifecycle handling and interaction consistency.
- Added and updated planning/source-ledger documents for Flower Dance, active festival architecture, festival requirements, story/event migration, mastery, prize ticket, and related implementation tracks.

#### Changes

- Updated the public mod version to `0.4.3`.

### 更新日志（中文）

#### 重点内容

- 加入第一轮主动花舞节实现，覆盖春 24 入场、时间冻结、HUD 隐藏、`Forest-FlowerFestival` 会场覆盖、节日音乐、皮埃尔节日商店，以及节日结束后的回家流程。
- 加入花舞节 NPC 参与系统，包括已确认会场站位、节日对白路由、NPC 舞伴邀请、好感阈值判断、邀请成功后的好感奖励、玩家互邀，以及舞伴已被占用时的拒绝处理。
- 加入花舞节主舞 cutscene 首版流程，包括刘易斯开始确认、已选择舞伴入场、客户端舞者/观众演员、镜头设置、真实 NPC 隐藏与阴影抑制，以及花舞节专用 cutscene 资源。

#### 节日架构

- 新增主动节日通用 handler 层，用于集中处理节日入场、主事件状态、NPC 交互锁、皮埃尔节日商店，以及后续自由阶段离场逻辑。
- 新增节日 NPC 控制钩子，让中央 NPC 移动系统在主动节日期间让位给节日演员，避免覆盖节日站位和路线。
- 扩展花舞节网络包，支持 NPC 邀舞、玩家互邀、玩家邀请确认和 cutscene 状态同步。

#### 内容、打磨与规划

- 新增花舞节装饰方块、方块实体、模型、贴图、结构数据、音乐资源和主舞 cutscene 事件定义。
- 改进被主动节日流程触及的复活节运行逻辑，包括节日生命周期和交互一致性。
- 新增并更新花舞节、主动节日架构、节日需求、剧情/事件迁移、精通、兑奖券等实现规划和源码对照文档。

#### 变更

- 项目公开版本号更新为 `0.4.3`。

## 0.4.2 - 2026-05-25

### Update Log (English)

#### Headline Features

- Added the first broad Desert Festival implementation pass for the Spring 15-17 festival window, including passive festival activation, desert map makeover behavior, Calico Egg economy support, NPC visit handling, and festival-specific shops/interactions.
- Added Desert Festival race systems and Stardew-style race UI flow, including room lists, live race screens, snapshot/watch views, single-bet handling, state synchronization, and festival race network payloads.
- Added Desert Festival Skull Cavern support with Calico Egg stone content, mine HUD synchronization, Marlon challenge/rating menus, challenge progress persistence, egg reward handling, and festival-specific pass-out penalties.

#### Desert Festival Content

- Added Desert Festival vendor coverage, including the Calico Egg shop, rotating villager vendor shops, Shady Guy UI, festival food/dish registration, desert cook dish items, and related item models/localization.
- Added new festival items and blocks such as Calico Eggs, Calico Egg stones, Calico statues, star plaques, prize tickets, the prize ticket machine, and desert festival reward/utility models.
- Added festival-specific NPC and route data used by the desert venue, along with portal trigger/Jade support for inspecting location triggers during map interaction work.

#### Gameplay and Stability Fixes

- Reworked farm join approval so farm owners receive an in-game confirmation dialog instead of relying on permission-sensitive chat commands, with queued handling for multiple pending requests.
- Fixed flower crop growth/Jade behavior by separating decorative placed flowers from planted crops, so planted flowers can grow and show progress while decorative mature flowers stay static.
- Tuned bomb mining behavior for the 3D Minecraft environment, including smaller effective bomb ranges, reduced bomb-ladder odds from destroyed rocks, and a higher normal-bomb crafting cost.
- Improved mine ladder highlighting, mine exit/menu behavior, NPC leash protection, Joja Community Center lock messaging, lightning rod model presentation, Bookseller trade direction, and several multiplayer/client presentation paths touched during this pass.

#### Changes

- Updated the public mod version to `0.4.2`.

### 更新日志（中文）

#### 重点内容

- 加入第一轮较完整的沙漠节实现，覆盖春 15-17 的被动节日开启、沙漠地图改造、印花蛋经济、NPC 到访处理，以及节日期间专用商店与交互。
- 加入沙漠节赛跑系统和星露谷风格赛跑 UI 流程，包括房间列表、实时比赛界面、快照/观赛界面、单次下注、状态同步和对应网络包。
- 加入沙漠节骷髅洞支持，包括印花蛋石头、矿洞 HUD 同步、马龙挑战/评分菜单、挑战进度持久化、印花蛋奖励和节日期间昏倒惩罚。

#### 沙漠节内容

- 补充沙漠节商店覆盖：印花蛋商店、轮换村民摊位、神秘商人界面、节日料理/菜品注册、沙漠节料理物品，以及相关物品模型与本地化。
- 新增印花蛋、印花蛋石头、印花雕像、星星牌、兑奖券、兑奖机等节日物品/方块和奖励/功能模型。
- 加入沙漠会场所需的 NPC 与路线数据，并补充传送触发器/Jade 支持，方便地图交互调试和位置触发查看。

#### 玩法与稳定性修复

- 将加入农场审批改为农场主收到游戏内确认弹窗，不再依赖受权限组影响的聊天命令，并支持多个待处理申请排队显示。
- 修复花类作物生长和 Jade 信息：区分“装饰用成熟花”和“播种后的花苗”，让花苗能正常生长并显示进度，装饰花保持静态。
- 针对 Minecraft 3D 环境调整炸弹挖矿：缩小有效爆炸半径、降低炸掉石头出梯子的概率，并上调普通炸弹制作成本。
- 改进矿井梯子高亮、矿井离开/菜单行为、NPC 防拴绳、Joja 路线社区中心上锁提示、避雷针模型显示、书摊兑换方向，以及本轮触及的若干联机和客户端表现路径。

#### 变更

- 项目公开版本号更新为 `0.4.2`。

## 0.4.1 - 2026-05-23

### Update Log (English)

#### Headline Features

- Added the first full book-system integration pass, including Stardew book definitions, book items, reading flow, persistent read stats, repeat-read XP rules, and the project-specific Animal Catalogue behavior.
- Added the Bookseller NPC at the Stardew Valley town location with scheduled appearance days, buy/trade menus, no-portrait shop presentation, morning in-town notice, and fixed-position spawning.
- Added a Stardew-style reading presentation using Minecraft's enchanting-table book model as the 3D page-turning carrier, with imported `book_read` audio and rainbow star finish effects.

#### Book Effects and Sources

- Hooked the retained permanent book powers into gameplay, including movement, horse speed, bomb damage reduction, defense, trash-can odds, artifact value, Marlon recovery cost, wild seeds, woodcutting, diamond drops, crab pots, roe treasure, friendship gains, void monster drops, mystery-box odds, and grass slowdown behavior.
- Added shop and acquisition coverage for the currently available systems: Marnie Animal Catalogue, Dwarf Bombs book, Bookseller stock/trades, tree chopping, fishing treasure, monster drops, artifact spots, and Mystery Box book drops.
- Updated Bookseller stock handling so main shop entries are daily per-player limited while trade entries remain repeatable; mapped currently missing trade items to available project items where requested.

#### Polish, Fixes, and Planning

- Fixed book reading settlement so right-click reading now completes effects/consumption instead of only playing the visual, and applied the same timed reading path to the Dwarvish Translation Guide.
- Restored the default Stardew item tooltip price and stacked total-price lines independently of the removed Price Catalogue book.
- Fixed Bookseller shop portrait behavior and cutscene return-position handling touched during the book-system pass.
- Added detailed book-system planning and source-ledger documents, including TODO notes for deferred Golden Walnuts, gift boxes, Prize Ticket, Raccoon, SquidFest, VolcanoShop, DesertFestival, and formal Well Read advancement work.

#### Changes

- Updated the public mod version to `0.4.1`.

### 更新日志（中文）

#### 重点内容

- 加入第一轮完整书籍系统接入，包括星露谷书籍定义、书籍物品、阅读流程、已读统计持久化、重复阅读经验规则，以及本项目专属的动物目录书行为。
- 加入固定位置书摊老板 NPC，支持按季节日历出没、买书/换书菜单、无头像商店展示、早晨到访提示和固定点生成。
- 加入星露谷风格读书表现：使用 Minecraft 附魔台书模型作为 3D 翻页载体，并接入原版 `book_read` 音效和彩虹爆星收尾特效。

#### 书本效果与来源

- 将保留的永久书本能力接入实际玩法，包括移速、骑马速度、炸弹减伤、防御、垃圾桶概率、古物售价、马龙找回费用、野生种子、伐木、钻石掉落、蟹笼、鱼籽宝箱、友谊增长、虚空怪物掉落、神秘盒概率和草地减速行为。
- 补齐当前已有系统能承载的来源：玛妮动物目录、矮人炸弹书、书摊库存/兑换、砍树、钓鱼宝箱、怪物掉落、远古斑点和神秘盒书本掉落。
- 调整书摊库存逻辑：主商店条目按每日每玩家限购，兑换商店保持可重复兑换；按本项目现有物品对缺失兑换物做映射。

#### 打磨、修复与规划

- 修复书籍右键阅读只播放动画不结算的问题，现在会正确触发效果与消耗；矮人语手册也复用同一套定时阅读结算路径。
- 恢复项目默认的星露谷物品售价与堆叠总价 tooltip，不再受已移除的价格目录书影响。
- 修复书摊商店误显示头像的问题，并修正本轮书籍接入过程中触及的 cutscene 结束回原位逻辑。
- 新增书籍系统详细规划与源码锁定表，记录后续 TODO：Golden Walnuts 条件、一次性礼盒、兑奖券、浣熊、鱿鱼节、火山商店、沙漠节和正式 Well Read advancement。

#### 变更

- 项目公开版本号更新为 `0.4.1`。

## 0.4.0 - 2026-05-21

### Update Log (English)

#### Headline Features

- Added the first active-festival implementation, centered on the Spring 13 Egg Festival with map overlay activation, Pierre's festival shop, festival entry/exit handling, NPC venue takeover, main-event cutscenes, egg hunt scoring, winner resolution, and return-home flow.
- Integrated Egg Festival story flow with the existing cutscene/event runtime instead of using a separate festival-only timeline, including fade timing, actor staging, camera control, spectator handling, multiplayer actor placement, and dynamic winner dialogue.
- Added multiplayer-aware Egg Festival actor presentation so cutscene player actors can use sorted participant slots, real player UUIDs, player skins, slim/wide model selection, and copied equipment.

#### Festival, Time, and Map Behavior

- Added festival map overlay state handling for the Egg Festival venue and synchronized NPC runtime behavior with the overlay lifecycle.
- Added Egg Festival time-freeze behavior so the venue holds the festival sky/time at 9:00 while participants are inside, then advances to the festival end time when the event finishes.
- Fixed Egg Festival entry checks so the venue opens from the real 9:00 to 14:00 festival window and does not stay blocked by a stale same-day closed session during testing.
- Restored the original setup/start messaging path for Spring 13, including the setup warning before 9:00 and the festival-start broadcast at 9:00.

#### NPCs, Cutscenes, and Interaction Polish

- Added Egg Festival NPC actor control for free-stage placement, main-event lineup, hunt-stage behavior, award-stage staging, temporary non-contestant removal, and spawn suppression during the contest.
- Added Lewis start-contest confirmation so clicking Lewis prompts the original ready question before the main Egg Festival cutscene starts.
- Matched the original NPC fallback winner branch: when no player reaches the egg threshold, Abigail wins, Vincent reacts, Abigail walks up for the prize, and then returns to the lineup.
- Improved cutscene player/NPC visuals around black fades and award movement so real-stage transitions do not leak on screen and award winners face the expected direction.

#### UI, Audio, Rewards, and Localization

- Added Egg Festival HUD, actionbar timer/count display, scoreboard scoring, whistle/timer/coin/music feedback, and localized festival dialogue and system messages.
- Added temporary player-facing prize text noting that prize items are not hooked up in this version yet.
- Expanded English and Simplified Chinese localization for the new festival flow, including setup, entry, hunt, result, and award text.

#### Changes

- Updated the public mod version to `0.4.0`.

### 更新日志（中文）

#### 重点内容

- 加入第一版主动节日实现，核心为春 13 蛋蛋节：包含节日地图 overlay、皮埃尔节日商店、进入/离开处理、NPC 会场接管、主事件剧情、寻蛋计分、胜者判定和结束回家流程。
- 蛋蛋节剧情流程已接入现有 cutscene/event 运行时，不再使用独立节日时间轴；支持黑屏时机、演员站位、镜头控制、旁观者处理、多人演员排位和动态获胜对白。
- 多人 cutscene 玩家演员现在会按排序后的参赛槽位同步真实玩家 UUID，并使用对应玩家皮肤、粗/细手臂模型和装备外观。

#### 节日、时间与地图行为

- 加入蛋蛋节会场地图 overlay 状态处理，并让 NPC 运行时随 overlay 生命周期进入或恢复节日状态。
- 加入蛋蛋节时间冻结：玩家在会场内时，节日天空/时间固定在 9:00；节日结束后推进到原版结束时间。
- 修复蛋蛋节入口判断：会场现在按真实 9:00 到 14:00 时间窗开放，不会在同日测试时被旧的 closed session 一直挡在“布置中”。
- 补回春 13 原版风格的布置中提示与 9:00 节日开始广播。

#### NPC、剧情与交互打磨

- 加入蛋蛋节 NPC actor 控制，包括自由阶段站位、主事件队列、寻蛋阶段行为、领奖阶段站位、非参赛 NPC 临时移除和比赛期间生成抑制。
- 点击刘易斯开始比赛时现在会先弹出原版 ready 确认，不再一点击就直接进入主剧情。
- 对齐原版 NPC fallback 获胜分支：玩家未达到彩蛋阈值时由阿比盖尔获胜，文森特做朝向反应，阿比盖尔上前领奖后返回队列。
- 修正 cutscene 黑屏前后的真实站位切换和领奖走向，避免穿帮，并让领奖者朝向符合预期。

#### UI、音频、奖励与本地化

- 加入蛋蛋节 HUD、actionbar 计时/彩蛋数、scoreboard 计分、哨声/倒计时/金币/音乐反馈，以及节日对白和系统提示。
- 玩家获胜领奖对白暂时标注“本版本暂时未接入奖励”，避免误以为已经发放草帽或兑奖券。
- 扩展蛋蛋节相关英文和简体中文文本，包括布置、进入、寻蛋、结果和领奖内容。

#### 变更

- 项目公开版本号更新为 `0.4.0`。

## 0.3.10-fix1 - 2026-05-20

### Update Log (English)

#### Fixes

- Fixed Stardew Valley pregen upgrades by synchronizing the region manifest with the bundled region files and bumping the pregen version, allowing existing saves to reinstall the updated map data.
- Added Secret Woods access handling for existing saves, including per-player unlocked entrance visibility and collision behavior after clearing the hollow log.
- Reworked the locked Secret Woods boundary so blocked players are pushed back with an actionbar warning instead of being repeatedly teleported and camera-locked.
- Restored reliable Secret Woods resource-clump chopping by deferring custom clump removal until after the canceled vanilla break event finishes processing.
- Changed Secret Woods slime refreshing to run lazily when an unlocked player actually enters the loaded Secret Woods area, instead of trying to spawn entities during farm wake-up while the area may be unloaded.

#### Changes

- Updated the public mod version to `0.3.10-fix1`.

### 更新日志（中文）

#### 修复

- 修复星露谷预生成地图升级：同步 region manifest 与实际打包的 region 文件，并提升 pregen 版本，让老存档能重新安装更新后的地图数据。
- 补上秘密森林入口的老档兼容逻辑，包括砍开空心木桩后的玩家独立入口可见性与碰撞状态。
- 重做未解锁秘密森林时的边界阻挡：现在只会 actionbar 提示并把玩家推回入口外，不再反复传送导致视角/移动被锁住。
- 修复秘密森林资源簇砍伐不稳定的问题：自定义资源簇移除延后一 tick 执行，避免被取消的原版破坏事件同步复原。
- 调整秘密森林史莱姆刷新时机：改为已解锁玩家实际进入已加载的秘密森林区域时按天懒刷新，不再在农场睡醒结算时尝试生成远处实体。

#### 变更

- 项目公开版本号更新为 `0.3.10-fix1`。

## 0.3.10 - 2026-05-19

### Update Log (English)

#### Headline Features

- Added the first mastery-system pass, including mastery data, rewards, menu entry points, mastery blocks, mastery statues, and supporting sync payloads.
- Added late-game forge and equipment foundations, including the mini forge, anvil/heavy furnace block entities, weapon forge data, combined rings, trinket items, and enchantment guard logic.
- Added new companion/trinket presentation work for prismatic butterflies, fairy companions, frog/parrot/fairy-style trinkets, Galaxy Soul handling, and related client effects.

#### Combat, Tools, and Player Progression

- Expanded weapon stats, tooltips, cooldown handling, combat events, equipment sync, ring effects, and Curios integration paths.
- Improved hoe, watering can, scythe, axe, pickaxe, pan, fishing rod, mining, forage, artifact spot, and skull-cavern reward behavior touched by the progression update.
- Added or refined player-data fields and sync coverage for mastery, equipment, trinkets, forging, progression hints, and related UI state.

#### Furniture, Models, and Interaction Polish

- Rebuilt oak, spruce, and birch table models around the lower 14/16-block tabletop height, with full per-connection top-edge textures instead of rotated texture reuse.
- Adjusted table display-item placement, table collision/selection shapes, tablecloth height, and table leg/top model composition to match the new table geometry.
- Replaced oak, spruce, and birch chair models and textures, and raised their sitting height to 9/16-block so seating aligns with the updated models.
- Added or updated furniture, mastery, forge, statue, forage grape, and item model resources used by the new release content.

#### UI, Menus, Audio, and Assets

- Expanded common GUI texture helpers and continued Stardew-style menu scaling/asset normalization across gameplay screens and tooltip components.
- Added mini-forge and mastery-related client screens, renderers, item models, GUI resources, and localized text.
- Added new sound assets and sound registrations for forge, mastery, combat, tool, and utility feedback.

#### Fixes and Behavior Cleanup

- Improved cutscene locking/tracking, NPC spawn/runtime handling, mail behavior, mine drops/spawns, farm/interior protection, pass-out flow, and time/event bookkeeping touched by the release pass.
- Refined crop, bush, flower placement, sunflower, forage, berry, hot-spring visual, desert artifact spot, and utility block behaviors.
- Updated the public mod version to `0.3.10`.

### 更新日志（中文）

#### 重点内容

- 加入第一轮精通系统，包括精通数据、奖励、菜单入口、精通方块、精通雕像以及对应的同步网络包。
- 加入后期锻造与装备系统基础，包括迷你锻造台、铁砧/重型熔炉方块实体、武器锻造数据、合成戒指、饰品物品和附魔保护逻辑。
- 补充棱彩蝴蝶、仙灵伙伴、青蛙/鹦鹉/仙灵类饰品、银河之魂以及相关客户端特效的表现基础。

#### 战斗、工具与玩家成长

- 扩展武器属性、tooltip、冷却、战斗事件、装备同步、戒指效果和 Curios 兼容路径。
- 改进锄头、喷壶、镰刀、斧头、镐子、淘盘、钓竿、采矿、采集物、蚯蚓点和骷髅洞奖励等与成长线相关的行为。
- 补充玩家数据字段与同步范围，用于精通、装备、饰品、锻造、进度提示和相关 UI 状态。

#### 家具、模型与交互打磨

- 重做橡木、杉木、桦木桌模型，使桌面高度统一为 14/16 格，并为所有连接形态接入独立顶面边缘贴图，不再旋转复用材质。
- 调整桌上物品显示高度、桌子碰撞/选择形状、桌布高度以及桌腿/桌面模型组合，使其匹配新的桌子几何。
- 替换橡木椅、杉木椅、桦木椅模型和贴图，并将坐下高度提高到 9/16 格，让坐姿贴合新模型。
- 新增或更新本次内容需要的家具、精通、锻造、雕像、野葡萄和物品模型资源。

#### UI、菜单、音频与资源

- 扩展通用 GUI 贴图 helper，并继续推进星露谷风格菜单缩放和资源规范化。
- 加入迷你锻造、精通相关客户端界面、渲染器、物品模型、GUI 资源和本地化文本。
- 新增锻造、精通、战斗、工具和通用反馈所需的声音资源与声音注册。

#### 修复与行为清理

- 改进剧情锁定/追踪、NPC 生成与运行时、邮件、矿井掉落/生成、农场与室内保护、昏倒流程和时间/事件记录等路径。
- 调整作物、灌木、花卉放置、向日葵、采集物、浆果、温泉视觉、沙漠蚯蚓点和工具方块行为。
- 项目公开版本号更新为 `0.3.10`。

## 0.3.9fix2 - 2026-05-17

### Update Log (English)

#### Fixes

- Tightened Stardew Valley social-page parity for NPCs, including Krobus visibility, unknown-name display, gift eligibility, and Introductions quest filtering.
- Added the original Krobus mugshot crop to the social UI and wired the same mugshot into the Xaero Minimap NPC icon set.
- Corrected Marnie's animal-shop counter route point and made NPCs finish walking naturally to route-point centers instead of relying on a final snap.
- Prevented ore-pan sparkle points and fish splash bubbles from spawning in hot spring, farm, and sewer areas, and cleaned up existing invalid water-feature points when encountered.
- Cleared remaining workspace diagnostics by aligning Bush block-entity non-null annotations, removing unused spawn helper overloads, and normalizing a renderer whitespace issue.

#### Changes

- Updated the public mod version to `0.3.9fix2`.

### 更新日志（中文）

#### 修复

- 进一步对齐星露谷原版社交界面逻辑，包括 Krobus 显示、未认识时的 `???`、送礼资格和打招呼任务过滤。
- 添加从原版裁出的 Krobus mugshot，并将同一份头像接入 Xaero Minimap 的 NPC 图标适配。
- 修正 Marnie 动物商店柜台路线点，并让 NPC 在路线终点自然走到方块中心，不再依赖最终吸附。
- 限制淘金闪光点和钓鱼气泡的生成区域，温泉、农场和下水道区域不再生成，并会清理已存在的非法水面点。
- 清理剩余工作区诊断：补齐 Bush 方块实体非空标注、删除未使用的生成 helper 重载，并规范一个渲染器文件的空白问题。

#### 变更

- 项目公开版本号更新为 `0.3.9fix2`。

## 0.3.9fix1 - 2026-05-17

### Update Log (English)

#### Fixes

- Raised the overworld wizard tower structure template by one block so newly generated towers no longer sink one block into the ground.
- Stopped Joja Mart NPC maintenance from running while no players are in the Stardew Valley dimension, preventing repeated Morris and cashier fresh-spawn loops while players are in the Overworld.
- Stopped Joja's maintenance tick from redundantly forcing camel merchant and traveling cart checks; those static merchants now also skip their own timed checks when Stardew Valley has no players.
- Disabled NPC movement debug snapshot/log work by default behind the `stardewcraft.npcMovementDebug` system property, removing hot-path debug overhead during normal play.

#### Changes

- Bumped `StardewValleyPrebuiltRegionInstaller.CURRENT_PREGEN_VERSION` to `5` so existing saves reinstall the updated pregen layout.
- Updated the public mod version to `0.3.9fix1`.
- Reduced NPC runtime bookkeeping by caching implemented movement entries and refreshing runtime/pathing metadata only when NPC capability data changes.
- Improved NPC path evaluation so NPCs avoid tall or cross-cell decor collisions such as Joja Mart shelves instead of treating those blocks as walkable space.

### 更新日志（中文）

#### 修复

- 将主世界法师塔结构模板整体抬高一格，新生成的法师塔不再陷进地里一格。
- Joja 超市 NPC 巡检现在只会在星露谷维度有玩家时运行，避免玩家在主世界时 Morris 和收银员反复 fresh-spawn 并拖慢服务器。
- 移除 Joja tick 对骆驼商人和旅行货车的重复强制巡检；这两个静态商人自己的定时巡检也会在星露谷无人时跳过。
- NPC 移动调试快照和日志默认关闭，仅在设置 `stardewcraft.npcMovementDebug` 时启用，减少正常游玩时的热路径开销。

#### 变更

- 将 `StardewValleyPrebuiltRegionInstaller.CURRENT_PREGEN_VERSION` 提升到 `5`，让旧存档重新安装更新后的 pregen 地图布局。
- 项目公开版本号更新为 `0.3.9fix1`。
- 缓存 NPC movement entry，并让 runtime/pathing 元数据只在 NPC capability 数据变化时刷新，减少每 tick 重复 bookkeeping。
- 改进 NPC 寻路节点判定，让 NPC 避开超市货架这类高碰撞或跨格装饰方块，不再把它们当成可走空间。

## 0.3.9 - 2026-05-17

### Update Log (English)

#### Headline Features

- Added the Stardew Valley 1.6-style Powers wallet page to the V-menu, following the active `PowersTab` / `Powers.json` layout model instead of the older unused skills-page wallet path.
- Added standalone wallet/power icon assets for Forest Magic, Dwarvish Translation Guide, Rusty Key, Club Card, Special Charm, Skull Key, Magnifying Glass, Dark Talisman, Magic Ink, Bear Paw, Spring Onion Mastery, and Key to the Town.
- Added client sync for `SpecialItems`, so wallet-style permanent unlocks can be displayed from authoritative player data instead of being inferred only from mail flags.
- Promoted the Skull Key and Dwarvish Translation Guide toward true Stardew-style special items: permanent, not sellable, not consumed, synced to player special-item data, and visible in the powers page.
- Added full item-tooltip rendering for item-backed powers in the V-menu, so special items show their real Minecraft/StardewCraft tooltip instead of a simplified hand-written hover label.

#### Wallet, Powers, and Special Items

- Added a new powers tab to the V-menu with the same nine-column icon rhythm used by Stardew Valley's power display.
- Added locked-power rendering using dark translucent silhouettes and `???` hover text.
- Added unlock checks that can use either a Stardew mail flag or a synced special-item id, preserving old-save compatibility while supporting the new special-item path.
- Added Skull Key special-item persistence when the key enters a player's inventory, including `HasSkullKey`, `stardewcraft:skull_key`, save, sync, and obtained feedback.
- Added Dwarvish Translation Guide special-item persistence using `HasDwarvishTranslationGuide` plus `stardewcraft:dwarvish_translation_guide`.
- Changed Dwarvish Translation Guide use behavior from instant consume to a short right-click reading action.
- Added local reading feedback for the Dwarvish Translation Guide: an immediate page-turn sound, additional page-turn sounds during use, and a visible sustained-use animation.
- Kept Dwarvish Translation Guide completion feedback private to the user instead of broadcasting the read/learn sound to nearby players.
- Added brown-themed tooltip border styling and special-item tooltip lines for the Dwarvish Translation Guide.
- Added special-item type presentation for the Dwarvish Translation Guide, matching the Skull Key / Rusty Key style of permanent reward items.
- Updated Dwarf language access so either the old mail flag or the new special-item unlock lets the player understand Dwarves.

#### UI and Menu Work

- Continued the large UI scale normalization pass, reducing direct large-atlas UV dependence in favor of standalone GUI textures and shared draw helpers.
- Expanded common GUI texture helpers for powers icons and tintable power rendering.
- Improved V-menu tooltip routing so inventory-backed UI entries can display native item tooltips with all custom injected Stardew lines intact.
- Improved inventory, crafting, leaderboard, shop, farm, building, chest, catalogue, quest, overnight, and other Stardew-style screens touched by the scaling pass.
- Improved GUI consistency for item rendering, menu boxes, tab interactions, hover text, button texture slices, and Stardew-style pixel scaling across GUI scales.
- Added or refined font and UI resources needed by the newer menu and tooltip presentation.

#### Leaderboards and Player Data

- Extended the leaderboard foundation added in the previous line with more player-data integration, client cache handling, metrics, categories, and snapshot sync refinement.
- Added player-data fields and sync coverage needed by powers, special items, crafting interactions, ranking snapshots, and other client-side displays.
- Improved player login synchronization for several gameplay systems that need reliable first-open client state.
- Added more server-authored data paths so menus do not depend on stale client guesses after login, dimension changes, or world reloads.

#### World, Locations, and Movement

- Continued the pre-generated map coordinate migration work, including a written ledger for tracked coordinate, version, and installation changes.
- Continued cleanup around Stardew Valley prebuilt-region installation, interior allocation, desert layout handling, quarry access, farm-entry barriers, and cross-dimension teleport placement.
- Consolidated interior and public-area protection logic so subspace protection is less scattered and easier to reason about.
- Improved portal hints, interior transitions, NPC routing support, and location-graph behavior touched by the coordinate migration pass.
- Improved biome patching, forage placement, artifact spots, coal forest clumps, quarry spawning, and map bootstrap behavior around newer world-layout assumptions.

#### NPCs, Shops, Quests, and Events

- Improved NPC movement and route-planning internals, including central movement service behavior, spawn management, path navigation, schedule runtime, and location graph usage.
- Improved NPC friendship overview sync and friendship-related command/debug paths.
- Refined shop services and purchase handling across several vendors, including item availability and interaction flows touched by the wallet/special-item work.
- Refined Dwarf interaction behavior so the language gate follows both legacy and new unlock data.
- Updated museum reward handling around special item rewards, including the Dwarvish Translation Guide path.
- Improved event/cutscene payloads, debug commands, camera/player runtime details, and wake-up scheduling touched by this release pass.

#### Items, Tools, Fishing, Warp, and Economy

- Improved Stardew item tooltip injection for special items and related permanent rewards.
- Improved fishing rod item data handling and treasure screen behavior touched by recent item-data work.
- Improved warp wand behavior, unlock payloads, teleport payloads, destination handling, and related UI feedback.
- Improved cooking, crafting inventory actions, shipping-bin menu behavior, and shop purchase payload handling.
- Added compatibility and category-registration refinements for some vanilla/Stardew item interactions.

#### Audio, Rendering, and Assets

- Improved Stardew music manager behavior and sound registration touched by the current feature pass.
- Updated several block entity renderers to align with current render helper and resource assumptions.
- Improved Junimo text/model resources and related community-center UI presentation.
- Added standalone powers icon resources and recorded source extraction coordinates for future auditing.
- Updated many model/resource JSON files touched by the latest asset normalization pass.

#### Localization and Documentation

- Added English and Chinese text for the new powers page, wallet entries, special-item tooltips, Dwarvish Translation Guide feedback, and related UI labels.
- Added or updated documentation for leaderboard planning and pre-generated coordinate migration tracking.
- Updated the public project version to `0.3.9`.

### 更新日志（中文）

#### 重点内容

- 新增 V 键菜单里的星露谷 1.6 风格“能力 / 钱包”页面，按当前原版 `PowersTab` / `Powers.json` 的显示方式复刻，而不是继续沿用旧版未实际绘制的钱包入口判断。
- 新增独立钱包/能力图标素材，覆盖森林魔法、矮人语手册、生锈钥匙、会员卡、特殊魅力、骷髅钥匙、放大镜、黑暗护符、魔法墨水、熊掌、青葱技术和城镇钥匙。
- 新增客户端 `SpecialItems` 同步，让永久特殊物品解锁可以从服务端玩家数据直接显示，不再只能依赖 mail flag 猜测。
- 将骷髅钥匙和矮人语手册推进到真正的星露谷特殊物品规格：永久保留、不可出售、不会被使用消耗、写入玩家特殊物品数据，并在能力页显示。
- 能力页里的物品型能力现在直接显示对应物品自己的完整 tooltip，不再只显示手写标题和描述。

#### 钱包、能力页与特殊物品

- 新增 V 键菜单能力页，使用接近原版星露谷的九列图标排布与间距。
- 新增未解锁能力的黑色半透明剪影显示和 `???` 悬浮提示。
- 新增能力解锁判定：同一条能力可以同时兼容旧 mail flag 与新 special item id，兼顾老存档和新数据结构。
- 骷髅钥匙进入玩家背包时会写入 `HasSkullKey` 和 `stardewcraft:skull_key`，并保存、同步和提示获得状态。
- 矮人语手册现在写入 `HasDwarvishTranslationGuide` 和 `stardewcraft:dwarvish_translation_guide`，成为永久特殊物品。
- 矮人语手册从“右键瞬间学习并消耗”改为“右键阅读一小段时间后学习”，使用后不消失。
- 矮人语手册新增本地阅读反馈：开始阅读立刻翻页，中途继续翻页，并使用更明显的持续使用动作。
- 矮人语手册完成阅读或重复阅读的声音只发给使用者本人，不会广播给附近玩家。
- 矮人语手册新增棕色主题 tooltip 边框、特殊物品类型显示和状态说明。
- 矮人语言理解判定现在同时认可旧 mail flag 与新特殊物品解锁，避免老存档失效。

#### UI 与菜单

- 继续推进大规模 UI 缩放规范化，将更多界面从直接采样大图集迁移到独立贴图和共享绘制 helper。
- 扩展通用 GUI 贴图 helper，支持能力图标和可染色能力图标绘制。
- 改进 V 键菜单 tooltip 分发，让背包物品型 UI 项可以显示原生物品 tooltip，并保留所有 StardewCraft 注入的自定义行。
- 继续修正背包、合成、排行榜、商店、农场、建筑、箱子、目录、任务、过夜结算等界面在 UI 缩放迁移中的细节。
- 改进物品绘制、菜单框、tab 交互、悬浮提示、按钮切片和星露谷像素缩放的一致性。
- 补充或调整新菜单与 tooltip 表现所需的字体和 UI 资源。

#### 排行榜与玩家数据

- 继续完善上一版加入的排行榜系统，补强客户端缓存、服务端快照、榜单指标、分类和同步路径。
- 扩展玩家数据字段与同步范围，支撑能力页、特殊物品、合成交互、排行榜快照和其他客户端展示。
- 改进玩家登录时的多系统同步，减少首次打开菜单时客户端状态过旧的问题。
- 将更多菜单展示改为服务端权威数据驱动，减少登录、切维度或重载世界后的客户端猜测。

#### 世界、地点与移动

- 继续推进预生成地图坐标迁移，并补充坐标、版本、安装状态的追踪文档。
- 继续清理星露谷预生成区域安装、室内分配、沙漠布局、采石场访问、农场入口屏障和跨维度传送落点。
- 合并并简化室内/公共区域保护逻辑，让 subspace 保护不再分散在多套事件里。
- 改进传送门提示、室内切换、NPC 路由支持和地点图行为，配合坐标迁移后的地图结构。
- 调整生物群系 patch、采集物、蚯蚓点、煤炭森林树桩、采石场生成和地图 bootstrap 等与新版地图布局相关的行为。

#### NPC、商店、任务与事件

- 改进 NPC 移动和路线规划底层，包括集中移动服务、生成管理、寻路、日程运行和地点图使用。
- 改进 NPC 好感度概览同步，以及好感度相关命令和调试路径。
- 调整多个商店服务和购买流程，覆盖商品可用性、交互流程和本轮特殊物品相关改动。
- 矮人交互逻辑现在会按旧 mail flag 或新特殊物品判断语言是否已解锁。
- 调整博物馆奖励路径，配合矮人语手册这类特殊物品奖励。
- 改进事件/剧情网络包、调试命令、镜头/玩家运行细节和早晨唤醒调度等本轮涉及路径。

#### 物品、工具、钓鱼、传送与经济

- 改进 Stardew 物品 tooltip 注入，特别是特殊物品与永久奖励物品的展示。
- 改进钓竿物品数据处理和宝箱界面相关行为。
- 改进传送法杖行为、解锁网络包、传送网络包、目的地处理和相关 UI 反馈。
- 改进烹饪、合成背包操作、出货箱菜单和商店购买网络包处理。
- 补充部分原版 / Stardew 物品交互兼容和分类注册细节。

#### 音频、渲染与资源

- 改进 Stardew 音乐管理器和本轮功能涉及的声音注册。
- 调整多个方块实体渲染器，使其更贴合当前渲染 helper 与资源路径假设。
- 改进祝尼魔文本、模型资源和社区中心相关 UI 表现。
- 新增独立能力页图标资源，并记录素材来源坐标，方便之后核对。
- 更新大量本轮资源规范化涉及的模型和资源 JSON。

#### 本地化与文档

- 补充英文和中文的能力页、钱包条目、特殊物品 tooltip、矮人语手册反馈和相关 UI 文本。
- 新增或更新排行榜规划、预生成坐标迁移等文档。
- 项目公开版本号更新为 `0.3.9`。

## 0.3.8-fix4 - 2026-05-14

### Update Log (English)

#### Fixes

- Fixed Stardew-style UI texture sampling and UV drift across non-4x Minecraft GUI scales by moving many atlas-dependent widgets to standalone PNG slices.
- Fixed V-menu layout regressions from the UI scaling pass, preserving the 4x9 Minecraft inventory grid and the original top tab placement.
- Fixed the V-menu top-left frame artifact by correcting the sliced menu tile resource instead of hiding it with layout offsets.
- Fixed the leaderboard page header and row styling, removing the metric icon from the title and making top-three rows visibly distinct.
- Fixed the leaderboard side tabs to follow the workbench-style icon tab behavior, hover tooltips, and SHWIP click sound.
- Fixed shop UI panel shadow layering so the upper shop panel no longer casts a dark overlay across the inventory area.
- Fixed shop money box and money digit alignment under non-4x GUI scale.

#### Changes

- Added the V-menu leaderboard system with server-authored snapshots, pagination, client cache, request/sync payloads, and money, mining, fishing, shipping, combat, and life metrics.
- Added reusable Stardew UI texture helpers for standalone PNG rendering, scaled item drawing, common buttons, arrows, boxes, dialogue parts, social icons, skill icons, and game menu widgets.
- Migrated many Stardew-style screens and HUD elements away from direct large-atlas UV rendering, including shop, quest log, billboard, overnight, geode, elevator, catalogue, workbench, TV, and common dialogue UI pieces.
- Added the UI atlas slicing manifest/tooling and a written UI scaling normalization standard for future UI work.
- Added leaderboard persistence hooks for player names, mine block statistics, bombed mine blocks, shipped item totals, and total shipping value.

#### Localization

- Added English and Chinese leaderboard text, metric descriptions, value formats, V-menu tab label updates, and related configuration labels.

### 更新日志（中文）

#### 修复

- 修复大量星露谷风格 UI 在 Minecraft 非 4x GUI scale 下的贴图采样、UV 漂移和缩放异常，逐步改为独立 PNG 切片绘制。
- 修复 UI 缩放迁移过程中 V 键菜单布局被误改的问题，保留 Minecraft 4x9 背包网格和原本的顶部 tab 位置。
- 修复 V 键菜单左上角脏块，改为修正切片资源本身，而不是用界面偏移遮挡。
- 修复排行榜页标题和榜单行样式，移除标题里的指标图标，并让前三名高亮更清晰。
- 修复排行榜侧边 tab，使其按工作台图标 tab 的交互、悬浮提示和 SHWIP 点击音效表现。
- 修复商店上半部分面板阴影层级错误，避免黑色阴影盖到下方背包区域。
- 修复商店金币框和金币数字在非 4x GUI scale 下的位置失调。

#### 改动

- 新增 V 键菜单排行榜系统，包含服务端排行榜快照、分页、客户端缓存、请求/同步网络包，以及财富、采矿、钓鱼、出货、战斗和生活类榜单。
- 新增可复用的星露谷 UI 独立贴图 helper，覆盖缩放物品绘制、通用按钮、箭头、面板、对话框部件、社交图标、技能图标和游戏菜单控件。
- 将大量星露谷风格界面和 HUD 从直接采样大合图迁移到独立 PNG 绘制，包括商店、任务日志、公告板、过夜结算、晶球、电梯、家具目录、工作台、电视和通用对话 UI 部件。
- 新增 UI atlas 切片清单/脚本，以及后续 UI 缩放规范化的书面标准。
- 新增排行榜所需的玩家名称、矿井方块、爆破方块、出货数量和出货总价值统计接入。

#### 本地化

- 补充英文和中文排行榜文本、榜单说明、数值格式、V 键菜单 tab 名称和相关配置文本。

## 0.3.8-fix3 - 2026-05-13

### Update Log (English)

#### Fixes

- Fixed a Lewis cutscene crash that could happen when the client disconnected while an event was ending.
- Fixed cutscene movement freezing so vertical motion is preserved and players are less likely to trigger flight checks.
- Fixed targeted bait losing its target fish data when inserted into and removed from fishing rods.
- Fixed Stardew Valley weather forcing vanilla overworld rain, so vanilla weather commands work normally again outside the Stardew Valley dimension.
- Fixed auto-grabbers not collecting held animal products such as cow milk, goat milk, sheep milk, and wool.
- Fixed auto-feed troughs failing to detect their barn or coop when placed as valid adjacent interior fixtures.
- Fixed auto-feed trough hay consumption so it now pulls from the owning farm's shared silo storage instead of the interacting player's personal key.
- Fixed silo, pasture grass, and wheat hay storage ownership so hay is credited to the farm where the action happens.
- Fixed Stardew bed interactions so players enter the sleeping pose before confirming sleep, can cancel back out of bed, and no longer get placed at incorrect offsets on custom bed models.
- Fixed Stardew multiplayer sleep voting so only players who remain in bed count toward the vote, while waiting sleepers continue recovering stamina each second.
- Fixed charged hoe range previews disappearing when aiming at protected Stardew yellow dirt.
- Fixed multiplayer silo managers staying visually unbuilt after construction when the silo belonged to a shared farm owner instead of the interacting member.
- Fixed hay hoppers and silo readouts resolving hay storage through the wrong player in shared farms, which could show 0/0 despite an existing silo.
- Fixed crab pots being blocked by public-area protection in Stardew Valley waterways.
- Fixed crab pot catches using one combined pool instead of separating ocean and freshwater catch pools.
- Fixed crab pot ownership so only the player who placed a crab pot can bait, collect, or remove it.
- Disabled external item automation for crab pots so pipes cannot bypass crab pot ownership.
- Fixed farm join accept/reject commands being hidden behind operator-only command registration in multiplayer.
- Increased glow radius for Small Glow Ring, Glow Ring, Iridium Band, and Glowstone Ring by about 50%.
- Fixed NPC-bound friendship doors rendering opaque instead of using the oak door cutout layer.
- Fixed seasonal leaf tinting registration for vanilla and Stardew leaves without applying the effect to cherry leaves.
- Fixed beverage items using the eating animation instead of Minecraft's drinking animation, including artisan drinks, milk, Joja Cola, clinic tonics, Ginger Ale, and Triple Shot Espresso.

#### Changes

- Auto-feed troughs now continuously perform low-frequency refill checks while the chunk is loaded, pulling silo hay into empty connected trough networks as needed.
- Shared-farm hay storage now aggregates legacy member-owned hay while using the farm owner as the canonical storage key for new hay.
- Stardew Valley weather sync now uses the custom Stardew weather state instead of mutating vanilla level weather.
- Custom Stardew beds now resolve their sleep anchor to the correct head block and use vanilla sleeping orientation/rendering behavior instead of applying custom entity position offsets.
- Added built-in Xaero's Minimap icon resources for StardewCraft NPCs, animals, Junimos, crows, and traveling merchants.
- Added NPC-bound friendship doors that use oak door visuals, let Stardew NPCs pass through, and block players until they meet the configured friendship requirement.

#### Localization

- Added or corrected small English and Chinese localization entries touched by the fix pass.

### 更新日志（中文）

#### 修复

- 修复刘易斯剧情在客户端断开连接、剧情结束回包时可能崩溃的问题。
- 修复剧情冻结玩家移动时清空竖直速度导致更容易触发飞行检测的问题。
- 修复针对性鱼饵装入钓竿再取出后丢失目标鱼数据、变回普通鱼饵的问题。
- 修复星露谷天气强行锁定主世界原版下雨，导致 `/weather` 指令无法正常关雨的问题。
- 修复自动采集器无法采集牛奶、羊奶、绵羊奶和羊毛等动物持有产物的问题。
- 修复自动喂食槽在合法贴着室内空气格摆放时识别不到所属鸡舍或畜棚，导致完全不会自动补草的问题。
- 修复自动喂食槽扣草时没有从所在农场的共享筒仓干草池扣除的问题。
- 修复筒仓、牧草和小麦产出干草时归属不稳定的问题，现在会优先按所在农场记入干草。
- 修复星露谷床交互流程，现在玩家会先进入躺床状态再确认是否睡觉，取消时会正常起床，并修复自定义床模型上的错误躺床偏移。
- 修复多人睡觉投票流程，现在只有仍然躺在床上的玩家会计入投票，等待投票期间仍会每秒恢复体力。
- 修复锄头蓄力范围预览在对准受保护的星露谷黄土时不显示的问题。
- 修复多人服务器中共享农场成员建造筒仓后，服务端提示已建成但管理界面仍显示未成型的问题。
- 修复共享农场里筒仓界面和喂料斗按错误玩家读取干草存储，导致已有筒仓仍显示 0/0 的问题。
- 修复星露谷公共水域因公共区域保护而无法放置蟹笼的问题。
- 修复蟹笼捕获物没有区分海水/淡水池子、所有产物混在一起随机的问题。
- 修复蟹笼所有权，现在只有放置者可以塞鱼饵、收取产物或拆除蟹笼。
- 禁用蟹笼的外部物品自动化接口，避免管道绕过蟹笼主人限制。
- 修复多人模式中 `/stardew farm accept` 和 `/stardew farm reject` 被管理员权限命令树误拦截的问题。
- 将小型光辉戒指、光辉戒指、铱环和光辉石戒指的发光半径提高约 50%。
- 修复绑定 NPC 的好感门没有使用橡木门 cutout 渲染层，导致透明区域显示为不透明的问题。
- 修正原版树叶和星露谷树叶的季节染色注册，并避免樱花树叶被季节染色影响。
- 修复饮品物品使用时播放吃东西动画的问题，现在酒、果汁、咖啡、牛奶、Joja 可乐、诊所药水、姜汁汽水和三倍浓缩咖啡会使用 Minecraft 的饮用动画。

#### 改动

- 自动喂食槽现在会在区块加载期间持续进行低频补草检查，按需从筒仓向空的连接喂食槽网络补充干草。
- 共享农场干草存储现在以农场主人作为新干草的统一归属，同时兼容读取和扣除旧版本成员名下的干草。
- 星露谷天气同步现在只使用自定义星露谷天气状态，不再改写原版维度天气。
- 自定义星露谷床现在会把睡眠锚点解析到正确的床头格，并使用原版睡眠朝向与渲染逻辑，不再手动给实体叠加位置偏移。
- 内置 Xaero 小地图图标资源，覆盖星野牧歌 NPC、动物、祝尼魔、乌鸦和旅行商人等实体。
- 新增可绑定 NPC 的好感门，外观复用橡木门，星露谷 NPC 可直接穿过，玩家需要达到配置的好感度后才能开门通行。

#### 本地化

- 补充或修正了本轮修复涉及的少量英文与中文文本。

## 0.3.8fix - 2026-05-08

基线版本：release: 0.3.8-alpha

### 本次我们做了什么

- 修复 NPC 对话期间会提前恢复行走的问题；对话现在会完整锁定 NPC 移动，关闭对话或玩家下线后会正确解锁。
- 优化 NPC 长距离移动的中间寻路点计算，减少室内外切换、高低差路径里的异常卡住与误判瞬移。
- 扩展星露谷物品到原版物品的一次性转换配方，补齐苹果、骨片、钻石、鸡蛋、绿宝石、羽毛、蜂蜜、墨汁、牛奶、鹦鹉螺壳、兔子脚、史莱姆球、羊毛等兼容入口。
- 新增基于 c 标签和本地兼容标签的原版配方适配，让更多原版合成与部分跨模组配方直接接受星露谷等价物品。
- 补上兔子脚相关酿造兼容，使星露谷兔子脚也能进入原版酿造链。
- 改进共享农场联机加入流程，客户端会同步“待审批加入”状态；已有加入申请时再创建农场会先确认，加入成功后的落地与起步资源发放也更顺。
- 放宽共享农场成员的日常系统权限，让作物、牧草、乌鸦、农场洞穴和每日结算更按成员所属农场生效，而不是只认房主。
- 增强离线玩家跨天补偿与晨间事件调度，避免多人环境里邮件、早晨事件和社区中心过场被静默跳过。
- 调整矿井与骷髅矿生态，重生成时会清理残留怪物，并重新收紧高层矿石与特殊房间节奏，减少一层就出现过量高价值矿的情况。
- 加入怪物召唤调试命令，并补充木乃伊坍塌的服务端同步、客户端渲染与爆炸处决链路，让木乃伊表现更接近原版星露谷。
- 修正鱼塘水体标签与鱼塘拉鱼交互，优化商店连续购买停止条件、电视烹饪频道解锁校验、Joja 路线细节、跨维度进出和时间对齐等边角问题。
- 将炸弹范围进一步削弱一档，避免当前版本的爆炸覆盖面偏大。

### 主要改动分类

#### NPC 与剧情

- 对话锁移动、关闭回包与登出清理补齐。
- NPC 路径中间点高度解析修正。
- 多人环境下的晨间事件、邮件与社区中心过场调度更稳定。

#### 物品兼容与配方

- 新增 Stardew 物品到原版物品的单向转换配方。
- 新增 c 标签桥接与 vanilla_compat 标签层。
- 覆写大量原版配方以接受钻石、蛋、奶、蜂蜜、羽毛、兔子脚、史莱姆球等星露谷等价物。
- 新增兔子脚酿造兼容。

#### 联机与共享农场

- 农场加入申请待处理状态同步到客户端。
- 加入申请与创建农场的冲突流程增加确认保护。
- 共享农场成员权限、传送、每日结算与农场洞穴逻辑继续向“真正共享”收敛。

#### 矿井、怪物与战斗

- 矿层重生成会清理残留怪物。
- 骷髅矿高层矿石与房间分布再平衡。
- 木乃伊倒地渲染、同步和处决逻辑补齐。
- 新增怪物召唤调试命令。

#### 其他修复

- 鱼塘可作为水体使用，并支持等待上钩时直接从鱼塘拉鱼。
- 商店长按购买会在背包鼠标堆叠装不下或余额不足时自动停止。
- 电视菜谱解锁改为校验当天实际播出的配方。
- 星露谷维度时间对齐、跨维度落点、传送效果与若干本地化文本继续修正。