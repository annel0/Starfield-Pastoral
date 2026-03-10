# data/stardew/function/utility/sprinkler/place_raycast_loop.mcfunction
# 洒水器放置射线循环

# 1. 检测是否击中固体方块
execute unless block ~ ~ ~ #minecraft:air unless block ~ ~ ~ #minecraft:replaceable run function stardew:utility/sprinkler/place_sprinkler
execute unless block ~ ~ ~ #minecraft:air unless block ~ ~ ~ #minecraft:replaceable run return 1

# 2. 步进
scoreboard players add @s sd_ray_steps 1
execute if score @s sd_ray_steps matches ..30 positioned ^ ^ ^0.2 run function stardew:utility/sprinkler/place_raycast_loop
