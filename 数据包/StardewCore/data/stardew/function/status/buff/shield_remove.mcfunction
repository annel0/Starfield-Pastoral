# data/stardew/function/status/buff/shield_remove.mcfunction
# 移除护盾效果

scoreboard players set @s sd_buff_shield 0
scoreboard players set @s sd_shield_duration 0
scoreboard players set @s sd_shield_amount 0

# 粒子提示效果消散
particle minecraft:glow ~ ~1 ~ 0.3 0.5 0.3 0.1 5
playsound minecraft:block.glass.break player @s ~ ~ ~ 0.5 0.8
