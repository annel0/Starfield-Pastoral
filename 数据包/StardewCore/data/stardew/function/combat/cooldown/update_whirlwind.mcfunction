# 更新旋风斩技能冷却

# 减少冷却时间
scoreboard players remove @s sd_skill_2_cooldown 1

# 计算 Boss 血条进度
bossbar set stardew:whirlwind_cooldown players @s
bossbar set stardew:whirlwind_cooldown visible true
execute store result bossbar stardew:whirlwind_cooldown value run scoreboard players get @s sd_skill_2_cooldown

# 冷却结束
execute if score @s sd_skill_2_cooldown matches ..0 run function stardew:combat/cooldown/end_whirlwind
