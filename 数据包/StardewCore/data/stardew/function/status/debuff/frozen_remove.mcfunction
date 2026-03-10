# data/stardew/function/status/debuff/frozen_remove.mcfunction
# 移除冰冻效果

scoreboard players set @s sd_debuff_frozen 0
scoreboard players set @s sd_frozen_duration 0
scoreboard players set @s sd_frozen_level 0

# 清除 Minecraft 效果
effect clear @s minecraft:slowness
effect clear @s minecraft:mining_fatigue

# 粒子提示效果消散
particle minecraft:happy_villager ~ ~1 ~ 0.3 0.5 0.3 0.1 5
