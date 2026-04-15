# StardewCraft Wiki 开发总规划

> 本文档是 wiki 站点的完整开发蓝图。目标：**覆盖模组中每一个物品、每一个方块、每一个实体、每一条机制，不遗漏任何内容。**

---

## 〇、编写原则与踩坑记录

### 编写原则

1. **一切数据以源码为准**
   - 物品名称必须与 `zh_cn.json` 语言文件中的 key 完全一致（例如是「蒜」不是「大蒜」，是「甘蓝菜」不是「羽衣甘蓝」，是「蓝爵」不是「蓝色爵士花」，是「青豆」不是「四季豆」，是「上古水果」不是「远古水果」）。
   - 数值（售价、生长天数、能量、生命等）必须从 Java 源码中的 `SELL_PRICE_BY_QUALITY`、`PHASE_DAYS`、`ENERGY_BY_QUALITY`、`HEALTH_BY_QUALITY` 等字段提取，不可从星露谷 wiki 或第三方资料照搬。
   - 物品描述和风味文本必须使用 `zh_cn.json` 中对应的 `.desc` 和 `.flavor` 原文。

2. **面向玩家，不面向开发者**
   - Wiki 是给玩家看的，不能出现源码函数名、变量名、类名等技术性表述（如 ~~`getExtraHarvestChance() → 0.2`~~、~~`与源码 XXX 一致`~~）。
   - 机制用自然语言描述，例如「收获时有 20% 的概率额外获得一个土豆」。

3. **贴图路径必须对应实际文件**
   - 写入 wiki 前先确认 `wiki/public/img/` 下的真实文件名。
   - 有颜色变体的物品（如郁金香、蓝爵）文件名带数字后缀（`tulip0.png` ~ `tulip4.png`），不存在不带数字的 `tulip.png`。
   - 默认使用第 0 号变体作为展示图。

4. **分类遵循源码中的归类**
   - 水果/蔬菜的分类依据 `PreservesCropTypeHelper` 中的 `FRUIT_CROPS` / `VEGETABLE_CROPS` 列表。
   - 花卉在源码中没有独立类型 key（不在水果/蔬菜列表中即为花卉或特殊类型）。

5. **特殊机制必须标注**
   - 镰刀收割（甘蓝菜等）、额外掉落概率（土豆 20%）、固定产量（咖啡豆每次 4 个）、不可食用（大黄）、不可出售种子（上古水果）等。
   - 来源同样是源码（`getHarvestMethod()`、`getExtraHarvestChance()`、`getHarvestMinStack/MaxStack`、`isFood`、`sellPrice = -1`），但描述时只写结论，不写代码。

### 踩坑记录

| # | 问题 | 错误示例 | 正确做法 |
|---|------|---------|---------|
| 1 | 照搬星露谷 wiki 数据而非源码 | 大蒜售价写成 60/75/90/120 | 从 `GarlicItem.java` 读取实际值 40/60/75/90 |
| 2 | 物品名用了自己翻译的名字 | 「大蒜」「羽衣甘蓝」「蓝色爵士花」「四季豆」「远古水果」 | 以 `zh_cn.json` 为准：「蒜」「甘蓝菜」「蓝爵」「青豆」「上古水果」 |
| 3 | 贴图路径不存在 | `tulip.png`、`blue_jazz.png` | 有颜色变体的用 `tulip0.png`、`blue_jazz0.png` |
| 4 | wiki 中写源码引用 | `源码 getExtraHarvestChance() → 0.2` | 「收获时有 20% 的概率额外获得一个」 |
| 5 | 种子价格混淆 | 把皮埃尔商店购买价当成种子售价 | 源码中 `seedSellPrice` 才是种子的出售价格 |

---

## 一、Wiki 整体架构

```
wiki/
├── index.md                      # 首页（Hero + 6 大分类入口）
├── changelog.md                  # 更新日志
├── guide/                        # 新手指南
│   ├── getting-started.md        # 快速开始
│   ├── installation.md           # 安装与兼容
│   └── faq.md                    # 常见问题
├── wiki/
│   ├── crops/                    # 作物系统
│   ├── farming/                  # 农牧综合
│   ├── mining/                   # 矿洞系统
│   ├── fishing/                  # 钓鱼系统
│   ├── foraging/                 # 觅食系统
│   ├── combat/                   # 战斗系统
│   ├── skills/                   # 技能与职业
│   ├── npc/                      # NPC 与社交
│   ├── crafting/                 # 烹饪与合成
│   ├── machines/                 # 加工机器
│   ├── items/                    # 物品总表
│   ├── equipment/                # 装备系统
│   ├── buildings/                # 建筑管理
│   ├── animals/                  # 动物系统
│   ├── quests/                   # 任务系统
│   ├── world/                    # 世界与维度
│   ├── economy/                  # 经济系统
│   └── furniture/                # 家具装饰
└── reference/                    # 数据速查
    ├── all-items.md              # 全物品索引
    ├── all-blocks.md             # 全方块索引
    └── version-parity.md         # SDV 对标进度
```

---

## 二、内容优先级分层

### P0 — 核心系统页面（首批完成）
玩家每次游玩必接触的系统，也是搜索量最高的内容。

### P1 — 重要子系统页面（第二批）
深度玩家查阅频率高的内容。

### P2 — 百科补全页面（第三批）
完整性收录，确保每个物品都有页面。

### P3 — 辅助与参考页面（持续维护）
索引、对比表、进度跟踪等参考性内容。

---

## 三、全量页面清单与进度跟踪

### 3.1 指南 (Guide) — 3 页

| # | 页面 | 路径 | 优先级 | 状态 |
|---|------|------|--------|------|
| G1 | 快速开始 | `/guide/getting-started` | P0 | ✅ 骨架 |
| G2 | 安装与兼容 | `/guide/installation` | P0 | ✅ 骨架 |
| G3 | 常见问题 | `/guide/faq` | P1 | ⬜ |

---

### 3.2 作物系统 (Crops) — 43 页

> 39 种作物 + 4 个野生种子作物 = 43 个作物方块，每个作物一页 + 3 个季节总览 + 1 个系统说明 = **47 页**

#### 3.2.1 总览页

