# 路径1: 家 → 镇中心
# 73.5, -54, 130.5 -> 72, -54, 119(北) -> 87, -54, 120(东) -> 90.5, -54, 111.5(北)

# 点0: 起点（朝北）
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_x 720
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_z 1190
execute if score @s stardew.npc.path_index matches 0 run scoreboard players set @s stardew.npc.target_yaw 180

# 点1: 72, -54, 119 (朝东)
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_x 870
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_z 1200
execute if score @s stardew.npc.path_index matches 1 run scoreboard players set @s stardew.npc.target_yaw -90

# 点2: 87, -54, 120 (朝东)
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_x 905
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_y -540
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_z 1115
execute if score @s stardew.npc.path_index matches 2 run scoreboard players set @s stardew.npc.target_yaw 180

# 点3: 终点 90.5, -54, 111.5 (朝北) - 到达！
execute if score @s stardew.npc.path_index matches 3 run function stardew:npc/abigail/movement/finish_path
