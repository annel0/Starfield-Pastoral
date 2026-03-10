# 路径3: 墓地 → 酒馆
# 66.5, -54, 46.5 -> 66, -54, 34(北) -> 58, -54, 34(西) -> 56, -54, 55(南) -> 94, -54, 55(东) -> 94, -54, 97(南) -> 66, -54, 96(西) -> 66, -54, 98(南) -> 66, -52, 100(南)

execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_x 660
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_z 340
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_yaw 180

execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_x 580
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_z 340
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_yaw 90

execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_x 560
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_z 550
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_x 940
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_z 550
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_yaw -90

execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_x 940
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_z 970
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_x 660
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_z 960
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_yaw 90

execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_x 660
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_z 980
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 7 run scoreboard players set @s stardew.npc.target_x 660
execute if score @s stardew.npc.path_index matches 7 run scoreboard players set @s stardew.npc.target_y -520
execute if score @s stardew.npc.path_index matches 7 run scoreboard players set @s stardew.npc.target_z 1000
execute if score @s stardew.npc.path_index matches 7 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 8 run function stardew:npc/abigail/movement/finish_path