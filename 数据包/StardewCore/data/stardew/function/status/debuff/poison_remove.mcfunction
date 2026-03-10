# data/stardew/function/status/debuff/poison_remove.mcfunction
# 移除中毒效果

scoreboard players set @s sd_debuff_poison 0
scoreboard players set @s sd_poison_duration 0
scoreboard players set @s sd_poison_level 0
scoreboard players set @s sd_poison_tick_timer 0

# 粒子提示效果消散
particle minecraft:happy_villager ~ ~1 ~ 0.3 0.5 0.3 0.1 5
