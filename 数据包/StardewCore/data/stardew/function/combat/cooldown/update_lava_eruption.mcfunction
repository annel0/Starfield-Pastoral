# 更新熔岩爆发冷却 bossbar

# 减少冷却时间
scoreboard players remove @s sd_skill_2_cooldown 1

# 更新 bossbar 值
execute store result bossbar stardew:lava_eruption_cooldown value run scoreboard players get @s sd_skill_2_cooldown

# 冷却结束
execute if score @s sd_skill_2_cooldown matches ..0 run tag @s remove sd_using_lava_eruption
execute if score @s sd_skill_2_cooldown matches ..0 run bossbar set stardew:lava_eruption_cooldown visible false