| # | 页面 | 路径 | 优先级 | 状态 |
|---|------|------|--------|------|
| C0 | 作物系统说明 | `/wiki/crops/` | P0 | ✅ 骨架 |
| C1 | 春季作物总览 | `/wiki/crops/spring` | P0 | ✅ 骨架 |
| C2 | 夏季作物总览 | `/wiki/crops/summer` | P0 | ⬜ 占位 |
| C3 | 秋季作物总览 | `/wiki/crops/fall` | P0 | ⬜ 占位 |

#### 3.2.2 春季作物（13 种）

| # | 作物 | 成熟天数 | 再生 | 路径 | 优先级 |
|---|------|---------|------|------|--------|
| C-S01 | 防风草 Parsnip | 4 天 | ❌ | `/wiki/crops/parsnip` | P0 |
| C-S02 | 青豆 Green Bean | 10 天 | 3 天 | `/wiki/crops/green-bean` | P0 |
| C-S03 | 花椰菜 Cauliflower | 12 天 | ❌ | `/wiki/crops/cauliflower` | P0 |
| C-S04 | 土豆 Potato | 6 天 | ❌ | `/wiki/crops/potato` | P0 |
| C-S05 | 蒜 Garlic | 4 天 | ❌ | `/wiki/crops/garlic` | P1 |
| C-S06 | 甘蓝菜 Kale | 6 天 | ❌ | `/wiki/crops/kale` | P1 |
| C-S07 | 大黄 Rhubarb | 13 天 | ❌ | `/wiki/crops/rhubarb` | P1 |
| C-S08 | 草莓 Strawberry | 8 天 | 4 天 | `/wiki/crops/strawberry` | P0 |
| C-S09 | 咖啡豆 Coffee Bean | 10 天 | 2 天 | `/wiki/crops/coffee-bean` | P0 |
| C-S10 | 胡萝卜 Carrot | 3 天 | ❌ | `/wiki/crops/carrot` | P1 |
| C-S11 | 郁金香 Tulip | 6 天 | ❌ | `/wiki/crops/tulip` | P2 |
| C-S12 | 蓝爵 Blue Jazz | 7 天 | ❌ | `/wiki/crops/blue-jazz` | P2 |
| C-S13 | 春季野生种子作物 | — | ❌ | `/wiki/crops/spring-wild` | P2 |

#### 3.2.3 夏季作物（14 种）

| # | 作物 | 成熟天数 | 再生 | 路径 | 优先级 |
|---|------|---------|------|------|--------|
| C-U01 | 甜瓜 Melon | 12 天 | ❌ | `/wiki/crops/melon` | P0 |
| C-U02 | 番茄 Tomato | 11 天 | 4 天 | `/wiki/crops/tomato` | P0 |
| C-U03 | 蓝莓 Blueberry | 13 天 | 4 天 | `/wiki/crops/blueberry` | P0 |
| C-U04 | 辣椒 Hot Pepper | 5 天 | 3 天 | `/wiki/crops/hot-pepper` | P0 |
| C-U05 | 小麦 Wheat | 4 天 | ❌ | `/wiki/crops/wheat` | P1 |
| C-U06 | 萝卜 Radish | 6 天 | ❌ | `/wiki/crops/radish` | P1 |
| C-U07 | 红甘蓝 Red Cabbage | 9 天 | ❌ | `/wiki/crops/red-cabbage` | P1 |
| C-U08 | 星之果 Starfruit | 13 天 | ❌ | `/wiki/crops/starfruit` | P0 |
| C-U09 | 玉米 Corn | 14 天 | 4 天 | `/wiki/crops/corn` | P0 |
| C-U10 | 啤酒花 Hops | 11 天 | 1 天 | `/wiki/crops/hops` | P0 |
| C-U11 | 向日葵 Sunflower | 8 天 | ❌ | `/wiki/crops/sunflower` | P2 |
| C-U12 | 虞美人 Poppy | 7 天 | ❌ | `/wiki/crops/poppy` | P2 |
| C-U13 | 夏季南瓜 Summer Squash | 6 天 | 3 天 | `/wiki/crops/summer-squash` | P1 |
| C-U14 | 夏季野生种子作物 | — | ❌ | `/wiki/crops/summer-wild` | P2 |

#### 3.2.4 秋季作物（12 种）

| # | 作物 | 成熟天数 | 再生 | 路径 | 优先级 |
|---|------|---------|------|------|--------|
| C-F01 | 茄子 Eggplant | 5 天 | 5 天 | `/wiki/crops/eggplant` | P0 |
| C-F02 | 玉米 Corn (跨季) | — | — | (同 C-U09) | — |
| C-F03 | 洋蓟 Artichoke | 8 天 | ❌ | `/wiki/crops/artichoke` | P1 |
| C-F04 | 南瓜 Pumpkin | 13 天 | ❌ | `/wiki/crops/pumpkin` | P0 |
| C-F05 | 白菜 Bok Choy | 4 天 | ❌ | `/wiki/crops/bok-choy` | P1 |
| C-F06 | 山药 Yam | 10 天 | ❌ | `/wiki/crops/yam` | P1 |
| C-F07 | 蔓越莓 Cranberry | 7 天 | 5 天 | `/wiki/crops/cranberry` | P0 |
| C-F08 | 甜菜 Beet | 6 天 | ❌ | `/wiki/crops/beet` | P1 |
| C-F09 | 葡萄 Grape | 10 天 | 3 天 | `/wiki/crops/grape` | P0 |
| C-F10 | 苋菜 Amaranth | 7 天 | ❌ | `/wiki/crops/amaranth` | P1 |
| C-F11 | 西兰花 Broccoli | 8 天 | 4 天 | `/wiki/crops/broccoli` | P1 |
| C-F12 | 粉甜瓜 Powder Melon | 7 天 | ❌ | `/wiki/crops/powder-melon` | P1 |
| C-F13 | 仙女玫瑰 Fairy Rose | 12 天 | ❌ | `/wiki/crops/fairy-rose` | P2 |
| C-F14 | 夏日绣球 Summer Spangle (跨季花) | 8 天 | ❌ | `/wiki/crops/summer-spangle` | P2 |
| C-F15 | 秋季野生种子作物 | — | ❌ | `/wiki/crops/fall-wild` | P2 |

#### 3.2.5 特殊作物

| # | 作物 | 成熟天数 | 再生 | 路径 | 优先级 |
|---|------|---------|------|------|--------|
| C-X01 | 上古水果 Ancient Fruit | 28 天 | 7 天 | `/wiki/crops/ancient-fruit` | P0 |
| C-X02 | 冬季野生种子作物 | — | ❌ | `/wiki/crops/winter-wild` | P2 |

