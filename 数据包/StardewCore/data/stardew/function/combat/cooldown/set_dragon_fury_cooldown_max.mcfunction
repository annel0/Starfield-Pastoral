# 设置龙牙狂怒技能冷却时间最大值

# 移除旧的bossbar（如果存在）
bossbar remove stardew:dragon_fury_cooldown
bossbar remove stardew:dragon_fury_duration

# 创建冷却bossbar（灰色，显示"冷却中"）
bossbar add stardew:dragon_fury_cooldown {"text":"� 龙牙狂怒 - 冷却中","color":"gray","bold":true}
bossbar set stardew:dragon_fury_cooldown color white
execute store result bossbar stardew:dragon_fury_cooldown max run scoreboard players get @s sd_skill_2_cooldown
execute store result bossbar stardew:dragon_fury_cooldown value run scoreboard players get @s sd_skill_2_cooldown
bossbar set stardew:dragon_fury_cooldown players @s
bossbar set stardew:dragon_fury_cooldown visible false

# 创建持续时间bossbar（红色，显示正常技能名）
bossbar add stardew:dragon_fury_duration {"text":"� 龙牙狂怒","color":"#8B008B","bold":true}
bossbar set stardew:dragon_fury_duration color red
bossbar set stardew:dragon_fury_duration max 200
execute store result bossbar stardew:dragon_fury_duration value run scoreboard players get @s sd_fury_timer
bossbar set stardew:dragon_fury_duration players @s
bossbar set stardew:dragon_fury_duration visible true

