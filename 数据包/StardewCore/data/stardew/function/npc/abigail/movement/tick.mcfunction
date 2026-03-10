# NPC路径移动系统（通用版本 - 多NPC并发安全）
# 每tick调用，让NPC沿路径移动
# @s = 任何 npc 实体
# 使用每个NPC自己的scoreboard存储临时计算，避免多NPC冲突

# 检查是否有目标路径点
execute unless score @s stardew.npc.path_id matches 1.. run return 0

# 根据path_id加载对应的路径文件（设置target坐标）
execute if score @s stardew.npc.path_id matches 1 run function stardew:npc/abigail/paths/path1_home_to_town
execute if score @s stardew.npc.path_id matches 2 run function stardew:npc/abigail/paths/path2_town_to_graveyard
execute if score @s stardew.npc.path_id matches 3 run function stardew:npc/abigail/paths/path3_graveyard_to_saloon
execute if score @s stardew.npc.path_id matches 4 run function stardew:npc/abigail/paths/path4_saloon_to_home
execute if score @s stardew.npc.path_id matches 5 run function stardew:npc/abigail/paths/path5_graveyard_to_home
execute if score @s stardew.npc.path_id matches 6 run function stardew:npc/abigail/paths/path6_town_to_home

# 获取当前位置（存到自己的scoreboard）
execute store result score @s stardew.npc.pos_x run data get entity @s Pos[0] 10
execute store result score @s stardew.npc.pos_y run data get entity @s Pos[1] 10
execute store result score @s stardew.npc.pos_z run data get entity @s Pos[2] 10

# 计算距离差（直接在自己的scoreboard上操作）
# dx = target_x - pos_x
scoreboard players operation @s stardew.npc.pos_x -= @s stardew.npc.target_x
# dy = target_y - pos_y
scoreboard players operation @s stardew.npc.pos_y -= @s stardew.npc.target_y
# dz = target_z - pos_z
scoreboard players operation @s stardew.npc.pos_z -= @s stardew.npc.target_z

# 计算距离平方（简化版，只用xz平面）
# dist_sq = dx² + dz²
scoreboard players operation @s stardew.npc.calc_1 = @s stardew.npc.pos_x
scoreboard players operation @s stardew.npc.calc_1 *= @s stardew.npc.pos_x
scoreboard players operation @s stardew.npc.calc_2 = @s stardew.npc.pos_z
scoreboard players operation @s stardew.npc.calc_2 *= @s stardew.npc.pos_z
scoreboard players operation @s stardew.npc.calc_1 += @s stardew.npc.calc_2

# 如果距离小于0.9格（9单位的平方=81），认为到达
execute if score @s stardew.npc.calc_1 matches ..81 run function stardew:npc/movement/reach_waypoint

# 如果还在移动，向目标移动
execute if score @s stardew.npc.calc_1 matches 82.. run function stardew:npc/movement/move_to_target
