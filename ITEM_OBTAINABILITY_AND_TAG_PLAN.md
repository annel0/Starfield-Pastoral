# 物品获取途径 & 标签 & JEI 全面开发规划

> 生成日期: 2026-04-08  
> 状态: 待审阅  

---

## 一、现状问题总览

| 维度 | 现状 |
|------|------|
| 总注册物品 | ~1020 个 |
| ✅ 有获取途径 | ~350 个 (34%) |
| ❌ 无获取途径 | ~300+ 个 (30%) |
| ⚠️ 间接不可获取 | ~80 个 (cooking_pot 缺失导致料理链断裂) |
| JEI 分类 | 仅 1 个 (钓鱼信息) |
| 物品标签 | ~30 个 (全是鱼/种子/工具分类，无 MC 通用标签) |
| 方块标签 | 5 个 (仅矿石/石材/镐等级) |

---

## 二、🔴 P0 — 阻断性问题（不修则无法正常游玩）

### 2.1 基础工具给予

**问题**: 新玩家登录后没有任何工具，无法开始游戏。

**方案**: `PlayerDataEventHandler.onPlayerLogin()` 中检测新玩家（首次登录），给予基础 6 件套:
- pickaxe, axe, hoe, watering_can, scythe, fishing_rod

### 2.2 缺失合成配方的机器

以下机器已注册但无合成配方/商店，玩家完全无法获取:

| 机器 | SDV 获取方式 | 建议方案 |
|------|-------------|---------|
| oil_maker | Farming 8 合成 | 加入合成配方 |
| fish_smoker | Fishing 10 合成 | 加入合成配方 |
| bait_maker | Fishing 6 合成 | 加入合成配方 |
| lightning_rod | Foraging 6 合成 | 加入合成配方 |
| cooking_pot | 农舍升级赠送 | Robin 建筑购买 或 合成 |
| fridge | 农舍升级赠送 | 同 cooking_pot 绑定 |
| shipping_bin | 农场初始 | 合成 或 Robin 购买 |
| auto_petter | Joja/地牢宝箱 | 商店 或 冒险家协会购买 |
| deluxe_worm_bin | Fishing 9 合成 | 加入合成配方 |

### 2.3 石材变体合成

**问题**: 36 个 slab/stairs/wall 有方块有纹理但无法合成。

**方案**: 已有 `data/stardewcraft/recipe/` 下的 84 个 stonecutting JSON — **检查是否覆盖了所有变体**。如果已覆盖就只需要确认 stonecutter 可用性；如果未覆盖就补充 stonecutting + crafting JSON。

---

## 三、🟡 P1 — 重要功能补全

### 3.1 家具获取系统 (~150 个物品)

**方案 A (推荐)**: Robin 家具商店
- 扩展 Robin NPC 的菜单选项: 建造 / **家具商店** / 材料商店 / 离开
- 家具按类别分组: 座椅、桌子、灯具、地毯、墙饰、杂项
- 按游戏进度解锁部分家具

**方案 B**: 合成配方
- 部分简单家具加入合成(木桌=木材×8, 椅子=木材×4 等)
- 精致家具仍需购买

**建议**: 两者结合 — 基础家具可合成，精致/装饰性家具在 Robin 购买。

**不加入获取途径的物品** (世界生成/NPC 室内专用):
- 商店装饰 (shop_basket, shop_crate_*, shop_counter_*, supermarket_shelf_*)
- NPC 专属海报 (hospital_poster_*, alex_poster_*, leah_poster_*, sebastian_poster_*)
- 特殊家具 (wizard_cauldron, museum_exhibit_stand, joja_vending_machine, fish_shop_counter, hospital_counter)
- 矿井结构 (mine_barrier, mine_ladder, mine_exit, elevator)

### 3.2 采集系统 (~22 个物品)

**问题**: wild_horseradish, daffodil, leek 等采集物品在 SDV 中由地图刷新点产出。

**方案**: 
- 在 Stardew 主世界维度的地表按季节生成采集物品实体
- 或者加入到 loot_tables 中通过杂草/草丛掉落

### 3.3 蘑菇系统 (6 个物品)