---

### 3.3 觅食系统 (Foraging) — 30 页

#### 3.3.1 总览

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| F0 | 觅食系统说明 | `/wiki/foraging/` | P0 |
| F1 | 春季觅食 | `/wiki/foraging/spring` | P0 |
| F2 | 夏季觅食 | `/wiki/foraging/summer` | P0 |
| F3 | 秋季觅食 | `/wiki/foraging/fall` | P0 |
| F4 | 冬季觅食 | `/wiki/foraging/winter` | P0 |
| F5 | 海滩觅食 | `/wiki/foraging/beach` | P1 |
| F6 | 沙漠觅食 | `/wiki/foraging/desert` | P1 |
| F7 | 洞穴蘑菇 | `/wiki/foraging/cave-mushrooms` | P1 |

#### 3.3.2 觅食物品清单（每项含在季节总览页）

**春季 (5):** 野辣根、水仙花、韭葱、蒲公英、春洋葱
**夏季 (3):** 香味浆果、甜豌豆、蕨菜
**秋季 (3):** 野梅、榛子、黑莓
**冬季 (4):** 冬根、水晶果、番红花、冬青树
**海滩 (4):** 鹦鹉螺壳、珊瑚、彩虹贝壳、海胆
**沙漠 (2):** 椰子、仙人掌果
**蘑菇洞 (6):** 普通蘑菇、红蘑菇、紫蘑菇、羊肚菌、鸡油菌、魔法蘑菇帽
**通用 (1):** 洞穴胡萝卜

---

### 3.4 钓鱼系统 (Fishing) — 85 页

#### 3.4.1 系统页面（8 页）

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| FI0 | 钓鱼系统总览 | `/wiki/fishing/` | P0 |
| FI1 | 钓鱼小游戏机制 | `/wiki/fishing/minigame` | P0 |
| FI2 | 鱼饵系统 | `/wiki/fishing/bait` | P0 |
| FI3 | 浮漂与配件 | `/wiki/fishing/tackle` | P0 |
| FI4 | 鱼竿升级 | `/wiki/fishing/rods` | P1 |
| FI5 | 蟹笼 | `/wiki/fishing/crab-pot` | P1 |
| FI6 | 钓鱼按地点索引 | `/wiki/fishing/by-location` | P0 |
| FI7 | 钓鱼按季节索引 | `/wiki/fishing/by-season` | P0 |

#### 3.4.2 鱼类个体页面（74 种 = 74 页）

**河鱼 (9):** 鲷鱼、鲶鱼、鲢鱼、狗鱼、鲑鱼、西鲱、小嘴鲈鱼、虎纹鳟鱼、大眼鱼

**湖鱼 (10):** 大头鱼、鲤鱼、大嘴鲈鱼、江鳕、午夜鲤鱼、鲈鱼、虹鳟鱼、鲟鱼、太阳鱼、木跳鱼

**海鱼 (16):** 长鳍金枪鱼、凤尾鱼、鳗鱼、比目鱼、庸鰈鱼、鲱鱼、章鱼、河豚、红鲻鱼、红鲷鱼、沙丁鱼、海参、鱿鱼、大海参、罗非鱼、金枪鱼

**特殊鱼 (12):** 水滴鱼、多拉多鱼、鬼鱼、冰笋、熔岩鳗鱼、午夜鱿鱼、沙鱼、蝎鲤、粘液鱼、幽灵鱼、石鱼、虚空鲑鱼

**传奇鱼 (10):** 垂钓者、深红鱼、冰川鱼、冰川鱼二世、传说之鱼 II、传说之鱼、安格勒夫人、变异鲤鱼、放射鲤鱼、深红之子

**蟹笼 (11):** 蛤蜊、鸟蛤、螃蟹、小龙虾、龙虾、贻贝、牡蛎、蟹足蜗牛、虾、蜗牛

**杂项 (6):** 洞穴果冻、绿藻、河果冻、海果冻、海草、白藻

#### 3.4.3 钓鱼装备页面（含在系统页）

**饵料 (7):** 普通鱼饵、磁铁、野生鱼饵、魔法鱼饵、高级鱼饵、挑战鱼饵、定向鱼饵

**浮漂/配件 (10):** 旋转器、闪亮旋转器、陷阱浮漂、软木浮漂、铅坠浮漂、寻宝者、倒刺钩、好奇诱饵、品质浮漂、声纳浮漂

---

### 3.5 矿洞与采矿 (Mining) — 22 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| M0 | 采矿系统总览 | `/wiki/mining/` | P0 |
| M1 | 矿洞楼层机制 | `/wiki/mining/floors` | P0 |
| M2 | 矿洞电梯 | `/wiki/mining/elevator` | P1 |
| M3 | 矿洞木桶与战利品 | `/wiki/mining/barrels` | P1 |
| M4 | 矿石分布表 | `/wiki/mining/ore-distribution` | P0 |

**矿石（5 种金属矿 + 7 种宝石矿 = 12 页）：**

| # | 矿石 | 路径 | 优先级 |
|---|------|------|--------|
| M-O01 | 铜矿石 Copper Ore | `/wiki/mining/copper` | P0 |
| M-O02 | 铁矿石 Iron Ore | `/wiki/mining/iron` | P0 |
| M-O03 | 金矿石 Gold Ore | `/wiki/mining/gold` | P0 |
| M-O04 | 铱矿石 Iridium Ore | `/wiki/mining/iridium` | P0 |
| M-O05 | 煤矿石 Coal Ore | `/wiki/mining/coal` | P0 |
| M-O06 | 紫水晶矿 Amethyst Ore | `/wiki/mining/amethyst` | P1 |
| M-O07 | 海蓝宝石矿 Aquamarine Ore | `/wiki/mining/aquamarine` | P1 |
| M-O08 | 钻石矿 Diamond Ore | `/wiki/mining/diamond` | P1 |
| M-O09 | 祖母绿矿 Emerald Ore | `/wiki/mining/emerald` | P1 |
| M-O10 | 翡翠矿 Jade Ore | `/wiki/mining/jade` | P1 |
| M-O11 | 红宝石矿 Ruby Ore | `/wiki/mining/ruby` | P1 |
| M-O12 | 黄玉矿 Topaz Ore | `/wiki/mining/topaz` | P1 |

**矿物收集品 (4):** 石英、大地水晶、冰泪石、火石英

