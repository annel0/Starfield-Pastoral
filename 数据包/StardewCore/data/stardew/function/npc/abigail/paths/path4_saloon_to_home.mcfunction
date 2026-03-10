# 路径4: 酒馆 → 家
# 66, -52, 100 -> 66, -54, 98(北) -> 66, -54, 96(北) -> 85, -54, 96(东) -> 85, -54, 124(南) -> 73, -54, 124(西) -> 73, -54, 130(南)

execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_x 660
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_z 980
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_yaw 180

execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_x 660
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_z 960
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_yaw 180

execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_x 850
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_z 960
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_yaw -90

execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_x 850
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_z 1240
execute if score @s stardew.npc.path_index matches 3 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_x 730
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_z 1240
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_yaw 90

execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_x 730
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_z 1300
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 6 run function stardew:npc/abigail/movement/finish_path