**方案**: 
- 蘑菇洞(矿井特定层)随机生成蘑菇方块
- 或者作为采集系统的一部分在特定区域生成

### 3.4 水果树 (9 种水果)

**问题**: apple, apricot, orange 等无果树系统。

**方案**: 
- 长期: 实现水果树种植系统
- 短期 workaround: 加入 Pierre/Sandy 商店直接出售水果

### 3.5 人工制品获取 (~30 个)

**方案**: 
- 矿井挖掘时有概率掉落 artifact（SDV 原版就是这样）
- 钓鱼宝箱池添加 artifact
- 已有 MineMonsterDropHandler — 可以扩展掉落池

### 3.6 图腾合成 (7 个)

| 物品 | SDV 配方 | 建议 |
|------|---------|------|
| warp_totem_farm | 硬木×1 + 蜂蜜×1 + 纤维×20 | 加入合成 |
| warp_totem_mountain | 铁锭×1 + 石头×25 + 纤维×10 | 加入合成 |
| warp_totem_beach | 硬木×1 + 珊瑚×2 + 纤维×10 | 加入合成 |
| rain_totem | 硬木×1 + 松露油×1 + 松焦油×5 | 加入合成 |
| totem_pole_farm/mountain/beach | 装饰品 | 合成或Robin购买 |

### 3.7 缺失武器获取 (~20 把)

SDV 中这些武器来自:
- 矿井宝箱/怪物掉落 (neptunes_glaive, obsidian_edge, etc.)
- 特殊任务 (insect_head, holy_blade)
- 火山/沙漠地牢 (dragontooth_*, dwarf_*)

**方案**: 
- mine_barrel 开箱添加武器掉落池
- 特定层级的怪物掉落
- 冒险家协会(Marlon)解锁更多武器

### 3.8 特殊工具

| 工具 | SDV 获取 | 建议 |
|------|---------|------|
| golden_scythe | 矿洞奖励 | 矿井特定层宝箱 |
| iridium_scythe | 不存在于原版 | 合成配方 或 移除 |
| training_rod | Willy 赠送/商店 | 加入 Willy 商店 |
| advanced_iridium_rod | 不存在于标准SDV | 决定是否保留 |
| paintbrush | 装饰专用 | Robin 商店购买 |

---

## 四、🟢 P2 — 完善性工作

### 4.1 其他不可获取物品处理

| 物品 | 方案 |
|------|------|
| radioactive_ore/bar | 矿井深层生成 或 暂时移除 |
| mystery_box/golden_mystery_box | 钓鱼/矿井奖励 |
| pearl | 海洋钓鱼极低概率 |
| treasure_chest | 钓鱼宝箱 |
| stardrop_tea | 任务奖励 |
| golden_animal_cracker | 成就奖励 |
| bouquet/wilted_bouquet | Pierre 商店 (好感度系统) |
| rotten_plant | 作物枯萎产物 |
| ancient_seed (artifact→seeds) | 人工制品系统 → 博物馆捐赠奖励 |

### 4.2 隐藏物品处理

以下物品**不应该有生存获取途径**，应加入 JEI 隐藏列表:
- 所有世界生成专用方块: mine_barrier, mine_exit, mine_ladder, elevator
- 树木组件方块: wild_*_trunk/branch/leaves (方块物品)
- 矿石方块: *_ore 方块物品 (挖掘只掉资源不掉方块)
- 内部方块: wallpaper_block, flooring_block, wallpaper_icon, flooring_icon
- NPC 专属装饰: 所有商店装饰方块、NPC 海报
- 内部系统物品: animal_produce_spot, dead_crop

---

## 五、物品/方块标签规划

### 5.1 需要新增的 Minecraft 通用标签

#### 方块标签 (`data/minecraft/tags/block/`)

