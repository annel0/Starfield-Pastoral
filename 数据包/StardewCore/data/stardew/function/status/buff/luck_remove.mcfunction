# data/stardew/function/status/buff/luck_remove.mcfunction
# 移除幸运效果

scoreboard players set @s sd_buff_luck 0
scoreboard players set @s sd_luck_duration 0
scoreboard players set @s sd_luck_level 0

# 粒子提示效果消散
particle minecraft:glow ~ ~1 ~ 0.3 0.5 0.3 0.1 5
