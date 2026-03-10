# 路径6: 镇中心 → 家
# 90.5, -54, 111.5 -> 85, -54, 111(西) -> 85, -54, 124(南) -> 73, -54, 124(西) -> 73, -54, 130(南)

execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_x 850
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_z 1110
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_yaw 90

execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_x 850
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_z 1240
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_x 730
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_z 1240
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_yaw 90

execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_x 730
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_z 1300
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 4 run function stardew:npc/abigail/movement/finish_path