**矿洞石材 (6):** 大地页岩、寒霜片麻岩、熔岩玄武岩（含暗色变体）

---

### 3.6 战斗系统 (Combat) — 52 页

#### 3.6.1 系统页面

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| CB0 | 战斗系统总览 | `/wiki/combat/` | P0 |
| CB1 | 伤害计算公式 | `/wiki/combat/damage` | P0 |
| CB2 | 增益与减益效果 | `/wiki/combat/buffs` | P1 |

#### 3.6.2 武器（45 种 = 45 页）

**剑类 (26):**

| # | 武器 | 等级 | 路径 | 优先级 |
|---|------|------|------|--------|
| W-S01 | 生锈的剑 Rusty Sword | Lv.1 | `/wiki/combat/rusty-sword` | P1 |
| W-S02 | 钢制小剑 Steel Smallsword | Lv.1 | `/wiki/combat/steel-smallsword` | P2 |
| W-S03 | 木剑 Wooden Blade | Lv.1 | `/wiki/combat/wooden-blade` | P2 |
| W-S04 | 海盗剑 Pirate Sword | Lv.2 | `/wiki/combat/pirate-sword` | P2 |
| W-S05 | 银色军刀 Silver Saber | Lv.2 | `/wiki/combat/silver-saber` | P2 |
| W-S06 | 弯刀 Cutlass | Lv.3 | `/wiki/combat/cutlass` | P2 |
| W-S07 | 森林之剑 Forest Sword | Lv.3 | `/wiki/combat/forest-sword` | P2 |
| W-S08 | 铁刃 Iron Edge | Lv.3 | `/wiki/combat/iron-edge` | P2 |
| W-S09 | 喵喵剑 Meowmere | Lv.4 | `/wiki/combat/meowmere` | P1 |
| W-S10 | 骨剑 Bone Sword | Lv.5 | `/wiki/combat/bone-sword` | P2 |
| W-S11 | 阔剑 Claymore | Lv.5 | `/wiki/combat/claymore` | P2 |
| W-S12 | 海神戟 Neptune's Glaive | Lv.5 | `/wiki/combat/neptunes-glaive` | P1 |
| W-S13 | 圣殿骑士之刃 Templar's Blade | Lv.5 | `/wiki/combat/templars-blade` | P2 |
| W-S14 | 虫头刀 Insect Head | Lv.6 | `/wiki/combat/insect-head` | P1 |
| W-S15 | 黑曜石之刃 Obsidian Edge | Lv.6 | `/wiki/combat/obsidian-edge` | P1 |
| W-S16 | 骨化之刃 Ossified Blade | Lv.6 | `/wiki/combat/ossified-blade` | P2 |
| W-S17 | 圣剑 Holy Blade | Lv.7 | `/wiki/combat/holy-blade` | P2 |
| W-S18 | 淬火阔刀 Tempered Broadsword | Lv.7 | `/wiki/combat/tempered-broadsword` | P2 |
| W-S19 | 雪人之牙 Yeti Tooth | Lv.7 | `/wiki/combat/yeti-tooth` | P2 |
| W-S20 | 钢弧弯刀 Steel Falchion | Lv.8 | `/wiki/combat/steel-falchion` | P2 |
| W-S21 | 暗黑之剑 Dark Sword | Lv.9 | `/wiki/combat/dark-sword` | P1 |
| W-S22 | 熔岩武士刀 Lava Katana | Lv.10 | `/wiki/combat/lava-katana` | P0 |
| W-S23 | 龙牙弯刀 Dragontooth Cutlass | Lv.13 | `/wiki/combat/dragontooth-cutlass` | P1 |
| W-S24 | 矮人剑 Dwarf Sword | Lv.13 | `/wiki/combat/dwarf-sword` | P1 |
| W-S25 | 银河之剑 Galaxy Sword | Lv.13 | `/wiki/combat/galaxy-sword` | P0 |
| W-S26 | 无限之刃 Infinity Blade | Lv.17 | `/wiki/combat/infinity-blade` | P0 |

**匕首类 (16):**

| # | 武器 | 等级 | 路径 | 优先级 |
|---|------|------|------|--------|
| W-D01 | 雕刻刀 Carving Knife | Lv.1 | `/wiki/combat/carving-knife` | P2 |
| W-D02 | 铁短剑 Iron Dirk | Lv.1 | `/wiki/combat/iron-dirk` | P2 |
| W-D03 | 风之尖 Wind Spire | Lv.1 | `/wiki/combat/wind-spire` | P2 |
| W-D04 | 精灵之刃 Elf Blade | Lv.2 | `/wiki/combat/elf-blade` | P2 |
| W-D05 | 窃贼之刃 Burglar's Shank | Lv.4 | `/wiki/combat/burglars-shank` | P2 |
| W-D06 | 水晶匕首 Crystal Dagger | Lv.4 | `/wiki/combat/crystal-dagger` | P2 |
| W-D07 | 暗影匕首 Shadow Dagger | Lv.4 | `/wiki/combat/shadow-dagger` | P2 |
| W-D08 | 邪恶短剑 Wicked Kris | Lv.8 | `/wiki/combat/wicked-kris` | P2 |
| W-D09 | 银河匕首 Galaxy Dagger | Lv.8 | `/wiki/combat/galaxy-dagger` | P1 |
| W-D10 | 矮人匕首 Dwarf Dagger | Lv.11 | `/wiki/combat/dwarf-dagger` | P1 |
| W-D11 | 铱针 Iridium Needle | Lv.12 | `/wiki/combat/iridium-needle` | P1 |
| W-D12 | 龙牙匕 Dragontooth Shiv | Lv.12 | `/wiki/combat/dragontooth-shiv` | P1 |
| W-D13 | 无限匕首 Infinity Dagger | Lv.16 | `/wiki/combat/infinity-dagger` | P0 |
| W-D14 | 折断的三叉戟 Broken Trident | Lv.5 | `/wiki/combat/broken-trident` | P2 |
| W-D15 | 其他匕首 (待确认) | — | — | P2 |
| W-D16 | 其他匕首 (待确认) | — | — | P2 |

**棍棒类 (1+):**

| # | 武器 | 等级 | 路径 | 优先级 |
|---|------|------|------|--------|
| W-C01 | 大腿骨 Femur | Lv.2 | `/wiki/combat/femur` | P2 |

---

### 3.7 装备系统 (Equipment) — 50 页

