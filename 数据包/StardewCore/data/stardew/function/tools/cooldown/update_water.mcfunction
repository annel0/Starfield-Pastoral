# data/stardew/functions/tools/cooldown/update_water.mcfunction
# 更新水壶冷却

# 减少冷却时间
scoreboard players remove @s sd_water_cd 1

# 计算 Boss 血条进度（总冷却30 ticks = 1.5秒）
bossbar set stardew:water_cooldown players @s
bossbar set stardew:water_cooldown visible true
execute store result bossbar stardew:water_cooldown value run scoreboard players get @s sd_water_cd
bossbar set stardew:water_cooldown max 30

# 冷却结束
execute if score @s sd_water_cd matches ..0 run function stardew:tools/cooldown/end_water
