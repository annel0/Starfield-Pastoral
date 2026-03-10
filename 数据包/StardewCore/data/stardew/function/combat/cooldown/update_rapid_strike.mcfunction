# 更新连击冷却

# 减少冷却
scoreboard players remove @s sd_skill_2_cooldown 1

# 更新bossbar
bossbar set stardew:rapid_strike_cooldown players @s
bossbar set stardew:rapid_strike_cooldown visible true
execute store result bossbar stardew:rapid_strike_cooldown value run scoreboard players get @s sd_skill_2_cooldown

# 冷却结束
execute if score @s sd_skill_2_cooldown matches ..0 run function stardew:combat/cooldown/end_rapid_strike
