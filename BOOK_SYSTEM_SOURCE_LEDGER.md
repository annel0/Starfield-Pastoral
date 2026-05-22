# 书籍系统源码锁定表

> 范围：Stardew Valley 1.6 书籍系统第一批接入。
> 状态：阶段 0 初版，后续实现前继续补充每个效果的精确代码片段与测试用例。
> 最后更新：2026/05/21

> 2026/05/22 项目规则覆盖：`Book_PriceCatalogue` 不接入本项目；`Book_AnimalCatalogue` 改为不可消耗目录书，阅读后直接打开 Marnie 动物购买界面。原版表中相关行仅保留为源码参考，不作为当前实现验收标准。

---

## 一、硬规则

- 行为以 `源文件/` 中的 SDV C# 源码为准。
- 物品数据以 `源文件/Content/Data/Objects.json` 为准。
- 商店数据以 `源文件/Content/Data/Shops.json` 为准。
- Powers 展示以 `源文件/Content/Data/Powers.json` 为准。
- 视觉、声音、特效必须从原版资源中截取/转换后纳入本项目资产，不能运行时引用外部 loose asset。
- 音频转换脚本位置：`scripts/convert_music.py`；新增 cue 使用 `--cue NAME HEX_ID SUBDIR` 参数导出。

---

## 二、核心行为源码

| 行为 | SDV 源 | 锁定内容 |
| --- | --- | --- |
| 读书入口 | `源文件/StardewValley/Object.cs` `readBook(GameLocation location)` | `canMove=false`、`freezePause=1030`、面向 2、frame 57 持续 1000ms、`MusicDuckTimer=4000f`、`Game1.playSound("book_read")`。 |
| 正常玩法限制 | `Object.performUseAction` | 非 event/festival/fade/swim/bathing/onBridge 且 category 为 `-102` 或 `-103` 才读。 |
| 读书 3D 翻页 | StardewCraft 适配层 | 使用 MC 附魔台书模型/纹理/翻页参数在玩家面前临时渲染；SDV `LooseSprites\\Book_Animation` 仅作为源码参考，不作为运行时资产或播放对象。 |
| 读完特效 | `Object.readBook` frameEndBehavior | 移除 id `1987654`，在玩家站位 `(-40,-156)` 调 `Utility.addRainbowStarExplosion(..., 8)`。 |
| 技能书 | `Object.readBook` | `SkillBook_0..4` 给对应技能 250 XP；未触发升级提示时 1000ms 后显示 `SkillBookMessage`。 |
| 星之书 | `Object.readBook` `PurpleBook` | 五个技能各 250 XP。 |
| 能力书首读 | `Object.readBook` category `-102` | `stats.Increment(itemId)`，1000ms 后显示 `LearnedANewPower`，添加 `read_a_book` mail flag，检查 Well Read。 |
| 能力书重复阅读 | `Object.readBook` + context tags | 已读且不是 `Book_PriceCatalogue` / `Book_AnimalCatalogue`：有 `book_xp_*` 给对应技能 100 XP；否则五技能各 20 XP。 |
| 酱料女皇书 | `Object.readBook` `Book_QueenOfSauce` | 遍历 `Data/Tv_CookingChannel`，补齐未学菜谱，递增 stat，显示 `QoS_Cookbook` 数量。 |
| 已读描述 | `Object.cs` description 分支 | 已读能力书显示重复阅读经验说明。 |
| Well Read | `源文件/StardewValley/Stats.cs` `checkForBooksReadAchievement` | 要求 19 本永久能力书，不含 5 本技能书、`PurpleBook`、`Book_QueenOfSauce`。 |

---

## 三、原版资产锁定

