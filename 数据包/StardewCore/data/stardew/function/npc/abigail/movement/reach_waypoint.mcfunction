# 到达路径点
# @s = npc.abigail

# 增加路径索引
scoreboard players add @s stardew.npc.path_index 1

# 根据当前路径和索引，加载下一个路径点
execute if score @s stardew.npc.path_id matches 1 run function stardew:npc/abigail/paths/path1_home_to_town
execute if score @s stardew.npc.path_id matches 2 run function stardew:npc/abigail/paths/path2_town_to_graveyard
execute if score @s stardew.npc.path_id matches 3 run function stardew:npc/abigail/paths/path3_graveyard_to_saloon
execute if score @s stardew.npc.path_id matches 4 run function stardew:npc/abigail/paths/path4_saloon_to_home
execute if score @s stardew.npc.path_id matches 5 run function stardew:npc/abigail/paths/path5_graveyard_to_home
execute if score @s stardew.npc.path_id matches 6 run function stardew:npc/abigail/paths/path6_town_to_home