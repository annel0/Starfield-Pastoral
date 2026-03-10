# data/stardew/function/status/debuff/slime_remove.mcfunction
# 移除粘液效果

scoreboard players set @s sd_debuff_slime 0
scoreboard players set @s sd_slime_duration 0
scoreboard players set @s sd_slime_level 0

# 清除 Minecraft 效果
effect clear @s minecraft:slowness
effect clear @s minecraft:nausea

# 粒子提示效果消散
particle minecraft:happy_villager ~ ~1 ~ 0.3 0.5 0.3 0.1 5
