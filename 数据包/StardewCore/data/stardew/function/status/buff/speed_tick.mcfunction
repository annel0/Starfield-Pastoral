# data/stardew/function/status/buff/speed_tick.mcfunction
# 速度效果 tick 处理
# 效果: 移动速度提升

# 持续时间倒计时
scoreboard players remove @s sd_speed_duration 1

# 应用速度提升效果
execute if score @s sd_speed_level matches 1 run effect give @s minecraft:speed 1 0 true
execute if score @s sd_speed_level matches 2 run effect give @s minecraft:speed 1 1 true
execute if score @s sd_speed_level matches 3.. run effect give @s minecraft:speed 1 2 true

# 粒子效果
particle minecraft:electric_spark ~ ~0.1 ~ 0.3 0.1 0.3 0.1 2

# 持续时间结束时清除效果
execute if score @s sd_speed_duration matches ..0 run function stardew:status/buff/speed_remove
