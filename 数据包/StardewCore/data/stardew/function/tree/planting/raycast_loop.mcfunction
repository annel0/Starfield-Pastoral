# data/stardew/functions/tree/planting/raycast_loop.mcfunction

# 1. 命中检测 (使用 #stardew:tillable 标签，因为树能种在泥土和草上)
# 且要求上方是空气 (防止种在方块内部)
execute if block ~ ~ ~ #stardew:tillable if block ~ ~1 ~ minecraft:air run function stardew:tree/planting/plant_router
execute if block ~ ~ ~ #stardew:tillable if block ~ ~1 ~ minecraft:air run return 1

# 2. 步进
scoreboard players add @s sd_ray_steps 1
execute if score @s sd_ray_steps matches ..30 positioned ^ ^ ^0.2 run function stardew:tree/planting/raycast_loop