| 资产 | SDV 源 | 当前项目状态 | StardewCraft 目标 | 处理要求 |
| --- | --- | --- | --- | --- |
| 书籍 item icon | `Objects.json` `Texture="TileSheets\\Objects_2"` + `SpriteIndex` | 已用 `scripts/extract_book_assets.py` 从 `TileSheets\\Objects_2` 截取 26 本独立贴图，并生成 item model | `textures/item/book/<book>.png` | 按 `Objects_2.png` 每行 8 个 16x16 图块换算；不能使用 `Maps\\springobjects`。 |
| 读书 3D 翻页 | Minecraft enchantment table book model/texture | 待接入客户端临时渲染器 | vanilla `BookModel` / `ModelLayers.BOOK` / `textures/entity/enchanting_table_book.png` | 当前 1.21.1 classpath 已确认存在 `BookModel(ModelPart)`、`BookModel.setupAnim(float,float,float,float)`、`ModelLayers.BOOK`；右键读书时在玩家面前打开、翻页、合上，不使用附魔台方块实体逻辑、附魔声音或附魔粒子。 |
| 读完彩虹爆星：星形 | `Utility.addRainbowStarExplosion(..., 8)` | 已用 `scripts/extract_book_assets.py` 截取 `star_00..07.png` | `textures/particle/rainbow_star/star_*.png` | `TileSheets\\animations` rect `(0,640,64,64)`，8 帧，50ms/帧，prismatic color，围绕原点径向飞出。 |
| 读完彩虹爆星：中心 | `Utility.addRainbowStarExplosion(..., 8)` | 已用 `scripts/extract_book_assets.py` 截取 `center_00..07.png` | `textures/particle/rainbow_star/center_*.png` | `TileSheets\\animations` rect `(0,320,64,64)`，8 帧，60ms/帧，白色中心爆闪。 |
| 读书声音 | `Game1.playSound("book_read")` | 已导入 `sounds/utility/book_read.ogg` 并注册 `sounds.json` cue `book_read` | `sounds/utility/book_read.ogg` + `sounds.json` | 音频 wiki：`book_read`，Sound，Wavebank，index `423`，hex `000001a7`；已用 `scripts/convert_music.py --cue book_read 000001a7 utility` 从原版音频导出。 |

音频 wiki 锚点：`https://zh.stardewvalleywiki.com/模组:音频` / `https://stardewvalleywiki.com/Modding:Audio`。

---

## 四、读书字符串锁定

| Key | en-US | zh-CN | 用途 |
| --- | --- | --- | --- |
| `skillBook_Category` | `Skill Book` | `技能书` | 技能书类别显示。 |
| `alreadyreadbook` | `Reading this book again will grant some {0} experience.` | `再次阅读这本书可以获得一些{0}经验。` | 已读且有 `book_xp_*` tag 的能力书描述。 |
| `alreadyreadbook_random` | `Reading this book again will grant a small amount of experience in all areas.` | `再次阅读这本书可以在所有领域获得少量经验。` | 已读且无指定 XP tag 的能力书描述。 |
| `BooksellerInTown` | `The Bookseller is in town today.` | `书摊老板今天在镇上。` | 书商当天早晨提示。 |
| `SkillBookMessage` | `You've learned a few things about {0}.` | `你学到了一些{0}方面的知识。` | 技能书未触发升级动画时的提示。 |
| `SkillName_0` | `Farming` | `耕种` | 技能书消息参数。 |
| `SkillName_1` | `Fishing` | `钓鱼` | 技能书消息参数。 |
| `SkillName_2` | `Foraging` | `采集` | 技能书消息参数。 |
| `SkillName_3` | `Mining` | `采矿` | 技能书消息参数。 |
| `SkillName_4` | `Combat` | `战斗` | 技能书消息参数。 |
| `QoS_Cookbook` | `You learned {0} new recipes.` | `你学习了 {0} 个新食谱。` | 酱料女皇书提示。 |
| `FoundABook` | `You found a copy of '{0}'.` | `你找到了一本《{0}》。` | 找到书提示。 |
| `buy_books` | `Buy Books` | `购买书籍` | 书商 Buy 选项。 |
| `trade_books` | `Trade In Books` | `回收书籍` | 书商 Trade 选项。 |
| `books_welcome` | `Welcome to Marcello's Books!` | `我是马尔赛罗，欢迎来到我的书摊！` | 读过书后书商问答欢迎语。 |
| `books_buy_welcome` | `Marcello's Books... At your service!` | `我是马尔赛罗，这里的书……请随意挑选！` | Bookseller shop 欢迎语。 |
| `books_trade_welcome` | `I'll trade useful stuff for your extra books.` | `你可以用多余的书来换有用的东西。` | BooksellerTrade shop 欢迎语。 |
| `LearnedANewPower` | `You've learned a new power!` | `你学会了新的能力！` | 能力书首次阅读延迟提示。 |

书籍物品名称与描述已从 `源文件/Content/Strings/Objects.json` 和 `源文件/Content/Strings/Objects.zh-CN.json` 写入项目语言文件：`item.stardewcraft.<book_id>` 与 `item.stardewcraft.<book_id>.desc`。`Book_Horse` 使用原版 `Book_Riding_*` key，`Book_Grass` 使用原版 `GrassBook_*` key，`SkillBook_0..4` 使用原版 `SkillBook0..4_*` key。

---

