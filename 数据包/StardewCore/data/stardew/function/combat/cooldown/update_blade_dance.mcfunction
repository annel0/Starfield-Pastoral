# 更新刀锋之舞技能冷却

# 显示冷却bossbar
bossbar set stardew:blade_dance_cooldown players @s
bossbar set stardew:blade_dance_cooldown visible true

# 减少冷却时间
scoreboard players remove @s sd_skill_cooldown 1

# 更新冷却bossbar
execute store result bossbar stardew:blade_dance_cooldown value run scoreboard players get @s sd_skill_cooldown

# 冷却结束
execute if score @s sd_skill_cooldown matches ..0 run function stardew:combat/cooldown/end_blade_dance