#### 3.7.1 总览

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| EQ0 | 装备系统总览 | `/wiki/equipment/` | P0 |
| EQ1 | 靴子总览 | `/wiki/equipment/boots` | P0 |
| EQ2 | 戒指总览 | `/wiki/equipment/rings` | P0 |

#### 3.7.2 靴子（17 种）

| # | 靴子 | 路径 | 优先级 |
|---|------|------|--------|
| B01 | 运动鞋 Sneakers | `/wiki/equipment/sneakers` | P2 |
| B02 | 橡胶靴 Rubber Boots | `/wiki/equipment/rubber-boots` | P2 |
| B03 | 皮靴 Leather Boots | `/wiki/equipment/leather-boots` | P1 |
| B04 | 工靴 Work Boots | `/wiki/equipment/work-boots` | P2 |
| B05 | 战斗靴 Combat Boots | `/wiki/equipment/combat-boots` | P1 |
| B06 | 冻原靴 Tundra Boots | `/wiki/equipment/tundra-boots` | P1 |
| B07 | 保温靴 Thermal Boots | `/wiki/equipment/thermal-boots` | P2 |
| B08 | 暗黑靴 Dark Boots | `/wiki/equipment/dark-boots` | P1 |
| B09 | 火行者靴 Firewalker Boots | `/wiki/equipment/firewalker-boots` | P1 |
| B10 | 精灵鞋 Genie Shoes | `/wiki/equipment/genie-shoes` | P2 |
| B11 | 太空靴 Space Boots | `/wiki/equipment/space-boots` | P0 |
| B12 | 牛仔靴 Cowboy Boots | `/wiki/equipment/cowboy-boots` | P2 |
| B13 | 小妖精鞋 Leprechaun Shoes | `/wiki/equipment/leprechaun-shoes` | P2 |
| B14 | 灰烬小丑鞋 Cinderclown Shoes | `/wiki/equipment/cinderclown-shoes` | P2 |
| B15 | 人鱼靴 Mermaid Boots | `/wiki/equipment/mermaid-boots` | P1 |
| B16 | 龙鳞靴 Dragonscale Boots | `/wiki/equipment/dragonscale-boots` | P1 |
| B17 | 水晶鞋 Crystal Shoes | `/wiki/equipment/crystal-shoes` | P1 |

#### 3.7.3 戒指（30 种）

| # | 戒指 | 路径 | 优先级 |
|---|------|------|--------|
| R01 | 小萤火虫戒指 Small Glow Ring | `/wiki/equipment/small-glow-ring` | P2 |
| R02 | 萤火虫戒指 Glow Ring | `/wiki/equipment/glow-ring` | P1 |
| R03 | 小磁力戒指 Small Magnet Ring | `/wiki/equipment/small-magnet-ring` | P2 |
| R04 | 磁力戒指 Magnet Ring | `/wiki/equipment/magnet-ring` | P1 |
| R05 | 史莱姆克星戒指 Slime Charmer Ring | `/wiki/equipment/slime-charmer-ring` | P1 |
| R06 | 战士戒指 Warrior Ring | `/wiki/equipment/warrior-ring` | P1 |
| R07 | 吸血鬼戒指 Vampire Ring | `/wiki/equipment/vampire-ring` | P0 |
| R08 | 野蛮戒指 Savage Ring | `/wiki/equipment/savage-ring` | P1 |
| R09 | 尤巴之戒 Ring of Yoba | `/wiki/equipment/ring-of-yoba` | P0 |
| R10 | 坚固戒指 Sturdy Ring | `/wiki/equipment/sturdy-ring` | P1 |
| R11 | 窃贼戒指 Burglar's Ring | `/wiki/equipment/burglars-ring` | P1 |
| R12 | 铱护带 Iridium Band | `/wiki/equipment/iridium-band` | P0 |
| R13 | 紫水晶戒指 Amethyst Ring | `/wiki/equipment/amethyst-ring` | P2 |
| R14 | 黄玉戒指 Topaz Ring | `/wiki/equipment/topaz-ring` | P2 |
| R15 | 海蓝宝石戒指 Aquamarine Ring | `/wiki/equipment/aquamarine-ring` | P2 |
| R16 | 翡翠戒指 Jade Ring | `/wiki/equipment/jade-ring` | P2 |
| R17 | 祖母绿戒指 Emerald Ring | `/wiki/equipment/emerald-ring` | P2 |
| R18 | 红宝石戒指 Ruby Ring | `/wiki/equipment/ruby-ring` | P2 |
| R19 | 蟹壳戒指 Crabshell Ring | `/wiki/equipment/crabshell-ring` | P1 |
| R20 | 凝固汽油弹戒指 Napalm Ring | `/wiki/equipment/napalm-ring` | P1 |
| R21 | 荆棘戒指 Thorns Ring | `/wiki/equipment/thorns-ring` | P1 |
| R22 | 幸运戒指 Lucky Ring | `/wiki/equipment/lucky-ring` | P0 |
| R23 | 热咖啡戒指 Hot Java Ring | `/wiki/equipment/hot-java-ring` | P1 |
| R24 | 保护戒指 Protection Ring | `/wiki/equipment/protection-ring` | P1 |
| R25 | 灵魂吸取戒指 Soul Sapper Ring | `/wiki/equipment/soul-sapper-ring` | P1 |
| R26 | 凤凰戒指 Phoenix Ring | `/wiki/equipment/phoenix-ring` | P0 |
| R27 | 免疫护带 Immunity Band | `/wiki/equipment/immunity-band` | P1 |
| R28 | 萤石戒指 Glowstone Ring | `/wiki/equipment/glowstone-ring` | P1 |
| R29-30 | (待确认的其他戒指) | — | P2 |

---

### 3.8 工具 (Tools) — 8 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| T0 | 工具系统总览 | `/wiki/items/tools` | P0 |
| T1 | 镐 Pickaxe (5 阶) | `/wiki/items/pickaxe` | P0 |
| T2 | 斧 Axe (5 阶) | `/wiki/items/axe` | P0 |
| T3 | 锄头 Hoe (5 阶) | `/wiki/items/hoe` | P0 |
| T4 | 水壶 Watering Can (5 阶) | `/wiki/items/watering-can` | P0 |
| T5 | 镰刀 Scythe (3 阶) | `/wiki/items/scythe` | P0 |
| T6 | 鱼竿 Fishing Rod (5 种) | `/wiki/items/fishing-rod` | P0 |
| T7 | 其他工具 (奶桶/剪刀/画笔等) | `/wiki/items/utility-tools` | P1 |

