# 刘易斯兑奖券系统接入规划

> 目标：按原版《星露谷物语》1.6 的 `PrizeTicketMenu` / Prize Machine 逻辑接入兑奖券系统。实现前必须以原版源码和素材为准，不用猜测奖励、UI 坐标、声音或特效。

## 1. 原版源码锚点

| 模块 | 原版位置 | 已确认逻辑 |
| --- | --- | --- |
| 兑奖机菜单 | `源文件/StardewValley.Menus/PrizeTicketMenu.cs` | 菜单尺寸、按钮状态、奖励轨、按钮/出奖/轨道移动计时、声音、消耗券和递增 `ticketPrizesClaimed`。 |
| 兑奖机交互 | `源文件/StardewValley/GameLocation.cs` | `PrizeMachine` action 直接打开 `PrizeTicketMenu`。 |
| 特殊订单兑奖券领取 | `源文件/StardewValley/GameLocation.cs` | `SpecialOrdersPrizeTickets` action：若 `specialOrderPrizeTickets != 0` 且背包可接收，则给 1 张 `(O)PrizeTicket`，递减 stat，播放 `coin`；否则红字背包满。 |
| 镇子地图显示 | `源文件/StardewValley.Locations/Town.cs` | 特殊订单板解锁后设置 `SpecialOrdersPrizeTickets` 地图 tile；若 `specialOrderPrizeTickets != 0` 且非节日，在镇子板旁绘制可领取提示。 |
| 每日公告栏任务 | `源文件/StardewValley.Quests/Quest.cs` | 每完成 daily quest：`BillboardQuestsDone++`，首次加 `completedFirstBillboardQuest`；每 3 次给 1 张 `(O)PrizeTicket`；累计 >= 6 后加 `gotFirstBillboardPrizeTicket`。 |
| 特殊订单完成 | `源文件/StardewValley.SpecialOrders/SpecialOrder.cs` | 普通特殊订单完成且非 Qi/沙漠节订单时，`specialOrderPrizeTickets++`，实际券在镇子板旁领取。 |
| 引导事件 | `源文件/Content/Data/Events/ManorHouse*.json` | `prizeTicketIntro/n completedFirstBillboardQuest`，刘易斯说明兑奖机；声音含 `distantBanjo`、`doorClose`。 |
| 物品数据 | `源文件/Content/Data/Objects.json` | `PrizeTicket` 使用 `TileSheets\\Objects_2`，`SpriteIndex=69`，不可赠送，可丢弃，context tag `color_red`。 |
| 原版商店解锁 | `源文件/Content/Data/Shops.json` | 部分家具/帽子在 `ticketPrizesClaimed` 到达指定值后进入商店。 |
| 节日额外来源 | `源文件/StardewValley/Event.cs` | 蛋节第一次赢给草帽，之后赢给兑奖券；冰钓第一次赢给原奖励，之后赢给兑奖券；偶数年万灵节宝箱给兑奖券。 |
| 夜间事件 | `源文件/StardewValley/Utility.cs` | 任意在线玩家有 `gotFirstBillboardPrizeTicket` 或天数 > 50 可触发 Qi Plane。当前系统可先记录，不强制接 Qi Plane。 |

## 2. 原版兑奖菜单行为

### 2.1 UI 尺寸与布局

- 菜单逻辑尺寸：`116 x 94` SDV px；屏幕显示为 `464 x 376`，即原版 scale 4。
- 主背景：`LooseSprites\\PrizeTicketMenu` 源矩形 `(0, 0, 116, 94)`。
- 顶部奖励轨遮罩/装饰：同纹理源矩形 `(0, 106, 76, 22)`，绘制在菜单内 `(25, 18)`。
- 主按钮：屏幕矩形 `(x+192, y+216, 92, 88)`，等价 SDV 坐标 `(48, 54, 23, 22)`。
- 主按钮源矩形：普通 `(150, 29, 23, 22)`，悬停 `(150, 51, 23, 22)`，按下 `(150, 73, 23, 22)`。
- 奖励轨物品坐标：第 `i` 个物品绘制在 `(28 + 22*i, 21)` SDV px。
- 当前奖励高亮：菜单内屏幕矩形 `(100, 76, 88, 80)`，半透明浅黄，不是贴图。
- 票数文字：`ticketCount` 居中绘制在屏幕 `x+360`，`y+276`，等价 SDV `(90, 69)`。

### 2.2 交互与计时

