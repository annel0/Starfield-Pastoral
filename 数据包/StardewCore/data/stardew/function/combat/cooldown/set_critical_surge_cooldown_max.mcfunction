# 设置暴击涌动技能冷却时间最大值

# 移除旧的bossbar（如果存在）
bossbar remove stardew:critical_surge_cooldown
bossbar remove stardew:critical_surge_duration

# 创建冷却bossbar（灰色，显示"冷却中"）
bossbar add stardew:critical_surge_cooldown {"text":"⚡ 暴击涌动 - 冷却中","color":"gray","bold":true}
bossbar set stardew:critical_surge_cooldown color white
execute store result bossbar stardew:critical_surge_cooldown max run scoreboard players get @s sd_skill_2_cooldown
execute store result bossbar stardew:critical_surge_cooldown value run scoreboard players get @s sd_skill_2_cooldown
bossbar set stardew:critical_surge_cooldown players @s
bossbar set stardew:critical_surge_cooldown visible false

# 创建持续时间bossbar（红色，显示正常技能名）
bossbar add stardew:critical_surge_duration {"text":"⚡ 暴击涌动","color":"#FFD700","bold":true}
bossbar set stardew:critical_surge_duration color red
bossbar set stardew:critical_surge_duration max 120
execute store result bossbar stardew:critical_surge_duration value run scoreboard players get @s sd_surge_timer
bossbar set stardew:critical_surge_duration players @s
bossbar set stardew:critical_surge_duration visible true


