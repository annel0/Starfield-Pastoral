# 更新暗影步技能冷却

# 减少冷却时间
scoreboard players remove @s sd_skill_cooldown 1

# 更新冷却条
execute store result bossbar stardew:shadow_step_cooldown value run scoreboard players get @s sd_skill_cooldown

# 冷却结束
execute if score @s sd_skill_cooldown matches ..0 run function stardew:combat/cooldown/end_shadow_step