- 打开菜单立即播放 `machine_bell`。
- 鼠标悬停按钮且未按下/未出奖/未移动奖励轨：首次进入 hover 播放 `button_tap`，按钮源矩形切到 hover。
- 点击按钮：播放 `button_press`，按钮按下计时 `200ms`。
- 只有玩家背包里 `PrizeTicket` 数量 > 0 才进入出奖流程；没有券也会播放按钮按下声，但不出奖。
- 出奖流程：按钮恢复后开始 `getRewardTimer`，到 `2000ms` 结算。
- 点击后 `750ms` 播放 `discoverMineral`。
- `2000ms` 时播放 `coin`，把当前奖励轨第 1 个物品放入背包，背包满则掉落在玩家脚下；消耗 1 张 `PrizeTicket`；`ticketPrizesClaimed++`。
- 结算后等待 `500ms`，播放 `ticket_machine_whir`，奖励轨移动 `2000ms`。
- 轨道移动时：剩余奖励横向滑动，并在 whir 后加随机 `[-1, 1]` 像素抖动。
- 轨道移动结束后补入新的第 4 个预览奖励，奖励编号为当前 `ticketPrizesClaimed + 3`。
- 出奖时禁止关闭菜单；轨道移动时不能再次点击。

## 3. 原版奖励轨

原版初始化时显示 `ticketPrizesClaimed`、`+1`、`+2`、`+3` 四个奖品。领取的是第一个。前 22 个奖品固定序号；后续按 9 张一组循环。

### 3.1 固定奖励 0-21

| 等级 | 原版 ItemId / 逻辑 | 说明 |
| --- | --- | --- |
| 0 | `Utility.getRaccoonSeedForCurrentTimeOfYear(player, r, 12)` | 当前季节浣熊种子，数量 12。 |
| 1 | 随机 `(O)631` 或 `(O)630` | Peach Sapling / Orange Sapling。 |
| 2 | 随机 `(O)770 x10` 或 `(O)MixedFlowerSeeds x15` | 混合种子 / 混合花种。 |
| 3 | `(O)MysteryBox x3` | 神秘盒。 |
| 4 | `(O)StardropTea` | 星之果茶。 |
| 5 | 房屋升级后 `(F)BluePinstripeDoubleBed`，否则 `(F)BluePinstripeBed` | 蓝色细条纹床。 |
| 6 | 随机 `(O)621` / `(BC)15` / `(BC)MushroomLog`，数量 4 | Quality Sprinkler / Preserves Jar / Mushroom Log。 |
| 7 | 随机 `(O)633` 或 `(O)632` | Apple Sapling / Pomegranate Sapling。 |
| 8 | `(O)Book_Friendship` | 友谊书，书籍系统已预留来源。 |
| 9 | 随机 `(O)286 x20` / `(O)287 x12` / `(O)288 x6` | 炸弹类奖励。 |
| 10 | `(H)SportsCap` | 运动帽。 |
| 11 | 随机 `(BC)FishSmoker` 或 `(BC)Dehydrator` | 鱼熏机 / 脱水机。 |
| 12 | 随机 `(O)275` 或 `(O)MysteryBox`，数量 4 | 后续需按物品映射核对。 |
| 13 | 随机 `(F)FancyHousePlant1/2/3` | 精美盆栽。 |
| 14 | `(O)SkillBook_0..4` 随机一个 | 技能书。 |
| 15 | `(O)StardropTea` | 星之果茶。 |
| 16 | `(F)CowDecal` | 牛贴花。 |
| 17 | `(O)749 x8` | Omni Geode。 |
| 18 | 随机 `(BC)10` 或 `(BC)12`，数量 4 | Bee House / Keg。 |
| 19 | `(O)72 x5` | 钻石。 |
| 20 | `(O)MysteryBox x5` | 神秘盒。 |
| 21 | `(O)279` | 魔法糖冰棍。 |

### 3.2 22+ 循环奖励

`prizeLevel >= 22` 时，随机种子为 `CreateRandom(uniqueIDForThisGame, prizeLevel - prizeLevel % 9)`；同一 9 张分组内使用同一个 `r2`。按 `prizeLevel % 9`：

| 余数 | 原版奖励 |
| --- | --- |
| 0 | `(O)MysteryBox x5` |
| 1 | `(O)872 x1-2`，Fairy Dust |
| 2 | 从 `(O)337` Iridium Bar、`(O)226` Spicy Eel、`(O)253` Triple Shot Espresso、`(O)732` Crab Cakes、`(O)275` Artifact Trove 中随机，数量 5 |
| 3 | 随机 `(F)FancyHousePlant1/2/3` |
| 4 | `(O)StardropTea` |
| 5 | `(O)166`，Treasure Chest |
| 6 | `(O)645`，Iridium Sprinkler |
| 7 | 随机 `(F)FancyTree1/2/3`, `(F)PigPainting` |
| 8 | 随机 `(O)287 x15` 或 `(O)288 x8` |

