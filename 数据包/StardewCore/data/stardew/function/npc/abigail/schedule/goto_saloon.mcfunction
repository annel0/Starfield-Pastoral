# 前往酒馆
# @s = npc.abigail 实体

# 检查是否已经在目标位置
execute if score @s stardew.npc.schedule matches 4 run return 0

# 检查是否正在前往酒馆（避免重复触发）
execute if score @s stardew.npc.target_schedule matches 4 run return 0

# 设置目标日程
scoreboard players set @s stardew.npc.target_schedule 4

# 如果在墓地(schedule=3)，启动路径3: 墓地 → 酒馆
execute if score @s stardew.npc.schedule matches 3 run scoreboard players set @s stardew.npc.path_id 3
execute if score @s stardew.npc.schedule matches 3 run scoreboard players set @s stardew.npc.path_index 0

# 如果不在墓地，没有预设路径，直接传送（由update_target_location处理）
scoreboard players set @s stardew.npc.path_id 3
scoreboard players set @s stardew.npc.path_index 0
