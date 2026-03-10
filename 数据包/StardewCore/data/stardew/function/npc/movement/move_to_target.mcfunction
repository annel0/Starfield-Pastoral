# 向目标点移动（通用多NPC版本）
# @s = 任何npc实体
# 速度: 0.15格/tick = 3格/秒

# 使用共享storage临时存储坐标（每个NPC按序执行，不会冲突）
execute store result storage stardew:npc_temp target_x double 0.1 run scoreboard players get @s stardew.npc.target_x
execute store result storage stardew:npc_temp target_y double 0.1 run scoreboard players get @s stardew.npc.target_y
execute store result storage stardew:npc_temp target_z double 0.1 run scoreboard players get @s stardew.npc.target_z

# 朝向目标并移动
function stardew:npc/movement/tp_towards with storage stardew:npc_temp

# 根据NPC标签调用对应的动画系统
execute if entity @s[tag=npc.abigail] unless score @s stardew.animation matches 2 run function stardew:npc/abigail/animation/play_walk
# 未来添加更多NPC:
# execute if entity @s[tag=npc.emily] unless score @s stardew.animation matches 2 run function stardew:npc/emily/animation/play_walk