## 4. 项目现状与落点

### 4.1 已有基础

- `src/main/resources/data/stardewcraft/npc/vanilla/data/Objects.json` 已导入原版 `PrizeTicket` 数据，但还不是可用的 `ModItems` 物品。
- `QuestManager` 已持久化 `BillboardQuestsDone`，`StardewQuest.questComplete()` 已在每日任务完成时递增。
- `BillboardScreen` 已显示三颗星进度，注释也标明每 3 次奖励应接入。
- 音效已有 `button_tap`、`coin`、`discover_mineral`；缺 `machine_bell`、`button_press`、`ticket_machine_whir`。
- `src/main/resources/assets/stardewcraft/textures/gui/**` 已有大量拆分 UI 组件；本系统也必须拆成独立 PNG。
- `LooseSprites/PrizeTicketMenu.png`、`LooseSprites/Cursors_1_6.png`、`TileSheets/Objects_2.png` 可从 `源文件/Content/**` 裁切。

### 4.2 需要新增/改造

- 新增 `ModItems.PRIZE_TICKET`，物品名、描述、堆叠、不可食用、不可赠送语义尽量按原版映射。
- 从 `TileSheets/Objects_2.png` 第 69 格裁切 `prize_ticket.png`，放入项目物品纹理；不引用整张图。
- 如果用户已有兑奖机模型但未命名为 `prize/ticket/machine`，需要在实现前确认实际文件名；当前搜索未找到明确的兑奖机 block/model/texture。
- 新增 `PrizeTicketMachineBlock` 或等价交互方块：右键打开兑奖菜单；如果作为地图装饰块，走 `MapDecorStaticBlock` / `MapDecorWallStaticBlock` 现有占位逻辑。
- 新增兑奖菜单 Screen 和必要网络 Payload：领奖必须由服务端验证并扣券/发奖/递增统计，客户端只负责动画和请求。
- 新增玩家持久字段：`ticketPrizesClaimed`、`specialOrderPrizeTickets`，或用现有数据容器扩展；需要同步到客户端显示。
- 每日任务完成每 3 次发券：当前 `StardewQuest.questComplete()` 只递增统计，需补实物发放和 `completedFirstBillboardQuest` / `gotFirstBillboardPrizeTicket` mail flag。
- 特殊订单系统尚不完整：先预留 API 和数据字段；等特殊订单完成逻辑落地时按原版累积 `specialOrderPrizeTickets`，再由特殊订单板旁领取。
- 蛋节后续奖励和万灵节偶数年宝箱可作为后续接入点；本阶段至少把 PrizeTicket 物品接通，方便替换现有“奖励未接入”提示。
- Shops 条件解锁暂不抢做：`ticketPrizesClaimed` 达到 6/11/17 后解锁床、运动帽、CowDecal 等，等商店/家具映射表齐后接。

## 5. UI 素材切片要求

必须从原版 PNG 裁切为独立组件，不能在运行时引用大图 atlas。

| 组件 | 原版 PNG | 源矩形 | 目标建议 |
| --- | --- | --- | --- |
| 兑奖机菜单背景 | `源文件/Content/LooseSprites/PrizeTicketMenu.png` | `(0,0,116,94)` | `textures/gui/prize_ticket/menu_background.png` |
| 奖励轨遮罩/上盖 | 同上 | `(0,106,76,22)` | `textures/gui/prize_ticket/reward_track_overlay.png` |
| 按钮普通 | 同上 | `(150,29,23,22)` | `textures/gui/prize_ticket/button.png` |
| 按钮悬停 | 同上 | `(150,51,23,22)` | `textures/gui/prize_ticket/button_hover.png` |
| 按钮按下 | 同上 | `(150,73,23,22)` | `textures/gui/prize_ticket/button_pressed.png` |
| 兑奖券物品图标 | `源文件/Content/TileSheets/Objects_2.png` | `SpriteIndex=69` | `textures/item/prize_ticket.png` |
| 镇子可领取提示气泡 | `Game1.mouseCursors` | `(141,465,20,24)` | 可复用/新增 `textures/gui/prize_ticket/town_prompt_bubble.png` |
| 镇子兑奖券提示图标 | `源文件/Content/LooseSprites/Cursors_1_6.png` | `(240,240,16,16)` | `textures/gui/prize_ticket/town_prompt_ticket.png` |

实现中按 `UI_SCALING_NORMALIZATION_STANDARD.md`：把 SDV px 通过项目 `ui(sdvPx)` 规则缩放，不用视口宽度缩放字体，不让动态文本撑动布局。

