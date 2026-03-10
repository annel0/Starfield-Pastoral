# 前往墓地
# @s = npc.abigail 实体

# 检查是否已经在目标位置
execute if score @s stardew.npc.schedule matches 3 run return 0

# 检查是否正在前往墓地（避免重复触发）
execute if score @s stardew.npc.target_schedule matches 3 run return 0

# 设置目标日程
scoreboard players set @s stardew.npc.target_schedule 3

# 如果在镇中心(schedule=2)，启动路径2: 镇中心 → 墓地
execute if score @s stardew.npc.schedule matches 2 run scoreboard players set @s stardew.npc.path_id 2
execute if score @s stardew.npc.schedule matches 2 run scoreboard players set @s stardew.npc.path_index 0

# 如果不在镇中心，没有预设路径，直接传送（由update_target_location处理）
execute if score @s stardew.npc.schedule matches 2 run scoreboard players set @s stardew.npc.target_schedule 3
execute if score @s stardew.npc.target_schedule matches 3 run return 0

# 如果不在镇中心，传送到镇中心起点，然后启动路径2
tp @s 90.5 -54.0 111.5 180 0
execute at @s run tp @e[tag=npc.abigail.visual,limit=1,sort=nearest] 90.5 -54.0 111.5 180 0
execute at @s run tp @e[type=interaction,tag=npc.abigail.interaction,limit=1,sort=nearest] 90.5 -54.0 111.5
scoreboard players set @s stardew.npc.path_id 2
scoreboard players set @s stardew.npc.path_index 0
scoreboard players set @s stardew.npc.target_schedule 3