| 标签 | 内容 | 用途 |
|------|------|------|
| `mineable/axe` | ✅ 已补全 | 工具加速 |
| `mineable/pickaxe` | ✅ 已补全 | 工具加速 |
| `mineable/shovel` | ✅ 已补全(yellow_dirt) | 工具加速 |
| `mineable/hoe` | 所有作物方块、yellow_dirt | 锄头加速 |
| `planks` | — | 无对应方块 |
| `slabs` | 所有 *_slab (12个) | MC 通用分类 |
| `stairs` | 所有 *_stairs (12个) | MC 通用分类 |
| `walls` | ✅ 已有 (12个) | MC 通用分类 |
| `leaves` | 所有 wild_*_leaves (5个) | 树叶行为 |
| `saplings` | 所有 wild_*_sapling0/1 (10个) | 树苗分类 |
| `logs` | 所有 wild_*_trunk0/1 (10个) | 原木分类 |
| `crops` | 所有作物方块 (~40个) | 作物分类 |
| `replaceable` | wild_weeds, pasture_grass, blue_pasture_grass | 可被覆盖放置 |
| `fire_resistant` | furnace, charcoal_kiln, fireplace_large, lava_basalt 系列 | 抗火 |
| `dragon_immune` | mine_barrier, mine_exit | 龙不可破坏 |
| `wither_immune` | mine_barrier, mine_exit | 凋零不可破坏 |

#### 物品标签 (`data/minecraft/tags/item/`)

| 标签 | 内容 | 用途 |
|------|------|------|
| `swords` | 所有模组剑 | 原版手持分类 |
| `axes` | 所有模组斧 | 原版手持分类 |
| `pickaxes` | 所有模组镐 | 原版手持分类 |
| `hoes` | 所有模组锄 | 原版手持分类 |
| `shovels` | — (无铲) | — |
| `coals` | coal | 熔炉燃料兼容 |

### 5.2 需要新增的模组标签

#### 物品标签 (`data/stardewcraft/tags/item/`)

**获取来源标签** (JEI/信息展示用):

| 标签 | 内容 | 说明 |
|------|------|------|
| `shop/pierre` | Pierre 商店所有物品 | 商店来源标记 |
| `shop/willy` | Willy 商店所有物品 | |
| `shop/marnie` | Marnie 商店所有物品 | |
| `shop/clint` | Clint 商店所有物品 | |
| `shop/sandy` | Sandy 商店所有物品 | |
| `shop/gus` | Gus 商店所有物品 | |
| `shop/marlon` | Marlon 商店所有物品 | |
| `shop/robin` | Robin 商店所有物品 | |

**功能分类标签**:

| 标签 | 内容 | 说明 |
|------|------|------|
| `artisan_goods` | 所有工匠产品 (酒/果汁/蜂蜜/果酱/腌菜/奶酪/布等) | 工匠品标记 |
| `animal_products` | 蛋/奶/羊毛/鸭毛/兔脚/松露 | 动物产品 |
| `animal_products/eggs` | 所有蛋类 | 子分类 |
| `animal_products/milk` | 所有奶类 | 子分类 |
| `gems` | 所有宝石 (amethyst~topaz + diamond + prismatic_shard) | 宝石分类 |
| `minerals` | 所有晶洞矿物 | 矿物分类 |
| `artifacts` | 所有人工制品 | 人工制品 |
| `geodes` | geode, frozen_geode, magma_geode, omni_geode | 晶洞分类 |
| `bars` | copper_bar, iron_bar, gold_bar, iridium_bar, radioactive_bar | 金属锭 |
| `ores` | copper_ore, iron_ore, gold_ore, iridium_ore | 金属矿石(物品) |
| `resources` | wood_normal, stone, wood_hard, fiber, clay, coal, sap | 基础资源 |
| `fertilizers` | 所有 9 种肥料 | 肥料分类 |
| `baits` | 所有 7 种鱼饵 | 鱼饵分类 |
| `tackles` | 所有 9 种渔具 | 渔具分类 |
| `weapons` | 所有武器 (已有 tools 标签，但需要 weapons 分开) | 武器分类 |
| `weapons/swords` | 所有剑 | 子分类 |
| `weapons/daggers` | 所有匕首 | 子分类 |
| `weapons/clubs` | 所有棍棒 | 子分类 |
| `monster_drops` | slime_item, bat_wing, solar_essence, void_essence, bone_fragment, bug_meat | 怪物掉落 |
| `cooking_ingredients` | sugar, wheat_flour, oil, rice, vinegar, milk, egg 等 | 烹饪材料 |
| `cooking_dishes` | 所有 80 种料理 | 料理分类 |
| `forage/spring` | spring 季节采集物 | 季节采集 |
| `forage/summer` | summer 季节采集物 | |
| `forage/fall` | fall 季节采集物 | |
| `forage/winter` | winter 季节采集物 | |
| `fruits` | 所有水果 (作物水果 + 树果) | 水果分类 (keg/preserves_jar 判断用) |
| `vegetables` | 所有蔬菜 | 蔬菜分类 |
| `flowers` | tulip, blue_jazz, poppy, summer_spangle, sunflower, fairy_rose | 花卉 |
| `machine_output_only` | 标记只能通过机器产出的物品 | JEI 信息 |
| `hidden` | 不应出现在 JEI/创造模式中的内部物品 | 隐藏列表 |

