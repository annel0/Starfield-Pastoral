# stardew:mine/ore/roll_theme1.mcfunction
# 主题1 (普通矿洞) 概率表 - 第1-25层
# 执行位置: 要生成矿石的位置
#
# 概率分布 (调整后):
# 石头:    78%  (1-78)
# 煤炭:    8%   (79-86)
# 铜矿:    6%   (87-92)
# 石英:    2%   (93-94)
# 地晶:    2%   (95-96)
# 紫水晶:  2%   (97-98)
# (预留):  2%   (99-100) -> 石头

# 随机 1-100
execute store result score #ore_roll sd_mine_temp run random value 1..100

# 石头 78%
execute if score #ore_roll sd_mine_temp matches 1..78 run function stardew:mine/ore/spawn_stone

# 煤炭 8%
execute if score #ore_roll sd_mine_temp matches 79..86 run function stardew:mine/ore/spawn_coal

# 铜矿 6%
execute if score #ore_roll sd_mine_temp matches 87..92 run function stardew:mine/ore/spawn_copper

# 石英 2%
execute if score #ore_roll sd_mine_temp matches 93..94 run function stardew:mine/ore/spawn_quartz

# 地晶 2%
execute if score #ore_roll sd_mine_temp matches 95..96 run function stardew:mine/ore/spawn_earth_crystal

# 紫水晶 2%
execute if score #ore_roll sd_mine_temp matches 97..98 run function stardew:mine/ore/spawn_amethyst

# 预留 (生成石头)
execute if score #ore_roll sd_mine_temp matches 99..100 run function stardew:mine/ore/spawn_stone
