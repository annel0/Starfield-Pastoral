# 更新海王怒涛冷却 bossbar

# 减少冷却时间
scoreboard players remove @s sd_skill_2_cooldown 1

# 更新 bossbar 值
execute store result bossbar stardew:neptune_wrath_cooldown value run scoreboard players get @s sd_skill_2_cooldown

# 当冷却结束时隐藏 bossbar 并清理标签
execute if score @s sd_skill_2_cooldown matches ..0 run tag @s remove sd_using_neptune_wrath
execute if score @s sd_skill_2_cooldown matches ..0 run bossbar set stardew:neptune_wrath_cooldown visible false