## 五、Objects.json 书籍表

| ID | Name | Category | Price | SpriteIndex | ContextTags |
| --- | --- | --- | --- | --- | --- |
| `SkillBook_0` | Stardew Valley Almanac | -103 | 500 | 90 | `color_gold`, `book_item` |
| `SkillBook_1` | Bait And Bobber | -103 | 500 | 92 | `color_blue`, `book_item` |
| `SkillBook_2` | Woodcutter's Weekly | -103 | 500 | 91 | `color_green`, `book_item` |
| `SkillBook_3` | Mining Monthly | -103 | 500 | 93 | `color_brown`, `book_item` |
| `SkillBook_4` | Combat Quarterly | -103 | 500 | 94 | `color_red`, `book_item` |
| `PurpleBook` | Book Of Stars | -103 | 2500 | 89 | `color_iridium`, `book_item` |
| `Book_AnimalCatalogue` | Animal Catalogue | -102 | 2000 | 118 | `color_dark_pink`, `book_item` |
| `Book_Artifact` | Book_Artifact | -102 | 500 | 140 | `color_brown`, `book_item` |
| `Book_Bombs` | Dwarvish Safety Manual | -102 | 1000 | 104 | `color_red`, `book_item`, `book_xp_mining` |
| `Book_Crabbing` | The Art O' Crabbing | -102 | 1000 | 103 | `color_black`, `book_item`, `book_xp_fishing` |
| `Book_Defense` | Jack Be Nimble, Jack Be Thick | -102 | 500 | 108 | `color_brown`, `book_item`, `book_xp_combat` |
| `Book_Diamonds` | The Diamond Hunter | -102 | 1000 | 116 | `color_cyan`, `book_item`, `book_xp_mining` |
| `Book_Friendship` | Friendship 101 | -102 | 3000 | 109 | `color_pink`, `book_item` |
| `Book_Grass` | Book_Grass | -102 | 1000 | 145 | `color_green`, `book_item` |
| `Book_Horse` | Book_Horse | -102 | 1000 | 141 | `color_brown`, `book_item` |
| `Book_Marlon` | Mapping Cave Systems | -102 | 4000 | 112 | `color_dark_gray`, `book_item`, `book_xp_combat` |
| `Book_Mystery` | Book of Mysteries | -102 | 3000 | 117 | `color_blue`, `book_item` |
| `Book_PriceCatalogue` | Price Catalogue | -102 | 1000 | 114 | `color_gold`, `book_item` |
| `Book_QueenOfSauce` | Queen Of Sauce Cookbook | -102 | 10000 | 115 | `color_blue`, `book_item` |
| `Book_Roe` | Jewels Of The Sea | -102 | 800 | 105 | `color_orange`, `book_item`, `book_xp_fishing`, `fish_has_roe` |
| `Book_Speed` | Way Of The Wind pt. 1 | -102 | 5000 | 111 | `color_iron`, `book_item` |
| `Book_Speed2` | Way Of The Wind pt. 2 | -102 | 10000 | 119 | `color_cyan`, `book_item` |
| `Book_Trash` | The Alleyway Buffet | -102 | 3000 | 102 | `color_dark_gray`, `book_item`, `book_xp_foraging` |
| `Book_Void` | Monster Compendium | -102 | 2000 | 110 | `color_black`, `book_item`, `book_xp_combat` |
| `Book_WildSeeds` | Ways Of The Wild | -102 | 1000 | 106 | `color_green`, `book_item`, `book_xp_foraging` |
| `Book_Woodcutting` | Woody's Secret | -102 | 500 | 107 | `color_brown`, `book_item`, `book_xp_foraging` |

---

## 六、阅读效果矩阵

