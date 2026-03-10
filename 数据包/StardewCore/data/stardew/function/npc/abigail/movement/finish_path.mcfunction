# 路径完成
# @s = npc.abigail

# 应用目标schedule（从target_schedule复制到schedule）
execute if score @s stardew.npc.target_schedule matches 1.. run scoreboard players operation @s stardew.npc.schedule = @s stardew.npc.target_schedule
scoreboard players reset @s stardew.npc.target_schedule

# 保存最后的朝向作为idle朝向（用于对话后恢复）
scoreboard players operation @s stardew.npc.idle_yaw = @s stardew.npc.target_yaw

# 清除路径数据
scoreboard players reset @s stardew.npc.path_id
scoreboard players reset @s stardew.npc.path_index
scoreboard players reset @s stardew.npc.target_x
scoreboard players reset @s stardew.npc.target_y
scoreboard players reset @s stardew.npc.target_z
scoreboard players reset @s stardew.npc.target_yaw

# 播放idle动画
execute unless score @s stardew.animation matches 1 run function stardew:npc/abigail/animation/play_idle

# 更新日程状态（防止重复触发）
execute if score @s stardew.npc.schedule matches 1 run scoreboard players set @s stardew.npc.schedule 1
execute if score @s stardew.npc.schedule matches 2 run scoreboard players set @s stardew.npc.schedule 2
execute if score @s stardew.npc.schedule matches 3 run scoreboard players set @s stardew.npc.schedule 3
execute if score @s stardew.npc.schedule matches 4 run scoreboard players set @s stardew.npc.schedule 4