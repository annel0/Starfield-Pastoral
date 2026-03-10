# stardew:mine/ore/roll_theme2.mcfunction
# 主题2 (冰川矿洞) 概率表 - 第26-50层
# 执行位置: 要生成矿石的位置
#
# 概率分布:
# 石头:    70%  (1-70)
# 煤炭:    10%  (71-80)
# 铜矿:    5%   (81-85)
# 铁矿:    10%  (86-95)
# 泪晶矿:  3%   (96-98) - 冰川特色宝石
# 石英:    1%   (99)
# 紫水晶:  0.5% (100, 奇数roll)
# 地晶:    0.5% (100, 偶数roll)

# 随机 1-100
execute store result score #ore_roll sd_mine_temp run random value 1..100

# 石头 70% (冰川主题专用，CMD 7202)
execute if score #ore_roll sd_mine_temp matches 1..70 run function stardew:mine/ore/spawn_stone_theme2

# 煤炭 10%
execute if score #ore_roll sd_mine_temp matches 71..80 run function stardew:mine/ore/spawn_coal

# 铜矿 5%
execute if score #ore_roll sd_mine_temp matches 81..85 run function stardew:mine/ore/spawn_copper

# 铁矿 10%
execute if score #ore_roll sd_mine_temp matches 86..95 run function stardew:mine/ore/spawn_iron

# 泪晶矿 3% (冰川特色)
execute if score #ore_roll sd_mine_temp matches 96..98 run function stardew:mine/ore/spawn_frozen_tear

# 石英 1%
execute if score #ore_roll sd_mine_temp matches 99 run function stardew:mine/ore/spawn_quartz

# 100的话再随机判断是紫水晶还是地晶
execute if score #ore_roll sd_mine_temp matches 100 store result score #gem_roll sd_mine_temp run random value 1..2
execute if score #ore_roll sd_mine_temp matches 100 if score #gem_roll sd_mine_temp matches 1 run function stardew:mine/ore/spawn_amethyst
execute if score #ore_roll sd_mine_temp matches 100 if score #gem_roll sd_mine_temp matches 2 run function stardew:mine/ore/spawn_earth_crystal