| ID | 首读/每读效果 | 重复阅读效果 | Well Read |
| --- | --- | --- | --- |
| `SkillBook_0` | Farming +250 XP | 同左 | 否 |
| `SkillBook_1` | Fishing +250 XP | 同左 | 否 |
| `SkillBook_2` | Foraging +250 XP | 同左 | 否 |
| `SkillBook_3` | Mining +250 XP | 同左 | 否 |
| `SkillBook_4` | Combat +250 XP | 同左 | 否 |
| `PurpleBook` | 五技能各 +250 XP | 同左 | 否 |
| `Book_AnimalCatalogue` | `stats.Increment("Book_AnimalCatalogue")` | 原版不转 XP | 是 |
| `Book_Artifact` | `stats.Increment("Book_Artifact")` | 五技能各 +20 XP | 是 |
| `Book_Bombs` | `stats.Increment("Book_Bombs")` | Mining +100 XP | 是 |
| `Book_Crabbing` | `stats.Increment("Book_Crabbing")` | Fishing +100 XP | 是 |
| `Book_Defense` | `stats.Increment("Book_Defense")` | Combat +100 XP | 是 |
| `Book_Diamonds` | `stats.Increment("Book_Diamonds")` | Mining +100 XP | 是 |
| `Book_Friendship` | `stats.Increment("Book_Friendship")` | 五技能各 +20 XP | 是 |
| `Book_Grass` | `stats.Increment("Book_Grass")` | 五技能各 +20 XP | 是 |
| `Book_Horse` | `stats.Increment("Book_Horse")` | 五技能各 +20 XP | 是 |
| `Book_Marlon` | `stats.Increment("Book_Marlon")` | Combat +100 XP | 是 |
| `Book_Mystery` | `stats.Increment("Book_Mystery")` | 五技能各 +20 XP | 是 |
| `Book_PriceCatalogue` | `stats.Increment("Book_PriceCatalogue")` | 原版不转 XP | 是 |
| `Book_QueenOfSauce` | 补齐 Queen of Sauce 菜谱，递增 `Book_QueenOfSauce` | 再次读仍走补齐逻辑，通常新增 0 | 否 |
| `Book_Roe` | `stats.Increment("Book_Roe")` | Fishing +100 XP | 是 |
| `Book_Speed` | `stats.Increment("Book_Speed")` | 五技能各 +20 XP | 是 |
| `Book_Speed2` | `stats.Increment("Book_Speed2")` | 五技能各 +20 XP | 是 |
| `Book_Trash` | `stats.Increment("Book_Trash")` | Foraging +100 XP | 是 |
| `Book_Void` | `stats.Increment("Book_Void")` | Combat +100 XP | 是 |
| `Book_WildSeeds` | `stats.Increment("Book_WildSeeds")` | Foraging +100 XP | 是 |
| `Book_Woodcutting` | `stats.Increment("Book_Woodcutting")` | Foraging +100 XP | 是 |

---

## 七、Powers.json 展示锁定

| ID | DisplayName | Description | TexturePosition | UnlockedCondition |
| --- | --- | --- | --- | --- |
| `Book_PriceCatalogue` | `Strings\\Objects:Book_PriceCatalogue_Name` | `Strings\\Objects:Book_PriceCatalogue_Description` | `32,224` | `PLAYER_STAT Current Book_PriceCatalogue 1` |
| `Book_Marlon` | `Strings\\Objects:Book_Marlon_Name` | `Strings\\Objects:Book_Marlon_Description` | `0,224` | `PLAYER_STAT Current Book_Marlon 1` |
| `Book_Speed` | `Strings\\Objects:Book_Speed_Name` | `Strings\\Objects:Book_Speed_Description` | `112,208` | `PLAYER_STAT Current Book_Speed 1` |
| `Book_Speed2` | `Strings\\Objects:Book_Speed2_Name` | `Strings\\Objects:Book_Speed2_Description` | `112,224` | `PLAYER_STAT Current Book_Speed2 1` |
| `Book_Void` | `Strings\\Objects:Book_Void_Name` | `Strings\\Objects:Book_Void_Description` | `96,208` | `PLAYER_STAT Current Book_Void 1` |
| `Book_Friendship` | `Strings\\Objects:Book_Friendship_Name` | `Strings\\Objects:Book_Friendship_Description` | `80,208` | `PLAYER_STAT Current Book_Friendship 1` |
| `Book_Defense` | `Strings\\Objects:Book_Defense_Name` | `Strings\\Objects:Book_Defense_Description` | `64,208` | `PLAYER_STAT Current Book_Defense 1` |
| `Book_Woodcutting` | `Strings\\Objects:Book_Woodcutting_Name` | `Strings\\Objects:Book_Woodcutting_Description` | `48,208` | `PLAYER_STAT Current Book_Woodcutting 1` |
| `Book_WildSeeds` | `Strings\\Objects:Book_WildSeeds_Name` | `Strings\\Objects:Book_WildSeeds_Description` | `32,208` | `PLAYER_STAT Current Book_WildSeeds 1` |
| `Book_Roe` | `Strings\\Objects:Book_Roe_Name` | `Strings\\Objects:Book_Roe_Description` | `16,208` | `PLAYER_STAT Current Book_Roe 1` |
| `Book_Bombs` | `Strings\\Objects:Book_Bombs_Name` | `Strings\\Objects:Book_Bombs_Description` | `0,208` | `PLAYER_STAT Current Book_Bombs 1` |
| `Book_Crabbing` | `Strings\\Objects:Book_Crabbing_Name` | `Strings\\Objects:Book_Crabbing_Description` | `112,192` | `PLAYER_STAT Current Book_Crabbing 1` |
| `Book_Trash` | `Strings\\Objects:Book_Trash_Name` | `Strings\\Objects:Book_Trash_Description` | `96,192` | `PLAYER_STAT Current Book_Trash 1` |
| `Book_Diamonds` | `Strings\\Objects:Book_Diamonds_Name` | `Strings\\Objects:Book_Diamonds_Description` | `64,224` | `PLAYER_STAT Current Book_Diamonds 1` |
| `Book_Mystery` | `Strings\\Objects:Book_Mystery_Name` | `Strings\\Objects:Book_Mystery_Description` | `80,224` | `PLAYER_STAT Current Book_Mystery 1` |
| `Book_Horse` | `Strings\\Objects:Book_Riding_Name` | `Strings\\Objects:Book_Riding_Description` | `80,272` | `PLAYER_STAT Current Book_Horse 1` |
| `Book_Artifact` | `Strings\\Objects:Book_Artifact_Name` | `Strings\\Objects:Book_Artifact_Description` | `64,272` | `PLAYER_STAT Current Book_Artifact 1` |
| `Book_Grass` | `Strings\\Objects:GrassBook_Name` | `Strings\\Objects:GrassBook_Description` | `16,288` | `PLAYER_STAT Current Book_Grass 1` |
| `Book_AnimalCatalogue` | `Strings\\Objects:Book_AnimalCatalogue_Name` | `Strings\\Objects:Book_AnimalCatalogue_Description` | `96,224` | `PLAYER_STAT Current Book_AnimalCatalogue 1` |