#### 方块标签 (`data/stardewcraft/tags/block/`)

| 标签 | 内容 | 说明 |
|------|------|------|
| `machines` | 所有 24 个机器方块 | 机器分类 |
| `machines/artisan` | keg, preserves_jar, cheese_press 等加工机 | 工匠机器 |
| `machines/utility` | sprinkler, lightning_rod, bee_house 等 | 实用设施 |
| `furniture` | 所有家具方块 (~150个) | 家具分类 |
| `furniture/seating` | 所有座椅 | 子分类 |
| `furniture/tables` | 所有桌子 | 子分类 |
| `furniture/lighting` | 所有灯具 | 子分类 |
| `furniture/wall_decor` | 所有墙饰 | 子分类 |
| `furniture/carpet` | 所有地毯 | 子分类 |
| `wild_tree_parts` | 所有野生树组件 | 树组件 |
| `world_gen_only` | mine_barrier, mine_exit, elevator, 矿石方块等 | 世界生成专用 |
| `animal_facility` | feed_trough, hay_hopper, auto_grabber 等 | 动物设施 |

---

## 六、JEI 集成规划

### 6.1 需要新增的 JEI 分类

| 分类 | 数据来源 | 覆盖物品 | 优先级 |
|------|----------|----------|--------|
| **机器加工 (ArtisanRecipe)** | `artisan/*.json` | ~200 条配方, 13 个机器 | P0 |
| **商店信息 (ShopInfo)** | `ShopRegistry` 硬编码 | ~80 个商品 | P0 |
| **玩家合成 (StardewCrafting)** | `StardewCraftingRecipeData` | ~38 个配方 | P1 |
| **晶洞加工 (GeodeProcessing)** | `GeodeLootService` | ~44 种矿物 | P1 |
| **怪物掉落 (MonsterDrop)** | `MineMonsterDropHandler` | ~20 种掉落 | P1 |
| **工具升级 (ToolUpgrade)** | `BlacksmithService` | ~20 种工具 | P1 |
| **种植信息 (CropGrowing)** | CropBlock 子类 | ~35 种作物 | P2 |
| **树液/蜂蜜 (TreeTapper)** | BlockEntity 逻辑 | ~5 种产品 | P2 |

### 6.2 JEI 隐藏列表

从 JEI 中隐藏不应出现的内部物品（用 `hidden` 标签自动处理）:
- 矿石方块物品 (22个)
- 树木组件方块物品 (25个)
- 矿井结构方块 (mine_barrier, mine_exit, mine_ladder, elevator)
- 内部方块 (wallpaper_block, flooring_block, wallpaper_icon, flooring_icon)
- NPC 专属装饰方块 (~30个商店装饰 + NPC 海报)
- 系统方块 (animal_produce_spot, dead_crop)
- 多方块渲染辅助 (office_stool_top_render, office_chair_2_top_render)

---

## 七、开发路线图

### Phase 1: 基础可玩性修复 (P0)
1. ✅ 新玩家给予基础工具 6 件套
2. ✅ 补充缺失机器合成配方 (oil_maker, fish_smoker, bait_maker, lightning_rod, deluxe_worm_bin)
3. ✅ cooking_pot / fridge / shipping_bin / trash_bin / incubator 获取方式 (Robin 购买)
4. ✅ 检查/补全石材变体 stonecutting 配方