---

### 3.9 技能与职业 (Skills) — 12 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| SK0 | 技能系统总览 | `/wiki/skills/` | P0 |
| SK1 | 耕种 Farming | `/wiki/skills/farming` | P0 |
| SK2 | 钓鱼 Fishing | `/wiki/skills/fishing` | P0 |
| SK3 | 觅食 Foraging | `/wiki/skills/foraging` | P0 |
| SK4 | 采矿 Mining | `/wiki/skills/mining` | P0 |
| SK5 | 战斗 Combat | `/wiki/skills/combat` | P0 |

**职业详情页 (6 页，每技能 1 页含 6 职业):**

| # | 页面 | 内含职业数 | 路径 | 优先级 |
|---|------|-----------|------|--------|
| PR1 | 耕种职业 | 6 (牧场主/农耕者/鸡舍大师/牧羊人/工匠/农学家) | `/wiki/skills/farming-professions` | P0 |
| PR2 | 钓鱼职业 | 6 (渔夫/捕猎者/垂钓者/海盗/水手/诱饵大师) | `/wiki/skills/fishing-professions` | P0 |
| PR3 | 觅食职业 | 6 (护林人/采集者/伐木工/采集者/植物学家/追踪者) | `/wiki/skills/foraging-professions` | P0 |
| PR4 | 采矿职业 | 6 (矿工/地质学家/铁匠/探矿者/挖掘者/宝石学家) | `/wiki/skills/mining-professions` | P0 |
| PR5 | 战斗职业 | 6 (战士/侦察兵/蛮力/防御者/杂技师/亡命徒) | `/wiki/skills/combat-professions` | P0 |

---

### 3.10 NPC 与社交 — 37+ 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| NPC0 | NPC 系统总览 | `/wiki/npc/` | P0 |
| NPC1 | 好感度机制 | `/wiki/npc/friendship` | P0 |
| NPC2 | 送礼指南 | `/wiki/npc/gifts` | P0 |
| NPC3 | 日程系统 | `/wiki/npc/schedules` | P1 |

**NPC 个体页面（每人一页，约 33 人）：**

| # | NPC | 路径 | 优先级 |
|---|-----|------|--------|
| N01 | Alex | `/wiki/npc/alex` | P1 |
| N02 | Abigail | `/wiki/npc/abigail` | P0 |
| N03 | Caroline | `/wiki/npc/caroline` | P1 |
| N04 | Clint | `/wiki/npc/clint` | P1 |
| N05 | Demetrius | `/wiki/npc/demetrius` | P1 |
| N06 | Dwarf | `/wiki/npc/dwarf` | P2 |
| N07 | Elliott | `/wiki/npc/elliott` | P1 |
| N08 | Emily | `/wiki/npc/emily` | P0 |
| N09 | Evelyn | `/wiki/npc/evelyn` | P2 |
| N10 | George | `/wiki/npc/george` | P2 |
| N11 | Gus | `/wiki/npc/gus` | P1 |
| N12 | Gunther | `/wiki/npc/gunther` | P2 |
| N13 | Harvey | `/wiki/npc/harvey` | P1 |
| N14 | Haley | `/wiki/npc/haley` | P1 |
| N15 | Jodi | `/wiki/npc/jodi` | P2 |
| N16 | Jas | `/wiki/npc/jas` | P2 |
| N17 | Leah | `/wiki/npc/leah` | P0 |
| N18 | Lewis | `/wiki/npc/lewis` | P1 |
| N19 | Linus | `/wiki/npc/linus` | P1 |
| N20 | Marnie | `/wiki/npc/marnie` | P1 |
| N21 | Marlon | `/wiki/npc/marlon` | P2 |
| N22 | Maru | `/wiki/npc/maru` | P0 |
| N23 | Sebastian | `/wiki/npc/sebastian` | P0 |
| N24 | Shane | `/wiki/npc/shane` | P0 |
| N25 | Penny | `/wiki/npc/penny` | P1 |
| N26 | Pierre | `/wiki/npc/pierre` | P1 |
| N27 | Pam | `/wiki/npc/pam` | P2 |
| N28 | Robin | `/wiki/npc/robin` | P1 |
| N29 | Sandy | `/wiki/npc/sandy` | P2 |
| N30 | Sam | `/wiki/npc/sam` | P0 |
| N31 | Vincent | `/wiki/npc/vincent` | P2 |
| N32 | Wizard | `/wiki/npc/wizard` | P1 |
| N33 | Willy | `/wiki/npc/willy` | P1 |

---

### 3.11 加工机器 (Machines) — 33 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| MA0 | 机器系统总览 | `/wiki/machines/` | P0 |

**生产机器 (20):**

| # | 机器 | 路径 | 优先级 |
|---|------|------|--------|
| MA01 | 酿桶 Keg | `/wiki/machines/keg` | P0 |
| MA02 | 罐头瓶 Preserves Jar | `/wiki/machines/preserves-jar` | P0 |
| MA03 | 脱水机 Dehydrator | `/wiki/machines/dehydrator` | P1 |
| MA04 | 鱼饵机 Bait Maker | `/wiki/machines/bait-maker` | P1 |
| MA05 | 熏鱼机 Fish Smoker | `/wiki/machines/fish-smoker` | P1 |
| MA06 | 回收机 Recycling Machine | `/wiki/machines/recycling-machine` | P1 |
| MA07 | 烹饪锅 Cooking Pot | `/wiki/machines/cooking-pot` | P0 |
| MA08 | 宝石复制机 Crystalarium | `/wiki/machines/crystalarium` | P0 |
| MA09 | 种子生产器 Seed Maker | `/wiki/machines/seed-maker` | P0 |
| MA10 | 熔炉 Furnace | `/wiki/machines/furnace` | P0 |
| MA11 | 木炭窑 Charcoal Kiln | `/wiki/machines/charcoal-kiln` | P1 |
| MA12 | 织布机 Loom | `/wiki/machines/loom` | P1 |
| MA13 | 奶酪压制机 Cheese Press | `/wiki/machines/cheese-press` | P1 |
| MA14 | 蛋黄酱机 Mayonnaise Machine | `/wiki/machines/mayonnaise-machine` | P1 |
| MA15 | 产油机 Oil Maker | `/wiki/machines/oil-maker` | P1 |
| MA16 | 木桶 Cask | `/wiki/machines/cask` | P0 |
| MA17 | 避雷针 Lightning Rod | `/wiki/machines/lightning-rod` | P1 |
| MA18 | 太阳能板 Solar Panel | `/wiki/machines/solar-panel` | P2 |
| MA19 | 蜂房 Bee House | `/wiki/machines/bee-house` | P0 |
| MA20 | 蟹笼 Crab Pot | `/wiki/machines/crab-pot` | P1 |

