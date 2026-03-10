# ================================================================
# 更新幼年鸭动画状态
# ================================================================
# @s = aj.chicken_baby.root（用于幼年鸭）
# 需要 #sync_id 已设置

# 获取运动数据
execute store result score #motion_x stardew.animal.temp as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp run data get entity @s Motion[0] 1000
execute store result score #motion_z stardew.animal.temp as @e[type=chicken,tag=stardew.animal] if score @s stardew.animal.id = #sync_id stardew.animal.temp run data get entity @s Motion[2] 1000

# 计算绝对值
execute if score #motion_x stardew.animal.temp matches ..-1 run scoreboard players operation #motion_x stardew.animal.temp *= #-1 stardew.animal.temp
execute if score #motion_z stardew.animal.temp matches ..-1 run scoreboard players operation #motion_z stardew.animal.temp *= #-1 stardew.animal.temp

# 总运动量
scoreboard players operation #total_motion stardew.animal.temp = #motion_x stardew.animal.temp
scoreboard players operation #total_motion stardew.animal.temp += #motion_z stardew.animal.temp

# 状态机：运动时播放 walk，静止时停止所有动画
# 阈值 10 (0.01 blocks/tick) 与成年鸭一致
execute if score #total_motion stardew.animal.temp matches 10.. unless score @s stardew.animal.anim_state matches 1 run function stardew:animal/animated_java/play_duck_baby_walk
execute if score #total_motion stardew.animal.temp matches ..9 unless score @s stardew.animal.anim_state matches 0 run function stardew:animal/animated_java/play_duck_baby_idle
