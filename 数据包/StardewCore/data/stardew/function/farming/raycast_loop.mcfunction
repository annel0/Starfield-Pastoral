# data/stardew/functions/farming/raycast_loop.mcfunction

# --- 1. 命中检测 ---
# 击中耕地 -> 调用命中分发器
execute if block ~ ~ ~ minecraft:farmland run function stardew:farming/hit_router
execute if block ~ ~ ~ minecraft:farmland run return 1

# --- 2. 步进 ---
scoreboard players add @s sd_ray_steps 1
execute if score @s sd_ray_steps matches ..30 positioned ^ ^ ^0.2 run function stardew:farming/raycast_loop