### Phase 2: 内容补全 (P1)
5. ☐ 家具获取系统 (Robin 家具商店 + 基础家具合成)
6. ✅ 图腾合成配方 (warp_totem × 3 + rain_totem + totem_pole × 3)
7. ✅ 缺失武器获取途径 (Marlon 冒险家商店 19 把武器)
8. ✅ training_rod / paintbrush 加入商店
9. ✅ auto_petter 获取途径 (Marnie 商店 50,000g)

### Phase 3: 世界内容 (P1-P2)
10. ✅ 采集物品生成系统 (22 种 forage 方块 + 4 区域 SDV 同步刷新 + 品质/XP/职业)
11. ⏭️ 蘑菇获取 — 暂时跳过
12. ✅ 水果树系统 — 短期 workaround (9 种树果加入 Pierre 商店)
13. ✅ 人工制品掉落源 (矿井/钓鱼宝箱/翻土)

### Phase 4: 标签系统 (P1)
14. ✅ MC 通用方块标签 (slabs, stairs, leaves, saplings, logs, crops, mineable/hoe, replaceable, dragon_immune, wither_immune)
15. ✅ MC 通用物品标签 (swords, hoes, coals)
16. ✅ 模组功能标签 (gems, minerals, artifacts, ores, bars, resources, fertilizers, baits, tackles, weapons, monster_drops, flowers)
17. ✅ 模组分类标签 (machines, machines/artisan, machines/utility, furniture, furniture/*, wild_tree_parts, world_gen_only, animal_facility)
18. ✅ JEI 隐藏标签 (hidden — 95个内部物品)

### Phase 5: JEI 集成 (P1-P2)
19. ☐ 通用机器加工 JEI 分类 (ArtisanRecipeCategory)
20. ☐ 商店信息 JEI 分类 (ShopInfoCategory)
21. ☐ 玩家合成 JEI 分类
22. ☐ 晶洞加工 JEI 分类
23. ☐ 怪物掉落 / 工具升级 / 种植信息 JEI 分类
24. ☐ JEI 隐藏列表注册

### Phase 6: 收尾处理 (P2)
25. ☐ 特殊物品决策 (radioactive_ore, mystery_box, pearl 等 — 实现获取 或 标记为未来内容)
26. ☐ 全物品获取途径二次审计
27. ☐ JEI 信息完整性测试

---

## 八、不可获取物品完整清单

### 🔴 需要添加获取途径的

<details>
<summary>机器 (11个)</summary>

- oil_maker
- fish_smoker
- bait_maker
- lightning_rod
- cooking_pot
- fridge
- shipping_bin
- trash_bin
- auto_petter
- deluxe_worm_bin
- incubator (鸡舍建成后内置)

</details>

<details>
<summary>工具 (10个)</summary>

- pickaxe (基础)
- axe (基础)
- hoe (基础)
- watering_can (基础)
- scythe (基础)
- fishing_rod (基础)
- golden_scythe
- iridium_scythe
- training_rod
- paintbrush

</details>

<details>
<summary>家具 (~150个) — 完整列表</summary>

**座椅**: sofa, chair_1/2/3, cushion, office_stool, office_chair_2, stool, dining_chair_wood, dining_chair_iron  
**桌子**: oak_table, spruce_table, birch_table, spruce_counter, oak_round_table, kitchen_counter  
**灯**: light_1~7, floor_lamp, table_lamp, table_lantern  
**床**: bed_1, bed_2  
**柜子**: dresser_1/2/3, wine_cabinet_1/2/3  
**地毯**: carpet_1~21 (21个)  
**墙饰**: wall_hanging_small_a/b, wall_notice_board_small/medium, wall_bulletin_notes, wall_hanging_strip/triptych/ornament, wall_switch_panel, wall_frame_wide/double, wall_sticky_notes, wall_poster_gamepad/dolphin/game_character, wall_outlet, wall_photo_white_hall/frame, wall_blacksmith_sign/hammers, wall_bone_decor, wall_kitchen_cabinet, wall_adventurer_map, wall_buoy, wall_fish_sign, wall_island_map  
**盆栽**: bonsai_1~5, bonsai_bush, potted_plant_1~6  
**书架**: book_stack_1/2/3, bookshelf_wall, bookshelf_tall_1/2  
**杂项**: photo_frame, tv_1/2, jukebox, arcade_machine, computer, sailboat, radio, barrel, wood_bundle, fireplace_large, bear_figurine, globe, telescope, pool_table, white_teacup, electric_piano, guitar, microwave, grandfather_clock, drum_set, bear_skin_rug, leaning_sword, leah_sculpture, easel, blue_bear_plushie, board_game, microscope, beaker, scattered_papers, tableware_pink/blue, sink_4, shrine, pillar  

</details>

<details>
<summary>武器 (~20个)</summary>

**匕首**: elf_blade, burglars_shank, wicked_kris, dwarf_dagger, iridium_needle, infinity_dagger, dragontooth_shiv, broken_trident  
**剑**: meowmere, neptunes_glaive, templars_blade, insect_head, ossified_blade, holy_blade, yeti_tooth, dark_sword, dragontooth_cutlass, dwarf_sword, infinity_blade  

</details>

<details>
<summary>采集物 (~22个)</summary>

wild_horseradish, daffodil, leek, dandelion, cave_carrot, fiddlehead_fern, holly, nautilus_shell, coral, rainbow_shell, spice_berry, sea_urchin, spring_onion, sweet_pea, wild_plum, hazelnut, blackberry, winter_root, crystal_fruit, crocus, ginger, taro_root

</details>

<details>
<summary>蘑菇 (6个)</summary>

common_mushroom, red_mushroom, purple_mushroom, morel, chanterelle, magma_cap

</details>

<details>
<summary>水果树果实 (9个)</summary>

apple, apricot, orange, peach, pomegranate, cherry, banana, mango, pineapple

</details>

<details>
<summary>人工制品 (~30个)</summary>

chipped_amphora, arrowhead, ancient_doll, elvish_jewelry, chewing_stick, ornamental_fan, dinosaur_egg, ancient_sword, rusty_spoon, rusty_spur, rusty_cog, chicken_statue, ancient_seed, prehistoric_tool, dried_starfish, anchor, glass_shards, bone_flute, prehistoric_handaxe, golden_mask, golden_relic, strange_doll_green/yellow, prehistoric_scapula/skull, skeletal_hand, prehistoric_rib/vertebra, skeletal_tail, nautilus_fossil, amphibian_fossil, palm_fossil, trilobite

</details>

<details>
<summary>图腾 (7个)</summary>

warp_totem_farm, warp_totem_mountain, warp_totem_beach, rain_totem, totem_pole_farm, totem_pole_mountain, totem_pole_beach

</details>

<details>
<summary>杂项 (~15个)</summary>

radioactive_ore, radioactive_bar, mystery_box, golden_mystery_box, pearl, treasure_chest, stardrop_tea, golden_animal_cracker, bouquet, wilted_bouquet, rotten_plant, ancient_seed(artifact), golden_egg

</details>

### ⚪ 设计意图不可获取 — 加入 JEI 隐藏列表

<details>
<summary>世界生成/内部用方块 (~90个)</summary>

**矿井结构**: mine_barrier, mine_exit, mine_ladder, elevator  
**矿石方块**: earth/frost/lava_copper/iron/gold/iridium/coal_ore, amethyst/aquamarine/diamond/emerald/jade/ruby/topaz_ore (22个)  
**树组件方块**: wild_oak/maple/pine/mahogany/mystic_tree_trunk0/1/branch1/2/leaves (25个)  
**内部方块**: wallpaper_block, flooring_block, wallpaper_icon, flooring_icon, animal_produce_spot, dead_crop  
**渲染辅助**: office_stool_top_render, office_chair_2_top_render  
**NPC 专属装饰**: shop_basket, shop_crate_fruit_1~10, shop_pack_box, shop_counter_1/2/3, supermarket_shelf_1/2, shop_shipping_bin, shop_window_1/2, fish_shop_counter, hospital_counter, hospital_poster_1~5, alex_poster_1~3, leah_poster_1~3, sebastian_poster_1~3, periodic_table, train_photo, wall_photo_1, paper_checklist, sine_wave_poster  

</details>
