# 到达路径点（通用版本）
# @s = 任何npc实体
# 每个NPC调用自己的路径数据

# 增加路径索引
scoreboard players add @s stardew.npc.path_index 1

# 根据NPC标签，调用对应的路径加载器
execute if entity @s[tag=npc.abigail] run function stardew:npc/abigail/paths/load_waypoint
# 未来添加更多NPC:
# execute if entity @s[tag=npc.emily] run function stardew:npc/emily/paths/load_waypoint
# execute if entity @s[tag=npc.haley] run function stardew:npc/haley/paths/load_waypoint
