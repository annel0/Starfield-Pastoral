# 前往镇中心广场
# @s = npc.abigail 实体

# 检查是否已经在目标位置
execute if score @s stardew.npc.schedule matches 2 run return 0

# 检查是否正在前往镇中心（避免重复触发）
execute if score @s stardew.npc.target_schedule matches 2 run return 0

# 设置目标日程
scoreboard players set @s stardew.npc.target_schedule 2

# 如果在家(schedule=1)，启动路径1: 家 → 镇中心
execute if score @s stardew.npc.schedule matches 1 run scoreboard players set @s stardew.npc.path_id 1
execute if score @s stardew.npc.schedule matches 1 run scoreboard players set @s stardew.npc.path_index 0

# 如果不在家，没有预设路径，直接传送（由update_target_location处理）
