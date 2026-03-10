# 更新精准打击持续时间和视觉效果

# 减少持续时间
scoreboard players remove @s sd_precision_duration 1

# 更新持续时间bossbar
execute store result bossbar stardew:precision_strike_duration value run scoreboard players get @s sd_precision_duration

# 持续粒子效果（每5tick一次，减少性能消耗）
execute if score @s sd_precision_duration matches 1.. if predicate stardew:random_20 run particle minecraft:crit ~ ~1 ~ 0.2 0.4 0.2 0.3 5 force
execute if score @s sd_precision_duration matches 1.. if predicate stardew:random_20 run particle minecraft:enchanted_hit ~ ~1 ~ 0.15 0.3 0.15 0.05 3 force

# 效果即将结束提示（最后1秒）
execute if score @s sd_precision_duration matches 20 run playsound minecraft:block.note_block.bell player @s ~ ~ ~ 0.5 1.5
execute if score @s sd_precision_duration matches 20 run title @s actionbar {"text":"精准打击即将结束！","color":"yellow","bold":true}

# 效果结束 - 切换到冷却bossbar
execute if score @s sd_precision_duration matches ..0 run bossbar set stardew:precision_strike_duration visible false
execute if score @s sd_precision_duration matches ..0 store result bossbar stardew:precision_strike_cooldown value run scoreboard players get @s sd_skill_cooldown
execute if score @s sd_precision_duration matches ..0 run bossbar set stardew:precision_strike_cooldown visible true
execute if score @s sd_precision_duration matches ..0 run function stardew:combat/end_precision_strike