说明：`Book_QueenOfSauce` 不在 `Powers.json`，不属于 Powers/Well Read 的 19 本永久能力书。

---

## 八、永久效果源码锚点

| ID | 效果 | SDV 源码锚点 |
| --- | --- | --- |
| `Book_Trash` | 垃圾桶掉落概率提升 | `源文件/StardewValley/GameLocation.cs` 垃圾桶逻辑，`stats.Get("Book_Trash")`。 |
| `Book_Crabbing` | 蟹笼收获 25% 双倍 | `源文件/StardewValley.Objects/CrabPot.cs`，`CreateDaySaveRandom(...).NextDouble() < 0.25`。 |
| `Book_Bombs` | 炸弹对玩家伤害 ×0.75 | `源文件/StardewValley/GameLocation.cs` 爆炸伤害分支。 |
| `Book_Roe` | 钓鱼宝箱 25% 额外鱼籽 | `源文件/StardewValley.Tools/FishingRod.cs` treasure 分支。 |
| `Book_WildSeeds` | 杂草混合种子掉率 5%→9% | `源文件/StardewValley/Object.cs` 杂草破坏分支。 |
| `Book_Woodcutting` | 砍树 5% 双倍木头 | `源文件/StardewValley.TerrainFeatures/Tree.cs`。 |
| `Book_Defense` | 防御 +1 | `源文件/StardewValley/Farmer.cs` defense getter。 |
| `Book_Friendship` | 正向友谊增长 ×1.1 | `源文件/StardewValley/Farmer.cs` friendship gain。 |
| `Book_Void` | 怪物掉落 3% 复制已有掉落 | `源文件/StardewValley/GameLocation.cs` monster loot。 |
| `Book_Speed` | 非骑马移速 +0.25 | `源文件/StardewValley/Farmer.cs` addedSpeed。 |
| `Book_Marlon` | 死亡找回价格减半 | `源文件/StardewValley.Internal/ItemQueryResolver.cs` Marlon recovery。 |
| `Book_PriceCatalogue` | tooltip 显示可售价格 | `源文件/StardewValley.Menus/IClickableMenu.cs` hover price。 |
| `Book_Diamonds` | 手动镐石 0.66% 掉钻石 | `源文件/StardewValley.Tools/Pickaxe.cs`。 |
| `Book_Mystery` | Mystery Box 概率系数 0.66→0.88 | `源文件/StardewValley/Utility.cs` mystery box 逻辑。 |
| `Book_AnimalCatalogue` | Marnie 不在柜台也能开动物商店 | `源文件/StardewValley/GameLocation.cs` AnimalShop action。 |
| `Book_Speed2` | 非骑马移速再 +0.25 | `源文件/StardewValley/Farmer.cs` addedSpeed。 |
| `Book_Artifact` | 古物售价 ×3 | `源文件/StardewValley/Object.cs` sell price。 |
| `Book_Horse` | 骑马移速 +0.5 | `源文件/StardewValley/Farmer.cs` horse movement。 |
| `Book_Grass` | 草/作物减速 -1→-0.33 | `源文件/StardewValley.TerrainFeatures/Grass.cs` / `HoeDirt.cs`。 |

