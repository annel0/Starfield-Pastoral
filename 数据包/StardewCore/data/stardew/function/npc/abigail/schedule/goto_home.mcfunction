# 前往家中（卧室）
# @s = npc.abigail 实体

# 检查是否已经在目标位置
execute if score @s stardew.npc.schedule matches 1 run return 0

# 检查是否正在回家（避免重复触发）
execute if score @s stardew.npc.target_schedule matches 1 run return 0

# 设置目标日程
scoreboard players set @s stardew.npc.target_schedule 1

# 根据当前位置选择合适的路径回家
# 如果在墓地(schedule=3)，启动路径5: 墓地 → 家
execute if score @s stardew.npc.schedule matches 3 run scoreboard players set @s stardew.npc.path_id 5
execute if score @s stardew.npc.schedule matches 3 run scoreboard players set @s stardew.npc.path_index 0

# 如果在酒馆(schedule=4)，启动路径4: 酒馆 → 家
execute if score @s stardew.npc.schedule matches 4 run scoreboard players set @s stardew.npc.path_id 4
execute if score @s stardew.npc.schedule matches 4 run scoreboard players set @s stardew.npc.path_index 0

# 如果在镇中心(schedule=2)，启动路径6: 镇中心 → 家
execute if score @s stardew.npc.schedule matches 2 run scoreboard players set @s stardew.npc.path_id 6
execute if score @s stardew.npc.schedule matches 2 run scoreboard players set @s stardew.npc.path_index 0

# 如果不在已知位置，没有路径，直接传送（由update_target_location处理）

# 如果在墓地(schedule=3)，启动路径5: 墓地 → 家
execute if score @s stardew.npc.schedule matches 3 run scoreboard players set @s stardew.npc.path_id 5
execute if score @s stardew.npc.schedule matches 3 run scoreboard players set @s stardew.npc.path_index 0
execute if score @s stardew.npc.schedule matches 3 run scoreboard players set @s stardew.npc.target_schedule 1
execute if score @s stardew.npc.schedule matches 3 run return 1

# 如果在酒馆(schedule=4)，启动路径4: 酒馆 → 家
execute if score @s stardew.npc.schedule matches 4 run scoreboard players set @s stardew.npc.path_id 4
execute if score @s stardew.npc.schedule matches 4 run scoreboard players set @s stardew.npc.path_index 0
execute if score @s stardew.npc.schedule matches 4 run scoreboard players set @s stardew.npc.target_schedule 1
execute if score @s stardew.npc.schedule matches 4 run return 1

# 如果在镇中心(schedule=2)，启动路径6: 镇中心 → 家
execute if score @s stardew.npc.schedule matches 2 run scoreboard players set @s stardew.npc.path_id 6
execute if score @s stardew.npc.schedule matches 2 run scoreboard players set @s stardew.npc.path_index 0
execute if score @s stardew.npc.schedule matches 2 run scoreboard players set @s stardew.npc.target_schedule 1
execute if score @s stardew.npc.schedule matches 2 run return 1

# 如果已经在家，不需要移动
scoreboard players set @s stardew.npc.schedule 1