**农场设施 (8):**

| # | 设施 | 路径 | 优先级 |
|---|------|------|--------|
| MA21 | 洒水器 (3 阶) | `/wiki/machines/sprinklers` | P0 |
| MA22 | 树液采集器 Tapper | `/wiki/machines/tapper` | P1 |
| MA23 | 蠕虫箱 Worm Bin | `/wiki/machines/worm-bin` | P2 |
| MA24 | 高级蠕虫箱 Deluxe Worm Bin | `/wiki/machines/deluxe-worm-bin` | P2 |
| MA25 | 孵化器 Incubator | `/wiki/machines/incubator` | P1 |
| MA26 | 自动采集器 Auto-Grabber | `/wiki/machines/auto-grabber` | P1 |
| MA27 | 自动抚摸器 Auto-Petter | `/wiki/machines/auto-petter` | P2 |
| MA28 | 饲料槽 Feed Trough/Autofeed | `/wiki/machines/feed-trough` | P1 |

---

### 3.12 烹饪系统 (Cooking) — 84 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| CK0 | 烹饪系统总览 | `/wiki/crafting/` | P0 |
| CK1 | 食谱获取方式索引 | `/wiki/crafting/recipe-sources` | P0 |
| CK2 | 增益效果汇总 | `/wiki/crafting/buff-summary` | P0 |

**菜肴个体页面 (81 道菜)** — 每道菜含：原料、增益效果、售价、获取方式

> 81 道菜品完整列表见上方扫描结果。每道菜一页，路径格式：`/wiki/crafting/[dish-name]`
> 优先级：有增益效果的 P1，其余 P2

---

### 3.13 动物系统 (Animals) — 14 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| AN0 | 动物系统总览 | `/wiki/animals/` | P0 |
| AN1 | 鸡舍管理 | `/wiki/animals/coop` | P0 |
| AN2 | 畜棚管理 | `/wiki/animals/barn` | P0 |

**动物个体 (11):**

| # | 动物 | 类型 | 路径 | 优先级 |
|---|------|------|------|--------|
| AN-01 | 白鸡 White Chicken | 鸡舍 | `/wiki/animals/white-chicken` | P0 |
| AN-02 | 金鸡 Golden Chicken | 鸡舍 | `/wiki/animals/golden-chicken` | P1 |
| AN-03 | 虚空鸡 Void Chicken | 鸡舍 | `/wiki/animals/void-chicken` | P1 |
| AN-04 | 鸭子 Duck | 鸡舍 | `/wiki/animals/duck` | P1 |
| AN-05 | 兔子 Rabbit | 鸡舍 | `/wiki/animals/rabbit` | P1 |
| AN-06 | 恐龙 Dinosaur | 鸡舍 | `/wiki/animals/dinosaur` | P1 |
| AN-07 | 鸵鸟 Ostrich | 鸡舍 | `/wiki/animals/ostrich` | P2 |
| AN-08 | 奶牛 Cow | 畜棚 | `/wiki/animals/cow` | P0 |
| AN-09 | 山羊 Goat | 畜棚 | `/wiki/animals/goat` | P1 |
| AN-10 | 绵羊 Sheep | 畜棚 | `/wiki/animals/sheep` | P1 |
| AN-11 | 猪 Pig | 畜棚 | `/wiki/animals/pig` | P1 |

---

### 3.14 建筑管理 (Buildings) — 5 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| BL0 | 建筑系统总览 | `/wiki/buildings/` | P1 |
| BL1 | 鸡舍管理器 | `/wiki/buildings/coop-manager` | P0 |
| BL2 | 畜棚管理器 | `/wiki/buildings/barn-manager` | P0 |
| BL3 | 筒仓管理器 | `/wiki/buildings/silo-manager` | P1 |
| BL4 | 出货箱 | `/wiki/buildings/shipping-bin` | P1 |

---

### 3.15 任务系统 (Quests) — 10 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| Q0 | 任务系统总览 | `/wiki/quests/` | P1 |
| Q1 | 制作任务 Crafting | `/wiki/quests/crafting` | P2 |
| Q2 | 钓鱼任务 Fishing | `/wiki/quests/fishing` | P2 |
| Q3 | 前往任务 Go Somewhere | `/wiki/quests/go-somewhere` | P2 |
| Q4 | 建造任务 Have Building | `/wiki/quests/have-building` | P2 |
| Q5 | 交付任务 Item Delivery | `/wiki/quests/item-delivery` | P1 |
| Q6 | 收获任务 Item Harvest | `/wiki/quests/item-harvest` | P2 |
| Q7 | 采集任务 Resource Collection | `/wiki/quests/resource-collection` | P1 |
| Q8 | 讨伐任务 Slay Monster | `/wiki/quests/slay-monster` | P1 |
| Q9 | 社交任务 Socialize | `/wiki/quests/socialize` | P2 |

---

### 3.16 世界与维度 (World) — 5 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| W0 | 世界系统总览 | `/wiki/world/` | P1 |
| W1 | 矿洞维度 (3 层级) | `/wiki/world/mine-dimensions` | P0 |
| W2 | 室内子空间系统 | `/wiki/world/interior-subspace` | P1 |
| W3 | 季节与天气 | `/wiki/world/seasons` | P0 |
| W4 | 昼夜与时间 | `/wiki/world/time` | P1 |

---

### 3.17 经济系统 (Economy) — 4 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| EC0 | 经济系统总览 | `/wiki/economy/` | P1 |
| EC1 | 出货与售卖 | `/wiki/economy/shipping` | P0 |
| EC2 | 品质系统 | `/wiki/economy/quality` | P0 |
| EC3 | 价格计算公式 | `/wiki/economy/pricing` | P1 |

---

### 3.18 家具装饰 (Furniture) — 5 页