## 6. 音效清单

| 原版 cue | 当前项目状态 | 处理 |
| --- | --- | --- |
| `machine_bell` | 缺 | 从原版音频导出，注册 `ModSounds.MACHINE_BELL`。 |
| `button_tap` | 已有 | 直接复用 `ModSounds.BUTTON_TAP`。 |
| `button_press` | 缺 | 从原版音频导出，注册 `ModSounds.BUTTON_PRESS`。 |
| `discoverMineral` | 已有 `discover_mineral` | 使用 `ModSounds.DISCOVER_MINERAL`，注意 750ms 延迟。 |
| `coin` | 已有 | 使用 `ModSounds.COIN`。 |
| `ticket_machine_whir` | 缺 | 从原版音频导出，注册 `ModSounds.TICKET_MACHINE_WHIR`。 |
| 引导事件 `distantBanjo` | 已有 music/distant_banjo | 等事件接入时复用。 |
| 引导事件 `doorClose` | 已有 | 等事件接入时复用。 |

若本地缺音频文件，按用户要求先到 Stardew 中文 wiki 模组音频页确认 cue 编号，再用项目音频转换流程导出，不能用近似音代替。

## 7. 实现阶段拆分

1. **物品与素材阶段**
   - 注册 `prize_ticket` 物品、模型、贴图和中英文翻译。
   - 裁切 PrizeTicketMenu/Objects_2/Cursors_1_6 组件 PNG。
   - 注册缺失音效，补 `sounds.json`。
   - 验证：资源文件存在、JSON 可解析、物品能在开发环境中显示。

2. **数据与奖励服务阶段**
   - 在 `PlayerStardewData` 中加入 `ticketPrizesClaimed`、`specialOrderPrizeTickets`。
   - 新增 `PrizeTicketRewardService`：按原版奖励表生成 ItemStack；随机必须稳定，绑定世界种子/玩家 UUID/奖励等级。
   - 新增服务端 claim payload：验证玩家有券，扣券，发奖，递增 `ticketPrizesClaimed`，返回客户端动画状态。
   - 验证：无券不领奖；有券扣 1；背包满时走掉落；计数递增后下一个奖励正确。

3. **兑奖机方块与菜单阶段**
   - 接入用户的兑奖机模型，注册方块/方块物品/语言。
   - 右键打开 `PrizeTicketMachineScreen`。
   - 按原版 `116x94` 布局实现背景、奖励轨、按钮状态、飞出奖励、轨道滑动、抖动、票数显示。
   - 验证：按钮悬停/点击/无券/有券/领奖中关闭限制/轨道移动都与原版计时一致。

4. **来源接入阶段**
   - 每日公告栏任务：每 3 次完成给 1 张券，并写入原版 mail flags。
   - 特殊订单：若当前特殊订单系统未完整，先接数据和领取动作；等订单完成入口稳定后补累积。
   - 蛋节：把后续获胜奖励从“未接入”替换成实物兑奖券。
   - 万灵节偶数年宝箱、冰钓后续奖励、Town 一次性礼盒根据现有节日/礼盒系统逐步接入。

5. **引导事件与提示阶段**
   - 接 `prizeTicketIntro`：完成第一次公告栏任务后在刘易斯家触发。
   - 特殊订单板旁可领取提示：按 Town.cs 的提示气泡和券图标绘制。
   - 领取特殊订单累积券：右键板旁机器/动作点，每次给一张并播放 `coin`。

## 8. 验证清单

- `./gradlew classes --stacktrace` 通过。
- 所有新增 JSON 通过解析校验。
- UI 截图核对：菜单背景、按钮状态、奖励轨、票数文本、奖励飞出不重叠。
- 声音核对：打开、hover、press、750ms reveal、2000ms coin、轨道 whir 全触发。
- 奖励核对：前 22 张和 22+ 循环按原版表；随机奖励对同一世界/玩家稳定。
- 联机核对：服务端发奖和 stats 以玩家为单位，不让客户端伪造奖励。
- 背包满核对：奖品掉落，兑奖券仍按原版已消耗。

## 9. 暂不做但需留口

- Qi Plane 夜间事件只记录条件，不在本阶段强接。
- `ticketPrizesClaimed` 条件商店解锁等商店物品映射齐后接。
- 特殊订单完整系统若还缺底层，不在兑奖机第一阶段硬补全部特殊订单。
- 用户提供的兑奖机模型若文件名未能搜索到，实现前需确认实际路径或让模型文件进入 `assets/stardewcraft`。