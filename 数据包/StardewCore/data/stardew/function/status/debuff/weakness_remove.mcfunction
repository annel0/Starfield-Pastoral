# data/stardew/function/status/debuff/weakness_remove.mcfunction
# 移除虚弱效果

scoreboard players set @s sd_debuff_weakness 0
scoreboard players set @s sd_weakness_duration 0
scoreboard players set @s sd_weakness_level 0

# 粒子提示效果消散
particle minecraft:happy_villager ~ ~1 ~ 0.3 0.5 0.3 0.1 5
