# stardew:mine/ore/roll_theme3.mcfunction
# 主题3 (51-75层) 矿石概率表
# 
# 概率分布:
# - 石头: 58%
# - 煤炭: 8%
# - 铜矿: 3%
# - 铁矿: 12%
# - 金矿: 10%
# - 石英: 2%
# - 紫水晶: 2%
# - 大地水晶: 2%
# - 翡翠: 2%
# - 红宝石: 1%
# 总计: 100%

# 随机 1-100
execute store result score #ore_roll sd_mine_temp run random value 1..100

# 1-58: 石头 (58%)
execute if score #ore_roll sd_mine_temp matches 1..58 run function stardew:mine/ore/spawn_stone_theme3

# 59-66: 煤炭 (8%)
execute if score #ore_roll sd_mine_temp matches 59..66 run function stardew:mine/ore/spawn_coal

# 67-69: 铜矿 (3%)
execute if score #ore_roll sd_mine_temp matches 67..69 run function stardew:mine/ore/spawn_copper

# 70-81: 铁矿 (12%)
execute if score #ore_roll sd_mine_temp matches 70..81 run function stardew:mine/ore/spawn_iron

# 82-91: 金矿 (10%)
execute if score #ore_roll sd_mine_temp matches 82..91 run function stardew:mine/ore/spawn_gold

# 92-93: 石英 (2%)
execute if score #ore_roll sd_mine_temp matches 92..93 run function stardew:mine/ore/spawn_quartz

# 94-95: 紫水晶 (2%)
execute if score #ore_roll sd_mine_temp matches 94..95 run function stardew:mine/ore/spawn_amethyst

# 96-97: 大地水晶 (2%)
execute if score #ore_roll sd_mine_temp matches 96..97 run function stardew:mine/ore/spawn_earth_crystal

# 98-99: 翡翠 (2%)
execute if score #ore_roll sd_mine_temp matches 98..99 run function stardew:mine/ore/spawn_emerald

# 100: 红宝石 (1%)
execute if score #ore_roll sd_mine_temp matches 100 run function stardew:mine/ore/spawn_ruby
