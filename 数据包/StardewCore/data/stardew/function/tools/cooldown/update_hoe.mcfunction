# data/stardew/functions/tools/cooldown/update_hoe.mcfunction
# 更新锄头冷却

# 减少冷却时间
scoreboard players remove @s sd_hoe_cd 1

# 计算 Boss 血条进度（总冷却20 ticks = 1秒）
bossbar set stardew:hoe_cooldown players @s
bossbar set stardew:hoe_cooldown visible true
execute store result bossbar stardew:hoe_cooldown value run scoreboard players get @s sd_hoe_cd
bossbar set stardew:hoe_cooldown max 20

# 冷却结束
execute if score @s sd_hoe_cd matches ..0 run function stardew:tools/cooldown/end_hoe
