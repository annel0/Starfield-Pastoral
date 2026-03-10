# 草籽种植的射线检测循环
# 检测到地面或实体方块时触发种植

# --- 1. 命中检测 ---
# 击中土壤类型方块 -> 调用草种植函数
execute if block ~ ~ ~ #minecraft:dirt run function stardew:grass/plant
execute if block ~ ~ ~ #minecraft:dirt run return 1
execute if block ~ ~ ~ minecraft:grass_block run function stardew:grass/plant
execute if block ~ ~ ~ minecraft:grass_block run return 1
execute if block ~ ~ ~ minecraft:coarse_dirt run function stardew:grass/plant
execute if block ~ ~ ~ minecraft:coarse_dirt run return 1
execute if block ~ ~ ~ minecraft:podzol run function stardew:grass/plant
execute if block ~ ~ ~ minecraft:podzol run return 1

# --- 2. 步进 ---
scoreboard players add @s sd_ray_steps 1
execute if score @s sd_ray_steps matches ..30 positioned ^ ^ ^0.2 run function stardew:grass/plant_raycast_loop