> 80+ 装饰方块，按类别合并成总览页而非单独页面。

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| FN0 | 家具总览 | `/wiki/furniture/` | P2 |
| FN1 | 床与座椅 | `/wiki/furniture/seating` | P2 |
| FN2 | 灯具 | `/wiki/furniture/lighting` | P2 |
| FN3 | 桌柜与收纳 | `/wiki/furniture/storage` | P2 |
| FN4 | 植物与装饰品 | `/wiki/furniture/decorative` | P2 |

---

### 3.19 其他物品 — 6 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| IT01 | 炸弹 (3 种) | `/wiki/items/bombs` | P1 |
| IT02 | 图腾 (2 种) | `/wiki/items/totems` | P1 |
| IT03 | 肥料 (6 种) | `/wiki/items/fertilizers` | P0 |
| IT04 | 储存方块 (箱子/冰箱等) | `/wiki/items/storage` | P1 |
| IT05 | 矿洞石材与装饰石 | `/wiki/items/stone-types` | P2 |
| IT06 | 商店装饰方块 | `/wiki/items/shop-blocks` | P2 |

---

### 3.20 参考索引 — 4 页

| # | 页面 | 路径 | 优先级 |
|---|------|------|--------|
| REF1 | 全物品索引(可搜索表) | `/reference/all-items` | P1 |
| REF2 | 全方块索引 | `/reference/all-blocks` | P1 |
| REF3 | SDV 对标进度总表 | `/reference/version-parity` | P2 |
| REF4 | 更新日志 | `/changelog` | P3 |

---

## 四、统计汇总

| 大类 | 页面数 | P0 | P1 | P2 | P3 |
|------|--------|----|----|----|----|
| 指南 | 3 | 2 | 1 | 0 | 0 |
| 作物 | 47 | 15 | 17 | 15 | 0 |
| 觅食 | 8 | 5 | 3 | 0 | 0 |
| 钓鱼 | 82 | 4 | 8 | 70 | 0 |
| 矿洞 | 22 | 7 | 10 | 5 | 0 |
| 战斗 | 52 | 6 | 15 | 31 | 0 |
| 装备 | 50 | 6 | 22 | 22 | 0 |
| 工具 | 8 | 7 | 1 | 0 | 0 |
| 技能 | 12 | 12 | 0 | 0 | 0 |
| NPC | 37 | 8 | 17 | 12 | 0 |
| 机器 | 33 | 11 | 15 | 7 | 0 |
| 烹饪 | 84 | 3 | 40 | 41 | 0 |
| 动物 | 14 | 4 | 8 | 2 | 0 |
| 建筑 | 5 | 2 | 3 | 0 | 0 |
| 任务 | 10 | 0 | 4 | 6 | 0 |
| 世界 | 5 | 2 | 3 | 0 | 0 |
| 经济 | 4 | 2 | 2 | 0 | 0 |
| 家具 | 5 | 0 | 0 | 5 | 0 |
| 其他物品 | 6 | 1 | 3 | 2 | 0 |
| 参考索引 | 4 | 0 | 2 | 1 | 1 |
| **总计** | **~491** | **~97** | **~174** | **~219** | **~1** |

---

## 五、每个物品页面模板规范

### 作物页面包含
- 物品名称（中/英）、图标
- 种子来源与价格
- 生长天数（各阶段分布）
- 季节限制
- 是否再生 + 再生天数
- 肥料加速效果
- 售价（4 品质：普通/银/金/铱）
- 加工产品（酿桶/罐头瓶产物）
- 用途（烹饪食谱、任务、送礼偏好）
- 获取建议/攻略提示

### 鱼类页面包含
- 鱼名（中/英）、图标
- 出没地点、季节、时段、天气
- 难度等级、行为模式（mixed/dart/smooth/sinker）
- 尺寸范围
- 售价（4 品质）
- 用途（烹饪、任务、博物馆）

### 武器页面包含
- 武器名（中/英）、图标
- 类型（剑/匕首/棍棒）
- 等级、伤害范围
- 暴击率、击退
- 特殊效果
- 获取方式

### NPC 页面包含
- 姓名、头像、生日、住所
- 最爱/喜欢/普通/不喜欢/讨厌礼物
- 基础好感关系
- 日程安排（按季节/天气）
- 剧情事件列表
- 对话摘录

### 机器页面包含
- 机器名（中/英）、图标
- 合成配方
- 输入 → 输出对照表（全部产品）
- 加工时间
- 放置位置要求

---

## 六、开发路线图

### 第一阶段 — 骨架搭建（当前）
- [x] VitePress 初始化 + 自定义主题
- [x] 首页 + 导航结构
- [ ] sidebar 配置完成（覆盖全部 491 页路径）
- [ ] 所有目录创建 + 占位文件生成

### 第二阶段 — P0 核心页面（~97 页）
- [ ] 作物系统（15 页：系统说明 + 季节总览 + 经济作物个体）
- [ ] 钓鱼系统说明（4 页系统页）
- [ ] 矿洞系统（7 页：总览 + 5 矿石 + 分布表）
- [ ] 技能与职业（12 页：全部完成）
- [ ] 工具系统（7 页）
- [ ] 机器核心（11 页：酿桶/罐头/熔炉/洒水器等）
- [ ] 动物核心（4 页：总览 + 鸡舍 + 畜棚 + 白鸡/奶牛）
- [ ] NPC 核心（8 页：系统 + 可婚角色优先）
- [ ] 战斗核心（6 页：系统 + 终极武器）
- [ ] 装备核心（6 页：系统 + 顶级装备）
- [ ] 经济 + 世界（4 页）
- [ ] 烹饪总览（3 页）
- [ ] 觅食总览（5 页）
- [ ] 肥料（1 页）

### 第三阶段 — P1 重要扩展（~174 页）
- [ ] 全部作物个体页
- [ ] 全部鱼类个体页（热门鱼种优先）
- [ ] 全部 NPC 个体页
- [ ] 全部机器个体页
- [ ] 有增益的烹饪菜肴页
- [ ] 全部靴子/戒指个体页
- [ ] 任务系统页
- [ ] 参考索引页

### 第四阶段 — P2 百科补全（~219 页）
- [ ] 剩余鱼类个体页
- [ ] 全部武器个体页
- [ ] 剩余烹饪菜肴页
- [ ] 家具装饰系统
- [ ] 花卉类作物
- [ ] 装饰石材/商店方块

### 持续维护
- [ ] 随模组版本更新同步百科内容
- [ ] SDV 对标进度跟踪
- [ ] 社区贡献指南
