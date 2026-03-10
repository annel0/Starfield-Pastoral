# data/stardew/function/status/buff/regen_remove.mcfunction
# 移除再生效果

scoreboard players set @s sd_buff_regen 0
scoreboard players set @s sd_regen_duration 0
scoreboard players set @s sd_regen_level 0

# 粒子提示效果消散
particle minecraft:glow ~ ~1 ~ 0.3 0.5 0.3 0.1 5
