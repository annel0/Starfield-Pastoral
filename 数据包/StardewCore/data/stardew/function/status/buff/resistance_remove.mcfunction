# data/stardew/function/status/buff/resistance_remove.mcfunction
# 移除抗性效果

scoreboard players set @s sd_buff_resistance 0
scoreboard players set @s sd_resistance_duration 0
scoreboard players set @s sd_resistance_level 0

# 粒子提示效果消散
particle minecraft:glow ~ ~1 ~ 0.3 0.5 0.3 0.1 5
