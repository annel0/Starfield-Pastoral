# data/stardew/function/status/buff/strength_remove.mcfunction
# 移除力量效果

scoreboard players set @s sd_buff_strength 0
scoreboard players set @s sd_strength_duration 0
scoreboard players set @s sd_strength_level 0

# 粒子提示效果消散
particle minecraft:glow ~ ~1 ~ 0.3 0.5 0.3 0.1 5
