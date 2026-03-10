# 在5x5区域内生成草
# 执行者: 玩家 (@s)

# 初始化计数器
scoreboard players set grass_count_tmp sd_temp 0

# 在5x5范围内的所有位置生成草（无随机，整片生成）
execute positioned ~-2 ~ ~-2 run function stardew:grass/try_spawn_area_grass
execute positioned ~-1 ~ ~-2 run function stardew:grass/try_spawn_area_grass
execute positioned ~ ~ ~-2 run function stardew:grass/try_spawn_area_grass
execute positioned ~1 ~ ~-2 run function stardew:grass/try_spawn_area_grass
execute positioned ~2 ~ ~-2 run function stardew:grass/try_spawn_area_grass

execute positioned ~-2 ~ ~-1 run function stardew:grass/try_spawn_area_grass
execute positioned ~-1 ~ ~-1 run function stardew:grass/try_spawn_area_grass
execute positioned ~ ~ ~-1 run function stardew:grass/try_spawn_area_grass
execute positioned ~1 ~ ~-1 run function stardew:grass/try_spawn_area_grass
execute positioned ~2 ~ ~-1 run function stardew:grass/try_spawn_area_grass

execute positioned ~-2 ~ ~ run function stardew:grass/try_spawn_area_grass
execute positioned ~-1 ~ ~ run function stardew:grass/try_spawn_area_grass
execute positioned ~ ~ ~ run function stardew:grass/try_spawn_area_grass
execute positioned ~1 ~ ~ run function stardew:grass/try_spawn_area_grass
execute positioned ~2 ~ ~ run function stardew:grass/try_spawn_area_grass

execute positioned ~-2 ~ ~1 run function stardew:grass/try_spawn_area_grass
execute positioned ~-1 ~ ~1 run function stardew:grass/try_spawn_area_grass
execute positioned ~ ~ ~1 run function stardew:grass/try_spawn_area_grass
execute positioned ~1 ~ ~1 run function stardew:grass/try_spawn_area_grass
execute positioned ~2 ~ ~1 run function stardew:grass/try_spawn_area_grass

execute positioned ~-2 ~ ~2 run function stardew:grass/try_spawn_area_grass
execute positioned ~-1 ~ ~2 run function stardew:grass/try_spawn_area_grass
execute positioned ~ ~ ~2 run function stardew:grass/try_spawn_area_grass
execute positioned ~1 ~ ~2 run function stardew:grass/try_spawn_area_grass
execute positioned ~2 ~ ~2 run function stardew:grass/try_spawn_area_grass

# 显示生成结果
execute as @s run tellraw @s [{"text":"在5x5区域内生成了 ","color":"green"},{"score":{"name":"grass_count_tmp","objective":"sd_temp"},"color":"yellow"},{"text":" 个草","color":"green"}]