# stardew:weeds/spawn_area_weeds.mcfunction
# 在目标位置周围5格内批量生成杂草（约40%密度）
# 执行者: 玩家 (@s)
# 执行位置: 目标方块位置

# 计数器
scoreboard players set #weed_count sd_temp 0

# 在5x5范围内尝试生成杂草（以当前方块为中心）
execute positioned ~-2 ~ ~-2 run function stardew:weeds/try_spawn_area_weed
execute positioned ~-1 ~ ~-2 run function stardew:weeds/try_spawn_area_weed
execute positioned ~ ~ ~-2 run function stardew:weeds/try_spawn_area_weed
execute positioned ~1 ~ ~-2 run function stardew:weeds/try_spawn_area_weed
execute positioned ~2 ~ ~-2 run function stardew:weeds/try_spawn_area_weed

execute positioned ~-2 ~ ~-1 run function stardew:weeds/try_spawn_area_weed
execute positioned ~-1 ~ ~-1 run function stardew:weeds/try_spawn_area_weed
execute positioned ~ ~ ~-1 run function stardew:weeds/try_spawn_area_weed
execute positioned ~1 ~ ~-1 run function stardew:weeds/try_spawn_area_weed
execute positioned ~2 ~ ~-1 run function stardew:weeds/try_spawn_area_weed

execute positioned ~-2 ~ ~ run function stardew:weeds/try_spawn_area_weed
execute positioned ~-1 ~ ~ run function stardew:weeds/try_spawn_area_weed
execute positioned ~ ~ ~ run function stardew:weeds/try_spawn_area_weed
execute positioned ~1 ~ ~ run function stardew:weeds/try_spawn_area_weed
execute positioned ~2 ~ ~ run function stardew:weeds/try_spawn_area_weed

execute positioned ~-2 ~ ~1 run function stardew:weeds/try_spawn_area_weed
execute positioned ~-1 ~ ~1 run function stardew:weeds/try_spawn_area_weed
execute positioned ~ ~ ~1 run function stardew:weeds/try_spawn_area_weed
execute positioned ~1 ~ ~1 run function stardew:weeds/try_spawn_area_weed
execute positioned ~2 ~ ~1 run function stardew:weeds/try_spawn_area_weed

execute positioned ~-2 ~ ~2 run function stardew:weeds/try_spawn_area_weed
execute positioned ~-1 ~ ~2 run function stardew:weeds/try_spawn_area_weed
execute positioned ~ ~ ~2 run function stardew:weeds/try_spawn_area_weed
execute positioned ~1 ~ ~2 run function stardew:weeds/try_spawn_area_weed
execute positioned ~2 ~ ~2 run function stardew:weeds/try_spawn_area_weed

# 反馈信息
tellraw @s [{"text":"[DEBUG] ","color":"gold"},{"text":"在5x5区域内生成了 ","color":"green"},{"score":{"name":"#weed_count","objective":"sd_temp"},"color":"yellow"},{"text":" 个杂草","color":"green"}]