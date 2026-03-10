# data/stardew/function/status/debuff/hunger_remove.mcfunction
# 移除饥饿效果

scoreboard players set @s sd_debuff_hunger 0
scoreboard players set @s sd_hunger_duration 0
scoreboard players set @s sd_hunger_level 0
scoreboard players set @s sd_hunger_timer 0

# 粒子提示效果消散
particle minecraft:happy_villager ~ ~1 ~ 0.3 0.5 0.3 0.1 5
