# 尝试向相邻格子扩散草
# 执行者: 草实体 (@s)

# 50%概率执行扩散
execute store result score grass_spread_roll sd_temp run random value 1..100
execute if score grass_spread_roll sd_temp matches 51.. run return 0

# 随机选择四个方向之一进行扩散
execute store result score spread_direction sd_temp run random value 0..3

# 根据方向选择目标位置
execute if score spread_direction sd_temp matches 0 positioned ~1 ~ ~ run function stardew:grass/try_spread_to_position
execute if score spread_direction sd_temp matches 1 positioned ~-1 ~ ~ run function stardew:grass/try_spread_to_position
execute if score spread_direction sd_temp matches 2 positioned ~ ~ ~1 run function stardew:grass/try_spread_to_position
execute if score spread_direction sd_temp matches 3 positioned ~ ~ ~-1 run function stardew:grass/try_spread_to_position