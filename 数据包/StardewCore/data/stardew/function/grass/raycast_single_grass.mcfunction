# 射线检测放置单个草
# 执行者: 玩家 (@s)

# 增加步数
scoreboard players add @s sd_ray_steps 1

# Debug信息
#tellraw @s [{"text":"[Debug] Raycast step ","color":"gray"},{"score":{"name":"@s","objective":"sd_ray_steps"},"color":"white"},{"text":", block: ","color":"gray"},{"nbt":"block_state","block":"~ ~ ~","color":"yellow"}]

# 如果超过最大步数（50步=10格），停止
execute if score @s sd_ray_steps matches 50.. run return run tellraw @s {"text":"范围过远！","color":"red"}

# 检测到实体方块（非空气、非水、非岩浆）
# 在击中的方块上方尝试放置草
execute unless block ~ ~ ~ minecraft:air unless block ~ ~ ~ minecraft:water unless block ~ ~ ~ minecraft:lava positioned ~ ~1 ~ run return run function stardew:grass/try_spawn_single_grass

# 如果还是空气、水或岩浆，继续前进
execute if block ~ ~ ~ minecraft:air positioned ^ ^ ^0.2 run function stardew:grass/raycast_single_grass
execute if block ~ ~ ~ minecraft:water positioned ^ ^ ^0.2 run function stardew:grass/raycast_single_grass
execute if block ~ ~ ~ minecraft:lava positioned ^ ^ ^0.2 run function stardew:grass/raycast_single_grass