---

## 九、Shops.json 商店锁定

| Shop | 条目 | 价格/兑换 | Stock | 条件 |
| --- | --- | --- | --- | --- |
| `AnimalShop` | `Book_AnimalCatalogue` | 5000g | Global -1 | `YEAR 2` |
| `Bookseller` | `PurpleBook` | 15000g | Player 1 | `SYNCED_RANDOM day purplebookSale 0.25` |
| `Bookseller` | `RandomSkillBook1` | 10000g | Player 1 | `SYNCED_RANDOM day thirdBookSale 0.6`，随机 5 本技能书 |
| `Bookseller` | `RandomSkillBook2` | 8000g | Player 1 | `SYNCED_RANDOM day secondBookSale 0.8`，随机 5 本技能书，`AvoidRepeat=true` |
| `Bookseller` | `RandomSkillBook3` | 5000g | Player 1 | 无条件，随机 5 本技能书，`AvoidRepeat=true` |
| `Bookseller` | `RandomBook` | 20000g | Player 1 | `YEAR 3`，随机 `Book_Trash/Crabbing/Bombs/Roe/WildSeeds/Woodcutting/Defense/Friendship/Void/Marlon/Artifact` |
| `Bookseller` | `Book_Speed` | 15000g | Player 1 | 无条件 |
| `Bookseller` | `Book_Speed2` | 35000g | Player 1 | `PLAYER_STAT Current Book_Speed 1` |
| `Bookseller` | `Book_Horse` | 25000g | Player 1 | 无条件 |
| `Bookseller` | `Book_Grass` | 25000g | Player 1 | 无条件 |
| `Bookseller` | `Book_QueenOfSauce` | 50000g | Global -1 | 原版：`WORLD_STATE_FIELD GoldenWalnutsFound 100`；StardewCraft 临时映射：Year 3 后出售。TODO：补 Golden Walnuts 世界状态后改回原版条件。 |
| `Bookseller` | `SkillBook_2` | 8000g | Player 1 | `SYNCED_RANDOM day bookExtraForaging 0.33` |
| `Bookseller` | `Book_PriceCatalogue` | 3000g | Global -1 | 无条件 |
| `BooksellerTrade` | random `CaveJelly/RiverJelly/SeaJelly` | 交 `Book_Roe` x1，得 3 个 | Global -1 | `PLAYER_STAT Current Book_Roe 1` |
| `BooksellerTrade` | `(O)709` hardwood | 交 `Book_Woodcutting` x1，得 20 个 | Global -1 | `PLAYER_STAT Current Book_Woodcutting 1` |
| `BooksellerTrade` | `(O)239` stuffing | 交 `Book_Defense` x1，得 3 个 | Global -1 | `PLAYER_STAT Current Book_Defense 1` |
| `BooksellerTrade` | random `(BC)158/(BC)156` | 交 `Book_Void` x2，得 1 个 | Global -1 | `PLAYER_STAT Current Book_Void 1` |
| `BooksellerTrade` | `MysteryBox` | 交 `Book_Mystery` x1，得 7 个 | Global -1 | `PLAYER_STAT Current Book_Mystery 1` |
| `BooksellerTrade` | random `(O)226/(O)275` | 交 `Book_Artifact` x1，得 3 个 | Global -1 | `PLAYER_STAT Current Book_Artifact 1` |
| `BooksellerTrade` | `(O)872` fairy dust | 交 `PurpleBook` x1，得 8 个 | Global -1 | 无条件 |
| `BooksellerTrade` | `(O)215` pepper | 交 `SkillBook_0` x1，得 2 个 | Global -1 | 无条件 |
| `BooksellerTrade` | `DeluxeBait` | 交 `SkillBook_1` x1，得 30 个 | Global -1 | 无条件 |
| `BooksellerTrade` | `(O)388` wood | 交 `SkillBook_2` x1，得 100 个 | Global -1 | 无条件 |
| `BooksellerTrade` | `(O)382` coal | 交 `SkillBook_3` x1，得 20 个 | Global -1 | 无条件 |
| `BooksellerTrade` | `(O)879` monster musk | 交 `SkillBook_4` x1，得 1 个 | Global -1 | 无条件 |
| `DesertFestival_EggShop` | `SkillBook_2` | 交 `CalicoEgg` x100 | Global -1 | 无条件 |
| `Dwarf` | `Book_Bombs` | 4000g | Player 1 | 无条件 |
| `Raccoon` | `Book_WildSeeds` | 交 `(O)771` x999 | Global -1 | `WORLD_STATE_FIELD TimesFedRaccoons 2` |
| `Traveler` | random skill book | 6000g | Global -1 | `SYNCED_RANDOM day travelerSkillBook .05` |
| `VolcanoShop` | `Book_Diamonds` | 交 `(O)72` diamond x10 | Player 1 | 无条件 |

