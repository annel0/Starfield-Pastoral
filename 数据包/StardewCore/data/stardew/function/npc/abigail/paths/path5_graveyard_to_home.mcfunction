# 路径5: 墓地  家
# 使用已验证的路径点，确保不穿模
# 66.5, -54, 46.5 -> 66, -54, 34(北) -> 58, -54, 34(西) -> 56, -54, 55(南) -> 94, -54, 55(东) -> 94, -54, 99(南) -> 90, -54, 99(西) -> 85, -54, 99(西) -> 85, -54, 124(南) -> 73, -54, 124(西) -> 73, -54, 130(南)

# 路径点0-3：复用path2/path3的前段（墓地到94, -54, 55）
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

# 路径点4：从94, -54, 55到94, -54, 99（往南）
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_x 940
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_z 990
execute if score @s stardew.npc.path_index matches 4 run scoreboard players set @s stardew.npc.target_yaw 0

# 路径点5：从94, -54, 99到90, -54, 99（往西，回到path2的起点区域）
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_x 900
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_z 990
execute if score @s stardew.npc.path_index matches 5 run scoreboard players set @s stardew.npc.target_yaw 90

# 路径点6-9：复用path6/path4的后半段（镇中心区域到家）
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_x 850
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_z 990
execute if score @s stardew.npc.path_index matches 6 run scoreboard players set @s stardew.npc.target_yaw 90

execute if score @s stardew.npc.path_index matches 7 run scoreboard players set @s stardew.npc.target_x 850
execute if score @s stardew.npc.path_index matches 7 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 7 run scoreboard players set @s stardew.npc.target_z 1240
execute if score @s stardew.npc.path_index matches 7 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 8 run scoreboard players set @s stardew.npc.target_x 730
execute if score @s stardew.npc.path_index matches 8 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 8 run scoreboard players set @s stardew.npc.target_z 1240
execute if score @s stardew.npc.path_index matches 8 run scoreboard players set @s stardew.npc.target_yaw 90

execute if score @s stardew.npc.path_index matches 9 run scoreboard players set @s stardew.npc.target_x 730
execute if score @s stardew.npc.path_index matches 9 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 9 run scoreboard players set @s stardew.npc.target_z 1300
execute if score @s stardew.npc.path_index matches 9 run scoreboard players set @s stardew.npc.target_yaw 0

execute if score @s stardew.npc.path_index matches 10 run function stardew:npc/abigail/movement/finish_path