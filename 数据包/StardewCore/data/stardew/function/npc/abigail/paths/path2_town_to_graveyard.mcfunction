# 路径2: 镇中心 → 墓地
# 90.5, -54, 111.5 -> 90, -54, 99(北) -> 94, -54, 99(东) -> 94, -54, 58(北) -> 56, -54, 57(西) -> 58, -54, 34(北) -> 66, -54, 34(东) -> 66.5, -54, 46.5(南)

execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_x 900
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_z 990
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_yaw 180

execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_x 940
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_z 990
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_yaw -90

execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_x 940
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_z 580
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_yaw 180

execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_x 560
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_z 570
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_yaw 90

execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_x 580
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_z 340
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_yaw 180

execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_x 660
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_z 340
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_yaw -90

execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_x 665
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_z 465
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 7 run function stardew:npc/abigail/movement/finish_path