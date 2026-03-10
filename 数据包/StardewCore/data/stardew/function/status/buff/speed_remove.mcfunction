# data/stardew/function/status/buff/speed_remove.mcfunction
# 移除速度效果

scoreboard players set @s sd_buff_speed 0
scoreboard players set @s sd_speed_duration 0
scoreboard players set @s sd_speed_level 0

# 清除 Minecraft 效果
effect clear @s minecraft:speed

# 粒子提示效果消散
particle minecraft:glow ~ ~1 ~ 0.3 0.5 0.3 0.1 5
