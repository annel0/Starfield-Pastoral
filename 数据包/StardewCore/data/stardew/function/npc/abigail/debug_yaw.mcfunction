# Debug: 显示朝向信息
# 显示target_yaw和实际Rotation[0]

execute as @e[tag=npc.abigail] run tellraw @a [{"text":"[Debug] ","color":"gray"},{"text":"Target Yaw: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.target_yaw"},"color":"green"}]
execute as @e[tag=npc.abigail] store result score #villager_yaw stardew.temp run data get entity @s Rotation[0]
tellraw @a [{"text":"[Debug] ","color":"gray"},{"text":"Villager Yaw: ","color":"yellow"},{"score":{"name":"#villager_yaw","objective":"stardew.temp"},"color":"aqua"}]
execute as @e[tag=npc.abigail.visual] store result score #visual_yaw stardew.temp run data get entity @s Rotation[0]
tellraw @a [{"text":"[Debug] ","color":"gray"},{"text":"Visual Yaw: ","color":"yellow"},{"score":{"name":"#visual_yaw","objective":"stardew.temp"},"color":"light_purple"}]
execute as @e[tag=npc.abigail] run tellraw @a [{"text":"[Debug] ","color":"gray"},{"text":"Path Index: ","color":"yellow"},{"score":{"name":"@s","objective":"stardew.npc.path_index"},"color":"gold"}]
