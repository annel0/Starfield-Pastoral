# ================================================================
# 星露谷物语 - 更新鸡的动画状态
# ================================================================
# 用途：根据鸡的移动状态切换动画
# @s = AJ root entity

# 保存 ID
scoreboard players operation #sync_id stardew.animal.temp = @s stardew.animal.id

# 检查逻辑鸡是否在移动（通过 Motion 判断）
execute store result score #motion_x stardew.animal.temp as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp run data get entity @s Motion[0] 1000
execute store result score #motion_z stardew.animal.temp as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp run data get entity @s Motion[2] 1000

# 计算总移动量（绝对值）
execute if score #motion_x stardew.animal.temp matches ..-1 run scoreboard players operation #motion_x stardew.animal.temp *= #-1 stardew.animal.temp
execute if score #motion_z stardew.animal.temp matches ..-1 run scoreboard players operation #motion_z stardew.animal.temp *= #-1 stardew.animal.temp
scoreboard players operation #total_motion stardew.animal.temp = #motion_x stardew.animal.temp
scoreboard players operation #total_motion stardew.animal.temp += #motion_z stardew.animal.temp


# 动画状态机
# 0 = idle, 1 = walk

# 如果在移动（motion > 10，即 0.01 blocks/tick，降低阈值）
execute if score #total_motion stardew.animal.temp matches 10.. unless score @s stardew.animal.anim_state matches 1 run function stardew:animal/animated_java/play_walk
execute if score #total_motion stardew.animal.temp matches 10.. run scoreboard players set @s stardew.animal.anim_state 1

# 如果静止
execute if score #total_motion stardew.animal.temp matches ..9 unless score @s stardew.animal.anim_state matches 0 run function stardew:animal/animated_java/play_idle
execute if score #total_motion stardew.animal.temp matches ..9 run scoreboard players set @s stardew.animal.anim_state 0
