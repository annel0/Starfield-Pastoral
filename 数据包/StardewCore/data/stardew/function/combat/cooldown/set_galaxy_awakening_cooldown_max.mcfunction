# 设置银河觉醒技能冷却时间最大值（15秒=300 ticks）
scoreboard players set @s sd_skill_2_cooldown 300

# 移除旧的bossbar（如果存在）
bossbar remove stardew:galaxy_awakening_cooldown
bossbar remove stardew:galaxy_awakening_duration

# 创建冷却bossbar（灰色，显示"冷却中"）- 初始设为满值
bossbar add stardew:galaxy_awakening_cooldown {"text":"✨ 银河觉醒 - 冷却中","color":"gray","bold":true}
bossbar set stardew:galaxy_awakening_cooldown color white
bossbar set stardew:galaxy_awakening_cooldown max 300
bossbar set stardew:galaxy_awakening_cooldown value 300
bossbar set stardew:galaxy_awakening_cooldown players @s
bossbar set stardew:galaxy_awakening_cooldown visible false

# 创建持续时间bossbar（紫色，显示正常技能名）
bossbar add stardew:galaxy_awakening_duration {"text":"✨ 银河觉醒","color":"#9933FF","bold":true}
bossbar set stardew:galaxy_awakening_duration color purple
bossbar set stardew:galaxy_awakening_duration max 120
execute store result bossbar stardew:galaxy_awakening_duration value run scoreboard players get @s sd_awakening_timer
bossbar set stardew:galaxy_awakening_duration players @s
bossbar set stardew:galaxy_awakening_duration visible true
