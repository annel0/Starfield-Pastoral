# 传送NPC到目标日程位置
# @s = npc.abigail
# 根据target_schedule直接传送到目的地

# 1=在家
execute if score @s stardew.npc.target_schedule matches 1 run tp @s 73 -54 130 0 0
execute if score @s stardew.npc.target_schedule matches 1 run scoreboard players set @s stardew.npc.idle_yaw 0

# 2=镇中心
execute if score @s stardew.npc.target_schedule matches 2 run tp @s 94 -54 99 0 0
execute if score @s stardew.npc.target_schedule matches 2 run scoreboard players set @s stardew.npc.idle_yaw 0

# 3=墓地
execute if score @s stardew.npc.target_schedule matches 3 run tp @s 66 -54 34 0 0
execute if score @s stardew.npc.target_schedule matches 3 run scoreboard players set @s stardew.npc.idle_yaw 0

# 4=酒馆
execute if score @s stardew.npc.target_schedule matches 4 run tp @s 66 -54 96 180 0
execute if score @s stardew.npc.target_schedule matches 4 run scoreboard players set @s stardew.npc.idle_yaw 180

# 应用日程状态
scoreboard players operation @s stardew.npc.schedule = @s stardew.npc.target_schedule
scoreboard players reset @s stardew.npc.target_schedule

# 清除路径状态(因为已经瞬移到目的地了)
scoreboard players reset @s stardew.npc.path_id
scoreboard players reset @s stardew.npc.path_index
scoreboard players reset @s stardew.npc.target_x
scoreboard players reset @s stardew.npc.target_y
scoreboard players reset @s stardew.npc.target_z
scoreboard players reset @s stardew.npc.target_yaw

# 播放idle动画
execute unless score @s stardew.animation matches 1 run function stardew:npc/abigail/animation/play_idle

# 传送粒子效果(可选)
particle minecraft:portal ~ ~1 ~ 0.3 0.5 0.3 0.5 20
playsound minecraft:entity.enderman.teleport master @a[distance=..16] ~ ~ ~ 0.3 1.5