兑换物映射：

| SDV ID | 原版物品 | StardewCraft ID | 状态 |
| --- | --- | --- | --- |
| `(O)709` | Hardwood | `stardewcraft:wood_hard` | 已注册 |
| `(O)239` | Stuffing | `stardewcraft:stuffing` | 已注册 |
| `(BC)158` | Slime Egg-Press | 未找到 | 需要补齐或实现等价机器物品 |
| `(BC)156` | Slime Incubator | 未找到 | 需要补齐或实现等价机器物品 |
| `(O)MysteryBox` | Mystery Box | `stardewcraft:mystery_box` | 已注册 |
| `(O)226` | Spicy Eel | `stardewcraft:spicy_eel` | 已注册 |
| `(O)275` | Artifact Trove | `stardewcraft:artifact_trove` | 已注册 |
| `(O)872` | Fairy Dust | `stardewcraft:fairy_dust` | 已注册 |
| `(O)215` | Pepper Poppers | `stardewcraft:pepper_poppers` | 已注册 |
| `DeluxeBait` | Deluxe Bait | `stardewcraft:deluxe_bait` | 已注册 |
| `(O)388` | Wood | `stardewcraft:wood_normal` | 已注册 |
| `(O)382` | Coal | `stardewcraft:coal` | 已注册 |
| `(O)879` | Monster Musk | 未找到 | 需要补齐物品；已有 `CombatBuffType.MONSTER_MUSK` 概念但未找到 item 注册 |
| `(O)72` | Diamond | `stardewcraft:diamond` | 已注册 |

---

## 十、Bookseller 日期与交互

| 行为 | SDV 源 | 锁定内容 |
| --- | --- | --- |
| 每季出没日 | `源文件/StardewValley/Utility.cs` `getDaysOfBooksellerThisSeason()` | `CreateRandom(year * 11, uniqueIDForThisGame, seasonIndex)`，每季候选日选 index1 和半长环绕的第二天。 |
| 春季候选 | 同上 | `11, 12, 21, 22, 25` |
| 夏季候选 | 同上 | `9, 12, 18, 25, 27` |
| 秋季候选 | 同上 | `4, 7, 8, 9, 12, 19, 22, 25` |
| 冬季候选 | 同上 | `5, 11, 12, 19, 22, 24` |
| 当天早晨提示 | `源文件/StardewValley/Game1.cs` | `BooksellerInTown` global message。 |
| Town 展示 | `源文件/StardewValley.Locations/Town.cs` | 当天 `showBookseller=true`，添加两盏 light source。 |
| 交互 | `源文件/StardewValley/GameLocation.cs` Bookseller action | 当天可开；有 `read_a_book` 时出现 Buy/Trade/Leave；否则直接 `Bookseller` shop。 |

---

## 十一、非商店来源锁定

| 来源 | 书籍 | SDV 源 |
| --- | --- | --- |
| 城镇一次性礼盒 | `Book_Trash` | `源文件/StardewValley.Locations/Town.cs` |
| 冒险家公会一次性礼盒 | `Book_Marlon` | `源文件/StardewValley.Locations/AdventureGuild.cs` |
| 浣熊奖励 | `Book_WildSeeds` / `PurpleBook` | `源文件/StardewValley.Characters/Raccoon.cs` |
| 兑奖券 | `Book_Friendship` | `源文件/StardewValley.Menus/PrizeTicketMenu.cs` |
| 砍树掉落 | `Book_Woodcutting` | `源文件/StardewValley.TerrainFeatures/Tree.cs` |
| 钓鱼宝箱 | `Book_Roe` | `源文件/StardewValley.Tools/FishingRod.cs` |
| 怪物掉落 | `Book_Void` | `源文件/StardewValley/GameLocation.cs` |
| 古物点/相关掉落 | `Book_Defense` | `源文件/StardewValley/Object.cs` |
| Mystery Box / 通用神秘盒逻辑 | `Book_Mystery` / `PurpleBook` | `源文件/StardewValley/Utility.cs` |
| SquidFest 奖励 | `Book_Crabbing` | `源文件/StardewValley/GameLocation.cs` |

