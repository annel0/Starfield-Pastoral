# data/stardew/functions/tools/cooldown/update_scythe.mcfunction
# 更新镰刀冷却

# 减少冷却时间
scoreboard players remove @s sd_scythe_cd 1

# 计算 Boss 血条进度（总冷却20 ticks = 1秒）
bossbar set stardew:scythe_cooldown players @s
bossbar set stardew:scythe_cooldown visible true
execute store result bossbar stardew:scythe_cooldown value run scoreboard players get @s sd_scythe_cd
bossbar set stardew:scythe_cooldown max 20

# 冷却结束
execute if score @s sd_scythe_cd matches ..0 run function stardew:tools/cooldown/end_scythe
