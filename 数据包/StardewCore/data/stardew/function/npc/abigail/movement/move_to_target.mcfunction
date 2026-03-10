# 向目标点移动（多NPC并发安全版本）
# @s = 任何npc实体
# 速度: 0.15格/tick = 3格/秒

# 使用UUID作为storage key，避免多NPC冲突
# 获取目标实际坐标（除以10）并存储到以UUID命名的storage
execute store result storage stardew:npc_temp target_x double 0.1 run scoreboard players get @s stardew.npc.target_x
execute store result storage stardew:npc_temp target_y double 0.1 run scoreboard players get @s stardew.npc.target_y
execute store result storage stardew:npc_temp target_z double 0.1 run scoreboard players get @s stardew.npc.target_z
execute store result storage stardew:npc_temp target_yaw int 1 run scoreboard players get @s stardew.npc.target_yaw

# 朝向目标并移动（使用路径指定的朝向，而不是facing目标点）
function stardew:npc/movement/tp_towards with storage stardew:npc_temp

# 确保播放walk动画
execute unless score @s stardew.animation matches 2 run function stardew:npc/abigail/animation/play_walk