---

## 十二、下一步必须补齐的锁定项

1. 补齐 `Monster Musk`、`Slime Egg-Press`、`Slime Incubator`，否则 BooksellerTrade 里 `Book_Void` 和 `SkillBook_4` 的兑换无法 1:1 还原。

---

## 十三、2026/05/23 接入状态与剩余缺口

### 已接入

| 范围 | 当前实现 |
| --- | --- |
| 书摊日期 | `BooksellerSchedule` 使用原版候选日：春 `11,12,21,22,25`；夏 `9,12,18,25,27`；秋 `4,7,8,9,12,19,22,25`；冬 `5,11,12,19,22,24`；随机 index + 半长环绕得到两天。 |
| 书摊出现 | `BooksellerEvents` 仅在书摊日生成固定 NPC；非书摊日释放 chunk 并移除托管实体；早晨广播 `BooksellerInTown` 等价消息。 |
| 书摊库存 | Bookseller 主商店随机/固定书改为当日 per-player 限购 1；BooksellerTrade 保持无限兑换；Price Catalogue 不接入。 |
| 砍树来源 | 对齐 `Tree.cs`：`TreesChopped > 20` 后按 `0.0003 + (已掉过 ? 0.0007 : TreesChopped * 0.00001)` 掉 `Book_Woodcutting`，设置 `GotWoodcuttingBook`。 |
| 钓鱼宝箱来源 | 对齐 `FishingRod.cs`：Fishing level > 4 且 `FishingTreasures > 2` 后按 `0.02 + (已掉过 ? 0.001 : FishingTreasures * 0.001)` 额外给 `Book_Roe`，设置 `roeBookDropped`。 |
| 怪物来源 | 对齐 `GameLocation.cs`：`MonstersKilled > 10` 后按 `0.0001 + (已掉过 ? 0.0004 : MonstersKilled * 0.000015)` 掉 `Book_Void`，设置 `voidBookDropped`。 |
| 远古斑点来源 | 对齐 `Object.cs`：`ArtifactSpotsDug > 2` 后按 `0.008 + (已掉过 ? 0.005 : ArtifactSpotsDug * 0.002)` 掉 `Book_Defense`，设置 `DefenseBookDropped`。 |
| Mystery Box 来源 | 对齐 `Utility.cs` 稀有池逻辑：未获得时掉 `Book_Mystery` 并设置 `GotMysteryBook`；之后在 `PurpleBook` / `Book_Mystery` 间随机。 |

### 仍缺或受阻

| 缺口 | 原因/下一步 |
| --- | --- |
| 书摊日历 Billboard 图标 | 日历 UI 尚未接入 bookseller day marker；后续在日历/公告板 UI 接入 `BooksellerSchedule.daysForSeason`。 |
| `Book_QueenOfSauce` 书摊条件 | 已按拍板使用 Year 3 临时条件。TODO：补 Golden Walnuts 世界状态后改回 `GoldenWalnutsFound >= 100`。 |
| `Book_Trash` 城镇礼盒、`Book_Marlon` 冒险家公会礼盒 | 本阶段先不做；后续需要用户录入 MC 坐标与礼盒外观后再接。 |
| `Book_WildSeeds` / `PurpleBook` 浣熊奖励 | 本阶段不做浣熊奖励链。 |
| `Book_Friendship` 兑奖券 | 本阶段不做 Prize Ticket 菜单/兑奖系统。 |
| `Book_Crabbing` SquidFest 奖励 | 本阶段不做鱿鱼节计分/奖励系统。 |
| `Book_Diamonds` VolcanoShop、`SkillBook_2` DesertFestival_EggShop | 本阶段不做对应商店/货币/节日商店。 |
| `Book_Void` 与 `SkillBook_4` 兑换物 | 已按拍板做项目映射：`big_chest` / `big_stone_chest` 映射为 `wooden_chest` / `stone_chest`；`monster_musk` 映射为 `bat_wing` x30。 |
| Powers 展示与 Well Read 成就表现 | Powers 页可后续直接接书本 stat；Well Read 暂不做正式 achievement/advancement，保留现有读齐提示。 |