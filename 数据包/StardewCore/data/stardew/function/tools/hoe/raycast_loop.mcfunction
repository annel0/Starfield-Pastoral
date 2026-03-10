# data/stardew/functions/tools/hoe/raycast_loop.mcfunction

# 1. 命中检测
# 如果是可耕种方块 -> 执行耕地逻辑
execute if block ~ ~ ~ #stardew:tillable run function stardew:tools/hoe/hit_router

# 2. [核心修复] 停止条件
# A. 如果是可耕种方块 (没耕之前的状态) -> 停止
execute if block ~ ~ ~ #stardew:tillable run return 1

# B. [新增] 如果是耕地 (刚刚耕完的状态，或者对着已有耕地) -> 停止
# 这一行完美解决了"穿透"问题，因为耕完瞬间方块变成了 farmland
execute if block ~ ~ ~ minecraft:farmland run return 1

# C. [新增] 碰到障碍物 (石头、木头等非空气) -> 停止
# 防止隔山打牛
execute unless block ~ ~ ~ minecraft:air unless block ~ ~ ~ minecraft:water unless block ~ ~ ~ minecraft:short_grass unless block ~ ~ ~ minecraft:tall_grass unless block ~ ~ ~ #stardew:tillable unless block ~ ~ ~ minecraft:farmland run return 1

# 3. 步进 (0.2格)
scoreboard players add @s sd_ray_steps 1
execute if score @s sd_ray_steps matches ..30 positioned ^ ^ ^0.2 run function stardew:tools/hoe/raycast_loop