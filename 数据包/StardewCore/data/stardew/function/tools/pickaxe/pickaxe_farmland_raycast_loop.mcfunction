# data/stardew/function/tools/pickaxe/pickaxe_farmland_raycast_loop.mcfunction
# 镐子射线循环 - 检测耕地

# --- 1. 命中检测 ---
# 击中耕地 -> 调用破坏函数并立即终止射线
execute if block ~ ~ ~ minecraft:farmland run tag @s add sd_hit_farmland
execute if block ~ ~ ~ minecraft:farmland run function stardew:tools/pickaxe/break_farmland
execute if block ~ ~ ~ minecraft:farmland run return 1

# --- 2. 停止条件 - 碰到障碍物立即停止 ---
# 如果不是空气、水、草、structure_void等可穿透方块,则停止射线
execute unless block ~ ~ ~ minecraft:air unless block ~ ~ ~ minecraft:water unless block ~ ~ ~ minecraft:short_grass unless block ~ ~ ~ minecraft:tall_grass unless block ~ ~ ~ minecraft:structure_void unless block ~ ~ ~ minecraft:farmland run return 1

# --- 3. 步进 ---
scoreboard players add @s sd_ray_steps 1
execute if score @s sd_ray_steps matches ..30 positioned ^ ^ ^0.2 run function stardew:tools/pickaxe/pickaxe_farmland_raycast_loop
